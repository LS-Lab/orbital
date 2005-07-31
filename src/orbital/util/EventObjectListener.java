/*
 * @(#)EventObjectListener.java 0.9 1998/03/13 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.EventListener;
import java.util.EventObject;

/**
 * EventObjectListener (Notify) callback interface is a description of
 * a listen-mechanism to react on all types of EventObjects at once.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public interface EventObjectListener extends EventListener {

    /**
     * called back when EventObjectListener is notified.
     * This method should be heavily multithread-safe and not
     * synchronized in an implementing subclass.
     */
    public abstract void eventObjectOccured(EventObject e);
}
