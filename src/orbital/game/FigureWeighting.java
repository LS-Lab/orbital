/*
 * @(#)FigureWeighting.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;
import orbital.robotic.strategy.ContainerWeighting;
import orbital.robotic.strategy.Selection;

import orbital.robotic.Move;
import java.util.Iterator;
import orbital.robotic.Position;
import orbital.util.Pair;

/**
 * FigureWeighting class base weights a Figure by summing up the weights of all possible valid moves.
 * 
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 */
public class FigureWeighting extends ContainerWeighting {
    public FigureWeighting(Selection sel, Function/*<Object, Number>*/ weighting) {
	super(sel, weighting);
    }
    public FigureWeighting(Function/*<Object, Number>*/ weighting) {
	super(weighting);
    }

    /**
     * returns weight value of a Figure by sum of its Moves exclusive NaN.
     */
    public Object/*>Number<*/ apply(Object arg) {
	Argument i = (Argument) arg;
	for (Iterator e = i.figure.iterateValidPairs(); e.hasNext(); ) {
	    Pair p = (Pair) e.next();
	    assert p.B != null : "non-null pairs";

	    assert i.figure.movePath((Move)p.A).equals(p.B) : "consistent movePath expected";
	    super.apply(new MoveWeighting.Argument(i, (Move) p.A, (Position) p.B));
	} 
	evaluate();
	return selection.getWeight();
    } 

    //TODO: find a good name
    /**
     * Argument structure description for FigureWeighting.
     */
    public static class Argument {
	public final Field  field;
	public final Figure figure;
	public Argument(Field fld, Figure f) {
	    this.field = fld;
	    this.figure = f;
	}
	public String toString() {
	    return "[" + figure.x + "|" + figure.y + "]";
	} 
    }
}
