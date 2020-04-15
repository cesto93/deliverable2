package controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import model.FileByRelease;
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
		    	printer.printRecord("Index","Version ID","Version Name","Date");
		    	int pos = 1;
		    	for (ReleaseInfo r : rs) {
		    		printer.printRecord(pos, r.getVersionID(), r.getVersionName(), r.getDate());
		    		pos++;
		    	}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static void printGitFileWithRelease(FileByRelease[] rs, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
		    	printer.printRecord("Version","File Name","Buggy");
		    	int indexRel = 1;
		    	
		    	if (rs[0].getBugginess())
	    			printer.printRecord(indexRel, rs[0].getName(), "YES");
	    		else
	    			printer.printRecord(indexRel, rs[0].getName(), "NO");
		    	
		    	for (int i = 1; i < rs.length; i++) {
		    		if (rs[i].getReleaseInfo().getVersionID() != rs[i - 1].getReleaseInfo().getVersionID())
		    			indexRel++;
		    		
		    		if (rs[i].getBugginess())
		    			printer.printRecord(indexRel, rs[i].getName(), "YES");
		    		else
		    			printer.printRecord(indexRel, rs[i].getName(), "NO");
		    		
		    	}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
