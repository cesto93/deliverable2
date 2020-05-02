package controller;

import java.util.TreeSet;

import model.BugTicket;
import model.FileByRelease;
import model.GitCommit;
import model.GitFile;
import model.Release;

public class FileByReleaseController {
	private GitLogRetriever retriever;
	private String[] extTaken;
	
	public FileByReleaseController(GitLogRetriever retriever, String[] extTaken) {
	    this.retriever = retriever;
	    this.extTaken = extTaken;
	}
	
	public FileByRelease[] getFileByRelease(Release[] releases) {
		TreeSet<FileByRelease> gitFiles = new TreeSet<>(FileByRelease.getComparator());
		for (Release release : releases) {
			GitCommit last;
			if ((last = release.getLastCommit()) != null ) {
				for (GitFile file : retriever.getFiles(last.getHash(), extTaken)) {
					FileByRelease byRelease = new FileByRelease(release, file);
					gitFiles.add(byRelease);
				}
			}
		}
		return gitFiles.toArray(new FileByRelease[0]);
	}
	
	public void setLoc(FileByRelease[] files) {
		for (FileByRelease file : files) {
			file.getFile().setLOC(retriever.getLOC(file.getFile().getHash()));
		}
	}
	
	public static void setFileBuggy(FileByRelease[] files) {
		for (FileByRelease file : files) {
			for (BugTicket bug : file.getRelease().getBugs()) {
					if (bug.getFileNames().contains(file.getFile().getName())) {
						file.getFile().setBuggy(true);
						break;
					}
			}
		}
	}
	
}
