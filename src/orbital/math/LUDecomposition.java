/**
 * @(#)LUDecomposition.java 0.9 2000/09/09 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.io.Serializable;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.util.Setops;
import orbital.logic.functor.Predicate;

import orbital.math.functional.Functions;
import orbital.logic.functor.Predicates;

/**
 * LUDecomposition class, decomposing A into P&middot;A = L&middot;U.
 *
 * @version 0.9, 2000/09/09
 * @author  Andr&eacute; Platzer
 * @stereotype &laquo;Structure&raquo;
 * @stereotype &laquo;Wrapper&raquo;
 * @invariant !isRegular() || getP().multiply(M).equals(getL().multiply(getU()))
 * @see #decompose(Matrix)
 * @see NumericalAlgorithms
 * @note this class is more or less just a workaround for returning multiple values.
 */
public class LUDecomposition/*<R implements Arithmetic>*/ implements Serializable {
    private static final long serialVersionUID = 4112378842817846198L;
    /**
     * decomposition data, contains lower triangular as well as upper triangular.
     * @serial
     */
    private Matrix/*<R>*/ A;
    /**
     * permutation matrix.
     * @serial
     */
    private Matrix/*<R>*/ P;
    /**
     * the sign of the permutation P.
     * True if P is an even permutation, false if P is odd.
     * The permutation is even if and only if an even number of pivotising swaps was done.
     * @serial
     */
    private boolean sign;
    /**
     * Gaussian LU-decomposition implementation.
     * Such that P.A = L.U
     * @pre A.isSquare()
     */
    protected LUDecomposition(Matrix/*<R>*/ A, Matrix/*<R>*/ P, boolean sign) {
	this.A = A;
	this.P = P;
	this.sign = sign;
    }
    /**
     * Gaussian LU-decomposition implementation.
     * Such that P.A = L.U
     * <p>Number of multiplications is 1/3*(n<sup>3</sup>-n)</p>
     * @pre M.isSquare()
     * @todo optimize
     */
    private LUDecomposition(Matrix/*<R>*/ M) {
	if (!M.isSquare())
	    throw new IllegalArgumentException("only square matrices can be LU-decomposed");
	// we restrict ourselves to AbstractMatrix because they have these nice swapRows methods which might possibly have an incredible speed
	AbstractMatrix/*<R>*/ A = (AbstractMatrix) M.clone();
	AbstractMatrix/*<R>*/ P = (AbstractMatrix/*<R>*/) (Matrix/*<R>*/) Values.IDENTITY(A.dimension());
	sign = true;
	for (int k = 0; k < A.dimension().width - 1; k++) {	   /* last column need not be eliminated, so -1 */

	    // column pivotising
	    {
		// @see orbital.util.Setops#argmax
		int pivot = k;
		for (int i = k + 1; i < A.dimension().height; i++)
		    if (((Real) A.get(i, k)).compareTo(A.get(pivot, k)) > 0)
			pivot = i;
		if (pivot != k) {
		    A.swapRows(k, pivot);
		    P.swapRows(k, pivot);
		    sign = !sign;
		}
	    }

	    Arithmetic apinv;
	    try {
		apinv = A.get(k, k).inverse();
	    }
	    catch(ArithmeticException x) {continue;}
	    for (int i = k + 1; i < A.dimension().height; i++)
		A.set(i, k, (Arithmetic/*>R<*/) A.get(i, k).multiply(apinv));

	    // partial multiplication (of upper triangular part, only)
	    for (int i = k + 1; i < A.dimension().height; i++)
		for (int j = k + 1; j < A.dimension().width; j++)
		    A.set(i, j, (Arithmetic/*>R<*/) A.get(i, j).subtract(A.get(i, k).multiply(A.get(k, j))));
	} 
	this.A = A;
	this.P = P;
	assert P.multiply(M).equals(getL().multiply(getU())) : "P.A = L.U";

	/*
	  Alternative implementation:
	  Matrix A = new Matrix(this);                    // could also contain lower triangular as well as upper triangular
	  Matrix L = IDENTITY(dimension().width);         // lower triangular matrix
	  Matrix U = new Matrix(A); 			            // upper triangular matrix
	  Matrix P = IDENTITY(dimension().width);			// permutation matrix
	  for (int k=0; k<dimension().width; k++) {
	  // column pivotising
	  int pivot = k;
	  for (int j = pivot+1; j<dimension().height; j++)
	  if (A.get(j,k)>A.get(pivot,k))
	  pivot = j;
	  A.swapRows(k, pivot);
	  P.swapRows(k, pivot);
	  U.swapRows(k, pivot);
	 
	  Matrix lk = new Matrix(dimension().height, 1);
	  for (int j=k+1; j<lk.dimension().height; j++)
	  lk.set(j, 0, A.get(j,k) / A.get(k,k));
	  Matrix t = lk.multiply(Vector.BASE(dimension().width, k).transpose());
	 
	  Matrix Lk = IDENTITY(dimension().width).subtract(t);
	  L = L.add(t);
	  U = Lk.multiply(U);
	  A = Lk.multiply(A);
	  }
	  return new Matrix[] {L,U,P};
	*/
    } 
    
