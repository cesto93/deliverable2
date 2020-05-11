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
	
	public BugTicket[] getBugTicket(String projName) {
		List<BugTicket> tickets = JIRATicketRetriever.getBugTicket(projName);
		StringBuilder bld = new StringBuilder();
		int i = 0;
		while(i < tickets.size()) {
			List<GitCommit> commits = getGitCommits(tickets.get(i));
			if (commits.isEmpty()) {
				bld.append(tickets.get(i).getKey() + " ");
				tickets.remove(i);
			} else {
				tickets.get(i).setFileNames(getFileModified(commits));
				tickets.get(i).setResolutionDate(commits.get(0).getDate());
				i++;
			}
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info("removed empty ticket: " + bld.toString());
		
		handleMissingFV(tickets);
		return tickets.toArray(new BugTicket[0]);
	}
	
	private void handleMissingFV(List<BugTicket> tickets) {
		StringBuilder bld = new StringBuilder();
		int i = 0;
		while (i < tickets.size()) {
			if (tickets.get(i).getFixedVersions().length == 0) {
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
