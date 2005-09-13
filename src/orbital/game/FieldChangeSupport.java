/**
 * @(#)FieldChangeSupport.java 1.1 2003-01-03 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import java.io.*;

/**
 * Utility class supporting field change event management.
 *
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see java.beans.PropertyChangeSupport
 * @see java.awt.AWTEventMulticaster
 */
class FieldChangeSupport implements FieldChangeListener, java.io.Serializable {
    private static final long serialVersionUID = 5762015723242972218L;
    public FieldChangeSupport() {
        
    }
    
    /**
     * Add a FieldChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener  The FieldChangeListener to be added
     */
    public synchronized void addFieldChangeListener(FieldChangeListener listener) {
        if (listeners == null) {
            listeners = new java.util.Vector();
        }
        listeners.addElement(listener);
    }

    /**
     * Remove a FieldChangeListener from the listener list.
     * This removes a FieldChangeListener that was registered
     * for all properties.
     *
     * @param listener  The FieldChangeListener to be removed
     */
    public synchronized void removeFieldChangeListener(FieldChangeListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.removeElement(listener);
    }

    /**
     * Fire an existing FieldChangeEvent to any registered listeners.
     * No event is fired if the given event's old and new values are
     * equal and non-null.
     * @param evt  The FieldChangeEvent object.
     */
    public void componentChanged(FieldChangeEvent evt) {
        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector) listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                FieldChangeListener target = (FieldChangeListener)targets.elementAt(i);
                target.componentChanged(evt);
            }
        }
    }

    public void movePerformed(FieldChangeEvent evt) {
        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector) listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                FieldChangeListener target = (FieldChangeListener)targets.elementAt(i);
                target.movePerformed(evt);
            }
        }
    }

    public void stateChanged(FieldChangeEvent evt) {
        java.util.Vector targets = null;
        synchronized (this) {
            if (listeners != null) {
                targets = (java.util.Vector) listeners.clone();
            }
        }

        if (targets != null) {
            for (int i = 0; i < targets.size(); i++) {
                FieldChangeListener target = (FieldChangeListener)targets.elementAt(i);
                target.stateChanged(evt);
            }
        }
    }

    /**
     * @serialData Null terminated list of <code>FieldChangeListeners</code>.
     * <p>
     * At serialization time we skip non-serializable listeners and
     * only serialize the serializable listeners.
     *
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

        java.util.Vector v = null;
        synchronized (this) {
            if (listeners != null) {
                v = (java.util.Vector) listeners.clone();
            }
        }

        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                FieldChangeListener l = (FieldChangeListener)v.elementAt(i);
                if (l instanceof Serializable) {
                    s.writeObject(l);
                }
            }
        }
        s.writeObject(null);
    }


    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();
      
        Object listenerOrNull;
        while (null != (listenerOrNull = s.readObject())) {
          addFieldChangeListener((FieldChangeListener)listenerOrNull);
        }
    }

    /**
     * "listeners" lists all the generic listeners.
     *
     *  This is transient - its state is written in the writeObject method.
     */
    transient private java.util.Vector/*_<FieldChangeListener>_*/ listeners = null;

}// FieldChangeSupport
