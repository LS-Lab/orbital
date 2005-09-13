// Title:        Evolutionary genetic algorithms
// Version:
// Copyright:    Copyright (c) 1999
// Author:       André Platzer
// Company:
// Description:  front-end for evolutionary algorithms

package orbital.algorithm.evolutionary;

import orbital.moon.evolutionary.ChromosomeCustomizer;
import java.beans.*;

/**
 *
 * @exclude
 * @deprecated since orbital1.0.
 */
public
class ChromosomeBeanInfo extends SimpleBeanInfo {
	Class  beanClass = Chromosome.class;
	String iconColor16x16Filename;
	String iconColor32x32Filename;
	String iconMono16x16Filename;
	String iconMono32x32Filename;

	public ChromosomeBeanInfo() {}

	public BeanDescriptor getBeanDescriptor() {
		BeanDescriptor bd = new BeanDescriptor(beanClass, ChromosomeCustomizer.class);
		bd.setName("Chromosome");
		return bd;
	} 

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor _data = new PropertyDescriptor("data", beanClass, "getData", "setData");
			_data.setShortDescription("boolean chromosome data");
			PropertyDescriptor _fitness = new PropertyDescriptor("fitness", beanClass, "getFitness", "setFitness");
			_fitness.setShortDescription("fitness associated with this Chromosome");
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
				_data, _fitness
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
