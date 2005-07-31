/*
 * @(#)Weighting.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic.strategy;

import orbital.logic.functor.Function;

/**
 * Weighting interface describes a weighting-algorithm
 * determining the problem-specific weight of an object.
 * This interface encapsulates a Weighting-algorithm returning the weight
 * of an Object (f.ex. a situation state for a Strategy).
 * <p>Implementing classes are usually stateless.</p>
 * 
 * <p>User-Defined Weighting-Algorithms must simply implement this Interface
 * and calc the weight of a situation-Object.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @deprecated Since Orbital1.0 use super class orbital.logic.functor.Function<Object, Number> instead.
 */
public
interface Weighting extends Function/*<Object, Number>*/ {
	//TODO: think about replacing by Function<Object, Number>

	/**
	 * Is called to calculate the weight, a given situation has.
	 * @param situation an Object describing the situation state to be weighed.
	 * @return returns weight value of an arg.
	 */
	//double weight(Object situation);
}
