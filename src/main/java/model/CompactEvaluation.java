package model;

import weka.classifiers.Evaluation;

public class CompactEvaluation {
	private Double tp;
	private Double fp;
	private Double tn;
	private Double fn;
	private Double precision;
	private Double recall;
	private Double auc;
	private Double kappa;
	
	public CompactEvaluation(Evaluation eval, int classIndex) {
		if (eval != null) {
		this.tp = eval.numTruePositives(classIndex);
		this.fp = eval.numFalsePositives(classIndex);
		this.tn = eval.numTrueNegatives(classIndex);
		this.fn = eval.numFalseNegatives(classIndex);
		this.precision = eval.precision(classIndex);
		this.recall = eval.recall(classIndex);
		this.auc = eval.areaUnderROC(classIndex);
		this.kappa = eval.kappa();
		} else {
			this.tp = null;
			this.fp = null;
			this.tn = null;
			this.fn = null;
			this.precision = null;
			this.recall = null;
			this.auc = null;
			this.kappa = null;
		}
	}
	
	public Double getPrecision() {
		return precision;
	}
	public void setPrecision(Double precision) {
		this.precision = precision;
	}
	public Double getRecall() {
		return recall;
	}
	public void setRecall(Double recall) {
		this.recall = recall;
	}
	public Double getKappa() {
		return kappa;
	}
	public void setKappa(Double kappa) {
		this.kappa = kappa;
	}
	public Double getAuc() {
		return auc;
	}
	public void setAuc(Double auc) {
		this.auc = auc;
	}

	public Double getTp() {
		return tp;
	}

	public void setTp(Double tp) {
		this.tp = tp;
	}

	public Double getFp() {
		return fp;
	}

	public void setFp(Double fp) {
		this.fp = fp;
	}

	public Double getTn() {
		return tn;
	}

	public void setTn(Double tn) {
		this.tn = tn;
	}

	public Double getFn() {
		return fn;
	}

	public void setFn(Double fn) {
		this.fn = fn;
	}
}
