/*
 * @(#)ChartModel.java 0.9 1999/03/16 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import orbital.math.Matrix;
import orbital.logic.functor.Function;

import orbital.math.Arithmetic;
import orbital.math.Scalar;
import orbital.math.Real;
import orbital.math.Vector;
import orbital.math.Values;

import orbital.math.functional.Operations;
import orbital.math.MathUtilities;

import java.io.Serializable;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import orbital.util.Setops;

import orbital.util.Utility;

/**
 * Containement class that represents a model for chart data.
 * <p>
 * Graphs in the chart data are objects like, for example:
 * <ul>
 *   <li>a set of points represented as a {@link orbital.math.Matrix}</li>.
 *   <li>a function represented as a {@link orbital.logic.functor.Function},
 *     or even {@link orbital.math.functional.Function}</li>.
 * </ul>
 * </p>
 * <p>
 * Each graph can have associated an attribute-map that specifies
 * attributes for displaying it.
 * Which keys are recognized and which types of graphs are expected is specific to the
 * View of this Model.
 * </p>
 * 
 * @structure aggregate graphs:java.util.List<ChartModel.Entry> unidirectional
 * @structure aggregate range:Range n-sized unidirectional
 * @structure aggregate scale:Vector n-sized unidirectional
 * @version 0.9, 1999/03/16
 * @author  Andr&eacute; Platzer
 */
public class ChartModel implements Serializable {

    /**
     * version of this class for versioning with serialization and deserialization.
     */
    private static final long serialVersionUID = 4576261898967344924L;

    private static final int DEFAULT_PRECISION_POINTS = 200;

    /**
     * The List of graphs to be displayed.
     * This list contains objects of kind <code>ChartModel.Entry</code>.
     * @serial
     */
    private List/*<Entry>*/ graphs;

    /**
     * The Range to be displayed visibly.
     * @serial
     */
    private Range			  range = null;

    /**
     * The scale vector.
     * A component value of v<sub>i</sub> says that every v<sub>i</sub> units a mark is drawn on the Chart.
     * @serial
     */
    private Vector		  scale = null;

    /**
     * Whether rainbow colors are used for graphs that have no color setting.
     * @serial
     */
    private boolean		  rainbow = false;
    /**
     * the last rainbow color used (not yet reduced modulo number of colors).
     * @serial
     */
    private int				  rainbowColor = 0;

    public ChartModel() {
	graphs = new ArrayList/*<Entry>*/();
    }

    /**
     * Get the number of graphs displayed.
     */
    public int getGraphCount() {
	return graphs.size();
    } 

    /**
     * Get the List of graphs to be displayed.
     * This list contains objects of kind <code>ChartModel.Entry</code>.
     * @see #graphList()
     * @see #attributeList()
     */
    public List/*<Entry>*/ getGraphs() {
	return Collections.unmodifiableList(graphs);
    } 

    /**
     * Returns an iterator over the elements in this chart model in proper sequence.
     * 
     * @return an iterator over the elements in this chart model in proper sequence.
     */
    public Iterator iterator() {
	return Setops.unmodifiableIterator(graphs.iterator());
    } 

    /**
     * Get the scale.
     * On the axis<sub>i</sub>, marks will be placed with distance scale<sub>i</sub>.
     */
    public Vector getScale() {
	return scale;
    } 

    /**
     * Set the scale.
     * On the axis<sub>i</sub>, marks will be placed with distance scale<sub>i</sub>.
     */
    public void setScale(Vector scale) {
	if (((Comparable) Operations.inf.apply(scale)).compareTo(Values.ZERO) <= 0)
	    throw new IllegalArgumentException("Scales must neither negative nor 0");
	Vector old = this.scale;
	this.scale = scale;
	propertyChangeListeners.firePropertyChange("scale", old, scale);
    } 

    /**
     * Set the scale such that at least a specified number of marks is visible on each axis.
     */
    public void setScaleMarks(double marks) {
	Vector scale = Values.getInstance(range.min.dimension());
	for (int i = 0; i < scale.dimension(); i++)
	    //@todo round to get more regular results. Perhaps n scales at the shortest axis, and the same scales at the longer axes
	    scale.set(i, Values.valueOf(MathUtilities.ceily((((Number) range.max.get(i)).doubleValue() - ((Number) range.min.get(i)).doubleValue()) / marks, MathUtilities.precisionFor(range.getLength(i), 0.1))));
	setScale(scale);
    } 

    /**
     * Get the visible range to be displayed.
     */
    public Range getRange() {
	return range;
    } 

    /**
     * Set the visible range to be displayed.
     */
    public void setRange(Range range) {
	Range old = this.range;
	this.range = range;
	propertyChangeListeners.firePropertyChange("range", old, range);
    } 

