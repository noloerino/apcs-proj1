
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Superclass for all pieces. Includes getter and setter methods for values like location and color.
 * @author jhshi
 *
 */
public abstract class Piece {
	
//	****************** PROPERTIES ******************

	/**
	 * An integer representing the type of piece this is.
	 * Key:
	 * BLANK = 0
	 * WHITE_PAWN = 1
	 * BLACK_PAWN = 2
	 * WHITE_BISHOP = 3
	 * BLACK_BISHOP = 4
	 * WHITE_ROOK = 5
	 * BLACK_ROOK = 6
	 * WHITE_KNIGHT = 7
	 * BLACK_KNIGHT = 8
	 * WHITE_QUEEN = 9
	 * BLACK_QUEEN = 10
	 * WHITE_KING = 11
	 * BLACK_KING = 12
	 * LABEL = 13
	 * OTHER = 14
	 */
	private int pieceType;
	
	/**
	 * The icon of the piece that is displayed on the board.
	 * Possible values are as follows:
	 * "_|" = blank
	 * "x|" = error
	 * "K|" = white king
	 * "Q|" = white queen
	 * "R|" = white rook
	 * "B|" = white bishop
	 * "N|" = white knight
	 * "P|" = white pawn
	 * and lowercase letters for black pieces
	 */
	private String icon;
	
	/**
	 * Unicode has chess pieces. This takes full advantage of that.
	 */
	private String coolIcon;
	
	/**
	 * The character label for each type of piece, defined by pieceType.
	 * 'o' = blank
	 * 'l' = label
	 * 'x' = error
	 * for white:
	 * 'K' = king
	 * 'Q' = queen
	 * 'R' = rook
	 * 'B' = bishop
	 * 'N' = knight
	 * 'P' = pawn
	 * and lowercase for black pieces
	 */
	private char identifier;

	private Move lastMove;
	private Move nextMove;
	private Square currentSquare;
	private boolean captured = false; // Whether or not the piece has been captured.
	
//	****************** FIELDS ******************

	/**
	 * Tracks whether or not to use fancy icons.
	 * May or may not work, because unicode piece characters sometimes have
	 * different width than normal letters.
	 */
	public static boolean fancy = false;
	/**
	 * ANSI escape code that sets the background color for light squares.
	 */
	final public static String ANSI_LIGHT_SQUARE = "\033[48;5;250m";
	/**
	 * ANSI escape code that sets the background color for dark squares.
	 */
	final public static String ANSI_DARK_SQUARE = "\033[48;5;242m";
	/**
	 * ANSI escape code that sets the background color for border squares.
	 */
	final public static String ANSI_BORDER = "\033[48;5;246m";
	/**
	 * ANSI escape code that resets the terminal color.
	 */
	final public static String ANSI_RESET = "\033[0;00m";

