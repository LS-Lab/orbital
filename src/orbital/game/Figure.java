/**
 * @(#)Figure.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
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
 * Representation of a generic figure.
 * <p>
 * Figures belong to a league and have a specified type.
 * An array of legal moves declares their possibilities to move and/or
 * beat other figures. Which of those legal moves are valid in a certain position
 * is determined by {@link #movePath(Move)}.
 * 
 * @stereotype &laquo;Structure&raquo;
 * @version 1.0, 13/07/98
 * @author  Andr&eacute; Platzer
 */
public class Figure extends Moving {
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

    /**
     * The Field-Container parent including ALL figures moving around on it.
     * @serial
     * @internal see #movePath(orbital.robotic.Move)
     */
    private Field			field = null;

    // common generic figure properties

    /**
     * The league that this figure belongs to.
     * @serial
     */
    public int				league;

    /**
     * The general type of this figure.
     * @serial
     */
    public int				type;

    /**
     * The Image displayed for this figure.
     */
    public transient Image  image;

    /**
     * The Moves that this figure can do legally.
     * @serial
     */
    private Move			legalMoves[];

    /**
     * Construct a new Figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param fld the field that this figure stands on. Can be automatically set by {@link Field#setFigure(Position, Figure)}
     * @param x the positions x-part.
     * @param y the positions y-part.
     * @param dir the direction into which this figure is looking.
     * @param leag the league we belong to.
     * @param typ the type we have.
     * @param img the image to be displayed for this figure.
     * @param legals the moves that this figure can do legally.
     */
    public Figure(Field fld, int x, int y, Direction dir, int leag, int typ, Image img, Move[] legals) {
	this(x, y, dir, leag, typ, img);
	setField(fld);
	setLegalMoves(legals);
    }

    /**
     * Construct a new Figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param x the positions x-part.
     * @param y the positions y-part.
     * @param dir the direction into which this figure is looking.
     * @param leag the league we belong to.
     * @param typ the type we have.
     * @param img the image to be displayed for this figure.
     * @param legals the moves that this figure can do legally.
     */
    public Figure(int x, int y, Direction dir, int leag, int typ, Image img, Move[] legals) {
	this(null, x, y, dir, leag, typ, img, legals);
    }

    /**
     * Construct a new Figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param x the positions x-part.
     * @param y the positions y-part.
     * @param dir the direction into which this figure is looking.
     * @param leag the league we belong to.
     * @param typ the type we have.
     * @param img the image to be displayed for this figure.
     */
    public Figure(int x, int y, Direction dir, int leag, int typ, Image img) {
	super(x, y, dir);
	this.league = leag;
	this.type = typ;
	this.image = img;
    }
    public Figure(int x, int y, int leag, int typ, Image img) {
	this(x, y, new Direction(Direction.North), leag, typ, img);
    }
    public Figure(int x, int y, int leag, int typ) {
	this(x, y, leag, typ, null);
    }

    /**
     * Construct an explicit empty field.
     * Fields are implicitly empty if they are <code>null</code>.
     */
    public Figure(int x, int y) {
	this(x, y, NOONE, EMPTY, null);
    }

    // get/set methods and administratives

    /**
     * Checks whether two figures are equal in position, league, and type, regardless of the field.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Figure) {
	    Figure f = (Figure) obj;
	    return super.equals(obj) && (league == f.league) && (type == f.type);
	} 
	return false;
    } 

    public int hashCode() {
	return league + 17 * type + 101 * super.hashCode();
    } 

    // get/set properties

    /**
     * Get the field which this figure is contained in.
     * @return the Field-Container parent including ALL figures moving around on it.
     */
    public final Field getField() {
	return field;
    } 