    /**
     * Whether rainbow colors are used for graphs that have no color setting.
     */
    public boolean isRainbow() {
	return rainbow;
    } 

    /**
     * Set rainbow colors for all graphs added to display.
     * A new color will be chosen for each graph added except if its color is explicitly specified in the attributes.
     * @see #nextRainbowColor()
     */
    public void setRainbow(boolean rainbowize) {
	boolean old = this.rainbow;
	this.rainbow = rainbowize;
	propertyChangeListeners.firePropertyChange("rainbow", old, rainbow);
    } 


    /**
     * calculate next possible rainbow color as hue in HSB-Color values.
     * @see <a href="Streuende maxiperiodische Folgen.nb">Streuende maxiperiodische Folgen</a>
     * @see #isRainbow()
     */
    private float nextRainbowColor() {
	// TODO: calculate optimal m, s values
	// float rainbowColor = (rainbowColor+.3107f)%1.0f,		// intuitive
	rainbowColor = (rainbowColor + rainbowStride) % rainbowModulus;
	return rainbowColor / (float) rainbowModulus;
    } 
    private static final int			rainbowModulus = 64;
    private static final int			rainbowStride = 87;


    /**
     * @serial
     */
    private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeListeners.addPropertyChangeListener(listener);
    } 
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeListeners.removePropertyChangeListener(listener);
    } 
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	propertyChangeListeners.addPropertyChangeListener(propertyName, listener);
    } 
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	propertyChangeListeners.removePropertyChangeListener(propertyName, listener);
    } 


    /**
     * Add a new graph to be displayed.
     * Attributes for displaying this graph are contained in the associated <code>Map</code>.
     * @param graph the graph to be displayed in the chart.
     * @param attributes map of attributes how graph should be displayed. Use <code>null</code> if for no attributes.
     */
    public void add(Object graph, Map attributes) {
	if (rainbow) {
	    if (attributes == null)
		attributes = new HashMap();
	    if (!attributes.containsKey("color"))
		attributes.put("color", Color.getHSBColor(nextRainbowColor(), 0.9f, 0.8f));
	} 
	graphs.add(new Entry(graph, attributes));
	propertyChangeListeners.firePropertyChange("graphs", null, graphs);	   // forget about remembering the old value
    } 

    /**
     * Add a new graph to be displayed without attributes.
     */
    public void add(Object graph) {
	add(graph, null);
    } 

    /**
     * Remove a graph.
     */
    public void remove(int index) {
	graphs.remove(index);
	propertyChangeListeners.firePropertyChange("graphs", null, graphs);	   // forget about remembering the old value
    } 

    /**
     * Remove all graphs.
     */
    public void removeAll() {
	graphs.clear();
	propertyChangeListeners.firePropertyChange("graphs", null, graphs);	   // forget about remembering the old value
    } 

    /**
     * Get the List of solely graphs to be displayed.
     * This list contains objects of kind <code>Object</code>.
     * Unlike getGraphs(), no <code>ChartModel.Entry</code> objects are returned,
     * but only the <code>ChartModel.Entry.graph</code> part.
     * @see #getGraphs()
     */
    public List graphList() {
	List list = new ArrayList();
	for (Iterator i = graphs.iterator(); i.hasNext(); ) {
	    Entry e = (Entry) i.next();
	    list.add(e.getGraph());
	} 
	return Collections.unmodifiableList(list);
    } 

    /**
     * Get the List of solely attributes to be displayed.
     * This list contains objects of kind <code>Map</code>.
     * Unlike getGraphs(), no <code>ChartModel.Entry</code> objects are returned.
     * but only the <code>ChartModel.Entry.attributes</code> part.
     * @see #getGraphs()
     */
    public List attributeList() {
	List list = new ArrayList();
	for (Iterator i = graphs.iterator(); i.hasNext(); ) {
	    Entry e = (Entry) i.next();
	    list.add(e.getAttributes());
	} 
	return Collections.unmodifiableList(list);
    } 


    /**
     * Sets the scale and range parameters automatically,
     * according to the graphs displayed.
     * <p>
     * The range is determined by the minima / maxima and the scale
     * is set to default.
     */
    public void setAutoScaling() {
	Vector min = null;
	Vector max = null;

	// find minimum and maximum (x|y)-values from matrices
	for (Iterator i = graphs.iterator(); i.hasNext(); ) {
	    Entry  e = (Entry) i.next();
	    Object o = e.getGraph();
	    if (o instanceof Matrix) {
		Matrix A = (Matrix) o;

		// enlarge min vector if this points Matrix is broader, preserve values
		if (min == null)
		    min = (Vector) A.getRow(0).clone();
		else
		    while (A.dimension().width > min.dimension())
			min = min.insert(A.get(0, min.dimension()));

		// enlarge max vector if this points Matrix is broader, preserve values
		if (max == null)
		    max = (Vector) A.getRow(0).clone();
		else
		    while (A.dimension().width > max.dimension())
			max = max.insert(A.get(0, min.dimension()));

		// adapt minimum and maximum values
		//@todo rewrite pure functional
		for (int c = 0; c < A.dimension().width; c++) {
		    Vector     col = A.getColumn(c);
		    Arithmetic m = (Arithmetic) Operations.inf.apply(col);
		    if (((Comparable) m).compareTo(min.get(c)) < 0)
			min.set(c, m);
		    Arithmetic M = (Arithmetic) Operations.sup.apply(col);
		    if (((Comparable) M).compareTo(max.get(c)) > 0)
			max.set(c, M);
		} 
	    }
	} 

	// use default x-values otherwise (when no Matrix wass found)
	if (min == null || max == null) {
	    min = Values.valueOf(new double[] {-4, 0});
	    max = Values.valueOf(new double[] {+4, 0});
	}

	// for getSpecifiedPrecision to work, we need to set this right now
	range = new Range(min, max);

	// find minimum and maximum y-values from functions
	for (Iterator i = graphs.iterator(); i.hasNext(); ) {
	    Entry  e = (Entry) i.next();
	    Object o = e.getGraph();
	    if (o instanceof Function) {
		Function f = (Function) o;
		double precision = getSpecifiedPrecision(e.getAttributes(), DEFAULT_PRECISION_POINTS);
		//@todo How to determine "bound" min f(x) and max f(x) that do not consider infinite singularities?
		for (double t = ((Real) min.get(0)).doubleValue(); t < ((Real)max.get(0)).doubleValue(); t += precision) {
		    Arithmetic v = (Arithmetic) f.apply(Values.valueOf(t + precision));
		    if (!(v instanceof Scalar))
			// can only handle functions R->R, here.
			break;
		    // adapt minimum and maximum y-values
		    if (((Comparable) v).compareTo(min.get(1)) < 0)
			min.set(1, v);
		    if (((Comparable) v).compareTo(max.get(1)) > 0)
			max.set(1, v);
		} 
	    }
        }

	range = new Range(min, max);

	// standard scale: 10 marks
	setScaleMarks(10);
	setRange(new Range(min.subtract(scale.scale(Values.valueOf(.3))), max.add(scale.scale(Values.valueOf(.3)))));
    } 

    double getSpecifiedPrecision(Map attribs, int defaultPrecisionPoints) {
	int precisionPoints = defaultPrecisionPoints;
	if (attribs != null) {
	    Number prec = (Number) attribs.get("precisionPoints");
	    if (prec != null) 
		precisionPoints = prec.intValue();
	}
	double precision = (((Real)range.max.get(0)).doubleValue() - ((Real)range.min.get(0)).doubleValue()) / precisionPoints;
	if (attribs != null) {
	    Number prec = (Number) attribs.get("precision");
	    if (prec != null)
		precision = prec.doubleValue();
    	}
    	return precision;
    }

    /**
     * The entries of a ChartModel's List of displayed graphs.
     * 
     * @structure aggregate graph:java.lang.Object unidirectional
     * @structure aggregate attributes:java.util.Map unidirectional
     * 
     * @stereotype &laquo;Structure&raquo;
     * @version 0.9, 1999/03/23
     * @author  Andr&eacute; Platzer
     */
    public class Entry implements Serializable {
	private static final long serialVersionUID = -3709330542731558305L;
	/**
	 * @serial
	 */
	private Object graph;

	/**
	 * @serial
	 */
	private Map	 attributes;
	public Entry(Object graph, Map attributes) {
	    this.graph = graph;
	    this.attributes = attributes;
	}
	Entry() {
	    this(null, null);
	}

	public Object getGraph() {
	    return graph;
	} 
	public void setGraph(Object graph) {
	    this.graph = graph;
	} 

	public Map getAttributes() {
	    return attributes;
	} 
	public void setAttributes(Map attributes) {
	    this.attributes = attributes;
	} 

	public boolean equals(Object o) {
	    if (o instanceof Entry) {
		Entry e = (Entry) o;
		return Utility.equals(getGraph(), e.getGraph())
		    && Utility.equals(getAttributes(), e.getAttributes());
	    } 
	    return false;
	} 

	public int hashCode() {
	    return Utility.hashCode(getGraph()) ^ Utility.hashCode(getAttributes());
	} 
    }
}
