/**
 * @(#)IndexedClausalSetImpl.java 1.2 2004-01-07 Andre Platzer
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
 * Implementation of a representation of a set of clauses with clause indexing.
 * @version 1.2, 2004-01-07
 * @author  Andr&eacute; Platzer
 */
public class IndexedClausalSetImpl extends ClausalSetImpl {
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
//@fixme .hashCode() is wrong, we need .hashCode() of toplevel symbol p not of p(x) or p(x10) or p(s2) which differ!
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
     * occurring in clauses of this set to the list of clauses where
     * literals of the same indexHash occur (which thus are possible
     * unifiables).
     */
    private final Map/*_<Integer,List<Clause>>_*/ index = new LinkedHashMap();

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public IndexedClausalSetImpl(Set/*<Formula>*/ clauses) {
	//@internal this order is important, otherwise index will not yet have been initialized
	super();
	addAll(clauses);
    }
    public IndexedClausalSetImpl() {}

    public Iterator/*_<Clause>_*/ getProbableComplementsOf(final Clause C) {
	return new SequenceIterator((List)Setops.createSelection(Functionals.bindSecond(Predicates.unequal,null))
				    .apply(Functionals.map(
		new Function() {
		    public Object apply(Object o) {
			Integer hash = (Integer)indexHashNegation.apply((Formula)o);
			List probableUnifiables = (List)index.get(hash);
			//System.err.println("  complement " + probableUnifiables + " of " + C + "\n    in " + IndexedClausalSetImpl.this);
			return probableUnifiables == null ? null : probableUnifiables.iterator();
		    }
		},
		new LinkedList(C)))
				    );
    }

    /**
     * Get an iterator of all clauses that contain literals which could possibly unify with L.
     */
    public Iterator/*_<Clause>_*/ getProbableUnifiables(Formula L) {
	return getIndex(L).iterator();
    }
    
    /**
     * Get the list index.get(o) from the index or an empty list if not present.
     */
    private final List/*_<Clause/Formula>_*/ getIndex(Object o) {
	assert o instanceof Formula;
	Integer hash = (Integer)indexHash.apply(o);
	List l = (List)index.get(hash);
	return l != null ? l : Collections.EMPTY_LIST;
    }

    /**
     * Get the list index.get(o) from the index or create an empty
     * list if not present.  Contrary to {@link #getIndex(Object)},
     * this method ensures that the list returned occurs in index.
     */
    private final List/*_<Clause/Formula>_*/ getIndexEnsure(Object o) {
	assert o instanceof Formula;
	Integer hash = (Integer)indexHash.apply(o);
	List l = (List)index.get(hash);
	if (l != null) {
	    return l;
	} else {
	    l = new LinkedList();
	    index.put(hash, l);
	    return l;
	}
    }
    
    // manage index in sync with the current data

    public boolean add(Object o) {
	if (!super.add(o)) {
	    //@todo assert getIndex(o).contains(o) : o + " already present in " + this + " so no change to index necessary";
	    return false;
	}
	addIndex((Clause)o);
	return true;
    }

    /**
     * Add clause C to our index. Adds C to all indices of literals in C.
     */
    private void addIndex(Clause C) {
	for (Iterator i = C.iterator(); i.hasNext(); ) {
	    getIndexEnsure((Formula)i.next()).add(C);
	}
    }
    /**
     * Remove clause C from our index. Removes C from all indices of literals in C.
     */
    private void removeIndex(Clause C) {
	for (Iterator i = C.iterator(); i.hasNext(); ) {
	    if (!getIndex((Formula)i.next()).remove(C))
		throw new IllegalStateException("removing index should change the index");
	}
    }

    public void clear() {
	super.clear();
	index.clear();
    }

    public boolean remove(Object o) {
	if (!super.remove(o)) {
	    //@todo assert !getIndex(o).contains(o) : o + " not present in " + this + " so no change to index necessary";
	    return false;
	}
	removeIndex((Clause)o);
	return true;
    }

    public Iterator iterator() {
	return new DelegateIterator(super.iterator()) {
		private Object current = null;
		public Object next() {
		    return this.current = super.next();
		}
		public void remove() {
		    super.remove();
		    removeIndex((Clause)current);
		}
	    };
    }
}
