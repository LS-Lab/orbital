/*

Copyright (c) Xerox Corporation 2000.  All rights reserved.

Use and copying of this software and preparation of derivative works based
upon this software are permitted.  Any distribution of this software or
derivative works must comply with all applicable United States export control
laws.

This software is made available AS IS, and Xerox Corporation makes no warranty
about the software, its performance or its conformity to any specification.


*/

package orbital.moon.aspects;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import java.beans.PropertyChangeSupport;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * Add bound properties aspect and serialization.
 *
 * @author Andr&eacute; Platzer
 * @version 2000/11/15
 **/
aspect BoundProperties implements Serializable /*of nothing*/ of eachobject(instanceof(Point)) {

    /**
     * For which types we want to add bound properties and serialization.
     * Overwrite this abstract pointcut in subclasses to specify.
     */
    /*abstract*/ pointcut types(): instanceof(Point);
    
    /**
     * Constructions of new object of the corresponding type to reference.
     * Overwrite to be aware of constructor cascading and avoid multiple modifications.
     */
    pointcut constructors(): types() && receptions(new(..));

    /**
     * Pointcut describing the set<property> methods.
     */
    pointcut setters(): types() && receptions(void set*(*));
  
    private PropertyChangeSupport support = null;
    private Object bean = null;
    
    /**
     * Introduce the property change registration methods into T.
     * also introduce implementation of the Serializable interface.
     */
    //FUTURE: the generic way "abstract pointcut types();" is not yet provided
    introduction(Point) {
  
	implements Serializable;
  
	public void addPropertyChangeListener(PropertyChangeListener listener){
	    BoundProperties.aspectOf(this).getSupport().addPropertyChangeListener(listener);
	}
  
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
	    BoundProperties.aspectOf(this).getSupport().addPropertyChangeListener(propertyName, listener);
	}
  
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
	    BoundProperties.aspectOf(this).getSupport().removePropertyChangeListener(propertyName, listener);
	}
  
	public void removePropertyChangeListener(PropertyChangeListener listener) {
	    BoundProperties.aspectOf(this).getSupport().removePropertyChangeListener(listener);
	}
  
	public void hasListeners(String propertyName) {
	    BoundProperties.aspectOf(this).getSupport().hasListeners(propertyName);
	}
  
    }
  
    /**
     * After new object is created, keep a reference to it.
     */
    after() returning(Object o): constructors() {
        bean = o;
    }
  
    /**
     * Advice to get the property change event fired when the
     * setters are called. It's around advice because you need 
     * the old value of the property.
     */
    around() returns void: setters() {
        String propertyName = Introspector.decapitalize(thisJoinPoint.methodName.substring("set".length()));
	try {
	    Method reader = getPropertyReadMethod(bean.getClass(), propertyName);
	    if (reader == null)
		throw new NullPointerException("no read method for " + propertyName);
	    Object old = reader.invoke(bean, NO_ARGUMENTS);
            thisJoinPoint.runNext();
            firePropertyChange(propertyName, old, reader.invoke(bean, NO_ARGUMENTS));
	} catch (IntrospectionException x) {
            thisJoinPoint.runNext();
	} catch (IllegalAccessException x) {
            thisJoinPoint.runNext();
	} catch (InvocationTargetException x) {
            thisJoinPoint.runNext();
	} 
    }

    private static final Object[] NO_ARGUMENTS = new Object[0];
    private static Method getPropertyReadMethod(Class beanClass, String propertyName) throws IntrospectionException {
	BeanInfo info = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
	if (info == null)
	    throw new NullPointerException("no BeanInfo for class: " + beanClass);
	BeanDescriptor desc = info.getBeanDescriptor();
	if (desc == null)
	    throw new NullPointerException("no BeanDescriptor for class: " + beanClass);
	PropertyDescriptor[] beanProperties = info.getPropertyDescriptors();
	if (beanProperties == null)
	    throw new NullPointerException("no PropertyDescriptors for class: " + beanClass);

	for (int i = 0; i < beanProperties.length; i++)
	    if (propertyName.equals(beanProperties[i].getName()))
		return beanProperties[i].getReadMethod();
	return null;
    }
  
    /**
     * Lazy initialize the property change support object so that
     * it won't be created until after we have the reference to the T object.
     */
    public synchronized PropertyChangeSupport getSupport() {
        if (support == null)
            if (bean != null)
                support = new PropertyChangeSupport(bean);
            else
                throw new Error("BoundProperties<<aspect>>: " + 
                		"can't initialize property change support -- " +
				"no object reference yet.");
        return support;
    }
  
    /**
     * Utility to fire the property change event.
     */
    void firePropertyChange(String property, Object oldval, Object newval) {
        getSupport().firePropertyChange(property, oldval, newval);
    }
}
