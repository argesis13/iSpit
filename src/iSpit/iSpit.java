package iSpit;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

/**
 * <h1> The most awaited, most exciting game ever: 
 * <br>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;
 * &emsp;&emsp;&emsp;
 * iSpit</h1>
 * @author Dan-Eduard Petrescu
 *
 */
public class iSpit extends JFrame implements Commons{
	
	private static final long serialVersionUID = 3075040004390576537L;
	
	GamePanel content;
	static iSpit game;
	
	// Constructor
	/**
	 * Creates the game object
	 */
	public iSpit() {
		// Creating the menu bar
		initMenu();
		
		// Creating main game panel
		content = new GamePanel(); 

		// Getting the screen size
		Toolkit tk = Toolkit.getDefaultToolkit();
		Dimension screenSize = tk.getScreenSize();
		
		// Defining the properties of the JFrame window		
		setContentPane(content);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("iSpit");
		setLocation((screenSize.width - PANEL_WIDTH) / 2,  // -> centered
				(screenSize.height - PANEL_HEIGHT) / 2);
		//setSize(PANEL_WIDTH, PANEL_HEIGHT);
		pack();
		setVisible(true);
		setResizable(false);
	}
	
	// Creates the menu
	private void initMenu() {
		
		// Creating menu elements 
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		JMenuItem newGame = new JMenuItem("New Game");
		fileMenu.add(newGame);
		
		JMenuItem saveGame = new JMenuItem("Save Game...");
		fileMenu.add(saveGame);
		
		JMenuItem loadGame = new JMenuItem("Load Game...");
		fileMenu.add(loadGame);
		
		JMenuItem instructGame = new JMenuItem("Instructions");
		fileMenu.add(instructGame);
		
		fileMenu.addSeparator();
		
		JMenuItem exitGame = new JMenuItem("Exit");
		fileMenu.add(exitGame);
	
		newGame.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		saveGame.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		loadGame.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		exitGame.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		instructGame.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		
		// Dealing with the user interaction with the menu
		ActionListener menuActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(e.getSource() == newGame) {
					content.newGame();
				} else if(e.getSource() == saveGame) {
					try {
						JFileChooser chooser = new JFileChooser();
						File workingDirectory = new File(System.getProperty("user.dir"));
						chooser.setCurrentDirectory(workingDirectory);
						chooser.showSaveDialog(game);
						chooser.setDialogType(JFileChooser.SAVE_DIALOG);
						File saveFile = chooser.getSelectedFile();
						
						String file_name = saveFile.toString();
						if (!file_name.endsWith(".savedata"))
						    file_name += ".savedata";
						
						saveFile = new File(file_name);
						
						content.saveGame(saveFile);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
				} else if(e.getSource() == loadGame) {					
					try {
						JFileChooser chooser = new JFileChooser();
						File workingDirectory = new File(System.getProperty("user.dir"));
						chooser.setCurrentDirectory(workingDirectory);
						chooser.showOpenDialog(game);
						chooser.setDialogType(JFileChooser.OPEN_DIALOG);
						File loadFile = chooser.getSelectedFile();
						
						if(!loadFile.toString().endsWith(".savedata")) {
							JOptionPane.showMessageDialog(chooser, "Please choose a '.savedata' extension file");
						} else {
							content.loadGame(loadFile);
						}
					} catch(Exception ex) {
						//JOptionPane.showMessageDialog(null, "You didn't select anything...");
					}
					
				} else if(e.getSource() == exitGame) {
					System.exit(0);
					
				} else if(e.getSource() == instructGame) {
					content.showInstructions();
				}
			}
		};
		
		newGame.addActionListener(menuActionListener);
		saveGame.addActionListener(menuActionListener);
		loadGame.addActionListener(menuActionListener);
		exitGame.addActionListener(menuActionListener);
		instructGame.addActionListener(menuActionListener);
	}
	
	// Main method
	public static void main(String[] args) {
		game = new iSpit();
	}
	
}
