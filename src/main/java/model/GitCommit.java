package model;

import java.time.LocalDate;
import java.util.List;

public class GitCommit  {
	private String hash;
	private LocalDate date;
	private List<String> fileNames; 
	
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

	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        GitCommit other = (GitCommit) obj;
        return this.hash.equals(other.getHash());
	}

	@Override
	public int hashCode() {
		return this.getHash().hashCode();
	}
	
	
	
}


