package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import model.BugTicket;
import model.FileByRelease;
import model.GitFile;
import model.Release;

public class FileByReleaseController {
	
	private FileByReleaseController() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static FileByRelease[] getFileByRelease(Map<String, ArrayList<BugTicket>> bugByRelease, 
													Release[] releases) {
		TreeSet<FileByRelease> gitFiles = new TreeSet<>(FileByRelease.getComparator());
		for (Release release: releases) {
			ArrayList<BugTicket> bugs = bugByRelease.get(release.getVersionID());
			if (bugs.size() != 0) {
				Collection<GitFile> files =  ReleaseController.getLastCommit(bugs).getFiles().values();
				for(GitFile file : files) {
					FileByRelease byRelease = new FileByRelease(release, file);
					gitFiles.add(byRelease);
				}
			}
		}
		return gitFiles.toArray(new FileByRelease[0]);
	}
	
	public static void setLoc(GitObjectController controller, FileByRelease[] files) {
		for (FileByRelease file : files) {
			controller.setLOC(file.getFile());
		}
	}
	
	public static void setFileBuggy(GitObjectController controller, 
									FileByRelease[] files, Map<String, ArrayList<BugTicket>> bugByRelease,
									BugTicket[] bugs) {
		HashMap<String, List<String>> filesPerBug = new HashMap<>();
		for (BugTicket bug : bugs) {
			filesPerBug.put(bug.getKey(), controller.getFileModifiedByTicket(bug));
		}
		for (FileByRelease file : files) {
			for (BugTicket bug : bugByRelease.get(file.getRelease().getVersionID())) {
				if (filesPerBug.get(bug.getKey()).contains(file.getFile().getName())) {
					file.getFile().setBuggy(true);
					break;
				}
			}
		}
	}
	
}
