/**
 * @(#)ArithmeticMatrix.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import java.awt.Dimension;
import java.util.Iterator;

import orbital.util.Utility;

/**
 * Represents a general matrix in A<sup>n&times;m</sup> of arithmetic values.
 * <p>
 * The components m<sub>i,j</sub> in A are Arithmetic objects.</p>
 * 
 * @structure composite D:Arithmetic[][] unidirectional
 * @version 1.0, 2000/08/08
 * @author  Andr&eacute; Platzer
 */
class ArithmeticMatrix/*<R implements Arithmetic>*/ extends AbstractMatrix/*<R>*/ {
    private static final long serialVersionUID = -2994686890096422385L;
    /**
     * contains the Matrix data m<sub>i,j</sub> as Arithmetic objects.
     * <p>
     * Matrix components are store row-wise, which means that
     * as the first index in D, the row i is used
     * and as the second index in D, the column j is used.
     * </p>
     * @serial
     */
    Arithmetic/*>R<*/ D[][];

    /**
     * Creates a new Matrix with dimension height&times;width.
     * It then has width columns and height rows.
     */
    public ArithmeticMatrix(int height, int width) {
	D = new Arithmetic/*>R<*/[height][width];
    }
    public ArithmeticMatrix(Dimension dim) {
	this(dim.height, dim.width);
    }

    /**
     * creates a new Matrix backed by a two-dimensional array of arithmetic objects.
     * The rows are first index, the columns second index.
     * @preconditions values is rectangular, i.e. v[i].length==v[i-1].length
     */
    public ArithmeticMatrix(Arithmetic/*>R<*/ values[][]) {
	for (int i = 1; i < values.length; i++)
	    Utility.pre(values[i].length == values[i - 1].length, "rectangular array required");
	D = values;
    }

    protected Matrix/*<R>*/ newInstance(Dimension dim) {
	return new ArithmeticMatrix/*<R>*/(dim);
    } 



    public final Dimension dimension() {
	return new Dimension(D.length != 0 ? D[0].length : 0, D.length);
    } 

    public Arithmetic/*>R<*/ get(int i, int j) {
	validate(i, j);
	return D[i][j];
    } 
    public void set(int i, int j, Arithmetic/*>R<*/ m) {
	validate(i, j);
	D[i][j] = m;
    } 

    public Vector/*<R>*/ getRow(int r) {
	validate(r, 0);
	return new ArithmeticVector/*<R>*/(D[r]);
    } 
    public void setRow(int r, Vector/*<R>*/ row) throws UnsupportedOperationException {
	validate(r, row.dimension() - 1);
	if (row instanceof ArithmeticVector)
	    D[r] = (Arithmetic/*>R<*/[]) ((ArithmeticVector) row).D.clone();
	else
	    super.setRow(r, row);
    } 

    /**
     * @preconditions values is rectangular, i.e. v[i].length==v[i-1].length
     * @todo could we forget about cloning v?
     */
    protected void set(Arithmetic/*>R<*/[][] v) {
	D = new Arithmetic/*>R<*/[v.length][];
	for (int i = 0; i < v.length; i++) {
	    if (i > 0)
		Utility.pre(v[i].length == v[i - 1].length, "rectangular array required");
	    D[i] = (Arithmetic/*>R<*/[]) v[i].clone();
	} 
	modCount++;
    } 

    public Object clone() {
	return new ArithmeticMatrix/*<R>*/(toArray());
    } 

    public Matrix/*<R>*/ insertRows(int index, Matrix/*<R>*/ rows) {
	if (index != dimension().height)
	    validate(index, 0);
	Utility.pre(dimension().width == rows.dimension().width, "Matrix must have same width (number of columns)");
	Arithmetic/*>R<*/[][] A = new Arithmetic/*>R<*/[dimension().height + rows.dimension().height][dimension().width];
	for (int i = 0; i < index; i++)
	    A[i] = D[i];
	if (rows instanceof ArithmeticMatrix) {
	    ArithmeticMatrix/*<R>*/ m = (ArithmeticMatrix) rows;
	    for (int i = 0; i < rows.dimension().height; i++)
		A[index + i] = m.D[i];
	} else
	    for (int i = 0; i < rows.dimension().height; i++)
		for (int j = 0; j < rows.dimension().width; j++)
		    A[index + i][j] = rows.get(i, j);
	for (int i = index; i < dimension().height; i++)
	    A[rows.dimension().height + i] = D[i];
	set(A);
	return this;
    } 

    public Matrix/*<R>*/ removeRow(int r) {
	validate(r, 0);
	Arithmetic/*>R<*/[][] A = new Arithmetic/*>R<*/[dimension().height - 1][dimension().width];
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
    public Arithmetic/*>R<*/[][] toArray() {
	// unlike cloning D, this is safe since it does not lead to shallow copy of the first array dimension
	Arithmetic/*>R<*/[][] v = new Arithmetic/*>R<*/[D.length][];
	for (int i = 0; i < v.length; i++)
	    // we do not need to clone D[i][j] as well?
	    v[i] = (Arithmetic/*>R<*/[]) D[i].clone();
	return v;
    } 
}
