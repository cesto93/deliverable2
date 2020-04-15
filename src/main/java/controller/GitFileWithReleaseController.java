package controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import model.BugTicket;
import model.GitFileWithRelease;
import model.ReleaseInfo;

public class GitFileWithReleaseController {
	
	private GitFileWithReleaseController() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static GitFileWithRelease[] orderFile(Map<String, ArrayList<String>> files, 
			Map<String, BugTicket> tickets, Map<String, ReleaseInfo> releases) {
		GitFileWithRelease.getComparator();
		TreeSet<GitFileWithRelease> gitFiles = new TreeSet<>(GitFileWithRelease.getComparator());
		Set<String> ticketKeys = tickets.keySet();
		for (String key : ticketKeys) {
			if (files.containsKey(key)) {
				for (String av : tickets.get(key).getAffectedVersions()) {
					files.get(key).forEach(file -> gitFiles.add(new GitFileWithRelease(file, releases.get(av), true)));
				}
			
				for (String fv : tickets.get(key).getFixedVersions()) {
					files.get(key).forEach(file -> gitFiles.add(new GitFileWithRelease(file, releases.get(fv), false)));
				}
			}
		}
		return gitFiles.toArray(new GitFileWithRelease[0]);
	}
	
}