	final public static int WHITE = 1;
	final public static int BLACK = 2;
	final public static int OTHER_COLOR = 3;
	final public static int BLANK = 0;
	final public static int WHITE_PAWN = 1;
	final public static int BLACK_PAWN = 2;
	final public static int WHITE_BISHOP = 3;
	final public static int BLACK_BISHOP = 4;
	final public static int WHITE_ROOK = 5;
	final public static int BLACK_ROOK = 6;
	final public static int WHITE_KNIGHT = 7;
	final public static int BLACK_KNIGHT = 8;
	final public static int WHITE_QUEEN = 9;
	final public static int BLACK_QUEEN = 10;
	final public static int WHITE_KING = 11;
	final public static int BLACK_KING = 12;
	final public static int LABEL = 13;
	final public static int OTHER = 14;
	/**
	 * Converts a string value of pieceType to an integer.
	 */
	final private static Map<String, Integer> pieceTypeStrToInt = new HashMap<String, Integer>();
	/**
	 * Converts the value of pieceType to the value of identifier.
	 */
	final private static char[] pieceTypeIntToChar = new char[15];
	/**
	 * Converts the value of pieceType to the value of its icon.
	 */
	final private static String[] pieceTypeIntToIcon = new String[15];
	/**
	 * Converts an icon to the corresponding fancy icon.
	 */
	final private static Map<String, String> iconToFancyIcon = new HashMap<String, String>();
	/**
	 * Initializes all maps and arrays.
	 */
	static {
		pieceTypeStrToInt.put("BLANK", BLANK);
		pieceTypeIntToChar[BLANK] = 'o';
		pieceTypeIntToIcon[BLANK] = "_|";
//		iconToFancyIcon.put("_|", "_|");
		iconToFancyIcon.put("_|", "  ");
		pieceTypeStrToInt.put("WHITE_PAWN", WHITE_PAWN);
		pieceTypeIntToChar[WHITE_PAWN] = 'P';
		pieceTypeIntToIcon[WHITE_PAWN] = "P|";
//		iconToFancyIcon.put("P|", "\u2659|");
		iconToFancyIcon.put("P|", "\u2659 ");
		pieceTypeStrToInt.put("BLACK_PAWN", BLACK_PAWN);
		pieceTypeIntToChar[BLACK_PAWN] = 'p';
		pieceTypeIntToIcon[BLACK_PAWN] = "p|";
//		iconToFancyIcon.put("p|", "\u265F|");
		iconToFancyIcon.put("p|", "\u265F ");
		pieceTypeStrToInt.put("WHITE_BISHOP", WHITE_BISHOP);
		pieceTypeIntToChar[WHITE_BISHOP] = 'B';
		pieceTypeIntToIcon[WHITE_BISHOP] = "B|";
//		iconToFancyIcon.put("B|", "\u2657|");
		iconToFancyIcon.put("B|", "\u2657 ");
		pieceTypeStrToInt.put("BLACK_BISHOP", BLACK_BISHOP);
		pieceTypeIntToChar[BLACK_BISHOP] = 'b';
		pieceTypeIntToIcon[BLACK_BISHOP] = "b|";
//		iconToFancyIcon.put("b|", "\u265D|");
		iconToFancyIcon.put("b|", "\u265D ");
		pieceTypeStrToInt.put("WHITE_ROOK", WHITE_ROOK);
		pieceTypeIntToChar[WHITE_ROOK] = 'R';
		pieceTypeIntToIcon[WHITE_ROOK] = "R|";
//		iconToFancyIcon.put("R|", "\u2656|");
		iconToFancyIcon.put("R|", "\u2656 ");
		pieceTypeStrToInt.put("BLACK_ROOK", BLACK_ROOK);
		pieceTypeIntToChar[BLACK_ROOK] = 'r';
		pieceTypeIntToIcon[BLACK_ROOK] = "r|";
//		iconToFancyIcon.put("r|", "\u265C|");
		iconToFancyIcon.put("r|", "\u265C ");
		pieceTypeStrToInt.put("WHITE_KNIGHT", WHITE_KNIGHT);
		pieceTypeIntToChar[WHITE_KNIGHT] = 'N';
		pieceTypeIntToIcon[WHITE_KNIGHT] = "N|";
//		iconToFancyIcon.put("N|", "\u2658|");
		iconToFancyIcon.put("N|", "\u2658 ");
		pieceTypeStrToInt.put("BLACK_KNIGHT", BLACK_KNIGHT);
		pieceTypeIntToChar[BLACK_KNIGHT] = 'n';
		pieceTypeIntToIcon[BLACK_KNIGHT] = "n|";
//		iconToFancyIcon.put("n|", "\u265E|");
		iconToFancyIcon.put("n|", "\u265E ");
		pieceTypeStrToInt.put("WHITE_QUEEN", WHITE_QUEEN);
		pieceTypeIntToChar[WHITE_QUEEN] = 'Q';
		pieceTypeIntToIcon[WHITE_QUEEN] = "Q|";
//		iconToFancyIcon.put("Q|", "\u2655|");
		iconToFancyIcon.put("Q|", "\u2655 ");
		pieceTypeStrToInt.put("BLACK_QUEEN", BLACK_QUEEN);
		pieceTypeIntToChar[BLACK_QUEEN] = 'q';
		pieceTypeIntToIcon[BLACK_QUEEN] = "q|";
//		iconToFancyIcon.put("q|", "\u265B|");
		iconToFancyIcon.put("q|", "\u265B ");
		pieceTypeStrToInt.put("WHITE_KING", WHITE_KING);
		pieceTypeIntToChar[WHITE_KING] = 'K';
		pieceTypeIntToIcon[WHITE_KING] = "K|";
//		iconToFancyIcon.put("K|", "\u2654|");
		iconToFancyIcon.put("K|", "\u2654 ");
		pieceTypeStrToInt.put("BLACK_KING", BLACK_KING);
		pieceTypeIntToChar[BLACK_KING] = 'k';
		pieceTypeIntToIcon[BLACK_KING] = "k|";
//		iconToFancyIcon.put("k|", "\u265A|");
		iconToFancyIcon.put("k|", "\u265A ");
		pieceTypeStrToInt.put("LABEL", LABEL);
		pieceTypeIntToChar[LABEL] = 'l';
		pieceTypeIntToIcon[LABEL] = "l|";
		iconToFancyIcon.put("l|", "l|");
		pieceTypeStrToInt.put("OTHER", OTHER);
		pieceTypeIntToChar[OTHER] = 'x';
		pieceTypeIntToIcon[OTHER] = "x|";
		iconToFancyIcon.put("x|", "x|");
	}
	
//	****************** CONSTRUCTORS ******************

