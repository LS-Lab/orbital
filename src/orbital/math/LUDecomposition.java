/**
 * @(#)LUDecomposition.java 0.9 2000/09/09 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.moon.math.AbstractMatrix;

import java.io.Serializable;

import orbital.math.functional.Functionals;
import orbital.math.functional.Operations;
import orbital.util.Setops;
import orbital.logic.functor.Predicate;

import orbital.math.functional.Functions;
import orbital.logic.functor.Predicates;

/**
 * LUDecomposition class, decomposing <span class="matrix">A</span>
 * into <span class="matrix">P</span>&#8729;<span class="matrix">A</span> = <span class="matrix">L</span>&#8729;<span class="matrix">U</span>.
 * Solves linear equation systems.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @stereotype Structure
 * @stereotype Wrapper
 * @invariants !isInvertible() || getP().multiply(A).equals(getL().multiply(getU()))
 * @see #decompose(Matrix)
 * @see NumericalAlgorithms
 * @note this class is more or less just a workaround for returning multiple values.
 */
public final class LUDecomposition/*<R extends Arithmetic>*/ implements Serializable {
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
     * the sign of the permutation <span class="matrix">P</span>.
     * True if <span class="matrix">P</span> is an even permutation, false if <span class="matrix">P</span> is odd.
     * The permutation is even if and only if an even number of pivotising swaps was done.
     * @serial
     */
    private boolean sign;
    /**
     * Gaussian LU-decomposition implementation.
     * Such that <span class="matrix">P</span>.<span class="matrix">A</span> = <span class="matrix">L</span>.<span class="matrix">U</span>
     * @preconditions <span class="matrix">A</span>.isSquare()
     */
    protected LUDecomposition(Matrix/*<R>*/ A, Matrix/*<R>*/ P, boolean sign) {
        this.A = A;
        this.P = P;
        this.sign = sign;
    }
    /**
     * Gaussian LU-decomposition implementation.
     * Such that <span class="matrix">P</span>.<span class="matrix">A</span> = <span class="matrix">L</span>.<span class="matrix">U</span>
     * <p>Number of multiplications is 1/3*(n<sup>3</sup>-n)</p>
     * @preconditions M.isSquare()
     * @todo optimize
     */
    private LUDecomposition(Matrix/*<R>*/ M) {
        if (!M.isSquare())
            throw new IllegalArgumentException("only square matrices can be LU-decomposed");
        // we restrict ourselves to AbstractMatrix because they have these nice swapRows methods which might possibly have an incredible speed
        AbstractMatrix/*<R>*/ A = (AbstractMatrix) M.clone();
        AbstractMatrix/*<R>*/ P = (AbstractMatrix/*<R>*/) (Matrix/*<R>*/) Values.getDefaultInstance().IDENTITY(A.dimension());
        sign = true;
        for (int k = 0; k < A.dimension().width - 1; k++) {        /* last column need not be eliminated, so -1 */

            // column pivotising
            {
                // @see orbital.util.Setops#argmax
                int pivot = k;
                for (int i = k + 1; i < A.dimension().height; i++)
                    if ((A.get(i, k).norm()).compareTo(A.get(pivot, k).norm()) > 0)
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
        assert P.multiply(M).equals(getL().multiply(getU()), Values.getDefaultInstance().valueOf(MathUtilities.getDefaultTolerance())) : "P.A = L.U: " + P + "*" + M + "=" + P.multiply(M) + "  =  " + getL().multiply(getU()) + "=" + getL() + "*" + getU();

        /*
          Alternative implementation:
          Matrix A = new Matrix(this);                    // could also contain lower triangular as well as upper triangular
          Matrix L = IDENTITY(dimension().width);         // lower triangular matrix
          Matrix U = new Matrix(A);                                 // upper triangular matrix
          Matrix P = IDENTITY(dimension().width);                       // permutation matrix
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
     * Such that <span class="matrix">P</span>&#8729;<span class="matrix">A</span> = <span class="matrix">L</span>&#8729;<span class="matrix">U</span>
     * <p>Number of multiplications is 1/3*(n<sup>3</sup>-n)</p>
     * @preconditions M.isSquare()
     */
    public static /*<R extends Arithmetic>*/ LUDecomposition/*<R>*/ decompose(Matrix/*<R>*/ M) {
        return new LUDecomposition/*<R>*/(M);
    }

    /**
     * <span class="matrix">A</span> is regular if and only if <span class="matrix">U</span> is which depends upon whether there is a 0 on the diagonal.
     * @see Matrix#isInvertible()
     */
    public boolean isInvertible() throws ArithmeticException {
        for (int i = 0; i < A.dimension().height; i++)
            if (A.get(i, i).isZero())
                return false;
        return true;
    }
    /**
     * @deprecated Since Orbital1.1 use {@link #isInvertible()} instead.
     */
    public boolean isRegular() throws ArithmeticException {
        return isInvertible();
    }

    /**
     * Rank of the matrix.
     * i.e. the number of non-zero elements on the diagonal of <span class="matrix">U</span>.
     * @see Matrix#linearRank()
     */
    public int linearRank() {
        return Setops.count(A.getDiagonal().iterator(), Functionals.compose(Functionals.bindSecond(Predicates.unequal, Values.getDefault().ZERO()), Functions.norm));
    }

    /**
     * The determinant of <span class="matrix">A</span>.
     * <p>
     * det <span class="matrix">A</span> = (-1)<sup>p</sup>*det <span class="matrix">U</span> where p = sign <span class="matrix">P</span> is the number of permutations in <span class="matrix">P</span>.
     * Since det(<span class="matrix">P</span>)*det(<span class="matrix">A</span>) = det(<span class="matrix">P</span>&#8729;<span class="matrix">A</span>) = det(<span class="matrix">L</span>&#8729;<span class="matrix">U</span>) = det(<span class="matrix">L</span>)*det(<span class="matrix">U</span>) = det(<span class="matrix">U</span>).</p>
     * @see Matrix#det()
     */
    public Arithmetic/*>R<*/ det() {
        Arithmetic/*>R<*/ detU = (Arithmetic/*>R<*/) Functionals.foldRight(Operations.times, Values.getDefault().ONE(), A.getDiagonal().iterator());
        return sign ? detU : (Arithmetic/*>R<*/) detU.minus();
    }

    // extract triangular matrices from <span class="matrix">A</span>
    /**
     * lower triangular matrix <span class="matrix">L</span> with diagonal 1s.
     * <p>
     * Because of pivotising for numberical stability, this matrix only contains values
     * with an absolute &le;1.</p>
     */
    public Matrix/*<R>*/ getL() {
        Matrix/*<R>*/ L = Values.getDefaultInstance().IDENTITY(A.dimension());
        for (int i = 0; i < A.dimension().height; i++)
            for (int j = 0; j < i; j++)
                L.set(i, j, A.get(i, j));
        return L;
    }

    /**
     * upper triangular matrix <span class="matrix">U</span>.
     */
    public Matrix/*<R>*/ getU() {
        Matrix/*<R>*/ U = Values.getDefaultInstance().ZERO(A.dimension());
        for (int i = 0; i < A.dimension().height; i++)
            for (int j = i; j < A.dimension().width; j++)
                U.set(i, j, A.get(i, j));
        return U;
    }

    /**
     * permutation matrix.
     */
    public Matrix/*<R>*/ getP() {
        return Values.getDefaultInstance().constant(P);
    }

    /**
     * Solve linear equation system <span class="matrix">A</span>&#8729;<span class="vector>x</span>=<span class="vector>b</span>.
     * <p>
     * Implementation solves
     * <span class="matrix">L</span>&#8729;<span class="vector">z</span> = <span class="matrix">P</span>&#8729;<span class="vector">b</span>
     * per forward-substitution, and then solves
     * <span class="matrix">R</span>&#8729;<span class="vector">x</span> = <span class="vector">z</span> per backward-substitution.
     * @return x such that <span class="matrix">A</span>&#8729;<span class="vector">x</span> = <span class="vector">b</span>.
     */
    public Vector/*<R>*/ solve(Vector/*<R>*/ b) {
        Vector/*<R>*/ c = P.multiply(b);
        Vector/*<R>*/ z = Values.getDefaultInstance().newInstance(A.dimension().width);
        // forward-substitution of L.z = P.b = c
        for (int i = 0; i < A.dimension().height; i++) {
            Arithmetic/*>R<*/ t = c.get(i);
            for (int j = 0; j < i; j++)
                t = (Arithmetic/*>R<*/) t.subtract(A.get(i, j).multiply(z.get(j)));
            // need not divide by l[i,i]=1
            z.set(i, t);
        }

        Vector/*<R>*/ x = Values.getDefaultInstance().newInstance(A.dimension().width);
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
