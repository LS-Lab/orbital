/**
 * @(#)PackageUtilities.java 1.0 2002/07/06 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Iterator;
import orbital.util.Pair;

/**
 * @version 1.0, 2002/07/06
 * @author  Andr&eacute; Platzer
 */
final class PackageUtilities {
    /**
     * Get the minimum argument and its f-value.
     * @param choices the available choices in M.
     * @param f the evaluation function f:M&rarr;<b>R</b>.
     * @return the Pair (a, f(a))&isin;M&times;<b>R</b> with minimum f(a).
     * @pre choices.hasNext()
     * @post RES = (a,v) &and; a = arg min<sub>a'&isin;M</sub> f(a')
     *  &and; v = min<sub>a'&isin;M</sub> f(a').
     * @throws NoSuchElementException if !choices.hasNext()
     */
    protected Pair/*<Object, Comparable>*/ min(Iterator choices, Function f) {
	// search for minimum f in choices
	// current best choice of choices
	Object best = choices.next();
	// f(best)
	Comparable bestValue = (Comparable) f.apply(o);
	while (choices.hasNext()) {
	    final Object o = choices.next();
	    final Object value = f.apply(o);
	    if (value.compareTo(bestValue) < 0) {
		bestValue = (Comparable) value;
		best = o;
	    }
	}

	// return the best choice along with its value
	return new Pair/*<Object, Comparable>*/(best, bestValue);
    }

    protected Pair/*<Object, Comparable>*/ max(Iterator choices, Function f) {
	// search for maximum f in choices
	// current best choice of choices
	Object best = choices.next();
	// f(best)
	Comparable bestValue = (Comparable) f.apply(o);
	while (choices.hasNext()) {
	    final Object o = choices.next();
	    final Object value = f.apply(o);
	    if (value.compareTo(bestValue) > 0) {
		bestValue = (Comparable) value;
		best = o;
	    }
	}

	// return the best choice along with its value
	return new Pair/*<Object, Comparable>*/(best, bestValue);
    }
}
