/**
 * @(#)MatrixGraph.java 0.9 2001/05/23 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import dkfz.collections.graph.Graph;
import dkfz.collections.graph.Vertex;
import dkfz.collections.graph.Edge;
import dkfz.collections.graph.GraphListener;
import dkfz.collections.graph.GraphFactory;

import dkfz.collections.graph.GraphEvent;

import java.io.Serializable;

import java.util.List;
import java.util.Set;
import java.util.Collection;

import orbital.math.Matrix;
import orbital.logic.functor.Predicate;
import java.awt.Dimension;

import orbital.math.Arithmetic;
import orbital.math.Normed;
import orbital.math.Real;

import orbital.util.Setops;
import orbital.math.Scalar;
import orbital.math.Values;

import java.util.Iterator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class encapsulates a graph implemented with an adjacence matrix.
 * <p>
 * An adjacence matrix M=(m<sub>i,j</sub>) contains the edge leading from i to j at m<sub>i,j</sub>,
 * or a value of <code>null</code> indicating that there is no such edge.</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @invariant edges.isSquare() && edges.dimension().height >= nodes.size()
 */
public class MatrixGraph implements Graph, Serializable {
    private static final long serialVersionUID = -358719334874320565L;
    /**
     * The edges of this graph.
     * edges[i][j] is <code>null</code> if and only if there is no edge from node i to node j.
     * So the first index specifies the from-vertex, and the second the to-vertex.
     * <p>
     * Note that this matrix may have a higher capacity than this graph has an actual size.
     * That is for performance issues, the matrix will be created with slightly higher dimension
     * capacities than the current number of vertices suspects.
     * However, the size, i.e. the current number of vertices is available as <code>nodes.size()</code>.</p>
     * @serial
     */
    protected Matrix edges;
    /**
     * The nodes in this graph.
     * @serial
     * @todo unique list
     */
    protected List nodes;

    /**
     * The current collection of graph listeners.
     * @serial
     */
    private Collection listeners = new LinkedList();

    public MatrixGraph() {
        nodes = new ArrayList();
        edges = Values.getDefault().newInstance(0, 0);
    }

    public MatrixGraph(int capacity) {
        nodes = new ArrayList(capacity);
        edges = unconnectedMatrix(capacity, capacity);
    }

    public MatrixGraph(Collection vertices, Collection edges) {
	this(vertices.size());
	for (Iterator i = vertices.iterator(); i.hasNext(); )
	    add((Vertex) i.next());
	for (Iterator i = edges.iterator(); i.hasNext(); )
	    add((Edge) i.next());
    }

    
    public boolean equals(Object o) {
    	//@todo what about a ListGraph that equals this? Would that be too inefficient? What about same hashCodes, then?
    	if (!(o instanceof MatrixGraph))
	    return false;
    	MatrixGraph b = (MatrixGraph) o;
    	return nodes.equals(b.nodes) && edges.equals(b.edges);
    }

    public int hashCode() {
    	return nodes.hashCode() ^ edges.hashCode();
    }

    public boolean add(Edge e) {
	int i = ((MatrixVertex) e.getFromVertex()).getIndex();
	int j = ((MatrixVertex) e.getToVertex()).getIndex();
	boolean was = edges.get(i, j) != null;
	edges.set(i, j, (Arithmetic) e);
	fireGraphChange(new GraphEvent(this, GraphEvent.ACTION_ADD, e));
	return was;
    }
    public boolean add(Vertex v) {
    	if (!(v instanceof MatrixVertex))
	    throw new ClassCastException(MatrixVertex.class + " expected");
	if (nodes.contains(v))
	    return false;
	int index = nodes.size();
	nodes.add(index, v);
	if (index >= edges.dimension().height) {
	    // enlarge matrix
	    //@todo optimize enlarge with more than one additional row and column
	    if (edges.dimension().equals(new Dimension(0, 0)))
		// explicit construction since in this case, insertColumns does not do anything, so insertRows will result in the wrong size, as well
		edges = Values.getDefault().newInstance(1, 1);
	    else {
		edges.insertColumns(index, unconnectedMatrix(edges.dimension().height, 1));
		edges.insertRows(index, unconnectedMatrix(1, edges.dimension().width));
	    }
	}
	((MatrixVertex) v).setIndex(index);
	fireGraphChange(new GraphEvent(this, GraphEvent.ACTION_ADD, v));
	return true;
    }
    public boolean remove(Edge e) {
	int i = ((MatrixVertex) e.getFromVertex()).getIndex();
	int j = ((MatrixVertex) e.getToVertex()).getIndex();
	if (edges.get(i, j) == null)
	    return false;
	edges.set(i, j, null);
	fireGraphChange(new GraphEvent(this, GraphEvent.ACTION_REMOVE, e));
	return true;
    }
    public boolean remove(Vertex v) {
	int index = ((MatrixVertex) v).getIndex();
	try {
	    nodes.remove(index);
    	}
    	catch (IndexOutOfBoundsException x) {return false;}
    	// shrink edges in any case such that no holes appear in the matrix
	edges.removeColumn(index);
	edges.removeRow(index);
	fireGraphChange(new GraphEvent(this, GraphEvent.ACTION_REMOVE, v));
	return true;
    }
    public Collection getVertices() {
	return Collections.unmodifiableList(nodes);
    }
    public Collection getEdges() {
	return Collections.unmodifiableList(filter(connected, edges.iterator()));
    }
    public Collection getToEdges(Vertex v) {
	return Collections.unmodifiableList(filter(connected, edges.getColumn(((MatrixVertex) v).getIndex()).subVector(0, nodes.size() - 1).iterator()));
    }
    public Collection getFromEdges(Vertex v) {
	return Collections.unmodifiableList(filter(connected, edges.getRow(((MatrixVertex) v).getIndex()).subVector(0, nodes.size() - 1).iterator()));
    }
    public Collection getAdjacentEdges(Vertex v) {
	return Collections.unmodifiableCollection(Setops.union(getFromEdges(v), getToEdges(v)));
    }
	
