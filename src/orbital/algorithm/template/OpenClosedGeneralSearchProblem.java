/**
 * @(#)OpenClosedGeneralSearchProblem.java 0.9 2001/05/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Collection;
import java.util.Set;
import java.io.Serializable;

import orbital.logic.functor.MutableFunction;
import java.util.Iterator;

import orbital.util.Setops;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * GeneralSearchProblem wrapper keeping track of open and closed sets.
 * <p>
 * This class extends a problem such that it keeps track of closed sets.
 * And thus it optimizes search in search spaces that form graphs, as well as prevents
 * circular expansions. However, this book-keeping will prove useless and time-consuming
 * on search spaces that form real trees (without repetitive nodes and cross edges).</p>
 * <p>
 * The closed set is the set of closed nodes that have already been expanded.
 * Nodes in the search space that are not closed are called open.</p>
 *
 * @version 0.9, 2001/05/09
 * @author  Andr&eacute; Platzer
 * @see DynamicBacktracking
 * @see <a href="{@docRoot}/Patterns/Design/Decorator.html">Decorator</a>
 * @todo optimizable for A* since then we can replace a child c which is already in the open set of candidates by c when c can be reached by a shorter path.
 */
public class OpenClosedGeneralSearchProblem/*<A,S>*/ implements GeneralSearchProblem/*<A,S>*/, Serializable {
    private static final long serialVersionUID = 7335267055267390660L;
    /**
     * The proper problem to solve which does not yet keep track of closed sets.
     * @serial
     */
    private final GeneralSearchProblem problem;
    /**
     * The set of closed states that have already been expanded.
     * Nodes in the search space that are not closed are called open.
     * @serial
     */
    private Set closedSet;
    /**
     * Create a GeneralSearchProblem keeping track of closed sets.
     * @param problem the proper problem to solve which does not yet keep track of closed sets.
     */
    public OpenClosedGeneralSearchProblem(GeneralSearchProblem problem) {
	this.problem = problem;
    }
	
    /**
     * Get the proper (inner) problem to solve which does not yet keep track of closed sets.
     */
    public GeneralSearchProblem getProblem() {
	return problem;
    }

    /**
     * Get the initial state of the problem.
     * Clears the closed set.
     */
    public Object getInitialState() {
	closedSet = new HashSet();
	return problem.getInitialState();
    }
    
    /**
     * Only returns actions leading to open nodes, and only if s is not closed itself.
     */
    public Iterator actions(Object/*>S<*/ s) {
	if (closedSet.contains(s))
	    return Collections.EMPTY_LIST.iterator();
	// visit s by expanding it, so add s to the closed list
	closedSet.add(s);
	LinkedList copy = new LinkedList();
	for (Iterator i = problem.actions(s); i.hasNext(); ) {
	    Object a = i.next();
	    if (closedSet.contains(problem.states(a, s).next()))
		i.remove();
	    else
		copy.add(a);
	}
	return Setops.unmodifiableIterator(copy.iterator());
    }

    public Iterator states(Object/*>A<*/ a, Object/*>S<*/ s) {
	//@internal we do not again check like actions already did, but assume the usual case that a results from actions(s)
	return problem.states(a, s);
    }

    public TransitionModel.Transition transition(Object/*>A<*/ a, Object/*>S<*/ s, Object/*>S<*/ sp) {
	//@internal we do not again check like actions already did, but assume the usual case that a and sp result from actions(s) and states(a,s)
	return problem.transition(a, s, sp);
    }

    public boolean isSolution(Object/*>S<*/ s) {
	return problem.isSolution(s);
    }

    public MutableFunction getAccumulatedCostFunction() {
	return problem.getAccumulatedCostFunction();
    }
}
