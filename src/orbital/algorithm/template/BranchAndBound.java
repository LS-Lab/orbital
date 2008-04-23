/**
 * @(#)BranchAndBound.java 1.0 2000/09/22 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Collection;

import orbital.logic.functor.Functionals;
import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Values;
import orbital.math.Real;

/**
 * Branch-and-bound (B&B).
 * <p>
 * B&B is complete, optimal, and
 * has a time complexity of O(b<sup>d</sup>) and a space complexity of O(b*d).</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @note is a basic aspect of bounding the search space by the best known solution.
 *  This is automatically satisfied by several other search algorithms.
 * @internal Branch-and-bound is a technique from Operations Research.
 * @see "Lawler, E.L. and Wood, D.E. Branch-and-bound methods: A survey. Operations Research. 14(4):699-719. 1966."
 * @todo we could just as well formulate Branch-and-bound as a Decorator of GeneralSearchProblem (may be more useful for chaining and combining search algorithm policies). BUT such a decorator cannot easily tell the searching algorithm to continue even though we found a solution, in order to search for a better one. If however, decorated isSolution() would store the solution but return false in order to implement this, then the searching algorithm won't know the last (best) solution found either, but conclude that there is no solution at all.
 */
public class BranchAndBound/*<A,S>*/
    extends DepthFirstBoundingSearch/*<A,S>*/
    implements HeuristicAlgorithm/*<GeneralSearchProblem<A,S>,S>*/ {
    private static final long serialVersionUID = -1698181871423830937L;
    //TODO: is Operations Research Branch and Bound more general than this or equivalent? And what is Branch and Cut?
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @serial
     */
    private Function/*<S,Real>*/ heuristic;
    /**
     * A sufficiently high upper bound for a solution beyond which search will not continue.
     * @serial
     */
    private Real maxBound;
    
    /**
     * Create a new instance of Branch and Bound search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @param maximumUpperBound is a sufficiently high upper bound for a solution.
     *  If there is no solution below this bound, the search will fail.
     * @see #getEvaluation()
     */
    public BranchAndBound(Function heuristic, Real maximumUpperBound) {
        setHeuristic(heuristic);
        setContinuedWhenFound(true);
        setMaxBound(maximumUpperBound);
    }
    /**
     * @deprecated Since Orbital1.1 use {@link #BranchAndBound(Function,Real)} instead.
     */
    public BranchAndBound(Function heuristic, double maximumUpperBound) {
        this(heuristic, Values.getDefaultInstance().valueOf(maximumUpperBound));
    }
    BranchAndBound() {}
    
    /**
     * Get the maximum upper bound for a solution.
     */
    public Real getMaxBound() {
        return maxBound;
    }

    /**
     * Set the maximum upper bound for a solution.
     * @param maximumUpperBound is a sufficiently high upper bound for a solution.
     *  If there is no solution below this bound, the search will fail.
     */
    public void setMaxBound(Real maximumUpperBound) {
        this.maxBound = maximumUpperBound;
        // firePropertyChange
    }
    /**
     * @deprecated Since Orbital1.1 use {@link #setMaxBound(Real)} instead.
     */
    public void setMaxBound(double maximumUpperBound) {
        setMaxBound(Values.getDefaultInstance().valueOf(maximumUpperBound));
    }
        
    public Function/*<S,Real>*/ getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(Function/*<S,Real>*/ heuristic) {
        if (heuristic == null)
            throw new NullPointerException("null is not a heuristic");
        Function/*<S,Real>*/ old = this.heuristic;
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
    public boolean isOptimal() {
        return true;
    }
        
    /**
     * Updates bound to solution cost.
     * Since better solutions can only be found below this bound.
     */
    protected Object/*>S<*/ processSolution(Object/*>S<*/ node) {
        setBound((Real/*__*/) getProblem().getAccumulatedCostFunction().apply(node));
        return node;
    }
        
    protected Object/*>S<*/ solveImpl(GeneralSearchProblem problem) {
        setBound(getMaxBound());
        return super.solveImpl(problem);
    }
}
