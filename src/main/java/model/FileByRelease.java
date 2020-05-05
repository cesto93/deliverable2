package model;

import java.util.ArrayList;
import java.util.List;

import model.CSVField.CSVFields;

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
	
	public List<Object> getFieldsValues(int nRel, int  nfile, CSVFields[] fields) {
		ArrayList<Object> res = new ArrayList<>();
		res.add(nRel);
		for (int i = 1; i < fields.length; i++)
			res.add(this.getFiles().get(nfile).getFieldValue(fields[i]));
		return res;
	}
}
