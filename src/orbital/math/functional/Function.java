/**
 * @(#)Function.java 1.0 1999/06/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;

import orbital.math.Arithmetic;
import orbital.math.Normed;
import orbital.logic.functor.Notation;

import orbital.moon.math.AbstractFunctor;

import orbital.math.Matrix;
import java.awt.Dimension;
import orbital.math.Vector;
import orbital.math.Values;

/**
 * This interface encapsulates a mathematical unary function "r = f(x)".
 * <p>
 * apply: A&rarr;B; x &#8614; f(x).
 * </p>
 * 
 * @structure inherit orbital.logic.functor.Function
 * @structure inherit orbital.math.functional.MathFunctor
 * @version 1.0, 2000/08/01
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 * @see orbital.logic.functor.Function
 */
public interface Function/*<A implements Arithmetic, B implements Arithmetic>*/ extends orbital.logic.functor.Function/*<A, B>*/, MathFunctor {

    /**
     * Derives this function and returns the resulting Function <i>d</i>f/<i>d</i>x.
     * @return the derived Function <i>d</i>f/<i>d</i>x = f'.
     *    Which is the (total) derivative with respect to the argument x.
     *    The partial derivative with respect to the argument x would be &part;f/&part;x.
     * @throws ArithmeticException if this function is not derivable.
     * @throws UnsupportedOperationException if this function does not implement derivation but could principally be derived.
     */
    Function/* <A, Arithmetic> */ derive();

    /**
     * Integrates this function and returns the resulting indefinite integral &int; f <i>d</i>x.
     * @return the indefinite integral Function &int; f <i>d</i>x.
     * @throws ArithmeticException if this function is not integrable.
     * @throws UnsupportedOperationException if this function does not implement integration but could principally be integrated.
     */
    Function integrate();

    /**
     * A composite function.
     * <p>
     * <div>compose: Map(A,B)&times;Map(C,A)&rarr;Map(C,B); (f,g) &#8614; f &#8728; g := f(g).</div>
     * <div>derive: (f &#8728; g)' = (f' &#8728; g) &middot; g'.</div>
     * </p>
     * A Function could be composed of
     * two Functions with one concatenated with the other.
     * </p>
     * <p>
     * <p>
     * This class is the infimum (greatest common subtype) {@link orbital.logic.functor.Functor.Composite}&cap;{@link Function}.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link Function}
     * @structure extends Function<A,B>
     * @structure extends orbital.logic.functor.Function<A, B>.Composite
     * @structure aggregate outer:Function
     * @structure aggregate inner:Function
     * @version 1.0, 1999/03/16
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, Function)
     */
    static interface Composite extends orbital.logic.functor.Function/*<A, B>*/.Composite, Function/*<A, B>*/, MathFunctor.Composite {}
}

//TODO: make public or make public inner class?
// this implements the union of MathFunctor and Function, and extends the default pointwise arithmetic operations
/*private static*/ abstract class AbstractFunction/*<A implements Arithmetic, B implements Arithmetic>*/  extends AbstractFunctor implements Function/*<A,B>*/ {}


/**
 * A ComponentCompositeFunction is a vectorial Function defined with a vector of component-functions.
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
/*static*/ class ComponentCompositeFunction/*ComponentCompositeFunction<Vector.class> */ extends MathFunctor_CompositeFunctor implements Function {
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
    public Functor getCompositor() {
	return null;
    } 

    /**
     * Get the inner component functions applied per element.
     */
    public Object getComponent() {
	return componentFunction;
    } 

    public void setCompositor(Functor f) throws IllegalArgumentException {
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
	Vector r = Values.getInstance(dimension());
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
 * A ComponentCompositeFunction is a matrix Function defined with component-functions.
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
/*static*/ class MatrixComponentCompositeFunction/*ComponentCompositeFunction<Matrix.class> */ extends MathFunctor_CompositeFunctor implements Function {
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
    public Functor getCompositor() {
	return null;
    } 

    /**
     * Get the inner component functions applied per element.
     */
    public Object getComponent() {
	return componentFunction;
    } 

    public void setCompositor(Functor f) throws IllegalArgumentException {
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
	Matrix ret = Values.getInstance(dimension());
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
	
	
/*
  public static double gamma(double d)
  throws IllegalArgumentException
  {
  if(d > 170D)
  return (1.0D / 0.0D);
  if(d is negative integer)
  throw new IllegalArgumentException((new MathlibMessages()).getMessage(0, d, "gamma"));
  if(d < 0.0D)
  return 3.1415926535897931D / (Math.sin(3.1415926535897931D * d) * gamma(1.0D - d));
  if(d > 13D)
  return str(d);
  int j;
  d += j = (int)Math.floor(13D - d) + 1;
  double d1 = str(d);
  for(int i = j; i >= 1; i--)
  d1 /= --d;

  return d1;
  }
*/
