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
public class PopulationBeanInfo extends SimpleBeanInfo {
    Class beanClass = Population.class;

    public PopulationBeanInfo() {}

    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(beanClass);
        bd.setName("Population");
        return bd;
    } 

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _generation = new PropertyDescriptor("generation", beanClass, "getGeneration", null);
            _generation.setShortDescription("the current generation count");
            PropertyDescriptor _size = new PropertyDescriptor("size", beanClass, "size", null);
            _size.setShortDescription("size of population");
            PropertyDescriptor _members = new IndexedPropertyDescriptor("members", beanClass, null/*"getMembersArray"*/, null, "get", null);
            _members.setShortDescription("members of population");
            //@todo _members.setPropertyEditorClass(ListEditor.class)
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                _generation, _size, _members
            };
            return pds;
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            return null;
        } 
    } 

    public static class PopulationEditor extends PropertyEditorSupport {
        private Customizer custom = null;
        public boolean supportsCustomEditor() {
            return true;
        } 
        public Component getCustomEditor() {
            try {
                if (custom == null) {
                    //@xxx how to determine whether to use Population.class or PopulationImpl.class? Initially getValue()=null
                    custom = new orbital.moon.awt.DefaultCustomizer(getValue() == null
                                                                    ? PopulationImpl.class
                                                                    : getValue().getClass());
                    custom.setObject(getValue());
                } 
                return (Component) custom;
            } catch (Exception e) {
                throw new InnerCheckedException(e);
            } 
        }
        public boolean isInlineCustomEditor() {
            return true;
        }
        public boolean isPaintable() {
            return false;
        } 
        public void setValue(Object v) {
            super.setValue(v);
            if (custom != null)
                custom.setObject(getValue());
        } 
    }
}
