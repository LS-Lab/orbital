/*
 * @(#)v_Component.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import orbital.math.Matrix3D;
import java.io.Externalizable;
import java.io.IOException;

/**
 * A generic Virtual Reality component in 3D VR-Space.
 * <p>
 * <i><b>Evolves</b>: Externalizable might be substituted by java.io.Serializable.</i>
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 * @todo can we replace Externalizable by Serializable, as well?
 */
public interface v_Component extends Externalizable {

    /**
     * transforms the position of this component.
     */
    public abstract /* synchronized */ void transform(Matrix3D mat);

    /**
     * draws this component at the position.
     */
    public abstract /* synchronized */ void draw(v_Graphics g);
}