	/**
	 * Creates a new piece. Should only be called by subclasses.
	 * 
	 * @param currentSquareStr The current square as represented by a string.
	 * @param pieceType The type of piece this is.
	 * @param icon The icon of the piece.
	 * @param coolIcon The unicode character representing the piece.
	 * @param identifier The character identifying the type of piece this is.
	 * @param color The color of the piece.
	 */
	protected Piece(String currentSquareStr, int pieceType, String icon, String coolIcon, char identifier, int color) {
		this(new Square(currentSquareStr), pieceType, icon, coolIcon, identifier, color);
	}
	
	/**
	 * Creates a new piece. Should only be called by subclasses.
	 * 
	 * @param currentSquare The current square.
	 * @param pieceType The type of piece this is.
	 * @param icon The icon of the piece.
	 * @param coolIcon The unicode character representing the piece.
	 * @param identifier The character identifying the type of piece this is.
	 * @param color The color of the piece.
	 */
	protected Piece(Square currentSquare, int pieceType, String icon, String coolIcon, char identifier, int color) {
		this.pieceType = pieceType;
		this.icon = icon;
		this.coolIcon = coolIcon;
		this.identifier = identifier;
		this.setCurrentSquare(currentSquare); // TODO change constructor
	}
	
	/**
	 * Creates a new piece. Should only be called by subclasses.
	 * 
	 * @param file The character representing the file of the piece.
	 * @param rank The integer representing the rank of the piece.
	 * @param pieceType The type of piece this is.
	 * @param icon The icon of the piece.
	 * @param coolIcon The unicode character representing the piece.
	 * @param identifier The character identifying the type of piece this is.
	 * @param color The color of the piece.
	 */
	protected Piece(char file, int rank, int pieceType, String icon, String coolIcon, char identifier, int color) {
		this(new Square(Character.toString(file) + Integer.toString(rank)), pieceType, icon, coolIcon, identifier, color);
	}
	
