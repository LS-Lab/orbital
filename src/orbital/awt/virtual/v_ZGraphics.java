/*
 * @(#)v_ZGraphics.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.Graphics;
import java.awt.*;
import orbital.math.Point3D;

/**
 * This class encapsulates a v_Graphics interface automatically
 * projecting given virtual 3D coordinates into real 2D ones.
 * The Origin of the 3D kartesian coordinates system is at "origin".
 * The outmost point is at (+&infin;|+&infin;|+&infin;) which is at the right-top-back
 * edge of the monitor.
 * This is a v_PerspectiveGraphics that automatically uses ZBuffer
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 */
//TODO: change inheritance to allow mathematical projection as well.
public class v_ZGraphics extends v_PerspectiveGraphics {
    private short z_buffer[][];
    private void clearZBuffer() {
	for (int j = 0; j < z_buffer.length; j++)
	    for (int i = 0; i < z_buffer[0].length; i++)
		z_buffer[j][i] = +32767;
    } 

    /**
     * Constructs a new v_Graphics display being 2D projected to and
     * displayed on real inner Graphics.
     * It uses the Origin (0|0|0) of origin.
     * @param gr the inner graphics used to display.
     * @param origin which point to use as origin (0|0|0).
     */
    public v_ZGraphics(Graphics gr, Point3D origin) {
	super(gr, origin);
	Rectangle bounds = new Rectangle(-origin.x, -origin.y, 2 * origin.x, 2 * origin.y);	   // gr.getClipRect();   //XXX =:-)= how big is it?
	z_buffer = new short[bounds.height][bounds.width];
	clearZBuffer();
    }

    public Object clone() {
    	return new v_ZGraphics(gr, origin);
    }
	
    /**
     * Draws a pixel at the screen coordinates (xs,ys) in depth z
     * @param xs the point's screen x coordinate
     * @param ys the point's screen y coordinate
     * @param z the point's virtual y coordinate
     */
    private void drawPixel(int xs, int ys, int z) {
	if (ys < 0 || xs < 0)
	    return;
	if (ys >= z_buffer.length || xs >= z_buffer[0].length)
	    return;
	if (z > z_buffer[ys][xs])
	    return;
	z_buffer[ys][xs] = (short) z;
	gr.drawLine(xs, ys, xs, ys);
    } 

    /**
     * Draws a point at the coordinates (x,y,z)
     * @param x the point's x coordinate
     * @param y the point's y coordinate
     * @param z the point's z coordinate
     */
    public void drawPoint(int x, int y, int z) {
	drawPixel(xlate(x, y, z), ylate(x, y, z), z);
    } 

    /**
     * Draws a line between the coordinates (x1,y1,z1) and (x2,y2,z2).
     * The line is drawn below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param z1 the first point's z coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     * @param z2 the second point's z coordinate
     */
    public void drawLine(int x1, int y1, int z1, int x2, int y2, int z2) {
	x1 = xlate(x1, y1, z1);
	y1 = ylate(x1, y1, z1);
	x2 = xlate(x2, y2, z2);
	y2 = ylate(x2, y2, z2);
	int x = x1, y = y1, z = z1;	   // starting position
	int x_unit = 1, xdiff = x2 - x1;	// changing-unit and difference in x
	if (xdiff < 0) {
	    xdiff = -xdiff;		 // abs
	    x_unit = -x_unit;	 // andere richtung
	} 
	int y_unit = 1, ydiff = y2 - y1;	// changing-unit and difference in y
	if (ydiff < 0) {
	    ydiff = -ydiff;		 // abs
	    y_unit = -y_unit;	 // andere richtung
	} 
	int z_unit = 1, zdiff = z2 - z1;
	int ddiff = (int) Math.sqrt(xdiff * xdiff + ydiff * ydiff);	   // Pythagoras Diagonale

	int error_term = 0;
	int z_error = 0;
	if (xdiff > ydiff) {				// If xdiff is bigger
	    int length = xdiff + 1;
	    for (int i = 0; i < length; i++) {
		drawPixel(x, y, z);
		x += x_unit;				// move in x-direction
		error_term += ydiff;		// error is meanwhile ydiff
		z_error += zdiff;
		if (error_term > xdiff) {	 // If error is now bigger than xdiff
		    y += y_unit;			// move in y-direction
		    error_term -= xdiff;	// correct error
		} 
		if (z_error > ddiff) {
		    z_error -= ddiff;
		    z += z_unit;
		} 
	    } 
	} else {							// If ydiff is bigger
	    int length = ydiff + 1;
	    for (int i = 0; i < length; i++) {
		drawPixel(x, y, z);
		y += y_unit;				// move in y-direction
		error_term += xdiff;		// error is meanwhile xdiff
		z_error += zdiff;
		if (error_term > ydiff) {	 // If error is now bigger than ydiff
		    x += x_unit;			// move in x-direction
		    error_term -= ydiff;	// correct error
		} 
		if (z_error > 0) {
		    z_error -= ddiff;
		    z += z_unit;
		} 
	    } 
	} 
    } 

