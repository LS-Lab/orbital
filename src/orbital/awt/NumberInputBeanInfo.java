// Title:        Your Product Name
// Version:
// Copyright:    Copyright (c) 1999
// Author:       André
// Company:
// Description:  Your description

package orbital.awt;

import java.beans.*;

/**
 * @exclude
 */
public class NumberInputBeanInfo extends SimpleBeanInfo {
    Class  beanClass = NumberInput.class;
    String iconColor16x16Filename = "NumberInput16.gif";
    String iconColor32x32Filename;
    String iconMono16x16Filename;
    String iconMono32x32Filename;

    public NumberInputBeanInfo() {}

    public BeanDescriptor getBeanDescriptor() {
	BeanDescriptor bd = new BeanDescriptor(beanClass);
	bd.setName("NumberInput");
	bd.setDisplayName("NumberInput");
	bd.setShortDescription("field for numerical input in various styles");
	return bd;
    } 

    /*
     * public int getDefaultEventIndex() {
     * return 0;
     * }
     */

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor _enabled = new PropertyDescriptor("enabled", beanClass, "isEnabled", "setEnabled");
	    _enabled.setShortDescription("whether the user can change the value");
	    PropertyDescriptor _precision = new PropertyDescriptor("precision", beanClass, "getPrecision", "setPrecision");
	    _precision.setShortDescription("with what precision mouse operations will change the value");
	    PropertyDescriptor _style = new PropertyDescriptor("style", beanClass, "getStyle", "setStyle");
	    _style.setShortDescription("choose in which style to show");
	    _style.setPropertyEditorClass(StylePropertyEditor.class);
	    PropertyDescriptor _value = new PropertyDescriptor("value", beanClass, "getValue", "setValue");
	    _value.setShortDescription("Number value");
	    _value.setPreferred(true);
	    PropertyDescriptor[] pds = new PropertyDescriptor[] {
		_enabled, _precision, _style, _value, 
	    };
	    return pds;
	} catch (IntrospectionException ex) {
	    ex.printStackTrace();
	    return null;
	} 
    } 

    public java.awt.Image getIcon(int iconKind) {
	switch (iconKind) {
	case BeanInfo.ICON_COLOR_16x16:
	    return iconColor16x16Filename != null ? loadImage(iconColor16x16Filename) : null;
	case BeanInfo.ICON_COLOR_32x32:
	    return iconColor32x32Filename != null ? loadImage(iconColor32x32Filename) : null;
	case BeanInfo.ICON_MONO_16x16:
	    return iconMono16x16Filename != null ? loadImage(iconMono16x16Filename) : null;
	case BeanInfo.ICON_MONO_32x32:
	    return iconMono32x32Filename != null ? loadImage(iconMono32x32Filename) : null;
	}
	return null;
    } 

    public static class StylePropertyEditor extends TaggedPropertyEditorSupport {
	public StylePropertyEditor() {
	    super(new String[] {
		"typing", "slider", "numpad"
	    }, new Object[] {
		new Integer(NumberInput.TYPING), new Integer(NumberInput.SLIDER), new Integer(NumberInput.NUMPAD)
	    }, new String[] {
		"NumberInput.TYPING", "NumberInput.SLIDER", "NumberInput.NUMPAD"
	    });
	}
    }
}

