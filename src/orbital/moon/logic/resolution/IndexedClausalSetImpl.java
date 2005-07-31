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
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see ClauseIndex
 * @internal straightforward combination of ClausalSetImpl and ClauseIndex
 */
public class IndexedClausalSetImpl extends ClausalSetImpl {

    /**
     * The clause index mapping literals occurring in clauses of this
     * set to the set of clauses where literals occur which are
     * possible unifiables.
     */
    private final ClausalIndex index = new ClausalIndex();

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public IndexedClausalSetImpl(Set/*_<Clause>_*/ clauses) {
	//@internal this order is important, otherwise index will not yet have been initialized
	super();
	addAll(clauses);
    }
    public IndexedClausalSetImpl() {}

    public Iterator/*_<Clause>_*/ getProbableComplementsOf(final Clause C) {
	//@internal convert to set and back again to iterator, in order to remove duplicates from distinct iterator parts of the SequenceIterator
	Set res = Setops.asSet(new SequenceIterator(Functionals.map(
		new Function() {
		    public Object apply(Object o) {
			return index.getProbableComplementClauses((Formula)o);
		    }
		},
		new LinkedList(C))
						    ));
	assert this.containsAll(res) : "ClausalIndex.getProbableComplementClauses only returns clauses of this set ";
	assert Setops.asSet(super.getProbableComplementsOf(C)).containsAll(res) : "ClausalIndex.getProbableComplementClauses returns less clauses than " + ClausalSetImpl.class.getName() + ".getProbableComplementsOf(Clause)";
	assert !Setops.hasDuplicates(res.iterator()) : res + " contains no duplicates but stems from a set";
	return res.iterator();
    }

    /**
     * Get an iterator of all clauses that contain literals which could possibly unify with L.
     */
    public Iterator/*_<Clause>_*/ getProbableUnifiables(Formula L) {
	return index.getProbableUnifiableClauses(L);
    }
    
    // manage index in sync with the current data

    public boolean add(Object o) {
	if (!super.add(o)) {
	    //@todo assert getIndex(o).contains(o) : o + " already present in " + this + " so no change to index necessary";
	    return false;
	}
	index.add((Clause)o);
	return true;
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
	if (!index.remove((Clause)o))
	    throw new IllegalStateException("removing index should change the index at least once");
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
		    if (!index.remove((Clause)current))
			throw new IllegalStateException("removing index should change the index at least once");
		}
	    };
    }
}
