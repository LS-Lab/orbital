/**
 * @(#)v_CoordinateTransformationGraphics.java 0.9 2000/08/30 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

/**
 * Base class for coordinate system transformation wrapper graphics.
 * <p>
 * Wrap around a v_Graphics instance to transform coordinates before projection.</p>
 *
 * @version 0.9, 2000/08/30
 * @author  Andr&eacute; Platzer
 */
public abstract class v_CoordinateTransformationGraphics extends v_Graphics {
    /**
     * The inner v_Graphics projection instance to wrap around.
     */
    protected v_Graphics projection;

    /**
     * Constructs a new v_Graphics wrapper performing coordinate transformation.
     * @param projection the inner projection graphics to wrap around.
     */
    protected v_CoordinateTransformationGraphics(v_Graphics projection) {
	super(projection.getGraphics(), projection.getOrigin());
	this.projection = projection;
    }

    /**
     * Creates a clone of this v_CoordinateTransformationGraphics object.
     * @return a clone of identical class which draws on a copy of the inner v_Graphics context (deep copied).
     */
    public abstract Object clone();

    /**
     * Get the inner v_Graphics projection instance to wrap around.
     */
    public v_Graphics getProjection() {
	return projection;
		
    }

    /**
     * Set the inner v_Graphics projection instance to wrap around.
     */
    public void setProjection(v_Graphics projection) {
	this.projection = projection;
		
    }

}
