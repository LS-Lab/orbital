/**
 * @(#)ScheduledLocalOptimizerSearch.java 1.1 2002/06/02 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;

import orbital.math.Integer;
import orbital.math.Real;

/**
 * A local optimizing search that uses scheduling.
 * <p>
 * Provides some get/set methods for managing scheduling functions that control a parameter
 * for search (usually for controlling transition acceptance probability).
 * That parameter is sometimes called "temperature".
 * </p>
 * @version 1.1, 2002/06/02
 * @author  Andr&eacute; Platzer
 */
abstract class ScheduledLocalOptimizerSearch extends LocalOptimizerSearch implements HeuristicAlgorithm {
    private static final long serialVersionUID = -8843368383007953329L;
    /**
     * The heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @serial
     */
    private Function/*<GeneralSearchProblem.Option, Arithmetic>*/ heuristic;
    /**
     * A mapping <b>N</b>&rarr;<b>R</b> from time to "temperature"
     * controlling the cooling, and thus the probability of downward steps.
     * @serial
     */
    private Function/*<Integer, Real>*/ schedule;
    /**
     * Create a new instance of a local optimizer search with a scheduling function.
     * <p>
     * <table border="1" width="100%">
     *   <caption>common temperature scheduling functions (Braun, H.)</caption>
     *   <tr>
     *     <th>scheduling</th>
     *     <th>function T</th>
     *     <th>recurrence</th>
     *   </tr>
     *   <tr>
     *     <td>logarithmic</td>
     *     <td>1 / &#8968;&#13266; n&#8969;</td>
     *     <td>&nbsp;T(n)</td>
     *   </tr>
     *   <tr>
     *     <td>linear</td>
     *     <td>T<sub>0</sub>-n*&#948;</td>
     *     <td>&nbsp;T(n) = T(n-1)-&#948;</td>
     *   </tr>
     *   <tr>
     *     <td>hyperbolic</td>
     *     <td>1/T<sub>0</sub>+n*&#948;</td>
     *     <td>1/&nbsp;T(n) = 1/T(n-1)+&#948;</td>
     *   </tr>
     *   <tr>
     *     <td>exponential</td>
     *     <td>T<sub>0</sub>*q<sup>n</sup></td>
     *     <td>&nbsp;T(n) = T(n-1)*q</td>
     *   </tr>
     * </table>
     * </p>
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> to be used as evaluation function f(n) = h(n).
     * @param schedule a mapping <b>N</b>&rarr;<b>R</b>
     *  from time to "temperature" controlling the cooling, and thus
     *  the probability of downward steps.
     *  Algorithm stops if the temperature drops to <span class="Number">0</span>
     *  (or isSolution is <span class="keyword">true</span>,
     *  or it fails due to a lack of alternative expansion nodes).
     * @param localSelection the variant of local selection used.
     */
    public ScheduledLocalOptimizerSearch(Function/*<GeneralSearchProblem.Option, Arithmetic>*/ heuristic, Function/*<Integer, Real>*/ schedule, LocalSelection localSelection) {
	super(localSelection);
    	this.heuristic = heuristic;
    	this.schedule = schedule;
    }

    public Function/*<GeneralSearchProblem.Option, Arithmetic>*/ getHeuristic() {
	return heuristic;
    }

    public void setHeuristic(Function/*<GeneralSearchProblem.Option, Arithmetic>*/ heuristic) {
	this.heuristic = heuristic;
    }

    /**
     * Get the scheduling function.
     * @return a mapping <b>N</b>&rarr;<b>R</b>
     *  from time to "temperature" controlling the cooling, and thus
     *  the probability of downward steps.
     *  Algorithm stops if the temperature drops to <span class="Number">0</span>
     *  (or isSolution is <span class="keyword">true</span>,
     *  or it fails due to a lack of alternative expansion nodes).
     */
    public Function/*<Integer, Real>*/ getSchedule() {
	return schedule;
    }

    public void setSchedule(Function/*<Integer, Real>*/ schedule) {
	this.schedule = schedule;
    }

    /**
     * Local optimizers are usally not correct.
     * Because the scheduling function is (usually) independent from the current state,
     * and thus may be too fast, such that the search has not yet reached an optimum.
     */
    public boolean isCorrect() {
	return false;
    }

    /**
     * Local optimizers are not optimal (usually).
     * Because the scheduling function may be too fast, such that the search
     * cannot yet have reached the global optimum.
     */
    public boolean isOptimal() {
	return false;
    }
}
