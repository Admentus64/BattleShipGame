package battleShipGame;



/* Java File Imports */
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;

/* Java Utility Imports */
import java.util.ArrayList;
import java.util.Collections;

/* Swing Imports (GUI) */
import javax.swing.JButton;
import javax.swing.JLabel;



/* Game Logic Class */
public class GameLogic {
	/* Private Variables */
	private final byte           maxShips      = 5;
	private final byte           maxPlayers    = 2;
	private final byte           maxGrid       = 10;
	private final byte           maxScores     = 10;
	
	private final JLabel         gameLabel     = new JLabel("");
	private final JLabel         playerLabel   = new JLabel("");
	private final JLabel         turnLabel     = new JLabel("");
	private final JLabel[]       scoreLabels   = new JLabel[maxScores];
	
	private final JButton        endTurnButton = new JButton("End Turn");
	private final JButton        restartButton = new JButton("Restart");
	private final JButton        exitButton    = new JButton("Exit");
	
	private final File           scoresFile    = new File("highscores.txt");
	private ArrayList<HighScore> highScores    = new ArrayList<HighScore>();
	
	// Private variables which aren't initialized just yet, but will be during the constructor call
	private boolean[][][]        blastCoords;
	private boolean[][][]        shipCoords;
	private boolean[][][]        tempCoords;
	private boolean[][]          ships;
	private short                turn;
	private boolean              gameStarted;
	private boolean              turnEnded;
	private int                  playerTurn;
	
	
	
	/* Constructor */
	private GameLogic() {
		for (byte i=0; i<maxScores; i++) // Making 10 new score text labels
			scoreLabels[i] = new JLabel("");
		readScoresFromFile(); // Read in the scores if present
		resetGame();          // Reset the game using default values, which also initializes the missing variables
	}
	
	
	
	/* Singleton */
	private static GameLogic instance = new GameLogic();
	
	public static GameLogic getInstance() {
		if (instance == null)
            instance = new GameLogic();
        return instance;
	}
	
	
	
	/* Getters */
	public JLabel                getGameLabel()       { return gameLabel;               }
	public JLabel                getPlayerLabel()     { return playerLabel;             }
	public JLabel                getTurnLabel()       { return turnLabel;               }
	public JLabel[]              getScoreLabels()     { return scoreLabels;             }
	public JButton               getEndTurnButton()   { return endTurnButton;           } 
	public JButton               getRestartButton()   { return restartButton;           }
	public JButton               getExitButton()      { return exitButton;              }
	public byte                  getMaxScores()       { return maxScores;               }
	public int                   getPlayer()          { return playerTurn;              }
	public int                   getOtherPlayer()     { return playerTurn == 1 ? 2 : 1; }
	public boolean               isGameStarted()      { return gameStarted;             }
	public boolean               isTurnEnded()        { return turnEnded;               }
	public short                 getTurn()            { return turn;                    }
	public  ArrayList<HighScore> getScores()          { return highScores;              }
	
	
	
	/* Public Methods */
	public int     getShipsTotal()             { return shipsCount(playerTurn);         }
	public int     getShipsCount()             { return coordsCount(playerTurn, false); }
	public int     horizontalRow()             { return horizontalRow(    playerTurn);  }
	public int     verticalColumn()            { return verticalColumn(   playerTurn);  }
	public byte    getIntactParts()            { return getIntactParts(   playerTurn);  }
	public boolean isHit(      int x, int y)   { return isHit(      x, y, playerTurn);  }
	public boolean isShip(     int x, int y)   { return isShip(     x, y, playerTurn);  }
	public boolean isShipHit(  int x, int y)   { return isShipHit(  x, y, playerTurn);  }
	public boolean isPart(     int x, int y)   { return isPart(     x, y, playerTurn);  }
	
