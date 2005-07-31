/*
 * @(#)GrVirtual.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.Graphics;
import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * This class encapsulates a virtual graphics interface that automatically
 * transformes virtual coordinates into real ones.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public abstract class GrVirtual extends Graphics {

    /**
     * the real Graphics object.
     */
    protected Graphics gr;

    /**
     * constructs a Virtual Graphics instance associated with the
     * real Graphics Implementation.
     */
    protected GrVirtual(Graphics gr) {
	this.gr = gr;
    }

    /**
     * returns the associated real Graphics.
     */
    public Graphics getGraphics() {
	return gr;
    } 

    /**
     * sets the associated real Graphics.
     */
    public void setGraphics(Graphics gr) {
	this.gr = gr;
    } 

    /**
     * translates a virtual x into a real x
     * @todo change types from int to float
     */
    public abstract int xlate(int x);

    /**
     * translates a virtual y into a real y
     */
    public abstract int ylate(int y);

    /**
     * Translates the specified parameters into the origin of the graphics context. All subsequent
     * operations on this graphics context will be relative to this origin.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void translate(int x, int y) {
	gr.translate(xlate(x), ylate(y));
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
     * Draws a line between the coordinates (x1,y1) and (x2,y2). The line is drawn
     * below and to the left of the logical coordinates.
     * @param x1 the first point's x coordinate
     * @param y1 the first point's y coordinate
     * @param x2 the second point's x coordinate
     * @param y2 the second point's y coordinate
     */
    public void drawLine(int x1, int y1, int x2, int y2) {
	gr.drawLine(xlate(x1), ylate(y1), xlate(x2), ylate(y2));
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
    public void fillRect(int x, int y, int width, int height) {
	gr.fillRect(xlate(x), ylate(y), xlate(width), ylate(height));
    } 

    /**
     * Draws the outline of the specified rectangle using the current color.
     * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
     * rectangle.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillRect
     * @see #clearRect
     */
    public void drawRect(int x, int y, int width, int height) {
	gr.drawRect(xlate(x), ylate(y), xlate(width), ylate(height));
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
     * @see #fillRect
     * @see #drawRect
     */
    public void clearRect(int x, int y, int width, int height) {
	gr.clearRect(xlate(x), ylate(y), xlate(width), ylate(height));
    } 

    /**
     * Draws an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #fillOval
     */
    public void drawOval(int x, int y, int width, int height) {
	gr.drawOval(xlate(x), ylate(y), xlate(width), ylate(height));
    } 

    /**
     * Fills an oval inside the specified rectangle using the current color.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @see #drawOval
     */
    public void fillOval(int x, int y, int width, int height) {
	gr.fillOval(xlate(x), ylate(y), xlate(width), ylate(height));
    } 

    /**
     * Draws a polygon defined by an array of x points and y points.
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #fillPolygon
     */
    public void drawPolygon(int xPoints[], int yPoints[], int nPoints) {
	for (int x = 0; x < xPoints.length; x++)
	    xPoints[x] = xlate(xPoints[x]);
	for (int y = 0; y < yPoints.length; y++)
	    yPoints[y] = ylate(yPoints[y]);
	gr.drawPolygon(xPoints, yPoints, nPoints);
    } 

    /**
     * Draws a polygon defined by the specified point.
     * @param p the specified polygon
     * @see #fillPolygon
     */
    public void drawPolygon(Polygon p) {
	drawPolygon(p.xpoints, p.ypoints, p.npoints);
    } 

    /**
     * Fills a polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param xPoints an array of x points
     * @param yPoints an array of y points
     * @param nPoints the total number of points
     * @see #drawPolygon
     */
    public void fillPolygon(int xPoints[], int yPoints[], int nPoints) {
	for (int x = 0; x < xPoints.length; x++)
	    xPoints[x] = xlate(xPoints[x]);
	for (int y = 0; y < yPoints.length; y++)
	    yPoints[y] = ylate(yPoints[y]);
	gr.fillPolygon(xPoints, yPoints, nPoints);
    } 

    /**
     * Fills the specified polygon with the current color using an
     * even-odd fill rule (otherwise known as an alternating rule).
     * @param p the polygon
     * @see #drawPolygon
     */
    public void fillPolygon(Polygon p) {
	fillPolygon(p.xpoints, p.ypoints, p.npoints);
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
    public void drawString(String str, int x, int y) {
	gr.drawString(str, xlate(x), ylate(y));
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
    public void drawChars(char data[], int offset, int length, int x, int y) {
	drawString(new String(data, offset, length), x, y);
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
    public void drawBytes(byte data[], int offset, int length, int x, int y) {
	drawString(new String(data, offset, length), x, y);
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
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
	return gr.drawImage(img, xlate(x), ylate(y), observer);
    } 

    /**
     * Draws the specified image inside the specified rectangle. The image is
     * scaled if necessary. If the image is incomplete the image observer will be
     * notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     */
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
	return gr.drawImage(img, xlate(x), ylate(y), xlate(width), ylate(height), observer);
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
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
	return gr.drawImage(img, xlate(x), ylate(y), bgcolor, observer);
    } 

    /**
     * Draws the specified image inside the specified rectangle,
     * with the given solid background Color. The image is
     * scaled if necessary. If the image is incomplete the image
     * observer will be notified later.
     * @param img the specified image to be drawn
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param observer notifies if the image is complete or not
     * @see Image
     * @see ImageObserver
     */
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
	return gr.drawImage(img, xlate(x), ylate(y), xlate(width), ylate(height), bgcolor, observer);
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
	return getClass().getName() + "[" + "gr=" + gr + ",color=" + getColor() + "]";
    } 


    /*
     * private void drawLineImpl(int x1,int y1,int x2,int y2) {
     * // this algorithm is not kommutative f(x1,y1, x2,y2) != f(x2,y2, x1,y1)
     * 
     * int x = x1, y = y1;               // starting position
     * int x_unit = 1, xdiff = x2-x1;    // changing-unit and difference in x
     * if (xdiff<0) {
     * xdiff = -xdiff;               // abs
     * x_unit = -x_unit;             // andere richtung
     * }
     * int y_unit = 1, ydiff = y2-y1;    // changing-unit and difference in y
     * if (ydiff<0) {
     * ydiff = -ydiff;               // abs
     * y_unit = -y_unit;             // andere richtung
     * }
     * 
     * int error_term = 0;
     * if (xdiff > ydiff) {              // If xdiff is bigger
     * int length = xdiff+1;
     * for (int i=0; i<length; i++) {
     * drawPixel(x,y);
     * x += x_unit;              // move in x-direction
     * error_term += ydiff;      // error is meanwhile ydiff
     * if (error_term>xdiff) {   // If error is now bigger than xdiff
     * y += y_unit;          // move in y-direction
     * error_term -= xdiff;  // correct error
     * }
     * }
     * } else {                          // If ydiff is bigger
     * int length = ydiff+1;
     * for (int i=0; i<length; i++) {
     * drawPixel(x,y);
     * y += y_unit;              // move in y-direction
     * error_term += xdiff;      // error is meanwhile xdiff
     * if (error_term>ydiff) {   // If error is now bigger than ydiff
     * x += x_unit;          // move in x-direction
     * error_term -= ydiff;  // correct error
     * }
     * }
     * }
     * }
     */

}
