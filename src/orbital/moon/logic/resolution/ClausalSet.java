/**
 * @(#)ClausalSet.java 0.8 2003-04-23 Andre Platzer
 *
 * Copyright (c) 2001-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import java.util.Set;
import orbital.logic.imp.Formula;

import java.util.Iterator;

import java.util.Collections;

/**
 * Represents a set of clauses. A set of clauses
 * {C<sub>1</sub>,...,C<sub>n</sub>} is a different notation for the
 * conjunction C<sub>1</sub>&and;...&and;C<sub>n</sub>. This set
 * representation already incorporates associative and commutative.
 * @version 0.8, 2003-04-23
 * @author  Andr&eacute; Platzer
 * @todo rename
 */
public interface ClausalSet extends Set/*_<Clause>_*/ {
    /**
     * The contradictory <em>singleton</em> set of clauses {&#9633;}.
     */
    static final ClausalSet CONTRADICTION_SINGLETON_SET = ResolutionBase.getClausalFactory().createClausalSet(Collections.singleton(Clause.CONTRADICTION));

    /**
     * The tautological <em>singleton</em> set of clauses {}.
     */
    static final ClausalSet TAUTOLOGY_SINGLETON_SET = ResolutionBase.getClausalFactory().createClausalSet(Collections.EMPTY_SET);

    //@todo introduce method for fast indexed lookup of complementary top-level(c'est assez) symbols

    /**
     * Remove all clauses from this set which are subsumed by any of
     * the clauses of T. <p> In case of T == this, don't let clauses
     * remove by mutual subsumption, or by self-subsumption.</p>
     * @return whether this set has changed as a result of the deletion by subsumption.
     */
    public boolean removeAllSubsumedBy(ClausalSet T);

    // lookup methods

    /**
     * Get (an iterator over) all clauses contained in this set that
     * may possibly form a complement to C for resolution. The clauses
     * returned will more likely qualify for resolution with C, but
     * need not do so with absolute confidence.  <p>Implementations
     * may use indexing to estimate the clauses to return very
     * quickly.</p>
     * @postconditions RES&sube;this
     *  &and; RES &supe; {D&isin;this &brvbar; &exist;L&isin;C &exist;K&isin;D &exist;mgU{L,~K}}
     * @todo 14 rename to getProbableUnifiables(Clause C) and leave negation to our caller (more systematic)?
     */
    Iterator/*_<Clause>_*/ getProbableComplementsOf(Clause C);

    
    /**
     * Convert this set of clauses to a formula representation.
     * @internal this is not a view but a copy, because several
     * operations would not work reliably, otherwise. Imagine a
     * traversal to the last literal, when another one is added. Then
     * the last literal returned by getComponent() should have been an
     * &and; operator in retrospect.
     * @internal the result is right associative
     */
    Formula toFormula();
}
