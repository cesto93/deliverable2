package model;

public class GitFileWithMetrics extends GitFile {
	private boolean buggy;
	private int LOC;
	
	public GitFileWithMetrics(String name, String hash) {
		super(name, hash);
		this.buggy = false;
		LOC = -1;
	}
	
	public boolean isBuggy() {
		return buggy;
	}

	public void setBuggy(boolean bugginess) {
		this.buggy = bugginess;
	}
	
	public int getLOC() {
		return LOC;
	}

	public void setLOC(int lOC) {
		LOC = lOC;
	}

}
