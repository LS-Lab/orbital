/**
 * @(#)DelegateGeneralSearchProblem.java 1.1 2002/07/13 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.MutableFunction;
import java.util.Iterator;
import java.io.Serializable;


/**
 * Delegates to a GeneralSearchProblem.
 * <p>
 * For examples, this class may be used as a base class for implementing Decorators, like
 * Decorator restricting neighbourhood by preselecting more promising
 * actions.
 * Either filter neighbours randomly,
 * or select best according to an inexpensive evaluation function,
 * or use a combined approach.
 * </p>
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @structure delegates problem:GeneralSearchProblem
 */
public abstract class DelegateGeneralSearchProblem implements GeneralSearchProblem, Serializable {
    private static final long serialVersionUID = 759071020836279592L;
    /**
     * @serialize
     */
    private GeneralSearchProblem problem;
    /**
     * Get the value of problem.
     * @return value of problem.
     */
    protected GeneralSearchProblem getDelegatee() {
	return problem;
    }
    
    /**
     * Set the value of problem.
     * @param v  Value to assign to problem.
     */
    protected void setDelegatee(GeneralSearchProblem  v) {
	this.problem = v;
    }
    
    public DelegateGeneralSearchProblem(GeneralSearchProblem problem){
	this.problem = problem;
    }

    // Code for delegation of orbital.algorithm.template.GeneralSearchProblem methods to problem

    /**
     *
     * @param param1 <description>
     * @return <description>
     * @see orbital.algorithm.template.GeneralSearchProblem#actions(Object)
     */
    public Iterator actions(Object param1)
    {
	return problem.actions(param1);
    }

    /**
     *
     * @return <description>
     * @see orbital.algorithm.template.GeneralSearchProblem#getInitialState()
     */
    public Object getInitialState()
    {
	return problem.getInitialState();
    }

    /**
     *
     * @return <description>
     * @see orbital.algorithm.template.GeneralSearchProblem#getAccumulatedCostFunction()
     */
    public MutableFunction getAccumulatedCostFunction()
    {
	return problem.getAccumulatedCostFunction();
    }

    /**
     *
     * @param param1 <description>
     * @param param2 <description>
     * @return <description>
     * @see orbital.algorithm.template.GeneralSearchProblem#states(Object, Object)
     */
    public Iterator states(Object param1, Object param2)
    {
	return problem.states(param1, param2);
    }

    /**
     *
     * @param param1 <description>
     * @param param2 <description>
     * @param param3 <description>
     * @return <description>
     * @see orbital.algorithm.template.GeneralSearchProblem#transition(Object, Object, Object)
     */
    public TransitionModel.Transition transition(Object param1, Object param2, Object param3)
    {
	return problem.transition(param1, param2, param3);
    }
    // Code for delegation of orbital.algorithm.template.MarkovDecisionProblem methods to problem

    /**
     *
     * @param param1 <description>
     * @return <description>
     * @see orbital.algorithm.template.MarkovDecisionProblem#isSolution(Object)
     */
    public boolean isSolution(Object param1)
    {
	return problem.isSolution(param1);
    }
}// DelegateGeneralSearchProblem
