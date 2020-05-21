package weka;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.filters.supervised.instance.Resample;

public class Evaluator {
	private Instances training;
	private Instances testing;
	private static final Logger LOGGER = Logger.getLogger(Evaluator.class.getName());
	
	public Evaluator(Instances training, Instances testing) {
		this.training = training;
		this.testing = testing;
	}
	
	public Evaluation evaluate(Classifier classifier) {
		try {
			classifier.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(classifier, testing);
			return eval;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	public static FilteredClassifier getResample(Classifier classifier, Instances inst) {
		try {
			Resample resample = new Resample();
			resample.setInputFormat(inst);
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(resample);
			fc.setClassifier(classifier);
			return fc;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
}
