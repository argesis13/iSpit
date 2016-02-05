package iSpit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Class used for creating brick instances.
 * A brick is a graphical representation of an
 * wall in the game
 *
 * @author Dan-Eduard Petrescu
 *
 */
public class Brick implements Commons {

	// INSTANCE VARIABLES
		// images
	private transient Image brick;
		// position
	private int x;
	private int y;
	
	/**
	 * Creates an instance of brick in a specified place
	 * @param x the brick position in pixels on horizontal axis from left to right
	 * @param y the brick position in pixels on vertical axis from top to bottom
	 */
	public Brick(int x, int y) {
		this.x = x;
		this.y = y;
		URL url = iSpit.class.getResource(
                "/Brick.png");
		brick = new ImageIcon(url).getImage();
	}
	
	/**
	 * Draws the brick image to the specified coordinates
	 * @param g graphics context
	 */
	public void drawBrick(Graphics2D g) {
		g.drawImage(brick, x, y, BRICK_WIDTH, BRICK_HEIGHT, null);
	}

	// GETTERS
	/**
	 * Get the current x axis position
	 * @return the current brick position in pixels on horizontal axis
	 * 			from left to right 
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the current y axis position
	 * @return the current brick position in pixels on vertical axis
	 * 			from top to bottom 
	 */
	public int getY() {
		return y;
	}

	// SETTERS
	/**
	 * Sets the current brick position in pixels on horizontal axis
	 * from left to right
	 * @param x representing the number of pixels
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/** 
	 * Sets the current brick position in pixels on vertical axis
	 * 	from top to bottom
	 * @param y representing the number of pixels
	 */
	public void setY(int y) {
		this.y = y;
	}
	

}
