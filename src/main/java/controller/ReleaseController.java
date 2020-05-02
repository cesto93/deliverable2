package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import model.BugTicket;
import model.GitCommit;
import model.Release;
import model.ReleaseInfo;

public class ReleaseController {
	GitLogRetriever retriever;
	
	public ReleaseController(GitLogRetriever retriever) {
	    this.retriever = retriever;
	}
	
	public Release[] getRelease(ReleaseInfo[] relInfo, BugTicket[] bugs) {	
		Release[] releases = new Release[relInfo.length];
		
		releases[0] = new Release(relInfo[0], getCommits(retriever.getCommitsHash(relInfo[0].getDate().toLocalDate())), 
											getBugByRelease(relInfo[0], bugs));
		for (int i = 1; i < relInfo.length; i++) {
			LocalDate after = relInfo[i - 1].getDate().toLocalDate();
			LocalDate before = relInfo[i].getDate().toLocalDate();
			releases[i] = new Release(relInfo[i], getCommits(retriever.getCommitsHash(before, after)), 
										getBugByRelease(relInfo[i], bugs));
		}
		return releases;
	}
	
	private GitCommit[] getCommits(List<String> hashes) {
		TreeSet<GitCommit> commits = new TreeSet<>(GitCommit.getComparator());
		for (String hash : hashes) {
			LocalDate date = retriever.getCommitDate(hash);
			commits.add(new GitCommit(hash, date));
		}
		return commits.toArray(new GitCommit[0]);
	}
	
	private BugTicket[] getBugByRelease(ReleaseInfo release, BugTicket[] tickets) {
		ArrayList<BugTicket> bugs = new ArrayList<>();
		for (BugTicket ticket : tickets) {
			for (String av : ticket.getAffectedVersions()) {
				if (av.equals(release.getVersionID()))
					bugs.add(ticket);
			}
		}
		return bugs.toArray(new BugTicket[0]);
	}
}
