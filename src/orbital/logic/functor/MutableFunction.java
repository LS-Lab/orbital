/**
 * @(#)MutableFunction.java 0.9 2001/06/10 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;


import java.util.Map;

import java.util.HashMap;

/**
 * A mutable function that can change its values.
 * <p>
 * A mutable function is a function that can be modified by assigning new values
 * to it at some arguments.</p>
 * <p>
 * <i><b>Note:</b> this class might be subject to change in a redesign.</i></p>
 *
 * @version 0.9, 2001/06/10
 * @author  Andr&eacute; Platzer
 */
public interface MutableFunction/*<A, B>*/ extends Function/*<A, B>*/ {
    /**
     * Set f(arg) := value.
     * <p>
     * Modifies a function by assigning a new value to the function at a specified argument.</p>
     * @param arg the argument for which to change the function.
     * @param value the new value this function should have for argument arg, from now on.
     * @return the old value f(arg) prior to updating the function.
     * @postconditions RES == OLD(apply(arg)) && apply(arg) == value
     */
    Object/*>B<*/ set(Object/*>A<*/ arg, Object/*>B<*/ value);
	
    /**
     * @throws CloneNotSupportedException if this function does not support cloning.
     * @postconditions RES != RES
     */
    Object clone() throws CloneNotSupportedException;
    
    /**
     * A mutable function implemented as a tabular HashMap.
     * @version 0.9, 2001/06/10
     * @author  Andr&eacute; Platzer
     * @see java.util.HashMap
     */
    public static class TableFunction/*<A, B>*/ implements MutableFunction/*<A, B>*/ {
    	private final boolean cache;
    	private final Map/* <A, B> */ map;
    	private Function/*<A, B>*/ initialization;
    	/**
    	 * Create a table-based mutable function.
    	 * @param initialization The function h to use for implicit lazy initialization f(x) = h(x)
    	 *  for yet unknown arguments x.
    	 *  Use <code>null</code> to disable lazy initialization,
    	 *  leading to <code>null</code> being the value for yet unknown arguments.
    	 * @param cache whether values lazy initialized via <code>initialization</code> should
    	 *  be cached in the hash map.
    	 *  Caching is important when no memory constraints are posed and the initialization
    	 *  function performs expensive calculation.
    	 */
    	public TableFunction(Function/*<A, B>*/ initialization, boolean cache) {
	    this.map = new HashMap/* <A, B> */();
	    this.initialization = initialization;
	    this.cache = cache;
    	}
    	public TableFunction(Function/*<A, B>*/ initialization) {
	    this(initialization, true);
    	}
    	/**
    	 * Create a table-based mutable function without lazy initialization.
    	 */
    	public TableFunction() {
	    this(null);
    	}
        
        public Object clone() {
	    TableFunction clone = new TableFunction(initialization);
	    clone.map.putAll(map);
	    return clone;
        }
        
        /**
         * Get the initialization function h.
         */
        public Function/*<A, B>*/ getInitialization() {
	    return initialization;
        }
        /**
         * Set the initialization function h to use.
    	 * @param initialization The function h to use for implicit lazy initialization f(x) = h(x)
    	 *  for yet unknown arguments x, from now on.
    	 *  Use <code>null</code> to disable lazy initialization,
    	 *  leading to <code>null</code> being the value for yet unknown arguments.
    	 */
        public void setInitialization(Function/*<A, B>*/ h) {
	    this.initialization = h;
        }
        
    	/**
    	 * Get the value of an argument.
    	 * @return f(x).
    	 *  Implicitly lazy initializing f(x) = h(x) for yet unknown arguments x.
    	 */
    	public Object/*>B<*/ apply(Object/*>A<*/ arg) {
	    Object/*>B<*/ value = (Object/*>B<*/) map.get(arg);
	    if (value == null && initialization != null) {
		value = initialization.apply(arg);
		if (cache)
		    map.put(arg, value);
	    }
	    return value;
    	}
    
    	/**
    	 * Update the value at an argument.
    	 * @param arg the argument x whose cost to update.
    	 * @param value the new value f(x) to set for argument x.
	 * @return the old value f(arg) prior to updating the function.
    	 */
    	public Object/*>B<*/ set(Object/*>A<*/ arg, Object/*>B<*/ value) {
	    return (Object/*>B<*/) map.put(arg, value);
    	}
    }
}
