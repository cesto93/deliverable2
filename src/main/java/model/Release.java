package model;

public class Release {
	private ReleaseInfo releaseInfo;
	private GitCommit[] commits;
	private BugTicket[] bugs;
	
	public Release(ReleaseInfo releaseInfo, GitCommit[] commits, BugTicket[] bugs) {
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
	public GitCommit[] getCommits() {
		return commits;
	}
	public void setCommits(GitCommit[] commits) {
		this.commits = commits;
	}

	public BugTicket[] getBugs() {
		return bugs;
	}

	public void setBugs(BugTicket[] bugs) {
		this.bugs = bugs;
	}
	
	public GitCommit getLastCommit() {
		if (this.getCommits().length == 0)
			return null;
		return this.getCommits()[this.getCommits().length - 1];
	}
	
}
