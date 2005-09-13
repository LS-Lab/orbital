/*
 * @(#)Search.java 0.9 1998/12/17 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;
import orbital.logic.functor.Predicate;
import orbital.logic.RecursionStoppedException;

/**
 * Search recursively through any search-space that is a container with a logical
 * two-dimensional structure ({@link Table Table}).
 * The concrete implementation of the Table-structure searched through is
 * abstracted from. This Search-Algorithm does not depend upon an Interface,
 * it only callbacks a predicate with Point-Objects.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.algorithm.template.GeneralSearchProblem
 * @deprecated Since Orbital1.1 use {@link orbital.algorithm.template.DepthFirstSearch} on OpenClosedGeneralSearchProblems instead.
 */
public class Search extends Table {
    //TODO: unify with a orbital.algorithm.template.DepthFirstSearch implementation
    // it's not exactly orbital.algorithm.template.DepthFirstSearch since it memorizes not to go anywhere, twice, to avoid being trapped in a loop

    /**
     * The two-dimensional array with the boolean-elements history contained.
     * History is true if has already been visited, false otherwise.
     * @serial
     */
    private boolean history[][];

    /**
     * Predicate called to check whether visiting a node
     * should be continued into all other directions.
     * This Predicate is callbacked on each Point.
     * @serial
     * @see #search(java.awt.Point, orbital.logic.functor.Predicate)
     */
    private Predicate cont;


    /**
     * Initialize a Search on a certain range.
     * This constructor is independent of the concrete search-space searched through
     * as well as the underlying implementation of the Table searched through.
     */
    public Search(Rectangle bounds) {
	super(bounds);
	history = new boolean[bounds.height][bounds.width];
    }
    public Search(int x, int y, int width, int height) {
	super(x, y, width, height);
	history = new boolean[height][width];
    }
    public Search(Point p, Dimension dim) {
	super(p, dim);
	history = new boolean[dim.height][dim.width];
    }

    /**
     * Start the Search beginning at the Point indexed with <i>start</i>
     * and a continue-Predicate.
     * @param start the original point where to start the search.
     * @param cont a predicate called to check on visit of each node
     * saying whether to continue the search from that point into all subsequent directions.
     * cont can throw a orbital.logic.RecursionStoppedException to abort all searching.
     * @see #cont
     * @see #visit(int,int)
     */
    public void search(Point start, Predicate cont) {
	// clear history -> was nowhere
	for (int j = 0; j < history.length; j++)
	    for (int i = 0; i < history[j].length; i++)
		history[j][i] = false;
	this.cont = cont;
	visit(start.x, start.y);	// visit each field starting with start
    } 

    /**
     * Recursively visit each sub "node".
     * <p>
     * If cont evaluates to <code>true</code>, will visit all subsequent nodes.
     * If cont evaluates to <code>false</code> subsequent nodes of this will be discarded
     * (but apart from that searching continues).
     * If cont throws a RecursionStoppedException all searching will be aborted.
     * 
     * @return <code>true</code> if visiting exploration continued from this point to its subsequent nodes.
     * This will be done if cont evaluates to <code>true</code>.
     * <code>false</code> if subsequent nodes of this are discarded and searching should continue elsewhere.
     * This will be if cont evaluates to <code>false</code>.
     * @throws   RecursionStoppedException if all searching is aborted.
     * This will occur if cont evaluates throws a <code>RecursionStoppedException</code>.
     * @see #getSubsequentExplorations(int,int)
     */
    protected boolean visit(int x, int y) throws RecursionStoppedException {
	final Rectangle bounds = getBounds();
	if (!inRange(new Point(x, y)))
	    return false;
	// RA: was here already->break
	if (history[y - bounds.y][x - bounds.x])
	    return true;
	history[y - bounds.y][x - bounds.x] = true;

	if (cont != null && !cont.apply(new Point(x, y)))	// !continue->break
	    return false;

	int[][] ds = getSubsequentExplorations(x, y);

	// continue recursively in all Directions
	for (int i = 0; i < ds.length; i++)
	    // RS: visit subsequent nodes
	    visit(x + ds[i][0], y + ds[i][1]);

	// history[y-min.y][x-min.x] = false;                    	// for multiple cycles, RA not defined
	return true;
    } 

    /**
     * Get all subsequent directions that should be checked from a Point (x|y)
     * for the paths that are leading further.
     * <p>
     * This implementation will return all directions in (E, S, W, N) - order.
     * Subclasses can optimize this behaviour to specify another search strategy.
     * @return  an Array of <code>int[]</code> leading to all directions. The whole array
     * has an Element <code>[i][0]</code> for <i>x</i> and an Element <code>[i][1]</code> for <i>y</i>.
     */
    protected int[][] getSubsequentExplorations(int x, int y) {
	return standard_ds;
    } 

    private static int standard_ds[][] = {
	{+1,  0},
	{ 0, +1},
	{-1,  0},
	{ 0, -1}
    };

    /**
     * not supported.
     * @throws UnsupportedOperationException if this Table does not support reading.
     */
    public Object get(Point p) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    } 

    /**
     * not supported.
     * @throws UnsupportedOperationException if this Table is readonly.
     */
    public void set(Point p, Object what) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    } 
}