    /**
     * Get the Gaussian LU-decomposition of a matrix.
     * Such that P.A = L.U
     * <p>Number of multiplications is 1/3*(n<sup>3</sup>-n)</p>
     * @pre M.isSquare()
     */
    public static /*<R implements Arithmetic>*/ LUDecomposition/*<R>*/ decompose(Matrix/*<R>*/ M) {
	return new LUDecomposition/*<R>*/(M);
    }

    /**
     * A is regular if and only if U is which depends upon whether there is a 0 on the diagonal.
     * @see Matrix#isRegular()
     */
    public boolean isRegular() throws ArithmeticException {
	for (int i = 0; i < A.dimension().height; i++)
	    if (A.get(i, i).norm().equals(Values.ZERO))
		return false;
	return true;
    }

    /**
     * Rank of the matrix.
     * i.e. the number of non-zero elements on the diagonal of U.
     * @see Matrix#linearRank()
     */
    public int linearRank() {
	return Setops.count(A.getDiagonal().iterator(), Functionals.compose(Functionals.bindSecond(Predicates.equal, Values.ZERO), Functions.norm) /*new Predicate() {
		public boolean apply(Object o) {return ((Arithmetic)o).norm() != 0;}
	}*/);
    }

    /**
     * The determinant of A.
     * <p>
     * det A = (-1)<sup>p</sup>*det U where p = sign P is the number of permutations in P.
     * Since det(P)*det(A) = det(P&middot;A) = det(L&middot;U) = det(L)*det(U) = det(U).</p>
     * @see Matrix#det()
     */
    public Arithmetic/*>R<*/ det() {
	Arithmetic/*>R<*/ detU = (Arithmetic/*>R<*/) Functionals.foldRight(Operations.times, Values.valueOf(1), A.getDiagonal().iterator());
	return sign ? detU : (Arithmetic/*>R<*/) detU.minus();
    }

    // extract triangular matrices from A
    /**
     * lower triangular matrix L with diagonal 1s.
     * <p>
     * Because of pivotising for numberical stability, this matrix only contains values
     * with an absolute &le;1.</p>
     */
    public Matrix/*<R>*/ getL() {
	Matrix/*<R>*/ L = Values.IDENTITY(A.dimension());
	for (int i = 0; i < A.dimension().height; i++)
	    for (int j = 0; j < i; j++)
		L.set(i, j, A.get(i, j));
	return L;
    }

    /**
     * upper triangular matrix U.
     */
    public Matrix/*<R>*/ getU() {
	Matrix/*<R>*/ U = Values.ZERO(A.dimension());
	for (int i = 0; i < A.dimension().height; i++)
	    for (int j = i; j < A.dimension().width; j++)
		U.set(i, j, A.get(i, j));
	return U;
    }

    /**
     * permutation matrix.
     */
    public Matrix/*<R>*/ getP() {
	return Values.constant(P);
    }

    /**
     * Solve LES A&middot;x=b.
     * <p>
     * Implementation solves
     * L.z = P.b per forward-substitution, and then solves
     * R.x = z per backward-substitution.
     * @return x such that A&middot;x = b.
     */
    public Vector/*<R>*/ solve(Vector/*<R>*/ b) {
	Vector/*<R>*/ c = P.multiply(b);
	Vector/*<R>*/ z = Values.getInstance(A.dimension().width);
	// forward-substitution of L.z = P.b = c
	for (int i = 0; i < A.dimension().height; i++) {
	    Arithmetic/*>R<*/ t = c.get(i);
	    for (int j = 0; j < i; j++)
		t = (Arithmetic/*>R<*/) t.subtract(A.get(i, j).multiply(z.get(j)));
	    // need not divide by l[i,i]=1
	    z.set(i, t);
	}

	Vector/*<R>*/ x = Values.getInstance(A.dimension().width);
	// backward-substitution of R.x = z
	for (int i = A.dimension().height - 1; i >= 0; i--) {
	    Arithmetic/*>R<*/ t = c.get(i);
	    for (int j = i + 1; j < A.dimension().width; j++)
		t = (Arithmetic/*>R<*/) t.subtract(A.get(i, j).multiply(x.get(j)));
	    x.set(i, (Arithmetic/*>R<*/) t.divide(A.get(i,i)));
	}
	return x;
    }
}
