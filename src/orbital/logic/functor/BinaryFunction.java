/**
 * @(#)BinaryFunction.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.functor.Functor.Specification;

/**
 * A functor that encapsulates the binary function <code>f/2</code>.
 * Like <code>"r = f(a,b)"</code> it applies on
 * <ul>
 *   <li><b>argument</b> <i>first</i> of type A1.</li>
 *   <li><b>argument</b> <i>second</i> of type A2.</li>
 *   <li><b>returns</b> of type B.</li>
 * </ul>
 * <p>
 * The set of all binary functions of type A<sub>1</sub>&times;A<sub>2</sub>&rarr;B is Map(A<sub>1</sub>&times;A<sub>2</sub>,B) = B<sup>A</sup>.
 * These functions have the form
 * <blockquote>
 *   f: A<sub>1</sub>&times;A<sub>2</sub>&rarr;B; (a<sub>1</sub>,a<sub>2</sub>) &#8614; f(a<sub>1</sub>,a<sub>2</sub>)
 * </blockquote>
 * Additionally, the types A<sub>1</sub>&times;A<sub>2</sub>&rarr;B and A<sub>1</sub>&rarr;(A<sub>2</sub>&rarr;B)
 * are isomorph and
 * Map(A<sub>1</sub>&times;A<sub>2</sub>,B) &cong; Map(A<sub>1</sub>,Map(A<sub>2</sub>,B))
 * </p>
 * 
 * @structure inherit Functor
 * @version 1.0, 1998/11/14
 * @author  Andr&eacute; Platzer
 * @see Function
 * @internal alternative implementation would regard BinaryFunctions as Function<Object[],B> but that would loose even more type safety.
 */
public /*template*/ interface BinaryFunction/*<A1, A2, B>*/ extends Functor {

    /**
     * Called to apply the BinaryFunction. f(a,b).
     * 
     * @param first     generic Object as first argument
     * @param second    generic Object as second argument
     * @return returns a generic Object.
     */
    Object/*>B<*/ apply(Object/*>A1<*/ first, Object/*>A2<*/ second);

    /**
     * specification of these functors.
     */
    static final Specification callTypeDeclaration = new Specification(2);

    /**
     * A composed BinaryFunction.
     * <div>compose: (f,g,h) &#8614; f &#8728; (g &times; h) := f(g,h).</div>
     * <p>
     * Binary functions could be composed of an outer BinaryFunction and two inner BinaryFunctions concatenated with the outer binary one.
     * In other words, results are f<big>(</big>g(x,y),h(x,y)<big>)</big>.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link BinaryFunction}
     * @structure extends BinaryFunction<A1,A2,B>
     * @structure extends Functor.Composite
     * @structure aggregate outer:BinaryFunction<D1,D2,B>
     * @structure aggregate left:BinaryFunction<A1,A2,D1>
     * @structure aggregate right:BinaryFunction<A1,A2,D2>
     * @version 1.0, 2000/01/23
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryFunction, BinaryFunction, BinaryFunction)
     */
    static interface Composite extends Functor.Composite, BinaryFunction/*<A1, A2, B>*/ {}
}
