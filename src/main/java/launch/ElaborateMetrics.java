package launch;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import controller.BugTicketRepository;
import controller.FileByReleaseController;
import controller.Proportion;
import controller.ReleaseController;
import exporter.CSVExporter;
import git.GitLogRetriever;
import git.GitRepoHandler;
import model.BugTicket;
import model.FileByRelease;
import model.ReleaseInfo;
import utils.GetProperty;
import model.Release;

public class ElaborateMetrics {
	private static final Logger LOGGER = Logger.getLogger(ElaborateMetrics.class.getName());
	private static final String[] extTaken = {".java"};
	private static final String VERSIONSUF = "_VersionInfo.csv";
	private static final String METRICSUF = "_File.csv";
	
	
	public static void main(String[] args) {
		
		final String[] projName = {GetProperty.getProperty("projectName1"), GetProperty.getProperty("projectName2")};
		final String[] urlProj = {GetProperty.getProperty("urlProject1"), GetProperty.getProperty("urlProject2")};
		final String[] repoDir = {GetProperty.getProperty("repoDir1"), GetProperty.getProperty("repoDir2")};
		final String repoPath = GetProperty.getProperty("repoPath");
		
		LOGGER.info(projName[0]);
		elaborateMetrics(projName[0], urlProj[0], new File(repoPath, repoDir[0]));
		LOGGER.info(projName[1]);
		elaborateMetrics(projName[1], urlProj[1], new File(repoPath, repoDir[1]));
		LOGGER.info("Done");
	}
	
	private static void elaborateMetrics(String projName, String urlProj, File repoPathProj) {
		GitLogRetriever retriever = new GitLogRetriever(new GitRepoHandler(urlProj, repoPathProj));
		BugTicketRepository bugRepo = new BugTicketRepository(retriever, extTaken);
		ReleaseController relController = new ReleaseController(retriever);
		FileByReleaseController fbrController = new FileByReleaseController(retriever, extTaken);
		
		ReleaseInfo[] relsInfo = relController.getReleaseInfo(projName);
			
		List<BugTicket> bugs = bugRepo.getBugTicket(projName, relsInfo);
		Proportion.addMissingAV(bugs, relsInfo);
		LOGGER.info(() -> String.format("Found bug tickets: %s", bugs.size()));
		
		relsInfo = Arrays.copyOfRange(relsInfo, 0, relsInfo.length / 2); //remove last half of versions
		Release[] releases =  relController.getRelease(relsInfo, bugs);
		LOGGER.info(() -> String.format("Found release: %s", releases.length));
		
		List<FileByRelease> files = fbrController.getFileByRelease(releases);
		
		for (FileByRelease fbr : files) {
			FileByReleaseController.setFileBuggy(fbr);
			fbrController.setLoc(fbr);
			fbrController.setnRevisions(fbr);
			fbrController.setnAuth(fbr);
			fbrController.setLocTouchedAddedChurn(fbr);
			fbrController.setNFix(fbr, bugs);
		}
		fbrController.setAge(files);
		
		CSVExporter.printReleaseInfo(releases, projName + VERSIONSUF);
		CSVExporter.printGitFileByRelease(files,  projName + METRICSUF);
		LOGGER.info("Done exporting metrics and versions");
	}

}
