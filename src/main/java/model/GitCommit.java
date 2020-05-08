package model;

import java.time.LocalDate;

public class GitCommit  {
	private String hash;
	private LocalDate date;
	
	public GitCommit(String hash, LocalDate date) {
		this.setHash(hash);
		this.setDate(date);
	}
	
	public GitCommit(String hash) {
		this.setHash(hash);
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
	
}


