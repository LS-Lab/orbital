/**
 * @(#)DefaultCustomizer.java 1.0 2000/03/30 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital.moon.awt;

import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.Customizer;
import java.beans.Beans;
import java.beans.Introspector;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.beans.IntrospectionException;
import orbital.util.InnerCheckedException;
import orbital.util.SuspiciousError;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditorManager;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Frame;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;
import orbital.awt.UIUtilities;
import orbital.util.Utility;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.text.JTextComponent;

import orbital.awt.NumberInput;
import java.awt.TextField;
import java.awt.TextComponent;

/**
 * Provides a fully-automatic generic JavaBean customizer.
 * This customizer is displaying fields according to the introspection of the target class
 * revealing all bean properties. Thereby, it provides average complexity
 * customizers for all beans, automatically, without a need for programming by hand.
 * <p>
 * For {@link PropertyEditor}s that support a custom editor, this customizer supports an extra
 * method in the {@link PropertyEditor} implementation. If it exists, the method of the signature
 * <pre>
 *    boolean isInlineCustomEditor()
 * </pre>
 * is used to tell whether a custom editor wants to be inlined. If this method returns <code>false</code>,
 * any custom editors are displayed in an exterior dialog upon user request. If it returns <code>true</code>,
 * custom editors will be displayed inline.
 * If this method does not exist, the size of the custom editor is used to determine a sufficient
 * behaviour instead.
 * </p>
 * 
 * @version 1.0, 2000/03/30
 * @author  Andr&eacute; Platzer
 * @author  Ute Platzer
 * @author  Rolf Lohaus
 * @see orbital.awt.CustomizerViewController
 * @see orbital.awt.CustomizerViewController#customizerFor(Class)
 * @todo support multiple register tabs for different groups of properties. One register tab for each BeanInfo, and each BeanInfo got from getAdditionalBeanInfo(). Use ResourceBundle for the names of these tabs à la getClass().getName() + ".tab"
 */
public class DefaultCustomizer extends JPanel implements Customizer {
    private static final Logger logger = Logger.getLogger(DefaultCustomizer.class.getName());
    /**
     * for which class this customizer is intended.
     * @serial
     */
    protected Class				   beanClass;

    /**
     * list of bean properties. same order as propertyEditors and propertyEditorComponents.
     */
    protected PropertyDescriptor[] beanProperties;

    /**
     * list of property editors for bean properties. same order as beanProperties.
     */
    protected PropertyEditor[]	   propertyEditors;

    /**
     * list of visual editor components for bean properties. same order as beanProperties.
     */
    protected Component[]		   propertyEditorComponents;

    /**
     * which bean to customize currently, as set in the last call to setObject(Object).
     * @serial
     * @see #getObject()
     */
    private Object				   bean = null;
	
    /**
     * maximum number of characters to display per default.
     * Zero to disable truncation.
     * @serial
     */
    private int	                   truncation = 30;

    /**
     * whether or not to put spacing between property fields
     * @serial
     */
    private boolean                spacing = false;


    /**
     * Create a default customizer initialized to customize a bean class.
     * @see orbital.awt.CustomizerViewController#customizerFor(Class)
     * @see #init(Class)
     */
    public DefaultCustomizer(Class beanClass) throws IntrospectionException {
	init(beanClass);
    }

    /**
     * Create a default customizer that is not yet initialized to a bean class.
     * <p>
     * In order to use a default customizer created this way, you need to call
     * {@link #init(Class)} explicitly, first.</p>
     * @see orbital.awt.CustomizerViewController#customizerFor(Class)
     * @see #init(Class)
     */
    protected DefaultCustomizer() throws IntrospectionException {}
	
    // get/set methods
	
    /**
     * get the class of the bean this customizer is responsible for.
     */
    public Class getBeanClass() {
	return beanClass;
    }
	
    public int getTruncation() {
	return truncation;
    }
    public void setTruncation(int truncateAfter) {
	truncation = truncateAfter;
    }

    /**
     * Whether or not to put spacing (additional insets) between property fields.
     * Default is disabled.
     *
     * @param value <code>true</code> to enable, <code>false</code> to disable
     */
    public void setSpacing(boolean value) {
	spacing = value;
    }

