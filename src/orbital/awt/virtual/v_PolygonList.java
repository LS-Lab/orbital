/*
 * @(#)v_PolygonList.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import orbital.math.Point3D;
import java.util.Vector;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * a v_Container containing visible v_Polygons temporarily for sorted draw.
 */
class v_PolygonList extends v_Container {
    private static final long serialVersionUID = -6082301011865465076L;

    /**
     * constructs a new empty polygonlist.
     */
    public v_PolygonList() {}

    /**
     * constructs a new polygonlist spaced for n Polygons.
     */
    public v_PolygonList(int n) {
	super(n);
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

    public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
	throw new UnsupportedOperationException();
    } 

    public synchronized void writeExternal(ObjectOutput os) throws IOException {
	throw new UnsupportedOperationException();
    } 



    /**
     * simply swaps two polygons.
     */
    private void swapPolygons(int A, int B) {
	v_Polygon t = getPolygon(A);
	setPolygon(getPolygon(B), A);
	setPolygon(t, B);
    } 

    /**
     * takes polygons that necessitate to be displayed from the world.
     */
    public void makelist(v_World world) {
	getComponents().removeAllElements();
	for (int iObject = 0; iObject < world.getComponentCount(); iObject++) {
	    v_Object object = world.getObject(iObject);

	    for (int iPolygon = 0; iPolygon < object.getComponentCount(); iPolygon++) {
		v_Polygon polygon = object.getPolygon(iPolygon);

		if (!polygon.isBackface()) {

		    // Find maximum & minimum coordinates for polygon:  (for overlap)
		    int pxmax = Integer.MIN_VALUE;	  // Initialize all mins & maxes
		    int pxmin = Integer.MAX_VALUE;	  // to highest and lowest
		    int pymax = Integer.MIN_VALUE;	  // possible values
		    int pymin = Integer.MAX_VALUE;
		    int pzmax = Integer.MIN_VALUE;
		    int pzmin = Integer.MAX_VALUE;
		    for (int iVertex = 0; iVertex < polygon.getComponentCount(); iVertex++) {
			Point3D w = polygon.getVertex(iVertex).getWorldPosition();
			pxmax = Math.max(pxmax, w.x);
			pxmin = Math.min(pxmin, w.x);
			pymax = Math.max(pymax, w.y);
			pymin = Math.min(pymin, w.y);
			pzmax = Math.max(pzmax, w.z);
			pzmin = Math.min(pzmin, w.z);
		    } 
		    polygon.xmin = pxmin;
		    polygon.xmax = pxmax;
		    polygon.ymin = pymin;
		    polygon.ymax = pymax;
		    polygon.zmin = pzmin;
		    polygon.zmax = pzmax;

		    // Calculate center of polygon z extent:    (for z_sort)
		    int xcen = (pxmin + pxmax) >> 1;
		    int ycen = (pymin + pymax) >> 1;
		    int zcen = (pzmin + pzmax) >> 1;
		    polygon.distance = xcen * xcen + ycen * ycen + zcen * zcen;

		    // If polygon is in front of the view plane,
		    // add it to the polygon list:
		    if (pzmax > 1)
			getComponents().addElement(polygon);
		}									  // !isbackface?
	    }										  // iPolygon
	}											  // iObject
    } 

    /**
     * sorts polygons in list by descending distance   +Inf..0
     * 
     * not quick but bubbled sort.
     */
    private void qsort(int lo0, int hi0) {
	int lo = lo0;	 // look at demo/wireframe/
	int hi = hi0;
	if (lo >= hi)
	    return;
	int mid = getPolygon((lo + hi) / 2).distance;
	while (lo < hi) {
	    while (lo < hi && getPolygon(lo).distance < mid) {	  // descending
		lo++;
	    } 
	    while (lo < hi && getPolygon(hi).distance >= mid) {	   // descending
		hi--;
	    } 
	    if (lo < hi) {
		swapPolygons(lo, hi);
	    } 
	} 
	if (hi < lo) {
	    int T = hi;
	    hi = lo;
	    lo = T;
	} 
	qsort(lo0, lo);
	qsort(lo == lo0 ? lo + 1 : lo, hi0);
    } 
    public void z_sort() {
	qsort(0, getComponentCount() - 1);

	/*
	 * boolean swapflag = true;
	 * while (swapflag) {
	 * swapflag = false;
	 * for (int iPolygon=0;iPolygon<getComponentCount()-1;iPolygon++)
	 * if (getPolygon(iPolygon).distance
	 * < getPolygon(iPolygon+1).distance) {
	 * swapPolygons(iPolygon,iPolygon+1);
	 * swapflag = true;
	 * }
	 * }
	 */
    } 

