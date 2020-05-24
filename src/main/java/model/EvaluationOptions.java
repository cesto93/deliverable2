package model;


public class EvaluationOptions implements Comparable<EvaluationOptions> {
	private MyClassifier classifier;
	private boolean featureSelection;
	private MySampling sampling;
	
	public enum MyClassifier {
		RANDOMFOREST("Random Forest"), NAIVEBAYES("Naive Bayes") , IBK("IBk");
		
		private String name;
		
		private MyClassifier(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	public enum MySampling {
		NOSAMPLING("No sampling"), OVERSAMPLING("Oversampling"), UNDERSAMPLING("Undersampling"), SMOTE("Smote");
		
		private String name;
		
		private MySampling(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}

	public EvaluationOptions(MyClassifier classifier, boolean featureSelection, MySampling sampling) {
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
