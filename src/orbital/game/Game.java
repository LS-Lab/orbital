/**
 * @(#)Game.java    1.0 1998/07/02 Andre Platzer
 * 
 * Copyright (c) 1996-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import java.applet.Applet;
import orbital.logic.functor.Function;
import java.awt.Container;

import orbital.robotic.Position;
import orbital.moon.awt.AppletFrame;
import orbital.awt.UIUtilities;
import java.awt.BorderLayout;
import java.awt.MenuBar;
import java.awt.Menu;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Panel;
import java.awt.Label;
import java.awt.FileDialog;
import javax.swing.JOptionPane;   //@internal version 1.1 but orbital.game.* already use Iterator of 1.2 so what shell's
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.InputEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Iterator;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.Enumeration;
import java.util.StringTokenizer;

import java.util.Dictionary;
import java.util.Hashtable;

import orbital.util.InnerCheckedException;

/**
 * Game Applet is a generic class for games on Gameboards. To apply it
 * on a certain game, you must define the games' rules with any instance
 * implementing the GameRules Interface.
 * This Interface can then start an AI on request via {@link GameRules#startAIntelligence(String)}.
 * 
 * @stereotype UI
 * @version 1.0, 2000/02/26
 * @author Andr&eacute; Platzer
 * @see <a href="doc-files/Game.html">Game applet parameter example</a>
 * @attribute resources = menubar and popup menu
 * @internal note It is possible to add functionality for playing our games by email or via TCP/IP server communication. Serialization of the field (plus perhaps of the move information for authentic tracking) should do the job.
 * @xxx turnDone should be called "performedMove" and perhaps we can get rid of this old way of using events. Also we need a more customizable way of deciding when to end a turn (f.ex. some games may allow a player to perform multiple moves before ending his turn)
 */
public class Game extends Applet implements Runnable {
    private static final long serialVersionUID = 1298765184014728813L;
    public static void main(String arg[]) throws Exception {
	if (arg.length == 0 || orbital.signe.isHelpRequest(arg)) {
	    System.out.println(usage);
	    System.out.println(AppletFrame.info(new Game()));
	    return;
	} 
    	AppletFrame.showApplet(new Game(), "Game Application", arg);
    }
    public static final String usage = "usage: " + Game.class + " (<parameter>=<value>)*" + System.getProperty("line.separator") + "\tThe game applet has access to the values assigned to the parameters.";

    /**
     * Program resources.
     * @note It is not required that this variable really contains the ResourceBundle used.
     * Instead, getResources() may also store them in any other way. Therefor it is strictly
     * recommended not to access this variable, directly.
     * @see #getResources()
     * @todo seriality?
     */
    private ResourceBundle resources;
	
    private static final String FILE_IDENTIFIER = "Game";

    // {{DECLARE_PARAMETERS

    /**
     * The name of the game.
     * @serial
     */
    private String      gameName = null;

    /**
     * Instance of the game rules to use.
     * @serial
     */
    private GameRules   rules;

    /**
     * Arguments passed to the AIs in {@link #player}.
     * @serial
     */
    private String	playerArgument[];

    /**
     * The diverse AI-players.
     * <code>players[i] == null</code> if and only if <code>i</code> is a human player.
     * @serial
     */
    private Function	players[];

    // }}

    // {{DECLARE_CONTROLS

    /**
     * A gameboard displaying the field to play on.
     * Also handles mouse drags.
     * @serial
     */
    private Gameboard	board;

    /**
     * Container for the control panel of the UserDialog.
     * @serial
     */
    private Container	control = null;

    /**
     * The heading label.
     * @serial
     */
    private Label	nameLabel;

    // }}
	
    /**
     * The ActionListeners reacting on menu events.
     * This is a mapping from menu keys and menu item keys as defined in the {@link #resources}
     * to {@link java.awt.event.ActionListener ActionListeners} that will be notified of a menu action.
     */
    private final Dictionary actions;

    /**
     * only for pure AI games without real players.
     * @serial
     * @todo we cannot store threads, can we? Besides, can we really store applets?
     */
    //XXX: concurrent synchronization may be required for this volatile field
    private volatile Thread runner = null;

