package model;

public class GitFileWithMetrics extends GitFile {
	private boolean buggy;
	private int loc;
	
	public GitFileWithMetrics(String name, String hash) {
		super(name, hash);
		this.buggy = false;
		loc = -1;
	}
	
	public boolean isBuggy() {
		return buggy;
	}

	public void setBuggy(boolean bugginess) {
		this.buggy = bugginess;
	}
	
	public int getLOC() {
		return loc;
	}

	public void setLOC(int loc) {
		this.loc = loc;
	}

}
