// Title:        Evolutionary genetic algorithms
// Version:
// Copyright:    Copyright (c) 1999
// Author:       André Platzer
// Company:
// Description:  front-end for evolutionary algorithms

package orbital.algorithm.evolutionary;

import java.beans.*;
import java.awt.*;
import orbital.util.InnerCheckedException;

/**
 * @exclude
 */
public class PopulationImplBeanInfo extends SimpleBeanInfo {
    Class beanClass = PopulationImpl.class;

    public PopulationImplBeanInfo() {}

    public BeanDescriptor getBeanDescriptor() {
	BeanDescriptor bd = new BeanDescriptor(beanClass);
	bd.setName("Population");
	return bd;
    }

    public BeanInfo[] getAdditionalBeanInfo() {
	return new BeanInfo[] {new PopulationBeanInfo()};
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor _childrenCount = new PropertyDescriptor("childrenCount", beanClass, "getChildrenCount", "setChildrenCount");
	    _childrenCount.setShortDescription("number of children produced with one reproduction");
	    PropertyDescriptor _maximumRecombination = new PropertyDescriptor("maximumRecombination", beanClass, "getMaximumRecombination", "setMaximumRecombination");
	    _maximumRecombination.setShortDescription("maximum probability rating of recombining parental genomes per production");
	    PropertyDescriptor _maximumMutation = new PropertyDescriptor("maximumMutation", beanClass, "getMaximumMutation", "setMaximumMutation");
	    _maximumMutation.setShortDescription("maximum probability rating of mutation level for reproducation");
	    PropertyDescriptor _parentCount = new PropertyDescriptor("parentCount", beanClass, "getParentCount", "setParentCount");
	    _parentCount.setShortDescription("number of abstract parents required to produce children");
	    PropertyDescriptor[] pds = new PropertyDescriptor[] {
		_childrenCount, _maximumRecombination, _maximumMutation, _parentCount
	    };
	    return pds;
	} catch (IntrospectionException ex) {
	    ex.printStackTrace();
	    return null;
	} 
    } 
}
