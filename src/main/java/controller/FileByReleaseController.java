package controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import git.GitLogRetriever;
import model.BugTicket;
import model.FileByRelease;
import model.GitCommit;
import model.GitFile;
import model.FileWithMetrics;
import model.Release;
import utils.AddMap;

public class FileByReleaseController {
	private GitLogRetriever retriever;
	private String[] extTaken;
	
	public FileByReleaseController(GitLogRetriever retriever, String[] extTaken) {
	    this.retriever = retriever;
	    this.extTaken = extTaken;
	}
	
	public List<FileByRelease> getFileByRelease(Release[] releases) {
		List<FileByRelease> res= new ArrayList<>();
		for (Release release : releases) {
			TreeSet<FileWithMetrics> filesInRel = new TreeSet<>(
													(FileWithMetrics c, FileWithMetrics d) -> c.compareName(d));
			FileByRelease byRelease = new FileByRelease(release);
			GitCommit last = release.getLastCommit();
			if (last != null ) {
				for (GitFile file : retriever.getFiles(last.getHash(), extTaken)) {
					filesInRel.add(new FileWithMetrics(file.getName(), file.getHash()));
				}
			}
			
			byRelease.setFiles(new ArrayList<>(filesInRel));
			res.add(byRelease);
		}
		return res;
	}
	
	public static void setFileBuggy(FileByRelease fbr) {
		for (BugTicket bug : fbr.getRelease().getBugs()) { 
			for (FileWithMetrics file : fbr.getFiles()) {
				for (GitCommit commit : bug.getCommits()) {
					if (!file.isBuggy() && commit.getFileNames().contains(file.getName())) {
						file.setBuggy(true);
					}
				}
			}
		}
	}
	
	public void setLoc(FileByRelease fbr) {
			for (FileWithMetrics file : fbr.getFiles())
				file.setLoc(retriever.getLOC(file.getHash()));
	}
	
	public void setnAuth(FileByRelease fbr) {
		HashMap<String, Set<String>> authsByFile = new HashMap<>();
		for (GitCommit commit : fbr.getRelease().getCommits()) {
			String auth = retriever.getAuthor(commit.getHash());
			for(String fileName : retriever.getFilesModifiedByCommit(commit.getHash(), extTaken)) {
				Set<String> auths = authsByFile.get(fileName);
				if (auths == null)
					auths = new HashSet<>();
				auths.add(auth);
				authsByFile.put(fileName, auths);
			}
		}
		for (FileWithMetrics file : fbr.getFiles()) {
			Set<String> auths = authsByFile.get(file.getName());
			if (auths == null)
				file.setnAuth(0);
			else
				file.setnAuth(auths.size());
		}
	}
	
	public void setnRevisions(FileByRelease fbr) {
		HashMap<String, Integer> rev = new HashMap<>();
		for (GitCommit commit : fbr.getRelease().getCommits()) {
			for(String fileName : retriever.getFilesModifiedByCommit(commit.getHash(), extTaken)) {
				AddMap.sumValuesInMap(rev, fileName, 1);
			}
		}
		for (FileWithMetrics file : fbr.getFiles()) {
			file.setnRevisions(AddMap.getValuesInMap(rev, file.getName()));
		}
	}
	
	public void setLocTouchedAddedChurn(FileByRelease fbr) {
		HashMap<String, Integer> added = new HashMap<>();
		HashMap<String, Integer> touched = new HashMap<>();
		HashMap<String, Integer> churn = new HashMap<>();
		HashMap<String, Integer> maxAdded = new HashMap<>();
		HashMap<String, Integer> maxChurn = new HashMap<>();
		for (GitCommit commit : fbr.getRelease().getCommits()) {
			for(String fileName : retriever.getFilesModifiedByCommit(commit.getHash(), extTaken)) {
				int[] query = retriever.getLOCaddedAndDeleted(fileName, commit.getHash());
				int incAdded = query[0];
				int incTouched = query[0] + query[1];
				int incChurn = query[0] - query[1];
				
				AddMap.sumValuesInMap(added, fileName, incAdded);
				AddMap.sumValuesInMap(touched, fileName, incTouched);
				AddMap.sumValuesInMap(churn, fileName, incChurn);
				
				AddMap.maxValuesInMap(maxAdded, fileName, incAdded);
				AddMap.maxValuesInMap(maxChurn, fileName, incChurn);
			}
		}
		for (FileWithMetrics file : fbr.getFiles()) {
			file.setLocAdded(AddMap.getValuesInMap(added, file.getName()));
			file.setLocTouched(AddMap.getValuesInMap(touched, file.getName()));
			file.setChurn(AddMap.getValuesInMap(churn, file.getName()));
			
			file.setMaxLocAdded(AddMap.getValuesInMap(maxAdded, file.getName()));
			file.setMaxChurn(AddMap.getValuesInMap(maxChurn, file.getName()));
			
			setAvgMetrics(file);
		}
	}
	