    /**
     * write-through to enable or disable all content property editor components.
     * @note lightweight components used as property editor components
     *  usually won't support enabling or disabling.
     *@todo test all enabling/disabling cases
     */
    public void setEnabled(boolean enabled) {
	logger.log(Level.FINEST, "DefaultCustomizer.setEnabled={0}", new Boolean(enabled));
	super.setEnabled(enabled);
	// note that setEnabled(false);setEnabled(true); on getComponents() might reenable a component although it was never enabled at all
	// however setEnabled(false); on propertyEditorComponents will let additional components be enabled
	Component components[] = propertyEditorComponents;			//getComponents();
	if (enabled) {
	    for (int i = 0; i < components.length; i++) {
		// only re-enable components that have a property editor (otherwise it's pointless) and who is paintable, or supports a custom editor, or at least is writable
		components[i].setEnabled(enabled && propertyEditors[i] != null &&
					 (
					  propertyEditors[i].isPaintable()
					  || propertyEditors[i].supportsCustomEditor()
					  || beanProperties[i].getWriteMethod() != null
					  )
					 );
	    }
	} else {	// only an optimized version for enabled = false
	    for (int i = 0; i < components.length; i++) {
		components[i].setEnabled(enabled);
	    }
	}
    }

    /**
     * write-through editable to all content property editor
     * components.  Components that don't support a writable editable
     * property will instead have {@link #setEnabled(boolean)} called.
     */
    public void setEditable(boolean editable) {
	final Boolean editableWrapped = editable ? Boolean.TRUE : Boolean.FALSE;
	logger.log(Level.FINEST, "DefaultCustomizer.setEditable={0}", editableWrapped);
	// note that setEditabled(false);setEditabled(true); on getComponents() might reeditable a component although it was never editabled at all
	// however setEditabled(false); on propertyEditorComponents will let additional components be editabled
	Component components[] = propertyEditorComponents;			//getComponents();
	for (int i = 0; i < components.length; i++) {
	    final Component c = unwrapComponent(components[i]);
	    // try to invoke setEditable on c, only if it is a
	    // JTextComponent, such that it seems to have the same
	    // semantic understanding of what setEditable(boolean)
	    // should do
	    if (c instanceof JTextComponent) {
		try {
		    c.getClass().getMethod("setEditable", new Class[] {Boolean.TYPE})
			.invoke(c, new Object[] {editableWrapped});
		    continue;
		}
		catch (NoSuchMethodException noEditableChange) {}
		catch (SecurityException security) {}
		catch (IllegalAccessException inaccessible) {}
		catch (InvocationTargetException exception) {
		    throw new InnerCheckedException("Could not " + c.getClass() + ".setEditable(" + editable +")", exception);
		}
	    }
	    logger.log(Level.FINEST, "searching for {0}.setEditable(boolean) was not successful", c.getClass());
	    //@internal treat like setEnabled(editable)
	    c.setEnabled(editable);
	}
    }

    /**
     * Unwraps components wrapped inside mere "decorators" to find the
     * component that holds the final responsibility for displaying
     * and editing it.
     * @internal recursively unwraps components from scroll panes and
     * InlinePaintablePropertyEditorComponents.
     */
    private static final Component unwrapComponent(Component c) {
	while (true) {
	    // try to find a JTextComponent
	    if ((c instanceof InlinePaintablePropertyEditorComponent)
		&& (((InlinePaintablePropertyEditorComponent)c).customEditor != null)) {
		c = ((InlinePaintablePropertyEditorComponent)c).customEditor;
	    } else if (c instanceof JScrollPane) {
		c = ((JScrollPane)c).getViewport().getView();
	    } else {
		return c;
	    }
	}
    }
    
    // visual view methods
	