	/**
	 * Creates a new piece, given only the coordinates of the starting square and piece type.
	 * 
	 * @param file A character representing the file of the piece.
	 * @param rank An integer representing the rank of the piece.
	 * @param pieceTypeStr A string representing the type of piece.
	 */
	protected Piece(char file, int rank, String pieceTypeStr) {
		this(Character.toString(file) + Integer.toString(rank), pieceTypeStrToInt.get(pieceTypeStr));
	}
	
	/**
	 * Creates a new piece, given only the string representation of the starting square and piece type.
	 * 
	 * @param currentSquareStr A string representing the current square.
	 * @param pieceTypeStr A string representing the type of piece.
	 */
	protected Piece(String currentSquareStr, int pieceType) {
		this.setCurrentSquare(new Square(currentSquareStr));
		this.pieceType = pieceType;
		this.icon = pieceTypeIntToIcon[pieceType];
		this.coolIcon = iconToFancyIcon.get(icon);
		this.identifier = pieceTypeIntToChar[pieceType];
		
	}
	
	/**
	 * Creates labels for squares. Should be called only by the "label" subclass.
	 * 
	 * @param file The x-coordinate of the square.
	 * @param rank The y-coordinate of the square.
	 * @param label The letter or number representing the label.
	 */
	protected Piece(int file, int rank, char label) {
		if((file == 0 && !Character.toString(label).matches("[1-8 ]")) 
				|| (rank == 0 && !Character.toString(label).matches("[a-h ]")))
				throw new IndexOutOfBoardException("Square at index 0 must be a label.");
		this.setCurrentSquare(new Square(file, rank));
		this.pieceType = LABEL;
		this.identifier = 'l';
		this.icon = Character.toString(label) + " ";
		this.coolIcon = icon;
	}
	
	/**
	 * Creates a new blank square. Should only be called by the "blank" subclass.
	 * 
	 * @param file The x-coordinate of the square.
	 * @param rank The y-coordinate of the square.
	 */
	protected Piece(int file, int rank) {
		try {
			this.setCurrentSquare(new Square(file, rank));
			this.pieceType = BLANK;
			this.identifier = 'o';
			this.icon = "_|";
			this.coolIcon = iconToFancyIcon.get(icon);
		}
		catch(IndexOutOfBoardException e) {
			throw new IndexOutOfBoardException(file, rank);
		}
	}
	
	
//	****************** ABTRACT METHODS ******************
	
	/**
	 * Checks whether or not the attempted move is legal.
	 * 
	 * @param pos The current position.
	 * @return True if and only if the move is legal.
	 */
	abstract boolean checkLegalMove(Position pos);
	
	/**
	 * Returns the point value of this piece.
	 * 
	 * @return 0, 1, 3, 5, 9, or 999 depending on the type of piece.
	 */
	abstract int getValue();
	
	/**
	 * Finds every possible move that a piece can make.
	 * 
	 * @param pos The current position.
	 * @return A list of all possible moves.
	 */
	abstract List<Move> getPossibleMoves(Position pos);
	
	/**
	 * Returns a copy of this piece.
	 * 
	 * @return A copy of this piece.
	 */
	abstract Piece makeCopy();
	
//	****************** GETTERS/SETTERS ******************
	
	/**
	 * Updates parameters in anticipation of a new move.
	 */
	public void makeNextMove() {
		setCurrentSquare(nextMove.getTargetSquare());
		setLastMove(nextMove);
		nextMove = null;
	}
	
	/**
	 * Returns information about the piece.
	 * Should be overridden by the King, Rook, Pawn, Blank, and Label subclasses.
	 * 
	 * @return A string with about the piece.
	 */
	public String toDebugString() {
		return "DEBUGGING PIECE"
				+ "\nPiece type: " + getPieceType()
				+ "\nIcon: " + getIcon()
				+ "\nFancy icon: " + getCoolIcon()
				+ "\nIdentifier: " + getIdentifier()
				+ "\nNext move: " + getNextMove()
				+ "\nLast move: " + getLastMove()
				+ "\nCurrent square: " + getCurrentSquare()
				+ "\nTarget square: " + getTargetSquare()
				+ "\nCaptured: " + isCaptured();
	}
	
