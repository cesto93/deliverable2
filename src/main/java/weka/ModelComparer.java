package weka;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.EvaluationOptions.MyClassifier;
import weka.attributeSelection.AttributeSelection;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ModelComparer {
	private static final Logger LOGGER = Logger.getLogger(ModelComparer.class.getName());
	
	private ModelComparer() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static EvaluationResult compare(String name, String file) {
		try {
			DataSource source = new DataSource(file);
			Instances dataset = source.getDataSet();
			int nRel = dataset.numDistinctValues(0);
			InstancesRepository repo = new InstancesRepository(file);
			
			EvaluationResult result = new EvaluationResult(name, nRel - 1, 3);
			List<Map<EvaluationOptions, CompactEvaluation>> list = result.getEval();
			
			int classIndex = 1;
			
			for (int i = 0; i < nRel - 1; i++) {
				Instances training = repo.getInstances(1, i + 1);
				Instances testing = repo.getInstances(i + 2, i + 2);
				Evaluator evalModel = new Evaluator(training, testing);
				
				Map<EvaluationOptions, CompactEvaluation> map = new TreeMap<>();
				
				map.put(new EvaluationOptions(MyClassifier.RANDOMFOREST, false, false), 
						getCompactEvaluation(evalModel.evaluate(new RandomForest()), classIndex));
				map.put(new EvaluationOptions(MyClassifier.NAIVEBAYES, false, false), 
						getCompactEvaluation(evalModel.evaluate(new NaiveBayes()), classIndex));
				map.put(new EvaluationOptions(MyClassifier.IBK, false, false), 
						 getCompactEvaluation(evalModel.evaluate(new IBk()), classIndex));
				
				AttributeSelection selection = InstancesRepository.getAttributeSelection(training);
				Instances trainingFS = selection.reduceDimensionality(training);
				Instances testingFS = selection.reduceDimensionality(testing);
				evalModel = new Evaluator(trainingFS, testingFS);
				
				map.put(new EvaluationOptions(MyClassifier.RANDOMFOREST, true, false), 
						getCompactEvaluation(evalModel.evaluate(new RandomForest()), classIndex));
				map.put(new EvaluationOptions(MyClassifier.NAIVEBAYES, true, false), 
						getCompactEvaluation(evalModel.evaluate(new NaiveBayes()), classIndex));
				map.put(new EvaluationOptions(MyClassifier.IBK, true, false), 
						 getCompactEvaluation(evalModel.evaluate(new IBk()), classIndex));
				
				list.add(map);
			}
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	private static CompactEvaluation getCompactEvaluation(Evaluation eval, int classIndex) {
		double precision = eval.precision(classIndex);
		double recall = eval.recall(classIndex);
		double auc = eval.areaUnderROC(classIndex);
		double kappa = eval.kappa();
		double tp = eval.numTruePositives(classIndex);
		double fp = eval.numFalsePositives(classIndex);
		double tn = eval.numTrueNegatives(classIndex);
		double fn = eval.numFalseNegatives(classIndex);
		return new CompactEvaluation(tp, fp, tn, fn, precision, recall, auc, kappa);
	}
	
}
