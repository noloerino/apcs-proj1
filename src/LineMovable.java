import java.util.LinkedList;
import java.util.List;

public interface LineMovable {

	/**
	 * Finds all possible moves that can be made by a given piece that can move
	 * horizontally or vertically.
	 * 
	 * @param pos The current position.
	 * @param testing The piece to be tested.
	 * @return A list of all possible moves that the piece being tested can make.
	 */
	public static List<Move> findPossibleMoves(Position pos, Piece testing) {
		List<Move> possible = new LinkedList<Move>();
		Square dummySquare = testing.getCurrentSquare();
		int dummyFile = testing.getCurrentX();
		int dummyRank = testing.getCurrentY();
		char id = testing.getIdentifier();
		// Checks squares above (increasing file, constant rank)
		for(int rank = dummyRank + 1; rank < pos.getHeight(); rank++) {
			// testing check should never be reached, but just to be safe
			if(pos.isOccupiedAt(dummyFile, rank)
					|| pos.getOccupiedAt(dummyFile, rank) == Piece.LABEL)
				break;
			// If the square can be stopped on, it is added as a move
			else if(pos.canStopOn(dummyFile, rank, testing))
				possible.add(new Move(id, dummySquare, dummyFile, rank));
			// Otherwise, the program should break
			else
				break;
		}
		// Checks squares to the left (constant rank, decreasing file)
		for(int file = dummyFile - 1; file > 0; file--) {
			if(pos.isOccupiedAt(file, dummyRank)
					|| pos.getOccupiedAt(file, dummyRank) == Piece.LABEL)
				break;
			else if(pos.canStopOn(file, dummyRank, testing))
				possible.add(new Move(id, dummySquare, file, dummyRank));
			else
				break;
		}
		// Checks squares to the right (constant rank, increasing file)
		for(int file = dummyFile + 1; file < pos.getWidth(); file++) {
			if(pos.isOccupiedAt(file, dummyRank)
				|| pos.getOccupiedAt(file, dummyRank) == Piece.LABEL)
				break;
			else if(pos.canStopOn(file, dummyRank, testing))
				possible.add(new Move(id, dummySquare, file, dummyRank));
			else
				break;
			
		}
		// Checks squares below (decreasing rank, constant file)
		for(int rank = dummyRank - 1; rank > 0; rank--) {
			if(pos.isOccupiedAt(dummyFile, rank)
					|| pos.getOccupiedAt(dummyFile, rank) == Piece.LABEL)
				break;
			else if(pos.canStopOn(dummyFile, rank, testing))
				possible.add(new Move(id, dummySquare, dummyFile, rank));
			else
				break;
		}
		// An inelegant fix to account for checks, but perhaps a necessary one
		// The alternative is to go back and do this in every single method
		possible.removeIf(m -> pos.isStillCheckAfterMove(m));
		return possible;
	}
}
