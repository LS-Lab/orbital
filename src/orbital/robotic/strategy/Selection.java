/*
 * @(#)Selection.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic.strategy;

import java.util.List;

import java.util.ListIterator;

/**
 * Selection interface describing any selection strategy for weights.
 * <p>
 * User-Defined Selection-Algorithms must simply implement this interface
 * and select the "adequate" object from a list of weights.</p>
 * <p>Implementing classes are usually stateless.</p>
 * 
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 * @see orbital.util.Filters
 */
public interface Selection {

    /**
     * Get the weight of the object selected in the last call to select.
     */
    Object/*>Number<*/ getWeight();

    /**
     * Select the preferred one (resp. Collection) from a list of weights.
     * @param weights the list to be selected from.
     * @return returns index of the weight selected in the list.
     * @todo how to select whole collections instead of an int?
     */
    int select(List weights);



    /**
     * Selecting Implementations.
     * These methods return new selection objects on each call to avoid state confusion.
     */
    public static final class Selecting {

	/**
	 * selects the sum of all weights that are not Double.NaN.
	 * @todo optimize since hotspot
	 */
	public static final Selection sum() {
	    return new SelectionImpl() {
		    public int select(List weights) {
			weight = 0;
			int	   selected = -1;
			double bw = Double.NEGATIVE_INFINITY;
			for (ListIterator i = weights.listIterator(); i.hasNext(); ) {
			    double w = ((Number) i.next()).doubleValue();
			    if (Double.isNaN(w))
				continue;
			    weight += w;
			    if (w >= bw) {
				selected = i.previousIndex();
				bw = w;
			    } 
			} 
			if (selected < 0)
			    weight = Double.NaN;
			return selected;
		    } 
		};
	} 

	/**
	 * selects the minimum of all weights that are not Double.NaN.
	 * @see orbital.algorithm.template.PackageUtilities#min(Iterator,Function)
	 */
	public static final Selection min() {
	    return new SelectionImpl() {

		    /**
		     * returns index of last selected minimum weight.
		     */
		    public int select(List weights) {
			int selected = -1;
			weight = Double.POSITIVE_INFINITY;
			for (ListIterator i = weights.listIterator(); i.hasNext(); ) {
			    double w = ((Number) i.next()).doubleValue();
			    if (w <= weight) {
				selected = i.previousIndex();
				weight = w;
			    } 
			} 
			if (selected < 0)
			    weight = Double.NaN;
			return selected;
		    } 
		};
	} 

	/**
	 * selects the maximum of all weights that are not Double.NaN.
	 */
	public static final Selection max() {
	    return new SelectionImpl() {

		    /**
		     * returns index of last selected maximum weight.
		     */
		    public int select(List weights) {
			int selected = -1;
			weight = Double.NEGATIVE_INFINITY;
			for (ListIterator i = weights.listIterator(); i.hasNext(); ) {
			    double w = ((Number) i.next()).doubleValue();
			    if (w >= weight) {
				selected = i.previousIndex();
				weight = w;
			    } 
			} 
			if (selected < 0)
			    weight = Double.NaN;
			return selected;
		    } 
		};
	} 

	/**
	 * @see #max()
	 */
	public static final Selection best() {
	    return max();
	} 
    }
}


/**
 * SelectionImpl class declaring a basic selection algorithm of a weighted Strategy.
 * 
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 */
abstract class SelectionImpl implements Selection {
    protected double weight;

    /**
     * returns selected weight.
     */
    public Object/*>Number<*/ getWeight() {
	return new Double(weight);
    } 
}
