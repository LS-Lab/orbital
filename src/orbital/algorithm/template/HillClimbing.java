/**
 * @(#)HillClimbing.java 1.0 2000/09/17 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Iterator;
import java.util.Collection;
import java.util.Random;

import java.util.List;

import orbital.logic.functor.Function;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Collections;
import java.util.Comparator;
import orbital.math.MathUtilities;

import orbital.math.Values;

/**
 * Hill-climbing search.
 * An heuristic search algorithm and local optimizer.
 * <p>
 * ({@link LocalOptimizerSearch#BEST_LOCAL_SELECTION One variant} of hill-climbing)
 * Expands best nodes first, i.e. those that have min h(n) and forgets about the alternatives.</p>
 * <p>
 * Hill climbing is neither complete <em>nor</em> optimal,
 * has a time complexity of O(&infin;) but a space complexity of O(b).</p>
 * <p>
 * No special implementation data structure since hill climbing <em>discards old</em> nodes.
 * Because of this "amnesy", hill climbing is a suboptimal search strategy
 * and hill climbing is not complete.
 * Due to its concept hill-climbing may get caught at local extrema, will only perform a random walk
 * on "plateaux", and may have difficulties passing ridges when no "sloping" step actions exist.
 * However, these problems can be solved probabilistically by using an iterative <dfn>random-restart hill-climbing</dfn>
 * with a sufficient number of iterations. Random-restart hill-climbing requires that ties break randomly.
 * Which is the cause for hill-climbing to be a simple probabilistic algorithm.
 * </p>
 * <p>
 * <small>
 * Note that random-restart hill-climbing could be implemented by a Decorator decorating
 * GeneralSearchProblem with a broad range of equally cheap initial actions prepended,
 * that branch to several random locations.
 * </small>
 * </p>
 * <p>
 * Note that hill-climbing approximates gradient descent.
 * If the state space is spanned by a system of linear unequalities, and
 * the evaluation function is linear, then hill-climbing equals
 * the simplex algorithm of linear programming.
 * Local optimization guarantees that local optimum is global optimum if
 * the state-space as well as the evaluation function are convex, then.
 * </p>
 * <p>
 * <blockquote>
 * Hill-climbing "resembles trying to find the top of Mount Everest in a thick fog while suffering from amnesia." (Russel&amp;Norvig, Ch 4.4)
 * </blockquote>
 * </p>
 *
 * @version 1.0, 2000/09/17
 * @author  Andr&eacute; Platzer
 * @note the father of local optimizers, also the most simple version
 * @see Greedy
 */
public class HillClimbing extends LocalOptimizerSearch implements HeuristicAlgorithm {
    private static final long serialVersionUID = -3281919447532950063L;
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f(n) = h(n).
     * @serial
     */
    private Function heuristic;
    /**
     * Create a new instance of hill climbing search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @param localSelection the variant of local selection used.
     */
    public HillClimbing(Function heuristic, LocalSelection localSelection) {
	super(localSelection);
    	this.heuristic = heuristic;
    }
    /**
     * Create a new instance of hill climbing search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     */
    public HillClimbing(Function heuristic) {
    	this(heuristic, BEST_LOCAL_SELECTION);
    }

    public Function getHeuristic() {
	return heuristic;
    }

    public void setHeuristic(Function heuristic) {
	this.heuristic = heuristic;
    }

    /**
     * f(n) = h(n).
     * @internal wenn ich sonst einen Übergang wegen zu hoher akkumulierter Kosten verschmähen
     *  würde, käm ich ja nie mehr von meinem eingeschlagenen (evtl. Sackgassen) Pfad weg.
     */
    public Function getEvaluation() {
    	return getHeuristic();
    }

    /**
     * O(&infin;).
     */
    public orbital.math.functional.Function complexity() {
	return orbital.math.functional.Functions.constant(Values.POSITIVE_INFINITY);
    }
    /**
     * O(b) where b is the branching factor and d the solution depth.
     */
    public orbital.math.functional.Function spaceComplexity() {
	return orbital.math.functional.Functions.constant(Values.symbol("b"));
    }
    public boolean isOptimal() {
    	return false;
    }

    public boolean isCorrect() {
	return false;
    }

    protected Iterator createTraversal(GeneralSearchProblem problem) {
	return acceptStep(getLocalSelection().createLocalRestriction(problem, this), this);
    }


