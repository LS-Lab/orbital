/**
 * @(#)AWTMonitor.java 0.9 2001/03/28 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.TextListener;
import java.awt.event.WindowListener;

import java.io.PrintWriter;

/**
 * Monitors various awt events.
 * 
 * @version 0.9, 2000/03/10
 * @author  Andr&eacute; Platzer
 */
public class AWTMonitor extends Monitor
    implements ActionListener, AdjustmentListener, AWTEventListener,
	       ComponentListener, ContainerListener, FocusListener,
	       HierarchyBoundsListener, HierarchyListener,
	       InputMethodListener,
	       ItemListener,
	       KeyListener, MouseListener, MouseMotionListener,
	       TextListener, WindowListener {

    public AWTMonitor(PrintWriter wr) {
	super(wr);
    }
    public AWTMonitor() {}

    // java.awt.event.ActionListener
    public void actionPerformed(java.awt.event.ActionEvent e) {
	monitor("actionPerformed", e);
    }

    // java.awt.event.AdjustmentListener
    public void adjustmentValueChanged(java.awt.event.AdjustmentEvent e) {
        monitor("adjustmentValueChanged", e);
    }

    // java.awt.event.AWTEventListener
    public void eventDispatched(java.awt.AWTEvent e) {
        monitor("eventDispatched", e);
    }
    
    // java.awt.event.ComponentListener
    public void componentResized(java.awt.event.ComponentEvent e) {
	monitor("componentResized", e);
    } 

    public void componentMoved(java.awt.event.ComponentEvent e) {
	monitor("componentMoved", e);
    } 

    public void componentShown(java.awt.event.ComponentEvent e) {
	monitor("componentShown", e);
    } 

    public void componentHidden(java.awt.event.ComponentEvent e) {
	monitor("componentHidden", e);
    } 

    // java.awt.event.ContainerListener
    public void componentAdded(java.awt.event.ContainerEvent e) {
	monitor("componentAdded", e);
    }
    public void componentRemoved(java.awt.event.ContainerEvent e) {
	monitor("componentRemoved", e);
    }
    
    // java.awt.event.FocusListener
    public void focusGained(java.awt.event.FocusEvent e) {
	monitor("focusGained", e);
    } 

    public void focusLost(java.awt.event.FocusEvent e) {
	monitor("focusLost", e);
    } 
	
    // java.awt.event.HierarchyBoundsListener
    public void ancestorMoved(java.awt.event.HierarchyEvent e) {
        monitor("ancestorMoved", e);
    }
    public void ancestorResized(java.awt.event.HierarchyEvent e) {
        monitor("ancestorResized", e);
    }
    
    // java.awt.event.HierarchyListener
    public void hierarchyChanged(java.awt.event.HierarchyEvent e) {
        monitor("hierarchyChanged", e);
    }
    
    // java.awt.event.ItemListener
    public void itemStateChanged(java.awt.event.ItemEvent e) {
    	monitor("itemStateChanged", e);
    }

    // java.awt.event.InputMethodListener
    public void inputMethodTextChanged(java.awt.event.InputMethodEvent e) {
    	monitor("inputMethodTextChanged", e);
    }
    public void caretPositionChanged(java.awt.event.InputMethodEvent e) {
    	monitor("caretPositionChanged", e);
    }

    // java.awt.event.KeyListener
    public void keyTyped(java.awt.event.KeyEvent e) {
        monitor("keyTyped", e);
    }
    public void keyPressed(java.awt.event.KeyEvent e) {
        monitor("keyPressed", e);
    }
    public void keyReleased(java.awt.event.KeyEvent e) {
        monitor("keyReleased", e);
    }
    
    // java.awt.event.MouseListener
    public void mouseClicked(java.awt.event.MouseEvent e) {
        monitor("mouseClicked", e);
    }
    public void mousePressed(java.awt.event.MouseEvent e) {
        monitor("mousePressed", e);
    }
    public void mouseReleased(java.awt.event.MouseEvent e) {
        monitor("mouseReleased", e);
    }
    public void mouseEntered(java.awt.event.MouseEvent e) {
        monitor("mouseEntered", e);
    }
    public void mouseExited(java.awt.event.MouseEvent e) {
        monitor("mouseExited", e);
    }

    // java.awt.event.MouseMotionListener
    public void mouseDragged(java.awt.event.MouseEvent e) {
        monitor("mouseDragged", e);
    }
    public void mouseMoved(java.awt.event.MouseEvent e) {
        monitor("mouseMoved", e);
    }

    // java.awt.event.TextListener
    public void textValueChanged(java.awt.event.TextEvent e) {
        monitor("textValueChanged", e);
    }

    // java.awt.event.WindowListener
    public void windowOpened(java.awt.event.WindowEvent e) {
	monitor("windowOpened", e);
    } 

    public void windowClosing(java.awt.event.WindowEvent e) {
	monitor("windowClosing", e);
    } 

    public void windowClosed(java.awt.event.WindowEvent e) {
	monitor("windowClosed", e);
    } 

    public void windowIconified(java.awt.event.WindowEvent e) {
	monitor("windowIconified", e);
    } 

    public void windowDeiconified(java.awt.event.WindowEvent e) {
	monitor("windowDeiconified", e);
    } 

    public void windowActivated(java.awt.event.WindowEvent e) {
	monitor("windowActivated", e);
    } 

    public void windowDeactivated(java.awt.event.WindowEvent e) {
	monitor("windowDeactivated", e);
    } 	
}
