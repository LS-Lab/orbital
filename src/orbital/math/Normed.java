/**
 * @(#)Normed.java 1.0 2000/08/22 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * This interface imposes a norm on the objects of each class that implements it.
 * A norm is a measure for lengths.
 * <p>
 * Let A be an S-module.
 * <table style="border:none" id="norm">
 *   <tr> <td colspan="3">||.||:A&rarr;<b>R</b> is a <dfn>norm</dfn> if &forall;a,b&isin;A, &alpha;&isin;S:</td> </tr>
 *   <tr> <td>(pdef)</td> <td>||a||&ge;0 and ||a||=0 &hArr; a=0</td> <td>&quot;positive definite&quot;</td> </tr>
 *   <tr> <td>(&Delta;)</td> <td>||a+b|| &le; ||a|| + ||b||</td> <td>&quot;triangular inequality&quot;</td> </tr>
 *   <tr> <td>(hom)</td> <td>||&lambda;a|| = |&lambda;|·||a||</td> <td>&quot;absolute homogenous&quot;</td> </tr>
 *   <tr> <td><b>&rArr;</b></td> <td>Properties</td></tr>
 *   <tr> <td>(<span style="text-decoration: overline">&Delta;</span>)</td> <td><big>|</big>||a|| - ||b||<big>|</big> &le; ||a - b||</td> <td>&quot;inverse triangular inequality&quot;</td> </tr>
 * </table>
 * </p>
 * <p>
 * A norm ||.|| induces a metric d:A&times;A&rarr;<b>R</b>; (a,b)&#8614;d(a,b) := ||a-b||.
 * </p>
 * <p>
 * In turn, a norm itself can be induced by a scalar product as ||a|| := &radic;<span style="text-decoration: overline">&lang;a,a&rang;</span>.
 * It is induced by a scalar product &hArr; ||a+b||<sup>2</sup> + ||a-b||<sup>2</sup> = 2||a||<sup>2</sup> + 2||b||<sup>2</sup>. This is the parallelogram identity.
 * </p>
 * 
 * @version 1.0, 2000/08/22
 * @author  Andr&eacute; Platzer
 * @see Metric
 * @see java.lang.Comparable
 */
public interface Normed {

    // norms, metrics and measures

    /**
     * Returns a norm ||.|| of this arithmetic object.
     * @return the norm of this object,
     *  or perhaps <code>Double.NaN</code> if it is symbolic and really does not have a numeric norm
     *  or a useful symbolic norm.
     * @preconditions true
     * @postconditions RES &ge 0 &and; (RES=0 &hArr; this=0)
     *   &and; a.add(b).norm(x,y) &le; a.norm() + b.norm()
     *   &and; a.multiply(&lambda;).norm() == Math.abs(&lambda;) * a.norm()
     *   &and; RES&ne;null
     */
    Real norm();
}
