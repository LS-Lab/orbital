/*
 * @(#)PatternMatching.java 0.9 1999/02/10 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.text;

import java.util.Collection;

/**
 * PatternMatching is a base interface for pattern matching algorithms.
 * 
 * @version 0.9, 10/02/99
 * @author  Andr&eacute; Platzer
 */
public interface PatternMatching {

    /**
     * Sets the pattern to try and match. Preprocessing of the pattern can be done now.
     * This might be necessary to generate a Parse-Tree for the pattern, or things alike.
     */
    public void setPattern(String pattern);

    /**
     * Get a Collection of Match instances that contain a match
     * of the pattern specified in setPattern() and the text specified here.
     */
    public Collection matches(String text);
}

