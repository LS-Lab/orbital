/**
 * @(#)AStar.java 1.0 2000/09/18 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;

import orbital.math.functional.Functions;
import orbital.math.functional.Operations;
import orbital.math.Values;

/**
 * A<sup>*</sup> search class.
 * <p>
 * In fact this is a special instance of WA<sup>*</sup> with W=1.</p>
 * <p>
 * A<sup>*</sup> is complete, optimal if the heuristic function h is admissible.
 * It has a time and space complexity of O(b<sup>d</sup>).
 * A<sup>*</sup> is optimal in another sense: no other algorithm expands less nodes than A<sup>*</sup> 
 * with the same heuristic function. It also expands nodes only once.
 * But this does not mean that it is always fastest.
 * A<sup>*</sup> expands less nodes with better heuristic functions
 * (h' is better than h if 0 &lt; h &lt; h' &le; h<sup>*</sup>).</p>
 * <p>
 * To be precise, like most (if not all) other search algorithms, A<sup>*</sup> achieves completeness only
 * on locally finite graphs (i.e. with finite branching factors) provided that the costs
 * keep away from zero, i.e.
 * <center>&exist;&epsilon;&gt; 0 &forall;s&isin;S&forall;a&isin;A(s) c(s,a) &gt; &epsilon;</center>
 * </p>
 * <p>
 * For the (admissible) heuristic h=0, A<sup>*</sup> results in (non-uniform cost) {@link BreadthFirstSearch},
 * whilst no admissible heuristic can exist that will let A<sup>*</sup> resemble DepthFirstSearch.
 * For the ignorant cost function g=0, A<sup>*</sup> degrades to a simple best first search that
 * always selects best nodes first regardless of the history.
 * This behaviour for g=0 resembles HillClimbing but is not limited to selecting
 * from most recently expaned nodes,
 * nevertheless with g=0 it is still incomplete and not optimal.</p>
 *
 * @version 1.0, 2000/09/18
 * @author  Andr&eacute; Platzer
 * @see "P. E. Hart, N. J. Nilsson, and B. Raphael. A formal basis for the heuristic determination of minimum cost paths. IEEE Transactions of Systems Science and Cybernetics, 4:100-107, 1968."
 * @todo update g(n) to fit minimum cost to reach n?
 */
public class AStar extends BestFirstSearch implements HeuristicAlgorithm {
    private static final long serialVersionUID = 4507556265837848039L;
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @serial
     */
    private Function heuristic;
    /**
     * Create a new instance of A<sup>*</sup> search.
     * Which is a best first search using the evaluation function f(n) = g(n) + h(n).
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f.
     * @see #getEvaluation()
     */
    public AStar(Function heuristic) {
    	this.heuristic = heuristic;
    }

    public Function getHeuristic() {
	return heuristic;
    }

    public void setHeuristic(Function heuristic) {
	this.heuristic = heuristic;
    }

    /**
     * f(n) = g(n) + h(n).
     */
    public Function getEvaluation() {
	return evaluation;
    }
    private transient Function evaluation = createEvaluation();
    private final Function createEvaluation() {
	//@todo could transform into a package-protected Support class with a constructor argument of HeuristicAlgorithm
	return new Function() {
    		public Object apply(Object a) {
		    GeneralSearchProblem.Option o = (GeneralSearchProblem.Option)a;
		    return Operations.plus.apply(Values.valueOf(o.getCost()), heuristic.apply(o/*.getState()*/));
    		}
	    };
    }
    /**
     * Sustain transient variable initialization when deserializing.
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    	in.defaultReadObject();
    	evaluation = createEvaluation();
    }

    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function complexity() {
	return (orbital.math.functional.Function) Operations.power.apply(Values.symbol("b"),Functions.id);
    }
	
    /**
     * O(b<sup>d</sup>) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function spaceComplexity() {
	return complexity();
    }
	
    /**
     * Optimal if heuristic is admissible.
     */
    public boolean isOptimal() {
    	return true;
    }
}
