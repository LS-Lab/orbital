/**
 * @(#)AbstractGameRules.java 0.9 2000/02/26 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import java.awt.Component;
import java.awt.Image;
import orbital.robotic.Move;
import java.io.Serializable;

import java.awt.MediaTracker;
import java.applet.Applet;
import java.awt.Toolkit;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Defines an abstract base class for GameRules.
 * <p>
 * call {@link #loadAllImages(Component) loadAllImages} in startField to load the images
 * named <code><var>prefix</var>+(int)<var>league</var>+"_"+(int)<var>type</var>+<var>suffix</var></code> from the codebase.
 * </p>
 * <p>
 * Implementations should also call {@link #isNextTurn()} once after setting the league
 * that begins the game, perhaps from the <code>startField()</code> implementation.
 * </p>
 * 
 * @version 0.9, 2000/02/26
 * @author  Andr&eacute; Platzer
 */
public abstract class AbstractGameRules implements GameRules, Serializable {
    /**
     * = Figure.NOONE.
     * @see Figure#NOONE
     */
    public static final int   NOONE = Figure.NOONE;

    /**
     * = Figure.EMPTY.
     * @see Figure#EMPTY
     */
    public static final int   EMPTY = Figure.EMPTY;

    private static final int  FIGURE_IMAGE_ID = 17;

    /**
     * the image cache.
     */
    private transient Image images[][];
    /**
     * the prefix for image names.
     * @serial
     */
    private String			  image_prefix;
    /**
     * the suffix for image names.
     * @serial
     */
    private String			  image_suffix;

    /**
     * indicating current league whom this turn is up to.
     * @serial
     */
    private int				  turn = NOONE;

    /**
     * The league of the last turn (i.e. that was the last league to make a move).
     * @see #turn
     * @see #isNextTurn()
     */
    private transient int oldTurn = NOONE;

    /**
     * Create with image naming.
     * named <code>prefix</code><var>league</var>_<var>type</var><code>suffix</code>
     * (or with Java code <code><var>prefix</var> + (int)<var>league</var> + "_" + (int)<var>type</var> + <var>suffix</var></code>)
     * from the codebase.
     * @param prefix the prefix for image names.
     * @param suffix the suffix for image names.
     */
    protected AbstractGameRules(String prefix, String suffix) {
	this.image_prefix = prefix;
	this.image_suffix = suffix;
	images = null;
    }

    /**
     * Create with default image naming.
     * images load are named <code>F<var>leagnum</var>_<var>typnum</var>.gif</code> from the codebase.
     */
    protected AbstractGameRules() {
	this("F", ".gif");
    }

    /**
     * get the current league whose turn it is.
     */
    public int getTurn() {
	return turn;
    } 

    /**
     * set the current league whose turn it is.
     */
    protected void setTurn(int t) {
	this.turn = t;
    } 

    // partial GameRules implementation

    /**
     * Get the image of the figure from cache according to its league and type.
     * @see #images
     */
    public Image getImage(Figure f) {
	int leag = f.league;
	int typ = f.type;
	if (leag == NOONE || typ == EMPTY) {
	    leag = NOONE;
	    typ = EMPTY;
	}
	if (images == null)
	    throw new IllegalStateException("images have not yet been loaded");
	if (leag<0 || images.length<=leag)
	    throw new IllegalArgumentException("illegal league: "+leag);
	if (typ<0 || images[leag].length<=typ)
	    throw new IllegalArgumentException("illegal type: "+typ);
	return images[leag][typ];
    } 

    /**
     * {@inheritDoc}.
     * Will check whether the {@link #getTurn() current turn} and the turn on the last call
     * of this method are distinct.
     */    
    public boolean isNextTurn() {
	if (getTurn() != oldTurn) {
	    oldTurn = getTurn();
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Call to load all Images needed for the Figures.
     * @param component for which component (usually an applet) to load all images.
     * Used for the media tracker except when <code>null</code>.
     * If component is an instance of <code>java.applet.Applet</code>, images are load
     * from its codebase, otherwise from the default toolkit.
     * @see java.awt.MediaTracker
     * @see java.applet.Applet#getImage(java.net.URL,java.lang.String)
     * @see java.awt.Toolkit#getImage(java.net.URL)
     */
    protected void loadAllImages(Component component) {
	// two load possibilities, the other is null!
	Applet  applet = null;
	Toolkit tk = null;

	// excerpt the applet if possible, or get a toolkit
	if (component instanceof Applet)
	    applet = (Applet) component;
	else
	    tk = component.getToolkit();

	this.images = new Image[getLeagues()][getFigureTypes()];
	MediaTracker tracker = component == null ? null : new MediaTracker(component);
	images[NOONE][EMPTY] = null;
	for (int leag = NOONE + 1; leag < getLeagues(); leag++)
	    for (int typ = EMPTY + 1; typ < getFigureTypes(); typ++) {
		Image img = getImage(applet, tk, image_prefix + leag + "_" + typ + image_suffix);
		images[leag][typ] = img;
		if (tracker != null)
		    tracker.addImage(img, FIGURE_IMAGE_ID);
	    } 
	try {
	    tracker.waitForID(FIGURE_IMAGE_ID);
	} catch (InterruptedException irq) {
	    Logger.getLogger(AbstractGameRules.class.getName()).log(Level.WARNING, "initialization had been interrupted", irq);
	    Thread.currentThread().interrupt();
	} 
    } 

    private Image getImage(Applet applet, Toolkit tk, String filename) {
	if (applet != null)
	    return applet.getImage(applet.getCodeBase(), filename);
	else if (tk != null)
	    return tk.getImage(getClass().getResource(filename));
	else
	    throw new RuntimeException("neither applet nor toolkit is available to get images");
    } 

    /**
     * helper to convert from string-array to an array of moves.
     */
    protected static Move[][] stringToMove(String[][] moveStrings) {
	Move[][] moves = new Move[moveStrings.length][];
	for (int fig = EMPTY; fig < moves.length; fig++) {
	    moves[fig] = new Move[moveStrings[fig].length];
	    for (int i = EMPTY; i < moves[fig].length; i++)
		moves[fig][i] = new Move(moveStrings[fig][i]);
	} 
	return moves;
    } 
}