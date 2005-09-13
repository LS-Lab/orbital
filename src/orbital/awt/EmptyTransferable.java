/**
 * @(#)EmptyTransferable.java 0.9 1999/03/29 Andre Platzer
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
 * Empty transferable to clear clipboard. Corresponds to <code>null</code>-Object.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public final class EmptyTransferable implements Transferable {
    protected DataFlavor supportedFlavors[] = null;
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavors;
    } 

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return false;
    } 

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        throw new UnsupportedFlavorException(flavor);
    } 
}
