/**
 * @(#)VoidFunction.java 1.0 1999/01/04 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.functor.Functor.Specification;

/**
 * A functor that encapsulates the void function <code>f/0</code>
 * which is constant if consistent.
 * Like <code>"r = f()"</code> it applies on
 * <ul>
 *   <li><b>non-argument</b> <span class="keyword">void</span>.</li>
 *   <li><b>returns</b> of type B.</li>
 * </ul>
 * <p>
 * The set of all void functions of type {()}&rarr;B is Map(A<sup>0</sup>,B) = Map({()},B) = B<sup>{()}</sup> &cong; B.
 * These functions have the form
 * <blockquote>
 *   f: {()}&rarr;B; () &#8614; f()
 * </blockquote>
 * </p>
 * <p>
 * Note that if you stick to functional void funtions (who are right-unique or consistent then),
 * there are only |B| different void maps who are all {@link Functions#constant(Object) constant}.
 * </p>
 * 
 * @version 1.0, 1999/01/04
 * @author  Andr&eacute; Platzer
 * @see Function
 */
public interface VoidFunction/*<B>*/ extends Functor {

    /**
     * Called to apply the VoidFunction. <code>f()</code>.
     * 
     * @param none void non-argument
     * @return returns a generic Object.
     */
    Object/*>B<*/ apply();

    /**
     * specification of these functors.
     */
    static final Specification callTypeDeclaration = new Specification(0);

    /**
     * A composed VoidFunction.
     * <div>
     * compose: (f,g) &#8614; f &#8728; g := f(g).
     * </div>
     * <p>
     * A VoidFunction could be composed of
     * a Function and a VoidFunction concatenated with each other.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link VoidFunction}
     * @structure extends VoidFunction<B>
     * @structure extends Functor.Composite
     * @structure aggregate outer:Function<D,B>
     * @structure aggregate inner:VoidFunction<D>
     * @version 1.0, 2000/01/23
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, VoidFunction)
     */
    static interface Composite extends Functor.Composite, VoidFunction/*<B>*/ {}
}
