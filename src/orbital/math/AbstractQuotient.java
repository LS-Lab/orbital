/*
 * @(#)AbstractQuotient.java 0.9 1998/11/27 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.logic.functor.Function;
import java.io.Serializable;

import orbital.util.Utility;

//@todo this implementation could be optimized to either delaying canonical representation, or performing eager canonicalization but relying on that invariant val==representative lateron
class AbstractQuotient/*<M implements Arithmetic>*/ implements Quotient/*<M>*/, Serializable {
    private static final long serialVersionUID = 5546024068517708793L;
    /**
     * The quotient operator applied for each calculation.
     * @invariant All operations (add, subtract, pow,...) will return an AbstractQuotient with the same modulus.
     * @serial
     */
    protected final Function/*<M,M>*/ quotientOperator;

    /**
     * The underlying M value representative.
     * @serial
     */
    protected Arithmetic/*>M<*/ value;
    
    public AbstractQuotient(Arithmetic/*>M<*/ val, Function/*<M,M>*/ mod) {
	this.quotientOperator = mod;
	this.value = val == null ? null : (Arithmetic) quotientOperator.apply(val);
	//this.value = val == null ? null : quotientOperator.apply(val);
	//this.value = val == null ? null : (Arithmetic) quotientOperator.apply(val);
	//this.value = val == null ? null : (Arithmetic/*>M<*/) quotientOperator.apply(val);
    }
    /**
     * Special remainder classes modulo m in euclidean rings.
     * @note assuming that M extends Euclidean
     */
    public AbstractQuotient(Euclidean/*>M<*/ val, Euclidean m) {
	this(val, new EuclideanModulo(m));
    }
    private static class EuclideanModulo/*<M implements Arithmetic>*/ implements Function/*<M,M>*/, Serializable {
	private static final long serialVersionUID = -8846695670222356251L;
	private final Euclidean m;
	public EuclideanModulo(Euclidean m) {
	    this.m = m;
	}
	public boolean equals(Object o) {
	    return (o instanceof EuclideanModulo) && Utility.equals(m, ((EuclideanModulo) o).m);
	}
	public int hashCode() {
	    return Utility.hashCode(m);
	}
	/**
	 * Get the modulus modulo whom we reduce values.
	 */
	public Euclidean getModulus() {
	    return m;
	}
	public Object/*>M<*/ apply(Object/*>M<*/ a) {
	    return (Object/*>M<*/) ((Euclidean) a).modulo(m);
	}
    }

    /**
     * Returns true if x is a Quotient whose value and quotientOperator
     * are equals to ours.
     */
    public boolean equals(Object x) {
	if (x instanceof Quotient) {
	    Quotient b = (Quotient) x;
	    return getQuotientOperator().equals(b.getQuotientOperator())
		&& representative().equals(b.representative());
	} else
	    return false;
    } 

    public boolean equals(Object x, Real tolerance) {
	if (x instanceof Quotient) {
	    Quotient b = (Quotient) x;
	    return getQuotientOperator().equals(b.getQuotientOperator())
		&& representative().equals(b.representative(), tolerance);
	} else
	    return false;
    } 

    public int hashCode() {
	return representative().hashCode() ^ getQuotientOperator().hashCode();
    } 

	
    // get/set Properties.

    public Function/*<M,M>*/ getQuotientOperator() {
	return quotientOperator;
    } 

    /**
     * @todo couldn't we somehow replace this by introduce extends M?
     */
    public Arithmetic/*>M<*/ representative() {
	return value;
    } 
	
    /**
     * Get the equivalence class v&#772; of v.
     */
    private final Quotient/*<M>*/ equivalenceClass(Arithmetic v) {
	return new AbstractQuotient((Arithmetic/*>M<*/) v, getQuotientOperator());
    }
    
    // Arithmetic implementation by passing to the quotient
    
    public Real norm() {
	return representative().norm();
    } 