	/**
	 * Returns a string representation of the piece.
	 * 
	 * @param mode The mode the game is in.
	 * @return A string representation of the piece.
	 */
	public String toString(int mode) {
		switch(mode) {
			default:
				return this.toString();
			case 1:
				return this.toFancyString();
			case 2:
				return this.toDebugString();
		}
	}
	
	/**
	 * Returns the icon to be displayed on the board.
	 * If the global variable fancy is true, then coolIcon is used instead.
	 * Should be overridden by label and blank subclasses.
	 * 
	 * @return A string representing the square.
	 */
	public String toString() {
		return (fancy)?(this.toFancyString()):(this.getIcon());
	}
	
	/**
	 * Returns the cool icon and a background color.
	 * 
	 * @return A fancy string representing the square.
	 */
	public String toFancyString() {
		String fancy = "";
		if(this.isLabel())
			fancy += ANSI_BORDER;
		else if((this.getCurrentX() + this.getCurrentY()) % 2 == 1)
			fancy += ANSI_LIGHT_SQUARE;
		else if((this.getCurrentX() + this.getCurrentY()) % 2 == 0)
			fancy += ANSI_DARK_SQUARE;
		return fancy + this.getCoolIcon() + ANSI_RESET;
	}

	public int getPieceType() {
		return pieceType;
	}
	
	public void setPieceType(int pieceType) {
		this.pieceType = pieceType;
		this.setIdentifier(pieceTypeIntToChar[pieceType]);
		this.setIcon(pieceTypeIntToIcon[pieceType]);
		this.setCoolIcon(iconToFancyIcon.get(icon));
	}

	public String getIcon() {
		return icon;
	}

	private void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCoolIcon() {
		return coolIcon;
	}

	private void setCoolIcon(String coolIcon) {
		this.coolIcon = coolIcon;
	}

	public char getIdentifier() {
		return identifier;
	}

	private void setIdentifier(char identifier) {
		this.identifier = identifier;
	}
	
	public boolean isOn(Square sq) {
		return this.getCurrentSquare().equals(sq);
	}
	
	public boolean isOn(char file, int rank) {
		return this.getCurrentFile() == file && this.getCurrentRank() == rank;
	}
	
	public boolean isOn(int file, int rank) {
		return this.getCurrentX() == file && this.getCurrentY() == rank;
	}
	
	public boolean isOccupied() {
		return (getColor() == WHITE || getColor() == BLACK || getColor() == LABEL);
	}
	
	public int getOccupied() {
		return getColor();
	}

	public int getColor() {
		if(pieceType == BLANK)
			return BLANK;
		else if(pieceType == LABEL || pieceType == OTHER)
			return OTHER_COLOR;
		else if(pieceType % 2 == 1)
			return WHITE;
		else
			return BLACK;
	}
	
	public int getOppositeColor() {
		if(pieceType == WHITE)
			return BLACK;
		else if(pieceType == BLACK)
			return WHITE;
		else
			return BLANK;
	}

	public Move getNextMove() {
		return nextMove;
	}

	public void setNextMove(Move nextMove) {
		this.nextMove = nextMove;
	}
	
	public Move getLastMove() {
		return lastMove;
	}
	
	private void setLastMove(Move lastMove) {
		this.lastMove = lastMove;
	}
	
	public int getCurrentX() {
		return currentSquare.getX();
	}
	
	public char getCurrentFile() {
		return currentSquare.getFile();
	}
	
	public int getCurrentY() {
		return currentSquare.getY();
	}
	
	public int getCurrentRank() {
		return currentSquare.getRank();
	}

	public Square getCurrentSquare() {
		return currentSquare;
	}

	public void setCurrentSquare(Square startSquare) {
		this.currentSquare = startSquare;
	}
	
