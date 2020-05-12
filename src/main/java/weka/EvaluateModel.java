package weka;

import java.util.logging.Logger;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class EvaluateModel {
	private Instances training;
	private Instances testing;
	private static final Logger LOGGER = Logger.getLogger(EvaluateModel.class.getName());
	
	public EvaluateModel(String file, int nRevTrain) {
		try {
			DataSource source1 = new DataSource(file);
			training = source1.getDataSet();
			DataSource source2 = new DataSource(file);
			testing = source2.getDataSet();
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);
		removeVersionAfter(training, nRevTrain);
		removeVersionUntil(testing, nRevTrain);
	}
	
	public void removeVersionUntil(Instances insts, int end) {
		for (int i = insts.numInstances() - 1; i >= 0; i--) {
		    if (insts.get(i).value(0) <= end) {
		    	insts.delete(i);
		    }
		}
	}
	
	public void removeVersionAfter(Instances insts, int start) {
		for (int i = insts.numInstances() - 1; i >= 0; i--) {
		    if (insts.get(i).value(0) > start) {
		    	insts.delete(i);
		    }
		}
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
