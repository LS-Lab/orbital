/**
 * @(#)IterativeDeepeningAStar.java 1.0 2000/09/20 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Iterator;

import orbital.logic.functor.Functionals;
import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Real;
import orbital.math.Values;

/**
 * Iterative Deepening A<sup>*</sup> (IDA<sup>*</sup>). A heuristic search algorithm.
 * <p>
 * Apart from the evaluation function, IDA<sup>*</sup> has not much in common with A<sup>*</sup>
 * but resembles ID, instead. It is not an instance of best-first search, but of bounding search.
 * Which also means that it has less administrative overhead than maintaining priority queues.</p>
 * <p>
 * IDA<sup>*</sup> is complete, optimal if the heuristic function h is admissible.
 * It has a time complexity of O(b<sup>d</sup>) and a space complexity of O(b*d).</p>
 * <p>
 * The effective time complexity of IDA<sup>*</sup> depends upon the number of different values
 * the heuristic returns. If |h(S)| is small, IDA<sup>*</sup> only has to go through very little
 * iterations. Of course, if the heuristic has too few values, than it does not guide search at all.</p>
 *
 * @version 1.0, 2000/09/20
 * @author  Andr&eacute; Platzer
 * @see "Korf, R.E. (1985) Depth-first iterative deepening. An optimal admissible tree search. AIJ, 27(1), 97-109"
 * @todo why is IDA* with iterators about 11% slower than IDA* with collections?
 */
public class IterativeDeepeningAStar extends DepthFirstBoundingSearch implements HeuristicAlgorithm {
    private static final long serialVersionUID = 5814132461076107994L;
    /**
     * Cost of cheapest node pruned, or <code>null</code> if we did not prune a node yet.
     * @serial
     * @see IterativeDeepeningAStar#havePruned
     */
    private Real nextBound;
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
    public IterativeDeepeningAStar(Function heuristic) {
    	setHeuristic(heuristic);
    	this.nextBound = null;
    	setContinuedWhenFound(true);
    	//@TODO: setContinuedWhenFound(?); depends upon whether or not h is admissible?
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

    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function complexity() {
	return (orbital.math.functional.Function) Operations.power.apply(Values.getDefaultInstance().symbol("b"),Functions.id);
    }
    /**
     * Optimal if heuristic is admissible.
     */
    public boolean isOptimal() {
    	return true;
    }

    /**
     * Compare f(n) with bound, normally and in case update cheapest pruned bound.
     * @see #nextBound
     */
    protected boolean isOutOfBounds(Object/*>S<*/ node) {
	Real v = (Real) getEvaluation().apply(node);
	if (v.compareTo(Values.getDefaultInstance().valueOf(getBound())) <= 0)
	    return false;
	// OutOfBounds -> nextBound is cheapest node pruned (minimum)
	if (nextBound == null || v.compareTo(nextBound) < 0)
	    nextBound = v;
	return true;
    }

    /**
     * Solve with bounds f(n<sub>0</sub>), f(n<sub>1</sub>), f(n<sub>2</sub>), ... until a solution is found.
     * Where n<sub>0</sub>=root, and n<sub>i</sub> is the cheapest node pruned in iteration i-1.
     */
    protected Object/*>S<*/ solveImpl(GeneralSearchProblem problem) {
	final Object n0 = problem.getInitialState();
	nextBound = (Real) getEvaluation().apply(n0);
	Object/*>S<*/ solution;
	do {
	    setBound(nextBound.doubleValue());
	    nextBound = null;
	    solution = super.search(createTraversal(problem));
	} while (solution == null && nextBound != null);
	// either we have a solution,
	// or we have no nextBound because no node was pruned, so no further nodes can be found if we extended the bounds.

    	// report solution or fail
	return solution;
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
	return new DepthFirstSearch.OptionIterator(problem);
    }



    //	protected GeneralSearchProblem.Option search(Collection nodes) {
    //		final Object n0 = nodes.iterator().next();
    //		nextBound = (Scalar) getEvaluation().apply(n0);
    //		GeneralSearchProblem.Option solution;
    //		do {
    //			setBound(nextBound.doubleValue());
    //			nextBound = null;
    //			Collection n = createCollection();
    //			n.addAll(nodes);
    //			solution = super.search(n);
    //		} while (solution == null && nextBound != null);
    //		// either we have a solution,
    //		// or we have no nextBound because no node was pruned, so no further nodes can be found if we extended the bounds.
    //
    //    	// report solution or fail
    //		return solution;
    //	}
}
