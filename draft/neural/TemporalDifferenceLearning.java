/**
 * @(#)TemporalDifferenceLearning.java 0.9 2001/06/18 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import java.io.Serializable;
import orbital.logic.functor.MutableFunction;

import orbital.math.Vector;
import orbital.math.Values;

import orbital.util.Utility;

/**
 * Temporal-difference (TD) learning.
 * <p>
 * <var>Temporal-difference learning requires a Markov Decision Problem, though it does not even
 * use all its methods.</var></p>
 *
 * @version 0.9, 2001/06/18
 * @author  Andr&eacute; Platzer
 * @internal too optimized
 */
public
class TemporalDifferenceLearning implements Serializable {
	private static final long serialVersionUID = -5821136891764886004L;
	/**
	 * the learning rate &lambda; which is a step-size parameter.
	 * @serial
	 * @xxx redundant for neural networks which themselves already have a learning rate. The product of these values count.
	 */
	private double learningRate = 1;
	/**
	 * the current discount factor &gamma;.
	 * @serial
	 */
	private double discount = 1;

	private MutableFunction U;

    public TemporalDifferenceLearning(MutableFunction U) {
    	this.U = U;
    }
    
	/**
	 * Get the learning rate &alpha;.
	 * @return &alpha;
	 */
	public double getLearningRate() {
		return learningRate;
	}

	/**
	 * Set the learning rate &alpha;.
	 * @param alpha the learning rate step-size to apply, usually a value in (0,1].
	 *  A value of 0 disables learning.
	 */
	public void setLearningRate(double alpha) {
		this.learningRate = alpha;
	}
    
    /**
     * Set the discount factor &gamma;.
     * @param gamma The discount factor &gamma; describes, how much immediate results are
     *  preferred over future results.
     *  The higher the factor, the more balanced preference, the lower the factor, the more
     *  preference is taken for immediate results.
     *  For &gamma;=0, immediate costs are considered, only.
     *  For &gamma;=1, the undiscounted case, additional assumptions are required to produce
     *  a well-defined decision problem and ensure convergence.
     * @pre gamma&isin;[0,1]
     */
    public void setDiscount(double gamma) {
    	Utility.pre(0 <= gamma && gamma <= 1, "discount " + gamma + " isin [0,1]");
    	this.discount = gamma;
    }
    /**
     * Get the discount factor &gamma;.
     * @post RES&isin;[0,1]
     */
    public double getDiscount() {
    	return discount;
    }
    
    //
	
	/**
	 * performs one TD(0) update.
	 * <p>
	 * <center>U(s) := U(s) + &alpha;*(r + &gamma;U(s') - U(s))</center>
	 * </p>
	 * @param sp the new state s'&isin;S reached after the transition from s to s'.
	 * @param state the last state s&isin;S.
	 * @param reward the reward received for the transition from s to s'.
	 */
	protected void update(Object sp, Object state, double reward) {
		// Note that this order is important for optimized {@link Backpropagation#learn(Vector)} to work. Calling any U(t) after U(s) would hurt the precondition of {@link Backpropagation#learn(Vector)}.
		double usp = valueOf(sp);
		// TD(0) update
		// optimized version of U.set(state, /*Vector.valueOf*/ new java.lang.Double(us + getLearningRate() * (reward + getDiscount() * usp - us)));
		update(usp, state, reward);
	}

	/**
	 * performs one TD(0) update.
	 * <p>
	 * Implementation method for optimization reasons.</p>
	 * @param usp the utility U(s') of the new state s'&isin;S reached after the transition from s to s'.
	 * @param state the last state s&isin;S.
	 * @param reward the reward received for the transition from s to s'.
	 */
	void update(double usp, Object state, double reward) {
		double us = valueOf(state);
		// TD(0) update
		// optimized version of U.set(state, /*Vector.valueOf*/ new java.lang.Double(us + getLearningRate() * (reward + getDiscount() * usp - us)));

		// optimized call to {@link #learn(Vector, Vector) learn}(input, expectedOutput)
		// compute the error
		Vector error = Values.getDefault().valueOf(new double[] {getLearningRate() * (reward + getDiscount() * usp - us)});
		((Backpropagation) U).learn(error);
	}
	
	private double valueOf(Object state) {
		Object u = U.apply(state);
		if (u instanceof Vector) {
			Vector v = (Vector) u;
			assert v.dimension() == 1 : "we currently require scalar utility values";
			u = v.get(0);
		}
		return ((Number) u).doubleValue();
	}
}
