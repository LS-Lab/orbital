/**
 * @(#)BeanMonitor.java 0.9 2001/03/28 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

import java.io.PrintWriter;

/**
 * Monitors various bean events.
 * 
 * @version 0.9, 2000/03/10
 * @author  Andr&eacute; Platzer
 * @todo implements java.beans.beancontext.*Listener as well
 */
public
class BeanMonitor extends Monitor
	implements PropertyChangeListener, VetoableChangeListener {

	public BeanMonitor(PrintWriter wr) {
		super(wr);
	}
	public BeanMonitor() {}

    // java.beans.PropertyChangeListener
    public void propertyChange(java.beans.PropertyChangeEvent e) {
        monitor("propertyChange", e);
    }

    // java.beans.VetoableChangeListener
    public void vetoableChange(java.beans.PropertyChangeEvent e) throws java.beans.PropertyVetoException {
        monitor("vetoableChange", e);
    }
}
