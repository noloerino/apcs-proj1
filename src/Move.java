
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class Move {
	
	/**
	 * Controls whether square objects should be passed 
	 * by reference, or as cloned values.
	 */
	private static boolean squaresByReference = false;
	
//	****************** PROPERTIES ******************
	
	private String inputString;
	private Square targetSquare;
	private Square startSquare; // Optional
	private char pieceType;
	private char specifier;
	private boolean capture = false;
	private boolean check = false;
	private boolean checkmate = false;
	private boolean needsSpecifier;
	private int castling = 0; // 0 represents no castle, 1 for kingside, 2 for queenside
	private char promotingTo; // Pawns only
	private int notationType; // For debugging, as defined by fields
	private boolean legal;
	private int gameEnded = 0; // 0 represents in progress, 1 means white wins, 2 means black wins, 3 means draw
	
//	****************** REGULAR EXPRESSIONS ******************

	/**
	 * Regex specifying the verbose description of a move in long algebraic notation.
	 * The type of piece, its start square, end square, and possible promotions are specified.
	 */
	final public static String LONG_VERBOSE = "(?i)^((pawn|bishop|knight|rook|queen|king)\\s+" // piece
			+ "((on|from)\\s+)?)?[a-h][1-8]\\s+" // start square
			+ "((to|takes|x|captures)\\s+)?[a-h][1-8]" // end square
			+ "(\\s+((promotes to)|becomes)\\s+(bishop|knight|rook|queen))?\\s*$"; // promotion
	/**
	 * Regex specifying the verbose description of a move in standard algebraic notation.
	 * The type of piece, its end square, and possible promotions are specified.
	 */
	final public static String SHORT_VERBOSE = "(?i)^((pawn|bishop|knight|rook|queen|king)\\s+" //piece
			+ "((on|from)\\s+)?)?+([a-h1-8]\\s+)?" //specifier
			+ "((to|takes|x|captures)\\s+)?[a-h][1-8]" // destination
			+ "(\\s+((promotes to)|becomes)\\s+(bishop|knight|rook|queen))?\\s*$"; // promotion
	/**
	 * Regex specifying a description of a move specifying only the start and end square of a move.
	 * Other information about the move must be filled from the implementing class.
	 */
	final public static String SIMPLE_VERBOSE = "(?i)^[a-h][1-8]\\s+((to|takes|x|captures)\\s+)?[a-h][1-8]\\s*$";
	/**
	 * Regex specifying a castling move.
	 */
	final public static String CASLTE_VERBOSE = "(?i)^castles?\\s+(king|queen)side\\s*$";
	/**
	 * String indicating that a player wishes to resign.
	 */
	final public static String RESIGN = "(?i)^resigns?\\s*$"; //TODO implement
	/**
	 * String indicating that a player wishes for a draw.
	 */
	final public static String DRAW = "(?i)draw\\s*$"; //TODO implement
	/**
	 * Regex specifying any move in long algebraic notation; for example, Re1-e4 as opposed to Re4.
	 * The optional first character must be either 'B', 'N', 'R', 'Q', 'K', or 'P'.
	 * The second and third characters must specify the starting file and rank of the piece.
	 * A hyphen is used to indicate movement, an 'x' to indicate capture.
	 * The fifth and sixth characters must specify the target square.
	 * Optional characters for promotion and checks and annotation are appended.
	 */
	final public static String LONG_ALGEBRAIC = "(?i)^[BNRQKP]?[a-h][1-8][x\\s\\-]?[a-h][1-8]"
			+ "(=[BNRQ])?[\\+#]?\\s*([!?][!?]?)?$";
	/**
	 * Regex specifying a castle move.
	 * Matched expressions must take the form 0-0 or O-O (kingside castle), or 0-0-0 or O-O-O (queenside castle)
	 */
	final public static String CASTLE = "(?i)[O0]-[O0](-[O0])?\\s*([!?][!?]?)?$";
	/**
	 * Regex specifying a move by a major piece, or anything other than a pawn in algebraic notation.
	 * The first character must be either 'B', 'N', 'R', 'Q', 'K', or 'P'.
	 * An 'x' is optional to denote capture.
	 * A letter between a-h or a number between 1-8 is optional to specify the original square of the piece.
	 * A letter between a-h is required to specify the destination file.
	 * A number between 1-8 is required to specify the destination rank.
	 * A '+' or '#' is optional, to denote check or checkmate.
	 * Optional characters for promotion and checks are appended.
	 */
	final public static String PIECE_MOVE = "(?i)^[BNRQKP]([a-h1-8])?x?[a-h][1-8][\\+#]?\\s*([!?][!?]?)?$";
	/**
	 * Regex specifying a move by a pawn in algebraic notation.
	 * A specifying letter a-h, followed by an 'x' is optional to denote capture.
	 * A letter between a-h is required to specify the destination file.
	 * A number between 1-8 is required to specify the destination rank.
	 * A '=' followed by a character 'B', 'N', 'R', or 'Q' is optional to denote promotion.
	 * A '+' or '#' is optional, to denote check or checkmate.
	 */
	final public static String PAWN_MOVE = "(?i)^([a-h]x)?[a-h][1-8](=[BNRQ])?[\\+#]?\\s*([!?][!?]?)?$";
	/**
	 * Regex specifying the result of a game, usually to indicate resignation or a draw offer.
	 * Matched inputs must take the form "1-0" (white wins), "0-1" (black wins), or "1/2-1/2"
	 * or "0.5-0.5" to denote a draw.
	 */
	final public static String RESULT = "^(1-0)|(0-1)|(1/2-1/2)|(0.5-0.5)\\s*$";
	/**
	 * Checks if an expression contains a piece.
	 */
	final private static String PIECES = "(?i)^[BNRQKP]";
	
//	****************** OTHER FIELDS ******************
	
	final private static Map<String, Character> pieceTypeStrToChar = createPieceTypeMap();
	final private static Map<Integer, String> notationTypeMap = createMoveTypeMap();
	final private static int IS_STANDARD_ALGEBRAIC = 0;
	final private static int IS_LONG_ALGEBRAIC = 1;
	final private static int IS_LONG_VERBOSE = 2;
	final private static int IS_SHORT_VERBOSE = 3;
	final private static int IS_SIMPLE_VERBOSE = 4;
	final public static int NOT_CASTLE = 0;
	final public static int KINGSIDE_CASTLE = 1;
	final public static int QUEENSIDE_CASTLE = 2;
	
//	****************** TESTER ******************

	/**
	 * Tests whether or not a given pattern is a move, for debugging purposes.
	 * 
	 * @param args Command line arguments can be given to check the validity of each argument.
	 */
	public static void main(String[] args) {
		
		System.out.println("MOVE INPUT TESTER");
		if(args.length > 0) {
			String[] input = new String[args.length];
			System.out.println("Your inputs were: ");
			for(String s : input) {
				System.out.println("Received: " + s);
				try {
					System.out.println(Move.getDebugString(new Move(s)));
				}
				catch(AlgebraicInputException e) {
					System.out.println("Not a valid move!");
				}
				finally {
					System.out.println();
				}
			}
		}
		else
			main();
	}
	
	/**
	 * Tests whether or not input patterns are moves, for debugging purposes.
	 */
	public static void main() {
		@SuppressWarnings("resource")
		java.util.Scanner sc = new java.util.Scanner(System.in);
		String input;
		while(true) {
			System.out.print("Enter a string representing a move: ");
			try {
				input = sc.nextLine();
				if(input.equals("quit"))
					return;
				System.out.println(Move.getDebugString(new Move(input)));
			}
			catch(AlgebraicInputException e) {
				System.out.println("Not a valid move!");
			}
			finally {
				System.out.println();
			}
		}
	}
	
//	****************** CONSTRUCTORS ******************

	/**
	 * Creates an identical copy of another move.
	 * 
	 * @param o The move to be copied.
	 */
	public Move(Move o) {
		inputString = o.getInputString();
		targetSquare = new Square(o.getTargetSquare());
		if(o.getStartSquare() != null)
			startSquare = new Square(o.getStartSquare());
		pieceType = o.getPieceType();
		specifier = o.getSpecifier();
		capture = o.isCapture();
		check = o.isCheck();
		checkmate = o.isCheckmate();
		needsSpecifier = o.needsSpecifier();
		castling = o.getCastling();
		promotingTo = o.getPromotingTo();
		notationType = o.getNotationType();
		legal = o.isLegal();
	}
	
	/**
	 * Creates a new move object given a string in algebraic notation.
	 * The input can be specified in one of three general formats, parsed by regex:
	 * 1. Standard algebraic notation, specifying the type of piece and destination square
	 * 2. Long algebraic notation, specifying the origin square as well as the destination.
	 * 3. A verbose string, such as "rook from e1 to e4."
	 * 
	 * @param input A string representing a move.
	 */
	public Move(String input) {
		boolean isVerbose = isVerboseString(input);
		this.inputString = isVerbose?(input):(input.replaceAll("\\s",""));
		this.castling = interpretCastling(input);
		// TODO interpret result strings
		if(this.castling != 0)
			this.pieceType = 'K';
		else if(isLongAlgebraicString(inputString))
			this.selfTranslateLongAlgebraicString();
		else if(isStandardAlgebraicString(inputString))
				this.selfTranslateStandardAlgebraicString();
		else {
			if(isSimpleVerboseString(inputString)) // This check must come first
				this.selfTranslateSimpleVerboseString();
			else if(isShortVerboseString(inputString))
				this.selfTranslateShortVerboseString();
			else if(isLongVerboseString(inputString))
				this.selfTranslateLongVerboseString();
			else
				throw new AlgebraicInputException("Argument did not match verbose input format.");
		}
		if(this.isCapture())
			this.setNeedsSpecifier(true);
	}
	
	/**
	 * Makes a move that represents the end of a game.
	 * 
	 * @param gameEnded An integer indicating the result of the game.
	 * 1 means white wins, 2 means black wins, 3 means it's a draw.
	 */
	public Move(int gameEnded) {
		this.gameEnded = gameEnded;
	}
	
	/**
	 * Creates a move object of the form Re1; that is, moves a piece to a destination without capture.
	 * 
	 * @param pieceType The type of piece moving.
	 * @param targetX The destination file.
	 * @param targetY The destination rank.
	 */
	public Move(char pieceType, char targetX, int targetY) {
		this(pieceType, '\u0000', false, targetX, targetY, '\u0000', false);
	}
	
	/**
	 * Creates a new move object given the piece type, starting square, and coordinates of the
	 * destination square.
	 * 
	 * @param pieceType The type of piece moving.
	 * @param startSquare The starting square.
	 * @param targetX The destination file.
	 * @param targetY The destination rank.
	 */
	public Move(char pieceType, Square startSquare, int targetX, int targetY) {
		this(pieceType, Square.xToFileMap.get(targetX), targetY);
		this.startSquare = new Square(startSquare);
		this.promotingTo = 0;
	}
	
	/**
	 * Creates a move object, given all the information about it.
	 * 
	 * @param pieceType A character representing the type of piece that is moving, as outlined in the Piece class.
	 * @param specifier A specifier for the starting rank or file of the piece.
	 * @param capture Whether or not the piece is capturing another piece.
	 * @param targetX The target file.
	 * @param targetY The target rank.
	 * @param promotingTo The type of piece this is promoting to. Should be invoked only on pawns.
	 * @param check Whether or not the move causes a check.
	 */
	public Move(char pieceType, char specifier, boolean capture, char targetX, int targetY, char promotingTo, boolean check) {
		this.pieceType = pieceType;
		this.capture = capture;
		this.targetSquare = new Square(targetX, targetY);
		this.promotingTo = promotingTo;
		this.castling = 0;
		this.check = check;
		this.specifier = specifier;
		if(Character.isDigit(specifier))
			this.startSquare = new Square(Character.getNumericValue(specifier));
		else
			this.startSquare = new Square(specifier);
	}
	
	/**
	 * Creates a new "null move", a blank move with no values initialized.
	 */
	protected Move() {
		this.inputString = "NULL";
	}
	
	/**
	 * Converts a string from long algebraic notation to short algebraic notation.
	 * Used to match an input move against the array of possible moves.
	 * 
	 * @param s A string of long algebraic notation.
	 * @return A string of standard algebraic notation.
	 */
	public static String convertLAToSA(String s) {
		if(!isLongAlgebraicString(s))
			throw new AlgebraicInputException("Argument is not a long algebraic string.");
		// TODO result string
		// For castling
		if(interpretCastling(s) != 0)
			return s;
		// For a piece move
		if(s.matches(PIECES))
			return s.substring(0,1) + (s.contains("x")?s.substring(3):s.substring(4));
		// For a pawn move
		else
			return s.substring(0,1) + (s.contains("x")?s.substring(2):s.substring(3));
	}
	
	/**
	 * Returns information about the move, for debugging purposes.
	 * 
	 * @param m The move being examined.
	 * @return Information about the move.
	 */
	public static String getDebugString(Move m) {
		if(m.getCastling() == 0) {
			return "DEBUGGING MOVE"
					+ "\nOriginal input: " + m.getInputString()
					+ "\nOriginal input type: " + m.getNotationTypeStr()
					+ "\nStandard algebraic notation: " + m.getAlgebraicString()
					+ "\nLong algebraic notation: " + m.getLongAlgebraicString()
					+ "\nSpecifier: " + m.getSpecifier()
					+ "\nNeeds specifier: " + m.needsSpecifier()
					+ "\nTarget square: " + m.getTargetSquare()
					+ "\nTarget X: " + m.getTargetX()
					+ "\nTarget Y: " + m.getTargetY()
					+ "\nStart square: " + m.getStartSquare() 
					+ "\nStart X: " + m.getStartX()
					+ "\nStart Y: " + m.getStartY()
					+ "\nPiece type: " + m.getPieceType()
					+ "\nCapture: " + m.isCapture()
					+ "\nCheck: " + m.isCheck()
					+ "\nCheckmate: " + m.isCheckmate()
					+ "\nCastle: " + m.getCastling()
					+ "\nPromoting to: " + m.getPromotingTo();
		}
		else {
			return "DEGUGGING MOVE"
					+ "\nOriginal input: " + m.getInputString()
					+ "\nOriginal input type: " + m.getNotationTypeStr()
					+ "\nStandard algebraic notation: " + m.getAlgebraicString()
					+ "\nLong algebraic notation: " + m.getLongAlgebraicString()
					+ "\nPiece type: " + m.getPieceType()
					+ "\nCastle: " + m.getCastling();
		}
	}
	
	/**
	 * Returns the standard algebraic notation of the move.
	 * 
	 * @return The algebraic notation.
	 */
	@Override
	public String toString() {
		return this.toAlgebraicString();
	}
	
	/**
	 * Returns an string of the move.
	 * Accepts a parameter to determine the type of notation, "l" for long algebraic, 
	 * "d" for a verbose representation, and anything else for standard algebraic.
	 * 
	 * @param mode The type of string to return.
	 * @return A string representation of the move.
	 */
	public String toString(String mode) {
		if(mode.charAt(0) == 'l')
			return this.toLongAlgebraicString();
		else if(mode.charAt(0) == 'd')
			return getDebugString(this);
		else
			return this.toString();
	}

//  ****************** HELPER METHODS ******************
	
	/**
	 * Translates a long verbose input into properties of a move.
	 */
	private void selfTranslateLongVerboseString() {
		this.pieceType = interpretVerbosePieceType(inputString);
		if(this.pieceType == 'P' || this.pieceType == 'p') {
			try {
				this.promotingTo = interpretVerbosePromotion(inputString);
			}
			catch(IndexOutOfBoundsException | IllegalStateException e) {

			}
		}
		this.startSquare = new Square(interpretLongVerboseStartSquare(inputString));
		this.targetSquare = new Square(interpretLongVerboseTargetSquare(inputString));
		this.capture = interpretVerboseCapture(this.inputString);
		this.notationType = IS_LONG_VERBOSE;
	}
	
	/**
	 * Translates a short verbose input into properties of a move.
	 */

	private void selfTranslateShortVerboseString() {
		this.pieceType = interpretVerbosePieceType(inputString);
		if(this.pieceType == 'P' || this.pieceType == 'p') {
			try {
				this.promotingTo = interpretVerbosePromotion(inputString);
			}
			catch(IndexOutOfBoundsException | IllegalStateException e) {

			}
		}
		this.specifier = interpretVerboseSpecifier(inputString);
		this.startSquare = new Square(specifier);
		this.targetSquare = new Square(interpretShortVerboseTargetSquare(inputString));
		this.capture = interpretVerboseCapture(this.inputString);
		this.notationType = IS_SHORT_VERBOSE;
	}
	
	/**
	 * Translates a simple verbose input into properties of a move.
	 */
	private void selfTranslateSimpleVerboseString() {
		this.startSquare = new Square(interpretSimpleVerboseStartSquare(inputString));
		this.targetSquare = new Square(interpretSimpleVerboseTargetSquare(inputString));
		this.notationType = IS_SIMPLE_VERBOSE;
	}
	
	/**
	 * Translates a long algebraic string into properties of a move.
	 * Part of the implementation uses charAt() rather than regex, which could
	 * be a potential source of error.
	 */
	private void selfTranslateLongAlgebraicString() {
		int i = 0;
		// Sets piece type
		this.pieceType = interpretPieceType(inputString);
		if(this.pieceType == 'P' || this.pieceType == 'p') {
			// Sets promotion
			try {
				this.promotingTo = interpretPromotion(inputString);
			}
			catch(IndexOutOfBoundsException | IllegalStateException e) {

			}
		}
		else
			i++;
		// Sets starting file and rank
		this.setStartSquare(new Square(inputString.charAt(i), Character.getNumericValue(inputString.charAt(i + 1))));
		if(inputString.matches("^.*[a-h][1-8][a-h][1-8].*$"))
			i += 2;
		else
			i += 3;
		// Sets capture
		this.capture = interpretCapture(inputString);
		// Sets destination file and rank
		this.setTargetSquare(new Square(inputString.charAt(i), Character.getNumericValue(inputString.charAt(i + 1))));
		// Sets check
		this.check = interpretCheck(inputString);
		// Sets checkmate
		this.check = interpretCheckmate(inputString);
		// Sets notation type
		this.notationType = IS_LONG_ALGEBRAIC;
	}
	
	/**
	 * Translates a standard algebraic string into properties of a move.
	 * Mostly dependent on static regex-interpreting methods, which are a little inefficient.
	 */
	private void selfTranslateStandardAlgebraicString() {
		// Sets target square
		String checker = "[a-h][1-8]";
		//assert(Pattern.matches(checker, algebraic));
		Matcher m = Pattern.compile(checker, Pattern.CASE_INSENSITIVE).matcher(inputString);
		String destination = "00"; // warning: unsafe code here
		while(m.find())
			destination = m.group();
		//System.out.println(destination);
		assert(destination.length() == 2);
		this.setTargetSquare(new Square(destination.charAt(0), Character.getNumericValue(destination.charAt(1))));
		// Sets start square, if any
		try {
			this.specifier = interpretSpecifier(inputString);
			if(Character.isDigit(this.specifier))
				this.setStartSquare(new Square(Character.getNumericValue(this.specifier)));
			else
				this.setStartSquare(new Square(specifier));
		}
		catch(IndexOutOfBoundsException | IllegalStateException e) {
			this.setStartSquare(new Square('\u0000'));
		}
		// Sets piece type
		this.pieceType = interpretPieceType(inputString);
		if(this.pieceType == 'P' || this.pieceType == 'p') {
			try {
				this.promotingTo = interpretPromotion(inputString);
			}
			catch(IndexOutOfBoundsException | IllegalStateException e) {

			}
		}
		// Sets whether or not is a capture
		this.capture = interpretCapture(inputString);
		// Sets whether or not is a check
		this.check = interpretCheck(inputString);
		// Sets whether or not is checkmate
		this.check = interpretCheckmate(inputString);
		// Sets notation type
		this.notationType = IS_STANDARD_ALGEBRAIC;
	}
	
	/**
	 * Gets the standard algebraic notation of a move.
	 * 
	 * @return A string of standard algebraic notation, such as Re4.
	 */
	public String toAlgebraicString() {
		String notation = "";
		if(this.inputString != null && this.inputString.equals("NULL"))
			notation += "..";
		else if(this.gameEnded == Piece.WHITE)
			notation = "1-0";
		else if(this.gameEnded == Piece.BLACK)
			notation = "0-1";
		else if(this.gameEnded == 3)
			notation = "0.5-0.5";
		else if(this.castling == 1)
			notation = "0-0";
		else if(this.castling == 2)
			notation = "0-0-0";
		else {
			if(this.getPieceType() == 'P' || this.getPieceType() == 'p');
			else
				notation += Character.toUpperCase(this.getPieceType());
			if(this.needsSpecifier() || 
					((this.getPieceType() == 'P' || this.getPieceType() == 'p') && this.isCapture()))
				notation += Character.toLowerCase(specifier);
			if(this.isCapture())
				notation += "x";
			notation += Character.toString(this.getTargetX()).toLowerCase()
					+ Integer.toString(this.getTargetY());
			if(this.getPromotingTo() != 0)
				notation += "=" + Character.toUpperCase(this.getPromotingTo());
			if(this.isCheckmate())
				notation += "#";
			else if(this.isCheck())
				notation += "+";
		}
		return notation;
		
	}
	
	/**
	 * Gets the long algebraic notation of a move.
	 * 
	 * @return A string of long algebraic notation, such as Re1-e4.
	 */
	public String toLongAlgebraicString() {
		String notation = "";
		if(this.gameEnded == Piece.WHITE)
			notation = "1-0";
		else if(this.gameEnded == Piece.BLACK)
			notation = "0-1";
		else if(this.gameEnded == 3)
			notation = "0.5-0.5";
		else if(this.castling == 1)
			notation = "0-0";
		else if(this.castling == 2)
			notation = "0-0-0";
		else {
			if(this.getPieceType() == 'P' || this.getPieceType() == 'p');
			else
				notation += Character.toUpperCase(this.getPieceType());
			notation += Character.toString(this.getStartX()).toLowerCase()
					+ Integer.toString(this.getStartY());
			if(this.isCapture())
				notation += "x";
			else
				notation += "-";
			notation += Character.toString(this.getTargetX()).toLowerCase()
					+ Integer.toString(this.getTargetY());
			if(this.getPromotingTo() != 0)
				notation += "=" + Character.toUpperCase(this.getPromotingTo());
			if(this.isCheckmate()) 
				notation += "#";
			else if (this.isCheck())
				notation += "+";
		}
		return notation;
	}
	
//	****************** STATIC HELPER METHODS ******************
	
	/**
	 * Checks if an input is a verbose input.
	 * 
	 * @param input The input string.
	 * @return True if the move matches the pattern for a verbose input.
	 */
	private static boolean isVerboseString(String input) {
		return input.matches(SHORT_VERBOSE) || input.matches(SIMPLE_VERBOSE) 
				|| input.matches(LONG_VERBOSE) || input.matches(CASLTE_VERBOSE);
	}
	
	/**
	 * Checks if an input is a long verbose input.
	 * 
	 * @param input The input string.
	 * @return True if the move matches the pattern for a long verbose input.
	 */
	private static boolean isLongVerboseString(String input) {
		return input.matches(LONG_VERBOSE);
	}
	
	/**
	 * Checks if an input is a short verbose input.
	 * 
	 * @param input The input string.
	 * @return True if the move matches the pattern for a short verbose input.
	 */
	private static boolean isShortVerboseString(String input) {
		return input.matches(SHORT_VERBOSE);
	}
	
	/**
	 * Checks if an input is a simple verbose input.
	 * 
	 * @param input The input string.
	 * @return True if the move matches the pattern for a simple verbose input.
	 */
	private static boolean isSimpleVerboseString(String input) {
		return input.matches(SIMPLE_VERBOSE);
	}
	
	/**
	 * Checks if an input string is valid in long algebraic notation.
	 * 
	 * @param input The input string.
	 * @return True if the move matches the pattern for long algebraic notation.
	 */
	public static boolean isLongAlgebraicString(String input) {
		return input.matches(LONG_ALGEBRAIC) || input.matches(CASTLE) || input.matches(RESULT);
	}
	
	/**
	 * Checks if an input string is valid in standard algebraic notation.
	 * 
	 * @param input The input string.
	 * @return True if the move matches any pattern for standard algebraic notation.
	 */
	public static boolean isStandardAlgebraicString(String input) {
		return input.matches(CASTLE) || input.matches(PIECE_MOVE) || input.matches(PAWN_MOVE) || input.matches(RESULT);
	}
	
	/**
	 * Interprets the type of piece that is moving.
	 * 
	 * Precondition: The input string is a valid verbose move input specifying a piece.
	 * 
	 * @param input A user-inputted string.
	 * @return The character corresponding to the type of piece.
	 */
	private static char interpretVerbosePieceType(String input) {
		String type = "(?i)^pawn|bishop|knight|rook|queen|king";
		Matcher m = Pattern.compile(type).matcher(input);
		if(m.find())
			return Move.pieceTypeStrToChar.get(m.group().toLowerCase());
		else
			return 'P';
	}
	
	/**
	 * Interprets the type of piece that is moving.
	 * 
	 * Precondition: The input string is a valid algebraic move input specifying a piece.
	 * 
	 * @param input A user-inputted string.
	 * @return The character corresponding to the type of piece.
	 */
	private static char interpretPieceType(String input) {
		if(input.matches("bx[a-h][1-8]") // Catches the special bxc6 case, which could represent a pawn or bishop capture
				|| input.matches("b[1-8][-\\s]?[A-Ha-h][1-8]"
						+ "\\s*([!?][!?]?)?") // Catches the case b2-b3, which would match incorrectly
				|| input.matches("^\\s*[a-h][1-8]\\s*([!?][!?]?)?$")) // Catches all pawn moves?
			return 'P';
		String type = "^[BNRQKPbnrqkp].+"; // No b because there's confusion with squares
		Matcher m = Pattern.compile(type).matcher(input);
		if(m.find())
			return m.group().charAt(0);
		else
			return 'P';
	}
	
	/**
	 * Interprets whether or not the move is a check.
	 * 
	 * Precondition: The input string is in valid algebraic notation.
	 * 
	 * @param input A user-inputted string.
	 * @return True if and only if the move is a check.
	 */
	private static boolean interpretCheck(String input) {
		String checks = ".+[\\+#]\\s*$";
		return input.matches(checks);
	}
	
	/**
	 * Interprets whether or not the move is checkmate.
	 * 
	 * Precondition: The input string is in valid algebraic notation.
	 * 
	 * @param input A user-inputted string.
	 * @return True if and only if the move is a checkmate.
	 */
	private static boolean interpretCheckmate(String input) {
		String checkmate = ".+#\\s*$";
		return input.matches(checkmate);
	}
	
	/**
	 * Interprets whether or not the move is a capture.
	 * 
	 * Precondition: The input string is a valid verbose move input.
	 * 
	 * @param input A user-inputted string.
	 * @return True if and only if the word "takes" or "captures" or "x" appears within the string.
	 */
	private static boolean interpretVerboseCapture(String input) {
		return input.contains("takes") || input.contains("captures") || input.matches("\\bx\\b");
	}
	
	/**
	 * Interprets whether or not the move is a capture.
	 * 
	 * Precondition: The input string is in valid algebraic notation.
	 * 
	 * @param input A user-inputted string.
	 * @return True if and only if the move is a capture.
	 */
	private static boolean interpretCapture(String input) {
		String captures = ".+x.+";
		return input.matches(captures);
	}
	
	/**
	 * Interprets the type of piece being promoted to.
	 * 
	 * Precondition: The input string is a valid verbose move input, and the piece moving is a pawn.
	 * 
	 * @param input A user-inputted string.
	 * @return The pieceType of the promoted piece.
	 */
	private static char interpretVerbosePromotion(String input) {
		String promotion = "(?<=(((promotes to)|becomes)\\s+))(bishop|knight|rook|queen)";
		Matcher m = Pattern.compile(promotion).matcher(input);
		m.find();
		return pieceTypeStrToChar.get(m.group().toLowerCase());
	}
	
	/**
	 * Interprets the type of piece being promoted to.
	 * 
	 * Precondition: The input string is in valid algebraic notation, and the piece moving is a pawn.
	 * 
	 * @param input A user-inputted string.
	 * @return The pieceType of the promoted piece.
	 */
	private static char interpretPromotion(String input) {
		String promotion = "(?i)=[BNRQ]";
		Matcher m = Pattern.compile(promotion).matcher(input);
		m.find();
		return m.group().charAt(1);
	}
	
	/**
	 * Interprets whether or not the move has a specifier.
	 * For example, if two rooks can go to e1, then the original file/rank must be specified with Ree1.
	 * 
	 * Precondition: The input string is a valid verbose move input.
	 * 
	 * @param input A user-inputted string.
	 * @return The character of the specifier.
	 */
	private static char interpretVerboseSpecifier(String input) {
		String specifier = "(?i)(^|(?<=((on|from)\\s+)))[a-h1-8](?=\\s)";
		// Note: for whatever reason, matches fail on "from 1 to d3" but succeed on "pawn from 1 to d3"
		Matcher m = Pattern.compile(specifier).matcher(input);
		if(m.find())
			return m.group().charAt(0);
		else
			return '\u0000';
	}
	
	/**
	 * Interprets whether or not the move has a specifier.
	 * For example, if two rooks can go to e1, then the original file/rank must be specified with Ree1.
	 * 
	 * Precondition: The input string is in valid algebraic notation.
	 * 
	 * @param input A user-inputted string.
	 * @return The character of the specifier.
	 */
	private static char interpretSpecifier(String input) {
		// Bxc3 must return nothing
		// bxc3 must return b
		// bc3 must return nothing
		String pawnTakesSpecifier = "^\\s*[a-hA-H&&[^B]]x(?=[a-hA-H][1-8])";
		Matcher m1 = Pattern.compile(pawnTakesSpecifier).matcher(input);
		if(m1.find()) 
			return m1.group().charAt(0);
//		String weirdThingSpecifier = "";
//		Matcher m4 = Pattern.compile(weirdThingSpecifier).matcher(input);
//		if(m4.find())
//			return m4.group().charAt(0);
		String generalSpecifier = "(?<=[BNRQKPbnrqkp]|^)[a-hA-H1-8&&[^Bb]]x?(?=[a-hA-H][1-8])";
		Matcher m2 = Pattern.compile(generalSpecifier).matcher(input);
		if(m2.find())
			return m2.group().charAt(0);
		// Accounts for moves like "Nbd2" that use 'b' as a specifier
		else {
			String bFileSpecifier = "(?<=[BNRQKPbnrqkp])[a-hA-H1-8]x?(?=[a-hA-H][1-8])";
			Matcher m3 = Pattern.compile(bFileSpecifier).matcher(input);
			m3.find();
			return m3.group().charAt(0);
		}
	}
	
	/**
	 * Interprets the starting square of a long verbose move input.
	 * 
	 * Precondition: The input string is a valid long verbose move input.
	 * 
	 * @param input A user-inputted string.
	 * @return The string representation of the starting square
	 */
	private static String interpretLongVerboseStartSquare(String input) {
		String startSquare = "(?i)(^|(?<=((pawn|bishop|knight|rook|queen|king)\\s+((on|from)\\s+)?)))"
				+ "[a-h][1-8](?=\\b)"; // square
		Matcher m = Pattern.compile(startSquare).matcher(input);
		m.find();
		return m.group();
	}
	
	/**
	 * Interprets the target square of a long verbose move input.
	 * 
	 * Precondition: The input string is a valid long verbose move input.
	 * 
	 * @param input A user-inputted string.
	 * @return The string representation of the starting square.
	 */
	private static String interpretLongVerboseTargetSquare(String input) {
		String targetSquare = "(?<=[a-h][1-8]\\s(\\s*(to|takes|x|captures)\\s+)?)[a-h][1-8]";
		Matcher m = Pattern.compile(targetSquare).matcher(input);
		m.find();
		return m.group();
	}
	
	private static String interpretSimpleVerboseStartSquare(String input) {
		String startSquare = "[a-h][1-8]";
		Matcher m = Pattern.compile(startSquare).matcher(input);
		m.find();
		return m.group();
	}
	
	/**
	 * Interprets the target square of a short verbose input by finding the last
	 * occurrence of a square string in the input string.
	 * 
	 * Precondition: The input string is a valid short verbose move input.
	 * 
	 * @param input A user-inputted string.
	 * @return The string representation of the ending square.
	 */
	private static String interpretShortVerboseTargetSquare(String input) {
		String targetSquare = "[a-h][1-8]";
		Matcher m = Pattern.compile(targetSquare).matcher(input);
		String match = null; // warning: unsafe code here
		while(m.find())
			match = m.group();
		return match;
	}
	
	/**
	 * Interprets the target square of a simple verbose input by finding the last
	 * occurrence of a square string in the input string.
	 * This is identical to interpretShortVerboseTargetSquare(input).
	 * 
	 * Precondition: The input string is a valid simple verbose move input.
	 * 
	 * @param input A user-inputted string.
	 * @return The string representation of the ending square.
	 */
	private static String interpretSimpleVerboseTargetSquare(String input) {
		return interpretShortVerboseTargetSquare(input);
	}
	
	/**
	 * Interprets if a move is a castle.
	 * Identical to interpretCastling(input).
	 * 
	 * @param input A user-inputted string.
	 * @return 0 if it is not a castling move, 1 for a kingside castle, and 2 for a queenside castle.
	 */
	@SuppressWarnings("unused")
	private static int interpretVerboseCastling(String input) {
		return interpretCastling(input);
	}
	
	/**
	 * Interprets if a move is a castle.
	 * Works for both verbose and algebraic inputs.
	 * 
	 * @param input A user-inputted string.
	 * @return 0 if it is not a castling move, 1 for a kingside castle, and 2 for a queenside castle.
	 */
	private static int interpretCastling(String input) {
		String kingside = "(?i)^[O0]-[O0]\\s*$";
		String queenside = "(?i)^[O0]-[O0]-[O0]\\s*$";
		int castling = 0;
		if(input.matches(CASLTE_VERBOSE))
			if(input.contains("king"))
				castling = 1;
			else
				castling = 2;
		else if(input.matches(kingside)) // kingside castle
			castling = 1;
		else if(input.matches(queenside)) // queenside castle
			castling = 2;
		return castling;
	}

//	****************** GETTERS AND SETTERS ******************
	
	/**
	 * Checks if the move has a completely specified starting square.
	 * 
	 * @return True if and only if both coordinates of the starting square have a value.
	 */
	public boolean hasStartSquare() {
		return startSquare.getX() != 0 && startSquare.getY() != 0;
	}
	
	/**
	 * Checks if the move has the start file specified.
	 * 
	 * @return True if and only if the x coordinate of the starting square has a value.
	 */
	public boolean hasStartFile() {
		return startSquare.getX() != 0;
	}
	
	/**
	 * Checks if the move has the start rank specified.
	 * 
	 * @return True if and only if the y coordinate of the starting square has a value.
	 */
	public boolean hasStartRank() {
		return startSquare.getY() != 0;
	}
	
	/**
	 * Checks if the specified piece is not a pawn.
	 * The name is slightly misleading, but this check is performed because only pawn
	 * moves do not specify the type of piece that is moving.
	 * 
	 * @return True if pieceType is bishop, rook, knight, queen, or king.
	 */
	public boolean hasPieceType() {
		return pieceType != 'P' && pieceType != 'p';
	}
	
	public String getInputString() {
		return inputString;
	}

	public void setInputString(String inputString) {
		this.inputString = inputString;
	}
	
	public String getAlgebraicString() {
		return this.toAlgebraicString();
	}
	
	public String getLongAlgebraicString() {
		return this.toLongAlgebraicString();
	}

	public char getTargetX() {
		return this.targetSquare.getFile();
	}

	public void setTargetX(char targetX) {
		if(squaresByReference)
			this.targetSquare = new Square(this.targetSquare);
		this.targetSquare.setFile(targetX);
	}

	public int getTargetY() {
		return this.targetSquare.getRank();
	}

	public void setTargetY(int targetY) {
		if(squaresByReference)
			this.targetSquare = new Square(this.targetSquare);
		this.targetSquare.setRank(targetY);
	}

	public char getStartX() {
		try {
			return this.startSquare.getFile();
		}
		catch(NullPointerException e) {
			return 0;
		}
	}

	public void setStartX(char startX) {
		if(squaresByReference)
			this.startSquare = new Square(this.startSquare);
		this.startSquare.setFile(startX);
	}

	public int getStartY() {
		try {
			return this.startSquare.getRank();
		}
		catch(NullPointerException e) {
			return 0;
		}
	}

	public void setStartY(int startY) {
		if(squaresByReference)
			this.startSquare = new Square(this.startSquare);
		this.startSquare.setRank(startY);
		
	}

	public char getPieceType() {
		return pieceType;
	}
	
	public void setPieceType(Piece p) {
		this.pieceType = p.getIdentifier();
	}

	public void setPieceType(char pieceType) {
		this.pieceType = pieceType;
	}
	
	public char getSpecifier() {
		return specifier;
	}
	
	public void setSpecifier(char specifier) {
		this.specifier = specifier;
	}
	
	/**
	 * Sets the color of the piece being moved by changing the pieceType field.
	 * A value of 0 or 3 will set pieceType to be 'x', as this should not occur.
	 * A value of 1 will set the color to be white, so pieceType should be uppercase.
	 * A value of 2 will set the color to be black, so pieceType should be lowercase.
	 * 
	 * @param color The color of the piece.
	 */
	public void setPieceColor(int color) {
		if(color == 1)
			this.pieceType = Character.toUpperCase(this.pieceType);
		else if(color == 2)
			this.pieceType = Character.toLowerCase(this.pieceType);
		else
			this.pieceType = 'x';
	}

	public boolean isCapture() {
		return capture;
	}

	public void setCapture(boolean capture) {
		this.capture = capture;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
	
	public boolean isCheckmate() {
		return checkmate;
	}
	
	public void setCheckmate(boolean checkmate) {
		this.checkmate = checkmate;
	}

	/**
	 * Returns if the piece is castling.
	 * If there is no castling, returns 0; if it is a kingside castle, returns 1;
	 * if it is a queenside castle, returns 2.
	 * 
	 * @return An integer representing if the piece is castling.
	 */
	public int getCastling() {
		return castling;
	}

	public void setCastling(int castling) {
		this.castling = castling;
	}

	public char getPromotingTo() {
		return promotingTo;
	}

	public void setPromotingTo(char promotingTo) {
		this.promotingTo = promotingTo;
	}
	
	public boolean specifiesRank() {
		return Character.isDigit(specifier);
	}
	
	public boolean specifiesFile() {
		return Character.isAlphabetic(specifier);
	}
	
	public boolean needsSpecifier() {
		return needsSpecifier;
	}
	
	public void setNeedsSpecifier(boolean needsSpecifier) {
		this.needsSpecifier = needsSpecifier;
	}
	
	public boolean isLegal() {
		return legal;
	}
	
	public void setLegal(boolean legal) {
		this.legal = legal;
	}
	
	public Square getTargetSquare() {
		return targetSquare;
	}
	
	public void setTargetSquare(Square targetSquare) {
		this.targetSquare = new Square(targetSquare);
	}
	
	public Square getStartSquare() {
		return startSquare;
	}
	
	public void setStartSquare(Square startSquare) {
		this.startSquare = new Square(startSquare);
	}
	
	public int getNotationType() {
		return notationType;
	}
	
	public String getNotationTypeStr() {
		return notationTypeMap.get(notationType);
	}
	
	private static HashMap<String, Character> createPieceTypeMap() {
		HashMap<String, Character> m = new HashMap<String, Character>();
		m.put("knight", 'N');
		m.put("rook", 'R');
		m.put("bishop", 'B');
		m.put("queen", 'Q');
		m.put("pawn", 'p');
		m.put("king", 'K');
		return m;
	}
	
	private static HashMap<Integer, String> createMoveTypeMap() {
		HashMap<Integer, String> m = new HashMap<Integer, String>();
		m.put(Move.IS_LONG_ALGEBRAIC, "long algebraic");
		m.put(Move.IS_STANDARD_ALGEBRAIC, "standard algebraic");
		m.put(Move.IS_LONG_VERBOSE, "long verbose");
		m.put(Move.IS_SHORT_VERBOSE, "short verbose");
		m.put(Move.IS_SIMPLE_VERBOSE, "simple verbose");
		return m;
	}
	
}
