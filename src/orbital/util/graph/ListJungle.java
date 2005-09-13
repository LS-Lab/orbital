/**
 * @(#)ListJungle.java 0.9 2000/07/15 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util.graph;

import java.util.Iterator;

import java.util.Arrays;

/**
 * Encapsulates a jungle implemented with adjacence lists.
 * A jungle is a directed acyclic graph with exactly one root.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ListJungle extends ListGraph {
    /**
     * the single root node of this jungle.
     * @serial
     */
    protected Node root;
    public ListJungle(GraphNode root) {
	super(root);
    }

    /**
     * Get the single root of this tree.
     * Having single root is a property of jungles.
     */
    public Node getRoot() {
	return root;
    }

    /**
     * Set the single root of this tree.
     * Having single root is a property of jungles.
     */
    public void setRoot(Node root) {
	this.root = root;
    } 

    public Iterator getRoots() {
	return Arrays.asList(new Node[] {root}).iterator();
    } 

    /**
     * set the single root if none is set yet.
     * bound to a single node due to jungle property.
     */
    public boolean addRoot(Node root) {
	if (this.root != null)
	    throw new IllegalStateException("trees can only have a single root");
	setRoot(root);
	return true;
    }
	
    public boolean removeRoot(Node root) {
	if (!this.root.equals(root))
	    return false;
	setRoot(null);
	return true;
    }
}
