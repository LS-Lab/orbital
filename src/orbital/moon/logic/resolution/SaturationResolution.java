/**
 * @(#)SaturationResolution.java 1.2 2004-01-14 Andre Platzer
 *
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
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
 * Na&iuml;ve saturation. Repeatedly adds all resolvents of the first
 * clause with other clauses to the clausal set.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class SaturationResolution extends ResolutionBase {
    private static final Logger logger = Logger.getLogger(SaturationResolution.class.getName());

    /**
     * Perform a <em>fair</em> selection of one clause out of S.
     * Does not remove the selected clause from S.
     * @preconditions !S.isEmpty()
     * @postconditions OLD(S) = S &and; RES&isin;S
     */
    protected Clause selectClause(ClausalSet S) {
	assert S instanceof LinkedHashSet : "instance of " + LinkedHashSet.class + " expected for fair selection";
	Iterator i = S.iterator();
	Clause C = (Clause) i.next();
	return C;
    }


    /**
     * Delete superfluous clauses.
     * Apply any deletion strategies to the specified sets of clauses R and S.
     * @param newResolvents the new resolvents just resolved most recently.
     * @param setOfClauses the current set of clauses prior to adding newResolvents.
     * @internal run-time is heavily dependent on the precise order of operations (by factors of 4).
     */
    protected void deletion(ClausalSet newResolvents, ClausalSet setOfClauses) {
	//@internal first letting setOfClauses subsume newResolvents seems better, perhaps because setOfClauses already survived a longer time so it is more likely for them to be more general in the subsumption hierarchy.
	newResolvents.removeAllSubsumedBy(setOfClauses);
	//setOfClauses.removeAllSubsumedBy(newResolvents);
	if (true)
	    return;
	// remove tautologies and handle contradictions
    	// for all clauses F&isin;newResolvents
    	for (Iterator i = newResolvents.iterator(); i.hasNext(); ) {
	    final Clause F = (Clause) i.next();
	    if (F.isElementaryValid())
		// if F is obviously valid, forget about it for resolving a contradiction
		i.remove();
	}
    }

    /**
     * @param knowledge base W assumed consistent.
     * W is kept in clausal normal form, and thus contains sets of literals.
     * @param query the initial set of support.
     * @preconditions knowledgebase is satisfiable
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     * @todo add unit-preference, i.e. do k unit resolutions then 1 resolution, etc.
     */
    protected boolean prove(final ClausalSet knowledgebase, final ClausalSet query) {
	assert !knowledgebase.contains(Clause.CONTRADICTION) : "knowledgebase W assumed consistent, so contains no elementary contradiction";
	assert !query.contains(Clause.CONTRADICTION) : "query contains no elementary contradiction any more";
	ClausalSet setOfClauses = SetOfSupportResolution.INDEXING
	    ? new IndexedClausalSetImpl(query)
	    : getClausalFactory().createClausalSet(query);
	setOfClauses.addAll(knowledgebase);
	setOfClauses.removeAllSubsumedBy(setOfClauses);

	logger.log(Level.FINEST, "saturation resolution starting with {0}", new ClausalSetImpl(setOfClauses));

	// counts how many clauses of setOfClauses have no resolution possibilities, consequtively
	int noResolutionsCounter = 0;
	while (!setOfClauses.isEmpty()) {
	    // fairly choose any clause C&isin;S
	    final Clause C = selectClause(setOfClauses);
	    assert !C.equals(Clause.CONTRADICTION) : "already checked for contradiction";

	    logger.log(Level.FINEST, "resolving {0} with ...", new ClauseImpl(C));

	    // the set of resolvents obtained from resolution of C with any D that are new to us
	    ClausalSet newResolvents = getClausalFactory().newClausalSet();
	    // whether C has been resolved with any D
	    boolean resolvable = false;
	    // choose any clause D&isin;S
	    for (Iterator i2 = setOfClauses.getProbableComplementsOf(C);
		 i2.hasNext(); ) {
		final Clause D = (Clause) i2.next();
		logger.log(Level.FINEST, "resolving {0} with {1}", new Object[] {new ClauseImpl(C), new ClauseImpl(D)});
		// try to resolve C with D
		for (Iterator resolvents = ((ClauseImpl)C).resolveWithVariantFactors(D); resolvents.hasNext(); ) {
		    resolvable = true;
		    final Clause R = (Clause)resolvents.next();
		    if (setOfClauses.contains(R)) {
			continue;
		    } else if (R.equals(Clause.CONTRADICTION)) {
			logger.log(Level.FINE, "resolved contradiction {0} from {1} and {2}",  new Object[] {R, C, D});
			return true;
		    } else {
			newResolvents.add(R);
		    }
		}
	    }
	    //assert !newResolvents.isEmpty() || !resolvable: "there are no resolvents => !resolvable";
	    if (!resolvable || newResolvents.isEmpty()) {
		//@internal even when there are resolvents which we already knew, we should terminate resolution if no new things happen.
		//@internal better not remove link-less C in case a refinement like ordered resolution is used. Then possible resolutions may have been deferred until bigger literals have been resolved.
		noResolutionsCounter++;
		if (noResolutionsCounter >= 2*setOfClauses.size()) {
		    //@xxx factor 2 is just to be sure
		    // no clause in setOfClauses can resolve anymore
		    logger.log(Level.FINEST, "saturation stuck with no more clauses appearing {0}", new ClausalSetImpl(setOfClauses));
		    return false;
		}
		//@internal removing and adding C ensures that we consider the other clauses next before resolving C again
		setOfClauses.remove(C);
		setOfClauses.add(C);
		continue;
	    } else {
		noResolutionsCounter = 0;
	    }

	    System.out.println("  setOfClauses " + setOfClauses.size() + "\tresolvents " + newResolvents.size());
	    deletion(newResolvents, setOfClauses);
	    System.out.println(" >setOfClauses " + setOfClauses.size() + "\tresolvents " + newResolvents.size());
	    //@internal removing and adding C ensures that we first consider the other clauses before resolving C again
	    setOfClauses.remove(C);
	    setOfClauses.addAll(newResolvents);
	    setOfClauses.add(C);
	}

	logger.log(Level.FINEST, "saturation has no clauses {0}", new ClausalSetImpl(setOfClauses));
	return false;
    }

}
