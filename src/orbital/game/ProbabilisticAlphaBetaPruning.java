/**
 * @(#)ProbabilisticAlphaBetaPruning..java 1.0 2001/08/22 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.algorithm.template.ProbabilisticAlgorithm;
import orbital.logic.functor.Function;
import java.util.Random;

/**
 * ProbabilisticAlphaBetaPruning class randomly choosing moves slightly worse than the best.
 * <p>
 * This variant of &alpha;-&beta;-pruning is of advantage if you need a slight exploration
 * of the search space. Although desirable, a more goal-oriented exploration would
 * exhaust extra memory because state visiting statistics would need to be stored.</p>
 *
 * @version 1.0, 2001/08/22
 * @author  Andr&eacute; Platzer
 */
public
class ProbabilisticAlphaBetaPruning extends AlphaBetaPruning /*implements ProbabilisticAlgorithm*/ {
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, float probability) {
        this(depth, utility, probability, new Random());
    }

    /**
     * @param probability is the probability of choosing a better alternative, if there is any.
     *  Note that this does not specify the probability of choosing the best alternative.
     */
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, float probability, Random random) {
        super(depth, utility);
        this.probability = probability;
        this.random = random;
    }

	private Random random;
	private float  probability;

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
	 * Prefers better moves only with some probability.
	 */
	protected boolean isPreferred(double v, double w) {
		if (random == null)
			throw new IllegalStateException("no random generator has been set");
		// either w is all too bad (since it is the null option) or probably v is better
		return w == Double.NEGATIVE_INFINITY || (v > w && random.nextFloat() <= probability);
	}
}