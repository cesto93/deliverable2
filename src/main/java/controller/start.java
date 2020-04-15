package controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import model.BugTicket;
import model.GitFileWithRelease;
import model.ReleaseInfo;

public class start {

	public static void main(String[] args) {  
			String projName ="BOOKKEEPER";
			//String projName ="ZOOKEEPER";
			
			GitLogRetriever retriever = new GitLogRetriever("https://github.com/apache/bookkeeper", 
											"/home/pier/git/bookkeeper/");
			HashMap<String, ReleaseInfo> releases = ReleaseInfoController.getReleaseInfo(projName);
			if (releases == null || releases.size() < 6)
				return;
			
			ReleaseInfo[] ReleasesByDate = ReleaseInfoController.orderReleaseByData(releases.values());
			CSVExporter.printReleaseInfo(ReleasesByDate, projName + "VersionInfo.csv");
			HashMap<String, BugTicket> tickets = JIRATicketRetriever.readTicketKeysAndVersion(projName);
			Set<String> keys = tickets.keySet();
			for (String key : keys) {
				System.out.println(tickets.get(key).toString());
			}
			
			String[] extIgnored = {".txt", ".xml", ".conf", ".textile"};
			HashMap<String, ArrayList<String>> files = retriever.getFiles(keys.toArray(new String[0]), extIgnored);
			for (String key : keys) {
				System.out.println(key + " " + files.get(key));
			}
			
			
			GitFileWithRelease[] gitFiles = GitFileWithReleaseController.orderFile(files, tickets, releases);
			CSVExporter.printGitFileWithRelease(gitFiles,  projName + "File.csv");
	}

}
