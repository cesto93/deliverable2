package model;

import model.CSVField.CSVFields;

public class FileWithMetrics extends GitFile {
	private int loc;
	private int nRevisions;
	private int nFix;
	private int nAuth;
	private int locAdded;
	private int locTouched;
	private int churn;
	private int avgChurn;
	private int age;
	private boolean buggy;
	
	public FileWithMetrics(String name, String hash) {
		super(name, hash);
		loc = -1;
		setnFix(-1);
		nRevisions = -1;
		nAuth = -1;
		setLocAdded(-1);
		setLocTouched(-1);
		setChurn(-1);
		setAvgChurn(-1);
		setAge(-1);
		buggy = false;
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
	
	public String getBuggyText() {
		if (buggy)
			return "YES";
		else
			return "NO";
	}
	
	public Object getFieldValue(CSVFields field) {
		switch(field) {
		case AGE: return this.getAge();
		case AVGCHURN: return this.getAvgChurn();
		case BUGGY: return this.getBuggyText();
		case CHURN: return this.getChurn();
		case FILENAME: return this.getName();
		case LOC: return this.getLOC();
		case LOCADDED: return this.getLocAdded();
		case LOCTOUCHED: return this.getLocTouched();
		case NAUTH: return this.getnAuth();
		case NFIX: return this.getnFix();
		case NREVISIONS: return this.getnRevisions();
		default: return null;
		}
	}

	public int getnFix() {
		return nFix;
	}

	public void setnFix(int nFix) {
		this.nFix = nFix;
	}

	public int getLocAdded() {
		return locAdded;
	}

	public void setLocAdded(int locAdded) {
		this.locAdded = locAdded;
	}

	public int getLocTouched() {
		return locTouched;
	}

	public void setLocTouched(int locTouched) {
		this.locTouched = locTouched;
	}

	public int getAvgChurn() {
		return avgChurn;
	}

	public void setAvgChurn(int avgChurn) {
		this.avgChurn = avgChurn;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getChurn() {
		return churn;
	}

	public void setChurn(int churn) {
		this.churn = churn;
	}

}
