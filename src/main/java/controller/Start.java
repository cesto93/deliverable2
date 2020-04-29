package controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.BugTicket;
import model.FileByRelease;
import model.Release;

public class Start {
	private static final Logger LOGGER = Logger.getLogger(Start.class.getName());
	
	public static void main(String[] args) {
		final String projName ="BOOKKEEPER";
		final String[] extTaken = {".java", ".cpp"};
		//ZOOKEEPER
		
		GitObjectController gitController = new GitObjectController(
											new GitLogRetriever("https://github.com/apache/bookkeeper", 
													"/home/pier/git/bookkeeper/"), extTaken);
		
		Release[] releases = JIRATicketRetriever.getReleaseInfo(projName);
		if (releases == null || releases.length < 6)
			return;
			
		CSVExporter.printReleaseInfo(releases, projName + "VersionInfo.csv");
		LOGGER.log(Level.INFO, "Done writing release");
		
		//remove last half of versions
		releases = Arrays.copyOfRange(releases, 0, releases.length / 2);
		BugTicket[] tickets = JIRATicketRetriever.readTicketKeysAndVersion(projName);
		LOGGER.log(Level.INFO, "Done getting tickets");
		
		gitController.setGitCommitsTickets(tickets);
		LOGGER.log(Level.INFO, "Done getting commits");
		gitController.addFileToCommits(tickets);
		LOGGER.log(Level.INFO, "Done getting files");
		
		Map<String, ArrayList<BugTicket>> bugByRelease =  ReleaseController.getBugByRelease(releases, tickets);
		
		FileByRelease[] files = FileByReleaseController.getFileByRelease(bugByRelease, releases);
		FileByReleaseController.setLoc(gitController, files);
		FileByReleaseController.setFileBuggy(gitController, files, bugByRelease);
		CSVExporter.printGitFileWithRelease(files,  projName + "File.csv");
		LOGGER.log(Level.INFO, "Done");
		
	}

}
