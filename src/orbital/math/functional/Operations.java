/**
 * @(#)Operations.java 1.0 2000/08/03 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.math.Arithmetic;

import orbital.logic.functor.BinaryPredicate;

import java.util.Iterator;
import java.util.Collection;

import java.lang.reflect.Method;

import orbital.math.Real;

import orbital.math.MathUtilities;
import java.lang.reflect.InvocationTargetException;

import orbital.math.ValueFactory;
import orbital.math.Values;
import orbital.math.Vector;
import orbital.math.Matrix;

import java.util.Arrays;
import orbital.util.Utility;

import orbital.logic.sign.concrete.Notation;
import orbital.logic.sign.concrete.Notation.NotationSpecification;

// due to JDK1.3 bug  Bug-ID: 4306909
// this class can only be compiled with JDK1.4, JDK1.3.1 or JDK 1.2.2, but not with JDK 1.3.0 or JDK 1.3.0_02
// due to JDK1.3 bug  Bug-ID: 4401422
// this class can only be javadoced with JDK1.4, but not with JDK 1.3.0 or JDK 1.3.0_02 or JDK1.3.1

/**
 * Provides central arithmetic operations for algebraic types.
 * <p>
 * Operations contains BinaryFunction abstractions of
 * mathematical operations like <code>+ - &sdot; / ^</code> etc.
 * For Arithmetic objects, the corresponding elemental function in orbital.math.Arithmetic is called,
 * for functions the operations are defined pointwise.
 * So these Operations can be applied to arithmetic objects as well as functions in the same manner!
 * All function objects in this class provide canonical equality:
 * <center>a.equals(b) if and only if a <span class="operator">==</span> b</center>
 * </p>
 * <p>
 * Operation functions are very useful to implement sly arithmetic operations with full dynamic dispatch.
 * They are then performed with the correct type concerning all argument types, automatically.
 * Simply use an idiom like
 * <pre>
 * <span class="keyword">public</span> <span class="Orbital">Arithmetic</span> add(<span class="Orbital">Arithmetic</span> b) {
 *     <span class="comment">// base case: "add" for two instances of <var>ThisClass</var></span>
 *     <span class="keyword">if</span> (b <span class="keyword">instanceof</span> <var>ThisClass</var>)
 *         <span class="keyword">return</span> <span class="keyword">new</span> <var>ThisClass</var>(value() <span class="operator">+</span> ((<var>ThisClass</var>) b).value());
 *     <span class="comment">// dynamic dispatch with regard to dynamic types of all arguments</span>
 *     <span class="comment">// for sly defer to "add" of most restrictive type</span>
 *     <span class="keyword">return</span> (<span class="Orbital">Arithmetic</span>) <span class="Orbital">Operations</span>.plus.apply(<span class="keyword">this</span>, b);
 * }
 * </pre>
 * Which implicitly uses the tranformation function {@link orbital.math.ValueFactory#getCoercer()}.
 * The static functions provided in <tt>Operations</tt> delegate type handling like in
 * <pre>
 *     <span class="Orbital">Arithmetic</span> operands[] <span class="operator">=</span> (<span class="Orbital">Arithmetic</span>[]) <span class="Orbital">Values</span>.getDefaultInstance().getCoercer().apply(<span class="keyword">new</span> <span class="Orbital">Arithmetic</span>[] {x, y});
 *     <span class="keyword">return</span> operands[<span class="Number">0</span>].add(operands[<span class="Number">1</span>]);
 * </pre>
 * </p>
 * <p>
 * In addition, Operations contains arithmetized versions of the comparison predicates
 * of {@link orbital.logic.functor.Predicates}.
 * The essential difference is that the implementations in Operations
 * respect coercing, type compatibility, and precision.
 * </p>
 * 
 * @structure depends {@link orbital.math.ValueFactory#getCoercer()}
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.math.Arithmetic
 * @see java.lang.Comparable
 * @see orbital.logic.functor.Functor
 * @see orbital.math.functional.Functionals#compose(Function, Function)
 * @see orbital.math.functional.Functionals#genericCompose(BinaryFunction, Object, Object)
 * @see orbital.logic.functor.Functionals.Catamorphism
 */
