/**
 * @(#)Predicate.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.functor.Functor.Specification;

//TODO: whether to implement functor.adapter.FunctionPredicate(Function) : Predicate and vice versa?

/**
 * A functor that encapsulates a unary predicate <code>P/1</code>.
 * Like <code>"P(a)"</code> it applies on
 * <ul>
 *   <li><b>argument</b> of type A.</li>
 *   <li><b>returns</b> whether the relation holds as a <span class="keyword">boolean</span> value.</li>
 * </ul>
 * <p>
 * The set of all predicates (or relations) of type (A) is the power set <dfn>&weierp;(A) = 2<sup>A</sup></dfn>.
 * For any cardinalities of A, it is in fact true that |2<sup>A</sup>| = 2<sup>|A|</sup>.
 * These predicates of &weierp;(A) have the form:
 * <blockquote>
 *     &rho; &sube; A
 * </blockquote>
 * </p>
 * 
 * @structure inherit orbital.logic.functor.Functor
 * @version 1.0, 1999/07/21
 * @author  Andr&eacute; Platzer
 * @see BinaryPredicate
 * @see VoidPredicate
 */
public interface Predicate/*<A>*/ extends Functor /*abstract template extends Function<A, boolean> abstract */ {

    /**
     * Called to apply the Predicate. Evaluates to <code>P(a)</code>.
     * 
     * @param arg single Object argument
     * @return a boolean.
     */
    boolean apply(Object/*>A<*/ arg);

    /**
     * specification of these functors.
     */
    static final Specification callTypeDeclaration = new Specification(1, java.lang.Boolean.TYPE);

    /**
     * A composed Predicate.
     * <div>
     * compose: (P,g) &#8614; P &#8728; g := P(g).
     * </div>
     * </p>
     * A Predicate could be composed of
     * an outer Predicate and an inner Function.
     * </p>
     * <p>
     * This class is the infimum (greatest common subtype) {@link Functor.Composite}&cap;{@link Predicate}.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link Predicate}
     * @structure extends Predicate<A>
     * @structure extends Functor.Composite
     * @structure aggregate outer:Predicate<B>
     * @structure aggregate inner:Function<A,B>
     * @version 1.0, 2000/01/23
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Predicate, Function)
     */
    static interface Composite extends Functor.Composite, Predicate/*<A>*/ {}
}
