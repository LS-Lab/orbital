/**
 * @(#)ProbabilisticAlphaBetaPruning..java 1.0 2001/08/22 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.algorithm.template.ProbabilisticAlgorithm;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryPredicate;
import java.util.Random;
import java.util.Iterator;
import java.io.Serializable;

import java.util.List;
import java.util.Collections;
import orbital.util.Setops;
import orbital.util.Utility;
import orbital.math.MathUtilities;

import java.util.logging.Level;

/**
 * ProbabilisticAlphaBetaPruning class randomizing evaluation order of moves.
 * <p>
 * This variant of &alpha;-&beta;-pruning is of advantage if you need a slight exploration
 * of the search space. Although desirable, a more goal-oriented exploration would
 * exhaust extra memory because state visiting statistics would need to be stored.</p>
 *
 * @version 1.0, 2001/08/22
 * @author  Andr&eacute; Platzer
 */
public class ProbabilisticAlphaBetaPruning extends AlphaBetaPruning /*implements ProbabilisticAlgorithm*/ {
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, BinaryPredicate preference, boolean randomizeSuccessors, Random random) {
        super(depth, utility, preference);
	this.randomizeSuccessors = randomizeSuccessors;
        this.random = random;
    }
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
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience constructor</a>
     */
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, boolean randomizeSuccessors, float improveProbability, float fluctuateProbability, double fluctuateThreshold, Random random) {
        this(depth, utility,
	     new ProbabilisticPreference(improveProbability, fluctuateProbability, fluctuateThreshold, random),
	     randomizeSuccessors, random);
    }

    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, float improveProbability) {
	this(depth, utility, false, improveProbability, 0, 0);
    }
    public ProbabilisticAlphaBetaPruning(int depth, Function/*<Object, Number>*/ utility, float improveProbability, Random random) {
	this(depth, utility, false, improveProbability, 0, 0, random);
    }

    private Random random;
    private boolean randomizeSuccessors;

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
	if (randomizeSuccessors) {
	    List s2 = Setops.asList(s);
	    Collections.shuffle(s2, getRandom());
	    return s2.iterator();
	} else {
	    return s;
	}
    }

    /**
     * Randomly preferring moves slightly worse than the best.  This
     * implementation achieves this by preferring better moves only
     * with some probability.  Further, it also backs up again to
     * slightly worse moves with some other probability.
     * @version 1.1, 2003-01-20
     * @author  Andr&eacute; Platzer
     * @see ProbabilisticAlgorithm
     * @todo could use Boltzmann distribution for acception of fluctuation to worse choices.
     */
    public static class ProbabilisticPreference implements BinaryPredicate, Serializable {
	private static final long serialVersionUID = -7441340781248583962L;
	private float  improveProbability;
	private float  fluctuateProbability;
	private double fluctuateThreshold;

	private Random random;

	/**
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
	public ProbabilisticPreference(float improveProbability, float fluctuateProbability, double fluctuateThreshold, Random random) {
	    setImproveProbability(improveProbability);
	    setFluctuateProbability(fluctuateProbability);
	    setFluctuateThreshold(fluctuateThreshold);
	    setRandom(random);
	    Utility.pre((fluctuateThreshold == 0.0) == (fluctuateProbability == 0.0), "no fluctuation probability (means =0) if and only if no fluctuation threshold (means =0)");
	}

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
	 * The probability of accepting a better
	 *  alternative, when there is one. Note that - due to the iterative
	 * nature of the algorithm - this does not specify the probability
	 * of choosing the best alternative.
	 */
	public float getImproveProbability() {
	    return this.improveProbability;
	}

	public void setImproveProbability(float argImproveProbability){
	    Utility.pre(MathUtilities.isProbability(argImproveProbability), "improveProbability " + argImproveProbability + " is a probability");
	    this.improveProbability = argImproveProbability;
	}

	/**
	 * The probability of falling back to an alike
	 *  (or slightly worse) solution, when there is one.
	 */
	public float getFluctuateProbability() {
	    return this.fluctuateProbability;
	}

	public void setFluctuateProbability(float argFluctuateProbability){
	    Utility.pre(MathUtilities.isProbability(argFluctuateProbability), "fluctuateProbability " + argFluctuateProbability + " is a probability");
	    this.fluctuateProbability = argFluctuateProbability;
	}

	/**
	 * The maximum amount that a
	 * solution may be lower than the current best choice, for still
	 * counting as an alike (or slightly worse) alternative.
	 */
	public double getFluctuateThreshold() {
	    return this.fluctuateThreshold;
	}

	public void setFluctuateThreshold(double argFluctuateThreshold){
	    Utility.pre(argFluctuateThreshold <= 0, "threshold " + argFluctuateThreshold + " =< 0 expected for fluctuation. The > 0 cases are subject to improveProbability");
	    Utility.pre((argFluctuateThreshold != 0.0) || (getFluctuateProbability() == 0.0), "no fluctuation probability (means =0) if and only if no fluctuation threshold (means =0)");
	    this.fluctuateThreshold = argFluctuateThreshold;
	}



	public boolean apply(Object vo, Object wo) {
	    double v = ((Option)vo).getUtility();
	    double w = ((Option)wo).getUtility();
	    if (random == null)
		throw new IllegalStateException("no random generator has been set");
	    // either w is all too bad (since it is the null option),
	    float rnd;
	    if (w == Double.NEGATIVE_INFINITY)
		logger.log(Level.FINEST, "isPreferred: {0} preferred to {1} because {1}=-inf", new Object[] {format(v), format(w)});
	    else if(v > w && (rnd = random.nextFloat()) <= getImproveProbability())
		// or v is better and we probably want to improve our choice
		logger.log(Level.FINEST, "isPreferred: {0} preferred to {1} because {0}>{1} and improve randomly because of {2}=<{3}", new Object[] {format(v), format(w), format(rnd), format(improveProbability)});
	    else if (MathUtilities.equals(v, w, -getFluctuateThreshold()) && (rnd =random.nextFloat()) <= getFluctuateProbability())
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

	public String toString() {
	    return getClass().getName() + '[' + "improve=" + improveProbability
		+ ",fluctuate=" + fluctuateProbability + " with threshold=" + fluctuateThreshold
		+ ']';
	}
    };
}