    /**
     * Set the field which this figure is contained in.
     */
    final void setField(Field f) {
	this.field = f;
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
     * Returns whether this Figure represents an empty Figure.
     * An explicit Figure different from <code>null</code> is regarded empty
     * if its league is NOONE or its type is EMPTY.
     * @see #NOONE
     * @see #EMPTY
     */
    public boolean isEmpty() {
	return league == NOONE || type == EMPTY;
    } 

    /**
     * Makes this Figure representing an empty figure.
     */
    public void setEmpty() {
	setLeague(NOONE);
	setType(EMPTY);
	setImage(null);
    } 

    /**
     * Get the image currently used to display this figure.
     */
    public Image getImage() {
	return image;
    } 

    /**
     * Set the image currently used to display this figure.
     */
    public void setImage(Image image) {
	this.image = image;
    } 

    /**
     * Gets the set of Moves that this Figure can perform legally.
     * Moves do also include beats.
     */
    public Move[] getLegalMoves() {
	return legalMoves;
    } 

    /**
     * Sets the set of Moves that this Figure can perform legally.
     * Moves do also include beats.
     */
    public void setLegalMoves(Move moves[]) {
	legalMoves = moves;
    } 
    
    public Dimension getPreferredSize() {
	Image image = getImage();
	if (image == null)
	    return null;
	//XXX: how do we know of the Gameboard that is our ImageObserver, for images comming up later?
	Dimension dim = new Dimension(image.getWidth(null), image.getHeight(null));
	return dim.width >= 0 && dim.height >= 0 ? dim : null;
    }

    /**
     * Returns an iterator over all Moves for this Figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     */
    public final Iterator iterator() {
	return Arrays.asList(legalMoves).iterator();
    } 

    /**
     * Returns an iterator over all Moves <dfn>valid</dfn> for this Figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     * Validity of a path to move is checked via {@link #movePath(Move)}.
     * @see #movePath(Move)
     */
    public final Iterator iterateValid() {
	final List v = new ArrayList(legalMoves.length);
	for (int i = 0; i < legalMoves.length; i++) {
	    Position destination = movePath(legalMoves[i]);
	    // reaches legally => Move valid
	    if (destination != null)
		v.add(legalMoves[i]);
	} 
	return v.iterator();
    } 

    /**
     * Returns an iterator over all pairs of (Moves|destinations) <dfn>valid</dfn> for this Figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     * Validity of a path to move is checked via {@link #movePath(Move)}.
     * @see #iterateValid()
     * @see #movePath(Move)
     * @post &forall;i&isin;RES moveFigure(i.A).equals(i.B)
     * @todo introduce iteratePossiblePairs() returns an iterator over all pairs of (Moves|destinations) valid and <dfn>possible</dfn> for this Figure, i.e. if our league is allowed to move at all.
     */
    public final Iterator iterateValidPairs() {
	final List v = new ArrayList(legalMoves.length);
	for (int i = 0; i < legalMoves.length; i++) {
	    Move	 move = legalMoves[i];
	    Position destination = movePath(move);
	    if (destination != null)					 // reaches legally => Move valid
		v.add(new Pair/*_<Move,Position>_*/(move, destination));	 //TODO: Use a Map instead? List(new KeyValuePair())
	} 
	return v.iterator();
    } 


    // methods used to perform moves

    /**
     * Moves this Figure performing a given movement.
     * <p>
     * Note that this method does not change the corresponding field.</p>
     * @return the destination position that was reached after the move
     * or <code>null</code> if the Move was invalid and therefore cancelled.
     * @throws IllegalArgumentException if this move is not in the list of legal moves.
     * @see Field#move(Position, Move)
     * @see #movePath(Move)
     * @see #moving
     * @see orbital.logic.functor.Function
     */
    public Position moveFigure(Move move) {
	if (move == null)
	    throw new NullPointerException();
	// contained in legalMoves?
	for (int i = 0; i < legalMoves.length; i++)
	    if (legalMoves[i].equals(move)) {
		Position destination = movePath(move);
		return destination != null && moving(move, destination) ? destination : null;
	    } 
	throw new IllegalArgumentException("illegal move");
    } 

    /**
     * Moves this Figure to a given Position performing a movement.
     * <p>
     * Note that this method does not change the corresponding field.</p>
     * @return whether the move was done validly, or was invalid and therefore cancelled.
     * @see Field#move(Position, Position)
     * @see #findValidPath(Position)
     * @see #moving
     * @see orbital.logic.functor.Function
     */
    public boolean moveFigure(Position destination) {
	if (destination == null)
	    throw new NullPointerException();
	Move move = findValidPath(destination);
	if (move == null)
	    return false;
	// valid?
	return moving(move, destination);
    } 


    /**
     * Called back when this Figure is moving.
     * Has the final decision whether this Figure could the Move to the given position
     * or if that would break the rules.
     * <p>
     * This implementaion simply returns true.
     * If additional action should occur (f.ex. keeping track of whose turn it is)
     * derived methods must provide this. If beats are only valid to foreign Figures,
     * the methods in the subclasses must check for it.
     * @param move a valid Move, <em>really reaching</em> the given destination.
     * @param destination the destination reached by the move.
     * @pre &exist;i move.equals(getLegalMoves()[i]) &and; movePath(move).equals(destination)
     * @return whether the move sticks to the rules,
     *  provided that the Move itself is a legal move and reaches the given position on the board.
     */
    protected boolean moving(Move move, Position destination) {
	return true;
    } 


    // methods for supporting all Moves for this figure

    /**
     * Hypothetically finds a valid path to a destination Position from a given set of legal Moves.
     * <p>works like: <code>?- movePath(X)==destination.</code>
     * @return the first legal Move-Path which reaches the destination Position.
     *  <code>null</code> if destination Field cannot be reached at all, or is not empty but cannot be beaten.
     * @post (&exist;i RES.equals(getLegalMoves()[i]) &and; movePath(RES).equals(destination)) xor RES == null
     * @see #movePath(Move)
     */
    protected Move findValidPath(final Position destination) {
	for (int i = 0; i < legalMoves.length; i++) {
	    Position p = movePath(legalMoves[i]);

	    // if (move.movement.indexOf(Move.Teleport)!=-1)
	    //      // well Teleport reaches destination
	    //      return i;
			
	    // reaches destination?
	    if (destination.equals(p)) {
		assert p != null : "destination.equals(null) == false";
		return legalMoves[i];
	    }
	} 
	return null;
    } 

    /**
     * Hypothetically tries a given movement and returns the position where the move would end,
     * or <code>null</code> if the move is invalid and does not lead anywhere.
     * <p>
     * Checks whether a movement path is valid on all fields passed on the way.
     * It is valid when each field in between (and including)
     * the current position and the end of the move
     * <ul class="or">
     *   <li>is
     *     <ul class="and">
     *       <li>on the field</li>
     *       <li>and fulfills isEmpty()</li>
     *     </ul>
     *   </li>
     *   <li>or
     *     <ul class="and">
     *       <li>the move beats(<code>*</code>) at that position
     *       <li>and the figure there fulfills !isEmpty()</li>
     *     </ul>
     *   </li>
     *   <li>or
     *     <ul class="and">
     *       <li>the move allows jumping(<code>_</code>) there.
     *     </ul>
     *   </li>
     * </ul>
     * This method will not check for opposing leagues since moves for covering-checks
     * will also be returned as valid.
     * @param move the movement to try.
     * @pre {@link #field} contains the current field considered whether the path is empty
     * @return the destination position if the given move is passable
     *  or <code>null</code> if the movement path is invalid.
     * @see #isEmpty()
     * @see #field
     * @see Move
     * @todo optimize this hotspot
     */
    final Position movePath(final Move move) {
	if (move == null)
	    throw new NullPointerException("null is not a move");
	boolean   was_jumping = false;	  					// can jump this step
	final Moving hyp = (Moving) super.clone();				//@todo should we transform this to new Moving(x, y, direction.clone()) such that we don't get a Figure, here?
	final String movement = move.movement;

	moves:
	for (int i = 0; i < movement.length(); i++) {
	    switch (movement.charAt(i)) {
	    case Move.Teleport:
		throw new UnsupportedOperationException("teleport not yet supported");	  // break moves;				//@TODO: what's then? How do we find the target at all
	    case Move.Jumping:
		was_jumping = true;
		continue moves;
	    case Move.Sloping:
		i++;	// skip the step after Sloping to perform two steps as one
		hyp.move(movement.charAt(i++));	// move first part w/o validation checks
		break;
	    case Move.Beating:
		if (field.isEmpty(hyp) && !was_jumping)	// whether field.getFigure(hyp).league==league is checked later on in another method
		    return null;
		was_jumping = false;
		continue moves;
	    default:
		break;
	    }
	    hyp.move(movement.charAt(i));
	    // left the the Field?
	    if (!field.inRange(hyp))
		return null;

	    if (was_jumping)
		// when jumping, it doesn't matter who's on the field
		was_jumping = false;
	    else if (!(move.isBeating(i + 1) || field.isEmpty(hyp)))
		// move and not beat => must be empty
		return null;
	} 

	return hyp;
    } 

    /**
     * Paint a representation of this Figure.
     * <p>
     * This implementation displays the Figure's image.</p>
     * @param g the Graphics object to paint to.
     * @param box the rectangle within graphics object into which we should paint.
     */
    public void paint(Graphics g, Rectangle box) {
	if (image != null)
	    g.drawImage(image, box.x, box.y, box.width, box.height, null);
    } 

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return getClass().getName() + "[" + league + ":" + type + " @(" + x + '|' + y + ' ' + direction.toString() + ")]";
    } 
}
