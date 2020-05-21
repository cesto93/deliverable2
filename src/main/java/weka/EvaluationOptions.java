package weka;


public class EvaluationOptions implements Comparable<EvaluationOptions> {
	private MyClassifier classifier;
	private boolean featureSelection;
	private boolean overSampling;
	
	public enum MyClassifier {
		RANDOMFOREST, NAIVEBAYES, IBK;
		
		private final String[] names = new String[] {"Random Forest", "Naive Bayes", "IBk"};
		
		@Override
		public String toString() {
			return names[this.ordinal()];
		}
	}

	public EvaluationOptions(MyClassifier classifier, boolean featureSelection, boolean overSampling) {
		super();
		this.featureSelection = featureSelection;
		this.overSampling = overSampling;
		this.classifier = classifier;
	}

	public boolean isFeatureSelection() {
		return featureSelection;
	}

	public void setFeatureSelection(boolean featureSelection) {
		this.featureSelection = featureSelection;
	}

	public boolean isOverSampling() {
		return overSampling;
	}

	public void setOverSampling(boolean overSampling) {
		this.overSampling = overSampling;
	}

	public MyClassifier getClassifier() {
		return classifier;
	}

	public void setClassifier(MyClassifier classifier) {
		this.classifier = classifier;
	}
	
	@Override
    public int hashCode() {
		return classifier.ordinal() * 100 + (featureSelection ? 1 : 0) * 10 
				+ (overSampling ? 1 : 0);
	}
	
	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        EvaluationOptions other = (EvaluationOptions) obj;
        if (classifier != other.classifier || featureSelection != other.featureSelection || 
        		overSampling != other.overSampling)
            return false;
        return true;
    }

	@Override
	public int compareTo(EvaluationOptions o) {
		return this.hashCode() - o.hashCode();
	}
	
}
