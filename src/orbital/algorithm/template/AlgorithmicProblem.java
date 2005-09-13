/**
 * @(#)AlgorithmicProblem.java 1.0 2000/07/07 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

/**
 * AlgorithmicProblem interface used for tagging hook class interfaces for algorithmic templates.
 * <p>
 * An <dfn>algorithmic problem interface</dfn> is the hook declaring the problem-specific properties
 * for the algorithmic template class that solves the general problem.
 * After implementing such an algorithmic problem interface, it can simply be plugged into the
 * corresponding algorithmic template class to start solving the problem.
 * So algorithmic problem interfaces contain the inherent properties describing the specific
 * problem at hand. Thereby it specifies <em>what</em> the problem really is,
 * but usually not (or not much of) <em>how</em> the problem should be solved.
 * </p>
 * <p>
 * Apart from the relief from implementing the algorithm itself, using algorithmic
 * templates often has the powerful advantage that several different
 * algorithms can be interchanged freely without the need to change more than
 * just the single constructor call to the concrete implementation. Once a problem has
 * been modeled accordingly by implementing its hook class, concrete solution
 * algorithms can even be interchanged at runtime.</p>
 * 
 * @structure depends AlgorithmicTemplate
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see AlgorithmicTemplate
 * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
 */
public abstract interface AlgorithmicProblem {

    /**
     * measure for the complexity of the central operations in O-notation.
     * @return the function f for which the central methods of this problem run in O<big>(</big>f(n)<big>)</big>.
     */

    // TODO: introduce
    // Function complexity();
} 