    /**
     * Initializes default customizer view according to the BeanInfo of the given object.
     * Will add a property sheet to this DefaultCustomizer panel.
     * @param beanClass for which class of objects this customizer is intended.
     */
    protected void init(Class beanClass) throws IntrospectionException {
	this.beanClass = beanClass;
	BeanInfo info = getBeanInfo(beanClass);
	if (info == null)
	    throw new NullPointerException("no BeanInfo for class: " + beanClass);
	beanProperties = getAllPropertyDescriptors(info);
	if (beanProperties == null)
	    throw new NullPointerException("no PropertyDescriptors for class: " + beanClass);

	this.removeAll();
	this.setLayout(new GridBagLayout());
	GridBagConstraints l = new GridBagConstraints();	// left labels
	l.anchor = GridBagConstraints.NORTHWEST;
	l.insets = new Insets(0, 0, 0, 12);
	GridBagConstraints r = new GridBagConstraints();	// right components
	r.gridwidth = GridBagConstraints.REMAINDER;
	r.anchor = GridBagConstraints.NORTHWEST;
	r.fill = GridBagConstraints.BOTH;
	r.weightx = 1;
	GridBagConstraints v = new GridBagConstraints();	// vertical spacing
	v.gridwidth = GridBagConstraints.REMAINDER;
	v.anchor = GridBagConstraints.NORTH;
	v.fill = GridBagConstraints.BOTH;
	GridBagConstraints f = new GridBagConstraints();	// vertical filler
	f.gridwidth = GridBagConstraints.REMAINDER;
	f.anchor = GridBagConstraints.NORTH;
	f.fill = GridBagConstraints.BOTH;
	f.weighty = 0.5;

	propertyEditors = new PropertyEditor[beanProperties.length];
	propertyEditorComponents = new Component[beanProperties.length];
	for (int i = 0; i < beanProperties.length; i++) {
	    if (beanProperties[i].isHidden()) {
		// not shown
		propertyEditors[i] = null;
		propertyEditorComponents[i] = null;
		continue;
	    }

	    // Inserts a vertical component spacing. The height of the space is
	    // 5 pixels as specified in the Java Look and Feel Design Guidelines
	    if (spacing && (i != 0)) {
		Component verticalStrut = Box.createVerticalStrut(5);
		this.add(verticalStrut, v);
	    }

	    String displayName = beanProperties[i].getDisplayName();
	    // add colon to label if there is none
	    if ((displayName != null) && (displayName.length() != 0)
		&& (displayName.charAt(displayName.length() - 1) != ':'))
	    {
		displayName += ":";
	    }
	    JLabel label = new JLabel(displayName);
	    label.setToolTipText(beanProperties[i].getShortDescription());
	    this.add(label, l);


	    // get a bean defined or property editor manager defined property editor.
	    propertyEditors[i] = getPropertyEditor(beanProperties[i]);
	    propertyEditorComponents[i] = createPropertyEditorComponent(beanProperties[i], propertyEditors[i]);

	    // register to property editor property changes
	    registerPropertyChangeUpdater(beanProperties[i], propertyEditors[i], propertyEditorComponents[i]);
	    this.add(propertyEditorComponents[i], r);
	    label.setLabelFor(propertyEditorComponents[i]);
	}

	// Inserts a vertical filler to occupy any remaining vertical space
	// at the bottom.
	if (spacing) {
	    Component verticalGlue = Box.createVerticalGlue();
	    this.add(verticalGlue, f);
	}
    }

    /**
     * Create a component to use as an editor for the property specified.
     * <p>
     * If a special system-default display component is used, a listener to that component
     * updating the property editor will have been set as well.
     * It is set to a listener that forwards the display component-specific event
     * to a generic property change event of the PropertyEditor.
     * </p>
     * @see PropertyEditor#isPaintable()
     * @see PropertyEditor#supportsCustomEditor()
     * @see PropertyEditor#getTags()
     * @see DefaultCustomizer.PropertyEditingChange
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    protected Component createPropertyEditorComponent(PropertyDescriptor property, PropertyEditor peditor) throws IntrospectionException {
	logger.log(Level.FINER, "property {1} of {0} has editor {2}", new Object[] {beanClass, UIUtilities.info(property), peditor});
	if (peditor == null) {
	    // display default non-editable representation
	    JTextField editor = new JTextField();
	    editor.setName(property.getName());
	    editor.setEditable(false);
	    return editor;
	} 
	if (peditor.isPaintable() || peditor.supportsCustomEditor()) {
	    // property editor is painting itself or customizing itself
	    if (peditor.supportsCustomEditor()) {
		// property editor is customizing itself
		Dimension dim = peditor.getCustomEditor().getPreferredSize();
		try {
		    // virtual interface method call
		    if (((Boolean) peditor.getClass().getMethod("isInlineCustomEditor", null).invoke(peditor, null)).booleanValue())
			return new InlinePaintablePropertyEditorComponent(property, peditor);
		    else
			return new ExteriorPaintablePropertyEditorComponent(property, peditor);
		}
		catch (NoSuchMethodException trial) {}
		catch (IllegalAccessException trial) {}
		catch (ClassCastException trial) {}
		catch (IllegalArgumentException imp) {}
		catch (InvocationTargetException ignore) {}
		catch (SecurityException ignore) {}
		return (dim.width <= 200 && dim.height <= 120)
		    ? (Component) new InlinePaintablePropertyEditorComponent(property, peditor)
		    : (Component) new ExteriorPaintablePropertyEditorComponent(property, peditor);
	    } else
		// property editor is only painting itself, so display inline
		return new InlinePaintablePropertyEditorComponent(property, peditor);
	} else if (peditor.getTags() != null) {
	    // display a choice of all possible tags
	    String[] tags = peditor.getTags();
	    JComboBox   editor = new JComboBox();
	    for (int i = 0; i < tags.length; i++)
		editor.addItem(tags[i]);
	    editor.setName(property.getName());
	    if (property.getWriteMethod() != null)
		editor.addActionListener(new PropertyEditingChange(peditor));
	    else
		editor.setEnabled(false);
	    return editor;
	} else {
	    // display a text editor
	    JTextField editor = new JTextField();
	    editor.setName(property.getName());
	    if (property.getWriteMethod() != null)
		editor.getDocument().addDocumentListener(new PropertyEditingChange(peditor));
	    else
		editor.setEditable(false);
	    return editor;
	} 
    } 
    
    // factor support methods
	
    /**
     * Register to property change events of the property editor.
     * <p>
     * Register a property updater updating the bean's property specified
     * in the property descriptor with the values of the given property editor
     * on changes.
     * </p>
     * <p>
     * This central method can be overwritten to implement
     * </p>
     * @param property descriptor of the property to update.
     *  Usage of property is both, get and set.
     * @param peditor the property editor to set and get the values from.
     *  Its getValue() method is considered for the value to set for the bean's property.
     *  Usage of peditor is read-only.
     * @param propertyEditorComponent visual editor component for property.
     * @internal see DefaultCustomizer.PropertyUpdater
     */
    protected void registerPropertyChangeUpdater(PropertyDescriptor property, PropertyEditor peditor, Component propertyEditorComponent) {
	if (peditor != null)
	    peditor.addPropertyChangeListener(new PropertyUpdater(property, peditor, propertyEditorComponent));
    }

