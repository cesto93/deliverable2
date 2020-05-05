package controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import model.BugTicket;
import model.FileByRelease;
import model.GitCommit;
import model.GitFile;
import model.FileWithMetrics;
import model.Release;

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
			GitCommit last;
			if ((last = release.getLastCommit()) != null ) {
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
	
	public void setnAuth(List<FileByRelease> files) {
		LocalDate before = files.get(0).getRelease().getReleaseInfo().getDate().toLocalDate();
		for (FileWithMetrics file: files.get(0).getFiles()) {
			file.setnAuth(retriever.getAuth(file.getName(), before));
		}
		
		for (int i = 1; i < files.size() ; i++) {
			before = files.get(i).getRelease().getReleaseInfo().getDate().toLocalDate();
			LocalDate after = files.get(i - 1).getRelease().getReleaseInfo().getDate().toLocalDate();
			for (FileWithMetrics file: files.get(i).getFiles()) {
				file.setnAuth(retriever.getAuth(file.getName(), before, after));
			}
		}
	}
	
	public void setnRevisions(List<FileByRelease> files) {
		LocalDate before = files.get(0).getRelease().getReleaseInfo().getDate().toLocalDate();
		for (FileWithMetrics file: files.get(0).getFiles()) {
			file.setnRevisions(retriever.getnRev(file.getName(), before));
		}
		
		for (int i = 1; i < files.size() ; i++) {
			before = files.get(i).getRelease().getReleaseInfo().getDate().toLocalDate();
			LocalDate after = files.get(i - 1).getRelease().getReleaseInfo().getDate().toLocalDate();
			for (FileWithMetrics file: files.get(i).getFiles()) {
				file.setnRevisions(retriever.getnRev(file.getName(), before, after));
			}
		}
	}
	
	public void setLocTouchedAndChurn(List<FileByRelease> files) {
		int[] query;
		LocalDate before = files.get(0).getRelease().getReleaseInfo().getDate().toLocalDate();
		for (FileWithMetrics file: files.get(0).getFiles()) {
			query = retriever.getLOCaddedAndDeleted(file.getName(), before);
			int locTouched = query[0] + query[1];
			int churn = query[0] - query[1];
			file.setLocTouched(locTouched);
			file.setChurn(churn);
		}
		
		for (int i = 1; i < files.size() ; i++) {
			before = files.get(i).getRelease().getReleaseInfo().getDate().toLocalDate();
			LocalDate after = files.get(i - 1).getRelease().getReleaseInfo().getDate().toLocalDate();
			for (FileWithMetrics file: files.get(i).getFiles()) {
				query = retriever.getLOCaddedAndDeleted(file.getName(), before, after);
				int locTouched = query[0] + query[1];
				int churn = query[0] - query[1];
				file.setLocTouched(locTouched);
				file.setChurn(churn);
			}
		}
		
	}
	
}
