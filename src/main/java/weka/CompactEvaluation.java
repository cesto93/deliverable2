package weka;

public class CompactEvaluation {
	private double tp;
	private double fp;
	private double tn;
	private double fn;
	private double precision;
	private double recall;
	private double auc;
	private double kappa;
	
	public CompactEvaluation(double tp, double fp, double tn, double fn, 
								double precision, double recall, double auc, double kappa) {
		super();
		this.tp = tp;
		this.fp = fp;
		this.tn = tn;
		this.fn = fn;
		this.precision = precision;
		this.recall = recall;
		this.auc = auc;
		this.kappa = kappa;
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
