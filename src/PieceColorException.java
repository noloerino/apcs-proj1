
@SuppressWarnings("serial")
public class PieceColorException extends PieceTypeException {

	/**
	 * Indicates that the piece is neither black nor white, and
	 * therefore should not be moved.
	 */
	public PieceColorException() {
		super("Piece must be either black or white.");
	}
	
	/**
	 * Indicates that the piece was not of the desired color.
	 * 
	 * @param mode 0 if the piece should have been white, 1 if anything else.
	 */
	public PieceColorException(int mode) {
		super("Piece must be " + ((mode == Piece.WHITE)?("white."):("black.")));
	}
	
	public PieceColorException(String s) {
		super(s);
	}
}
