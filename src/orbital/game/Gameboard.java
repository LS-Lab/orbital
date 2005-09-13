/*
 * @(#)Gameboard.java 0.9 1996/03/04 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.game;
import orbital.game.AdversarySearch.Option;

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

import java.util.Iterator;

/**
 * Gameboard class is the view of a field which can be
 * modified by mouse-dragging.
 * 
 * @events FieldChangeEvent.USER_ACTION | FieldChangeEvent.MOVE when user move has been performed.
 * @stereotype UI
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Field
 */
public class Gameboard extends Canvas implements ImageObserver, Serializable {
    private static final long serialVersionUID = 6205399044524716717L;
    private static final Logger logger = Logger.getLogger(Gameboard.class.getName());
    /**
     * The Field displayed.
     * @serial
     */
    private Field field = null;

    /**
     * The position whose figure is currently being dragged elsewhere.
     */
    private transient volatile Position dragging = null;

    /**
     * Whether we currently seem to perform layouting.
     */
    private transient boolean layouting = false;

    public Gameboard() {}
    public Gameboard(Field f) {
        setField(f);
    }

    public Field getField() {
        return field;
    } 

    /**
     * Set the model for this view.
     */
    public void setField(Field f) {
        Field old = field;
        field = f;
        propertyChangeListeners.firePropertyChange("field", old, field);
        invalidate();
        repaint();
        field.addFieldChangeListener(new FieldChangeAdapter() {
                //@internal we are transient
                public void componentChanged(FieldChangeEvent e) {
                    assert e.getField() == getField() : "we have only registered ourselves to our field " + getField() + " source=" + e.getField();
                    assert e.getType() == FieldChangeEvent.SET_FIGURE : "SET_FIGURE assumed";
                    assert e.getChangeInfo() instanceof Position : "assuming position of change is changeInfo";
                    repaint((Position)e.getChangeInfo());
                    // tell our listeners
                    propertyChangeListeners.firePropertyChange("field", null, e.getField());
                }
            });
    } 

    /**
     * @deprecated Since Orbital1.1 use {@link #getField()}.{@link #getFigure(Position)} instead.
     */
    public Figure getFigure(Position p) {
        return field.getFigure(p);
    }

    /**
     * Set a figure on the field with automatic painting.
     * @see Field#setFigure(Position, Figure)
     * @see #repaint(Position)
     * @deprecated Since Orbital1.1 use {@link #getField()}.{@link #setFigure(Position,Figure)} instead.
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
     * @internal we only need this for backward compatibility issues.
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
     * Returns the field-position (i|j) of a graphical point (x|y).
     * @see Field#boundsOf(Rectangle, Position)
     */
    public Position figureOn(int x, int y) {
        if (field == null)
            return null;
        Dimension dim = field.getDimension();
        return new Position(x * dim.width / size().width, y * dim.height / size().height);        // @version 1.1
    } 

    // view
    
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

    // controller
    
    public boolean mouseDown(Event evt, int x, int y) {
        dragging = figureOn(x, y);
        return true;
    } 
    public boolean mouseUp(Event evt, int x, int y) {
        Position src = dragging;
        //@internal if EventHandler wasn't synchronizing events, concurrent synchronization could be required since dragging could already have changed again
        dragging = null;
        if (src == null || field.getFigure(src) == null)
            // ignore
            return super.mouseUp(evt, x, y);
        Position dst = figureOn(x, y);
        if (dst == null /*@xxx remove condition || src.equals(dst)*/)
            // ignore
            return super.mouseUp(evt, x, y);

        //@xxx only allow an operation for real players!

        Move moveToDst = findValidPath(field.getFigure(src), dst);
        // would this be a correct move?
        boolean validOperation = moveToDst != null;
        Field nextField = null;
        try {
            if (moveToDst != null) {
                nextField = (Field)field.clone();
                validOperation &= nextField.move(src, moveToDst);
            }
        }
        catch (CloneNotSupportedException cannotMoveHypothetically) {
            // cannot precheck user move, nor predict result
            logger.log(Level.FINE, "cannot precheck user move {0}-->{1} because of {2}", new Object[] {src, dst, cannotMoveHypothetically});
        }

        if (validOperation)
            field.getFieldChangeMulticaster().movePerformed(new FieldChangeEvent(field, FieldChangeEvent.USER_ACTION | FieldChangeEvent.MOVE,
                                                                                 new Option(nextField, dst, field.getFigure(src), moveToDst)));
        else {  // nope, hmm, one of the users wasn't making too much sense of his move
            logger.log(Level.WARNING, "Wrong move {0}-->{1}", new Object[] {src, dst});
        }
        return true;
    } 

    // how to move a figure to a destination on user click

    /**
     * Finds a valid path to a destination position.
     * <p>works like:
     * <code lang="prolog">?- movePath(X)==destination.</code></p>
     * <p>
     * You can overwrite this method if you have special case rules to determine
     * which exact move to perform upon user request. Also overwrite if you want to
     * let the user choose which one of multiple possible moves to the destination
     * he intended.
     * </p>
     * @return any legal move-path from the source figure which reaches the destination position.
     *  <code>null</code> if destination field cannot be reached at all, or is not empty but cannot be beaten.
     * @postconditions (&exist;i RES.equals(getLegalMoves()[i]) &and; movePath(RES).equals(destination)) xor RES == null
     * @internal @see Figure#possibleMoves()
     */
    protected Move findValidPath(final Figure source, final Position destination) {
        for (Iterator i = source.possibleMoves(); i.hasNext(); ) {
            //@internal we do not need Option.getField() here
            Option   o = (Option) i.next();
            Move     move = o.getMove();
            Position dst = o.getDestination();

            // if (move.movement.indexOf(Move.Teleport)!=-1)
            //      // well Teleport reaches destination
            //      return i;
                        
            // reaches destination?
            if (destination.equals(dst)) {
                assert dst != null : "destination.equals(null) == false";
                return move;
            }
        } 
        return null;
    } 

}
