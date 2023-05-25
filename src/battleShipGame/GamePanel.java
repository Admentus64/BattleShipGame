package battleShipGame;



/* Java Imports */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/* Swing Imports */
import javax.swing.JPanel;



// Game Panel Class
public class GamePanel extends JPanel implements MouseListener {
	/* Private Variables */
	private static final long serialVersionUID = 1L;
	private GameLogic logic                    = GameLogic.getInstance();
	
	
	
	/* Constructor */
	public GamePanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(552, 552));
		addMouseListener(this);
	}
	
	
	
	/* Public Methods */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        
		int xPos, yPos;
        int width	= 50;
        int height	= 50;
        
        Font f = new Font("bold", Font.PLAIN, 20);
        g.setFont(f);
        
        //Draw squares, letters and digits, ships, hits and misses
        for (byte x=0; x<=10; x++) {
        	xPos = x * 50 + 1;
        	if (x > 0) {
        		g.setColor(Color.black);
        		g.drawString(String.valueOf((char)(x + 64)), xPos + 18, 35); // Letters
        	}
        	
        	for (byte y=0; y<=10; y++) {
        		yPos = y * 50 + 2;
        		g.setColor(Color.black);
        		g.drawRect(xPos, yPos, width, height); // Draw Rectangles
        		
        		// Continue if at square 0,0
        		if (x == 0 || y == 0)
        			continue;
        		
        		// Draw digits
        		if (y < 10)
            		 g.drawString("" + y, 20, yPos + 32); // Draw single digits
        		else g.drawString("" + y, 13, yPos + 32); // Draw double digits
        		
        		// Draw ships, hits and misses
        		if (logic.isShip(x, y)) { // Draw ships
    				g.setColor(logic.getPlayer() == 1 ? Color.gray : Color.lightGray);
    				g.fillRect(50*x+2, 50*y+3, 48, 48);
    				if (logic.isShipHit(x, y)) { // Draw hit ships
    					g.setColor(Color.orange);
        				g.fillRect(50*x+8, 50*y+9, 35, 35);
    				}
    			}
    			else if (logic.isTemp(x, y)) { // Draw temporary placements
    				g.setColor(Color.green);
    				g.fillRect(50*x+2, 50*y+3, 48, 48);
    			}
    			
    			if (logic.isHit(x, y)) { // Draw hits and misses
    				if (!logic.isShip(x, y, logic.getOtherPlayer())) { // Draw misses
    					g.setColor(Color.red);
    					g.drawLine(50*x+3,  50*y+4, 50*x+49, 50*y+50);
    					g.drawLine(50*x+49, 50*y+4, 50*x+3,  50*y+2+48);
    				}
    				else if (logic.isShip(x, y, logic.getOtherPlayer())) { // Draw hits
    					g.setColor(Color.green);
    					g.drawOval(50*x+3, 50*y+3, 47, 47);
    				}
    			}
        	}
        }
    }
	
	
	
	/* Mouse Event Methods */
	@Override
	public void mousePressed(MouseEvent e) {
		byte x = (byte) (e.getX() / 50);
		byte y = (byte) (e.getY() / 50);
		
		if (x < 1 || y < 1 || x > 10 || y > 10)
			return;
		
		if (!logic.isGameStarted()) {
			switch (e.getButton() ) {
				case MouseEvent.BUTTON1:
					if (logic.isTemp(x, y))
						logic.makeShip();
					else logic.placeShip(x, y);
					break;
				case MouseEvent.BUTTON3:
					logic.removeTemp(x, y);
					break;
			}
		}
		else {
			if (e.getButton() == MouseEvent.BUTTON1)
				logic.hitSpot(x, y);
		}
		
		repaint();
	}
	
	@Override public void mouseClicked( MouseEvent e)   { }
	@Override public void mouseReleased(MouseEvent e)   { }
	@Override public void mouseEntered( MouseEvent e)   { }
	@Override public void mouseExited(  MouseEvent e)   { }
	
}
