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
 * ({@link #LOCAL_BEST_IMPROVEMENT One variant} of hill-climbing)
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
     * The local selection mechanism used to evaluate states.
     * Determines the exact type of hill-climbing specifying which exact variant is used.
     * @version 1.1, 2002/06/04
     * @author  Andr&eacute; Platzer
     * @see <a href="{@docRoot}/DesignPatterns/enum.html">typesafe enum pattern</a>
     * @internal typesafe enumeration pattern class currently specifies whole OptionIterator
     * @invariant a.equals(b) &hArr; a==b
     * @todo turn into a Decorator of GeneralSearchProblem instead
     */
    public static abstract class LocalSelection {
	/**
	 * the name to display for this enum value
	 * @serial
	 */
	private final String	  name;

	/**
	 * Ordinal of next enum value to be created
	 */
	private static int	  nextOrdinal = 0;

	/**
	 * Table of all canonical references to enum value classes.
	 */
	private static LocalSelection[] values = new LocalSelection[3];

	/**
	 * Assign an ordinal to this enum value
	 * @serial
	 */
	private final int		  ordinal = nextOrdinal++;

	LocalSelection(String name) {
	    this.name = name;
	    values[nextOrdinal - 1] = this;
	}
	/**
	 * Maintains the guarantee that all equal objects of the enumerated type are also identical.
	 * @post a.equals(b) &hArr; if a==b.
	 */
	public final boolean equals(Object that) {
	    return super.equals(that);
	} 
	public final int hashCode() {
	    return super.hashCode();
	} 

	/**
	 * Maintains the guarantee that there is only a single object representing each enum constant.
	 * @serialData canonicalized deserialization
	 */
	private Object readResolve() throws java.io.ObjectStreamException {
	    // canonicalize
	    return values[ordinal];
	}

	public String toString() {
	    return this.name;
	}

	
	abstract Iterator createTraversal(GeneralSearchProblem problem, HillClimbing algorithm);
    }

    // enumeration of LocalSelections
    
    /**
     * accept the best improvement (default).
     * Although we have a local convergence criterion then, that
     * variant is no good for very high branching factors (or
     * expensive expansions).
     * @see BestFirstSearch
     * @todo why do some authors think that hill climbing should not forget about other alternatives, but remember them as depth-first search does. Will this really be another / or better algorithm, then?
     */
    public static final LocalSelection LOCAL_BEST_IMPROVEMENT = new LocalSelection("LocalBest") {
	    Iterator createTraversal(GeneralSearchProblem problem, HillClimbing algorithm) {
		return algorithm.new OptionIterator(problem);
	    }
	};
    /**
     * + accept the first (randomly chosen) improvement.  At least for
     * local derivable evaluation functions, the expected number of
     * random trials until finding an improvement is 2, anyway.
     */
    public static final LocalSelection LOCAL_FIRST_IMPROVEMENT = new LocalSelection("LocalFirst") {
	    Iterator createTraversal(GeneralSearchProblem problem, HillClimbing algorithm) {
		return new OptionIterator_First(problem, algorithm);
	    }
	};
    /**
     * -- accept the first improvement trying nodes according to a given order.
     * At least for local derivable evaluation functions, the worst
     * number of random trials until finding an improvement is b/2,
     * and may lead to the worst possible improvement.
     * (Not implemented).
     * <p>
     * Returning the options in the right order is the responsibility of
     * {@link GeneralSearchProblem#actions(Object)}.
     * </p>
     */
    static final LocalSelection LOCAL_ORDERED_IMPROVEMENT = null;
    /**
     * The exact type of hill-climbing specifying which exact variant is used.
     * @serial
     */
    private LocalSelection type;
    /**
     * The applied heuristic cost function h:S&rarr;<b>R</b> embedded in the evaluation function f(n) = h(n).
     * @serial
     */
    private Function heuristic;
    /**
     * Create a new instance of hill climbing search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @param acceptionType the exact type of hill-climbing specifying which exact variant is used.
     * @see #LOCAL_BEST_IMPROVEMENT
     * @see #LOCAL_FIRST_IMPROVEMENT
     */
    public HillClimbing(Function heuristic, LocalSelection acceptionType) {
    	this.heuristic = heuristic;
	setType(acceptionType);
    }
    /**
     * Create a new instance of hill climbing search.
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     */
    public HillClimbing(Function heuristic) {
    	this(heuristic, LOCAL_BEST_IMPROVEMENT);
    }

    private void setType(LocalSelection type) {
	this.type = type;
    }
    private LocalSelection getType() {
	return type;
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
	return getType().createTraversal(problem, this);
    }

    /**
     * An iterator over a state space in (randomized) greedy order for hill-climbing.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
     * @todo really turn this class into a inner static class whose constructor requires an EvaluativeAlgorithm that also is a ProbabilisticAlgorithm?
     */
    public class OptionIterator extends GeneralSearch.OptionIterator {
	private static final long serialVersionUID = -6802484555661425572L;
	/**
	 * the data collection implementation maintained.
	 * @serial
	 */
	private Iterator nodes;
	public OptionIterator(GeneralSearchProblem problem) {
	    super(problem);
	    nodes = Collections.singletonList(problem.getInitialState()).iterator();
	}
        protected boolean isEmpty() {
	    return !nodes.hasNext();
        }
        /**
         * Select the node with min f(n).
	 * @see PackageUtilities#min
         */
        protected Object/*>S<*/ select() {
	    Comparator comparator = new EvaluationComparator(HillClimbing.this);
	    // modified minimum greedy search that keeps track of the index, additionally
	    Object candidate = nodes.next();
	    // contains all candidates that are as well as candidate
	    List candidates = new LinkedList();
	    candidates.add(candidate);
	    while (nodes.hasNext()) {
		Object next = nodes.next();
		int cmp = comparator.compare(next, candidate);
		// use better one
		if (cmp < 0) {
		    candidate = next;
		    candidates = new LinkedList();
		    candidates.add(candidate);
		} else if (cmp == 0)
		    candidates.add(candidate);
	    }
	    // for multiple candidates with optimal evaluation value, select one, randomly
	    return candidates.get(getRandom().nextInt(candidates.size()));
        }
       	/**
       	 * discard old list, using new.
    	 */
        protected boolean add(Iterator newNodes) {
	    nodes = newNodes;
	    return newNodes.hasNext();
        }
    };

    /**
     * An iterator over a state space in (probabilistic) greedy order for hill-climbing.
     * @version 1.0, 2001/08/01
     * @author  Andr&eacute; Platzer
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
	    final ScheduledLocalOptimizerSearch algorithm = (ScheduledLocalOptimizerSearch) getAlgorithm();
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