    /**
     * Clears the specified rectangle by filling it with the current background color
     * of the current drawing surface.
     * Which drawing surface it selects depends on how the graphics context
     * was created.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void clearRect(int x0, int y0, int width, int height) {
	gr.clearRect(x0, y0, width, height);
	for (int y = y0; y < y0 + height; y++)
	    for (int x = x0; x < x0 + width; x++)
		z_buffer[y + origin.y][x + origin.x] = -32768;
    } 

    /**
     * Fills the specified rectangle with the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */

    /*
     * public void fillRect(int x, int y, int z, int width, int height, int depth) {
     * gr.fillRect(xlate(x,y,z),ylate(x,y,z),xlate(width,height,depth),ylate(width,height,depth));
     * }
     */

    /**
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see v_Graphics#fillPolygon
     */
    public void drawPolygon(int xPoints[], int yPoints[], int zPoints[], int nPoints) {
	for (int l = 0; l < nPoints - 1; l++) {
	    drawLine(xPoints[l], yPoints[l], zPoints[l], xPoints[l + 1], yPoints[l + 1], zPoints[l + 1]);
	} 
    } 

    /**
     * Draws a horizontal line between the screen coordinates (x1s,ys) and (x2s,ys).
     * The depth ranges from z1 to z2.
     */
    private void drawHorizLine(int ys, int x1s, int z1, int x2s, int z2) {
	int x = x1s, y = ys, z = z1;
	int x_unit = 1, xdiff = x2s - x1s;	  // normal form
	if (xdiff < 0) {
	    xdiff = -xdiff;		 // abs
	    x_unit = -x_unit;	 // andere richtung
	} 
	int z_unit = 1, zdiff = z2 - z1;
	int ddiff = xdiff;	  // Pythagoras Diagonale

	int z_error = 0;
	{
	    int length = xdiff + 1;
	    for (int i = 0; i < length; i++) {
		drawPixel(x, y, z);
		x += x_unit;
		z_error += zdiff;
		if (z_error > ddiff) {
		    z_error -= ddiff;
		    z += z_unit;
		} 
	    } 
	} 
    } 

    /**
     * Fills a polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolygon
     */