    public void addGraphListener(GraphListener l) {
    	synchronized(listeners) {
	    listeners.add(l);
	}
    }
    public void removeGraphListener(GraphListener l) {
    	synchronized(listeners) {
	    listeners.remove(l);
	}
    }
    protected void fireGraphChange(GraphEvent e) {
    	Collection s;
    	synchronized(listeners) {
    	    s = new LinkedList(listeners);
    	}
    	Iterator it = s.iterator();
    	while (it.hasNext()) {
    	    GraphListener gl = (GraphListener) it.next();
    	    gl.graphChanged(e);
    	}
    }
    
    public GraphFactory getGraphFactory() {
    	return graphFactory;
    }
    public void setGraphFactory(GraphFactory factory) {
    	this.graphFactory = factory;
    }
    private GraphFactory graphFactory = new Factory();
    public static class Factory implements GraphFactory, Serializable {
        private static final long serialVersionUID = -4945991972342370334L;
        public Vertex createVertex() {
	    return new MatrixVertex();
    	}
        public Vertex createVertex(Object o) {
	    Vertex v = new MatrixVertex();
	    v.setObject(o);
	    return v;
    	}
        public Edge createEdge(Vertex from, Vertex to) {
	    return new ArithmeticEdge(from, to);
    	}
        public Edge createEdge(Vertex from, Vertex to, Object o) {
	    Edge e = new ArithmeticEdge(from, to);
	    e.setObject(o);
	    return e;
    	}
        public Graph createGraph() {
	    return new MatrixGraph();
    	}
        public Graph createGraph(Collection vertices, Collection edges) {
	    return new MatrixGraph(vertices, edges);
    	}
    	protected Graph newInstance(int capacity) {
	    return new MatrixGraph(capacity);
    	}
    }
	
    static class MatrixVertex implements Vertex, Serializable {
	private static final long serialVersionUID = -5862202923663587579L;
	/**
	 * The content object.
	 * @serial
	 */
	private Object object = null;
	/**
	 * The index of this vertex in the link matrix.
	 * @serial
	 */
	private int	   index = -1;
	public Object getObject() {
	    return object;
	}
	public void setObject(Object o) {
	    this.object = o;
	}
		
	protected final int getIndex() {
	    return index;
	}
	protected final void setIndex(int index) {
	    this.index = index;
	}
	public String toString() {
	    return object + "";
	}
    }
    
    /**
     * An Edge that behaves arithmetic if only its content is.
     * Note however that the objects returned will not be arithmetic edges, again, but
     * depend upon the content's arithmetic types.
     * @structure forward Arithmetic to getObject()
     */
    static class ArithmeticEdge extends dkfz.collections.graph.EdgeImpl implements Arithmetic, Serializable {
	private static final long serialVersionUID = -7539784203267770666L;
	public ArithmeticEdge(Vertex from, Vertex to) {
	    super(from, to);
	}
	public ArithmeticEdge(Vertex from, Vertex to, Object o) {
	    super(from, to);
	    setObject(o);
	}
		
	public boolean equals(Object o, Real tolerance) {
	    return getDelegatee().equals(o, tolerance);
	}

	public Arithmetic zero() {
	    return convert(getDelegatee().zero());
	}

	public Arithmetic one() {
	    return convert(getDelegatee().one());
	}

