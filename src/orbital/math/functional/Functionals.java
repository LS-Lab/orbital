/**
 * @(#)Functionals.java 1.0 2000/08/04 Andre Platzer
 * 
 * Copyright (c) 1996-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;
import orbital.math.Tensor;
import orbital.math.Vector;
import orbital.math.Matrix;

import orbital.math.Arithmetic;
import orbital.logic.sign.concrete.Notation;
import orbital.math.Values;
import orbital.math.Scalar;

import java.util.Iterator;
import java.util.ListIterator;

import java.lang.reflect.Array;
import orbital.util.GeneralComplexionException;

import java.util.logging.Logger;
import java.util.logging.Level;
import orbital.util.Utility;

//TODO: enhance documentation
//TODO: implement like those new features contained in the superclass

/**
 * Provides important compositional functionals for mathematical functions.
 * For that, this module class contains several static methods that work like <dfn>Functionals</dfn>,
 * i.e. high-order functions that have a Function in their signature.
 * <p>
 * The Functionals class comes in two versions:<ul>
 * <li><tt>{@linkplain orbital.logic.functor.Functionals orbital.logic.functor.Functionals}</tt>
 * which is responsible for general functors
 * like functions and predicates that do not satisfy the criteria of full
 * mathematical functions with all the possible operations like derivation,
 * or that do not need this overhead.
 * Additonally this class provides general recursion schemes and things that do not
 * make much sense for mathematical functions, or would hurt derivation operations
 * (with a possible infinite recursion).
 * </li>
 * <li><tt>{@linkplain orbital.math.functional.Functionals orbital.math.functional.Functionals}</tt>
 * which is an specialized extension for mathematical functions
 * that can be derived or integrated and possibly need this behaviour.
 * Mathematical functions will usually be called with arithmetic objects and functions
 * only.
 * Functional operations which are the same like for more general functors are
 * not contained here, too.</li>
 * </ul>
 * </p>
 * <p>
 * <b><i>Evolves</i>:</b> This class might under some circumstances be renamed or unified
 * with the orbital.logic.functor.Functionals, which is not very likely to happen,
 * since both classes address fairly different levels of composition.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 * @see orbital.logic.functor.Functionals
 * @see Function
 * @see BinaryFunction
 * @see java.util.Collection
 * @see java.util.Iterator
 * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
 * @note compose methods cannot have a return-type of Function.Composite etc. since they optimize away compositions with 1 and 0.
 */
public class Functionals extends orbital.logic.functor.Functionals /*@todo uncomment once all compiling errors have been removed extends orbital.logic.functor.Functionals*/ {
    private static final Logger logger = Logger.getLogger(Functionals.class.getPackage().getName());
    /**
     * Class alias object.
     */
    public static final Functionals functionals = new Functionals();

    /**
     * prevent instantiation - module class
     * @todo sure?
     */
    protected Functionals() {}

    // composition functionals

    /**
     * compose:  (f,g) &#8614; f &#8728; g := f(g).
     * @return x&#8614;f &#8728; g (x) = f<big>(</big>g(x)<big>)</big>.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     * @see Function.Composite
     */
    public static Function compose(Function f, Function g) {
        // optimized composition of constant function
        // fast check for f.derive().norm() == 0
        if (f instanceof Functions.ConstantFunction)
            return f;
        return new Compositions.CompositeFunction(f, g);
    } 

    /**
     * compose:  (f,g,h) &#8614; f &#8728; (g &times; h) := f(g,h) .
     * @return (x,y)&#8614;f<big>(</big>g(x,y),h(x,y)<big>)</big>.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     * @see BinaryFunction.Composite
     */
    public static BinaryFunction compose(BinaryFunction f, BinaryFunction g, BinaryFunction h) {
        // optimized composition of constant function
        if (f instanceof Functions.BinaryConstantFunction)
            return f;
        return new Compositions.CompositeBinaryFunction(f, g, h);
    } 

