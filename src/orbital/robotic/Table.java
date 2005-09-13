/*
 * @(#)Table.java 0.9 1998/12/19 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;
import java.io.Serializable;

/**
 * A Table is an abstraction of any set of data (Container)
 * organized in a logical two-dimensional structure.
 * <p>
 * A Table is a generic Container that holds up a two-dimensional
 * structure of elements. Each element is identified via its <code>x|y</code>-coordinates
 * ranging inside a rectangle of <code>minx|miny</code> and <code>minx|miny + width-1|height-1</code>.
 * <p>
 * This is a so called <i>CARRY</i> (Container Array).
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo remove this class and all descendants replacing them by orbital.algorithm.template.*Search stuff
 */
public abstract class Table implements Serializable {

    /**
     * The rectangular bounds of the coordinate ranges. <code>minx..|miny..</code> until <code>..minx+width|..miny+height</code>.
     * @serial
     */
    private Rectangle bounds;

    /**
     * Ranging inside given rectangle.
     */
    protected Table(Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * Ranging from <code>min</code> to <code>min + dim</code>.
     */
    protected Table(Point min, Dimension dim) {
        this(new Rectangle(min.x, min.y, dim.width, dim.height));
    }

    /**
     * Ranging from <code>x|y</code> to <code>x|y + width-1|height-1</code>.
     * The corresponding size of this table is <code>width|height</code>.
     */
    protected Table(int x, int y, int width, int height) {
        this(new Point(x, y), new Dimension(width, height));
    }

    // TODO: property change event

    // Get/Set Methods.

    /**
     * get the rectangular bounds.
     */
    public Rectangle getBounds() {
        return bounds;
    } 

    /**
     * set the rectangular bounds.
     */
    protected void setBounds(Rectangle new_bounds) {
        this.bounds = new_bounds;
    } 

    /**
     * get the dimension.
     */
    public Dimension getDimension() {
        return new Dimension(bounds.width, bounds.height);
    } 

    /**
     * set the dimension.
     */
    protected void setDimension(Dimension new_dim) {
        bounds.width = new_dim.width;
        bounds.height = new_dim.height;
    } 

    /**
     * get the minimum (upper left hand corner).
     */
    public Point getMinimum() {
        return new Point(bounds.x, bounds.y);
    } 

    /**
     * set the minimum (upper left hand corner).
     */
    public void setMinimum(Point min) {
        bounds.x = min.x;
        bounds.y = min.y;
    } 


    /**
     * Check whether a Point <code>(x|y)</code> is within the specified range.
     */
    public boolean inRange(Point p) {
        if (p.x < bounds.x || p.x >= bounds.x + bounds.width)
            return false;
        if (p.y < bounds.y || p.y >= bounds.y + bounds.height)
            return false;
        return true;
    } 

    /**
     * Get the Object at Point p of the Container.
     * @throws IndexOutOfBoundsException if the specified Point is out of bounds.
     * @throws UnsupportedOperationException if this Table does not support reading.
     */
    public abstract Object get(Point p) throws IndexOutOfBoundsException, UnsupportedOperationException;

    /**
     * Set the Object at Point p of the Container to the Object specified.
     * @throws IndexOutOfBoundsException if the specified Point is out of bounds.
     * @throws UnsupportedOperationException if this Table is readonly.
     */
    public abstract void set(Point p, Object what) throws IndexOutOfBoundsException, UnsupportedOperationException;
}
