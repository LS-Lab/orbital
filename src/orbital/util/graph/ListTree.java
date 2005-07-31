/**
 * @(#)ListTree.java 0.9 2000/07/15 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util.graph;

import java.util.Iterator;

import java.util.Arrays;
import orbital.util.StreamMethod;

/**
 * Encapsulates a tree implemented with adjacence lists.
 * For a tree (graph with exactly one incoming node (parent), except for root)
 * neighbours are one parent (stored at index 0), and several children.
 * 
 * <pre>
 *      parent
 *        |
 *       this
 *      /    \
 * child1    child2
 * </pre>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ListTree extends ListJungle {
    public ListTree(TreeNode root) {
	super(root);
    }

    public void setRoot(Node root) {
	if (!(root instanceof TreeNode))
	    throw new IllegalArgumentException("can only handle nodes of type " + TreeNode.class);
	((TreeNode) root).setParent(null);
	super.setRoot(root);
    }

    /**
     * Returns an iterator for preorder traversal of this tree.
     */
    public Iterator preorder() {
	return new StreamMethod(true) {
		public void runStream() {
		    visit(getRoot());
		} 
		protected final void visit(Node node) {
		    resumedReturn(node);
		    for (Iterator i = node.edges(); i.hasNext(); )
			visit((Node) i.next());
		} 
	    }.apply();
    } 

    /**
     * Returns an iterator for inorder traversal of this tree.
     */
    public Iterator inorder() {
	return new StreamMethod(true) {
		public void runStream() {
		    visit(getRoot());
		} 
		protected final void visit(Node node) {
		    if (node.isLeaf()) {
			resumedReturn(node);
			return;
		    } 
		    for (Iterator i = node.edges(); i.hasNext(); ) {
			visit((Node) i.next());
			if (i.hasNext())
			    resumedReturn(node);
		    } 
		} 
	    }.apply();
    } 

    /**
     * Returns an iterator for postorder traversal of this tree.
     */
    public Iterator postorder() {
	return new StreamMethod(true) {
		public void runStream() {
		    visit(getRoot());
		} 
		protected final void visit(Node node) {
		    for (Iterator i = node.edges(); i.hasNext(); )
			visit((Node) i.next());
		    resumedReturn(node);
		} 
	    }.apply();
    } 

    //TODO: implement levelorder()   as  breadth-first-search
	
    //XXX: print as a tree, not as a list!
    public String toString() {
	StringBuffer sb = new StringBuffer();
	for (Iterator i = preorder(); i.hasNext(); )
	    sb.append(i.next() + (i.hasNext() ? "," : ""));
	return getClass().getName() + "[" + sb.toString() + "]";
    }

    public static class TreeNode extends ListGraph.GraphNode {

	/**
	 * The reference to the node's parent in the tree.
	 */
	protected TreeNode parent;

	public TreeNode(Object key, Object data) {
	    super(key, data);
	    parent = null;
	}

	public TreeNode getParent() {
	    return parent;
	} 

	// Net-Manipulation
	public void setParent(TreeNode parent) {
	    this.parent = parent;
	} 

	/**
	 * Add a node to this node's children.
	 * Set this node the parent of n.
	 */
	public boolean add(Node n) {
	    ((TreeNode) n).setParent(this);
	    return children.add(n);
	} 

	/**
	 * Remove a node from this node's children. Remove this node from the children.
	 * null node will now be the its parent.
	 */
	public boolean remove(Node n) {
	    ((TreeNode) n).setParent(null);
	    return children.remove(n);
	} 
    }
}
