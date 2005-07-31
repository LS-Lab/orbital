/**
 * @(#)FieldChangeListener.java 1.1 2003-01-03 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import java.util.EventListener;

/**
 * Interface for listeners on changes occuring on a field.
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see Field
 */
public interface FieldChangeListener extends EventListener {

    // low-level

    /**
     * Invoked when a new figure was set at a position of the field.
     */
    void componentChanged(FieldChangeEvent event);

    // higher-level

    /**
     * Invoked when a figure has moved.
     */
    void movePerformed(FieldChangeEvent event);

    /**
     * Invoked at the end of a state.
     * For example, at the end of turn, or at the end of game when the winner is known.
     */
    void stateChanged(FieldChangeEvent event);
}// FieldChangeListener
