/**
 * @(#)Matrix3D.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

/**
 * This class uses a 4&times;4 Matrix to encapsulate three dimensional
 * affine transformations including affine translations.
 * <p>
 * <b>Note:</b> the angles in the methods of this class are interpreted as degree instead of radian.</p>
 * 
 * @version 0.9, 1996/03/02
 * @author  Andr&eacute; Platzer
 * @see "J.D FOLEY A.VAN DAM. Fundamentals of Interactive Computer Graphics. Addison-Wesley ISBN 0-201-14468-9 pp 245-265"
 * @todo somehow get rid of this 3D class, but without loosing its affine transformation functionality (perhaps generalized to >=3D)?
 */
public class Matrix3D extends RMatrix {

	/**
	 * The identity Matrix.
	 * It has all elements set to <code>0</code>, except the main-diagonal <code>m<sub>i,i</sub></code> set to <code>1</code>.
	 */
	public static final Matrix3D IDENTITY = new Matrix3D(Values.constant(RMatrix.IDENTITY(4)));
	public Matrix3D() {
		super(4, 4);
		set(IDENTITY);
	}
	public Matrix3D(Matrix B) {
		// we restrict ourselves to AbstractMatrix here, for speed considerations
		super(MathUtilities.toDoubleArray(B));
		if (dimension().width != 4 || dimension().height != 4)
			throw new IllegalArgumentException("Matrix3D must be a 4 by 4 Matrix");
	}
	private Matrix3D(double[][] D) {
		super(D);
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
	 * @see java.lang.Math#toRadian(double)
	 * @todo use Math.toRadian(double), instead?
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
		set(this.multiply(DIAGONAL(new RVector(new double[] {sxf, syf, szf, 1}))));
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
		xmat.set(2, 2, cos(ax));	// x -?
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
		ymat.set(2, 2, cos(ay));	// -? x
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



/**
 * ****************************************************************************
 * ctm3d                                    *
 * *
 * Homogeneous Coordinates                            *
 * -----------------------                            *
 * *
 * Homogeneous coordinates allow transformations to be represented by     *
 * matrices. A 3x3 matrix is used for 2D transformations, and a 4x4 matrix*
 * for 3D transformations.                                                *
 * *
 * THIS MODULE IMPLEMENTS ONLY 3D TRANSFORMATIONS.                        *
 * *
 * in homogeneous coordination the point P(x,y,z) is represented as       *
 * P(w*x, w*y, w*z, w) for any scale factor w!=0.                         *
 * in this module w == 1.                                                 *
 * *
 * Transformations:                                                            *
 * 1. translation                                                     *
 * [x, y, z] --> [x + Dx, y + Dy, z + Dz]                     *
 * *
 * Ú          ¿                                   *
 * ³1  0  0  0³                                   *
 * T(Dx, Dy, Dz) = ³0  1  0  0³                                   *
 * ³0  0  1  0³                                   *
 * ³Dx Dy Dz 1³                                   *
 * À          Ù                                   *
 * 2. scaling                                                         *
 * [x, y, z] --> [Sx * x, Sy * y, Sz * z]                     *
 * *
 * Ú          ¿                                       *
 * ³Sx 0  0  0³                                       *
 * S(Sx, Sy) = ³0  Sy 0  0³                                       *
 * ³0  0  Sz 0³                                       *
 * ³0  0  0  1³                                       *
 * À          Ù                                       *
 * *
 * 3. rotation                                                        *
 * *
 * a) Around the Z axis:                                          *
 * *
 * [x, y, z] --> [x*cost - t*sint, x*sint + y*cost, z]        *
 * Ú                  ¿                                   *
 * ³cost  sint   0   0³                                   *
 * Rz(t) = ³-sint cost   0   0³                                   *
 * ³0     0      1   0³                                   *
 * ³0     0      0   1³                                   *
 * À                  Ù                                   *
 * *
 * b) Around the X axis:                                          *
 * *
 * [x, y, z] --> [x, y*cost - z*sint, y*sint + z*cost]        *
 * Ú                  ¿                                   *
 * ³1     0     0    0³                                   *
 * Rx(t) = ³0     cost  sint 0³                                   *
 * ³0    -sint  cost 0³                                   *
 * ³0     0     0    1³                                   *
 * À                  Ù                                   *
 * *
 * c) Around the Y axis:                                          *
 * *
 * [x, y, z] --> [xcost + z*sint, y, z*cost - x*sint]         *
 * Ú                  ¿                                   *
 * ³cost  0   -sint  0³                                   *
 * Ry(t) = ³0     1    0     0³                                   *
 * ³sint  0    cost  0³                                   *
 * ³0     0    0     1³                                   *
 * À                  Ù                                   *
 * *
 * transformation of the vector [x,y,z,1] by transformation matrix T is given *
 * by the formula:                                                           *
 * Ú   ¿                               *
 * [x', y', z', 1] = [x,y,z,1]³ T ³                               *
 * À   Ù                               *
 * Optimizations:                                                              *
 * The most general composition of R, S and T operations will produce a matrix*
 * of the form:                                                              *
 * Ú                       ¿                                      *
 * ³r11    r12     r13    0³                                      *
 * ³r21    r22     r23    0³                                      *
 * ³r31    r32     r33    0³                                      *
 * ³tx     ty      tz     1³                                      *
 * À                       Ù                                      *
 * The task of matrix multiplication can be simplified by                    *
 * x' = x*r11 + y*r21 + z*r31 + tx                                        *
 * y' = x*r12 + y*r22 + z*r32 + ty                                        *
 * z' = x*r13 + y*r23 + z*r33 + tz                                        *
 * *
 * *
 * See also:                                                                   *
 * "Fundamentals of Interactive Computer Graphics" J.D FOLEY A.VAN DAM    *
 * Addison-Wesley ISBN 0-201-14468-9 pp 245-265                           *
 * *
 * ***************************************************************************
 */
