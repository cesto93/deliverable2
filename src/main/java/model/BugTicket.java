package model;

public class BugTicket {
	private String key;
	private String[] affectedVersions;
	private String[] fixedVersions;
	
	public BugTicket(String key, String[] affectedVersions, String[] fixedVersions) {
		this.affectedVersions = affectedVersions;
		this.key = key;
		this.fixedVersions = fixedVersions;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	public String[] getAffectedVersions() {
		return affectedVersions;
	}
	
	public String[] getFixedVersions() {
		return fixedVersions;
	}
	
	@Override
	public String toString() {
		String s = key;
		s += " Affected versions: ";
		for (String av : this.affectedVersions) {
			s = s + av + " ";
		}
		s += "Fixed versions: ";
		for (String fv : this.fixedVersions) {
			s = s + fv + " ";
		}
		return s;
		
	}
}
