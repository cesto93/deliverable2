package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import model.BugTicket;
import model.GitFileWithRelease;
import model.ReleaseInfo;

public class GitFileWithReleaseController {

	public static GitFileWithRelease[] orderFile(HashMap<String, ArrayList<String>> files, 
			HashMap<String, BugTicket> tickets, HashMap<String, ReleaseInfo> releases) {
		GitFileWithRelease.getComparator();
		TreeSet<GitFileWithRelease> gitFiles = new TreeSet<>(GitFileWithRelease.getComparator());
		Set<String> ticketKeys = tickets.keySet();
		for (String key : ticketKeys) {
			if (files.containsKey(key)) {
				for (String av : tickets.get(key).getAffectedVersions()) {
					for (String fileName : files.get(key)) {
						gitFiles.add(new GitFileWithRelease(fileName, releases.get(av), true));
					}
				}
			
				for (String fv : tickets.get(key).getFixedVersions()) {
					for (String fileName : files.get(key)) {
						gitFiles.add(new GitFileWithRelease(fileName, releases.get(fv), false));
					}
				}
			}
		}
		return gitFiles.toArray(new GitFileWithRelease[0]);
	}
}
