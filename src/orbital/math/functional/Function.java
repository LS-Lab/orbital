/**
 * @(#)Function.java 1.0 1999/06/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;

/**
 * This interface encapsulates a mathematical unary function "r = f(x)".
 * <p>
 * apply: A&rarr;B; x &#8614; f(x).
 * </p>
 * 
 * @structure inherit orbital.logic.functor.Function
 * @structure inherit orbital.math.functional.MathFunctor
 * @version 1.0, 2000/08/01
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 * @see orbital.logic.functor.Function
 */
public interface Function/*<A implements Arithmetic, B implements Arithmetic>*/ extends orbital.logic.functor.Function/*<A, B>*/, MathFunctor {

    /**
     * Derives this function and returns the resulting Function <i>d</i>f/<i>d</i>x.
     * @return the derived Function <i>d</i>f/<i>d</i>x = f'.
     *    Which is the (total) derivative with respect to the argument x.
     *    The partial derivative with respect to the argument x would be &part;f/&part;x.
     * @throws ArithmeticException if this function is not derivable.
     * @throws UnsupportedOperationException if this function does not implement derivation but could principally be derived.
     */
    Function/* <A, Arithmetic> */ derive();

    /**
     * Integrates this function and returns the resulting indefinite integral &int; f <i>d</i>x.
     * @return the indefinite integral Function &int; f <i>d</i>x.
     * @throws ArithmeticException if this function is not integrable.
     * @throws UnsupportedOperationException if this function does not implement integration but could principally be integrated.
     */
    Function integrate();

    /**
     * A composite function.
     * <p>
     * <div>compose: Map(A,B)&times;Map(C,A)&rarr;Map(C,B); (f,g) &#8614; f &#8728; g := f(g).</div>
     * <div>derive: (f &#8728; g)' = (f' &#8728; g) &middot; g'.</div>
     * </p>
     * A Function could be composed of
     * two Functions with one concatenated with the other.
     * </p>
     * <p>
     * 
     * @structure is {@link orbital.logic.functor.Functor.Composite}&cap;{@link Function}
     * @structure extends Function<A,B>
     * @structure extends orbital.logic.functor.Function<A, B>.Composite
     * @structure aggregate outer:Function
     * @structure aggregate inner:Function
     * @version 1.0, 1999/03/16
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, Function)
     */
    static interface Composite extends orbital.logic.functor.Function/*<A, B>*/.Composite, Function/*<A, B>*/, MathFunctor.Composite {}
}
