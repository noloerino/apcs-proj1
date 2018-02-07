
@SuppressWarnings("serial")
public class AlgebraicInputException extends ChessException {

	/**
	 * Indicates that the argument is not a valid input string.
	 */
	public AlgebraicInputException() {
		super("Argument is not a valid string in algebraic notation.");
	}
	
	/**
	 * Indicates that the argument is not a valid input string.
	 * 
	 * @param s The error message.
	 */
	public AlgebraicInputException(String s) {
		super(s);
	}
}
