/**
 * @(#)Clause.java 0.8 2003-04-23 Andre Platzer
 *
 * Copyright (c) 2001-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import orbital.logic.sign.Signature;
import java.util.Set;
import java.util.Iterator;

import java.util.Collections;

/**
 * Represents a clause, i.e. a set of literals.  The individual
 * literals, i.e. positive or negated atoms are ordinary formulas.
 * This set representation already incorporates associative and commutative.
 *
 * @version 0.8, 2003-04-23
 * @author  Andr&eacute; Platzer
 */
public interface Clause extends Set/*<Formula>*/ {
    /**
     * The contradictory clause &empty; &equiv; &#9633; &equiv;
     * &perp;.  <p> The {@link ClausalSet#CONTRADICTION_SINGLETON_SET
     * contradictory singleton set of clauses} is {&empty;}={&#9633;}
     * while the tautological set of clauses is {}.  </p>
     */
    static final Clause CONTRADICTION = new ClauseImpl(Collections.EMPTY_SET);

    /**
     * Get the free variables of a formula represented as a clause.
     * @return freeVariables(this)
     * @internal note that for clauses FV(C)=V(C) &and; BV(C)=&empty;
     * @xxx change return-type to Set to reflect Formula.getFreeVariables.
     */
    Signature getFreeVariables();

    /**
     * Get a variant of this clause with the given variables renamed.
     * &alpha;-conversion
     * @param disjunctify the variables to rename (in order to produce a variable disjunct variant of F relative to some formula G).
     * @internal Variantenbildung in disjunkte Variablen erforderlich(!). Dazu entweder die aus V(F)&cap;V(G) etwa in F umbenennen, oder mit Variable.setSymbol(Variable.getSymbol()+neueNummer) alles explizit fortzählen.
     */
    public Clause variant(Signature disjunctify);

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
     * Factorize a clause as much as possible.
     * <p>
     * Will implement the factorization rule necessary for binary resolution:
     * <div>{L1,...,Ln} |- {s(L1),...,s(Lk)} with s=mgU({Lk,...,Ln})</div>
     * Which is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Lk,...,Ln})</div>
     * because of the set representation, and which again is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Li,Lj})</div>
     * because of set notation. The latter is the way we (currently) implement things.
     * </p>
     * @return the factorized clause, or <code>this</code> if no factorization was possible.
     */
    Clause factorize();

    /**
     * Returns true when this clause obviously is an elementary tautology.
     * That is a p&or;&not;p &isin; F
     * <p>
     * We can forget about elementary valid clauses for resolving false,
     * because true formulas will only imply true formulas, never false ones.
     * </p>
     * @todo or even when there is a single-sided matcher of p and q in p&or;&not;q?
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
     * because true formulas will only imply true formulas, never false ones.
     * </p>
     * @param G a clause
     * @todo or even when there is a single-sided matcher of p and q in p&or;&not;q?
     * @preconditions (&not;this.isElementaryValid() &and; &not;G.isElementaryValid()) &or; F=G
     * @todo look for true&isin;F?
     */
    public boolean isElementaryValidUnion(Clause G);
}
