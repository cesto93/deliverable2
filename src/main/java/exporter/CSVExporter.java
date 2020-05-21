package exporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import model.CSVField;
import model.FileByRelease;
import model.FileWithMetrics;
import model.Release;
import weka.CompactEvaluation;
import weka.EvaluationOptions;
import weka.EvaluationResult;

public class CSVExporter {
	
	private static final Logger LOGGER = Logger.getLogger(CSVExporter.class.getName());
	private static final CSVField[] fields = new CSVField[]{ CSVField.VERSION, CSVField.FILENAME, 
					CSVField.LOC, CSVField.LOCTOUCHED, CSVField.NREVISIONS, CSVField.NAUTH, CSVField.LOCADDED, 
					CSVField.AVGLOCADDED, CSVField.CHURN, CSVField.AVGCHURN, CSVField.AGE, CSVField.BUGGY};
	
	private CSVExporter() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static void printReleaseInfo(Release[] rs, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
		    	printer.printRecord("Index", "Version ID", "Version Name", "Date");
		    	int pos = 1;
		    	for (Release r : rs) {
		    		printer.printRecord(pos, r.getReleaseInfo().getVersionID(), r.getReleaseInfo().getVersionName(), 
		    							r.getReleaseInfo().getDate());
		    		pos++;
		    	}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static List<Object> getFieldsValues(int nRel, FileWithMetrics file, CSVField[] fields) {
		ArrayList<Object> res = new ArrayList<>();
		res.add(nRel);
		for (int i = 1; i < fields.length; i++)
			res.add(file.getFieldValue(fields[i]));
		return res;
	}
	
	public static void printGitFileByRelease(List<FileByRelease> fbr, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
				printer.printRecord(CSVField.getFieldsName(fields));
		    	
		    	for (int i = 0; i < fbr.size(); i++) {
					for (FileWithMetrics fwm : fbr.get(i).getFiles()) {
						printer.printRecord(getFieldsValues(i + 1, fwm, fields));
					}
				}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static void printEvaluationResult(EvaluationResult result, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
				printer.printRecord("DataSet", "#TrainingRelease", "%training", "Classifier", "Feature Selection", 
									"TP", "FP", "TN", "FN", "Precision", "Recall", "AUC", "Kappa");
				String dataset = result.getDateset();
		    	List<Map<EvaluationOptions, CompactEvaluation>> eval = result.getEval();
		    	for (int i = 0; i < eval.size(); i++) {
		    		Map<EvaluationOptions, CompactEvaluation> map = eval.get(i);
					for (EvaluationOptions key : map.keySet()) {
						double precision =map.get(key).getPrecision();
						double recall = map.get(key).getRecall();
						double auc = map.get(key).getAuc();
						double kappa = map.get(key).getKappa();
						double tp = map.get(key).getTp();
						double fp = map.get(key).getFp();
						double tn =map.get(key).getTn();
						double fn = map.get(key).getFn();
						double perTrain = ((double) (i + 1)) / (eval.size() + 1);
						
						printer.printRecord(dataset, i + 1, perTrain, key.getClassifier().toString(), 
											key.isFeatureSelection() ? "Best First" : "No selection", 
											tp, fp, tn, fn, precision, recall, auc, kappa);
					}
				}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
