package deliverable2;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import controller.FileByReleaseController;
import git.GitLogRetriever;
import model.FileByRelease;
import model.FileWithMetrics;
import model.GitCommit;
import model.Release;

public class TestFileByReleaseController {
	
	private static String[] extTaken = new String[] {".java", ".cpp"};
	private List<FileByRelease> fbrs;
	private FileByReleaseController controller;
	
	@Before
	public void setUp() {
		List<GitCommit> commits = new ArrayList<>();
		commits.add(new GitCommit("aaa"));
		commits.add(new GitCommit("bbb"));
		commits.add(new GitCommit("ccc"));
		
		List<List<String>> filesByCommit = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			filesByCommit.add(new ArrayList<>());
		}
		filesByCommit.get(0).add("aaa.java");
		filesByCommit.get(0).add("bbb.java");
		filesByCommit.get(1).add("ccc.java");
		filesByCommit.get(2).add("aaa.java");
		filesByCommit.get(2).add("bbb.java");
		
		List<FileWithMetrics> fwm = new ArrayList<>();
		fwm.add(new FileWithMetrics("aaa.java", "aaaa"));
		fwm.add(new FileWithMetrics("bbb.java", "bbbb"));
		fwm.add(new FileWithMetrics("ccc.java", "cccc"));
		fwm.add(new FileWithMetrics("ddd.java", "dddd"));
		
		
		fbrs = new ArrayList<>();
		FileByRelease fbr = new FileByRelease(new Release(null, commits, null));
		fbr.setFiles(fwm);
		fbrs.add(fbr);
		
		GitLogRetriever retriever = Mockito.mock(GitLogRetriever.class);
		Mockito.when(retriever.getFilesModifiedByCommit("aaa", extTaken)).thenReturn(filesByCommit.get(0));
		Mockito.when(retriever.getFilesModifiedByCommit("bbb", extTaken)).thenReturn(filesByCommit.get(1));
		Mockito.when(retriever.getFilesModifiedByCommit("ccc", extTaken)).thenReturn(filesByCommit.get(2));
		
		controller = new FileByReleaseController(retriever, extTaken);
	}
	
	@Test
	public void TestSetnRevisions() {
		List<FileWithMetrics> expected = new ArrayList<>();
		expected.add(new FileWithMetrics("aaa.java", "aaaa"));
		expected.add(new FileWithMetrics("bbb.java", "bbbb"));
		expected.add(new FileWithMetrics("ccc.java", "cccc"));
		expected.add(new FileWithMetrics("ddd.java", "dddd"));
		expected.get(0).setnRevisions(2);
		expected.get(1).setnRevisions(2);
		expected.get(2).setnRevisions(1);
		expected.get(3).setnRevisions(0);
		
		for (FileByRelease fbr : fbrs) {
			controller.setnRevisions(fbr);
			for (int i = 0; i < fbrs.get(0).getFiles().size(); i++) {
					assertEquals(fbrs.get(0).getFiles().get(i).getnRevisions(), expected.get(i).getnRevisions());
			}
		}
	}
}
