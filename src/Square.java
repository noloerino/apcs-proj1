import java.util.HashMap;
import java.util.Map;

/**
 * A glorified pair of coordinates representing a square on a game board.
 * Maps ensure that both character and integer inputs are acceptable.
 * 
 * @author jhshi
 *
 */
public class Square {

	private char file;
	private int rank;
	private int x;
	private int y;
	
	final public static Map<Character, Integer> fileToXMap;
	final public static Map<Integer, Character> xToFileMap;
	
	static {
		fileToXMap = new HashMap<Character, Integer>();
		xToFileMap = new HashMap<Integer, Character>();
		fileToXMap.put('o', 0);
		fileToXMap.put('O', 0);
		fileToXMap.put('\u0000', 0);
		xToFileMap.put(0, '\u0000');
		char start = 'a';
		for(int i = 1; i < 9; i++) {
			fileToXMap.put(start, i);
			fileToXMap.put(Character.toUpperCase(start), i);
			xToFileMap.put(i, start);
			start++;
		}
	}
	
//	****************** CONSTRUCTORS ******************
	
	/**
	 * Creates an identical copy of this square.
	 * 
	 * @param o The square to be copied.
	 */
	public Square(Square o) {
		this(o.getX(), o.getY());
	}
	
	/**
	 * Creates a new square given a location and a color.
	 * 
	 * @param o The square to be copied.
	 * @param colorOccupied The color of the piece occupying this square.
	 */
	public Square(Square o, int colorOccupied) {
		this(o.getX(), o.getY());
	}
	
	/**
	 * Creates a square, accounting for the color of the piece occupying it.
	 * Should only be called when the position is initialized.
	 * 
	 * @param p The occupying piece.
	 */
	public Square(Piece p) {
		this(p.getCurrentSquare());
	}
	
	/**
	 * Initializes an unoccupied square given a string of algebraic notation, such as e4 or c5.
	 * 
	 * @param s The input string.
	 * @throws IndexOutOfBoundsException if the input string is longer than two characters.
	 * @throws AlgebraicInputException if the string is not a valid representation of a square.
	 * @throws IndexOutOfBoardException if the string represents a valid square, but is not actually on the board.
	 */
	public Square(String s) {
		this(s, 0);
	}
	
	/**
	 * Initializes a square given a string of algebraic notation, such as e4 or c5, and the color of the piece occupying it.
	 * 
	 * @param s The input string.
	 * @param colorOccupied The color of the piece on the square, as defined by the Piece class.
	 * @throws IndexOutOfBoundsException if the input string is longer than two characters.
	 * @throws AlgebraicInputException if the string is not a valid representation of a square.
	 * @throws IndexOutOfBoardException if the string represents a valid square, but is not actually on the board.
	 */
	public Square(String s, int colorOccupied) {
		this(s.charAt(0), Character.getNumericValue(s.charAt(1)), colorOccupied);
		if(!s.matches("(?i)^[a-h][1-8]$"))
			throw new AlgebraicInputException("Argument is not a valid string representation of a square.");
	}
	
	/**
	 * Initializes a square given only its file. Rank is assigned a value of 0.
	 * If the character parameter turns out to be an integer, then assigns the rank instead.
	 * 
	 * @param file The character of the file.
	 * @throws IndexOutOfBoardException if the specified file is not between a-h.
	 */
	public Square(char file) {
		if(Character.isDigit(file)) {
			this.setRank(Character.getNumericValue(file));
			this.setFile('\u0000');
		}
		else {
			this.setFile(file);
			this.setRank(0);
		}
		if(x < 0 || x > 8 || y < 0 || y > 8)
			throw new IndexOutOfBoardException();
	}
	
	/**
	 * Initializes a square given only its rank. File is assigned a value of '\u0000'.
	 * 
	 * @param rank The integer value of the rank.
	 * @throws IndexOutOfBoardException if the specified rank is not within 0-8.
	 */
	public Square(int rank) {
		this(0, rank);
	}
	
	/**
	 * Initializes an unoccupied square given the rank as an integer and file as a character.
	 * 
	 * @param file A character a-h.
	 * @param rank An integer 1-8.
	 * @throws IndexOutOfBoardException if the specified file is not between a-h, or the rank is not within 0-8.
	 */
	public Square(char file, int rank) {
		this(file, rank, 0);
	}
	
	/**
	 * Initializes an square given the rank as an integer and file as a character, and the color of the piece occupying it.
	 * The values of the colorOccupied parameter are as defined in the Piece class; 0 represents a blank square, 1 represents
	 * a white piece, 2 represents a black piece, and 3 represents any other value.
	 * 
	 * @param file A character a-h.
	 * @param rank An integer 1-8.
	 * @param colorOccupied The color of the piece on the square, as defined by the Piece class.
	 * @throws IndexOutOfBoardException if the specified file is not between a-h, or the rank is not within 1-8.
	 */
	public Square(char file, int rank, int colorOccupied) {
		this(fileToXMap.get(file), rank, colorOccupied);
	}
	
	/**
	 * Initializes a unoccupied square given the rank and file as integers.
	 * The values of the colorOccupied parameter are as defined in the Piece class; 0 represents a blank square, 1 represents
	 * a white piece, 2 represents a black piece, and 3 represents any other value.
	 * 
	 * @param x An integer 1-8, representing the file.
	 * @param y An integer 1-8, representing the rank.
	 * @param colorOccupied The color of the piece on the square, as defined by the Piece class.
	 * @throws IndexOutOfBoardException if x and y are not within 1-8.
	 */
	public Square(int x, int y, int colorOccupied) {
		if(x < 1 || x > 8 || y < 1 || y > 8)
			throw new IndexOutOfBoardException();
		this.x = x;
		this.y = y;
		this.file = xToFileMap.get(x);
		this.rank = y;
	}
	
	/**
	 * Initializes an unoccupied square.
	 * This constructor instead checks if x and y are between 0 and 8 instead of 1-8.
	 * A value of 0 for either parameter indicates an unknown parameter.
	 * This should only be called when creating squares where one value is unknown,
	 * or when creating a label square.
	 * 
	 * @param x An integer 0-8, representing the file.
	 * @param y An integer 0-8, representing the rank.
	 */
	public Square(int x, int y) {
		if(x < 0 || x > 8 || y < 0 || y > 8)
			throw new IndexOutOfBoardException();
		this.x = x;
		this.y = y;
		this.file = xToFileMap.get(x);
		this.rank = y;
	}
	
	/**
	 * Checks if this square has the same location as another square.
	 * Other properties, such as the color of the occupying piece, are irrelevant.
	 * 
	 * @param o The square to be checked against.
	 * @return True if and only if the squares have the same coordinates.
	 */
	public boolean equals(Square o) {
		return this.getFile() == o.getFile() && this.getRank() == o.getRank();
	}
	
//	****************** GETTERS AND SETTERS ******************

	/**
	 * Returns a string representation of this square, with its file and rank.
	 * 
	 * @return A two-character string of coordinates, such as "e4".
	 */
	@Override
	public String toString() {
		return "" + file + rank;
	}
	
	public char getFile() {
		return file;
	}

	public void setFile(char file) {
		this.file = file;
		this.x = fileToXMap.get(file);
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
		this.y = rank;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
		this.file = xToFileMap.get(x);
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
		this.rank = y;
	}
	
}
