/*
 * @(#)LabelledComponent.java 0.9 1996/04/11 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Panel;
import java.awt.Label;
import java.awt.Component;
import java.awt.event.*;

/**
 * An Panel Combination of a Label and a Component.
 * 
 * @version 0.9, 04/11/96
 * @author  Andr&eacute; Platzer
 */
public class LabelledComponent extends Panel {

    /**
     * @serial
     */
    protected Label			  label;

    /**
     * @serial
     */
    protected final Component comp;

    public LabelledComponent(String label, Component component) {
	add(this.label = new LabellingLabel(label));
	add(this.comp = component);

	addFocusListener(new FocusAdapter() {

		/**
		 * will pass on focus over to the component.
		 */
		public void focusGained(FocusEvent evt) {
		    comp.requestFocus();
		} 
	    });
    }
}

class LabellingLabel extends Label {
    public LabellingLabel(String label) {
	super(label);
	addMouseListener(new MouseAdapter() {

		/**
		 * will pass on focus over to the parent.
		 */
		public void mouseClicked(MouseEvent evt) {
		    getParent().requestFocus();
		} 
	    });
    }
}
