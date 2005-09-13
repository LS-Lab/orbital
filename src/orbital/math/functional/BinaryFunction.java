/**
 * @(#)BinaryFunction.java 1.0 1999/06/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;

/**
 * This interface encapsulates a binary function "r = f(x,y)".
 * <p>
 * apply: A&times;B&rarr;C; (x,y) &#8614; f(x,y).<br />
 * Where A&times;A is often written as A<sup>2</sup> as well.</p>
 * 
 * @structure inherits orbital.logic.functor.BinaryFunction
 * @structure inherits orbital.math.functional.MathFunctor
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 */
public interface BinaryFunction/*<A1 implements Arithmetic, A2 implements Arithmetic, B implements Arithmetic>*/ extends orbital.logic.functor.BinaryFunction/*<A1, A2, B>*/, MathFunctor {

    /**
     * Derives this function and returns the resulting BinaryFunction
     * <i>d</i>f/<i>d</i>(x,y).
     * Which is the total derivative with respect to the arguments x, y.
     * 
     * @throws ArithmeticException if this function is not differentiable.
     * @throws UnsupportedOperationException if this function does not implement derivation but could principally be derived.
     */
    BinaryFunction derive();
	
    /**
     * Integrates this function and returns the resulting indefinite integral &int; f <i>d</i>x<sub>i</sub>.
     * @return the indefinite integral Function &int; f <i>d</i>x<sub>i</sub>.
     * @throws ArithmeticException if this function is not integrable.
     * @throws UnsupportedOperationException if this function does not implement integration but could principally be integrated.
     * @preconditions 0<=i && i<=1
     */
    BinaryFunction integrate(int i);


    /**
     * A composite function.
     * <div>compose: Map(A<sub>1</sub>&times;A<sub>2</sub>,B)&times;Map(C<sub>1</sub>&times;C<sub>2</sub>,A<sub>1</sub>)&times;Map(C<sub>1</sub>&times;C<sub>2</sub>,A<sub>2</sub>)&rarr;Map(C<sub>1</sub>&times;C<sub>2</sub>,B); (o,l,r) &rarr; o &#8728; (l &times; t)<sup>T</sup> := o(l,r).</div>
     * <div>derive: &part;/&part;(x,y) <big>(</big>f &#8728; (g &times; h)<sup>T</sup><big>)</big> = f'<big>(</big>(g,h)<big>)</big> &middot; (g' &times; h')
     *    = &part;f/&part;x (g,h) &middot; g' + &part;f/&part;y (g,h) &middot; h'.
     * </div>
     * <p>
     * A BinaryFunction could be composed of
     * an outer BinaryFunction and two inner BinaryFunctions concatenated with the outer binary one.
     * In other words, results o<big>(</big>l(x,y),r(x,y)<big>)</big>.
     * </p>
     * 
     * @structure is {@link orbital.logic.functor.Functor.Composite}&cap;{@link BinaryFunction}
     * @structure extends BinaryFunction<A1,A2, B>
     * @structure extends orbital.logic.functor.BinaryFunction<A1,A2, B>.Composite
     * @structure aggregate outer:BinaryFunction
     * @structure aggregate left:BinaryFunction
     * @structure aggregate right:BinaryFunction
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryFunction, BinaryFunction, BinaryFunction)
     */
    static interface Composite extends orbital.logic.functor.BinaryFunction/*<A1,A2,B>*/.Composite, BinaryFunction/*<A1,A2,B>*/, MathFunctor.Composite {}
}
