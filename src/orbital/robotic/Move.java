/*
 * @(#)Move.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.io.Serializable;

/**
 * A Move consists of a movement which is a sequence of single
 * step Moves. It is represented as a String.
 * <p>
 * Move class is including constants to name possible Moves.</p>
 * 
 * @version 0.9, 09/07/98
 * @author  Andr&eacute; Platzer
 */
public class Move implements Serializable {

    // named Move constants

    /**
     * don't move just stay
     */
    public static final char Rest = ' ';

    /**
     * next step leads everywhere.
     */
    public static final char Teleport = '#';

    /**
     * next step is jumping over things.
     * If the next step is an ordinary move, then it does bypass a nonempty field.
     * If it specifies a beat, then absent (empty) things are ignored such that
     * a normal move is performed instead.
     */
    public static final char Jumping = '_';

    /**
     * moves sloping. such that the next two steps are compound and performed as one
     */
    public static final char Sloping = '/';	   // schräg:  "/LF"

    /**
     * will beat other things which hinder moving on this step.
     */
    public static final char Beating = '*';

    /**
     * Turn (right) relative to current direction.
     */
    public static final char Right = 'r';

    /**
     * Step (right) sideward relative to current direction
     */
    public static final char RightW = 'R';
    public static final char For = 'f';
    public static final char ForW = 'F';
    public static final char Left = 'l';
    public static final char LeftW = 'L';
    public static final char Back = 'b';
    public static final char BackW = 'B';

    public static final char All = 'a';
    public static final char AllW = 'A';

    /**
     * turn absolute to the east
     */
    public static final char East = 'e';

    /**
     * step absolutely eastward
     */
    public static final char EastW = 'E';
    public static final char North = 'n';
    public static final char NorthW = 'N';
    public static final char West = 'w';
    public static final char WestW = 'W';
    public static final char South = 's';
    public static final char SouthW = 'S';

    /**
     * Contains the sequence of Moves. Called movement or path.
     * @serial
     */
    public String			 movement;

    public Move(char mv) {
	movement = "" + mv;
    }
    public Move(String mv) {
	movement = mv;
    }
    public Move() {
	this(Rest);
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof Move))
	    return false;
	return movement.equals(((Move) o).movement);
    }
	
    public int hashCode() {
	return movement.hashCode();
    }

    /**
     * get the length of the movement.
     * @see #movement
     */
    public final int length() {
	return movement.length();
    } 

    /**
     * Checks whether this move contains a beat(*) at all.
     */
    public final boolean isBeating() {
	return movement.indexOf(Beating) >= 0;
    } 

    /**
     * Checks whether this move is beating(*) at a step.
     * @param istep the index where to begin the search for the next beat.
     * @return whether istep or the next step following istep that is no jump(_), is a beat(*).
     */
    public final boolean isBeating(int istep) {
	if (istep < 0 || istep >= movement.length())
	    return false;

	// skip all Jumping chars after istep
	while (movement.charAt(istep) == Jumping)
	    if (++istep >= movement.length())
		return false;

	// is the next step Beating?
	if (movement.charAt(istep) == Beating)
	    return true;
	return false;
    } 

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return getClass().getName() + "[" + movement + "]";
    } 
}
