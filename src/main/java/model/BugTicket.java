package model;

import java.time.LocalDate;
import java.util.List;

public class BugTicket {
	private String key;
	private List<String> affectedVersions;
	private List<String> fixedVersions;
	private List<GitCommit> commits;
	private LocalDate creationDate;
	
	public BugTicket(String key, List<String> affectedVersions, List<String> fixedVersions, LocalDate creationDate) {
		this.affectedVersions = affectedVersions;
		this.key = key;
		this.fixedVersions = fixedVersions;
		this.creationDate = creationDate;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public List<String> getAffectedVersions() {
		return affectedVersions;
	}
	
	public void setAffectedVersions(List<String> affectedVersions) {
		this.affectedVersions = affectedVersions;
	}
	
	public List<String> getFixedVersions() {
		return fixedVersions;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append(key);
		bld.append(" Affected versions: ");
		for (String av : this.affectedVersions) {
			bld.append(av);
			bld.append(" ");
		}
		bld.append("Fixed versions: ");
		for (String fv : this.fixedVersions) {
			bld.append(fv);
			bld.append(" ");
		}
		return bld.toString();
	}

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate resolutionDate) {
		this.creationDate = resolutionDate;
	}

	public List<GitCommit> getCommits() {
		return commits;
	}

	public void setCommits(List<GitCommit> commits) {
		this.commits = commits;
	}
}
