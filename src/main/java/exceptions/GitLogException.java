package exceptions;

public class GitLogException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public GitLogException(String errorMsg) {
		super(errorMsg);
	}

}
