/*
 * @(#)NumberInput.java 0.9 1996/04/11 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.Panel;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.TextField;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.Color;
import orbital.math.MathUtilities;

/**
 * An InputField for Numbers which can be adjusted by +- buttons.
 * 
 * @version 0.9, 04/11/96
 * @author  Andr&eacute; Platzer
 * @see javax.swing.Spinner
 */
// TODO: register a class as a PropertyEditor for Double.TYPE etc.
// TODO: display with MathUtilities.format(), but keep original number
public
class NumberInput extends Panel {
	private static final long serialVersionUID = -818804052063274016L;

	// TODO: turn into typesafe enum pattern
	// enumeration of styles
	public static final int   TYPING = 0;
	public static final int   SLIDER = 1;
	public static final int   NUMPAD = 2;

	/**
	 * Number value
	 * @serial
	 */
	protected Number		  nvalue;

	/**
	 * the style in which to show.
	 * @serial
	 */
	protected int			  style = TYPING;

	/**
	 * with what precision mouse operations will change the value.
	 * @serial
	 */
	protected double		  precision = 1.0;

	/**
	 * @serial
	 */
	protected boolean		  editable = true;

	/**
	 * @serial
	 */
	protected TextField		  text;
	public NumberInput(Number def) {
		nvalue = def;
		init();
	}
	public NumberInput() {
		this(null);
	}

	/**
	 * get current Number value
	 */
	public Number getValue() {
		return this.nvalue;
	} 

	/**
	 * set Number to value.
	 */
	public void setValue(Number value) {
		setValueImpl(value);
		text.setText(MathUtilities.format(nvalue));
	} 
	private void setValueImpl(Number value) {
		Number old = nvalue;
		this.nvalue = value;
		text.setForeground(getForeground());
		propertyChangeListeners.firePropertyChange("nvalue", old, nvalue);
	} 

	/**
	 * get the style in which to show.
	 */
	public int getStyle() {
		return style;
	} 

	/**
	 * set the style in which to show.
	 */
	public void setStyle(int st) {
		int old = style;
		style = st;
		propertyChangeListeners.firePropertyChange("style", old, style);
	} 

	/**
	 * get precision with that mouse operations will change the value.
	 */
	public double getPrecision() {
		return precision;
	} 

	/**
	 * set precision with that mouse operations will change the value.
	 */
	public void setPrecision(double new_precision) {
		double old = precision;
		precision = new_precision;
		propertyChangeListeners.firePropertyChange("precision", new Double(old), new Double(precision));
	} 

	/**
	 * set whether the value can be changed by the user.
	 */
	public void setEnabled(boolean b) {
		Component[] c = getComponents();
		for (int i = 0; i < c.length; i++)
			c[i].setEnabled(b);
		super.setEnabled(b);
	} 

	// forward focus to text component
	public void addFocusListener(FocusListener l) {
		text.addFocusListener(l);
	} 
	public void removeFocusListener(FocusListener l) {
		text.removeFocusListener(l);
	} 

	/**
	 * @serial
	 */
	private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.addPropertyChangeListener(l);
	} 
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeListeners.removePropertyChangeListener(l);
	} 

	private void init() {
		removeAll();
		{
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			add(text = new TextField(nvalue == null ? "" : MathUtilities.format(nvalue.doubleValue()), 6));
			text.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					try {
						setValueImpl(Double.valueOf(text.getText()));	// don't use setValue !
					} catch (NumberFormatException x) {
						text.setForeground(Color.red);
					} 
				} 
			});
			Button c;
			add(c = new HelperButton("+"));
			c.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setValue(new Double(getValue().doubleValue() + NumberInput.this.precision));
				} 
			});
			add(c = new HelperButton("-"));
			c.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setValue(new Double(getValue().doubleValue() - NumberInput.this.precision));
				} 
			});
		} 
	} 
}

/**
 * buttons are helper buttons only which are used for mouse but not for keyboard operations.
 * rejects focus.
 */
class HelperButton extends Button {
	public HelperButton(String s) {
		super(s);
	}

	public boolean isFocusTraversable() {
		return false;
	} 
}
