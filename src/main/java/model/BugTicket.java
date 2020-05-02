package model;

import java.util.List;

public class BugTicket {
	private String key;
	private String[] affectedVersions;
	private String[] fixedVersions;
	private List<String> fileNames;
	
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
		StringBuilder bld = new StringBuilder();
		bld.append(key);
		bld.append(" Affected versions: ");
		for (String av : this.affectedVersions) {
			bld.append(av);
			bld.append(" ");
		}
		bld.append("Fixed versions: ");
		for (String fv : this.fixedVersions) {
			bld.append(fv);
			bld.append(" ");
		}
		return bld.toString();
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<String> fileNames) {
		this.fileNames = fileNames;
	}
}
