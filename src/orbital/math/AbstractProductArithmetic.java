/**
 * @(#)AbstractProductArithmetic.java 1.0 2002-08-12 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.io.Serializable;
import java.util.ListIterator;

import java.util.Iterator;

import orbital.util.Setops;
import orbital.util.Utility;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.logic.functor.Predicates;

/**
 * A base implementation of arithmetic in the product &prod;<sub>i&isin;I</sub> A<sub>i</sub>.
 * <p>
 * This class may serve as a basis to derive from and refine some methods.
 * This implementation does not induce any constraints on the particular type of arithmetic objects
 * in the laws, but is satisfied with just {@link Arithmetic}.
 * The corresponding methods enabling us to do this are
 * {@link #productIndexSet(Arithmetic)}, {@link #iterator(Arithmetic)}, and {@link #newInstance(Object)}.
 * </p>
 * @version 1.0, 2002-08-12
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractProductArithmetic/*<R implements Arithmetic, T implements Arithmetic>*/ extends AbstractArithmetic implements Arithmetic, Serializable {
    private static final long serialVersionUID = 1257583291254889178L;

    /**
     * Get the index set underlying the product.
     * Queries productObject for the index set in an implementation dependent manner.
     * @param productObject the object (a<sub>i</sub>)<sub>i&isin;I</sub> &isin; &prod;<sub>i&isin;I</sub> A<sub>i</sub>
     *  whose components to iterate over.
     * @return an (memento) description of the index set I.
     */
    protected abstract Object productIndexSet(Arithmetic/*<T>*/ productObject);

    /**
     * Creates an iterator for the components of a product object.
     * Queries productObject for the iterator in an implementation dependent manner.
     * @param productObject the object (a<sub>i</sub>)<sub>i&isin;I</sub> &isin; &prod;<sub>i&isin;I</sub> A<sub>i</sub>
     *  whose components to iterate over.
     * @return an iterator that iterates over (a<sub>i</sub>)<sub>i&isin;I</sub>.
     */
    protected abstract ListIterator/*_<R>_*/ iterator(Arithmetic/*<T>*/ productObject);
    
    // factory-methods
    
    /**
     * Instantiates a new arithmetic object of the product set
     * with the specified index set.
     * <p>This method is a replacement for a constructor in the implementation of Arithmetic.</p>
     * @param productIndexSet the index set I for the product, as in {@link #productIndexSet()} .
     * @return a tensor of the same type as this, dimensions as specified.
     * The elements need not be initialized since they will soon be by the calling method.
     * @post RES != RES
     * @see <a href="{@docRoot}/DesignPatterns/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected abstract Arithmetic/*<T>*/ newInstance(Object productIndexSet);
	
    // object-methods
	
    /**
     * Checks two tensors for equality.
     */
    public boolean equals(Object o) {
	if (o instanceof Arithmetic) {
	    Arithmetic/*<T>*/ b = (Arithmetic) o;
	    if (!Utility.equalsAll(productIndexSet(this), productIndexSet(b)))
		return false;
	    return Setops.all(iterator(this), iterator(b), Predicates.equal);
	} 
	return false;
    } 

    public int hashCode() {
	//@todo can we use Utility.hashCodeAll(Object) as well?
	int hash = 0;
	//@todo functional?
	for (Iterator i = iterator(this); i.hasNext(); ) {
	    Object e = i.next();
	    hash ^= e == null ? 0 : e.hashCode();
	} 
	return hash;
    } 

    // arithmetic-operations
	
    public Arithmetic/*<T>*/ add(Arithmetic/*<T>*/ b) {
	Utility.pre(Utility.equalsAll(productIndexSet(this),productIndexSet(b)), "a+b only defined for equal productIndexSet()");
	Arithmetic/*<T>*/ ret = newInstance(productIndexSet(this));

	//@todo implement via orbital.logic.functor.Functionals#map(
	// component-wise
	for (ListIterator i = iterator(this), j = iterator(b), it = iterator(ret); i.hasNext() || j.hasNext() || it.hasNext(); ) {
	    assert i.next() == it.next() && i.next() == j.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).add((Arithmetic/*>R<*/) j.next()));
	}
	return ret;
    } 

    public Arithmetic/*<T>*/ subtract(Arithmetic/*<T>*/ b) {
	Utility.pre(Utility.equalsAll(productIndexSet(this),productIndexSet(b)), "a-b only defined for equal productIndexSet()");
	Arithmetic/*<T>*/ ret = newInstance(productIndexSet(this));

	// component-wise
	for (ListIterator i = iterator(this), j = iterator(b), it = iterator(ret); i.hasNext() || j.hasNext() || it.hasNext(); ) {
	    assert i.next() == it.next() && i.next() == j.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).subtract((Arithmetic/*>R<*/) j.next()));
	}
	return ret;
    } 

    public Arithmetic/*<T>*/ scale(Arithmetic/*<T>*/ s) {
	Arithmetic/*<T>*/ ret = newInstance(productIndexSet(this));

	// component-wise
	for (ListIterator i = iterator(this), it = iterator(ret); i.hasNext() || it.hasNext(); ) {
	    assert i.next() == it.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(s.multiply((Arithmetic/*>R<*/) i.next()));
	}
	return ret;
    } 

    public Arithmetic/*<T>*/ multiply(Arithmetic/*<T>*/ b) {
	Utility.pre(Utility.equalsAll(productIndexSet(this),productIndexSet(b)), "a*b only defined for equal productIndexSet()");
	Arithmetic/*<T>*/ ret = newInstance(productIndexSet(this));

	// component-wise
	for (ListIterator i = iterator(this), j = iterator(b), it = iterator(ret); i.hasNext() || j.hasNext() || it.hasNext(); ) {
	    assert i.next() == it.next() && i.next() == j.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).multiply((Arithmetic/*>R<*/) j.next()));
	}
	return ret;
    } 

    public Arithmetic minus() {
	Arithmetic/*<T>*/ ret = newInstance(productIndexSet(this));

	// component-wise
	for (ListIterator i = iterator(this), it = iterator(ret); i.hasNext() || it.hasNext(); ) {
	    assert i.next() == it.next() : "equal productIndexSet() implies equal structure of iterators";
	    it.next();
	    it.set(((Arithmetic/*>R<*/) i.next()).minus());
	}
	return ret;
    } 

    public Arithmetic inverse() throws ArithmeticException {
	throw new UnsupportedOperationException();
    } 

}
