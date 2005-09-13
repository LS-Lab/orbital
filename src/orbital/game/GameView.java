/**
 * @(#)GameView.java    1.0 1998-07-02 Andre Platzer
 * 
 * Copyright (c) 1996-2002 Andre Platzer. All Rights Reserved.
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
import orbital.moon.awt.AboutDialog;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.Enumeration;
import java.util.StringTokenizer;

import java.util.Dictionary;
import java.util.Hashtable;

import orbital.signe;
import orbital.util.InnerCheckedException;

/**
 * GameView is an applet and application displaying games on game
 * boards. Given any GameRules implementation, GameView starts and
 * displays the board game. A GameRules implementation can either be
 * specified as an applet parameter or a command-line argument, and
 * should consist of a fully qualified class name. For sophisticated
 * problems consider sub classing GameView.
 * <p>
 * To use GameView for displaying a specific board game, you
 * must define the games' rules with any instance implementing the
 * {@link GameRules} interface. This interface can then start an AI on
 * request via {@link GameRules#startAIntelligence(String)}.
 * </p>
 *
 * @stereotype UI
 * @version $Id$
 * @version $Id$
 * @author Andr&eacute; Platzer
 * @see <a href="doc-files/Game.html">GameView applet parameter example</a>
 * @see "examples/SChess/"
 * @attribute resources = menubar, popup menu, dialog
 * @internal note It is possible to add functionality for playing our games by email or via TCP/IP server communication. Serialization of the field (plus perhaps of the move information for authentic tracking) should do the job.
 * @xxx turnDone should be called "performedMove" and perhaps we can get rid of this old way of using events. Also we need a more customizable way of deciding when to end a turn (f.ex. some games may allow a player to perform multiple moves before ending his turn)
 */
public class GameView extends Applet {
    private static final long serialVersionUID = 1298765184014728813L;
    public static void main(String arg[]) throws Exception {
        if (arg.length == 0 || orbital.signe.isHelpRequest(arg)) {
            System.out.println(usage);
            System.out.println(AppletFrame.info(new GameView()));
            return;
        } 
        AppletFrame.showApplet(new GameView(), "Game Application", arg);
    }
    public static final String usage = "usage: " + GameView.class + " (<parameter>=<value>)*" + System.getProperty("line.separator") + "\tThe game applet has access to the values assigned to the parameters.";

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
     * The gamemaster used for coordinating the game.
     * @serial
     */
    private Gamemaster  gamemaster;

    // }}

    // {{DECLARE_CONTROLS

    /**
     * A gameboard displaying the field to play on.
     * Also handles mouse drags.
     * @serial
     */
    private Gameboard   board;

    /**
     * Container for the control panel of the UserDialog.
     * @serial
     */
    private Container   control = null;

    /**
     * The heading label.
     * @serial
     */
    private Label       nameLabel;

    // }}
        
    /**
     * The ActionListeners reacting on menu events.
     * This is a mapping from menu keys and menu item keys as defined in the {@link #getResources() resources}
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
    public GameView() {
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
                    FileDialog dlg = new FileDialog(UIUtilities.getParentalFrame(GameView.this), getResources().getString("dialog.game.load.title"), FileDialog.LOAD);
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
                    FileDialog dlg = new FileDialog(UIUtilities.getParentalFrame(GameView.this), getResources().getString("dialog.game.save.title"), FileDialog.SAVE);
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
        actions.put("about", new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    about();
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
     * Get the gamemaster working to coordinate the current game.
     * That's the model for this view.
     */
    public Gamemaster getGamemaster() {
        return gamemaster;
    }
    protected void setGamemaster(Gamemaster newMaster) {
        //System.out.println("GameView.setGamemaster");
        this.gamemaster = newMaster;
    }

    /**
     * Get the game rules used.
     * @deprecated Since Orbital1.1 use {@link #getGamemaster()}.{@link Gamemaster#getGameRules()}. instead.
     */
    public GameRules getGameRules() {
        return getGamemaster().getGameRules();
    }
    /**
     * @deprecated Since Orbital1.1 use {@link #getGamemaster()}.{@link Gamemaster#setGameRules(GameRules)}. instead.
     */
    protected void setGameRules(GameRules newRules) {
        getGamemaster().setGameRules(newRules);
    }

    /**
     * Get all players currently playing.
     * @deprecated Since Orbital1.1 use {@link #getGamemaster()}.{@link Gamemaster#getPlayers()}. instead.
     */
    public Function[] getPlayers() {
        return getGamemaster().getPlayers();
    } 
    
    /**
     * Get the gameboard.
     * That gameboard also handles mouse drags.
     * @return a gameboard displaying the field to play on.
     */
    public Gameboard getGameboard() {
        return board;
    }
    private void setGameboard(Gameboard newGameboard) {
        this.board = newGameboard;
    }

