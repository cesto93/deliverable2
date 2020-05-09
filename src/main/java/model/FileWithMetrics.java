package model;

import java.util.HashMap;
import java.util.Map;

import model.CSVField;

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
	
	public void addMetric(CSVField field, int value) {
		metrics.put(field.toString(), value);
	}
	
	public Object getFieldValue(CSVField field) {
		switch(field) {
		case BUGGY: return this.buggy;
		case FILENAME: return this.getName();
		default: return this.metrics.get(field.toString());
		}
	}
	
	public void setLoc(int val) {
		addMetric(CSVField.LOC, val);
	}
	
	public void setnAuth(int val) {
		addMetric(CSVField.NAUTH, val);
	}
	
	public void setnRevisions(int nRevisions) {
		addMetric(CSVField.NREVISIONS, nRevisions);
	}
	
	public void setnFix(int nFix) {
		addMetric(CSVField.NFIX, nFix);
	}

	public void setLocAdded(int locAdded) {
		addMetric(CSVField.LOCADDED, locAdded);
	}
	
	public void setAvgLocAdded(int avgLocAdded) {
		addMetric(CSVField.AVGLOCADDED, avgLocAdded);
	}
	
	public void setLocTouched(int locTouched) {
		addMetric(CSVField.LOCTOUCHED, locTouched);
	}
	
	public void setChurn(int churn) {
		addMetric(CSVField.CHURN, churn);
	}
	
	public void setAvgChurn(int avgChurn) {
		addMetric(CSVField.AVGCHURN, avgChurn);
	}
	
	public void setAge(int age) {
		addMetric(CSVField.AGE, age);
	}
	
	public int getChurn() {
		return this.metrics.get(CSVField.CHURN.toString());
	}
	
	public int getnRevisions() {
		return this.metrics.get(CSVField.NREVISIONS.toString());
	}
	
	public int getLocAdded() {
		return this.metrics.get(CSVField.LOCADDED.toString());
	}
	
}
