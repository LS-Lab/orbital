/**
 * @(#)TransitionPath.java 1.1 2002/05/31 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;
import orbital.algorithm.template.TransitionModel.Option;

import java.util.Iterator;
import java.util.Random;
import java.io.Serializable;
import orbital.algorithm.template.ProbabilisticAlgorithm;

/**
 * A (randomized) path through state space S by transition &tau; of a TransitionModel.
 * <p>
 * This iterator will randomly follow a path from the given initial state under the given
 * actions according to the probabilities of the transition &tau;.
 * </p>
 *
 * @author Andr&eacute; Platzer
 * @version 0.9, 2002/05/31
 */

public class TransitionPath implements Iterator, ProbabilisticAlgorithm, Serializable {
    private static final long serialVersionUID = 7547930705530088169L;
    private final TransitionModel/*<A,S,O>*/ transition;
    private final Iterator/*<A>*/ actions;
    private Object/*>S<*/ state;
    private Random random;
    /**
     * Create a new (randomized) path through a transition model.
     * @param transition the transition model &tau; through which to create a path.
     * @param actions the iterator of the actions to perform.
     * @param initialState the initial state s<sub>0</sub>&isin;S from which to start the path.
     */
    public TransitionPath(TransitionModel/*<A,S,O>*/ transition, Iterator/*<A>*/ actions, Object/*>S<*/ initialState) {
	this.transition = transition;
	this.actions = actions;
	this.state = initialState;
	this.random = new Random();
    }

    // implementation of orbital.algorithm.template.ProbabilisticAlgorithm interface

    public boolean isCorrect()
    {
	return true;
    }

    public Random getRandom()
    {
	return random;
    }

    public void setRandom(Random randomGenerator)
    {
	this.random = randomGenerator;
    }

    
    // implementation of java.util.Iterator interface

    /**
     * Advance to the next state of the path.
     * <p>
     * If a&isin;A(s) is the next action of the actions iterator
     * and s&isin;S is the current state,
     * this method will perform the state transition s&rarr;<sub>a</sub>s&#697; with
     * probability &tau;(a)(s,s&#697;).
     * </p>
     * @return the next state of the path.
     */
    public Object next() {
	final Object action = actions.next();
	final double r = random.nextDouble();
	double r_sum = 0;
	for (Iterator t = transition.transitions(state, action); t.hasNext(); ) {
	    Option p = (Option) t.next();
	    r_sum += p.getProbability();
	    assert 0 <= r_sum && r_sum <= 1+0.0001 : "probability distribution";
	    if (r >= r_sum)
		return state = p.getState();
	}
	throw new AssertionError("@post TransitionModel.transitions(Object,Object): probabilities sum up to 1.0");
    }

    /**
     * @post RES == actions.hasNext()
     */
    public boolean hasNext() {
	return actions.hasNext();
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }

}// TransitionModelPath
