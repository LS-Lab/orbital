/**
 * @(#)Functions.java 1.0 2000/08/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;
import orbital.math.UnivariatePolynomial;

import orbital.math.Arithmetic;
import orbital.math.Normed;
import orbital.logic.functor.Notation;
import orbital.logic.functor.Predicate;

import orbital.moon.math.AbstractFunctor;

import orbital.math.Matrix;
import java.awt.Dimension;
import orbital.math.Vector;
import orbital.math.Scalar;
import orbital.math.Complex;
import orbital.math.Values;
import orbital.math.Real;
import orbital.math.Integer;

import orbital.logic.trs.Variable;

import orbital.util.Utility;
import orbital.util.GeneralComplexionException;

/**
 * Common function implementations.
 * @stereotype &laquo;Module&raquo;
 * @version 1.0, 2000/08/01
 * @author  Andr&eacute; Platzer
 */
public final class Functions {
    /**
     * Class alias object.
     * <p>
     * To alias the methods in this class, use an idiom like<pre>
     * <span class="comment">// alias object</span>
     * <span class="Orbital">Functions</span> F = <span class="Orbital">Functions</span>.functions;
     * <span class="Orbital">Operations</span> op = <span class="Orbital">Operations</span>.operations;
     * <span class="comment">// use alias</span>
     * <span class="Orbital">Function</span> f = (<span class="Orbital">Function</span>) op.times.apply(F.sin, op.plus.apply(F.square, F.cos));
     * <span class="comment">// instead of the long form</span>
     * <span class="Orbital">Function</span> f = (<span class="Orbital">Function</span>) <span class="Orbital">Operations</span>.times.apply(<span class="Orbital">Functions</span>.sin, <span class="Orbital">Operations</span>.plus.apply(<span class="Orbital">Functions</span>.square, <span class="Orbital">Functions</span>.cos));
     * </pre>
     * </p>
     */
    public static final Functions functions = new Functions();

    private static final Values valueFactory = Values.getDefaultInstance();

    private static final Scalar TWO = valueFactory.valueOf(2);

    /**
     * prevent instantiation - module class
     */
    private Functions() {}
    
    
    // unary functions
	
    // elemental functions

    /**
     * zero: R&rarr;R; x &#8614; 0 .
     * @todo should we return x.zero()
     */
    public static final Function zero = constant(valueFactory.valueOf(0));
    //TODO: transform to anonymous inner class to improve integration (zero or id for below)

    /**
     * one: R&rarr;R; x &#8614; 1 .
     * @todo should we return x.one()
     */
    public static final Function one = constant(valueFactory.valueOf(1));

    /**
     * constant &acirc;: R&rarr;R; x &#8614; a .
     * <p>derive: &acirc;' = 0<br />
     * integrate: &int;a<i>d</i>x = a*x</p>
     */
    public static final /*<A implements Arithmetic, B implements Arithmetic>*/ Function/*<A,B>*/ constant(Object/*>B<*/ a) {
	return new ConstantFunction/*<A,B>*/(a);
    }
    /**
     * A constant function.
     * <p>
     * constant &acirc;: R&rarr;R; x &#8614; a.</p>
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
     * @todo should we really introduce an interface(or orbital.logic.trs.Variable?) and provide boolean isConstant() {return true;} and getCompositor() {return c;} (why not simply apply();?)
     * @todo could we change this to Function<A implements Arithmetic,M> or to Function<Arithmetic,M>?
     */
    static final class ConstantFunction/*<A implements Arithmetic, B implements Arithmetic>*/ extends AbstractFunction/*<A,B>*/ implements orbital.logic.functor.VoidFunction/*<B>*/, Variable {
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
	public Object/*>B<*/ apply(Object/*>A<*/ x) {
	    return apply();
	} 
	public Function derive() {
	    Real b = ((Normed) a).norm();
	    return b.isInfinite() || b.isNaN() ? constant(Values.NaN) : zero;
	} 
	public Function integrate() {
	    return linear((Arithmetic)a);
	} 
	public Real norm() {
	    return ((Normed) a).norm();
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
     * symbolic f:R&rarr;R; x &#8614; f(x).
     * <p>derive: (f)' = f'<br />
     * integrate: &int;f(x)<i>d</i>x = &int;f(x)<i>d</i>x</p>
     * @param name the name of the symbolic function.
     * @return a pure symbolic function with a specified name.
     */
    public static final Function symbolic(String name) {
	return new SymbolicFunction(name);
    }
    private static final class SymbolicFunction extends AbstractFunction/*<Arithmetic,Arithmetic>*/ {
    	private String name;
    	public SymbolicFunction(String name) {
	    this.name = name;
    	}
	public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
	    return Functionals.genericCompose(this, x);
	} 
	public Function derive() {
	    return symbolic(name + "'");
	} 
	public Function integrate() {
	    return symbolic("\u222B" + name + " dx");
	} 
	public Real norm() {
	    return Values.NaN;
	}
	public boolean equals(Object o) {
	    return (o instanceof SymbolicFunction)
		&& Utility.equals(name, ((SymbolicFunction) o).name);
	}
	public int hashCode() {
	    return Utility.hashCode(name);
	}
	public String toString() {
	    return name + "";
	} 
    } 

