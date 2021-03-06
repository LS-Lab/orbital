/**
 * @(#)WAStar.java 1.0 2000/09/18 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;

import orbital.logic.functor.Functionals;
import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Arithmetic;
import orbital.math.Real;
import orbital.math.Values;

/**
 * WA<sup>*</sup> search class.
 *
 * <p>
 * Time and memory requirements can be lowered significantly by multiplying the heuristic function
 * h(n) by a constant W&gt;1. WA<sup>*</sup> uses evaluation function f(n) = g(n) + W*h(n).
 * However solutions are no longer optimal but at most W times from optimal.</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see "Pohl, I. (1973). The avoidance of (relative) catastrophe, heuristic competence, genuine dynamic weighting and computational issues in heuristic problem solving. In Proceedings of the Third International Joint Conference on Artificial Intelligence (IJCAI-73), pages 20-23, Stanford, California, IJCAII."
 * @internal Sustain transient variable initialization when deserializing. Done by AStar.readObject() calling firePropertyChange(...).
 */
public class WAStar/*<A,S>*/ extends AStar/*<A,S>*/ {
    private static final long serialVersionUID = -3210623238172266780L;
    /**
     * the weighting argument W for the evaluation function.
     * @serial
     */
    private Real W;
    /**
     * Create a new instance of WA<sup>*</sup> search.
     * Which is a best first search using the evaluation function f(n) = g(n) + W*h(n).
     * @param W the weighting argument W for the evaluation function.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @preconditions W >= 1.
     * @see #getEvaluation()
     */
    public WAStar(Real W, Function/*<S,Real>*/ heuristic) {
        super(heuristic);
        this.setWeight(W);
    }
    public WAStar(double W, Function/*<S,Real>*/ heuristic) {
        this(Values.getDefaultInstance().valueOf(W), heuristic);
    }
    public WAStar(Function heuristic) {
        this(1, heuristic);
    }
    WAStar() {}

    /**
     * Get the weighting argument W for the evaluation function.
     */
    public Real getWeight() {
        return W;
    }
    /**
     * Get the weighting argument W for the evaluation function.
     */
    public void setWeight(Real W) {
        if (!(W.compareTo(W.one()) >= 0))
            throw new IllegalArgumentException("weighting argument W must be >= 1 for WA*");
        this.W = W;
    }

    /**
     * f(n) = g(n) + W*h(n).
     */
    public Function/*<S,Real>*/ getEvaluation() {
        return evaluation;
    }
    private transient Function/*<S,Real>*/ evaluation;
    void firePropertyChange(String property, Object oldValue, Object newValue) {
        super.firePropertyChange(property, oldValue, newValue);
        if (!("heuristic".equals(property) || "problem".equals(property) || "weight".equals(property)))
            return;
        GeneralSearchProblem/*<A,S>*/ problem = getProblem();
        this.evaluation = problem != null
            ? Functionals.compose(Operations.plus, problem.getAccumulatedCostFunction(), Functionals.compose(Operations.times, Functions.constant(getWeight()), getHeuristic()))
            : null;
    }

    /**
     * at most W times from optimal if heuristic is admissible.
     */
    public boolean isOptimal() {
        return false;
    }
}
