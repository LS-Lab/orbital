package orbital.moon.math;

import java.beans.*;
import orbital.awt.TaggedPropertyEditorSupport;

public class AbstractValuesBeanInfo extends SimpleBeanInfo {
    Class beanClass = AbstractValues.class;

    public AbstractValuesBeanInfo() {}

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _representation = new PropertyDescriptor("representation", beanClass);
            _representation.setDisplayName("number representation");
            _representation.setShortDescription("the internal representation of numbers");
            _representation.setPropertyEditorClass(RepresentationPropertyEditor.class);
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                _representation
            };
            return pds;
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            return null;
        } 
    } 

    public static class RepresentationPropertyEditor extends TaggedPropertyEditorSupport {
        public RepresentationPropertyEditor() {
            super(new String[] {
                "big", "machine", "dynamic"
            }, new String[] {
                "big", "machine", "dynamic"
            }, new String[] {
                "\"big\"", "\"machine\"", "\"dynamic\""
            });
        }
    }

}
