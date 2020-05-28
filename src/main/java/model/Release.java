package model;

import java.util.List;

public class Release {
	private ReleaseInfo releaseInfo;
	private List<GitCommit> commits;
	private BugTicket[] bugs;
	
	public Release(ReleaseInfo releaseInfo, List<GitCommit> commits, BugTicket[] bugs) {
		this.releaseInfo = releaseInfo;
		this.commits = commits;
		this.bugs = bugs;
	}
	
	public ReleaseInfo getReleaseInfo() {
		return releaseInfo;
	}
	public void setRelease(ReleaseInfo releaseInfo) {
		this.releaseInfo = releaseInfo;
	}
	public List<GitCommit> getCommits() {
		return commits;
	}
	public void setCommits(List<GitCommit> commits) {
		this.commits = commits;
	}

	public BugTicket[] getBugs() {
		return bugs;
	}

	public void setBugs(BugTicket[] bugs) {
		this.bugs = bugs;
	}
	
	public GitCommit getLastCommit() {
		if (this.getCommits().isEmpty())
			return null;
		return this.getCommits().get(0);
	}
	
}
