/**
 * @(#)FieldChangeSupport.java 1.1 2003-01-03 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

/**
 * Utility class supporting field change event management.
 *
 *
 * @author <a href="mailto:NOSPAM@functologic.com">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-01-03
 * @see java.beans.PropertyChangeSupport
 * @see java.awt.AWTEventMulticaster
 */
class FieldChangeSupport implements FieldChangeListener {
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
     * "listeners" lists all the generic listeners.
     *
     *  This is transient - its state is written in the writeObject method.
     */
    transient private java.util.Vector listeners = null;

}// FieldChangeSupport
