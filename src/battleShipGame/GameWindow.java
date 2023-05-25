package battleShipGame;



// Java Imports
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

// Swing Imports
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;



// GUI Window Class
public class GameWindow extends JFrame {
	/* Private Variables */
	private static final long serialVersionUID = 1L;
	private GameLogic     logic                = GameLogic.getInstance();
	
	private GamePanel gamePanel                = new GamePanel();
	private JPanel    uiPanel                  = new JPanel();
	
	
	
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
		
		// Scores and names
		for (JLabel label : logic.getScoreLabels())
			setLabel(uiPanel, label, 200, 20, Color.BLACK);
		
		// Set event for keyboard buttons
		addKeyListener(new KeyListener() {
			@Override public void keyPressed(KeyEvent e) {
				if      (e.getKeyCode() == KeyEvent.VK_ENTER)    { endTurn();           }
				else if (e.getKeyCode() == KeyEvent.VK_SPACE)    { restart();           }
				else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)   { dispose();           }
				else if (e.getKeyCode() == KeyEvent.VK_F5)       { prepareTestSetup1(); }
				else if (e.getKeyCode() == KeyEvent.VK_F6)       { prepareTestSetup2(); }
				else if (e.getKeyCode() == KeyEvent.VK_F7)       { prepareTestSetup3(); }
				else if (e.getKeyCode() == KeyEvent.VK_F8)       { prepareTestSetup4(); }
			}
			@Override public void keyTyped(   KeyEvent e)   { }
			@Override public void keyReleased(KeyEvent e)   { }
		});
		
		addWindowListener (new WindowListener() {
			@Override public void windowOpened(WindowEvent e)        { }
			@Override public void windowClosing(WindowEvent e)       { }
			@Override public void windowClosed(WindowEvent e)        { }
			@Override public void windowIconified(WindowEvent e)     { }
			@Override public void windowDeiconified(WindowEvent e)   { }
			@Override public void windowActivated(WindowEvent e)     { }
			@Override public void windowDeactivated(WindowEvent e)   { }
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
	public JFrame getWindow() { return this; }
	
	public String askName() {
		String name = JOptionPane.showInputDialog(this, "Name:", null);
		return name;
	}
	
	private void endTurn() {
		logic.switchPlayer();
        repaint();
	}
	
	private void restart() {
		logic.resetGame();
        repaint();
	}
	
	private void setButton(JPanel panel, JButton button, int width, int height, Color fore, Color back) {
		panel.add(button);
		button.setSize(width, height);
		button.setFocusable(false);
		button.setForeground(fore);
        if (back != null)
        	button.setBackground(back);
    }
	
	private void setLabel(JPanel panel, JLabel label, int width, int size, Color color) {
		panel.add(label);
		label.setPreferredSize(new Dimension(width, 10));
		label.setFont(new Font("Verdana", Font.PLAIN, size));
		label.setFocusable(false);
		label.setForeground(color);
    }
	
	/* Private Methods (Testing) */
	private void prepareTestSetup4() {
		System.out.println("Testing deleting highscores");
		logic.clearScores();
	}
	
	private void prepareTestSetup3() {
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
	
	private void prepareTestSetup2() {
		System.out.println("Testing winning the game");
		
		prepareTestSetup1();
		
		for (byte x=1; x<=10; x++)
			for (byte y=1; y<=10; y++)
				for (byte player=1; player<=2; player++) {
					logic.hitSpot(x, y, player);
					logic.switchPlayer();
				}
		repaint();
	}
	
	private void prepareTestSetup1() {
		System.out.println("Testing setting up the game");
		
		logic.resetGame();
		
		logic.placeShip(1,  1,  1);
		logic.placeShip(2,  1,  1);
		logic.makeShip(1);
		logic.switchPlayer();
		
		logic.placeShip(1,  1,  2);
		logic.placeShip(2,  1,  2);
		logic.makeShip(2);
		logic.switchPlayer();
		
		logic.placeShip(2,  3,  1);
		logic.placeShip(3,  3,  1);
		logic.placeShip(4,  3,  1);
		logic.makeShip(1);
		logic.switchPlayer();
		
		logic.placeShip(2,  3,  2);
		logic.placeShip(3,  3,  2);
		logic.placeShip(4,  3,  2);
		logic.makeShip(2);
		logic.switchPlayer();
		
		logic.placeShip(5,  6,  1);
		logic.placeShip(6,  6,  1);
		logic.placeShip(7,  6,  1);
		logic.makeShip(1);
		logic.switchPlayer();
		
		logic.placeShip(5,  6,  2);
		logic.placeShip(6,  6,  2);
		logic.placeShip(7,  6,  2);
		logic.makeShip(2);
		logic.switchPlayer();
		
		logic.placeShip(1,  8,  1);
		logic.placeShip(2,  8,  1);
		logic.placeShip(3,  8,  1);
		logic.placeShip(4,  8,  1);
		logic.makeShip(1);
		logic.switchPlayer();
		
		logic.placeShip(1,  8,  2);
		logic.placeShip(2,  8,  2);
		logic.placeShip(3,  8,  2);
		logic.placeShip(4,  8,  2);
		logic.makeShip(2);
		logic.switchPlayer();
		
		logic.placeShip(10, 10, 1);
		logic.placeShip(10, 9,  1);
		logic.placeShip(10, 8,  1);
		logic.placeShip(10, 7,  1);
		logic.placeShip(10, 6,  1);
		logic.makeShip(1);
		logic.switchPlayer();
		
		logic.placeShip(10, 10, 2);
		logic.placeShip(10, 9,  2);
		logic.placeShip(10, 8,  2);
		logic.placeShip(10, 7,  2);
		logic.placeShip(10, 6,  2);
		logic.makeShip(2);
		logic.switchPlayer();
		
		repaint();
	}
	
}
