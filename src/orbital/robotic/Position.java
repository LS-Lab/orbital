/*
 * @(#)Position.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.awt.Point;
import java.io.Serializable;

/**
 * Position class is a Point in space. It can move absoluteley and translate relatively.
 * 
 * @version 0.9, 04/05/98
 * @author  Andr&eacute; Platzer
 * @TODO: transform to n-dimensional point and provide n-dimensional(?) directions, as well
 */
public class Position extends Point implements Serializable {
    private static final long serialVersionUID = 7705012188222777623L;
    public Position(int x, int y) {
	super(x, y);
    }
    public Position(Position b) {
	super(b);
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof Position))
	    return false;
	Position b = (Position) o;
	return x == b.x && y == b.y;
    }
	
    public int hashCode() {
	return x ^ y;
    }

    /**
     * Move to a new position.
     */
    public void move(Position p) {
	super.move(p.x, p.y);
    } 

    /**
     * Translates this position.
     * @see #add(Position)
     */
    public void translate(Position p) {
	super.translate(p.x, p.y);
    } 

    // arithmetics with Positions:

    /**
     * Add a positions components.
     * Returns a Position which is equivalent to this, translated by B.
     * @see #translate(Position)
     */
    public Position add(Position B) {
	return new Position(x + B.x, y + B.y);
    } 
    public Position subtract(Position B) {
	return new Position(x - B.x, y - B.y);
    } 
    public double length() {
	return Math.sqrt((double) x * x + y * y);
    } 

    /**
     * Checks whether the transformation whould be cross, i. e. horizontal or vertical.
     * This method returns <code>true</code> if adding it to a Position would move it in a cross direction.
     */
    public static boolean isCross(Position d) {
	return ((Math.abs(d.x) == 0) ^ (Math.abs(d.y) == 0));
    } 

    /**
     * Checks whether the transformation whould be diagonal.
     * This method returns <code>true</code> if adding it to a Position would move it in a diagonal direction.
     */
    public static boolean isDiagonal(Point d) {
	return (Math.abs(d.x) == Math.abs(d.y));
    } 

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return getClass().getName() + "[" + x + '|' + y + "]";
    } 
}
