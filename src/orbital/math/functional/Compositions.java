/**
 * @(#)Compositions..java 1.0 2001/11/22 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;

import orbital.logic.sign.concrete.Notation;

import orbital.util.GeneralComplexionException;


/**
 * Contains implementations of composite functors.
 *
 * @version 1.0, 2001/11/22
 * @author  Andr&eacute; Platzer
 * @note package-level protected to orbital.math.functional
 * @see Functionals
 */
class Compositions {
    /**
     * prevent instantiation - module class
     */
    private Compositions() {}

    /**
     * A CompositeFunction is a Function that is composed of
     * two Functions with one concatenated with the other.
     * <p>
     * compose:  Map(A,B)&times;Map(C,A)&rarr;Map(C,B); (f,g) &#8614; f &#8728; g := f(g).<br />
     * derive  (f &#8728; g)' = (f' &#8728; g) &middot; g'.</p>
     * 
     * @structure inherit Function
     * @structure concretizes MathFunctor.Composite
     * @structure aggregate outer:Function
     * @structure aggregate inner:Function
     * @version 1.0, 1999/03/16
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, Function)
     */
    static class CompositeFunction extends MathFunctor_CompositeFunctor implements Function.Composite {
	protected Function outer;
	protected Function inner;
	public CompositeFunction(Function f, Function g) {
	    this(f, g, null);
	}

	/**
	 * Create <code>f(g)</code>.
	 * @param notation specifies which notation should be used for string representations.
	 */
	public CompositeFunction(Function f, Function g, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.inner = g;
	}

	private CompositeFunction() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (Function) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.inner = (Function) g;
	}

	public Object apply(Object x) {
	    return outer.apply(inner.apply(x));
	} 

	/**
	 * <i>d</i>/<i>d</i>x o(i) = (o' &#8728; i) &middot; i'.
	 */
	public Function derive() {
	    return (Function) Functionals.compose(Operations.times, Functionals.compose(outer.derive(), inner), inner.derive());
	} 
		
	public Function integrate() {
	    // simple cases
	    if (outer == Operations.minus)
		return Functionals.compose(Operations.minus, inner.integrate());
	    else if (outer == Functions.id)
		return inner.integrate();
	    throw new GeneralComplexionException("integrating a composition would require integral substitution");
	}
    }

    /**
     * A CompositeFunction is a BinaryFunction that is composed of
     * an outer BinaryFunction and two inner BinaryFunctions concatenated with the outer binary one.
     * <p>
     * compose: Map(A<sub>1</sub>&times;A<sub>2</sub>,B)&times;Map(C<sub>1</sub>&times;C<sub>2</sub>,A<sub>1</sub>)&times;Map(C<sub>1</sub>&times;C<sub>2</sub>,A<sub>2</sub>)&rarr;Map(C<sub>1</sub>&times;C<sub>2</sub>,B); (o,l,r) &rarr; o &#8728; (l &times; t)<sup>T</sup> := o(l,r).
     * In other words, results o<big>(</big>l(x,y),r(x,y)<big>)</big>.
     * 
     * @structure inherit BinaryFunction
     * @structure concretizes MathFunctor.Composite
     * @structure aggregate outer:BinaryFunction
     * @structure aggregate left:BinaryFunction
     * @structure aggregate right:BinaryFunction
     * @version 1.0, 1999/03/16
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryFunction, BinaryFunction, BinaryFunction)
     */
    static class CompositeBinaryFunction extends MathFunctor_CompositeFunctor implements BinaryFunction.Composite {
	protected BinaryFunction outer;
	protected BinaryFunction left;
	protected BinaryFunction right;
	public CompositeBinaryFunction(BinaryFunction outer, BinaryFunction left, BinaryFunction right) {
	    this(outer, left, right, null);
	}

	/**
	 * Create <code>f(g,h)</code>.
	 * @param notation specifies which notation should be used for string representations.
	 */
	public CompositeBinaryFunction(BinaryFunction outer, BinaryFunction left, BinaryFunction right, Notation notation) {
	    super(notation);
	    this.outer = outer;
	    this.left = left;
	    this.right = right;
	}
		
	private CompositeBinaryFunction() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return new BinaryFunction[] {left, right};
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (BinaryFunction/*_<B1, B2, C>_*/) f;
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    BinaryFunction[] a = (BinaryFunction[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(BinaryFunction.class + "[2] expected");
	    this.left = a[0];
	    this.right = a[1];
	}

	/**
	 * @return outer<big>(</big>left(x,y),right(x,y)<big>)</big>.
	 */
	public Object apply(Object x, Object y) {
	    return outer.apply(left.apply(x, y), right.apply(x, y));
	} 

	/**
	 * &part;/&part;(x,y) <big>(</big>o &#8728; (l &times; r)<sup>T</sup><big>)</big> = o'<big>(</big>(l,r)<big>)</big> &middot; (l' &times; r')
	 * = &part;o/&part;x (l,r) &middot; l' + &part;o/&part;y (l,r) &middot; r'.
	 */
	//TODO: update documentation here, there, and everywhere
	public BinaryFunction derive() {
	    return Functionals.compose(Operations.times, Functionals.compose(outer.derive(), left, right), (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
		{left.derive(), right.derive()}
	    }));
	} 

	public BinaryFunction integrate(int i) {
	    // simple cases
	    if (outer == Operations.plus)
		return Functionals.compose(Operations.plus, left.integrate(i), right.integrate(i));
	    else if (outer == Operations.subtract)
		return Functionals.compose(Operations.subtract, left.integrate(i), right.integrate(i));
	    throw new GeneralComplexionException("integrating a composition would require integral substitution");
	}
    }
}
