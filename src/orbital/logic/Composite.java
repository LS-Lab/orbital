/**
 * @(#)Composite.java 1.1 2002-11-27 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * The base interface for all things that are composed of other things.
 * Such compositions include but are not limited to functors.
 * 
 * @structure aggregate compositor:Object
 * @structure aggregate component:Object
 * @version 1.1, 2002-11-27
 * @author  Andr&eacute; Platzer
 * @see <a href="{@docRoot}/Patterns/Design/Composite.html">(unidirectional and multiple) Composite Pattern</a>
 * @todo move to another package?
 */
public interface Composite /*<Compositor,Component>*/ {
    /**
     * Get the outer compositor.
     * @return the outer compositor f that operates on the results of the inner component.
     */
    Object/*>Compositor<*/ getCompositor();

    /**
     * Get the inner component.
     * @return the inner component object g, or an array of the inner components {g<sub>1</sub>,...g<sub>k</sub>}<sup>T</sup>.
     *  Multi-dimensional component arrays and alike are permitted, as well.
     */
    Object/*>Component<*/ getComponent();
		
    // factory-methods
	
    /**
     * Construct a new composition with the given parameters, of the same type like this.
     * @param f the outer compositor f that operates on the results of the inner component.
     * @param g the inner component object g, or an array of the inner components {g<sub>1</sub>,...g<sub>k</sub>}<sup>T</sup>.
     *  Multi-dimensional component arrays and alike are permitted, as well.
     * @return a composite object of the same type as this,
     *  with compositor and component as specified.
     * @post RES != RES &and; RES.getClass()==getClass() &and; RES.getCompositor()==f &and; RES.getComponent()==g
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @throws IllegalArgumentException if f is an illegal compositor
     *  or g is an illegal component for this kind of composite object.
     *  Depending upon context, this method may also throw ClassCastException, instead.
     * @throws ClassCastException if f has the wrong type for a compositor,
     *  or g the wrong type for a component.
     *  Depending upon context, this method may also throw IllegalArgumentException, instead.
     * @throws UnsupportedOperationException if this method does not support modification cloning.
     * @see #clone()
     */
    Composite construct(Object/*>Compositor<*/ f, Object/*>Component<*/ g) throws IllegalArgumentException, ClassCastException;

    // Set properties
	
    /**
     * Set the outer compositor.
     * @param f the outer functor f that operates on the results of the inner component.
     * @throws IllegalArgumentException if f is an illegal compositor for this composite object.
     *  Depending upon context, this method may also throw ClassCastException, instead.
     * @throws ClassCastException if f has the wrong type for a compositor.
     *  Depending upon context, this method may also throw IllegalArgumentException, instead.
     * @throws UnsupportedOperationException if this method is not supported by this object, or this object is immutable.
     */
    void setCompositor(Object/*>Compositor<*/ f) throws IllegalArgumentException, ClassCastException;

    /**
     * Set the inner component.
     * @param g the inner component object g, or an array of the inner components {g<sub>1</sub>,...g<sub>k</sub>}<sup>T</sup>.
     *  Multi-dimensional component arrays and alike are permitted, as well.
     * @throws IllegalArgumentException if g is an illegal component for this composite object.
     *  Depending upon context, this method may also throw ClassCastException, instead.
     * @throws ClassCastException if g has the wrong type for a component.
     *  Depending upon context, this method may also throw IllegalArgumentException, instead.
     * @throws UnsupportedOperationException if this method is not supported by this object, or this object is immutable.
     */
    void setComponent(Object/*>Component<*/ g) throws IllegalArgumentException, ClassCastException;
}
