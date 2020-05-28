package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	
	public List<BugTicket> getBugTicket(String projName,  ReleaseInfo[] rels) {
		List<BugTicket> tickets = JIRATicketRetriever.getBugTicket(projName);
		StringBuilder bld = new StringBuilder();
		int i = 0;
		while(i < tickets.size()) {
			List<GitCommit> commits = getGitCommits(tickets.get(i));
			if (commits.isEmpty()) {
				bld.append(tickets.get(i).getKey() + " ");
				tickets.remove(i);
			} else {
				tickets.get(i).setCommits(commits);
				setFileModified(commits);
				i++;
			}
		}
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.info("removed empty ticket: " + bld.toString());
		
		removeUnkownFV(tickets, rels);
		removeUnkownAV(tickets, rels);
		removeBadFV(tickets, rels);
		handleMissingFV(tickets, rels);
		return tickets;
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
	
	private void setFileModified(List<GitCommit> commits) {
		for (GitCommit commit : commits) {
				commit.setFileNames(retriever.getFilesModifiedByCommit(commit.getHash(), extTaken));
		}
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
	
	// remove fv that have a date previous of the ticket creation
	public static void removeBadFV(List<BugTicket> tickets, ReleaseInfo[] rels) {
		Map<String, LocalDateTime> relDates = new HashMap<>();
		for (ReleaseInfo rel : rels) {
			relDates.put(rel.getVersionID(), rel.getDate());
		}
		
		for (BugTicket ticket : tickets) {
			ticket.getFixedVersions().removeIf(fv -> relDates.get(fv).isBefore(ticket.getCreationDate().atStartOfDay()));
		}
	}
	
	//if FV is missing set it as ticket last commit version
	private void handleMissingFV(List<BugTicket> tickets, ReleaseInfo[] rels) {
		for (BugTicket ticket : tickets) {
			if (ticket.getFixedVersions().isEmpty()) {
				for (ReleaseInfo rel : rels) {
					List<String> hashes = retriever.getCommitsHash(ticket.getKey());
					LocalDate lastCommitDate =  retriever.getCommitDate(hashes.get(0));
					if (rel.getDate().isAfter(lastCommitDate.atStartOfDay())) {
						ticket.getFixedVersions().add(rel.getVersionID());
						if (LOGGER.isLoggable(Level.INFO))
							LOGGER.info("add FV to ticket : " + ticket.getKey());
						return;
					}
				}
			}
		}
	}
	
}