	public int getTargetX() {
		return getTargetSquare().getX();
	}
	
	public char getTargetFile() {
		return getTargetSquare().getFile();
	}
	
	public int getTargetY() {
		return getTargetSquare().getY();
	}
	
	public int getTargetRank() {
		return getTargetSquare().getRank();
	}

	public Square getTargetSquare() {
		return nextMove.getTargetSquare();
	}

	public boolean isCaptured() {
		return captured;
	}

	public void setCaptured(boolean captured) {
		this.captured = captured;
	}

	public boolean isBlank() {
		return false;
	}
	
	public boolean isWhite() {
		return getColor() == WHITE;
	}
	
	public boolean isBlack() {
		return getColor() == BLACK;
	}
	
	public boolean isPawn() {
		return false;
	}
	
	public boolean isKnight() {
		return false;
	}
	
	public boolean isBishop() {
		return false;
	}
	
	public boolean isRook() {
		return false;
	}
	
	public boolean isQueen() {
		return false;
	}
	
	public boolean isKing() {
		return false;
	}
	
	public boolean isLabel() {
		return false;
	}
	
	/**
	 * Checks if the square defined by the arguments can be moved to.
	 * Equivalent to pos.canStopOn(file, rank, this).
	 * 
	 * @param file The file of the square.
	 * @param rank The rank of the square.
	 * @param pos The current position.
	 * @return True if and only if the color of the destination square is blank
	 * or the opposite color piece.
	 */
	public boolean canStopOn(char file, int rank, Position pos) {
		return pos.canStopOn(file, rank, this);
	}
	
	/**
	 * Checks if the argument square can be moved to.
	 * Equivalent to pos.canStopOn(sq, this).
	 * 
	 * @param sq The target square.
	 * @param pos The current position.
	 * @return True if and only if the color of the destination square is blank
	 * or the opposite color piece.
	 */
	public boolean canStopOn(Square sq, Position pos) {
		return pos.canStopOn(sq, this);
	}
	
	/**
	 * Checks if the target square of this piece can be moved to.
	 * 
	 * @param pos The current position.
	 * @return True if and only if the color of the destination square is blank
	 * or the opposite color piece.
	 */
	public boolean canStopOnTargetSquare(Position pos) {
		return pos.canStopOn(this.getTargetSquare(), this);
	}
	
	/**
	 * Checks if this piece has any possible moves.
	 * 
	 * @param pos The current position.
	 * @return True if there are moves that can be made by this piece, false if not.
	 */
	public boolean hasLegalMoves(Position pos) {
		return this.getPossibleMoves(pos) != null;
	}
	
}

/*

	White to move
	  h g f e d c b a		Last 10 moves
	1|R|N|B|K|Q|B|N|R|		1.
	2|P|P|P|P|P|P|P|P|		2.
	3|_|_|_|_|_|_|_|_|		3.
	4|_|_|_|_|_|_|_|_|		4.
	5|_|_|_|_|_|_|_|_|		5.
	6|_|_|_|_|_|_|_|_|		6.
	7|p|p|p|p|p|p|p|p|		7.
	8|r|n|b|k|q|b|n|r|		8.
	Type "/help" for options.
	
	  a b c d e f g h
	8|r|n|b|k|q|b|n|r|
	7|p|p|p|p|p|p|p|p|
	6|_|_|_|_|_|_|_|_|
	5|_|_|_|_|_|_|_|_|
	4|_|_|_|_|_|_|_|_|
	3|_|_|_|_|_|_|_|_|
	2|P|P|P|P|P|P|P|P|
	1|R|N|B|K|Q|B|N|R|
	
Help
	1. See full move list
	2. Flip board
	3. Undo last move
	4. Resign
	5. Offer draw
	6. Export PGN
	7. See all shortcuts

/flip
/resign
/draw
/undo 
/list

 */
