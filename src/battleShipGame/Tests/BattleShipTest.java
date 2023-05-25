package battleShipGame.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import battleShipGame.GameLogic;
import junit.framework.TestCase;

class BattleShipTest extends TestCase {
	GameLogic logic = GameLogic.getInstance();
	
	@BeforeEach
	public void setUp() {
		logic.resetGame();
	}
	
	@Test
	void testValidMakeShip() {
		logic.placeShip(1, 1, 1);
		logic.placeShip(2, 1, 1);
		logic.placeShip(4, 1, 1);
		
		assertTrue( logic.isTemp(1, 1));
		assertTrue( logic.isTemp(2, 1));
		assertFalse(logic.isTemp(4, 1));
		
		logic.makeShip();
		
		assertTrue( logic.isShip(1, 1));
		assertTrue( logic.isShip(2, 1));
		assertFalse(logic.isShip(4, 1));
	}
	
	@Test
	void testInValidMakeShip() {
		logic.placeShip(1, 1);
		logic.placeShip(2, 1);
		logic.makeShip();
		
		assertTrue(logic.isShip(1, 1));
		assertTrue(logic.isShip(2, 1));
		assertEquals(logic.shipsCount(), 1);
		
		logic.placeShip(1, 1);
		logic.placeShip(2, 1);
		logic.makeShip();
		
		assertEquals(logic.shipsCount(), 1);
	}
}
