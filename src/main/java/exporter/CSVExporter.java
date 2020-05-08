package exporter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import model.CSVField;
import model.FileByRelease;
import model.FileWithMetrics;
import model.ReleaseInfo;
import model.CSVField.CSVFields;

public class CSVExporter {
	
	private static final Logger LOGGER = Logger.getLogger(CSVExporter.class.getName());
	private static final CSVFields[] fields = new CSVFields[]{ CSVFields.VERSION, CSVFields.FILENAME, 
			CSVFields.LOC, CSVFields.LOCTOUCHED, CSVFields.NREVISIONS, CSVFields.NAUTH, CSVFields.CHURN, CSVFields.BUGGY};
	
	private CSVExporter() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static void printReleaseInfo(ReleaseInfo[] rs, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
		    	printer.printRecord("Index", "Version ID", "Version Name", "Date");
		    	int pos = 1;
		    	for (ReleaseInfo r : rs) {
		    		printer.printRecord(pos, r.getVersionID(), r.getVersionName(), r.getDate());
		    		pos++;
		    	}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static List<Object> getFieldsValues(int nRel, FileWithMetrics file, CSVFields[] fields) {
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
	
}
