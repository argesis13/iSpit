package iSpit;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Class that creates the game panel representing the content of
 * the frame. It creates the player instances, the map, and sets things 
 * up for playing. It is actually the game loop, checking constantly
 * for collision between objects and interaction with the users and
 * updating everything accordingly. 
 * Also has methods for saving and loading the current
 * state of the game.
 * 
 * @author Dan-Eduard Petrescu
 *
 */
public class GamePanel extends JPanel implements Runnable, Commons {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7002490392787359847L;
	// INSTANCE VARIABLES
	private Thread thread;
	private boolean running;
	private boolean gameStart;
	
	public static Tank tank1;
	public static Tank tank2; 
	public static ArrayList<Bullet> bullets;
	
	public static ArrayList<Brick> bricks;

	private int FPS = 30;
	
	// CONSTRUCTOR
	/**
	 * Creates a game panel setting things up like the colors
	 * the dimensions of the panel, adding the tank, brick, 
	 * bullets objects, and waiting for the user to interact.
	 * 
	 */
	public GamePanel() {
		
		// Setting things up
		super();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		addKeyListener(new MyKeyAdapter());
		setFocusable(true);
		requestFocus();
		
		// Constructing the tanks
		if(tank1 == null) {
			tank1 = new Tank(Players.PLAYER1);
		}
		if(tank2 == null) {
			tank2 = new Tank(Players.PLAYER2);
		}
		
		// Constructing the bullets array
		bullets = new ArrayList<Bullet>();
		
		// Constructing the bricks array
		bricks = new ArrayList<Brick>();
		
		addBricks();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	/**
	 * Method used when user decides he wants to start a new game
	 */
	public void newGame() {
		tank1 = new Tank(Players.PLAYER1);
		tank2 = new Tank(Players.PLAYER2);
		bullets = new ArrayList<Bullet>();
		running = true;
	}
	
	/**
	 * Method used when the user decides he wants to load a saved game
	 * from a file
	 * @param f representing the path to the file that
	 * 			contains the saved game info
	 */
	public void loadGame(File f) {
		// clearing tank instances of the old info
		tank1 = null;
		tank2 = null;
		
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(f));
			 
			// start getting the objects out in the order in which they were written
			tank1 = (Tank) objectInputStream.readObject();
			tank1.setImage(Players.PLAYER1); // Image is not Serializable
			
			tank2 = (Tank) objectInputStream.readObject(); 
			tank2.setImage(Players.PLAYER2); // Image is not Serializable
			
			bullets.clear();
			
			objectInputStream.close();			
		} catch(IOException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Method used when the user decides he wants to save a game to a file
	 * @param f representing the path to the file that
	 * 			will contain the saved game info
	 */
	public void saveGame(File f) {
		try {
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(
	                new FileOutputStream(f));
			
			objectOutputStream.writeObject(tank1);
			
			System.out.println(tank1.getX());
			
			objectOutputStream.writeObject(tank2);
			
			System.out.println(tank2.getX());

			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}	
	}
	
	@Override
	/**
	 *  Paint method actually does all the graphical drawing
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
			
		// game over
		if(tank1.isDead() || tank2.isDead())
			gameOver(g2d);
		
		// draw score
		drawScore(g2d);
		
		// draw tanks
		tank1.draw(g2d);
		tank2.draw(g2d);
		
		//draw pause
		if(!running && !tank1.isDead() && !tank2.isDead())
			drawPause(g2d);
		
		// draw bullets
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).draw(g2d);
		}	
		
		// draw bricks
		for(Brick b : bricks) {
			b.drawBrick(g2d);
		}
	}
	
	@Override
	/**
	 * The run method the the game loop will be run within
	 */
	public void run() {
		
		gameStart = true;
		running = true;
		
		long startTime;
		long frameTime;
		long waitTime;
		
		// target time for one loop to maintain 30 FPS
		long targetTime = 1000 / FPS;		
		
		showInstructions();
		
		// GAME LOOP
		while(gameStart) {
			
			// setting things up for 30 FPS
			if(running) {

				startTime = System.currentTimeMillis();
				
				gameUpdate();

				
				frameTime = System.currentTimeMillis() - startTime;
				waitTime = targetTime - frameTime;
				
				try {
					Thread.sleep(waitTime);
				}
				catch(Exception e) {
					System.out.println("interrupted");
				}

			} else {						
				repaint();
			}
		}
		
	}
	
	// UPDATE GAME
	/**
	 * Gathers all the updates from the game objects, 
	 * does the collision checking and updates the info, 
	 * getting things ready for the next redraw
	 */
	private void gameUpdate() {
		// update tanks
		tank1.update();
		tank2.update();
	
		// update bullets
		for(int i = 0; i < bullets.size(); i++) {
			boolean remove = bullets.get(i).update();
			if(remove) {
				bullets.remove(i);
				i--;
			}
		}
		
		checkForCollision();	
		
		repaint();
	}
	
	// CHECK FOR COLLISION
	/**
	 * Checks for collisions between game objects: Tanks, Bricks, Bullets
	 */
	public void checkForCollision() {
		
		// starting by setting all the tanks to not colliding
		signalTankCollisions(false);
		
		// collision between bullets and tanks
		for(int i = 0; i < bullets.size(); i++) {
			Rectangle rb = new Rectangle(bullets.get(i).getX(), bullets.get(i).getY(),
				BULLET_WIDTH, BULLET_HEIGHT);
			Rectangle tb1 = new Rectangle(tank1.getX(), tank1.getY(), 
					TANK_WIDTH, TANK_HEIGHT);
			Rectangle tb2 = new Rectangle(tank2.getX(), tank2.getY(), 
					TANK_WIDTH, TANK_HEIGHT);
			if(rb.intersects(tb1)) {
				tank1.hit();
				bullets.remove(i);
				i--;
			}
			if(rb.intersects(tb2)) {
				tank2.hit();
				bullets.remove(i);
				i--;
			}
		}
		
		// collision between bullets and bricks
		for(int i = 0; i < bullets.size(); i++) {
			Rectangle rb = new Rectangle(bullets.get(i).getX(), bullets.get(i).getY(),
				BULLET_WIDTH, BULLET_HEIGHT);
			for(Brick b : bricks) {
				Rectangle br = new Rectangle(b.getX(), b.getY(), BRICK_WIDTH, BRICK_HEIGHT);
				if(rb.intersects(br)) {
					bullets.remove(i);
				}
			}
			
		}
		// collision between tanks
		Rectangle t1 = new Rectangle(tank1.getX(), tank1.getY(), 
				TANK_WIDTH, TANK_HEIGHT);
		Rectangle t2 = new Rectangle(tank2.getX(), tank2.getY(), 
				TANK_WIDTH, TANK_HEIGHT);

	
		if(tank1.getRight() && tank1.getDown() && t1.intersects(t2)) {
			tank1.setX(tank1.getX() - TANK_WIDTH / 5);
			tank1.setY(tank1.getY() - TANK_WIDTH / 5);
			tank1.setTankCollisionRight(true);
			tank1.setTankCollisionDown(true);
		}
		else if(tank1.getRight() && tank1.getUp() && t1.intersects(t2)) {
			tank1.setX(tank1.getX() - TANK_WIDTH / 5);
			tank1.setY(tank1.getY() + TANK_WIDTH / 5);
			tank1.setTankCollisionRight(true);
			tank1.setTankCollisionUp(true);
		}
		else if(tank1.getLeft() && tank1.getDown() && t1.intersects(t2)) {
			tank1.setX(tank1.getX() + TANK_WIDTH / 5);
			tank1.setY(tank1.getY() - TANK_WIDTH / 5);
			tank1.setTankCollisionLeft(true);
			tank1.setTankCollisionDown(true);
		}
		else if(tank1.getLeft() && tank1.getUp() && t1.intersects(t2)) {
			tank1.setX(tank1.getX() + TANK_WIDTH / 5);
			tank1.setY(tank1.getY() + TANK_WIDTH / 5);
			tank1.setTankCollisionLeft(true);
			tank1.setTankCollisionUp(true);
		}
		else if(tank1.getRight() && t1.intersects(t2)) {
			tank1.setX(tank1.getX() - TANK_WIDTH / 5);
			tank1.setTankCollisionRight(true);
		}
		else if(tank1.getLeft() && t1.intersects(t2)) {
			tank1.setX(tank1.getX() + TANK_WIDTH / 5);
			tank1.setTankCollisionLeft(true);
		}
		else if(tank1.getDown() && t1.intersects(t2)) {
			tank1.setY(tank1.getY() - TANK_WIDTH / 5);
			tank1.setTankCollisionDown(true);
		}
		else if(tank1.getUp() && t1.intersects(t2)) {
			tank1.setY(tank1.getY() + TANK_WIDTH / 5);
			tank1.setTankCollisionUp(true);
		} 
		
		if(tank2.getRight() && tank2.getDown() && t1.intersects(t2)) {
			tank2.setX(tank2.getX() - TANK_WIDTH / 5);
			tank2.setY(tank2.getY() - TANK_WIDTH / 5);
			tank2.setTankCollisionRight(true);
			tank2.setTankCollisionDown(true);
		}
		else if(tank2.getRight() && tank2.getUp() && t1.intersects(t2)) {
			tank2.setX(tank2.getX() - TANK_WIDTH / 5);
			tank2.setY(tank2.getY() + TANK_WIDTH / 5);
			tank2.setTankCollisionRight(true);
			tank2.setTankCollisionUp(true);
		}
		else if(tank2.getLeft() && tank2.getDown() && t1.intersects(t2)) {
			tank2.setX(tank2.getX() + TANK_WIDTH / 5);
			tank2.setY(tank2.getY() - TANK_WIDTH / 5);
			tank2.setTankCollisionLeft(true);
			tank2.setTankCollisionDown(true);
		}
		else if(tank2.getLeft() && tank2.getUp() && t1.intersects(t2)) {
			tank2.setX(tank2.getX() + TANK_WIDTH / 5);
			tank2.setY(tank2.getY() + TANK_WIDTH / 5);
			tank2.setTankCollisionLeft(true);
			tank2.setTankCollisionUp(true);
		}
		else if(tank2.getRight() && t1.intersects(t2)) {
			tank2.setX(tank2.getX() - TANK_WIDTH / 5);
			tank2.setTankCollisionRight(true);
		}
		else if(tank2.getLeft() && t1.intersects(t2)) {
			tank2.setX(tank2.getX() + TANK_WIDTH / 5);
			tank2.setTankCollisionLeft(true);
		}
		else if(tank2.getDown() && t1.intersects(t2)) {
			tank2.setY(tank2.getY() - TANK_WIDTH / 5);
			tank2.setTankCollisionDown(true);
		}
		else if(tank2.getUp() && t1.intersects(t2)) {
			tank2.setY(tank2.getY() + TANK_WIDTH / 5);
			tank2.setTankCollisionUp(true);
		} 
		
		// collision between tank and bricks
		for(Brick b : bricks) {
			Rectangle br = new Rectangle(b.getX(), b.getY(), BRICK_WIDTH, BRICK_HEIGHT);
			if(tank1.getRight() && tank1.getDown() && t1.intersects(br)) {
				tank1.setX(tank1.getX() - TANK_WIDTH / 8);
				tank1.setY(tank1.getY() - TANK_WIDTH / 8);
				tank1.setTankCollisionRight(true);
				tank1.setTankCollisionDown(true);
			}
			else if(tank1.getRight() && tank1.getUp() && t1.intersects(br)) {
				tank1.setX(tank1.getX() - TANK_WIDTH / 8);
				tank1.setY(tank1.getY() + TANK_WIDTH / 8);
				tank1.setTankCollisionRight(true);
				tank1.setTankCollisionUp(true);
			}
			else if(tank1.getLeft() && tank1.getDown() && t1.intersects(br)) {
				tank1.setX(tank1.getX() + TANK_WIDTH / 8);
				tank1.setY(tank1.getY() - TANK_WIDTH / 8);
				tank1.setTankCollisionLeft(true);
				tank1.setTankCollisionDown(true);
			}
			else if(tank1.getLeft() && tank1.getUp() && t1.intersects(br)) {
				tank1.setX(tank1.getX() + TANK_WIDTH / 8);
				tank1.setY(tank1.getY() + TANK_WIDTH / 8);
				tank1.setTankCollisionLeft(true);
				tank1.setTankCollisionUp(true);
			}
			else if(tank1.getRight() && t1.intersects(br)) {
				tank1.setX(tank1.getX() - TANK_WIDTH / 4);
				tank1.setTankCollisionRight(true);
			}
			else if(tank1.getLeft() && t1.intersects(br)) {
				tank1.setX(tank1.getX() + TANK_WIDTH / 4);
				tank1.setTankCollisionLeft(true);
			}
			else if(tank1.getDown() && t1.intersects(br)) {
				tank1.setY(tank1.getY() - TANK_WIDTH / 4);
				tank1.setTankCollisionDown(true);
			}
			else if(tank1.getUp() && t1.intersects(br)) {
				tank1.setY(tank1.getY() + TANK_WIDTH / 4);
				tank1.setTankCollisionUp(true);
			} 
			
			if(tank2.getRight() && tank2.getDown() && t2.intersects(br)) {
				tank2.setX(tank2.getX() - TANK_WIDTH / 8);
				tank2.setY(tank2.getY() - TANK_WIDTH / 8);
				tank2.setTankCollisionRight(true);
				tank2.setTankCollisionDown(true);
			}
			else if(tank2.getRight() && tank2.getUp() && t2.intersects(br)) {
				tank2.setX(tank2.getX() - TANK_WIDTH / 8);
				tank2.setY(tank2.getY() + TANK_WIDTH / 8);
				tank2.setTankCollisionRight(true);
				tank2.setTankCollisionUp(true);
			}
			else if(tank2.getLeft() && tank2.getDown() && t2.intersects(br)) {
				tank2.setX(tank2.getX() + TANK_WIDTH / 8);
				tank2.setY(tank2.getY() - TANK_WIDTH / 8);
				tank2.setTankCollisionLeft(true);
				tank2.setTankCollisionDown(true);
			}
			else if(tank2.getLeft() && tank2.getUp() && t2.intersects(br)) {
				tank2.setX(tank2.getX() + TANK_WIDTH / 8);
				tank2.setY(tank2.getY() + TANK_WIDTH / 8);
				tank2.setTankCollisionLeft(true);
				tank2.setTankCollisionUp(true);
			}
			else if(tank2.getRight() && t2.intersects(br)) {
				tank2.setX(tank2.getX() - TANK_WIDTH / 4);
				tank2.setTankCollisionRight(true);
			}
			else if(tank2.getLeft() && t2.intersects(br)) {
				tank2.setX(tank2.getX() + TANK_WIDTH / 4);
				tank2.setTankCollisionLeft(true);
			}
			else if(tank2.getDown() && t2.intersects(br)) {
				tank2.setY(tank2.getY() - TANK_WIDTH / 4);
				tank2.setTankCollisionDown(true);
			}
			else if(tank2.getUp() && t2.intersects(br)) {
				tank2.setY(tank2.getY() + TANK_WIDTH / 4);
				tank2.setTankCollisionUp(true);
			} 
		}		
		
	}
	
	/**
	 * Signals the tank object that is colliding something and 
	 * where that collision getting the tank to stop going
	 * in that direction
	 * @param b signaling to check all colliding or not
	 */
	private void signalTankCollisions(boolean b) { 
		tank1.setTankCollisionUp(b);
		tank1.setTankCollisionDown(b);
		tank1.setTankCollisionLeft(b);
		tank1.setTankCollisionRight(b);
		tank2.setTankCollisionUp(b);
		tank2.setTankCollisionDown(b);
		tank2.setTankCollisionLeft(b);
		tank2.setTankCollisionRight(b);
	}
	
	// GAME OVER
	/**
	 * Method for drawing the game over state
	 * @param g representing the graphics context
	 */
	public void gameOver(Graphics2D g) {
		running = false;
		g.setColor(Color.WHITE);
		g.setFont(new Font("SANS_SERIF", Font.BOLD, 24));
		g.drawString("GAME OVER", 250, 25);
		g.setFont(new Font("SANS_SERIF", Font.BOLD, 14));
		if(tank1.isDead()) {
			g.setColor(Color.CYAN);
			g.drawString("CYAN PLAYER WINS THE FIGHT!", 220, 75);
		}
		if(tank2.isDead()) {
			g.setColor(Color.RED);
			g.drawString("RED PLAYER WINS THE FIGHT!", 220, 75);
		}
	}
	
	// DRAW SCORE
	/**
	 * Method for drawing the score
	 * @param g representing the graphics context
	 */
	public void drawScore(Graphics2D g) {
		g.setFont(new Font("SANS_SERIF", Font.BOLD, 14));
		g.setColor(Color.RED);
		g.drawString("RED: " + tank1.getLives(), 10, 15);
		g.setColor(Color.CYAN);
		g.drawString("CYAN: " + tank2.getLives(), 575, 15);
	}
	
	// DRAW PAUSE
	/**
	 * Method for drawing the pause game state
	 * @param g representing the graphics context
	 */
	public void drawPause(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("SANS_SERIF", Font.BOLD, 24));
		g.drawString("GAME PAUSED", 250, 35);
	}
	
