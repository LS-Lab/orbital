/**
 * @(#)ComponentCompositions.java 1.1 2002-08-25 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;

import orbital.math.Arithmetic;
import orbital.math.Normed;
import orbital.logic.sign.concrete.Notation;

import orbital.math.Matrix;
import java.awt.Dimension;
import orbital.math.Vector;
import orbital.math.Values;

/**
 * Contains implementations of composite functors that work by
 * component composition.
 *
 * @version 1.1, 2002-08-25
 * @author  Andr&eacute; Platzer
 * @note package-level protected to orbital.math.functional
 * @see Functionals
 */
class ComponentCompositions {
    /**
     * prevent instantiation - module class
     */
    private ComponentCompositions() {}

    /**
     * A ComponentCompositeFunction is a vectorial Function
     * defined with a vector of component-functions.
     * <p>
     * Map(A,B)<sup>n</sup>&rarr;Map(A,B<sup>n</sup>); (f<sub>1</sub>,...f<sub>n</sub>)&#8614;f = f<sub>1</sub>&times;...&times;f<sub>n</sub>; x&#8614;f(x) = <big>(</big>f<sub>1</sub>(x),...f<sub>n</sub>(x)<big>)</big><sup>T</sup>.
     * </p>
     * 
     * @structure inherit Function
     * @structure concretizes MathFunctor.Composite
     * @structure composite componentFunction:Function[] unidirectional
     * @version 0.9, 1999/04/18
     * @author  Andr&eacute; Platzer
     * @see MatrixComponentCompositeFunction
     */
    static class ComponentCompositeFunction/*ComponentCompositeFunction<Vector.class> */ extends MathFunctor_CompositeFunctor implements Function {
	private Function componentFunction[];
	public ComponentCompositeFunction(Function componentFunction[]) {
	    this(componentFunction, null);
	}

	/**
	 * Create f<sub>1</sub>&times;...&times;f<sub>n</sub> = (f<sub>1</sub>,...f<sub>n</sub>)<sup>T</sup>.
	 * @param notation specifies which notation should be used for string representations.
	 */
	public ComponentCompositeFunction(Function componentFunction[], Notation notation) {
	    super(notation);
	    this.componentFunction = componentFunction;
	}

	/**
	 * Get the dimension of the resulting vectors.
	 */
	public int dimension() {
	    return componentFunction.length;
	} 
	public Object getCompositor() {
	    return null;
	} 

	/**
	 * Get the inner component functions applied per element.
	 */
	public Object getComponent() {
	    return componentFunction;
	} 

	public void setCompositor(Object f) throws IllegalArgumentException {
	    if (f != null)
		throw new IllegalArgumentException("cannot set compositor");
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.componentFunction = (Function[]) g;
	}

	/**
	 * Called to apply the component composite function.
	 * @return f<sub>1</sub>&times;...&times;f<sub>n</sub>(x) = <big>(</big>f<sub>1</sub>(x),...f<sub>n</sub>(x)<big>)</big><sup>T</sup> as a {@link orbital.math.Vector}.
	 */
	public Object apply(Object x) {
	    Vector r = Values.getDefaultInstance().newInstance(dimension());
	    //@todo if we kept componentFunctions in a Matrix, we could perhaps use mapping of iterators().
	    // component-wise
	    for (int i = 0; i < r.dimension(); i++)
		r.set(i, (Arithmetic) componentFunction[i].apply(x));
	    return r;
	} 

	/**
	 * <i>d</i>f/<i>d</i>x = f' = <big>(</big>f<sub>1</sub>'(x),...f<sub>n</sub>'(x)<big>)</big><sup>T</sup>
	 * Component-wise.
	 */
	public Function derive() {
	    Function[] di = new Function[dimension()];
	    // component-wise
	    for (int i = 0; i < di.length; i++)
		di[i] = componentFunction[i].derive();
	    return new ComponentCompositeFunction(di);
	} 