    public Arithmetic zero() {
	//@todo we could just as well get the canonical representative of the equivalence class of representative().zero with getQuotientOperator() but this usually would be wasted brainpower
	return representative().zero();
    }

    public Arithmetic one() {
	return representative().one();
    }

    public Quotient/*<M>*/ add(Quotient/*<M>*/ b) throws ArithmeticException {
	if (!getQuotientOperator().equals(b.getQuotientOperator()))
	    throw new ArithmeticException("different modulus " + getQuotientOperator() + "!=" + b.getQuotientOperator());
	return equivalenceClass(representative().add(b.representative()));
    } 

    public Quotient/*<M>*/ subtract(Quotient/*<M>*/ b) {
	if (!getQuotientOperator().equals(b.getQuotientOperator()))
	    throw new ArithmeticException("different modulus " + getQuotientOperator() + "!=" + b.getQuotientOperator());
	return equivalenceClass(representative().subtract(b.representative()));
    } 

    public Quotient/*<M>*/ multiply(Quotient/*<M>*/ b) {
	if (!getQuotientOperator().equals(b.getQuotientOperator()))
	    throw new ArithmeticException("different modulus " + getQuotientOperator() + "!=" + b.getQuotientOperator());
	return equivalenceClass(representative().multiply(b.representative()));
    } 

    public Quotient/*<M>*/ divide(Quotient/*<M>*/ b) throws ArithmeticException {
	if (!getQuotientOperator().equals(b.getQuotientOperator()))
	    throw new ArithmeticException("different modulus " + getQuotientOperator() + "!=" + b.getQuotientOperator());
	//@xxx implement better if b only gets invertible modulo getQuotientOperator()
	return equivalenceClass(representative().divide(b.representative()));
    } 

    public Arithmetic inverse() throws ArithmeticException {
	Function/*<M,M>*/ quotientOperator = getQuotientOperator();
	if (quotientOperator instanceof EuclideanModulo) {
	    Euclidean m = ((EuclideanModulo) quotientOperator).getModulus();
	    assert representative() instanceof Euclidean : "euclidean modulo requires elements of the euclidean ring";
	    // 1 = gcd(a,m) = r*a + s*m &hArr; 1 = r*a (mod m) &hArr; r = a^-1 (mod m)
	    Euclidean r[] = MathUtilities.gcd(new Euclidean[] {(Euclidean) representative(), m});
	    if (r[r.length - 1].one().equals(r[r.length - 1]))
		return r[0];
	    else
		throw new ArithmeticException("(" + representative() + ", " + m + ") are not coprime");
	} else
	    //@xxx should still implement better (with ELBA-gcd on euclidean?) if we only get invertible modulo modulus m. Since 7 has no inverse in Z but in Z<sub>16</sub>
	    try {
		return equivalenceClass(representative().inverse());
	    }
	    catch (ArithmeticException unable) {throw new UnsupportedOperationException("can only invert quotients with non-invertible representatives in euclidean rings");}
	    catch (UnsupportedOperationException unable) {throw new UnsupportedOperationException("can only invert quotients with non-invertible representatives in euclidean rings");}
    } 

    public Quotient/*<M>*/ power(Quotient/*<M>*/ b) throws ArithmeticException {
	return equivalenceClass(representative().power(b));
    }

    // Arithmetic implementation

    //@todo would we need this ugly trick of Polynomial et al?
    public Arithmetic add(Arithmetic b) {
	return add((Quotient) b);
    } 
    public Arithmetic subtract(Arithmetic b) {
	return subtract((Quotient) b);
    } 
    public Arithmetic minus() {
	return equivalenceClass(representative().minus());
    } 
    public Arithmetic multiply(Arithmetic b) {
	return multiply((Quotient) b);
    } 
    public Arithmetic divide(Arithmetic b) {
	return divide((Quotient) b);
    } 
    public Arithmetic power(Arithmetic b) {
	return power((Quotient) b);
    } 

    public String toString() {
	return representative().toString();
    } 
}