    /**
     * Checks whether we find a customizer for the type of the property specified.
     * @see BeanDescriptor#getCustomizerClass()
     */
    protected static boolean customizerForProperty(PropertyDescriptor property) {
	try {
	    BeanInfo info = Introspector.getBeanInfo(property.getPropertyType());
	    if (info == null)
		return false;
	    BeanDescriptor desc = info.getBeanDescriptor();
	    if (desc == null)
		return false;
	    return desc.getCustomizerClass() != null;
	} catch (IntrospectionException x) {
	    return false;
	} 
    } 

    /**
     * Get a property editor for the property specified.
     * @see PropertyDescriptor#getPropertyEditorClass()
     * @see PropertyEditorManager#findEditor(Class)
     */
    protected static PropertyEditor getPropertyEditor(PropertyDescriptor property) throws IntrospectionException {
	// get a bean defined or property editor manager defined property editor.
	Class propertyEditorClass = property.getPropertyEditorClass();
	if (propertyEditorClass == null)
	    if (property.getPropertyType() == null)
		// indexed property that does not support non-indexed access
		//@todo use our own IndexedPropertyEditor (that has a CustomEditor)
		return null;
	    else
		return PropertyEditorManager.findEditor(property.getPropertyType());
	else
	    try {
		return (PropertyEditor) propertyEditorClass.newInstance();
	    } catch (IllegalAccessException x) {
		throw new IntrospectionException("invalid property editor class" + x);
	    } catch (InstantiationException x) {
		throw new IntrospectionException("invalid property editor class" + x);
	    } 
    } 

    /**
     * Get the object currently customized.
     * @return the bean object currently customized, as set in the last call to {@link #setObject(Object)}.
     * @see #setObject(Object)
     */
    protected Object getObject() {
	return bean;
    }

    // customization run methods
	
    /**
     * Set the object to be customized now.
     * @param bean the bean object to be customized.
     *  Requires bean to be the kind of type specified in the constructor.
     *  <code>null</code> will clear all values displayed.
     */
    public void setObject(Object bean) {
	if (bean != null && !Beans.isInstanceOf(bean, beanClass))
	    throw new IllegalArgumentException("instance initialized for another type: " + beanClass + ". Could not accept argument of type " + (bean != null ? bean.getClass().toString() : "null"));
	try {
	    this.bean = null;
	    update(bean);
	    this.bean = bean;
	} catch (IntrospectionException e) {
	    throw new InnerCheckedException(e);
	} 
	setVisible(true);
    } 

    /**
     * Update all values displayed for the given bean.
     * Gets all property values from the bean, and
     * set representation for their property editor and property editor component.
     */
    protected void update(Object bean) throws IntrospectionException {
	try {
	    for (int i = 0; i < beanProperties.length; i++) {
		if (beanProperties[i].isHidden())
		    // not shown
		    continue;
		if (getReadMethod(beanProperties[i]) == null)
		    // value cannot be displayed
		    continue;
		if (beanProperties[i] instanceof IndexedPropertyDescriptor
		    && beanProperties[i].getReadMethod() == null)
		    //@XXX: what to do for indexed property read methods without support for non-indexed access?
		    continue;
		Object		   v = bean != null ? beanProperties[i].getReadMethod().invoke(bean, null) : null;
		PropertyEditor ped = propertyEditors[i];
		Component	   ed = propertyEditorComponents[i];
		if (ped == null) {
		    // default non-editable representation
		    displaying(ed, v);
		    continue;
		} 
		try {
		    ped.setValue(v);
		    if (ped.isPaintable() || ped.supportsCustomEditor())
			// property managed customization
			;
		    else if ((ed instanceof JTextComponent) || (ed instanceof TextComponent))
			displaying(ed, ped.getAsText());
		    else if (ed instanceof JComboBox)
			((JComboBox) ed).setSelectedItem(ped.getAsText());
		    else if (ed instanceof java.awt.Choice)
			((java.awt.Choice) ed).select(ped.getAsText());
		    else if (ed instanceof NumberInput)
			//NOTE: NumberInput is no javax.swing.JComponent at all, see javax.swing.S...
			((NumberInput) ed).setValue((Number) ped.getValue());
		    else
			throw new IllegalStateException("unsupported editor component " + ed + " for property " + beanProperties[i].getName());
		}
		// swallow exceptions if they were only due to a clearing with setObject(null)
		catch (NullPointerException x) {if (bean != null) throw x;}
		catch (IllegalArgumentException x) {if (bean != null) throw x;}
	    } 
	} catch (InvocationTargetException ex) {
	    throw (IntrospectionException) new IntrospectionException("nested " + ex + ": " + ex.getTargetException().getMessage()).initCause(ex);
	} catch (IllegalAccessException ex) {
	    throw (IntrospectionException) new IntrospectionException("nested " + ex).initCause(ex);
	} 
    } 
    
