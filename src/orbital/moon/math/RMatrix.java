/**
 * @(#)RMatrix.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import java.awt.Dimension;
import java.util.Iterator;

import orbital.math.functional.Functions;
import java.util.Arrays;

import orbital.util.Utility;

/**
 * Represents a real matrix in <b>R</b><sup>n&times;m</sup> of <code>double</code> values.
 * <p>
 * The components m<sub>i,j</sub> in <b>R</b> are double-values so this is a fast implementation.
 * </p>
 * <p>
 * We could implement a new version, that will fall-back to more general arithmetic matrix whenever necessary but this in turn consumes a little
 * time for conversion. f.ex. if an operation is performed with complex numbers or an arithmetic matrix.
 * </p>
 * 
 * @structure composite D:double[][] unidirectional
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 * @todo sub-views momentarily will not clone and newInstance() to RMatrix or RVectors, but be handled by super class.
 */
class RMatrix extends AbstractMatrix {
    private static final long serialVersionUID = -5257297603569417580L;
    /**
     * Gets zero Matrix, with all elements set to 0.
     */
    public static final Matrix ZERO(Dimension dim) {
	RMatrix zero = new RMatrix(dim.height, dim.width);
	for (int i = 0; i < zero.dimension().height; i++)
	    Arrays.fill(zero.D[i], 0);
	return zero;
    } 
    public static final Matrix ZERO(int size) {
	return ZERO(new Dimension(size, size));
    } 

    /**
     * Gets identity Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to 1.
     * @see Functions#delta
     */
    public static final Matrix IDENTITY(int size) {
	RMatrix identity = new RMatrix(size, size);	   // width, height
	for (int i = 0; i < identity.dimension().height; i++)
	    for (int j = 0; j < identity.dimension().width; j++)
		identity.D[i][j] = Functions.delta(i, j);
	return identity;
    } 
    public static final Matrix IDENTITY(Dimension dim) {
	Utility.pre(dim.width == dim.height, "identity matrix is square");
	return IDENTITY(dim.width);
    } 

    /**
     * Gets diagonal Matrix, with all elements set to 0, except the leading diagonal m<sub>i,i</sub> set to v<sub>i</sub>.
     * @see Functions#delta
     */
    public static final Matrix DIAGONAL(Vector diagon) {
	RMatrix diagonal = new RMatrix(diagon.dimension(), diagon.dimension());	   // width, height
	for (int j = 0; j < diagonal.dimension().height; j++)
	    for (int i = 0; i < diagonal.dimension().width; i++)
		diagonal.D[j][i] = Functions.delta(i, j) * ((Number) diagon.get(i)).doubleValue();
	return diagonal;
    } 

    /**
     * Gets a Hilbert Matrix, with all elements m<sub>i,j</sub> set to <code>1 / (i+j+1)</code>.
     */
    public static final Matrix HILBERT(int width, int height) {
	RMatrix hilbert = new RMatrix(height, width);
	for (int j = 0; j < hilbert.dimension().height; j++)
	    for (int i = 0; i < hilbert.dimension().width; i++)
		hilbert.D[j][i] = 1. / (i + j + 1);
	return hilbert;
    } 
    public static final Matrix HILBERT(int size) {
	return HILBERT(size, size);
    } 


    /**
     * contains the Matrix data m<sub>i,j</sub> as double.
     * <p>
     * As the first index in D, the row i is used
     * and as the second index in D, the column j is used.
     * @serial
     * @todo privatize? But what about Matrix3D, then? And what about other implementations with double[][] subclassing this?
     */
    protected double D[][];

    /**
     * Creates a new Matrix with dimension height&times;width.
     * It then has width columns and height rows.
     */
    public RMatrix(int height, int width) {
	D = new double[height][width];
    }
    public RMatrix(Dimension dim) {
	this(dim.height, dim.width);
    }

    /**
     * creates a new Matrix backed by a two-dimensional array of doubles.
     * The rows are first index, the columns second index.
     * @preconditions values is rectangular, i.e. values[i].length==values[i-1].length
     */
    public RMatrix(double values[][]) {
	set(values);
    }

