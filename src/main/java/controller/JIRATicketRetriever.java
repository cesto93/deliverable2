package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import model.BugTicket;
import utils.JSONReader;

import org.json.JSONArray;

public class JIRATicketRetriever {
	
	private static final Logger LOGGER = Logger.getLogger(JIRATicketRetriever.class.getName());
	
	private JIRATicketRetriever() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static Map<String, BugTicket> readTicketKeysAndVersion(String projName) {
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		
		HashMap<String, BugTicket> tickets = new HashMap<>();
		//Get JSON API for closed bugs w/ AV in the project
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if res >1000
			j = i + 1000;
			// Type == “defect” AND (status == “Closed” OR status==“Resolved”) AND Resolution ==“Fixed”
			String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
					+ projName + "%22AND%22issueType%22=%22Bug%22" + "AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22" 
					+ "&fields=key,resolutiondate,versions,fixVersions,created&startAt="
					+ i.toString() + "&maxResults=" + j.toString();
			JSONObject json;
			try {
				json = JSONReader.readJsonFromUrl(url);
				JSONArray issues = json.getJSONArray("issues");
				total = json.getInt("total");
				for (; i < total && i < j; i++) {
					//Iterate through each res
					JSONObject issue = issues.getJSONObject(i%1000);
					String key = issue.get("key").toString();
					JSONArray versionsJ = issue.getJSONObject("fields").getJSONArray("versions");
					JSONArray fixedVersionsJ = issue.getJSONObject("fields").getJSONArray("fixVersions");
					ArrayList <String> versions = new ArrayList<>();
					ArrayList <String> fixedVersions = new ArrayList<>();
					for (int k = 0; k < versionsJ.length(); k++) {
						versions.add(versionsJ.getJSONObject(k).getString("id"));
					}
					for (int k = 0; k < fixedVersionsJ.length(); k++) {
						fixedVersions.add(fixedVersionsJ.getJSONObject(k).getString("id"));
					}
					if (!versions.isEmpty()) {
						BugTicket ticket = new BugTicket(key, versions.toArray(new String[0]), 
												fixedVersions.toArray( new String[0]));
						tickets.put(key, ticket);
					}
				} 
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			} 
		} while (i < total);
		return tickets;
	}
}
