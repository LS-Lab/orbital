/*
 * @(#)v_World.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt.virtual;

import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * a v_Container containing v_Objects.
 */
public class v_World extends v_Container {
    private static final long serialVersionUID = -478945021041389057L;

    /**
     * constructs a new empty world.
     */
    public v_World() {}

    /**
     * constructs a new world spaced for n v_Objects.
     */
    public v_World(int n) {
	super(n);
    }

    /**
     * Gets the nth Object.
     */
    public v_Object getObject(int n) {
	return (v_Object) getComponent(n);
    } 

    /**
     * Sets the nth Object.
     */
    public void setObject(v_Object c, int n) {
	setComponent(c, n);
    } 

    public synchronized void readExternal(ObjectInput is) throws ClassNotFoundException, IOException {
	setComponentCount(is.readShort());
	for (int c = 0; c < getComponentCount(); c++)
	    setComponent(new v_Object(), c);
	super.readExternal(is);
    } 

    public synchronized void writeExternal(ObjectOutput os) throws IOException {
	os.writeShort(getComponentCount());
	super.writeExternal(os);
    } 
}
