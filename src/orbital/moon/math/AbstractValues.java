/**
 * @(#)AbstractValues.java 1.1 2002-12-06 Andre Platzer
 * 
 * Copyright (c) 2000-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;
import orbital.math.Integer;

import java.awt.Dimension;
import orbital.logic.functor.Function;

import orbital.math.functional.MathFunctor;

import orbital.logic.functor.Predicate;
import orbital.logic.functor.Functor;
import java.util.Collection;

import java.util.List;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

import orbital.math.functional.Functions;
import java.util.ListIterator;
import java.util.Iterator;

/**
 * Abstract base class for arithmetic object value constructor factories, already
 * implementing basic methods.
 * 
 * @version 1.1, 2002-12-06
 * @author  Andr&eacute; Platzer
 */
abstract class AbstractValues extends Values {
    private orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer = null;

    // scalar value constructors - facade factory

    public Scalar valueOf(Number val) {
	if (val == null)
	    return null;
	else if (val instanceof Scalar)
	    return (Scalar) val;
	else if (val instanceof java.lang.Integer)
	    return valueOf((java.lang.Integer) val);
	else if (val instanceof java.lang.Long)
	    return valueOf((java.lang.Long) val);
	else if (val instanceof java.lang.Double)
	    return valueOf((java.lang.Double) val);
	else if (val instanceof java.lang.Float)
	    return valueOf((java.lang.Float) val);
	else if (val instanceof java.lang.Byte)
	    return valueOf((java.lang.Byte) val);
	else if (val instanceof java.lang.Short)
	    return valueOf((java.lang.Short) val);
	else if (val instanceof java.math.BigInteger)
	    //@internal cast because of stupid "reference to valueOf is ambiguous, both method valueOf(java.math.BigInteger) in orbital.math.ValueFactory and method valueOf(java.lang.Number) in orbital.moon.math.AbstractValues match"
	    return ((ValueFactory)this).valueOf((java.math.BigInteger) val);
	else if (val instanceof java.math.BigDecimal)
	    return ((ValueFactory)this).valueOf((java.math.BigDecimal) val);
	else
	    return narrow(valueOf(val.doubleValue()));
    }

    // instantiation

    protected AbstractValues() {}

    /**
     * Create a new value factory where the sub class already sets the equalizer.
     * @see #setEqualizer(orbital.logic.functor.Function)
     */
    protected AbstractValues(orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer) {
	this.equalizer = equalizer;
    }

    // scalar value constructors - facade factory
    // primitive type conversion methods

    // integer scalar value constructors - facade factory

    public Integer valueOf(java.lang.Integer val) {
	return valueOf(val.intValue());
    }
    public Integer valueOf(java.lang.Long val) {
	return valueOf(val.longValue());
    }
    public Integer valueOf(java.lang.Byte val) {
	return valueOf(val.byteValue());
    }
    public Integer valueOf(java.lang.Short val) {
	return valueOf(val.shortValue());
    }

    // real scalar value constructors - facade factory

    public Real valueOf(java.lang.Double val) {
	return valueOf(val.doubleValue());
    }
    public Real valueOf(java.lang.Float val) {
	return valueOf(val.floatValue());
    }

    // non-standard naming scalar value constructors

    public Rational rational(int p, int q) {
	return rational(valueOf(p), valueOf(q));
    } 
    public Rational rational(int p) {
	return rational(valueOf(p));
    } 

    // complex scalar values constructors

    public Complex complex(double a, double b) {
	return complex(valueOf(a), valueOf(b));
    } 
    public Complex complex(float a, float b) {
	return complex(valueOf(a), valueOf(b));
    }
    public Complex complex(int a, int b) {
	return complex(valueOf(a), valueOf(b));
    }
    public Complex complex(long a, long b) {
	return complex(valueOf(a), valueOf(b));
    }
    public Complex complex(double a) {
	return complex(valueOf(a));
    } 

    public Complex cartesian(double a, double b) {
	return cartesian(valueOf(a), valueOf(b));
    } 
    public Complex polar(double r, double phi) {
	return polar(valueOf(r), valueOf(phi));
    } 

    // vector constructors and conversion utilities

    public Vector ZERO(int n) {
	return CONST(n, Values.ZERO);
    } 

