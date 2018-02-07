import java.util.LinkedList;
import java.util.List;

public class King extends Piece implements ChecksIfMoved {
	
	/*
	public static void main(String[] args) {
		java.util.Scanner input = new java.util.Scanner(System.in);
//		System.out.print("Enter FEN to check: ");
		Game g = new Game(input, "8/8/8/8/8/6K1/8/4Q2k b - -");
		Position pos = g.getPosition();
		System.out.println(pos);
		System.out.println(pos.sideToMoveIs(BLACK));
		System.out.println(((King) pos.getPieceAt('h', 1)).isInCheck(pos));
	}*/
	
	private boolean moved = false;
	
	public King(char file, int rank, int color) {
		this(Character.toString(file) + Integer.toString(rank), color);
	}
	
	public King(String startSquare, int color) {
		super(startSquare, (color == WHITE)?(WHITE_KING):(BLACK_KING));
		if(color != WHITE && color != BLACK)
			throw new PieceColorException();
	}
	
	private King(String startSquare) {
		super(startSquare, BLANK);
	}
	
	@Override
	public Piece makeCopy() {
		Piece o = new King(getCurrentFile(), getCurrentRank(), getColor());
		if(this.moved)
			((King) o).setMovedTrue();
		return o;
	}
	
	@Override
	public String toDebugString() {
		return super.toDebugString()
				+ "\nHas moved: " + hasMoved();
	}
	
	@Override
	public boolean isKing() {
		return true;
	}
	
	/**
	 * Checks if the attempted move is legal.
	 * Since castling is handled separately, this method does not check that.
	 * 
	 * @param pos The current position.
	 * @return True if and only if the king is not in check after the move, and
	 * the destination square contains a blank or enemy piece.
	 */
	@Override
	public boolean checkLegalMove(Position pos) { 
		return King.findCanMoveTo(this.getTargetSquare(), pos).size() > 0;
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
		if(this.isWhite()) {
			if(pos.whiteCanCastleKingside())
				possible.add(new Move("castle kingside"));
			if(pos.whiteCanCastleQueenside())
				possible.add(new Move("castle queenside"));
		}
		else {
			if(pos.blackCanCastleKingside())
				possible.add(new Move("castle kingside"));
			if(pos.blackCanCastleQueenside())
				possible.add(new Move("castle queenside"));
		}
		for(int file = dummyFile - 1; file <= dummyFile + 1; file++) {
			for(int rank = dummyRank + 1; rank >= dummyRank - 1; rank--) {
				if(file != 0 && rank != 0) {
					try {
						Square sq = pos.getSquareAt(file, rank);
						if(!pos.getPieceAt(sq).isLabel()
								&& !pos.isAttacked(sq) && pos.canStopOn(sq, this))
							possible.add(new Move(id, dummySquare, sq.getX(), sq.getY()));
					}
					catch(IndexOutOfBoardException e) {
						
					}
				}
			}
		}
		// An inelegant fix to account for checks, but perhaps a necessary one
		// The alternative is to go back and do this in every single method
		possible.removeIf(m -> (m.getCastling() == 0 && pos.isStillCheckAfterMove(m)));
		return possible;
	}
	
	@Override
	public int getValue() {
		return 0;
	}
	
	/**
	 * Checks whether the king is in check.
	 * 
	 * @return True if and only if the king is in check.
	 */
	public boolean isInCheck(Position pos) {
		return pos.isAttacked(this.getCurrentSquare());
	}
	
	/**
	 * Checks if a king is attacking a given square.
	 * 
	 * @param sq The square to check.
	 * @param pos The current position.
	 * @return True if and only if a king of the moving color is adjacent to sq.
	 */
	public static boolean isAttacking(Square sq, Position pos) {
		return findAttacking(sq, pos).size() > 0;
	}
	
	/**
	 * Finds all possible kings that can move to a given square.
	 * 
	 * @param sq The square to be checked.
	 * @param pos The current position.
	 * @return A list of all possible kings that can move to that square.
	 */
	public static LinkedList<Piece> findCanMoveTo(Square sq, Position pos) {
		LinkedList<Piece> test = findAttacking(sq, pos);
		test.removeIf(p -> pos.isStillCheckAfterMove(p.getCurrentSquare(), sq));
		return test;
	}
	
	/**
	 * Finds all kings of the moving color that are attacking a given square.
	 * 
	 * @param sq The square to check.
	 * @param pos The current position.
	 * @return A list of all possible kings that can move to a given square.
	 * Its length should never be more than 1, for obvious reasons.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		// Creates a dummy piece
		King dummy = new King(sq.toString());
		int dummyFile = dummy.getCurrentX();
		int dummyRank = dummy.getCurrentY();
		int dummyColor = pos.getColorAt(sq);
		int movingColor = pos.getSideToMove();
		if(dummyColor == movingColor)
			return possible;
		for(int file = dummyFile - 1; file <= dummyFile + 1; file++) {
			for(int rank = dummyRank + 1; rank >= dummyRank - 1; rank--) {
				try {
					Piece p = pos.getPieceAt(file, rank);
					if(p != null && p instanceof King && p.getColor() == movingColor)
						possible.add(p);
				}
				catch(IndexOutOfBoardException e) {
					
				}
				// Remove this line if you ever want to have more than one king for some reason
				if(possible.size() > 0)
					return possible;
			}
		}
		return possible;
	}
	
	@Override
	public boolean hasMoved() {
		return moved;
	}
	
	@Override
	public void setMovedTrue() {
		this.moved = true;
	}
	
}
