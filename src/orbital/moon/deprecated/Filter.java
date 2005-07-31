/*
 * @(#)Filter.java 0.9 1997/07/06 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import orbital.logic.functor.Function;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import orbital.SP;

/**
 * An abstraction for collection filtering.
 * <p>
 * Implementing classes are usually stateless.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Predicate
 * @see java.util.Collection
 * @deprecated Since Orbital1.0 Use orbital.logic.functor.Function<Collection, Collection> instead.
 * @TODO: com.objectspace.jgl.algorithms.Filtering
 */
public interface Filter extends Function/*<Collection, Collection>*/{

    // TODO: implement Functionals.filter(Iterator) or Setops.filter(...) or change Collection to Iterator or anything

    /**
     * Apply the Filter on a Collection of data to get a filtered Collection.
     * @param data is the original Collection of data
     * @return filtered Collection.
     */
    Object/*>Collection<*/ apply(Object/*>Collection<*/ data);

    /**
     * specification of these functors
     */
    static final Specification callTypeDeclaration = new Specification(1, new Class[] {Object/*>Collection<*/.class}, Object/*>Collection<*/.class);


    /**
     * Filtering Implementations.
     */
    static final class Filtering {

	/**
	 * Select <code>*</code>.
	 * Selects all data in the collection.
	 */
	public static final Filter all = new Filter() {
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
	public static final Filter first = new Filter() {
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
	public static final Filter last = new Filter() {
		public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
		    Collection f = new ArrayList(1);
		    Object	   l = null;
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
	public static Filter ranged(final int min, final int max) {
	    return new Filter() {
		    public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
			Collection f = new ArrayList(max - min + 1);
			int		   count = 0;
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
	 * @see #top(int, boolean)
	 */
	public static Filter top(int number) {
	    return ranged(0, number - 1);
	} 
        
	/**
	 * Filter for the first number percent elements.
	 * @param percent true to filter for the first number percent elements.
	 *  false to filter for the first number elements {@link #top(int)}.
	 */
	public static Filter top(final double number, final boolean percent) {
	    if (!percent)
		return top((int) number);
	    SP.pre(0 <= number && number <= 100, "number must be a valid percentage");
	    return new Filter() {
		    public Object/*>Collection<*/ apply(Object/*>Collection<*/ d) {
			return top((int) Math.ceil(number * ((Collection) d).size() / 100)).apply(d);
		    }
		};
	}
        
	//TODO: implement
	/*
	 * Best(Weighting)
	 *
	 public static Filter best(final Weighting w) {
	 return new Filter() {
	 public Collection apply(Collection d) {
	 }
	 }
	 }
	*/
    }

}
