package model;

public class GitFile {
	private String name;
	private String hash;
	
	public GitFile(String name, String hash) {
		this.setName(name);
		this.setHash(hash);
	}
	
	@Override
	public String toString() {
		return "GitFile [name=" + name + "]";
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

}
