package orbital.awt;

import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * @exclude
 */
public class Plot2DBeanInfo extends SimpleBeanInfo {
    Class  beanClass = Plot2D.class;
    String iconColor16x16Filename = "Plot2D16.gif";
    String iconColor32x32Filename = "Plot2D.gif";
    String iconMono16x16Filename;
    String iconMono32x32Filename;

    public Plot2DBeanInfo() {}

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(beanClass, Plot2DCustomizer.class);
        bd.setName("Plot2D");
        bd.setDisplayName("Chart 2D");
        bd.setShortDescription("2D Chart that plots functions and sets of points");
        return bd;
    } 

    /*
     * public int getDefaultEventIndex() {
     * return 0;
     * }
     */

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _autoScaling = new PropertyDescriptor("autoScaling", beanClass, "isAutoScaling", "setAutoScaling");
            _autoScaling.setShortDescription("automatically adjust range and scaling");
            PropertyDescriptor _chartModel = new PropertyDescriptor("model", beanClass, "getModel", "setModel");
            _chartModel.setShortDescription("ChartModel displayed");
            PropertyDescriptor _fullScaling = new PropertyDescriptor("fullScaling", beanClass, "isFullScaling", "setFullScaling");
            _fullScaling.setShortDescription("show scaling numbers throughout the chart");
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                _autoScaling, _chartModel, _fullScaling, 
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
}
