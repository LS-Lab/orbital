/**
 * @(#)FieldChangeAdapter.java 1.1 2003-01-03 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

/**
 * An abstract adapter class for field change events. The methods in
 * this class are empty. This class exists as convenience for creating
 * listener objects.
 *
 *
 * @author <a href="mailto:NOSPAM@functologic.com">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-01-03
 */
public class FieldChangeAdapter implements FieldChangeListener{
    public void componentChanged(FieldChangeEvent event) {}

    public void movePerformed(FieldChangeEvent event) {}

    public void stateChanged(FieldChangeEvent event) {}

}// FieldChangeAdapter
