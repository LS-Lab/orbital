/*
 * @(#)NaturalComparator.java 1.0 2002-08-03 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Comparator;

/**
 * A natural comparator comparing per {@link Comparable}.
 *
 * @version 1.0, 2002-08-03
 * @author  Andr&eacute; Platzer
 */
final class NaturalComparator implements Comparator  {
    // implementation of java.util.Comparator interface

    /**
     *
     * @param param1 <description>
     * @return <description>
     */
    public boolean equals(Object param1)
    {
	return param1 instanceof NaturalComparator;
    }

    /**
     *
     * @param param1 <description>
     * @param param2 <description>
     * @return <description>
     */
    public int compare(Object param1, Object param2)
    {
	return ((Comparable)param1).compareTo(param2);
    }

}