    /*
     * public void fillPolygon(int xPoints[], int yPoints[], int zPoints[], int nPoints) {
     * int xsPoints[] = new int[nPoints];
     * int ysPoints[] = new int[nPoints];
     * for (int x=0;x<nPoints;x++)
     * xsPoints[x] = xlate( xPoints[x], yPoints[i], zPoints[x] );
     * for(int y=0;y<nPoints;y++)
     * ysPoints[y] = ylate( xPoints[i], yPoints[y], zPoints[y] );
     * 
     * int ydiff1,ydiff2,         // Difference between starting x and ending x
     * xdiff1,xdiff2,         // Difference between starting y and ending y
     * start,                 // Starting offset of line between edges
     * length,                // Distance from edge 1 to edge 2
     * errorterm1,errorterm2, // Error terms for edges 1 & 2
     * offset1,offset2,       // Offset of current pixel in edges 1 & 2
     * count1,count2,         // Increment count for edges 1 & 2
     * xunit1,xunit2;         // Unit to advance x offset for edges 1 & 2
     * 
     * int edgecount=nPoints-1;
     * 
     * // Determine which vertex is at top of polygon:
     * int firstvert=0;           // Start by assuming vertex 0 is at top
     * int min_amt=ysPoints[0];   // Find y coordinate of vertex 0
     * for (int i=1; i<nPoints; i++) {  // Search thru vertices
     * if (ysPoints[i] < min_amt) {  // Is another vertex higher?
     * firstvert=i;                   // If so, replace previous top vertex
     * min_amt=ysPoints[i];
     * }
     * }
     * 
     * // Finding starting and ending vertices of first two edges:
     * int startvert1=firstvert;      // Get starting vertex of edge 1
     * int startvert2=firstvert;      // Get starting vertex of edge 2
     * int xstart1=xsPoints[startvert1];
     * int ystart1=ysPoints[startvert1];
     * int xstart2=xsPoints[startvert2];
     * int ystart2=ysPoints[startvert2];
     * int endvert1=startvert1-1;                   // Get ending vertex of edge 1
     * if (endvert1<0) endvert1=nPoints-1;  // Check for wrap
     * int xend1=xsPoints[endvert1];      // Get x & y coordinates
     * int yend1=ysPoints[endvert1];      // of ending vertices
     * int endvert2=startvert2+1;                   // Get ending vertex of edge 2
     * if (endvert2==(nPoints)) endvert2=0;  // Check for wrap
     * int xend2=xsPoints[endvert2];      // Get x & y coordinates
     * int yend2=ysPoints[endvert2];      // of ending vertices
     * 
     * // Draw the polygon:
     * 
     * while (edgecount>0) {    // Continue drawing until all edges drawn
     * offset1=320*ystart1+xstart1;  // Offset of edge 1
     * offset2=320*ystart2+xstart2;  // Offset of edge 2
     * errorterm1=0;           // Initialize error terms
     * errorterm2=0;           // for edges 1 & 2
     * if ((ydiff1=yend1-ystart1)<0) ydiff1=-ydiff1; // Get absolute value of
     * if ((ydiff2=yend2-ystart2)<0) ydiff2=-ydiff2; // x & y lengths of edges
     * if ((xdiff1=xend1-xstart1)<0) {               // Get value of length
     * xunit1=-1;                                  // Calculate X increment
     * xdiff1=-xdiff1;
     * }
     * else {
     * xunit1=1;
     * }
     * if ((xdiff2=xend2-xstart2)<0) {               // Get value of length
     * xunit2=-1;                                  // Calculate X increment
     * xdiff2=-xdiff2;
     * }
     * else {
     * xunit2=1;
     * }
     * 
     * // Choose which of four routines to use:
     * 
     * if (xdiff1>ydiff1) {    // If X length of edge 1 is greater than y length
     * if (xdiff2>ydiff2) {  // If X length of edge 2 is greater than y length
     * 
     * // Increment edge 1 on X and edge 2 on X:
     * 
     * count1=xdiff1;    // Count for x increment on edge 1
     * count2=xdiff2;    // Count for x increment on edge 2
     * while (count1 && count2) {  // Continue drawing until one edge is done
     * 
     * // Calculate edge 1:
     * 
     * while ((errorterm1<xdiff1)&&(count1>0)) { // Finished w/edge 1?
     * if (count1--) {     // Count down on edge 1
     * offset1+=xunit1;  // Increment pixel offset
     * xstart1+=xunit1;
     * }
     * errorterm1+=ydiff1; // Increment error term
     * if (errorterm1<xdiff1) {  // If not more than XDIFF
     * screen_buffer[offset1]=clip->color; // ...plot a pixel
     * }
     * }
     * errorterm1-=xdiff1; // If time to increment X, restore error term
     * 
     * // Calculate edge 2:
     * 
     * while ((errorterm2<xdiff2)&&(count2>0)) {  // Finished w/edge 2?
     * if (count2--) {     // Count down on edge 2
     * offset2+=xunit2;  // Increment pixel offset
     * xstart2+=xunit2;
     * }
     * errorterm2+=ydiff2; // Increment error term
     * if (errorterm2<xdiff2) {  // If not more than XDIFF
     * screen_buffer[offset2]=clip->color;  // ...plot a pixel
     * }
     * }
     * errorterm2-=xdiff2; // If time to increment X, restore error term
     * 
     * // Draw line from edge 1 to edge 2:
     * 
     * length=offset2-offset1; // Determine length of horizontal line
     * if (length<0) {         // If negative...
     * length=-length;       // Make it positive
     * start=offset2;        // And set START to edge 2
     * }
     * else start=offset1;     // Else set START to edge 1
     * //					for (int i=start; i<start+length+1; i++)  // From edge to edge...
     * //						screen_buffer[i]=clip->color;         // ...draw the line
     * setmem(&screen_buffer[start],length+1,clip->color);
     * offset1+=320;           // Advance edge 1 offset to next line
     * ystart1++;
     * offset2+=320;           // Advance edge 2 offset to next line
     * ystart2++;
     * }
     * }
     * else {
     * 
     * // Increment edge 1 on X and edge 2 on Y:
     * count1=xdiff1;    // Count for X increment on edge 1
     * count2=ydiff2;    // Count for Y increment on edge 2
     * while (count1 && count2) {  // Continue drawing until one edge is done
     * 
     * // Calculate edge 1:
     * while ((errorterm1<xdiff1)&&(count1>0)) { // Finished w/edge 1?
     * if (count1--) {     // Count down on edge 1
     * offset1+=xunit1;  // Increment pixel offset
     * xstart1+=xunit1;
     * }
     * errorterm1+=ydiff1; // Increment error term
     * if (errorterm1<xdiff1) {  // If not more than XDIFF
     * screen_buffer[offset1]=clip->color; // ...plot a pixel
     * }
     * }
     * errorterm1-=xdiff1; // If time to increment X, restore error term
     * 
     * // Calculate edge 2:
     * 
     * errorterm2+=xdiff2; // Increment error term
     * if (errorterm2 >= ydiff2)  { // If time to increment Y...
     * errorterm2-=ydiff2;        // ...restore error term
     * offset2+=xunit2;           // ...and advance offset to next pixel
     * xstart2+=xunit2;
     * }
     * --count2;
     * 
     * // Draw line from edge 1 to edge 2:
     * 
     * length=offset2-offset1; // Determine length of horizontal line
     * if (length<0) {         // If negative...
     * length=-length;       // ...make it positive
     * start=offset2;        // And set START to edge 2
     * }
     * else start=offset1;     // Else set START to edge 1
     * //					for (int i=start; i<start+length+1; i++)  // From edge to edge
     * //						screen_buffer[i]=clip->color;         // ...draw the line
     * setmem(&screen_buffer[start],length+1,clip->color);
     * offset1+=320;           // Advance edge 1 offset to next line
     * ystart1++;
     * offset2+=320;           // Advance edge 2 offset to next line
     * ystart2++;
     * }
     * }
     * }
     * else {
     * if (xdiff2>ydiff2) {
     * 
     * // Increment edge 1 on Y and edge 2 on X:
     * 
     * count1=ydiff1;  // Count for Y increment on edge 1
     * count2=xdiff2;  // Count for X increment on edge 2
     * while(count1 && count2) {  // Continue drawing until one edge is done
     * 
     * // Calculate edge 1:
     * 
     * errorterm1+=xdiff1; // Increment error term
     * if (errorterm1 >= ydiff1)  {  // If time to increment Y...
     * errorterm1-=ydiff1;         // ...restore error term
     * offset1+=xunit1;            // ...and advance offset to next pixel
     * xstart1+=xunit1;
     * }
     * --count1;
     * 
     * // Calculate edge 2:
     * 
     * while ((errorterm2<xdiff2)&&(count2>0)) { // Finished w/edge 1?
     * if (count2--) {     // Count down on edge 2
     * offset2+=xunit2;  // Increment pixel offset
     * xstart2+=xunit2;
     * }
     * errorterm2+=ydiff2; // Increment error term
     * if (errorterm2<xdiff2) {  // If not more than XDIFF
     * screen_buffer[offset2]=clip->color; // ...plot a pixel
     * }
     * }
     * errorterm2-=xdiff2;  // If time to increment X, restore error term
     * 
     * // Draw line from edge 1 to edge 2:
     * 
     * length=offset2-offset1; // Determine length of horizontal line
     * if (length<0) {    // If negative...
     * length=-length;  // ...make it positive
     * start=offset2;   // And set START to edge 2
     * }
     * else start=offset1;  // Else set START to edge 1
     * //					for (int i=start; i<start+length+1; i++) // From edge to edge...
     * //						screen_buffer[i]=clip->color;        // ...draw the line
     * setmem(&screen_buffer[start],length+1,clip->color);
     * offset1+=320;         // Advance edge 1 offset to next line
     * ystart1++;
     * offset2+=320;         // Advance edge 2 offset to next line
     * ystart2++;
     * }
     * }
     * else {
     * 
     * // Increment edge 1 on Y and edge 2 on Y:
     * 
     * count1=ydiff1;  // Count for Y increment on edge 1
     * count2=ydiff2;  // Count for Y increment on edge 2
     * while(count1 && count2) {  // Continue drawing until one edge is done
     * 
     * // Calculate edge 1:
     * 
     * errorterm1+=xdiff1;  // Increment error term
     * if (errorterm1 >= ydiff1)  {  // If time to increment Y
     * errorterm1-=ydiff1;         // ...restore error term
     * offset1+=xunit1;            // ...and advance offset to next pixel
     * xstart1+=xunit1;
     * }
     * --count1;
     * 
     * // Calculate edge 2:
     * 
     * errorterm2+=xdiff2; // Increment error term
     * if (errorterm2 >= ydiff2)  {  // If time to increment Y
     * errorterm2-=ydiff2;         // ...restore error term
     * offset2+=xunit2;            // ...and advance offset to next pixel
     * xstart2+=xunit2;
     * }
     * --count2;
     * 
     * // Draw line from edge 1 to edge 2:
     * 
     * length=offset2-offset1;  // Determine length of horizontal line
     * if (length<0) {          // If negative...
     * length=-length;        // ...make it positive
     * start=offset2;         // And set START to edge 2
     * }
     * else start=offset1;      // Else set START to edge 1
     * //					for (int i=start; i<start+length+1; i++)   // From edge to edge
     * //						screen_buffer[i]=clip->color;          // ...draw the linee
     * setmem(&screen_buffer[start],length+1,clip->color);
     * offset1+=320;            // Advance edge 1 offset to next line
     * ystart1++;
     * offset2+=320;            // Advance edge 2 offset to next line
     * ystart2++;
     * }
     * }
     * }
     * // Another edge (at least) is complete. Start next edge, if any.
     * 
     * if (!count1) {           // If edge 1 is complete...
     * --edgecount;           // Decrement the edge count
     * startvert1=endvert1;   // Make ending vertex into start vertex
     * --endvert1;            // And get new ending vertex
     * if (endvert1<0) endvert1=nPoints-1; // Check for wrap
     * xend1=xsPoints[endvert1];  // Get x & y of new end vertex
     * yend1=ysPoints[endvert1];
     * }
     * if (!count2) {          // If edge 2 is complete...
     * --edgecount;          // Decrement the edge count
     * startvert2=endvert2;  // Make ending vertex into start vertex
     * endvert2++;           // And get new ending vertex
     * if (endvert2==nPoints) endvert2=0; // Check for wrap
     * xend2=xsPoints[endvert2];  // Get x & y of new end vertex
     * yend2=ysPoints[endvert2];
     * }
     * }
     * }
     */

