/**
 * @(#)FigureImpl.java 1.1 2003-01-01 Andre Platzer
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
 * Implementation of a figure or piece of a board game.
 * <p>
 * In this implementation, an array of legal moves declares their possibilities
 * to move and/or beat other figures.
 * 
 * @stereotype Structure
 * @version 1.1, 2003-01-01
 * @author  Andr&eacute; Platzer
 * @internal  Which of the legal moves are valid in a certain situation
 * is determined by {@link #movePath(Move)}.
 */
public class FigureImpl extends Figure {
    private static final long serialVersionUID = 5872415441343592642L;
    /**
     * The Field-Container parent including all figures moving around on it.
     * @serial
     * @internal see #movePath(orbital.robotic.Move)
     */
    private Field field = null;

    /**
     * The Image displayed for this figure.
     * @xxx couldn't we also move this to subclass FigureImpl?
     */
    private transient Image image;

    /**
     * The Moves that this figure can do legally.
     * @serial
     */
    private Move legalMoves[];

    /**
     * Construct a new figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param fld the field that this figure stands on. Can also be set automatically by {@link Field#setFigure(Position, Figure)}
     * @param x the positions x-part.
     * @param y the positions y-part.
     * @param dir the direction into which this figure is looking.
     * @param leag the league we belong to.
     * @param typ the type we have.
     * @param img the image to be displayed for this figure.
     * @param legals the moves that this figure can do legally.
     */
    public FigureImpl(Field fld, int x, int y, Direction dir, int leag, int typ, Image img, Move[] legals) {
	super(x, y, dir, leag, typ);
	setImage(img);
	setField(fld);
	setLegalMoves(legals);
    }

    /**
     * Construct a new figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param x the positions x-part.
     * @param y the positions y-part.
     * @param dir the direction into which this figure is looking.
     * @param leag the league we belong to.
     * @param typ the type we have.
     * @param img the image to be displayed for this figure.
     * @param legals the moves that this figure can do legally.
     */
    public FigureImpl(int x, int y, Direction dir, int leag, int typ, Image img, Move[] legals) {
	this(null, x, y, dir, leag, typ, img, legals);
    }

    /**
     * Construct a new figure positioned at (x|y) looking into a direction,
     * of a league and of a certain type.
     * @param x the positions x-part.
     * @param y the positions y-part.
     * @param dir the direction into which this figure is looking.
     * @param leag the league we belong to.
     * @param typ the type we have.
     * @param img the image to be displayed for this figure.
     */
    public FigureImpl(int x, int y, Direction dir, int leag, int typ, Image img) {
	super(x, y, dir, leag, typ);
	setImage(img);
    }
    public FigureImpl(int x, int y, int leag, int typ) {
	this(x, y, new Direction(Direction.North), leag, typ, null);
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
     * Gets the set of moves that this figure can perform legally.
     * Moves do also include beats.
     */
    public Move[] getLegalMoves() {
	return legalMoves;
    } 

    /**
     * Sets the set of moves that this figure can perform legally.
     * Moves do also include beats.
     */
    public void setLegalMoves(Move moves[]) {
	legalMoves = moves;
    } 
    
    public void setEmpty() {
	super.setEmpty();
	setImage(null);
    } 

    public Dimension getPreferredSize() {
	Image image = getImage();
	if (image == null)
	    return null;
	//XXX: how do we know of the Gameboard that is our ImageObserver, for images comming up later?
	Dimension dim = new Dimension(image.getWidth(null), image.getHeight(null));
	return dim.width >= 0 && dim.height >= 0 ? dim : null;
    }

    public void paint(Graphics g, Rectangle box) {
	Image image = getImage();
	if (image != null)
	    g.drawImage(image, box.x, box.y, box.width, box.height, null);
    } 

    /**
     * Returns an iterator over all moves for this figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     */
    public final Iterator/*_<Move>_*/ iterator() {
	return Arrays.asList(legalMoves).iterator();
    } 

    /**
     * Returns an iterator over all moves <dfn>valid</dfn> for this figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     * Checks validity of a path to move per {@link #movePath(Move)}.
     * @deprecated Since Orbital1.1 use {@link Figure#possibleMoves()} instead.
     */
    public final Iterator/*_<Move>_*/ iterateValid() {
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
     * Returns an iterator over all pairs of (moves|destinations) <dfn>valid</dfn> for this figure.
     * Use the Iterator methods on the returned object to fetch
     * the elements sequentially.
     * Checks validity of a path to move per {@link #movePath(Move)}.
     * @see #iterateValid()
     * @postconditions &forall;i&isin;RES moveFigure(i.A).equals(i.B)
     * @todo introduce iteratePossiblePairs() returns an iterator over all pairs of (moves|destinations) valid and <dfn>possible</dfn> for this figure, i.e. if our league is allowed to move at all.
     * @deprecated Since Orbital1.1 use {@link Figure#validMoves()} instead.
     */
    public final Iterator/*_<Move,Position>_*/ iterateValidPairs() {
	return possibleMoves();
    } 


    /**
     * @internal Checks validity of a path to move per {@link #movePath(Move)}.
     */
    public /*final*/ Iterator/*_<Move,Position>_*/ possibleMoves() {
	final List v = new ArrayList(legalMoves.length);
	for (int i = 0; i < legalMoves.length; i++) {
	    Move	 move = legalMoves[i];
	    Position destination = movePath(move);
	    if (destination != null)					 // reaches legally => move valid
		v.add(new Pair/*<Move,Position>*/(move, destination));	 //@TODO: Use a Map instead? List(new KeyValuePair())
	} 
	return v.iterator();
    } 
    
    // methods used to perform moves

    /**
     * Moves this figure performing a given movement.
     * <p>
     * Note that this method does not change the corresponding field.</p>
     * @return the destination position that was reached after the move
     * or <code>null</code> if the Move was invalid and therefore cancelled.
     * @throws IllegalArgumentException if this move is not in the list of legal moves.
     * @post getField() = getField()@pre
     * @see Field#move(Position, Move)
     * @see #movePath(Move)
     * @see #moving
     */
    public Position moveFigure(Move move) {
	if (move == null)
	    throw new NullPointerException();
	// contained in legalMoves?
	for (int i = 0; i < legalMoves.length; i++)
	    if (move.equals(legalMoves[i])) {
		Position destination = movePath(move);
		return destination != null && moving(move, destination) ? destination : null;
	    } 
	throw new IllegalArgumentException("illegal move");
    } 

    /**
     * Called back when this figure is really moving.

     * This method has the final decision whether this figure could do
     * the move to the given position or if that would break the
     * rules.
     * <p>
     * This implementaion simply returns true. If additional
     * actions should take place (f.ex. keeping track of whose turn it is)
     * derived methods must provide this. If beats are only valid to
     * foreign figures, the methods in the subclasses must check for
     * it.
     * </p>
     * @param move a valid Move, <em>really reaching</em> the given destination.
     * @param destination the destination reached by the move.
     * @preconditions &exist;i move.equals(getLegalMoves()[i]) &and; movePath(move).equals(destination)
     * @return whether the move sticks to the rules,
     *  provided that the move itself is a legal move and reaches the given position on the board.
     */
    protected boolean moving(Move move, Position destination) {
	return true;
    } 




    // methods for supporting all moves for this figure

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
     * @preconditions {@link #field} contains the current field considered whether the path is empty
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
	final Field  field = getField();
	boolean      was_jumping = false;	  					// can jump this step
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
	    if (!field.inRange(hyp))
		// left the Field
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

}
