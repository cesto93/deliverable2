package model;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class GitCommit implements Comparable<GitCommit> {
	private String hash;
	private Map<String, GitFile> files;
	private LocalDate date;
	
	public GitCommit(String hash, LocalDate date) {
		this.setHash(hash);
		this.setDate(date);
		files = new TreeMap<>();
	}
	
	public GitCommit(String hash, Map <String, GitFile> files) {
		this.setHash(hash);
		this.setFiles(files);
	}
	
	@Override
	public String toString() {
		return "GitCommit [hash=" + hash + "]";
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public Map<String, GitFile> getFiles() {
		return files;
	}

	public void setFiles(Map<String, GitFile> files) {
		this.files = files;
	}

	
	public int compareTo(GitCommit commit) {
		return this.getDate().compareTo(commit.getDate());
	}
	
}


