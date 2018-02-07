import java.util.Scanner;

public class GameMenu {
	
	private static Scanner sc = new Scanner(System.in);

	public static void baseMenu() {
		while(true) {
			System.out.println("Main Menu" );
			System.out.println("\t[1] New two-player game");
			System.out.println("\t[2] New two-player game from FEN");
//			System.out.println("\t[3] New game vs computer");
			System.out.println("\t[9] Quit");
//			System.out.println("\t[0] Debug");
			boolean running = true;
			while(running) {
				String userString = getNextLine();
				try { //Tries to interpret input as a number 
					int userNumber = Integer.parseInt(userString);
					switch(userNumber) {
						case 1:
							newGame(true);
							running = false;
							System.out.println();
							break;
						case 2:
							try {
								newGameFromFEN();
							}
							catch(AlgebraicInputException e) {
								System.out.println("Invalid FEN.");
							}
							running = false;
							System.out.println();
							break;
						case 3:
							newGame(false);
							System.out.println();
							break;
						case 0: 
							debugMenu();
							running = false;
							break;
						case 9:
							System.out.println("Quitting.");
							System.exit(0);
						default:
							System.out.println("Please enter a valid input.");
					}
				}
				catch(NumberFormatException e) {
					if(userString.length() > 0) {
						if(help(userString.substring(1)) == 1)
							return;
					}
				}				
			}
		}
	}
	
	/**
	 * Interprets whether or not an input string is a command.
	 * 
	 * @param input The input string.
	 * @return An integer representing the action that should be taken as a result.
	 * 1 means return to the previous menu, 3 means flip the board, 2 means
	 * show the move list, 4 means print the FEN, and 5 means print the position.
	 */
	public static int help(String input) {
		switch(input.toLowerCase()) {
			case "help": // if string starts with "help"
				System.out.println("Valid commands: ");
				System.out.println("\t/help: Displays this menu.");
				System.out.println("\t/help input: Gives examples of valid inputs");
				System.out.println("\t/help bugs: Shows known bugs.");
				System.out.println("\t/flip: Flips the board.");
				System.out.println("\t/list or /moves: Returns the move list.");
				System.out.println("\t/quit or /exit: Returns to the main menu.");
				System.out.println("\t/export or /fen: Prints out an FEN string representing the position.");
				System.out.println("\t/show Displays the position.");
				System.out.println("\tTo resign or offer a draw, type \"resign\" or \"draw\" without a slash.");
				System.out.println();
				break;
			case "help input":
				System.out.println("Valid move inputs: ");
				System.out.println("- A move in long algebraic notation, such as Re3-e5 or e6xd5, or c2-c4");
				System.out.println("- A move in normal algebraic notation, such as Re5 or exd5, or c4.");
				System.out.println("- A move specifiying only the start and end square, such as e3-e5, e6-d5, or c2-c4");
				System.out.println("- A string such as \"rook to e5\", \"rook from e3 to e5\","
						+ "\n\t\"pawn to d5\", \"pawn takes d5\", \"pawn on e6 takes d5\", etc.");
				System.out.println("- An attempt to castle, such as 0-0 or \"castle kingside\".");
				System.out.println("- An attempt to resign, such as \"resigns\" or 1-0");
				System.out.println();
				break;
			case "help bugs":
				System.out.println("Known problems: ");
				System.out.println("- Draws cannot be offered.");
				System.out.println("- Some move inputs specifying only the start and end square won't run.");
				System.out.println("- The fifty-move and three-move draw rules are not obeyed.");
				System.out.println("- The engine is not quite sure how to promote.");
				System.out.println("- The engine sometimes tries to move for the wrong side.");
				System.out.println("- There is also the possibility that the engine does not exist.");
				System.out.println("- Minimax algorithm tends to be... just wrong.");
				System.out.println("- Move list on sidebar is a little wonky.");
				System.out.println();
				break;
			case "flip":
				return 3;
			case "undo":
				System.out.println("All moves are final. "
						+ "You gotta live with the consequences of your actions, you know.");
				break;
			case "list": case "moves":
				return 2;
			case "quit": case "exit":
				return 1;
			case "export": case "fen":
				return 4;
			case "show": case "position": case "pos": case "display":
				return 5;
			default:
				System.out.println("Invalid input. Type \"help\" for a list of commands.");
		}
		return 0;
	}
	
	private static void newGame(boolean twoPlayer) {
		System.out.println("Starting new game.");
		System.out.println();
		if(twoPlayer)
			Game.normalGame(sc);
		else {
			System.out.println("You're not suppposed to be here. Please leave.");
			System.out.println("Which side do you want to play?");
			System.out.println("Be warned: the computer is really bad and really glitchy.");
			System.out.println("If it hangs, just press ctrl-C to quit.");
			System.out.println("[1] White");
			System.out.println("[2] Black");
			System.out.println("[9] Go back to previous menu");
			boolean running = true;
			while(running) {
				String userString = getNextLine();
				try {
					int userNumber = Integer.parseInt(userString);
					switch(userNumber) {
					case 1:
						Game.vsComputerGame(sc, true);
						running = false;
						break;
					case 2:
						Game.vsComputerGame(sc, false);
						running = false;
						break;
					default:
						System.out.println("Returning to previous menu");
					}
				}
				catch(NumberFormatException e) {
					if(userString.length() > 0) {
						if(help(userString.substring(1)) == 1)
							return;
					}
				}
			}
		}
		return;
	}
	
	private static void newGameFromFEN() {
		System.out.println("Paste an FEN string here: ");
		Game.normalGame(sc, getNextLine());
	}
	
	private static void debugMenu() {
		System.out.println("YOU SHOULD NOT BE HERE. BEGONE.");
		while(true) {
			System.out.println("Main [2]: Debug Menu");
			System.out.println("\t[1] Do nothing");
			System.out.println("\t[2] Run game with debugging displays");
			System.out.println("\t[3] Test input strings");
			System.out.println("\t[9] Return to main menu");
			String userString = getNextLine();
			try {
				int userNumber = Integer.parseInt(userString);
				switch(userNumber) {
					case 2:
						Game.debugGame(sc);
						break;
					case 3:
						Move.main();
						break;
					case 9:
						return;
					default:
						System.out.println("Invalid input.");
						return;
				}
			}
			catch(NumberFormatException e) {
				//This is a string
			}
		}
	}
	
	private static String getNextLine() {
		System.out.print(">>> ");
		return sc.nextLine().trim();
	}
	
}
