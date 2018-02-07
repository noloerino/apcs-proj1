import java.util.LinkedList;
import java.util.List;

public class Pawn extends Piece {
	
	
//	public static void main(String[] args) {
//		Game g = new Game("7k/2p4p/8/2p3p1/2PP4/5P2/4P1P1/7K w - -");
//		Position pos = g.getPosition();
//		System.out.println(pos);
//		pos.switchSideToMove();
//		System.out.println(Pawn.findCanMoveTo(new Square('f', 3), pos).get(0).getCurrentSquare());
//		/*
//		Game g = new Game(new java.util.Scanner(System.in), "7k/2p4p/6p1/2p4r/2PP2P1/5P2/4P3/7K w - -");
//		Position pos = g.getPosition();
//		System.out.println(pos);
//		String[] tests = {"c4", "d4", "e2", "f3", "g4"};
//		for(String s : tests) {
//			Piece test = pos.getPieceAt(new Square(s));
//			List<Move> moves = test.getPossibleMoves(pos);
//			System.out.print("Moves for pawn on " + s + ": ");
//			for(Move m : moves) {
//				System.out.print(m.toLongAlgebraicString() + " ");
//			}
//			System.out.println();
//		}*/
//	}
	
	public Pawn(char file, int rank, int color) {
		this(Character.toString(file) + Integer.toString(rank), color);
	}
	
	public Pawn(String startSquare, int color) {
		super(startSquare, (color == WHITE)?(WHITE_PAWN):(BLACK_PAWN));
		if(color != WHITE && color != BLACK)
			throw new PieceColorException();
	}
	
	@Override
	public Piece makeCopy() {
		return new Pawn(getCurrentFile(), getCurrentRank(), getColor());
	}
	
	@Override
	public boolean isPawn() {
		return true;
	}

	@Override
	public boolean checkLegalMove(Position pos) {
		return Pawn.findCanMoveTo(this.getTargetSquare(), pos).size() > 0;
	}
	
	/**
	 * A list of all possible moves this piece can make.
	 * 
	 * @param pos The current position.
	 * @return The list of all possible moves.
	 */
	@Override
	public List<Move> getPossibleMoves(Position pos) {
		List<Move> possible = new LinkedList<Move>();
		Square dummySquare = this.getCurrentSquare();
		int dummyFile = this.getCurrentX();
		int dummyRank = this.getCurrentY();
		char id = this.getIdentifier();
		int color = this.getColor();
		int rankUpOne = (color == WHITE) ? (dummyRank + 1) : (dummyRank - 1);
		int rankUpTwo = (color == WHITE) ? (dummyRank + 2) : (dummyRank - 2);
		// There can only ever be 4 possible moves in any given position:
		// 1. Moving one square forward
		if(!pos.isOccupiedAt(dummyFile, rankUpOne))
			possible.add(new Move(id, dummySquare, dummyFile, rankUpOne));
		// 2. Moving two squares forward
		try {
			possible.add(new Move(id,
					(findTwoSquaresForward(new Square(dummyFile, rankUpTwo), pos).get(0)).getCurrentSquare(),
					dummyFile, rankUpTwo));
		}
		catch(IndexOutOfBoundsException | IndexOutOfBoardException e) {
			
		}
		// 3. Capturing to the right
		try {
			if(pos.canCaptureOn(dummyFile + 1, rankUpOne, this))
				possible.add(new Move(id, dummySquare, dummyFile + 1, rankUpOne));
		}
		catch(IndexOutOfBoundsException | IndexOutOfBoardException e) {
			
		}
		// 4. Capturing to the left
		try {
			if(pos.canCaptureOn(dummyFile - 1, rankUpOne, this))
				possible.add(new Move(id, dummySquare, dummyFile - 1, rankUpOne));
		}
		catch(IndexOutOfBoundsException | IndexOutOfBoardException e) {
			
		}
		if(pos.isEnPassantTurn()) {
			// 3a. Capturing e.p. to the right
			try {
				if(pos.getEnPassantSquare().equals(new Square(dummyFile + 1, rankUpOne)))
					possible.add(new Move(id, dummySquare, dummyFile + 1, rankUpOne));
			}
			catch(IndexOutOfBoardException | NullPointerException e) {
				
			}
			// 4a. Capturing e.p. to the left
			try {
				if(pos.getEnPassantSquare().equals(new Square(dummyFile - 1, rankUpOne)))
					possible.add(new Move(id, dummySquare, dummyFile - 1, rankUpOne));
			}
			catch(IndexOutOfBoardException | NullPointerException e) {
				
			}
		}
		// An inelegant fix to account for checks, but perhaps a necessary one
		// The alternative is to go back and do this in every single method
		possible.removeIf(m -> pos.isStillCheckAfterMove(m));
		return possible;
	}
	
