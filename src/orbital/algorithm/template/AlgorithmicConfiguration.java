/**
 * @(#)AlgorithmicConfiguration.java 1.1 2002-11-13 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

/**
 * <dfn>Algorithmic configurations</dfn> provide the glue between a problem and an algorithm
 * used for solving it. They know some aspects of the problem and translate them
 * into the right choices of parameters for the specific instance of the algorithm.
 * Unlike the algorithmic problems themselves, algorithmic configurations manage
 * non-inherent parameters of the way the problem is really solved with the algorithm.
 * So they define a lot more of how to tackle the problem, instead of just declaring
 * what the problem really is. Since there may be far more than one way of solving one
 * specific problem, you may also instantiate multiple different configurations solving
 * the same problem with different algorithms and varying non-inherent parameters.
 * <p>
 * Note that the use of this interface is optional. If your problem only admits a single
 * way of solving it, and does not want to encapsulate the passing and management of
 * additional parameters in an algorithmic configuration, you can explicitly invoke the
 * algorithm instance on the problem without loss of functionality. If, however, you want
 * a convenient way of managing (and choosing from) several ways of solving the problem
 * algorithmic configurations may be precisely what you need.  
 * </p>
 *
 * @structure association class of the relation solving:AlgorithmicTemplate&times;AlgorithmicProblem
 * @structure aggregates algorithm:AlgorithmicTemplate (derived)
 * @structure aggregates problem:AlgorithmicProblem (derived)
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see AlgorithmicProblem
 * @see AlgorithmicTemplate
 * @todo rename Configuration to ...?
 */
public interface AlgorithmicConfiguration/*<Problem extends AlgorithmicProblem, Solution extends Object>*/ {
    /**
     * Gets the specific problem to solve.
     */
    AlgorithmicProblem/*>Problem<*/ getProblem();
    
    /**
     * Gets the algorithm used for solving the problem.
     * @return the algorithm, with our additional parameters already adjusted.
     * @postconditions RES&ne;OLD(RES) &or; RES is stateless
     */
    AlgorithmicTemplate/*<Problem,Solution>*/ getAlgorithm();

    /**
     * Solves the problem with the algorithm with regard to the additional parameters
     * managed by this algorithmic configuration.
     * @return the solution to the problem {@link #getProblem()} by using the algorithm
     *  {@link #getAlgorithm()} after adjusting our additional parameters.
     * @postconditions usually RES = getAlgorithm().solve(getProblem())
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @todo remove and say our clients can do getAlgorithm().solve(getProblem()) himself?
     */
    Object/*>Solution<*/ solve();
      
}// AlgorithmicConfiguration
