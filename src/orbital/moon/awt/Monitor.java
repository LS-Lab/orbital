/**
 * @(#)Monitor.java 0.9 2000/03/10 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.util.EventObject;
import java.awt.AWTEvent;
import java.beans.PropertyChangeEvent;
import java.beans.beancontext.BeanContextEvent;
import java.io.PrintWriter;

/**
 * A Monitor is an abstract base class for monitoring events.
 * 
 * @version 0.9, 2000/03/10
 * @author  Andr&eacute; Platzer
 */
public class Monitor {
    protected PrintWriter out;

    /**
     * Construct a monitor specifying where to print monitoring statements.
     */
    public Monitor(PrintWriter wr) {
	this.out = wr;
    }
    public Monitor() {
	this(new PrintWriter(System.out));
    }
	
    protected void monitor(String topic, EventObject e) {
	monitorImpl(topic, e);
    }

    // specialized
    protected void monitor(String topic, AWTEvent e) {
	monitorImpl(topic, e);
    }

    protected void monitor(String topic, BeanContextEvent e) {
	monitorImpl(topic, e);
    }

    protected void monitor(String topic, PropertyChangeEvent e) {
	monitorImpl(topic, e);
    }

    /**
     * monitoring implementation
     */
    protected void monitorImpl(String topic, Object e) {
	out.println(topic + ' ' + e);
    }
}
