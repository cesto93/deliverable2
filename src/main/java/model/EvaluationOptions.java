package model;


public class EvaluationOptions implements Comparable<EvaluationOptions> {
	private MyClassifier classifier;
	private boolean featureSelection;
	private MySampling sampling;
	
	public enum MyClassifier {
		RANDOMFOREST, NAIVEBAYES, IBK;
		
		private final String[] names = new String[] {"Random Forest", "Naive Bayes", "IBk"};
		
		@Override
		public String toString() {
			return names[this.ordinal()];
		}
	}
	
	public enum MySampling {
		NOSAMPLING, OVERSAMPLING, UNDERSAMPLING, SMOTE;
		
		private final String[] names = new String[] {"No sampling", "Oversampling", "Undersampling", "Smote"};
		
		@Override
		public String toString() {
			return names[this.ordinal()];
		}
	}

	public EvaluationOptions(MyClassifier classifier, boolean featureSelection, MySampling sampling) {
		super();
		this.featureSelection = featureSelection;
		this.sampling = sampling;
		this.classifier = classifier;
	}

	public boolean isFeatureSelection() {
		return featureSelection;
	}

	public void setFeatureSelection(boolean featureSelection) {
		this.featureSelection = featureSelection;
	}

	public MySampling getSampling() {
		return sampling;
	}

	public void setSampling(MySampling sampling) {
		this.sampling = sampling;
	}

	public MyClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(MyClassifier classifier) {
		this.classifier = classifier;
	}
	
	@Override
    public int hashCode() {
		return classifier.ordinal() * 100 + (sampling.ordinal()) * 10 + (featureSelection ? 1 : 0);
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        EvaluationOptions other = (EvaluationOptions) obj;
        return (classifier == other.classifier && featureSelection == other.featureSelection &&
        		sampling == other.sampling);
    }

	@Override
	public int compareTo(EvaluationOptions o) {
		return this.hashCode() - o.hashCode();
	}
	
}
