/**
 * @(#)ArithmeticTensor.java 1.0 2002-08-07 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.util.Iterator;

import java.lang.reflect.Array;
import orbital.util.Utility;

/**
 * Represents a general tensor in A<sup>n<sub>1</sub>&times;n<sub>2</sub>&times;&#8230;&times;n<sub>r</sub></sup> of arithmetic values.
 * <p>
 * The components m<sub>i<sub>1</sub>&times;i<sub>2</sub>&times;&#8230;&times;i<sub>r</sub></sub> in A are Arithmetic objects.</p>
 * 
 * @structure composite D:Arithmetic[][] unidirectional
 * @version 1.0, 2002-08-07
 * @author  Andr&eacute; Platzer
 */
class ArithmeticTensor/*<R implements Arithmetic>*/ extends AbstractTensor/*<R>*/ {
    private static final long serialVersionUID = -6766356302306780151L;
    /**
     * contains the tensor data m<sub>i<sub>1</sub>&times;i<sub>2</sub>&times;&#8230;&times;i<sub>r</sub></sub> as Arithmetic objects.
     * <p>
     * Tensor components are store row-wise, which means that
     * as the first index in D, the row i is used
     * and as the second index in D, the column j is used etc.
     * </p>
     * @serial
     */
    protected Object D[];

    /**
     * Creates a new Tensor with dimension n<sub>1</sub>&times;n<sub>2</sub>&times;&#8230;&times;n<sub>r</sub>.
     */
    public ArithmeticTensor(int[] dimensions) {
 	D = (Object[]) Array.newInstance(Arithmetic/*>R<*/.class, dimensions);
    }

    /**
     * creates a new Tensor backed by a multi-dimensional array of arithmetic objects.
     * The rows are first index, the columns second index, etc.
     * @pre values is rectangular, i.e. values[i<sub>1</sub>]...[i<sub>k-1</sub>][i<sub>k</sub>].length==values[i<sub>1</sub>]...[i<sub>k-1</sub>][i<sub>k</sub>-1].length etc.
     * @todo accept double[][]...[]
     */
    public ArithmeticTensor(Object values[]) {
	if (values == null)
	    throw new NullPointerException("illegal tensor " + values);
	D = values;
	// check rectangular and that base type is instanceof Arithmetic
	final int[] dim = dimensions();
	Object[] o = D;
	// check multi-dimensional array for rectangularity
	for (int i = 0; i < dim.length - 1; i++) {
	    assert o instanceof Object[][] : "by definition of rank";
	    final Object[][] a = (Object[][])o;
	    for (int k = 1; k < dim[i]; k++)
		Utility.pre(a[k].length == a[k - 1].length, "rectangular multi-dimensional array required");
	    o = a[0];
	}
	// check that base type is instanceof Arithmetic
	if (dim.length > 0) {
	    assert !(o instanceof Object[][]) : "by definition of rank";
	    for (int k = 0; k < dim[dim.length - 1]; k++)
		Utility.pre(Arithmetic.class.isInstance(o[k]), "multi-dimensional array of " + Arithmetic.class + " expected");
	}
    }

    protected Tensor/*<R>*/ newInstance(int[] dim) {
	return new ArithmeticTensor/*<R>*/(dim);
    } 


    public final int rank() {
	//@todo optimize cache result
    	Object[] o = D;
        int r = 1;
	while (o[0] instanceof Object[]) {
	    o = (Object[])o[0];
	    r++;
	}
	return r;
    }

    public final int[] dimensions() {
	//@todo optimize cache result
	int[] dim = new int[rank()];
    	Object[] o = D;
	for (int i = 0; i < dim.length - 1; i++) {
	    dim[i] = o.length;
	    assert o[0] instanceof Object[] : "by definition of rank";
	    o = (Object[])o[0];
	}
	dim[dim.length - 1] = o.length;
	return dim;
    }

    public Arithmetic/*>R<*/ get(int[] i) {
	validate(i);
	return (Arithmetic/*>R<*/)Utility.getPart(D, i);
    } 
    public void set(int i[], Arithmetic/*>R<*/ m) {
	validate(i);
	Utility.setPart(D, i, m);
    } 

    public Object clone() {
	return new ArithmeticTensor/*<R>*/(toArray__Tensor());
    } 
}
