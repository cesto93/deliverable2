package git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.GitLogException;

public class GitRepoHandler implements GitDataProvider {
	private static final Logger LOGGER = Logger.getLogger(GitRepoHandler.class.getName());
	private File repo;
	private File parent;
	private Process p;

	public GitRepoHandler(String repoURL, File repo) {
		this.repo = repo;
		parent = repo.getParentFile();
		if (!repoExist()) 
			createRepo(repoURL);
	}
	
	public boolean repoExist() {
		if (!repo.exists() || !repo.isDirectory())
			return false;
		File git = new File(this.repo.getAbsolutePath(),".git");
		return (git.exists());
	}
	
	//no checkout
	private void createRepo(String repoURL) {
		LOGGER.log(Level.INFO, "creating repo");
		ProcessBuilder pb = new ProcessBuilder( "git", "clone", repoURL, "-n");
		pb.directory(parent);
		try {
			p = pb.start(); 
			logErrors();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}
	
	public void logErrors() throws GitLogException, IOException, InterruptedException  {
		if (p.waitFor() == 0)
			return;
		StringBuilder bld = new StringBuilder();
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		for (String line = stdError.readLine(); line != null; line = stdError.readLine())
			bld.append(line);
		throw new GitLogException(bld.toString());
	}
	
	public BufferedReader getResultStream(String ... par) throws GitLogException {
		ProcessBuilder pb = new ProcessBuilder(par);
		pb.directory(repo);
		try {
			p = pb.start();
			return new BufferedReader(new InputStreamReader(p.getInputStream()));
		} catch (IOException e) {
			throw new GitLogException(e.getMessage());
		}
	}

}