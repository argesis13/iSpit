package iSpit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Class used for creating bullet instances. A bullet hurts when touched.
 * Ouch!
 * 
 * @author Dan-Eduard Petrescu
 *
 */
public class Bullet implements Commons {
	
	// INSTANCE VARIABLES
		// images
	private transient Image bullet;
		// position
	private int x;
	private int y;
	private Direction direction;
		// speed
	private int dx;
	private int dy;
	
	// EMPTY CONSTRUCTOR -> can't create static arrayList without this one ?!?
	/**
	 * Creates a generic bullet object with no direction, image or speed
	 */
	public Bullet() {}

	// CONSTRUCTOR
	/**
	 * Creates an instance of brick in a specified place and facing a 
	 * specified direction
	 * @param x the bullet position in pixels on horizontal axis from left to right
	 * @param y the bullet position in pixels on vertical axis from top to bottom
	 * @param dir the direction the bullet is facing
	 */
	public Bullet(int x, int y, Direction dir) {	
		this.x = x;
		this.y = y;
		direction = dir;
		// setting the speed
		dx = 16;
		dy = 16;
		
		// loading the image according to the direction facing
		switch(direction) {
			case UP:
			case DOWN:
				URL bullet1 = iSpit.class.getResource(
		                "/Bullet_V.png");
				bullet = new ImageIcon(bullet1).getImage();

				break;
	
			case RIGHT:
			case LEFT:
				URL bullet2 = iSpit.class.getResource(
		                "/Bullet_H.png");
				bullet = new ImageIcon(bullet2).getImage();
				break;
				
			default:
				System.out.println("ERROR! No such direction!");
				break;
		}
	}
	
	// DRAW METHOD
	/**
	 * Draws the bullet image to the specified coordinates
	 * @param g graphics context
	 */
	public void draw(Graphics2D g) {
		switch(direction){
			case UP:
				g.drawImage(bullet, x, y, BULLET_HEIGHT, BULLET_WIDTH, null);
				break;
			case DOWN:
				g.drawImage(bullet, x, y, BULLET_HEIGHT, BULLET_WIDTH, null);
				break;
			case RIGHT:
				g.drawImage(bullet, x, y, BULLET_WIDTH, BULLET_HEIGHT, null);
				break;
			case LEFT:
				g.drawImage(bullet, x, y, BULLET_WIDTH, BULLET_HEIGHT, null);
				break;
			default:
				System.out.println("ERROR! No such direction!");
				break;
		}
	}

	// UPDATE METHOD
	/**
	 * Updates the bullet movement checking if reaching the end 
	 * of the canvas
	 * @return a boolean if reached the end or not
	 */
	public boolean update() { 
		if(direction == Direction.UP) {
			y -= dy;
			if(y < -BULLET_HEIGHT || y > PANEL_HEIGHT + BULLET_HEIGHT)
				return true;
		}
		if(direction == Direction.DOWN) {
			y += dy;
			if(y < -BULLET_HEIGHT || y > PANEL_HEIGHT + BULLET_HEIGHT)
				return true;
		}
		if(direction == Direction.LEFT) {
			x -= dx;
			if(x < -BULLET_WIDTH || x > PANEL_WIDTH + BULLET_WIDTH)
				return true;
		}
		if(direction == Direction.RIGHT) {
			x += dx;
			if(x < -BULLET_WIDTH || x > PANEL_WIDTH + BULLET_WIDTH)
				return true;
		}
		return false;
	}
	
	// GETTERS
	/**
	 * Get the current x axis position
	 * @return the current bullet position in pixels on horizontal axis 
 				from left to right 
	 */
	public int getX() {	return x; }
	
	/**
	 * Get the current y axis position
	 * @return the current tank position in pixels on vertical axis 
 				from top to bottom 
	 */
	public int getY() {	return y; }
}
