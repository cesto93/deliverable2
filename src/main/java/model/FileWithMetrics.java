package model;



public class FileWithMetrics extends GitFile {
	private boolean buggy;
	private int loc;
	private int nRevisions;
	private int nAuth;
	
	public enum CSVField {
		VERSION,
		FILENAME,
	    BUGGY,
	    LOC,
	    NREVISIONS,
	    NAUTH;
	}
	
	public FileWithMetrics(String name, String hash) {
		super(name, hash);
		this.buggy = false;
		loc = -1;
		nRevisions = -1;
		nAuth = -1;
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

	public int getnRevisions() {
		return nRevisions;
	}

	public void setnRevisions(int nRevisions) {
		this.nRevisions = nRevisions;
	}

	public int getnAuth() {
		return nAuth;
	}

	public void setnAuth(int nAuth) {
		this.nAuth = nAuth;
	}
	
	public static String getFieldName(CSVField field) {
		 final String[] names =  {"Version", "File Name", "LOC", "NR", "NAuth",  "Buggy"};
		 switch (field) {
		 case VERSION: return names[0];
		 case FILENAME: return names[1];
		 case LOC: return names[2];
		 case NREVISIONS: return names[3];
		 case NAUTH: return names[4];
		 case BUGGY: return names[5];
		 
		 default: return null;
		 }
	}
	
	public String getBuggyText() {
		if (buggy)
			return "YES";
		else
			return "NO";
	}

}
