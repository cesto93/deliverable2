package model;

import java.util.Comparator;

public class FileByRelease {
	private Release release;
	private GitFileWithMetrics file;
	
	public FileByRelease(Release release, GitFile file) {
		this.file = new GitFileWithMetrics(file.getName(), file.getHash());
		this.release = release;
	}
	
	public FileByRelease(Release release, GitFileWithMetrics file) {
		this.file = file;
		this.release = release;
	}
	
	public Release getRelease() {
		return release;
	}
	
	public void setRelease(Release releaseInfo) {
		this.release = releaseInfo;
	}
	
	public GitFileWithMetrics getFile() {
		return file;
	}

	public void setFile(GitFileWithMetrics file) {
		this.file = file;
	}
	
	public int compareName(FileByRelease gitFile) {
		return this.getFile().getName().compareTo(gitFile.getFile().getName());
	}

	public static Comparator<FileByRelease> getComparator() {
		Comparator<FileByRelease> byRelease = (FileByRelease a, FileByRelease b) -> 
						a.getRelease().compareDate(b.getRelease());
		Comparator<FileByRelease> byFile = (FileByRelease c, FileByRelease d) -> 
						c.compareName(d);
		return byRelease.thenComparing(byFile);
	}
}
