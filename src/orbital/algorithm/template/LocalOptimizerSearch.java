/**
 * @(#)LocalOptimizerSearch.java 1.1 2002/06/01 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Iterator;
import java.util.Collection;
import java.util.Random;

/**
 * @version 1.1, 2002/06/01
 * @author  Andr&eacute; Platzer
 */
class LocalOptimizerSearch extends GeneralSearch implements ProbabilisticAlgorithm {
    /**
     * The random generator source.
     * @serial the random source is serialized to let the seed persist.
     */
    private Random random;

    public boolean isCorrect() {
	return false;
    }

    public Random getRandom() {
	return random;
    }

    public void setRandom(Random randomGenerator) {
	this.random = randomGenerator;
    }

    /**
     * An iterator over a state space in "choosy" random order.
     * <p>
     * It will pick a random move, but only accept some transitions.
     * </p>
     * @version 1.1, 2002/06/01
     * @author  Andr&eacute; Platzer
     * @see TransitionPath
     * @internal optimized version of a TransitionPath sandwhiched with a TransitionModel and an action iterator.
     */
    public static class OptionIterator implements Iterator {
	/**
	 * The probabilistic algorithm using this random order iterator.
	 */
	private final ProbabilisticAlgorithm probabilisticAlgorithm;
	/**
	 * The current state s&isin;S of this transition path.
	 * @serial
	 */
	private Object/*>S<*/ state;
	/**
	 * The predicate asked whether to accept a transition.
	 * <div>s&rarr;<sub>a</sub>s&#697; is accepted iff accept(s,s&#697;)</div>
	 * @todo remove and replace by an abstract method accept() in this class?
	 */
	private final BinaryPredicate accept;
	/**
	 * The predicate asked whether to continue or stop further transition.
	 * <div>transitions are continued further iff cont(s)</div>
	 * where s is the current state.
	 * @todo remove and replace by an abstract method hasNext() in this class?
	 */
	private final Predicate cont;
	public OptionIterator(GeneralSearchProblem problem, ProbabilisticAlgorithm probabilisticAlgorithm, BinaryPredicate accept, Predicate cont) {
	    super(problem);
	    this.state = new GeneralSearchProblem.Option(getProblem().getInitialState());
	    this.probabilisticAlgorithm = probabilisticAlgorithm;
	    this.accept = accept;
	    this.cont = cont;
	}

	public Object next() {
	    //@internal this has a horrible performance if constructing states is an expensive operation, and the technique of lazy state construction is not applied. @todo Could perhaps solve by transforming GSP into a TransitionModel with separated actions() and transitions() with the latter constructing the state.
	    List nodes = Setops.asList(problem.expand(state));
	    if (nodes.isEmpty())
		throw new NoSuchElementException("no transitions from " + state);
	    Option sp =  (GeneralSearchProblem.Option)
		nodes.get(probabilisticAlgorithm.getRandom().nextInt(nodes.size()));

	    if (accept.apply(state, sp)) {
		// accept the transition current->sp
		state = sp;
	    }

	    return state;
	}

	/**
	 * Decides whether to stop further transitions.
	 */
	public boolean hasNext() {
	    return cont.apply(state);
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    };
}
