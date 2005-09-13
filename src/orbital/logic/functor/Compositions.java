/**
 * @(#)Compositions..java 1.0 2001/11/22 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import orbital.logic.sign.concrete.Notation;

/**
 * Contains implementations of composite functors.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.logic.functor.Functionals
 * @note package-level protected to orbital.logic.functor
 * @todo serialVersionUIDs
 */
class Compositions {
    /**
     * prevent instantiation - module class
     */
    private Compositions() {}

    /**
     * A Function that is composed of
     * two Functions with one concatenated with the other.
     * <p>
     * compose: (f,g) &#8614; f &#8728; g := f(g)
     * </p>
     * 
     * @structure implements Function
     * @structure concretizes Functor.Composite.Abstract
     * @structure aggregate outer:Function
     * @structure aggregate inner:Function
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, Function)
     * @internal we have a union interface of Function and Functor.Composite, then
     *  we do not need to have this class public.
     *  The same goes for similar .Composite* classes here and in math.functional.
     */
    /*public*/ static class CompositeFunction/*<A, B, C>*/ extends AbstractCompositeFunctor implements Function/*<A, C>*/.Composite {
	private static final long serialVersionUID = -3564816275499022044L;
	/**
	 * @serial
	 */
	protected Function/*<B, C>*/ outer;
	/**
	 * @serial
	 */
	protected Function/*<A, B>*/ inner;
	public CompositeFunction(Function/*<B, C>*/ f, Function/*<A, B>*/ g) {
	    this(f, g, null);
	}

	/**
	 * Create <code>f(g)</code>.
	 * @param notation specifies which notation should be used for string representations.
	 */
	public CompositeFunction(Function/*<B, C>*/ f, Function/*<A, B>*/ g, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.inner = g;
	}
		
	protected CompositeFunction() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (Function/**<B, C>**/) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.inner = (Function/**<A, B>**/) g;
	}

