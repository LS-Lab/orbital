/**
 * @(#)BinaryPredicate.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.functor.Functor.Specification;

/**
 * A functor that encapsulates the binary predicate <code>P/2</code>.
 * Like <code>"P(a,b)"</code> it applies on
 * <ul>
 *   <li><b>argument</b> <i>first</i> of type A1.</li>
 *   <li><b>argument</b> <i>second</i> of type A2.</li>
 *   <li><b>returns</b> whether the relation holds as a <span class="keyword">boolean</span> value.</li>
 * </ul>
 * <p>
 * The set of all predicates (or relations) of type (A<sub>1</sub>&times;A<sub>2</sub>) is the power set <dfn>&weierp;(A<sub>1</sub>&times;A<sub>2</sub>)</dfn>.
 * These predicates have the form
 * <blockquote>
 *     &rho; &sube; A<sub>1</sub> &times; A<sub>2</sub>
 * </blockquote>
 * </p>
 * 
 * @version 1.0, 1997/06/13
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.Relation
 * @see Predicate
 * @see <a href="doc-files/Relations.html#relation">Properties of Relations</a>
 */
public interface BinaryPredicate/*<A1, A2>*/ extends Functor /*abstract template extends BinaryFunction<A1, A2, boolean> abstract */{

    /**
     * Called to apply the BinaryPredicate. <code>P(a,b)</code>.
     * 
     * @param first     generic Object as first argument
     * @param second    generic Object as second argument
     * @return which returns a boolean.
     */
    boolean apply(Object/*>A1<*/ first, Object/*>A2<*/ second);

    /**
     * specification of these functors.
     */
    static final Specification callTypeDeclaration = new Specification(2, java.lang.Boolean.TYPE);

    /**
     * A composed BinaryPredicate.
     * <div>
     * compose: (P,g,h) &#8614; P o (g,h) := P(g,h).
     * </div>
     * <p>
     * A BinaryPredicate could be composed of
     * an outer BinaryPredicate and two inner BinaryFunctions concatenated with the outer binary one.
     * In other words, results <code>P<big>(</big>g(x,y),h(x,y)<big>)</big></code>.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link BinaryPredicate}
     * @structure extends BinaryPredicate<A1,A2>
     * @structure extends Functor.Composite
     * @structure aggregate outer:BinaryPredicate<B1,B2>
     * @structure aggregate left:BinaryFunction<A1,A2,B1>
     * @structure aggregate right:BinaryFunction<A1,A2,B2>
     * @version 1.0, 2000/01/23
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryPredicate, BinaryFunction, BinaryFunction)
     */
    static interface Composite extends Functor.Composite, BinaryPredicate/*<A1, A2>*/ {}
}
