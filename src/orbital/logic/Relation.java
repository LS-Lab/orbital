/**
 * @(#)Relation.java 0.9 1999/03/03 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

import orbital.util.Callback;

/**
 * This basic Relation interface tags interfaces and classes that describe relations (or predicates) between objects.
 * A general <dfn>relation</dfn> over a set M is of the form:
 * <ul>
 *   <span class="Formula">~ &sube; M&times;M</span><br />
 *   relation: for a,b being objects, <span class="Formula">a ~ b</span> is consistently either <span class="keyword">true</span> or <span class="keyword">false</span>.
 * </ul>
 * <p>
 * A Relation is any relation-like method contained in an Object with
 * an invocation-signature like
 * <pre>
 *   <span class="keyword">public</span> <span class="keyword">boolean</span> <var>related</var><b>(</b><var>arg-type</var><b>,</b><var>arg-type</var><b>)</b>
 * </pre>
 * Of course, relations could also be defined for more than two objects.
 * They might as well be implemented as Predicate-{@link orbital.logic.functor.Functor Functors} P where
 * P(a,b) is <code>true</code> if <span class="Formula">a ~ b</span> and <code>false</code> otherwise.
 * A pseudo-Prolog definition for this would be <span class="Formula">P(a,b) :- a~b</span>.
 * <p>
 * Refer to <a href="functor/BinaryPredicate.html#Properties">BinaryPredicate</a> for properties of relations.</p>
 * <p>
 * <b><i>Evolves</i>:</b> This interface might be unified with Predicates, most likely BinaryPredicates.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.BinaryPredicate
 * @see orbital.logic.functor.Functor
 * @see orbital.util.Callback
 */
public abstract interface Relation extends Callback {}