	/**
	 * @return <code>outer<big>(</big>inner(arg)<big>)</big></code>.
	 */
	public Object/*>C<*/ apply(Object/*>A<*/ arg) {
	    return outer.apply(inner.apply(arg));
	} 
    }

    /**
     * A composed BinaryFunction.
     * <div>compose:  (f,g,h) &#8614; f &#8728; (g &times; h) := f(g,h).</div>
     * <p>
     * Binary functions can be composed of an outer BinaryFunction and two inner BinaryFunctions concatenated with the outer binary one.
     * In other words, results f<big>(</big>g(x,y),h(x,y)<big>)</big>.
     * </p>
     * 
     * @structure implements BinaryFunction
     * @structure concretizes Functor.Composite.Abstract
     * @structure aggregate outer:BinaryFunction
     * @structure aggregate left:BinaryFunction
     * @structure aggregate right:BinaryFunction
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryFunction, BinaryFunction, BinaryFunction)
     */
    /*public*/ static class CompositeBinaryFunction/*<A1, A2, B1, B2, C>*/ extends AbstractCompositeFunctor implements BinaryFunction/*<A1, A2, C>*/.Composite {
	private static final long serialVersionUID = 5501060120707478195L;
	/**
	 * @serial
	 */
	protected BinaryFunction/*<B1, B2, C>*/ outer;
	/**
	 * @serial
	 */
	protected BinaryFunction/*<A1, A2, B1>*/ left;
	/**
	 * @serial
	 */
	protected BinaryFunction/*<A1, A2, B2>*/ right;
	public CompositeBinaryFunction(BinaryFunction/*<B1, B2, C>*/ f, BinaryFunction/*<A1, A2, B1>*/ g, BinaryFunction/*<A1, A2, B2>*/ h) {
	    this(f, g, h, null);
	}

	/**
	 * Create f(g,h).
	 * @param notation specifies which notation should be used for string representations.
	 */
	public CompositeBinaryFunction(BinaryFunction/*<B1, B2, C>*/ f, BinaryFunction/*<A1, A2, B1>*/ g, BinaryFunction/*<A1, A2, B2>*/ h, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.left = g;
	    this.right = h;
	}

	protected CompositeBinaryFunction() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return new BinaryFunction[] {
		left, right
	    };
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (BinaryFunction/**<B1, B2, C>**/) f;
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    BinaryFunction[] a = (BinaryFunction[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(BinaryFunction.class + "[2] expected");
	    // parametric-typed instanceof checks and parametric-typed casting from non-parametric objects are not supported by GenericJava
	    /*else if (!(a[0] instanceof BinaryFunction<A1, A2, B1>))
	      throw new ClassCastException(a[0].getClass().getName());
	      else if (!(a[1] instanceof BinaryFunction<A1, A2, B2>))
	      throw new ClassCastException(a[1].getClass().getName());*/
	    this.left = a[0];
	    this.right = a[1];
	}

	/**
	 * @return <code>outer<big>(</big>left(first,second),right(first,second)<big>)</big></code>.
	 */
	public Object/*>C<*/ apply(Object/*>A1<*/ first, Object/*>A2<*/ second) {
	    return outer.apply(left.apply(first, second), right.apply(first, second));
	} 
    }

    /**
     * A VoidFunction that is composed of
     * a Function and a VoidFunction concatenated with eachother.
     * <p>
     * compose: <code> (f,g) &#8614; f &#8728; g := f(g) </code>.
     * 
     * @structure implements VoidFunction
     * @structure concretizes Functor.Composite.Abstract
     * @structure aggregate outer:Function
     * @structure aggregate inner:VoidFunction
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Function, Function)
     */
    /*public*/ static class CompositeVoidFunction/*<B, C>*/ extends AbstractCompositeFunctor implements VoidFunction/*<C>*/.Composite {
	private static final long serialVersionUID = 3582647885986645043L;
	/**
	 * @serial
	 */
	protected Function/*<B, C>*/	 outer;
	/**
	 * @serial
	 */
	protected VoidFunction/*<B>*/  inner;
	public CompositeVoidFunction(Function/*<B, C>*/ f, VoidFunction/*<B>*/ g) {
	    this(f, g, null);
	}

	/**
	 * Create <code>f(g)</code>.
	 * @param notation specifies which notation should be used for string representations.
	 */
	public CompositeVoidFunction(Function/*<B, C>*/ f, VoidFunction/*<B>*/ g, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.inner = g;
	}

	protected CompositeVoidFunction() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (Function/**<B, C>**/) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.inner = (VoidFunction/**<B>**/) g;
	}


	/**
	 * @return <code>outer<big>(</big>inner()<big>)</big></code>.
	 */
	public Object/*>C<*/ apply() {
	    return outer.apply(inner.apply());
	} 
    }

    /**
     * A Predicate that is composed of
     * an outer Predicate and an inner Function.
     * <p>
     * compose: <code> (P,g) &#8614; P &#8728; g := P(g) </code>.
     * 
     * @structure implements Predicate
     * @structure concretizes Functor.Composite.Abstract
     * @structure aggregate outer:Predicate
     * @structure aggregate inner:Function
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Predicate, Function)
     */
    /*public*/ static class CompositePredicate/*<A, B>*/ extends AbstractCompositeFunctor implements Predicate/*<A>*/.Composite {
	private static final long serialVersionUID = -967722688216761906L;
	/**
	 * @serial
	 */
	protected Predicate/*<B>*/ outer;
	/**
	 * @serial
	 */
	protected Function/*<A, B>*/  inner;
	public CompositePredicate(Predicate/*<B>*/ f, Function/*<A, B>*/ g) {
	    this(f, g, null);
	}
	public CompositePredicate(Predicate/*<B>*/ f, Function/*<A, B>*/ g, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.inner = g;
	}

	protected CompositePredicate() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (Predicate/**<B>**/) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.inner = (Function/**<A, B>**/) g;
	}


	/**
	 * @return <code>outer<big>(</big>inner(arg)<big>)</big></code>.
	 */
	public boolean apply(Object/*>A<*/ arg) {
	    return outer.apply(inner.apply(arg));
	} 
    }

    /**
     * A BinaryPredicate that is composed of
     * an outer BinaryPredicate and two inner BinaryFunctions concatenated with the outer binary one.
     * <p>
     * compose: <code> (f,g,h) &#8614; f o (g,h) := f(g,h) </code>.<br>
     * In other words, results <code>f<big>(</big>g(x,y),h(x,y)<big>)</big></code>.
     * 
     * @structure implements BinaryPredicate
     * @structure concretizes Functor.Composite.Abstract
     * @structure aggregate outer:BinaryPredicate
     * @structure aggregate left:BinaryFunction
     * @structure aggregate right:BinaryFunction
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryPredicate, BinaryFunction, BinaryFunction)
     */
    /*public*/ static class CompositeBinaryPredicate/*<A1, A2, B1, B2>*/ extends AbstractCompositeFunctor implements BinaryPredicate/*<A1, A2>*/.Composite {
	private static final long serialVersionUID = -805916714673823795L;
	/**
	 * @serial
	 */
	protected BinaryPredicate/*<B1, B2>*/ outer;
	/**
	 * @serial
	 */
	protected BinaryFunction/*<A1, A2, B1>*/  left;
	/**
	 * @serial
	 */
	protected BinaryFunction/*<A1, A2, B2>*/  right;
	public CompositeBinaryPredicate(BinaryPredicate/*<B1, B2>*/ f, BinaryFunction/*<A1, A2, B1>*/ g, BinaryFunction/*<A1, A2, B2>*/ h) {
	    this(f, g, h, null);
	}

	/**
	 * Create <code>f(g,h)</code>.
	 * @param notation specifies which notation should be used for string representations.
	 */
	public CompositeBinaryPredicate(BinaryPredicate/*<B1, B2>*/ f, BinaryFunction/*<A1, A2, B1>*/ g, BinaryFunction/*<A1, A2, B2>*/ h, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.left = g;
	    this.right = h;
	}

	protected CompositeBinaryPredicate() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return new BinaryFunction[] {
		left, right
	    };
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (BinaryPredicate) f;
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    BinaryFunction[] a = (BinaryFunction[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(BinaryFunction.class + "[2] expected");
	    this.left = a[0];
	    this.right = a[1];
	}

	/**
	 * @return <code>outer<big>(</big>left(first,second),right(first,second)<big>)</big></code>.
	 */
	public boolean apply(Object/*>A1<*/ first, Object/*>A2<*/ second) {
	    return outer.apply(left.apply(first, second), right.apply(first, second));
	} 
    }
	
    /**
     * A VoidPredicate that is composed of
     * an outer Predicate and an inner VoidFunction.
     * <p>
     * compose: <code> (P,g) &#8614; P &#8728; g := P(g) </code>.
     * 
     * @structure implements VoidPredicate
     * @structure concretizes Functor.Composite.Abstract
     * @structure aggregate outer:Predicate
     * @structure aggregate inner:Function
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(Predicate, VoidFunction)
     */
    /*public*/ static class CompositeVoidPredicate/*<B>*/ extends AbstractCompositeFunctor implements VoidPredicate.Composite {
	private static final long serialVersionUID = -1703403316513864511L;
	/**
	 * @serial
	 */
	protected Predicate/*<B>*/	 outer;
	/**
	 * @serial
	 */
	protected VoidFunction/*<B>*/ inner;
	public CompositeVoidPredicate(Predicate/*<B>*/ f, VoidFunction/*<B>*/ g) {
	    this(f, g, null);
	}
	public CompositeVoidPredicate(Predicate/*<B>*/ f, VoidFunction/*<B>*/ g, Notation notation) {
	    super(notation);
	    this.outer = f;
	    this.inner = g;
	}

	protected CompositeVoidPredicate() {}

	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (Predicate/**<B>**/) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    this.inner = (VoidFunction/**<B>**/) g;
	}

	/**
	 * @return <code>outer<big>(</big>inner()<big>)</big></code>.
	 */
	public boolean apply() {
	    return outer.apply(inner.apply());
	} 
    }
}
