package model;

import java.util.Comparator;

public class GitFileWithRelease {
	private String name;
	private ReleaseInfo releaseInfo;
	private boolean bugginess;
	
	public GitFileWithRelease(String name, ReleaseInfo releaseInfo, boolean bugginess) {
		this.name = name;
		this.releaseInfo = releaseInfo;
		this.bugginess = bugginess;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ReleaseInfo getReleaseInfo() {
		return releaseInfo;
	}
	
	public void setReleaseInfo(ReleaseInfo releaseInfo) {
		this.releaseInfo = releaseInfo;
	}
	
	public Boolean getBugginess() {
		return bugginess;
	}
	public void setBugginess(Boolean bugginess) {
		this.bugginess = bugginess;
	}
	
	public int compareName(GitFileWithRelease gitFile) {
		return this.getName().compareTo(gitFile.getName());
	}
	

	public static Comparator<GitFileWithRelease> getComparator() {
		Comparator<GitFileWithRelease> byRelease = (GitFileWithRelease a, GitFileWithRelease b) -> 
						a.getReleaseInfo().compareDate(b.getReleaseInfo());
		Comparator<GitFileWithRelease> byFile = (GitFileWithRelease c, GitFileWithRelease d) -> 
						c.compareName(d);
		return byRelease.thenComparing(byFile);
	}
	
	
}
