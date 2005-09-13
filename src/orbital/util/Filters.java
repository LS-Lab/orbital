/*
 * @(#)Filters.java 1.0 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import orbital.logic.functor.Function;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * Collection filtering implementations.
 * <p>
 * Implementing classes are usually stateless.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Predicate
 * @see java.util.Collection
 */
public class Filters {

    /**
     * Select <code>*</code>.
     * Selects all data in the collection.
     */
    public static final Function/*<Collection, Collection>*/ all = new Function/*<Collection, Collection>*/() {
            public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
                return d;
            } 
            public String toString() {
                return "*";
            } 
        };
    /**
     * Filter for first element.
     */
    public static final Function/*<Collection, Collection>*/ first = new Function/*<Collection, Collection>*/() {
            public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
                Collection f = new ArrayList(1);
                f.add(((Collection) d).iterator().next());
                return f;
            } 
            public String toString() {
                return "first";
            } 
        };
    /**
     * Filter for last element.
     */
    public static final Function/*<Collection, Collection>*/ last = new Function/*<Collection, Collection>*/() {
            public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
                Collection f = new ArrayList(1);
                Object     l = null;
                for (Iterator i = ((Collection) d).iterator(); i.hasNext(); )
                    l = i.next();
                f.add(l);
                return f;
            } 
            public String toString() {
                return "last";
            } 
        };


    /**
     * Filtering range from min to max.
     * All elements with an index between min and max (including both) are filtered.
     * The first element has index 0.
     */
    public static Function/*<Collection, Collection>*/ ranged(final int min, final int max) {
        return new Function/*<Collection, Collection>*/() {
                public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
                    Collection f = new ArrayList(max - min + 1);
                    int            count = 0;
                    for (Iterator i = ((Collection) d).iterator(); i.hasNext(); ) {
                        Object el = i.next();

                        if (count >= min && count <= max)
                            f.add(el);
                        count++;
                    } 
                    return f;
                } 
                public String toString() {
                    return "ranged(" + min + "," + max + ")";
                } 
            };
    } 

    /**
     * Filter for the first number elements.
     * @see #top(double, boolean)
     */
    public static Function/*<Collection, Collection>*/ top(int number) {
        return ranged(0, number - 1);
    } 
    
    /**
     * Filter for the first number percent elements.
     * @param percent true to filter for the first number percent elements.
     *  false to filter for the first number elements {@link #top(int)}.
     * @preconditions number &isin; [0,100]
     */
    public static Function/*<Collection, Collection>*/ top(final double number, final boolean percent) {
        if (!percent)
            return top((int) number);
        if (!(0 <= number && number <= 100))
            throw new IllegalArgumentException("number must be a valid percentage in [0,100]");
        return new Function/*<Collection, Collection>*/() {
                public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
                    return top((int) Math.ceil(number * ((Collection) d).size() / 100)).apply(d);
                }
            };
    }
    
    //TODO: implement
    /*
     * Best(Weighting)
     *
     public static Function<Collection, Collection> best(final Weighting w) {
     return new Function<Collection, Collection>() {
     public Collection apply(Collection d) {
     }
     }
     }
    */
}
