package orbital.awt;


// Title:        Your Product Name
// Version:
// Copyright:    Copyright (c) 1999
// Author:       André
// Company:
// Description:  Your description

import java.awt.*;
import orbital.math.Vector;
import orbital.math.Values;
import java.awt.event.*;
import java.beans.*;

//@todo introduce symmetric property that always fires changes to min and max simultaneously, only with negated signs.
public class Plot2DCustomizer extends Panel implements Customizer {
    Plot2D		  plot = null;
    ChartModel	  model = null;
    Panel		  panel1 = new Panel();
    GridBagLayout gridBagLayout2 = new GridBagLayout();
    NumberInput   xscale = new NumberInput();
    NumberInput   yscale = new NumberInput();
    NumberInput   ymin = new NumberInput();
    NumberInput   ymax = new NumberInput();
    NumberInput   xmin = new NumberInput();
    Label		  label1 = new Label();
    Label		  label2 = new Label();
    Label		  label3 = new Label();
    NumberInput   xmax = new NumberInput();
    Label		  label4 = new Label();
    Label		  label5 = new Label();
    Label		  label6 = new Label();
    BorderLayout  borderLayout1 = new BorderLayout();
    Checkbox	  autoScaling = new Checkbox();
    Checkbox	  fullScaling = new Checkbox();

    public Plot2DCustomizer() {
	try {
	    jbInit();
	} catch (Exception ex) {
	    ex.printStackTrace();
	} 
    }

    private void jbInit() throws Exception {
	label6.setText("Y-Scale:");
	label5.setText("X-Scale:");
	label4.setText(" to");
	label3.setText("Y-Range:");
	label2.setText(" to");
	label1.setText("X-Range:");
	this.setLayout(borderLayout1);
	panel1.setLayout(gridBagLayout2);
	panel1.setComponentOrientation(null);
	autoScaling.setName("Autoscaling");
	autoScaling.setLabel("Autoscaling");
	autoScaling.addItemListener(new java.awt.event.ItemListener() {

		public void itemStateChanged(ItemEvent e) {
		    autoScaling_itemStateChanged(e);
		} 
	    });
	fullScaling.setLabel("full scale");
	fullScaling.addItemListener(new java.awt.event.ItemListener() {

		public void itemStateChanged(ItemEvent e) {
		    fullScaling_itemStateChanged(e);
		} 
	    });
	xmin.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
		    xmin_propertyChange(e);
		} 
	    });
	ymin.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
		    ymin_propertyChange(e);
		} 
	    });
	xmax.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
		    xmax_propertyChange(e);
		} 
	    });
	ymax.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
		    ymax_propertyChange(e);
		} 
	    });
	xscale.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
		    xscale_propertyChange(e);
		} 
	    });
	yscale.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
		    yscale_propertyChange(e);
		} 
	    });
	this.add(panel1, BorderLayout.CENTER);
	panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(label5, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(xmin, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(xmax, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(label2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(ymin, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(label4, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(ymax, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(xscale, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(yscale, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(label3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(label6, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	panel1.add(fullScaling, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
	this.add(autoScaling, BorderLayout.NORTH);
    } 

    void autoScaling_itemStateChanged(ItemEvent e) {
	boolean as = e.getStateChange() == ItemEvent.SELECTED;
	xmin.setEnabled(!as);
	xmax.setEnabled(!as);
	ymin.setEnabled(!as);
	ymax.setEnabled(!as);
	xscale.setEnabled(!as);
	yscale.setEnabled(!as);
	fullScaling.setEnabled(!as);
	plot.setAutoScaling(as);
    } 

    public void setObject(Object bean) {
	plot = (Plot2D) bean;
	model = plot.getModel();
	xmin.setValue((Number) model.getRange().min.get(0));
	xmax.setValue((Number) model.getRange().max.get(0));
	ymin.setValue((Number) model.getRange().min.get(1));
	ymax.setValue((Number) model.getRange().max.get(1));
	xscale.setValue((Number) model.getScale().get(0));
	yscale.setValue((Number) model.getScale().get(1));
	boolean as = plot.isAutoScaling();
	autoScaling.setState(as);
	xmin.setEnabled(!as);
	xmax.setEnabled(!as);
	ymin.setEnabled(!as);
	ymax.setEnabled(!as);
	xscale.setEnabled(!as);
	yscale.setEnabled(!as);
	fullScaling.setEnabled(!as);
	fullScaling.setState(plot.isFullScaling());
	setVisible(true);
    } 

    private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeListeners.addPropertyChangeListener(listener);
    } 

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeListeners.removePropertyChangeListener(listener);
    } 

    void xmin_propertyChange(PropertyChangeEvent e) {
	Range r = (Range) model.getRange().clone();
	r.min.set(0, Values.getDefaultInstance().valueOf((Number) e.getNewValue()));
	model.setRange(r);
    } 

    void ymin_propertyChange(PropertyChangeEvent e) {
	Range r = (Range) model.getRange().clone();
	r.min.set(1, Values.getDefaultInstance().valueOf((Number) e.getNewValue()));
	model.setRange(r);
    } 

    void xmax_propertyChange(PropertyChangeEvent e) {
	Range r = (Range) model.getRange().clone();
	r.max.set(0, Values.getDefaultInstance().valueOf((Number) e.getNewValue()));
	model.setRange(r);
    } 

    void ymax_propertyChange(PropertyChangeEvent e) {
	Range r = (Range) model.getRange().clone();
	r.max.set(1, Values.getDefaultInstance().valueOf((Number) e.getNewValue()));
	model.setRange(r);
    } 

    void xscale_propertyChange(PropertyChangeEvent e) {
	Vector s = model.getScale();
	s.set(0, Values.getDefaultInstance().valueOf((Number) e.getNewValue()));
	model.setScale(s);
    } 

    void yscale_propertyChange(PropertyChangeEvent e) {
	Vector s = model.getScale();
	s.set(1, Values.getDefaultInstance().valueOf((Number) e.getNewValue()));
	model.setScale(s);
    } 

    void fullScaling_itemStateChanged(ItemEvent e) {
	plot.setFullScaling(e.getStateChange() == ItemEvent.SELECTED);
    } 
}
