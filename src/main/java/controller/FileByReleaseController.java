package controller;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import model.BugTicket;
import model.FileByRelease;
import model.ReleaseInfo;

public class FileByReleaseController {
	
	private FileByReleaseController() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static FileByRelease[] orderFile(Map<String, ArrayList<String>> files, 
			Map<String, BugTicket> tickets, Map<String, ReleaseInfo> releases) {
		FileByRelease.getComparator();
		TreeSet<FileByRelease> gitFiles = new TreeSet<>(FileByRelease.getComparator());
		Set<String> ticketKeys = tickets.keySet();
		for (String key : ticketKeys) {
			if (files.containsKey(key)) {
				for (String av : tickets.get(key).getAffectedVersions()) {
					files.get(key).forEach(file -> gitFiles.add(new FileByRelease(file, releases.get(av), true)));
				}
			
				for (String fv : tickets.get(key).getFixedVersions()) {
					files.get(key).forEach(file -> gitFiles.add(new FileByRelease(file, releases.get(fv), false)));
				}
			}
		}
		return gitFiles.toArray(new FileByRelease[0]);
	}
	
}
