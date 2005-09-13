/*
 * @(#)GoalOrientedSearch.java 0.9 1998/12/21 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.awt.Rectangle;
import java.awt.Point;
import orbital.logic.RecursionStoppedException;

/**
 * Search recursively through any Container with a logical
 * two-dimensional structure (Table).
 * The Search is goal-oriented in a way that the recursion tends
 * to a certain Point of destination (goal).
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.algorithm.template.AStar
 * @deprecated Since Orbital1.1 use a {@link orbital.algorithm.template.HeuristicAlgorithm} implementation instead.
 */
public class GoalOrientedSearch extends Search {
    //TODO: unify with a orbital.algorithm.template.HeuristicSearch implementation
    // it's neither orbital.algorithm.template.AStar, because GoalOrientedSearch assumes accumulated cost g(n)=0
    // nor orbital.algorithm.template.HillClimbing since it memorizes not to go anywhere, twice, to avoid being trapped in a loop
    // so perhaps it's a simple TabuSearch tabuing visited nodes.

    /**
     * The Point of destination, goal to reach.
     * 
     * @serial
     */
    private Point goal;

    /**
     * Initialize a Search on a certain range. This Search-Initialization
     * as well as the Search-class is independent of the
     * underlying implementation of the Table searched over.
     */
    public GoalOrientedSearch(int x, int y, int width, int height, Point goal) {
	super(x, y, width, height);
	this.goal = goal;
    }

    /**
     * Initialize a Search on a certain range. This Search-Initialization
     * as well as the Search-class is independent of the
     * underlying implementation of the Table searched over.
     */
    public GoalOrientedSearch(Rectangle bounds, Point goal) {
	super(bounds);
	this.goal = goal;
    }

    /**
     * Get all directions that should be checked from a Point (x|y)
     * for the paths that are leading further.
     * This function will return all directions in the order that the ones
     * oriented to the goal will be returned first.
     * 
     * @return  an Array of <code>int[]</code> leading to all directions. The whole array
     * has an Element <code>[i][0]</code> for <i>x</i> and an Element <code>[i][1]</code> for <i>y</i>.
     */
    protected int[][] getSubsequentExplorations(int x, int y) {
	if (goal.equals(new Point(x, y)))
	    throw new RecursionStoppedException();	  // stop now

	int		dx = x - goal.x;
	int		dy = y - goal.y;

	boolean xpref = Math.abs(dx) >= Math.abs(dy);	 // x preferred <= dx>=dy

	int[][] ds = new int[4][2];
	ds[xpref ? 0 : 1] = standard_ds[dx < 0 ? 0 : 2];
	ds[xpref ? 3 : 2] = standard_ds[!(dx < 0) ? 0 : 2];
	ds[xpref ? 1 : 0] = standard_ds[dy < 0 ? 1 : 3];
	ds[xpref ? 2 : 3] = standard_ds[!(dy < 0) ? 1 : 3];

	return ds;
    } 

    private static int standard_ds[][] = {
	{+1,  0},
	{ 0, +1},
	{-1,  0},
	{ 0, -1}
    };
}
