/*
 * @(#)v_Graphics.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.Graphics;
import java.awt.*;
import java.awt.image.ImageObserver;
import orbital.math.Point3D;

/**
 * Encapsulates a generic v_Graphics interface automatically
 * projecting given virtual 3D coordinates into real 2D ones.
 * The Origin of the 3D kartesian coordinates system is at the specified point.
 * The outmost point is at (+&infin;|+&infin;|+&infin;) which is at the right-top-back
 * edge of the monitor.
 * <p>
 * Sub-classes must provide the projection methods xlate(x,y,z) and ylate(x,y,z)</p>
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 * @see #xlate(int,int,int)
 * @see #ylate(int,int,int)
 * @todo change base type int to float in this package?
 */
public
abstract class v_Graphics {
	/**
	 * The inner graphics context used to display in 2D.
	 */
	protected Graphics gr;
	/**
	 * The location of the point in space that is used as the logical origin (0|0|0).
	 * In fact, this describes a affine transformation applied to the coordinate space.
	 */
	protected Point3D  origin;

	/**
	 * Constructs a new v_Graphics display being 2D projected to and
	 * displayed on real inner Graphics.
	 * It uses the origin (0|0|0) for the specified point.
	 * @param gr the inner graphics used to display.
	 * @param origin which point to use as the origin named (0|0|0).
	 */
	protected v_Graphics(Graphics gr, Point3D origin) {
		this.gr = gr;
		this.origin = origin;
	}
	protected v_Graphics(Graphics gr) {
		this(gr, new Point3D(100, 100, 100));
	}
	/**
	 * Constructs a new v_Graphics display that is not yet functioning because it has no graphics context.
	 */
	protected v_Graphics() {
		this(null);
	}

	/**
	 * Creates a clone of this v_Graphics object.
	 * @return a clone of identical class which draws on the same inner graphics context (shallow copied)
	 *  using the same origin (either shallow or deep copied, although shallow copy is recommended).
	 */
	public abstract Object clone();

	/**
	 * Get the Origin (0|0|0) of the 3D Space.
	 * @return the location of the point in space that is used as the origin (0|0|0).
	 */
	public Point3D getOrigin() {
		return origin;
	} 

	/**
	 * Sets the Origin (0|0|0) of the 3D Space.
	 * @param origin the location of the point in space that is used as the origin (0|0|0).
	 *  In fact, this describes a affine transformation applied to the coordinate space.
	 */
	public void setOrigin(Point3D origin) {
		this.origin = origin;
	} 

	/**
	 * Get the associated 2D real Graphics.
	 */
	public Graphics getGraphics() {
		return gr;
	} 

	/**
	 * Sets the associated 2D real Graphics.
	 */
	public void setGraphics(Graphics gr) {
		this.gr = gr;
	} 

	/**
	 * returns the projected screen coordinates (x'|y') of a 3D Point (x|y|z).
	 * (-origin.z) is assumes to be the z-distance of the user from the screen.
	 * The Y-Coordinates are negated for assurance of the outmost point.
	 */
	protected abstract int xlate(int vx, int vy, int vz);
	protected abstract int ylate(int vx, int vy, int vz);

	/**
	 * Translates the specified parameters into the origin of the graphics context. All subsequent
	 * operations on this graphics context will be relative to this origin.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public void translate(int x, int y, int z) {
		setOrigin(new Point3D(x, y, z));
	} 

	/**
	 * Gets the current color.
	 * @see #setColor
	 */
	public Color getColor() {
		return gr.getColor();
	} 

	/**
	 * Sets the current color to the specified color. All subsequent graphics operations
	 * will use this specified color.
	 * @param c the color to be set
	 * @see Color
	 * @see #getColor
	 */
	public void setColor(Color c) {
		gr.setColor(c);
	} 

	/**
	 * Draws a pixel at the screen coordinates (xs,ys)
	 * @param xs the point's screen x coordinate
	 * @param ys the point's screen y coordinate
	 */
	private void drawPixel(int xs, int ys) {
		gr.drawLine(xs, ys, xs, ys);
	} 

	/**
	 * Draws a point at the coordinates (x,y,z)
	 * @param x the point's x coordinate
	 * @param y the point's y coordinate
	 * @param z the point's z coordinate
	 */
	public void drawPoint(int x, int y, int z) {
		int xs = xlate(x, y, z);
		int ys = ylate(x, y, z);
		drawPixel(xs, ys);
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
		gr.drawLine(xlate(x1, y1, z1), ylate(x1, y1, z1), xlate(x2, y2, z2), ylate(x2, y2, z2));
	} 

	/**
	 * Fills the specified rectangle with the current color.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @see #drawRect
	 * @see #clearRect
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
	 * @see #fillPolygon
	 */
	public void drawPolygon(int xPoints[], int yPoints[], int zPoints[], int nPoints) {
		int xsPoints[] = new int[nPoints];
		int ysPoints[] = new int[nPoints];
		for (int i = 0; i < nPoints; i++)
			xsPoints[i] = xlate(xPoints[i], yPoints[i], zPoints[i]);
		for (int i = 0; i < nPoints; i++)
			ysPoints[i] = ylate(xPoints[i], yPoints[i], zPoints[i]);
		gr.drawPolygon(xsPoints, ysPoints, nPoints);
	} 

