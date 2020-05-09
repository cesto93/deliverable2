package model;

import java.util.HashMap;
import java.util.Map;

import model.CSVField.CSVFields;

public class FileWithMetrics extends GitFile {
	private boolean buggy;
	private Map<String, Integer> metrics;
	
	public FileWithMetrics(String name, String hash) {
		super(name, hash);
		metrics = new HashMap<>();
		buggy = false;
	}
	
	public boolean isBuggy() {
		return buggy;
	}

	public void setBuggy(boolean bugginess) {
		this.buggy = bugginess;
	}
	
	public String getBuggyText() {
		if (buggy)
			return "YES";
		else
			return "NO";
	}
	
	public void addMetric(CSVFields field, int value) {
		metrics.put(CSVField.getFieldName(field), value);
	}
	
	public Object getFieldValue(CSVFields field) {
		switch(field) {
		case BUGGY: return this.buggy;
		case FILENAME: return this.getName();
		default: return this.metrics.get(CSVField.getFieldName(field));
		}
	}
	
	public void setLoc(int val) {
		addMetric(CSVFields.LOC, val);
	}
	
	public void setnAuth(int val) {
		addMetric(CSVFields.NAUTH, val);
	}
	
	public void setnRevisions(int nRevisions) {
		addMetric(CSVFields.NREVISIONS, nRevisions);
	}
	
	public void setnFix(int nFix) {
		addMetric(CSVFields.NFIX, nFix);
	}

	public void setLocAdded(int locAdded) {
		addMetric(CSVFields.LOCADDED, locAdded);
	}
	
	public void setAvgLocAdded(int avgLocAdded) {
		addMetric(CSVFields.AVGLOCADDED, avgLocAdded);
	}
	
	public void setLocTouched(int locTouched) {
		addMetric(CSVFields.LOCTOUCHED, locTouched);
	}
	
	public void setChurn(int churn) {
		addMetric(CSVFields.CHURN, churn);
	}
	
	public void setAvgChurn(int avgChurn) {
		addMetric(CSVFields.AVGCHURN, avgChurn);
	}
	
	public void setAge(int age) {
		addMetric(CSVFields.AGE, age);
	}
	
	public int getChurn() {
		return this.metrics.get(CSVField.getFieldName(CSVFields.CHURN));
	}
	
	public int getnRevisions() {
		return this.metrics.get(CSVField.getFieldName(CSVFields.NREVISIONS));
	}
	
	public int getLocAdded() {
		return this.metrics.get(CSVField.getFieldName(CSVFields.LOCADDED));
	}
	
}
