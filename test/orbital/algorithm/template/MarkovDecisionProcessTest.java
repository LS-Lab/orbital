/**
 * @(#)MarkovDecisionProcessTest.java 1.2 2003-07-20 Andre Platzer
 * 
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

import junit.framework.*;

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import java.util.*;
import java.io.*;
import orbital.awt.Closer;
import java.awt.Frame;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Automatic test-driver checking (descendants of) orbital.algorithm.template.MarkovDecisionProcess.
 * Tests all known MarkovDecisionProcess implementations
 *
 * @version 0.8, 2003-07-20
 * @author  Andr&eacute; Platzer
 * @see RobotNavigation
 */
public class MarkovDecisionProcessTest extends check.TestCase {
    private static final int TRIALS = 30;
    private static final Logger logger = Logger.global;

    private RobotNavigation problems[];
	
    private Function/*<Moving,String>*/ solutions[];


    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(MarkovDecisionProcessTest.class);
    }

    protected void setUp() throws IOException {
	RobotNavigation.setDelay(40);

        InputStream input = new FileInputStream("examples/Algorithms/test.lab.txt");
	this.problems = new RobotNavigation[] {
	    new RobotNavigation(input)
	};
	input.close();

	this.solutions = new Function[] {
	    new Function() {
		String actions[] = {
		    "FF",
		    "FF",
		    "FF",
		    "l",
		    "FF",
		    "FF",
		    "FF",
		    "FF",
		    "FF",
		    "l",
		    "FF",
		    "FF",
		    "FF",
		    "r",
		    "FF"
		};
		int index = 0;
		public Object apply(Object o) {
		    return actions[index++];
		}
	    }
	};
    }
    
    public void testRTDP() throws IOException {
	for (int i = 0; i < problems.length; i++) {
	    RobotNavigation nav = problems[i];
	    MarkovDecisionProcess planner;
	    // here we decide which exact MDP planning algorithm to use
	    // the single difference in using another planning algorithm
	    // would only concern the constructor call
	    planner = new RealTimeDynamicProgramming(nav.getHeuristic());
	    testMDP(nav, planner, solutions[i]);
	}
    }

    public void testGSDP() throws IOException {
	for (int i = 0; i < problems.length; i++) {
	    RobotNavigation nav = problems[i];
	    MarkovDecisionProcess planner;
	    // here we decide which exact MDP planning algorithm to use
	    // the single difference in using another planning algorithm
	    // would only concern the constructor call
	    planner = new GaussSeidelDynamicProgramming(nav.getHeuristic(), nav.allStates(), 0.1);
	    testMDP(nav, planner, solutions[i]);
	}
    }
    
    protected void testMDP(RobotNavigation nav, MarkovDecisionProcess planner, Function expected) {
	Frame f = new Frame();
	new Closer(f, true, true);
	f.add(nav.getView());
	f.pack();
	f.setVisible(true);

	// really obtain a plan
	Function plan = planner.solve(nav);

	nav.followPlan(plan, TRIALS);

	compare(nav, plan, expected);

	f.setVisible(false);
	f.dispose();
    } 

    
    protected void compare(RobotNavigation nav, Function plan1, Function plan2) {
	List t1 = nav.tracePlan(plan1);
	List t2 = nav.tracePlan(plan2);
	assertEquals("plans lead to same (unique) optimal results in deterministic case",
		     t1,
		     t2
		     );
    }
}