public interface Operations /* implements ArithmeticOperations */ {

    // the functions below all follow the same recursion scheme but its too reflectious to stamp into a generic delegate

    // junctors of a general group (A,+)

    /**
     * plus +: A&times;A&rarr;A; (x,y) &#8614; x+y.
     * <p>
     * derive plus' = (1, 1)<br />
     * integrate: &int;x<sub>0</sub>+x<sub>1</sub> <i>d</i>x<sub>i</sub> = x<span class="doubleIndex"><sub>i</sub><sup>2</sup></span>/2 + x<sub>0</sub>*x<sub>1</sub></p>
     * @attribute associative
     * @attribute neutral
     * @attribute inverses
     * @attribute commutative
     * @see Arithmetic#add(Arithmetic)
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ plus = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            //@xxx either add this everywhere, or remove it here (otherwise it won't work)
            //private final orbital.logic.imp.Type logicalTypeDeclaration = orbital.logic.imp.Types.map(orbital.logic.imp.Types.product(new orbital.logic.imp.Type[] {orbital.logic.imp.Types.objectType(Arithmetic.class), orbital.logic.imp.Types.objectType(Arithmetic.class)}), orbital.logic.imp.Types.objectType(Arithmetic.class));
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)x).valueFactory().getCoercer(true).apply(new Arithmetic[] {
                    (Arithmetic) x, (Arithmetic) y
                });
                return operands[0].add(operands[1]);
            } 
            public BinaryFunction derive() {
                return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
                    {Functions.binaryone, Functions.binaryone}
                });
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                return (BinaryFunction) plus.apply( times.apply(Functions.projectFirst, Functions.projectSecond), divide.apply(Functionals.on(i, Functions.square), valueFactory().valueOf(2)));
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "+";
            } 
        };

    /**
     * sum &sum;: A<sup>n</sup>&rarr;A; (x<sub>i</sub>) &#8614; &sum;<sub>i</sub> x<sub>i</sub> = <span class="bananaBracket">(|</span>0,+<span class="bananaBracket">|)</span> (x<sub>i</sub>).
     * <p>
     * derive sum' = (1)<sub>n&isin;<b>N</b></sub><br />
     * integrate: ?</p>
     * <p>
     * Treats its argument as a list like {@link orbital.logic.functor.Functionals.Catamorphism}.
     * </p>
     * @see orbital.logic.functor.Functionals.Catamorphism
     * @see orbital.math.ValueFactory#ZERO()
     * @see #plus
     * @todo implements AbstractFunction<Vector<Arithmetic>,Arithmetic>?
     * @todo implements AbstractFunction<Matrix<Arithmetic>,Arithmetic>?
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ sum = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ a) {
            	Iterator i = Utility.asIterator(a);
            	Arithmetic z;
            	if (i.hasNext()) 
            		z = ((Arithmetic)i.next()).zero();
            	else
            		z = ((Arithmetic)a).valueFactory().ZERO();
                return Functionals.foldLeft(plus, z, Utility.asIterator(a));
            }
            public Function derive() {
                throw new ArithmeticException(this + " is only partially derivable");
            } 
            public Function integrate() {
                throw new ArithmeticException(this + " is only (undefinitely) integrable with respect to a single variable");
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "\u2211";
            } 
        };

    /**
     * minus &minus;: A&rarr;A; x &#8614; &minus;x.
     * <p>
     * derive minus' = &minus;1<br />
     * integrate: &int;&minus;x <i>d</i>x = &minus;x<sup>2</sup>/2</p>
     * @see Arithmetic#minus()
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ minus = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
                return ((Arithmetic) x).minus();
            } 
            public Function derive() {
                return Functions.constant(valueFactory().MINUS_ONE());
            } 
            public Function integrate() {
                // return (Function) minus.apply(Functions.id.integrate());
                return (Function) minus.apply( Operations.divide.apply(Functions.square, valueFactory().valueOf(2)) );
            }
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "-";
            } 
        };

    /**
     * subtract -: A&times;A&rarr;A; (x,y) &#8614; x-y.
     * <p>
     * derive subtract' = (1, -1)<br />
     * integrate: &int;x<sub>0</sub>-x<sub>1</sub> <i>d</i>x<sub>0</sub> = x<span class="doubleIndex"><sub>0</sub><sup>2</sup></span>/2 - x<sub>0</sub>*x<sub>1</sub><br />
     * integrate: &int;x<sub>0</sub>-x<sub>1</sub> <i>d</i>x<sub>1</sub> = x<sub>0</sub>*x<sub>1</sub> - x<span class="doubleIndex"><sub>1</sub><sup>2</sup></span>/2</p>
     * @see Arithmetic#subtract(Arithmetic)
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ subtract = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)x).valueFactory().getCoercer().apply(new Arithmetic[] {
                    (Arithmetic) x, (Arithmetic) y
                });
                return operands[0].subtract(operands[1]);
            } 
            public BinaryFunction derive() {
                return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
                    {Functions.binaryConstant(valueFactory().ONE()), Functions.binaryConstant(valueFactory().MINUS_ONE())}
                });
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                return i == 0
                    ? (BinaryFunction) subtract.apply( divide.apply(Functionals.onFirst(Functions.square), valueFactory().valueOf(2)), times.apply(Functions.projectFirst, Functions.projectSecond))
                    : (BinaryFunction) subtract.apply( times.apply(Functions.projectFirst, Functions.projectSecond), divide.apply(Functionals.onSecond(Functions.square), valueFactory().valueOf(2)));
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "-";
            } 
        };

    // junctors of a general group (A,&sdot;)

    /**
     * times &sdot;: A&times;A&rarr;A; (x,y) &#8614; x&sdot;y.
     * <p>
     * derive times' = (y, x)<br />
     * integrate: &int;x<sub>0</sub>&sdot;x<sub>1</sub> <i>d</i>x<sub>0</sub> = x<span class="doubleIndex"><sub>0</sub><sup>2</sup></span>&sdot;x<sub>1</sub> / 2<br />
     * integrate: &int;x<sub>0</sub>&sdot;x<sub>1</sub> <i>d</i>x<sub>1</sub> = x<sub>0</sub>&sdot;x<span class="doubleIndex"><sub>1</sub><sup>2</sup></span> / 2</p>
     * @attribute associative
     * @attribute neutral
     * @attribute commutative
     * @attribute distributive #plus
     * @see Arithmetic#multiply(Arithmetic)
     * @xxx but what about scale?
     * @see Arithmetic#scale(Arithmetic)
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ times = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)x).valueFactory().getCoercer(true).apply(new Arithmetic[] {
                    (Arithmetic) x, (Arithmetic) y
                });
                return operands[0].multiply(operands[1]);
            } 
            public BinaryFunction derive() {
                return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
                    {Functions.projectSecond, Functions.projectFirst}
                });
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                return i == 0
                    ? (BinaryFunction) divide.apply( times.apply(Functionals.onFirst(Functions.square), Functions.projectSecond), valueFactory().valueOf(2))
                    : (BinaryFunction) divide.apply( times.apply(Functions.projectFirst, Functionals.onSecond(Functions.square)), valueFactory().valueOf(2));
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "*";
            } 
        };

    /**
     * product &prod;: A<sup>n</sup>&rarr;A; (x<sub>i</sub>) &#8614; &prod;<sub>i</sub> x<sub>i</sub> = <span class="bananaBracket">(|</span>1,&sdot;<span class="bananaBracket">|)</span> (x<sub>i</sub>).
     * <p>
     * derive product' = ?<br />
     * integrate: ?</p>
     * <p>
     * Treats its argument as a list like {@link orbital.logic.functor.Functionals.Catamorphism}.
     * </p>
     * @see orbital.logic.functor.Functionals.Catamorphism
     * @see orbital.math.ValueFactory#ONE()
     * @see #times
     * @todo
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ product = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ a) {
            	Iterator i = Utility.asIterator(a);
            	Arithmetic o;
            	if (i.hasNext()) 
            		o = ((Arithmetic)i.next()).one();
            	else
            		o = ((Arithmetic)a).valueFactory().ONE();
                return Functionals.foldLeft(times, o, Utility.asIterator(a));
            }
            public Function derive() {
                throw new ArithmeticException(this + " is only partially derivable");
            } 
            public Function integrate() {
                throw new ArithmeticException(this + " is only (undefinitely) integrable with respect to a single variable");
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "\u220f";
            } 
    		public ValueFactory valueFactory() {
    			return Values.getDefault();
    		}
        };

    /**
     * inverse <sup>-1</sup>: A&rarr;A; x &#8614; x<sup>-1</sup>.
     * <p>
     * derive inverse' = -1/x<sup>2</sup><br />
     * integrate: &int;x<sup>-1</sup> <i>d</i>x<sub>1</sub> = &#13266; x</p>
     * @see Arithmetic#inverse()
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ inverse = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x) {
                return ((Arithmetic) x).inverse();
            } 
            public Function derive() {
                return Functionals.compose(minus, Functions.pow(valueFactory().valueOf(-2)));
            } 
            public Function integrate() {
                return Functions.log;
            }
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "^-1";
            } 
        };

    /**
     * divide &#8725;: A&times;A&rarr;A; (x,y) &#8614; x&#8725;y.
     * <p>
     * derive divide' = (1&#8725;y, -x&#8725;y<sup>2</sup>)<br />
     * integrate: &int;x<sub>0</sub>&#8725;x<sub>1</sub> <i>d</i>x<sub>0</sub> = x<span class="doubleIndex"><sub>0</sub><sup>2</sup></span>&#8725;(2*x<sub>1</sub>)<br />
     * integrate: &int;x<sub>0</sub>&#8725;x<sub>1</sub> <i>d</i>x<sub>1</sub> = x<sub>0</sub>&middot;&#13266;(x<sub>1</sub>)</p>
     * @see Arithmetic#divide(Arithmetic)
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ divide = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)x).valueFactory().getCoercer().apply(new Arithmetic[] {
                    (Arithmetic) x, (Arithmetic) y
                });
                return operands[0].divide(operands[1]);
            } 
            public BinaryFunction derive() {
                return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
                    {Functionals.onSecond(Functions.reciprocal),
                     Functionals.compose(divide, Functionals.onFirst(minus), Functionals.onSecond(Functions.square))}
                });
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                return i == 0
                    ? (BinaryFunction) divide.apply(divide.apply(Functionals.onFirst(Functions.square), Functions.projectSecond), valueFactory().valueOf(2))
                    : (BinaryFunction) times.apply(Functionals.onSecond(Functions.log), Functions.projectFirst);
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "/";
            } 
        };

    // extended junctors

    /**
     * power ^: A&times;A&rarr;A; (x,y) &#8614; x<sup>y</sup>.
     * <p>
     * power' = (y&sdot;x<sup>y-1</sup>, &#13266;(x)&sdot;x<sup>y</sup>)<br />
     * integrate: &int;x<sub>0</sub><sup>x<sub>1</sub></sup> <i>d</i>x<sub>0</sub> = x<sub>0</sub><sup>x<sub>1</sub>+1</sup> / (x<sub>1</sub>+1)<br />
     * integrate: &int;x<sub>0</sub><sup>x<sub>1</sub></sup> <i>d</i>x<sub>1</sub> = x<sub>0</sub><sup>x<sub>1</sub></sup> / &#13266;(x<sub>0</sub>)</p>
     * @see Arithmetic#power(Arithmetic)
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ power = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)x).valueFactory().getCoercer().apply(new Arithmetic[] {
                    (Arithmetic) x, (Arithmetic) y
                });
                return operands[0].power(operands[1]);
            } 
            public BinaryFunction derive() {
                return (BinaryFunction) Functionals.genericCompose(new BinaryFunction[][] {
                    {Functionals.compose(times, Functions.projectSecond, Functionals.compose(power, Functions.projectFirst, Functionals.compose(subtract, Functions.projectSecond, Functions.binaryone))), Functionals.compose(times, Functionals.onFirst(Functions.log), power)}
                });
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                return i == 0
                    ? (BinaryFunction) divide.apply(power.apply(Functions.projectFirst, plus.apply(Functions.projectSecond, valueFactory().ONE())), plus.apply(Functions.projectSecond, valueFactory().ONE()))
                    : (BinaryFunction) divide.apply(power, Functionals.onFirst(Functions.log));
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "^";
            } 
        };

    // order operations

    /**
     * min: A&times;A&rarr;A; (x,y) &#8614; min {x,y} = &#8851;{x,y}.
     * @attribute associative
     * @attribute commutative
     * @attribute idempotent
     * @attribute distributive #max
     * @internal computable in a simple way since Comparable is for total orders.
     * @todo can we get deribale by presuming min{x,y} = 1/2*(x+y-||x-y||) ?
     * @see java.lang.Comparable#compareTo(Object)
     * @todo AbstractBinaryFunction<Comparable,Comparable,Comparable> would be enough
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ min = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                if (x instanceof Comparable && y instanceof Comparable)
                    return ((Comparable) x).compareTo(y) <= 0 ? x : y;
                return Functionals.genericCompose(min, x, y);
            } 
            public BinaryFunction derive() {
                throw new UnsupportedOperationException(this + "'");
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                throw new UnsupportedOperationException("integrate " + this);
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "min";
            } 
        };

    /**
     * inf &#8851;: A<sup>n</sup>&rarr;A; (x<sub>i</sub>) &#8614; &#8851;<sub>i</sub> {x<sub>i</sub>} = <span class="bananaBracket">(|</span>&infin;,min<span class="bananaBracket">|)</span> (x<sub>i</sub>).
     * <p>
     * <dl class="def">
     *   Let (A,&le;) be an ordered set.
     *   <dt>lower bound</dt>
     *   <dd>b&isin;A is a lower bound of M&sube;A :&hArr; &forall;x&isin;M b&le;x</dd>
     *   <dt>infimum</dt>
     *   <dd>inf M = s&isin;A is the infimum of M&sube;A :&hArr; s is a lower bound of M &and; &forall;b&isin;A (b lower bound of M &rArr; b&le;s)</dd>
     * </dl>
     * </p>
     * <p>
     * Treats its argument as a list like {@link orbital.logic.functor.Functionals.Catamorphism}.
     * </p>
     * @see orbital.logic.functor.Functionals.Catamorphism
     * @see #min
     * @todo
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ inf = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ a) {
                return Functionals.foldLeft(min, valueFactory().POSITIVE_INFINITY(), Utility.asIterator(a));
            }
            public Function derive() {
                throw new UnsupportedOperationException(this + "'");
            } 
            public Function integrate() {
                throw new UnsupportedOperationException("integrate " + this);
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "\u2293";
            } 
        };

    /**
     * max: A&times;A&rarr;A; (x,y) &#8614; max {x,y} = &#8852;{x,y}.
     * @attribute associative
     * @attribute commutative
     * @attribute idempotent
     * @attribute distributive #min
     * @see java.lang.Comparable#compareTo(Object)
     */
    public static final BinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/ max = new AbstractBinaryFunction/*<Arithmetic,Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
                if (x instanceof Comparable && y instanceof Comparable)
                    return ((Comparable) x).compareTo(y) >= 0 ? x : y;
                return Functionals.genericCompose(max, x, y);
            } 
            public BinaryFunction derive() {
                throw new UnsupportedOperationException(this + "'");
            } 
            public BinaryFunction integrate(int i) {
                Utility.pre(0 <= i && i <= 1, "binary integral");
                throw new UnsupportedOperationException("integrate " + this);
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "max";
            } 
        };

    /**
     * sup &#8852;: A<sup>n</sup>&rarr;A; (x<sub>i</sub>) &#8614; &#8852;<sub>i</sub> {x<sub>i</sub>} = <span class="bananaBracket">(|</span>-&infin;,max<span class="bananaBracket">|)</span> (x<sub>i</sub>).
     * <p>
     * <dl class="def">
     *   Let (A,&le;) be an ordered set.
     *   <dt>upper bound</dt>
     *   <dd>b&isin;A is an upper bound of M&sube;A :&hArr; &forall;x&isin;M x&le;b</dd>
     *   <dt>supremum</dt>
     *   <dd>sup M = s&isin;A is the supremum of M&sube;A :&hArr; s is an upper bound of M &and; &forall;b&isin;A (b upper bound of M &rArr; s&le;b)</dd>
     * </dl>
     * </p>
     * <p>
     * Treats its argument as a list like {@link orbital.logic.functor.Functionals.Catamorphism}.
     * </p>
     * @see orbital.logic.functor.Functionals.Catamorphism
     * @see #max
     * @todo
     */
    public static final Function/*<Arithmetic,Arithmetic>*/ sup = new AbstractFunction/*<Arithmetic,Arithmetic>*/() {
            public Object/*>Arithmetic<*/ apply(Object/*>Arithmetic<*/ a) {
                return Functionals.foldLeft(max, valueFactory().NEGATIVE_INFINITY(), Utility.asIterator(a));
            }
            public Function derive() {
                throw new UnsupportedOperationException(this + "'");
            } 
            public Function integrate() {
                throw new UnsupportedOperationException("integrate " + this);
            } 
            public Real norm() {
                return valueFactory().POSITIVE_INFINITY();
            }
            public String toString() {
                return "\u2294";
            } 
        };

    
    // binary predicates
        
