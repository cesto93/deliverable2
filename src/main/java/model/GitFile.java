package model;

public class GitFile {
	private String name;
	private String hash;
	
	public GitFile(String name, String hash) {
		this.name = name;
		this.hash = hash;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public int compareName(GitFile gitFile) {
		return this.getName().compareTo(gitFile.getName());
	}
}
