/**
 * @(#)LocalOptimizerSearch.java 1.1 2002/06/01 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;
import orbital.algorithm.template.GeneralSearchProblem.Transition;

import java.util.Iterator;
import java.io.Serializable;

import orbital.logic.functor.MutableFunction;

import java.util.List;
import java.util.Random;

import java.util.NoSuchElementException;
import orbital.util.Setops;
import orbital.math.Real;
import orbital.math.Values;

/**
 * General search scheme for local optimizing search.
 * <p>
 * Local optimizers will try state transitions and accept only improvements, or states that
 * promise to lead to improvements. In order to achieve that, most of these local optimizers
 * use some sort of randomization and a probability depending on the degree of improvement or
 * decrease.
 * In fact, most uses of local optimizers also depend more or less on a random initial state.
 * Then, some of them may profit from random-restart.
 * </p>
 * <p>
 * Subclasses usually use the {@link LocalOptimizerSearch.OptionIterator} provided
 * to implement the traversal policy. Then they only have to provide the abstract
 * methods to implement the search algorithm.</p>
 *
 * @version 1.1, 2002/06/01
 * @author  Andr&eacute; Platzer
 * @todo should we implement HeuristicAlgorithm and provide get/setHeuristic, just because most of our subclasses are?
 * @see Greedy
 */
public abstract class LocalOptimizerSearch extends GeneralSearch implements ProbabilisticAlgorithm, EvaluativeAlgorithm {
    private static final long serialVersionUID = 465553782601369843L;
    /**
     * The random generator source.
     * @serial the random source is serialized to let the seed persist.
     */
    private Random random;
    /**
     * The local selection variant used.
     * @serial
     */
    private LocalSelection localSelection;

    /**
     * @param random the random generator source.
     * @param localSelection the variant of local selection used.
     * @see #BEST_LOCAL_SELECTION
     * @see #FIRST_LOCAL_SELECTION
     */
    public LocalOptimizerSearch(Random random, LocalSelection localSelection) {
	this.random = random;
	this.localSelection = localSelection;
    }
    public LocalOptimizerSearch(LocalSelection localSelection) {
	this(new Random(), localSelection);
    }
    //@todo remove
    LocalOptimizerSearch() {
	this(new Random(), FIRST_LOCAL_SELECTION);
    }

    private void setLocalSelection(LocalSelection type) {
	this.localSelection = type;
    }
    LocalSelection getLocalSelection() {
	return localSelection;
    }

    public Random getRandom() {
	return random;
    }

    public void setRandom(Random randomGenerator) {
	this.random = randomGenerator;
    }

    /**
     * A slight modification of {@link GeneralSearch#search(Iterator)}.
     * This method will finally return the most optimal node found, regardless of whether
     * it is a solution, or not.
     * This behaviour is especially useful if the isSolution test is omitted by categorically
     * returning <span class="keyword">true</span> there.
     * @post &not;super
     */
    protected Object/*>S<*/ search(Iterator/*<S>*/ nodes) {
	Object/*>S<*/ node = null;
	while (nodes.hasNext()) {
	    node = nodes.next();

	    if (getProblem().isSolution(node))
		return node;
    	}

    	// current choice instead of failing
    	return node;
    }

    /**
     * The local selection mechanism used to evaluate states.
     * Determines which transitions may take part in attempts of acception.
     * @version 1.1, 2002/06/04
     * @author  Andr&eacute; Platzer
     * @see <a href="{@docRoot}/Patterns/Design/enum.html">typesafe enum pattern</a>
     * @internal typesafe enumeration pattern class currently specifies whole OptionIterator
     * @invariant a.equals(b) &hArr; a==b
     * @todo turn into a Decorator of GeneralSearchProblem instead
     * @todo check naming since this class does not fully determine local selection, but only local restriction (so without fixing the acception rule which is different in diverse subclasses).
     * @see LocalOptimizerSearch#BEST_LOCAL_SELECTION
     * @see LocalOptimizerSearch#FIRST_LOCAL_SELECTION
     */
    public static abstract class LocalSelection implements Serializable {
	private static final long serialVersionUID = 1471057172168155681L;
	/**
	 * the name to display for this enum value
	 * @serial
	 */
	private final String name;

	/**
	 * Ordinal of next enum value to be created
	 */
	private static int nextOrdinal = 0;

	/**
	 * Table of all canonical references to enum value classes.
	 */
	private static LocalSelection[] values = new LocalSelection[3];

	/**
	 * Assign an ordinal to this enum value
	 * @serial
	 */
	private final int ordinal = nextOrdinal++;

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

	
	/**
	 * Restrict the problem locally by decorating it.
	 * @see PackageUtilities#restrictRandomly(GeneralSearchProblem,int,ProbabilisticAlgorithm)
	 * @see PackageUtilities#restrictBest(GeneralSearchProblem,Function)
	 */
	abstract GeneralSearchProblem createLocalRestriction(GeneralSearchProblem problem, LocalOptimizerSearch algorithm);
    }

    // enumeration of LocalSelections
    
