/**
 * @(#)v_CartesianGraphics.java 0.9 2000/08/30 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

/**
 * Cartesian coordinates wrapper graphics.
 * <p>
 * This is a null coordinate transformation, so using it is not required.
 * This class is provided nevertheless to simplify coordinate system transformation switching.</p>
 * <p>
 * Performs the null transformation<pre>
 * x' = x
 * y' = y
 * z' = z
 * </pre></p>
 *
 * @version 0.9, 2000/08/30
 * @author  Andr&eacute; Platzer
 */
public class v_CartesianGraphics extends v_CoordinateTransformationGraphics {
    /**
     * Constructs a new v_Graphics wrapper performing coordinate transformation.
     * @param projection the inner projection graphics to wrap around.
     */
    public v_CartesianGraphics(v_Graphics projection) {
	super(projection);
    }

    public Object clone() {
    	return new v_CartesianGraphics((v_Graphics) getProjection().clone());
    }
	
    /**
     * returns the projected screen coordinates of a 3D Point (x|y|z)
     * in cartesian coordinates. (as is)
     */
    protected final int xlate(int vx, int vy, int vz) {
	return projection.xlate(vx, vy, vz);
    } 
    protected final int ylate(int vx, int vy, int vz) {
	return projection.ylate(vx, vy, vz);
    } 

}
