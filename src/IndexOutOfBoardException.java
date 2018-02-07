import java.util.Map;

@SuppressWarnings("serial")
public class IndexOutOfBoardException extends ChessException {
	
	final private static Map<Integer, Character> xToFileMap = Square.xToFileMap;

	/**
	 * Indicates that the desired square is not on the board.
	 */
	public IndexOutOfBoardException() {
		super("Desired square is not on the board.");
	}
	
	/**
	 * Indicates that the desired square is not on the board.
	 * 
	 * @param file The character representing the file of the square.
	 * @param rank The integer representing the rank of the square.
	 */
	public IndexOutOfBoardException(char file, int rank) {
		this("Desired square " + file + rank + " is not on the board.");
	}
	
	/**
	 * Indicates that the desired square is not on the board.
	 * 
	 * @param file The integer representing the file of the square.
	 * @param rank The integer representing the rank of the square.
	 */
	public IndexOutOfBoardException(int file, int rank) {
		this("Desired square " + xToFileMap.get(file) + rank + " is not on the board.");
	}
	
	public IndexOutOfBoardException(String s) {
		super(s);
	}

}
