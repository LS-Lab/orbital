package orbital.moon.math;

import java.beans.*;
import orbital.awt.TaggedPropertyEditorSupport;

public class ArithmeticValuesImplBeanInfo extends SimpleBeanInfo {
    Class beanClass = ArithmeticValuesImpl.class;

    public ArithmeticValuesImplBeanInfo() {}

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _precision = new PropertyDescriptor("precision", beanClass);
            _precision.setDisplayName("precision");
            _precision.setShortDescription("the number of digits to be used for an operations with results being rounded to this precision. 0 means unlimited");
            PropertyDescriptor _sparse = new PropertyDescriptor("sparsePolynomials", beanClass);
            _sparse.setDisplayName("sparse polynomials");
            _sparse.setShortDescription("whether to use sparse or dense polynomial representations");
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                _precision,
                _sparse
            };
            return pds;
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            return null;
        } 
    } 
}