    /**
     * Get the container for the control-panel displayed.
     * @serial
     */
    protected Container getControl() {
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
                    return resources = ResourceBundle.getBundle(GameView.class.getName());
                } catch (MissingResourceException missing) {
                    log("missing resource: An error occured initializing " + GameView.class.getName() + ".\nThe package seems corrupt or a resource is missing, aborting\n" + missing);
                    //JOptionPane.showMessageDialog(null, "An error occured initializing " + Game.class.getName() + ".\nThe package seems corrupt or a resource is missing, aborting\n" + missing, "Error", JOptionPane.ERROR_MESSAGE);
                    throw (MissingResourceException) new MissingResourceException(trial.getMessage() + " AND " + missing.getMessage(), missing.getClassName(), missing.getKey()).initCause(missing);
                } 
            }
        }
        return resources;
    }

    /**
     * Get the map of actions.
     * This is a mapping from menu keys and menu item keys as defined in the {@link #getResources() resources}
     * to {@link java.awt.event.ActionListener ActionListeners} that will be notified of a menu action.
     * @see <a href="">demo/jfc/Notepad/src/Notepad.java for description of menu resource bundle</a> 
     */
    protected final Dictionary getActions() {
        return actions;
    }

    /**
     * Called when setting a new field.
     * Notifies our gameboard about the new field that we want to be displayed.
     * <p>
     * You may hook to this method for re-registering any listeners, especially
     * for cases at load time or when starting a new game.
     * </p>
     */
    protected void setField(Field field) {
        //System.out.println("GameView.setField(" + System.identityHashCode(field) + ")");
        board.setField(field);
        field.addFieldChangeListener(new FieldChangeAdapter() {
                //@internal are transient
                public void stateChanged(FieldChangeEvent evt) {
                    //@todo assert evt.getField() == getGamemaster().getField() : "we have only registered ourselves to our field " + getGamemaster().getField() + " source=" + evt.getField();
                    if (evt.getType() == FieldChangeEvent.END_OF_GAME)
                        displayWinner(((Integer) evt.getChangeInfo()).intValue());
                }
            });
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
            GameRules rules = createGameRules(gameRules);
            Function players[] = new Function[rules.getLeagues()];
            for (int i = 0; i < players.length; i++) {
                String arg = getParameter("player-" + i);
                players[i] = arg == null || arg.equals("null")
                    ? null
                    //@todo shouldn't we restart players in start()?
                    : rules.startAIntelligence(i, arg);
            }
            this.setGamemaster(new Gamemaster(this,
                                             rules,
                                             players)
                               );
        } catch (Exception e) {
            log(e);
        }

        // }}

        // {{INIT_CONTROLS
        final Container pane = this;
        pane.setLayout(new BorderLayout());

        if (control == null) {
            control = createControl();
            pane.add(control, BorderLayout.NORTH);
            setGameboard(new Gameboard());
            pane.add(board, BorderLayout.CENTER);
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
        start(null);
    }
    private void start(Field initialField) {
        //@internal that's better in order to not let the last gamemaster finally {setField(null)} at end of run() and our field's lost again after the first move.
        setGamemaster(new Gamemaster(this, getGamemaster().getGameRules(), getGamemaster().getPlayers(), initialField));
        getGamemaster().start();
        setField(getGamemaster().getField());
        showStatus(getResources().getString("statusbar.game.start"));
    } 

    /**
     * Applet-stop exit point.
     */
    public void stop() {
        getGamemaster().stop();
        showStatus(getResources().getString("statusbar.game.stop"));
    } 

    /**
     * Applet-destroy exit point.
     */
    public void destroy() {
        getGamemaster().destroy();
        setGamemaster(null);
        removeAll();
        control = null;
        this.resources = null;
        super.destroy();
    } 


    /**
     * Display help about this applet.
     */
    protected void about() {
        final String nl = System.getProperty("line.separator");
        final ResourceBundle resource = getResources();
        AboutDialog.showAboutDialog(this, resources.getString("dialog.about.text") + nl + usage + nl + nl + nl + getGameName() + " " + resources.getString("application.version") + " " + resources.getString("dialog.about.application.text") + " " + signe.getManifest(), resources.getString("dialog.about.title") + " " + getGameName());
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
        String     winner = (getPlayers()[Math.abs(league)] == null ? resources.getString("text.player") : resources.getString("text.computer")) + " (" + Math.abs(league) + ')';
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
        stop();
        if (!FILE_IDENTIFIER.equals(is.readUTF()))
            throw new IOException("illegal format of stream content");
        Field field = (Field) is.readObject();
        //System.out.println("GameView.load() field= " + System.identityHashCode(field));
        start(field);
        // reload images
        for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
            Figure f = (Figure) i.next();
            if (f instanceof FigureImpl)
                ((FigureImpl)f).setImage(getGameRules().getImage(f));
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
        return (GameRules) instantiate(gameRules);
    } 
    /**
     * @deprecated Use {@link #createGameRules(String)} instead.
     */
    protected GameRules getGameRules(String gameRules) throws Exception {
        return createGameRules(gameRules);
    }
    /**
     * @internal see javax.xml.parser.FactoryFinder#findClassLoader() for a version that - supposedly - runs under every JVM.
     */
    private static final Object instantiate(String className) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null)
            cl = ClassLoader.getSystemClassLoader();

        try {
            return Class.forName(className, true, cl).newInstance();
        } catch (ClassNotFoundException ex) {
            cl = GameView.class.getClassLoader();
            if (cl == null)
                cl = ClassLoader.getSystemClassLoader();
            return Class.forName(className, true, cl).newInstance();
        }
            
    }

    public void paint(Graphics g) {
        paintComponents(g);
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
        return "Generic Java applet displaying strategic board games. Copyright " + signe.getCreated() + " by " + signe.getCreator();
    } 

    /**
     * Parameter Info
     */
    public String[][] getParameterInfo() {
        String[][] info = {
            {"gameName",  "String", "name of the specific game"},
            {"gameRules", "String", "parameter describing the GameRules. Per default this is the name of a class that implements " + GameRules.class},
            {"player-X",  "String", "the argument to pass when starting player number X. If left out, a human player will play X."}
        };
        return info;
    } 
}


/**
 * .
 * @author  Timothy Prinzing
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @version $Id$
 * @internal see Notepad for more features
 */
class AwtResourceDecoder {
    private final ResourceBundle resources;
    private final Dictionary     actions;
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
     * hashtable so that it can be fetched.
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
