/**
 * @(#)MathFunctor.java 1.0 2000/08/05 Andre Platzer
 * 
 * Copyright (c) 2000-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;
import orbital.math.Arithmetic;

/**
 * MathFunctor interface tags all mathematical functors.
 * So this interface for <em>mathematical functors</em> extends normal <em>logic functors</em>.
 * <p>
 * The difference between MathFunctor and its superinterface Functor
 * is that the mathematical MathFunctor tags objects to provide extended mathematical
 * behaviour like derivation and integration, and that it is aware of arithmetic operations
 * on functions.
 * </p>
 * <p>
 * MathFunctors f:A&rarr;B; x &#8614; f(x) form a vector space over K if the function f returns elements in B=K.
 * MathFunctors form a field if and only if |A|=1 which is not a particulary exciting case.</p>
 * <p>
 * MathFunctors usually provide pointwise arithmetic operations.
 * <p>
 * For an arithmetic operation &#8902;:B&times;B&rarr;B this will be a pointwise composition
 * of the operation &#8728; with the functor operands
 * <center>&#8902;:Map(A,B)&times;Map(A,B)&rarr;Map(A,B); (f,g) &#8614; f &#8902; g: A&rarr;B; x &#8614; (f &#8902; g)(x) := f(x) &#8902; g(x)</center>
 * An consequence of this pointwise arithmetic is that the corresponding zero function
 * is the constant
 * <center>0:A&rarr;B; x &#8614; 0</center>
 * And the corresponding one function is the constant
 * <center>1:A&rarr;B; x &#8614; 1</center>
 * Which are both distinct from the identity function.
 * </p>
 * <p>
 * <i><b>Note:</b> once covariant return-types are available again in Java, this interface will
 * unveil its method for derivation</i>. It will have the signature
 * <pre>
 *   <span class="Orbital">MathFunctor</span> derive()
 * </pre>
 * It is now only provided in the sub interfaces because this approach saves inconvenient casting.
 * For the documentation concerning derivation methods in MathFunctor and its sub interfaces
 * see <a href="doc-files/derive.html">derive()</a>.
 * </p>
 * 
 * @structure inhert:orbital.logic.functor.Functor
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @since Orbital1.0
 * @see <a href="doc-files/derive.html">derive()</a>
 */
