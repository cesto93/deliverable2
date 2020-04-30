package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import model.BugTicket;
import model.GitCommit;
import model.GitFileWithMetrics;

public class GitObjectController {
	
	private GitLogRetriever retriever;
	private String[] extTaken;
	
	public GitObjectController(GitLogRetriever retriever, String[] extTaken) {
		this.retriever = retriever;
		this.extTaken = extTaken;
	}
	
	public void setGitCommitsTickets(BugTicket[] tickets) {	
		for (BugTicket ticket :  tickets) {
			List<String> hashes = retriever.getCommitsHash(ticket.getKey());
			//List<LocalDate> dates = retriever.getCommitsDate(ticket.getKey());
			TreeSet<GitCommit> temp = new TreeSet<>(GitCommit.getComparator());
			for (int i = 0; i < hashes.size(); i++) {
				LocalDate date = retriever.getCommitDate(hashes.get(i));
				temp.add(new GitCommit(hashes.get(i), date));
				
			}
			ticket.setCommits(temp.toArray(new GitCommit[0]));
		}
	}
	
	public void addFileToCommits(BugTicket[] tickets) {
		for (BugTicket  ticket : tickets) {
			for (GitCommit commit : ticket.getCommits()) {
				commit.setFiles(retriever.getFiles(commit.getHash(), extTaken));
			}
		}
	}
	
	public void setLOC(GitFileWithMetrics file) {
		file.setLOC(retriever.getLOC(file.getHash()));
	}
	
	public List<String> getFileModifiedByTicket(BugTicket ticket) {
		ArrayList<String> modifiedFiles = new ArrayList<>();
		for (GitCommit commit : ticket.getCommits()) {
			modifiedFiles.addAll(retriever.getFilesModifiedByCommit(commit.getHash(), extTaken));
		}
		return modifiedFiles;
	}
	
}