	/**
	 * &int; <big>(</big>f<sub>1</sub>(x),...f<sub>n</sub>(x)<big>)</big><sup>T</sup> <i>d</i>x = <big>(</big>&int;f<sub>1</sub>(x)<i>d</i>x,...&int;f<sub>n</sub>(x)<i>d</i>x<big>)</big><sup>T</sup>.
	 * Component-wise.
	 */
	public Function integrate() {
	    Function[] di = new Function[dimension()];
	    // component-wise
	    for (int i = 0; i < di.length; i++)
		di[i] = componentFunction[i].integrate();
	    return new ComponentCompositeFunction(di);
	}
    }

    /**
     * A ComponentCompositeFunction is a matrix Function
     * defined with component-functions.
     * <p>
     * Map(A,B)<sup>m&times;n</sup>&rarr;Map(A,B<sup>m&times;n</sup>); <big>(</big>(f<sub>0,0</sub>,..,f<sub>0,n-1</sub>),...(f<sub>m-1,0</sub>,..,f<sub>m-1,n-1</sub>)<big>)</big>&#8614;f = (f<sub>i,j</sub>); x&#8614;f(x) = <big>(</big>f<sub>i,j</sub>(x)<big>)</big> =
     * <pre>
     * [ f<sub>0,0 </sub>(x),  f<sub>0,1 </sub>(x),  f<sub>0,2 </sub>(x),...,  f<sub>0,m-1 </sub>(x) ]
     * [ f<sub>1,0 </sub>(x),  f<sub>1,1 </sub>(x),  f<sub>1,2 </sub>(x),...,  f<sub>1,m-1 </sub>(x) ]
     * [ ...                                         ]
     * [ f<sub>n-1,0</sub>(x), f<sub>n-1,1</sub>(x), f<sub>n-1,2</sub>(x),..., f<sub>n-1,m-1</sub>(x) ]</pre>
     * </p>
     * 
     * @structure inherit Function
     * @structure concretizes MathFunctor.Composite
     * @structure composite componentFunction:Function[][] unidirectional
     * @version 0.7, 2001/02/23
     * @author  Andr&eacute; Platzer
     * @see ComponentCompositeFunction
     */
    static class MatrixComponentCompositeFunction/*ComponentCompositeFunction<Matrix.class> */ extends MathFunctor_CompositeFunctor implements Function {
	//XXX: unify ComponentCompositeFunction with MatrixComponentCompositeFunction
	private Function componentFunction[][];
	public MatrixComponentCompositeFunction(Function componentFunction[][]) {
	    this(componentFunction, null);
	}

	/**
	 * Create (f<sub>i,j</sub>).
	 * @param notation specifies which notation should be used for string representations.
	 */
	public MatrixComponentCompositeFunction(Function componentFunction[][], Notation notation) {
	    super(notation);
	    this.componentFunction = componentFunction;
	}

	/**
	 * Get the dimension of the resulting matrix.
	 */
	public Dimension dimension() {
	    return new Dimension(componentFunction[0].length, componentFunction.length);
	} 
	public Object getCompositor() {
	    return null;
	} 

	/**
	 * Get the inner component functions applied per element.
	 */
	public Object getComponent() {
	    return componentFunction;
	} 

	public void setCompositor(Object f) throws IllegalArgumentException {
	    if (f != null)
		throw new IllegalArgumentException("cannot set compositor");
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.componentFunction = (Function[][]) g;
	}

	/**
	 * Called to apply the component composite function.
	 * @return (f<sub>i,j</sub>)(x) = <big>(</big>f<sub>i,j</sub>(x)<big>)</big> as a {@link orbital.math.Matrix}.
	 */
	public Object apply(Object x) {
	    Matrix ret = Values.getDefaultInstance().newInstance(dimension());
	    //@todo if we kept componentFunctions in a Matrix, we could perhaps use mapping of iterators().
	    // component-wise
	    for (int i = 0; i < dimension().height; i++)
		for (int j = 0; j < dimension().width; j++)
		    ret.set(i, j, (Arithmetic) componentFunction[i][j].apply(x));
	    return ret;
	} 