    /**
     * =.
     * In first-order logic, equality "=" is uniquely determined by
     * <ul>
     *   <li>reflexive, i.e. &forall;x (x=x)</li>
     *   <li>substitutive, &forall;&phi;&isin;Formula(&Sigma;) a=b,&phi; &#8872; &phi;[a&rarr;b]</li>
     * </ul>
     * @attribute equivalent
     * @attribute congruent for all f, P
     * @attribute substitutive
     */
    public static final BinaryPredicate/*<Object,Object>*/ equal = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                if (a == b)
                    return true;
                if (b == null)
                    //@xxx is this okay?
                    return false;
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)a).valueFactory().getCoercer(true).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return operands[0].equals(operands[1]);
            }
            public String toString() { return "="; }
        };

    /**
     * &ne;.
     * <p>
     * Inequality is defined as x&ne;y :&hArr; &not;(x=y).
     * </p>
     * @attribute irreflexive
     * @attribute symmetric
     * @see #equal
     */
    public static final BinaryPredicate/*<Object,Object>*/ unequal = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                if (a == b)
                    return false;
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)a).valueFactory().getCoercer(true).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return !operands[0].equals(operands[1]);
            }
            public String toString() { return "!="; }
        };

    /**
     * Compares two arithmetic numbers.
     * <p>
     * Result will be &lt;0 if x&lt;y,
     * and &gt;0 if x&gt;y
     * and =0 if x=y.
     * The result will be representable as an int.
     * </p>
     * @attribute strict order
     * @see java.lang.Comparable
     */
    public static final orbital.logic.functor.BinaryFunction/*<Object,Object,Integer>*/ compare = new orbital.logic.functor.BinaryFunction/*<Object,Object,Integer>*/() {
            public Object/*>Integer<*/ apply(Object a, Object b) {
            	ValueFactory vf = ((Arithmetic)a).valueFactory();
                Arithmetic operands[] = (Arithmetic[]) vf.getCoercer(false).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return vf.valueOf(((Comparable) operands[0]).compareTo(operands[1]));
            }
            public String toString() { return "cmp"; }
        };

    /**
     * &lt;.
     * <p>
     * It is true that x&lt;y &hArr; x&le;y &and; x&ne;y.
     * </p>
     * @attribute strict order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ less = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)a).valueFactory().getCoercer(false).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return ((Comparable) operands[0]).compareTo(operands[1]) < 0;
            }
            public String toString() { return "<"; }
        };

    /**
     * &gt;.
     * <p>
     * It is defined as x&gt;y :&hArr; y&lt;x.
     * </p>
     * @attribute strict order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ greater = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)a).valueFactory().getCoercer(false).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return ((Comparable) operands[0]).compareTo(operands[1]) > 0;
            }
            public String toString() { return ">"; }
        };

    /**
     * &le;.
     * <p>
     * It is true that x&le;y &hArr; x&lt;y &or; x&lt;y.
     * </p>
     * @attribute order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ lessEqual = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)a).valueFactory().getCoercer(false).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return ((Comparable) operands[0]).compareTo(operands[1]) <= 0;
            }
            public String toString() { return "=<"; }
        };

    /**
     * &ge;.
     * <p>
     * It is defined as x&ge;y :&hArr; y&le;x.
     * </p>
     * @attribute order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ greaterEqual = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                Arithmetic operands[] = (Arithmetic[]) ((Arithmetic)a).valueFactory().getCoercer(false).apply(new Arithmetic[] {
                    (Arithmetic) a, (Arithmetic) b
                });
                return ((Comparable) operands[0]).compareTo(operands[1]) >= 0;
            }
            public String toString() { return ">="; }
        };


    //@internal must be down here such that static initialization of Predicates.equal != null has already happened

    /**
     * Class alias object.
     * <p>
     * To alias the methods in this class for longer terms, use an idiom like<pre>
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
    public static final Operations operations = new Operations() {
            //@internal neither interfaces nor inner classes can have a static initializer block, so we keep a dummy variable initialization here, even though we could just as well keep it in any other inner class.
            private final short dummy = initialize();
            private final short initialize() {
                //@TODO: + and * could have yfy as well? Would avoid 1+(2+3)
                //@xxx how to avoid amibiguous prints like 7*1/2/3
                Notation.setAllNotations(new Object[][] {
                    {Operations.inverse,                                        // "^-1"
                     new NotationSpecification(195, "xf", Notation.POSTFIX)},
                    {Operations.minus,                                          // "-"/1
                     new NotationSpecification(197, "fx", Notation.PREFIX)},
                    {Operations.power,                                          // "^"
                     new NotationSpecification(200, "xfy", Notation.INFIX)},
                    {Operations.times,                                          // "*" 
                     new NotationSpecification(400, "yfy", Notation.INFIX)},
                    {Operations.divide,                                         // "/"
                     new NotationSpecification(400, "xfx", Notation.INFIX)},
                    {Operations.plus,                                           // "+"
                     new NotationSpecification(500, "yfy", Notation.INFIX)},
                    {Operations.subtract,                                       // "-"/2
                     new NotationSpecification(500, "xfx", Notation.INFIX)}
                });
                return Short.MIN_VALUE;
            }
        };

}


/**
 * A BinaryFunction that performs an operation elementwise.
 * <p>
 * For Arithmetic objects this will be the elemental method for x applied on y,
 * so to say <code>x.<i>elemental</i>(y)</code>.
 * For functor objects this will be a composition of this elementwise operation
 * with x and y.</p>
 * @see BinaryFunction.PointwiseFunction
 */

