/*
 * @(#)FieldWeighting.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;
import orbital.robotic.strategy.ContainerWeighting;
import orbital.robotic.strategy.Selection;

import java.util.Iterator;
import orbital.util.Pair;

/**
 * FieldWeighting class weights a Field by summing up the weights of all Figures.
 * Makes Pairs (Field|Figure).
 * 
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 * @deprecated Since Orbital 1.1 use {@link AdversarySearch} instead.
 */
public class FieldWeighting extends ContainerWeighting {
    //TODO: conform to GeneralSearchProblem.Option (including descendant classes)
    public FieldWeighting(Selection sel, Function/*<Object, Number>*/ weighting) {
	super(sel, weighting);
    }
    public FieldWeighting(Function/*<Object, Number>*/ weighting) {
	super(weighting);
    }

    /**
     * returns weight value of a Field by sum of its Figures excluding NaN.
     */
    public Object/*>Number<*/ apply(Object arg) {
	Field field = (Field) arg;
	for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
	    Figure figure = (Figure) i.next();
	    super.apply(new FigureWeighting.Argument(field, figure));
	} 
	evaluate();
	return selection.getWeight();
    } 
}
