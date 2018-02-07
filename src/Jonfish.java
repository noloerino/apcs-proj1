import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Very simple minimax algorithm for chess stuff.
 * 
 * @author jhshi
 *
 */
@SuppressWarnings("unused")
public class Jonfish {
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
//		Game.vsComputerGame(sc, "5k2/2r1ppp1/8/8/8/3PPP2/4K3/8 w - -", false);
//		Game.vsComputerGame(sc, "1r1r2k1/pp4p1/3Pppn1/1pRn3N/3P3P/8/PP1B1PP1/1K5R w - - 1 2", true);
		Game.vsComputerGame(sc, "6k1/6pp/8/8/8/8/6PP/5Q1K b - - 0 1", false);
	}
	
	private static int initialSideToMove = 0;
	
	/**
	 * Evaluates a position, taking into account its complexity.
	 * The depth of evaluation is determined by the number of possible moves
	 * in the current position. Crude, but works.
	 * 
	 * @param g The current game.
	 * @return The best move in the position.
	 */
	public static Move startEval(Game g) {
		int complexity = g.getAllMoves().size();
		initialSideToMove = g.getPosition().getSideToMove();
		int startDepth;
		// idk why i did this
		double interpreter = Math.log(complexity);
		System.out.println("Complexity is " + interpreter);
		System.out.println("Press enter to start computer evaluation");
		new Scanner(System.in).nextLine();
		if(interpreter > 2.5)
			startDepth = 4;
		else if(interpreter > 2.2)
			startDepth = 6;
		else if(interpreter > 2)
			startDepth = 8;
		else if(interpreter > 1)
			startDepth = 10;
		else
			startDepth = 12;
		Move m = eval(g, startDepth);
		System.out.println(m);
		return m;
	}
	
	/**
	 * Evaluates a position through simple minimax and returns the best move
	 * for the side to move.
	 * 
	 * @param g The game to evaluate.
	 * @param depth The depth remaining in this evaluation.
	 * @return The best move in this position.
	 */
	private static Move eval(Game g, int depth) {
		System.out.println("eval, depth = " + depth);
		Position p = g.getPosition();
		String fen = p.getImportantFEN();
		// Handles openings
		if(openingFENToMove.containsKey(fen))
			return openingFENToMove.get(fen).pickAGoodMove();
		// Everything else
		List<Move> allMoves = p.getAllMoves();
//		System.out.println(allMoves);
		int toMove = p.getSideToMove();
		Move maxMove = allMoves.get(0);
		int maxScore = -1000;
		Move minMove = allMoves.get(0);
		int minScore = 1000;
		// Terminalizes
		for(Move m : allMoves) {
			Game dummy = new Game(fen);
			dummy.makeMove(m);
			System.out.println("eval on move = " + m);
			int eval = evalScore(dummy, depth - 1);
			if((eval == maxScore && Math.random() > 0.2)
					|| eval > maxScore) {
				maxScore = eval;
				maxMove = m;
			}
			if((eval == minScore && Math.random() > 0.2)
					|| eval < minScore) {
				minScore = eval;
				minMove = m;
			}
		}
		if(toMove == Piece.WHITE)
			return maxMove;
		else
			return minMove;
	}
	
	private static int evalScore(Game g, int depth) {
		System.out.println("evalScore, depth = " + depth);
		Position p = g.getPosition();
		String fen = p.getImportantFEN();
		int toMove = p.getSideToMove();
		List<Move> allMoves = p.getAllMoves();
		if(allMoves.size() == 0)
			return g.getPosition().getDelta();
		Move maxMove = allMoves.get(0);
		int maxScore = -1000;
		Move minMove = allMoves.get(0);
		int minScore = 1000;
		if(depth == 0) {
			for(Move m : allMoves) {
				int score = scorePosAfterMove(fen, m);
				// If two moves evaluate to the same, it picks a random one
				if((score == maxScore && Math.random() > 0.2)
						|| score > maxScore) {
					maxScore = score;
					maxMove = m;
				}
				if((score == minScore && Math.random() > 0.2)
						|| score < minScore) {
					minScore = score;
					minMove = m;
				}
			}
		}
		else {
			for(Move m: allMoves) {
				Game test = new Game(fen);
				test.makeMove(m);
				int score = evalScore(test, depth - 1);
				// If two moves evaluate to the same, it picks a random one
				if((score == maxScore && Math.random() > 0.2)
						|| score > maxScore) {
					maxScore = score;
					maxMove = m;
				}
				if((score == minScore && Math.random() > 0.2)
						|| score < minScore) {
					minScore = score;
					minMove = m;
				}
			}
		}
		if(toMove == Piece.WHITE)
			return maxScore;
		else
			return minScore;
	}
	
	private static int scorePosAfterMove(String fen, Move m) {
		Game dummy = new Game(fen);
		dummy.makeMove(m);
		int score = dummy.getPosition().getDelta();
		System.out.println("Move "+ m + ", score " + score);
//		System.out.println(dummy.getPosition().getFEN());
		return score;
	}
	
	public static Map<String, Opening> openingFENToMove = new HashMap<String, Opening>();
	
	/**
	 * Initializes the values of the opening map.
	 */
	static {
		Opening startingPos = new Opening(
				"rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("e4")
//						new Move("d4"),
//						new Move("c4"),
//						new Move("Nf3"),
//						new Move("b3")
						)));
		Opening e4 = new Opening(
				"rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3",
				new ArrayList<Move>(Arrays.asList(
						new Move("e5"),
						new Move("d5"),
						new Move("c5"),
						new Move("e6"),
						new Move("g6")
						)));
		
		// *** OPEN GAME
		Opening e4e5 = new Opening(
				"rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nf3")
						)));
		Opening nearOpenGame = new Opening(
				"rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nc6")
						)));
		Opening openGame = new Opening(
				"r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Bb5"),
						new Move("Bc4"),
						new Move("d4")
						)));
		// TODO leave room for italian and ruy lopez
		
		// *** SICILIAN
		Opening sicilian = new Opening(
				"rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nf3")
						)));
		Opening sicBlack = new Opening(
				"rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nc6"),
						new Move("d6")
						)));
		Opening knightSic0 = new Opening(
				"r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("d4"),
						new Move("Bb5")
						)));
		Opening knightSic1 = new Opening(
				"r1bqkbnr/pp1ppppp/2n5/2p5/3PP3/5N2/PPP2PPP/RNBQKB1R b KQkq d3",
				new ArrayList<Move>(Arrays.asList(
						new Move("cxd5")
						)));
		Opening knightSic2 = new Opening(
				"r1bqkbnr/pp1ppppp/2n5/8/3pP3/5N2/PPP2PPP/RNBQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nxd4")
						)));
		Opening knightSic3 = new Opening(
				"r1bqkbnr/pp1ppppp/2n5/8/3NP3/8/PPP2PPP/RNBQKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nf6"),
						new Move("g6")
						)));
		
		Opening sicSveshnikov0 = new Opening(
				"r1bqkb1r/pp1ppppp/2n2n2/8/3NP3/8/PPP2PPP/RNBQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nc3")
						)));
		Opening sicSveshnikov1 = new Opening(
				"r1bqkb1r/pp1ppppp/2n2n2/8/3NP3/2N5/PPP2PPP/R1BQKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("e5")
						)));
		Opening sicSveshnikov2 = new Opening(
				"r1bqkb1r/pp1p1ppp/2n2n2/4p3/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq e6",
				new ArrayList<Move>(Arrays.asList(
						new Move("Ndb5")
						)));
		Opening sicSveshnikov3 = new Opening(
				"r1bqkb1r/pp1p1ppp/2n2n2/1N2p3/4P3/2N5/PPP2PPP/R1BQKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("d6")
						)));
		Opening sicSveshnikov3a = new Opening(
				"r1bqkb1r/1p1p1ppp/p1n2n2/1N2p3/4P3/2N5/PPP2PPP/R1BQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nd6")
						)));
		Opening sicSveshnikov3b = new Opening(
				"r1bqkb1r/pp3ppp/2np1n2/1N2p3/4P3/2N5/PPP2PPP/R1BQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Bg5")
						)));
		Opening sicSveshnikov4 = new Opening(
				"r1bqkb1r/pp3ppp/2np1n2/1N2p1B1/4P3/2N5/PPP2PPP/R2QKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("a6")
						)));
		Opening sicSveshnikov5 = new Opening(
				"r1bqkb1r/1p3ppp/p1np1n2/1N2p1B1/4P3/2N5/PPP2PPP/R2QKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Na3")
						)));
		Opening sicSveshnikov6 = new Opening(
				"r1bqkb1r/1p3ppp/p1np1n2/4p1B1/4P3/N1N5/PPP2PPP/R2QKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("b5")
						)));
		Opening sicSveshnikov7 = new Opening(
				"r1bqkb1r/5ppp/p1np1n2/1p2p1B1/4P3/N1N5/PPP2PPP/R2QKB1R w KQkq b6",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nd5")
						)));
		
		Opening sicADragon0 = new Opening(
				"r1bqkbnr/pp1ppp1p/2n3p1/8/3NP3/8/PPP2PPP/RNBQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nc3")
						)));
		Opening sicADragon1 = new Opening(
				"r1bqkbnr/pp1ppp1p/2n3p1/8/3NP3/2N5/PPP2PPP/R1BQKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Bg7")
						)));
		Opening sicADragon2 = new Opening(
				"r1bqk1nr/pp1pppbp/2n3p1/8/3NP3/2N5/PPP2PPP/R1BQKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Be3")
						)));
		Opening sicADragon3 = new Opening(
				"r1bqk1nr/pp1pppbp/2n3p1/8/3NP3/2N1B3/PPP2PPP/R2QKB1R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Nf6")
						)));
		Opening sicADragon4 = new Opening(
				"r1bqk2r/pp1pppbp/2n2np1/8/3NP3/2N1B3/PPP2PPP/R2QKB1R w KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("Bc4")
						)));
		Opening sicADragon5 = new Opening(
				"r1bqk2r/pp1pppbp/2n2np1/8/2BNP3/2N1B3/PPP2PPP/R2QK2R b KQkq -",
				new ArrayList<Move>(Arrays.asList(
						new Move("0-0")
						)));
		// TODO leave room for d4 stuff
	}
	
	private static class Opening {
		
		/**
		 * A list of moves that should be made from this position.
		 */
		private List<Move> goodMoves = new ArrayList<Move>();
		
		/**
		 * Makes a new opening from an FEN string.
		 * Also puts this opening in the opening map.
		 * 
		 * @param FEN The position string of the opening.
		 */
		public Opening(String FEN, ArrayList<Move> goodMoves) {
			this.goodMoves = goodMoves;
			openingFENToMove.put(FEN, this);
		}
		
		/**
		 * Randomly chooses from one of the possible moves in this position.
		 * 
		 * @return A good move.
		 */
		public Move pickAGoodMove() {
			int index = (int) (Math.random() * goodMoves.size());
			return goodMoves.get(index);
		}
		
	}
}
