/*
 * @(#)v_PerspectiveGraphics.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.awt.Graphics;
import orbital.math.Point3D;

/**
 * True perspective projection graphics.
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 */
public class v_PerspectiveGraphics extends v_Graphics {

    /**
     * Constructs a new v_Graphics display being 2D projected to and
     * displayed on real Graphics gr.
     * It uses the Origin (0|0|0) of origin.
     * @param gr the inner graphics used to display.
     * @param origin which point to use as origin (0|0|0).
     */
    public v_PerspectiveGraphics(Graphics gr, Point3D origin) {
	super(gr, origin);
    }
    public v_PerspectiveGraphics() {}

    public Object clone() {
    	return new v_PerspectiveGraphics(gr, origin);
    }
	
    /**
     * returns the projected screen (x'|y') coordinates of a 3D Point (x|y|z)
     */
    protected int xlate(int vx, int _vy, int vz) {
	return (int) ((double) (-origin.z) * -vx/vz + origin.x);
    } 
    protected int ylate(int _vx, int vy, int vz) {
	return (int) ((double) (-origin.z) * +vy/vz + origin.y);
    } 

}
