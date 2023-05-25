package battleShipGame;



import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
// Java Imports
import java.util.ArrayList;
import java.util.Collections;

// Swing Imports
import javax.swing.JButton;
import javax.swing.JLabel;



// Game Logic Class
public class GameLogic {
	/* Private Variables */
	private final byte           maxShips      = 5;
	private final byte           maxPlayers    = 2;
	private final byte           maxGrid       = 10;
	private final byte           maxScores     = 10;
	
	private JLabel               gameLabel     = new JLabel("");
	private JLabel               playerLabel   = new JLabel("");
	private JLabel               turnLabel     = new JLabel("");
	private JLabel[]             scoreLabels   = new JLabel[maxScores];
	
	private JButton              endTurnButton = new JButton("End Turn");
	private JButton              restartButton = new JButton("Restart");
	private JButton              exitButton    = new JButton("Exit");
	
	private ArrayList<HighScore> highScores    = new ArrayList<HighScore>();
	private File                 scoresFile    = new File("highscores.txt");
	
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
		for (byte i=0; i<maxScores; i++)
			scoreLabels[i] = new JLabel("");
		readScoresFromFile();
		resetGame();
	}
	
	
	
	/* Singleton */
	private static GameLogic instance = new GameLogic();
	
	public static GameLogic getInstance() {
		if (instance == null)
            instance = new GameLogic();
        return instance;
	}
	
	
	
	/* Private Methods */
	private boolean checkBounds(   int player)  { return (player >= 1 && player <= maxPlayers);       }
	private int     getOtherPlayer(int player)  { return player == 1 ? 2 : 1;                         }
	private void    updatePlayerText()          { playerLabel.setText("Player Turn: " + playerTurn ); }
	private void    updateTurnText()            { turnLabel.setText(  "Turn: "        + getTurn());   }
	
	private void endGame() {
		if (highScores.size() == maxScores) {
			if (turn < highScores.get(maxScores-1).getTurns()) {
				String name = Main.getWindow().askName();
				if (name != null && name != "" && name.length() > 0)
					setScore(name, turn);
			}
		}
		else {
			String name = Main.getWindow().askName();
			if (name != null && name != "" && name.length() > 0)
				setScore(name, turn);
		}
		resetGame();
	}
	
	private void endTurn(boolean end) {
		turnEnded = end;
		endTurnButton.setEnabled(end);
	}
	
	private boolean checkBounds(int x, int y, int player) {
		if (x < 1 || x > maxGrid || y < 1 || y > maxGrid)
			return false;
		return checkBounds(player);
	}
	
	private void updateGameText() {
		if (gameStarted)
			gameLabel.setText("Game Started");
		else gameLabel.setText("Preparation Phase");
	}
	
	private void writeScoresToFile() {
		try {
			if (scoresFile.exists())
				scoresFile.delete();
			if (scoresFile.createNewFile()) {
				PrintWriter writer = new PrintWriter(new FileWriter(scoresFile));
				getScores().forEach((score) -> {
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
	
	private void readScoresFromFile() {
		if (!scoresFile.exists())
			return;
		
		ArrayList<String> lines = new ArrayList<String>();
		try {
			LineNumberReader reader = new LineNumberReader(new FileReader(scoresFile));
			String line;
			while ((line = reader.readLine()) != null)
				lines.add(line);
			reader.close();
			
			highScores = new ArrayList<HighScore>();
			updateScores();
			for (byte i=0; i<lines.size()/2; i++) {
				if (i >= maxScores)
					break;
				try { setScore(lines.get(i*2), Integer.parseInt(lines.get(i*2+1))); }
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
	
	
	
	/* Public Methods */
	public JLabel     getGameLabel()              { return gameLabel;                     }
	public JLabel     getPlayerLabel()            { return playerLabel;                   }
	public JLabel     getTurnLabel()              { return turnLabel;                     }
	public JLabel[]   getScoreLabels()            { return scoreLabels;                   }
	public JButton    getEndTurnButton()          { return endTurnButton;                 }
	public JButton    getRestartButton()          { return restartButton;                 }
	public JButton    getExitButton()             { return exitButton;                    }
	public byte       getMaxScores()              { return maxScores;                     }
	
	public  int       getPlayer()                 { return playerTurn;                    }
	public  int       getOtherPlayer()            { return playerTurn == 1 ? 2 : 1;       }
	public  boolean   isGameStarted()             { return gameStarted;                   }
	public  boolean   isTurnEnded()               { return turnEnded;                     }
	public  short     getTurn()                   { return turn;                          }
	public  boolean[] getAllPlayerShips()         { return ships[playerTurn];             }
	
	public int        shipsCount()                { return shipsCount(       playerTurn); }
	public boolean    shipsIntact()               { return shipsIntact(      playerTurn); }
	public int        coordsCount()               { return coordsCount(      playerTurn); }
	public int        tempCoordsCount()           { return tempCoordsCount(  playerTurn); }
	public int        horizontalRow()             { return horizontalRow(    playerTurn); }
	public int        verticalColumn()            { return verticalColumn(   playerTurn); }
	public void       makeShip()                  {        makeShip(         playerTurn); }
	public void       hitSpot(    int x, int y)   {        hitSpot(    x, y, playerTurn); }
	public boolean    isHit(      int x, int y)   { return isHit(      x, y, playerTurn); }
	public boolean    isShip(     int x, int y)   { return isShip(     x, y, playerTurn); }
	public boolean    isShipHit(  int x, int y)   { return isShipHit(  x, y, playerTurn); }
	public boolean    isTemp(     int x, int y)   { return isTemp(     x, y, playerTurn); }
	public void       placeShip(  int x, int y)   {        placeShip(  x, y, playerTurn); }
	public void       removeTemp( int x, int y)   {        removeTemp( x, y, playerTurn); }
	
	public void clearScores() {
		highScores = new ArrayList<HighScore>();
		updateScores();
		if (scoresFile.exists())
			scoresFile.delete();
	}
	
	public void setScore(String name, int turns) {
		highScores.add(new HighScore(name, turns));
		Collections.sort(highScores);
		if (highScores.size() > maxScores)
			highScores.remove(highScores.size()-1);
		updateScores();
		writeScoresToFile();
	}
	
	public void updateScores() {
		for (byte i=0; i<maxScores; i++) {
			if      (i >= highScores.size())      { scoreLabels[i].setText("Untitled Score"); }
			else if (highScores.get(i).isSet())   { scoreLabels[i].setText(highScores.get(i).getName() + ": " + highScores.get(i).getTurns()); }
			else                                  { scoreLabels[i].setText("Untitled Score"); }
		}
	}
	
	public  ArrayList<HighScore> getScores() { return highScores; }
	
	public void resetGame() {
		blastCoords = new boolean [maxPlayers][maxGrid][maxGrid];
		shipCoords  = new boolean [maxPlayers][maxGrid][maxGrid];
		tempCoords  = new boolean [maxPlayers][maxGrid][maxGrid];
		ships       = new boolean [maxPlayers][maxShips];
		gameStarted = false;
		playerTurn  = turn = 1;
		endTurn(false);
		
		updateGameText();
		updatePlayerText();
		updateTurnText();
	}
	
	public void startGame() {
		gameStarted = true;
		playerTurn  = turn = 1;
		endTurn(false);
		
		updateGameText();
		updatePlayerText();
	}
	
	public void switchPlayer() {
		if (!turnEnded)
			return;
		endTurn(false);
		playerTurn = playerTurn == 1 ? 2 : 1;
		updatePlayerText();
		if (playerTurn == 1 && gameStarted) {
			turn++;
			updateTurnText();
		}
		else if (!gameStarted && shipsCount(1) == 5 && shipsCount(2) == 5)
			startGame();
	}
	
	public boolean getShip(int player, int index) {
		if (index < 0 || index > maxShips || !checkBounds(player))
			return false;
		return ships[player-1][index];
	}
	
	public byte shipsCount(int player) {
		if (!checkBounds(player))
			return -1;
		
		byte count = 0;
		for (boolean ship : ships[player-1])
			if (ship)
				count++;
		return count;
	}
	
	public boolean shipsIntact(int player) {
		if (!checkBounds(player))
			return false;
		
		byte count = 0;
		for (int x=0; x<maxGrid; x++)
			for (int y=0; y<maxGrid; y++) {
				if (shipCoords[player-1][x][y]) {
					count++;
					if (blastCoords[getOtherPlayer(player)-1][x][y])
						count--;
				}
			}
		
		return count > 0;
	}
	
	public int coordsCount(int player) {
		if (!checkBounds(player))
			return -1;
		
		int count = 0;
		for (int x=0; x<maxGrid; x++)
			for (int y=0; y<maxGrid; y++)
				if (shipCoords[player-1][x][y])
					count++;
		return count;
	}
	
	public int tempCoordsCount(int player) {
		if (!checkBounds(player))
			return -1;
		
		int count = 0;
		for (int x=0; x<maxGrid; x++)
			for (int y=0; y<maxGrid; y++)
				if (tempCoords[player-1][x][y])
					count++;
		return count;
	}
	
	public int horizontalRow(int player) {
		if (!checkBounds(player))
			return -1;
		if (tempCoordsCount(player) <= 1)
			return 0;
		
		for (int x=0; x<maxGrid; x++) {
			for (int y=0; y<maxGrid; y++) {
				if (tempCoords[player-1][x][y]) {
					if (x < 9)
						if (tempCoords[player-1][x+1][y])
							return y + 1;
					if (y > 0)
						if (tempCoords[player-1][x-1][y])
							return y + 1;
				}
			}
		}
		
		return 0;
	}
	
	public int verticalColumn(int player) {
		if (!checkBounds(player))
			return -1;
		if (tempCoordsCount(player) <= 1)
			return 0;
		
		for (int x=0; x<maxGrid; x++)
			for (int y=0; y<maxGrid; y++)
				if (tempCoords[player-1] [x][y]) {
					if (x < 9)
						if (tempCoords[player-1][x][y+1])
							return x + 1;
					if (y > 0)
						if (tempCoords[player-1][x][y-1])
							return x + 1;
				}
		
		return 0;
	}
	
	public void makeShip(int player) {
		if (!checkBounds(player) || turnEnded || gameStarted)
			return;
		int index = -1;
		
		if (tempCoordsCount(player) == 2 && !ships[player-1][0])
			index = 0;
		if (tempCoordsCount(player) == 3 && !ships[player-1][1])
			index = 1;
		else if (tempCoordsCount(player) == 3 && !ships[player-1][2])
			index = 2;
		else if (tempCoordsCount(player) == 4 && !ships[player-1][3])
			index = 3;
		else if (tempCoordsCount(player) == 5 && !ships[player-1][4])
			index = 4;
		
		if (index == -1)
			return;
		
		ships[player-1][index] = true;
		endTurn(true);
		for (int x=0; x<maxGrid; x++)
			for (int y=0; y<maxGrid; y++)
				if (tempCoords[player-1][x][y]) {
					tempCoords[player-1][x][y] = false;
					shipCoords[player-1][x][y] = true;
				}
	}
	
	public void hitSpot(int x, int y, int player) {
		if (!checkBounds(x, y, player) || turnEnded || !gameStarted)
			return;
		if (!blastCoords[player-1][x-1][y-1]) {
			blastCoords[player-1][x-1][y-1] = true;
			if (!shipsIntact(getOtherPlayer(player)))
				endGame();
			else endTurn(true);
		}
	}
	
	public boolean isHit(int x, int y, int player) {
		if (!checkBounds(x, y, player))
			return false;
		return blastCoords[player-1][x-1][y-1];
	}
	
	public boolean isShip(int x, int y, int player) {
		if (!checkBounds(x, y, player))
			return false;
		return shipCoords[player-1][x-1][y-1];
	}
	
	public boolean isShipHit(int x, int y, int player) {
		if (!checkBounds(x, y, player))
			return false;
		return isShip(x, y, player) && isHit(x, y, getOtherPlayer());
	}
	
	public boolean isTemp(int x, int y, int player) {
		if (!checkBounds(x, y, player))
			return false;
		return tempCoords[player-1][x-1][y-1];
	}
	
	public void placeShip(int x, int y, int player) {
		if (!checkBounds(x, y, player) || turnEnded || gameStarted)
			return;
		if (gameStarted)
			return;
		
		if (tempCoordsCount(player) >= 5 || shipsCount(player) == 5 || isShip(x, y, player) || isTemp(x, y, player))
			return;
		else if (tempCoordsCount(player) == 0) {
			if (!isShip(x, y, player) && !isTemp(x, y, player))
				tempCoords[player-1][x-1][y-1] = true;
		}
		else if (!isTemp(x+1, y, player) && !isTemp(x-1, y, player) && !isTemp(x, y+1, player) && !isTemp(x, y-1, player))
			return;
		else if (horizontalRow(player) == 0 && verticalColumn(player) == 0)
			tempCoords[player-1][x-1][y-1] = true;
		else if (horizontalRow(player) == y || verticalColumn(player) == x)
			tempCoords[player-1][x-1][y-1] = true;
	}
	
	public void removeTemp(int x, int y, int player) {
		if (!checkBounds(x, y, player))
			return;
		tempCoords[player-1][x-1][y-1] = false;
	}
	
}
