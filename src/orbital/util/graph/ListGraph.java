/*
 * @(#)ListGraph.java 0.7 2000/07/15 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util.graph;

import orbital.util.KeyValuePair;
import java.util.Iterator;
import java.util.List;

import java.util.Set;

import orbital.logic.functor.Predicate;
import orbital.util.SuspiciousError;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Encapsulates a graph implemented with adjacence lists.
 * 
 * @version 0.7, 2000/07/15
 * @author  Andr&eacute; Platzer
 */

// TODO: implement a little better
public class ListGraph implements Graph {
    /**
     * the root nodes of this graph.
     * @serial
     */
    protected Set roots;
    public ListGraph(GraphNode root) {
	this.roots = new HashSet();
	if (root != null)
	    addRoot(root);
    }

    public Iterator getRoots() {
	return Collections.unmodifiableCollection(roots).iterator();
    } 

    public boolean addRoot(Node root) {
	if (!(root instanceof GraphNode))
	    throw new IllegalArgumentException("can only handle nodes of type " + GraphNode.class);
	return roots.add((GraphNode) root);
    } 

    public boolean removeRoot(Node root) {
	return roots.remove((GraphNode) root);
    }


    /**
     * O(|e|+|n|)
     */
    public void visit(Predicate predescend, Predicate ondescend, Predicate postdescend) {
	throw new UnsupportedOperationException("not yet implemented");
    } 

    // TODO: implement general search algorithm with stack and queue
    // search()
    // visit(k)

    // O(|e|+|n|)
    // visit over Stack -> Depth-Search
    // visit over Queue -> Breadth-Search
    // TODO: implement minimal span tree
    
    public Node createNode() {
    	return new GraphNode(null, null);
    }
	
    public static class GraphNode extends KeyValuePair implements Node {

	/**
	 * The references to the node's neighbours in the graph.
	 * The graph is kept in an adjacence-list.
	 * @serial
	 */
	protected List children;

	public GraphNode(Object key, Object data) {
	    super(key, data);
	    children = new LinkedList();
	}

	public boolean isLeaf() {
	    return children.isEmpty();
	} 

	public int getEdgeCount() {
	    return children.size();
	} 

	public Iterator edges() {
	    return children.iterator();
	} 

	// Net-Manipulation

	public boolean add(Node n) {
	    return children.add(n);
	} 

	public boolean remove(Node n) {
	    return children.remove(n);
	} 

	public Node min() {
	    try {
		Node n = this;
		while (!n.isLeaf())
		    n = (Node) n.edges().next();
		return n;
	    } catch (ClassCastException oops) {
		throw new SuspiciousError("child is no Node: " + oops);
	    } 
	} 

	public Node max() {
	    try {
		Node n = this;
		while (!n.isLeaf())
		    n = (Node) ((GraphNode) n).children.get(((GraphNode) n).children.size() - 1);
		return n;
	    } catch (ClassCastException oops) {
		throw new SuspiciousError("child is no node: " + oops);
	    } 
			
	} 


	public String toString() {
	    return super.toString() + children;
	}

	/**
	 * Recursively visit each sub node. Passes on multiple times
	 * via each possible path.
	 * continues if Predicate is not throwing a VisitedException.
	 */

	/*
	 * public boolean visit(Predicate pred) {
	 * if (passed) return false;                   // already been here on the passing cycle?
	 * if (pred.predicate(this)) return true;      // is predicate destination?
	 * if (visited) return false;                  // visited except if predicate temporarily change this
	 * setPassed(true);                            // have visited
	 * 
	 * for (int i=0;i<nextElement.size();i++)
	 * if ( ((Element)nextElement.elementAt(i)).visit(pred)) {       // already found predicate destination?
	 * pred.predicate(this);               // part of the path
	 * setPassed(false);                   // passing cycle for this Element is over
	 * return true;
	 * }
	 * 
	 * setPassed(false);                           // passing cycle for this Element is over
	 * return false;
	 * }
	 */
    }

}
