package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GitLogRetriever {
	private static final Logger LOGGER = Logger.getLogger(GitLogRetriever.class.getName());
	private File repo;
	private File parent;
	
	public GitLogRetriever(String repoURL, String repoPath) {
		this.repo = new File(repoPath);
		parent = repo.getParentFile();
		if (!repoExist()) 
			createRepo(repoURL);
	}
	
	private boolean repoExist() {
		if (!repo.exists() || !repo.isDirectory())
			return false;
		
		File git = new File(this.repo.getAbsolutePath() + File.separator + ".git");
		return (git.exists());
	}
	
	//no checkout
	private void createRepo(String repoURL) {
		LOGGER.log(Level.INFO, "creating repo");
		ProcessBuilder pb = new ProcessBuilder( "git", "clone", repoURL, "-n");
		pb.directory(parent);
		try {
			Process p = pb.start(); 
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE) ) 
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}
	
	public Map<String, ArrayList<String>> getFiles(String[] keys, String[] extIgnored) {
		HashMap<String, ArrayList<String>> res = new HashMap<>();
		for (String key : keys) {
			ProcessBuilder pb = new ProcessBuilder( "git", "log", "--name-only", "--oneline", "--max-count=1", 
													"--grep=" + key + ":");
			pb.directory(repo);
			
			try {
				Process p = pb.start();
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line;
				ArrayList<String> files = new ArrayList<>();
				
				while ((line = stdInput.readLine()) != null) {
					if (!line.contains(key) && !endsWith(line, extIgnored))
						files.add(line);
				}
				if (!files.isEmpty()) 
					res.put(key, files);
				if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
					LOGGER.log(Level.SEVERE, readErrors(p));
			} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}
		return res;
	}
	
	private static boolean endsWith(String s, String[] keys) {
		for (String key : keys) {
			if (s.endsWith(key))
				return true;
		}
		return false;
	}
	
	private static String readErrors(Process p) throws IOException  {
		StringBuilder bld = new StringBuilder();
		String line = "";
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = stdError.readLine()) != null)
			bld.append(line);
		return bld.toString();
	}
}
