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
    private final ClausalIndex index = new ClausalIndex();

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
	if (true) return index.getProbableUnifiableLiterals(L);
	Collection i = Setops.asList(index.getProbableUnifiableLiterals(L));
	System.err.println("  punifiables " + i + " of " + L + "\n    in " + this);
	return i.iterator();
    }


    // manage index in sync with the current data

    public boolean add(Object o) {
	//@todo optimize implementation. We do not need to remove from index and add again, but only add (this,o) to the index
	//@todo perhaps only activate indexing once this clause gets inserted into a clausalset. Up to this point behave like super does
	//assert that after index.remove(this); index.isEmpty() : "index " + index + " is empty after removing its single clause " + this;
	index.clear();
	boolean changed = super.add(o);
	index.add(this);
	return changed;
    }

    public void clear() {
	super.clear();
	index.clear();
    }

    public boolean remove(Object o) {
	//assert that after index.remove(this); index.isEmpty() : "index " + index + " is empty after removing its single clause " + this;
	index.clear();
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
