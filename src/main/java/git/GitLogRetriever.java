package git;

import java.io.BufferedReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.GitFile;


public class GitLogRetriever {
	private static final Logger LOGGER = Logger.getLogger(GitLogRetriever.class.getName());
	private static final String ONELINE = "--oneline";
	private static final String MASTER = "master";
	private static final String SHORTLOG = "shortlog";
	private static final String NUMSTAT = "--numstat";
	private static final String BEFORE = "--before=";
	private static final String AFTER = "--after=";
	private static final String GREP = "--grep=";
	private static final String NOPATCH = "--no-patch";
	private static final String NONOTES = "--no-notes";
	private static final String NAMEONLY = "--name-only";
	
	private GitDataProvider dataProvider;
	
	public GitLogRetriever(GitDataProvider dataProvider) {
		LOGGER.setLevel(Level.INFO);
		this.dataProvider = dataProvider;
	}
	
	private static String grep(String key) {
		return GREP + key + ":";
	}
	
	private static boolean endsWith(String s, String[] keys) {
		for (String key : keys) {
			if (s.endsWith(key))
				return true;
		}
		return false;
	}
	
	public LocalDate getCommitDate(String hash) {
		LocalDate res = null;
		try {
			BufferedReader stdInput =  dataProvider.getResultStream("git", "show", NOPATCH, NONOTES, 
													"--date=short", "--pretty=format:'%cd'", hash);
			
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
				res = LocalDate.parse(line.substring(1, line.length() - 1));
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return res;
	}
	
	private List<String> getCommitsHashBase(String ... commands) {
		ArrayList<String> commits = new ArrayList<>();
		try {
			BufferedReader stdInput = dataProvider.getResultStream(commands);
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
					commits.add(line.split(" ")[0]);
			}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return commits;
	}
	
	public List<String> getCommitsHashByDate(String before) {
		return getCommitsHashBase("git", "log", BEFORE + before, ONELINE);
	}
	
	public List<String> getCommitsHashByDate(String before, String after) {
		return getCommitsHashBase("git", "log", BEFORE + before, AFTER + after, ONELINE);
	}
	
	public List<String> getCommitsHash(String key) {
		return getCommitsHashBase("git", "log", ONELINE, grep(key));
	}
	
	public List<GitFile> getFiles(String hash, String[] extTaken) {
		TreeSet<GitFile> files = new TreeSet<>((GitFile a, GitFile b) -> a.compareName(b));
		try {
			BufferedReader stdInput = dataProvider.getResultStream("git", "ls-tree", "-r", hash);			
			for (String line = stdInput.readLine(); line!=null; line = stdInput.readLine()) {
				if (endsWith(line, extTaken)) {
					String[] parsed = line.split("\t");
					String fileHash = parsed[0].split(" ")[2];
					String fileName = parsed[1];
					files.add(new GitFile(fileName, fileHash));
				}
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return new ArrayList<>(files);
	}
	
	public List<String> getFilesModifiedByCommit(String hash, String[] extTaken) {
		ArrayList<String> files = new ArrayList<>();
		try {
			BufferedReader stdInput = dataProvider.getResultStream("git", "show", hash, NAMEONLY, ONELINE);	
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
				if (!line.contains(hash) && endsWith(line, extTaken))
					files.add(line);
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return files;
	}
	
	public int getLOC(String fileHash) {
		int loc = 0;
		try {
			BufferedReader stdInput = dataProvider.getResultStream("git", "cat-file", "-p", fileHash);
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
					loc++;
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return loc;
	}
	
	public String getAuthor(String hash) {
		String auth = null;
		try {
			BufferedReader stdInput = dataProvider.getResultStream("git", "show", "-s", hash, "--format='%an'");
			auth = stdInput.readLine();
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return auth;
	}
	
	private int getNAuth(String ...cmd) {
		int auth = 0;
		try {
			BufferedReader stdInput = dataProvider.getResultStream(cmd);
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
				if (line.length() != 0) {
					auth++;
				}
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return auth;
	}
	
	public int getNAuthByDate(String filename, String before) {
		return getNAuth("git", SHORTLOG, MASTER,  "-n", "-s", BEFORE + before , "--", filename );
	}
	
	public int getNAuthByDate(String filename, String before, String after) {
		return getNAuth("git", SHORTLOG, MASTER, "-n", "-s", BEFORE + before, AFTER + after, "--", filename);
	}
	
	private int getnRev(String ...cmd) {
		int nRev = 0;
		try {
			BufferedReader stdInput = dataProvider.getResultStream(cmd);
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
				nRev++;
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return nRev;
	}
	
	public int getnRevByDate(String filename, String before) {
		return getnRev("git","log",  ONELINE, BEFORE + before, "--", filename );
	}
	
	public int getnRevByDate(String filename, String before, String after) {
		return getnRev("git", "log", ONELINE, BEFORE + before, AFTER + after, "--", filename);
	}
	
	public int[] getLOCaddedAndDeleted(String ...cmd) {
		int[] res = new int[2];
		try {
			BufferedReader stdInput = dataProvider.getResultStream(cmd);
			for (String line = stdInput.readLine() ; line!=null; line = stdInput.readLine()) {
				if (line.contains("\t") ) {
					String[] parsed = line.split("\t");
					res[0] += Integer.parseInt(parsed[0]); //added
					res[1] += Integer.parseInt(parsed[1]); //deleted
				}
			}
		} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				Thread.currentThread().interrupt();
		}
		return res;
	}
	
	public int[] getLOCaddedAndDeleted(String filename, String hash) {
		return getLOCaddedAndDeleted("git", "log", NUMSTAT, ONELINE, hash, "--", filename);
	}
	
	public int[] getLOCaddedAndDeletedByDate(String filename, String before) {
		return getLOCaddedAndDeleted("git", "log", NUMSTAT, ONELINE, BEFORE + before, "--", filename);
	}
	
	public int[] getLOCaddedAndDeletedByDate(String filename, String before, String after) {
		return getLOCaddedAndDeleted("git", "log", NUMSTAT, ONELINE, BEFORE + before, 
											AFTER + after, "--", filename);
	}
}
