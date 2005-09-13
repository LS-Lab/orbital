/**
 * @(#)Metric.java 1.0 2000/08/22 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * This interface imposes a metric on the objects supported by it.
 * A metric is a measure for distances.
 * <p>
 * <table style="border:none" id="metric">
 *   <tr> <td colspan="3">d:A&times;A&rarr;<b>R</b> is a <dfn>metric</dfn> (or distance function) on A if &forall;x,y,z,w&isin;A</td> </tr>
 *   <tr> <td>(def)</td> <td>d(x,y)=0 &hArr; x=y</td> <td>&quot;(positive?) definite&quot;</td> </tr>
 *   <tr> <td>(s)</td> <td>d(x,y) = d(y,x)</td> <td>&quot;symmetric&quot;</td> </tr>
 *   <tr> <td>(&Delta;)</td> <td>d(x,y) &le; d(x,z) + d(z,y)</td> <td>&quot;triangular inequality&quot;</td> </tr>
 *   <tr> <td colspan="3">&rArr; (even for half-metrics, i.e. without definite)</td> </tr>
 *   <tr> <td>(&#9633;)</td> <td>|d(x,y) - d(z,w)| &le; d(x,z) + d(y,w)</td> <td>&quot;rectangular inequality&quot;</td> </tr>
 *   <tr> <td>(pos)</td> <td>d(x,y)&ge;0</td> <td>&quot;positive&quot;</td> </tr>
 * </table>
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Normed
 * @see #INDUCED
 * @see java.util.Comparator
 */
public interface Metric/*<A>*/ {

    // norms, metrics and measures

    /**
     * Returns the distance of two objects.
     * @return the distance of the objects a and b, or <code>Double.NaN</code> if it is symbolic and has no numeric distance.
     * @postconditions RES >= 0 && RES==0 <=> x==y
     *   && distance(x,y) == distance(y,x)
     *   && distance(x,y) <= distance(x,z) + distance(z,y)
     *   && RES&ne;null
     */
    Real distance(Object/*>A<*/ x, Object/*>A<*/ y);
	
    // induced metric
	
    /**
     * The metric induced by a norm ||.||.
     * <p>
     * A norm {@link Normed ||.||} on arithmetic objects induces a metric
     * d:A&times;A&rarr;<b>R</b>; (a,b)&#8614;d(a,b) := ||a-b||.
     * </p>
     * @see Normed
     */
    static final Metric/*<Arithmetic>*/ INDUCED = new Metric/*<Arithmetic>*/() {
	    public Real distance(Object/*>Arithmetic<*/ x, Object/*>Arithmetic<*/ y) {
		return ((Arithmetic/*__*/) x).subtract((Arithmetic/*__*/) y).norm();
	    }
	};
}
