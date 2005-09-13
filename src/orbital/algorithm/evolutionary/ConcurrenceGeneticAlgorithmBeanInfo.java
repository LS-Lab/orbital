package orbital.algorithm.evolutionary;

import java.beans.*;
import orbital.awt.TaggedPropertyEditorSupport;

/**
 * @exclude
 * @todo add concurrenceType property
 */
public class ConcurrenceGeneticAlgorithmBeanInfo extends /*GeneticAlgorithmBeanInfo*/SimpleBeanInfo {
    Class beanClass = ConcurrenceGeneticAlgorithm.class;

    public ConcurrenceGeneticAlgorithmBeanInfo() {}

    public BeanDescriptor getBeanDescriptor() {
	BeanDescriptor bd = new BeanDescriptor(beanClass/*, orbital.moon.evolutionary.GeneticAlgorithmCustomizer.class*/);
	bd.setName("ConcurrenceGeneticAlgorithm");
	bd.setShortDescription("GA who will compare several chromosomes for evaluation");
	return bd;
    } 

    public PropertyDescriptor[] getPropertyDescriptors() {
	try {
	    PropertyDescriptor _type = new PropertyDescriptor("concurrenceType", beanClass, "getConcurrenceType", "setConcurrenceType");
	    _type.setShortDescription("The type of concurrence comparison used");
	    _type.setPropertyEditorClass(TypePropertyEditor.class);
	    PropertyDescriptor _comparisons = new PropertyDescriptor("concurrenceComparisons", beanClass, "getConcurrenceComparisons", null);
	    _comparisons.setShortDescription("The number of concurrence comparisons required for the current population");
	    PropertyDescriptor[] pds = new PropertyDescriptor[] {
		_type, _comparisons
	    };
	    return pds;
	} catch (IntrospectionException ex) {
	    ex.printStackTrace();
	    return null;
	} 
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

    public static class TypePropertyEditor extends TaggedPropertyEditorSupport {
	public TypePropertyEditor() {
	    super(new String[] {
		"round robin", "hierarchy", "flat hierarchy"
	    }, new Object[] {
		new Integer(ConcurrenceGeneticAlgorithm.ROUND_ROBIN), new Integer(ConcurrenceGeneticAlgorithm.HIERARCHY), new Integer(ConcurrenceGeneticAlgorithm.FLAT_HIERARCHY)
	    }, new String[] {
		"ConcurrenceGeneticAlgorithm.ROUND_ROBIN", "ConcurrenceGeneticAlgorithm.HIERARCHY", "ConcurrenceGeneticAlgorithm.FLAT_HIERARCHY"
	    });
	}
    }
}