    /**
     * attempt the best local transition (default).
     * Although we have a local convergence criterion then, that
     * variant is no good for very high branching factors (or
     * expensive expansions).
     * @see BestFirstSearch
     * @todo (for hill-climbing only) terminate search on local optimum in case of BEST_LOCAL_SELECTION.
     * terminate search if after an attempted transition s&rarr;s' it is f(s')>f(s) (local minimum convergence criterium), alias (due to acceptStep) if new state == old state.
     * @todo why do some authors think that hill climbing should not forget about other alternatives, but remember them as depth-first search does. Will this really be another / or better algorithm, then?
     */
    public static final LocalSelection BEST_LOCAL_SELECTION = new LocalSelection("LocalBest") {
	    private static final long serialVersionUID = 1233346780667611822L;
	    GeneralSearchProblem createLocalRestriction(GeneralSearchProblem problem, LocalOptimizerSearch algorithm) {
		return PackageUtilities.restrictRandomly(PackageUtilities.restrictBest(problem, algorithm.getEvaluation()),1,algorithm);
	    }
	};
    /**
     * + attempt a randomly chosen local transition.
     * In the usual case of accepting only improvements this becomes:
     * accept the first (randomly chosen) local transition
     * At least for
     * local derivable evaluation functions, the expected number of
     * random trials until finding an improvement is 2, anyway.
     */
    public static final LocalSelection FIRST_LOCAL_SELECTION = new LocalSelection("LocalFirst") {
	    private static final long serialVersionUID = 1622132645733195173L;
	    GeneralSearchProblem createLocalRestriction(GeneralSearchProblem problem, LocalOptimizerSearch algorithm) {
		return PackageUtilities.restrictRandomly(problem,1,algorithm);
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
    private static final LocalSelection ORDERED_LOCAL_SELECTION = null;

    /**
     * An iterator over a state space in "choosy" random order.
     * <p>
     * It will pick a random move, but only accept some transitions.
     * </p>
     * @version 1.1, 2002/06/01
     * @author  Andr&eacute; Platzer
     * @see TransitionPath
     * @internal optimized version of a TransitionPath sandwhiched with a TransitionModel and an action iterator.
     * @todo introduce template method  select(...) for selection strategy?
     * @todo would we benfit from extending GeneralSearchProblem.OptionIterator?
     * @internal note that we do not strictly require knowing the algorithm (its constant getEvaluation() would suffice), but we needs its getRandom() and our descendants might need any additional stuff.
     */
    public static abstract class OptionIterator implements Iterator, Serializable {
	private static final long serialVersionUID = -658271440377589506L;
    	/**
    	 * The search problem to solve.
    	 * @serial
	 * @internal we do not use algorithm.getProblem() for performance and (perhaps) update reasons.
    	 */
    	private final GeneralSearchProblem/*<A,S>*/ problem;
    	/**
    	 * Caching the accumulated cost function of problem.
	 * @invariant g == problem.getAccumulatedCostFunction()
	 * @see #problem
    	 * @serial
	 * @todo transientize?
    	 */
	private final MutableFunction g;
	/**
	 * The algorithm using this (randomized) iterator.
	 * @serial
	 */
	private final LocalOptimizerSearch algorithm;
	/**
	 * The current state s&isin;S of this transition path.
	 * @serial
	 */
	private Object/*>S<*/ state;
	/**
	 * Caching the accumulatedCost g(state).
	 * @see #state
	 * @todo transientize
	 */
	private Real accumulatedCost;
	public OptionIterator(GeneralSearchProblem problem, LocalOptimizerSearch algorithm) {
	    this.algorithm = algorithm;
	    this.problem = problem;
	    this.g = problem.getAccumulatedCostFunction();
	    //@todo super(problem); extend GeneralSearch.OptionIterator and use its select()?
	    this.state = getProblem().getInitialState();
	    this.accumulatedCost = (Real/*__*/) g.apply(state);
	    assert accumulatedCost.doubleValue() == 0 : "@post getInitialState(): accumulatedCost==0";
	}

    	/**
    	 * Get the current problem.
    	 * @pre true
    	 * @return the problem specified in the last call to solve.
    	 */
    	protected final GeneralSearchProblem/*<A,S>*/ getProblem() {
	    return problem;
    	}

	/**
	 * Get the algorithm using this (randomized) iterator.
	 */
	protected final LocalOptimizerSearch getAlgorithm() {
	    return algorithm;
	}

	/**
	 * Get the current state s&isin;S of this transition path.
	 * i.e. the last state returned by {@link #next()}, or the initial state if no transition has
	 * already occurred.
	 */
	protected final Object/*>S<*/ getState() {
	    return state;
	}

	public Object next() {
	    final List actions = Setops.asList(problem.actions(state));
	    if (actions.isEmpty())
		//@internal note that hasNext() will not respect this case, since it is considered as an error
		//@xxx
		throw new NoSuchElementException("specification hurt? there are no transitions from " + state);
	    //@todo rely on decorators of problem (PackageUtilities.restrictRandomly) to do the randomization?
	    // either by asserting that actions.size() == 1, or by accepting in the order of actions (with descending probabilities of p1, (1-p1)p2, (1-p1)(1-p2)p3, ...).

	    final Object/*>A<*/ a = actions.get(algorithm.getRandom().nextInt(actions.size()));
	    final Object/*>S<*/ sp = problem.states(a, state).next();

	    final Real spAccumulatedCost = accumulatedCost.add(Values.getDefaultInstance().valueOf(((Transition)problem.transition(a,state,sp)).getCost()));
	    g.set(sp, spAccumulatedCost);

	    if (accept(state, sp)) {
		// accept the transition state->sp
		this.state = sp;
		this.accumulatedCost = spAccumulatedCost;
	    }

	    return state;
	}

	/**
	 * The predicate asked whether to accept a transition.
	 * <div>s&rarr;<sub>a</sub>s&#697; is accepted (and performed) iff accept(s,s&#697;)</div>
	 * @internal alternative would be a delegation to a BinaryPredicate accept.
	 */
	protected abstract boolean accept(Object/*>S<*/ state, Object/*>S<*/ sp);

	/**
	 * Decides whether to stop further transitions.
	 * The predicate asked whether to continue or stop further transition.
	 * <div>transitions are continued further iff cont(s)</div>
	 * where s is the current state.
	 * @internal alternative would be a delegation to a Predicate cont.
	 */
	public abstract boolean hasNext();

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    };
}
