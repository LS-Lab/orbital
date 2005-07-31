/**
 * @(#)Euclidean..java 1.1 2001/12/09 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * Euclidean ring interface.
 * <dl class="def">
 * Let R be an integrity domain.
 *   <dt>Euclidean ring</dt>
 *   <dd>R is an Euclidean ring :&hArr;
 *     &exist;&delta;:R&#8726;{0}&rarr;<b>N</b>
 *     <blockquote>
 *       &forall;f&isin;R&forall;g&isin;R&#8726;{0} &exist;q,r&isin;R f = q&sdot;g + r
 *        =: (f div g)&sdot;g + (f mod g)
 *     </blockquote>
 *     with &delta;(r) &lt; &delta;(g) &or; r = 0.
 *     <div>
 *       &delta; is called Euclidean degree of R.
 *       The Euclidean &quot;quotient&quot; q is denoted by f div g,
 *       the Euclidean remainder r is denoted by f mod g.
 *     </div>
 *   </dd>
 * </dl>
 * <ul>
 * <b>&rArr; Properties:</b>
 *   <li>the Euclidean algorithm can compute greatest common divisors in R.</li>
 *   <li>R is a principal ideal integrity domain, therefore implying that &forall;a,b,d,m&isin;R&#8726;{0}
 *     <ul>
 *       <li>(d)=(a)+(b) &hArr; d=gcd(a,b)</li>
 *       <li>(m)=(a)&cap;(b) &hArr; m=lcm(a,b)</li>
 *       <li>(m)=(a)&sdot;(b) &hArr; m=a&sdot;b (always)</li>
 *     </ul>
 *   </li>
 *   <li>&forall;a,b&isin;R&#8726;{0} &exist;gcd(a,b)&isin;R &and; (gcd(a,b)) = (a,b)</li>
 * </ul>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo provide super interface for elements of factorial rings that provide a method R[] factorize()
 */
public interface Euclidean extends Arithmetic {
	
    /**
     * Get the Euclidean degree.
     * In case R is a discrete valuation ring, this also is the valuation of Quot(R).
     * @return the Euclidean degree &delta;(this) of this value.
     * @postconditions RES=&delta;(this)
     */
    Integer degree();
	
    /**
     * Get the Euclidean &quot;quotient&quot; by g.
     * @return this div g &isin; R.
     * @throws IllegalArgumentException if the argument type is illegal for this operation.
     *  Note: for single type handling it is also allowed to throw a ClassCastException, instead.
     * @note the Euclidean quotient f div g is distinct from the fractional quotient f/g=f.{@link Arithmetic#divide(Arithmetic) divide}(g).
     * @todo rename since quotient is f/g in the quotient ring itself.
     */
    Euclidean quotient(Euclidean g);

    /**
     * Get the Euclidean remainder, modulo g.
     * @return this mod g &isin; R.
     * @postconditions RES.degree() < g.degree() &or; RES==0
     * @throws IllegalArgumentException if the argument type is illegal for this operation.
     *  Note: for single type handling it is also allowed to throw a ClassCastException, instead.
     * @todo rename to remainder(Euclidean)?
     */
    Euclidean modulo(Euclidean g);

    // @todo ? introduce extends Arithmetic<Euclidean>
}