    /**
     * checks all direct neighbour polygons for correct order.
     */
    public void swap_necessaries() {
	for (int iPolygon = 0; iPolygon < getComponentCount() - 1; iPolygon++)
	    if (z_overlap(getPolygon(iPolygon), getPolygon(iPolygon + 1)))
		if (isnsort(getPolygon(iPolygon), getPolygon(iPolygon + 1))) {
		    swapPolygons(iPolygon, iPolygon + 1);
		} 
    } 

    /**
     * determines whether two Polygons that are assumed to z_overlap are
     * in the wrong order.
     * 
     * assumes A to be closer than B
     */
    protected final boolean isnsort(v_Polygon A, v_Polygon B) {
	if (!xy_overlap(A, B))
	    return false;
	if (!surface_outside(A, B))
	    return false;
	if (!surface_inside(A, B))
	    return false;
	return true;
    } 
    protected final boolean z_overlap(v_Polygon A, v_Polygon B) {
	if ((A.zmin >= B.zmax) || (B.zmin >= A.zmax))
	    return false;
	return true;	// z overlap
    } 
    protected final boolean xy_overlap(v_Polygon A, v_Polygon B) {
	if ((A.xmin > B.xmax) || (B.xmin > A.xmax))
	    return false;	 // ! x überlappend?
	if ((A.ymin > B.ymax) || (B.ymin > A.ymax))
	    return false;	 // ! y überlappend?
	return true;	// :xy überschneidung
    } 

    /**
     * @pre assumes A,B to be 'im Gegenuhrzeigersinn zum Betrachter'  sonst backface removal
     */
    //TODO: understand
    protected final boolean surface_inside(v_Polygon A, v_Polygon B) {
	Point3D w1 = B.getVertex(0).getWorldPosition();
	long	x1 = w1.x;
	long	y1 = w1.y;
	long	z1 = w1.z;
	Point3D w2 = B.getVertex(1).getWorldPosition();
	long	x2 = w2.x;
	long	y2 = w2.y;
	long	z2 = w2.z;
	Point3D w3 = B.getVertex(2).getWorldPosition();
	long	x3 = w3.x;
	long	y3 = w3.y;
	long	z3 = w3.z;
	long	a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
	long	b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
	long	c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
	long	d = -x1 * (y2 * z3 - y3 * z2) - x2 * (y3 * z1 - y1 * z3) - x3 * (y1 * z2 - y2 * z1);

	for (int iVertex = 0; iVertex < A.getComponentCount(); iVertex++) {
	    Point3D w = A.getVertex(iVertex).getWorldPosition();
	    if ((a * w.x + b * w.y + c * w.z + d) < 0)
		return false;	 // flunked
	} 
	return true;	// not flunked
    } 
    protected final boolean surface_outside(v_Polygon A, v_Polygon B) {
	Point3D w1 = B.getVertex(0).getWorldPosition();
	long	x1 = w1.x;
	long	y1 = w1.y;
	long	z1 = w1.z;
	Point3D w2 = B.getVertex(1).getWorldPosition();
	long	x2 = w2.x;
	long	y2 = w2.y;
	long	z2 = w2.z;
	Point3D w3 = B.getVertex(2).getWorldPosition();
	long	x3 = w3.x;
	long	y3 = w3.y;
	long	z3 = w3.z;
	long	a = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
	long	b = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
	long	c = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
	long	d = -x1 * (y2 * z3 - y3 * z2) - x2 * (y3 * z1 - y1 * z3) - x3 * (y1 * z2 - y2 * z1);

	for (int iVertex = 0; iVertex < B.getComponentCount(); iVertex++) {
	    Point3D w = B.getVertex(iVertex).getWorldPosition();
	    if ((a * w.x + b * w.y + c * w.z + d) < 0)
		return false;	 // flunked
	} 
	return true;	// not flunked
    } 

}
