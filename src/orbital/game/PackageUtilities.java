/**
 * @(#)PackageUtilities.java 1.1 2003-01-01 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.robotic.*;

import java.util.Iterator;
import orbital.util.Pair;

/**
 * PackageUtilities.java
 *
 * @stereotype Utilities
 * @version 1.1, 2003-01-01
 * @author  Andr&eacute; Platzer
 */
final class PackageUtilities {

    /**
     * prevent instantiation - final static utility class
     */
    private PackageUtilities() {}

    /**
     * Hypothetically finds a valid path to a destination position from a given set of legal moves.
     * <p>works like:
     * <code lang="prolog">?- movePath(X)==destination.</code>
     * @return the first legal move-path which reaches the destination position.
     *  <code>null</code> if destination Field cannot be reached at all, or is not empty but cannot be beaten.
     * @postconditions (&exist;i RES.equals(getLegalMoves()[i]) &and; movePath(RES).equals(destination)) xor RES == null
     * @see #movePath(Move)
     */
    public static final Move findValidPath(final Figure source, final Position destination) {
	for (Iterator i = source.validMoves(); i.hasNext(); ) {
	    Pair     p = (Pair) i.next();
	    Move     move = (Move) p.A;
	    Position dst = (Position) p.B;

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

    
}// PackageUtilities
