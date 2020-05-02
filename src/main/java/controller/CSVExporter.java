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
	
	public static String toText(boolean bool) {
		if (bool)
			return "YES";
		else
			return "NO";
	}
	
	public static void printGitFileWithRelease(FileByRelease[] rs, String file) {
		try (
				FileWriter fw = new FileWriter(file);
				CSVPrinter printer = new CSVPrinter(fw, CSVFormat.DEFAULT);	
			) {
		    	printer.printRecord("Version", "File Name", "LOC", "Buggy");
		    	int indexRel = 1;
		    	
		    	printer.printRecord(indexRel, rs[0].getFile().getName(), rs[0].getFile().getLOC(), 
		    							toText(rs[0].getFile().isBuggy()));
		    	
		    	for (int i = 1; i < rs.length; i++) {
		    		if (rs[i].getRelease().getReleaseInfo().getVersionID() != 
		    				rs[i - 1].getRelease().getReleaseInfo().getVersionID())
		    			indexRel++;
		    		
		    		printer.printRecord(indexRel, rs[i].getFile().getName(), rs[i].getFile().getLOC(), 
		    								toText(rs[i].getFile().isBuggy()));
		    	}
		} catch (IOException e) {
		     LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
}
