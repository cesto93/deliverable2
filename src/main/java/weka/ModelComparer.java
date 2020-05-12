package weka;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ModelComparer {
	
	
	public static EvaluationResult compare(String name, String file) {
		try {
			DataSource source = new DataSource(file);
			Instances dataset = source.getDataSet();
			int nRel = dataset.numDistinctValues(0);
			System.out.println("nrel " + nRel);
			EvaluationResult result = new EvaluationResult(name, nRel - 1, 3);
			Evaluation[][] eval = new Evaluation[nRel - 1][3];
			String[] classifier = new String[] {"Random Forset", "Naive Bayes", "IBk"};
			for (int i = 1; i < nRel; i++) {
				EvaluateModel evalModel = new EvaluateModel(file, i);
				
				eval[i -1][0] = evalModel.evaluateRandomForest();
				eval[i -1][1] = evalModel.evaluateNaiveBayes();
				eval[i -1][2] = evalModel.evaluateIBk();
			}
			result.setClassifier(classifier);
			result.setEval(eval);
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}