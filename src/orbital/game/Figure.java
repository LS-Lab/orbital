/**
 * @(#)Figure.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.robotic.Moving;
import orbital.robotic.Move;
import orbital.robotic.Position;
import orbital.robotic.Direction;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.util.Iterator;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import orbital.util.Pair;

/**
 * Representation of a generic figure or piece of a board game.
 * <p>
 * Figures belong to a league and have a specified type. Besides those
 * elementary properties, a figure encapsulates the ability of being
 * {@link #moveFigure(Move) moved} or queried for all its
 * {@link #possibleMoves() valid moves}. The implementation of these
 * methods is deferred to subclasses.
 * 
 * @stereotype Structure
 * @version 1.1, 2003-01-01
 * @version 1.0, 1998-07-13
 * @author  Andr&eacute; Platzer
 */
public abstract class Figure extends Moving {
    private static final long serialVersionUID = -3766957702427420203L;
    /**
     * represents the league for empty figures.
     * Empty figures belong to no certain league.
     * @see #EMPTY
     */
    public static final int NOONE = 0;

    /**
     * represents the type for empty figures.
     * @see #EMPTY
     */
    public static final int EMPTY = 0;

    // common generic figure properties

    /**
     * The league that this figure belongs to.
     * @serial
     */
    private int league;

    /**
     * The general type of this figure.
     * @serial
     */
    private int type;

    /**
     * Construct a new figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param x the position's x-part.
     * @param y the position's y-part.
     * @param dir the direction into which this figure is looking.
     * @param league the league we belong to.
     * @param type the type we have.
     */
    public Figure(int x, int y, Direction dir, int league, int type) {
	super(x, y, dir);
	this.league = league;
	this.type = type;
    }

    // get/set methods and administratives

    /**
     * Checks whether two figures are equal in position, league, and type,
     * regardless of the field that they are on.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Figure) {
	    Figure f = (Figure) obj;
	    return super.equals(obj) && (getLeague() == f.getLeague()) && (getType() == f.getType());
	} 
	return false;
    } 

    public int hashCode() {
	return getLeague() + 17 * getType() + 101 * super.hashCode();
    } 

    // get/set properties

    /**
     * Set the field which this figure is contained in.
     * Optional implementation.
     */
    void setField(Field f) {
    } 

    /**
     * Get the league that this figure belongs to.
     */
    public int getLeague() {
	return league;
    } 

    /**
     * Set the league that this figure belongs to.
     */
    public void setLeague(int l) {
	this.league = l;
    } 

    /**
     * Get the general type of this figure.
     */
    public int getType() {
	return type;
    } 

    /**
     * Set the general type of this figure.
     */
    public void setType(int t) {
	this.type = t;
    } 

    /**
     * Returns whether this figure represents an empty figure.
     * An explicit figure different from <code>null</code> is regarded empty
     * if its league is NOONE or its type is EMPTY.
     * @see #NOONE
     * @see #EMPTY
     */
    public boolean isEmpty() {
	return getLeague() == NOONE || getType() == EMPTY;
    } 

    /**
     * Makes this figure representing an empty figure.
     */
    public void setEmpty() {
	setLeague(NOONE);
	setType(EMPTY);
    } 

    // central Figure interface methods

    /**
     * Returns an iterator over all moves (and destinations) <dfn>valid</dfn> for this figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     * @return an iterator over the valid {@link Pair}&lt;{@link Move},{@link Position}&gt;.
     * @postconditions &forall;i&isin;RES moveFigure(i.A).equals(i.B)
     * @todo introduce iteratePossible() returns an iterator over all pairs of (moves|destinations) valid and <dfn>possible</dfn> for this figure, i.e. if our league is allowed to move at all.
     */
    public abstract Iterator/*_<Move,Position>_*/ possibleMoves();

    // methods used to perform moves

    /**
     * Moves this figure performing a given movement.
     * Contrary to {@link Moving#move(Move) super.move(Move)}
     * this method also checks that the move is legal and valid on the current field.
     * <p>
     * Note that this method does not change the corresponding field.</p>
     * @return the destination position that was reached after the move
     * or <code>null</code> if the Move was invalid and therefore cancelled.
     * @throws IllegalArgumentException if this move is not in the list of legal moves.
     * @post getField() = getField()@pre
     * @see Field#move(Position, Move)
     * @internal do not rename to move(Move) like in super class, because some classes need to treat our ordinary Moving behaviour without additional rule checks.
     */
    public abstract Position moveFigure(Move move);

    // for painting view

    /**
     * Get the preferred size for displaying this figure.
     * @see java.awt.Component#getPreferredSize()
     */
    public abstract Dimension getPreferredSize();

    /**
     * Paint a representation of this figure.
     * <p>
     * This implementation displays the figure's image.</p>
     * @param g the Graphics object to paint to.
     * @param box the rectangle within graphics object into which we should paint.
     */
    public abstract void paint(Graphics g, Rectangle box);

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return getClass().getName() + "[" + getLeague() + ":" + getType() + " @(" + x + '|' + y + ' ' + direction.toString() + ")]";
    } 
}
