/**
 * @(#)Functions.java 0.7 2001/08/03 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;


import orbital.logic.trs.Variable;

import orbital.util.Utility;

/**
 * Function Implementations.
 * 
 * @version 1.0, 2000/08/03
 * @author  Andr&eacute; Platzer
 * @see Functionals
 */
public final class Functions {
    /**
     * Class alias object.
     */
    public static final Functions functions = new Functions();

    /**
     * prevent instantiation - module class
     */
    private Functions() {}

    /**
     * constant &acirc;: A&rarr;B; x &#8614; a .
     * <p>derive: &acirc;' = 0<br />
     * integrate: &int;a<i>d</i>x = a*x</p>
     * @todo somehow provide a type-safe VoidFunction constant(Object)
     * @todo publicize?
     */
    static final /*<A,B>*/ Function/*<A,B>*/ constant(Object/*>B<*/ a) {
	return new ConstantFunction/*<A,A,B>*/(a);
    }
    /**
     * constant &acirc;: A<sub>1</sub>&times;A<sub>2</sub>&rarr;B; x &#8614; a .
     * <p>derive: &acirc;' = 0<br />
     * integrate: &int;a<i>d</i>x = a*x</p>
     */
    static final /*<A1,A2,B>*/ BinaryFunction/*<A1,A2,B>*/ binaryConstant(Object/*>B<*/ a) {
	return new ConstantFunction/*<A1,A2,B>*/(a);
    }
    /**
     * A constant function.
     * <p>
     * constant &acirc;: A&rarr;B; x &#8614; a.</p>
     * <p>derive: &acirc;' = 0<br />
     * integrate: &int;a<i>d</i>x = a*x
     * </p>
     * <p>
     * <i><b>Note:</b> this class will be made private soon, and checking for constant functions
     * will be made available in another way. So do not rely on the existence of this class.
     * </p>
     * @version 0.9, 2000/08/01
     * @author  Andr&eacute; Platzer
     * @see Functionals#bind(Function, Object)
     * @note We allow checking for constant functions via this instanceof VoidFunction and this.apply().
     *  Nevertheless, we distinguish mathematically constant expressions from expressions variable (alias not constant) with respect to term rewrite systems.
     *  Since mathematically constant expressions are simply VoidFunctions this saves us all a lot of trouble.
     * @todo could we change this to Function<A implements Arithmetic,M> or to Function<Arithmetic,M>?
     */
    static final class ConstantFunction/*<A1,A2,B>*/ implements VoidFunction/*<B>*/, Function/*<A1,B>*/, BinaryFunction/*<A1,A2,B>*/, Variable {
    	private Object/*>B<*/ a;
    	public ConstantFunction(Object/*>B<*/ a) {
	    this.a = a;
    	}
    	
	/**
	 * @structure delegate a:Object
	 */
	public boolean isVariable() {
	    return (a instanceof Variable) && ((Variable) a).isVariable();
	}
    	
	public Object/*>B<*/ apply() {
	    return a;
	} 
	public Object/*>B<*/ apply(Object/*>A1<*/ x) {
	    return apply();
	} 
	public Object/*>B<*/ apply(Object/*>A1<*/ x, Object/*>A2<*/ y) {
	    return apply();
	} 
	public boolean equals(Object o) {
	    return (o instanceof ConstantFunction)
		&& Utility.equals(a, ((ConstantFunction) o).a);
	}
	public int hashCode() {
	    return Utility.hashCode(a);
	}
	public String toString() {
	    return a + "";
	} 
    }
	
    /**
     * id: A&rarr;A; x &#8614; x .
     */
    public static final Function id = new Function() {
	    public Object apply(Object x) {
		return x;
	    } 
	    public String toString() {
		return "id";
	    } 
	};

	

    /**
     * Projects to the first argument, ignoring the second.
     * <p>
     * projectFirst: (x,y) &#8614; x.
     * </p>
     * <p>
     * Equals {@link Functionals#onFirst(Function) Functionals.onFirst}({@link orbital.math.functional.Functions#id})</p>
     * <p><b><i>Evolves</i>:</b> might be renamed.</p>
     */
    public static final BinaryFunction projectFirst = new BinaryFunction() {
	    public Object apply(Object first, Object second) {
		return first;
	    } 
	    public String toString() {
		return "#0";
	    } 

	};

    /**
     * Projects to the second argument, ignoring the first.
     * <p>
     * projectSecond: (x,y) &#8614; y.
     * </p>
     * <p>
     * Equals {@link Functionals#onSecond(Function) Functionals.onSecond}({@link orbital.math.functional.Functions#id})</p>
     * <p><b><i>Evolves</i>:</b> might be renamed.</p>
     */
    public static BinaryFunction projectSecond = new BinaryFunction() {
	    public Object apply(Object first, Object second) {
		return second;
	    } 
	    public String toString() {
		return "#1";
	    } 
	};
}