	public Real norm() {
	    return ((Normed) getObject()).norm();
	}			
        public Arithmetic add(Arithmetic b) throws ArithmeticException {
	    return convert(getDelegatee().add(b));
    	}
        public Arithmetic minus() throws ArithmeticException {
	    return convert(getDelegatee().minus());
    	}
        public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
	    return convert(getDelegatee().subtract(b));
    	}
        public Arithmetic multiply(Arithmetic b) throws ArithmeticException, UnsupportedOperationException {
	    return convert(getDelegatee().multiply(b));
    	}
        public Arithmetic scale(Arithmetic b) throws ArithmeticException {
	    return convert(getDelegatee().scale(b));
    	}
        public Arithmetic inverse() throws ArithmeticException, UnsupportedOperationException {
	    return convert(getDelegatee().inverse());
    	}
        public Arithmetic divide(Arithmetic b) throws ArithmeticException, UnsupportedOperationException {
	    return convert(getDelegatee().divide(b));
    	}
        public Arithmetic power(Arithmetic b) throws ArithmeticException, UnsupportedOperationException {
	    return convert(getDelegatee().power(b));
    	}
    	private final Arithmetic getDelegatee() {
	    return (Arithmetic) getObject();
    	}
    	private final Arithmetic convert(Arithmetic v) {
	    return v;
    	}
    }

    /**
     * Predicate that is true of an edge if it is a connecting edge (i.e. not <code>null</code>).
     */
    private static final Predicate connected = new Predicate() {
	    public boolean apply(Object o) {
    		return o != null;
	    }
	};
    
    /**
     * @see orbital.util.Setops#select(Function, Collection, Predicate)
     */
    private static List filter(Predicate p, Iterator i) {
	return (List) Setops.createSelection(null, p, null, true).apply(i);
    	/*List l = new LinkedList();
	  while (i.hasNext()) {
	  Object o = i.next();
	  if (p.apply(o))
	  l.add(o);
	  }
	  return l;*/
    }
	
    /**
     * Create a height&times;width edge matrix that is totally unconnected, that is
     * it contains no single edge.
     */
    private static Matrix unconnectedMatrix(int height, int width) {
	Matrix unconnectedMatrix = Values.getDefault().newInstance(height, width);
	for (int i = 0; i < unconnectedMatrix.dimension().height; i++)
	    for (int j = 0; j < unconnectedMatrix.dimension().width; j++)
		unconnectedMatrix.set(i, j, null);
	return unconnectedMatrix;
    }
	

    /**
     * Get the converse graph view of g.
     * The converse graph of g=(V,E) is the graph g<sup>C</sup>=(V,E<sup>C</sup>) with
     * E<sup>C</sup> := {(j,i) &brvbar; (i,j)&isin;E}.
     * <p>
     * The returned graph is backed by this graph, so changes in the returned graph are reflected in this graph, and vice-versa.</p>
     * @todo move to another utility class for graphs.
     */
    public static final Graph converseGraph(Graph g) {
	return new ConverseGraph(g);
    }
    private static class ConverseGraph implements Graph, Serializable {
    	private static final long serialVersionUID = -2865767173395445227L;
    	protected final Graph graph;
    	public ConverseGraph(Graph g) {
	    this.graph = g;
    	}
    	protected Edge converse(Edge e) {
	    Edge ec = new dkfz.collections.graph.EdgeImpl(e.getToVertex(), e.getFromVertex());
	    ec.setObject(e.getObject());
	    return ec;
    	}
    	protected Collection converseAll(Collection edges) {
	    Collection r = Setops.newCollectionLike(edges);
	    for (Iterator i = edges.iterator(); i.hasNext(); )
		r.add(converse((Edge) i.next()));
	    return r;
    	}

        public boolean add(Edge e) {
	    return graph.add(converse(e));
    	}
        public boolean add(Vertex v) {
	    return graph.add(v);
    	}
        public boolean remove(Edge e) {
	    return graph.remove(converse(e));
    	}
        public boolean remove(Vertex v) {
	    return graph.remove(v);
    	}
        public Collection getVertices() {
	    return graph.getVertices();
    	}
        public Collection getEdges() {
	    return converseAll(graph.getEdges());
    	}
        public Collection getToEdges(Vertex v) {
	    return converseAll(graph.getFromEdges(v));
    	}
        public Collection getFromEdges(Vertex v) {
	    return converseAll(graph.getToEdges(v));
    	}
        public Collection getAdjacentEdges(Vertex v) {
	    return converseAll(graph.getAdjacentEdges(v));
    	}
        public void addGraphListener(GraphListener l) {
	    graph.addGraphListener(l);
    	}
        public void removeGraphListener(GraphListener l) {
	    graph.removeGraphListener(l);
    	}
        public GraphFactory getGraphFactory() {
	    return graph.getGraphFactory();
        }
    }
}
