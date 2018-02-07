
@SuppressWarnings("serial")
public class IllegalMoveException extends ChessException {

	/**
	 * Indicates that the attempted move is illegal.
	 */
	public IllegalMoveException() {
		super("Not a legal move.");
	}
	
	/**
	 * Indicates that a move is illegal.
	 * 
	 * @param m The move that threw the exception.
	 */
	public IllegalMoveException(Move m) {
		super("Attempted move " + m.toString() + " is illegal.");
	}
	
	/**
	 * Indicates that the attempted move is illegal.
	 * 
	 * @param s The error message.
	 */
	public IllegalMoveException(String s) {
		super(s);
	}
}
