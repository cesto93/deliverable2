package weka;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.EvaluationOptions;
import model.EvaluationResult;
import model.EvaluationOptions.MyClassifier;
import model.EvaluationOptions.MySampling;
import weka.attributeSelection.AttributeSelection;
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
			
			EvaluationResult result = new EvaluationResult(name);
			List<Map<EvaluationOptions, CompactEvaluation>> list = result.getEval();
			
			final int classIndex = 1;
			
			MyClassifier[] myClassifiers = {MyClassifier.RANDOMFOREST, MyClassifier.NAIVEBAYES, MyClassifier.IBK};
			MySampling[] mySamplings = {MySampling.NOSAMPLING, MySampling.OVERSAMPLING, MySampling.UNDERSAMPLING};
			
			for (int i = 0; i < nRel - 1; i++) {
				Instances training = repo.getInstances(1, i + 1);
				Instances testing = repo.getInstances(i + 2, i + 2);
				Evaluator evalModel = new Evaluator(training, testing);
				
				Map<EvaluationOptions, CompactEvaluation> map = new TreeMap<>();
				
				for (MyClassifier myClassifier : myClassifiers) {
					for (MySampling mySampling: mySamplings) {
						EvaluationOptions evalOpts = new EvaluationOptions(myClassifier, false, mySampling);
						map.put(evalOpts, 
								new CompactEvaluation(evalModel.evaluateWithOptions(evalOpts), classIndex));
					}
				}
				
				AttributeSelection selection = InstancesRepository.getAttributeSelection(training);
				Instances trainingFS = selection.reduceDimensionality(training);
				Instances testingFS = selection.reduceDimensionality(testing);
				evalModel = new Evaluator(trainingFS, testingFS);
				
				for (MyClassifier myClassifier : myClassifiers) {
					for (MySampling mySampling: mySamplings) {
						EvaluationOptions evalOpts = new EvaluationOptions(myClassifier, true, mySampling);
						map.put(evalOpts, 
								new CompactEvaluation(evalModel.evaluateWithOptions(evalOpts), classIndex));
					}
				}
				
				list.add(map);
			}
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
}