    /**
     * compose:  (f,g,h) &#8614; f &#8728; (g &times; h) := f(g,h) .
     * @return x&#8614;f<big>(</big>g(x),h(x)<big>)</big>.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     * @see BinaryCompositeFunction
     */
    public static Function compose(BinaryFunction f, Function g, Function h) {
        // optimized composition of constant function
        if (f instanceof Functions.BinaryConstantFunction)
            return Functions.constant(((Functions.BinaryConstantFunction) f).a);
        return new BinaryCompositeFunction(f, g, h);
    } 

    /**
     * A BinaryCompositeFunction is a Function that is composed of
     * a BinaryFunction and two Functions concatenated with the binary one.
     * <p>
     * compose: Map(A<sub>1</sub>&times;R<sub>2</sub>,B)&times;Map(C,A<sub>1</sub>)&times;Map(C,A<sub>2</sub>)&rarr;Map(C,B); (f,g,h) &rarr; f &#8728;(g &times; h) := f(g,h) .
     * In other words results f<big>(</big>g(x),h(x)<big>)</big>.
     * 
     * @structure inherit Function
     * @structure concretizes MathFunctor.Composite
     * @structure aggregate outer:BinaryFunction
     * @structure aggregate left:Function
     * @structure aggregate right:Function
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functionals#compose(BinaryFunction, Function, Function)
     */
    private static class BinaryCompositeFunction extends MathFunctor_CompositeFunctor implements Function.Composite {
        protected BinaryFunction outer;
        protected Function         left;
        protected Function         right;
        public BinaryCompositeFunction(BinaryFunction f, Function g, Function h) {
            this(f, g, h, null);
        }

        /**
         * Create f(g,h).
         * @param notation specifies which notation should be used for string representations.
         */
        public BinaryCompositeFunction(BinaryFunction f, Function g, Function h, Notation notation) {
            super(notation);
            this.outer = f;
            this.left = g;
            this.right = h;
        }

        private BinaryCompositeFunction() {}

        public Object getCompositor() {
            return outer;
        } 
        public Object getComponent() {
            return new Function[] {
                left, right
            };
        } 

        public void setCompositor(Object f) throws ClassCastException {
            this.outer = (BinaryFunction) f;
        }
        public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
            Function[] a = (Function[]) g;
            if (a.length != 2)
                throw new IllegalArgumentException(Function.class + "[2] expected");
            this.left = (Function) a[0];
            this.right = (Function) a[1];
        }

        /**
         * @return outer<big>(</big>left(arg),right(arg)<big>)</big>.
         */
        public Object apply(Object arg) {
            return outer.apply(left.apply(arg), right.apply(arg));
        } 

        /**
         * o<big>(</big>l(x),r(x)<big>)</big>' = o'<big>(</big>l(x),r(x)<big>)</big> &middot; <big>(</big>l'(x),r'(x)<big>)</big>
         * = <i>d</i>o/<i>d</i>x (l(x),r(x))*l'(x) + <i>d</i>o/<i>d</i>y (l(x),r(x))*r'(x).
         * @preconditions 0<=i && i<=1
         */
        public Function derive() {
            //XXX: think about: ensure that (A*B)' = A'*B + A*B' instead of B*A' + A*B' which is important for non-commutative matrices
            BinaryFunction[] od = ((BinaryFunction[][]) ((MathFunctor.Composite) outer.derive()).getComponent())[0];
            return Functionals.compose(Operations.plus, Functionals.compose(Operations.times, left.derive(), Functionals.compose(od[0], left, right)), Functionals.compose(Operations.times, Functionals.compose(od[1], left, right), right.derive()));
            //return Functionals.compose(Operations.plus, Functionals.compose(Operations.times, Functionals.compose(outer.derive(0), left, right), left.derive()), Functionals.compose(Operations.times, Functionals.compose(outer.derive(1), left, right), right.derive()));
        } 

