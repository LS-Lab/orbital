/**
 * @(#)HeuristicAlgorithm.java 1.0 2000/09/20 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.io.Serializable;

import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Interface for heuristic (search) algorithms.
 * <p>
 * Also called informed search algorithms or informed planning algorithms.</p>
 *
 * @version 1.0, 2000/09/20
 * @author  Andr&eacute; Platzer
 */
public
interface HeuristicAlgorithm extends EvaluativeAlgorithm {
	/**
	 * Get the heuristic function used.
	 * @return the heuristic cost function h:S&rarr;<b>R</b> estimating h<sup>*</sup>.
	 */
	Function getHeuristic();

	/**
	 * Set the heuristic function to use.
     * <p>
     * An <dfn>heuristic cost function</dfn> h:S&rarr;<b>R</b>
     * is estimating the cost to get from a node n to a goal G.
     * For several heuristic algorithms this heuristic function needs to be <dfn>admissible</dfn>
     *   <center>h &le; h<sup>*</sup></center>
     * i.e. h is a lower bound for the effective cost function h<sup>*</sup>.
     * Then the pathmax heuristic f(s') := max {f(s), g(s') + h(s')} (for transitions s&rarr;s'=t(s,a) with a&isin;A(s))
     * is monotonic.</p>
     * <p>
     * A heuristic cost function h is <dfn>monotonic</dfn> :&hArr;
     * the f-costs (with h) do not decrease in any path from the initial state
     * &hArr; h obeys the <dfn>triangular inequality</dfn>
     *   <center>&forall;s&isin;S,a&isin;A(s) h(s) &le; h(s') + c(s,a) with s' = t(s,a)</center>
     * i.e. the sum of the costs from A to C and from C to B must not be less than the cost from A to B.</p>
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> estimating h<sup>*</sup>.
     *  h will be embedded in the evaluation function {@link #getEvaluation() f}.
     * @pre heuristic is functional, i.e. x.equals(y) &rArr; heuristic.apply(x).equals(heuristic.apply(y))
     * @see "Pearl, J. Heuristics: Intelligent Search Strategies for Computer Problem Solving. Addison-Wesley, Reading, Massachusetts. 1984."
	 */
	void setHeuristic(Function heuristic);

    /**
     * Get the evaluation function used while processing.
     * <p>
     * It may depend upon an {@link #setHeuristic(Function) heuristic cost function} h:S&rarr;<b>R</b>
     * </p>
     * @return the <dfn>evaluation function</dfn> f:S&rarr;<b>R</b> to use.
     */
    Function getEvaluation();

	/**
	 * An heuristic function that uses a pattern database.
	 * <p>
	 * This heuristic function uses a pattern database for better values or speedup.
	 * For states that are not yet known in the pattern database a backing traditional heuristic
	 * function is required.
	 * The patterns can either be build from the full or from a projected state space.</p>
	 * <p>
	 * Within the pattern database this heuristic is optimal (h|<sub>P</sub> = h<sup>*</sup>|<sub>P</sub></p>
	 * for the part P of the state space that is handled by the pattern database)
	 * and for the full state space it is admissible.
	 * But good a backing heuristic function is still required.</p>
	 * <p>
	 * To massively reduce memory usage the pattern database could store hash codes
	 * instead of whole state objects.
	 * Then the state objects are assumed to have an appropriate {@link Object#hashCode()} implementation.
	 * Although this dramatically reduces memory usage then the heuristic relies on disjunct hash codes
	 * or may lose admissibility.</p>
	 * @version 1.0, 2000/09/20
	 * @author  Andr&eacute; Platzer
	 * @see "Memory-Based Heuristics: Pattern Databases. Culbersion & Schaeffer. 1995."
	 */
	public static class PatternDatabaseHeuristic implements Function, Serializable {
		/**
		 * the heuristic function used for states not contained in the pattern database.
		 * @serial
		 */
		private Function heuristic;
		/**
		 * the pattern database to use for looking up cost.
		 * @serial
		 */
		private Map patternDatabase;
		/**
		 * whether to enter heuristic estimate cost
		 * into the pattern database for states not yet contained.
		 * This is almost only useful for very expensive backing heuristic functions.
		 * @serial
		 */
		private boolean autoUpdatePatternDatabase;
		/**
		 * Create a new heuristic function supported by a pattern database.
		 * @param backingHeuristic the heuristic function used for states not contained in the
		 *  pattern database.
		 * @param patternDatabase the pattern database to use for looking up cost.
		 * @param autoUpdatePatternDatabase whether to enter heuristic estimate cost
		 *  into the pattern database for states not yet contained.
		 *  This is almost only useful for very expensive backing heuristic functions.
		 */
		public PatternDatabaseHeuristic(Function backingHeuristic, Map patternDatabase, boolean autoUpdatePatternDatabase) {
			this.heuristic = backingHeuristic;
			this.patternDatabase = patternDatabase;
			this.autoUpdatePatternDatabase = autoUpdatePatternDatabase;
		}
		public PatternDatabaseHeuristic(Function backingHeuristic, Map patternDatabase) {
			this(backingHeuristic, patternDatabase, false);
		}
		public PatternDatabaseHeuristic(Function backingHeuristic) {
			this(backingHeuristic, new HashMap(), false);
		}
		
		/**
		 * Get the backing heuristic.
		 * @return the heuristic function used for states not contained in the pattern database.
		 */
		public Function getHeuristic() {
			return heuristic;
		}

		/**
		 * Get the pattern database.
		 * @return the pattern database used for looking up cost.
		 *  It is a Map from states to cost.
		 */
		public Map getPatternDatabase() {
			return patternDatabase;
		}

		/**
		 * Set the pattern database.
		 * @param patterns the pattern database to use for looking up cost.
		 *  It is a Map from states to cost.
		 */
		public void setPatternDatabase(Map patterns) {
			patternDatabase = patterns;
		}
		
		public Object apply(Object o) {
			GeneralSearchProblem.Option n = (GeneralSearchProblem.Option) o;
			Object v = patternDatabase.get(n.getState());
			// did not find a corresponding value in pattern database? Use backing heuristic instead
			if (v == null) {
				v = heuristic.apply(n);
				if (autoUpdatePatternDatabase)
					patternDatabase.put(n.getState(), v);
			}
			return v;
		}
	}
}