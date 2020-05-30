package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import git.GitLogRetriever;
import jira.JIRATicketRetriever;
import model.BugTicket;
import model.GitCommit;
import model.Release;
import model.ReleaseInfo;

public class ReleaseController {
	GitLogRetriever retriever;
	private static final Logger LOGGER = Logger.getLogger(ReleaseController.class.getName());
	
	public ReleaseController(GitLogRetriever retriever) {
	    this.retriever = retriever;
	}
	
	public ReleaseInfo[] getReleaseInfo(String projName) {
		List<ReleaseInfo> relInfo = JIRATicketRetriever.getReleaseInfo(projName);
		int i = 1;
		while(i < relInfo.size()) {
			String after = relInfo.get(i - 1).getDate().toLocalDate().toString();
			String before = relInfo.get(i).getDate().toLocalDate().toString();
			List<GitCommit> commits = getCommits(retriever.getCommitsHashByDate(before, after));
			if (commits.isEmpty()) {
				LOGGER.info("Release " + relInfo.get(i).getVersionName() + " has no commit");
				relInfo.remove(i);
			}
			else {
				i++;
			}
		}
		return relInfo.toArray(new ReleaseInfo[0]);
	}
	
	public Release[] getRelease(ReleaseInfo[] relInfo, List<BugTicket> bugs) {	
		List<Release> releases = new ArrayList<>(relInfo.length);
		List<GitCommit> commits = getCommits(retriever.getCommitsHashByDate(relInfo[0].getDate().toLocalDate().toString()));
		releases.add(new Release(relInfo[0], commits, getBugByRelease(relInfo[0], bugs)));
		
		for (int i = 1; i < relInfo.length; i++) {
			String after = relInfo[i - 1].getDate().toLocalDate().toString();
			String before = relInfo[i].getDate().toLocalDate().toString();
			commits = getCommits(retriever.getCommitsHashByDate(before, after));
			if (!commits.isEmpty()) {
				releases.add(new Release(relInfo[i], commits, getBugByRelease(relInfo[i], bugs)));
			} else {
				LOGGER.warning("Release " + relInfo[i].getVersionName() + " has no commit");
			}
		}
		
		return releases.toArray(new Release[0]);
	}
	
	private List<GitCommit> getCommits(List<String> hashes) {
		List<GitCommit> commits = new ArrayList<>();
		for (String hash : hashes) {
			LocalDate date = retriever.getCommitDate(hash);
			commits.add(new GitCommit(hash, date));
		}
		
		return commits;
	}
	
	private BugTicket[] getBugByRelease(ReleaseInfo release, List<BugTicket> tickets) {
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
