/**
 * @(#)ClausalSetImpl.java 0.8 2003-04-23 Andre Platzer
 *
 * Copyright (c) 2001-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import java.util.Set;
import java.util.LinkedHashSet;

import java.util.Iterator;
import orbital.util.Utility;
import orbital.util.Setops;
import orbital.logic.functor.Functionals;

/**
 * Default implementation of a representation of a set of clauses.
 * @version 0.8, 2003-04-23
 * @author  Andr&eacute; Platzer
 */
public class ClausalSetImpl extends LinkedHashSet/*_<Clause>_*/ implements ClausalSet {
    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public ClausalSetImpl(Set/*_<Clause>_*/ clauses) {
	super(clauses);
	assert Setops.all(clauses, Functionals.bindSecond(Utility.instanceOf, Clause.class)) : "instanceof Set<Formula>";
    }
    public ClausalSetImpl() {}

    public Iterator/*_<Clause>_*/ getProbableComplementsOf(Clause C) {
	//@todo use indexing for far better implementation
	return iterator();
    }

    public boolean removeAllSubsumedBy(ClausalSet T) {
	boolean avoidSelf = false;
	if (T.isEmpty()) {
	    return false;
	} else if (this == T) {
	    avoidSelf = true;
	} else if (this.equals(T)) {
	    throw new IllegalArgumentException("directly subsuming a set of clauses with itself would illegally result in the empty set (without additional constraints): \n   " + this + "\n = " + T);
	}
	boolean changed = false;
removeSubsumed:
	for (Iterator i = iterator(); i.hasNext(); ) {
	    final Clause D = (Clause)i.next();
	
	    for (Iterator j = T.iterator(); j.hasNext(); ) {
		final Clause C = (Clause)j.next();
		if (avoidSelf && C == D)
		    // avoid self-subsumption, since then everything would be subsumed
		    continue;

		if (C.subsumes(D)) {
		    i.remove();
		    changed = true;
		    continue removeSubsumed;
		}
	    }
	}
	return changed;
    }
}
