package controller;


import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.BugTicket;
import model.FileByRelease;
import model.ReleaseInfo;

public class Start {
	private static final Logger LOGGER = Logger.getLogger(Start.class.getName());
	
	public static void main(String[] args) {
		final String projName ="BOOKKEEPER";
		final String[] extIgnored = {".txt", ".xml", ".conf", ".textile"};
		//ZOOKEEPER
			
		GitLogRetriever retriever = new GitLogRetriever("https://github.com/apache/bookkeeper", 
											"/home/pier/git/bookkeeper/");
		Map<String, ReleaseInfo> releases = ReleaseInfoController.getReleaseInfo(projName);
		if (releases == null || releases.size() < 6)
			return;
			
		ReleaseInfo[] releasesByDate = ReleaseInfoController.orderReleaseByData(releases.values());
		CSVExporter.printReleaseInfo(releasesByDate, projName + "VersionInfo.csv");
		
		Map<String, BugTicket> tickets = JIRATicketRetriever.readTicketKeysAndVersion(projName);
		Set<String> keys = tickets.keySet();
		LOGGER.log(Level.INFO, tickets.toString());
			
		Map<String, ArrayList<String>> files = retriever.getFiles(keys.toArray(new String[0]), extIgnored);
		if (LOGGER.isLoggable(Level.INFO))
			LOGGER.log(Level.INFO,  files.toString() );
			
		FileByRelease[] gitFiles = FileByReleaseController.orderFile(files, tickets, releases);
		CSVExporter.printGitFileWithRelease(gitFiles,  projName + "File.csv");
		LOGGER.log(Level.INFO, "Done");
	}

}
