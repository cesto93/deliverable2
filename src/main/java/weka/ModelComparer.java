package weka;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.classifiers.Evaluation;
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
			Evaluation[][] eval = new Evaluation[nRel - 1][3];
			Evaluation[][] evalFS = new Evaluation[nRel - 1][3];
			String[] classifier = new String[] {"Random Forest", "Naive Bayes", "IBk"};
			
			for (int i = 1; i < nRel; i++) {
				Instances training = repo.getTraining(i, false);
				Instances testing = repo.getTest(i, false);
				Evaluator evalModel = new Evaluator(training, testing);
				
				eval[i -1][0] = evalModel.evaluateRandomForest();
				eval[i -1][1] = evalModel.evaluateNaiveBayes();
				eval[i -1][2] = evalModel.evaluateIBk();
				
				Instances trainingFS = repo.getTraining(i, true);
				Instances testingFS = repo.getTest(i, true);
				evalModel = new Evaluator(trainingFS, testingFS);
				
				evalFS[i -1][0] = evalModel.evaluateRandomForest();
				evalFS[i -1][1] = evalModel.evaluateNaiveBayes();
				evalFS[i -1][2] = evalModel.evaluateIBk();
			}
			result.setClassifier(classifier);
			result.setEval(eval);
			result.setEvalFS(evalFS);
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
}