	/**
	 * Fills a polygon with the current color using an
	 * even-odd fill rule (otherwise known as an alternating rule).
	 * @param xPoints an array of x points
	 * @param yPoints an array of y points
	 * @param nPoints the total number of points
	 * @see #drawPolygon
	 */
	public void fillPolygon(int xPoints[], int yPoints[], int zPoints[], int nPoints) {
		int sxPoints[] = new int[nPoints];
		int syPoints[] = new int[nPoints];
		for (int i = 0; i < nPoints; i++)
			sxPoints[i] = xlate(xPoints[i], yPoints[i], zPoints[i]);
		for (int i = 0; i < nPoints; i++)
			syPoints[i] = ylate(xPoints[i], yPoints[i], zPoints[i]);
		gr.fillPolygon(sxPoints, syPoints, nPoints);
	} 

	/**
	 * Draws the specified String using the current font and color.
	 * The x,y position is the starting point of the baseline of the String.
	 * @param str the String to be drawn
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #drawChars
	 * @see #drawBytes
	 */
	public void drawString(String str, int x, int y, int z) {
		gr.drawString(str, xlate(x, y, z), ylate(x, y, z));
	} 

	/**
	 * Draws the specified characters using the current font and color.
	 * @param data the array of characters to be drawn
	 * @param offset the start offset in the data
	 * @param length the number of characters to be drawn
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #drawString
	 * @see #drawBytes
	 */
	public void drawChars(char data[], int offset, int length, int x, int y, int z) {
		drawString(new String(data, offset, length), x, y, z);
	} 

	/**
	 * Draws the specified bytes using the current font and color.
	 * @param data the data to be drawn
	 * @param offset the start offset in the data
	 * @param length the number of bytes that are drawn
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #drawString
	 * @see #drawChars
	 */
	public void drawBytes(byte data[], int offset, int length, int x, int y, int z) {
		drawString(new String(data, 0, offset, length), x, y, z);
	} 

	/**
	 * Draws the specified image at the specified coordinate (x, y). If the image is
	 * incomplete the image observer will be notified later.
	 * @param img the specified image to be drawn
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param observer notifies if the image is complete or not
	 * @see Image
	 * @see ImageObserver
	 */
	public boolean drawImage(Image img, int x, int y, int z, ImageObserver observer) {
		return gr.drawImage(img, xlate(x, y, z), ylate(x, y, z), observer);
	} 

	/**
	 * Draws the specified image at the specified coordinate (x, y),
	 * with the given solid background Color.  If the image is
	 * incomplete the image observer will be notified later.
	 * @param img the specified image to be drawn
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param observer notifies if the image is complete or not
	 * @see Image
	 * @see ImageObserver
	 */
	public boolean drawImage(Image img, int x, int y, int z, Color bgcolor, ImageObserver observer) {
		return gr.drawImage(img, xlate(x, y, z), ylate(x, y, z), bgcolor, observer);
	} 

	/**
	 * Disposes of this graphics context.  The Graphics context cannot be used after
	 * being disposed of.
	 * @see #finalize
	 */
	public void dispose() {
		gr.dispose();
	} 

	/**
	 * Disposes of this graphics context once it is no longer referenced.
	 * @see #dispose
	 */
	public void finalize() {
		dispose();
	} 

	/**
	 * Returns a String object representing this Graphic's value.
	 */
	public String toString() {
		return getClass().getName() + "[" + "gr=" + gr + ",origin=" + origin + ",color=" + getColor() + "]";
	} 

}


/******************************************************************************
*                                 project3.h                                  *
*      this unit has 2 instances : the first one is used with the windows      *
*               graphic user interface, and the other does not,                *
*                                                                              *
*             +-------------------------------------------------+              *
*             |  this unit is NOT interfaced to the window GUI, |              *
*             |   for use on a bare screen ONLY - for runTime!  |              *
*             |                ****                             |              *
*             +-------------------------------------------------+              *
*                                                                              *
*this unit handles the 3d -> 2d projections, we use 2 different methods        *
*       of projections :                                                       *
*                                                                              *
*               A : axonometric projections, no perspective due to             *
*                       distance is performed, the general way                 *
*                       we can look at the coordinate system is as             *
*                       follows :                                              *
*                                                                              *
*                               |  z axis                                      *
*                               |                                              *
*                              / \                                             *
*                    x axis   /   \  y axis                                    *
*                                                                              *
*               B : perspective projections : the normal eye perspective       *
*                       projection is performed, we can look at the 3d         *
*                       universe we are refering to as a cube of               *
*                       1000 x 1000 x 1000 integer locations, with             *
*                       the x axis, and y axis parallel to the screen          *
*                       x, y axis respectivly, and the z axis going into       *
*                       the screen.                                            *
*                                                                              *
*                       we will look at the coordinate system as follows :     *
*                                                                              *
*                       | Y axis                                               *
*                       |                                                      *
*                Z axis x------ X axis                                         *
*                                                                              *
******************************************************************************/