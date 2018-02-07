import java.util.List;

public class Blank extends Piece {
	
	public Blank(int file, int rank) {
		super(file, rank);
	}
	
	public Blank(char file, int rank) {
		this(Square.fileToXMap.get(file), rank);
	}
	
	public Blank(String newLocation) {
		super(newLocation.charAt(0), Character.getNumericValue(newLocation.charAt(1)));
		if(!newLocation.matches("[a-h][1-8]"))
			throw new PieceTypeException("Blank cannot be placed at that square!");
		if(this.getColor() != BLANK)
			throw new PieceTypeException("Blanks must be initialized with a color value of 0!");
	}
	
	@Override
	public boolean isBlank() {
		return true;
	}
	
	@Override
	public Piece makeCopy() {
		return new Blank(getCurrentFile(), getCurrentRank());
	}
	
	@Override
	public int getValue() {
		return 0;
	}

	@Override
	public boolean checkLegalMove(Position pos) {
		return false;
	}
	
	@Override
	public List<Move> getPossibleMoves(Position pos) {
		return null;
	}
}
