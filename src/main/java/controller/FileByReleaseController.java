package controller;

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
	
	public void setLoc(List<FileByRelease> files) {
		for (FileByRelease filesInRel : files) {
			for (FileWithMetrics file : filesInRel.getFiles())
				file.setLOC(retriever.getLOC(file.getHash()));
		}
	}
	
	public static void setFileBuggy(List<FileByRelease> files) {
		for (FileByRelease filesInRel : files) {
			for (BugTicket bug : filesInRel.getRelease().getBugs()) { 
				for (FileWithMetrics file : filesInRel.getFiles()) {
					if (!file.isBuggy() && bug.getFileNames().contains(file.getName())) {
						file.setBuggy(true);
					}
				}
			}
		}
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
	
	public void setnAuth(List<FileByRelease> fbr) {
		for (FileByRelease filesInRel : fbr) {
			setnAuth(filesInRel);
		}
	}
	
	private void setnRevisions(FileByRelease fbr) {
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
	
	public void setnRevisions(List<FileByRelease> fbr) {
		for (FileByRelease filesInRel : fbr) {
			setnRevisions(filesInRel);
		}
	}
	
	private void setLocTouchedAndChurn(FileByRelease fbr) {
		HashMap<String, Integer> touched = new HashMap<>();
		HashMap<String, Integer> churn = new HashMap<>();
		for (GitCommit commit : fbr.getRelease().getCommits()) {
			for(String fileName : retriever.getFilesModifiedByCommit(commit.getHash(), extTaken)) {
				int[] query = retriever.getLOCaddedAndDeleted(fileName, commit.getHash());
				int incTouched = query[0] + query[1];
				int incChurn = query[0] - query[1];
				AddMap.sumValuesInMap(touched, fileName, incTouched);
				AddMap.sumValuesInMap(churn, fileName, incChurn);
			}
		}
		for (FileWithMetrics file : fbr.getFiles()) {
			file.setLocTouched(AddMap.getValuesInMap(touched, file.getName()));
			file.setChurn(AddMap.getValuesInMap(churn, file.getName()));
		}
	}
	
	public void setLocTouchedAndChurn(List<FileByRelease> fbr) {
		for (FileByRelease filesInRel : fbr) {
			setLocTouchedAndChurn(filesInRel);
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
