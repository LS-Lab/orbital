/**
 * @(#)SwingMonitor.java 0.9 2001/03/28 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import javax.swing.event.AncestorListener;
import javax.swing.event.CaretListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.UndoableEditListener;

import javax.swing.event.DocumentEvent;
import java.io.PrintWriter;

/**
 * Monitors various swing events.
 * 
 * @version 0.9, 2000/03/10
 * @author  Andr&eacute; Platzer
 */
public class SwingMonitor extends AWTMonitor
    implements AncestorListener,
	       CaretListener,
	       CellEditorListener,
	       ChangeListener,
	       DocumentListener,
	       HyperlinkListener,
	       InternalFrameListener,
	       ListDataListener,
	       ListSelectionListener,
	       MenuDragMouseListener,
	       MenuKeyListener,
	       MenuListener,
	       MouseInputListener,
	       PopupMenuListener,
	       TableColumnModelListener,
	       TableModelListener,
	       TreeExpansionListener,
	       TreeModelListener,
	       TreeSelectionListener,
	       TreeWillExpandListener,
	       UndoableEditListener {

    public SwingMonitor(PrintWriter wr) {
	super(wr);
    }
    public SwingMonitor() {}

    // other events
    protected void monitor(String topic, DocumentEvent e) {
	monitorImpl(topic, e);
    }

    // javax.swing.event.MouseInputListener 

    // javax.swing.event.AncestorListener 
    public void ancestorAdded(javax.swing.event.AncestorEvent e) {
        monitor("ancestorAdded", e);
    }
    public void ancestorRemoved(javax.swing.event.AncestorEvent e) {
        monitor("ancestorRemoved", e);
    }
    public void ancestorMoved(javax.swing.event.AncestorEvent e) {
        monitor("ancestorMoved", e);
    }

    // javax.swing.event.CaretListener 
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        monitor("caretUpdate", e);
    }

    // javax.swing.event.CellEditorListener 
    public void editingStopped(javax.swing.event.ChangeEvent e) {
        monitor("editingStopped", e);
    }
    public void editingCanceled(javax.swing.event.ChangeEvent e) {
        monitor("editingCanceled", e);
    }

    // javax.swing.event.ChangeListener 
    public void stateChanged(javax.swing.event.ChangeEvent e) {
        monitor("stateChanged", e);
    }

    // javax.swing.event.DocumentListener 
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
        monitor("insertUpdate", e);
    }
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
        monitor("removeUpdate", e);
    }
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
        monitor("changedUpdate", e);
    }

    // javax.swing.event.HyperlinkListener 
    public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent e) {
        monitor("hyperlinkUpdate", e);
    }

    // javax.swing.event.InternalFrameListener 
    public void internalFrameOpened(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameOpened", e);
    }
    public void internalFrameClosing(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameClosing", e);
    }
    public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameClosed", e);
    }
    public void internalFrameIconified(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameIconified", e);
    }
    public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameDeiconified", e);
    }
    public void internalFrameActivated(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameActivated", e);
    }
    public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent e) {
        monitor("internalFrameDeactivated", e);
    }

    // javax.swing.event.ListDataListener 
    public void intervalAdded(javax.swing.event.ListDataEvent e) {
        monitor("intervalAdded", e);
    }
    public void intervalRemoved(javax.swing.event.ListDataEvent e) {
        monitor("intervalRemoved", e);
    }
    public void contentsChanged(javax.swing.event.ListDataEvent e) {
        monitor("contentsChanged", e);
    }

    // javax.swing.event.ListSelectionListener 
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        monitor("valueChanged", e);
    }

    // javax.swing.event.MenuDragMouseListener 
    public void menuDragMouseEntered(javax.swing.event.MenuDragMouseEvent e) {
        monitor("menuDragMouseEntered", e);
    }
    public void menuDragMouseExited(javax.swing.event.MenuDragMouseEvent e) {
        monitor("menuDragMouseExited", e);
    }
    public void menuDragMouseDragged(javax.swing.event.MenuDragMouseEvent e) {
        monitor("menuDragMouseDragged", e);
    }
    public void menuDragMouseReleased(javax.swing.event.MenuDragMouseEvent e) {
        monitor("menuDragMouseReleased", e);
    }

    // javax.swing.event.MenuKeyListener 
    public void menuKeyTyped(javax.swing.event.MenuKeyEvent e) {
        monitor("menuKeyTyped", e);
    }
    public void menuKeyPressed(javax.swing.event.MenuKeyEvent e) {
        monitor("menuKeyPressed", e);
    }
    public void menuKeyReleased(javax.swing.event.MenuKeyEvent e) {
        monitor("menuKeyReleased", e);
    }

    // javax.swing.event.MenuListener 
    public void menuSelected(javax.swing.event.MenuEvent e) {
        monitor("menuSelected", e);
    }
    public void menuDeselected(javax.swing.event.MenuEvent e) {
        monitor("menuDeselected", e);
    }
    public void menuCanceled(javax.swing.event.MenuEvent e) {
        monitor("menuCanceled", e);
    }

    // javax.swing.event.MouseInputListener 
    // javax.swing.event.PopupMenuListener 
    public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
        monitor("popupMenuWillBecomeVisible", e);
    }
    public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
        monitor("popupMenuWillBecomeInvisible", e);
    }
    public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
        monitor("popupMenuCanceled", e);
    }

    // javax.swing.event.TableColumnModelListener 
    public void columnAdded(javax.swing.event.TableColumnModelEvent e) {
        monitor("columnAdded", e);
    }
    public void columnRemoved(javax.swing.event.TableColumnModelEvent e) {
        monitor("columnRemoved", e);
    }
    public void columnMoved(javax.swing.event.TableColumnModelEvent e) {
        monitor("columnMoved", e);
    }
    public void columnMarginChanged(javax.swing.event.ChangeEvent e) {
        monitor("columnMarginChanged", e);
    }
    public void columnSelectionChanged(javax.swing.event.ListSelectionEvent e) {
        monitor("columnSelectionChanged", e);
    }

    // javax.swing.event.TableModelListener 
    public void tableChanged(javax.swing.event.TableModelEvent e) {
        monitor("tableChanged", e);
    }

    // javax.swing.event.TreeExpansionListener 
    public void treeExpanded(javax.swing.event.TreeExpansionEvent e) {
        monitor("treeExpanded", e);
    }
    public void treeCollapsed(javax.swing.event.TreeExpansionEvent e) {
        monitor("treeCollapsed", e);
    }

    // javax.swing.event.TreeModelListener 
    public void treeNodesChanged(javax.swing.event.TreeModelEvent e) {
        monitor("treeNodesChanged", e);
    }
    public void treeNodesInserted(javax.swing.event.TreeModelEvent e) {
        monitor("treeNodesInserted", e);
    }
    public void treeNodesRemoved(javax.swing.event.TreeModelEvent e) {
        monitor("treeNodesRemoved", e);
    }
    public void treeStructureChanged(javax.swing.event.TreeModelEvent e) {
        monitor("treeStructureChanged", e);
    }

    // javax.swing.event.TreeSelectionListener 
    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
        monitor("valueChanged", e);
    }

    // javax.swing.event.TreeWillExpandListener 
    public void treeWillExpand(javax.swing.event.TreeExpansionEvent e) throws javax.swing.tree.ExpandVetoException {
        monitor("treeWillExpand", e);
    }
    public void treeWillCollapse(javax.swing.event.TreeExpansionEvent e) throws javax.swing.tree.ExpandVetoException {
        monitor("treeWillCollapse", e);
    }

    // javax.swing.event.UndoableEditListener 
    public void undoableEditHappened(javax.swing.event.UndoableEditEvent e) {
        monitor("undoableEditHappened", e);
    }
}
