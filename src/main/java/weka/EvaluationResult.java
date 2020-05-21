package weka;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EvaluationResult {
	private String dataset;
	private List<Map<EvaluationOptions, CompactEvaluation>> eval;
	
	public EvaluationResult(String dataset, int nRelease, int nClassifier) {
		this.setDateset(dataset);
		this.eval = new ArrayList<>();
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
	
}
