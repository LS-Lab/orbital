/*
 * @(#)Vertex.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.Color;
import orbital.math.Point3D;
import orbital.math.Matrix3D;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import orbital.math.Real;

/**
 * This class encapsulates the data of a Vertex in a 3D VR-Space.
 * It may be manipulated by transforming it with a Matrix.
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 */
public class v_Vertex implements v_Component {
    private static final long serialVersionUID = 3753082686113956103L;
    /**
     * local coordinates
     * @serial
     */
    //TODO: could do with a float (x|y|z) and Matrix3D extending Matrix.Float for speed and accuracy.
    private Point3D local;

    /**
     * world coordinates after transform
     * @serial
     * @see #transform(Matrix3D)
     */
    private Point3D world;

    /**
     * screen coordinates when projected  VRGraphics!
     */

    // Point2D screen;

    /**
     * constructs a new vertex without a position.
     */
    public v_Vertex() {}

    /**
     * constructs a new vertex at local position.
     */
    public v_Vertex(Point3D local) {
	this.local = local;
    }

    /**
     * constructs a new vertex at x|y|z position.
     */
    public v_Vertex(int x, int y, int z) {
	this(new Point3D(x, y, z));
    }

    /**
     * get local position.
     */
    public Point3D getPosition() {
	return local;
    } 

    /**
     * set new local position.
     */
    public void setPosition(Point3D local) {
	this.local = local;
    } 

    /**
     * set new local position.
     */
    public void setPosition(int x, int y, int z) {
	this.local = new Point3D(x, y, z);
    } 

    /**
     * get world position.
     */
    Point3D getWorldPosition() {
	return world;
    } 

    /**
     * transforms the position of this component.
     */
    public void transform(Matrix3D mat) {	 // faster multiplication of the vector with the matrix
	double lx = local.x, ly = local.y, lz = local.z;
	world = new Point3D();
	world.x = (int) (lx * mat.getDoubleValue(0, 0) + ly * mat.getDoubleValue(0, 1) + lz * mat.getDoubleValue(0, 2) + mat.getDoubleValue(0, 3));
	world.y = (int) (lx * mat.getDoubleValue(1, 0) + ly * mat.getDoubleValue(1, 1) + lz * mat.getDoubleValue(1, 2) + mat.getDoubleValue(1, 3));
	world.z = (int) (lx * mat.getDoubleValue(2, 0) + ly * mat.getDoubleValue(2, 1) + lz * mat.getDoubleValue(2, 2) + mat.getDoubleValue(2, 3));
    } 

    /**
     * draws this component at the position.
     */
    public void draw(v_Graphics g) {
	g.setColor(Color.magenta);
	g.drawPoint(world.x, world.y, world.z);
    } 

    /**
     * load this component from a stream.
     */
    public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
	int x = is.readShort();
	int y = is.readShort();
	int z = is.readShort();
	setPosition(x, y, z);
    } 

    /**
     * store this component into a stream.
     */
    public synchronized void writeExternal(ObjectOutput os) throws IOException {
	os.writeShort(local.x);
	os.writeShort(local.y);
	os.writeShort(local.z);
    } 

    // trial for v_Polygon.store
    public boolean equals(Object obj) {
	if (!(obj instanceof v_Vertex))
	    return false;
	v_Vertex v = (v_Vertex) obj;
	return local.equals(v.local);
    } 
	
    public int hashCode() {
	return local.hashCode();
    }

    public String toString() {
	return local.toString();
    } 

    public static v_Vertex valueOf(String s) throws NumberFormatException {
	if (s.indexOf('.') >= 0)
	    throw new NumberFormatException("integer values expected, not decimals");
	orbital.math.Vector v = (orbital.math.Vector) orbital.math.Values.getDefaultInstance().valueOf(s);
	if (v.dimension() != 3)
	    throw new NumberFormatException("illegal dimension");
	return new v_Vertex((int) ((Real) v.get(0)).doubleValue(), (int) ((Real)v.get(1)).doubleValue(), (int) ((Real)v.get(2)).doubleValue());
    } 
}