    /**
     * Runnable-init entry point.
     */
    public Game() {
    	// reactions on menu actions
    	this.actions = new Hashtable();
    	actions.put("new", new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
		    stop();
		    start();
		}
	    });
    	actions.put("load", new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
		    FileDialog dlg = new FileDialog(UIUtilities.getParentalFrame(Game.this), getResources().getString("dialog.game.load.title"), FileDialog.LOAD);
		    dlg.setVisible(true);
		    String file = dlg.getFile();
		    if (file == null)
			return;
		    File f = new File(dlg.getDirectory(), file);
		    if (f.exists())
			try {
			    ObjectInputStream is = new ObjectInputStream(new FileInputStream(f));
			    load(is);
			    is.close();
			    showStatus(getResources().getString("statusbar.game.load"));
			} catch (IOException x) {
			    log(x);
			} catch (ClassNotFoundException x) {
			    log(x);
			} 
		}
	    });
    	actions.put("save", new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
		    FileDialog dlg = new FileDialog(UIUtilities.getParentalFrame(Game.this), getResources().getString("dialog.game.save.title"), FileDialog.SAVE);
		    dlg.setVisible(true);
		    String file = dlg.getFile();
		    if (file == null)
			return;
		    File f = new File(dlg.getDirectory(), file);
		    try {
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(f));
			store(os);
			os.close();
			showStatus(getResources().getString("statusbar.game.save"));
		    } catch (IOException x) {
			log(x);
		    } 
		}
	    });
    	actions.put("stop", new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
		    stop();
		}
	    });
    }

    /**
     * Get the name of the game.
     */
    public String getGameName() {
	return gameName;
    }
    protected void setGameName(String newName) {
	this.gameName = newName;
    }

    /**
     * Get the game rules used.
     */
    public GameRules getGameRules() {
	return rules;
    }
    protected void setGameRules(GameRules newRules) {
	this.rules = newRules;
    }

    /**
     * Get all players currently playing.
     */
    public Function[] getPlayers() {
	return players;
    } 
    
    /**
     * Get the gameboard.
     * That gameboard also handles mouse drags.
     * @return a gameboard displaying the field to play on.
     */
    public Gameboard getGameboard() {
	return board;
    }

    /**
     * Get the container for the control-panel displayed.
     * @serial
     */
    protected Container	getControl() {
	return control;
    }

    /**
     * Get program resources.
     * Apart from providing general resources,
     * this bundle especially defines the menu bar, menus, menu items and the popup menu.
     * @todo document format of menu properties
     * @postconditions RES!=null
     * @throws MissingResourceException when resources could not be loaded at run-time.
     * @see #init()
     */
    protected ResourceBundle getResources() {
	if(resources == null) {
	    // try to get the resource bundle of our real sub classes, first. However, this won't work for the default package
	    try {
		return resources = ResourceBundle.getBundle(getClass().getName(), Locale.getDefault(), getClass().getClassLoader());
	    } catch (Exception trial) {
		System.out.println("no resources specified for: " + getClass().getName() + "\nUsing default resources\n" + trial);
		// else try to get our resource bundle
		try {
		    return resources = ResourceBundle.getBundle("orbital.resources.Game");
		} catch (MissingResourceException missing) {
		    log("missing resource: An error occured initializing " + Game.class.getName() + ".\nThe package seems corrupt or a resource is missing, aborting\n" + missing);
		    //JOptionPane.showMessageDialog(null, "An error occured initializing " + Game.class.getName() + ".\nThe package seems corrupt or a resource is missing, aborting\n" + missing, "Error", JOptionPane.ERROR_MESSAGE);
		    throw (MissingResourceException) new MissingResourceException(trial.getMessage() + " AND " + missing.getMessage(), missing.getClassName(), missing.getKey()).initCause(missing);
		} 
	    }
	}
	return resources;
    }

    /**
     * Get the map of actions.
     * This is a mapping from menu keys and menu item keys as defined in the {@link #resources}
     * to {@link java.awt.event.ActionListener ActionListeners} that will be notified of a menu action.
     * @see <a href="">demo/jfc/Notepad/src/Notepad.java for description of menu resource bundle</a> 
     */
    protected final Dictionary getActions() {
	return actions;
    }
	
    /**
     * Applet-init entry point.
     */
    public void init() {
	super.init();

	// {{GET_PARAMETERS
	String param = getParameter("gameName");
	setGameName(param == null ? "Generic Game" : param);

	param = getParameter("gameRules");
	String gameRules = param == null ? "YourGameRules" : param;

	try {
	    setGameRules(createGameRules(gameRules));
	    players = new Function[rules.getLeagues()];
	    playerArgument = new String[players.length];
	    for (int i = 0; i < players.length; i++)
		playerArgument[i] = getParameter("player-" + i);
	} catch (Exception e) {
	    log(e);
	} 
	// }}

	// {{INIT_CONTROLS
	setLayout(new BorderLayout());

	if (control == null) {
	    control = createControl();
	    add(control, BorderLayout.NORTH);
	    board = new Gameboard();
	    add(board, BorderLayout.CENTER);
	    final MenuBar ourBar = createMenuBar();
	    if (ourBar != null)
		try {
		    Frame parent = UIUtilities.getParentalFrame(this);
		    if (parent != null) {
			MenuBar bar = parent.getMenuBar();
			if (bar == null) {
			    bar = new MenuBar();
			    parent.setMenuBar(bar);
			}
			for (int i = 0; i < ourBar.getMenuCount(); i++)
			    bar.add(ourBar.getMenu(i));
		    }
    	        }
    	        catch (SecurityException ignore) {}
	} 

	// }}
		
	showStatus(getResources().getString("statusbar.game.init"));
    } 

    /**
     * Applet-start entry point.
     */
    public void start() {
	Field field = rules.startField(this);
	board.setField(field);
	int realPlayers = 0;
	for (int i = Figure.NOONE + 1; i < players.length; i++) {
	    if (playerArgument[i] == null) {
		players[i] = null;
		realPlayers++;
	    } else
		players[i] = rules.startAIntelligence(playerArgument[i]);
	}
	repaint();

	// computer players only
	if (realPlayers == 0) {
	    runner = new Thread(this, "AI_Runner");
	    runner.start();
	} else {
	    runner = null;
	    if (players[board.getField().getTurn()] != null)
		// if a computer commences, let him act
		turn();
	}
	showStatus(getResources().getString("statusbar.game.start"));
    } 

    /**
     * Applet-stop exit point.
     */
    public void stop() {
	Thread moribund = runner;
	runner = null;	  // runner.stop();
	if (moribund != null)
	    moribund.interrupt();

	// alternative implementation
	/*
	 * Thread[] ts = new Thread[Thread.currentThread().activeCount()];
	 * for (int i=Thread.currentThread().enumerate(ts)-1; i>=0; i--)
	 * if ("AI_Runner".equals(ts[i].getName()))
	 * ts[i].interrupt();
	 */
	showStatus(getResources().getString("statusbar.game.stop"));
    } 

    /**
     * Applet-destroy exit point.
     */
    public void destroy() {
	removeAll();
	control = null;
	rules = null;
	players = null;
	playerArgument = null;
	this.resources = null;
	//runner.destroy();
	super.destroy();
    } 


    /**
     * Runnable-start entry point.
     * @see #action(Event, Object)
     * @internal see #turn()
     */
    public void run() {
	Thread thisThread = Thread.currentThread();
	while (runner == thisThread && !Thread.interrupted()) {
	    //@xxx the above check executes no longer since change of turn to infinite loop.
	    if (turn() != Figure.NOONE)
		return;
	}
	// clean up: forget about references
	board = null;
	for (int i = 0; i < players.length; i++)
	    players[i] = null;
    } 

    /**
     * Called at the end of each user turn.
     * Once all real players made their turn, it will let all AIs take their actions.
     * Notifies the current GameRules implementation that a turn is done.
     * @see GameRules#turnDone(Field)
     */
    protected int turn() {
	// check for any winners
	int winner = rules.turnDone(board.getField());
	if (winner != Figure.NOONE) {
	    displayWinner(winner);
	    return winner;
	}

	// do moves while it's an AI's turn
	for (int turn = board.getField().getTurn();
	     players[turn] != null;
	     turn = board.getField().getTurn()) {
	    // @xxx doesn't this policy (a single player can move several times until it's another player's turn) conflict with AlphaBetaPruning which simply doesn't know about it?
	    showStatus(getResources().getString("statusbar.ai.thinking"));
	    Object action = players[turn].apply(board.getField());
	    showStatus(getResources().getString("statusbar.ai.moving"));
	    if (action instanceof MoveWeighting.Argument) {
		MoveWeighting.Argument move = (MoveWeighting.Argument) action;
		Position source = new Position(move.figure);
		// if we could rely on our AI, then we could optimize away this expensive moving and simply use the resulting field = move.field
		//@internal cloning the position information is necessary, otherwise move would detect that it gets lost during swap.
		if (!board.getField().move(source, move.move))
		    throw new Error("AI should only take legal moves: " + move);
		board.repaint(source);
		board.repaint(move.destination);
	    } else
		throw new Error("AI found no move: " + action);
	    //@xxx rename to moveDone?
	    winner = rules.turnDone(board.getField());
	    if (winner != Figure.NOONE) {
		displayWinner(winner);
		return winner;
	    }
	} 

	return winner;
    } 

    /**
     * Create the control-panel.
     * @return the control-panel container displayed in the {@link BorderLayout#NORTH north}
     *  to let the user control the game.
     */
    protected Container createControl() {
	Container control = new Panel();
	control.setLayout(new BorderLayout());
	nameLabel = new Label(gameName, Label.CENTER);
	control.add(nameLabel, BorderLayout.NORTH);

	final PopupMenu pop = createPopupMenu();
	if (pop != null) {
	    control.add(pop);
	    MouseListener popper = new MouseAdapter() {
		    public void mouseClicked(MouseEvent ms) {
			//if (ms.isPopupTrigger() || (ms.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			pop.show(ms.getComponent(), ms.getX(), ms.getY());
		    } 
    		};
	    control.addMouseListener(popper);
	    nameLabel.add(pop);
	    nameLabel.addMouseListener(popper);
	}
	return control;
    } 

    /**
     * Create the menu bar for the applet.
     * <p>
     * Note that applets may be restricted such that they are not allowed to have a menu bar.
     * </p>
     * @return the menu bar to display (if possible), or <code>null</code> if no menu bar is desired.
     */
    private MenuBar createMenuBar() {
	return new AwtResourceDecoder(getResources(), actions).createMenuBar();
    }
    /**
     * Create the popup menu for the applet.
     * @return the popup menu, or <code>null</code> if no popup menu is desired.
     */
    private PopupMenu createPopupMenu() {
	return new AwtResourceDecoder(getResources(), actions).createPopupMenu();
    }
    /**
     * display that a league has won this Game.
     */
    protected void displayWinner(int league) {
	showStatus(getResources().getString("statusbar.game.end"));
	ResourceBundle resources = getResources();
	String	   winner = (players[Math.abs(league)] == null ? resources.getString("text.player") : resources.getString("text.computer")) + " (" + Math.abs(league) + ')';
	int selected = JOptionPane.showConfirmDialog(UIUtilities.getParentalFrame(this), winner + ' ' + resources.getString("dialog.game.finish.hasWon") + (league > 0 ? resources.getString("dialog.game.finish.won") : resources.getString("dialog.game.finish.survived")) + resources.getString("dialog.game.finish.tryAgain"), resources.getString("dialog.game.finish.title"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	if (selected == JOptionPane.YES_OPTION) {
	    stop();
	    start();
	} else
	    repaint();
    } 

    /**
     * Load a saved game from a stream. Fetch new images for the figures.
     * @note Remember that class names are relative to this package for serialization.
     * If you desire your own package to be default, simply overwrite this method with the body
     * <pre>
     * super.load(is);
     * </pre>
     * to circumvent confusion with different default packages.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see GameRules#getImage(Figure)
     * @see #createGameRules(java.lang.String)
     */
    public void load(ObjectInputStream is) throws ClassNotFoundException, IOException {
	if (!FILE_IDENTIFIER.equals(is.readUTF()))
	    throw new IOException("illegal format of stream content");
	Field field = (Field) is.readObject();
	board.setField(field);
	for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
	    Figure f = (Figure) i.next();
	    if (f instanceof FigureImpl)
		((FigureImpl)f).setImage(rules.getImage(f));
	} 
    } 

    /**
     * Save game to a stream.
     */
    public void store(ObjectOutputStream os) throws IOException {
	os.writeUTF(FILE_IDENTIFIER);
	os.writeObject(board.getField());
    } 

    /**
     * called to get the class specifying the GameRules.
     * Subclasses can implement other criteria and behaviour.
     * Be aware that the package of the class implementing this method defines the
     * default package for class names. So for the default implementation you must
     * specify a fully qualified class name, but if you overwrite this method and load
     * the class yourself, your package will be used as default.
     * @param gameRules a String specifying which GameRules to use
     * according to the "gameRules" parameter given to this Applet.
     * For the default implementation, it is a fully qualified class name.
     * @see #load(java.io.ObjectInputStream)
     */
    protected GameRules createGameRules(String gameRules) throws Exception {
	return (GameRules) Class.forName(gameRules).newInstance();
    } 
    /**
     * @deprecated Use {@link #createGameRules(String)} instead.
     */
    protected GameRules getGameRules(String gameRules) throws Exception {
	return createGameRules(gameRules);
    }

    public void paint(Graphics g) {
	paintComponents(g);
    } 

    /**
     * Handles Event "turnDone" raised whenever a user finished his turn.
     * Then all AIs are requested for their actions, if any.
     * @see #run()
     * @internal see #turn()
     */
    public boolean action(Event evt, Object arg) {
	if ("turnDone".equals(arg)) {
	    repaint();
	    turn();
	    return true;
	} 
	return super.action(evt, arg);
    } 

    private void log(Object msg) {
	showStatus(msg + "");
	if (msg instanceof Throwable)
	    ((Throwable) msg).printStackTrace();
    }

    /**
     * Info.
     */
    public String getAppletInfo() {
	return "generic Applet for Games on a GameBoard. Copyright (c) 1996-2001 by Andre Platzer";
    } 

    /**
     * Parameter Info
     */
    public String[][] getParameterInfo() {
	String[][] info = {
	    {"gameName", "String", "name of the concrete Game"},
	    {"gameRules", "String", "parameter describing the GameRules. Per default the name of a class that implements GameRules."},
	    {"player-X", "String", "the argument to pass when starting player number X. If left out, a human player will play X."}
	};
	return info;
    } 
}


