package controller;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import exporter.CSVExporter;
import git.GitLogRetriever;
import git.GitRepoHandler;
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
	private static final String VERSIONSUF = "_VersionInfo.csv";
	private static final String METRICSUF = "_File.csv";
	private static final String EVALSUF = "_Evaluation.csv";
	
	public static void main(String[] args) {
		
		final String[] projName = {GetProperty.getProperty("projectName1"), GetProperty.getProperty("projectName2")};
		final String[] urlProj = {GetProperty.getProperty("urlProject1"), GetProperty.getProperty("urlProject2")};
		final String[] repoDir = {GetProperty.getProperty("repoDir1"), GetProperty.getProperty("repoDir2")};
		final String repoPath = GetProperty.getProperty("repoPath");
		
		elaborateMetrics(projName[0], urlProj[0], new File(repoPath, repoDir[0]));
		predictBugginess(projName[0]);
		
		//elaborateMetrics(projName[1], urlProj[1], new File(repoPath, repoDir[1]));
		//predictBugginess(projName[1]);
		LOGGER.log(Level.INFO, "Done");
	}
	
	private static void elaborateMetrics(String projName, String urlProj, File repoPathProj) {
		GitLogRetriever retriever = new GitLogRetriever(new GitRepoHandler(urlProj, repoPathProj));
		BugTicketRepository bugRepo = new BugTicketRepository(retriever, extTaken);
		ReleaseController relController = new ReleaseController(retriever);
		FileByReleaseController fbrController = new FileByReleaseController(retriever, extTaken);
		
		ReleaseInfo[] relsInfo = relController.getReleaseInfo(projName);
		relsInfo = Arrays.copyOfRange(relsInfo, 0, relsInfo.length / 2); //remove last half of versions
		LOGGER.log(Level.INFO, "Done getting release");
		List<BugTicket> bugs = bugRepo.getBugTicket(projName, relsInfo);
		LOGGER.log(Level.INFO, "Done getting bug tickets and commits");
		Proportion.addMissingAV(bugs, relsInfo);
		LOGGER.log(Level.INFO, "Done getting bug tickets and commits");
		
		Release[] releases =  relController.getRelease(relsInfo, bugs);
		CSVExporter.printReleaseInfo(releases, projName + VERSIONSUF);
		LOGGER.log(Level.INFO, "Done writing release");
		
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
		CSVExporter.printGitFileByRelease(files,  projName + METRICSUF);
		LOGGER.log(Level.INFO, "Done set file metrics");
	}
	
	public static void predictBugginess(String projName) {
		EvaluationResult result = ModelComparer.compare(projName, projName + METRICSUF);
		CSVExporter.printEvaluationResult(result, projName + EVALSUF);
	}

}
