/*
 * @(#)v_Object.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.util.Vector;
import orbital.math.Matrix3D;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * a v_Container containing v_Polygons.
 * And additionally v_Vertex points for fast single transformation.
 */
public
class v_Object extends v_Container {

	/**
	 * contains vertices of this object.
	 * @serial
	 */
	private v_Container vertex;

	// Point3D local_origin;

	/**
	 * constructs a new empty object.
	 */
	public v_Object() {
		vertex = new v_Container();
	}

	/**
	 * constructs a new object spaced for
	 * poly v_Polygons with vert v_Vertexes.
	 */
	public v_Object(int poly, int vert) {
		super(poly);
		vertex = new v_Container(vert);
	}

	/**
	 * Gets the nth Polygon.
	 */
	public v_Polygon getPolygon(int n) {
		return (v_Polygon) getComponent(n);
	} 

	/**
	 * Sets the nth Polygon.
	 */
	public void setPolygon(v_Polygon c, int n) {
		setComponent(c, n);
	} 

	/**
	 * Returns the Vertex v_Container.
	 */
	public v_Container getVertexes() {
		return vertex;
	} 

	/**
	 * Gets the nth Vertex.
	 */
	public v_Vertex getVertex(int n) {
		return (v_Vertex) vertex.getComponent(n);
	} 

	/**
	 * Sets the nth Vertex.
	 */
	public void setVertex(v_Vertex c, int n) {
		vertex.setComponent(c, n);
	} 

	/**
	 * Returns the number of vertexes.
	 */
	public int getVertexCount() {
		return vertex.getComponentCount();
	} 


	/**
	 * does not transform components but only Vertexes
	 */
	public void transform(Matrix3D mat) {
		for (int v = 0; v < vertex.getComponentCount(); v++)
			getVertex(v).transform(mat);
	} 

	/* public synchronized void draw(v_Graphics g)  // convex?: */

	public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
		setComponentCount(is.readShort());
		vertex.setComponentCount(is.readShort());

		for (int v = 0; v < vertex.getComponentCount(); v++) {
			setVertex(new v_Vertex(), v);
			getVertex(v).readExternal(is);
		} 

		v_Polygon.object = this;
		for (int c = 0; c < getComponentCount(); c++)
			setComponent(new v_Polygon(), c);

		super.readExternal(is);
		v_Polygon.object = null;
	} 

	public synchronized void writeExternal(ObjectOutput os) throws IOException {
		os.writeShort(getComponentCount());
		os.writeShort(vertex.getComponentCount());

		v_Polygon.object = this;
		vertex.writeExternal(os);
		super.writeExternal(os);
		v_Polygon.object = null;
	} 
}