/**
 * .
 * @author  Timothy Prinzing
 * @version 1.16 09/23/99
 * @author  Andr&eacute; Platzer
 * @version 1.0, 2001/11/23
 * @internal see Notepad for more features
 */
class AwtResourceDecoder {
    private final ResourceBundle resources;
    private final Dictionary	 actions;
    public AwtResourceDecoder(ResourceBundle resources, Dictionary actions) {
	this.resources = resources;
	this.actions = actions;
    }

    /**
     * Create the menubar for the app.  By default this pulls the
     * definition of the menu from the associated resource file. 
     */
    public MenuBar createMenuBar() {
    	MenuBar mb = new MenuBar();
    
    	Enumeration menus = new StringTokenizer(resources.getString("menubar"));
    	while (menus.hasMoreElements()) {
	    String menu = (String) menus.nextElement();
    	    Menu m = createMenu(menu);
    	    if (m != null)
		mb.add(m);
    	}
    	return mb;
    }

    /**
     * Create a popup menu for the app.  By default this pulls the
     * definition of the menu from the associated resource file.
     */
    public PopupMenu createPopupMenu() {
    	PopupMenu menu = new PopupMenu(resources.getString("popup" + ".label"));
    	fill(menu, "popup");
    	return menu;
    }

    /**
     * Create a menu for the app.  By default this pulls the
     * definition of the menu from the associated resource file.
     * @see Notepad
     */
    protected Menu createMenu(String key) {
    	Menu menu = new Menu(resources.getString(key + ".label"));
    	fill(menu, key);
    	return menu;
    }
    void fill(Menu menu, String key) {
    	Enumeration items = new StringTokenizer(resources.getString(key));
    	while (items.hasMoreElements()) {
	    String item = (String) items.nextElement();
    	    if (item.equals("-"))
		menu.addSeparator();
    	    else
		menu.add(createMenuItem(item));
    	}
    }

    /**
     * This is the hook through which all menu items are
     * created.  It registers the result with the menuitem
     * hashtable so that it can be fetched with getMenuItem().
     * @see #getMenuItem
     * @see Notepad
     * @internal see Notepad for more features
     */
    protected MenuItem createMenuItem(String cmd) {
    	MenuItem mi = new MenuItem(resources.getString(cmd + ".label"));
        /*URL url = getResource(cmd + imageSuffix);
	  if (url != null) {
	  mi.setHorizontalTextPosition(JButton.RIGHT);
	  mi.setIcon(new ImageIcon(url));
	  }*/
	String action = null;
    	try {
	    action = resources.getString(cmd + ".action");
    	}
    	catch (MissingResourceException trial) {}
    	if (action == null)
	    action = cmd;
    	mi.setActionCommand(action);
    	ActionListener a = (ActionListener) actions.get(action);
    	if (a != null) {
    	    mi.addActionListener(a);
    	    mi.setEnabled(true);
    	} else
	    mi.setEnabled(false);
    	return mi;
    }
}
