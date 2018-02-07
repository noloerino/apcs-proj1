import java.util.LinkedList;
import java.util.List;

public class Queen extends Piece implements LineMovable, DiagonalMovable {

	public static void main(String[] args) {
		Game g = new Game(new java.util.Scanner(System.in), "8/8/8/4Q3/8/8/8/8 w - - 0 1");
		Position pos = g.getPosition();
		System.out.println(pos);
		long startTime = System.nanoTime();
		System.out.println(((Queen) pos.getPieceAt('e', 5)).getPossibleMoves(pos));
		long endTime = System.nanoTime();
		System.out.println("Finished in " + (endTime - startTime) + " ns.");
	}
	
	public Queen(char file, int rank, int color) {
		this(Character.toString(file) + Integer.toString(rank), color);
	}
	
	public Queen(String startSquare, int color) {
		super(startSquare, (color == WHITE)?(WHITE_QUEEN):(BLACK_QUEEN));
		if(color != WHITE && color != BLACK)
			throw new PieceColorException();
	}
	
	@Override
	public Piece makeCopy() {
		return new Queen(getCurrentFile(), getCurrentRank(), getColor());
	}
	
	@Override
	public boolean isQueen() {
		return true;
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
	 * A list of all possible moves this piece can make.
	 * 
	 * @param pos The current position.
	 * @return The list of all possible moves.
	 */
	@Override
	public List<Move> getPossibleMoves(Position pos) {
		List<Move> possible = LineMovable.findPossibleMoves(pos, this);
		possible.addAll(DiagonalMovable.findPossibleMoves(pos, this));
		return possible;
	}

	/**
	 * Checks if the argument square is under attack horizontally, vertically, or diagonally.
	 * Should be called by kings when checking if it is in check, and by blank squares
	 * when a castling attempt is made.
	 * 
	 * @param checking The piece whose square is being checked.
	 * @param pos The current position.
	 * @return If the square is under attack vertically, horizontally, or diagonally.
	 */
	public static boolean isAttacking(Square checking, Position pos) {
		return Queen.findAttacking(checking, pos).size() > 0;
	}
	
	/**
	 * Finds all possible queens that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @return All possible queens of the correct color that can go to that square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos) {
		LinkedList<Piece> possible = Rook.findAttacking(sq, pos, true);
		possible.addAll(Bishop.findAttacking(sq, pos, true));
		return possible;
	}
	
	@Override
	public int getValue() {
		return 9;
	}
	
}
