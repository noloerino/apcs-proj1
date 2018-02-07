import java.util.LinkedList;
import java.util.List;

public class Bishop extends Piece implements DiagonalMovable {

	public static void main(String[] args) {
		Game g = new Game(new java.util.Scanner(System.in), "7B/4n1P1/8/6B1/3k1p2/8/8/8 w - - 0 1");
		Position pos = g.getPosition();
//		System.out.println(pos);
//		System.out.println(((Bishop) pos.getPieceAt('e', 5)).getPossibleMoves(pos));
		List<Piece> attacking = Bishop.findAttacking(new Square('f', 6), pos);
		System.out.print("Attacking f6: ");
		for(Piece p : attacking) {
			System.out.print(p.getCurrentSquare() + " ");
		}
	}
	
	public Bishop(char file, int rank, int color) {
		this(Character.toString(file) + Integer.toString(rank), color);
	}
	
	public Bishop(String startSquare, int color) {
		super(startSquare, (color == WHITE)?(WHITE_BISHOP):(BLACK_BISHOP));
		if(color != WHITE && color != BLACK)
			throw new PieceColorException();
	}
	
	private Bishop(String startSquare) {
		super(startSquare, 0);
	}
	
	@Override
	public Piece makeCopy() {
		return new Bishop(getCurrentFile(), getCurrentRank(), getColor());
	}
	
	@Override
	public boolean isBishop() {
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
		List<Move> possible = DiagonalMovable.findPossibleMoves(pos, this);
		return possible;
	}
	
	/**
	 * Checks if the argument square is under attack diagonally.
	 * Should be called by kings when checking if it is in check, and by blank squares
	 * when a castling attempt is made.
	 * 
	 * @param checking The piece whose square is being checked.
	 * @param pos The current position.
	 * @return If the square is under attack from a diagonal.
	 */
	public static boolean isAttacking(Square sq, Position pos) {
		return Bishop.findAttacking(sq, pos).size() > 0;
	}

	/**
	 * Finds all possible bishops that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @return All possible rooks of the correct color that can go to that square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos) {
		return findAttacking(sq, pos, false);
	}
	
	/**
	 * Finds all possible diagonal-moving pieces that can attack a given square.
	 * 
	 * @param sq The square to be tested.
	 * @param pos The current position.
	 * @param isQueen If this is true, the method will search for queens. Otherwise, it will
	 * search for bishops as normal.
	 * @return All possible rooks of the correct color that can go to that square.
	 */
	public static LinkedList<Piece> findAttacking(Square sq, Position pos, boolean isQueen) {
		LinkedList<Piece> possible = new LinkedList<Piece>();
		// Creates a dummy piece
		Bishop dummy = new Bishop(sq.toString());
		int dummyFile = dummy.getCurrentX();
		int dummyRank = dummy.getCurrentY();
		int dummyColor = pos.getColorAt(sq);
		int movingColor = pos.getSideToMove();
		if(dummyColor == movingColor)
			return possible;
		// Checks squares to the east (increasing file)
		int rankUp = dummyRank + 1;
		int rankDown = dummyRank - 1;
		boolean checkingUp = true; // If an obstruction has not been found yet
		boolean checkingDown = true;
		for(int file = dummyFile + 1; file < pos.getWidth(); file++) {
			// Checks squares to the northeast (increasing rank)
			if(rankUp > 8);
			else {
				Square testing = new Square(file, rankUp);
				int testColor = pos.getOccupiedAt(testing);
				if(pos.getOccupiedAt(testing) == LABEL)
					checkingUp = false;
				else if(!pos.isOccupiedAt(testing));
				else if(checkingUp && testColor == movingColor
						&& (isQueen ? (pos.getPieceAt(testing) instanceof Queen)
						: (pos.getPieceAt(testing) instanceof Bishop)))
					possible.add(pos.getPieceAt(testing));
				else
					checkingUp = false;
				rankUp++;
			}
			// Checks squares to the southeast (decreasing rank)
			if(rankDown < 1);
			else {
				Square testing = new Square(file, rankDown);
				int testColor = pos.getOccupiedAt(testing);
				if(pos.getOccupiedAt(testing) == LABEL)
					checkingDown = false;
				else if(!pos.isOccupiedAt(testing));
				else if(checkingDown && testColor == movingColor
						&& (isQueen ? (pos.getPieceAt(testing) instanceof Queen)
						: (pos.getPieceAt(testing) instanceof Bishop)))
					possible.add(pos.getPieceAt(testing));
				else
					checkingDown = false;
				rankDown--;
			}
			if(!checkingUp && !checkingDown)
				break;
		}
		// Checks squares to the west (decreasing file)
		rankUp = dummyRank + 1;
		rankDown = dummyRank - 1;
		checkingUp = true;
		checkingDown = true;
		for(int file = dummyFile - 1; file > 0; file--) {
			// Checks squares to the northwest (increasing rank)
			if(rankUp > 8);
			else {
				Square testing = new Square(file, rankUp);
				int testColor = pos.getOccupiedAt(testing);
				if(pos.getOccupiedAt(testing) == LABEL)
					checkingUp = false;
				else if(!pos.isOccupiedAt(testing));
				else if(checkingUp && testColor == movingColor
						&& (isQueen ? (pos.getPieceAt(testing) instanceof Queen)
						: (pos.getPieceAt(testing) instanceof Bishop)))
					possible.add(pos.getPieceAt(testing));
				else
					checkingUp = false;
				rankUp++;
			}
			// Checks squares to the southwest (decreasing rank)
			if(rankDown < 1);
			else {
				Square testing = new Square(file, rankDown);
				int testColor = pos.getOccupiedAt(testing);
				if(pos.getOccupiedAt(testing) == LABEL)
					checkingDown = false;
				else if(!pos.isOccupiedAt(testing));
				else if(checkingDown && testColor == movingColor
						&& (isQueen ? (pos.getPieceAt(testing) instanceof Queen)
						: (pos.getPieceAt(testing) instanceof Bishop)))
					possible.add(pos.getPieceAt(testing));
				else
					checkingDown = false;
				rankDown--;
			}
			if(!checkingUp && !checkingDown)
				break;
		}
		return possible;
	}
	
}
