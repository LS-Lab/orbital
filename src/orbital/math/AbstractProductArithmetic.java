/**
 * @(#)AbstractProductArithmetic.java 1.0 2002-08-12 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

import orbital.util.Setops;
import orbital.util.InnerCheckedException;
import orbital.util.Utility;
import orbital.algorithm.Combinatorical;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.math.functional.Functions;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.Predicates;

/**
 * A base implementation of arithmetic in the product &prod;<sub>i&isin;I</sub> A<sub>i</sub>.
 * <p>
 * This class may serve as a basis to derive from and refine some methods.
 * </p>
 * @version 1.0, 2002-08-12
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractProductArithmetic/*<R implements Arithmetic>*/ extends AbstractArithmetic implements Arithmetic, Serializable {
    //private static final long serialVersionUID = 0;

    //@todo move to TProductArithmetic or whatever interface should keep that. However, that interface would then have to be public and extended by all interfaces whose implementations somehow rely on it, like Tensor, Polynomial, 
    /**
     * Get an iterator over the components of this product.
     * @return an iterator over (a<sub>i</sub>)<sub>i&isin;I</sub>.
     */
    public abstract ListIterator/*_<R>_*/ iterator();

    protected abstract Object productIndexSet();
    
    // factory-methods
    
    /**
     * Instantiates a new tensor with dimensions dim of the same type like this.
     * <p>This method is a replacement for a constructor in the implementation of Arithmetic.</p>
     * @param dim the dimensions desired for the tensor.
     * @return a tensor of the same type as this, dimensions as specified.
     * The elements need not be initialized since they will soon be by the calling method.
     * @post RES != RES
     * @see <a href="{@docRoot}/DesignPatterns/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract TProductArithmetic/*<R>*/ newInstance(Object productIndexSet);
	
    // object-methods
	
    /**
     * Checks two tensors for equality.
     */
    public boolean equals(Object o) {
	if (o instanceof TProductArithmetic) {
	    TProductArithmetic/*<R>*/ b = (TProductArithmetic) o;
	    if (!Utilities.equalsAll(productIndexSet(), b.productIndexSet()))
		return false;
	    return orbital.util.Setops.all(iterator(), b.iterator(), Predicates.equal);
	} 
	return false;
    } 

    public int hashCode() {
	//@todo can we use Utility.hashCodeAll(Object) as well?
	int hash = 0;
	//@todo functional?
	for (Iterator i = iterator(); i.hasNext(); ) {
	    Object e = i.next();
	    hash ^= e == null ? 0 : e.hashCode();
	} 
	return hash;
    } 

    // arithmetic-operations
	
    public TProductArithmetic/*<R>*/ add(TProductArithmetic/*<R>*/ b) {
	Utility.pre(Utilities.equalsAll(productIndexSet(),b.productIndexSet()), "a+b only defined for equal productIndexSet()");
	TProductArithmetic/*<R>*/ ret = newInstance(productIndexSet());

	// component-wise
	for (ListIterator i = iterator(), j = b.iterator(), it = ret.iterator(); i.hasNext() || j.hasNext() || it.next(); ) {
	    assert i.next() == it.next() && i.next() == j.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).add((Arithmetic/*>R<*/) j.next()));
	}
	return ret;
    } 

    public TProductArithmetic/*<R>*/ subtract(TProductArithmetic/*<R>*/ b) {
	Utility.pre(Utilities.equalsAll(productIndexSet(),b.productIndexSet()), "a-b only defined for equal productIndexSet()");
	TProductArithmetic/*<R>*/ ret = newInstance(productIndexSet());

	// component-wise
	for (ListIterator i = iterator(), j = b.iterator(), it = ret.iterator(); i.hasNext() || j.hasNext() || it.next(); ) {
	    assert i.next() == it.next() && i.next() == j.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).subtract((Arithmetic/*>R<*/) j.next()));
	}
	return ret;
    } 

    public TProductArithmetic scale(TProductArithmetic s) {
	TProductArithmetic/*<R>*/ ret = newInstance(productIndexSet());

	// component-wise
	for (ListIterator i = iterator(), it = ret.iterator(); i.hasNext() || it.next(); ) {
	    assert i.next() == it.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(s.multiply((Arithmetic/*>R<*/) i.next()));
	}
	return ret;
    } 

    public TProductArithmetic/*<R>*/ multiply(TProductArithmetic/*<R>*/ b) {
	Utility.pre(Utilities.equalsAll(productIndexSet(),b.productIndexSet()), "a*b only defined for equal productIndexSet()");
	TProductArithmetic/*<R>*/ ret = newInstance(productIndexSet());

	// component-wise
	for (ListIterator i = iterator(), j = b.iterator(), it = ret.iterator(); i.hasNext() || j.hasNext() || it.next(); ) {
	    assert i.next() == it.next() && i.next() == j.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).multiply((Arithmetic/*>R<*/) j.next()));
	}
	return ret;
    } 

    // Arithmetic implementation

    public Arithmetic add(Arithmetic b) {
	return add((TProductArithmetic) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	return subtract((TProductArithmetic) b);
    } 
    public Arithmetic minus() {
	TProductArithmetic/*<R>*/ ret = newInstance(productIndexSet());

	// component-wise
	for (ListIterator i = iterator(), it = ret.iterator(); i.hasNext() || it.next(); ) {
	    assert i.next() == it.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).minus());
	}
	return ret;
    } 

    public Arithmetic multiply(Arithmetic b) {
	return multiply((TProductArithmetic) b);
    } 

    public Arithmetic inverse() throws ArithmeticException {
	throw new UnsupportedOperationException();
    } 

}
