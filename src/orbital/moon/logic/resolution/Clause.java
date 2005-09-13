/**
 * @(#)Clause.java 0.8 2003-04-23 Andre Platzer
 *
 * Copyright (c) 2001-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import orbital.logic.sign.Signature;
import orbital.logic.imp.Formula;
import java.util.Set;
import java.util.Iterator;

import java.util.Collections;

/**
 * Represents a clause, i.e. a set of literals.  The individual
 * literals, i.e. positive or negated atoms are ordinary formulas.
 * This set representation already incorporates associative and commutative.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public interface Clause extends Set/*<Formula>*/ {
    /**
     * The contradictory clause &empty; &equiv; &#9633; &equiv;
     * &perp;.  <p> The {@link ClausalSet#CONTRADICTION_SINGLETON_SET
     * contradictory singleton set of clauses} is {&empty;}={&#9633;}
     * while the tautological set of clauses is {}.  </p>
     */
    static final Clause CONTRADICTION = ResolutionBase.getClausalFactory().createClause(Collections.EMPTY_SET);

    /**
     * Get the free variables of a formula represented as a clause.
     * @return freeVariables(this)
     * @internal note that for clauses FV(C)=V(C) &and; BV(C)=&empty;
     * @xxx change return-type to Set to reflect Formula.getFreeVariables. Further using LinkedHashSets could be quicker than using the TreeSets underlying Signatures
     */
    Signature getFreeVariables();

    /**
     * Get a variant of this clause with the given variables renamed.
     * &alpha;-conversion
     * @param disjointify the variables to rename (in order to produce a variable disjoint variant of F relative to some formula G).
     * @return a variant (i.e. resulting from a variable renaming) of
     * this clause containing no variable of the signature
     * <code>disjointify</code>.
     * @postconditions RES.getFreeVariables().intersection(disjointify).isEmpty() &and; RES is a variant of this
     * @internal Variantenbildung in disjunkte Variablen erforderlich(!). Dazu entweder die aus V(F)&cap;V(G) etwa in F umbenennen, oder mit Variable.setSymbol(Variable.getSymbol()+neueNummer) alles explizit fortzählen.
     */
    public Clause variant(Signature disjointify);

    // proof utilities
	
    /**
     * Get all resolvents of F and G, if any. (Resolution rule)
     * Implementation already incorporates some cuts.
     * @return an iterator over the set of all resolvent clauses.
     */
    Iterator/*_<Clause>_*/ resolveWith(Clause G);

    /**
     * Get all resolvents of variants of F and G, if any. (Resolution rule)
     * Combines resolution and the required forming of variants beforehand.
     * @see #variant(Signature)
     * @see #resolveWith(Clause)
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     */
    Iterator/*_<Clause>_*/ resolveWithVariant(Clause G);

    /**
     * Get all resolvents of factors of F and G, if any. (Resolution
     * rule) Combines resolution and factorization, resulting in a
     * quicker implementation.  When F can be resolved with G via the
     * resolution literals L and K, then in addition to the ordinary
     * resolvent, this method also returns all resolvents resulting
     * from a factorization of F (involving L) or G (involving K).
     * Other factorizations are not necessary for completeness, since
     * they can be performed later during resolution over the
     * participating literals.
     * @see #factorize()
     * @see #resolveWith(Clause)
     */
    Iterator/*_<Clause>_*/ resolveWithFactors(Clause G);

    /**
     * Get all factors of F. (factorization rule).
     * <p>
     * Will implement the factorization rule necessary for binary resolution:
     * <div>{L1,...,Ln} |- {s(L1),...,s(Lk)} with s=mgU({Lk,...,Ln})</div>
     * Which is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Lk,...,Ln})</div>
     * because of the set representation, and which again is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Li,Lj})</div>
     * because of set notation. The latter is the way we (currently) implement things.
     * </p>
     * @return all (proper) factor clauses of this, or <code>&empty;</code> if no factorization was possible.
     */
    Iterator/*_<Clause>_*/ factorize();

    /**
     * Returns true when this clause subsumes D.
     * That is there is a subsitution &sigma; such that C&sigma; &sube; D
     * (and |C| &le; |D|).
     * <p>
     * We can forget about subsumed clause D for resolving false,
     * and work on C instead.
     * </p>
     * @preconditions true
     * @note Conservative estimations are possible, i.e. returning
     * false even for subsumption cases is allowed.
     * @todo look for true&isin;F?
     */
    boolean subsumes(Clause D);

    /**
     * Returns true when this clause obviously is an elementary tautology.
     * That is a p&or;&not;p &isin; F
     * <p>
     * We can forget about elementary valid clauses for resolving false,
     * because true formulas will only imply true formulas, never false ones.
     * </p>
     * @todo or even when there is a single-sided matcher of p and q in p&or;&not;q? No
     * @preconditions true
     * @todo look for true&isin;F?
     * @attribute derived {@link #isElementaryValidUnion(Clause)}
     */
    public boolean isElementaryValid();

    /**
     * Returns true when the union F&cup;G would obviously contain an elementary tautology.
     * That is a p&or;&not;p &isin; F&cup;G
     * <p>
     * We can forget about elementary valid clauses for resolving false,
     * (<em>essentially</em>) because true formulas will only imply true formulas,
     * never false ones.
     * </p>
     * @param G a clause
     * @preconditions (&not;this.isElementaryValid() &and; &not;G.isElementaryValid()) &or; F=G
     */
    public boolean isElementaryValidUnion(Clause G);

    // lookup methods


    /**
     * Select some literals of this clause, which are usable for resolution.
     * @postconditions RES &sube; this
     */
    Iterator/*_<Formula>_*/ getResolvableLiterals();

    /**
     * Get (an iterator over) all literals contained in this clause
     * that may possibly unify with L. The formulas returned will more
     * likely qualify for unification with C, but need not do so with
     * absolute confidence.  <p>Implementations may use indexing or
     * links to estimate the clauses to return very
     * quickly. Furthermore, implementations may apply selection
     * refinements.</p>
     * @postconditions RES&sube;this &and; RES&supe;getUnifiables(L) 
     * @see #getUnifiables(Formula)
     */
    Iterator/*_<Formula>_*/ getProbableUnifiables(Formula L);

    /**
     * Get all literals contained in this clause that unify with
     * L. <p>Implementations may use indexing or links to estimate the
     * clauses to return very quickly. Furthermore, implementations
     * may apply selection refinements.</p>
     * @postconditions RES = {K&isin;this &exist;mgU{L,K}}
     * @see #getProbableUnifiables(Formula)
     */
    Set/*_<Formula>_*/ getUnifiables(Formula L);

    
    /**
     * Convert this clause to a formula representation.
     * @internal this is not a view but a copy, because several
     * operations would not work reliably, otherwise. Imagine a
     * traversal to the last literal, when another one is added. Then
     * the last literal returned by getComponent() should have been an
     * &and; operator in retrospect.
     * @internal the result is right associative
     */
    Formula toFormula();
}
