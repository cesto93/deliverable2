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
import weka.EvaluationResult;
import weka.ModelComparer;
import model.Release;

public class Start {
	private static final Logger LOGGER = Logger.getLogger(Start.class.getName());
	private static final String[] extTaken = {".java", ".cpp"};
	private static final String VERSIONSUF = "VersionInfo.csv";
	private static final String METRICSUF = "File.csv";
	
	public static void main(String[] args) {
		
		final String projName = GetProperty.getProperty("projectName");
		final String urlProj = GetProperty.getProperty("urlProject");
		File repoPathProj = new File(GetProperty.getProperty("repoPath"), 
										GetProperty.getProperty("repoDir"));
		//ZOOKEEPER
		elaborateMetrics(projName, urlProj, repoPathProj);
		predictBugginess(projName);
		
	}
	
	private static void elaborateMetrics(String projName, String urlProj, File repoPathProj) {
		GitLogRetriever retriever = new GitLogRetriever(new GitRepoHandler(urlProj, repoPathProj));
		BugTicketRepository gitController = new BugTicketRepository(retriever, extTaken);
		ReleaseController relController = new ReleaseController(retriever);
		FileByReleaseController fbrController = new FileByReleaseController(retriever, extTaken);
		
		ReleaseInfo[] relsInfo = JIRATicketRetriever.getReleaseInfo(projName);
		if (relsInfo == null || relsInfo.length < 6)
			return;
		CSVExporter.printReleaseInfo(relsInfo, projName + VERSIONSUF);
		LOGGER.log(Level.INFO, "Done writing release");
		
		BugTicket[] bugs = gitController.getBugTicket(projName);
		LOGGER.log(Level.INFO, "Done getting bug tickets and commits");
		
		Proportion.addMissingAV(bugs, relsInfo);
		LOGGER.log(Level.INFO, "Done getting missing AV");
		
		relsInfo = Arrays.copyOfRange(relsInfo, 0, relsInfo.length / 2); //remove last half of versions
		
		Release[] releases =  relController.getRelease(relsInfo, bugs);
		LOGGER.log(Level.INFO, "Done getting release commits");
		
		List<FileByRelease> files = fbrController.getFileByRelease(releases);
		LOGGER.log(Level.INFO, "Done getting file by release");
		
		for (FileByRelease fbr : files) {
			FileByReleaseController.setFileBuggy(fbr);
			fbrController.setLoc(fbr);
			fbrController.setnRevisions(fbr);
			fbrController.setnAuth(fbr);
			fbrController.setLocTouchedAndChurn(fbr);
			fbrController.setAvgMetrics(fbr);
		}
		fbrController.setAge(files);
		LOGGER.log(Level.INFO, "Done set Age");
		CSVExporter.printGitFileByRelease(files,  projName + METRICSUF);
		LOGGER.log(Level.INFO, "Done");
	}
	
	public static void predictBugginess(String projName) {
		EvaluationResult result = ModelComparer.compare(projName, projName + METRICSUF);
		CSVExporter.printEvaluationResult(result, projName + "_Evaluation");
	}

}
