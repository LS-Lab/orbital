/*
 * @(#)MoveWeighting.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;

import orbital.robotic.Move;
import orbital.robotic.Position;

/**
 * MoveWeighting class is a neutral base for weighting a Move.
 * Sub-classes should return a weight for Pairs (((Field|Figure)|Move) in weight(a)
 * after calling MoveWeighting.weight.
 * 
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 */
public
class MoveWeighting implements Function/*<Object, Number>*/ {

	/**
	 * base for a weight value of a Move by providing names and destination checks.
	 */
	public Object/*>Number<*/ apply(Object arg) {
		Argument i = (Argument) arg;	// TODO: is there a better solution?

		assert i.destination != null : "targeted a figure that could not be reached though move is valid";

		return new Double(0);	   // neutral
	} 

	// TODO: good name
	/**
	 * Argument structure description for MoveWeighting.
	 */
	public static class Argument extends FigureWeighting.Argument {
		public final Move	  move;
		public final Position destination;
		public Argument(Field fld, Figure f, Move mv, Position dst) {
			super(fld, f);
			this.move = mv;
			this.destination = dst;
		}
		public Argument(FigureWeighting.Argument i, Move mv, Position dst) {
			this(i.field, i.figure, mv, dst);
		}
		/**
		 * @deprecated Since 1.0, this constructor is for convenience of converting old code, only.
		 */
		public Argument(AdversarySearch.Option opt) {
			this(opt.getState(), opt.getFigure(), opt.getMove(), opt.getDestination());
		}

		public String toString() {
			return "[" + figure.x + "|" + figure.y + "--" + move.movement + "-->" + destination.x + "|" + destination.y + "]";
		} 
	}
}
