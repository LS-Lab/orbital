/**
 * @(#)Signature.java 1.0 1999/01/12 Andre Platzer
 * 
 * Copyright (c) 1999-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import java.util.SortedSet;

/**
 * A signature &Sigma; contains the set of the names of all entities in a certain context.
 * Where a name in a signature is called a syntactic <dfn>{@link Symbol symbol}</dfn>.
 * A signature is the vocabulary or alphabet of logical signs of which to
 * build well-formed formulas. It is assumed to be provided effectively.
 * <p>
 * A signature &Sigma; often is partitioned into sets &Sigma;<sub class="type">&tau;</sub>
 * according to the types {@link Symbol#getType() <span class="type">&tau;</span>} of its symbols.
 * Then &Sigma;<sub class="type">&tau;</sub>&sube;&Sigma; is the subset of symbols
 * of type <span class="type">&tau;</span>.
 * It can also be partitioned into &Sigma;<sub>0</sub>, &Sigma;<sub>1</sub>, ... &Sigma;<sub>n</sub>
 * according to the arity of its symbols.
 * Constant-symbols are defined as functions of arity 0.
 * </p>
 * <p>
 * The elements in a signature are of type {@link Symbol} in order to ensure type-safety
 * and arity dependency.
 * </p>
 * 
 * @invariant &forall;s&isin;this: s instanceof {@link orbital.logic.imp.Symbol}
 *  &and; sorted according to {@link orbital.logic.functor.Notation.NotationSpecification#getPrecedence() precedence}
 * @structure extends java.util.SortedSet<Symbol>
 * @version 1.0, 1999/01/12
 * @author  Andr&eacute; Platzer
 * @see ExpressionSyntax#scanSignature(java.lang.String)
 * @see Expression#getSignature()
 * @see java.util.Set
 */
public interface Signature extends SortedSet/*<Symbol>*/ {
    /**
     * Checks two signatures for extensional equality.
     * Two signatures are equal if they contain the same symbols.
     */
    boolean equals(Object o);

    /**
     * Get a hash code fitting extensional equality.
     * {@inheritDoc}.
     */
    int hashCode();

    // Extended operations for functor symbols

    /**
     * Whether this set contains the a functor of the specified signifier compatible with the arguments.
     * @param signifier the signifier of the functor.
     * @param arg the arguments that the functor belonging to the singifier is called with.
     * @return <code>true</code> if this set contains a corresponding functor signifier
     *  denoted as <span class="signifier">signifier</span> and compatible with <tt>arg</tt>.
     * @see #get(String, Object[])
     */
    boolean contains(String signifier, Object[] arg);

    /**
     * Returns the symbol with the specified signifier of a functor.
     * Will only return functors that are compatible with the specified arguments.
     * <p>
     * The most usual use of the arguments array is to check for its length to distinguish
     * unary minus '-' from binary subtraction '-'. But in principle, type checking could be
     * required as well.
     * </p>
     * <p>
     * Note: if there are multiple symbols that match with the given signifier and arguments,
     * one of them will be selected, randomly.
     * </p>
     * @param signifier the signifier of the functor.
     * @param arg the arguments that the functor belonging to the signifier is called with.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @return the symbol that has the specified signifier, if this set contains a corresponding functor notation
     *  denoted as <span class="signifier">signifier</span> and compatible with <tt>arg</tt>,
     *  returns <code>null</code> otherwise.
     * @post ( (RES&ne;null &and; RES&isin;this &and; RES.getSignifier().equals(signifier) &and; contains(signifier,arg)) &or; (RES==null &and; &not;contains(signifier,arg)) ) &and; this.equals(OLD)
     * @see #contains(String, Object[])
     */
    //@todo perhaps we should ignore notation of Symbols (especially precedence etc.), since some callers may not know the exact precedence.
    //@note: Symbol distinguished according to arity. f.ex. by f/2, f/3, P/1
    //@note: Symbol distinguished into Variables, Function symbols and Predicate symbols
    Symbol get(String signifier, Object[] arg);

    // Extended Set operations.

    /**
     * Returns the union of two signatures.
     * @see orbital.util.Setops#union(java.util.Collection,java.util.Collection)
     * @return sigma &cup; sigma2.
     * @post RES == this &cup; sigma2 && RES.getClass() == getClass() && this.equals(OLD)
     */
    Signature union(Signature sigma2);

    /**
     * Returns the intersection of two signatures.
     * @see orbital.util.Setops#intersection(java.util.Collection,java.util.Collection)
     * @return sigma &cap; sigma2.
     * @post RES == this &cap; sigma2 && RES.getClass() == getClass() && this.equals(OLD)
     */
    Signature intersection(Signature sigma2);

    /**
     * Returns the difference to another signature.
     * @see orbital.util.Setops#difference(java.util.Collection,java.util.Collection)
     * @return sigma &#8726; sigma2.
     * @post RES == this &#8726; sigma2 && RES.getClass() == getClass() && this.equals(OLD)
     */
    Signature difference(Signature sigma2);

    /**
     * Returns the difference to another signature.
     * @see orbital.util.Setops#symmetricDifference(java.util.Collection,java.util.Collection)
     * @return sigma &Delta; sigma2.
     * @post RES == this &Delta; sigma2 && RES.getClass() == getClass() && this.equals(OLD)
     */
    Signature symmetricDifference(Signature sigma2);
}
