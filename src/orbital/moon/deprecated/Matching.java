/*
 * @(#)Matching.java 0.9 1997/07/17 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.setop;

import orbital.logic.functor.Predicate;
import orbital.logic.functor.VoidPredicate;
import java.util.Map;

/**
 * This Predicate is a matching algorithm for a Map of VoidPredicates.
 * Matching is true when the VoidPredicate associated to an Object in a Map is true.
 *
 * @deprecated We feel this predicate's use does not counter weight
 * the intention of a light-weight design.
 * It is seldom used but in case would be easy to implement again.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public
class Matching implements Predicate {

	/**
	 * The Map that contains all VoidPredicates associated with the Objects.
	 * 
	 * @serial
	 */
	protected Map conditions;
	public Matching(Map conds) {
		conditions = conds;
	}

	public boolean apply(Object obj) {
		VoidPredicate pred = (VoidPredicate) conditions.get(obj);
		return pred.apply();
	} 
}
