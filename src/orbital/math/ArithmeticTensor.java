/**
 * @(#)ArithmeticTensor.java 1.0 2002-08-07 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.util.Iterator;

import java.lang.reflect.Array;
import orbital.util.Utility;

import orbital.algorithm.Combinatorical;

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
    private Object D[];

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
     */
    public ArithmeticTensor(Object values[]) {
	this((Object)values);
    }
    /**
     * creates a new Tensor backed by a multi-dimensional array of arithmetic objects.
     * Also accepts primitive type arrays.
     * The rows are first index, the columns second index, etc.
     * @pre values is rectangular, i.e. values[i<sub>1</sub>]...[i<sub>k-1</sub>][i<sub>k</sub>].length==values[i<sub>1</sub>]...[i<sub>k-1</sub>][i<sub>k</sub>-1].length etc.
     */
    public ArithmeticTensor(Object values) {
	if (values == null)
	    throw new NullPointerException("illegal tensor " + values);
	else if (!values.getClass().isArray())
	    throw new UnsupportedOperationException("tensors of rank 0 should not get wrapped into tensors of non array type");
	final int[] dim = dimensions(values);
	// check rectangular and that base type is instanceof Arithmetic or primitive
	Utility.pre(checkRectangular(dim, 0, values), "multi-dimensional array of " + Arithmetic.class + " expected");
	final Combinatorical index = Combinatorical.getPermutations(dim);
	// whether the array has primitive types
	final boolean primitive = Values.isPrimitiveWrapper(Utility.getPart(values, index.next()).getClass());
	index.previous();
	assert primitive || values instanceof Object[] : "";
	// convert to Arithmetic array in case of primitive type arrays
	this.D = primitive
	    ? (Object[]) Array.newInstance(Arithmetic.class, dim)
	    : (Object[]) values;
	while (index.hasNext()) {
	    final int[] i = index.next();
	    final Object ai = Utility.getPart(values, i);
	    Utility.pre(primitive == Values.isPrimitiveWrapper(ai.getClass()), "multi-dimensional array either consistently has " + Arithmetic.class + " or consistently contains primitive types");
	    if (primitive) {
		assert ai instanceof Number : "primitive type get wrapped into instances of " + Number.class;
		Utility.setPart(D, i, Values.valueOf((Number)ai));
	    }
	}
    }

    /**
     * Checks that the multi-dimensional array o of dimensions dim is rectangular
     * starting from the depth-th index.
     */
    private boolean checkRectangular(int[] dim, int depth, Object o) {
	assert (depth < dim.length) == o.getClass().isArray() : "by definition of rank";
	if (depth >= dim.length)
	    return true;
	final int len = Array.getLength(o);
	if (len != dim[depth])
	    return false;
	// check multi-dimensional array for rectangularity
	for (int j = 0; j < len; j++) {
	    Object oj = Array.get(o, j);
	    if (depth < dim.length - 1) {
		assert oj.getClass().isArray() : "by definition of rank";
		if (!checkRectangular(dim, depth + 1, oj))
		    return false;
	    } else {
		// check that base type is instanceof Arithmetic
		assert !oj.getClass().isArray() : "by definition of rank";
		if (!(o.getClass().getComponentType().isPrimitive() ||
		      Arithmetic.class.isInstance(oj)))
		    return false;
	    }
	}
	return true;
    }

    protected Tensor/*<R>*/ newInstance(int[] dim) {
	return new ArithmeticTensor/*<R>*/(dim);
    } 


    public final int rank() {
	//@todo optimize cache result
	return rank(D);
    }
    private static final int rank(Object[] D) {
	//@todo optimize cache result
    	Object[] o = D;
        int r = 1;
	while (o[0] instanceof Object[]) {
	    o = (Object[])o[0];
	    r++;
	}
	return r;
    }
    private static final int rank(Object D) {
    	Object o = D;
        int r = 0;
	while (o.getClass().isArray()) {
	    o = Array.get(o, 0);
	    r++;
	}
	return r;
    }

    public final int[] dimensions() {
	//@todo optimize cache result
	return dimensions(D);
    }
    private static final int[] dimensions(Object[] D) {
	int[] dim = new int[rank(D)];
    	Object[] o = D;
	for (int i = 0; i < dim.length - 1; i++) {
	    dim[i] = o.length;
	    assert o[0] instanceof Object[] : "by definition of rank";
	    o = (Object[])o[0];
	}
	dim[dim.length - 1] = o.length;
	return dim;
    }
    private static final int[] dimensions(Object D) {
	int[] dim = new int[rank(D)];
    	Object o = D;
	for (int i = 0; i < dim.length; i++) {
	    assert o.getClass().isArray() : "by definition of rank";
	    dim[i] = Array.getLength(o);
	    o = Array.get(o, 0);
	}
	assert !o.getClass().isArray() : "by definition of rank";
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
