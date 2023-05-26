package battleShipGame.Tests;


/* Class Imports */
import battleShipGame.GameLogic;

/* JUnit Imports */
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertNotEquals;



/* JUnit Test Class */
class BattleShipTest extends TestCase {
	/* Private Variables */
	private final GameLogic logic = GameLogic.getInstance();
	
	
	
	/* Private Functions */
	private void makeShipAndEndTurn() { // Short method to make a ship and then switch the player
		logic.makeShip();
		logic.switchPlayer();
	}
	
	private void placeShips() { // Quick method to fill the grid with ships for both players
		for (byte i=0; i<2; i++) {
			logic.placeShip(1, 1);
			logic.placeShip(2, 1);
			makeShipAndEndTurn();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(2, 3);
			logic.placeShip(3, 3);
			logic.placeShip(4, 3);
			makeShipAndEndTurn();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(5, 6);
			logic.placeShip(6, 6);
			logic.placeShip(7, 6);
			makeShipAndEndTurn();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(1, 8);
			logic.placeShip(2, 8);
			logic.placeShip(3, 8);
			logic.placeShip(4, 8);
			makeShipAndEndTurn();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(10, 10);
			logic.placeShip(10, 9);
			logic.placeShip(10, 8);
			logic.placeShip(10, 7);
			logic.placeShip(10, 6);
			makeShipAndEndTurn();
		}
	}
	
	
	
	/* Initialization before each test case function */
	@BeforeEach
	public void setUp() {
		logic.clearScores(); // Clear out the scores
		logic.resetGame();   // Use default values
	}
	
	
	
	/* Test Case Functions */
	@Test
	void startMatch() { // Test starting a match
		// Confirm game hasn't been started yet, fill the grid, start the game and confirm the game is started
		assertFalse(logic.isGameStarted());
		placeShips();
		assertTrue(logic.isGameStarted());
		
		// Test the amount of ships and ship parts for both players, and if their ship parts are still intact
		assertEquals(logic.getShipsTotal(),  5,  1); // 5 ships
		assertEquals(logic.getShipsCount(),  17, 1); // 2 + 3 + 3 + 4 + 5
		assertEquals(logic.getIntactParts(), 17, 1); // 2 + 3 + 3 + 4 + 5
		assertEquals(logic.getShipsTotal(),  5,  2);
		assertEquals(logic.getShipsCount(),  17, 2);
		assertEquals(logic.getIntactParts(), 17, 2);
	}
	
	@Test
	void endMatch() { // Test ending a match by winning
		placeShips();
		int turn = logic.getTurn();
		assertEquals(turn, 1); // Confirm this is turn 1
		
		// Hit all spots in succession
		for (byte x=1; x<=10; x++)
			for (byte y=1; y<=10; y++) {
				for (byte i=0; i<2; i++) {
					logic.hitSpot(x, y);
					logic.switchPlayer();
				}
				if (logic.isGameStarted()) // Keep track of turns as long the game is going on
					turn = logic.getTurn();
			}
		
		// Confirm match ended, should have ended with a 100 turns (given ship positions and firing in order)
		assertEquals(turn, 100);
		assertFalse(logic.isGameStarted());
	}
	
	@Test
	void addScores() { // Testing adding new scores
		// Confirm no scores are set
		assertEquals(logic.getScores().size(), 0);
		
		// Add a new score and confirm it
		logic.setScore("A", 10);
		assertEquals(logic.getScores().size(), 1);
		assertEquals(logic.getScores().get(0).getName(),  "A");
		assertEquals(logic.getScores().get(0).getTurns(), 10);
		
		// Add 10 new scores
		logic.setScore("B", 20);
		logic.setScore("C", 21);
		logic.setScore("D", 22);
		logic.setScore("E", 23);
		logic.setScore("F", 24);
		logic.setScore("G", 25);
		logic.setScore("H", 26);
		logic.setScore("I", 27);
		logic.setScore("J", 28);
		logic.setScore("K", 29);
		
		// Only 10 scores, and not 11
		assertEquals(logic.getMaxScores(),     10);
		assertEquals(logic.getScores().size(), logic.getMaxScores());
		
		// Check lowest and highest scores (K with 29 didn't make it
		assertEquals(logic.getScores().get(0).getName(),  "A");
		assertEquals(logic.getScores().get(0).getTurns(), 10);
		assertEquals(logic.getScores().get(logic.getMaxScores()-1).getName(),  "J");
		assertEquals(logic.getScores().get(logic.getMaxScores()-1).getTurns(), 28);
		
		// Add new score, the previous lowest score J should be removed
		logic.setScore("L", 15);
		assertEquals(logic.getScores().get(0).getName(),  "A");
		assertEquals(logic.getScores().get(0).getTurns(), 10);
		assertEquals(logic.getScores().get(logic.getMaxScores()-1).getName(),  "I");
		assertEquals(logic.getScores().get(logic.getMaxScores()-1).getTurns(), 27);
	}
	
