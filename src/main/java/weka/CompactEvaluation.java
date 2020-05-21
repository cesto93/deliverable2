package weka;

import weka.classifiers.Evaluation;

public class CompactEvaluation {
	private double tp;
	private double fp;
	private double tn;
	private double fn;
	private double precision;
	private double recall;
	private double auc;
	private double kappa;
	
	public CompactEvaluation(Evaluation eval, int classIndex) {
		this.tp = eval.numTruePositives(classIndex);;
		this.fp = eval.numFalsePositives(classIndex);
		this.tn = eval.numTrueNegatives(classIndex);
		this.fn = eval.numFalseNegatives(classIndex);
		this.precision = eval.precision(classIndex);;
		this.recall = eval.recall(classIndex);;
		this.auc = eval.areaUnderROC(classIndex);;
		this.kappa = eval.kappa();
	}
	
	public double getPrecision() {
		return precision;
	}
	public void setPrecision(double precision) {
		this.precision = precision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public double getKappa() {
		return kappa;
	}
	public void setKappa(double kappa) {
		this.kappa = kappa;
	}
	public double getAuc() {
		return auc;
	}
	public void setAuc(double auc) {
		this.auc = auc;
	}

	public double getTp() {
		return tp;
	}

	public void setTp(double tp) {
		this.tp = tp;
	}

	public double getFp() {
		return fp;
	}

	public void setFp(double fp) {
		this.fp = fp;
	}

	public double getTn() {
		return tn;
	}

	public void setTn(double tn) {
		this.tn = tn;
	}

	public double getFn() {
		return fn;
	}

	public void setFn(double fn) {
		this.fn = fn;
	}
}
