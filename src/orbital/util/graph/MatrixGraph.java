/*
 * @(#)MatrixGraph.java 0.7 2000/07/15 Andre Platzer
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
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * This class encapsulates a graph implemented with adjacence matrix.
 * <p>
 * <i><b>Note:</b> due to its prototype state, this class is subject to change</i>.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @TODO: implement a little better
 * @invariants isSquare(edges) && size <= edges.length
 */
public class MatrixGraph implements Graph {
    /**
     * The edges of this graph.
     * edges[i][j] is true if and only if there is an edge from node i to node j.
     * @serial
     */
    protected boolean[][] edges;
    /**
     * The nodes in this graph.
     * @serial
     */
    //@todo unique list
    protected List nodes;
        
    /**
     * The indices of the root nodes of this graph.
     * @serial
     */
    protected Set roots;
        
    /**
     * The number of edges currently used in this graph.
     */
    private int size = 0;
        
    public MatrixGraph(int capacity) {
        this.edges = new boolean[capacity][capacity];
        this.nodes = new ArrayList(capacity);
        this.roots = new HashSet();
    }

    public Iterator getRoots() {
        // get all root nodes
        return new IndirectIterator(roots.iterator(), nodes);
        // alternative implementation (which probably will not work)
        // get all nodes that no edge leades to
        /*return new Iterator() {
          private int j = 0;
          public boolean hasNext() {
          while (j < edges[0].length)
          if (isRoot(j))
          return true;
          else
          j++;
          return false;
          }
          public Object next() {
          while (j < edges[0].length)
          if (isRoot(j))
          return nodes.get(j++);
          else
          j++;
          throw new NoSuchElementException();
          }
          private boolean isRoot(int j) {
          for (int i = 0; i < edges.length; i++)
          if (edges[i][j])
          return false;
          return true;
          }
          public void remove() {
          throw new UnsupportedOperationException();
          }
          };*/
    } 

    public boolean addRoot(Node root) {
        if (roots.contains(root))
            return false;
        GraphNode gn = (GraphNode) root;
        gn.index = size++;
        nodes.add(gn.index, gn);
        roots.add(new Integer(gn.index));
        return true;
    } 

    public boolean removeRoot(Node root) {
        for (Iterator i = roots.iterator(); i.hasNext(); ) {
            int index = ((Integer) i.next()).intValue();
            if (root.equals(nodes.get(index))) {
                i.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Transitive hull operator with matrix multiplication.
     */

    // TODO: implement with ...

    /*
      public MatrixGraph transitiveHull(int depth) {
      boolean[][] e = edges;                    // will contain e^i
      boolean[][] r = e;              // result
      for (int i=2; i<=depth; i++) {
      e = multiply(e,edges);
      r = r.add(e);
      }
      return r;
      }
    */

    public Node createNode() {
        return new GraphNode(null, null);
    }

    public class GraphNode extends KeyValuePair implements Node {
        /**
         * The index of this node in MatrixGraph#nodes
         */
        //FIXME: when to assign?
        protected int index = -1;

        public GraphNode(Object key, Object data) {
            super(key, data);
        }

        public boolean isLeaf() {
            //XXX: return whether there are no true values in e[index]
            return getEdgeCount() == 0;
        } 

        public int getEdgeCount() {
            // return number of true values in e[index]
            int count = 0;
            for (int j = 0; j < edges[index].length; j++)
                if (edges[index][j])
                    count++;
            return count;
        } 

        public Iterator edges() {
            return new Iterator() {
                    private int j = 0;
                    public boolean hasNext() {
                        while (j < edges[index].length)
                            if (edges[index][j])
                                return true;
                            else
                                j++;
                        return false;
                    }
                    public Object next() {
                        while (j < edges[index].length)
                            if (edges[index][j])
                                return nodes.get(j++);
                            else
                                j++;
                        throw new NoSuchElementException();
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
        }

        // Net-Manipulation

        public boolean add(Node n) {
            GraphNode gn = (GraphNode) n;
            if (gn.index < 0)
                gn.index = size++;
            boolean old = edges[index][gn.index];
            edges[index][gn.index] = true;
            if (!nodes.contains(n))
                nodes.add(n);
            return !old;
        } 

        public boolean remove(Node n) {
            GraphNode gn = (GraphNode) n;
            boolean old = edges[index][gn.index];
            edges[index][gn.index] = false;
            return old;
        } 

        public Node min() {
            throw new UnsupportedOperationException();
        } 

        public Node max() {
            throw new UnsupportedOperationException();
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

class IndirectIterator implements Iterator {
    private Iterator indices;
    private List lookup;
    private int lastIndex = -1;
    public IndirectIterator(Iterator indices, List lookup) {
        this.indices = indices;
        this.lookup = lookup;
    }
    public boolean hasNext() {
        return indices.hasNext();
    }
    public Object next() {
        return lookup.get(lastIndex = ((Integer) indices.next()).intValue());
    }
    public void remove() {
        if (lastIndex < 0)
            throw new IllegalStateException();
        Object backup;
        try {
            backup = lookup.remove(lastIndex);
        }
        catch (IndexOutOfBoundsException e) {throw new IllegalStateException(e.getMessage());}
        try {
            indices.remove();
        }
        catch (Exception failed) {
            lookup.add(lastIndex, backup);
            throw (UnsupportedOperationException) new UnsupportedOperationException("not supported by inner iterator").initCause(failed);
        }
                
    }
};
