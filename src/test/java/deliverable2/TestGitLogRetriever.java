package deliverable2;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import exceptions.GitLogException;
import git.GitDataProvider;
import git.GitLogRetriever;
import model.GitFile;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TestGitLogRetriever {
	
	private static String[] extTaken = new String[] {".java", ".cpp"};
	
	@Test 
	public void testGetFiles() throws GitLogException {
		String hash = "ec430abc5";
		GitDataProvider provider = Mockito.mock(GitDataProvider.class);
		Mockito.when(provider.getResultStream("git", "ls-tree", "-r", hash))
				.thenReturn(new BufferedReader(new StringReader(
				"100644 blob 10378060e345785b1fb37f5a763602c1d97ce132	hedwig-server/src/test/java/org/apache/hedwig/server/persistence/TestReadAheadCacheWhiteBox.java\n" + 
				"100644 blob 26b2ce3417d5f35d5b82d42ab888287076c281cb	hedwig-server/src/test/java/org/apache/hedwig/server/subscriptions/StubSubscriptionManager.java\n" + 
				"100644 blob 0e0f670fdcd32dfba2587be95ca1467cf0229752	hedwig-server/src/test/java/org/apache/hedwig/server/subscriptions/TestMMSubscriptionManager.java\n" + 
				"100644 blob 5983f0b5c3e6cef951dcca084e24ea62711ed60f	hedwig-server/src/test/resources/log4j.properties\n" + 
				"100644 blob 15187daf978307603df3e437b02f3969a3c0e51b	pom.xml\n" + 
				"100644 blob 47edf5c77e17fd4a6fdd577f26c2294c43400fd3	src/assemble/bin.xml\n" + 
				"100644 blob 395f2b3834aa5776877adf6f9a94e1760b9d2e14	src/assemble/src.xml\n"
				)));
		GitLogRetriever retriever = new GitLogRetriever(provider);
		List<GitFile> mocked = new ArrayList<>();
		mocked.add(new GitFile("hedwig-server/src/test/java/org/apache/hedwig/server/persistence/TestReadAheadCacheWhiteBox.java", 
								"10378060e345785b1fb37f5a763602c1d97ce132"));
		mocked.add(new GitFile("hedwig-server/src/test/java/org/apache/hedwig/server/subscriptions/StubSubscriptionManager.java",
								"26b2ce3417d5f35d5b82d42ab888287076c281cb")); 
		mocked.add(new GitFile("hedwig-server/src/test/java/org/apache/hedwig/server/subscriptions/TestMMSubscriptionManager.java",
								"0e0f670fdcd32dfba2587be95ca1467cf0229752"));
		List<GitFile> real = retriever.getFiles(hash, extTaken);
		
		assertFalse(real.isEmpty());
		for (int i = 0; i < mocked.size(); i++)	{	
			assertEquals(real.get(i).getHash(), mocked.get(i).getHash());
			assertEquals(real.get(i).getName(), mocked.get(i).getName());
		}
	}
	
	@Test 
	public void testGetFilesModified() throws GitLogException {
		String hash = "ec430abc5";
		GitDataProvider provider = Mockito.mock(GitDataProvider.class);
		Mockito.when(provider.getResultStream("git", "show", hash, "--name-only", "--oneline"))
				.thenReturn(new BufferedReader(new StringReader(
				"3969ff71a BOOKKEEPER-91: Bookkeeper and hedwig clients should not use log4j directly (ivank via fpj)\n" + 
				"CHANGES.txt\n" + 
				"bookkeeper-benchmark/pom.xml\n" + 
				"bookkeeper-benchmark/src/main/java/org/apache/bookkeeper/benchmark/MySqlClient.java\n" + 
				"bookkeeper-benchmark/src/main/java/org/apache/bookkeeper/benchmark/TestClient.java\n" + 
				"bookkeeper-server/pom.xml\n" + 
				"bookkeeper-server/src/main/java/org/apache/bookkeeper/bookie/Bookie.java\n" + 
				"bookkeeper-server/src/main/java/org/apache/bookkeeper/bookie/EntryLogger.java"
				)));
		GitLogRetriever retriever = new GitLogRetriever(provider);
		List<String> mocked = new ArrayList<>();
		mocked.add("bookkeeper-benchmark/src/main/java/org/apache/bookkeeper/benchmark/MySqlClient.java");
		mocked.add("bookkeeper-benchmark/src/main/java/org/apache/bookkeeper/benchmark/TestClient.java"); 
		mocked.add("bookkeeper-server/src/main/java/org/apache/bookkeeper/bookie/Bookie.java");
		mocked.add("bookkeeper-server/src/main/java/org/apache/bookkeeper/bookie/EntryLogger.java");
		List<String> real = retriever.getFilesModifiedByCommit(hash, extTaken);
		
		assertFalse(real.isEmpty());
		for (int i = 0; i < mocked.size(); i++)	{	
			assertEquals(real, mocked);
		}
	}

}
