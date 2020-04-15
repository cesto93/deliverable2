package model;

import java.util.Comparator;

public class FileByRelease {
	private String name;
	private ReleaseInfo releaseInfo;
	private boolean bugginess;
	
	public FileByRelease(String name, ReleaseInfo releaseInfo, boolean bugginess) {
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
	
	public int compareName(FileByRelease gitFile) {
		return this.getName().compareTo(gitFile.getName());
	}
	

	public static Comparator<FileByRelease> getComparator() {
		Comparator<FileByRelease> byRelease = (FileByRelease a, FileByRelease b) -> 
						a.getReleaseInfo().compareDate(b.getReleaseInfo());
		Comparator<FileByRelease> byFile = (FileByRelease c, FileByRelease d) -> 
						c.compareName(d);
		return byRelease.thenComparing(byFile);
	}
	
	
}
