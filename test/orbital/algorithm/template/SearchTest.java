/**
 * @(#)SearchTest.java 1.1 2002-04-06 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

import junit.framework.*;

import orbital.algorithm.template.*;
import orbital.algorithm.template.AlgorithmicTemplate.Configuration;
import orbital.logic.functor.Function;
import orbital.math.functional.Functions;
import orbital.math.Scalar;
import orbital.math.Values;
import orbital.math.ValueFactory;
import orbital.math.Symbol;
import orbital.math.Real;
import orbital.awt.*;
import java.lang.reflect.*;
import java.util.*;
import orbital.util.Setops;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Automatic test-driver checking (descendants of) orbital.algorithm.template.GeneralSearch.
 * Tests all known GeneralSearch implementations
 *
 * @version 0.8, 2002/04/06
 * @author  Andr&eacute; Platzer
 * @see AlgorithmicTable
 * @see SimpleGSP
 */
public class SearchTest extends check.TestCase {
    private static final Logger logger = Logger.global;
    private static final int TEST_REPETITION = 1;
    private static final int SIMPLE_GSP_RANGE = 5;
    private static final int UNSOLVABLE_GSP_RANGE = 4;

    private AlgorithmicConfiguration[] defaultAlgo;
    private Random random;
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(SearchTest.class);
    }

    protected void setUp() {
	this.random = new Random();
	final ValueFactory vf = Values.getDefault();
	final Real maxBound = vf.valueOf(2*SIMPLE_GSP_RANGE+SimpleGSP.PAY_FOR_PASSING);
	final Function h = SimpleGSP.createHeuristic();
	final Function schedule = new Function() {
		public Object apply(Object o) {
		    int i = ((Number) o).intValue();
		    double d = 10 - i/175.;
		    return vf.valueOf(Math.max(d, 0));
		}
	    };
	try {
	    //@internal the problem is ignored by test(...) anyway, so let's temporarily use null.
	    this.defaultAlgo = new AlgorithmicConfiguration[] {
		/*"DepthFirstSearch", */
		new Configuration(null, BreadthFirstSearch.class),
		new Configuration(null, IterativeDeepening.class),
		/*"IterativeBroadening", */
		new HeuristicAlgorithm.Configuration(null, h, AStar.class),
		new HeuristicAlgorithm.Configuration(null, h, HillClimbing.class),
		new HeuristicAlgorithm.Configuration(null, h, IterativeDeepeningAStar.class),
		Configuration.flexible(null, Setops.asMap(new Object[][] {
		    {"heuristic", h},
		    {"maxBound", maxBound}
		}), BranchAndBound.class),
		Configuration.flexible(null, Setops.asMap(new Object[][] {
		    {"heuristic", h},
		    {"maxBound", maxBound}
		}), ParallelBranchAndBound.class),
		new HeuristicAlgorithm.Configuration(null, h, IterativeExpansion.class),
		Configuration.flexible(null, Setops.asMap(new Object[][] {
		    {"heuristic", h},
		    {"schedule", schedule}
		}), SimulatedAnnealing.class),
		Configuration.flexible(null, Setops.asMap(new Object[][] {
		    {"heuristic", h},
		    {"schedule", schedule}
		}), ThresholdAccepting.class),
		Configuration.flexible(null, Setops.asMap(new Object[][] {
		  {"heuristic", h},
		  {"weight", Values.getDefaultInstance().valueOf(2)}
		  }), WAStar.class)
	    };
	} catch (java.beans.IntrospectionException ex) {
	    ex.printStackTrace();
	    fail(ex.getMessage());
	}
    }
    
    public void test() {
	test(defaultAlgo);
    }

    
    protected void test(AlgorithmicConfiguration algo[]) {
	System.out.println("Testing " + algo.length + " algorithms ...");
	for (int i = 0; i < algo.length; i++) {
	    System.out.println("Testing No. " + i + " : " + algo[i]);
	    test(algo[i]);
	}
    }

    protected void test(AlgorithmicConfiguration config) {
	try {
	    AlgorithmicTemplate algo = config.getAlgorithm();
	    System.out.println("Testing " + algo);

	    // instantiate
	    boolean complete = false;
	    boolean correct = false;
	    boolean optimal;
	    try {
		complete = (algo.complexity() != Functions.nondet 
			    //@xxx: norm is infinite for all polynoms, what else!
			    && !(algo.complexity().equals(Functions.constant(Values.getDefaultInstance().valueOf(Double.POSITIVE_INFINITY)))));
	    }
	    catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
	    try {
		algo.spaceComplexity();
	    }
	    catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
	    try {
		correct = !(algo instanceof ProbabilisticAlgorithm) || ((ProbabilisticAlgorithm) algo).isCorrect();
	    }
	    catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
	    optimal = algo instanceof GeneralSearch && ((GeneralSearch) algo).isOptimal();

	    // test problems that have a solution
	    for (int rep = 0; rep < TEST_REPETITION; rep++) {
		VerifyingSimpleGSP p = new VerifyingSimpleGSP((int)(random.nextFloat()*2*SIMPLE_GSP_RANGE-SIMPLE_GSP_RANGE), (int)(random.nextFloat()*2*SIMPLE_GSP_RANGE-SIMPLE_GSP_RANGE));
		System.out.println("Test solvable problem " + p + " for " + algo);
		Object solution = algo.solve(p);
		assertTrue( solution != null , algo + " should solve a problem that admits a solution. " + p);
		if (!p.isSolution(solution))
		    System.out.println(algo + " did not solve a problem that admits a solution. " + p + " \"solution\" found " + solution);
		if (optimal)
		    assertTrue(p.isOptimalSolution(solution) , algo + " is optimal but has found only a suboptimal solution " + solution + " to " + p);
	    }
				
	    // test problems that do not have a solution (but a finite search space)
	    for (int rep = 0; rep < TEST_REPETITION; rep++) {
		UnsolvableSimpleGSP p = new UnsolvableSimpleGSP((int)(random.nextFloat()*UNSOLVABLE_GSP_RANGE));
		System.out.println("Test unsolvable problem " + p + " for " + algo);
		try {
		    Object solution = algo.solve(p);
		    if (correct)
			assertTrue(solution == null , algo + " should not \"solve\" a problem that has no solution. " + p + " \"solution\" " + solution);
		} catch (NoSuchElementException ex) {
		    if (ex.getMessage().indexOf("local optimizer cannot continue") >= 0
			&& algo instanceof LocalOptimizerSearch)
			// local optimizers cannot continue when they get trapped at the end of the search space.
			;
		    else
			throw ex;
		}
	    }
	} catch (Exception ignore) {
	    logger.log(Level.FINER, "introspection", ignore);
	    ignore.printStackTrace();
	    fail(ignore.getMessage() + " in " + config);
	} 
    } 


    // individual tests for timing
    public void test0() {
	test(0);
    }
    public void test1() {
	test(1);
    }
    public void test2() {
	test(2);
    }
    public void test3() {
	test(3);
    }
    public void test4() {
	test(4);
    }
    public void test5() {
	test(5);
    }
    public void test6() {
	test(6);
    }
    public void test7() {
	test(7);
    }
    public void test8() {
	test(8);
    }
    public void test9() {
	test(9);
    }
    public void test10() {
	test(10);
    }
    public void test11() {
	test(11);
    }
    public void test12() {
	test(12);
    }
    public void test13() {
	test(13);
    }
    public void test14() {
	test(14);
    }
    private void test(int index) {
	if (index < defaultAlgo.length)
	    test(defaultAlgo[index]);
    }
}
