package controller;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONObject;

import model.ReleaseInfo;
import utils.JSONReader;

import org.json.JSONArray;

public class ReleaseInfoController {
	
	private static final Logger LOGGER = Logger.getLogger(ReleaseInfoController.class.getName());

	private ReleaseInfoController() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Map<String, ReleaseInfo> getReleaseInfo(String projName) {
		//Ignores releases with missing dates
		// If 2 release has the same dates pick the last one
		
		HashMap<String, ReleaseInfo> releases = new HashMap<>();
		String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
		JSONObject json;
		try {
			json = JSONReader.readJsonFromUrl(url);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
		JSONArray versions = json.getJSONArray("versions");

		for (Integer i = 0; i < versions.length(); i++ ) {
			String name = "";
			String id = "";
			if(versions.getJSONObject(i).has("releaseDate")) {
				if (versions.getJSONObject(i).has("name"))
					name = versions.getJSONObject(i).get("name").toString();
				if (versions.getJSONObject(i).has("id"))
					id = versions.getJSONObject(i).get("id").toString();
				LocalDateTime date = LocalDate.parse(versions.getJSONObject(i).get("releaseDate").toString()).atStartOfDay();
				ReleaseInfo release = new ReleaseInfo(id, name, date);
				
				releases.put(release.getVersionID(), release);
			}
		}
		return releases;
	}
	
	public static ReleaseInfo[] orderReleaseByData(Collection<ReleaseInfo> values) {
		TreeMap<LocalDateTime, ReleaseInfo> tree = new TreeMap<>();
		
		for (ReleaseInfo value : values) {
			tree.put(value.getDate(), value);
		}
		return tree.values().toArray(new ReleaseInfo[0]);
	}
}
