/**
 * @(#)IterativeExpanesion.java 1.0 2002-07-24 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;

import java.util.Iterator;

import java.util.List;
import java.util.Collections;
import orbital.util.Setops;

import orbital.logic.functor.Functionals;
import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Real;
import orbital.math.Values;

import java.util.LinkedList;

import orbital.util.Pair;
import orbital.util.KeyValuePair;

/**
 * Iterative Expansion (IE).
 * <p>
 * Iterative Expansion is a memory-bounded search algorithm that combines the space complexity
 * advantages of IDA<sup>*</sup> with A<sup>*</sup>'s ability of remembering more than just a bound
 * from the rest of the search tree. IE is inferior to SMA<sup>*</sup> which has a more flexible
 * tradeoff between reducing memory consumption and remembering parts of the search tree in order to
 * avoid re-expansion. However, IE has much less memory management overhead and thus has a comparable
 * performance to that of SMA<sup>*</sup>.
 * Nevertheless, all current memory-bounded algorithms have difficulties with problems having
 * too many distinct f-costs (i.e. |f(S)| is large).
 * </p>
 *
 * @author Andr&eacute; Platzer
 * @version 1.0, 2002-07-24
 * @internal also has a simple recursive formulation.
 * @see "Russel, S. (1992?) Efficient memory-bounded search methods."
 * @see "Korf, R.E. (1991) Best-first search with limited memory. UCLA Comp. Sci.Ann."
 * @todo derive IterativeExpansion from DepthFirstBoundingSearch, or doesn't that use DFS.OptionIterator, because it expands best local neighbour (also with varying f-costs on return of expansion)?
 * However, it's not BestFS.OptionIterator, either, since we don't consider backing up to some very different location somewhere in state space, but keep our mind focues on the current local neighbours.
 * So perhaps we should adapt BestFS.OptionIterator.add such that we forget about the old choices and simply sort the neighbourhood, and keep a stack of sorted local neighbourhoods. (remembering the second best, only, is perhaps a bad idea, since we may back up to the second (and third...) best multiple times if all neighbours are still below bound.).
 * On the current search path, we keep a stack of sorted lists of neighbours, and reinsert the last best element into the sorted list with new f-cost, on "backup".
 * @internal we do not extend GeneralBoundingSearch, since the bounds vary from layer to layer (argument of the recursive call).
 */
public class IterativeExpansion extends GeneralSearch implements EvaluativeAlgorithm {
    private static final long serialVersionUID = 4225973116092481279L;
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @serial
     */
    private Function heuristic;
    /**
     * Create a new instance of IDA<sup>*</sup> search.
     * Which is a bounding search using the evaluation function f(n) = g(n) + h(n).
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @see #getEvaluation()
     */
    public IterativeExpansion(Function heuristic) {
    	setHeuristic(heuristic);
	System.err.println(">>> @FIXME this implementation of " + getClass() + " still has errors");
    }

    public Function getHeuristic() {
	return heuristic;
    }

    public void setHeuristic(Function heuristic) {
	Function old = this.heuristic;
	this.heuristic = heuristic;
	firePropertyChange("heuristic", old, this.heuristic);
    }

    /**
     * f(n) = g(n) + h(n).
     */
    public Function getEvaluation() {
	return evaluation;
    }
    private transient Function evaluation;
    void firePropertyChange(String property, Object oldValue, Object newValue) {
	super.firePropertyChange(property, oldValue, newValue);
	if (!("heuristic".equals(property) || "problem".equals(property)))
	    return;
	GeneralSearchProblem problem = getProblem();
	this.evaluation = problem != null
	    ? Functionals.compose(Operations.plus, problem.getAccumulatedCostFunction(), getHeuristic())
	    : null;
    }	
    /**
     * Sustain transient variable initialization when deserializing.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    	in.defaultReadObject();
	firePropertyChange("heuristic", null, this.heuristic);
    }

    public orbital.math.functional.Function spaceComplexity() {
	throw new UnsupportedOperationException("@todo");
    }
    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function complexity() {
	return (orbital.math.functional.Function) Operations.power.apply(Values.getDefaultInstance().symbol("b"),Functions.id);
    }
    /**
     * Optimal if heuristic is admissible, and initial bound sufficiently large (usually &infin;).
     */
    public boolean isOptimal() {
    	return true;
    }

