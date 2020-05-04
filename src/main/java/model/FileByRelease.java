package model;

import java.util.ArrayList;
import java.util.List;

public class FileByRelease {
	private Release release;
	private List<FileWithMetrics> files;
	
	public FileByRelease(Release release) {
		this.files = new ArrayList<>();
		this.release = release;
	}
	
	public Release getRelease() {
		return release;
	}
	
	public void setRelease(Release release) {
		this.release = release;
	}
	
	public List<FileWithMetrics> getFiles() {
		return files;
	}

	public void setFiles(List<FileWithMetrics> files) {
		this.files = files;
	}
}
