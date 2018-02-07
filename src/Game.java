import java.util.List;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
	
	/**
	 * The scanner object associated with the game.
	 */
	private static Scanner sc;
	
	/**
	 * The current gamemode. 0 means sandbox, 1 means normal,
	 * 2 means blindfold.
	 */
	private int mode;
	/**
	 * The current position.
	 */
	private Position pos;
	/**
	 * The current halfmove count. If it's white's turn, it will be odd;
	 * if it's black's turn, it will be even.
	 */
	private int currentHalfMove;
	/**
	 * The status of the game, 0 for in progress, 1 for a white victory,
	 * 2 for a black victory, 3 for a draw, and 4 for exiting.
	 */
	private int gameStatus;
	/**
	 * The list of all moves, indexed by halfmove number.
	 */
	private List<Move> moveList;
	/**
	 * All previous positions the game has gone through, stored by value.
	 */
	private List<Position> positions = new LinkedList<Position>();
	
	public static int computer = 0; // 0 means no computer, 1 means white, 2 means black
	
	final private static String WHITE_MOVE = "White to move";
	final private static String BLACK_MOVE = "Black to move";
	final public static int SANDBOX = 0;
	final public static int NORMAL = 1;
	final public static int BLINDFOLD = 2;
	final public static int IN_PROGRESS = 0;
	final public static int WHITE_WIN = 1;
	final public static int BLACK_WIN = 2;
	final public static int DRAW = 3;
	
	public static void main(String[] args) {
		debugGame(new Scanner(System.in)
//				, "rnbqkbnr/pppppppp/8/4N3/8/8/PPPPPPPP/RNBQKB1R w KQkq - 0 1"
				);
	}
	
	/**
	 * Runs a normal game of chess, with legality checks and everything.
	 * 
	 * @param sc A scanner to be passed.
	 */
	public static void normalGame(Scanner sc) {
		Game g = new Game(sc);
		runNormalGame(g);
	}
	
	/**
	 * Plays a normal game of chess from a given position.
	 * 
	 * @param sc A scanner to be passed.
	 * @param fen A valid FEN string representing the starting position.
	 */
	public static void normalGame(Scanner sc, String fen) {
		Game g = new Game(sc, fen);
		runNormalGame(g);
	}
	
	/**
	 * Runs a normal game of chess given a game.
	 * 
	 * @param g The game.
	 */
	private static void runNormalGame(Game g) {
		if(System.console() != null)
			Piece.fancy = true;
		do {
			doPlayerTurn(g);
		} while(g.isInProgress());
		resolveGame(g);
	}
	
	/**
	 * Plays a game against the computer from the starting position.
	 * 
	 * @param sc A scanner object to be passed.
	 * @param playerIsWhite The side the player is.
	 */
	public static void vsComputerGame(Scanner sc, boolean playerIsWhite) {
		Game g = new Game(sc);
		runGameVsComputer(g, playerIsWhite);
	}
	
	/**
	 * Plays a game against the computer.
	 * 
	 * @param sc A scanner object to be passed.
	 * @param fen The starting FEN.
	 * @param playerIsWhite The side the player is.
	 */
	public static void vsComputerGame(Scanner sc, String fen, boolean playerIsWhite) {
		Game g = new Game(sc, fen);
		runGameVsComputer(g, playerIsWhite);
	}
	
	/**
	 * Runs a game against my very own JONFISH engine. Patent pending.
	 * 
	 * @param g The game.
	 * @param playerIsWhite The side that the player is.
	 */
	private static void runGameVsComputer(Game g, boolean playerIsWhite) {
		if(System.console() != null)
			Piece.fancy = true;
		Position.flipped = !playerIsWhite;
		if((playerIsWhite && g.getPosition().getSideToMove() == Piece.WHITE)
				|| (!playerIsWhite && g.getPosition().getSideToMove() == Piece.BLACK))
			doPlayerTurn(g);
		boolean inProgress = g.isInProgress();
		while(inProgress) {
			doComputerTurn(g);
			inProgress = g.isInProgress();
			if(inProgress)
				doPlayerTurn(g);
		}
		resolveGame(g);
	}
	
	/**
	 * Runs the turn of a player.
	 * 
	 * @param g The game.
	 */
	private static void doPlayerTurn(Game g) {
		System.out.println(g.toString());
		while(true) {
			try {
				Move move = getlnMove(g);
				g.makeMove(move);
				break;
			}
			catch(GameEndingException e) {
				break;
			}
			catch(InputInterruptedException e) {
				
			}
			catch(IllegalMoveException | NullPointerException e) {
				System.out.print("Illegal move.");
				if(g.getPosition().isCheck())
					System.out.println(" You must move out of check.");
				else
					System.out.println();
			}
			// More than one possible move
			catch(AlgebraicInputException e) {
				System.out.println("More than one piece can make that move.");
			}
			catch(PieceColorException e) {
				System.out.println("It's not your turn.");
			}
			catch(PieceTypeException e) {
				System.out.println("That piece can't make that move.");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * Runs the turn of a computer.
	 * 
	 * @param g The game.
	 */
	private static void doComputerTurn(Game g) {
		System.out.println("Computer is thinking...");
		while(true) {
			try {
				computer = g.getPosition().getSideToMove();
				System.out.println("***");
				Move m = Jonfish.startEval(g);
				g.makeMove(m);
				computer = 0;
				System.out.println("Computer's move: " + m);
				break;
			}
			catch(Exception e) {
				System.out.println("Well, there was an exception here. That's probably bad.");
				System.out.println("There's no recovering from this one. You should probably just go back");
				System.out.println("and play against a friend or something. Or play against yourself.");
				System.out.println("Yeah, that sounds fun.");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Runs a normal game of chess in debug mode from the starting position.
	 * 
	 * @param sc A scanner to be passed.
	 * @param fen A valid FEN string representing the starting position.
	 */
	public static void debugGame(Scanner sc) {
		Game g = new Game(sc);
		runDebugGame(g);
	}
	
	/**
	 * Runs a normal game of chess in debug mode from a given position.
	 * 
	 * @param sc A scanner to be passed.
	 * @param fen A valid FEN string representing the starting position.
	 */
	public static void debugGame(Scanner sc, String fen) {
		Game g = new Game(sc, fen);
		runDebugGame(g);
	}
	
	/**
	 * Runs a normal game, but with debugging constants shown.
	 * Will not catch IllegalMoveException or PieceTypeException.
	 * 
	 * @param g The current game.
	 */
	private static void runDebugGame(Game g) {
		if(System.console() != null)
			Piece.fancy = true;
		do {
			System.out.println(g.toString());
			System.out.println("DEBUGGING:");
			Position pos = g.getPosition();
			System.out.println("Game status: " + g.getStatus());
			System.out.println("Possible moves:");
			System.out.println(g.getAllMoves());
			if(pos.isEnPassantTurn()) {
				System.out.println("(En passant stuff:");
				System.out.println("\ttarget square: " + pos.getEnPassantSquare());
				System.out.println("\ttakeable piece: " + pos.getEnPassantPiece().getCurrentSquare() + ")");
			}
			System.out.println("(Check status: ");
			if(pos.getSideToMove() == Piece.WHITE)
				System.out.println("\twhite in check: " + pos.isCheck() + ")");
			else if(pos.getSideToMove() == Piece.BLACK)
				System.out.println("\tblack in check: " + pos.isCheck() + ")");
			System.out.println("(Castling availability: ");
			if(pos.getSideToMove() == Piece.WHITE) {
				System.out.println("\twhite kingside: " + pos.whiteCanCastleKingside());
				System.out.println("\twhite queenside: " + pos.whiteCanCastleQueenside() + ")");
			}
			else if(pos.getSideToMove() == Piece.BLACK) {
				System.out.println("\tblack kingside: " + pos.blackCanCastleKingside());
				System.out.println("\tblack queenside: " + pos.whiteCanCastleQueenside() + ")");
			}
			System.out.println("Evaluation: " + pos.getDelta());
			doPlayerTurn(g);
		} while(g.isInProgress());
		resolveGame(g);
	}
	
	/**
	 * Runs a game in sandbox mode for debugging purposes.
	 * All moves are legal, and a feature for dropping pieces on the board
	 * will eventually be implemented.
	 * 
	 * @param sc A scanner object to be passed.
	 * @throws GameEndingException If a signal for the game to be suddenly ended is thrown.
	 */
	public static void sandboxGame(Scanner sc) throws GameEndingException {
		Game test = new Game(sc, SANDBOX);
		do {
			System.out.println(test.toString());
			Move move = getlnMove(test);
			test.movePiece(move);
			System.out.println();
		} while(true);
	}
	
	/**
	 * Prints out game-ending messages and the like.
	 * 
	 * @param g The game.
	 */
	private static void resolveGame(Game g) {
		System.out.println("Game over");
		System.out.println(g.toString());
		if(g.getStatus() == WHITE_WIN) {
			if(Piece.fancy)
				System.out.print("\uD83C\uDF89");
			System.out.print("White wins!");
			if(Piece.fancy)
				System.out.println("\uD83C\uDF89");
			else
				System.out.println();
		}
		else if(g.getStatus() == BLACK_WIN) {
			if(Piece.fancy)
				System.out.print("\uD83C\uDF89");
			System.out.print("Black wins!");
			if(Piece.fancy)
				System.out.println("\uD83C\uDF89");
			else
				System.out.println();
		}
		else if(g.getStatus() == DRAW)
			System.out.println("It's a draw!");
		else
			System.out.println("There was no result.");
	}
	
	/**
	 * Creates a new game with a new scanner and in normal mode.
	 */
	public Game() {
		this(new Scanner(System.in));
	}
	
	/**
	 * Creates a new game in the mode specified.
	 * 
	 * @param mode An integer representing the gamemode.
	 */
	public Game(int mode) {
		this();
		this.mode = mode;
	}
	
	/**
	 * Creates a new normal game from a specified position.
	 * 
	 * @param fen A valid FEN string.
	 */
	public Game(String fen) {
		this(new Scanner(System.in), fen);
	}
	
	/**
	 * Creates a new game from a given FEN string.
	 * 
	 * @param sc A scanner to be passed.
	 * @param fen A valid FEN string.
	 */
	public Game(Scanner sc, String fen) {
		this(sc, NORMAL, fen);
	}
	
	/**
	 * Creates a new game from a given FEN string.
	 * 
	 * @param sc A scanner to be passed.
	 * @param mode An integer representing gamemode.
	 * @param fen A valid FEN string.
	 */
	public Game(Scanner sc, int mode, String fen) {
		this(sc, mode);
		this.pos = new Position(fen);
		if(pos.sideToMoveIs(Piece.BLACK)) {
			currentHalfMove = 1;
			moveList.add(new NullMove());
		}
	}
	
	/**
	 * Creates a new game in normal mode.
	 * 
	 * @param sc A scanner to be passed.
	 */
	public Game(Scanner sc) {
		this(sc, NORMAL);
	}
	
	/**
	 * Creates a new game.
	 * 
	 * @param sc A scanner to be passed.
	 * @param mode An integer representing the mode.
	 */
	public Game(Scanner sc, int mode) {
		this.mode = mode;
		Game.sc = sc;
		mode = NORMAL;
		pos = new Position();
		currentHalfMove = 0;
		gameStatus = 0;
		moveList = new ArrayList<Move>();
		if(pos.sideToMoveIs(Piece.BLACK)) {
			currentHalfMove = 1;
			moveList.add(new NullMove());
		}
	}
	
	/**
	 * Returns a string representation of the current game.
	 * By default, it will print out the side to move and the last 8 moves.
	 * 
	 * @return A string of the board.
	 */
	public String toString() {
		switch(mode) {
			default:
				return toStringNormal();
			case SANDBOX:
				return toStringSandbox();
			case BLINDFOLD:
				return toStringBlindfold();
		}
	}
	
	/**
	 * Returns a string of the board, with side to move and the last 8 moves.
	 * 
	 * @return A string of the board.
	 */
	public String toStringNormal() {
		String board = "";
//		if(pos.isWhiteToMove())
//			board += WHITE_MOVE + "\n";
//		else if(pos.isBlackToMove())
//			board += BLACK_MOVE + "\n";
//		else;
		if(moveList.size() != 0)
			board += "Position after " + moveList.get(moveList.size() - 1) + "\n";
		String[] posArray = pos.toStringArray();
		String[] moveArray = getLastEightMovesStringArray();
		assert(posArray.length == moveArray.length);
		for(int i = 0; i < posArray.length; i++) {
			board += posArray[i] + "\t";
			board += moveArray[i];
			if(i + 1 != posArray.length)
				board += "\n";
		}
		return board;
	}
	
	/**
	 * Prints out a string representation of the position.
	 * 
	 * @return The board with labels, squares, and nothing else.
	 */
	private String toStringSandbox() {
		String board = "";
		String[] posArray = pos.toStringArray();
		for(int i = 0; i < posArray.length; i++) {
			board += posArray[i];
			if(i + 1 != posArray.length)
				board += "\n";
		}
		return board;
	}
	
	/**
	 * Returns the side to move for blindfold mode.
	 * 
	 * @return The most recent move, and the current side to move.
	 */
	private String toStringBlindfold() {
		String lastMoves = "";
		try {
			lastMoves += moveList.get(moveList.size() - 1) + "\n";
		}
		catch(IndexOutOfBoundsException e) {
			
		}
		if(pos.isWhiteToMove())
			lastMoves += WHITE_MOVE;
		else if(pos.isBlackToMove())
			lastMoves += BLACK_MOVE;
		else;
		return lastMoves;
	}
	
	/**
	 * Returns the numbered move list.
	 * 
	 * @return The list of moves, numbered.
	 */
	private String getMoveList() {
		String moves = "";
		int fullMoveCt = 1;
		for(int i = 0; i < moveList.size(); i++) {
			if(i % 2 == 0)
				moves += Integer.toString(fullMoveCt) + ".";
			if(moveList.get(i) instanceof NullMove)
				moves += "..";
			else
				moves += " " + moveList.get(i).toString();
			if(i % 2 == 1) {
				moves += " ";
				fullMoveCt++;
			}
		}
		return moves;
	}
	
	/**
	 * Gets the last eight moves as a string array.
	 * 
	 * @return A string array of the last 8 moves, with one move on each line
	 * and the side to move on the first line.
	 */
	private String[] getLastEightMovesStringArray() {
		String[] moves = new String[9];
		// Divide by 2 and add 1 to get actual move number
		int numberSaved = 0;
		int thisHalfMove; // TODO fix this counter
		if(currentHalfMove > 16) {
			int moveIdx;
			for(int j = 1; j < 9; j++) {
				// halfMove 17 --> 3 and 4 on j = 1, counter shows 2
				// --> 5 and 6 on j = 2, counter shows 3
				thisHalfMove = currentHalfMove - 16 + (currentHalfMove + 1) % 2 + 2 * j;
				moveIdx = thisHalfMove / 2 + 1;
				moves[j] = moveIdx + ".";
				try {
					Move trying = moveList.get(thisHalfMove);
					if(trying instanceof NullMove);
					else
						moves[j] += " ";
					moves[j] += trying.toString();
					numberSaved++;
				}
				catch(IndexOutOfBoundsException e) {
					for(int k = j; k < moves.length; k++) {
						moves[k] = "";
					}
					break;
				}
				thisHalfMove++;
				try {
					moves[j] += " " + moveList.get(thisHalfMove).toString();
				}
				catch(IndexOutOfBoundsException e) {
					for(int k = j; k < moves.length; k++) {
						moves[k] = "";
					}
					break;
				}
			}
		}
		else {
			for(int j = 1; j < 9; j++) {
				thisHalfMove = (j - 1) * 2;
				moves[j] = j + ".";
				try {
					Move trying = moveList.get(thisHalfMove);
					if(trying instanceof NullMove);
					else
						moves[j] += " ";
					moves[j] += trying.toString();
					numberSaved++;
				}
				catch(IndexOutOfBoundsException e) {
					for(int k = j; k < moves.length; k++) {
						moves[k] = "";
					}
					break;
				}
				thisHalfMove++;
				try {
					moves[j] += " " + moveList.get(thisHalfMove).toString();
				}
				catch(IndexOutOfBoundsException e) {
					for(int k = j + 1; k < moves.length; k++) {
						moves[k] = "";
					}
					break;
				}
			}
		}
		if(numberSaved == 1)
			moves[0] = "Last move: ";
		else if(numberSaved > 0)
			moves[0] = "Last " + numberSaved + " moves:";
		else
			moves[0] = "";
		return moves;
	}
	
	/**
	 * Attempts to make a move, or processes resignation.
	 * 
	 * @param m The move to be attempted.
	 */
	public void makeMove(Move m) {
		if(gameStatus != 0) {
			moveList.add(m);
			return;
		}
		switch(pos.getSideToMove()) {
			default:
				throw new PieceColorException();
			case Piece.WHITE:
				m.setPieceType(Character.toUpperCase(m.getPieceType()));
				break;
			case Piece.BLACK:
				m.setPieceType(Character.toLowerCase(m.getPieceType()));
		}
		if(m.getCastling() != 0 || prepareMoveKnowingBothSquares(m));
		else
			findStartingSquareFromPieceType(m);
		movePiece(m);
	}
	
	/**
	 * Prepares the game to make a move, given that it knows both the start and end squares.
	 * Sets the piece type of the move object to the proper value.
	 * If the starting square is not known, then returns false.
	 * Note that if the piece is a pawn, move.hasStartSquare() will still return false after this.
	 * 
	 * Precondition: the color of the piece in move has been set correctly.
	 * 
	 * @param move The move to be attempted.
	 * @return True if the move can now be attempted, false otherwise.
	 */
	private boolean prepareMoveKnowingBothSquares(Move move) {
		if(!move.hasStartSquare())
			return false;
		else {
			if(pos.isStillCheckAfterMove(move))
				throw new IllegalMoveException("King is still in check");
			Square start = move.getStartSquare();
			Piece moving = pos.getPieceAt(start);
			// debug block
//			System.out.println(start);
//			System.out.println(moving.getIdentifier());
//			System.out.println(move.getPieceType());
			// debug block
//			if(move.getPieceType() != 0 && (move.getPieceType() != 'P' || move.getPieceType() != 'p')
//					&& moving.getIdentifier() != move.getPieceType())
//				throw new PieceTypeException(moving.getIdentifier(), move.getPieceType());
			moving.setNextMove(move);
			move.setPieceType(moving);
			if(moving.checkLegalMove(pos))
				return true;
			else
				throw new IllegalMoveException(move);
		}
	}
	
	/**
	 * Finds a piece that can make the specified move.
	 * 
	 * @param m The attempted move.
	 * @return The piece that can make the move.
	 */
	private void findStartingSquareFromPieceType(Move m) {
		Piece moving;
		try {
			switch(pos.getSideToMove()) {
				default:
					throw new PieceColorException();
				case Piece.WHITE:
					moving = findStartingSquareFromPieceType(m, 'P', 'B', 'N',
							'R', 'Q', 'K');
					break;
				case Piece.BLACK:
					moving = findStartingSquareFromPieceType(m, 'p', 'b', 'n',
							'r', 'q', 'k');
					break;
			}
		}
		catch(ChessException e) {
			throw e;
		}
		m.setStartSquare(moving.getCurrentSquare());
		if(prepareMoveKnowingBothSquares(m));
		// Should not be reachable
		else
			throw new IllegalMoveException(m);
	}
	
	/**
	 * Finds a piece that can make a given move.
	 * 
	 * @param m The move to be attempted.
	 * @param pId The character representing a pawn, 'P' for white and 'p' for black.
	 * @param bId The character representing a bishop, 'B' for white and 'b' for black.
	 * @param nId The character representing a knight, 'N' for white and 'n' for black.
	 * @param rId The character representing a rook, 'R' for white and 'r' for black.
	 * @param qId The character representing a queen, 'Q' for white and 'q' for black.
	 * @param kId The character representing a king, 'K' for white and 'k' for black.
	 * @return The piece that can make that move.
	 */
	private Piece findStartingSquareFromPieceType(Move m, char pId, char bId, char nId,
			char rId, char qId, char kId) {
		List<Piece> possible = new LinkedList<Piece>();
		char pT = m.getPieceType();
		Square sq = m.getTargetSquare();
		// Finds possible moves
		if(pT == pId)
			possible = Pawn.findCanMoveTo(sq, pos);
		else if(pT == bId)
			possible = Bishop.findAttacking(sq, pos);
		else if(pT == nId)
			possible = Knight.findAttacking(sq, pos);
		else if(pT == rId)
			possible = Rook.findAttacking(sq, pos);
		else if(pT == qId)
			possible = Queen.findAttacking(sq, pos);
		else if(pT == kId)
			possible = King.findCanMoveTo(sq, pos);
		else
			throw new PieceColorException(pos.getSideToMove());
		switch(possible.size()) {
			case 0:
				throw new IllegalMoveException(m);
			case 1:
				return possible.get(0);
		}
		m.setNeedsSpecifier(true);
		if(m.specifiesRank()) {
			int rank = Character.getNumericValue(m.getSpecifier());
			int matchCount = 0;
			Piece matched = null;
			for(Piece p : possible) {
				if(p.getCurrentRank() == rank) {
					matched = p;
					matchCount++;
				}
			}
			if(matchCount == 0)
				throw new IllegalMoveException(m);
			else if(matchCount == 1)
				return matched;
			else;
		}
		else if(m.specifiesFile()) {
			char file = m.getSpecifier();
			int matchCount = 0;
			Piece matched = null;
			for(Piece p : possible) {
				if(p.getCurrentFile() == file) {
					matched = p;
					matchCount++;
				}
			}
			if(matchCount == 0)
				throw new IllegalMoveException(m);
			else if(matchCount == 1)
				return matched;
			else;
		}
		throw new AlgebraicInputException("More than one move is possible.");
	}
		
	/**
	 * Executes a move, with some regard to legality checks.
	 * Castling and promotion are handled here.
	 * 
	 * @param move The move that is being made.
	 */
	private void movePiece(Move move) {
		// Saves the current position
		savePosition();
		// Handles castling
		if(move.getCastling() == Move.KINGSIDE_CASTLE) {
			pos.castleKingside();
			incMove();
		}
		else if(move.getCastling() == Move.QUEENSIDE_CASTLE) {
			pos.castleQueenside();
			incMove();
		}
		// Anything else
		else {
			// Prepares the piece for the next move
			Piece moving = pos.getPieceAt(move.getStartSquare());
			moving.setNextMove(move);
			// Handles promotion
			moving = handlePromotion(move, moving);
			// Verifies if the move is a capture
			if(pos.isOccupiedAt(move.getTargetSquare()))
				move.setCapture(true);
			// Resolves en passant captures
			else if((moving.isPawn() && pos.isEnPassantTurn()
					&& move.getTargetSquare().equals(pos.getEnPassantSquare()))) {
				move.setCapture(true);
				move.setSpecifier(move.getStartX());
				move.setNeedsSpecifier(true);
				pos.removePieceAt(pos.getEnPassantPiece().getCurrentSquare());
			}
			else if(moving.isPawn()
					&& Math.abs(move.getTargetY() - move.getStartY()) == 2)
				pos.setEnPassant(moving, 
						new Square(moving.getCurrentFile(),
								(moving.isWhite())?(3):(6)));
			else
				move.setCapture(false);
			// Makes the next move on the position
//			System.out.println(moving);
//			System.out.println(pos.getPieceAt(moving.getCurrentSquare()));
			moving.makeNextMove();
			movePiece(move.getStartSquare(), move.getTargetSquare());
		}
		// Updates the status of the game
		checkStatus();
		// Checks for check/checkmate
		if(getStatus() == WHITE_WIN || getStatus() == BLACK_WIN)
			move.setCheckmate(true);
		else if(pos.isCheck())
			move.setCheck(true);
		// Adds the move to the move list
		moveList.add(move);
	}
	
	/**
	 * Executes a move, with some regard to legality checks.
	 * 
	 * @param start The starting square.
	 * @param end The ending square.
	 */
	private void movePiece(Square start, Square end) {
		pos.movePiece(start, end);
		if(mode != SANDBOX)
			incMove();
	}
	
	/**
	 * Moves the program on to the next move. It increments the move counter,
	 * switches the side to move, and then checks if any en passant-related
	 * information needs to be reset.
	 */
	private void incMove() {
		currentHalfMove++;
		pos.switchSideToMove();
		// If the piece that can be taken e.p. is the same color as the side
		// to move, then it should be reset
		if(pos.isEnPassantTurn() && pos.getEnPassantPiece().getColor() == pos.getSideToMove())
			pos.endEnPassantTurn();
	}
	
	/**
	 * Determines if the game should be ended, and changes the status accordingly.
	 */
	private void checkStatus() {
		List<Move> allMoves = getAllMoves();
		if(pos.isInsufficientMaterial()) {
			gameStatus = DRAW;
			pos.setInProgress(false);
		}
		if(allMoves.size() == 0) {
			if(pos.isWhiteInCheck())
				gameStatus = BLACK_WIN;
			else if(pos.isBlackInCheck())
				gameStatus = WHITE_WIN;
			else
				gameStatus = DRAW;
			pos.setInProgress(false);
		}
	}
	
	/**
	 * Handles pawn promotions.
	 * 
	 * Precondition: The target square of the moving piece has been set.
	 * 
	 * @param m
	 * @param p
	 */
	private Piece handlePromotion(Move m, Piece p) {
		int endRank;
		if(p.isWhite())
			endRank = 8;
		else
			endRank = 1;
		// Makes sure the piece is promotable
		if(!p.isPawn() || m.getTargetY() != endRank) {
			m.setPromotingTo('\u0000');
			return p;
		}
		char promoteID = m.getPromotingTo();
		if(m.getPromotingTo() != 0);
		else if(computer == 0)
			promoteID = getchPromotion();
		else
			promoteID = 'Q';
//		System.out.println(promoteID);
		switch(promoteID) {
			case 'N': case 'n':
				p = new Knight(p.getCurrentSquare().toString(), p.getColor());
				break;
			case 'B': case 'b':
				p = new Bishop(p.getCurrentSquare().toString(), p.getColor());
				break;
			case 'R': case 'r':
				p = new Rook(p.getCurrentSquare().toString(), p.getColor());
				break;
			case 'Q': case 'q':
				p = new Queen(p.getCurrentSquare().toString(), p.getColor());
				break;
			default:
				throw new PieceTypeException();
		}
		pos.setPieceAt(p.getCurrentFile(), p.getCurrentRank(), p);
		m.setPromotingTo(Character.toUpperCase(promoteID));
		p.setNextMove(m);
//		System.out.println(p.toDebugString());
		return p;
	}
	
	/**
	 * Saves the current position in a list.
	 */
	private void savePosition() {
		positions.add(new Position(pos));
	}
	
	/**
	 * Returns a list of all possible moves in this position.
	 * 
	 * @return A list of all possible moves.
	 */
	public List<Move> getAllMoves() {
		return pos.getAllMoves();
	}
	
	/**
	 * Prompts the user to enter a move.
	 * 
	 * @return The move object generated by the user's input.
	 * @throws GameEndingException When the game is supposed to be exited.
	 */
	private static Move getlnMove(Game g) throws GameEndingException {
		while(true) {
			if(g.getPosition().sideToMoveIs(Piece.WHITE))
				System.out.println(WHITE_MOVE + ": ");
			else if(g.getPosition().sideToMoveIs(Piece.BLACK))
				System.out.println(BLACK_MOVE + ": ");
			System.out.print(">>> ");
			String input = sc.nextLine();
			if(parseHelp(input.trim(), g)) {
				throw new InputInterruptedException();
			}
			parseResultAttempt(g, input.trim());
			if(input.trim().equalsIgnoreCase("cancel"))
				throw new InputInterruptedException();
			else if(g.getStatus() != 0)
				return new Move(g.getStatus());
			try {
				return new Move(input);
			}
			catch(AlgebraicInputException e) {
				System.out.println("Invalid input. Type \"/help input\" for a list of valid inputs.");
				System.out.println();
			}
		}
	}
	
	private static boolean parseHelp(String s, Game g) throws GameEndingException {
		if(s.length() > 0 && s.charAt(0) == '/') {
			switch(GameMenu.help(s.substring(1))) {
				case 1: // exit
					System.out.println("Are you sure you want to quit?");
					System.out.print(">>> ");
					String input = sc.nextLine();
					if(input.equals("y") || input.equals("true") || input.equals("yes")) {
						System.out.println("Returning to main menu.");
						g.setStatus(4);
						throw new GameEndingException();
					}
					break;
				case 2: // print out move list
					System.out.println(g.getMoveList());
					break;
				case 3: // flip board
					Position.flipped = !Position.flipped;
					System.out.println(g.toString());
					break;
				case 4: // exports FEN
					System.out.println(g.getPosition().getFEN());
					break;
				case 5: // redraws board
					System.out.println(g.toString());
					break;
			}
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Interprets inputs that could lead to a result, i.e. resignation or draw.
	 * Currently, draw offers are illegal. We fight to the death.
	 * 
	 * @param g The game. You lost.
	 * @param s The input string.
	 */
	private static void parseResultAttempt(Game g, String s) {
		Position p = g.getPosition();
		if(s.matches("(?i)resigns?"))
			g.setStatus(p.getOtherSideToMove());
		else if(s.matches("(?i)draw") || s.matches("0.5-0.5") || s.matches("1/2-1/2"))
			throw new IllegalMoveException();
		else if(s.matches("1-0")) {
			if(p.sideToMoveIs(Piece.BLACK))
				g.setStatus(WHITE_WIN);
			else
				throw new IllegalMoveException();
		}
		else if(s.matches("0-1")) {
			if(p.sideToMoveIs(Piece.WHITE))
				g.setStatus(BLACK_WIN);
			else
				throw new IllegalMoveException();
		}
		else
			g.setStatus(IN_PROGRESS);
	}
	
	/**
	 * Prompts the user for a character to be used in promoting a pawn.
	 * 
	 * @return The first character of an input string, representing the piece being
	 * promoted to. Don't worry, "knight" does return 'n'.
	 */
	private static char getchPromotion() {
		// Regex stuff
		String pieces = "(?i)^([rbnq]|rook|queen|bishop|(?<=k)night)$";
		System.out.println("Enter a piece to promote to: ");
		System.out.print(">>> ");
		String input = sc.nextLine();
		Matcher m;
		int promptCount = 0;
		while(true) {
			m = Pattern.compile(pieces).matcher(input);
			if(m.find())
				break;
			else {
				promptCount++;
				if(promptCount > 6 || input.trim().equalsIgnoreCase("cancel"))
					throw new InputInterruptedException();
				System.out.println("Invalid piece type. Try again.");
				System.out.print(">>> ");
				input = sc.nextLine();
			}
		}
		return (input.charAt(0) == 'k' || input.charAt(0) == 'K')?('N'):(input.charAt(0));
	}
	
	/**
	 * Checks if the game is in progress.
	 * 
	 * @return True if and only if the status of the game is 0.
	 */
	public boolean isInProgress() {
		return gameStatus == IN_PROGRESS;
	}
	
	public int getStatus() {
		return gameStatus;
	}
	
	public void setStatus(int status) {
		this.gameStatus = status;
		if(status != 0)
			pos.setInProgress(false);
	}
	
	/**
	 * Returns the current position.
	 * 
	 * @return The current position.
	 */
	public Position getPosition() {
		return pos;
	}
	
	/**
	 * A special type of move that acts as a placeholder.
	 * 
	 * @author jhshi
	 *
	 */
	private static class NullMove extends Move {
		
		/**
		 * Creates a "null move" that has no properties.
		 */
		public NullMove() {
			
		}
		
	}
	
	/**
	 * A special type of exception to indicate that the user did something to interrupt
	 * the move prompt. Should be thrown only by the getlnMove() method.
	 * 
	 * @author jhshi
	 *
	 */
	@SuppressWarnings("serial")
	private static class InputInterruptedException extends AlgebraicInputException {
		
		public InputInterruptedException() {
			super();
		}
		
	}
	
	@SuppressWarnings("serial")
	private static class GameEndingException extends Exception {
		
		public GameEndingException() {
			super();
		}
		
	}
	
}
