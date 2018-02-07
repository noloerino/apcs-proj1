Chess instructions:

Hopefully, you know the rules of the game of chess.
If you don't, then go here:
https://en.wikipedia.org/wiki/Chess#Movement

To run, go to Documents by typing "cd ~/Documents". Then type "java -jar ChessFinal.jar".
If you really want to run the class files for some reason, then instead do "cd ~/Documents/workspace/Text\ Chess/bin", then type "java TextChess".

Please do not use the debug mode. I just forgot to take it out when I made the menus.
Also, you can try to play against the computer if you want to by pressing 3 on the main menu. Please be gentle with it. I programmed some openings, but otherwise is just kinda sucks, and hangs a lot.

You might notice that thing the option to start a new game from an "FEN." An FEN is a string that specifies the position of a game. You can generate one to test from https://www.chess.com/analysis-board-editor.

Help can be accessed any time with "/help".

Have fun!

Known bugs (can also be accessed by typing “/help bugs”):
- Draws cannot be offered.
- Some move inputs specifying only the start and end square won't run.
- The fifty-move and three-move draw rules are not obeyed.
- The engine is not quite sure how to promote.
- The engine sometimes tries to move for the wrong side.
- There is also the possibility that the engine does not exist.
- Minimax algorithm tends to be... just wrong.
- Minimax algorithm returns move at the end of the game branch, not the actual move to be made.
- Move list on sidebar is a little wonky."