/**
 * @(#)EvaluativeAlgorithm.java 1.0 2001/07/30 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.util.Comparator;
import java.io.Serializable;

/**
 * Interface for evaluative algorithms.
 * <p>
 * This interface provides unified access to an evaluation function (to minimize etc.)
 * while processing.
 * </p>
 * <p>
 * In order to transform a maximization problem into a minimization problem
 * use the evaluation function -f instead of f.
 * </p>
 *
 * @version 1.0, 2001/07/30
 * @author  Andr&eacute; Platzer
 */
public interface EvaluativeAlgorithm extends AlgorithmicTemplate {
    /**
     * Get the evaluation function used while processing.
     * <p>
     * Also called objective function.
     * </p>
     * @return the <dfn>evaluation function</dfn> f:S&rarr;<b>R</b> used to
     *  evaluate (either utility or cost) value of states.
     */
    Function/*<GeneralSearchProblem.Option???,Real>*/ getEvaluation();

    /**
     * The natural comparator induced by the evaluation function f(n).
     * <p>
     * For highest performance it might prove useful to cache most recent values
     * of the evaluation function.</p>
     * @version 1.0, 2000/09/20
     * @author  Andr&eacute; Platzer
     */
    public static class EvaluationComparator implements Comparator, Serializable {
	/**
	 * the evaluation function used in order to compare two states.
	 * @serial
	 */
	private Function evaluationFunction;
	/**
	 * Create a new comparator for states compared by their evaluation values.
	 * @param evaluation the evaluation function used in order to compare two states.
	 */
	public EvaluationComparator(Function evaluation) {
	    this.evaluationFunction = evaluation;
	}
	/**
	 * Create a new comparator for states compared by their evaluation values.
	 * @param a the heuristic algorithm whose {@link EvaluativeAlgorithm#getEvaluation() evaluation function} used in order to compare two states.
	 */
	public EvaluationComparator(EvaluativeAlgorithm a) {
	    this(a.getEvaluation());
	}

	/*
	 * an object cache for recent values of the evaluation function f(n).
	 * @TODO: use List(new KeyValuePair()) aka OrderedMap()
	 * @todo could communicate with the candidate list, instead of using weak hash maps, as well?
	 */
	//private Map cache = /*//TODO:constrained(size=4)*/ new WeakHashMap();
	/**
	 * @return f(o1).compareTo(f(o2))
	 */
	public final int compare(Object o1, Object o2) {
	    // get the values f(n) from the cache if possible, otherwise store them
	    Object v1, v2;
	    /*System.err.print("    "+cache.size()+"\t");
	      v1 = cache.get(o1);
	      if (v1 == null)
	      cache.put(o1, v1 = evaluationFunction.apply(o1));
	      else
	      System.err.print("X");
	      v2 = cache.get(o2);
	      if (v2 == null)
	      cache.put(o2, v2 = evaluationFunction.apply(o2));
	      else
	      System.err.print("X");
	      System.err.println();*/
	    v1 = evaluationFunction.apply(o1);
	    v2 = evaluationFunction.apply(o2);
	    return ((Comparable)v1).compareTo(v2);
	}
    }
}
