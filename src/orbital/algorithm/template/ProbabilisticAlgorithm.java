/**
 * @(#)ProbabilisticAlgorithm.java 1.0 2001/02/19 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import java.util.Random;

/**
 * Tags probabilistic algorithms leading to approximative solutions.
 * <p>
 * <dl class="def">
 *   <dt>Monte Carlo</dt>
 *   <dd>"Monte Carlo" algorithms are mostly correct in the sense that they
 *   approximate the solution using random choices that should usually converge.
 *   When we say that "Monte Carlo" algorithms are <em>mostly</em> correct,
 *   we should remember that "mostly" is a euphemism for "not".
 *   </dd>
 *   <dt>Las Vegas</dt>
 *   <dd>"Las Vegas" algorithms do repetitive randomization until
 *   a solution is verified to be found. (Somehow corresponding to what NP algorithms are to P).
 *   In fact, Las Vegas algorithms need not even terminate, and have O(&infin;).
 *   </dd>
 * </dl>
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see #isCorrect()
 */
public interface ProbabilisticAlgorithm {
    /**
     * Whether this algorithm is correct.
     * @return whether all solutions found by this algorithms are correct despite the approximative
     *  nature of this algorithm.
     *  Monte Carlo algorithms are <em>not</em> correct,
     *  while Las Vegas algorithms usually are.
     * @preconditions true
     * @postconditions RES == OLD(RES)
     */
    boolean isCorrect();
        
    /**
     * Get the random-generator used as probabilistic random source.
     * @return the random generator used for producing probabilistic effects.
     */
    Random getRandom();

    /**
     * Set the random-generator to use as probabilistic random source.
     * <p>
     * Especially called to change the random source to a previous, more secure, or more realistic
     * generator.</p>
     * @param randomGenarator the random generator to use for producing probabilistic effects.
     */
    void setRandom(Random randomGenerator);
}
