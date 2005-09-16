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

import orbital.moon.logic.ClassicalLogic;
import orbital.logic.sign.*;

/**
 * Implementation of a representation of a clauses with clause indexing.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see ClauseIndex
 * @internal straightforward combination of ClauseImpl and ClauseIndex
 */
public class IndexedClauseImpl extends ClauseImpl {

    /**
     * The clause index mapping literals occurring in clauses of this
     * set to the literals which are possible unifiables.
     * @attribute lazy-initialization in {@link #getProbableUnifiables(Formula)}, thereafter update in all methods
     * @internal This is a performance advantage, since the index does not need to be established until the first use. This is similar to but simpler than the following idea: perhaps only activate indexing once this clause gets inserted into a clausalset. Up to this point behave like super does
     */
    private ClausalIndex index = null;

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public IndexedClauseImpl(Set/*<Formula>*/ literals) {
        super(literals);
    }
    public IndexedClauseImpl() {}

    public Iterator/*<Formula>*/ getProbableUnifiables(Formula L) {
        if (index == null) {
            // lazy initialization
            index = new ClausalIndex();
            index.add(this);
        }
        if (true) return index.getProbableUnifiableLiterals(L);
        Collection i = Setops.asList(index.getProbableUnifiableLiterals(L));
        System.err.println("  punifiables " + i + " of " + L + "\n    in " + this);
        return i.iterator();
    }


    // manage index in sync with the current data

    public boolean add(Object o) {
        if (index == null) {
            return super.add(o);
        }
        //@todo 29 optimize implementation. We do not need to remove from index and add again, but only add (this,o) to the index. However this is noncritical, since due to lazy initialization these changes will usually only occur after the contents of this clause have stabilized.
        //assert that after index.remove(this); index.isEmpty() : "index " + index + " is empty after removing its single clause " + this;
        index.clear();
        boolean changed = super.add(o);
        index.add(this);
        return changed;
    }

    public void clear() {
        super.clear();
        if (index != null) {
            index.clear();
        }
    }

    public boolean remove(Object o) {
        if (index == null) {
            return super.remove(o);
        }
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
