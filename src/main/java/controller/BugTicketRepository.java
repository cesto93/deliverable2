package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import git.GitLogRetriever;
import jira.JIRATicketRetriever;
import model.BugTicket;
import model.GitCommit;
import model.ReleaseInfo;

public class BugTicketRepository {
	
	private GitLogRetriever retriever;
	private String[] extTaken;
	private static final Logger LOGGER = Logger.getLogger(BugTicketRepository.class.getName());
	
	public BugTicketRepository(GitLogRetriever retriever, String[] extTaken) {
		this.retriever = retriever;
		this.extTaken = extTaken;
	}
	
	private List<GitCommit> getGitCommits(BugTicket ticket) {	
		List<String> hashes = retriever.getCommitsHash(ticket.getKey());
		ArrayList<GitCommit> temp = new ArrayList<>();
		for (String hash : hashes) {
			LocalDate date = retriever.getCommitDate(hash);
			temp.add(new GitCommit(hash, date));
		}	
			return new ArrayList<>(temp);
	}	

	
	private List<String> getFileModified(List<GitCommit> commits) {
		ArrayList<String> fileNames = new ArrayList<>(); 
		for (GitCommit commit : commits) {
				fileNames.addAll(retriever.getFilesModifiedByCommit(commit.getHash(), extTaken));
		}
		return fileNames;
	}
	
	public List<BugTicket> getBugTicket(String projName,  ReleaseInfo[] rels) {
		List<BugTicket> tickets = JIRATicketRetriever.getBugTicket(projName);
		removeUnkownFV(tickets, rels);
		removeUnkownAV(tickets, rels);
		handleMissingFV(tickets);
		
		StringBuilder bld = new StringBuilder();
		int i = 0;
		while(i < tickets.size()) {
			List<GitCommit> commits = getGitCommits(tickets.get(i));
			if (commits.isEmpty()) {
				bld.append(tickets.get(i).getKey() + " ");
				tickets.remove(i);
			} else {
				tickets.get(i).setFileNames(getFileModified(commits));
				i++;
			}
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info("removed empty ticket: " + bld.toString());
		
		return tickets;
	}
	
	public static void removeUnkownAV(List<BugTicket> tickets, ReleaseInfo[] rels) {
		List<String> relIds = new ArrayList<>(rels.length);
		for (ReleaseInfo rel : rels) {
			relIds.add(rel.getVersionID());
		}
		
		for (BugTicket ticket : tickets) {
			ticket.getAffectedVersions().removeIf(av -> !relIds.contains(av));
		}
	}
	
	public static void removeUnkownFV(List<BugTicket> tickets, ReleaseInfo[] rels) {
		List<String> relIds = new ArrayList<>(rels.length);
		for (ReleaseInfo rel : rels) {
			relIds.add(rel.getVersionID());
		}
		
		for (BugTicket ticket : tickets) {
			ticket.getFixedVersions().removeIf(fv -> !relIds.contains(fv));
		}
	}
	
	private void handleMissingFV(List<BugTicket> tickets) {
		StringBuilder bld = new StringBuilder();
		int i = 0;
		while (i < tickets.size()) {
			if (tickets.get(i).getFixedVersions().isEmpty()) {
				bld.append(tickets.get(i).getKey() + " ");
				tickets.remove(i);
			}
			else
				i++;
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info("removed ticket with no FV: " + bld.toString());
	}
	
}
