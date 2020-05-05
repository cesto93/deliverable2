package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import model.BugTicket;
import model.ReleaseInfo;
import utils.JSONReader;

import org.json.JSONArray;

public class JIRATicketRetriever {
	
	private static final Logger LOGGER = Logger.getLogger(JIRATicketRetriever.class.getName());
	private static final String BASEURL = "https://issues.apache.org/jira/rest/api/2";
	
	private JIRATicketRetriever() {
	    throw new IllegalStateException("Utility class");
	}
	
	private static String[] toStringArray(JSONArray array, String jsonKey) {
		ArrayList <String> list = new ArrayList<>();
		for (int k = 0; k < array.length(); k++) {
			list.add(array.getJSONObject(k).getString(jsonKey));
		}
		return list.toArray(new String[0]);
	}
	
	public static BugTicket[] getTicketKeysAndVersion(String projName) {
		Integer j = 0;
		Integer i = 0;
		Integer total = 1;
		ArrayList<BugTicket> tickets = new ArrayList<>();
		
		//Get JSON API for closed bugs w/ AV in the project
		do {
			//Only gets a max of 1000 at a time, so must do this multiple times if res >1000
			j = i + 1000;
			String url = BASEURL + "/search?jql=project=%22" + projName 
					+ "%22AND%22issueType%22=%22Bug%22" + "AND(%22status%22=%22closed%22OR"
					+ "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22" 
					+ "&fields=key,resolutiondate,versions,fixVersions,created&startAt="
					+ i.toString() + "&maxResults=" + j.toString();
			
			try {
				JSONObject json = JSONReader.readJsonFromUrl(url);
				JSONArray issues = json.getJSONArray("issues");
				total = json.getInt("total");
				
				//Iterate through each res
				for (; i < total && i < j; i++) {
					JSONObject issue = issues.getJSONObject(i%1000);
					String key = issue.get("key").toString();
					JSONArray versions = issue.getJSONObject("fields").getJSONArray("versions");
					JSONArray fixVersions = issue.getJSONObject("fields").getJSONArray("fixVersions");
					
					if (versions.length() == 0)
						LOGGER.log(Level.WARNING, String.format("missing affected version %s", key));
					
					if (fixVersions.length() == 0)
						LOGGER.log(Level.WARNING, String.format("missing fixing version %s", key));

					BugTicket bug = new BugTicket(key, toStringArray(versions, "id"), toStringArray(fixVersions, "id"));
						tickets.add(bug);
				} 
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			} 
		} while (i < total);
		return tickets.toArray(new BugTicket[0]);
	}
	
	public static ReleaseInfo[] getReleaseInfo(String projName) {
		//Ignores releases with missing dates
		// If 2 release has the same dates pick the last one
		TreeMap<LocalDateTime, ReleaseInfo> releases = new TreeMap<>();
		String url = BASEURL + "/project/" + projName;
		
		try {
			JSONObject json = JSONReader.readJsonFromUrl(url);
			JSONArray versions = json.getJSONArray("versions");
			
			for (Integer i = 0; i < versions.length(); i++ ) {
				String name = "";
				String id = "";
				if(versions.getJSONObject(i).has("releaseDate")) {
					if (versions.getJSONObject(i).has("name"))
						name = versions.getJSONObject(i).get("name").toString();
					if (versions.getJSONObject(i).has("id"))
						id = versions.getJSONObject(i).get("id").toString();
					LocalDateTime date = LocalDate.parse(versions.getJSONObject(i).get("releaseDate").toString())
													.atStartOfDay();
					releases.put(date, new ReleaseInfo(id, name, date));
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return releases.values().toArray(new ReleaseInfo[0]);
	}
}
