/*
 * @(#)v_View.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import orbital.math.Matrix3D;
import java.awt.*;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * a View of a v_World.
 */
public class v_View extends v_World {
    private static final long serialVersionUID = 4255272902866916881L;

    /**
     * configure: general view options.
     * determines whether polygons are z-sorted before display.
     * <p>
     * <b>Pre:</b> for polygon sorting, all polygon must be part of a convex polyeder
     *  	&& the vertices of a polygon must be arranged counter-clockwise
     *  	   (if seen from the fix Viewpoint of (0,0,-z) as, in fact, at load time prior to any transformations).
     * </p>
     * @see v_Polygon#isBackface()
     */
    public static boolean	  polysorted = false;

    /**
     * configure: general view options.
     * determines whether everything is drawn on a ZBuffer.
     */
    public static boolean	  z_buffered = false;

    /**
     * excerpt: list of polygons to display
     * @serial
     */
    protected v_PolygonList   polylist = null;

    /**
     * the current total transformation
     * @serial
     */
    protected Matrix3D		  transformation = new Matrix3D();

    /**
     * whether the content of this view changed due to a move.
     * @serial
     */
    private boolean			  changed = true;

    /**
     * constructs a new empty view.
     */
    public v_View() {}

    /**
     * constructs a new view spaced for n v_Objects.
     */
    public v_View(int n) {
	super(n);
    }

    /**
     * draws this component at the position.
     */
    public void draw(v_Graphics g) {
	super.draw(g);
    } 

    /**
     * initializes a new sortedPolygonList if is polysorted.
     * new with maximum size for all existing Polygons.
     */
    public void initPolygonList() {
	if (!polysorted) {
	    polylist = null;
	    return;
	} 
	int cPolygon = 0;
	for (int iObject = 0; iObject < getComponentCount(); iObject++)
	    cPolygon += getObject(iObject).getComponentCount();
	polylist = new v_PolygonList(cPolygon);
    } 

    /**
     * moves the view with a particular additional movement.
     * Assumes that this movement will change positions.
     * <p>This method will concat the matrix to the current transformation.</p>
     */
    public void move(Matrix3D mv) {
	transformation = new Matrix3D(transformation.multiply(mv));
	changed = true;
    } 

    /**
     * returns the current transformation for change.
     * Assumes that this transformation will be changed.
     */
    public Matrix3D move() {
	changed = true;
	return transformation;
    } 

    /**
     * updates view's positions by moving if move changed at all.
     */
    public void update() {
	if (changed)
	    transform(transformation);
	if (polylist != null) {
	    polylist.makelist(this);
	    polylist.z_sort();
	    polylist.swap_necessaries();
	} 
	changed = false;
    } 

    /**
     * displays view and updates if necessary.
     */
    public void display(v_Graphics g) {
	update();

	if (polylist != null)
	    polylist.draw(g);
	else
	    draw(g);
    } 

    public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
	int  reserved = is.readInt();
	byte id = is.readByte();
	if (id != 0x3D)
	    throw new IOException("wrong file for 3DVector format *.vec");
	short version = is.readShort();
	short headerSize = is.readShort();
	short objectSize = is.readShort();
	short polygonSize = is.readShort();
	polysorted = is.readBoolean();
	z_buffered = is.readBoolean();
	int objectOffset = is.readInt();

	is.skipBytes(objectOffset - 19);
	super.readExternal(is);
    } 

    public synchronized void writeExternal(ObjectOutput os) throws IOException {
	int reserved = 0x12345678;
	os.writeInt(reserved);
	os.writeByte(0x3D);
	short version = 0x0109;
	os.writeShort(version);
	short headerSize = 0x13;
	os.writeShort(headerSize);
	short objectSize = 0x04;
	os.writeShort(objectSize);
	short polygonSize = 0x06;
	os.writeShort(polygonSize);
	os.writeBoolean(polysorted);
	os.writeBoolean(z_buffered);
	int objectOffset = 19;
	os.writeInt(objectOffset);

	for (int i = 0 ; i < objectOffset - 19; i++)
	    os.write(0);
	super.writeExternal(os);
    } 
}
