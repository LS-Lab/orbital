/**
 * @(#)IndexedClauseImpl.java 1.2 2004-01-08 Andre Platzer
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
 * Implementation of a representation of a clauses with clause indexing.
 * @version 1.2, 2004-01-08
 * @author  Andr&eacute; Platzer
 * @see ClauseIndex
 * @internal straightforward combination of ClauseImpl and ClauseIndex
 */
public class IndexedClauseImpl extends ClauseImpl {

    /**
     * The clause index mapping literals occurring in clauses of this
     * set to the literals which are possible unifiables.
     */
    private final ClauseIndex index = new ClauseIndex();

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public IndexedClauseImpl(Set/*_<Formula>_*/ literals) {
	//@internal this order is important, otherwise index will not yet have been initialized
	super();
	addAll(literals);
    }
    public IndexedClauseImpl() {}

    public Iterator/*_<Formula>_*/ getProbableUnifiables(Formula L) {
	return index.getProbableUnifiableLiterals(L);
    }

    public Set/*_<Formula>_*/ getUnifiables(Collection/*_<Formula>_*/ C, Formula L) {
	//@internal for faster lookup with contains
	C = new HashSet(C);
	Set/*_<Formula>_*/ r = new LinkedHashSet();
	for (Iterator i = getProbableUnifiables(L); i.hasNext(); ) {
	    Formula F = (Formula)i.next();
	    if (!C.contains(F))
		continue;
	    //@todo optimizable, we could remember the unifier instead of recalculating it lateron (f.ex. during factorization)
	    if (Substitutions.unify(Arrays.asList(new Formula[] {L,F})) != null) {
		r.add(F);
	    }
	}
	return r;
    }

    // manage index in sync with the current data

    public boolean add(Object o) {
	//@todo optimize implementation. We do not need to remove from index and add again, but only add (this,o) to the index
	//@todo perhaps only activate indexing once this clause gets inserted into a clausalset. Up to this point behave like super does
	index.remove(this);
	assert true || index.isEmpty() : "index " + index + " is empty after removing its single clause " + this;
	boolean changed = super.add(o);
	index.add(this);
	return changed;
    }

    public void clear() {
	super.clear();
	index.clear();
    }

    public boolean remove(Object o) {
	index.remove(this);
	assert true || index.isEmpty() : "index " + index + " is empty after removing its single clause " + this;
	boolean changed = super.remove(o);
	index.add(this);
	return changed;
    }

    public Iterator iterator() {
	return new DelegateIterator(super.iterator()) {
		private Object current = null;
		public Object next() {
		    return this.current = super.next();
		}
		public void remove() {
		    throw new UnsupportedOperationException("operation currently not yet supported");
		}
	    };
    }
}
