package weka;

import java.util.logging.Level;
import java.util.logging.Logger;

import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class InstancesRepository {
	
	private Instances instances;
	private Instances instancesFS;
	private static final Logger LOGGER = Logger.getLogger(InstancesRepository.class.getName());
	
	public InstancesRepository(String file) {
		try {
			DataSource source1 = new DataSource(file);
			instances = source1.getDataSet();
			instancesFS = selectFeatures(instances);
			
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
	}
	
	public Instances getTraining(int nRevTrain, boolean useFS) {
		Instances training;
		if (useFS) {
			training = new Instances(instancesFS);
		} else {
			training = new Instances(instances);
		}
		
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		removeVersionAfter(training, nRevTrain);
		return training;
	}
	
	public Instances getTest(int nRevTrain, boolean useFS) {
		Instances testing;
		if (useFS) {
			testing = new Instances(instancesFS);
		} else {
			testing = new Instances(instances);
		}
		int numAttr = testing.numAttributes();
		testing.setClassIndex(numAttr - 1);
		removeVersionUntil(testing, nRevTrain);
		return testing;
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
	
	private Instances selectFeatures(Instances inst){
		AttributeSelection attSelection = new AttributeSelection();
	    CfsSubsetEval eval = new CfsSubsetEval();
	    BestFirst search = new BestFirst();
	    attSelection.setEvaluator(eval);
	    attSelection.setSearch(search);
	    try {
			attSelection.SelectAttributes(inst);
			return attSelection.reduceDimensionality(inst);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage(), e);
		}
	    	return null;
	}
}