    /**
     * creates a new Matrix from a two-dimensional array of arithmetic values.
     * The rows are first index, the columns second index.
     * @preconditions v is rectangular, i.e. v[i].length==v[i-1].length
     */
    public RMatrix(Arithmetic v[][]) {
	for (int i = 1; i < v.length; i++)
	    Utility.pre(v[i].length == v[i - 1].length, "rectangular array required");
	D = new double[v.length][v[0].length];
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		D[i][j] = ((Number) v[i][j]).doubleValue();
    }

    protected Matrix newInstance(Dimension dim) {
	return new RMatrix(dim);
    } 

    // since Cloneable's default copy is too shallow for 2-dimensional arrays, this method is necessary
    public Object clone() {
	return new RMatrix(toDoubleArray());
    } 

    public final Dimension dimension() {
	return new Dimension(D.length != 0 ? D[0].length : 0, D.length);
    } 

    public Arithmetic get(int i, int j) {
	return Values.getDefaultInstance().valueOf(getDoubleValue(i, j));
    } 
    public double getDoubleValue(int i, int j) {
	validate(i, j);
	return D[i][j];
    } 
    public void set(int i, int j, Arithmetic m) {
	set(i, j, ((Number) m).doubleValue());
    } 
    public void set(int i, int j, double m) {
	validate(i, j);
	D[i][j] = m;
    } 

    public void setColumn(int c, double col[]) {
	setColumn(c, new RVector(col));
    } 
    public Vector getRow(int r) {
	validate(r, 0);
	return new RVector(D[r]);
    } 
    public void setRow(int r, double row[]) {
	validate(r, row.length - 1);
	D[r] = (double[]) row.clone();
    } 
    public void setRow(int r, Vector row) throws UnsupportedOperationException {
	if (row instanceof RVector)
	    setRow(r, ((RVector) row).D);
	else
	    super.setRow(r, row);
    } 

    /**
     * set all elements of this matrix.
     * @preconditions v is rectangular, i.e. v[i].length==v[i-1].length
     * @todo could we forget about cloning v?
     */
    protected void set(double[][] v) {
	// cloning arrays of arrays would not copy the second
	D = new double[v.length][];
	for (int i = 0; i < v.length; i++) {
	    if (i > 0)
		Utility.pre(v[i].length == v[i - 1].length, "rectangular array required for matrix");
	    D[i] = (double[])v[i].clone();
	}
	modCount++;
    } 

    protected void set(Arithmetic[][] v) {
	D = new double[v.length][v[0].length];
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		set(i, j, v[i][j]);
	modCount++;
    }

    public Arithmetic det() {
	return Values.getDefaultInstance().valueOf(determinantImpl());
    } 
    private double determinantImpl() {
	if (!isSquare())
	    throw new ArithmeticException("determinant only defined for square matrices");
	if (dimension().width == 1)
	    return getDoubleValue(0, 0);
	if (dimension().width == 2)
	    return getDoubleValue(0, 0) * getDoubleValue(1, 1) - getDoubleValue(1, 0) * getDoubleValue(0, 1);
	double det = 0.0;

	// development of 0-th row
	Matrix innerMatrix = ((Matrix) clone()).removeRow(0);
	for (int j = 0; j < dimension().width; j++) {
	    det += ((j & 1) == 0 ? 1 : -1) * getDoubleValue(0, j) * ((RMatrix) ((Matrix) innerMatrix.clone()).removeColumn(j)).determinantImpl();
	} 
	return det;
    } 

    public Matrix add(Matrix B) {
	if (!(B instanceof RMatrix))
	    // fall-back to more general operation
	    return new ArithmeticMatrix(toArray()).add(B);
	Utility.pre(dimension().equals(B.dimension()), "Matrix A+B only defined for equal dimension");
	RMatrix b = (RMatrix) B;
	RMatrix ret = new RMatrix(dimension());

	// element per element
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		ret.D[i][j] = D[i][j] + b.D[i][j];
	return ret;
    } 
    public Matrix subtract(Matrix B) {
	if (!(B instanceof RMatrix))
	    // fall-back to more general operation
	    return new ArithmeticMatrix(toArray()).subtract(B);
	Utility.pre(dimension().equals(B.dimension()), "Matrix A-B only defined for equal dimension");
	RMatrix b = (RMatrix) B;
	RMatrix ret = new RMatrix(dimension());

	// element per element
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		ret.D[i][j] = D[i][j] - b.D[i][j];
	return ret;
    } 

