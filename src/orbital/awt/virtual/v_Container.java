/**
 * @(#)v_Container.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.util.Vector;
import orbital.math.Matrix3D;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;

/**
 * A generic Virtual Reality container object is a component
 * that contains other VR components.
 * 
 * @version 0.9, 03/02/96
 * @author  Andr&eacute; Platzer
 * @see v_Component
 */
public class v_Container implements v_Component {
    private static final long serialVersionUID = -3614328224870373789L;

    /**
     * The v_Components in this v_Container.
     * @serial
     */
    protected Vector component;

    /**
     * Constructs a new Container. Containers should not be
     * instantiated directly.
     */
    protected v_Container() {
	component = new Vector();
    }

    /**
     * Constructs a new Container. Containers should not be
     * instantiated directly.
     * Constructs with a predefined capacity.
     */
    protected v_Container(int n) {
	component = new Vector(n);
    }

    /**
     * Returns the component Vector.
     */
    public Vector getComponents() {
	return component;
    } 

    /**
     * Returns the number of components in this container.
     * @see #getComponent
     */
    public int getComponentCount() {
	return component.size();
    } 

    /**
     * Sets the number of components in this container.
     * @see #getComponentCount
     */
    public void setComponentCount(int newCount) {
	component.setSize(newCount);
    } 

    /**
     * Gets the nth component in this container.
     * @param n the number of the component to get
     */
    public v_Component getComponent(int n) {
	return (v_Component) component.elementAt(n);
    } 

    /**
     * Sets the nth component in this container.
     * @param n the number of the component to get
     */
    public void setComponent(v_Component c, int n) {
	synchronized (component) {
	    if (getComponentCount() > n)
		component.setElementAt(c, n);
	    else
		component.insertElementAt(c, n);
	} 
    } 

    /**
     * Whether a given component is contained.
     */
    public boolean contains(v_Component v) {
	return component.contains(v);
    } 

    /**
     * Returns the index of the given vertex in the container vertexes.
     */
    public int indexOf(v_Component v) {
	return component.indexOf(v);
    } 

    /**
     * add a new component to this container.
     */
    public void add(v_Component c) {
	synchronized (component) {
	    component.addElement(c);
	} 
    } 

    /**
     * remove a component from this container.
     */
    public boolean remove(v_Component c) {
	synchronized (component) {
	    return component.removeElement(c);
	} 
    } 


    /**
     * transforms the positions of all contained components.
     */
    public void transform(Matrix3D mat) {
	for (int c = 0; c < getComponentCount(); c++)
	    getComponent(c).transform(mat);
    } 

    /**
     * draws all contained components.
     */
    public void draw(v_Graphics g) {
	for (int c = 0; c < getComponentCount(); c++)
	    getComponent(c).draw(g);
    } 

    /**
     * load all components from a stream.
     * @pre getComponentCount() is set in a subclass dependent way, for efficiency reasons
     */
    public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
	//@xxx setComponentCount(is.readShort()); will not work due to prior processing in sub classes
	for (int c = 0; c < getComponentCount(); c++)
	    getComponent(c).readExternal(is);
    } 

    /**
     * store all components into a stream.
     */
    public synchronized void writeExternal(ObjectOutput os) throws IOException {
	//os.writeShort(getComponentCount());
	for (int c = 0; c < getComponentCount(); c++)
	    getComponent(c).writeExternal(os);
    } 
}