	/**
	 * Finds all possible pawns that can move to a given square.
	 * 
	 * @param sq The square to be checked.
	 * @param pos The current position.
	 * @return A list of all possible pawns that can move to that square.
	 */
	public static LinkedList<Piece> findCanMoveTo(Square sq, Position pos) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		int movingColor = pos.getSideToMove();
		// Checks possible captures
		if(pos.getColorAt(sq) != movingColor && (pos.isOccupiedAt(sq) 
				// Checks en passant case
				|| (pos.isEnPassantTurn() && sq.equals(pos.getEnPassantSquare()))))
			possible.addAll(findAttacking(sq, pos));
		// DEBUG
//		System.out.print(pos.getColorAt(sq));
//		System.out.println(" " + movingColor);
//		if(findAttacking(sq, pos).size() > 0)
//			System.out.println(findAttacking(sq, pos).get(0).getCurrentSquare());
		// Checks two squares forward
		possible.addAll(findTwoSquaresForward(sq, pos));
		// Checks all other cases
		int file = sq.getX();
		int dummyRank = sq.getY();
		int startRank;
		if(movingColor == WHITE)
			startRank = dummyRank - 1;
		else if(movingColor == BLACK)
			startRank = dummyRank + 1;
		else
			return possible;
		Piece test = checkSquare(file, startRank, pos);
		if(test != null)
			possible.add(test);
		return possible;
	}
	
	/**
	 * Finds a pawn that can move two squares forward (from the second or seventh rank) to a given square.
	 * Though it can theoretically throw IndexOutOfBoardException, it should always return before it can
	 * possibly be invoked.
	 * 
	 * @param sq The square to be checked.
	 * @param pos The current position.
	 * @param possible A list of all possible moves.
	 */
	private static LinkedList<Piece> findTwoSquaresForward(Square sq, Position pos) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		int movingColor = pos.getSideToMove();
		int file = sq.getX();
		int possibleStartRank;
		int rankBetween;
		int rankMovingFrom;
		if(movingColor == WHITE) {
			possibleStartRank = 4;
			rankBetween = 3;
			rankMovingFrom = 2;
		}
		else if(movingColor == BLACK) {
			possibleStartRank = 5;
			rankBetween = 6;
			rankMovingFrom = 7;
		}
		else
			return possible;
		if(sq.getY() != possibleStartRank)
			return possible;
		// No exception should ever be thrown here, since the starting rank
		// must be 4 or 5, and the starting file should always be valid
		if(pos.isOccupiedAt(file, rankBetween) || pos.isOccupiedAt(file, possibleStartRank))
			return possible;
		Piece pawnAt = pos.getPieceAt(file, rankMovingFrom);
		if(pawnAt.isPawn() && pawnAt.getColor() == movingColor) {
//			pos.setEnPassant(pawnAt, new Square(file, rankBetween));
			possible.add(pawnAt);
		}
		return possible;
	}
	
	/**
	 * Checks if the argument square is under attack by a pawn.
	 * Should be called by kings when checking if it is in check, and by blank squares
	 * when a castling attempt is made.
	 * 
	 * @param sq The piece whose square is being checked.
	 * @param pos The current position.
	 * @return If the square is under attack from a diagonal.
	 */
	public static boolean isAttacking(Square sq, Position pos) {
		return Pawn.findAttacking(sq, pos).size() > 0;
	}

	/**
	 * Finds all possible pawns of the current side to move that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @return All possible pawns of the correct color that can capture on this square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		int movingColor = pos.getSideToMove();
		int dummyColor = pos.getColorAt(sq);
		int dummyFile = sq.getX();
		int dummyRank = sq.getY();
		int checkingRank;
		if(dummyColor == movingColor)
			return possible;
		if(movingColor == WHITE)
			checkingRank = dummyRank - 1;
		else if(movingColor == BLACK)
			checkingRank = dummyRank + 1;
		else
			return possible;
		Piece piece1 = checkSquareWithEnPassant(dummyFile + 1, checkingRank, pos);
		Piece piece2 = checkSquareWithEnPassant(dummyFile - 1, checkingRank, pos);
		if(piece1 != null && piece1.getColor() == movingColor)
			possible.add(piece1);
		if(piece2 != null && piece2.getColor() == movingColor)
			possible.add(piece2);
		return possible;
	}
	
	/**
	 * Finds a pawn piece at the specified square, or a square that can be
	 * taken en passant.
	 * Returns null if the piece at that square is not a pawn.
	 * 
	 * @param file The file of the square.
	 * @param rank The rank of the square.
	 * @param pos The current position.
	 * @return A pawn object, or null if there is no pawn at that square.
	 */
	private static Piece checkSquareWithEnPassant(int file, int rank, Position pos) {
		try {
			Piece pl = pos.getPieceAt(file, rank);
			// TODO this part is weird
			if((pl.getColor() == pos.getSideToMove() && pl.isPawn())
					// Is a pawn or the en passant square
					|| (pos.isEnPassantTurn()
							&& (pl.isPawn() || pl.getCurrentSquare().equals(pos.getEnPassantSquare()))))
				return pl;
			else
				return null;
		}
		catch(IndexOutOfBoardException e) {
			return null;
		}
	}
	
	/**
	 * Finds a pawn piece at the specified square.
	 * Returns null if the piece at that square is not a pawn.
	 * 
	 * @param file The file of the square.
	 * @param rank The rank of the square.
	 * @param pos The current position.
	 * @return A pawn object, or null if there is no pawn at that square.
	 */
	private static Piece checkSquare(int file, int rank, Position pos) {
		try {
			Piece pl = pos.getPieceAt(file, rank);
			if(pl.getColor() == pos.getSideToMove() && pl.isPawn())
				return pl;
			else
				return null;
		}
		catch(IndexOutOfBoardException e) {
			return null;
		}
	}

	@Override
	public int getValue() {
		return 1;
	}
	
}
