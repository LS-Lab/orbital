/**
 * @(#)AlgorithmicTemplate.java 1.0 2000/07/07 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.math.functional.Function;

/**
 * Base interface for algorithmic template frameworks.
 * <p>
 * An <dfn>algorithmic template</dfn> is a class that implements a problem-solving algorithm in a
 * general way, such that it is applicable in a multitude of different problems with the same
 * essential structure. It solves this whole bunch of problems by providing a maximum of methods
 * with shared behaviour, and deferring the problem-specific part into an interface.
 * Each algorithmic template class has an associated hook which is a sub-interface of
 * {@link AlgorithmicProblem} and declares the wholes to fill.
 * So this hook interface will contain the part that distinguishes two individual problems of the
 * same common structure solved by the algorithmic template class.</p>
 * <p>
 * Of course, algorithmic templates are more useful if they only require a very small
 * and almost declarative algorithmic problem hook.</p>
 * 
 * @structure aggregates problem:AlgorithmicProblem
 * @version 1.0, 2000/07/07
 * @author  Andr&eacute; Platzer
 * @see AlgorithmicProblem
 * @see <a href="{@docRoot}/Patterns/Design/Strategy.html">&asymp;Strategy</a>
 */
public interface AlgorithmicTemplate/*<Problem extends AlgorithmicProblem, Solution extends Object>*/ {

    /**
     * Generic solve method for a given algorithmic problem.
     * @param p algorithmic problem hook class which must fit the concrete
     * algorithmic template framework implementation.
     * @return the solution to the problem p, or null if solving failed.
     * @see orbital.logic.functor.Function#apply(Object)
     */
    Object/*>Solution<*/ solve(AlgorithmicProblem/*>Problem<*/ p);

    /**
     * Measure for the asymptotic time complexity of the central solution operation in O-notation.
     * @return the function f for which the solve() method of this algorithm runs in O<big>(</big>f(n)<big>)</big>
     *  assuming the algorithmic problem hook to run in O(1).
     * @pre true
     * @post RES == OLD(RES) && OLD(this) == this
     * @see #solve(AlgorithmicProblem)
     */
    Function complexity();

    /**
     * Measure for the asymptotic space complexity of the central solution operation in O-notation.
     * @return the function f for which the solve() method of this algorithm consumes memory with an amount in O<big>(</big>f(n)<big>)</big>
     *  assuming the algorithmic problem hook uses space in O(1).
     * @pre true
     * @post RES == OLD(RES) && OLD(this) == this
     * @see #solve(AlgorithmicProblem)
     */
    Function spaceComplexity();
}
