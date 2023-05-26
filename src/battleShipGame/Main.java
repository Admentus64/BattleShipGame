package battleShipGame;



/* Main Class */
public class Main {
	/* Private Variables */
	private static GameWindow gameWindow;
	
	
	
	/* Main Method */
	public static void main(String[] args) {
		gameWindow = new GameWindow("BattleShip Game"); // Generates new game window (generates game) when executed
	}
	
	
	
	/* Public Static Methods */
	public static boolean hasWindow()      { return gameWindow != null; } // Check if the game window is loaded, which is a static
	public static GameWindow getWindow()   { return gameWindow;         } // Return the game window, which is a static
	
	public static boolean isNumeric(String strNum) { // Confirm if a string is a number value
	    if (strNum == null)
	        return false;
	    try                                 { Double.parseDouble(strNum);}
	    catch (NumberFormatException nfe)   { return false;              }
	    return true;
	}
}
