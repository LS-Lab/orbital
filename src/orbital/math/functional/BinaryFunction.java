/**
 * @(#)BinaryFunction.java 1.0 1999/06/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;

import orbital.logic.functor.Notation;

import orbital.math.Arithmetic;
import orbital.math.Matrix;
import orbital.math.Values;

import orbital.moon.math.AbstractFunctor;

import java.awt.Dimension;

/**
 * This interface encapsulates a binary function "r = f(x,y)".
 * <p>
 * apply: A&times;B&rarr;C; (x,y) &#8614; f(x,y).<br />
 * Where A&times;A is often written as A<sup>2</sup> as well.</p>
 * 
 * @structure inherits orbital.logic.functor.BinaryFunction
 * @structure inherits orbital.math.functional.MathFunctor
 * @version 1.0, 2000/08/05
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 */
public interface BinaryFunction/*<A1 implements Arithmetic, A2 implements Arithmetic, B implements Arithmetic>*/ extends orbital.logic.functor.BinaryFunction/*<A1, A2, B>*/, MathFunctor {

    /**
     * Derives this function and returns the resulting BinaryFunction
     * <i>d</i>f/<i>d</i>(x,y).
     * Which is the total derivative with respect to the arguments x, y.
     * 
     * @throws ArithmeticException if this function is not differentiable.
     * @throws UnsupportedOperationException if this function does not implement derivation but could principally be derived.
     */
    BinaryFunction derive();
	
    /**
     * Integrates this function and returns the resulting indefinite integral &int; f <i>d</i>x<sub>i</sub>.
     * @return the indefinite integral Function &int; f <i>d</i>x<sub>i</sub>.
     * @throws ArithmeticException if this function is not integrable.
     * @throws UnsupportedOperationException if this function does not implement integration but could principally be integrated.
     * @pre 0<=i && i<=1
     */
    BinaryFunction integrate(int i);


    /**
     * A composite function.
     * <div>compose: Map(A<sub>1</sub>&times;A<sub>2</sub>,B)&times;Map(C<sub>1</sub>&times;C<sub>2</sub>,A<sub>1</sub>)&times;Map(C<sub>1</sub>&times;C<sub>2</sub>,A<sub>2</sub>)&rarr;Map(C<sub>1</sub>&times;C<sub>2</sub>,B); (o,l,r) &rarr; o &#8728; (l &times; t)<sup>T</sup> := o(l,r).</div>
     * <div>derive: &part;/&part;(x,y) <big>(</big>f &#8728; (g &times; h)<sup>T</sup><big>)</big> = f'<big>(</big>(g,h)<big>)</big> &middot; (g' &times; h')
     *    = &part;f/&part;x (g,h) &middot; g' + &part;f/&part;y (g,h) &middot; h'.
     * </div>
     * <p>
     * A BinaryFunction could be composed of
     * an outer BinaryFunction and two inner BinaryFunctions concatenated with the outer binary one.
     * In other words, results o<big>(</big>l(x,y),r(x,y)<big>)</big>.
     * </p>
     * <p>
     * This class is the infimum (greatest common subtype) {@link orbital.logic.functor.Functor.Composite}&cap;{@link BinaryFunction}.
     * </p>
     * 
     * @structure is {@link Functor.Composite}&cap;{@link BinaryFunction}
     * @structure extends BinaryFunction<A1,A2, B>
     * @structure extends orbital.logic.functor.BinaryFunction<A1,A2, B>.Composite
     * @structure aggregate outer:BinaryFunction
     * @structure aggregate left:BinaryFunction
     * @structure aggregate right:BinaryFunction
     * @version 1.0, 1999/03/16
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryFunction, BinaryFunction, BinaryFunction)
     */
    static interface Composite extends orbital.logic.functor.BinaryFunction/*<A1,A2,B>*/.Composite, BinaryFunction/*<A1,A2,B>*/, MathFunctor.Composite {}
}

/*private static*/ abstract class AbstractBinaryFunction/*<A1 implements Arithmetic, A2 implements Arithmetic, B implements Arithmetic>*/ extends AbstractFunctor implements BinaryFunction/*<A1,A2,B>*/ {}

/**
 * A ComponentCompositeFunction is a matrix-valued BinaryFunction defined with a matrix of binary component-functions.
 * 
 * @structure inherit BinaryFunction
 * @structure concretizes MathFunctor.Composite
 * @structure composite componentFunction:BinaryFunction[][] unidirectional
 * @version 0.7, 2001/02/23
 * @author  Andr&eacute; Platzer
 * @see ComponentCompositeFunction
 */
/*enclosing class orbital.math.functional.BinaryFunction */
class MatrixComponentCompositeBinaryFunction/*ComponentCompositeFunction<Matrix.class> */ extends MathFunctor_CompositeFunctor implements BinaryFunction {
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
	this.componentFunction = (BinaryFunction[][]) g;
    }

    /**
     * Called to apply the component composite function.
     * @return (f<sub>i,j</sub>)(x) = <big>(</big>f<sub>i,j</sub>(x)<big>)</big> as a {@link orbital.math.Matrix}.
     */
    public Object apply(Object x, Object y) {
	Matrix ret = Values.getInstance(dimension());
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
