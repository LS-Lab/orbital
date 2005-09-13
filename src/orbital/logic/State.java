/*
 * @(#)State.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.logic;

/**
 * This class represents any kind of logic State an entity or object
 * might have.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @deprecated This class is not applicable in a broad range.
 *  So by a careful balance of utility and conceptual weight, it is considered generally worthless
 *  and might be removed in future releases.
 */
public class State {

    /**
     * The current state.
     * 
     * @serial
     */
    public String state;
    public State(String stat) {
	this.state = stat;
    }
    public State() {
	this("UNINITIALIZED");
    }

    /**
     * Get current state.
     */
    public String getState() {
	return state;
    } 

    /**
     * Set current state.
     */
    public void setState(String state) {
	this.state = state;
    } 

    /**
     * Check whether this state is like arg.
     * @return If arg is a String, return whether it equals the current state description.
     * If arg is a State, return <code>this.equals(arg)</code>.
     * @see #equals(java.lang.Object)
     */
    public boolean is(Object arg) {
	if (arg instanceof String)
	    return state.equals(arg);
	else
	    return this.equals(arg);
    } 

    /**
     * Check two states for equality.
     */
    public boolean equals(Object arg) {
	if (arg instanceof State) {
	    State s = (State) arg;
	    return state == null ? s.state == null : state.equals(s.state);
	} 
	return false;
    } 

    public int hashCode() {
	return state.hashCode();
    } 

    /**
     * Returns a string representation of the object.
     */
    public String toString() {
	return "(:" + state + ":)";
    } 
}

