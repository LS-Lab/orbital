/**
 * @(#)ClausalIndex.java 1.2 2004-01-08 Andre Platzer
 *
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import java.util.Set;

import orbital.logic.imp.*;
import orbital.util.*;
import java.util.*;
import orbital.logic.functor.*;
import orbital.logic.trs.Substitutions;

import orbital.moon.logic.ClassicalLogic;
import orbital.logic.sign.*;

/**
 * Manages a clause index.
 * @version 1.2, 2004-01-08
 * @author  Andr&eacute; Platzer
 */
public class ClausalIndex {
    /**
     * The symbols of the logical junctors.
     */
    private static final Function NOT;
    static {
	//@note assuming the symbols and notation of ClassicalLogic, here
	final Logic logic = new ClassicalLogic();
	final Signature core = logic.coreSignature();
	// we also avoid creating true formulas, it's (more or less) futile
	//@xxx we need some valid non-null arguments.
	final Formula B = (Formula) logic.createAtomic(new SymbolBase("B", SymbolBase.BOOLEAN_ATOM));
	Formula[] arguments = {B};
	Symbol NOTs = core.get("~", arguments);
	assert NOTs != null : "operators in core signature";
	NOT = (Function) ((Formula)logic.createAtomic(NOTs)).apply(logic.coreInterpretation());
    }

    /**
     * The hash-function used for mapping a literal to its hash-code
     * for indexing. The function indexHash approximates the relation
     * of unifiability. The indexHash of two literals must be equal if
     * they are unifiable. If they are unifiable, the indexHash still
     * is allowed to be equal.  Indexing has to respect that
     * indexHash(f(x))=indexHash(f(a)) since f(x) and f(a) may unify
     * when x is a variable and a constant.
     * @note indexHash.apply(o) may well differ from o.hashCode() because it has a different intention.
     */
    private static final Function/*<Formula,Integer>*/ indexHash = new Function() {
	    public Object apply(Object o) {
		//System.err.print("    hsh\t" + o);
		//System.err.println(" -->\t" + myapply(o));
		return new Integer(myapply(o));
	    }
	    // index by top symbol, only
	    private int myapply(Object o) {
		assert o instanceof Formula : o + " instanceof " + Formula.class;
		Formula F = (Formula)o;
		if ((F instanceof Composite)) {
		    Composite f = (Composite) F;
		    Object    g = f.getCompositor();
		    assert !Substitutions.isVariable(g) : "our indexing does not work for variable compositor " + g;
		    if (g == NOT) {
			assert F.equals(((Formula)f.getComponent()).not()) : F + " starts with a negation, so removing and adding the negation again does not change anything";
			// process negations in front of L
			return -myapply(f.getComponent());
		    } else {
			//assert ClassicalLogic.Utilities.negation(F).equals(((Formula)f.getComponent()).not()) : F + " does not start with a negation, so adding the negation leads to the same effect regardless of whether or not duplex negatio is respected";
			return g.hashCode();
		    }
		} else {
		    assert !Substitutions.isVariable(F) : "our indexing does not work for variable literal " + F;
		    //@internal assuming F is atomic
		    return F.hashCode();
		}		
	    }
	};
    /**
     * indexHash of negated formula respecting duplex negatio est affirmatio.
     * @postconditions indexHashNegation.apply(L) == indexHash.apply(ClassicalLogic.Utilities.negation(L))
     */
    private static final Function/*<Formula,Integer>*/ indexHashNegation = new Function() {
	    //@internal this implementation is optimized under the knowledge of a symmetric indexHash, i.e. indexHash(~L) = -indexHash(L)
	    public Object apply(Object o) {
		return new Integer(-((Integer)indexHash.apply(o)).intValue());
	    }
	};

    /**
     * The index hash-map mapping the indexHash of the literals
     * occurring in clauses of this set to the set of (clause,literal)
     * pairs where literals of the same indexHash occur (which thus
     * are possible unifiables).
     * @todo in principle, we could return and memorize the clause and literal which could unify, but this would lead to a catastrophic structure.
     */
    private final Map/*_<Integer,Set<Pair<Clause,Formula>>>_*/ index = new LinkedHashMap();

    /**
     * Create a new empty index.
     */
    public ClausalIndex() {}

    public String toString() {
	return getClass().getName() + "[" + index + "]";
    }

    /**
     * Get an iterator of all (clause,literal) pairs which could possibly unify with L.
     * @postconditions RES = {(C,K)&isin;this &brvbar; K&isin;C &and; possibly &exist;mgU{L,K}}
     */
    public Iterator/*_<Pair<Clause,Literal>>_*/ getProbableUnifiables(Formula L) {
	return getIndex(L).iterator();
    }