	public void placePart(int x, int y) { // Place ship parts during the preparation phase
		if (!checkBounds(x, y, playerTurn) || turnEnded || gameStarted)
			return;
		
		// Must be an empty spot, less than maximum amount of ships and no more than 5 placed parts
		if (coordsCount(playerTurn, true) >= 5 || shipsCount(playerTurn) == maxShips || isShip(x, y, playerTurn) || isPart(x, y, playerTurn)) 
			return;
		else if (coordsCount(playerTurn, true) == 0) // May place if no parts have been placed yet
			tempCoords[playerTurn-1][x-1][y-1] = true;
		else if (!isPart(x+1, y, playerTurn) && !isPart(x-1, y, playerTurn) && !isPart(x, y+1, playerTurn) && !isPart(x, y-1, playerTurn)) // Can not place part where not connecting
			return;
		else if (horizontalRow(playerTurn) == 0 && verticalColumn(playerTurn) == 0) // Check for alignment to be off before placing
			tempCoords[playerTurn-1][x-1][y-1] = true;
		else if (horizontalRow(playerTurn) == y || verticalColumn(playerTurn) == x) // Check if parts are aligned horizon and vertical before placing
			tempCoords[playerTurn-1][x-1][y-1] = true;
	}
	
	public void removePart(int x, int y) { // Remove ship parts during the preparation phase
		if (!checkBounds(x, y, playerTurn) || turnEnded || gameStarted)
			return;
		tempCoords[playerTurn-1][x-1][y-1] = false;
	}
	
	public void makeShip() { // Turn placed ship parts into a permanent ship during the preparation phase
		if (!checkPlayers(playerTurn) || turnEnded || gameStarted)
			return;
		int index = -1;
		
		// Check for the amount of parts to know which index the ship goes into, but also checks if that index is already set
		if (coordsCount(playerTurn, true) == 2 && !ships[playerTurn-1][0])
			index = 0;
		if (coordsCount(playerTurn, true) == 3 && !ships[playerTurn-1][1])
			index = 1;
		else if (coordsCount(playerTurn, true) == 3 && !ships[playerTurn-1][2])
			index = 2;
		else if (coordsCount(playerTurn, true) == 4 && !ships[playerTurn-1][3])
			index = 3;
		else if (coordsCount(playerTurn, true) == 5 && !ships[playerTurn-1][4])
			index = 4;
		
		if (index == -1) // Not correct amount of parts / ship size is already active
			return;
		
		// Set ship for index active, end turn and turn all temporary coordinates into permanent coordinates to track
		ships[playerTurn-1][index] = true;
		endTurn(true);
		for (int x=0; x<maxGrid; x++)
			for (int y=0; y<maxGrid; y++)
				if (tempCoords[playerTurn-1][x][y]) {
					tempCoords[playerTurn-1][x][y] = false;
					shipCoords[playerTurn-1][x][y] = true;
				}
	}
	
	public void hitSpot(int x, int y) { // Hit a spot on the grid list
		if (!checkBounds(x, y, playerTurn) || turnEnded || !gameStarted)
			return;
		
		// If the spot hasn't been hit yet, hit the spot and end the turn, and if no ships parts are left intact end the game
		if (!blastCoords[playerTurn-1][x-1][y-1]) {
			blastCoords[playerTurn-1][x-1][y-1] = true;
			if (getIntactParts(getOtherPlayer()) == 0)
				endGame();
			else endTurn(true);
		}
	}
	
	public void clearScores() { // Clear out the scores
		highScores = new ArrayList<HighScore>();
		updateScores(); // Refresh
		if (scoresFile.exists())
			scoresFile.delete();
	}
	
	public void setScore(String name, int turns) { // Add a new score
		highScores.add(new HighScore(name, turns)); // Add new entry
		Collections.sort(highScores);               // Sort from lowest turns to highest turns
		if (highScores.size() > maxScores)          // Delete the last entry when the amount of scores is beyond the maximum limit
			highScores.remove(highScores.size()-1);
		updateScores();                             // Refresh
		writeScoresToFile();                        // Write to file
	}
	
	public void updateScores() { // Refresh the text for the score labels
		for (byte i=0; i<maxScores; i++) {
			if      (i >= highScores.size())      { scoreLabels[i].setText("Untitled Score"); }
			else if (highScores.get(i).isSet())   { scoreLabels[i].setText(highScores.get(i).getName() + ": " + highScores.get(i).getTurns()); }
			else                                  { scoreLabels[i].setText("Untitled Score"); }
		}
	}
	
