package battleShipGame;



/* Java Imports */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/* Swing Imports (GUI) */
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;



/* GUI Window Class */
public class GameWindow extends JFrame {
	/* Private Variables */
	private static final long serialVersionUID = 1L;
	private final GameLogic     logic          = GameLogic.getInstance();
	
	private final GamePanel gamePanel          = new GamePanel();
	private final JPanel    uiPanel            = new JPanel();
	
	
	
	/* Constructor */
	public GameWindow(String title) {
		// Add window
		super(title);
		setLayout(new FlowLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		// Set UI panel
		uiPanel.setLayout(new GridLayout(16, 1, 10, 10));
		uiPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		setLabel( uiPanel, logic.getGameLabel(),     190, 20, Color.BLACK);
		setLabel( uiPanel, logic.getPlayerLabel(),   190, 20, Color.BLACK);
		setLabel( uiPanel, logic.getTurnLabel(),     190, 20, Color.BLACK);
		setButton(uiPanel, logic.getEndTurnButton(), 100, 40, Color.BLACK, null);
		setButton(uiPanel, logic.getRestartButton(), 100, 40, Color.BLACK, null);
		setButton(uiPanel, logic.getExitButton(),    100, 40, Color.BLACK, null);
		
		// Add score labels to the GUI
		for (JLabel label : logic.getScoreLabels())
			setLabel(uiPanel, label, 200, 20, Color.BLACK);
		
		// Set event for keyboard buttons
		addKeyListener(new KeyListener() {
			@Override public void keyPressed(KeyEvent e) { // Keyboard button functions to be called
				if      (e.getKeyCode() == KeyEvent.VK_ENTER)    { endTurn();           } // Enter
				else if (e.getKeyCode() == KeyEvent.VK_SPACE)    { restart();           } // Space
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)   { dispose();           } // Escape
				else if (e.getKeyCode() == KeyEvent.VK_F5)       { prepareTestSetup1(); } // F5
				else if (e.getKeyCode() == KeyEvent.VK_F6)       { prepareTestSetup2(); } // F6
				else if (e.getKeyCode() == KeyEvent.VK_F7)       { prepareTestSetup3(); } // F7
				else if (e.getKeyCode() == KeyEvent.VK_F8)       { prepareTestSetup4(); } // F8
			}
			
			// Keyboard Button events which aren't used, but still required to include
			@Override public void keyTyped(   KeyEvent e)   { }
			@Override public void keyReleased(KeyEvent e)   { }
		});
		
		// Set event for buttons
		logic.getEndTurnButton().addActionListener(new ActionListener()   { @Override public void actionPerformed(ActionEvent e) { endTurn(); } });
		logic.getRestartButton().addActionListener(new ActionListener()   { @Override public void actionPerformed(ActionEvent e) { restart(); } });
		logic.getExitButton().addActionListener(   new ActionListener()   { @Override public void actionPerformed(ActionEvent e) { dispose(); } });
		
		// Add panels
		add(gamePanel);
		add(uiPanel);
		
		// Show window
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	
	
	/* Public Methods */
	public String askName() { // Ask for a name
		String name = JOptionPane.showInputDialog(this, "Name:", null);
		return name;
	}
	
	
	
	/* Private Methods */
	private void endTurn() { // End Turn button for ending the turn and refreshing the GUI
		logic.switchPlayer();
        repaint();
	}
	
	private void restart() { // Restart button for restarting the game and refreshing the GUI
		logic.resetGame();
        repaint();
	}
	
	private void setButton(JPanel panel, JButton button, int width, int height, Color fore, Color back) { // Add a new button to a panel
		panel.add(button);
		button.setSize(width, height);
		button.setFocusable(false);
		button.setForeground(fore);
        if (back != null)
        	button.setBackground(back);
    }
	
	private void setLabel(JPanel panel, JLabel label, int width, int size, Color color) { // Add a new text label to a panel
		panel.add(label);
		label.setPreferredSize(new Dimension(width, 10));
		label.setFont(new Font("Verdana", Font.PLAIN, size));
		label.setFocusable(false);
		label.setForeground(color);
    }
	
	
	
	/* Private Methods (Testing) */
	private void prepareTestSetup4() { // Testing function to reset all scores
		System.out.println("Testing deleting highscores");
		logic.clearScores();
	}
	
	private void prepareTestSetup3() { // Testing function to preset the score list
		System.out.println("Testing adding highscores");
		
		logic.clearScores();
		logic.setScore("Ben",    110);
		logic.setScore("Bob",    110);
		logic.setScore("Erik",   90);
		logic.setScore("Bert",   80);
		logic.setScore("Jakob",  70);
		logic.setScore("Kevin",  100);
		logic.setScore("Rybak",  50);
		logic.setScore("Björn",  30);
		logic.setScore("Brutus", 30);
		logic.setScore("Anders", 40);
	}
	
	private void prepareTestSetup2() { // Testing function for instantly winning the game
		System.out.println("Testing winning the game");
		
		prepareTestSetup1();
		
		for (byte x=1; x<=10; x++)
			for (byte y=1; y<=10; y++)
				for (byte i=0; i<2; i++) {
					logic.hitSpot(x, y);
					logic.switchPlayer();
				}
		repaint();
	}
	
	private void prepareTestSetup1() { // Testing function for setting up game ready to be played
		System.out.println("Testing setting up the game");
		
		logic.resetGame();
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(1, 1);
			logic.placeShip(2, 1);
			logic.makeShip();
			logic.switchPlayer();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(2, 3);
			logic.placeShip(3, 3);
			logic.placeShip(4, 3);
			logic.makeShip();
			logic.switchPlayer();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(5, 6);
			logic.placeShip(6, 6);
			logic.placeShip(7, 6);
			logic.makeShip();
			logic.switchPlayer();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(1, 8);
			logic.placeShip(2, 8);
			logic.placeShip(3, 8);
			logic.placeShip(4, 8);
			logic.makeShip();
			logic.switchPlayer();
		}
		
		for (byte i=0; i<2; i++) {
			logic.placeShip(10, 10);
			logic.placeShip(10, 9);
			logic.placeShip(10, 8);
			logic.placeShip(10, 7);
			logic.placeShip(10, 6);
			logic.makeShip();
			logic.switchPlayer();
		}
		
		repaint();
	}
}