        public Function integrate() {
            // simple cases
            if (outer == Operations.plus)
                return Functionals.compose(Operations.plus, left.integrate(), right.integrate());
            else if (outer == Operations.subtract)
                return Functionals.compose(Operations.subtract, left.integrate(), right.integrate());
            else if (outer == Operations.times) {
                if (false) {
                    //XXX: avoid infinite recursion, but use base case or abort instead
                    // if (left is constant || right is constant) 
                    // try partial integration
                    try {
                        Function leftIntegral = left.integrate();
                        return Functionals.compose(Operations.subtract, Functionals.compose(Operations.times, leftIntegral, right), Functionals.compose(Operations.times, leftIntegral, right.derive()).integrate());
                    } catch (GeneralComplexionException trial) {}
                    try {
                        Function rightIntegral = right.integrate();
                        return Functionals.compose(Operations.subtract, Functionals.compose(Operations.times, left, rightIntegral), Functionals.compose(Operations.times, left.derive(), rightIntegral).integrate());
                    } catch (GeneralComplexionException trial) {}
                }
            }
                                
            throw new GeneralComplexionException("integrating a composition would require integral substitution");
        }
    }

    /**
     * generic compose calls the compose function appropriate for the type of g.
     * @param g having one of the types {@link orbital.math.functional.Function}, {@link orbital.math.functional.Function Function[]}, {@link orbital.math.functional.Function Function[][]}, and {@link orbital.math.Arithmetic}.
     *  In the latter case, composition is done using a constant function.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     * @todo note that we might better resolve compositions with constants by binding in order to support the problem with reverting from constant functions back to normal in @see orbital.logic.trs.Substitutions#lambda
     */
    public static MathFunctor genericCompose(Function f, Object g) {
        if (g instanceof MathFunctor) {
            if (g instanceof Function)
                return Functionals.compose(f, (Function) g);
            if ((g instanceof Function[]) || (g instanceof Function[][]))
                return genericCompose(f, genericCompose(g));
        } else
            // compose for other types of arithmetics that are NOT any functions at all
            if ((g instanceof Arithmetic) && !(g instanceof Functor))
                return Functionals.compose(f, Functions.constant((Arithmetic) g));
        throw new IllegalArgumentException("illegal type to compose " + (g == null ? "null" : g.getClass() + ""));
    } 
    /**
     * generic compose uses component compose function appropriate for the type of g.
     * @param f an object having one of the types {@link orbital.math.functional.Function Function[]}, {@link orbital.math.functional.Function Function[][]},
     * {@link orbital.math.functional.BinaryFunction BinaryFunction[]}, {@link orbital.math.functional.BinaryFunction BinaryFunction[][]}.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     * @todo document
     */
    public/**/ static MathFunctor.Composite genericCompose(Object f) {
        //TODO: how to access CoordinateComponentCompositeFunction now? Still needed? Efficiency?
        if (f instanceof Function[])
            return new ComponentCompositions.ComponentCompositeFunction((Function[]) f);
        else if (f instanceof Function[][])
            return new ComponentCompositions.MatrixComponentCompositeFunction((Function[][]) f);
        //TODO: implement or think of a better way unifying all these compositions
        /*else if (f instanceof BinaryFunction[])
          return new ComponentCompositeBinaryFunction((BinaryFunction[]) f);*/
        else if (f instanceof BinaryFunction[][])
            return new ComponentCompositions.MatrixComponentCompositeBinaryFunction((BinaryFunction[][]) f);
        throw new IllegalArgumentException("illegal type to compose " + (f == null ? "null" : f.getClass() + ""));
    }

    /**
     * generic compose calls the compose function appropriate for the type of g and h.
     * @param g having one of the types listed for h.
     * @param h having one of the types {@link orbital.math.functional.Function}, {@link orbital.math.functional.BinaryFunction}
     *  and {@link orbital.math.Arithmetic}.
     *  In the latter case, composition is done using a constant function.
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade Pattern</a>
     */
    public static MathFunctor genericCompose(BinaryFunction f, Object g, Object h) {
        if (g instanceof MathFunctor || h instanceof MathFunctor) {
            // compose for equal types
            if (g instanceof Function && h instanceof Function)
                return Functionals.compose(f, (Function) g, (Function) h);
            else if (g instanceof BinaryFunction && h instanceof BinaryFunction)
                return Functionals.compose(f, (BinaryFunction) g, (BinaryFunction) h);
    
            //XXX: see MathUtilities.makeSymbolAware(Arithmetic) calls to constant(...)
            // compose for mixed types as well
            else if (g instanceof Function && !(h instanceof MathFunctor) && h instanceof Arithmetic)
                return Functionals.compose(f, (Function) g, Functions.constant((Arithmetic) h));
            else if (!(g instanceof MathFunctor) && g instanceof Arithmetic && h instanceof Function)
                return Functionals.compose(f, Functions.constant((Arithmetic) g), (Function) h);
            else if (g instanceof BinaryFunction && !(h instanceof MathFunctor) && h instanceof Arithmetic)
                return Functionals.compose(f, (BinaryFunction) g, Functions.binaryConstant((Arithmetic) h));
            else if (!(g instanceof MathFunctor) && g instanceof Arithmetic && h instanceof BinaryFunction)
                return Functionals.compose(f, Functions.binaryConstant((Arithmetic) g), (BinaryFunction) h);
        } else
            // compose for other types of arithmetics that are NOT any functions at all
            if (!(g instanceof Functor) && g instanceof Arithmetic && !(h instanceof Functor) && h instanceof Arithmetic)
                return Functionals.compose(f, Functions.constant((Arithmetic) g), Functions.constant((Arithmetic) h));
        throw new IllegalArgumentException("the type of the arguments to compose do not match: " + (g == null ? "null" : g.getClass() + "") + " and " + (h == null ? "null" : h.getClass() + ""));
    } 


    // argument binding and swapping

    /**
     * Binds the first argument of a BinaryFunction to a fixed value.
     * <p>
     * bindFirst: Map(A<sub>1</sub>&times;A<sub>2</sub>,B)&rarr;Map(A<sub>2</sub>,B); f&#8614;f(x,&middot;).
     * The unary left-adjoint.</p>
     * @return f(x,&middot;):A<sub>2</sub>&rarr;B; y &#8614; f(y) := f(x, y)
     */
    public static /*<A1 extends Arithmetic, A2 extends Arithmetic, B extends Arithmetic>*/ Function/*<A2,B>*/ bindFirst(final BinaryFunction/*<A1,A2,B>*/ f, final Object/*>A1<*/ x) {
        return new AbstractFunction/*<A2,B>*/() {
                public Object/*>B<*/ apply(Object/*>A2<*/ y) {
                    return f.apply(x, y);
                } 
                public Function derive() {
                    logger.log(Level.FINE, "bindFirst.derive()", ((Object[][]) ((MathFunctor.Composite) f.derive()).getComponent())[0][1]);
                    return bindFirst(((BinaryFunction[][]) ((MathFunctor.Composite) f.derive()).getComponent())[0][1], x);
                } 
                public Function integrate() {
                    return bindFirst(f.integrate(1), x);
                }
                public String toString() {
                    return Notation.DEFAULT.format(f, new Object[] {
                        x, "#0"
                    });
                } 
            };
    } 

    /**
     * Binds the second argument of a BinaryFunction to a fixed value.
     * <p>
     * bindSecond: Map(A<sub>1</sub>&times;A<sub>2</sub>,B)&rarr;Map(A<sub>1</sub>,B); f&#8614;f(&middot;,y).
     * The unary right-adjoint.</p>
     * @return f(&middot;,y):A<sub>1</sub>&rarr;B; x &#8614; f(x) := f(x, y)
     */
    public static /*<A1 extends Arithmetic, A2 extends Arithmetic, B extends Arithmetic>*/ Function/*<A1,B>*/ bindSecond(final BinaryFunction/*<A1,A2,B>*/ f, final Object/*>A2<*/ y) {
        return new AbstractFunction/*<A1,B>*/() {
                public Object/*>B<*/ apply(Object/*>A1<*/ x) {
                    return f.apply(x, y);
                } 
                public Function derive() {
                    logger.log(Level.FINE, "bindFirst.derive()", ((BinaryFunction[][]) ((MathFunctor.Composite) f.derive()).getComponent())[0][0]);
                    return bindSecond(((BinaryFunction[][]) ((MathFunctor.Composite) f.derive()).getComponent())[0][0], y);
                } 
                public Function integrate() {
                    return bindSecond(f.integrate(0), y);
                }
                public String toString() {
                    return Notation.DEFAULT.format(f, new Object[] {
                        "#0", y
                    });
                } 
            };
    } 

    /**
     * Binds both arguments of a BinaryFunction together.
     * <p>
     * bind: Map(A&times;A,B)&rarr;Map(A,B); f&#8614;g.
     * The unitary adjoint function.</p>
     * <p>derive:  bind' = &part;f/&part;x (x,x) + &part;f/&part;y (x,x).</p>
     * <p>
     * If, for example, f is a bilinear form &beta;:V&timesV&rarr;K this method
     * will return its square form Q:V&rarr;K;x&#8614;Q(x):=&beta;(x,x).</p>
     * @return g:A&rarr;B; x &#8614; g(x) := f(x, x)
     * @todo check & verify
     */
    public static /*<A extends Arithmetic, B extends Arithmetic>*/ Function/*<A,B>*/ bind(final BinaryFunction/*<A,A,B>*/ f) {
        return new AbstractFunction/*<A,B>*/() {
                public Object/*>B<*/ apply(Object/*>A<*/ x) {
                    return f.apply(x, x);
                } 
                public Function derive() {
                    // check that d/dx (x*x) = 2x
                    throw new UnsupportedOperationException("TODO: check & verify");
                    //return compose(Operations.plus, bind(f.derive(0)), bind(f.derive(1)));
                } 
                public Function integrate() {
                    throw new UnsupportedOperationException("dunno");
                }
                public String toString() {
                    return Notation.DEFAULT.format(f, new String[] {
                        "#0", "#0"
                    });
                } 
            };
    } 


    /**
     * Applies a function on the first argument, ignoring the second.
     * <p>
     * onFirst:  f&rarr;g; (x,y) &#8614; g(x,y) := f(x).</p>
     * <p><b><i>Evolves</i>:</b> might be renamed.</p>
     */
    public static /*<A1 extends Arithmetic, B extends Arithmetic>*/ BinaryFunction/*<A1, Arithmetic, B>*/ onFirst(final Function/*<A1, B>*/ f) {
        return new AbstractBinaryFunction/*<A1,Arithmetic,B>*/() {
                public Object/*>B<*/ apply(Object/*>A1<*/ first, Object/*>Arithmetic<*/ second) {
                    return f.apply(first);
                } 
                public BinaryFunction derive() {
                    return (BinaryFunction) genericCompose(new BinaryFunction[][] {
                        {onFirst(f.derive()), Functions.binaryzero}
                    });
                } 
                public BinaryFunction integrate(int i) {
                    Utility.pre(0 <= i && i <= 1, "binary integral");
                    return i == 0 ? Functionals.onFirst(f.integrate()) : Functions.binaryzero;
                } 
                // XXX: not quite beautyful for all functors
                public String toString() {
                    return Notation.DEFAULT.format(f, new String[] {
                        "#0"                                    //TODO: use #n everywhere //"x", "_"
                    });
                } 
            };
    } 

    /**
     * Applies a function on the second argument, ignoring the first.
     * <p>
     * onSecond:  f&rarr;g; (x,y) &#8614; g(x,y) := f(y).</p>
     * <p><b><i>Evolves</i>:</b> might be renamed.</p>
     */
    public static /*<A2 extends Arithmetic, B extends Arithmetic>*/ BinaryFunction/*<Arithmetic, A2, B>*/ onSecond(final Function/*<A2, B>*/ f) {
        return new AbstractBinaryFunction/*<Arithmetic,A2,B>*/() {
                public Object/*>B<*/ apply(Object/*>Arithmetic<*/ first, Object/*>A2<*/ second) {
                    return f.apply(second);
                } 
                public BinaryFunction derive() {
                    return (BinaryFunction) genericCompose(new BinaryFunction[][] {
                        {Functions.binaryzero, onSecond(f.derive())}
                    });
                } 
                public BinaryFunction integrate(int i) {
                    Utility.pre(0 <= i && i <= 1, "binary integral");
                    return i == 0 ? Functions.binaryzero : Functionals.onSecond(f.integrate());
                } 
                public String toString() {
                    return Notation.DEFAULT.format(f, new String[] {
                        "#1",                                   //"_", "y"
                    });
                } 
            };
    } 

    /**
     * @preconditions 0==i || i==1
     */
    static BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ on(int i, final Function/*<Arithmetic,Arithmetic>*/ f) {
        Utility.pre(0==i || i==1, "binary function");
        return i == 0 ? onFirst(f) : onSecond(f);
    }

    /**
     * Swaps the two arguments of a BinaryFunction.
     * <p>
     * swap: Map(A<sub>1</sub>&times;A<sub>2</sub>,B)&rarr;Map(A<sub>2</sub>&times;A<sub>1</sub>,B); f&#8614;f<sup>&harr;</sup>.</p>
     * <p>derive: swap' = (f<sub>y</sub>(y,x), f<sub>x</sub>(y,x)).</p>
     * </p>
     * @return f<sup>&harr;</sup>:A<sub>2</sub>&times;A<sub>1</sub>&rarr;B; (x,y) &#8614; f<sup>&harr;</sup>(x,y) := f(y,x)
     */
    public static /*<A1 extends Arithmetic, A2 extends Arithmetic, B extends Arithmetic>*/ BinaryFunction/*<A2, A1, B>*/ swap(final BinaryFunction/*<A1, A2, B>*/ f) {
        return new AbstractBinaryFunction/*<A2,A1,B>*/() {
                public Object/*>B<*/ apply(Object/*>A2<*/ x, Object/*>A1<*/ y) {
                    return f.apply(y, x);
                } 
                public BinaryFunction derive() {
                    BinaryFunction fd[] = ((BinaryFunction[][]) ((MathFunctor.Composite) f.derive()).getComponent())[0];
                    return (BinaryFunction) genericCompose(new BinaryFunction[][] {
                        {swap(fd[1]), swap(fd[0])}
                    });
                } 
                public BinaryFunction integrate(int i) {
                    Utility.pre(0 <= i && i <= 1, "binary integral");
                    return i == 0 ? f.integrate(1) : f.integrate(0);
                } 
                public String toString() {
                    return Notation.DEFAULT.format(f, new String[] {
                        "#1", "#0"
                    });
                } 
            };
    } 

    // functional-style bulk operations

    /**
     * Maps a list of arguments with a function.
     * @see orbital.logic.functor.Functionals#listable(Function)
     */
    public static /*<A, B>*/ Tensor/*<B>*/ map(Function/*<A, B>*/ f, Tensor/*<A>*/ a) {
        return (Tensor) map(f, (Object) a);
    }
    public static /*<A, B>*/ Vector/*<B>*/ map(Function/*<A, B>*/ f, Vector/*<A>*/ a) {
        return (Vector) map(f, (Tensor)a);
    }
    public static /*<A, B>*/ Matrix/*<B>*/ map(Function/*<A, B>*/ f, Matrix/*<A>*/ a) {
        return (Matrix) map(f, (Tensor)a);
    }
    /**
     * Maps two lists of arguments with a binary function.
     * @see orbital.logic.functor.Functionals#listable(BinaryFunction)
     */
    public static /*<A1, A2, B>*/ Tensor/*<B>*/ map(BinaryFunction/*<A1, A2, B>*/ f, Tensor/*<A1>*/ x, Tensor/*<A2>*/ y) {
        Utility.pre(Utility.equalsAll(x.dimensions(), y.dimensions()), "compatible dimensions");
        Tensor r = Values.getDefaultInstance().newInstance(x.dimensions());
        mapInto(f, x.iterator(), y.iterator(), r.iterator());
        return r;
    }
    public static /*<A1, A2, B>*/ Vector/*<B>*/ map(BinaryFunction/*<A1, A2, B>*/ f, Vector/*<A1>*/ x, Vector/*<A2>*/ y) {
        return (Vector) map(f, (Tensor)x, (Tensor)y);
    }
    public static /*<A1, A2, B>*/ Matrix/*<B>*/ map(BinaryFunction/*<A1, A2, B>*/ f, Matrix/*<A1>*/ x, Matrix/*<A2>*/ y) {
        return (Matrix) map(f, (Tensor)x, (Tensor)y);
    }


    // legacy conversion and migration functions
    
    /**
     * @see orbital.logic.functor.Functionals#foldRight(orbital.logic.functor.BinaryFunction, Object, Object[])
     */
    public static double foldRight(orbital.logic.functor.BinaryFunction f, double c, double[] a) {
        final Values vf = Values.getDefaultInstance();
        Object result = vf.valueOf(c);
        for (int i = a.length - 1; i >= 0; i--)
            result = f.apply(vf.valueOf(a[i]), result);
        return ((Number) result).doubleValue();
    } 
    
    /**
     * @see orbital.logic.functor.Functionals#map(orbital.logic.functor.Function, Object[])
     */
    public static double[] map(orbital.logic.functor.Function f, double[] a) {
        final Values vf = Values.getDefaultInstance();
        double[] r = new double[a.length];
        for (int i = 0; i < r.length; i++)
            r[i] = ((Number) f.apply(vf.valueOf(a[i]))).doubleValue();
        return r;
    } 
    public static int[] map(orbital.logic.functor.Function f, int[] a) {
        return (int[]) mapImpl(f, a);
    }
    public static long[] map(orbital.logic.functor.Function f, long[] a) {
        return (long[]) mapImpl(f, a);
    }
    public static float[] map(orbital.logic.functor.Function f, float[] a) {
        return (float[]) mapImpl(f, a);
    }
    /**
     * @preconditions a is an array of a primitive type or its compound wrapper class.
     * @postconditions RES.getClass() == a.getClass()
     * @see #map(orbital.logic.functor.Function, double[])
     * @see orbital.logic.functor.Functionals#map(orbital.logic.functor.Function, Object[])
     * @see java.lang.Number
     */
    private static Object mapImpl(orbital.logic.functor.Function f, Object a) {
        Utility.pre(a != null && a.getClass().isArray(), "map(Function, Object) works on arrays of primitive types or their compound wrapper classes, only");
        final Values vf = Values.getDefaultInstance();
        Object r = Array.newInstance(a.getClass().getComponentType(), Array.getLength(a));
        for (int i = 0; i < Array.getLength(r); i++) {
            Object o = f.apply(vf.valueOf((Number) Array.get(a, i)));
            Array.set(r, i, Values.isPrimitiveWrapper(o.getClass()) ? o : Values.toPrimitiveWrapper((Scalar)o));
        }
        return r;
    }

    /**
     * @internal perhaps somwhat faster implementation than mapImpl(BinaryFunction,Object,Object)
     * @see orbital.logic.functor.Functionals#map(orbital.logic.functor.BinaryFunction, Object[], Object[])
     */
    public static double[] map(orbital.logic.functor.BinaryFunction f, double[] x, double[] y) {
        Utility.pre(x.length == y.length, "argument arrays must have same length");
        final Values vf = Values.getDefaultInstance();
        double[] a = new double[x.length];
        for (int i = 0; i < a.length; i++)
            a[i] = ((Number) f.apply(vf.valueOf(x[i]), vf.valueOf(y[i]))).doubleValue();
        return a;
    } 
    public static int[] map(orbital.logic.functor.BinaryFunction f, int[] x, int[] y) {
        return (int[]) mapImpl(f, x, y);
    }
    public static long[] map(orbital.logic.functor.BinaryFunction f, long[] x, long[] y) {
        return (long[]) mapImpl(f, x, y);
    }
    public static float[] map(orbital.logic.functor.BinaryFunction f, float[] x, float[] y) {
        return (float[]) mapImpl(f, x, y);
    }
    /**
     * @preconditions x.length == y.length && x and y are arrays of a primitive type or its compound wrapper class.
     * @postconditions RES.getClass() == x.getClass()
     * @see #map(orbital.logic.functor.BinaryFunction, double[], double[])
     * @see orbital.logic.functor.Functionals#map(orbital.logic.functor.BinaryFunction, Object[], Object[])
     * @see java.lang.Number
     */
    private static Object mapImpl(orbital.logic.functor.BinaryFunction f, Object x, Object y) {
        Utility.pre(x != null && x.getClass().isArray()
                    && y != null && y.getClass().isArray(), "map(BinaryFunction, Object, Object) works on arrays of primitive types or their compound wrapper classes, only");
        Utility.pre(Array.getLength(x) == Array.getLength(y), "argument arrays must have same length");
        final Values vf = Values.getDefaultInstance();
        Object r = Array.newInstance(x.getClass().getComponentType(), Array.getLength(x));
        for (int i = 0; i < Array.getLength(r); i++) {
            Object o = f.apply(vf.valueOf((Number) Array.get(x, i)), vf.valueOf((Number) Array.get(y, i)));
            Array.set(r, i, Values.isPrimitiveWrapper(o.getClass()) ? o : Values.toPrimitiveWrapper((Scalar)o));
        }
        return r;
    }

    /**
     * Nests a function n times within itself.
     * <p>
     * nest:  (f,n) &#8614; f<sup>n</sup> = f &#8728; f &#8728; ... &#8728; f (n times).<br>
     * nest(f,n).apply(A) gives an expression with f applied n times to A.</p>
     * @param f the function to be nested.
     * @param n the number of times the f should be composed.
     * @preconditions n>=0
     */
    public static Function nest(Function f, int n) {
        Utility.pre(n >= 0, "non negative nesting expected");
        // only compose with id if necessary
        if (n == 0)
            return Functions.id;
        Function r = f;
        for (int i = 1; i < n; i++)
            r = compose(f, r);
        return r;
    } 

    
    // some toolkit functionals
    
    /**
     * A function that performs an operation pointwise.
     * <p>
     * For Arithmetic objects this will be the elemental function applied on x.
     * For Function objects this will be a composition of this pointwise operation
     * with x.
     * </p>
     * @param elemental the elemental function f to perform on Arithmetic x.
     * @return x &#8614; if x instanceof Arithmetic but not of MathFunctor then f(x) else f &#8728; x fi.
     * @todo document pointwise better
     */
    public static Function pointwise(Function elemental) {
        return new Functions.PointwiseFunction(elemental);
    }

    /**
     * A BinaryFunction that performs an operation pointwise.
     * <p>
     * For Arithmetic objects this will be the elemental function applied on x and y.
     * For functor objects this will be a composition of this pointwise operation
     * with x and y.
     * </p>
     * @param elemental the elemental function to perform on Arithmetic x.
     * @return (x,y) &#8614; if x and y instanceof Arithmetic but not of MathFunctor then f(x,y) else f &#8728; (x,y) fi.
     */
    public static BinaryFunction pointwise(BinaryFunction elemental) {
        return new Functions.PointwiseBinaryFunction(elemental);
    }
}
