/*
 * @(#)EventCollector.java 0.9 1998/03/12 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.EventObject;
import java.awt.AWTEvent;
import java.awt.event.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import orbital.util.SuspiciousError;

/**
 * EventCollector is a class capable of redirecting incoming EventObjects
 * to a set of EventObjectListeners. All events to which the EventCollector
 * is registered will be multiplexed according to the event_mask.
 * 
 * @version 0.9, 03/12/98
 * @author  Andr&eacute; Platzer
 * @see java.util.EventObject
 * @see orbital.util.EventObjectListener
 */
public class EventCollector implements KeyListener {

    /**
     * The Collection of EventObjectListeners registered.
     * 
     * @serial
     */
    protected Collection eventObjectListeners = new ArrayList();

    /**
     * The global event mask used for total subscription.
     * 
     * @serial
     */
    protected long		 event_mask;

    /**
     * Set up a new EventCollector multiplexing the following event_mask
     * of subscription.
     */
    public EventCollector(long event_subscription_mask) {
	event_mask = event_subscription_mask;
    }
    public EventCollector() {
	this(Long.MAX_VALUE);
    }

    /**
     * Add a listener.
     */
    public void addEventObjectListener(EventObjectListener l) {
	eventObjectListeners.add(l);
    } 

    /**
     * Remove a listener.
     */
    public void removeEventObjectListener(EventObjectListener l) {
	eventObjectListeners.remove(l);
    } 


    protected void multiplexEventObject(EventObject ev) {
	for (Iterator i = eventObjectListeners.iterator(); i.hasNext(); )
	    try {
		EventObjectListener l = (EventObjectListener) i.next();
		l.eventObjectOccured(ev);
	    } catch (ClassCastException oops) {
		throw new SuspiciousError("invalid member: " + oops);
	    } 
    } 


    /**
     * Impl: Implementation of the EventCollector. It will gather any
     * EventObjects receptable.
     */
    public void keyPressed(KeyEvent e) {
	if ((event_mask & AWTEvent.KEY_EVENT_MASK) != 0)
	    multiplexEventObject(e);
    } 
    public void keyTyped(KeyEvent e) {
	if ((event_mask & AWTEvent.KEY_EVENT_MASK) != 0)
	    multiplexEventObject(e);
    } 
    public void keyReleased(KeyEvent e) {
	if ((event_mask & AWTEvent.KEY_EVENT_MASK) != 0)
	    multiplexEventObject(e);
    } 
}

/*
 * if ( (event_mask&AWTEvent.ACTION_EVENT_MASK && e instanceof ActionEvent)
 * || (event_mask&AWTEvent.ADJUSTMENT_EVENT_MASK && e instanceof AdjustmentEvent)
 * || (event_mask&AWTEvent.COMPONENT_EVENT_MASK && e instanceof ComponentEvent)
 * || (event_mask&AWTEvent.CONTAINER_EVENT_MASK && e instanceof ContainerEvent)
 * || (event_mask&AWTEvent.FOCUS_EVENT_MASK && e instanceof FocusEvent)
 * || (event_mask&AWTEvent.ITEM_EVENT_MASK && e instanceof ItemEvent)
 * || (event_mask&AWTEvent.KEY_EVENT_MASK && e instanceof KeyEvent)
 * || (event_mask&AWTEvent.MOUSE_EVENT_MASK && e instanceof MouseEvent)
 * || (event_mask&AWTEvent.MOUSE_MOTION_EVENT_MASK && e instanceof MouseEvent)
 * || (event_mask&AWTEvent.TEXT_EVENT_MASK && e instanceof TextEvent)
 * || (event_mask&AWTEvent.WINDOW_EVENT_MASK && e instanceof WindowEvent)
 * )
 */

