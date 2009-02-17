/**
 * @(#)Matrix3D.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

/**
 * This class uses a 4&times;4 Matrix to encapsulate three dimensional
 * affine transformations (including affine translations).
 * <p>
 * The affine group 
 * <table>
 *   <tr><td rowspan="2">Aff(<b>R</b><sup>n</sup>) := {</td>
 *       <td rowspan="2" style="font-size: 200%; font-weight: 100">[</td> <td>A</td> <td>t</td> <td rowspan="2" style="font-size: 200%; font-weight: 100">]</td> <td rowspan="2">&brvbar;</td> <td rowspan="2">A&isin;GL<sub>n</sub>(<b>R</b>),t&isin;<b>R</b><sup>n</sup>} &lt; GL<sub>n+1</sub>(<b>R</b>)</td></tr>
 *   <tr> <td>0</td> <td>1</td> </tr>
 * </table>
 * is the (external) semi-direct product <b>R</b><sup>n</sup> &#8906;<sub>&phi;</sub> GL<sub>n</sub>(<b>R</b>) per &phi;:GL<sub>n</sub>(<b>R</b>)&rarr;Aut(<b>R</b><sup>n</sup>); A&#8614;(v&#8614;Av).
 * </p>
 * <p>
 * <b>Note:</b> the angles in the methods of this class are interpreted as degree instead of radian.</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see "J.D FOLEY A.VAN DAM. Fundamentals of Interactive Computer Graphics. Addison-Wesley ISBN 0-201-14468-9 pp 245-265"
 * @todo somehow get rid of this 3D class, but without loosing its affine transformation functionality (perhaps generalized to >=3D)?
 */
public class Matrix3D extends RMatrix {
    private static final long serialVersionUID = 2766955559623496088L;
    /**
     * The identity Matrix.
     * It has all elements set to <code>0</code>, except the main-diagonal <code>m<sub>i,i</sub></code> set to <code>1</code>.
     */
    public static final Matrix3D IDENTITY = new Matrix3D(Values.getDefaultInstance().constant(RMatrix.IDENTITY(4)));
    public Matrix3D() {
        super(4, 4, Values.getDefault());
        set(IDENTITY);
    }
    public Matrix3D(Matrix B) {
        // we restrict ourselves to AbstractMatrix here, for speed considerations
        super(((AbstractMatrix)B).toDoubleArray(), B.valueFactory());
        if (dimension().width != 4 || dimension().height != 4)
            throw new IllegalArgumentException("Matrix3D must be a 4 by 4 Matrix");
    }
    private Matrix3D(double[][] D) {
        super(D, Values.getDefault());
        if (dimension().width != 4 || dimension().height != 4)
            throw new IllegalArgumentException("Matrix3D must be a 4 by 4 Matrix");
    }

    public Object clone() {
        return new Matrix3D(D);
    } 

    private void set(Matrix A) {
        set(((RMatrix) A).D);
    } 

    /**
     * a sinus/cosinus for degrees
     * @see java.lang.Math#toRadians(double)
     * @todo use Math.toRadians(double), instead?
     */
    private double sin(double deg) {
        return Math.sin(deg * Math.PI / 180);
    } 
    private double cos(double deg) {
        return Math.cos(deg * Math.PI / 180);
    } 

    // caution(!) the order of transformations is NOT commutative.
    // it might be reverse ordered by v_View.move()

    /**
     * Scales a Matrix.
     * This is a linear isometric transformation.
     */
    public void scale(double sxf, double syf, double szf) {
        set(this.multiply(DIAGONAL(new RVector(new double[] {sxf, syf, szf, 1}, valueFactory()))));
    } 

    /**
     * Translates a Matrix.
     * This is an affine isometric transformation.
     * Especially, it is an affinity (bijective affine mapping).
     */
    public void translate(double xt, double yt, double zt) {
        Matrix3D t = (Matrix3D) IDENTITY.clone();
        t.set(0, 3, xt);
        t.set(1, 3, yt);
        t.set(2, 3, zt);
        set(this.multiply(t));
    } 

    /**
     * Rotates a Matrix around the x-axis.
     * This is a linear isometric transformation.
     */
    public void rotatex(double ax) {
        Matrix3D xmat = (Matrix3D) IDENTITY.clone();
        xmat.set(1, 1, cos(ax));
        xmat.set(2, 1, sin(ax));
        xmat.set(1, 2, -sin(ax));
        xmat.set(2, 2, cos(ax));        //@xxx x -?
        set(this.multiply(xmat));
    } 
    /**
     * Rotates a Matrix around the y-axis.
     * This is a linear isometric transformation.
     */
    public void rotatey(double ay) {
        Matrix3D ymat = (Matrix3D) IDENTITY.clone();
        ymat.set(0, 0, cos(ay));
        ymat.set(2, 0, -sin(ay));
        ymat.set(0, 2, sin(ay));
        ymat.set(2, 2, cos(ay));        //@xxx -? x
        set(this.multiply(ymat));
    } 
    /**
     * Rotates a Matrix around the z-axis.
     * This is a linear isometric transformation.
     */
    public void rotatez(double az) {
        Matrix3D zmat = (Matrix3D) IDENTITY.clone();
        zmat.set(0, 0, cos(az));
        zmat.set(1, 0, sin(az));
        zmat.set(0, 1, -sin(az));
        zmat.set(1, 1, cos(az));
        set(this.multiply(zmat));
    } 
    /**
     * Rotates a Matrix around the every axis.
     * This is a linear isometric transformation.
     */
    public void rotate(double ax, double ay, double az) {
        rotatex(ax);
        rotatey(ay);
        rotatez(az);
    } 
}