	// SHOW INSTRUCTIONS
	/**
	 * Method for displaying a MessageDialog with the instructions
	 */
	public void showInstructions() {
		JOptionPane.showMessageDialog(iSpit.game,
			    "Instructions:\n\n"
			    + "In order to win the game you have to spit \n"
			    + "the other guy at least three times.\n\n"
			    + "RIGHT BETWEEN THE EYES!\n\n"
			    + "Cyan player keys: " + "  " + "   " + " Red player keys:\n"
			    + "W - move up    " + "   " +  "  " +  "  " +  "  " + "   " + " Arrow UP - move up\n"
			    + "S - move down   " + "   " +  "  " +  "  " + "  " + " Arrow DOWN - move down\n"
			    + "A - move left    " + "   " +  "  " +  "   " + "    " + " Arrow LEFT - move left\n"
			    + "D - move right  " + "    " +  "  " +  " " + "    " + " Arrow RIGHT - move right\n"
			    + "SPACE - spit    " + "   " +  "   " +  " " + "    " + " Numpad 0 - spit\n");
	}
	
	// KEYLISTENER CLASS
	/**
	 * This class deals with the user interaction, signaling the
	 * game what actions were generated by him.
	 * @author Dan-Eduard Petrescu
	 *
	 */
	class MyKeyAdapter extends KeyAdapter{
		
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			switch(key) {
				case KeyEvent.VK_UP:
					tank1.setUp(true);
					break;
				case KeyEvent.VK_DOWN:
					tank1.setDown(true);
					break;
				case KeyEvent.VK_LEFT:
					tank1.setLeft(true);
					break;
				case KeyEvent.VK_RIGHT:
					tank1.setRight(true);
					break;
				case KeyEvent.VK_NUMPAD0:
					tank1.setFiring(true);
					break;
					
				case KeyEvent.VK_W:
					tank2.setUp(true);
					break;
				case KeyEvent.VK_S:
					tank2.setDown(true);
					break;
				case KeyEvent.VK_A:
					tank2.setLeft(true);
					break;
				case KeyEvent.VK_D:
					tank2.setRight(true);
					break;
				case KeyEvent.VK_SPACE:
					tank2.setFiring(true);
					break;
				case KeyEvent.VK_P:
					if (running) {
						System.out.println(running);
						running = false;
					} else {
						System.out.println(running);
						running = true;
					}
			}
		}
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				switch(key) {
					case KeyEvent.VK_UP:
						tank1.setUp(false);
						break;
					case KeyEvent.VK_DOWN:
						tank1.setDown(false);
						break;
					case KeyEvent.VK_LEFT:
						tank1.setLeft(false);
						break;
					case KeyEvent.VK_RIGHT:
						tank1.setRight(false);
						break;
					case KeyEvent.VK_NUMPAD0:
						tank1.setFiring(false);
						break;
						
					case KeyEvent.VK_W:
						tank2.setUp(false);
						break;
					case KeyEvent.VK_S:
						tank2.setDown(false);
						break;
					case KeyEvent.VK_A:
						tank2.setLeft(false);
						break;
					case KeyEvent.VK_D:
						tank2.setRight(false);
						break;
					case KeyEvent.VK_SPACE:
						tank2.setFiring(false);
						break;
				}			
		}
	}	
	
	// adds the bricks to the view corresponding to the map
	/**
	 * Method to add the bricks to the specified coordinates by the map
	 */
	void addBricks() {
		for(int i = 0; i < coord.length; i++) {
			if(coord[i] != 0) {
				Brick x = new Brick(((i) % 20) * 32, ((i + 1) / 20) * 32);
				bricks.add(x);
			}
		}
	}
	
	// maps the view into numbers representing : 
	//							0 for free space
	//							1 for brick added
	private int coord[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
							0, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0,
							0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
							0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0							
						};
}




