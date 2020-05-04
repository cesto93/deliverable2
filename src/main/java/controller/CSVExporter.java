package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import model.FileByRelease;
import model.FileWithMetrics;
import model.FileWithMetrics.CSVField;
import model.ReleaseInfo;

public class CSVExporter {
	
	private static final Logger LOGGER = Logger.getLogger(CSVExporter.class.getName());
	
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
	
	private static void printGitFileByRelease(CSVPrinter printer, int nRelease, FileWithMetrics file) throws IOException {
		printer.printRecord(nRelease, file.getName(), file.getLOC(), file.getnRevisions(), file.getnAuth(), 
							file.getBuggyText());
	}
	
	public static void printGitFileByRelease(List<FileByRelease> files, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
		    	printer.printRecord(FileWithMetrics.getFieldName(CSVField.VERSION), 
		    						FileWithMetrics.getFieldName(CSVField.FILENAME),
		    						FileWithMetrics.getFieldName(CSVField.LOC),
		    						FileWithMetrics.getFieldName(CSVField.NREVISIONS),
		    						FileWithMetrics.getFieldName(CSVField.NAUTH),
		    						FileWithMetrics.getFieldName(CSVField.BUGGY));
		    	
		    	for (int i = 0; i < files.size(); i++) {
					for (FileWithMetrics gitFile : files.get(i).getFiles()) {
						printGitFileByRelease(printer, i + 1, gitFile);
					}
				}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
