/**
 * @(#)v_SphericalGraphics.java 0.9 2000/08/30 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

/**
 * Spherical coordinates wrapper graphics.
 * <p>
 * Wrap around a v_Graphics instance to transform coordinates from spherical coordinates, first.</p>
 * <p>
 * Performs the transformation<pre>
 * x' = r * sin(&phi;)*cos(&phi;)
 * y' = r * sin(&phi;)*sin(&phi;)
 * z' = r * cos(&phi;)
 * </pre></p>
 *
 * @version 0.9, 2000/08/30
 * @author  Andr&eacute; Platzer
 */
public
class v_SphericalGraphics extends v_CoordinateTransformationGraphics {
	/**
	 * Constructs a new v_Graphics wrapper performing coordinate transformation.
	 * @param projection the inner projection graphics to wrap around.
	 */
	public v_SphericalGraphics(v_Graphics projection) {
		super(projection);
	}

    public Object clone() {
    	return new v_SphericalGraphics((v_Graphics) getProjection().clone());
    }
	
	/**
	 * returns the projected screen coordinates of a 3D Point (r|&theta;|&phi;)
	 * in spherical coordinates.
	 */
	protected int xlate(int r, int theta, int phi) {
		return projection.xlate((int)Math.round(r*Math.sin(theta)*Math.cos(phi)),
				(int)Math.round(r*Math.sin(theta)*Math.sin(phi)),
				(int)Math.round(r*Math.cos(theta))
				);
	} 
	protected int ylate(int r, int theta, int phi) {
		return projection.ylate((int)Math.round(r*Math.sin(theta)*Math.cos(phi)),
				(int)Math.round(r*Math.sin(theta)*Math.sin(phi)),
				(int)Math.round(r*Math.cos(theta))
				);
	} 

}