	public void resetGame() { // Reset the game
		// Initialize with default values
		blastCoords = new boolean [maxPlayers][maxGrid][maxGrid];
		shipCoords  = new boolean [maxPlayers][maxGrid][maxGrid];
		tempCoords  = new boolean [maxPlayers][maxGrid][maxGrid];
		ships       = new boolean [maxPlayers][maxShips];
		gameStarted = false;
		playerTurn  = turn = 1;
		endTurn(false); // Allow player turn action // Allow player turn action
	}
	
	public void startGame() { // Start the game
		gameStarted = true;
		playerTurn  = turn = 1;
		endTurn(false); // Allow player action
	}
	
	public void switchPlayer() { // Switch between both players
		if (!turnEnded)
			return;
		
		playerTurn = getOtherPlayer(); // Switch to the other player
		if (!gameStarted && shipsCount(1) == maxShips && shipsCount(2) == maxShips) // Start the game if both players placed all their ships
			startGame();
		else {
			if (gameStarted && playerTurn == 1) // Increment the turn when switched back to player 1
				turn++;
			endTurn(false); // Allow player action again
		}
	}
	
	public byte getIntactParts(int player) { // Check amount of intact parts for the player
		if (!checkPlayers(player))
			return -1;
		
		byte count = 0;
		for (int x=0; x<maxGrid; x++) // Go through each square on the grid
			for (int y=0; y<maxGrid; y++) {
				if (shipCoords[player-1][x][y]) { // Check if the square has a ship on it
					count++; // Increment the count
					if (blastCoords[player == 1 ? 1 : 0][x][y]) // If a part has been hit lower the count again
						count--;
				}
			}
		
		return count; // Return the count
	}
	
	public int coordsCount(int player, boolean temp) { // Check amount of placed parts for the player
		if (!checkPlayers(player))
			return -1;
		
		int count = 0;
		for (int x=0; x<maxGrid; x++) // Go through each square on the grid
			for (int y=0; y<maxGrid; y++) {
				if (tempCoords[player-1][x][y] && temp) // Turn temporary or permanent count of ship coordinates
					count++;
				else if (shipCoords[player-1][x][y] && !temp)
					count++;
			}
		return count; // Return the count
	}
	
	public byte shipsCount(int player) { // Get the amount of made ships for the player
		if (!checkPlayers(player))
			return -1;
		
		byte count = 0;
		for (boolean ship : ships[player-1])
			if (ship)
				count++; // If the ship is made, bump up the count to return
		return count;
	}
	
	
	
	/* Private Getters */
	private boolean isHit(int x, int y, int player) { // Check if the spot has been hit by the player
		if (!checkBounds(x, y, player))
			return false;
		return blastCoords[player-1][x-1][y-1];
	}
	
	public boolean isShip(int x, int y, int player) { // Check if the square has a ship part on it
		if (!checkBounds(x, y, player))
			return false;
		return shipCoords[player-1][x-1][y-1];
	}
	
	private boolean isShipHit(int x, int y, int player) { // Check if the square has a ship part on it which has been hit
		if (!checkBounds(x, y, player))
			return false;
		return isShip(x, y, player) && isHit(x, y, getOtherPlayer());
	}
	
	private boolean isPart(int x, int y, int player) { // Check if the square has a temporary ship part on it
		if (!checkBounds(x, y, player))
			return false;
		return tempCoords[player-1][x-1][y-1];
	}
	
	private boolean checkBounds(int x, int y, int player) { // Check if the provided coordinates are within the legal bounds
		if (x < 1 || x > maxGrid || y < 1 || y > maxGrid)
			return false;
		return checkPlayers(player);
	}
	
	private boolean checkPlayers(  int player)   { return (player >= 1 && player <= maxPlayers); } // Check if the provided player is valid
	
	
	
	/* Private Methods */
	private void updateTextLabels() { // Update the text labels
		playerLabel.setText("Player Turn: " + playerTurn );
		turnLabel.setText(  "Turn: "        + getTurn());
		
		if (gameStarted)
			gameLabel.setText("Game Started");
		else gameLabel.setText("Preparation Phase");
	}
	
