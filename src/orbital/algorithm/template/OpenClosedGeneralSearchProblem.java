/**
 * @(#)OpenClosedGeneralSearchProblem.java 0.9 2001/05/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Collection;
import java.util.Set;
import java.io.Serializable;

import java.util.Iterator;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * GeneralSearchProblem wrapper keeping track of open and closed sets.
 * <p>
 * This class extends a problem to keeping track of closed sets.
 * And thus it optimizes search in search spaces that form graphs, as well as prevents
 * circular expansions. However, this book keeping will prove useless and time-consuming
 * on search spaces that form real trees (without repetitive nodes and cross edges).</p>
 * <p>
 * The closed set is the set of closed nodes that have already been expanded. Nodes in the search space that are not closed are called open.</p>
 *
 * @version 0.9, 2001/05/09
 * @author  Andr&eacute; Platzer
 * @see DynamicBacktracking
 * @see <a href="{@docRoot}/DesignPatterns/Decorator.html">Decorator</a>
 * @todo optimizable for A* since then we can replace a child c which is already in the open set of candidates by c when c can be reached by a shorter path.
 */
public
class OpenClosedGeneralSearchProblem implements GeneralSearchProblem, Serializable {
	/**
	 * The real problem to solve which does not yet keep track of closed sets.
	 * @serial
	 */
	private final GeneralSearchProblem problem;
	/**
	 * The set of closed nodes that have already been expanded.
	 * Nodes in the search space that are not closed are called open.
	 * @serial
	 * @todo privatize?
	 */
	protected Set closedSet;
	/**
	 * Create a GeneralSearchProblem keeping track of closed sets.
	 * @param problem the real problem to solve which does not yet keep track of closed sets.
	 */
	public OpenClosedGeneralSearchProblem(GeneralSearchProblem problem) {
		this.problem = problem;
	}
	
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
	 * Expands a state.
	 * Only returns open nodes, and only if n is not closed itself.
	 */
	public Iterator expand(Option n) {
		if (closedSet.contains(n))
			return null;
		LinkedList copy = new LinkedList();
		for (Iterator i = problem.expand(n); i.hasNext(); ) {
			Object o = i.next();
			copy.add(o);
			if (closedSet.contains(o))
				i.remove();
		}
		return copy.iterator();
	}

	public boolean isSolution(Option n) {
		return problem.isSolution(n);
	}

	public double getCost(Option option) {
		return problem.getCost(option);
	}
}
