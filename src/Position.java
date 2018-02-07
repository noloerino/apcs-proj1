import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Position {
	
	public static boolean flipped = false;
	
	/**
	 * Holds the array of pieces.
	 * Note that the indexing of the arrays is different from the indexing 
	 * of the squares, which is why there is no getter method for this array.
	 */
	private Piece[][] pieces = new Piece[9][9]; // array of pieces
	private boolean inProgress = true;
	private boolean whiteToMove;
	private King whiteKing;
	private Rook whiteKingsideRook; // TODO set these from fen parsing
	private Rook whiteQueensideRook;
	private King blackKing;
	private Rook blackKingsideRook; // TODO set these from fen parsing
	private Rook blackQueensideRook;
	private Square enPassantSquare;
	private Piece enPassantPiece;
	private int halfMoveClock = 0;
	private int fullMoveNumber = 1;
	private String[] fen = new String[6];
	private static Map<Character, Integer> fileToXMap = Square.fileToXMap;
	
//	public static void main(String[] args) {
//		testPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
//	}
	
	/**
	 * Tests stuff in a position.
	 */
	public static void testPosition(String s) {
		Position pos = new Position(s);
		String[] testpos1 = pos.toStringArrayWhiteOnBottom();
		String[] testpos2 = pos.toStringArrayBlackOnBottom();
		System.out.println("Original position: ");
		for(int i = 0; i < testpos1.length; i++) {
			System.out.println(testpos1[i] + "\t" + testpos2[i] + "\t");
		}
		System.out.println(pos.getAllMoves());
		/*
		System.out.println("BK is at: " + pos.blackKing.getCurrentSquare());
		System.out.println("Side to move: " + pos.getSideToMove());
		System.out.println("Is side to move in check: " + pos.isCheck());
		System.out.println("Is side to move in check after move: ");
		System.out.println("King to d7: " + 
				pos.isStillCheckAfterMove(new Square('e', 8), new Square('d', 7)));
		System.out.println("Knight to d7: " +
				pos.isStillCheckAfterMove(new Square('b', 8), new Square('d', 7)));
		System.out.println("King teleports to f6: " +
				pos.isStillCheckAfterMove(new Square('e', 8), new Square('f', 6)));
		System.out.println("Queen teleports to kill b5: " + 
				pos.isStillCheckAfterMove(new Square('d', 7), new Square('b', 5)));
		*/
	}
	
	/**
	 * Creates a position from the copy of another position.
	 * Should be invoked when storing a position in an array, or testing a move.
	 * 
	 * @param o Another position object.
	 */
	public Position(Position o) {
		Piece[][] otherPieces = o.pieces;
		for(int i = 0; i < otherPieces.length; i++) {
			for(int j = 0; j < otherPieces[0].length; j++) {
				pieces[i][j] = otherPieces[i][j].makeCopy();
				if(otherPieces[i][j] == o.whiteKingsideRook)
					whiteKingsideRook = (Rook) pieces[i][j];
				else if(otherPieces[i][j] == o.whiteQueensideRook)
					whiteQueensideRook = (Rook) pieces[i][j];
				else if(otherPieces[i][j] == o.whiteKing)
					whiteKing = (King) pieces[i][j];
				else if(otherPieces[i][j] == o.blackKingsideRook)
					blackKingsideRook = (Rook) pieces[i][j];
				else if(otherPieces[i][j] == o.blackQueensideRook)
					blackQueensideRook = (Rook) pieces[i][j];
				else if(otherPieces[i][j] == o.blackKing)
					blackKing = (King) pieces[i][j];
			}
		}
		inProgress = o.isInProgress();
		whiteToMove = o.isWhiteToMove();
		halfMoveClock = o.halfMoveClock;
		fullMoveNumber = o.fullMoveNumber;
		for(int i = 0; i < fen.length; i++) {
			fen[i] = o.fen[i];
		}
		if(o.isEnPassantTurn()) {
			this.setEnPassant(o.getEnPassantPiece().makeCopy(), 
					o.getPieceAt(o.getEnPassantSquare()).makeCopy().getCurrentSquare());
		}
	}
	
	/**
	 * Creates a new 64-square board set to the starting position.
	 */
	public Position() {
		this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		/*
		 * Example:
		 *     a b c d e f g h	idx:8
		 *   8|r|n|b|q|k|b|n|r|		7
		 *   7|p|p|p|p|p|p|p|p|		6
		 *   6|_|_|_|_|_|_|_|_|		5
		 *   5|_|_|_|_|_|_|_|_|		4
		 *   4|_|_|_|_|_|_|_|_|		3
		 *   3|_|_|_|_|_|_|_|_|		2
		 *   2|P|P|P|P|P|P|P|P|		1
		 *   1|R|N|B|Q|K|B|N|R|		0
		 */
		/*
		whiteToMove = true;
		// Initializes file labels and pawns of both colors
		pieces[8][0] = new Label(0, 0, ' ');
		char currentLabel = 'a';
		for(int file = 1; file < pieces.length; file++) {
			pieces[8][file] = new Label(file, 0, currentLabel);
			pieces[6][file] = new Pawn(currentLabel, 7, Piece.BLACK);
			pieces[1][file] = new Pawn(currentLabel, 2, Piece.WHITE);
			currentLabel++;
		}
		// Initializes rank labels
		currentLabel = '1';
		for(int rank = 0; rank < 8; rank++) {
			pieces[rank][0] = new Label(0, rank + 1, currentLabel);
			currentLabel++;
		}
		// Initializes black pieces
		pieces[7][1] = new Rook("a8", Piece.BLACK);
		blackQueensideRook = (Rook) pieces[7][1];
		pieces[7][2] = new Knight("b8", Piece.BLACK);
		pieces[7][3] = new Bishop("c8", Piece.BLACK);
		pieces[7][4] = new Queen("d8", Piece.BLACK);
		pieces[7][5] = new King("e8", Piece.BLACK);
		blackKing = (King) pieces[7][5];
		pieces[7][6] = new Bishop("f8", Piece.BLACK);
		pieces[7][7] = new Knight("g8", Piece.BLACK);
		pieces[7][8] = new Rook("h8", Piece.BLACK);
		blackKingsideRook = (Rook) pieces[7][8];
		// Initializes the vast expanse of blank squares
		for(int rank = 2; rank <= 5; rank++) {
			for(int file = 1; file < pieces.length; file++) {
				pieces[rank][file] = new Blank(file, rank + 1);
			}
		}
		// Initializes white pieces
		pieces[0][1] = new Rook("a1", Piece.WHITE);
		whiteQueensideRook = (Rook) pieces[0][1];
		pieces[0][2] = new Knight("b1", Piece.WHITE);
		pieces[0][3] = new Bishop("c1", Piece.WHITE);
		pieces[0][4] = new Queen("d1", Piece.WHITE);
		pieces[0][5] = new King("e1", Piece.WHITE);
		whiteKing = (King) pieces[0][5];
		pieces[0][6] = new Bishop("f1", Piece.WHITE);
		pieces[0][7] = new Knight("g1", Piece.WHITE);
		pieces[0][8] = new Rook("h1", Piece.WHITE);
		whiteKingsideRook = (Rook) pieces[0][8];
		*/
	}
	
	/**
	 * Creates a new position based on a given FEN string.
	 * According to Wikipedia, an FEN string has 6 components:
	 * 1. A description of each rank, starting from rank 8 through 1
	 * going from file a to h, with empty squares denoted by numbers.
	 * 2. The current side to move.
	 * 3. Castling availability; - if nobody can castle, K if white can
	 * castle kingside, k if black can castle kingside, etc.
	 * 4. A target square for en passant. - if there is no such square.
	 * 5. The number of half moves since the last pawn move or capture.
	 * 6. The number of the move.
	 * Having fewer than the required number of strings does not necessarily
	 * a fatal error, but may lead to strange behavior.
	 * 
	 * @param fen A legal FEN string representing the starting position.
	 */
	public Position(String fen) {
		try {
		// Initializes file labels
		pieces[8][0] = new Label(0, 0, ' ');
		char currentLabel = 'a';
		for(int file = 1; file < pieces.length; file++) {
			pieces[8][file] = new Label(file, 0, currentLabel);
			currentLabel++;
		}
		// Initializes rank labels
		currentLabel = '1';
		for(int rank = 0; rank < 8; rank++) {
			pieces[rank][0] = new Label(0, rank + 1, currentLabel);
			currentLabel++;
		}
		String[] placeholder = fen.split("\\s");
		for(int i = 0; i < placeholder.length; i++) {
			this.fen[i] = placeholder[i];
		}
		if(this.fen[0] == null)
			throw new AlgebraicInputException("Invalid FEN.");
		String[] posFEN = this.fen[0].split("/");
		for(int rank = 7; rank >= 0; rank--) {
			Piece[] thisRank = findRankFromFEN(rank, posFEN[7 - rank]);
			for(int j = 0; j < pieces[rank].length; j++) {
				if(pieces[rank][j] == null) {
					pieces[rank][j] = thisRank[j-1];
				}
			}
		}
		if(countWhiteKings() > 1)
			throw new PieceTypeException("There is more than one white king!");
		if(countBlackKings() > 1)
			throw new PieceTypeException("There is more than one black king!");
		if(this.fen[1] == null)
			whiteToMove = true;
		else if(this.fen[1].equalsIgnoreCase("B"))
			whiteToMove = false;
		else
			whiteToMove = true;
		if(this.fen[2] == null) {
			if(whiteKingsideRook != null || !whiteKingsideRook.isOn('h', 1))
				whiteKingsideRook.setMovedTrue();
			if(whiteQueensideRook != null || !whiteQueensideRook.isOn('a',1))
				whiteQueensideRook.setMovedTrue();
			if(blackKingsideRook != null || !blackKingsideRook.isOn('h', 8))
				blackKingsideRook.setMovedTrue();
			if(blackQueensideRook != null || !blackQueensideRook.isOn('a', 8))
				blackQueensideRook.setMovedTrue();
		}
		else {
			if(!this.fen[2].contains("K") // Accounts for white kingside castling
					&& whiteKingsideRook != null)
				whiteKingsideRook.setMovedTrue();
			if(!this.fen[2].contains("k") // Accounts for black kingside castling
					&& blackKingsideRook != null)
				blackKingsideRook.setMovedTrue();
			if(!this.fen[2].contains("Q") // Accounts for white queenside castling
					&& whiteQueensideRook != null)
				whiteQueensideRook.setMovedTrue();
			if(!this.fen[2].contains("q") // Accounts for blcak queenside castling
					&& blackQueensideRook != null)
				blackQueensideRook.setMovedTrue();
		}
		if(this.fen[3] == null)
			enPassantSquare = null;
		else if(this.fen[3].matches("[a-h][1-8]")) {
			Square testSquare = new Square(this.fen[3]);
			char epFile = testSquare.getFile();
			if(getSideToMove() == Piece.WHITE && testSquare.getRank() == 6) {
				enPassantSquare = testSquare;
				enPassantPiece = getPieceAt(epFile, 5);
			}
			else if(getSideToMove() == Piece.BLACK && testSquare.getRank() == 3) {
				enPassantSquare = testSquare;
				enPassantPiece = getPieceAt(epFile, 4);
			}
		}
		if(this.fen[4] == null);
		else
			this.halfMoveClock = Integer.parseInt(this.fen[4]);
		if(this.fen[5] == null);
		else
			this.fullMoveNumber = Integer.parseInt(this.fen[5]);
		}
		catch(RuntimeException e) {
			throw new AlgebraicInputException("Invalid FEN.");
		}
	}
	
	/**
	 * Returns an array of pieces on a given rank based off a line of FEN.
	 * The argument string must be characters representing pieces or numbers
	 * representing squares, with all "/" characters removed.
	 * 
	 * @return An array of pieces.
	 */
	private Piece[] findRankFromFEN(int rank, String look) {
		Piece[] onThisRank = new Piece[8];
		rank = rank + 1;
		char file = 'a';
		int fileIdx = 0;
		List<Piece> whiteRooks = new ArrayList<Piece>();
		List<Piece> blackRooks = new ArrayList<Piece>();
		for(int i = 0; i < look.length(); i++) {
			if(Character.isDigit(look.charAt(i))) {
				file += Character.getNumericValue(look.charAt(i));
				fileIdx += Character.getNumericValue(look.charAt(i));
//				for(int j = i; j < Character.getNumericValue(look.charAt(i)); j++) {
//					file++;
//					fileIdx++;
//					// Blanks
//				}
			}
			else {
				int color = Character.isUpperCase(look.charAt(i)) ? Piece.WHITE : Piece.BLACK;
				switch(Character.toUpperCase(look.charAt(i))) {
					default:
						throw new AlgebraicInputException("Invalid FEN.");
					case 'P':
						onThisRank[fileIdx] = new Pawn(file, rank, color);
						break;
					case 'B':
						onThisRank[fileIdx] = new Bishop(file, rank, color);
						break;
					case 'N':
						onThisRank[fileIdx] = new Knight(file, rank, color);
						break;
					case 'R':
						onThisRank[fileIdx] = new Rook(file, rank, color);
						if(color == Piece.WHITE)
							whiteRooks.add(onThisRank[fileIdx]);
						else if(color == Piece.BLACK)
							blackRooks.add(onThisRank[fileIdx]);
						break;
					case 'Q':
						onThisRank[fileIdx] = new Queen(file, rank, color);
						break;
					case 'K':
						onThisRank[fileIdx] = new King(file, rank, color);
						if(color == Piece.WHITE) {
							whiteKing = (King) onThisRank[fileIdx];
						}
						else if(color == Piece.BLACK)
							blackKing = (King) onThisRank[fileIdx];
						break;
				}
				file++;
				fileIdx++;
			}
		}
		// Go back and initialize all nulls to blanks
		for(int i = 0; i < onThisRank.length; i++) {
			if(onThisRank[i] == null)
				onThisRank[i] = new Blank(i + 1, rank);
		}
		// Sets which pieces are needed for castling
		if(whiteKing != null && whiteKing.getCurrentRank() == 1) {
			char wKFile = whiteKing.getCurrentFile();
			for(Piece p : whiteRooks) {
				if(whiteQueensideRook == null && p.getCurrentFile() < wKFile)
					whiteQueensideRook = (Rook) p;
				else if((whiteKingsideRook == null && p.getCurrentFile() > wKFile)
						|| (whiteQueensideRook == null && p.getCurrentFile() < wKFile))
					whiteKingsideRook = (Rook) p;
			}
		}
		if(blackKing != null && blackKing.getCurrentRank() == 8) {
			char bKFile = blackKing.getCurrentFile();
			for(Piece p : blackRooks) {
				if(blackQueensideRook == null && p.getCurrentFile() < bKFile)
					blackQueensideRook = (Rook) p;
				else if((blackKingsideRook == null && p.getCurrentFile() > bKFile)
						|| (blackQueensideRook == null && p.getCurrentFile() < bKFile))
					blackKingsideRook = (Rook) p;
			}
		}
		return onThisRank;
	}
	
	/**
	 * Returns the string representation of the board to be printed.
	 * 
	 * @return A board.
	 */
	public String toString() {
		if(!flipped)
			return toStringWhiteOnBottom();
		else
			return toStringBlackOnBottom();
	}
	
	public String[] toStringArray() {
		if(!flipped)
			return toStringArrayWhiteOnBottom();
		else
			return toStringArrayBlackOnBottom();
	}
	
	/**
	 * Returns the string representation of the board with white on the bottom, as normal.
	 * 
	 * @return A board.
	 */
	private String toStringWhiteOnBottom() {
		String board = "";
		for(int i = 8; i >= 0; i--) {
			for(int j = 0; j < pieces[i].length; j++) {
				board += pieces[i][j];
			}
			if(i != pieces.length - 1)
				board += "\n";
		}
		return board;
	}
	
	/**
	 * Returns the string representation of the board with black on the bottom.
	 * 
	 * @return A board.
	 */
	private String toStringBlackOnBottom() {
		String board = pieces[8][0].toString();
		for(int j = pieces[8].length - 1; j >= 1; j--) {
			board += pieces[8][j];
		}
		for(int i = 1; i < pieces.length; i++) {
			board += "\n" + pieces[i][0].toString();
			for(int j = pieces[0].length - 1; j >= 1; j--) {
				board += pieces[i][j];
			}
		}
		return board;
	}
	
	/**
	 * Returns the occupation status of each square on the board, as specified in the Square class.
	 * Unlike other print methods, this one is unaffected by the flipped field.
	 * 
	 * @return A string representing whether each square is occupied, and what color piece is occupying it.
	 */
	@SuppressWarnings("unused")
	private String toStringOccupiedFlags() {
		String flags = "";
		for(int rank = 1; rank < pieces.length; rank++) {
			for(int file = 1; file < pieces[rank].length; file++) {
				flags += getOccupiedAt(file, rank) + "|";
			}
			if(rank + 1 != pieces.length)
				flags += "\n";
		}
		return flags;
	}
	
	/**
	 * Returns the 9 rows of text representing the board.
	 * 
	 * @return An array of 9 strings.
	 */
	private String[] toStringArrayWhiteOnBottom() {
		String[] rows = new String[9];
		for(int i = 8; i >= 0; i--) {
			String currentRow = "";
			for(int j = 0; j < pieces[i].length; j++) {
				currentRow += pieces[i][j];
			}
			rows[8 - i] = currentRow;
		}
		return rows;
	}
	
	/**
	 * Returns the 9 rows of text representing the board, but flipped.
	 * 
	 * @return An array of 9 strings.
	 */
	private String[] toStringArrayBlackOnBottom() {
		String[] rows = new String[9];
		rows[0] = pieces[8][0].toString();
		for(int j = pieces[8].length - 1; j >= 1; j--) {
			rows[0] += pieces[8][j];
		}
		int k = 1;
		for(int i = 1; i < pieces.length; i++) {
			rows[k] = pieces[i - 1][0].toString();
			for(int j = pieces[0].length - 1; j >= 1; j--) {
				rows[k] += pieces[i - 1][j];
			}
			k++;
		}
		return rows;
	}
	
	/**
	 * Executes a move, without regard for the legality of the move.
	 * The original square should become a blank.
	 * If there was a rook occupying the original square, then the corresponding castling rook
	 * placeholder is set to null.
	 * 
	 * @param startFile A character representing the starting file of the square.
	 * @param startRank An integer representing the starting rank of the square.
	 * @param endFile A character representing the ending file of the square.
	 * @param endRank An integer representing the ending rank of the square.
	 */
	public void movePiece(char startFile, int startRank, char endFile, int endRank) {
		if(whiteToMove)
			fullMoveNumber++;
		movePieceWithoutIncrementing(startFile, startRank, endFile, endRank);
	}
	
	/**
	 * Moves a piece from one square to another, without regard for the legality of the move.
	 * Identical to the other movePiece method.
	 * 
	 * @param startSquare The starting square of the piece.
	 * @param endSquare The ending square of the piece.
	 */
	public void movePiece(Square startSquare, Square endSquare) {
		char startFile = startSquare.getFile();
		int startRank = startSquare.getRank();
		char endFile = endSquare.getFile();
		int endRank = endSquare.getRank();
		movePiece(startFile, startRank, endFile, endRank);
	}
	
	/**
	 * Moves a piece from one square to another without regard for much anything at all.
	 * 
	 * @param startFile The starting file of the piece.
	 * @param startRank The starting rank of the piece.
	 * @param endFile The ending file of the piece.
	 * @param endRank The ending rank of the piece.
	 */
	private void movePieceWithoutIncrementing(char startFile, int startRank, char endFile, int endRank) {
		setPieceAt(endFile, endRank, getPieceAt(startFile, startRank));
		setPieceAt(startFile, startRank, new Blank(startFile, startRank));
		Piece testPiece = this.getPieceAt(endFile, endRank);
		if(testPiece instanceof ChecksIfMoved) {
			((ChecksIfMoved) testPiece).setMovedTrue();
		}
	}
	
	/**
	 * Tests a position by moving a piece without updating the move counter.
	 * This should only be called when looking to see if the king is in check after
	 * an attempted move.
	 * 
	 * @param startSquare
	 * @param endSquare
	 */
	private void movePieceWithoutUpdating(Square startSquare, Square endSquare) {
		char startFile = startSquare.getFile();
		int startRank = startSquare.getRank();
		char endFile = endSquare.getFile();
		int endRank = endSquare.getRank();
		setPieceAt(endFile, endRank, getPieceAt(startFile, startRank));
		setPieceAt(startFile, startRank, new Blank(startFile, startRank));
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
	public void setInProgress(boolean inProgress) {
		this.inProgress = inProgress;
	}
	
	public int toMove() {
		if(isWhiteToMove())
			return Piece.WHITE;
		else if(isBlackToMove())
			return Piece.BLACK;
		else
			return 0;	
	}
	
	public boolean isWhiteToMove() {
		return whiteToMove && inProgress;
	}
	
	public boolean isBlackToMove() {
		return !whiteToMove && inProgress;
	}
	
	public boolean sideToMoveIs(int color) {
		if(color == Piece.WHITE)
			return isWhiteToMove();
		else if(color == Piece.BLACK)
			return isBlackToMove();
		else
			return false;
	}
	
	public int getSideToMove() {
		if(isWhiteToMove())
			return Piece.WHITE;
		else if(isBlackToMove())
			return Piece.BLACK;
		else
			return Piece.BLANK;
	}
	
	public int getOtherSideToMove() {
		if(isWhiteToMove())
			return Piece.BLACK;
		else if(isBlackToMove())
			return Piece.WHITE;
		else
			return Piece.BLANK;
	}
	
	public void switchSideToMove() {
		whiteToMove = !whiteToMove;
	}
	
	public Piece getPieceAt(char file, int rank) {
		return getPieceAt(fileToXMap.get(file), rank);
	}
	
	public Piece getPieceAt(int file, int rank) {
		try {
			if(file == 0 || rank - 1 == 8)
				throw new IndexOutOfBoardException(file, rank);
			return pieces[rank - 1][file];
		}
		catch(IndexOutOfBoundsException e) {
			throw new IndexOutOfBoardException(file, rank);
		}
	}
	
	/**
	 * Instead of finding the piece at a given coordinate pair, finds the square
	 * a given distance away from another square.
	 * I know, it's confusing.
	 * 
	 * @param file The file index.
	 * @param rank The array index.
	 * @return The piece at the specified location.
	 */
	public Piece getLiteralPieceAt(int file, int rank) {
		try {
			if(file == 0 || rank == 0)
				throw new IndexOutOfBoardException(file, rank);
			return pieces[rank - 1][file];
		}
		catch(IndexOutOfBoundsException e) {
			throw new IndexOutOfBoardException(file, rank);
		}
	}
	
	public Piece getPieceAt(Square sq) {
		return getPieceAt(sq.getFile(), sq.getRank());
	}
	
	public void setPieceAt(char file, int rank, Piece piece) {
		setPieceAt(fileToXMap.get(file), rank, piece);
	}
	
	public void setPieceAt(int file, int rank, Piece piece) {
		piece.setCurrentSquare(new Square(file, rank));
		pieces[rank - 1][file] = piece;
	}
	
	/**
	 * Drops a piece onto a given square.
	 * 
	 * @param file The file of the square.
	 * @param rank The rank of the square.
	 * @param piece The piece to be dropped.
	 */
	public void dropPieceAt(char file, int rank, Piece piece) {
		setPieceAt(file, rank, piece);
	}
	
	/**
	 * Removes a piece from a given square by replacing it with a blank.
	 * 
	 * @param sq The square to be set blank.
	 */
	public void removePieceAt(Square sq) {
		removePieceAt(sq.getFile(), sq.getRank());
	}
	
	/**
	 * Removes a piece from a given square by replacing it with a blank.
	 * 
	 * @param file The file of the square to be set blank.
	 * @param rank The rank of the square to be set blank.
	 */
	public void removePieceAt(char file, int rank) {
		setPieceAt(file, rank, new Blank(file, rank));
	}
	
	/**
	 * Returns a list of all possible moves in this position.
	 * 
	 * @return A list of all possible moves.
	 */
	public List<Move> getAllMoves() {
		List<Move> allMoves = new LinkedList<Move>();
		for(int rank = 1; rank < getHeight(); rank++) {
			for(int file = 1; file < getWidth(); file++) {
				Piece p = getPieceAt(file, rank);
//				System.out.print(p);
				if(p.getColor() == getSideToMove() && !p.isBlank()) {	
					allMoves.addAll(getPieceAt(file, rank).getPossibleMoves(this));
				}
			}
//			System.out.println();
		}
		return allMoves;
	}
	
	public Square getSquareAt(char file, int rank) {
		return getPieceAt(file, rank).getCurrentSquare();
	}
	
	public Square getSquareAt(int file, int rank) {
		return getPieceAt(file, rank).getCurrentSquare();
	}
	
	public boolean isOccupiedAt(char file, int rank) {
		return getPieceAt(file, rank).isOccupied();
	}
	
	public boolean isOccupiedAt(int file, int rank) {
		return getPieceAt(file, rank).isOccupied();
	}
	
	public boolean isOccupiedAt(Square sq) {
		return getPieceAt(sq).isOccupied();
	}
	
	public int getOccupiedAt(int file, int rank) {
		return getPieceAt(file, rank).getColor();
	}
	
	public int getOccupiedAt(char file, int rank) {
		return getPieceAt(file, rank).getColor();
	}

	public int getOccupiedAt(Square sq) {
		return getOccupiedAt(sq.getFile(), sq.getRank());
	}
	
	public int getColorAt(int file, int rank) {
		return getOccupiedAt(file, rank);
	}
	
	public int getColorAt(char file, int rank) {
		return getOccupiedAt(file, rank);
	}
	
	public int getColorAt(Square sq) {
		return getOccupiedAt(sq);
	}
	
	private boolean generalCastlingCheck(King king, Rook rook, int side) {
		// Makes sure the king is of the right color
		if(king.getColor() != getSideToMove())
			return false;
		// Makes sure the king isn't in check
		if(isCheck())
			return false;
		// Checks if these pieces are the correct ones
		if(rook == null || king == null)
			return false;
		// Checks if either piece has moved
		if(king.hasMoved() || rook.hasMoved())
			return false;
		// Checks if they're on the same rank, and the starting rank
		int startRank = (king.getColor() == Piece.WHITE) ? 1 : 8 ;
		int kRank = king.getCurrentRank();
		int rRank = rook.getCurrentRank();
		if(rRank != startRank || kRank != startRank || kRank != rRank) {
			return false;
		}
		int kFile = king.getCurrentX();
		int rFile = rook.getCurrentX();
		// Checks to make sure the king is to the correct side of the rook
		if(side == Move.KINGSIDE_CASTLE && kFile >= rFile)
			return false;
		if(side == Move.QUEENSIDE_CASTLE && kFile <= rFile)
			return false;
		// Makes sure the squares between are safe
		if(side == Move.KINGSIDE_CASTLE) {
			for(int file = kFile + 1; file <= 7; file++) { // Checks up to the g-file
				if(this.isOccupiedAt(file, kRank) || this.isAttacked(file, kRank))
					return false;
			}
		}
		else if(side == Move.QUEENSIDE_CASTLE) {
			for(int file = kFile - 1; file >= 3; file--) { // Checks up to the c-file
				if(this.isOccupiedAt(file, kRank) || this.isAttacked(file, kRank))
					return false;
			}
		}
		return true;
	}
	
	public boolean whiteCanCastleKingside() {
		return generalCastlingCheck(whiteKing, whiteKingsideRook, Move.KINGSIDE_CASTLE);
	}
	
	public boolean whiteCanCastleQueenside() {
		return generalCastlingCheck(whiteKing, whiteQueensideRook, Move.QUEENSIDE_CASTLE);
	}

	public boolean blackCanCastleKingside() {
		return generalCastlingCheck(blackKing, blackKingsideRook, Move.KINGSIDE_CASTLE);
	}

	public boolean blackCanCastleQueenside() {
		return generalCastlingCheck(blackKing, blackQueensideRook, Move.QUEENSIDE_CASTLE);
	}
	
	/**
	 * Updates the board after a kingside castle.
	 * 
	 * Precondition: This method must be called after it is confirmed
	 * that castling can take place.
	 */
	public void castleKingside() {
		char kFile;
		char rFile;
		int rank;
		if(sideToMoveIs(Piece.WHITE)) {
			if(!whiteCanCastleKingside())
				throw new IllegalMoveException("White attempt to castle kingside is illegal.");
			rank = whiteKing.getCurrentRank();
			kFile = whiteKing.getCurrentFile();
			rFile = whiteKingsideRook.getCurrentFile();
		}
		else if(sideToMoveIs(Piece.BLACK)) {
			if(!blackCanCastleKingside())
				throw new IllegalMoveException("Black attempt to castle kingside is illegal.");
			rank = blackKing.getCurrentRank();
			kFile = blackKing.getCurrentFile();
			rFile = blackKingsideRook.getCurrentFile();
		}
		else
			return;
		movePieceWithoutIncrementing(kFile, rank, 'g', rank);
		movePiece(rFile, rank, 'f', rank);
	}
	
	/**
	 * Updates the board after a queenside castle.
	 * 
	 * Precondition: This method must be called after it is confirmed
	 * that castling can take place.
	 */
	public void castleQueenside() {
		char kFile;
		char rFile;
		int rank;
		if(sideToMoveIs(Piece.WHITE)) {
			if(!whiteCanCastleQueenside())
				throw new IllegalMoveException("White attempt to castle queenside is illegal.");
			whiteKing.setMovedTrue();
			whiteQueensideRook.setMovedTrue();
			rank = whiteKing.getCurrentRank();
			kFile = whiteKing.getCurrentFile();
			rFile = whiteQueensideRook.getCurrentFile();
		}
		else if(sideToMoveIs(Piece.BLACK)) {
			if(!blackCanCastleQueenside())
				throw new IllegalMoveException("Black attempt to castle queenside is illegal.");
			blackKing.setMovedTrue();
			blackKingsideRook.setMovedTrue();
			rank = blackKing.getCurrentRank();
			kFile = blackKing.getCurrentFile();
			rFile = blackQueensideRook.getCurrentFile();
		}
		else
			return;
		movePieceWithoutIncrementing(kFile, rank, 'c', rank);
		movePiece(rFile, rank, 'd', rank);
	}
	
	public Square getEnPassantSquare() {
		return enPassantSquare;
	}
	
	public Piece getEnPassantPiece() {
		return enPassantPiece;
	}
	
	public void setEnPassant(Piece enPassantPiece, Square enPassantSquare) {
		this.enPassantPiece = enPassantPiece;
		this.enPassantSquare = enPassantSquare;
	}

	public boolean isEnPassantTurn() {
		return enPassantSquare != null;
	}
	
	public void endEnPassantTurn() {
		enPassantSquare = null;
		enPassantPiece = null;
	}
	
	public boolean canStopOn(Square sq, Piece p) {
		return canStopOn(sq.getFile(), sq.getRank(), p);
	}
	
	public boolean canStopOn(char file, int rank, Piece p) {
		if(p.isPawn())
			throw new PieceTypeException("This method should not be called for pawns.");
		return this.getOccupiedAt(file, rank) != p.getColor()
				&& this.getOccupiedAt(file, rank) != Piece.LABEL;
	}
	
	public boolean canStopOn(int file, int rank, Piece p) {
		if(p.isPawn())
			throw new PieceTypeException("This method should not be called for pawns.");
		return this.getOccupiedAt(file, rank) != p.getColor()
				&& this.getOccupiedAt(file, rank) != Piece.LABEL;
	}
	
	public boolean canCaptureOn(Square sq, Piece p) {
		return canCaptureOn(sq.getFile(), sq.getRank(), p);
	}
	
	public boolean canCaptureOn(char file, int rank, Piece p) {
		if(p.isBlank() || p.isLabel())
			return false;
		if(p.isWhite())
			return this.getOccupiedAt(file, rank) == Piece.BLACK;
		else
			return this.getOccupiedAt(file, rank) == Piece.WHITE;
	}
	
	public boolean canCaptureOn(int file, int rank, Piece p) {
		if(p.isBlank() || p.isLabel())
			return false;
		if(p.isWhite())
			return this.getOccupiedAt(file, rank) == Piece.BLACK;
		else
			return this.getOccupiedAt(file, rank) == Piece.WHITE;
	}
	
	public boolean isSideInCheck(int side) {
		if(side == Piece.WHITE)
			return whiteKing.isInCheck(this);
		else if(side == Piece.BLACK)
			return blackKing.isInCheck(this);
		else
			return false;
	}
	
	public boolean isWhiteInCheck() {
		try {
			return whiteKing.isInCheck(this);
		}
		catch(NullPointerException e) {
			throw new PieceTypeException("White king not found.");
		}
	}
	
	public boolean isBlackInCheck() {
		try {
			return blackKing.isInCheck(this);
		}
		catch(NullPointerException e) {
			throw new PieceTypeException("Black king not found.");
		}
	}
	
	public boolean isCheck() {
		if(getSideToMove() == Piece.WHITE)
			return isWhiteInCheck();
		else if(getSideToMove() == Piece.BLACK)
			return isBlackInCheck();
		else
			return false;
	}
	
	/**
	 * Checks to see if the king is still in check after a move is made.
	 * 
	 * @param m The move to be attempted.
	 * @return True if the king of the side that made the move is still in check
	 * after the move is made.
	 */
	public boolean isStillCheckAfterMove(Move m) {
		return isStillCheckAfterMove(m.getStartSquare(), m.getTargetSquare());
	}
	
	/**
	 * Checks to see if the king is still in check after a move is made.
	 * 
	 * @param startSquare The starting square of the move.
	 * @param endSquare The target square of the move.
	 * @return True if the king of the side that made the move is still in check
	 * after the move is made.
	 */
	public boolean isStillCheckAfterMove(Square startSquare, Square endSquare) {
		// Makes sure the actual position isn't changed
		Position dummy = new Position(this);
		dummy.movePieceWithoutUpdating(startSquare, endSquare);
//		System.out.println(dummy);
//		System.out.println(dummy.getSideToMove() + " is now moving");
//		System.out.println(dummy.isCheck());
//		System.out.println(whiteKing.getCurrentSquare());
		// TODO debug
		return dummy.isCheck();
	}
	
	/**
	 * Finds pieces of the color opposite the side to move that can attack a given square.
	 * 
	 * @param sq The square to check.
	 * @return True if a piece is attacking it.
	 */
	public boolean isAttacked(Square sq) {
		switchSideToMove();
		boolean pAttacking = Pawn.findAttacking(sq, this).size() > 0;
		boolean bAttacking = Bishop.findAttacking(sq, this).size() > 0;
		boolean nAttacking = Knight.findAttacking(sq, this).size() > 0;
		boolean rAttacking = Rook.findAttacking(sq, this).size() > 0;
		boolean qAttacking = Queen.findAttacking(sq, this).size() > 0;
		boolean kAttacking = King.findAttacking(sq, this).size() > 0;
		switchSideToMove();
		return pAttacking || bAttacking || nAttacking || rAttacking || qAttacking || kAttacking;
	}
	
	public boolean isAttacked(char file, int rank) {
		return isAttacked(fileToXMap.get(file), rank);
	}
	
	public boolean isAttacked(int file, int rank) {
		return isAttacked(new Square(file, rank));
		
	}
	
	public int getWidth() {
		return pieces[0].length;
	}
	
	public int getHeight() {
		return pieces.length;
	}
	
	/**
	 * Returns the sum of the point values of all white pieces on the board.
	 * 
	 * @return An integer representing the sum of the point values of all white pieces.
	 */
	public int getWhitePoints() {
		int count = 0;
		for(Piece[] pa : pieces) {
			for(Piece p : pa) {
				if(p.getColor() == Piece.WHITE)
					count += p.getValue();
			}
		}
		return count;
	}
	
	/**
	 * Returns the sum of the point values of all black pieces on the board.
	 * 
	 * @return An integer representing the sum of the point values of all black pieces.
	 */
	public int getBlackPoints() {
		int count = 0;
		for(Piece[] pa : pieces) {
			for(Piece p : pa) {
				if(p.getColor() == Piece.BLACK)
					count += p.getValue();
			}
		}
		return count;
	}
	
	/**
	 * Returns the difference between point values. If white has more points, then
	 * the result will be positive.
	 * 
	 * @return An integer representing the difference in point values for each side.
	 */
	public int getDelta() {
		List<Move> allMoves = getAllMoves();
		if(allMoves.size() == 0) {
			if(isInsufficientMaterial())
				return 0;
			else if(isWhiteInCheck())
				return -999;
			else if(isBlackInCheck())
				return 999;
			else
				return 0;
		}
		else
			return getWhitePoints() - getBlackPoints();
	}
	
	/**
	 * Checks if there is insufficient material for either side to checkmate.
	 * Accounts for: BK vs K, NK vs K, K vs K.
	 * Does not account for: any number of bishops on the same color.
	 * Note that NNK vs K, BK vs BK, and NK vs NK are not automatic draws.
	 * @return
	 */
	public boolean isInsufficientMaterial() {
		return (getWhitePoints() == 3 && getBlackPoints() == 0)
				|| (getBlackPoints() == 3 && getWhitePoints() == 0)
				|| (getWhitePoints() == 0 && getBlackPoints() == 0);
	}
	
	public int getFullMoveCount() {
		return fullMoveNumber;
	}
	
	private int countWhiteKings() {
		int count = 0;
		for(Piece[] pa : pieces) {
			for(Piece p : pa) {
				if(p.isKing() && p.isWhite())
					count++;
			}
		}
		return count;
	}
	
	private int countBlackKings() {
		int count = 0;
		for(Piece[] pa : pieces) {
			for(Piece p : pa) {
				if(p.isKing() && p.isBlack())
					count++;
			}
		}
		return count;
	}
	/**
	 * Returns the important parts of the FEN string representing this position.
	 * According to Wikipedia, an FEN string has 6 components:
	 * 1. A description of each rank, starting from rank 8 through 1
	 * going from file a to h, with empty squares denoted by numbers.
	 * 2. The current side to move.
	 * 3. Castling availability; - if nobody can castle, K if white can
	 * castle kingside, k if black can castle kingside, etc.
	 * 4. A target square for en passant. "-" if there is no such square.
	 * 
	 * @return A valid FEN string.
	 */
	public String getImportantFEN() {
		String fen = "";
		// Does pieces
		int blankCounter = 0;
		for(int rank = 8; rank >= 1; rank--) {
			for(int file = 1; file <= 8; file++) {
				Piece p = getPieceAt(file, rank);
				if(p.isBlank())
					blankCounter++;
				else {
					if(blankCounter != 0)
						fen += Integer.toString(blankCounter);
					fen += p.getIdentifier();
					blankCounter = 0;
				}
			}
			if(blankCounter != 0)
				fen += Integer.toString(blankCounter);
			blankCounter = 0;
			if(rank != 1)
				fen += "/";
		}
		fen += " ";
		// Does side to move
		int toMove = getSideToMove();
		if(toMove == Piece.WHITE)
			fen += "w ";
		else
			fen += "b ";
		// Does castling availability
		String castling = "";
		try {
			if(!whiteKing.hasMoved() && !whiteKingsideRook.hasMoved())
				castling += "K";
		}
		catch(NullPointerException e) {
		}
		try {
			if(!whiteKing.hasMoved() && !whiteQueensideRook.hasMoved())
				castling += "Q";
		}
		catch(NullPointerException e) {
		}
		try {
			if(!blackKing.hasMoved() && !blackKingsideRook.hasMoved())
				castling += "k";
		}
		catch(NullPointerException e) {
		}
		try {
			if(!blackKing.hasMoved() && !blackQueensideRook.hasMoved())
				castling += "q";
		}
		catch(NullPointerException e) {
		}
		if(castling.length() == 0)
			fen += "- ";
		else
			fen += castling + " ";
		// Does en passant target square
		if(isEnPassantTurn())
			fen += getEnPassantSquare().toString();
		else
			fen += "-";
		return fen;
	}
	
	/**
	 * Returns the important stuff of the FEN, plus the rest of it.
	 * 5. The number of half moves since the last pawn move or capture.
	 * 6. The number of the move.
	 * Having fewer than the required number of strings does not necessarily
	 * a fatal error, but may lead to strange behavior.
	 */
	public String getFEN() {
		return getImportantFEN() + " " + Integer.toString(this.halfMoveClock) + " " + Integer.toString(this.fullMoveNumber);
	}
	
}