    /**
     * Returns a String object representing this Graphic's value.
     */
    public String toString() {
	return getClass().getName() + "[" + "gr=" + gr + ",origin=" + origin + ",color=" + getColor() + "]";
    } 

}



/**
 * ****************************************************************************
 * project3.h                                  *
 * this unit has 2 instances : the first one is used with the windows      *
 * graphic user interface, and the other does not,                *
 * *
 * +-------------------------------------------------+              *
 * |  this unit is NOT interfaced to the window GUI, |              *
 * |   for use on a bare screen ONLY - for runTime!  |              *
 * |                ****                             |              *
 * +-------------------------------------------------+              *
 * *
 * this unit handles the 3d -> 2d projections, we use 2 different methods        *
 * of projections :                                                       *
 * *
 * A : axonometric projections, no perspective due to             *
 * distance is performed, the general way                 *
 * we can look at the coordinate system is as             *
 * follows :                                              *
 * *
 * |  z axis                                      *
 * |                                              *
 * / \                                             *
 * x axis   /   \  y axis                                    *
 * *
 * B : perspective projections : the normal eye perspective       *
 * projection is performed, we can look at the 3d         *
 * universe we are refering to as a cube of               *
 * 1000 x 1000 x 1000 integer locations, with             *
 * the x axis, and y axis parallel to the screen          *
 * x, y axis respectivly, and the z axis going into       *
 * the screen.                                            *
 * *
 * we will look at the coordinate system as follows :     *
 * *
 * ³ Y axis                                               *
 * ³                                                      *
 * Z axis x------ X axis                                         *
 * *
 * ***************************************************************************
 */
