/*
 * @(#)Gameboard.java 0.9 1996/03/04 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.robotic.*;
import java.awt.Canvas;
import java.awt.image.ImageObserver;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.Image;
import java.io.Serializable;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Gameboard class is an AWT-view of a Field which can be
 * modified by mouse-dragging.
 * 
 * @actions "turnDone" to parent when move/beat has been processed.
 * @stereotype UI
 * @version 0.9, 14/07/98
 * @author  Andr&eacute; Platzer
 */
public class Gameboard extends Canvas implements ImageObserver, Serializable {
    private static final long serialVersionUID = 6205399044524716717L;
    private static final Logger logger = Logger.getLogger(Gameboard.class.getName());
    /**
     * The Field displayed.
     * @serial
     */
    private Field						field = null;

    /**
     * The position whose figure is currently being dragged elsewhere.
     */
    private transient volatile Position dragging = null;

    /**
     * Whether we currently seem to perform layouting.
     */
    private transient boolean			layouting = false;

    public Gameboard() {}
    public Gameboard(Field f) {
	field = f;
    }

    public Field getField() {
	return field;
    } 

    public void setField(Field f) {
	Field old = field;
	field = f;
	propertyChangeListeners.firePropertyChange("field", old, field);
	invalidate();
	repaint();
    } 

    public Figure getFigure(Position p) {
	return field.getFigure(p);
    }

    /**
     * Set a figure on the field with automatic painting.
     * @see Field#setFigure(Position, Figure)
     * @see #repaint(Position)
     */
    public void setFigure(Position p, Figure what) {
	field.setFigure(p, what);
	propertyChangeListeners.firePropertyChange("field", null, field);
	repaint(p);
    }
	
    public Dimension getPreferredSize() {
	layouting = true;
	return field != null ? field.getPreferredSize() : super.getPreferredSize();
    } 

    /**
     * @serial
     */
    private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener l) {
	propertyChangeListeners.addPropertyChangeListener(l);
    } 
    public void removePropertyChangeListener(PropertyChangeListener l) {
	propertyChangeListeners.removePropertyChangeListener(l);
    } 

    /**
     * Returns the Field-Position (i|j) of a graphical Point (x|y).
     * @see Field#boundsOf(Rectangle, Position)
     */
    public Position figureOn(int x, int y) {
	if (field == null)
	    return null;
	Dimension dim = field.getDimension();
	return new Position(x * dim.width / size().width, y * dim.height / size().height);	  // @version 1.1
    } 

    public void paint(Graphics g) {
	update(g);
    } 
    public void update(Graphics g) {
	if (field == null)
	    return;
	field.paint(g, new Rectangle(getSize()));
    } 

    /**
     * Repaint the gameboard at a given position.
     */
    public void repaint(Position p) {
	Graphics g = getGraphics();
	if (g == null)
	    return;
        Rectangle box = new Rectangle(getSize());
        Rectangle fr = field.boundsOf(box, p);
	Graphics fg = g.create(fr.x, fr.y, fr.width, fr.height);
	// local to fg
	fr.setLocation(0, 0);
	fg.setColor((p.x & 1 ^ p.y & 1) == 0 ? Color.white : Color.darkGray);
	fg.fillRect(fr.x, fr.y, fr.width, fr.height);
	Figure f = field.getFigure(p);
	if (f != null)
	    f.paint(fg, fr);
	fg.dispose();
    }		

    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
	if (infoflags == ImageObserver.ALLBITS) {
	    if (layouting)
		doLayout();
	    else
		repaint();
	    layouting = false;
	    return false;
	} 
	return true;
    } 

    public boolean mouseDown(Event evt, int x, int y) {
	dragging = figureOn(x, y);
	return true;
    } 
    public boolean mouseUp(Event evt, int x, int y) {
	Position src = dragging;
	//XXX: if EventHandler wasn't synchronizing events, concurrent synchronization could be required since dragging could already have changed again
	dragging = null;
	if (src == null || field.getFigure(src) == null)
	    // ignore
	    return super.mouseUp(evt, x, y);
	Position dst = figureOn(x, y);
	if (dst == null /*@xxx remove condition || src.equals(dst)*/)
	    // ignore
	    return super.mouseUp(evt, x, y);
	//@xxx only allow an operation for real players!
	Move moveToDst = PackageUtilities.findValidPath(field.getFigure(src), dst);
	boolean validOperation =
	    moveToDst != null && field.move(src, moveToDst);	   // is this a correct move/beat?
	repaint(src);
	repaint(dst);
	if (validOperation)
	    getParent().postEvent(new Event(this, Event.ACTION_EVENT, "turnDone"));
	else {	// nope, hmm, one of the users wasn't making too much sense of his move
	    logger.log(Level.WARNING, "Wrong move", src + "-->" + dst);
	}
	return true;
    } 
}
