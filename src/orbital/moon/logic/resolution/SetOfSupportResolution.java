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
     * Whether to use indexing for the clausal sets knowledgebase and set of support.
     */
    private static final boolean INDEXING = false;

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
     * Delete superfluous clauses.
     * Apply any deletion strategies to the specified sets of clauses U and S.
     * @param newResolvents the new resolvents just resolved most recently.
     * @param usable the set of usable clauses in the knowledgebase which are not in the set of support.
     * @param setOfSupport the current set of support prior to adding newResolvents.
     * @internal run-time is heavily dependent on the precise order of operations (by factors of 4).
     */
    protected void deletion(ClausalSet newResolvents, ClausalSet usable, ClausalSet setOfSupport) {
	//@internal first letting setOfSupport subsume newResolvents seems better, perhaps because setOfSupport already survived a longer time so it is more likely for them to be more general in the subsumption hierarchy.
	newResolvents.removeAllSubsumedBy(setOfSupport);
	setOfSupport.removeAllSubsumedBy(newResolvents);
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
     */
    protected boolean prove(final ClausalSet knowledgebase, final ClausalSet query) {
	assert !knowledgebase.contains(Clause.CONTRADICTION) : "knowledgebase W assumed consistent, so contains no elementary contradiction";
	assert !query.contains(Clause.CONTRADICTION) : "query contains no elementary contradiction any more";
	ClausalSet usable = INDEXING
	    ? new IndexedClausalSetImpl(knowledgebase)
	    : getClausalFactory().createClausalSet(knowledgebase);
	ClausalSet setOfSupport = INDEXING
	    ? new IndexedClausalSetImpl(query)
	    : getClausalFactory().createClausalSet(query);
	setOfSupport.removeAllSubsumedBy(setOfSupport);
	usable.removeAllSubsumedBy(setOfSupport);
	usable.removeAllSubsumedBy(usable);

	while (!setOfSupport.isEmpty()) {
	    // fairly choose any clause C&isin;S
	    final Clause C = selectClause(setOfSupport);
	    assert !C.equals(Clause.CONTRADICTION) : "already checked for contradiction";
	    //@internal moving C from S to U will prevent repetitive resolution of D (except with new clauses appearing in S all by themselves and thus resolving with our C by their own)
	    usable.add(C);

	    // the set of resolvents obtained from resolution of C with any D that are new to us
	    ClausalSet newResolvents = getClausalFactory().newClausalSet();
	    // whether C has been resolved with any D
	    boolean resolvable = false;
	    // choose any clause D&isin;U&cup;S
	    for (Iterator i2 = new SequenceIterator(new Iterator[] {
		    usable.getProbableComplementsOf(C),
		    setOfSupport.getProbableComplementsOf(C)
	        });
		 i2.hasNext(); ) {
		final Clause D = (Clause) i2.next();
		// try to resolve C with D
		for (Iterator resolvents = ((ClauseImpl)C).resolveWithVariantFactors(D); resolvents.hasNext(); ) {
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

	    System.out.println("  setOfSupport " + setOfSupport.size() + "\tusable " + usable.size() + "\tresolvents " + newResolvents.size());
	    deletion(newResolvents, usable, setOfSupport);
	    System.out.println(" >setOfSupport " + setOfSupport.size() + "\tusable " + usable.size() + "\tresolvents " + newResolvents.size());
	    setOfSupport.addAll(newResolvents);
	}

	// usable is satisifiable, and only setOfSupport={} is inconsistent, so conjecture was found wrong
	return false;
    }

}
