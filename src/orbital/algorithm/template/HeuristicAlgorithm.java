/**
 * @(#)HeuristicAlgorithm.java 1.0 2000/09/20 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.logic.functor.Function;
import java.io.Serializable;
import orbital.math.Real;

import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Interface for heuristic (search) algorithms.
 * <p>
 * Also called informed search algorithms or informed planning algorithms.</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public interface HeuristicAlgorithm/*<Problem extends AlgorithmicProblem, S extends Object>*/
    extends EvaluativeAlgorithm/*<Problem, S>*/ {
    /**
     * Get the heuristic function used.
     * @return the heuristic cost function h:S&rarr;<b>R</b> estimating h<sup>*</sup>.
     */
    Function/*<S,Real>*/ getHeuristic();

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
     * <p>
     * A simple improvement for heuristic functions is using pathmax.
     * </p>
     * @param heuristic the heuristic cost function h:S&rarr;<b>R</b> estimating h<sup>*</sup>.
     *  h will be embedded in the evaluation function {@link #getEvaluation() f}.
     * @preconditions heuristic is functional, i.e. x.equals(y) &rArr; heuristic.apply(x).equals(heuristic.apply(y))
     * @see "Pearl, J. Heuristics: Intelligent Search Strategies for Computer Problem Solving. Addison-Wesley, Reading, Massachusetts. 1984."
     */
    void setHeuristic(Function/*<S,Real>*/ heuristic);

    /**
     * {@inheritDoc}.
     * <p>
     * The evaluation function f may depend upon an {@link #setHeuristic(Function) heuristic cost function} h:S&rarr;<b>R</b>
     * </p>
     */
    Function/*<S,Real>*/ getEvaluation();

    /**
     * Algorithmic configuration objects for heuristic algorithms.
     * @author Andr&eacute; Platzer
     * @version $Id$
     * @todo move to HeuristicAlgorithm.Configuration ?
     */
    public static class Configuration/*<Problem extends AlgorithmicProblem, S extends Object>*/
	extends AlgorithmicTemplate.Configuration/*<Problem,S>*/ {
        private static final long serialVersionUID = 8651734898965188478L;

        /**
         * @serial
         */
        private Function/*<S,Real>*/ heuristic;
        /**
         * @param problem the problem to solve.
         * @param heuristic the {@link HeuristicAlgorithm#setHeuristic(Function) heuristic} used for solving.
         * @param algorithm the class of the AlgorithmicTemplate to instantiate for solving the problem.
         */
        public Configuration(AlgorithmicProblem/*>Problem<*/ problem, Function/*<S,Real>*/ heuristic, Class algorithm) {
            super(problem, algorithm, HeuristicAlgorithm.class);
            this.heuristic = heuristic;
        }

        public Function/*<S,Real>*/ getHeuristic() {
            return heuristic;
        }
        
        public AlgorithmicTemplate getAlgorithm() {
            HeuristicAlgorithm algo = (HeuristicAlgorithm) super.getAlgorithm();
            algo.setHeuristic(getHeuristic());
            return algo;
        }

    }

    /**
     * An heuristic function that uses a pattern database.
     * <p>
     * This heuristic function uses a pattern database for better values or speedup.
     * For states that are not yet known in the pattern database a backing traditional heuristic
     * function is required.
     * The patterns can either be build from the full or from a projected state space.</p>
     * <p>
     * Within the pattern database this heuristic is optimal (i.e. h|<sub>P</sub> = h<sup>*</sup>|<sub>P</sub>
     * for the part P of the state space that is handled by the pattern database)
     * and for the full state space it is admissible.
     * Still a good backing heuristic function is required for maximum performance.</p>
     * <p>
     * Dynamically building or increasing a pattern database can be a worthwhile
     * refinement of the pre-processing approach to pattern database creation,
     * as it better adapts to the current problem's need. Either way, the basic
     * idea of using a pattern database, especially in the presence of memoisation
     * (dynamically improving the database during the search) is {@link DynamicProgramming dynamic programming}.
     * Observe the close connection of pattern database heuristics and bidirectional search.
     * </p>
     * <p>
     * To massively reduce memory usage the pattern database could store hash codes
     * instead of whole state objects.
     * Then the state objects are assumed to have an appropriate {@link Object#hashCode()} implementation.
     * Although this dramatically reduces memory usage then the heuristic relies on disjunct hash codes
     * or may lose admissibility.</p>
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see "Memory-Based Heuristics: Pattern Databases. Culbersion & Schaeffer. 1995."
     * @see "Stefan Edelkamp. Symbolic pattern databases in heuristic search planning. In Ghallab, M. and Hertzberg, J. and Traverso, P., editors. Proceedings of the 7th International Conference on Artificial Intelligence Planning and Scheduling (AIPS-02), Toulouse, France, April, 2002, AAAI Press, Menlo Park. pages 274-283"
     */
    public static class PatternDatabaseHeuristic/*<S>*/ implements Function/*<S,Real>*/, Serializable {
        private static final long serialVersionUID = -4488685150678833742L;
        /**
         * the heuristic function used for states not contained in the pattern database.
         * @serial
         */
        private Function/*<S,Real>*/ heuristic;
        /**
         * the pattern database to use for looking up cost.
         * @serial
         */
        private Map/*<S,Real>*/ patternDatabase;
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
        public PatternDatabaseHeuristic(Function/*<S,Real>*/ backingHeuristic, Map/*<S,Real>*/ patternDatabase, boolean autoUpdatePatternDatabase) {
            this.heuristic = backingHeuristic;
            this.patternDatabase = patternDatabase;
            this.autoUpdatePatternDatabase = autoUpdatePatternDatabase;
        }
        public PatternDatabaseHeuristic(Function/*<S,Real>*/ backingHeuristic, Map/*<S,Real>*/ patternDatabase) {
            this(backingHeuristic, patternDatabase, false);
        }
        public PatternDatabaseHeuristic(Function/*<S,Real>*/ backingHeuristic) {
            this(backingHeuristic, new HashMap(), false);
        }
                
        /**
         * Get the backing heuristic.
         * @return the heuristic function used for states not contained in the pattern database.
         */
        public Function/*<S,Real>*/ getHeuristic() {
            return heuristic;
        }

        /**
         * Get the pattern database.
         * @return the pattern database used for looking up cost.
         *  It is a Map from states to cost.
         */
        public Map/*<S,Real>*/ getPatternDatabase() {
            return patternDatabase;
        }

        /**
         * Set the pattern database.
         * @param patterns the pattern database to use for looking up cost.
         *  It is a Map from states to cost.
         */
        public void setPatternDatabase(Map/*<S,Real>*/ patterns) {
            patternDatabase = patterns;
        }
                
        public Object/*>Real<*/ apply(Object/*>S<*/ o) {
            Object/*>S<*/ s = o;
            Object/*>Real<*/ v = patternDatabase.get(s);
            // did not find a corresponding value in pattern database? Use backing heuristic instead
            if (v == null) {
                v = heuristic.apply(s);
                if (autoUpdatePatternDatabase)
                    patternDatabase.put(s, v);
            }
            return v;
        }
    }
}
