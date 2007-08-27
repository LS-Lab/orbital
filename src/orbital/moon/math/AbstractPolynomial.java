/**
 * @(#)AbstractPolynomial.java 1.1 2002/08/21 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import orbital.math.functional.Function;
import java.util.ListIterator;
import java.util.Iterator;

import orbital.math.functional.Functionals;
import orbital.logic.functor.BinaryFunction;
import orbital.logic.functor.Predicate;
import orbital.math.functional.Operations;
import orbital.util.Setops;
import orbital.util.Pair;

/**
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo (working on it) this implementation could be split in two.
 *  One part that is general for every S,
 *  and one part that specially assumes S=<b>N</b><sup>n</sup>.
 */
abstract class AbstractPolynomial/*<R extends Arithmetic, S extends Arithmetic>*/
    extends AbstractProductArithmetic/*<R,S,Polynomial<R,S>>*/
    implements Polynomial/*<R,S>*/ {
    private static final long serialVersionUID = 4336092442446250306L;
        
    public AbstractPolynomial() {
    }
  
    public boolean equals(Object o) {
        if (o instanceof Polynomial) {
            final Polynomial/*>T<*/ b = (Polynomial) o;
            Setops.all(combinedIndices(this, b),
                       new Predicate() {
                           public boolean apply(Object o) {
                               Arithmetic/*>S<*/ i = (Arithmetic)o;
                               return get(i).equals(b.get(i));
                           }
                       });
        } 
        return false;
    } 

    public int hashCode() {
        //@xxx throw new UnsupportedOperationException("would require dimensions() to reduce to non-zero part");
        // the following is ok (though, perhaps, not very surjective), since 0 has hashCode 0 anyway
        int hash = 0;
        //@todo functional?
        for (java.util.Iterator i = iterator(this); i.hasNext(); ) {
            Object e = i.next();
            hash += e == null ? 0 : e.hashCode();
        } 
        return hash;
    }

    public boolean isZero() {
	Integer deg = degree();
        assert (deg.compareTo(deg.zero()) < 0) == equals(zero()) : "polynomial is zero iff its degree is negative: " + this;
	return deg.compareTo(deg.zero()) < 0;
    }

    public Integer degree() {
        return Values.getDefaultInstance().valueOf(degreeValue());
    }

    /**
     * Sets a value for the coefficient specified by index.
     * @preconditions i&isin;S
     * @throws UnsupportedOperationException if this polynomial is constant and does not allow modifications.
     * @todo move to Polynomial?
     */
    protected abstract void set(Arithmetic/*>S<*/ i, Arithmetic/*>R<*/ vi);

    protected Object/*>S<*/ productIndexSet(Arithmetic/*>Polynomial<R,S><*/ productObject) {
        return indexSet();
    }

    protected ListIterator/*<R>*/ iterator(Arithmetic/*>Polynomial<R,S><*/ productObject) {
        return ((Polynomial)productObject).iterator();
    }

    
    /**
     * Evaluate this polynomial at <var>a</var>.
     * Using the "Einsetzungshomomorphismus".
     * @return f(a) = f(X)|<sub>X=a</sub> =
     * @todo we could just as well generalize the argument and return type of R
     */
    public Object/*>R<*/ apply(Object/*>R<*/ a) {
        throw new UnsupportedOperationException("not yet implemented");
    }   

    public Function derive() {
        throw new UnsupportedOperationException("not yet implemented");
    } 

    public Function integrate() {
        throw new ArithmeticException(this + " is only (undefinitely) integrable with respect to a single variable");
    }

    public Real norm() {
        return degreeValue() < 0 ? Values.ZERO : Values.POSITIVE_INFINITY;
    }

    //@todo we should also support adding other functions (like in AbstractFunctor)?

    protected Arithmetic operatorImpl(final BinaryFunction op, Arithmetic bb) {
        final Polynomial b = (Polynomial)bb;
        if (!indexSet().equals(b.indexSet()))
            throw new IllegalArgumentException("a" + op + "b only defined for equal indexSet()");
        //@internal assuming the dimensions will grow as required
        Polynomial/*>T<*/ ret = (Polynomial) newInstance(indexSet());

        // component-wise
        ListIterator dst;
        Setops.copy(dst = ret.iterator(), Functionals.map(new orbital.logic.functor.Function() {
                public Object apply(Object o) {
                    //@todo could rewrite pure functional even more (by using pair copy function etc)
                    Arithmetic/*>S<*/ i = (Arithmetic)o;
                    return op.apply(get(i), b.get(i));
                }
            }, combinedIndices(this,b)));
        assert !dst.hasNext() : "equal indexSet() for iterator view implies equal structure of iterators";
        return ret;
    }
    
    public Arithmetic add(Arithmetic b) {
        return add((Polynomial)b);
    }
    public Polynomial/*<R,S>*/ add(Polynomial/*<R,S>*/ b) {
        return (Polynomial) operatorImpl(Operations.plus, b);
    }
        
    public Arithmetic subtract(Arithmetic b) throws ArithmeticException {
        return subtract((Polynomial)b);
    } 
    public Polynomial/*<R,S>*/ subtract(Polynomial/*<R,S>*/ b) {
        return (Polynomial) operatorImpl(Operations.subtract, b);
    }

    public Arithmetic multiply(Arithmetic b) {
        return multiply((Polynomial)b);
    }

    //@internal subclasses can optimize by far when using knowledge of the structure of S
    public Polynomial/*<R,S>*/ multiply(Polynomial/*<R,S>*/ bb) {
        Polynomial b = (Polynomial)bb;
        if (degreeValue() < 0)
            return this;
        else if (b.degreeValue() < 0)
            return b;
        //@internal assuming the dimensions will grow as required
        AbstractPolynomial/*<R,S>*/ ret = (AbstractPolynomial)newInstance(indexSet());
        setAllZero(ret);
        // ret = &sum;<sub>i&isin;indices(),j&isin;b.indices()</sub> a<sub>i</sub>b<sub>j</sub>X<sup>i * j</sup>
        // perform (very slow) multiplications "jeder mit jedem"
        for (Iterator index = Setops.cross(indices(), b.indices()); index.hasNext(); ) {
            Pair pair = (Pair) index.next();
            Arithmetic/*>S<*/ i = (Arithmetic/*>S<*/)pair.A;
            Arithmetic/*>S<*/ j = (Arithmetic/*>S<*/)pair.B;
            //@internal assuming (S,+) here
            Arithmetic/*>S<*/ Xij = i.add(j);
            // a<sub>i</sub>b<sub>j</sub>
            Arithmetic/*>R<*/ aibj = get(i).multiply(b.get(j));

            // + a<sub>i</sub>b<sub>j</sub>X<sup>i * j</sup>

            Arithmetic cXij = ret.get(Xij);
            ret.set(Xij, cXij != null ? cXij.add(aibj) : aibj);
        }
        return ret;
    }

    /**
     * Sets all coefficients of p to 0.
     */
    void setAllZero(Polynomial p) {
        final Arithmetic/*>R<*/ ZERO = get((Arithmetic/*>S<*/)indices().next()).zero();
        for (ListIterator i = p.iterator(); i.hasNext(); ) {
            i.next();
            i.set(ZERO);
        }
    }

    public Arithmetic divide(Arithmetic b) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("dividing polynomials is not generally defined");
    } 

    public Arithmetic inverse() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("inverse of polynomials is not generally defined");
    } 

    public String toString() {
        return ArithmeticFormat.getDefaultInstance().format(this);
    }

    /**
     * return an iterator over all indices occurring in either polynomial.
     */
    private static final Iterator combinedIndices(Polynomial f, Polynomial g) {
        return Setops.union(Setops.asList(f.indices()), Setops.asList(g.indices())).iterator();
    }
}