    /**
     * id: R&rarr;R; x &#8614; x .
     * <p>derive: id' = 1<br />
     * integrate: &int;id <i>d</i>x = x<sup>2</sup>/2</p>
     */
    public static final Function id = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		return x;
	    } 
	    public Function derive() {
		return one;
	    } 
	    public Function integrate() {
		return (Function) Operations.divide.apply(square, TWO);
	    } 
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "id";
	    } 
	};

    /**
     * linear: A&rarr;B; x &#8614; a*x.
     * <p>derive: linear' = a.<br />
     * integrate: &int;a*x <i>d</i>x = a*x<sup>2</sup>/2</p>
     * <p>
     * linear functions are Lipschitz-continuous.</p>
     * <p>
     * The concrete sets A and B depend on the exact type of a. For instance if a is a Matrix in K<sup>m&times;n</sup>
     * this function is the linear homomorphism K<sup>n</sup>&rarr;K<sup>m</sup>; x &#8614; a*x.</p>
     */
    public static final /*<M implements Arithmetic>*/ Function/*<M,M>*/ linear(final Arithmetic/*>M<*/ a) {
	return new AbstractFunction/*<M,M>*/() {
		public Object/*>M<*/ apply(Object/*>M<*/ x) {
		    return (Object/*>M<*/) a.multiply((Arithmetic) x);
		} 
		public Function derive() {
		    return constant(a);
		} 
		public Function integrate() {
		    return (Function) Operations.times.apply(Operations.divide.apply(a,TWO), square);
		} 
		public String toString() {
		    return a + "*x";
		} 
	    };
    } 

    /**
     * reciprocal: <b>C</b>\{0}&rarr;<b>C</b>; x &#8614; x<sup>-1</sup> = 1 / x.
     * <p>derive: reciprocal' = -x<sup>-2</sup><br />
     * integrate: &int;x<sup>-1</sup> <i>d</i>x = &#13266; x</p>
     * <p>
     * It is generally preferred to use Operations.inverse instead!</p>
     * @see Operations#inverse
     */
    public static final Function reciprocal = Operations.inverse;

    /**
     * pow<sub>p</sub>: R&rarr;R; x &#8614; x<sup>p</sup> .
     * <p>derive: (x<sup>p</sup>)' = p*x<sup>p-1</sup><br />
     * integrate: &int;x<sup>p</sup> <i>d</i>x = x<sup>p+1</sup>/(p+1)</p>
     */
    public static final Function pow(Arithmetic p) {
	return Functionals.bindSecond(Operations.power, p);
    } 
    public static final Function pow(double p) {
	return pow(valueFactory.valueOf(p));
    }

    /**
     * square: R&rarr;R; x &#8614; x<sup>2</sup> .
     * <p>derive: square' = 2*id<br />
     * integrate: &int;x<sup>2</sup> <i>d</i>x = x<sup>3</sup>/3</p>
     * <p>Implementation uses faster x*x for Values.</p>
     */
    public static final Function square = new SynonymFunction(pow(TWO)) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (Complex.hasType.apply(x)) {
		    Complex v = (Complex) x;
		    return v.multiply(v);
		} else if (x instanceof Number) {
		    double v = ((Number) x).doubleValue();
		    return valueFactory.valueOf(v * v);
		} else {
		    Arithmetic v = (Arithmetic) x;
		    return v.multiply(v);
		}
	    } 
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "x^2";
	    } 
	};

    /**
     * sqrt &radic;<span style="text-decoration: overline">&nbsp;</span>: <b>C</b>&rarr;<b>C</b>; x &#8614; &radic;<span style="text-decoration: overline">x</span> = x<sup>1/2</sup>.
     * <p>derive: &radic;<span style="text-decoration: overline">x</span>' = -1/2/&radic;<span style="text-decoration: overline">x</span><br />
     * integrate: &int; &radic;<span style="text-decoration: overline">x</span><i>d</i>x = 2/3*x<sup>3/2</sup></p>
     * <p>Implementation uses faster {@link Math#sqrt(double)} for scalars in [0,&infin;).</p>
     * <p>For complex numbers z=r*<b>e</b><sup><b>i</b>*&phi;</sup>&ne;0 this function returns
     * <div>&radic;<span style="text-decoration: overline">|z|</span>*<b>e</b><sup><b>i</b>*&phi;/2</sup> = &radic;<span style="text-decoration: overline">r</span> * <big>(</big>cos(&phi;/2) + <b>i</b>*sin(&phi;/2)<big>)</big></div>
     * But just like real numbers, the negative of this is a square root as well.</p>
     * </p>
     */
    public static final Function sqrt = new SynonymFunction(pow(valueFactory.valueOf(0.5))) {
	    public Object/*>Complex<*/ apply(Object/*>Complex<*/ x) {
		if (Complex.hasType.apply(x)) {
		    Complex v = (Complex) x;
		    return valueFactory.polar((Real/*__*/)apply(v.norm()), v.arg().divide(valueFactory.valueOf(2)));
		} 
		else if (x instanceof Number) {
		    double r = ((Number) x).doubleValue();
		    if (!(r == r))			// Double.isNaN
			return Values.NaN;
		    return r >= 0 ? valueFactory.valueOf(Math.sqrt(r)) : apply(valueFactory.cartesian(r, 0));
		}
		return (Complex) super.apply(x);
	    } 
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "sqrt";
	    } 
	};


    // elementary transcendental functions

    /**
     * exp: <b>C</b>&rarr;<b>C</b>\{0}; x &#8614; <b>e</b><sup>x</sup> = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> x<sup>n</sup> / n!.
     * <p>derive: (<b>e</b><sup>x</sup>)' = <b>e</b><sup>x</sup><br />
     * integrate: &int;<b>e</b><sup>x</sup> <i>d</i>x = <b>e</b><sup>x</sup></p>
     * <p>For complex numbers x = a + <b>i</b>*b&isin;<b>C</b>; a,b&isin;<b>R</b> this function returns <b>e</b><sup>a + <b>i</b>*b</sup> = <b>e</b><sup>a</sup><b>e</b><sup><b>i</b>*b</sup> = <b>e</b><sup>a</sup> * <big>(</big>cos(b) + <b>i</b>*sin(b)<big>)</big>.</p>
     * <p>
     * exp-function is exactly 2k&pi;<b>i</b>-periodic.
     * This is due to the relation
     * &forall;z&isin;<b>C</b> <b>e</b><sup>z+&omega;</sup> = <b>e</b><sup>z</sup>
     * &hArr; <b>e</b><sup>&omega;</sup> = 1
     * &hArr; &omega; = 2k&pi;<b>i</b>; k&isin;<b>Z</b>.</p>
     * @see #log
     * @see orbital.math.Values#polar(Real,Real)
     */
    public static final Function exp = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (Complex.hasType.apply(x)) {
		    Complex z = (Complex) x;
		    return valueFactory.polar((Real) apply(z.re()), z.im());
		} 
		else if (x instanceof Number)
		    return valueFactory.valueOf(Math.exp(((Number) x).doubleValue()));
		else if (x instanceof orbital.math.Matrix)
		    throw new UnsupportedOperationException("not yet implemented - limit or at least jordan normalization required");
		else if (x instanceof orbital.math.Symbol)
		    return valueFactory.symbol("e").power((Arithmetic) x);
		else if (x instanceof Arithmetic)
		    throw new UnsupportedOperationException("not yet implemented - dunno");

		//XXX: return Functionals.genericCompose(exp, x);
		throw new UnsupportedOperationException("not yet implemented - JDK1.3 bug");
	    } 
	    public Function derive() {
		return this;
	    } 
	    public Function integrate() {
		return this;
	    } 
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		//TODO: use e^x and log(x) or remove (x) everywhere, or whatcha want?
		return "exp";
	    } 
	};

    /**
     * &#13266;: <b>C</b>\{0}&rarr;<b>C</b>; x &#8614; &#13266;<sub><b>e</b></sub> x.
     * <p>derive: &#13266;' = 1 / x<br />
     * integrate: &int;&#13266; x <i>d</i>x = x*&#13266; |x| - x</p>
     * <p>For complex numbers x=r*<b>e</b><sup><b>i</b>&phi;</sup>&isin;<b>C</b> this function returns the principal logarithm
     * <div>&#13266;(z) = &#13266;(r) + <b>i</b>*&phi; = &#13266; |z| + <b>i</b>*arg(z)</div>
     * But adding 2k&pi;<b>i</b> will lead to all other (complex) logarithms.
     * These multiple logarithms of complex numbers result from exp-function being
     * 2k&pi;<b>i</b> periodic.</p>
     * @see #exp
     */
    public static final Function log = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (Complex.hasType.apply(x)) {
		    Complex v = (Complex) x;
		    return valueFactory.cartesian((Real/*__*/) this.apply(v.norm()), v.arg());
		} 
		else if (x instanceof Number) {
		    double r = ((Number) x).doubleValue();
		    return r >= 0 ? valueFactory.valueOf(Math.log(r)) : apply(valueFactory.cartesian(r, 0));
		} else if (x instanceof orbital.math.Matrix)
		    throw new UnsupportedOperationException("not yet implemented - something like limit required");
		else if (x instanceof Arithmetic)
		    throw new UnsupportedOperationException("not yet implemented - dunno how to logarithm this type");

		// XXX: return Functionals.compose(log, x);
		throw new UnsupportedOperationException("not yet implemented - JDK1.3 bug");
	    } 
	    public Function derive() {
		return reciprocal;
	    }
	    public Function integrate() {
		//TODO: is there any real difference between Functions.norm() and Functions.abs()?
		return (Function) Operations.subtract.apply( Operations.times.apply(id, Functionals.compose(this, Functions.norm)), id);
	    }
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "log";
	    } 
	};

    /**
     * exp<sub>b</sub>: <b>C</b>&rarr;<b>C</b>\{0}; x &#8614; b<sup>x</sup> .
     * <p>derive: (b<sup>x</sup>)' = &#13266; b * b<sup>x</sup><br />
     * integrate: &int;b<sup>x</sup> <i>d</i>x = b<sup>x</sup> / &#13266; b</p>
     */
    public static final Function exp(final Arithmetic b) {
	return Functionals.bindFirst(Operations.power, b);
    } 

    // elementary transcendental functions
    // trigonometric functions

    /**
     * sin: <b>C</b>&rarr;<b>C</b>; x &#8614; sin x = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> (-1)<sup>n</sup> * x<sup>2n+1</sup> / (2n+1)!. Sine function.
     * <p>derive: sin' = cos<br />
     * integrate: &int;sin x <i>d</i>x = -cos x</p>
     * <p>For complex numbers z this function returns sin z = sinh(<b>i</b>*z)/<b>i</b> = (e<sup><b>i</b>*z</sup>-e<sup>-<b>i</b>*z</sup>) / (2<b>i</b>).</p>
     * <p>
     * functional equations:
     * sin(x+y) = sin x * cos y + cos x * sin y,
     * cos(x+y) = cos x * cos y - sin x * sin y.<br />
     * sin(x)<sup>2</sup> + cos(x)<sup>2</sup> = 1
     * </p>
     * <p>
     * This sinus looks like &#8767;.
     * </p>
     * @see #arcsin
     * @see #sinh
     */
    public static final Function sin = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (Complex.hasType.apply(x))
		    return ((Arithmetic) sinh.apply(Values.i.multiply((Arithmetic) x))).divide(Values.i);
		else if (x instanceof Number)
		    return valueFactory.valueOf(Math.sin(((Number) x).doubleValue()));
		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
		return cos;
	    } 
	    public Function integrate() {
		return Functionals.compose(Operations.minus, cos);
	    }
	    public Real norm() {
		return Values.ONE;
	    }
	    public String toString() {
		return "sin";
	    } 
	};

    /**
     * cos: <b>C</b>&rarr;<b>C</b>; x &#8614; cos x = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> (-1)<sup>n</sup> * x<sup>2n</sup> / (2n)!. Cosine function
     * <p>derive: cos' = -sin<br />
     * integrate: &int;cos x <i>d</i>x = sin x</p>
     * <p>For complex numbers z this function returns cos z = cosh(<b>i</b>*z) = (e<sup><b>i</b>*z</sup>+e<sup>-<b>i</b>*z</sup>) / 2.</p>
     * <p>
     * reductions: cos x = sin(&pi;/2+x)</p>
     * @see #arccos
     * @see #cosh
     */
    public static final Function cos = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (Complex.hasType.apply(x))
		    return cosh.apply(Values.i.multiply((Arithmetic) x));
		else if (x instanceof Number)
		    return valueFactory.valueOf(Math.cos(((Number) x).doubleValue()));
		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
		return Functionals.compose(Operations.minus, sin);
	    } 
	    public Function integrate() {
		return sin;
	    } 
	    public Real norm() {
		return Values.ONE;
	    }
	    public String toString() {
		return "cos";
	    } 
	};
	
    /**
     * tan: <b>C</b>\(&pi;/2+&pi;<b>Z</b>)&rarr;<b>C</b>; x &#8614; tan x = sin x / cos x. Tangent function.
     * <p>derive: tan' = sec<sup>2</sup> = 1/cos<sup>2</sup> = 1 + tan<sup>2</sup><br />
     * integrate: &int;tan x <i>d</i>x = - &#13266;(cos x)</p>
     * <p>For complex numbers z this function is tan z = tanh(<b>i</b>*z)/<b>i</b>.</p>
     * @see #arctan
     * @see #cot
     */
    public static final Function tan = new SynonymFunction(Functionals.compose(Operations.divide, sin, cos)) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (x instanceof Number && !Complex.hasType.apply(x))
		    return valueFactory.valueOf(Math.tan(((Number) x).doubleValue()));
		return super.apply(x);
	    } 
	    public Function derive() {
		return Functionals.compose(pow(TWO), sec);
	    } 
	    public Function integrate() {
		return Functionals.compose(Operations.minus, Functionals.compose(log, cos));
	    }
	    public String toString() {
		return "tan";
	    } 
	};

    /**
     * cot: <b>C</b>\&pi;<b>Z</b>&rarr;<b>C</b>; x &#8614; cot x = cos x / sin x = 1 / tan x. Cotangent function.
     * <p>derive: cot' = -csc<sup>2</sup> = -(1 + cot<sup>2</sup>).</p>
     * integrate: &int;cot x <i>d</i>x = &#13266;(sin x)</p>
     * <p>For complex numbers z this function is cot z = coth(<b>i</b>*z)*<b>i</b>.</p>
     * @see #tan
     * @todo cot[0.00062084327023249+i*0.9271391688545392] is completely wrong. Numerical precision problems?
     */
    public static final Function cot = new SynonymFunction(Functionals.compose(Operations.divide, cos, sin)) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		return ((Arithmetic) tan.apply(x)).inverse();
	    } 
	    public Function derive() {
		return (Function) Operations.minus.apply(pow(TWO).apply(csc));
	    } 
	    public Function integrate() {
		return Functionals.compose(log, sin);
	    }
	    public String toString() {
		return "cot";
	    } 
	};

    /**
     * csc: R\{0}&rarr;R; x &#8614; csc x = 1 / sin(x). Cosecant function.
     * <p>derive: csc' = -cot*csc</p>
     */
    public static final Function csc = new SynonymFunction(Functionals.compose(Operations.inverse, sin)) {
	    public Function derive() {
		return (Function) Operations.times.apply(Operations.minus.apply(cot), csc);
	    } 
	    public String toString() {
		return "csc(x)";
	    } 
	};

    /**
     * sec: R&rarr;R; x &#8614; sec x = 1 / cos(x). Secant function.
     * <p>derive: sec' = sec*tan.</p>
     * @todo sec[7.0213123543172] is completely wrong. Numerical precision problems?
     */
    public static final Function sec = new SynonymFunction(Functionals.compose(Operations.inverse, cos)) {
	    public Function derive() {
		return (Function) Operations.times.apply(sec, tan);
	    } 
	    public String toString() {
		return "sec";
	    } 
	};

    // inverse trigonometric functions

    /**
     * arcsin: [-1,1]&rarr;[-&pi;/2,&pi;/2]; x &#8614; arcsin x = sin<sup>-1</sup> x. Arc sine function.
     * <p>derive: arcsin' = <span class="Formula">1 / &radic;<span style="text-decoration: overline">1 - x<sup>2</sup></span></span><br />
     * integrate: &int;arcsin x <i>d</i>x = <span class="Formula">x * arcsin x + &radic;<span style="text-decoration: overline">1 - x<sup>2</sup></span></span></p>
     * <p>
     * arcsin x = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> (-1)<sup>n</sup> * nCr(-1/2, n) * x<sup>2n+1</sup> / (2n+1) on (-1,1).</p>
     * @see #sin
     */
    public static final Function arcsin = new AbstractFunction/*<Real,Real>*/() {
	    public Object/*>Real<*/ apply(Object/*>Real<*/ x) {
    		if (x instanceof Number)
		    return Values.getDefaultInstance().valueOf(Math.asin(((Number) x).doubleValue()));
    		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
    		return (Function) Functionals.compose(sqrt, Functionals.compose(Operations.subtract, one, square)).inverse();
	    } 
	    public Function integrate() {
    		return (Function) Operations.plus.apply(Operations.times.apply(id, this), Functionals.compose(sqrt, Functionals.compose(Operations.subtract, one, square)));
	    }
	    public Real norm() {
    		return valueFactory.valueOf(Math.PI / 2);
	    }
	    public String toString() {
    		return "arcsin";
	    } 
	};
    
    /**
     * arccos: [-1,1]&rarr;[0,&pi;]; x &#8614; arccos x = cos<sup>-1</sup> x. Arc cosine function.
     * <p>derive: arccos' = <span class="Formula">- 1 / &radic;<span style="text-decoration: overline">1 - x<sup>2</sup></span></span><br />
     * integrate: &int;arccos x <i>d</i>x = <span class="Formula">x * arccos x - &radic;<span style="text-decoration: overline">1 - x<sup>2</sup></span></span></p>
     * <p>
     * arccos x = &pi;/2 - arcsin x.</p>
     * @see #cos
     */
    public static final Function arccos = new AbstractFunction/*<Real,Real>*/() {
	    public Object/*>Real<*/ apply(Object/*>Real<*/ x) {
    		if (x instanceof Number)
		    return valueFactory.valueOf(Math.acos(((Number) x).doubleValue()));
    		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
    		return (Function) Functionals.compose(sqrt, Functionals.compose(Operations.subtract, one, square)).inverse().minus();
	    } 
	    public Function integrate() {
    		return (Function) Operations.subtract.apply(Operations.times.apply(id, this), Functionals.compose(sqrt, (Function) Functionals.compose(Operations.subtract, one, square)));
	    }
	    public Real norm() {
    		return Values.PI;
	    }
	    public String toString() {
    		return "arccos";
	    } 
	};
    
    
    /**
     * arctan: <b>R</b>&rarr;(-&pi;/2,&pi;/2); x &#8614; arctan x = tan<sup>-1</sup> x. Arc tangent function.
     * <p>derive: arctan' = <span class="Formula"> 1 / (1 + x<sup>2</sup>)</span><br />
     * integrate: &int;arctan x <i>d</i>x = <span class="Formula">x * arctan x - &#13266;(x<sup>2</sup> + 1)/2</span></p>
     * <p>
     * arctan x = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> (-1)<sup>n</sup> * x<sup>2n+1</sup> / (2n+1) on [-1,1].</p>
     * @see #tan
     */
    public static final Function arctan = new AbstractFunction/*<Real,Real>*/() {
	    public Object/*>Real<*/ apply(Object/*>Real<*/ x) {
    		if (x instanceof Number)
		    return valueFactory.valueOf(Math.atan(((Number) x).doubleValue()));
    		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
    		return (Function) Functionals.compose(Operations.plus, one, square).inverse();
	    } 
	    public Function integrate() {
    		return (Function) Operations.subtract.apply(Operations.times.apply(id, this), Functionals.compose(log, Functionals.compose(Operations.plus, square, one)).divide(TWO));
	    }
	    public Real norm() {
    		return valueFactory.valueOf(Math.PI / 2);
	    }
	    public String toString() {
    		return "arctan";
	    } 
	};
    
    /**
     * arccot: <b>R</b>&rarr;(0,&pi;); x &#8614; arccot x = cot<sup>-1</sup> x. Arc cotangent function.
     * <p>derive: arccot' = <span class="Formula">- 1 / (1 + x<sup>2</sup>)</span><br />
     * integrate: &int;arccot x <i>d</i>x = <span class="Formula">x * arccot x + &#13266;(x<sup>2</sup> + 1)/2</span></p>
     * <p>
     * arccot x = &pi;/2 - arctan x.</p>
     * @see #cot
     * @todo arccot[-7.068942486988963] = 3.0010612665159697 != ArcCot[-7.068942486988963] = -0.1405313870738235 differing by PI. Is this ok?
     */
    public static final Function arccot = new AbstractFunction/*<Real,Real>*/() {
	    private final double PI_HALF = Math.PI / 2;
	    public Object/*>Real<*/ apply(Object/*>Real<*/ x) {
    		if (x instanceof Number)
		    return valueFactory.valueOf(PI_HALF - Math.atan(((Number) x).doubleValue()));
    		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
    		return (Function) Functionals.compose(Operations.plus, one, square).inverse().minus();
	    } 
	    public Function integrate() {
    		return (Function) Operations.plus.apply(Operations.times.apply(id, this), Functionals.compose(log, Functionals.compose(Operations.plus, square, one)).divide(TWO));
	    }
	    public Real norm() {
    		return Values.PI;
	    }
	    public String toString() {
    		return "arccot";
	    } 
	};
    
    // elementary transcendental functions
    // hyperbolic functions
    
    /**
     * sinh: <b>C</b>&rarr;<b>C</b>; x &#8614; sinh x = (<b>e</b><sup>x</sup>-<b>e</b><sup>-x</sup>) / 2 = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> x<sup>2n+1</sup> / (2n+1)!. Hyperbolic sine function.
     * <p>derive: sinh' = cosh<br />
     * integrate: &int;sinh x <i>d</i>x = cosh x</p>
     * <p>
     * functional equations:
     * sinh(x+y) = sinh x * cosh y + cosh x * sinh y,
     * cosh(x+y) = cosh x * cosh y + sinh x * sinh y.<br />
     * cosh(x)<sup>2</sup> - sinh(x)<sup>2</sup> = 1<br />
     * (cosh x+ sinh x)<sup>n</sup> = cosh(n*x) + sinh(n*x)</p>
     * @return (<b>e</b><sup>x</sup>-<b>e</b><sup>-x</sup>) / 2.
     * @see #arsinh
     */
    public static final Function sinh = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ z) {
		if (z instanceof Arithmetic) {
		    Arithmetic ez = (Arithmetic) exp.apply(z);
		    return ez.subtract(ez.inverse()).divide(TWO);
		} 
		throw new UnsupportedOperationException("not implemented for type: " + z.getClass());
	    } 
	    public Function derive() {
		return cosh;
	    } 
	    public Function integrate() {
		return cosh;
	    }
	    public String toString() {
		return "sinh";
	    } 
	};

    /**
     * cosh: <b>C</b>&rarr;<b>C</b>; x &#8614; cosh x = (<b>e</b><sup>x</sup>+<b>e</b><sup>-x</sup>) / 2 = &sum;<span class="doubleIndex"><sub>n=0</sub><sup>&infin;</sup></span> x<sup>2n</sup> / (2n)!. Hyperbolic cosine function.
     * <p>derive: cosh' = sinh<br />
     * integrate: &int;cosh x <i>d</i>x = sinh x</p>
     * @return (<b>e</b><sup>x</sup>+<b>e</b><sup>-x</sup>) / 2.
     * @see #arcosh
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ cosh = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ z) {
		if (z instanceof Arithmetic) {
		    Arithmetic ez = (Arithmetic) exp.apply(z);
		    return ez.add(ez.inverse()).divide(TWO);
		} 
		throw new UnsupportedOperationException("not implemented for type: " + z.getClass());
	    } 
	    public Function derive() {
		return sinh;
	    } 
	    public Function integrate() {
		return sinh;
	    } 
	    public String toString() {
		return "cosh";
	    } 
	};

    /**
     * tanh: <b>C</b>\(<span class="@todo">&pi;<b>i</b>/2*<b>Z</b>)&rarr;<b>C</b>; x &#8614; tanh x = sinh x / cosh x. Hyperbolic tangent function.
     * <p>derive: tanh' = sech<sup>2</sup> = 1/cosh(x)<sup>2</sup> = (1 + tanh x) * (1 - tanh x) = 1 - (tanh x)<sup>2</sup><br />
     * integrate: &int;tanh x <i>d</i>x = &#13266;(cosh x)</p>
     * <p>
     * The hyperbolic tangent is a sigmoid function.</p>
     * @return sinh x / cosh x.
     * @see #coth
     */
    public static final Function tanh = new SynonymFunction(Functionals.compose(Operations.divide, sinh, cosh)) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		// optimized as {int ex = e^x, e_x = e^-x = 1/ex; return (ex - e_x) / (ex + e_x);}
		Arithmetic ex = (Arithmetic) exp.apply(x);
		Arithmetic e_x = ex.inverse();
		return ex.subtract(e_x).divide(ex.add(e_x));
	    }
	    public Function derive() {
		return Functionals.compose(pow(TWO), sech);
	    } 
	    public Function integrate() {
		return Functionals.compose(log, cosh);
	    }
	    public String toString() {
		return "tanh(x)";
	    } 
	};

    /**
     * csch: R\{0}&rarr;R; x &#8614; csch x = 1 / sinh(x). Hyperbolic cosecant function.
     * <p>derive: csch' = -coth*csch.</p>
     * @todo integrate
     * @todo csch(0.05513825042760334+i*0.00009587384629695) is completely wrong. Numeric precision problems?
     */
    public static final Function csch = new SynonymFunction(Functionals.compose(Operations.inverse, sinh)) {
	    public Function derive() {
		return (Function) Operations.times.apply(Operations.minus.apply(coth), csch);
	    } 
	    public String toString() {
		return "csch";
	    } 
	};

    /**
     * sech: R&rarr;R; x &#8614; sech x = 1/cosh(x). Hyperbolic secant function.
     * <p>derive: sech' = -sech*tanh.</p>
     * @todo sech(x)[0.00016381146544142+i*0.4122743420758007] numerical precision problems?
     */
    public static final Function sech = new SynonymFunction(Functionals.compose(Operations.inverse, cosh)) {
	    public Function derive() {
		return (Function) Operations.times.apply(Operations.minus.apply(sech), tanh);
	    } 
	    public String toString() {
		return "sech";
	    } 
	};

    /**
     * coth: R\{0}&rarr;R; x &#8614; coth x = cosh x / sinh x = 1 / tanh x. Hyperbolic cotangent function.
     * <p>derive: coth' = -csch<sup>2</sup>.</p>
     * <p>integrate: &int;coth x <i>d</i>x = &#13266;(sinh x)</p>
     * <p>
     * |coth 0| = &infin; is a singularity.</p>
     * @see #tanh
     */
    public static final Function coth = new SynonymFunction(Functionals.compose(Operations.divide, cosh, sinh)) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		return ((Arithmetic) tanh.apply(x)).inverse();
	    }
	    public Function derive() {
		return (Function) Operations.minus.apply(pow(TWO).apply(csch));
	    } 
	    public Function integrate() {
		return Functionals.compose(log, sinh);
	    }
	    public String toString() {
		return "coth";
	    } 
	};

    // inverse hyperbolic functions

    /**
     * arsinh: R&rarr;R; x &#8614; arsinh x = sinh<sup>-1</sup> x = &#13266;(x + &radic;<span style="text-decoration: overline">x<sup>2</sup>+1</span>). Area hyperbolic sine function.
     * <p>derive: arsinh' = 1 / &radic;<span style="text-decoration: overline">x<sup>2</sup>+1</span>)<br />
     * integrate: &int;arsinh x <i>d</i>x = x * arsinh x - &radic;<span style="text-decoration: overline">x<sup>2</sup>+1</span>)</p>
     * @see #sinh
     */
    public static final Function arsinh = new SynonymFunction(Functionals.compose(log, (Function) Operations.plus.apply(id, Functionals.compose(sqrt, Functionals.compose(Operations.plus, square, one))))) {
	    /*public Object apply(Object x) {
	      if (x instanceof Arithmetic) {
	      Arithmetic v = (Arithmetic) x;
	      return log.apply(v.add((Arithmetic) sqrt.apply(v.multiply(v).add(Values.ONE))));
	      }
	      throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	      }*/
	    public Function derive() {
		return (Function) Functionals.compose(sqrt, Functionals.compose(Operations.plus, square, one)).inverse();
	    } 
	    public Function integrate() {
		return (Function) Operations.subtract.apply(Operations.times.apply(id, this), Functionals.compose(sqrt, (Function) Functionals.compose(Operations.plus, square, one)));
	    }
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "arsinh";
	    } 
	};

    /**
     * arcosh: [1,&infin;)&rarr;[0,&infin;); x &#8614; arcosh x = (cosh|<sub>[0,&infin;)</sub>)<sup>-1</sup> x = &#13266;(x &plusmn; &radic;<span style="text-decoration: overline">x<sup>2</sup>-1</span>). Area hyperbolic cosine function.
     * <p>derive: arcosh' = 1 / &radic;<span style="text-decoration: overline">x<sup>2</sup>-1</span>)<br />
     * integrate: &int;arcosh x <i>d</i>x = x*arcosh x - &radic;<span style="text-decoration: overline">x<sup>2</sup>-1</span>)</p>
     * @see #sinh
     */
    public static final Function arcosh = new SynonymFunction(Functionals.compose(log, (Function) Operations.plus.apply(id, Functionals.compose(sqrt, Functionals.compose(Operations.subtract, square, one))))) {
	    public Function derive() {
		return (Function) Functionals.compose(sqrt, Functionals.compose(Operations.subtract, square, one)).inverse();
	    } 
	    public Function integrate() {
		return (Function) Operations.subtract.apply(Operations.times.apply(id, this), Functionals.compose(sqrt, (Function) Functionals.compose(Operations.subtract, square, one)));
	    }
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "arcosh";
	    } 
	};

    /**
     * artanh: (-1,1)&rarr;R; x &#8614; artanh x = tanh<sup>-1</sup> x = <span class="Formula">&#13266;((1+x) / (1-x)) / 2</span>. Area hyperbolic tangent function.
     * <p>derive: artanh' = 1 / (1 - x<sup>2</sup>)<br />
     * integrate: &int;artanh x <i>d</i>x = x*artanh x + &#13266;(x<sup>2</sup>-1) / 2</p>
     * @see #tanh
     */
    public static final Function artanh = new SynonymFunction((Function) Operations.divide.apply(Functionals.compose(log, (Function) Operations.divide.apply(Functionals.bindFirst(Operations.plus, Values.ONE), Functionals.bindFirst(Operations.subtract, Values.ONE))), TWO)) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (x instanceof Arithmetic) {
		    Arithmetic v = (Arithmetic) x;
		    //@todo choose either this, or SynonymFunction
		    // for |x|<=1 or |x|<1
		    return ((Arithmetic) log.apply(Values.ONE.add(v).divide(Values.ONE.subtract(v)))).divide(TWO);
		}
		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
		return (Function) Functionals.compose(Operations.subtract, one, square).inverse();
	    } 
	    public Function integrate() {
		return (Function) Operations.plus.apply(Operations.times.apply(id, this), Functionals.compose(log, Functionals.compose(Operations.subtract, square, one)).divide(TWO));
	    }
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "artanh";
	    } 
	};

    /**
     * arcoth: R\[-1,1]&rarr;R\{0}; x &#8614; arcoth x = coth<sup>-1</sup> x = <span class="Formula">&#13266;((x+1) / (x-1)) / 2</span>. Area hyperbolic cotangent function.
     * <p>derive: arcoth' = 1 / (1 - x<sup>2</sup>)<br />
     * integrate: &int;arcoth x <i>d</i>x = x*arcoth x + &#13266;(x<sup>2</sup>-1) / 2</p>
     * @see #coth
     */
    public static final Function arcoth = new SynonymFunction(new SynonymFunction((Function) Operations.divide.apply(Functionals.compose(log, (Function) Operations.divide.apply(Functionals.bindSecond(Operations.plus, Values.ONE), Functionals.bindSecond(Operations.subtract, Values.ONE))), TWO))) {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		if (x instanceof Arithmetic) {
		    Arithmetic v = (Arithmetic) x;
		    //@todo choose either this, or SynonymFunction
		    // for |x|>1
		    return ((Arithmetic) log.apply(v.add(Values.ONE).divide(v.subtract(Values.ONE)))).divide(TWO);
		}
		throw new UnsupportedOperationException("not implemented for type: " + x.getClass());
	    } 
	    public Function derive() {
		return (Function) Functionals.compose(Operations.subtract, one, square).inverse();
	    } 
	    public Function integrate() {
		return (Function) Operations.plus.apply(Operations.times.apply(id, this), Functionals.compose(log, Functionals.compose(Operations.subtract, square, one)).divide(TWO));
	    }
	    public Real norm() {
		return Values.POSITIVE_INFINITY;
	    }
	    public String toString() {
		return "arcoth";
	    } 
	};


    // amusing functions

    /**
     * norm: A&rarr;[0,&infin;); x &#8614; ||x||.
     * <p>
     * derive: norm'|<sub><b>R</b><sup>n</sup>\{0}</sub> = x / ||x||.
     * which is true for the euclidian 2-norm ||x||<sub>2</sub> =&radic;<span style="text-decoration: overline">x&middot;x</span>, only.</p>
     * <p>
     * derive: abs'|<sub>(-&infin;,0)</sub> = -1, abs'|<sub>(0,&infin;)</sub> = 1.</p>
     * @see orbital.math.Vector#multiply(orbital.math.Vector)
     * @todo <Normed,Real> would suffice
     */
    public static final Function norm = new AbstractFunction/*<Arithmetic,Real>*/() {
	    public Object/*>Real<*/ apply(Object/*>Arithmetic<*/ x) {
		return ((Normed) x).norm();
	    } 
	    public Function derive() {
		return (Function) Operations.divide.apply(id, norm);
	    } 
	    public Function integrate() {
		throw new UnsupportedOperationException("integrate " + this);
	    } 
	    public Real norm() {
		throw new UnsupportedOperationException("||" + this + "||");
	    } 
	    public String toString() {
		return "||x||";
	    } 
	};

    /**
     * projection &pi;<sub>c</sub>: A<sup>n</sup>&rarr;A; (x<sub>1</sub>,...x<sub>n</sub>)<sup>T</sup> &#8614; x<sub>c</sub>.
     */
    public final static Function projection(final int component) {
	return new AbstractFunction/*<Vector<Arithmetic>,Arithmetic>*/() {
		public Object/*>Arithmetic<*/ apply(Object/*>Vector<Arithmetic><*/ x) {
		    return ((Vector) x).get(component);
		} 
		public Function derive() {
		    throw new UnsupportedOperationException(this + "'");
		} 
		public Function integrate() {
		    throw new UnsupportedOperationException("integrate " + this);
		} 
		public String toString() {
		    return "pi" + component;
		} 
	    };
    } 
    /**
     * projection &pi;<sub>i,j</sub>: A<sup>n&times;m</sup>&rarr;A; (x<sub>i,j</sub>) &#8614; x<sub>i,j</sub>.
     */
    public final static Function projection(final int i, final int j) {
	return new AbstractFunction/*<Matrix<Arithmetic>,Arithmetic>*/() {
		public Object/*>Arithmetic<*/ apply(Object/*>Matrix<Arithmetic><*/ x) {
		    return ((Matrix) x).get(i, j);
		} 
		public Function derive() {
		    throw new UnsupportedOperationException(this + "'");
		} 
		public Function integrate() {
		    throw new UnsupportedOperationException("integrate " + this);
		} 
		public String toString() {
		    return "pi" + i + "_" + j;
		} 
	    };
    } 

    /**
     * Represents a nondeterministic function.
     * <p>
     * This nondeterministic function returns results randomly. It is provided for theoretical
     * reasons and cannot be used as a random generator.</p>
     */
    public static final Function nondet = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
		return Math.random() >= 0.5 ? x : ((Arithmetic) x).minus();
	    } 
	    public Function derive() {
		return nondet;
	    } 
	    public Function integrate() {
		return nondet;
	    } 
	    public Real norm() {
		return Values.NaN;
	    }
	    public String toString() {
		return "nondet";
	    } 
	};

    // extended functions

    /**
     * Bernstein-Polynom i of degree n for the range [a,b].
     */

    /*
     * public static final UnivariatePolynomial bernstein(final int i, final int n, final double a, final double b) {
     * return new Function() {
     * public Object apply(Object x) { return MathUtilities.nCr(n,i) * Math.pow(x-a, i) * Math.pow(b-x, n-i) / Math.pow(b-a, n); }
     * public Function derive() {throw new UnsupportedOperationException(this + "'");}
     * public String toString() {return "B"+i+","+n+"(.;"+a+","+b+")";}
     * };
     * }
     */

    /**
     * polynom: R'&rarr;R'; X&#8614;<big>&sum;</big><span class="doubleIndex"><sub>i=0</sub><sup>d</sup></span> X<sup>i</sup>.
     * <p>
     * This method will return the polynom in R[X]<sub>d</sub> of the given degree that corresponds to
     * the Vandermonde Matrix so all coefficients are 1. dim R[X]<sub>d</sub> = d+1.
     * The ring R' is "compatible" with R.</p>
     * @see #polynom(Vector)
     * @see orbital.math.UnivariatePolynomial
     */
    public static final UnivariatePolynomial polynom(final int degree) {
	return polynom(valueFactory.IDENTITY(degree, degree).getDiagonal());
	// 	    Function p[] = new Function[degree];
	// 	    for (int i = 0; i < degree; i++)
	// 		p[i] = Functions.pow(valueFactory.valueOf(i));
	// 	    return new ComponentCompositeFunction(p) {
	// 		    public Real norm() {
	// 			return degree == 0 ? Values.ZERO : Values.POSITIVE_INFINITY;
	// 		    }
	// 		};
    } 

    /**
     * polynom: R'&rarr;R'; X&#8614;<big>&sum;</big><span class="doubleIndex"><sub>i=0</sub><sup>d</sup></span> a<sub>i</sub>X<sup>i</sup>.
     * <p>
     * This method will return the polynom in R[X] with the specified coefficients vector in R<sup>d</sup>.
     * Although dim R[X] = &infin; this method will return a polynomial
     * in R[X]<sub>d</sub> of degree &le;d:=coeff.dimension().
     * The ring R' is "compatible" with R.</p>
     * @see #polynom(int)
     * @see orbital.math.Values#asPolynomial(Vector)
     * @see orbital.math.UnivariatePolynomial
     * @internal see #polynom(int)
     */
    public static final UnivariatePolynomial polynom(Vector coeff) {
	return valueFactory.asPolynomial(coeff);
	// @xxx ClassCastException due to ArithmeticVector
	// return (Function) Operations.times.apply(coeff, polynom(coeff.dimension()));
    } 
	
    /**
     * logistic: A&rarr;(0,1); x &#8614; <span class="Formula">1 / (1 + <b>e</b><sup>-x</sup>)</span>.
     * <p>derive: logistic' = <span class="Formula"><b>e</b><sup>-x</sup> / (1 + <b>e</b><sup>-x</sup>)<sup>2</sup></span> = logistic(x) * (1 - logistic(x)).<br />
     * integrate: &int;logistic(x)<i>d</i>x = </p>
     * <p>
     * The logistic function is a sigmoid function, and resembles the continuous logistic distribution.</p>
     * @see #sign
     */
    public static final Function logistic = new SynonymFunction((Function) ((Function) Operations.plus.apply(valueFactory.valueOf(1), Functionals.compose(exp, Operations.minus))).inverse()) {
	    public Function derive() {
		// a little bit optimized version of usual derivative, avoiding some 1* and 0* factors.
		// e^-x
		Function emx = Functionals.compose(exp, Operations.minus);
		return (Function) emx.divide(Functionals.compose(square, (Function) Operations.plus.apply(valueFactory.valueOf(1), emx)));
		//@todo return logistic * (1 - logistic)
	    } 
	    public Real norm() {
		return Values.ONE;
	    }
	    public String toString() {
		return "logistic";
	    } 
	};

    /**
     * sign: A&rarr;{-1,0,1}; x &#8614; -1 if x&lt;0, x &#8614; 0 if x=0, x &#8614; 1 if x&gt;0.
     * <p>derive: sign' = diracDelta = 0 on <b>R</b>\{0}.</p>
     * @see #step(Real)
     * @see #logistic
     */
    public static final Function sign = new AbstractFunction/*<Real,Real>*/() {
	    public Object/*>Real<*/ apply(Object/*>Real<*/ x) {
		if (Real.hasType.apply(x)) {
		    Real z = (Real) x;
		    Real abs = z.norm();
		    return abs.equals(Values.ZERO) ? Values.ZERO : z.divide(abs);
		}
		int cmp = Values.ZERO.compareTo(x);
		return valueFactory.valueOf(cmp < 0 ? -1 : cmp > 0 ? 1 : 0);
	    } 
	    public Function derive() {
		return diracDelta;
	    } 
	    public Function integrate() {
		throw new UnsupportedOperationException("integrate " + this);
	    } 
	    public Real norm() {
		return Values.ONE;
	    }
	    public String toString() {
		return "sign";
	    } 
	};

    /**
     * step h<sub>t</sub>: A&rarr;{0,1}; x &#8614; 1 if x&ge;t, x &#8614; 0 if x&lt;t.
     * <p>derive: step<sub>t</sub>' = diracDelta(t-x).<br />
     * integrate: &int;step<sub>t</sub>(x)<i>d</i>x = step<sub>t</sub>(x) * (x - t)</p>
     * Step function alias Heaviside function.
     * @see #sign
     * @see #diracDelta
     * @todo comparable arithmetic objects <C implements Arithmetic & Comparable> instead of Real would be enough
     */
    public static final Function step(final Real t) {
	return new AbstractFunction/*<Real,Integer>*/() {
		public Object/*>Integer<*/ apply(Object/*>Real<*/ x) {
		    return valueFactory.valueOf(t.compareTo(x) < 0 ? 0 : 1);
		} 
		public Function derive() {
		    return Functionals.compose(diracDelta, Functionals.bindFirst(Operations.subtract, t));
		} 
		public Function integrate() {
		    return (Function) Operations.times.apply(step(t), Functionals.bindSecond(Operations.subtract, t));
		} 
		public Real norm() {
		    return Values.ONE;
		}
		public String toString() {
		    return "step_" + t;
		} 
	    };
    }
	
    /**
     * diracDelta &delta;: M\{0}&rarr;{0}; x &#8614; 0 if x&ne;0.
     * <p>derive: &delta;' = &delta; ??.<br />
     * integrate: &int;&delta;(x)<i>d</i>x = step<sub>0</sub>(x)</p>
     * @see #delta
     * @see #step(Real)
     * @see #piecewise(Predicate[], Function[])
     */
    public static final Function diracDelta = new AbstractFunction/*<Arithmetic,Integer>*/() {
	    public Object/*>Integer<*/ apply(Object/*>Arithmetic<*/ x) {
		if (((Normed) x).norm().equals(Values.ZERO))
		    throw new IllegalArgumentException(x + "");
		else
		    return Values.ZERO;
	    } 
	    public Function derive() {
		return diracDelta;
	    } 
	    public Function integrate() {
		return step(Values.ZERO);
	    } 
	    public Real norm() {
		return Values.NaN;
	    }
	    public String toString() {
		return "diracDelta";
	    } 
	};

    /**
     * Get a function defined piecewise.
     * piecewise: A&rarr;B; x&#8614;f<sub>min {i &brvbar; c<sub>i</sub>(x) &and; 1&le;i&le;m}</sub>(x).
     * <p>
     * deriving this function requires it to be in C<sup>1</sup>(A, B).
     * Similarly, integrating requires it to be integrable, at all.
     * <em>Unless</em> you make sure that these requirements are met, the implementation
     * will return values that are completely meaningless.</p>
     * <p>
     * Note that piecewise functions can be defined in terms of appropriate compositions with
     * heaviside functions which in turn can be defined as translations of the unit step function.</p>
     * @param cond the condition predicates c<sub>i</sub>.
     * @param value the functions of whom the first one is applied whose associated predicate
     *  yields true.
     * @pre cond.length == value.length
     * @throws IllegalArgumentException if no condition predicate matches for an argument x.
     * @see #step
     */
    public static final /*<A implements Arithmetic, B implements Arithmetic>*/ Function/*<A,B>*/ piecewise(final Predicate/*<A>*/ cond[], final Function/*<A,B>*/ value[]) {
	Utility.pre(cond.length == value.length, "same number of conditions and values");
	return new AbstractFunction/*<A,B>*/() {
		private int getIndexOfFirstTrue(Object/*>A<*/ x) {
		    // search the first predicate j that is true of x
		    for (int j = 0; j < cond.length; j++)
			if (cond[j].apply(x))
			    return j;
		    throw new IllegalArgumentException("piecewise function undefined at " + x + " since no predicate is true");
    		}
    		public Object/*>B<*/ apply(Object/*>A<*/ x) {
		    return value[getIndexOfFirstTrue(x)].apply(x);
    		} 
    		public Function derive() throws ArithmeticException {
		    Function[] di = new Function[value.length];
		    // piece-wise, if continuously differentiable at all
		    for (int i = 0; i < di.length; i++)
			di[i] = value[i].derive();
		    return piecewise(cond, di);
    		} 
    		public Function integrate() throws ArithmeticException {
		    Function[] di = new Function[value.length];
		    // piece-wise, if integrable at all
		    for (int i = 0; i < di.length; i++)
			di[i] = value[i].integrate();
		    return piecewise(cond, di);
    		} 
		public Real norm() {
		    return (Real) Functionals.foldRight(Operations.max, value[0].norm(), (Object[]) Functionals.map(norm, value));
		}
		public String toString() {
		    StringBuffer sb = new StringBuffer();
		    for (int i = 0; i < cond.length; i++)
			sb.append((i > 0 ? ", " : "") + cond[i] + " => " + value[i]);
		    return "piecewise [" + sb.toString() + "]";
		} 
	    };
    }

    /**
     * A Function that performs an operation pointwise.
     * 
     * @version 1.0, 2000/08/10
     * @author  Andr&eacute; Platzer
     * @see Functionals#pointwise(Function)
     */
    static class PointwiseFunction extends AbstractFunctor implements Function {
	private final Function elemental;

	/**
	 * Constructs a new pointwise function from an elemental function.
	 * @param elemental the elemental function to perform on Arithmetic x.
	 */
	public PointwiseFunction(Function elemental) {
	    this.elemental = elemental;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof PointwiseFunction))
		return false;
	    return elemental.equals(((PointwiseFunction) o).elemental);
	} 

	public int hashCode() {
	    return elemental.hashCode();
	} 

	/**
	 * Performs this operation pointwise on x and y.
	 * For Arithmetic objects this will be the elemental function applied on x
	 * as <code>elemental(x)</code>.
	 * For Function objects etc. this will be a composition of this pointwise operation
	 * with x.
	 * @param x argument to this pointwise operation which must be Arithmetic or a Function.
	 * @see #elemental
	 * @see Functionals#compose(Function, Function)
	 * @todo test
	 */
	public Object apply(Object x) {
	    if (x instanceof Arithmetic && !(x instanceof MathFunctor))
		return elemental.apply(x);
	    return Functionals.genericCompose(this, x);
	} 

	/**
	 * <i>d</i>/<i>d</i>x f = <i>d</i>/<i>d</i>x elemental.
	 */
	public Function derive() {
	    return elemental.derive();
	} 

	/**
	 * &int; f <i>d</i>x = &int; elemental <i>d</i>x.
	 */
	public Function integrate() {
	    return elemental.integrate();
	} 

	public String toString() {
	    return elemental.toString();
	} 
    }



    // binary functions
	
    static final BinaryFunction binaryzero = binaryConstant(valueFactory.valueOf(0));
    static final BinaryFunction binaryone = binaryConstant(valueFactory.valueOf(1));

    /**
     * constant &acirc;: R&times;R&rarr;R; (x,y) &#8614; a.
     * <p>derive: &acirc;' = 0.<br>
     * integrate: &int;a<i>d</i>x<sub>i</sub> = a*x<sub>i</sub></p>
     * @todo couldn't we join constant VoidFunction, Function, BinaryFunction objects like in orbital.logic.functor.Functions.ConstantFunction
     */
    //TODO: some "binary" functions can be defined by onFirst or onSecond of the corresponding unary (or even void) function Functions.constant (how about overhead?)
    public/*@xxx*/ static final /*<A1 implements Arithmetic, A2 implements Arithmetic, B implements Arithmetic>*/ BinaryFunction/*<A1,A2,B>*/ binaryConstant(final Object/*>B<*/ a) {
	return new BinaryConstantFunction/*<A1,A2,B>*/(a);
    }
    static final class BinaryConstantFunction/*<A1 implements Arithmetic, A2 implements Arithmetic, B implements Arithmetic>*/ extends AbstractBinaryFunction/*<A1,A2,B>*/ implements orbital.logic.functor.VoidFunction/*<B>*/{
    	Object/*>B<*/ a;
    	public BinaryConstantFunction(Object/*>B<*/ a) {
	    this.a = a;
    	}
	public Object/*>B<*/ apply() {
	    return a;
	} 
	public Object/*>B<*/ apply(Object/*>A1<*/ x, Object/*>A2<*/ y) {
	    return apply();
	} 
	public BinaryFunction derive() {
	    //XXX: couldn't this be simply a one dimensional and unary "zero"?
	    return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
		{binaryzero, binaryzero}
	    });
	} 
	public BinaryFunction integrate(int i) {
	    return Functionals.on(i, Functions.linear((Arithmetic)a));
	} 
	public Real norm() {
	    return ((Normed) a).norm();
	}
	public boolean equals(Object o) {
	    return (o instanceof BinaryConstantFunction)
		&& Utility.equals(a, ((BinaryConstantFunction) o).a);
	}
	public int hashCode() {
	    return Utility.hashCode(a);
	}
	public String toString() {
	    return a + "";
	} 
    }

    /**
     * symbolic f:R&times;R&rarr;R; (x,y) &#8614; f(x, y).
     * <p>derive:  (f)' = f'<br>
     * integrate: &int;f(x,y)<i>d</i>x<sub>i</sub> = &int;f(x,y)<i>d</i>x<sub>i</sub></p>
     * @param name the name of the symbolic function.
     * @return a pure symbolic function with a specified name.
     */
    public/*@xxx*/ static final BinaryFunction binarySymbolic(final String name) {
	return new BinarySymbolicFunction(name);
    }
    private static final class BinarySymbolicFunction extends AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ {
    	private  String name;
    	public BinarySymbolicFunction(String name) {
	    this.name = name;
    	}
	public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
	    return Functionals.genericCompose(this, x, y);
	} 
	public BinaryFunction derive() {
	    return binarySymbolic(name + "'");
	} 
	public BinaryFunction integrate(int i) {
	    Utility.pre(0 <= i && i <= 1, "binary integral");
	    return binarySymbolic("\u222B" + name + " d" + (i == 0 ? 'x' : 'y'));
	} 
	public Real norm() {
	    return Values.NaN;
	}
	public boolean equals(Object o) {
	    return (o instanceof BinarySymbolicFunction)
		&& Utility.equals(name, ((BinarySymbolicFunction) o).name);
	}
	public int hashCode() {
	    return Utility.hashCode(name);
	}
	public String toString() {
	    return name + "";
	} 
    } 

    /**
     * Projects to the first argument, ignoring the second.
     * <p>
     * projectFirst:  (x,y) &#8614; x.</p>
     * <p>
     * Equals {@link Functionals#onFirst(Function) Functionals.onFirst}({@link Functions#id})</p>
     * <p><b><i>Evolves</i>:</b> might be renamed.</p>
     */
    public static final BinaryFunction projectFirst = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ first, Object/*>Arithmetic<*/ second) {
		return first;
	    } 
	    public BinaryFunction derive() {
		return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
		    {binaryone, binaryzero}
		});
	    } 
	    public BinaryFunction integrate(int i) {
		Utility.pre(0 <= i && i <= 1, "binary integral");
		return i == 0 ? Functionals.onFirst(Functions.id.integrate()) : binaryzero;
	    } 
	    public String toString() {
		return "#0";
	    } 

	};

    /**
     * Projects to the second argument, ignoring the first.
     * <p>
     * projectSecond:  (x,y) &#8614; y.</p>
     * <p>
     * Equals {@link Functionals#onSecond(Function) Functionals.onSecond}({@link Functions#id})</p>
     * <p><b><i>Evolves</i>:</b> might be renamed.</p>
     */
    public static BinaryFunction projectSecond = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
	    public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ first, Object/*>Arithmetic<*/ second) {
		return second;
	    } 
	    public BinaryFunction derive() {
		return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
		    {binaryzero, binaryone}
		});
	    } 
	    public BinaryFunction integrate(int i) {
		Utility.pre(0 <= i && i <= 1, "binary integral");
		return i == 0 ? binaryzero : Functionals.onSecond(Functions.id.integrate());
	    } 
	    public String toString() {
		return "#1";
	    } 
	};

    /**
     * delta: <b>R</b>&times;<b>R</b>&rarr;<b>R</b>; (x,x) &#8614; 1, (x,y) &#8614; 0 for x&ne;y.
     * Kronecker-delta function.
     * @see #diracDelta
     */
    public static final BinaryFunction delta = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Integer>*/() {
	    public Object/*>Integer<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
		return valueFactory.valueOf(x.equals(y) ? 1 : 0);
	    } 
	    public BinaryFunction derive() {
		throw new UnsupportedOperationException("delta'");
	    } 
	    public BinaryFunction integrate(int i) {
		throw new UnsupportedOperationException("integrate delta");
	    } 
	    public Real norm() {
		return Values.ONE;
	    }
	    public String toString() {
		return "delta";
	    } 
	};
    public static final int delta(int i, int j) {
	return i == j ? 1 : 0;
    } 

    /**
     * A BinaryFunction that performs an operation pointwise.
     * 
     * @version 1.0, 2000/08/10
     * @author  Andr&eacute; Platzer
     * @see Functionals#pointwise(BinaryFunction)
     */
    static class PointwiseBinaryFunction extends AbstractFunctor implements BinaryFunction {
	private final BinaryFunction elemental;

	/**
	 * Constructs a new pointwise function from an elemental function.
	 * @param elemental the elemental function to perform on Arithmetic x and y.
	 */
	public PointwiseBinaryFunction(BinaryFunction elemental) {
	    this.elemental = elemental;
	}

	public boolean equals(Object o) {
	    if (!(o instanceof PointwiseBinaryFunction))
		return false;
	    return elemental.equals(((PointwiseBinaryFunction) o).elemental);
	} 

	public int hashCode() {
	    return elemental.hashCode();
	} 

	/**
	 * Performs this operation pointwise on x and y.
	 * For Arithmetic objects this will be the elemental function applied on x and y
	 * as <code>elemental(x, y)</code>.
	 * For functor objects this will be a composition of this pointwise operation
	 * with x and y.
	 * @param x first argument to this pointwise operation which must be Arithmetic or a Functor.
	 * @param y second argument to this pointwise operation which must be Arithmetic or a Functor.
	 * @see #elemental
	 * @see Functionals#genericCompose(BinaryFunction, Object, Object)
	 * @todo test
	 */
	public Object apply(Object x, Object y) {
	    if ((x instanceof Arithmetic && !(x instanceof MathFunctor)) && (y instanceof Arithmetic && !(y instanceof MathFunctor)))
		return elemental.apply(x, y);
	    return Functionals.genericCompose(this, x, y);
	} 

	public BinaryFunction derive() {
	    return elemental.derive();
	} 

	public BinaryFunction integrate(int i) {
	    return elemental.integrate(i);
	}

	public String toString() {
	    return elemental.toString();
	} 
    }
}

/**
 * A synonym function whose methods can be partially overwritten for better performance and alike.
 * So one can overwrite some methods to implement fast base cases and let the complex synonym function
 * handle more difficult cases like non-scalars.
 */
class SynonymFunction/*<A implements Arithmetic, B implements Arithmetic>*/ extends AbstractFunctor implements Function/*<A,B>*/ {
    /**
     * the synonym function to apply
     */
    private final Function/*<A,B>*/ synonym;
    public SynonymFunction(Function/*<A,B>*/ synonym) {
	this.synonym = synonym;
    }
    public Object/*>B<*/ apply(Object/*>A<*/ x) {
	return synonym.apply(x);
    } 
    public Function derive() {
	return synonym.derive();
    } 
    public Function integrate() {
	return synonym.integrate();
    } 
    public Real norm() {
	return synonym.norm();
    }
    public String toString() {
	return synonym.toString();
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