    // support methods
	
    /**
     * Set the displaying version of a string in a component.
     * Truncates the number of columns displayed to shorten.
     * @param value the stringified value to display
     */
    private void displaying(Component ed, Object value) {
	String s = value == null ? "<null>" : value.toString();
	if (ed instanceof TextComponent) {
	    ((TextComponent) ed).setText(s);
	    if (ed instanceof TextField) {
		TextField c = (TextField) ed;
		if (s.length() > truncation)
		    c.setColumns(truncation);
	    }
	} else if (ed instanceof JTextComponent) {
	    ((JTextComponent) ed).setText(s);
	    if (ed instanceof JTextField) {
		JTextField c = (JTextField) ed;
		if (s.length() > truncation)
		    c.setColumns(truncation);
	    }
	} else
	    throw new ClassCastException("illegal argument");
    } 

    /**
     * Get the read method of the property (indexed if possible).
     */
    private static Method getReadMethod(PropertyDescriptor property) {
	if (property instanceof IndexedPropertyDescriptor) {
	    Method indexed = ((IndexedPropertyDescriptor) property).getIndexedReadMethod();
	    if (indexed != null)
		return indexed;
	}
	return property.getReadMethod();
    }

    /**
     * Get the BeanInfo of a class.
     * @see Introspector#getBeanInfo(Class, int)
     */
    protected BeanInfo getBeanInfo(Class cls) throws IntrospectionException {
        return Introspector.getBeanInfo(cls, Introspector.USE_ALL_BEANINFO);
    }

    /**
     * Get all PropertyDescriptors from the BeanInfo including its additonal BeanInfos.
     * @see BeanInfo#getAdditionalBeanInfo()
     */
    protected PropertyDescriptor[] getAllPropertyDescriptors(BeanInfo info) {
	BeanInfo additional[] = info.getAdditionalBeanInfo();
	if (additional == null || additional.length == 0)
	    //RA:
	    return info.getPropertyDescriptors();
	// set without duplicates that is not consistent with equals (which does not matter)
	// Caution: using a Set instead of a List here shuffles the property order which Introspector does, anyway (Bug-Id 4088897 closed)
	//@internal perhaps we could also use a LinkedHashSet or even LinkedIdentityHashSet here for keeping the initial order instead of sorting
	Set l = new TreeSet(featureDescriptorComparator);
	l.addAll(Arrays.asList(info.getPropertyDescriptors()));
	// add all properties not yet contained
	for (int i = additional.length - 1; i>= 0; i--) {
	    //RS:
	    PropertyDescriptor property[] = getAllPropertyDescriptors(additional[i]);
	    if (property == null)
		continue;
	    l.addAll(Arrays.asList(property));
	}
	return (PropertyDescriptor[]) l.toArray(new PropertyDescriptor[0]);
    }
    /**
     * Compare FeatureDescriptors according to their name.
     * Perhaps inconsistent with equals!
     * Not yet serializable
     */
    private static final Comparator featureDescriptorComparator = new Comparator() {
	    public int compare(Object a, Object b) {
		return ((FeatureDescriptor) a).getName().compareTo(((FeatureDescriptor) b).getName());
	    }
	};

