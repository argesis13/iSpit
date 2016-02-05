package iSpit;

/**
 * Interface with the most common used constants in the game
 * @author Dan-Eduard Petrescu
 *
 */
public interface Commons {

	//	orientation of the tanks
	public static enum Direction {UP, DOWN, LEFT, RIGHT};
	
	// 	players in the game
	public static enum Players {PLAYER1, PLAYER2};
	
	//	constants
	public static final int PANEL_WIDTH = 640;
	public static final int PANEL_HEIGHT = 640;
	
	
	public static final int TANK_SPEED = 8;
	public static final int TANK_WIDTH = 32;
	public static final int TANK_HEIGHT = 32;
	
	public static final int BRICK_WIDTH = 32;
	public static final int BRICK_HEIGHT = 32;
	
	public static final int BULLET_WIDTH = 5;
	public static final int BULLET_HEIGHT = 2;
}
