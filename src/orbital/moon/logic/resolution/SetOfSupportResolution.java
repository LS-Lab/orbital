/**
 * @(#)SetOfSupportResolution.java 1.1 2003-11-05 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;
import orbital.moon.logic.ClassicalLogic;
import orbital.moon.logic.ClassicalLogic.Utilities;

import orbital.logic.imp.*;
import orbital.logic.sign.*;

import orbital.logic.functor.*;

import orbital.util.SequenceIterator;
import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Direct set of support resolution.
 *
 * @version 1.1, 2003-11-05
 * @author  Andr&eacute; Platzer
 */
public class SetOfSupportResolution extends ResolutionBase {

    /**
     * Perform a <em>fair</em> selection of one clause out of S.
     * Removes the selected clause from S.
     * @preconditions !S.isEmpty()
     * @postconditions OLD(S) = {RES} union S
     */
    protected Clause selectClause(ClausalSet S) {
	assert S instanceof LinkedHashSet : "instance of " + LinkedHashSet.class + " expected for fair selection";
	Iterator i = S.iterator();
	Clause C = (Clause) i.next();
	i.remove();
	return C;
    }

    /**
     * @param knowledge base W assumed consistent.
     * W is kept in clausal normal form, and thus contains sets of literals.
     * @param query the initial set of support.
     * @preconditions knowledgebase is satisfiable
     */
    protected boolean prove(final ClausalSet knowledgebase, final ClausalSet query) {
	assert !knowledgebase.contains(Clause.CONTRADICTION) : "knowledgebase W assumed consistent, so contains no elementary contradiction";
	assert !query.contains(Clause.CONTRADICTION) : "query contains no elementary contradiction any more";
	ClausalSet usable = knowledgebase;
	ClausalSet setOfSupport = query;
	while (!setOfSupport.isEmpty()) {
	    // fairly choose any clause C&isin;S
	    final Clause C = selectClause(setOfSupport);
	    assert !C.equals(Clause.CONTRADICTION) : "already checked for contradiction";
	    //@internal moving C from S to U will prevent repetitive resolution of D (except with new clauses appearing in S all by themselves and thus resolving with our C by their own)
	    usable.add(C);

	    // the set of resolvents obtained from resolution of C with any D that are new to us
	    Collection/*_<Clause>_*/ newResolvents = new LinkedList();
	    // whether C has been resolved with any D
	    boolean resolvable = false;
	    // choose any clause D&isin;U&cup;S
	    for (Iterator i2 = new SequenceIterator(new Iterator[] {usable.iterator(), setOfSupport.iterator()});
		 i2.hasNext(); ) {
		final Clause D = (Clause) i2.next();
		// try to resolve C with D
		for (Iterator resolvents = C.resolveWithVariant(D); resolvents.hasNext(); ) {
		    resolvable = true;
		    final Clause R = (Clause)resolvents.next();
		    if (usable.contains(R) || setOfSupport.contains(R)) {
			continue;
		    } else if (R.equals(Clause.CONTRADICTION)) {
			logger.log(Level.FINE, "resolved contradiction {0} from {1} and {2}",  new Object[] {R, C, D});
			return true;
		    } else {
			newResolvents.add(R);
		    }
		}
	    }
	    if (!resolvable) {
		//@internal if C had not been resolvable (and we have no links, so that this has not been detected), remove C also from U. This will be performed generally once we implement links (and sublinks etc.).
		// remove link-less C also from U
		usable.remove(C);
	    }

	    //@todo deletion(newResolvents, usable, setOfSupport);
	    setOfSupport.addAll(newResolvents);
	}

	// usable is satisifiable, and only setOfSupport={} is inconsistent, so conjecture was found wrong
	return false;
    }
}
