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
public class ImageCanvasBeanInfo extends SimpleBeanInfo {
    Class  beanClass = ImageCanvas.class;
    String iconColor16x16Filename = "ImageCanvas16.gif";
    String iconColor32x32Filename = "ImageCanvas.gif";
    String iconMono16x16Filename;
    String iconMono32x32Filename;

    public ImageCanvasBeanInfo() {}

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor _image = new PropertyDescriptor("image", beanClass, "getImage", "setImage");
	    _image.setShortDescription("the image to be shown on the canvas");
	    PropertyDescriptor _stretched = new PropertyDescriptor("stretched", beanClass, "isStretched", "setStretched");
	    _stretched.setShortDescription("if the image should be stretched to fit the whole canvas");
	    PropertyDescriptor _preferredSize = new PropertyDescriptor("preferredSize", beanClass, "getPreferredSize", null);
	    _preferredSize.setShortDescription("prefers original dimension of the image");
	    PropertyDescriptor[] pds = new PropertyDescriptor[] {
		_image, _stretched, _preferredSize, 
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

    public BeanInfo[] getAdditionalBeanInfo() {
	Class superclass = beanClass.getSuperclass();
	try {
	    BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
	    return new BeanInfo[] {
		superBeanInfo
	    };
	} catch (IntrospectionException ex) {
	    ex.printStackTrace();
	    return null;
	} 
    } 
}
