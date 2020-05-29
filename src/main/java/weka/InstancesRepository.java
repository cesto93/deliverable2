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
	
	public Instances getInstances() {
		Instances res = new Instances(instances);
		
		int numAttr = res.numAttributes();
		res.setClassIndex(numAttr - 1);
		return res;
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
	    try {
		    String[] opts = new String[]{ "-D", "2", "-N", String.valueOf(inst.numAttributes() - 1)};
		    search.setOptions(opts);
		    attSelection.setEvaluator(eval);
		    attSelection.setSearch(search);
			attSelection.SelectAttributes(inst);
			logAttributeSelected(attSelection, inst);
			return attSelection;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	    	return null;
	}
	
	private static void logAttributeSelected(AttributeSelection attSelection, Instances inst) throws Exception {
		if (LOGGER.isLoggable(Level.INFO)) {
			int[] attributes = attSelection.selectedAttributes();
			StringBuilder bld = new StringBuilder();
			bld.append("Attributes selected: ");
			for (int attribute : attributes) {
				bld.append(inst.attribute(attribute).name() + " ");
			}
			LOGGER.info(bld.toString());
		}
	}
}
