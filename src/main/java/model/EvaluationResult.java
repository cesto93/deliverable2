package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import weka.CompactEvaluation;

public class EvaluationResult {
	private String dataset;
	private List<Map<EvaluationOptions, CompactEvaluation>> eval;
	private List<Double> defectiveTraining;
	private List<Double> defectiveTesting;
	
	public EvaluationResult(String dataset) {
		this.setDateset(dataset);
		this.eval = new ArrayList<>();
		this.setDefectiveTesting(new ArrayList<>());
		this.setDefectiveTraining(new ArrayList<>());
	}
	
	public String getDateset() {
		return dataset;
	}
	public void setDateset(String dataset) {
		this.dataset = dataset;
	}

	public List<Map<EvaluationOptions, CompactEvaluation>> getEval() {
		return eval;
	}

	public void setEval(List<Map<EvaluationOptions, CompactEvaluation>> eval) {
		this.eval = eval;
	}

	public List<Double> getDefectiveTraining() {
		return defectiveTraining;
	}

	public void setDefectiveTraining(List<Double> defectiveTraining) {
		this.defectiveTraining = defectiveTraining;
	}

	public List<Double> getDefectiveTesting() {
		return defectiveTesting;
	}

	public void setDefectiveTesting(List<Double> defectiveTesting) {
		this.defectiveTesting = defectiveTesting;
	}
	
}
