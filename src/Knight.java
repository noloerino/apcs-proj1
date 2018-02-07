import java.util.LinkedList;
import java.util.List;

public class Knight extends Piece {

	/*public static void main(String[] args) {
		Game g = new Game(new java.util.Scanner(System.in), "8/8/N7/8/8/8/8/n7 w - - 0 1");
		Position pos = g.getPosition();
		System.out.println(pos);
		System.out.println(((Knight) pos.getPieceAt('a', 6)).getPossibleMoves(pos));
		System.out.println(Knight.findAttacking(new Square('b',4), pos));
	}*/
	
	public Knight(char file, int rank, int color) {
		this(Character.toString(file) + Integer.toString(rank), color);
	}
	
	public Knight(String startSquare, int color) {
		super(startSquare, (color == WHITE)?(WHITE_KNIGHT):(BLACK_KNIGHT));
		if(color != WHITE && color != BLACK)
			throw new PieceColorException();
	}
	
	private Knight(String startSquare) {
		super(startSquare, BLANK);
	}
	
	@Override
	public Piece makeCopy() {
		return new Knight(getCurrentFile(), getCurrentRank(), getColor());
	}
	
	@Override
	public boolean isKnight() {
		return true;
	}

	@Override
	public int getValue() {
		return 3;
	}
	
	/**
	 * Checks if the attempted move is legal.
	 * 
	 * @param pos The current position.
	 * @return True if and only if the target square is blank or contains an enemy piece.
	 */
	@Override
	public boolean checkLegalMove(Position pos) {
		return isAttacking(this.getTargetSquare(), pos);
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
		// DEBUG
//		System.out.println(dummyFile);
//		System.out.println(dummyRank);
		// DEBUG
		char id = this.getIdentifier();
		Piece[] tests = new Piece[8];
		// Ranks are all incremented by one for some reason
		tests[0] = findPieceAt(dummyFile + 1, dummyRank + 2, pos);
		tests[1] = findPieceAt(dummyFile + 2, dummyRank + 1, pos);		
		tests[2] = findPieceAt(dummyFile + 2, dummyRank - 1, pos);
		tests[3] = findPieceAt(dummyFile + 1, dummyRank - 2, pos);
		tests[4] = findPieceAt(dummyFile - 1, dummyRank - 2, pos);
		tests[5] = findPieceAt(dummyFile - 2, dummyRank - 1, pos);
		tests[6] = findPieceAt(dummyFile - 2, dummyRank + 1, pos);
		tests[7] = findPieceAt(dummyFile - 1, dummyRank + 2, pos);
		for(Piece p : tests) {
			try {
//				System.out.println("file: " + p.getCurrentFile() + " rank: " + p.getCurrentRank());
				if(pos.canStopOn(p.getCurrentSquare(), this))
					possible.add(new Move(id, dummySquare, p.getCurrentX(), p.getCurrentY()));
			}
			catch(NullPointerException | IndexOutOfBoardException e) {
		
			}
		}
		// An inelegant fix to account for checks, but perhaps a necessary one
		// The alternative is to go back and do this in every single method
		possible.removeIf(m -> (pos.isStillCheckAfterMove(m)));
		// DEBUG
//		for(Move p : possible)
//			System.out.println(pos.getPieceAt(p.getStartSquare()) +
//					"" + p.getStartSquare() + "-" + p.getTargetSquare());
		// DEBUG
		return possible;
	}
	
	/**
	 * Checks if the argument square is under attack by a knight.
	 * Should be called by kings when checking if it is in check, and by blank squares
	 * when a castling attempt is made.
	 * 
	 * @param sq The piece whose square is being checked.
	 * @param pos The current position.
	 * @return If the square is under attack by an enemy knight.
	 */
	public static boolean isAttacking(Square sq, Position pos) {
		return Knight.findAttacking(sq, pos).size() > 0;
	}

	/**
	 * Finds all possible knights of the moving color that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @return All possible knights of the correct color that can go to that square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		// Creates a dummy piece
		Knight dummy = new Knight(sq.toString());
		int dummyFile = dummy.getCurrentX();
		int dummyRank = dummy.getCurrentY();
		int dummyColor = pos.getColorAt(sq);
		int movingColor = pos.getSideToMove();
		if(dummyColor == movingColor)
			return possible;
		/* 
		 * POSSIBLE KNIGHT MOVES
		 * rank indices are weird for essentially semantic reasons
		 * idk why
		 * _|_|_|_|_|_|_|_|  0: [file + 1][rank + 3]
		 * _|_|7|_|0|_|_|_|  1: [file + 2][rank + 2]
		 * _|6|_|_|_|1|_|_|  2: [file + 2][rank]
		 * _|_|_|N|_|_|_|_|  3: [file + 1][rank - 1]
		 * _|5|_|_|_|2|_|_|  4: [file - 1][rank - 1]
		 * _|_|4|_|3|_|_|_|  5: [file - 2][rank]
		 * _|_|_|_|_|_|_|_|  6: [file - 2][rank + 2]
		 * _|_|_|_|_|_|_|_|  7: [file - 1][rank + 3]
		 */
		Piece[] tests = new Piece[8];
		tests[0] = findPieceAt(dummyFile + 1, dummyRank + 2, pos);
		tests[1] = findPieceAt(dummyFile + 2, dummyRank + 1, pos);
		tests[2] = findPieceAt(dummyFile + 2, dummyRank - 1, pos);
		tests[3] = findPieceAt(dummyFile + 1, dummyRank - 2, pos);
		tests[4] = findPieceAt(dummyFile - 1, dummyRank - 2, pos);
		tests[5] = findPieceAt(dummyFile - 2, dummyRank - 1, pos);
		tests[6] = findPieceAt(dummyFile - 2, dummyRank + 1, pos);
		tests[7] = findPieceAt(dummyFile - 1, dummyRank + 2, pos);
		for(Piece p : tests) {
			if(p != null && p instanceof Knight && p.getColor() == movingColor)
				possible.add(p);
		}
		return possible;
	}
	
	/**
	 * Finds the piece at a given square.
	 * 
	 * @param file The file of the piece.
	 * @param rank The rank of the piece.
	 * @param pos The current position.
	 * @return The piece at the square if it's on the board, null if it is not.
	 */
	private static Piece findPieceAt(int file, int rank, Position pos) {
		try {
			return pos.getPieceAt(file, rank);
		}
		catch(IndexOutOfBoardException e) {
			return null;
		}
	}
	
}
