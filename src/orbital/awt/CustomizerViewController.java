/**
 * @(#)CustomizerViewController.java 1.0 1999/03/26 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.Customizer;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.awt.Component;
import java.awt.Frame;
import orbital.logic.functor.Function;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import orbital.moon.awt.DefaultCustomizer;
import orbital.moon.awt.JUserDialog;
import orbital.util.InnerCheckedException;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.InputEvent;

/**
 * This class is a CustomizerViewController that can be registered to a Bean and
 * controls showing a Customizer on double-click.
 * <p>
 * A default customizer is used, if no Bean-specific customizer is registered in the BeanInfo
 * class, or no BeanInfo class is available at all. This default customizer already possesses
 * most features of a good dialog for customization by the user. While for complex dialogs, it
 * can even be extended by providing advanced information in a BeanInfo class, or by
 * {@link java.beans.PropertyEditorManager registering new PropertyEditors} to the system.
 * </p>
 * 
 * @version 1.0, 2000/03/31
 * @author  Andr&eacute; Platzer
 * @todo enhance documentation
 */
public class CustomizerViewController extends MouseAdapter implements MouseListener {
    private Frame  parent;
    private String title;
    private static Function defaultCustomizerFactory;
    static {
	setDefaultCustomizerFactory(null);
    }
    public CustomizerViewController(Frame parent, String title) {
	this.parent = parent;
	this.title = title;
    }
    public CustomizerViewController(Frame parent) {
	this(parent, null);
    }

    // get/set properties
    public Frame getParent() {
	return parent;
    } 
    public void setParent(Frame p) {
	parent = p;
    } 
    public String getTitle() {
	return title;
    } 
    public void setTitle(String title) {
	this.title = title;
    } 

    /**
     * Set the factory for default customizers.
     * This factory is called with a beanClass (of type {@link Class}) for which it
     * should instantiate a new default {@link Customizer customizer}. That customizer
     * is used for beans that do not have a specified bean-specific customizer.
     */
    public static final void setDefaultCustomizerFactory(Function/*<Class,Customizer>*/ newDefaultCustomizerFactory) {
	CustomizerViewController.defaultCustomizerFactory = newDefaultCustomizerFactory != null
	    ? newDefaultCustomizerFactory
	    : new Function() {
		    public Object apply(Object beanClass) {
			try {
			    return new DefaultCustomizer((Class) beanClass);
			} catch (IntrospectionException e) {
			    throw new InnerCheckedException("Introspection for Customizer failed: " + e.getMessage(), e);
			}
		    }
		};
    }

    private static final Function/*<Class,Customizer>*/ getDefaultCustomizerFactory() {
	return defaultCustomizerFactory;
    }

    /**
     * Call to show a dialog containing the customizer for a specific bean.
     * <p>
     * Will check to see if bean has a customizer {@link BeanInfo#getCustomizerClass() specified}
     * in its BeanInfo.
     * Otherwise, will use a {@link#setDefaultCustomizer(Function) default customizer}.
     * </p>
     * @see #showCustomizer(Component, String)
     * @see #customizerFor(Class)
     */
    public void showCustomizer(Object bean) {
	try {
	    Class      beanClass = bean.getClass();
	    Customizer custom = customizerFor(beanClass);
	    custom.setObject(bean);
	    BeanInfo info = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
	    BeanDescriptor desc = info.getBeanDescriptor();
	    showCustomizer((Component) custom, desc == null ? null : desc.getDisplayName());
	} catch (IntrospectionException e) {
	    throw new InnerCheckedException("Introspection for Customizer failed: " + e.getMessage(), e);
	} catch (ClassCastException e) {
	    throw new ClassCastException("Customizer for bean " + bean.getClass() + " is invalid: " + e.getMessage());
	} 
    } 

    /**
     * Called to show a customization dialog containing the specified component with the specified display name.
     * Simply displays a dialog, customizer properties and events must already have been set.
     */
    public void showCustomizer(Component custom, String displayName) {
	//TODO: replace by a (modal) JOptionPane sometime?
	// use JFrame.getRootPane().setDefaultButton(JButton);
	JUserDialog dlg = new JUserDialog(parent, title != null ? title : ("Customize " + displayName), custom);
	Closer	    closer = new Closer(dlg, true);
	UIUtilities.setCenter(dlg, parent);
	dlg.start();
    } 

    /**
     * Get the Customizer for a specific bean class.
     * <p>
     * Will check to see if bean has a customizer {@link BeanInfo#getCustomizerClass() specified}
     * in its BeanInfo.
     * Otherwise, will use a {@link#setDefaultCustomizer(Function) default customizer}.
     * </p>
     * @see BeanDescriptor#getCustomizerClass()
     * @see orbital.moon.awt.DefaultCustomizer
     */
    public static final Customizer customizerFor(Class beanClass) throws IntrospectionException {
	try {
	    BeanInfo info = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
	    if (info == null)
		throw new NullPointerException("no BeanInfo for class: " + beanClass);
	    BeanDescriptor desc = info.getBeanDescriptor();
	    if (desc == null)
		throw new NullPointerException("no BeanDescriptor for class: " + beanClass);
	    Class customizerClass = desc.getCustomizerClass();
	    if (customizerClass != null)
		return (Customizer) customizerClass.newInstance();
	    else
		return (Customizer) getDefaultCustomizerFactory().apply(beanClass);
	} catch (InstantiationException x) {
	    throw new IntrospectionException("Customizer could not be instantiated: " + x.getMessage())/*.initCause(x)*/;
	} catch (IllegalAccessException x) {
	    throw new IntrospectionException("Illegal access to Customizer: " + x.getMessage());
	} catch (ClassCastException x) {
	    throw new ClassCastException("Customizer for bean is invalid " + x.getMessage());
	} 
    } 

    public void mouseClicked(MouseEvent e) {
	if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK && e.getClickCount() == 2)
	    showCustomizer(e.getComponent());
    } 
}
