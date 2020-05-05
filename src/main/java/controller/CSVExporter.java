package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import model.CSVField;
import model.FileByRelease;
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
	
	public static void printGitFileByRelease(List<FileByRelease> files, String file) {
		
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
				printer.printRecord(CSVField.getFieldsName(fields));
		    	
		    	for (int i = 0; i < files.size(); i++) {
					for (int j = 0; j < files.get(i).getFiles().size(); j++) {
						printer.printRecord(files.get(i).getFieldsValues(i + 1, j, fields));
						//printGitFileByRelease(printer, i + 1, gitFile);
					}
				}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
