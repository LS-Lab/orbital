/**
 * @(#)VoidPredicate.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.functor.Functor.Specification;

/**
 * A functor that encapsulates the void predicate <code>P/0</code>
 * which is constant if consistent.
 * Like <code>"P()"</code> it applies on
 * <ul>
 *   <li><b>non-argument</b> <span class="keyword">void</span>.</li>
 *   <li><b>returns</b> whether the relation holds as a <span class="keyword">boolean</span> value.</li>
 * </ul>
 * <p>
 * The set of all void predicates of type () is the set <dfn>&weierp;(A<sup>0</sup>)</dfn>=&weierp;({()})={&empty;,{()}}&cong;{True,False}.
 * <p>
 * Note that if you stick to consistent void predicates, there
 * are only two different void predicates,
 * constant {@link Predicates#TRUE true} and constant {@link Predicates#FALSE false}.
 * </p>
 * 
 * @version 1.0, 1997/06/13
 * @author  Andr&eacute; Platzer
 * @see Predicate
 */
public
interface VoidPredicate extends Functor /*abstract template extends VoidFunction<boolean> abstract */{

	/**
	 * Called to apply the VoidPredicate. <code>P()</code>.
	 * 
	 * @param none void non-argument
	 * @return which returns a boolean.
	 */
	boolean apply();

	/**
	 * specification of these functors
	 */
	static final Specification specification = new Specification(0, java.lang.Boolean.TYPE);

	/**
	 * A composed VoidPredicate.
	 * <div>
	 * compose: (P,g) &#8614; P &#8728; g := P(g).
	 * </div>
	 * <p>
	 * A VoidPredicate could be composed of
	 * an outer Predicate and an inner VoidFunction.
	 * </p>
	 * <p>
	 * This class is the infimum (greatest common subtype) {@link Functor.Composite}&cap;{@link VoidPredicate}.
	 * </p>
	 * 
	 * @structure is {@link Functor.Composite}&cap;{@link VoidPredicate}
	 * @structure extends VoidPredicate
	 * @structure extends Functor.Composite
	 * @structure aggregate outer:Predicate<B>
	 * @structure aggregate inner:VoidFunction<B>
	 * @version 1.0, 2000/01/23
	 * @author  Andr&eacute; Platzer
	 * @see Functionals#compose(Predicate, VoidFunction)
	 */
	static interface Composite extends Functor.Composite, VoidPredicate {}
}
