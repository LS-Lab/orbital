// Title:        Your Product Name
// Version:
// Copyright:    Copyright (c) 1999
// Author:       André
// Company:
// Description:  Your description

package orbital.awt;

import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;

/**
 * @exclude
 */
public
class ChartModelBeanInfo extends SimpleBeanInfo {
	Class beanClass = ChartModel.class;

	public ChartModelBeanInfo() {}

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor _graphCount = new PropertyDescriptor("graphCount", beanClass, "getGraphCount", null);
			_graphCount.setShortDescription("number of graphs");
			PropertyDescriptor _graphs = new PropertyDescriptor("graphs", beanClass, "getGraphs", null);
			_graphs.setShortDescription("List of graphs to be displayed");
			PropertyDescriptor _range = new PropertyDescriptor("range", beanClass, "getRange", "setRange");
			_range.setShortDescription("the range to be displayed visibly");
			PropertyDescriptor _scale = new PropertyDescriptor("scale", beanClass, "getScale", "setScale");
			_scale.setShortDescription("vector of distances for scaling marks");
			PropertyDescriptor _scaleMarks = new PropertyDescriptor("scaleMarks", beanClass, null, "setScaleMarks");
			_scaleMarks.setShortDescription("set scale to have this number of scale marks on each axis");
			PropertyDescriptor _rainbow = new PropertyDescriptor("rainbow", beanClass, "isRainbow", "setRainbow");
			_rainbow.setShortDescription("Whether rainbow colors are used for graphs that have no color setting");
			PropertyDescriptor[] pds = new PropertyDescriptor[] {
				_graphCount, _graphs, _range, _scale, _scaleMarks, _rainbow
			};
			return pds;
		} catch (IntrospectionException ex) {
			ex.printStackTrace();
			return null;
		} 
	} 
}
