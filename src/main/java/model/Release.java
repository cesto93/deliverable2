package model;

import java.time.LocalDateTime;


public class Release {
	
	private String versionID;
	private String versionName;
	private LocalDateTime date;
	
	public Release(String id, String name, LocalDateTime date) {
		this.setVersionID(id);
		this.setVersionName(name);
		this.setDate(date);
	}

	public String getVersionID() {
		return versionID;
	}

	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public int compareDate(Release r) {
		return this.date.compareTo(r.date);
	}

}
