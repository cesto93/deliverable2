package git;

import java.io.BufferedReader;

import exceptions.GitLogException;

public interface GitDataProvider {
	
	public BufferedReader getResultStream(String ... par) throws GitLogException;
}
