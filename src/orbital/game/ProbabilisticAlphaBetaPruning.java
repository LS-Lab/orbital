/**
 * @(#)ProbabilisticAlphaBetaPruning..java 1.0 2001/08/22 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.algorithm.template.ProbabilisticAlgorithm;
import orbital.logic.functor.Function;
import java.util.Random;
import java.util.Iterator;

import java.util.List;
import java.util.Collections;
import orbital.util.Setops;
import orbital.util.Utility;
import orbital.math.MathUtilities;

import java.util.logging.Level;

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
public class ProbabilisticAlphaBetaPruning extends AlphaBetaPruning /*implements ProbabilisticAlgorithm*/ {
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, boolean randomizeSuccessors, float improveProbability, float fluctuateProbability, double fluctuateThreshold) {
        this(depth, utility, randomizeSuccessors, improveProbability, fluctuateProbability, fluctuateThreshold, new Random());
    }
    /**
     * @param randomizeSuccessors whether to locally shuffle the successors state order randomly.
     * @param improveProbability is the probability of accepting a better
     *  alternative, when there is one. Note that - due to the iterative
     * nature of the algorithm - this does not specify the probability
     * of choosing the best alternative.
     * @param fluctuateProbability is the probability of falling back to an alike
     *  (or slightly worse) solution, when there is one.
     * @param fluctuateThreshold gives the maximum amount that a
     * solution may be lower than the current best choice, for still
     * counting as an alike (or slightly worse) alternative.
     */
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, boolean randomizeSuccessors, float improveProbability, float fluctuateProbability, double fluctuateThreshold, Random random) {
        super(depth, utility);
	this.randomizeSuccessors = randomizeSuccessors;
	Utility.pre(MathUtilities.isProbability(improveProbability), "improveProbability " + improveProbability + " is a probability");
        this.improveProbability = improveProbability;
	Utility.pre(MathUtilities.isProbability(fluctuateProbability), "fluctuateProbability " + fluctuateProbability + " is a probability");
        this.fluctuateProbability = fluctuateProbability;
	Utility.pre(fluctuateThreshold <= 0, "threshold =< 0 expected for fluctuation. The > 0 cases are subject to improveProbability");
        this.fluctuateThreshold = fluctuateThreshold;
        this.random = random;
	Utility.pre((fluctuateThreshold == 0.0) == (fluctuateProbability == 0.0), "no fluctuation probability (means =0) if and only if no fluctuation threshold (means =0)");
    }

    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, float improveProbability) {
	this(depth, utility, false, improveProbability, 0, 0);
    }
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, float improveProbability, Random random) {
	this(depth, utility, false, improveProbability, 0, 0, random);
    }

    private Random random;
    private boolean randomizeSuccessors;
    private float  improveProbability;
    private float  fluctuateProbability;
    private double fluctuateThreshold;

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
     * Randomized local exploration order.
     */
    protected Iterator successors(Field state) {
        Iterator s = state.expand();
	if (!randomizeSuccessors)
	    return s;
	else {
	    List s2 = Setops.asList(s);
	    Collections.shuffle(s2, getRandom());
	    return s2.iterator();
	}
    }

    /**
     * Prefers better moves only with some probability.
     * Also backs up to slightly worse moves with some probability.
     */
    protected boolean isPreferred(double v, double w) {
	if (random == null)
	    throw new IllegalStateException("no random generator has been set");
	// either w is all too bad (since it is the null option),
	float rnd;
	if (w == Double.NEGATIVE_INFINITY)
	    logger.log(Level.FINEST, "isPreferred: {0} preferred to {1} because {1}=-inf", new Object[] {format(v), format(w)});
	else if(v > w && (rnd = random.nextFloat()) <= improveProbability)
	    // or v is better and we probably want to improve our choice
	    logger.log(Level.FINEST, "isPreferred: {0} preferred to {1} because {0}>{1} and improve randomly because of {2}=<{3}", new Object[] {format(v), format(w), format(rnd), format(improveProbability)});
	else if (MathUtilities.equals(v, w, -fluctuateThreshold) && (rnd =random.nextFloat()) <= fluctuateProbability)
	    // or v is alike and we probably want to "schwanken" and fall back to another choice
	    logger.log(Level.FINEST, "isPreferred: {0} preferred to {1} because {0}~=~{1} (by threshold {4})and fluctuate randomly because of {2}=<{3}", new Object[] {format(v), format(w), format(rnd), format(fluctuateProbability), format(fluctuateThreshold)});
	else
	    return false;
	return true;

	//@internal original version without logging
// 	// either w is all too bad (since it is the null option),
// 	return w == Double.NEGATIVE_INFINITY
// 	    // or v is better and we probably want to improve our choice
// 	    || (v > w && random.nextFloat() <= improveProbability)
// 	    // or v is alike and we probably want to "schwanken" and fall back to another choice
// 	    || (MathUtilities.equals(v, w, -fluctuateThreshold) && random.nextFloat() <= fluctuateProbability);
    }
}
