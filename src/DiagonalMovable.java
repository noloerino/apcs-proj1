import java.util.LinkedList;
import java.util.List;

public interface DiagonalMovable {

	/**
	 * Finds all possible moves of a piece that can move diagonally.
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
		// Checks squares to the east (increasing file)
		int rankUp = dummyRank + 1;
		int rankDown = dummyRank - 1;
		boolean checkingUp = true;
		boolean checkingDown = true;
		for(int file = dummyFile + 1; file < pos.getWidth(); file++) {
			// Checks squares to the northeast (increasing rank)
			if(rankUp > 8);
			else {
				if(pos.getOccupiedAt(file, rankUp) == Piece.LABEL
						|| pos.isOccupiedAt(file, rankUp))
					checkingUp = false;
				else if(checkingUp && pos.canStopOn(file, rankUp, testing))
					possible.add(new Move(id, dummySquare, file, rankUp));
				else
					checkingUp = false;
				rankUp++;
			}
			// Checks squares to the southeast (decreasing rank)
			if(rankDown < 1);
			else {
				if(pos.getOccupiedAt(file, rankDown) == Piece.LABEL
						|| pos.isOccupiedAt(file, rankDown))
					checkingDown = false;
				else if(checkingDown && pos.canStopOn(file, rankDown, testing))
					possible.add(new Move(id, dummySquare, file, rankDown));
				else
					checkingDown = false;
				rankDown--;
			}
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
				if(pos.getOccupiedAt(file, rankUp) == Piece.LABEL
						|| pos.isOccupiedAt(file, rankUp))
					checkingUp = false;
				else if(checkingUp && pos.canStopOn(file, rankUp, testing))
					possible.add(new Move(id, dummySquare, file, rankUp));
				else
					checkingUp = false;
				rankUp++;
			}
			// Checks squares to the southwest (decreasing rank)
			if(rankDown < 1);
			else {
				if(pos.getOccupiedAt(file, rankDown) == Piece.LABEL
						|| pos.isOccupiedAt(file, rankDown))
					checkingDown = false;
				else if(checkingDown && pos.canStopOn(file, rankDown, testing))
					possible.add(new Move(id, dummySquare, file, rankDown));
				else
					checkingDown = false;
				rankDown--;
			}
		}
//		System.out.println(possible);
		// An inelegant fix to account for checks, but perhaps a necessary one
		// The alternative is to go back and do this in every single method
		possible.removeIf(m -> pos.isStillCheckAfterMove(m));
		return possible;
	}
	
}
