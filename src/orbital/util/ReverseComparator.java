/**
 * @(#)ReverseComparator.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Comparator;
import java.io.Serializable;

/**
 * This class is a ReverseComparator which imposes the reverse order
 * on a collection of objects.
 * <p>
 * Depending on whether a Comparator to be reverted is given,
 * the ordering induced by that Comparator will be reverted.
 * Otherwise, the natural ordering will be reverted - as Collections.revertOrder() does -
 * supposing that the objects in the collection implement the Comparable interface.</p>
 * <p>
 * This corresponds roughly to a negation of a comparator (except for the case of equality).</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see java.util.Collections#reverseOrder()
 */
public class ReverseComparator implements Comparator, Serializable {
    private static final long serialVersionUID = -9160553140712258086L;

    /**
     * Contains the Comparator to be inverted, or <code>null</code>
     * if the objects Comparable implementation should be used, instead.
     */
    protected Comparator inner;

    /**
     * Create a Comparator that is the opposite to an ordering induced by a given one.
     * @param inner the Comparator to be inverted, or <code>null</code>
     * if the objects Comparable implementation should be used, instead.
     */
    public ReverseComparator(Comparator inner) {
	this.inner = inner;
    }

    /**
     * Create a Comparator that is the opposite to the natural ordering induced
     * by the Comparable implementation of the objects compared.
     */
    public ReverseComparator() {
	this.inner = null;
    }

    /**
     * Compares two objects.
     * The inner Comparator is used if specified, or one of the arguments are used if they implement Comparable.
     * @return If a Comparator <code>inner!=null</code>, then this method returns <code>inner.compare(o2,o1)</code>.
     *  If <code>inner==null</code> and if one of the objects (preferably o1) is Comparable, <code>-((Comparable)o<var>n</var>).compareTo(o<var>m</var>)</code> is returned.
     *  If no comparator is specified and none of the objects implements Comparable, an exception is thrown.
     * @throws java.lang.ClassCastException if Comparator <code>inner==null</code> and both, o1 and o2 do not implement <tt>Comparable</tt>.
     */
    public int compare(Object o1, Object o2) {
	if (inner != null)
	    return inner.compare(o2, o1);
	if (o1 instanceof Comparable)
	    return -((Comparable) o1).compareTo(o2);
	if (o2 instanceof Comparable)
	    return -((Comparable) o2).compareTo(o1);
	throw new ClassCastException("no Comparator is specified and none of the objects is Comparable");
    } 

    public boolean equals(Object obj) {
	return (obj instanceof ReverseComparator) && inner.equals(((ReverseComparator)obj).inner);
    } 
	
    public int hashCode() {
	return 27 ^ inner.hashCode();
    }
}