    protected Object/*>S<*/ solveImpl(GeneralSearchProblem/*<A,S>*/ problem) {
	return solveByIterativeExpand(problem.getInitialState(), Values.POSITIVE_INFINITY).A;
    }

    /**
     * @return a Pair&lt;S,Real&gt; of solution state and new cost of node.
     * @internal we do not need to set the f-cost, but only tell our caller the new f-cost for updating successors.
     * @todo optimizable by far, also modularize to an OptionIterator?
     * @todo optimize (the performance is embarrassing)
     */
    private final Pair/*<S,Real>*/ solveByIterativeExpand(Object/*>S<*/ node, Real bound) {
	assert bound.compareTo(Values.ZERO) >= 0 && !bound.isNaN() : "bound " + bound + " >= 0";
	Real cost = (Real) getEvaluation().apply(node);
	System.err.println(node + ",\t" + cost + "/" + bound);
	if (cost.compareTo(bound) > 0)
	    {System.err.println("cut");
	    return new Pair(null, cost);}
	else if (getProblem().isSolution(node))
	    return new Pair(node, cost);
	//@internal optimizable by using a (min) heap instead of a list that is kept in sorted order
	// here, we currently use a (sorted) list of KeyValuePairs with the key that is used for ordering being the f-cost
	List/*_<KeyValuePair>_*/ successors = new LinkedList();
	{
	    final Function f = getEvaluation();
	    for (Iterator i = GeneralSearch.expand(getProblem(), node); i.hasNext(); ) {
		final Object o = i.next();
		successors.add(new KeyValuePair(f.apply(o), o));
	    }
	}
	if (successors.isEmpty())
	    return new Pair(null, cost);
	// sort successors in order to have fast access to min and second-best min
	Collections.sort(successors);
	assert orbital.util.Utility.sorted(successors, null) : "@post Collections.sort";
	while (cost.compareTo(bound) <= 0) {
	    final KeyValuePair bestPair = (KeyValuePair)successors.get(0);
	    final Object/*>S<*/ best = bestPair.getValue();
	    assert !Setops.some(successors, new orbital.logic.functor.Predicate() { public boolean apply(Object o) {return ((Comparable)((KeyValuePair)o).getKey()).compareTo(bestPair.getKey()) < 0;} }) : "best has lowest f-cost";
	    final Real newbound = (Real) Operations.min.apply(bound, (Real)((KeyValuePair)successors.get(1)).getKey());
	    System.err.println(node + ",\t" + cost + "/" + bound + "\t expanding to " + best + ",\t" + bestPair.getKey() + "/" + newbound + "\n\t\talternative " + ((KeyValuePair)successors.get(1)).getValue() + ", " + ((KeyValuePair)successors.get(1)).getKey());
	    final Pair solutionAndCostUpdate = solveByIterativeExpand(best, newbound);
	    final Object/*>S<*/ solution = solutionAndCostUpdate.A;
	    if (solution != null)
		return new Pair(solution, cost);
	    // circumscription of getEvaluation().set(best, its new cost (from recursive call));
	    System.err.println(best + ",\t" + solutionAndCostUpdate.B + "/" + newbound + "\treally updated cost to " + solutionAndCostUpdate.B);
	    successors.remove(0);
	    Setops.insert(successors, new KeyValuePair(solutionAndCostUpdate.B, best));
	    assert orbital.util.Utility.sorted(successors, null) : "@post Setops.insert";
	    System.err.print(node + ",\t" + cost + "/" + bound);
	    cost = (Real) ((KeyValuePair)successors.get(0)).getKey();
	    System.err.println("\tupdated cost to " + cost + " (due to " + ((KeyValuePair)successors.get(0)).getValue() + ")");
	}

	// circumscription of getEvaluation().set(node, cost);
	//@internal we do not need to set the f-cost, but only tell our caller the new f-cost for updating successors
	// @fixme change the f-cost or the heuristics, not the accumulated cost!
	//getProblem().getAccumulatedCostFunction().set(node, cost.subtract((Real)getHeuristic().apply(node)));
	//if (!cost.equals(getEvaluation().apply(node)))
	//    throw new AssertionError("setting f-cost did not work as expected");
	return new Pair(null, cost);
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
	throw new AssertionError("should not get called");
    }
    
}// IterativeExpansion
