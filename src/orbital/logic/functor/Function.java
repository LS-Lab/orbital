/**
 * @(#)Function.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.functor.Functor.Specification;

/**
 * A functor that encapsulates the unary function <code>f/1</code>
 * which is kind of the most general Functor.
 * Like <code>"r = f(a)"</code> it applies on
 * <ul>
 *   <li><b>argument</b> of type A.</li>
 *   <li><b>returns</b> of type B.</li>
 * </ul>
 * <p>
 * The set of all functions (or maps) of type A&rarr;B is called <dfn>Map(A,B) = B<sup>A</sup></dfn>.
 * For any cardinalities of A,B, it is in fact true that |B<sup>A</sup>| = |B|<sup>|A|</sup>.
 * These functions of Map(A,B) have the form
 * <blockquote>
 *     f: A&rarr;B; a &#8614; f(a)
 * </blockquote>
 * </p>
 * 
 * @structure inherit Functor
 * @version 1.0, 1999/07/21
 * @author  Andr&eacute; Platzer
 * @see BinaryFunction
 * @see VoidFunction
 * @see <a href="doc-files/Relations.html#function">Properties of Functions</a>
 */
public /*template*/ interface Function/*<A, B>*/ extends Functor {

    /**
     * Called to apply the Function. Evaluates to <code>f(a)</code>.
     * 
     * @param arg generic Object as argument
     * @return returns a generic Object.
     */
    Object/*>B<*/ apply(Object/*>A<*/ arg);


    /**
     * specification of these functors.
     * @todo what to specify at runtime when templates are enabled?
     */
    static final Specification callTypeDeclaration = new Specification(1 /*, new Class[] {A.class}, B.class*/);

    /**
     * A composed Function.
     * <div>compose: (f,g) &#8614; f &#8728; g := f(g)</div>
     * <p>
     * Functions could be composed of an outer Function and an inner Function concatenated with each other.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link Function}
     * @structure extends Function<A,B>
     * @structure extends Functor.Composite
     * @structure aggregate outer:Function<D,B>
     * @structure aggregate inner:Function<A,D>
     * @version 1.0, 2000/01/23
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, Function)
     * @internal if we only had a section interface of Function and Functor.Composite, then
     *  we would not need this interface.
     *  The same goes for similar *.Composite* interface here and in math.functional.
     */
    static interface Composite extends Functor.Composite, Function/*<A,B>*/ {}
}
