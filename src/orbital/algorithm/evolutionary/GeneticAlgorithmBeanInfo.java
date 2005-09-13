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
 * @todo could use Selectors.* with a TaggedPropertyEditorSupport as customizer for selection.
 */
public class GeneticAlgorithmBeanInfo extends SimpleBeanInfo {
    Class  beanClass = GeneticAlgorithm.class;
    String iconColor16x16Filename;
    String iconColor32x32Filename;
    String iconMono16x16Filename;
    String iconMono32x32Filename;

    public GeneticAlgorithmBeanInfo() {}

    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            PropertyDescriptor _population = new PropertyDescriptor("population", beanClass, "getPopulation", "setPopulation");
            _population.setShortDescription("population for this genetic algorithm");
            _population.setPropertyEditorClass(PopulationBeanInfo.PopulationEditor.class);
            PropertyDescriptor _selection = new PropertyDescriptor("selection", beanClass, "getSelection", "setSelection");
            _selection.setShortDescription("the selection scheme to apply for evolving");
            PropertyDescriptor _populationGrowth = new PropertyDescriptor("populationGrowth", beanClass, "getPopulationGrowth", null);
            _populationGrowth.setShortDescription("the factor by which the population size increases with each generation (or decreases if < 1)");
            PropertyDescriptor[] pds = new PropertyDescriptor[] {
                _population, _selection, _populationGrowth
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
