package weka;

import java.util.logging.Level;
import java.util.logging.Logger;

import model.EvaluationOptions;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

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
	
	public Evaluation evaluateWithOptions(EvaluationOptions options) {
		Classifier classifier;
		switch (options.getClassifier()) {
		case IBK:
			classifier = new IBk();
			break;
		case NAIVEBAYES:
			classifier = new NaiveBayes();
			break;
		case RANDOMFOREST:
			classifier = new RandomForest();
			break;
		default:
			return null;
		}
		
		switch (options.getSampling()) {
		case NOSAMPLING:
			break;
		case OVERSAMPLING:
			classifier = getResample(classifier, training);
			break;
		case SMOTE:
			classifier = getSmote(classifier, training);
			break;
		case UNDERSAMPLING:
			classifier = getSubsample(classifier, training);
			break;
		default:
			return null;
		}
		
		return evaluate(classifier);
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
	
	public static FilteredClassifier getSubsample(Classifier classifier, Instances inst) {
		try {
			SpreadSubsample  spreadSubsample = new SpreadSubsample();
			String[] opts = new String[]{ "-M", "1.0"};
			spreadSubsample.setOptions(opts);
			spreadSubsample.setInputFormat(inst);
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(spreadSubsample);
			fc.setClassifier(classifier);
			return fc;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	public static FilteredClassifier getSmote(Classifier classifier, Instances inst) {
		try {
			SMOTE smote = new SMOTE();
			smote.setInputFormat(inst);
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(smote);
			fc.setClassifier(classifier);
			return fc;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
}