    /**
     * Get an iterator of all (clause,literal) pairs which could possibly unify with ~L.
     * @postconditions RES = getProbableUnifiables(ClassicalLogic.Utilities.negation(L))
     */
    public Iterator/*_<Pair<Clause,Literal>>_*/ getProbableComplements(Formula L) {
	Integer hash = (Integer)indexHashNegation.apply(L);
	Set probableUnifiables = (Set)index.get(hash);
	//System.err.println("  complement " + probableUnifiables + " of " + C + "\n    in " + IndexedClausalSetImpl.this);
	return probableUnifiables != null ? probableUnifiables.iterator() : Setops.EMPTY_ITERATOR;
    }

    /**
     * Get an iterator of all clauses that contain literals which could possibly unify with L.
     */
    public Iterator/*_<Clause>_*/ getProbableUnifiableClauses(Formula L) {
	return projectClause(getProbableUnifiables(L));
    }

    /**
     * Get an iterator of all clauses that contain literals which could possibly unify with ~L.
     */
    public Iterator/*_<Clause>_*/ getProbableComplementClauses(Formula L) {
	return projectClause(getProbableComplements(L));
    }

    /**
     * Get an iterator of all literals which could possibly unify with L.
     */
    public Iterator/*_<Formula>_*/ getProbableUnifiableLiterals(Formula L) {
	return projectLiteral(getProbableUnifiables(L));
    }

    /**
     * Get an iterator of all literals which could possibly unify with ~L.
     */
    public Iterator/*_<Clause>_*/ getProbableComplementLiterals(Formula L) {
	return projectLiteral(getProbableComplements(L));
    }

    /**
     * Project an iterator over (clause,literal) pairs to the clauses
     * occurring, but in set notation, i.e. do not mention the same
     * clause twice just because it occurs as (clause,literal1) and
     * (clause,literal2).
     */
    private final Iterator/*_<Clause>_*/ projectClause(Iterator/*_<Pair<Clause,Formula>>_*/ i) {
	Set res = Setops.asSet(Functionals.map(new Function() {
		public Object apply(Object o) {
		    return ((Pair)o).A;
		}
	    }, i));
	assert !Setops.hasDuplicates(res.iterator()) : "no duplicates occur in " + res;
	return res.iterator();
    }

    /**
     * Project an iterator over (clause,literal) pairs to the literals
     * occurring.
     */
    private final Iterator/*_<Formula>_*/ projectLiteral(Iterator/*_<Pair<Clause,Formula>>_*/ i) {
	return Functionals.map(new Function() {
		public Object apply(Object o) {
		    return ((Pair)o).B;
		}
	    }, i);
    }
    
    /**
     * Get the list index.get(o) from the index or an empty list if not present.
     */
    private final Set/*_<Clause/Formula>_*/ getIndex(Object o) {
	assert o instanceof Formula;
	Integer hash = (Integer)indexHash.apply(o);
	Set l = (Set)index.get(hash);
	return l != null ? l : Collections.EMPTY_SET;
    }

    /**
     * Get the list index.get(o) from the index or create an empty
     * list if not present.  Contrary to {@link #getIndex(Object)},
     * this method ensures that the list returned occurs in index.
     */
    private final Set/*_<Clause/Formula>_*/ getIndexEnsure(Object o) {
	assert o instanceof Formula;
	Integer hash = (Integer)indexHash.apply(o);
	Set l = (Set)index.get(hash);
	if (l != null) {
	    return l;
	} else {
	    l = new LinkedHashSet();
	    index.put(hash, l);
	    return l;
	}
    }

    /**
     * Check whether this index is empty.
     */
    public boolean isEmpty() {
	return index.isEmpty();
    }

    // index modification methods
    
    /**
     * Add clause C to our index. Adds C to all indices of literals in C.
     * @return whether the index changed as a result of this operation.
     */
    public boolean add(Clause C) {
	boolean changed = false;
	for (Iterator i = C.iterator(); i.hasNext(); ) {
	    Formula L = (Formula)i.next();
	    changed |= getIndexEnsure(L).add(new Pair(C,L));
	}
	return changed;
    }

    /**
     * Remove clause C from our index. Removes C from all indices of literals in C.
     * @return whether the index changed as a result of this operation.
     */
    public boolean remove(Clause C) {
	boolean changed = false;
	for (Iterator i = C.iterator(); i.hasNext(); ) {
	    Formula L = (Formula)i.next();
	    //@todo furthermore remove getIndex(L) from index if it is empty anyway
	    changed |= getIndex(L).remove(new Pair(C,L));
	}
	return changed;
    }

    public void clear() {
	index.clear();
    }

}
