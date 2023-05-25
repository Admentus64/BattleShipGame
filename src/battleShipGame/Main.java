package battleShipGame;



// Main Class
public class Main {
	/* Private Variables */
	private static GameWindow gameWindow;
	
	
	
	/* Main Method */
	public static void main(String[] args) {
		// Add new game window (generates game)
		gameWindow = new GameWindow("BattleShip Game");
	}
	
	
	
	/* Public Methods */
	public static GameWindow getWindow() { return gameWindow; }
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null)
	        return false;
	    try                                 { Double.parseDouble(strNum);}
	    catch (NumberFormatException nfe)   { return false;              }
	    return true;
	}
}