	//needs nRevision, Churn, LocAdded to be setted
	private void setAvgMetrics(FileWithMetrics file) {
		if (file.getnRevisions() != 0) {
			file.setAvgChurn(file.getChurn() / file.getnRevisions());
			file.setAvgLocAdded((file.getLocAdded() / file.getnRevisions()));
		}
		else
		{
			file.setAvgChurn(0);
			file.setAvgLocAdded(0);
		}
	}
	
	public void setAge(List<FileByRelease> fbr) {
		HashMap<String, LocalDate> creationDates = new HashMap<>();
		for (FileByRelease filesInRel : fbr) {
			for (FileWithMetrics file :filesInRel.getFiles()) {
				LocalDate creationDate = creationDates.get(file.getName());
				if (creationDate == null) {
					creationDate = retriever.getFileDate(file.getName());
					creationDates.put(file.getName(), creationDate);
				}
				LocalDate releaseDate = filesInRel.getRelease().getReleaseInfo().getDate().toLocalDate();
				long age = creationDate.until(releaseDate, ChronoUnit.WEEKS);
				file.setAge((int) age);
			}
		}
	}
	
	public void setNFix(FileByRelease fbr, List<BugTicket> bugs) {
		for (FileWithMetrics file :fbr.getFiles()) {
			int nFix = 0;
			for (BugTicket bug : bugs) {
				for (GitCommit bugCommit : bug.getCommits()) {
					if (fbr.getRelease().getCommits().contains(bugCommit) &&
							bugCommit.getFileNames().contains(file.getName())) {
						nFix++;
					}
				}
			}
			file.setnFix(nFix);
		}
	}
	
	public void setLocTouchedAndChurnSlow(List<FileByRelease> files) {
		int[] query;
		String before = files.get(0).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
		for (FileWithMetrics file: files.get(0).getFiles()) {
			query = retriever.getLOCaddedAndDeletedByDate(file.getName(), before);
			int locTouched = query[0] + query[1];
			int churn = query[0] - query[1];
			file.setLocTouched(locTouched);
			file.setChurn(churn);
		}
		
		for (int i = 1; i < files.size() ; i++) {
			before = files.get(i).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
			String after = files.get(i - 1).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
			for (FileWithMetrics file: files.get(i).getFiles()) {
				query = retriever.getLOCaddedAndDeletedByDate(file.getName(), before, after);
				int locTouched = query[0] + query[1];
				int churn = query[0] - query[1];
				file.setLocTouched(locTouched);
				file.setChurn(churn);
			}
		}
		
	}
	
	public void setnRevisionsSlow(List<FileByRelease> files) {
		String before = files.get(0).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
		for (FileWithMetrics file: files.get(0).getFiles()) {
			file.setnRevisions(retriever.getnRevByDate(file.getName(), before));
		}
		
		for (int i = 1; i < files.size() ; i++) {
			before = files.get(i).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
			String after = files.get(i - 1).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
			for (FileWithMetrics file: files.get(i).getFiles()) {
				file.setnRevisions(retriever.getnRevByDate(file.getName(), before, after));
			}
		}
	}
	
	public void setnAuthSlow(List<FileByRelease> files) {
		String before = files.get(0).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
		for (FileWithMetrics file: files.get(0).getFiles()) {
			file.setnAuth(retriever.getNAuthByDate(file.getName(), before));
		}
		
		for (int i = 1; i < files.size() ; i++) {
			before = files.get(i).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
			String after = files.get(i - 1).getRelease().getReleaseInfo().getDate().toLocalDate().toString();
			for (FileWithMetrics file: files.get(i).getFiles()) {
				file.setnAuth(retriever.getNAuthByDate(file.getName(), before, after));
			}
		}
	}
	
}
