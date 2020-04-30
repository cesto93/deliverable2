package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import model.BugTicket;
import model.GitCommit;
import model.Release;

public class ReleaseController {
	
	private ReleaseController() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static Map<String, ArrayList<BugTicket>> getBugByRelease(Release[] releases, BugTicket[] tickets) {
		HashMap<String, ArrayList<BugTicket>> map = new HashMap<>(); 
		for (Release release: releases) {
			ArrayList<BugTicket> bugs = new ArrayList<>();
			for (BugTicket ticket : tickets) {
				for (String av : ticket.getAffectedVersions()) {
					if (av.equals(release.getVersionID()))
						bugs.add(ticket);
				}
			}
			map.put(release.getVersionID(), bugs);
		}
		return map;
	}
	
	public static GitCommit getLastCommit(List<BugTicket> bugs) {
		TreeSet<GitCommit> commits = new TreeSet<>(GitCommit.getComparator());
		for(BugTicket bug : bugs) {
			if (bug.getCommits().length != 0) {
				commits.add(bug.getLastCommit());
			}
		}
		return commits.last();
	}
}
