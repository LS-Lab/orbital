/**
 * @(#)ClipboardObserver.java 0.9 1999/03/29 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

/**
 * This class is a ClipboardObserver that automatically enables or disables
 * the objects registered in its corresponding list, depending upon
 * Clipboard content.
 * 
 * @version 0.5, 1999/03/29
 * @author  Andr&eacute; Platzer
 */
public class ClipboardObserver extends EnablerDisabler implements ClipboardOwner {

    /**
     * the clipboard observed for data with acceptable flavor
     * @serial
     */
    protected Clipboard  observedClipboard;

    /**
     * the flavors looked for
     * @serial
     */
    protected DataFlavor supportedFlavors[];
    public ClipboardObserver(Clipboard clipboard, DataFlavor supportedFlavors[]) {
	this.supportedFlavors = supportedFlavors;
	this.observedClipboard = clipboard;
	if (!hasAcceptableContents())
	    if (isEmpty())
		clipboard.setContents(new EmptyTransferable(), this);	 // set null and notify me
    }
    public ClipboardObserver(Clipboard clipboard) {
	this(clipboard, null);
    }

    /**
     * Called to update the states of all Objects registered to this Observable.
     */
    public void update() {
	if (true)	 // if (hasAcceptableContents())
	    apply(Boolean.TRUE);
	else
	    apply(Boolean.FALSE);
    } 

    /**
     * Returns whether the clipboard has content whose flavors match our supported flavors.
     * If our supportedFlavors are <code>null</code> every content is acceptable.
     */
    public boolean hasAcceptableContents() {
	Transferable xfer = observedClipboard.getContents(this);
	if (xfer == null)
	    return false;
	if (supportedFlavors == null)
	    return true;
	for (int i = 0; i < supportedFlavors.length; i++)
	    if (xfer.isDataFlavorSupported(supportedFlavors[i]))
		return true;
	return false;
    } 

    /**
     * Returns whether the clipboard is empty.
     */
    public boolean isEmpty() {
	Transferable xfer = observedClipboard.getContents(this);
	if (xfer == null)
	    return true;
	DataFlavor[] flavors = xfer.getTransferDataFlavors();
	if (flavors == null)
	    return true;
	return false;
    } 

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
	update();
    } 
}