	@Test
	void hitShip() { // Test having successfully hit a ship
		placeShips();
		
		// Hit a ship, confirm the spot is hit, switch player and then confirm his/her ship got hit
		logic.hitSpot(1, 1);
		assertTrue(logic.isHit(1, 1));
		logic.switchPlayer();
		assertTrue(logic.isShipHit(1, 1));
		assertEquals(logic.getIntactParts(), 16); // Lost one ship part, so 17 - 1
	}
	
	@Test
	void missHitShip() { // Test missing a shot
		placeShips();
		
		// Miss hitting a ship, confirm the spot is hit, switch player and then confirm his/her ship didn't get hit at that spot
		logic.hitSpot(1, 2);
		assertTrue(logic.isHit(1, 2));
		logic.switchPlayer();
		assertFalse(logic.isShipHit(1, 1));
		assertEquals(logic.getIntactParts(), 17); // Didn't lose any ship part
	}
	
	@Test
	void hitShipButMisconfirm() { // Test having successfully hit a ship but confirming the wrong location
		placeShips();
		
		// Hit a ship, but confirm a different spot which is not hit
		logic.hitSpot(2, 1);
		assertTrue(logic.isHit(2, 1));
		logic.switchPlayer();
		assertFalse(logic.isShipHit(1, 1));       // This spot was not hit
		assertFalse(logic.isShipHit(2, 2));       // There is no ship here
		assertEquals(logic.getIntactParts(), 16); // Still lost a ship part
	}
	
	@Test
	void switchPlayer() { // Test switching the player after having placed a ship
		// Confirm default player
		assertEquals(logic.getPlayer(),         1);
		assertEquals(logic.getOtherPlayer(),    2);
		
		logic.switchPlayer();
		
		// Can not switch before having made an action
		assertNotEquals(logic.getPlayer(),      2);
		assertNotEquals(logic.getOtherPlayer(), 1);
		
		// Do action, then switch player
		logic.placeShip(1, 1);
		logic.placeShip(1, 2);
		makeShipAndEndTurn();
		
		// Player is switched now
		assertEquals(logic.getPlayer(),         2);
		assertEquals(logic.getOtherPlayer(),    1);
	}
	
	@Test
	void validMakeShip() { // Test if a ship can be made
		// Confirm no ships are made
		assertEquals(logic.getShipsTotal(), 0);
		
		// Place ship
		logic.placeShip(1, 1);
		logic.placeShip(2, 1);
		logic.placeShip(4, 1);
		
		// Confirm placements are placed
		assertTrue( logic.isTemp(1, 1));
		assertTrue( logic.isTemp(2, 1));
		assertFalse(logic.isTemp(4, 1));
		
		// Make the ship
		logic.makeShip();
		
		// Confirm the ship is permanently placed
		assertTrue( logic.isShip(1, 1));
		assertTrue( logic.isShip(2, 1));
		assertFalse(logic.isShip(4, 1));
		
		// Confirm one ship is present
		assertEquals(logic.getShipsTotal(), 1);
	}
	
	@Test
	void inValidMakeShip() { // Test making ships which aren't allowed
		// Confirm no ships are made
		assertEquals(logic.getShipsTotal(), 0);
		
		// Place and make ship
		logic.placeShip(1, 1);
		logic.placeShip(2, 1);
		logic.makeShip();
		
		// Confirm ship is placed by having a total count of 1
		assertTrue(logic.isShip(1, 1));
		assertTrue(logic.isShip(2, 1));
		assertEquals(logic.getShipsTotal(), 1);
		
		// Place and make the ship again
		logic.placeShip(1, 1);
		logic.placeShip(2, 1);
		logic.makeShip();
		
		// Confirm ship count hasn't been changed
		assertEquals(logic.getShipsTotal(), 1);
		
		// Place and make the ship once more, but at a different location
		logic.placeShip(3, 1);
		logic.placeShip(4, 1);
		logic.placeShip(6, 1); // Invalid
		logic.placeShip(4, 2); // Invalid
		logic.makeShip(); // Ship can't be made because a) The third and fourth spots aren't valid, b) Size of 2 is already placed
		
		// Confirm ship count hasn't been changed
		assertEquals(logic.getShipsTotal(), 1);
	}
}