    private static Iterator acceptStep(GeneralSearchProblem problem, LocalOptimizerSearch algorithm) {
	return new OptionIterator_First(problem, algorithm);
    }
    /**
     * An iterator over a state space in (probabilistic) greedy order for hill-climbing.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     * @todo replace by acceptStep(restrictRandomly(problem,algorithm,1))
     */
    private static class OptionIterator_First extends LocalOptimizerSearch.OptionIterator {
	private static final long serialVersionUID = -3674513421043835094L;
	public OptionIterator_First(GeneralSearchProblem problem, LocalOptimizerSearch algorithm) {
	    super(problem, algorithm);
	    this.currentValue = ((Number) algorithm.getEvaluation().apply(getState())).doubleValue();
	}

	private double currentValue;

	/**
	 * {@inheritDoc}.
	 * <p>
	 * This implementation will only move to better nodes, categorically.</p>
	 * @internal we avoid using the help of EvaluativeComparator here, because we ourselves can cache currentValue
	 */
	public boolean accept(Object/*>S<*/ state, Object/*>S<*/ sp) {
	    final LocalOptimizerSearch algorithm = getAlgorithm();
	    final double value = ((Number) algorithm.getEvaluation().apply(sp)).doubleValue();
	    final double deltaEnergy = value - currentValue;

	    if (deltaEnergy <= 0) {
		// an improvement
		currentValue = value;
		return true;
	    } else
		return false;
	}

	public boolean hasNext() {
	    return true;
	}
    };

//     /**
//      * An iterator over a state space in (randomized) greedy order for hill-climbing.
//      * @version 1.0, 2001/08/01
//      * @author  Andr&eacute; Platzer
//      * @todo really turn this class into a inner static class whose constructor requires an EvaluativeAlgorithm that also is a ProbabilisticAlgorithm?
//      * @todo replace by acceptStep(restrictRandomly(restrictBest(problem),algorithm,1))
//      */
//     public class OptionIterator extends GeneralSearch.OptionIterator {
// 	private static final long serialVersionUID = -6802484555661425572L;
// 	/**
// 	 * the data collection implementation maintained.
// 	 * @serial
// 	 */
// 	private Iterator nodes;
// 	public OptionIterator(GeneralSearchProblem problem) {
// 	    super(problem);
// 	    nodes = Collections.singletonList(problem.getInitialState()).iterator();
// 	}
//         protected boolean isEmpty() {
// 	    return !nodes.hasNext();
//         }
//         /**
//          * Select the node with min f(n).
// 	 * @see orbital.util.Setops#argmin
//          */
//         protected Object/*>S<*/ select() {
// 	    Comparator comparator = new EvaluationComparator(HillClimbing.this);
// 	    // aggregate minimum greedy search
// 	    Object candidate = nodes.next();
// 	    // contains all candidates that are as well as candidate
// 	    List candidates = new LinkedList();
// 	    candidates.add(candidate);
// 	    while (nodes.hasNext()) {
// 		Object next = nodes.next();
// 		int cmp = comparator.compare(next, candidate);
// 		// use better one
// 		if (cmp < 0) {
// 		    candidate = next;
// 		    candidates = new LinkedList();
// 		    candidates.add(candidate);
// 		} else if (cmp == 0)
// 		    // collect all best candidates
// 		    candidates.add(candidate);
// 		else
// 		    // forget about worse candidates
// 		    ;
// 	    }
// 	    // for multiple candidates with optimal evaluation value, select one, randomly
// 	    return candidates.get(getRandom().nextInt(candidates.size()));
//         }
//        	/**
//        	 * discard old list, using new.
//     	 */
//         protected boolean add(Iterator newNodes) {
// 	    nodes = newNodes;
// 	    return newNodes.hasNext();
//         }
//     };



    //	protected Collection createCollection() {
    //		return new LinkedList();
    //	}
    //
    //    /**
    //     * Select the node with min h(n).
    //     */
    //    protected GeneralSearchProblem.Option select(Collection nodes) {
    //    	Comparator comparator = new EvaluationComparator(this);
    //    	List _nodes = (List) nodes;
    //    	// modified minimum greedy search that keeps track of the index, additionally
    //    	ListIterator i = _nodes.listIterator();
    //    	Object candidate = i.next();
    //    	int index = i.previousIndex();
    //    	while (i.hasNext()) {
    //    	    Object next = i.next();
    //    	    if (comparator.compare(next, candidate) < 0) {
    //    			candidate = next;
    //    			index = i.previousIndex();
    //    		}
    //	    	//@todo for multiple candidates with optimal evaluation value, select one, randomly
    //    	}
    //    	
    //    	// in principle unnecessary since add will discard old list, anyway
    //    	_nodes.remove(index);
    //    	return (GeneralSearchProblem.Option) candidate;
    //    }
    //
    //   	/**
    //   	 * discard old list, returning new.
    //	 */
    //    protected Collection add(Collection newNodes, Collection oldNodes) {
    //    	return newNodes;
    //    }
}
