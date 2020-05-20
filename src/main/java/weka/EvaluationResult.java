package weka;

import weka.classifiers.Evaluation;

public class EvaluationResult {
	private String dataset;
	private int trainingRelease;
	private String[] classifier;
	private Evaluation[][] eval;
	private Evaluation[][] evalFS;
	
	public EvaluationResult(String dataset, int nRelease, int nClassifier) {
		this.setDateset(dataset);
		setEval(new Evaluation[nRelease][nClassifier]);
		classifier = new String[nClassifier];
	}
	
	public String getDateset() {
		return dataset;
	}
	public void setDateset(String dataset) {
		this.dataset = dataset;
	}
	public int getTrainingRelease() {
		return trainingRelease;
	}
	public void setTrainingRelease(int trainingRelease) {
		this.trainingRelease = trainingRelease;
	}
	public String[] getClassifier() {
		return classifier;
	}
	public void setClassifier(String[] classifier) {
		this.classifier = classifier;
	}

	public Evaluation[][] getEval() {
		return eval;
	}

	public void setEval(Evaluation[][] eval) {
		this.eval = eval;
	}

	public Evaluation[][] getEvalFS() {
		return evalFS;
	}

	public void setEvalFS(Evaluation[][] evalFS) {
		this.evalFS = evalFS;
	}
	
}
