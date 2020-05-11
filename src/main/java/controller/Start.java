package controller;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import exporter.CSVExporter;
import git.GitLogRetriever;
import git.GitRepoHandler;
import jira.JIRATicketRetriever;
import model.BugTicket;
import model.FileByRelease;
import model.ReleaseInfo;
import utils.GetProperty;
import model.Release;

public class Start {
	private static final Logger LOGGER = Logger.getLogger(Start.class.getName());
	
	public static void main(String[] args) {
		final String[] extTaken = {".java", ".cpp"};
		final String projName = GetProperty.getProperty("projectName");
		final String urlProj = GetProperty.getProperty("urlProject");
		File repoPathProj = new File(GetProperty.getProperty("repoPath"), 
										GetProperty.getProperty("repoDir"));
		//ZOOKEEPER
		
		GitLogRetriever retriever = new GitLogRetriever(new GitRepoHandler(urlProj, repoPathProj));
		BugTicketRepository gitController = new BugTicketRepository(retriever, extTaken);
		ReleaseController relController = new ReleaseController(retriever);
		FileByReleaseController fbrController = new FileByReleaseController(retriever, extTaken);
		
		ReleaseInfo[] relsInfo = JIRATicketRetriever.getReleaseInfo(projName);
		if (relsInfo == null || relsInfo.length < 6)
			return;
		CSVExporter.printReleaseInfo(relsInfo, projName + "VersionInfo.csv");
		LOGGER.log(Level.INFO, "Done writing release");
		
		BugTicket[] bugs = gitController.getBugTicket(projName);
		LOGGER.log(Level.INFO, "Done getting bug tickets and commits");
		
		
		Proportion.addMissingAV(bugs, relsInfo);
		LOGGER.log(Level.INFO, "Done getting missing AV");
		
		//remove last half of versions
		relsInfo = Arrays.copyOfRange(relsInfo, 0, relsInfo.length / 2);
		
		Release[] releases =  relController.getRelease(relsInfo, bugs);
		LOGGER.log(Level.INFO, "Done getting release commits");
		
		List<FileByRelease> files = fbrController.getFileByRelease(releases);
		LOGGER.log(Level.INFO, "Done getting file by release");
		
		fbrController.setLoc(files);
		LOGGER.log(Level.INFO, "Done set LOC");
		fbrController.setnRevisions(files);
		LOGGER.log(Level.INFO, "Done set nRev");
		fbrController.setnAuth(files);
		LOGGER.log(Level.INFO, "Done set nAuth");
		fbrController.setLocTouchedAndChurn(files);
		LOGGER.log(Level.INFO, "Done set touchedAndChurn");
		fbrController.setAvgMetrics(files);
		LOGGER.log(Level.INFO, "Done set AVGChurn and AVGLocAdded");
		fbrController.setAge(files);
		LOGGER.log(Level.INFO, "Done set Age");
		
		FileByReleaseController.setFileBuggy(files);
		LOGGER.log(Level.INFO, "Done setFileBuggy");
		CSVExporter.printGitFileByRelease(files,  projName + "File.csv");
		LOGGER.log(Level.INFO, "Done");
		
	}

}
