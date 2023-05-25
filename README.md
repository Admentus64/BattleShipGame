# BattleShipGame
BattleShip Game Example

Made with Java JDK 20 (https://www.oracle.com/java/technologies/javase/jdk20-archive-downloads.html) and JUnit 5 for Java. Written with the use of Eclipse IDE Workspace (https://www.eclipse.org/ide).

Game is executed with the Main.java file (F11 with Eclipse). JUnit test file included "BattleShipTest.java" (also F11 with Eclipse).



--- Once the game is being executed ---
1. Highscores are read from highscores.txt file. Only top 10 are shown. Needs equal or lower amount of turns for a replacement. By default there are no scores set.

2. Player 1 starts

3. Place ship with Mouse Button 1 (Left Button) by clicking on a square. Ship shows up green to indicate it's still being placed. Shows in gray once it's permanently placed. A ship is ranging from 2 to 5 parts in a straight line horizontal or vertical. Each ship size can only exist once, except for size 3 which can exist twice.

4. Ships which are still being placed can be removed with Mouse Button 3 (Right Button) by clicking on a square. Once it's permanantly placed it can no longer be removed.

5. To permanently place a ship click on a green square again. If it's valid it will then turn permanent and is shown as gray.

6. Once an action has been taken (placed ship or hit spot) the player has to press the End Turn button to pass the turn along to the other player.

7. When both player have placed 5 ships the game begins once that turn has been passed.

8. Click on a sqaure with Mouse Button 1 (Left Button) once the game has started to target a spot. It will then show a green circle if you hit a ship or a red cross if it's a miss. If one of your own ships is hit the parts which have been hit show up in orange.

9. Game ends once either player successfully hit all ships. The winning player is then asked to enter his name if he/she earned a spot for the top 10.

10. Game can be restarted by pressing Restart.



--- Keyboard Shortcuts ---
1. Enter:  End Turn for player (when usable)
2. Space:  Restart game
3. Escape: Exit game

4. F5: Setup the game with placed ships for both players for quick testing
5. F6: Setup the game and hit all spots in succession for quick testing
6. F7: Setup the highscores list with preset scores
7. F8: Delete the highscores