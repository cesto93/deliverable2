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
	private static final Logger LOGGER = Logger.getLogger(InstancesRepository.class.getName());
	
	public InstancesRepository(String file) {
		try {
			DataSource source1 = new DataSource(file);
			instances = source1.getDataSet();
			
		} catch (Exception e) {
			LOGGER.warning(e.toString());
		}
	}
	
	public Instances getInstances(int start, int end) {
		Instances res = new Instances(instances);
		
		int numAttr = res.numAttributes();
		res.setClassIndex(numAttr - 1);
		removeVersionBefore(res, start);
		removeVersionAfter(res, end);
		return res;
	}
	
	public static void removeVersionBefore(Instances insts, int end) {
		for (int i = insts.numInstances() - 1; i >= 0; i--) {
		    if (insts.get(i).value(0) < end) {
		    	insts.delete(i);
		    }
		}
	}
	
	public static void removeVersionAfter(Instances insts, int start) {
		for (int i = insts.numInstances() - 1; i >= 0; i--) {
		    if (insts.get(i).value(0) > start) {
		    	insts.delete(i);
		    }
		}
	}
	
	public static AttributeSelection getAttributeSelection(Instances inst){
		AttributeSelection attSelection = new AttributeSelection();
	    CfsSubsetEval eval = new CfsSubsetEval();
	    BestFirst search = new BestFirst();
	    attSelection.setEvaluator(eval);
	    attSelection.setSearch(search);
	    try {
			attSelection.SelectAttributes(inst);
			return attSelection;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	    	return null;
	}
}