    public Matrix multiply(Matrix B) {
	if (!(B instanceof RMatrix))
	    // fall-back to more general operation
	    return new ArithmeticMatrix(toArray()).multiply(B);
	Utility.pre(dimension().width == B.dimension().height, "Matrix A.B only defined for dimension n by m multiplied with m by l");
	RMatrix b = (RMatrix) B;
	RMatrix ret = new RMatrix(dimension().height, B.dimension().width);
	//@todo could use section striping for large matrices and cache blocking (Willi Schönhauer, Scientific Supercomputing). However we would need to know the cache size then.
	// faster alternative (according to Tichy and Schönhauer)
	assert ret.equals(ZERO(ret.dimension())) : "initialization of double[][] to 0 by system";
	for (int i = 0; i < ret.dimension().height; i++)
       	    for (int k = 0; k < D[i].length; k++) {
		final double r = D[i][k];
		for (int j = 0; j < ret.dimension().width; j++)
		    ret.D[i][j] += r * b.D[k][j];
	    }
	// alternative old version. Why did we use this?
	/*for (int i = 0; i < ret.dimension().height; i++)
	  for (int j = 0; j < ret.dimension().width; j++) {
	  ret.D[i][j] = 0;
	  for (int k = 0; k < D[i].length; k++)
	  ret.D[i][j] += D[i][k] * b.D[k][j];
	  }*/

	return ret;
    }
	 
    public Matrix scale(double s) {
	RMatrix ret = new RMatrix(dimension());

	// element per element
	for (int i = 0; i < dimension().height; i++)
	    for (int j = 0; j < dimension().width; j++)
		ret.D[i][j] = D[i][j] * s;
	return ret;
    } 
    public Arithmetic scale(Arithmetic s) {
	if (!Real.isa.apply(s))
	    // fall-back to more general operation
	    return new ArithmeticMatrix(toArray()).scale(s);
	return scale(((Real)s).doubleValue());
    }

    // diverse decomposition algorithms

    //	 public Arithmetic inverse() throws ArithmeticException {


	
    public Matrix insertRows(int index, Matrix rows) {
	if (index != dimension().height)
	    validate(index, 0);
	Utility.pre(dimension().width == rows.dimension().width, "Matrix must have same width (number of columns)");
	double[][] A = new double[dimension().height + rows.dimension().height][dimension().width];
	for (int i = 0; i < index; i++)
	    A[i] = D[i];
	if (rows instanceof RMatrix) {
	    RMatrix m = (RMatrix) rows;
	    for (int i = 0; i < rows.dimension().height; i++)
		A[index + i] = m.D[i];
	} else if (rows instanceof AbstractMatrix) {
	    AbstractMatrix m = (AbstractMatrix) rows;
	    for (int i = 0; i < m.dimension().height; i++)
		for (int j = 0; j < m.dimension().width; j++)
		    A[index + i][j] = m.getDoubleValue(i, j);
	} else {
	    for (int i = 0; i < rows.dimension().height; i++)
		for (int j = 0; j < rows.dimension().width; j++)
		    A[index + i][j] = ((Real) rows.get(i, j)).doubleValue();
	}
	for (int i = index; i < dimension().height; i++)
	    A[rows.dimension().height + i] = D[i];
	set(A);
	return this;
    } 

    public Matrix removeRow(int r) {
	validate(r, 0);
	double[][] A = new double[dimension().height - 1][dimension().width];
	for (int i = 0; i < r; i++)
	    A[i] = D[i];
	for (int i = r + 1; i < dimension().height; i++)
	    A[i - 1] = D[i];
	set(A);
	return this;
    }

    /**
     * Returns an array containing all the elements in this matrix.
     * The first index in this array specifies the row, the second is for column.
     */
    public double[][] toDoubleArray() {
	// unlike cloning D, this is safe since it does lead to shallow copy of the first array dimension
	double[][] v = new double[D.length][];
	for (int i = 0; i < v.length; i++)
	    v[i] = (double[]) D[i].clone();
	return v;
    } 
}
