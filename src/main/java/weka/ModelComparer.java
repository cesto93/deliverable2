package weka;

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

public class ModelComparer {
	private static final Logger LOGGER = Logger.getLogger(ModelComparer.class.getName());
	
	private ModelComparer() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static EvaluationResult compare(String name, String file) {
		try {
			InstancesRepository repo = new InstancesRepository(file);
			int nRel = repo.getInstances().numDistinctValues(0);
			final int classIndex = 1;
			
			EvaluationResult result = new EvaluationResult(name);
			MyClassifier[] myClassifiers = {MyClassifier.RANDOMFOREST, MyClassifier.NAIVEBAYES, MyClassifier.IBK};
			MySampling[] mySamplings = {MySampling.NOSAMPLING, MySampling.OVERSAMPLING, MySampling.UNDERSAMPLING ,
										};
			
			for (int i = 0; i < nRel - 1; i++) {
				Instances training = repo.getInstances(1, i + 1);
				Instances testing = repo.getInstances(i + 2, i + 2);
				Evaluator evalModel = new Evaluator(training, testing);
				double defectiveTestingPerc = getDefectivePerc(training);
				double majorityPerc = Math.max(defectiveTestingPerc, 100 - defectiveTestingPerc);
				
				Map<EvaluationOptions, CompactEvaluation> map = new TreeMap<>();
				
				for (MyClassifier myClassifier : myClassifiers) {
					for (MySampling mySampling: mySamplings) {
						EvaluationOptions evalOpts = new EvaluationOptions(myClassifier, false, mySampling);
						map.put(evalOpts, 
								new CompactEvaluation(evalModel.evaluateWithOptions(evalOpts, majorityPerc), classIndex));
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
								new CompactEvaluation(evalModel.evaluateWithOptions(evalOpts, majorityPerc), classIndex));
					}
				}
				
				result.getEval().add(map);
				result.getDefectiveTesting().add(defectiveTestingPerc);
				result.getDefectiveTraining().add(getDefectivePerc(training));
			}
			return result;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	private static double getDefectivePerc(Instances insts) {
		int def = 0;
		int tot = 0;
		for (int i = insts.numInstances() - 1; i >= 0; i--) {
		    if (insts.get(i).stringValue(insts.classIndex()).equals("YES")) {
		    	def++;
		    }
		    tot++;
		}
		return (((double) def) / tot) * 100;
	}
	
}
