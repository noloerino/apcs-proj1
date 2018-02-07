
public interface ChecksIfMoved {

	/**
	 * Checks if a piece has been moved.
	 * 
	 * @return True if and only if the piece has moved at least once this game.
	 */
	boolean hasMoved();
	
	/**
	 * States that a piece has been moved.
	 */
	void setMovedTrue();
	
}
