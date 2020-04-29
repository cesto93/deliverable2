package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.GitFile;
import model.GitFileWithMetrics;


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
	
	public LocalDate[] getCommitsDate(String key) {
		ArrayList<LocalDate> res = new ArrayList<>();
		ProcessBuilder pb = new ProcessBuilder( "git", "log", "--date=short", "--pretty=format:\"%cd\"",
									 "--grep=" + key);
		pb.directory(repo);
			
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			
			while ((line = stdInput.readLine()) != null) {
				line = line.substring(1, line.length() - 1);
				res.add(LocalDate.parse(line));
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
			} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		return res.toArray(new LocalDate[0]);
	}
	
	public String[] getCommitsHash(String key) {
		ArrayList<String> commits = new ArrayList<>();
		
		ProcessBuilder pb = new ProcessBuilder( "git", "log", "--oneline", "--grep=" + key + ":");
		pb.directory(repo);
			
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
				
			while ((line = stdInput.readLine()) != null) {
					commits.add(line.split(" ")[0]);
			}

			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
			} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return commits.toArray(new String[0]);
	}
	
	public int getLOC(String fileHash) {
		ProcessBuilder pb = new ProcessBuilder( "git", "cat-file", "-p", fileHash);
		pb.directory(repo);
		int LOC = 0;
		try {
			String line;
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
			while ((line = stdInput.readLine()) != null) {
				if (line.length() != 0) {
					LOC++;
				}
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return LOC;
	}
	
	public Map<String, GitFile> getFiles(String hash, String[] extTaken) {
		ProcessBuilder pb = new ProcessBuilder( "git", "ls-tree", "-r", hash);
		pb.directory(repo);
		TreeMap<String, GitFile> files = new TreeMap<>();
			
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
				
			while ((line = stdInput.readLine()) != null) {
				if (endsWith(line, extTaken)) {
					String fileHash = line.split(" ")[2].split("\t")[0];
					String fileName = line.split("\t")[1];
					files.put(fileName, new GitFileWithMetrics(fileName, fileHash));
				}
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		
		return files;
	}
	
	public ArrayList<String> getFilesModifiedByCommit(String hash, String[] extTaken) {
		ProcessBuilder pb = new ProcessBuilder( "git", "show", hash, "--name-only", "--oneline");
		pb.directory(repo);
		ArrayList<String> files = new ArrayList<>();
		
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
				
			while ((line = stdInput.readLine()) != null) {
				if (!line.contains(hash) && endsWith(line, extTaken))
					files.add(line);
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}

		return files;
	}
	
	public ArrayList<String> getFilesModifiedByTicket(String key, String[] extTaken) {
		ProcessBuilder pb = new ProcessBuilder( "git", "log", "--name-only", "--oneline", 
													"--grep=" + key + ":");
		pb.directory(repo);
		ArrayList<String> files = new ArrayList<>();
		
		try {
			Process p = pb.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
				
			while ((line = stdInput.readLine()) != null) {
				if (!line.contains(key) && endsWith(line, extTaken))
					files.add(line);
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}

		return files;
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
