/**
 * @(#)SearchTest.java 1.1 2002-04-06 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

import junit.framework.*;

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.functional.Functions;
import orbital.math.Scalar;
import orbital.math.Values;
import orbital.math.Symbol;
import orbital.awt.*;
import java.lang.reflect.*;
import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Automatical test-driver checking (descendants of) orbital.algorithm.template.GeneralSearch.
 * Tests all known GeneralSearch implementations
 *
 * @version 0.8, 2002/04/06
 * @author  Andr&eacute; Platzer
 * @see AlgorithmicTable
 * @see SimpleGSP
 */
public class SearchTest extends check.TestCase {
    private static final Logger logger = Logger.global;
    private static final int TEST_REPETITION = 2;
    private static final int SIMPLE_GSP_RANGE = 5;
    private static final int UNSOLVABLE_GSP_RANGE = 4;
    // pseudo-heuristic to be replaced lateron
    private static final Function h = Functions.one;
    private static final Function schedule = new Function() {
	    public Object apply(Object o) {
		int i = ((Number) o).intValue();
		double d = 10 - i/175.;
		return new Double(Math.max(d, 0));
	    }
	};
    public static final AlgorithmicTemplate[] defaultAlgo = {
	/*"DepthFirstSearch", */ new BreadthFirstSearch(), new IterativeDeepening(), /*"IterativeBroadening", */new AStar(h), new HillClimbing(h), new IterativeDeepeningAStar(h), new BranchAndBound(h, 0), new ParallelBranchAndBound(h, 0),
	new IterativeExpansion(h),
	new SimulatedAnnealing(h, schedule), new ThresholdAccepting(h, schedule),
	new WAStar(2, h)
    };
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	new SearchTest().test(defaultAlgo);
    }
    public static Test suite() {
	return new TestSuite(SearchTest.class);
    }
    
    private static final Random random = new Random();
    
    public void testAllSearchAlgorithms() {
	test(defaultAlgo);
    }

    protected void test(AlgorithmicTemplate algo[]) {
	for (int i = 0; i < algo.length; i++)
	    try {
		// instantiate
		boolean complete = false;
		boolean correct = false;
		boolean optimal;
		try {
		    complete = (algo[i].complexity() != Functions.nondet 
				//@FIXME: norm is infinite for all polynoms, what else!
				&& !(algo[i].complexity().equals(Functions.constant(Values.getDefaultInstance().valueOf(Double.POSITIVE_INFINITY)))));
		}
		catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
		try {
		    correct = algo[i] instanceof ProbabilisticAlgorithm ? ((ProbabilisticAlgorithm) algo[i]).isCorrect() : true;
		}
		catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
		optimal = algo[i] instanceof GeneralSearch ? ((GeneralSearch) algo[i]).isOptimal() : false;
		// special handling
		if (algo[i] instanceof HeuristicAlgorithm)
		    ((HeuristicAlgorithm)algo[i]).setHeuristic(SimpleGSP.createHeuristic());
		if (algo[i] instanceof BranchAndBound)
		    ((BranchAndBound)algo[i]).setMaxBound(2*SIMPLE_GSP_RANGE+SimpleGSP.PAY_FOR_PASSING);
				

		System.out.println("Testing " + algo[i]);
				
		// test problems that have a solution
		for (int rep = 0; rep < TEST_REPETITION; rep++) {
		    VerifyingSimpleGSP p = new VerifyingSimpleGSP((int)(random.nextFloat()*2*SIMPLE_GSP_RANGE-SIMPLE_GSP_RANGE), (int)(random.nextFloat()*2*SIMPLE_GSP_RANGE-SIMPLE_GSP_RANGE));
		    Object solution = algo[i].solve(p);
		    assertTrue( solution != null , algo[i] + " should solve a problem that admits a solution. " + p);
		    if (!p.isSolution(solution))
			System.out.println(algo[i] + " did not solve a problem that admits a solution. " + p + " \"solution\" found " + solution);
		    if (optimal)
			assertTrue(p.isOptimalSolution(solution) , algo[i] + " is optimal but has found only a suboptimal solution " + solution + " to " + p);
		}
				
		// test problems that do not have a solution (but a finite search space)
		for (int rep = 0; rep < TEST_REPETITION; rep++) {
		    UnsolvableSimpleGSP p = new UnsolvableSimpleGSP((int)(random.nextFloat()*UNSOLVABLE_GSP_RANGE));
		    Object solution = algo[i].solve(p);
		    if (correct)
			assertTrue(solution == null , algo[i] + " should not \"solve\" a problem that has no solution. " + p + " \"solution\" " + solution);
		}
	    } catch (Exception ignore) {
		logger.log(Level.FINER, "introspection", ignore);
	    } 
    } 
}
