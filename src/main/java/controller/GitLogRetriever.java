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
import model.FileWithMetrics;


public class GitLogRetriever {
	private static final Logger LOGGER = Logger.getLogger(GitLogRetriever.class.getName());
	private File repo;
	private File parent;
	private static final String ONELINE = "--oneline";
	private static final String BEFORE = "--before=";
	private static final String AFTER = "--after=";
	
	public GitLogRetriever(String repoURL, File repo) {
		this.repo = repo;
		parent = repo.getParentFile();
		if (!repoExist()) 
			createRepo(repoURL);
		LOGGER.setLevel(Level.INFO);
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
		LOGGER.log(Level.FINEST, pb.command().toString());
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
	
	private List<String> getCommitsHashBase(String ... commands) {
		ArrayList<String> commits = new ArrayList<>();
		try {
			Process p = getProcess(commands);
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
	
	public List<String> getCommitsHash(LocalDate before) {
		return getCommitsHashBase("git", "log", BEFORE + before.toString(), ONELINE);
	}
	
	public List<String> getCommitsHash(LocalDate before, LocalDate after) {
		return getCommitsHashBase("git", "log", "--before=" + before.toString(), AFTER + after.toString(), 
									ONELINE);
	}
	
	public List<String> getCommitsHash(String key) {
		return getCommitsHashBase("git", "log", ONELINE, grep(key));
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
					files.put(fileName, new FileWithMetrics(fileName, fileHash));
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
	
	public int getLOC(String fileHash) {
		int loc = 0;
		try {
			String line;
			Process p = getProcess("git", "cat-file", "-p", fileHash);
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
	
	private int getAuth(String ...cmd) {
		int auth = 0;
		try {
			String line = null;
			Process p = getProcess(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdInput.readLine()) != null) {
				if (line.length() != 0) {
					auth++;
				}
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return auth;
	}
	
	public int getAuth(String filename, LocalDate before) {
		return getAuth("git", "shortlog", "master",  "-n", "-s", BEFORE + before.toString() , "--", filename );
	}
	
	public int getAuth(String filename, LocalDate before, LocalDate after) {
		return getAuth("git", "shortlog", "master", "-n", "-s", BEFORE + before.toString(), AFTER + after.toString(),
						 "--", filename);
	}
	
	private int getnRev(String ...cmd) {
		int auth = 0;
		try {
			String line = null;
			Process p = getProcess(cmd);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = stdInput.readLine()) != null) {
				if (line.length() != 0) {
					auth += Integer.parseInt(line.split("\t")[0].trim());
				}
			}
			if (p.waitFor() != 0 && LOGGER.isLoggable(Level.SEVERE))
				LOGGER.log(Level.SEVERE, readErrors(p));
		} catch (IOException | InterruptedException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return auth;
	}
	
	public int getnRev(String filename, LocalDate before) {
		return getnRev("git", "shortlog", "master",  "-n", "-s", BEFORE + before.toString() , "--", filename );
	}
	
	public int getnRev(String filename, LocalDate before, LocalDate after) {
		return getnRev("git", "shortlog", "master", "-n", "-s", BEFORE + before.toString(), AFTER + after.toString(),
						 "--", filename);
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
