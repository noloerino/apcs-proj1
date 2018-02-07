import java.util.LinkedList;
import java.util.List;

public class Rook extends Piece implements ChecksIfMoved, LineMovable {

	private boolean moved;
	
	public static void main(String[] args) {
		Game g = new Game(new java.util.Scanner(System.in), "8/8/8/4R3/8/8/8/8 w - - 0 1");
		Position pos = g.getPosition();
		System.out.println(pos);
		System.out.println(((Rook) pos.getPieceAt('e', 5)).getPossibleMoves(pos));
	}
	
	public Rook(char file, int rank, int color) {
		this(Character.toString(file) + Integer.toString(rank), color);
	}
	
	public Rook(String startSquare, int color) {
		super(startSquare, (color == WHITE)?(WHITE_ROOK):(BLACK_ROOK));
		if(color != WHITE && color != BLACK)
			throw new PieceColorException();
	}
	
	/**
	 * Creates a "dummy" rook at a given location to test if a square is attacked.
	 * 
	 * @param startSquare The square the rook should be placed on.
	 */
	private Rook(String startSquare) {
		super(startSquare, BLANK);
	}
	
	@Override
	public Piece makeCopy() {
		Piece o = new Rook(getCurrentFile(), getCurrentRank(), getColor());
		if(this.moved)
			((Rook) o).setMovedTrue();
		return o;
	}

	/**
	 * Checks if the attempted move is legal.
	 * 
	 * @param pos The current position.
	 * @return True if and only if the target square is on the same file, the target square
	 * is blank or contains an enemy piece, and all squares in between are empty.
	 */
	@Override
	public boolean checkLegalMove(Position pos) {
		return isAttacking(this.getTargetSquare(), pos);
	}
	
	/**
	 * Returns a list of all possible moves this piece can make.
	 * 
	 * @param pos The current position.
	 * @return The list of all possible moves.
	 */
	@Override
	public List<Move> getPossibleMoves(Position pos) {
		List<Move> possible = LineMovable.findPossibleMoves(pos, this);
		return possible;
	}
	
	/**
	 * Checks if the argument square is under attack horizontally or vertically.
	 * Should be called by kings when checking if it is in check, and by blank squares
	 * when a castling attempt is made.
	 * 
	 * @param checking The piece whose square is being checked.
	 * @param pos The current position.
	 * @return If the square is under attack vertically or horizontally.
	 */
	public static boolean isAttacking(Square checking, Position pos) {
		return Rook.findAttacking(checking, pos).size() > 0;
	}
	
	/**
	 * Finds all possible rooks that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @return All possible rooks of the correct color that can go to that square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos) {
		return findAttacking(sq, pos, false);
	}
	
	/**
	 * Finds all possible horizontal-moving pieces that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @param isQueen If this is true, the method will search for queens. Otherwise, it will
	 * search for rooks as normal.
	 * @return All possible rooks or queens of the correct color that can go to that square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos, boolean isQueen) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		// Creates a dummy piece
		Rook dummy = new Rook(sq.toString());
		int dummyFile = dummy.getCurrentX();
		int dummyRank = dummy.getCurrentY();
		int dummyColor = pos.getColorAt(sq);
		int movingColor = pos.getSideToMove();
		// This means that the piece on this square is the same color
		// as the side to move, meaning the capture shouldn't be possible
		if(dummyColor == movingColor)
			return possible;
		// Checks squares above (increasing file, constant rank)
		for(int rank = dummyRank + 1; rank < pos.getHeight(); rank++) {
			int testColor = pos.getColorAt(dummyFile, rank);
			// This check should never be reached, but just to be safe
			if(pos.getOccupiedAt(dummyFile, rank) == LABEL)
				break;
			// This square is blank
			else if(!pos.isOccupiedAt(dummyFile, rank))
				continue;
			// This means that the square might be under attack!
			else if(testColor == movingColor
					&& (isQueen ? (pos.getPieceAt(dummyFile, rank) instanceof Queen)
					: (pos.getPieceAt(dummyFile, rank) instanceof Rook))) {
				possible.add(pos.getPieceAt(dummyFile, rank));
				break;
			}
			// Means that the square here is occupied by something other than a rook
			else
				break;
		}
		// Checks squares to the left (constant rank, decreasing file)
		for(int file = dummyFile - 1; file > 0; file--) {
			int testColor = pos.getOccupiedAt(file, dummyRank);
			if(pos.getOccupiedAt(file, dummyRank) == LABEL)
				break;
			else if(!pos.isOccupiedAt(file, dummyRank))
				continue;
			else if(testColor == movingColor
					&& (isQueen ? (pos.getPieceAt(file, dummyRank) instanceof Queen)
							: (pos.getPieceAt(file, dummyRank) instanceof Rook))) {
				possible.add(pos.getPieceAt(file, dummyRank));
				break;
			}
			else
				break;
		}
		// Checks squares to the right (constant rank, increasing file)
		for(int file = dummyFile + 1; file < pos.getWidth(); file++) {
			int testColor = pos.getOccupiedAt(file, dummyRank);
			if(pos.getOccupiedAt(file, dummyRank) == LABEL)
				break;
			else if(!pos.isOccupiedAt(file, dummyRank))
				continue;
			else if(testColor == movingColor
					&& (isQueen ? (pos.getPieceAt(file, dummyRank) instanceof Queen)
							: (pos.getPieceAt(file, dummyRank) instanceof Rook))) {
				possible.add(pos.getPieceAt(file, dummyRank));
				break;
			}
			else
				break;
		}
		// Checks squares below (decreasing rank, constant file)
		for(int rank = dummyRank - 1; rank > 0; rank--) {
			int testColor = pos.getColorAt(dummyFile, rank);
			if(pos.getOccupiedAt(dummyFile, rank) == LABEL)
				break;
			else if(!pos.isOccupiedAt(dummyFile, rank))
				continue;
			else if(testColor == movingColor
					&& (isQueen ? (pos.getPieceAt(dummyFile, rank) instanceof Queen)
							: (pos.getPieceAt(dummyFile, rank) instanceof Rook))) {
				possible.add(pos.getPieceAt(dummyFile, rank));
				break;
			}
			else
				break;
		}
		return possible;
	}
	
	@Override
	public String toDebugString() {
		return super.toDebugString()
				+ "\nHas moved: " + hasMoved();
	}
	
	@Override
	public boolean isRook() {
		return true;
	}
	
	@Override
	public int getValue() {
		return 5;
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