/* private static inner class is not allowed for interfaces */
abstract class PointwiseMethodFunction implements BinaryFunction {
    private final Method elemental;

    /**
     * Constructs a new elementwise function from an elemental method.
     * @param elemental the elemental method to perform on Arithmetic x (as "this") and y (as argument).
     * So an Arithmetic x must support <code>x.<i>elemental</i>(y)</code> for this
     * elementwise BinaryFunction to work properly.
     */
    protected PointwiseMethodFunction(Method elemental) {
        this.elemental = elemental;
    }

    /**
     * Performs this operation elementwise on x and y.
     * For Arithmetic objects this will be the elemental method for x applied on y,
     * so to say <code>x.<i>elemental</i>(y)</code>.
     * For functor objects this will be a composition of this elementwise operation
     * with x and y.
     * @param x first argument to this elementwise operation which must be Arithmetic or a Functor.
     * Additionally, it must support the elemental method call.
     * @param y second argument to this elementwise operation which must be Arithmetic or a Functor.
     * @see #elemental
     * @see Functionals#genericCompose(BinaryFunction, Object, Object)
     */
    public Object apply(Object x, Object y) {
        if ((x instanceof Arithmetic && !(x instanceof MathFunctor)) && (y instanceof Arithmetic && !(y instanceof MathFunctor)))
            try {
                return elemental.invoke(x, new Object[] {y});
            } catch (IllegalAccessException err) {
                throw new IllegalArgumentException("argument does not support invocation because of " + err);
            } catch (InvocationTargetException err) {
                throw new IllegalArgumentException("argument does not support invocation because of " + err + ": " + err.getTargetException());
            } 
        return Functionals.genericCompose(this, x, y);
    } 
}
