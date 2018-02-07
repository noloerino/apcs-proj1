import java.util.List;

public class Label extends Piece {
	
	public Label(int file, int rank, char label) {
		super(file, rank, label);
	}
	
	@Override
	public Piece makeCopy() {
		return new Label(getCurrentX(), getCurrentY(), getIcon().charAt(0));
	}

	@Override
	public boolean checkLegalMove(Position pos) {
		return false;
	}
	
	@Override
	public int getValue() {
		return 0;
	}
	
	@Override
	public boolean isLabel() {
		return true;
	}
	
	@Override
	public List<Move> getPossibleMoves(Position pos) {
		return null;
	}
}
