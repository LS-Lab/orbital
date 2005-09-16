/**
 * @(#)SparsePolynomial.java 1.1 2002-09-01 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import orbital.math.functional.Function;
import java.util.ListIterator;
import java.util.Iterator;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

/**
 * Implementation of polynomials in R[S] with a sparse map of coefficients.
 * Arbitrary S.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
class SparsePolynomial/*<R extends Arithmetic, S extends Arithmetic>*/ extends AbstractPolynomial/*<R,S>*/ {
    private static final long serialVersionUID = -8833160240745985849L;

    /**
     * Maps indices in S to the corresponding coefficients &iota;(s)&isin;R.
     */
    private final Map/*<S,R>*/ coefficients;
    /**
     * The index 0&isin;S of the constant term.
     */
    private final Arithmetic/*>S<*/ CONSTANT_TERM;

    public SparsePolynomial(Arithmetic/*>S<*/ anIndexObject) {
        //@internal assuming (S,+) here
        this.CONSTANT_TERM = anIndexObject.zero();
        this.coefficients = new HashMap();
    }
    public SparsePolynomial(Map/*<S,R>*/ coefficients) {
        this.CONSTANT_TERM = ((Arithmetic/*>S<*/)coefficients.keySet().iterator().next()).zero();
        this.coefficients = coefficients;
    }
    
    // factory-methods
    
    protected final Arithmetic/*>T<*/ newInstance(Object productIndexSet) {
        return new SparsePolynomial((Arithmetic)productIndexSet);
    }

    // iterator-views
    
    /**
     * The number of times this object has been structurally modified.
     * Structural modifications are those that change the number of elements in
     * the object or otherwise modify its internal structure.
     * This field is used to make iterators of the object fail-fast.
     * <p>
     * To use this feature, increase modCount whenever an implementation method changes
     * this tensor.</p>
     * @see java.util.ConcurrentModificationException
     */
    protected transient int modCount = 0;

    public Iterator indices() {
        return coefficients.keySet().iterator();
    } 

    public ListIterator iterator() {
        return new ListIterator() {
                private final Iterator cursor = coefficients.entrySet().iterator();
                private Entry lastRet = null;
                /**
                 * The modCount value that the iterator believes that the backing
                 * object should have. If this expectation is violated, the iterator
                 * has detected concurrent modification.
                 */
                private transient int expectedModCount = modCount;
                public boolean hasNext() {
                    return cursor.hasNext();
                } 
                public Object next() {
                    try {
                        Object v = (lastRet = (Entry) cursor.next()).getValue();
                        checkForComodification();
                        return v;
                    }
                    catch(IndexOutOfBoundsException e) {
                        checkForComodification();
                        throw (AssertionError) new AssertionError("cursor should already have thrown a NoSuchElementException").initCause(e);
                    }
                } 
                public boolean hasPrevious() {
                    throw new UnsupportedOperationException("not currently supported");
                } 
                public Object previous() {
                    throw new UnsupportedOperationException("not currently supported");
                } 

                public void set(Object o) {
                    if (!(o instanceof Arithmetic))
                        throw new IllegalArgumentException();
                    if (lastRet == null)
                        throw new IllegalStateException();
                    checkForComodification();
        
                    try {
                        lastRet.setValue((Arithmetic)o);
                        expectedModCount = modCount;
                    } catch(IndexOutOfBoundsException e) {
                        throw new ConcurrentModificationException();
                    }
                }

                // UnsupportedOperationException, categorically

                public int nextIndex() {
                    throw new UnsupportedOperationException("a polynomial does not have a one-dimensional index");
                }
                public int previousIndex() {
                    throw new UnsupportedOperationException("a polynomial does not have a one-dimensional index");
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException("adding a single element from a polynomial is impossible");
                } 
                public void remove() {
                    throw new UnsupportedOperationException("removing a single element from a polynomial is impossible");
                } 

                private final void checkForComodification() {
                    if (modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
    }

    public Object indexSet() {
        return CONSTANT_TERM;
    }

    public int degreeValue() {
        throw new UnsupportedOperationException("no degree on " + get(CONSTANT_TERM).getClass().getName() + "[" + CONSTANT_TERM.getClass().getName() + "]");
    }

    public Arithmetic get(Arithmetic i) {
        return (Arithmetic/*__*/) coefficients.get(i);
    }
        
    public void set(Arithmetic i, Arithmetic ci) {
        coefficients.put(i, ci);
    }

    public Arithmetic zero() {
        SparsePolynomial r = (SparsePolynomial)newInstance(CONSTANT_TERM);
        r.set(CONSTANT_TERM, get(CONSTANT_TERM).zero());
        return r;
    }

    public Arithmetic one() {
        SparsePolynomial r = (SparsePolynomial)newInstance(CONSTANT_TERM);
        r.set(CONSTANT_TERM, get(CONSTANT_TERM).one());
        return r;
    }

}
