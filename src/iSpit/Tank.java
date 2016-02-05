package iSpit;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;
import java.net.URL;

import javax.swing.ImageIcon;

/**
 * Class used for creating tank instances.
 * A tank is a graphical representation of a player 
 * and can be controlled by the user
 * @author Dan-Eduard Petrescu
 *
 */
public class Tank implements Commons, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6855468575991249376L;
	
	// INSTANCE VARIABLES
	/** Tank Images corresponding to different directions */
	private transient Image tankUp, tankDown, tankLeft, tankRight;
	
		// coordinates
	private int x;
	private int y;
		// speed
	private int dx;
	private int dy;
		// direction
	private Direction direction;
	private boolean up;
	private boolean down;
	private boolean left;
	private boolean right;
		// tank collision
	private boolean tankCollisionUp;
	private boolean tankCollisionDown;
	private boolean tankCollisionLeft;
	private boolean tankCollisionRight;
		// living
	private boolean visible;
	private int lives;
	private boolean dead;
		// shooting
	private boolean firing;
	private long firingTimer;
	private long firingDelay;
	
	/**
	 * Creates an instance of Tank with 3 lives
	 * @param player representing the player that is created
	 */
	public Tank(Players player) {
		
		dx = TANK_SPEED;
		dy = TANK_SPEED;
		visible = true;
		lives = 3;
		firing = false;
		firingTimer = System.currentTimeMillis();
		firingDelay = 250;
		
		switch(player) {
		
			case PLAYER1:
				// starting coordinates (orientation)
				x = TANK_WIDTH;
				y = TANK_WIDTH;
				
				// starting direction
				direction = Direction.DOWN;
				
				// loading up the images for the tank
				URL tankUpUrl = iSpit.class.getResource(
		                "/Tank1Up.png");
				tankUp = new ImageIcon(tankUpUrl).getImage();
				
				URL tankDownUrl = iSpit.class.getResource(
		                "/Tank1Down.png");
				tankDown = new ImageIcon(tankDownUrl).getImage();
				
				URL tankLeftUrl = iSpit.class.getResource(
		                "/Tank1Left.png");
				tankLeft = new ImageIcon(tankLeftUrl).getImage();
				
				URL tankRightUrl = iSpit.class.getResource(
		                "/Tank1Right.png");
				tankRight = new ImageIcon(tankRightUrl).getImage();
				break;
				
			case PLAYER2:
				// starting coordinates
				x = PANEL_WIDTH - 2 * TANK_WIDTH;
				y = PANEL_HEIGHT - 2 * TANK_WIDTH;
				
				// starting direction (orientation)
				direction = Direction.UP;
				
				// loading up the images for the tank
				URL tankUpUrl1 = iSpit.class.getResource(
		                "/Tank2Up.png");
				tankUp = new ImageIcon(tankUpUrl1).getImage();
				
				URL tankDownUrl1 = iSpit.class.getResource(
		                "/Tank2Down.png");
				tankDown = new ImageIcon(tankDownUrl1).getImage();
				
				URL tankLeftUrl1 = iSpit.class.getResource(
		                "/Tank2Left.png");
				tankLeft = new ImageIcon(tankLeftUrl1).getImage();
				
				URL tankRightUrl1 = iSpit.class.getResource(
		                "/Tank2Right.png");
				tankRight = new ImageIcon(tankRightUrl1).getImage();
				
				break;
				
			default:
				System.out.println("Error! NO SUCH PLAYER!");
				break;
		}
			
	}
	
	/**
	 * Draw the tank according to the direction it is facing
	 * @param g graphics context
	 */
	public void draw(Graphics2D g) {
		if(!dead) {
			switch(direction) {
			case UP :
				g.drawImage(tankUp, x, y, TANK_WIDTH, TANK_HEIGHT, null);
				break;
			case DOWN:
				g.drawImage(tankDown, x, y, TANK_WIDTH, TANK_HEIGHT, null);
				break;
			case LEFT:
				g.drawImage(tankLeft, x, y, TANK_WIDTH, TANK_HEIGHT, null);
				break;
			case RIGHT:
				g.drawImage(tankRight, x, y, TANK_WIDTH, TANK_HEIGHT, null);
				break;
			default:
				System.out.println("Error drawing the tank, no such direction!");
			}
		}
	}
	
	/**
	 * Updates the tank and bullets movement
	 *  according to the user commands
	 */
	public void update() {
		// tank update
		if(up) { moveUp(); }
		if(down) { moveDown(); }
		if(left) { moveLeft(); }
		if(right) { moveRight(); }
		// bullets update
		if(firing) {
			long elapsed = (System.currentTimeMillis() - firingTimer);
			if(elapsed > firingDelay) {
				if(direction == Direction.UP)
					GamePanel.bullets.add(new Bullet(x + TANK_WIDTH / 2, y, Direction.UP));
				if(direction == Direction.DOWN)
					GamePanel.bullets.add(new Bullet(x + TANK_WIDTH / 2, y + TANK_HEIGHT, Direction.DOWN));
				if(direction == Direction.LEFT)
					GamePanel.bullets.add(new Bullet(x, y  + TANK_HEIGHT / 2, Direction.LEFT));
				if(direction == Direction.RIGHT)
					GamePanel.bullets.add(new Bullet(x + TANK_WIDTH, y + TANK_HEIGHT / 2, Direction.RIGHT));
				firingTimer = System.currentTimeMillis();
			}
		}
	}
	
	// HIT METHOD
	/**
	 * This method decrements the lives of the hit player
	 */
	public void hit() {
			lives--;
		if(lives <= 0)
			dead = true;
	}
	
	// MOVE METHODS
	/** Moves the tank up 32 pixels */
	private void moveUp() {
		setDirection(Direction.UP);
		if(!tankCollisionUp) {
			if(y - dy <= 0)
				y = 0;
			else
				y -= dy;
		}
	}
	
	/** Moves the tank down 32 pixels */
	private void moveDown() {
		setDirection(Direction.DOWN);
		if(!tankCollisionDown) {
			if(y + dy >= PANEL_WIDTH - TANK_WIDTH)
				y = PANEL_WIDTH - TANK_WIDTH;
			else
				y += dy;
		}
	}
	
	/** Moves the tank left 32 pixels */
	private void moveLeft() {
		setDirection(Direction.LEFT);
		if(!tankCollisionLeft) {
			if(x - dx <= 0)
				x = 0;
			else
				x -= dx;
		}
	}
	
	/** Moves the tank right 32 pixels */
	private void moveRight() {
		setDirection(Direction.RIGHT);
		if(!tankCollisionRight) {
			if(x + dx >= PANEL_WIDTH - TANK_WIDTH)
				x = PANEL_WIDTH - TANK_WIDTH;
			else
				x += dx;
		}
	}
	
	// GETTERS
		/** 
		 * Get the current x axis position
		 * @return the current tank position in pixels on horizontal axis
		 * 			from left to right 
		 */
		public int getX() {	return x; }
		/** 
		 * Get the current y axis position
		 * @return the current tank position in pixels on vertical axis 
		 * 			from top to bottom 
		 */
		public int getY() {	return y; }
		/** 
		 * Get the visibility of the tank
		 * @return a boolean describing if the tank is visible or not */
		public boolean isVisible() { return visible; }
		/** 
		 * Get the number of tank lives
		 * @return the number of lives left */
		public int getLives() { return lives; }
		/** 
		 * Get if the tank facing direction is up or not
		 * @return a boolean describing if the tank is facing up */
		public boolean getUp() { return up;	}
		/**
		 * Get if the tank facing direction is down or not
		 *  @return a boolean describing if the tank is facing down */
		public boolean getDown() { return down; }
		/** 
		 * Get if the tank facing direction is left or not
		 * @return a boolean describing if the tank is facing left */
		public boolean getLeft() { return left; }
		/** 
		 * Get if the tank facing direction is right or not
		 * @return a boolean describing if the tank is facing right */
		public boolean getRight() {	return right; }
		/** 
		 * Get the dead or alive state of a tank
		 * @return a boolean describing if the tank is dead or not */
		public boolean isDead() { return dead; }
		
		// SETTERS
		/** 
		 * Sets the direction that the tank is moving  
		 * @param dir representing the direction to move
		 */
		public void setDirection(Direction dir) { direction = dir; }
		
		/** 
		 * Sets the current tank position in pixels on horizontal axis
		 * 	from left to right
		 * @param x representing the number of pixels
		 */
		public void setX(int x) { this.x = x; }
		
		/**
		 * Sets the current tank position in pixels on horizontal axis
		 * from left to right
		 * @param y representing the number of pixels
		 */
		public void setY(int y) { this.y = y; }
		
		/**
		 * Set the tank facing up
		 * @param b representing if facing up or not
		 */
		public void setUp(boolean b) { up = b; }
		
		/**
		 * Set the tank facing down
		 * @param b representing if facing down or not
		 */
		public void setDown(boolean b) { down = b; }
		
		/**
		 * Set the tank facing left
		 * @param b representing if facing left or not
		 */
		public void setLeft(boolean b) { left = b; }
		
		/**
		 * Set the tank facing right
		 * @param b representing if facing right or not
		 */
		public void setRight(boolean b) { right = b; }
		
		/**
		 * Set the tank visible
		 * @param b representing if tank is visible or not
		 */
		public void setVisible(boolean b) { visible = b; }
		
		/**
		 * Set the tank to firing state, as firing bullets 
		 * @param b representing if tank is firing or not
		 */
		public void setFiring(boolean b) { firing = b; }
		
		/**
		 * Sets the tank as colliding up, meaning can't move up anymore
		 * @param b representing if tank is colliding up or not
		 */
		public void setTankCollisionUp(boolean b) { tankCollisionUp = b; }
		
		/**
		 * Sets the tank as colliding down, meaning can't move down anymore
		 * @param b representing if tank is colliding down or not
		 */
		public void setTankCollisionDown(boolean b) { tankCollisionDown = b; }
		
		/**
		 * Sets the tank as colliding left, meaning can't move left anymore
		 * @param b representing if tank is colliding left or not
		 */
		public void setTankCollisionLeft(boolean b) { tankCollisionLeft = b; }
		
		/**
		 * Sets the tank as colliding right, meaning can't move right anymore
		 * @param b representing if tank is colliding right or not
		 */
		public void setTankCollisionRight(boolean b) { tankCollisionRight = b; }
		
		
		/**
		 * Gets the images from the resources and sets the visual representation
		 * of the user on the screen according to the direction he is facing
		 * @param player representing the actual player that will get the image
		 */
		// used in the load method
		public void setImage(Players player) {
			switch(player) {
			case PLAYER1:
				
				URL tankUpUrl = iSpit.class.getResource(
		                "/Tank1Up.png");
				tankUp = new ImageIcon(tankUpUrl).getImage();
				
				URL tankDownUrl = iSpit.class.getResource(
		                "/Tank1Down.png");
				tankDown = new ImageIcon(tankDownUrl).getImage();
				
				URL tankLeftUrl = iSpit.class.getResource(
		                "/Tank1Left.png");
				tankLeft = new ImageIcon(tankLeftUrl).getImage();
				
				URL tankRightUrl = iSpit.class.getResource(
		                "/Tank1Right.png");
				tankRight = new ImageIcon(tankRightUrl).getImage();
				break;
				
			case PLAYER2:
				
				URL tankUpUrl1 = iSpit.class.getResource(
		                "/Tank2Up.png");
				tankUp = new ImageIcon(tankUpUrl1).getImage();
				
				URL tankDownUrl1 = iSpit.class.getResource(
		                "/Tank2Down.png");
				tankDown = new ImageIcon(tankDownUrl1).getImage();
				
				URL tankLeftUrl1 = iSpit.class.getResource(
		                "/Tank2Left.png");
				tankLeft = new ImageIcon(tankLeftUrl1).getImage();
				
				URL tankRightUrl1 = iSpit.class.getResource(
		                "/Tank2Right.png");
				tankRight = new ImageIcon(tankRightUrl1).getImage();
		
				break;
				
			default:
				System.out.println("Error! NO SUCH PLAYER!");
				break;
			}
		}
		
	
}
