// Title:        Evolutionary genetic algorithms
// Version:
// Copyright:    Copyright (c) 1999
// Author:       André Platzer
// Company:
// Description:

package orbital.algorithm.evolutionary;

import java.beans.*;

/**
 * @exclude
 */
public
class GeneticAlgorithmBeanInfo extends SimpleBeanInfo {
	Class  beanClass = GeneticAlgorithm.class;
	String iconColor16x16Filename;
	String iconColor32x32Filename;
	String iconMono16x16Filename;
	String iconMono32x32Filename;

	public GeneticAlgorithmBeanInfo() {}

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor _childrenCount = new PropertyDescriptor("childrenCount", beanClass, "getChildrenCount", "setChildrenCount");
			_childrenCount.setShortDescription("number of children produced with one reproduction");
			PropertyDescriptor _maximumRecombination = new PropertyDescriptor("maximumRecombination", beanClass, "getMaximumRecombination", "setMaximumRecombination");
			_maximumRecombination.setShortDescription("maximum probability rating of recombining parental genomes per production");
			PropertyDescriptor _maximumMutation = new PropertyDescriptor("maximumMutation", beanClass, "getMaximumMutation", "setMaximumMutation");
			_maximumMutation.setShortDescription("maximum probability rating of mutation level for reproducation");
			PropertyDescriptor _parentCount = new PropertyDescriptor("parentCount", beanClass, "getParentCount", "setParentCount");
			_parentCount.setShortDescription("number of abstract parents virtually required to produce children");
			PropertyDescriptor _population = new PropertyDescriptor("population", beanClass, "getPopulation", "setPopulation");
			_population.setShortDescription("population for this genetic algorithm");
			_population.setPropertyEditorClass(PopulationBeanInfo.PopulationEditor.class);
			PropertyDescriptor _selection = new PropertyDescriptor("selection", beanClass, "getSelection", "setSelection");
			_selection.setShortDescription("the selection scheme to apply for evolving");
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
				_childrenCount, _maximumRecombination, _maximumMutation, _parentCount, _population, _selection, 
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
