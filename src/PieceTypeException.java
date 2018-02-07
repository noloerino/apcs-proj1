
@SuppressWarnings("serial")
public class PieceTypeException extends ChessException {

	/**
	 * Indicates that the attempted moving piece is not of the correct type.
	 */
	public PieceTypeException() {
		super("Piece is not of the correct type.");
	}
	
	/**
	 * Indicates that the piece that is trying to move does not match the piece
	 * that should be moving.
	 * 
	 * @param expected The expected piece type.
	 * @param received The attempted piece type.
	 */
	public PieceTypeException(char expected, char received) {
		super("Piece is not of the correct type: expected "
				+ "\'" + expected + "\', received"
				+ "\'" + received + "\'."
				);
	}
	
	public PieceTypeException(String s) {
		super(s);
	}
	
}
