/**
 * @(#)v_CylindricalGraphics.java 0.9 2000/08/30 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

/**
 * Cylindrical coordinates wrapper graphics.
 * <p>
 * Wrap around a v_Graphics instance to transform coordinates from cylindrical coordinates, first.</p>
 * <p>
 * Performs the transformation<pre>
 * x' = r * cos(&phi;)
 * y' = r * sin(&phi;)
 * z' = z
 * </pre></p>
 *
 * @version 0.9, 2000/08/30
 * @author  Andr&eacute; Platzer
 */
public class v_CylindricalGraphics extends v_CoordinateTransformationGraphics {
    /**
     * Constructs a new v_Graphics wrapper performing coordinate transformation.
     * @param projection the inner projection graphics to wrap around.
     */
    public v_CylindricalGraphics(v_Graphics projection) {
	super(projection);
    }

    public Object clone() {
    	return new v_CylindricalGraphics((v_Graphics) getProjection().clone());
    }
	
    /**
     * returns the projected screen coordinates of a 3D Point (r|&theta;|z)
     * in cylindrical coordinates.
     */
    protected int xlate(int r, int theta, int z) {
	return projection.xlate((int)Math.round(r*Math.cos(theta)),
				(int)Math.round(r*Math.sin(theta)),
				z
				);
    } 
    protected int ylate(int r, int theta, int z) {
	return projection.ylate((int)Math.round(r*Math.cos(theta)),
				(int)Math.round(r*Math.sin(theta)),
				z
				);
    } 

}
