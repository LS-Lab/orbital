/*
 * @(#)Direction.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.io.Serializable;

/**
 * Direction class is offering <code>turn()</code> support for absolute north/east/south/west Directions
 * and relative left/right/back Direction-manipulation.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class Direction implements Cloneable, Serializable {
    private static final long serialVersionUID = -3508145947359548778L;
    /**
     * enumeration of absolute direction constants in degree.
     */
    public static final int East = 0;
    public static final int SouthEast = 45;
    public static final int South = 90;
    public static final int SouthWest = 135;
    public static final int West = 180;
    public static final int NorthWest = 225;
    public static final int North = 270;
    public static final int NorthEast = 315;

    /**
     * enumeration of relative direction constants
     */
    public static final int Left = -90;
    public static final int HalfLeft = -45;
    public static final int HalfRight = +45;
    public static final int Right = +90;
    public static final int Back = +180;
    public static final int For = 0;

    /**
     * The current Direction, one of the absolute ones.
     * @serial
     */
    public int				direction;
    public Direction(int dir) {
	direction = dir;
    }
    public Direction() {
	this(North);
    }
    public Direction(Direction dir) {
	this(dir.direction);
    }


    /**
     * Creates a clone of the object. A new instance is allocated and a
     * copied clone of the current object is placed in the new object.
     * @return		a clone of this Object.
     * @throws	OutOfMemoryError If there is not enough memory.
     */
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException imp) {
	    throw new InternalError("clone not supported though Cloneable");
	} 
    } 

    /**
     * Checks whether two Directions are the same.
     */
    public boolean equals(Object obj) {
	if (obj instanceof Direction)
	    return direction == ((Direction) obj).direction;
	return false;
    } 

    public int hashCode() {
	return direction;
    } 

    /**
     * Sets the absolute direction. Reorients into a new absolute direction.
     * @param new_Dir the new absolute direction to be set.
     */
    public void setDirection(int new_Dir) {
	direction = new_Dir;
    } 

    /**
     * Gets the absolute direction integer.
     */
    public int getDirection() {
	return direction;
    } 
    
    /**
     * Get the direction vector pointing in this direction.
     */
    public Position getDirectionVector() {
	switch (direction) {
	case East:		return new Position( 1,  0);
	case SouthEast:	return new Position( 1,  1);
	case South:		return new Position( 0,  1);
	case SouthWest:	return new Position(-1,  1);
	case West:		return new Position(-1,  0);
	case NorthWest:	return new Position(-1, -1);
	case North:		return new Position( 0, -1);
	case NorthEast:	return new Position( 1, -1);
	default: throw new IllegalStateException("illegal direction: " + direction);
	}
    }

    /**
     * relatively turns current Direction.
     */
    public void turn(int rel_turn_dir) {
	direction = getTurned(rel_turn_dir);
    } 

    /**
     * for optimization purpose.
     * @see #turn(int)
     * @see Moving#slide(int)
     */
    final int getTurned(final int rel_turn_dir) {
	//return (direction + rel_turn_dir + 360) % 360;
	int dir = (direction + rel_turn_dir) % 360;
	return dir < 0 ? dir + 360 : dir;
    } 

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return nameOfAbsolute(direction);
    } 
    public static final String nameOfAbsolute(int direction) {
	return absDirections[(int) (direction / 45)];
    } 
    public static final String nameOfRelative(int direction) {
	return relDirections[2 + (int) (direction / 45)];
    } 
    private static final String absDirections[] = {
	"East", "SouthEast", "South", "SouthWest", "West", "NorthWest", "North", "NorthEast"
    };
    private static final String relDirections[] = {
	"Left", "HalfLeft", "For", "HalfRight", "Right", null, "Back", null, null, null, "For"
    };
}