    private final PropertyChangeSupport propertyChangeListeners = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeListeners.addPropertyChangeListener(listener);
    } 

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	propertyChangeListeners.removePropertyChangeListener(listener);
    } 



    // property change write-through listeners
	
    /**
     * The central property updater that will change bean properties on events.
     * Registered to property editors.
     * @see #registerPropertyChangeUpdater(PropertyDescriptor, PropertyEditor, Component)
     */
    private class PropertyUpdater implements PropertyChangeListener {
	protected PropertyDescriptor property;
	protected PropertyEditor	 peditor;
	protected Component			 propertyEditorComponent;

	/**
	 * Create a new property updater updating the bean's property specified
	 * in the property descriptor with the values of the given property editor.
	 * @param property descriptor of the property to update.
	 *  Usage of property is both, get and set.
	 * @param peditor the property editor to set and get the values from.
	 *  Its getValue() method is considered for the value to set for the bean's property.
	 *  If peditor is <code>null</code>, the event source is assumed to be the corresponding
	 *  property editor.
	 *  Usage of peditor is read-only.
	 * @param propertyEditorComponent visual editor component for property.
	 *  Used for repainting on changes.
	 */
	public PropertyUpdater(PropertyDescriptor property, PropertyEditor peditor, Component propertyEditorComponent) {
	    this.property = property;
	    this.peditor = peditor;
	    this.propertyEditorComponent = propertyEditorComponent;
	}

	/**
	 * Set the value of the bean's property associated with this property updater
	 * according to the value of the property change event that occured.
	 */
	public void propertyChange(PropertyChangeEvent e) {
	    try {
		if (bean == null)
		    return;
		logger.log(Level.FINER, "change event {0} from \"{1}\" to \"{2}\", via {3}", new Object[] {property.getName(), e.getOldValue(), e.getNewValue(), e});
		//XXX: should we use e.getNew...Value() or the PropertyEditor, now?
		// get old and new value from event
		Object oldValue = e.getOldValue();
		Object newValue = e.getNewValue();
		// whether all has changed from all to everything
		boolean  allChanged = oldValue == null && newValue == null;

		// get old value from bean, new value from PropertyEditor
		PropertyEditor ped = peditor != null ? peditor : (PropertyEditor) e.getSource();
		Object		   oldFromBean = property.getReadMethod().invoke(bean, null);
		Object		   newFromPed = ped.getValue();
		logger.log(Level.FINEST, "change verify that old value fired ({0}) equals current value of bean ({2})\n"
			   + "new value fired ({1}) equals value of property editor ({3})", new Object[] {oldValue, newValue, oldFromBean, newFromPed});

		// avoid property changes for identical old and new values, except when all changed.
		// (according to both event and bean/ped info)
		if (allChanged) {
		    // use old values from bean and new values from PropertyEditor, instead
		    oldValue = oldFromBean;
		    newValue = newFromPed;
		} else if (Utility.equals(newValue, oldValue))
		    // old and new value of event are equal
		    if (Utility.equals(newFromPed, oldFromBean))
			// old value from bean and new value from PropertyEditor are equal, too
			return;
		    else {
			// use old values from bean and new values from PropertyEditor, instead
			oldValue = oldFromBean;
			newValue = newFromPed;
		    }

		// property really changed, write-through
		property.getWriteMethod().invoke(bean, new Object[] {newValue});
		propertyChangeListeners.firePropertyChange(property.getName(), oldValue, newValue);
		logger.log(Level.FINER, "changed {0} from \"{1}\" to \"{2}\", via {3}", new Object[] {property.getName(), oldValue, newValue, e});
				
		// repaint if necessary, i.e. property editor is paintable but we are responsible for repainting it
		if (propertyEditorComponent != null && peditor.isPaintable())
		    //@todo would only need this for things like InlinePaintablePropertyEditorComponent
		    propertyEditorComponent.repaint();
	    } catch (InvocationTargetException x) {
		try {
		    throw x.getTargetException();
		} catch (PropertyVetoException cause) {
		    logger.log(Level.WARNING, "illegal value", cause);
		    //@todo what if we simply zigzag underlined the corresponding field (if possible), and displayed a message in a separate (parametric) status bar?
		    JOptionPane.showMessageDialog(DefaultCustomizer.this, cause.getMessage(), "value rejected", JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException cause) {
		    logger.log(Level.WARNING, "illegal value", cause);
		    JOptionPane.showMessageDialog(DefaultCustomizer.this, cause.getMessage(), "illegal value", JOptionPane.ERROR_MESSAGE);
		} catch (NullPointerException cause) {
		    logger.log(Level.WARNING, "illegal value", cause);
		    JOptionPane.showMessageDialog(DefaultCustomizer.this, "null value " + cause.getMessage(), "illegal value", JOptionPane.ERROR_MESSAGE);
		} catch (Throwable rethrow) {throw new InnerCheckedException(rethrow);}
	    } catch (IllegalAccessException ex) {
		throw new InnerCheckedException(ex);
	    } catch (IllegalArgumentException cause) {
		// TODO: display Dialog!
		logger.log(Level.WARNING, "illegal value", cause);
	    } 
	} 
    }



    // visual property editor component forwarding listener
	
    /**
     * Reacts on system-default GUI components property editing with redirection to a Property Editor.
     * Forwards display component specific events to general property changes of the PropertyEditor.
     */
    private class PropertyEditingChange implements ActionListener, ItemListener, DocumentListener, InputMethodListener, TextListener {
	protected PropertyEditor peditor;

	/**
	 * Create a new property editing redirector, updating the value
	 * of the given property editor according to the GUI events it receives.
	 * @param peditor the property editor to set the values for.
	 * This property editors set methods will be called after a change
	 * in the GUI component we are listening to.
	 */
	public PropertyEditingChange(PropertyEditor peditor) {
	    this.peditor = peditor;
	}
        
        // ActionListener
		
	/**
	 * Set the value of the property editor to notify.
	 */
	public void actionPerformed(ActionEvent e) {
	    if (bean == null)
		return;
	    try {
		Object source = e.getSource();
		String v;
		if (source instanceof JComboBox)
		    v = (String) ((JComboBox) e.getSource()).getSelectedItem();
		else
		    throw new ClassCastException("illegal event source type " + source.getClass());
		peditor.setAsText(v);
	    } catch (IllegalArgumentException cause) {
		// TODO: display Dialog or redden the component foreground!
		logger.log(Level.WARNING, "illegal value", cause);
	    } 
	} 

	// ItemListener

	/**
	 * Set the value of the property editor to notify.
	 */
	public void itemStateChanged(ItemEvent e) {
	    if (bean == null)
		return;
	    if (e.getStateChange() != ItemEvent.SELECTED)
		return;
	    try {
		String v = "" + e.getItem();
		peditor.setAsText(v);
	    } catch (IllegalArgumentException cause) {
		// TODO: display Dialog!
		logger.log(Level.WARNING, "illegal value", cause);
	    } 
	} 

	// DocumentListener

        public void insertUpdate(DocumentEvent e) {
	    documentUpdate(e);
	}
        public void removeUpdate(DocumentEvent e) {
	    documentUpdate(e);
	}
        public void changedUpdate(DocumentEvent e) {}
        private void documentUpdate(DocumentEvent e) {
	    if (bean == null)
		return;
	    String v = null;
	    try {
		Document doc = e.getDocument();
		v = doc.getText(0, doc.getLength());
		peditor.setAsText(v);
		//((Component) e.getSource()).setForeground(null);
	    }
	    catch (BadLocationException imp) {throw new SuspiciousError("strange document. " + imp);}
	    catch (NumberFormatException cause) {
		if (v != null && v.length() == 0)
		    // ignore number format exceptions with empty strings since they occur too often when editing.
		    // This is due to the problem that they somehow update in intermediate states as well, instead of after loosing the focus, only
		    return;
		// TODO: display Dialog or redden the component foreground!
		logger.log(Level.WARNING, "illegal value", cause.getMessage());
		//((Component) e.getSource()).setForeground(Color.red);
	    } 
	    catch (IllegalArgumentException cause) {
		// TODO: display Dialog or redden the component foreground!
		logger.log(Level.WARNING, "illegal value", cause);
		//((Component) e.getSource()).setForeground(Color.red);
	    } 
	}

	// InputMethodListener

	public void inputMethodTextChanged(InputMethodEvent e) {
	    if (bean == null)
		return;
	    try {
		String v = ((JTextComponent) e.getSource()).getText();
		peditor.setAsText(v);
	    } catch (IllegalArgumentException cause) {
		// TODO: display Dialog!
		logger.log(Level.WARNING, "illegal value", cause);
	    } 
	} 
	/**
	 * method required by Interface InputMethodListener, but not
	 * necessary here.
	 */
	public void caretPositionChanged(InputMethodEvent event) {}

	// TextListener

	public void textValueChanged(TextEvent e) {
	    if (bean == null)
		return;
	    try {
		String v = ((java.awt.TextComponent) e.getSource()).getText();
		peditor.setAsText(v);
	    } catch (IllegalArgumentException cause) {
		// TODO: display Dialog!
		logger.log(Level.WARNING, "illegal value", cause);
	    } 
	} 
    }



    // property editor managed visual property editor component

    /**
     * A component displaying a property editor.
     * The property editor's paintValue method is used if paintable.
     * Otherwise default displaying is used.
     */
    private class PropertyEditorComponent extends JPanel {
	protected PropertyEditor peditor;

	/**
	 * Create a new property editor component for paintable and customizable
	 * property editors.
	 * It is delegating paints and double-click customizing to the property editor.
	 * @param property descriptor of the property to update.
	 * @param peditor the property editor to set and get the values which from.
	 * This property editors set methods will be called after a change
	 * and the result of its getValue() method is set for the bean's property.
	 */
	public PropertyEditorComponent(PropertyEditor peditor) {
	    this.peditor = peditor;
	    setLayout(new BorderLayout());
	}

	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Dimension d = getSize();
	    if (peditor.isPaintable())
		peditor.paintValue(g, new Rectangle(d));
	    else
		g.drawString(peditor.getValue() == null ? "<null>" : "...", 0, d.height * 4 / 5);
	} 

	void paintSuperComponent(Graphics g) {
	    super.paintComponent(g);
	}

	//@xxx shouldn't we use plaf colors, somehow
	// change color to grey when we are disabled
	public void setEnabled(boolean enabled) {
	    logger.log(Level.FINEST, "PropertyEditorComponent.setEnabled={0}", new Boolean(enabled));
	    boolean oldEnabled = isEnabled();
	    super.setEnabled(enabled);
	    if (enabled != oldEnabled) {
		setForeground(enabled ? java.awt.SystemColor.textText : java.awt.SystemColor.textInactiveText);
		setBackground(enabled ? java.awt.SystemColor.control : java.awt.SystemColor.text);
	    }
	}
    }

    /**
     * A component displaying a property editor that is paintable and small enough to fit inline.
     * If the property editor is not paintable, default displaying is used.
     * Can be edited inline if the property editor supports a customizer.
     */
    private class InlinePaintablePropertyEditorComponent extends PropertyEditorComponent {
	private final Component customEditor;

	/**
	 * Create a new property editor component for paintable and customizable
	 * property editors.
	 * It is delegating paints and double-click customizing to the property editor.
	 * @param property descriptor of the property to update.
	 * @param peditor the property editor to set and get the values which from.
	 * This property editors set methods will be called after a change
	 * and the result of its getValue() method is set for the bean's property.
	 */
	public InlinePaintablePropertyEditorComponent(final PropertyDescriptor property, final PropertyEditor peditor) {
	    super(peditor);
	    this.customEditor = peditor.supportsCustomEditor() ? peditor.getCustomEditor() : null;
	    if (customEditor != null)
		add(customEditor, BorderLayout.CENTER);
	    logger.log(Level.FINER, "inline property editor component: create inline property editor component for {0} with visual content {1}", new Object[] {property, customEditor});
	}
	
	// no insets or padding at all
	public Dimension getMinimumSize() {
	    return customEditor != null ? customEditor.getMinimumSize() : super.getMinimumSize();
	}

	public Dimension getPreferredSize() {
	    return customEditor != null ? customEditor.getPreferredSize() : super.getPreferredSize();
	}

	public Dimension getMaximumSize() {
	    return customEditor != null ? customEditor.getMaximumSize() : super.getMaximumSize();
	}

	/**
	 * @structure delegate customEditor | super
	 */
	public void paintComponent(Graphics g) {
	    if (customEditor != null) {
		super.paintSuperComponent(g);
		// already painted in member component customEditor
		/*g.clipRect(0, 0, d.width, d.height);
		  if (customEditor instanceof JComponent)
		  ((JComponent) customEditor).paintComponent(g);
		  else
		  customEditor.paint(g);*/
	    } else
		super.paintComponent(g);
	}
		
	/**
	 * write-through enabled changes to custom editor components.
	 * @structure delegate super & customEditor
	 */
	public void setEnabled(boolean enabled) {
	    super.setEnabled(enabled);
	    if (customEditor != null)
		customEditor.setEnabled(enabled);
	}
    }

    /**
     * A component displaying a property editor that is paintable but large.
     * If the property editor is not paintable, default displaying is used.
     * Can be edited externally in a pop-up window if the property editor supports a customizer.
     */
    private class ExteriorPaintablePropertyEditorComponent extends PropertyEditorComponent {
	/**
	 * Create a new property editor component for paintable and customizable
	 * property editors.
	 * It is delegating paints and double-click customizing to the property editor.
	 * @param property descriptor of the property to update.
	 * @param peditor the property editor to set and get the values which from.
	 * This property editors set methods will be called after a change
	 * and the result of its getValue() method is set for the bean's property.
	 */
	public ExteriorPaintablePropertyEditorComponent(final PropertyDescriptor property, final PropertyEditor peditor) {
	    super(peditor);
	    if (peditor.supportsCustomEditor())
		//@todo test that this mouselistener cannot fire when disabled
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    if (bean == null)
				return;
			    logger.log(Level.FINEST, "exterior property editor component: open the custom editor that is supported");
			    //if (e.getClickCount() == 2)
			    //XXX: how to get changes with the PropertyChangeListener to changing the value?
			    new orbital.awt.CustomizerViewController((Frame) SwingUtilities.getAncestorOfClass(Frame.class, DefaultCustomizer.this)).showCustomizer(peditor.getCustomEditor(), property.getDisplayName());
			} 
		    });
	    //@todo could we use {1,choice} somehow?
	    logger.log(Level.FINER, "exterior property editor component: create exterior property editor component for {0} {1} custom editor support", new Object[] {property, peditor.supportsCustomEditor() ? "with" : "without"});
	}
	public void setEnabled(boolean enabled) {
	    //@xxx since we seem to be a lightweight component, Java does not disable MouseEvents just because we are totally disabled
	    super.setEnabled(enabled);
	}
    }
}