    public /*<R implements Arithmetic>*/ Vector/*<R>*/ constant(final Vector/*<R>*/ v) {
	return /*refine/delegate Vector*/ new AbstractVector/*<R>*/() {
		private static final long serialVersionUID = 4473448798599904941L;
		protected Vector/*<R>*/ newInstance(int d) {
		    assert false : "this method should never get called in this context of constant(...)";
		    //@todo there are still some methods missing for the above assertion to prove true. transpose, Tensor add(Tensor);
		    // so we simply pass it through until all these have been added
		    return ((AbstractVector)v).newInstance(d);
		}
		public int dimension() { return v.dimension(); }
		public Arithmetic/*>R<*/ get(int i) { return v.get(i); }
		public void set(int i, Arithmetic/*>R<*/ vi) { throw new UnsupportedOperationException(); }
		protected void set(Arithmetic/*>R<*/ vs[]) { throw new UnsupportedOperationException(); }
		public ListIterator iterator() { return Setops.unmodifiableListIterator((ListIterator)v.iterator()); }
		public boolean equals(Object b) { return v.equals(b); }
		public int hashCode() { return v.hashCode(); }
		public Object clone() { return v.clone(); }
		public Real norm() { return v.norm(); }
		public Real norm(double p) { return v.norm(p); }
		public Arithmetic add(Arithmetic b) { return v.add(b); }
		public Arithmetic subtract(Arithmetic b) { return v.subtract(b); }
		public Arithmetic minus() { return v.minus(); }
		public Arithmetic multiply(Arithmetic b) { return v.multiply(b); }
		public Arithmetic scale(Arithmetic b) { return v.scale(b); }
		public Arithmetic inverse() { return v.inverse(); }
		public Arithmetic divide(Arithmetic b) { return v.divide(b); }
		public Arithmetic power(Arithmetic b) { return v.power(b); }
		public Vector/*<R>*/ cross(Vector/*<R>*/ b) { return v.cross(b); }
		public Matrix/*<R>*/ transpose() { return v.transpose(); }
		public Vector/*<R>*/ insert(int i, Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ insert(int i, Vector/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ append(Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ append(Vector/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ remove(int i) { throw new UnsupportedOperationException(); }
		public Arithmetic/*>R<*/[] toArray() { return v.toArray(); }
		public String toString() { return v.toString(); }
	    };
    }

    // matrix constructors and conversion utilities

    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ ZERO(Dimension dim) {
	return ZERO(dim.height, dim.width);
    } 
    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ ZERO(int height, int width) {
	return (Matrix) ZERO(new int[] {height, width});
    }

    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ IDENTITY(Dimension dim) {
	return IDENTITY(dim.height, dim.width);
    } 

    //@todo turn into a true view?
    public /*<R implements Scalar>*/ Matrix/*<R>*/ DIAGONAL(Vector/*<R>*/ diagon) {
	Matrix/*<R>*/ diagonal = newInstance(new Dimension(diagon.dimension(), diagon.dimension()));
	for (int i = 0; i < diagonal.dimension().height; i++)
	    for (int j = 0; j < diagonal.dimension().width; j++)
		diagonal.set(i, j, i == j ? diagon.get(i) : (Arithmetic/*>R<*/) ZERO);
	return diagonal;
    } 

    public /*<R implements Arithmetic>*/ Matrix/*<R>*/ constant(final Matrix/*<R>*/ m) {
	return /*refine/delegate Matrix*/ new AbstractMatrix/*<R>*/() {
		private static final long serialVersionUID = 482711902153502751L;
		protected Matrix/*<R>*/ newInstance(Dimension d) {
		    assert false : "this method should never get called in this context of constant(...)";
		    //@todo there are still some methods missing for the above assertion to prove true. transpose, Tensor add(Tensor);
		    // so we simply pass it through until all these have been added
		    return ((AbstractMatrix)m).newInstance(d);
		}
		public Dimension dimension() { return m.dimension(); }
		public Arithmetic/*>R<*/ get(int i, int j) { return m.get(i,j); }
		public void set(int i, int j, Arithmetic/*>R<*/ v) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ getColumn(int j) { return m.getColumn(j); }
		public void setColumn(int j, Vector/*<R>*/ v) { throw new UnsupportedOperationException(); }
		public Vector/*<R>*/ getRow(int i) { return m.getRow(i); }
		public void setRow(int i, Vector/*<R>*/ v) { throw new UnsupportedOperationException(); }
		public void set(Arithmetic/*>R<*/ v[][]) { throw new UnsupportedOperationException(); }
		public ListIterator getColumns() { return Setops.unmodifiableListIterator((ListIterator)m.getColumns()); }
		public ListIterator getRows() { return Setops.unmodifiableListIterator((ListIterator)m.getRows()); }
		public ListIterator iterator() { return Setops.unmodifiableListIterator(m.iterator()); }
		public Vector/*<R>*/ getDiagonal() { return m.getDiagonal(); }
		public boolean isSquare() { return m.isSquare(); }
		public boolean isSymmetric() throws ArithmeticException{ return m.isSymmetric(); }
		public boolean isInvertible() throws ArithmeticException{ return m.isInvertible(); }
		public int isDefinite() throws ArithmeticException{ return m.isDefinite(); }
		public boolean equals(Object b) { return m.equals(b); }
		public int hashCode() { return m.hashCode(); }
		public Object clone() { return m.clone(); }
		public Matrix/*<R>*/ subMatrix(int r1, int r2, int c1, int c2) { return m.subMatrix(r1,r2, c1,c2); }
		public Real norm() { return m.norm(); }
		public Real norm(double p) { return m.norm(p); }
		public Arithmetic/*>R<*/ trace() { return m.trace(); }
		public Arithmetic/*>R<*/ det() { return m.det(); }
		public Arithmetic add(Arithmetic b) {return m.add(b);}
		public Arithmetic minus() {return m.minus();}
		public Arithmetic subtract(Arithmetic b) {return m.subtract(b);}
		public Arithmetic multiply(Arithmetic b) {return m.multiply(b);}
		public Arithmetic scale(Arithmetic b) { return m.scale(b); }
		public Arithmetic inverse() {return m.inverse();}
		public Arithmetic divide(Arithmetic b) {return m.divide(b);}
		public Arithmetic power(Arithmetic b) {return m.power(b);}
		public Matrix add(Matrix b) {return m.add(b);}
		public Matrix subtract(Matrix b) {return m.subtract(b);}
		public Matrix multiply(Matrix b) {return m.multiply(b);}
		public Matrix scale(Scalar b) { return m.scale(b); }
		public Vector multiply(Vector b) {return m.multiply(b);}
		public Matrix/*<R>*/ transpose() { return m.transpose(); }
		public Matrix/*<R>*/ pseudoInverse() { return m.pseudoInverse(); }
		public Matrix/*<R>*/ appendColumns(Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ appendRows(Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ insertColumns(int i, Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ insertRows(int i, Matrix/*<R>*/ b) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ removeColumn(int j) { throw new UnsupportedOperationException(); }
		public Matrix/*<R>*/ removeRow(int i) { throw new UnsupportedOperationException(); }
		public Arithmetic/*>R<*/[][] toArray() {return m.toArray(); }
		public String toString() { return m.toString(); }
	    };
    }

    // tensor constructors
    
    public /*<R implements Arithmetic>*/ Tensor/*<R>*/ ZERO(int[] dimensions) {
	Tensor zero = newInstance(dimensions);
	for (ListIterator i = zero.iterator(); i.hasNext(); ) {
	    i.next();
	    i.set(Values.ZERO);
	}
	return zero;
    }

    public /*<R implements Arithmetic>*/ Tensor/*<R>*/ constant(final Tensor/*<R>*/ t) {
	//@todo would we simplify these by providing a hierarchy of delegation things?
	// but then some tensors would no longer be AbstractTensors.
	//@todo so perhaps just identify multiple delegations to Tensor, or to Polynomial etc.
	return /*refine/delegate Tensor*/ new AbstractTensor/*<R>*/() {
		private static final long serialVersionUID = 3658988168257832220L;
		protected Tensor/*<R>*/ newInstance(int[] dim) {
		    assert false : "this method should never get called in this context of constant(...)";
		    //@todo there are still some methods missing for the above assertion to prove true. transpose, Tensor add(Tensor);
		    // so we simply pass it through until all these have been added
		    return ((AbstractTensor)t).newInstance(dim);
		}
		// Code for delegation of orbital.math.Normed methods to t

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Normed#norm()
		 */
		public Real norm()
		{
		    return t.norm();
		}
		// Code for delegation of orbital.math.Tensor methods to t

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#clone()
		 */
		public Object clone()
		{
		    return t.clone();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#add(Tensor)
		 */
		public Tensor add(Tensor param1)
		{
		    return t.add(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#get(int[])
		 */
		public Arithmetic get(int[] param1)
		{
		    return t.get(param1);
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#iterator()
		 */
		public ListIterator iterator()
		{
		    return t.iterator();
		}

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Tensor#set(int[], Arithmetic)
		 */
		public void set(int[] param1, Arithmetic param2) throws UnsupportedOperationException
		{
		    throw new UnsupportedOperationException();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#dimensions()
		 */
		public int[] dimensions()
		{
		    return t.dimensions();
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Tensor#rank()
		 */
		public int rank()
		{
		    return t.rank();
		}

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#subTensor(int[], int[])
		 */
		public Tensor subTensor(int[] param1, int[] param2)
		{
		    return t.subTensor(param1, param2);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#subtract(Tensor)
		 */
		public Tensor subtract(Tensor param1)
		{
		    return t.subtract(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#multiply(Tensor)
		 */
		public Tensor multiply(Tensor param1)
		{
		    return t.multiply(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @see orbital.math.Tensor#tensor(Tensor)
		 */
		public Tensor tensor(Tensor param1)
		{
		    return t.tensor(param1);
		}
		// Code for delegation of orbital.math.Arithmetic methods to t

		/**
		 *
		 * @param param1 <description>
		 * @param param2 <description>
		 * @return <description>
		 * @see orbital.math.Arithmetic#equals(Object, Real)
		 */
		public boolean equals(Object param1, Real param2)
		{
		    return t.equals(param1, param2);
		}

		/**
		 *
		 * @return <description>
		 * @see orbital.math.Arithmetic#toString()
		 */
		public String toString()
		{
		    return t.toString();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#add(Arithmetic)
		 */
		public Arithmetic add(Arithmetic param1) throws ArithmeticException
		{
		    return t.add(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#subtract(Arithmetic)
		 */
		public Arithmetic subtract(Arithmetic param1) throws ArithmeticException
		{
		    return t.subtract(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#multiply(Arithmetic)
		 */
		public Arithmetic multiply(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.multiply(param1);
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#zero()
		 */
		public Arithmetic zero() throws ArithmeticException
		{
		    return t.zero();
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#one()
		 */
		public Arithmetic one() throws UnsupportedOperationException
		{
		    return t.one();
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @see orbital.math.Arithmetic#minus()
		 */
		public Arithmetic minus() throws ArithmeticException
		{
		    return t.minus();
		}

		/**
		 *
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#inverse()
		 */
		public Arithmetic inverse() throws ArithmeticException, UnsupportedOperationException
		{
		    return t.inverse();
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#divide(Arithmetic)
		 */
		public Arithmetic divide(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.divide(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#scale(Arithmetic)
		 */
		public Arithmetic scale(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.scale(param1);
		}

		/**
		 *
		 * @param param1 <description>
		 * @return <description>
		 * @exception java.lang.ArithmeticException <description>
		 * @exception java.lang.UnsupportedOperationException <description>
		 * @see orbital.math.Arithmetic#power(Arithmetic)
		 */
		public Arithmetic power(Arithmetic param1) throws ArithmeticException, UnsupportedOperationException
		{
		    return t.power(param1);
		}

	    };
    }

    // polynomial constructors and utilities

    // @todo implementation could be generalized to non-AbstractMultivariatePolynomials.
    public final Polynomial/*<R,S>*/ MONOMIAL(Arithmetic/*>R<*/ coefficient, Arithmetic/*>S<*/ exponent) {
	return MONOMIAL(coefficient, ArithmeticMultivariatePolynomial.convertIndex(exponent));
    }
    public final Polynomial/*<R implements Scalar,S>*/ MONOMIAL(Arithmetic/*>S<*/ exponent) {
	return MONOMIAL(ONE, exponent);
    }
    public final Polynomial/*<R implements Scalar>*/ MONOMIAL(int[] exponents) {
	return MONOMIAL(ONE, exponents);
    }

    // univariate polynomial constructors and utilities

    public UnivariatePolynomial/*<Real>*/ polynomial(double[] coefficients) {
	return (UnivariatePolynomial) polynomial((Object)coefficients);
    }
    public UnivariatePolynomial/*<Integer>*/ polynomial(int[] coefficients) {
	return (UnivariatePolynomial) polynomial((Object)coefficients);
    }

    public /*<R implements Arithmetic>*/ Vector/*<R>*/ asVector(UnivariatePolynomial/*<R>*/ p) {
    	return (Vector) asTensor(p);
    }

    // quotient constructor synonyms

    public /*<M implements Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, UnivariatePolynomial m) {
	return quotient(a, (Euclidean)m);
    }
    public /*<M implements Euclidean>*/ Quotient/*<M>*/ quotient(Euclidean/*>M<*/ a, Function/*<M,M>*/ mod) {
	return quotient((Arithmetic)a, mod);
    }
    public Quotient quotient(Arithmetic a, Polynomial m) {
	if ((a instanceof Euclidean) && (m instanceof Euclidean))
	    return quotient((Euclidean)a, (Euclidean)m);
	else
	    throw new ClassCastException(m.getClass() + " most probably is no quotient operator.\nConsider using Values.quotient(Polynomial,Set,Comparator) instead.\nIf the instance " + m + " truely is a quotient operator\nand you really know what you are doing, then call Values.quotient(Arithmetic,Function) instead.");
    }

    public Quotient/*<Integer>*/ quotient(int a, int m) {
	return quotient(valueOf(a), valueOf(m));
    }

    // conversion methods

    public /*<R implements ListIterator,  Arithmetic>*/ Vector/*<R>*/ asVector(final Matrix/*<R>*/ m) {
	return /*refine/delegate Vector*/ new AbstractVector/*<R>*/() {
		private static final long serialVersionUID = 7697252236109892826L;
		protected Vector/*<R>*/ newInstance(int dim) {
		    return m instanceof RMatrix
			? (Vector) new RVector(dim)
			: (Vector) new ArithmeticVector/*<R>*/(dim);
		} 
		public int dimension() {
		    Dimension dim = m.dimension();
		    return dim.height * dim.width;
		}
		/**
		 * Return the corresponding row index of the given vector index.
		 */
		private int rowOf(int i) {
		    return i / m.dimension().width;
		}
		/**
		 * Return the corresponding column index of the given vector index.
		 */
		private int columnOf(int i) {
		    return i % m.dimension().width;
		}
		public Arithmetic/*>R<*/ get(int i) { return m.get(rowOf(i),columnOf(i)); }
		public void set(int i, Arithmetic/*>R<*/ v) {m.set(rowOf(i),columnOf(i),v);}
		protected void set(Arithmetic/*>R<*/ v[]) { throw new UnsupportedOperationException("not currently supported"); }
		public ListIterator iterator() { return m.iterator(); }
		public Object clone() { throw new UnsupportedOperationException("@xxx dunno"); }
		public Vector/*<R>*/ insert(int i, Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ insert(int i, Vector/*<R>*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ append(Arithmetic/*>R<*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ append(Vector/*<R>*/ b) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
		public Vector/*<R>*/ remove(int i) { throw new UnsupportedOperationException("structurally unmodifiable view"); }
	    };
    }
    public /*<R implements ListIterator,  Arithmetic>*/ Vector/*<R>*/ asVector(final Tensor/*<R>*/ t) {
	if (t instanceof Vector)
	    return (Vector)t;
	else if (t instanceof Matrix)
	    return asVector((Matrix)t);
	else
	    throw new UnsupportedOperationException("not yet implemented");
    }


    // arithmetic widening equalizer
	
    public final orbital.logic.functor.Function/*<Object[],Object[]>*/ getEqualizer() {
	return equalizer;
    } 

    protected final void initialSetEqualizer(orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer) {
	this.equalizer = equalizer;
    } 
    public final void setEqualizer(orbital.logic.functor.Function/*<Object[],Object[]>*/ equalizer) throws SecurityException {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new java.util.PropertyPermission(ValueFactory.class.getName() + ".equalizer", "write"));
	} 
	initialSetEqualizer(equalizer);
    } 
}