public abstract interface MathFunctor extends Functor, Arithmetic {
    /**
     * Derives this function and returns the resulting function (<i>d</i>f/<i>d</i>x) f.
     * In fact it applies the differential operator on this function.
     * <p>
     * For a set A &sube; <b>R</b><sup>n</sup>(<i>or a banach space</i>(?)) we define
     * <dl class="def">
     *   <dt>the differential operator</dt>
     *   <dd><i>D</i>=&part;/&part;x:(A&rarr;<b>R</b><sup>m</sup>)&rarr;(A&rarr;Hom(<b>R</b><sup>n</sup>,<b>R</b><sup>m</sup>)) &sub; (A&rarr;(<b>R</b><sup>n</sup>&rarr;<b>R</b><sup>m</sup>)); f &#8614; <i>D</i>f<br />
     *     where<p style="margin-left: 2em; white-space: pre">
     *       <i>D</i>f:A&rarr;Hom(<b>R</b><sup>n</sup>,<b>R</b><sup>m</sup>); x &#8614; (<i>D</i>f)(x),<br />
     *       (<i>D</i>f)(x):<b>R</b><sup>n</sup>&rarr;<b>R</b><sup>m</sup>; h &#8614; (<i>D</i>f)(x)(h) = <span style="letter-spacing: .2em">f'</span>(x)&middot;h
     *       with f'(x)&isin;<b>R</b><sup>m&times;n</sup> (which is the functional matrix)
     *     </p>
     *     If f is derivable at x&isin;<b>R</b><sup>n</sup> it is
     *     <center><big>(</big>f(x+h) - f(x) - (<i>D</i>f)(x)(h)<big>)</big> / ||h|| &rarr; 0 (h&rarr;0)</center>
     *   </dd>
     *   <dt>the total derivative</dt>
     *   <dd>
     *     <table>
     *       <tr><td rowspan="4"><i>d</i>f/<i>d</i>x = <span style="letter-spacing: .2em">f'</span> = &part;f/&part;x = <big>(</big>&part;f<sub>i</sub>/&part;x<sub>j</sub><big>)</big><sub>i,j</sub> = </td>
     *           <td rowspan="4" style="font-size: 600%; font-weight: 200">(</td> <td>&#8711;f<sub>1</sub></td> <td rowspan="4" style="font-size: 600%; font-weight: 200">)</td>
     *           <td rowspan="4">=</td>
     *           <td rowspan="4" style="font-size: 600%; font-weight: 200">[</td> <td>&part;f<sub>1</sub>/&part;x<sub>1</sub>,</td> <td>&part;f<sub>1</sub>/&part;x<sub>2</sub>, </sub>,</td> <td>&#8230;,</td> <td>&part;f<sub>1</sub>/&part;x<sub>n</sub></td> <td rowspan="4" style="font-size: 600%; font-weight: 200">]</td>
     *       </tr>
     *       <tr>
     *           <td>&#8711;f<sub>2</sub></td>
     *           <td>&part;f<sub>2</sub>/&part;x<sub>1</sub>,</td> <td>&part;f<sub>2</sub>/&part;x<sub>2</sub>,</td> <td>&#8230;,</td> <td>&part;f<sub>2</sub>/&part;x<sub>n</sub></td>
     *       </tr>
     *       <tr>
     *           <td colspan="1">&#8942;</td>
     *           <td>&#8942;</td>
     *           <td></td>
     *           <td>&#8230;</td>
     *           <td>&#8942;</td>
     *       </tr>
     *       <tr>
     *           <td>&#8711;f<sub>m</sub></td>
     *           <td>&part;f<sub>m</sub>/&part;x<sub>1</sub>,</td> <td>&part;f<sub>m</sub>/&part;x<sub>2</sub>,</td> <td>&#8230;,</td> <td>&part;f<sub>m</sub>/&part;x<sub>n</sub></td>
     *       </tr>
     *     </table>
     * 
     *     The total derivative <i>d</i>f/<i>d</i>x:A&rarr;<b>R</b><sup>m&times;n</sup>
     *     can be identified with the matrix of <i>D</i>f.
     *     Note that we identify a matrix of real-valued unary functions with a matrix-valued function, here.
     *     If it exists it is identical to the functional matrix or Jacobi matrix of the partial derivatives &part;f<sub>i</sub>/&part;x<sub>j</sub>.
     *     Note that if we identify Hom(<b>R</b><sup>n</sup>,<b>R</b><sup>m</sup>) = <b>R</b><sup>m&times;n</sup> it is true that
     *     <span style="letter-spacing: .2em">f'</span> = <i>D</i>f.
     *   </dd>
     *   <dt>the total differential</dt>
     *   <dd>(<i>D</i>f)(x)(<i>d</i>x) = <span style="letter-spacing: .2em">f'</span>(x)&middot;<i>d</i>x = &part;f/&part;x(x)&middot;dx
     *     = &part;f/&part;x<sub>1</sub>(x)<i>d</i>x<sub>1</sub> + &part;f/&part;x<sub>2</sub>(x)<i>d</i>x<sub>2</sub> + &#8230; + &part;f/&part;x<sub>n</sub>(x)<i>d</i>x<sub>n</sub>
     *     for h:=<i>d</i>x&isin;<b>R</b><sup>n</sup>.
     *   </dd>
     * </dl>
     * </p>
     * @return the total derivative function <i>D</i>f.
     * @throws ArithmeticException if this function is not derivable.
     * @throws UnsupportedOperationException if this function does not implement derivation but could be derived in principle.
     */
    //TODO: introduce MathFunctor derive(); once covariant return-types are allowed.
        
    //TODO: or introduce Function<A, Arithmetic> derive()
    //NO: or introduce either Function<A, Function<R^n,R^m>> derive()
    //NO: or even Function<A, Matrix<R, n x m>/Arithmetic> derive()

    /**
     * Integrates this function and returns the resulting indefinite integral &int; f <i>d</i>x.
     * @return the indefinite integral function &int; f <i>d</i>x.
     * @throws ArithmeticException if this function is not integrable.
     * @throws UnsupportedOperationException if this function does not implement integration but could be integrated in principle.
     */
    //TODO: introduce Function integrate();

    /**
     * A composed mathematical functors.
     * <div>compose: (f,g) &#8614; f &#8728; g := f(g)</div>
     * <p>
     * Functions could be composed of an outer Function and an inner Function concatenated with each other.
     * </p>
     * 
     * @structure is {@link orbital.logic.functor.Functor.Composite}&cap;{@link MathFunctor}
     * @structure extends MathFunctor
     * @structure extends Functor.Composite
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @internal if we only had a section interface of Function and Functor.Composite, then
     *  we would not need this interface.
     *  The same goes for similar *.Composite* interface here and in math.functional.
     */
    static interface Composite extends orbital.logic.functor.Functor.Composite, MathFunctor {}
}
