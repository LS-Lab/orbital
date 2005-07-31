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
import java.awt.Frame;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Automatic test-driver checking (descendants of) orbital.algorithm.template.MarkovDecisionProcess.
 * Tests all known MarkovDecisionProcess implementations
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see RobotNavigation
 */
public class MarkovDecisionProcessTest extends check.TestCase {
    private static final Logger logger = Logger.global;

    private RobotNavigation problems[];
	
    private Function/*<Moving,String>*/ solutions[];

    /**
     * How many trials (roughly) needed (by RTDP).
     */
    private int trials[];

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(MarkovDecisionProcessTest.class);
    }

    protected void setUp() throws IOException {
	RobotNavigation.setDelay(20);

	this.problems = new RobotNavigation[] {
	    new RobotNavigation(new FileInputStream("examples/Algorithms/test.lab.txt")),
	    new RobotNavigation(new FileInputStream("examples/Algorithms/testshell.lab.txt"))
	};

	this.trials = new int[] {
	    30,
	    100
	};

	this.solutions = new Function[] {
	    new EnumFunction(new String[] {
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
	    }),
	    new EnumFunction(new String[] {
		"l",
		"FF",
		"FF",
		"FF",
		"r",
		"FF",
		"l",
		"FF",
		"FF",
		"FF",
		"FF",
		"r",
		"FF",
		"FF",
		"FF",
		"r",
		"FF",
		"FF",
		"FF",
		"FF",
		"FF",
		"FF",
		"r",
		"FF",
		"FF",
		"r",
		"FF",
		"FF",
		"FF",
		"FF",
		"r",
		"FF",
		"r",
		"FF",
		"FF",
		"FF"
	    })
	};
    }

    private static class EnumFunction implements Function {
	private final String actions[];
	public EnumFunction(String actions[]) {
	    this.actions = actions;
	}
	private int index = 0;
	public Object apply(Object o) {
	    return actions[index++];
	}
    };
    
    public void testRTDP() throws IOException {
	for (int i = 0; i < problems.length; i++) {
	    RobotNavigation nav = problems[i];
	    MarkovDecisionProcess planner;
	    // here we decide which exact MDP planning algorithm to use
	    // the single difference in using another planning algorithm
	    // would only concern the constructor call
	    planner = new RealTimeDynamicProgramming(nav.getHeuristic());
	    testMDP(nav, planner, solutions[i], trials[i]);
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
	    testMDP(nav, planner, solutions[i], 2);
	}
    }
    
    protected void testMDP(RobotNavigation nav,
			   MarkovDecisionProcess planner,
			   Function expected,
			   int trials) {
	try {
	    System.out.println(planner + " " + planner.complexity());
	}
	catch (UnsupportedOperationException noComplexityInformation) {}
	try {
	    System.out.println(planner + " " + planner.spaceComplexity());
	}
	catch (UnsupportedOperationException noComplexityInformation) {}
	Frame f = new Frame();
	f.add(nav.getView());
	f.pack();
	f.setVisible(true);
	try {

	    // really obtain a plan
	    Function plan = planner.solve(nav);
	    
	    nav.followPlan(plan, trials);
	    
	    compare(nav, plan, expected);

	} finally {
	    f.setVisible(false);
	    f.dispose();
	}
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
