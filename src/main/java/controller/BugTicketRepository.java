package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import model.BugTicket;
import model.GitCommit;

public class BugTicketRepository {
	
	private GitLogRetriever retriever;
	private String[] extTaken;
	
	public BugTicketRepository(GitLogRetriever retriever, String[] extTaken) {
		this.retriever = retriever;
		this.extTaken = extTaken;
	}
	
	private List<GitCommit> getGitCommits(BugTicket ticket) {	
		List<String> hashes = retriever.getCommitsHash(ticket.getKey());
		TreeSet<GitCommit> temp = new TreeSet<>(GitCommit.getComparator());
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
		BugTicket[] tickets = JIRATicketRetriever.getTicketKeysAndVersion(projName);
		for (BugTicket ticket : tickets) {
		ticket.setFileNames(getFileModified(getGitCommits(ticket)));
		}
		return tickets;
	}
	
	
}
