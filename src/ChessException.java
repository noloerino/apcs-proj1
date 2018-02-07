/**
 * A class of exceptions to be used in chess applications.
 * 
 * @author jhshi
 *
 */
@SuppressWarnings("serial")
public abstract class ChessException extends RuntimeException {
	
	public ChessException() {
		super();
	}

	public ChessException(String s) {
		super(s);
	}
}
