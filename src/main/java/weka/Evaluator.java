package weka;

import java.util.logging.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class Evaluator {
	private Instances training;
	private Instances testing;
	private static final Logger LOGGER = Logger.getLogger(Evaluator.class.getName());
	
	public Evaluator(Instances training, Instances testing) {
		this.training = training;
		this.testing = testing;
	}
	
	public Evaluation evaluateNaiveBayes() {
		try {
			NaiveBayes classifier = new NaiveBayes();
			classifier.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(classifier, testing);
			return eval;

		} catch (Exception e) {
			LOGGER.warning(e.toString());
		} 
			return null;
	}
	
	public Evaluation evaluateRandomForest() {
		try {
			RandomForest classifier = new RandomForest();
			classifier.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(classifier, testing);
			return eval;
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		} 
			return null;
	}
	
	public Evaluation evaluateIBk() {
		try {
			IBk classifier = new IBk();
			classifier.buildClassifier(training);
			Evaluation eval = new Evaluation(testing);	
			eval.evaluateModel(classifier, testing);
			return eval;
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		} 
			return null;
	}
	
}
