/*
 * @(#)Map.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic;

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Dimension;

import java.util.Arrays;

/**
 * A map is a generic Container for robotics that holds up a two-dimensional
 * array of Object-elements (Table). Each Element is identified via its <code>(x,y)</code>-coordinates
 * ranging <code>minx..minx+width</code> and <code>miny..miny+height</code>.
 * 
 * @version 0.9, 04/03/98
 * @author  Andr&eacute; Platzer
 * @structure composite map: Object[][] unidirectional
 */
public class Map extends Table {

    /**
     * The two-dimensional array with the Objects-elements contained.
     * @serial 2D table of objects in map.
     */
    private Object map[][];

    /**
     * Initializes a Map ranging inside given rectangle.
     */
    public Map(Rectangle bounds) {
	super(bounds);
	map = new Object[bounds.height][bounds.width];
    }

    /**
     * Initializes a Map of size <code>width|height</code>
     * ranging <code>(x,y)</code> .. <code>(x,y) + (width-1,height-1)</code>.
     */
    public Map(int x, int y, int width, int height) {
	this(new Rectangle(x, y, width, height));
    }

    /**
     * Initializes a Map of size <code>width|height</code>
     * ranging <code>(0,0)</code> .. <code>(width-1,height-1)</code>.
     */
    public Map(int width, int height) {
	this(0, 0, width, height);
    }

    /**
     * Initializes a Map of size <code>width|height</code>
     * ranging <code>(0,0)</code> .. <code>(width-1,height-1)</code>.
     */
    public Map(Dimension dim) {
	this(0, 0, dim.width, dim.height);
    }

    /**
     * Initializes Map of maximum size n with unknown starting position
     * and orientation (direction).
     * So a Map sized (<code>-n,-n .. +n,+n</code>) is constructed.
     */
    public Map(int maxsize) {
	this(-maxsize, -maxsize, 2 * maxsize, 2 * maxsize);
    }

    // get/set methods

    /**
     * get object at the specified position.
     */
    public Object get(int x, int y) throws IndexOutOfBoundsException {
	final Rectangle bounds = getBounds();
	return map[y - bounds.y][x - bounds.x];
    } 

    /**
     * set object at the specified position.
     */
    public void set(int x, int y, Object what) throws IndexOutOfBoundsException {
	final Rectangle bounds = getBounds();
	map[y - bounds.y][x - bounds.x] = what;
    } 

    /**
     * get object at the specified position.
     */
    public Object get(Point p) throws IndexOutOfBoundsException {
	return get(p.x, p.y);
    } 

    /**
     * set object at the specified position.
     */
    public void set(Point p, Object what) throws IndexOutOfBoundsException {
	set(p.x, p.y, what);
    } 

    protected void setBounds(Rectangle new_bounds) {
	throw new UnsupportedOperationException("update of data for change of bounds not yet implemented");
    } 

    protected void setDimension(Dimension new_dim) {
	final Rectangle bounds = getBounds();
	Dimension  old = getDimension();
	Object[][] oldmap = map;
	super.setDimension(new_dim);
	map = new Object[bounds.height][bounds.width];
	for (int i = 0; i < bounds.height; i++)
	    for (int j = 0; j < bounds.width; j++)
		map[i][j] = (i < old.height && j < old.width) ? oldmap[i][j] : null;
    } 

    /**
     * Shallow copy.
     */
    public Object clone() {
	Map clone = new Map(getBounds());
	// unlike cloning map, this is safe since it does lead to a shallow copy of the first array dimension
	clone.map = new Object[map.length][];
	for (int i = 0; i < map.length; i++)
	    // we do not need to clone map[i][j] as well?
	    clone.map[i] = (Object[]) map[i].clone();
	return clone;
    }
	
    public boolean equals(Object b) {
	return b instanceof Map ? super.equals(b) && Arrays.equals(map, ((Map) b).map) : false;
    }
	
    public int hashCode() {
	//TODO: can we use Utility.hashCodeAll(Object[]) as well?
	int hash = 0;
	for (int i = 0; i < getDimension().height; i++)
	    for (int j = 0; j < getDimension().width; j++)
		hash ^= map[i][j] == null ? 0 : map[i][j].hashCode();
	return hash;
    }
}