	private void endGame() { // End the game 
		if (highScores.size() == maxScores) { // If there's no room left to add a new score, check if the new score is better so the worst one can be removed
			if (turn < highScores.get(maxScores-1).getTurns())
				askForScore();
		}
		else askForScore(); // As long there's room for a new score proceed to add one
		resetGame();        // Restart the game again
	}
	
	private void askForScore() { // Ask for a name and then set a score if the name is valid
		if (!Main.hasWindow()) // Only proceed if the GUI is present
			return;
		
		String name = Main.getWindow().askName();
		if (name != null && name != "" && name.length() > 0)
			setScore(name, turn);
	}
	
	private void endTurn(boolean end) { // Disable or enable the turn again, which allows players to do an action
		turnEnded = end;
		endTurnButton.setEnabled(end);
		updateTextLabels();
	}
	
	private void writeScoresToFile() { // Write the scores to the scores file
		try {
			if (scoresFile.exists()) // Delete existing one first
				scoresFile.delete();
			if (scoresFile.createNewFile()) { // Make new file
				PrintWriter writer = new PrintWriter(new FileWriter(scoresFile));
				getScores().forEach((score) -> { // For each score, add two new lines (name then turns)
					writer.write(score.getName()  + "\n");
					writer.write(score.getTurns() + "\n");
				});
				writer.close();
			}
		}
		catch (IOException e) {
			System.out.println("Could not create or write to file: " + scoresFile.getName());
			e.printStackTrace();
		}
	}
	
	private void readScoresFromFile() { // Read the scores from the scores file
		if (!scoresFile.exists()) // If the scores file doesn't exist, then stop
			return;
		
		ArrayList<String> lines = new ArrayList<String>(); // Prepare to store read lines
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(scoresFile));
			String line;
			while ((line = reader.readLine()) != null) // Read each line in the scores file and store it
				lines.add(line);
			reader.close();
			
			highScores = new ArrayList<HighScore>(); // Making a new scores list
			updateScores();                          // Refresh the scores again, which are supposed to be empty
			for (byte i=0; i<lines.size()/2; i++) {  // Go through each entry
				if (i >= maxScores)
					break;
				try { setScore(lines.get(i*2), Integer.parseInt(lines.get(i*2+1))); } // Try and read the entries, then add it to the score list
				catch (Exception e) {
					System.out.println("Could not read score value: " + scoresFile.getName());
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			System.out.println("Could not read or delete file: " + scoresFile.getName());
			e.printStackTrace();
		}
	}
	
	private int horizontalRow(int player) { // Check if the temporary placed ships are placed horizontal
		if (!checkPlayers(player))
			return -1;
		if (coordsCount(player, true) <= 1) // No need to check if there's only up to one placed part
			return 0;
		
		for (int x=0; x<maxGrid; x++) // Go through each square on the grid
			for (int y=0; y<maxGrid; y++) {
				if (tempCoords[player-1][x][y]) {         // Start from the first found ship part
					if (x < 9)                            // Only check up to x index 9 to prevent out of bounds
						if (tempCoords[player-1][x+1][y]) // Return the horizontal line if there's a ship part already on the tile to the right
							return y + 1;
					if (y > 0)                            // Only check down to y index 0 to prevent out of bounds
						if (tempCoords[player-1][x-1][y]) // Return the horizontal line if there's a ship part already on the tile to the left
							return y + 1;
				}
			}
		
		return 0;
	}
	
	private int verticalColumn(int player) { // Check if the temporary placed ships are placed vertical
		if (!checkPlayers(player))
			return -1;
		if (coordsCount(player, true) <= 1) // No need to check if there's only up to one placed part
			return 0;
		
		for (int x=0; x<maxGrid; x++) // Go through each square on the grid
			for (int y=0; y<maxGrid; y++)
				if (tempCoords[player-1] [x][y]) {        // Start from the first found ship part
					if (x < 9)                            // Only check up to x index 9 to prevent out of bounds
						if (tempCoords[player-1][x][y+1]) // Return the vertical line if there's a ship part already on the tile below
							return x + 1;
					if (y > 0)                            // Only check down to y index 0 to prevent out of bounds
						if (tempCoords[player-1][x][y-1]) // Return the vertical line if there's a ship part already on the tile above
							return x + 1;
				}
		
		return 0;
	}
}
