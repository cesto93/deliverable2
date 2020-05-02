package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.GitFile;
import model.GitFileWithMetrics;


public class GitLogRetriever {
	private static final Logger LOGGER = Logger.getLogger(GitLogRetriever.class.getName());
	private File repo;
	private File parent;
	private static final String ONELINE = "--oneline";
	
	public GitLogRetriever(String repoURL, String repoPath) {
		this.repo = new File(repoPath);
		parent = repo.getParentFile();
		if (!repoExist()) 
			createRepo(repoURL);
	}
	
	private static String grep(String key) {
		return "--grep=" + key + ":";
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
	
	private Process getProcess(String ... par) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(par);
		pb.directory(repo);
		return pb.start();
	}
	
	public LocalDate getCommitDate(String hash) {
		LocalDate res = null;

		try {
			Process p =  getProcess("git", "show", "--no-patch", "--no-notes", "--date=short",
												"--pretty=format:\"%cd\"", hash);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			
			while ((line = stdInput.readLine()) != null) {
				res = LocalDate.parse(line.substring(1, line.length() - 1));
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
			} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		return res;
	}
	
	public List<LocalDate> getCommitsDate(String key) {
		ArrayList<LocalDate> res = new ArrayList<>();
		
			
		try {
			Process p = getProcess( "git", "log", "--date=short", "--pretty=format:\"%cd\"", grep(key));
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
		return res;
	}
	
	public List<String> getCommitsHash(LocalDate before) {
		ArrayList<String> commits = new ArrayList<>();
			
		try {
			Process p = getProcess( "git", "log", "--before=" + before.toString(), ONELINE);
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
		return commits;
	}
	
	public List<String> getCommitsHash(LocalDate before, LocalDate after) {
		ArrayList<String> commits = new ArrayList<>();
			
		try {
			Process p = getProcess( "git", "log", "--before=" + before.toString(), "--after=" + after.toString(), 
									ONELINE);
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
		return commits;
	}
	
	public List<String> getCommitsHash(String key) {
		ArrayList<String> commits = new ArrayList<>();
			
		try {
			Process p = getProcess( "git", "log", ONELINE, grep(key));
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
		return commits;
	}
	
	public int getLOC(String fileHash) {
		int loc = 0;
		try {
			String line;
			Process p = getProcess( "git", "cat-file", "-p", fileHash);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
			while ((line = stdInput.readLine()) != null) {
				if (line.length() != 0) {
					loc++;
				}
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return loc;
	}
	
	public List<GitFile> getFiles(String hash, String[] extTaken) {
		TreeMap<String, GitFile> files = new TreeMap<>();
			
		try {
			Process p = getProcess( "git", "ls-tree", "-r", hash);
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
		
		return new ArrayList<>(files.values());
	}
	
	public List<String> getFilesModifiedByCommit(String hash, String[] extTaken) {
		ArrayList<String> files = new ArrayList<>();
		
		try {
			Process p = getProcess( "git", "show", hash, "--name-only", ONELINE);
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
	
	//unused
	public List<String> getFilesModifiedByTicket(String key, String[] extTaken) {
		ArrayList<String> files = new ArrayList<>();
		
		try {
			Process p = getProcess( "git", "log", "--name-only", ONELINE, grep(key));
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
