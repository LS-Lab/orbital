/*
 * @(#)v_Polygon.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import orbital.math.Point3D;
import java.awt.Color;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * a v_Container containing v_Vertex points.
 * <p>
 * Does only need references to the vertices.</p>
 * @see java.awt.Polygon
 */
public
class v_Polygon extends v_Container {

	/**
	 * parental object which contains this polygon.
	 */
	static v_Object object;		   // XXX: ugly solution: temporary parent

	/**
	 * in which color to display.
	 * Null for transparent polygon leading to the drawing of the edges, only.
	 * Remember that transparency clashes with {@link v_View#polysorted polygon sorting}.
	 * @serial
	 */
	private Color   color;
	/**
	 * serial version of a transparent color (identifier for null).
	 */
	private static final int TRANSPARENT_COLOR = 0xFEFEFEFE;

	// only for sorted backface removal:
	int				zmin, zmax;	   // Minimim..Maximum
	int				xmin, xmax;	   // der X|Y|Z-
	int				ymin, ymax;	   // koordinaten
	int				distance;	 // ( xcentD+ycentD+zcentD )    distanceD to center

	/**
	 * constructs a new empty polygon.
	 */
	public v_Polygon() {}

	/**
	 * constructs a new polygon spaced for n Vertexes.
	 */
	public v_Polygon(int n) {
		super(n);
	}

	/**
	 * constructs a new polygon spaced for n Vertexes
	 * with a particular Color.
	 */
	public v_Polygon(int n, Color color) {
		this(n);
		this.color = color;
	}

	/**
	 * gets the color of the polygon
	 */
	public Color getColor() {
		return color;
	} 

	/**
	 * sets the color of the polygon
	 */
	public void setColor(Color color) {
		this.color = color;
	} 

	/**
	 * Gets the nth Vertex.
	 */
	public v_Vertex getVertex(int n) {
		return (v_Vertex) getComponent(n);
	} 

	/**
	 * Sets the nth Vertex.
	 */
	public void setVertex(v_Vertex c, int n) {
		setComponent(c, n);
	} 


	/**
	 * draws this component at the position.
	 */
	public void draw(v_Graphics g) {
		int nPoints = getComponentCount() + 1;
		int xPoints[] = new int[nPoints];
		int yPoints[] = new int[nPoints];
		int zPoints[] = new int[nPoints];

		for (int v = 0; v < getComponentCount(); v++) {
			v_Vertex vv = (v_Vertex) getComponent(v);
			Point3D  w = vv.getWorldPosition();
			xPoints[v] = w.x;
			yPoints[v] = w.y;
			zPoints[v] = w.z;
		}	 // closed polygon please
		xPoints[getComponentCount()] = xPoints[0];
		yPoints[getComponentCount()] = yPoints[0];
		zPoints[getComponentCount()] = zPoints[0];

		if (color == null) {
			// transparent face, draw edges, only
			g.drawPolygon(xPoints, yPoints, zPoints, nPoints);
		} else {
			g.setColor(color);
			g.fillPolygon(xPoints, yPoints, zPoints, nPoints);
		}

		super.draw(g);
	} 

	public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
		setComponentCount(is.readShort());
		int colori = is.readInt();
		color = colori == TRANSPARENT_COLOR ? null : new Color(colori);

		for (int c = 0; c < getComponentCount(); c++) {
			int iVertex = is.readShort();
			setComponent(object.getVertex(iVertex), c);
		} 

		// do NOT really load the vertexes via super.readExternal(is)
	} 

	public synchronized void writeExternal(ObjectOutput os) throws IOException {
		os.writeShort(getComponentCount());
		os.writeInt(color == null ? TRANSPARENT_COLOR : color.getRGB() & ~0x02000000);	  // FF-->FD as start marker of Colors

		int[] indices = getVertexIndices(object);
		for (int c = 0; c < indices.length; c++) {

			// search the position in the master object table
			os.writeShort(indices[c]);
		} 

		// do NOT really store the vertexes via super.writeExternal(os)
	} 

	/**
	 * Get the indices of our vertexes in the given objects vertexes.
	 * @param obj the object that contains this polygon.
	 * @pre obj.contains(this)
	 */
	public int[] getVertexIndices(v_Object obj) {
		if (!obj.contains(this))
			throw new IllegalArgumentException("the given v_Object must contain this polygon");
		int[] indices = new int[getComponentCount()];
		for (int c = 0; c < getComponentCount(); c++)

			// search the position in the table in obj
			indices[c] = obj.getVertexes().indexOf(getComponent(c));
		return indices;
	} 

	/**
	 * returns whether this Polygon is a backface of a convex polyhedron.
	 * counter-clockwise.
	 * @pre polygon must be part of a convex polyeder
	 *  	&& the (first three) vertices must be arranged counter-clockwise
	 *  	   (if seen from the Viewpoint of (0,0,-z) as, in fact, at load time prior to any transformations).
	 */
	final boolean isBackface() {
		Point3D w1 = getVertex(0).getWorldPosition();
		Point3D w2 = getVertex(1).getWorldPosition();
		Point3D w3 = getVertex(2).getWorldPosition();
		double  x1 = w1.x;
		double  x2 = w2.x;
		double  x3 = w3.x;
		double  y1 = w1.y;
		double  y2 = w2.y;
		double  y3 = w3.y;
		double  z1 = w1.z;
		double  z2 = w2.z;
		double  z3 = w3.z;

		// Punktemenge < 0
		return (x3 * (z1 * y2 - y1 * z2) + y3 * (x1 * z2 - z1 * x2) + z3 * (y1 * x2 - x1 * y2) < 0);
	} 
}