	public Function derive() {
	    throw new UnsupportedOperationException("not yet implemented");
	} 

	public Function integrate() {
	    throw new UnsupportedOperationException("not yet implemented");
	}

	public String toString() {
	    //XXX: should be provided in superclass
	    String		 nl = System.getProperty("line.separator");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < dimension().height; i++) {
		sb.append((i == 0 ? "" : nl) + '[');
		for (int j = 0; j < dimension().width; j++)
		    sb.append((j == 0 ? "" : ",\t") + componentFunction[i][j]);
		sb.append(']');
	    } 
	    return sb.toString();
	}
    }


    /**
     * A ComponentCompositeFunction is a matrix-valued BinaryFunction
     * defined with a matrix of binary component-functions.
     * 
     * @structure inherit BinaryFunction
     * @structure concretizes MathFunctor.Composite
     * @structure composite componentFunction:BinaryFunction[][] unidirectional
     * @version 0.7, 2001/02/23
     * @author  Andr&eacute; Platzer
     * @see ComponentCompositeFunction
     */
    /*enclosing class orbital.math.functional.BinaryFunction */
    static class MatrixComponentCompositeBinaryFunction/*ComponentCompositeFunction<Matrix.class> */ extends MathFunctor_CompositeFunctor implements BinaryFunction {
	private BinaryFunction componentFunction[][];
	public MatrixComponentCompositeBinaryFunction(BinaryFunction componentFunction[][]) {
	    this(componentFunction, null);
	}

	/**
	 * Create (f<sub>i,j</sub>).
	 * @param notation specifies which notation should be used for string representations.
	 */
	public MatrixComponentCompositeBinaryFunction(BinaryFunction componentFunction[][], Notation notation) {
	    super(notation);
	    this.componentFunction = componentFunction;
	}

	/**
	 * Get the dimension of the resulting matrix.
	 */
	public Dimension dimension() {
	    return new Dimension(componentFunction[0].length, componentFunction.length);
	} 
	public Object getCompositor() {
	    return null;
	} 

	/**
	 * Get the inner component functions applied per element.
	 */
	public Object getComponent() {
	    return componentFunction;
	} 

	public void setCompositor(Object f) throws IllegalArgumentException {
	    if (f != null)
		throw new IllegalArgumentException("cannot set compositor");
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.componentFunction = (BinaryFunction[][]) g;
	}

	/**
	 * Called to apply the component composite function.
	 * @return (f<sub>i,j</sub>)(x) = <big>(</big>f<sub>i,j</sub>(x)<big>)</big> as a {@link orbital.math.Matrix}.
	 */
	public Object apply(Object x, Object y) {
	    Matrix ret = Values.getDefaultInstance().newInstance(dimension());
	    //@todo if we kept componentFunctions in a Matrix, we could perhaps use mapping of iterators().
	    // component-wise
	    for (int i = 0; i < dimension().height; i++)
		for (int j = 0; j < dimension().width; j++)
		    ret.set(i, j, (Arithmetic) componentFunction[i][j].apply(x, y));
	    return ret;
	} 

	public BinaryFunction derive() {
	    throw new UnsupportedOperationException("not yet implemented");
	} 

	public BinaryFunction integrate(int i) {
	    throw new UnsupportedOperationException("not yet implemented");
	}

	public String toString() {
	    //XXX: should be provided in superclass
	    String		 nl = System.getProperty("line.separator");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < dimension().height; i++) {
		sb.append((i == 0 ? "" : nl) + '[');
		for (int j = 0; j < dimension().width; j++)
		    sb.append((j == 0 ? "" : ",\t") + componentFunction[i][j]);
		sb.append(']');
	    } 
	    return sb.toString();
	}
    }
    
}
