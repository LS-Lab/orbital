/**
 * @(#)NumericalAlgorithms.java 1.0 2000/06/10 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.moon.math.AbstractMatrix;

import orbital.math.functional.Function;
import orbital.math.functional.Functionals;

import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

import orbital.moon.math.functional.AbstractFunctor;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import orbital.util.Utility;

/**
 * This class contains numerical algorithms.
 * <p>
 * The implementation algorithms for the methods of this class are free to vary
 * in order to increase performance or accuracy. Nevertheless, the signatures
 * and assertions must be fulfilled by every implementation.
 * </p>
 * 
 * @stereotype Utilities
 * @stereotype Module
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.math.MathUtilities
 * @see LUDecomposition
 * @todo introduce GMRES-Algorithm
 * @todo introduce Newton-Iteration or more general fixed point iteration (see Functionals)
 * @todo introduce a row Pivot with a threshold of &asymp;1.5
 * @todo introduce GaussJordanAlgorithm
 */
public final class NumericalAlgorithms {
    private static final Logger logger = Logger.getLogger(NumericalAlgorithms.class.getName());
    /**
     * prevent instantiation - module class
     */
    private NumericalAlgorithms() {}

    // interpolation type enum values

    /**
     * complete spline interpolation with border conditions s'(a)=f'(a) and s'(b)=f'(b).
     */
    public static final int COMPLETE_SPLINE_INTERPOLATION = 1;

    /**
     * natural spline interpolation with natural decay conditions s''(a)=0 and s''(b)=0.
     */
    public static final int NATURAL_SPLINE_INTERPOLATION = 2;

    /**
     * periodical spline interpolation with periodicity conditions s'(a)=s'(b) and s''(a)=s''(b).
     * The length of the period is b-a.
     */
    public static final int PERIODICAL_SPLINE_INTERPOLATION = 3;


    // utility methods calculating numerical "Kenngrößen"
        
    /**
     * Checks whether a matrix is diagonally dominant.
     * @return whether &forall;i&isin;{0,...,n-1} &sum;<sub>k&isin;{0,...,n-1}\{i}</sub> |a<sub>k,i</sub>| &le; |a<sub>i,i</sub>|.
     *  Or perhaps passing to absolutes |.| instead.
     * @preconditions A.isSquare()
     * @postconditions RES == &forall;i&isin;{0,...,A.dimension().width-1} &sum;<sub>k&isin;{0,...,A.dimension().height-1}\{i}</sub> Math.abs(a<sub>k,i</sub>) &le; Math.abs(a<sub>i,i</sub>)
     */
    //@todo introduce boolean isDiagonallyDominant(Matrix A)


    // solving linear equation systems (LES)

    /**
     * Cholesky-decomposition of positive definite matrices implementation.
     * Such that A = L.L<sup>T</sup>.
     * <p>#Multiplications = 1/6*n<sup>3</sup>, #Square Roots = n</p>
     * <p>
     * The implementation is numerically stable.
     * </p>
     * @preconditions A.isPositiveDefinite()
     * @postconditions L*L^T == A
     * @todo test
     * @todo optimize
     */
    public static Matrix decomposeCholesky(Matrix A) throws ArithmeticException {
        Utility.pre(A.isSymmetric() && A.isDefinite() > 0, "Only symmetric and positive-definite matrices can be Cholesky-decomposed");
        // Utility.pre(A.isPositiveDefinite(), "Only positive-definite matrices can be Cholesky-decomposed");
        // we restrict ourselves to AbstractMatrix because they have these fast getDoubleValue methods
        final Values vf = Values.getDefaultInstance();
        AbstractMatrix A_ = (AbstractMatrix) A;
        AbstractMatrix L = (AbstractMatrix) vf.ZERO(A.dimension().width, A.dimension().width);
        for (int k = 0; k < A_.dimension().width; k++) {
            double t = 0;
            for (int j = 0; j < k; j++) {
                double L_kj = L.getDoubleValue(k, j);
                t += L_kj * L_kj;
            } 
            L.set(k, k, vf.valueOf(Math.sqrt(A_.getDoubleValue(k, k) - t)));

            for (int i = k + 1; i < A_.dimension().height; i++) {
                t = 0;
                for (int j = 0; j < k; j++)
                    t += L.getDoubleValue(i, j) * L.getDoubleValue(k, j);
                L.set(i, k, vf.valueOf((A_.getDoubleValue(i, k) - t) / L.getDoubleValue(k, k)));
            } 
        } 
        assert L.multiply(L.transpose()).equals(A_) : "L.L^T = A";
        return L;
    } 

    /**
     * cg-algorithm for solving A&#8729;x=b iteratively starting with x0.
     * Conjugate gradients algorithm.
     * @preconditions A.isPositiveDefinite() && b.dimension()==A.dimension().width && x0.dimension() == A.dimension().width
     * @postconditions A.multiply(x) == b
     * @return the solution vector x solving Ax=b.
     * @todo test sometime
     */
    public static Vector cgSolve(Matrix A, Vector x0, Vector b) {
        assert A.isSquare() : "Only square matrices can be solved with cg-algorithm";
        // assert A.isPositiveDefinite(),"Only positive-definite matrices can be solved with cg-algorithm");
        assert b.dimension() == A.dimension().width : "compatible result vector dimension required";
        assert x0.dimension() == A.dimension().width : "compatible starting vector dimension required";
        Vector p;                                                                 // A-orthogonal basis vectors
        Vector x = (Vector) x0.clone();                   // trial solution
        Vector r = b.subtract(A.multiply(x0));    // residual
        p = (Vector) r.clone();
        final Vector zero = Values.getDefaultInstance().ZERO(r.dimension());
        final Real tolerance = Values.getDefaultInstance().valueOf(MathUtilities.getDefaultTolerance());
        for (int m = 0; m < A.dimension().width && !r.equals(zero, tolerance); m++) {
            Vector o_r = (Vector) r.clone();    // old residual
            Vector a = A.multiply(p);
            Scalar alpha = (Scalar/*__*/) ((Scalar/*__*/)Functions.square.apply(r.norm(2))).divide(a.multiply(p));
            x = x.add(p.scale(alpha));
            r = r.subtract(a.scale(alpha));
            Scalar beta = (Scalar/*__*/) ((Scalar/*__*/)Functions.square.apply(r.norm(2))).divide((Scalar/*__*/)Functions.square.apply(o_r.norm(2)));
            p = r.add(p.scale(beta));
            logger.log(Level.FINE, "cg-iteration {0}, a={1}\nalpha={2}\nx={3}\nr={4}\nbeta={5}\np={6}", new Object[] {new java.lang.Integer(m), a, alpha, x, r, beta, p});
        } 

        assert MathUtilities.equalsCa(A.multiply(x), b) : "A.x == b";
        return x;
    } 


    // Interpolation and approximation

    /**
     * Polynomial interpolation.
     * <p>
     * Currently implemented as polynomial interpolation with Neville.
     * </p>
     * @param A the matrix with supporting nodes.
     *  These are row vectors (x,y) of width 2.
     * @preconditions A.dimension().width == 2
     * @see UnivariatePolynomial
     */
    public static Function polynomialInterpolation(Matrix A) {
        Utility.pre(A.dimension().width == 2, "supporting nodes matrix 2 x m");
        return new PolynomialInterpolationFunction(MathUtilities.toDoubleArray(A.getColumn(0)), MathUtilities.toDoubleArray(A.getColumn(1)));
    } 
    private static class PolynomialInterpolationFunction extends AbstractFunctor implements Function {

        /**
         * supporting nodes' x-values
         */
        private double[]   t;

        /**
         * supporting nodes' f(x)=y-values
         */
        private double[]   f;

        /**
         * the polynomial values of sub polynoms.
         * This matrix will be a lower triangular matrix.
         * first column p[i][0] will remain f[i] not depending on x.
         */
        private double[][] p;
        public PolynomialInterpolationFunction(double[] t, double[] f) {
            assert t.length == f.length : "same number of supporting x-values as corresponding y-values";
            this.t = t;
            this.f = f;
            this.p = new double[t.length][t.length];
            for (int i = 0; i < f.length; i++)
                p[i][0] = f[i];
        }

        /**
         * performs Neville recursion, which follows dynamic programming.
         */
        public Object apply(Object arg) {
            double x = ((Number) arg).doubleValue();
            for (int k = 1; k < t.length; k++)
                for (int i = k; i < t.length; i++)
                    p[i][k] = p[i][k - 1] + (p[i][k - 1] - p[i - 1][k - 1]) * (x - t[i]) / (t[i] - t[i - k]);
            return Values.getDefaultInstance().valueOf(p[t.length - 1][t.length - 1]);
        } 
        public Function derive() throws ArithmeticException {
            // coefficients already constructed as a sum in p after each call to apply
            throw new UnsupportedOperationException("derivative of polynomial interpolation not yet implemented");
        } 
        public Function integrate() throws ArithmeticException {
            throw new UnsupportedOperationException("integral of polynomial interpolation not yet implemented");
        } 
        public String toString() {
            return "P(f|" + MathUtilities.format(t) + ")";
        } 
    }

    /**
     * Spline interpolation.
     * For a grid &Delta;={t<sub>0</sub>,...,t<sub>l+1</sub>}&isin;Part([a,b]),
     * the space of splines of grade k-1 is S<sub>k,&Delta;</sub>&sub;C<sup>k-2</sup>([a,b],<b>R</b>).
     * Where &forall;s&isin;S<sub>k,&Delta;</sub> s|<sub>[t<sub>i</sub>,t<sub>i+1</sub>]</sub>&isin;<b>R</b><sub>k-1</sub>[x].
     * <p>
     * This method is currently only implemented for cubical spline with k=4.</p>
     * @param k the order of splines desired. k-1 is the grade of the piecewise interpolating polynoms.
     * @param A the matrix with supporting nodes (the first column is the grid &Delta; of x-values, second column are y-values).
     *  A must be <b>ordered</b> by ascending x-values.
     * @param interpolationType specifies which type of interpolation to do (see config).
     * <ul>
     *  <li>{@link #NATURAL_SPLINE_INTERPOLATION} - natural spline interpolation</li>
     *  <li>{@link #PERIODICAL_SPLINE_INTERPOLATION} - periodical spline interpolation</li>
     * </ul>
     * @preconditions A is ordered by ascending x-values. &forall;i A.get(i, 0) < A.get(i+1, 0)
     * @return the interpolating spline s of grade k-1 that is minimally crooked (&kappa; globally minimized).
     *  &kappa;(t) = s''(t) / <big>(</big>1 + s'(t)<sup>2</sup><big>)</big><sup>3/2</sup>.
     * @see #NATURAL_SPLINE_INTERPOLATION
     * @see #PERIODICAL_SPLINE_INTERPOLATION
     * @see #splineInterpolation(int,Matrix,int,Real,Real)
     * @see #COMPLETE_SPLINE_INTERPOLATION
     */
    public static Function splineInterpolation(int k, Matrix A, int interpolationType) {
        if (interpolationType == COMPLETE_SPLINE_INTERPOLATION)
            throw new IllegalArgumentException("use NumericalAlgorithms.splineInterpolation(int,Matrix,int,Real,Real) for complete spline interpolation");
        return splineInterpolation(k, A, interpolationType, null);
    }
    /**
     * Spline interpolation.
     * For a grid &Delta;={t<sub>0</sub>,...,t<sub>l+1</sub>}&isin;Part([a,b]),
     * the space of splines of grade k-1 is S<sub>k,&Delta;</sub>&sub;C<sup>k-2</sup>([a,b],<b>R</b>).
     * Where &forall;s&isin;S<sub>k,&Delta;</sub> s|<sub>[t<sub>i</sub>,t<sub>i+1</sub>]</sub>&isin;<b>R</b><sub>k-1</sub>[x].
     * <p>
     * This method is currently only implemented for cubical spline with k=4.</p>
     * @param k the order of splines desired. k-1 is the grade of the piecewise interpolating polynoms.
     * @param A the matrix with supporting nodes (the first column is the grid &Delta; of x-values, second column are y-values).
     *  A must be <b>ordered</b> by ascending x-values.
     * @param interpolationType specifies which type of interpolation to do
     * <ul>
     *  <li>{@link #COMPLETE_SPLINE_INTERPOLATION} - complete spline interpolation.</li>
     * </ul>
     * @param fp_a the value f'(a) of the derivative of f at a.
     * @param fp_b the value f'(b) of the derivative of f at b.
     * @preconditions A is ordered by ascending x-values. &forall;i A.get(i, 0) < A.get(i+1, 0)
     * @return the interpolating spline s of grade k-1 that is minimally crooked (&kappa; globally minimized).
     *  &kappa;(t) = s''(t) / <big>(</big>1 + s'(t)<sup>2</sup><big>)</big><sup>3/2</sup>.
     * @see #COMPLETE_SPLINE_INTERPOLATION
     * @see #splineInterpolation(int,Matrix,int)
     * @see #NATURAL_SPLINE_INTERPOLATION
     * @see #PERIODICAL_SPLINE_INTERPOLATION
     */
    public static Function splineInterpolation(int k, Matrix A, int interpolationType, Real fp_a, Real fp_b) {
        if (interpolationType != COMPLETE_SPLINE_INTERPOLATION)
            throw new IllegalArgumentException("use NumericalAlgorithms.splineInterpolation(int,Matrix,int) for natural and periodical spline interpolation");
        return splineInterpolation(k, A, interpolationType, new Number[] {new Double(fp_a.doubleValue()), new Double(fp_b.doubleValue())});
    }
    /**
     * Implementation method.
     * @deprecated Use {@link #splineInterpolation(int,Matrix,int,Real,Real)}, or {@link #splineInterpolation(int,Matrix,int)} instead
     *  since they have a more reasonable argument list.
     * @todo test
     */
    public static Function splineInterpolation(int k, Matrix A, int interpolationType, Object[] config) {
        if (k != 4)
            throw new UnsupportedOperationException("spline interpolation not yet implemented for k!=4");
        Utility.pre(A.dimension().width == 2, "supporting nodes matrix 2 x m");
        double[] t = MathUtilities.toDoubleArray(A.getColumn(0));
        double[] f = MathUtilities.toDoubleArray(A.getColumn(1));
        assert t.length == f.length : "same number of supporting x-values as corresponding y-values";
	assert isSorted(t) : "ordered grid of x-values in " + A;
        final int l = t.length - 2;
        final Values vf = Values.getDefaultInstance();

        // node distances
        double[]           h = new double[t.length];
        for (int j = 1; j < t.length; j++)
            h[j] = t[j] - t[j - 1];

        // calculate momentum
        Matrix mom; // = Matrix.getInstance(l + 2, l + 2);
        // avoid null in the sparse tridiagonal matrix to allow multiplication
        mom = vf.ZERO(l + 2, l + 2);
        Vector d = vf.newInstance(l + 2);
        for (int j = 0; j < mom.dimension().height; j++)
            mom.set(j, j, vf.valueOf(2));
        for (int j = 1; j <= l; j++) {
            double lambdaj = h[j + 1] / (h[j] + h[j + 1]);
            // lambda[j]
            mom.set(j, j + 1, vf.valueOf(lambdaj));
            // my[j]
            mom.set(j, j - 1, vf.valueOf(1 - lambdaj));
            // d[j]
            d.set(j, vf.valueOf(((f[j + 1] - f[j]) / h[j + 1] - (f[j] - f[j - 1]) / h[j]) * 6 / (h[j] + h[j + 1])));
        } 

        switch (interpolationType) {
        case COMPLETE_SPLINE_INTERPOLATION:                                             // complete interpolation
            // s'(a) = f'(a), s'(b) = f'(b)
            double fda = ((Number) config[0]).doubleValue();    // f'(a)
            double fdb = ((Number) config[1]).doubleValue();    // f'(b)
            mom.set(0, 1, vf.valueOf(1));                                       // lambda[0]
            d.set(0, vf.valueOf(((f[1] - f[0]) / h[1] - fda) * 6 / h[1]));        // d[0]
            mom.set(l + 1, l, vf.ONE);                          // my[l+1]
            d.set(l + 1, vf.valueOf((fdb - (f[l + 1] - f[1]) / h[l + 1]) * 6 / h[l + 1]));        // d[l+1]
            break;
        case NATURAL_SPLINE_INTERPOLATION:                                              // natural interpolation
            // s''(a) = 0 = s''(b)
            mom.set(0, 1, vf.ZERO());                                 // lambda[0]
            d.set(0, vf.ZERO());                                              // d[0]
            mom.set(l + 1, l, vf.ZERO());                             // my[l+1]
            d.set(l + 1, vf.ZERO());                                  // d[l+1]
            break;
        case PERIODICAL_SPLINE_INTERPOLATION:
            throw new UnsupportedOperationException("periodical spline interpolation not yet implemented");
        default:
            throw new IllegalArgumentException("no valid interpolation type: " + interpolationType);
        }
        
        // solve sparse tridiagonal LES with diagonal dominant matrix momentum (is f.ex. LU stable)
        Vector solution = (Vector) mom.inverse().multiply(d);

        // momentum values m[j] = s_j''[t[j]]
        double[] m = MathUtilities.toDoubleArray(solution);

        // calculate coefficients of spline from momentum values m[j] = s_j''[t[j]]
        double[][] coefficients = new double[l + 1][k];
        for (int j = 0; j < coefficients.length; j++) {
            // Taylor-Series of s_j around t[j]
            // s_j[t[j]]
            coefficients[j][0] = f[j];
            // s_j'[t[j]]
            coefficients[j][1] = (f[j + 1] - f[j]) / h[j + 1] - (2 * m[j] + m[j + 1]) * h[j + 1] / 6;
            // 1/2 * s_j''[t[j]]
            coefficients[j][2] = 0.5 * m[j];
            // 1/6 * s_j'''[t[j]] but no longer continuous
            coefficients[j][3] = (m[j + 1] - m[j]) / (6 * h[j + 1]);
        } 

        return new SplineInterpolationFunction(k, t, coefficients);
    } 
    private static class SplineInterpolationFunction extends AbstractFunctor implements Function {
        private final int order;
        private final double[] t;
        private final double[][] coefficients;
        public SplineInterpolationFunction(int order, double[] t, double[][] coefficients) {
            this.order = order;
            this.t = t;
            this.coefficients = coefficients;
        }
        /**
         * spline piecewise polynom
         * @see UnivariatePolynomial
         */
        public Object apply(Object arg) {
            double x = ((Number) arg).doubleValue();

            // search the interval [t[j],t[j+1]) which contains x
            int    j;
            for (j = 0; j + 1 < t.length; j++)
                if (x <= t[j + 1])
                    break;
            // simply extrapolate for coefficients out of range of [t[0],t[l+1]]
            if (x < t[0])
                j = 0;
            else if (x > t[t.length - 1])
                j = coefficients.length - 1;

            // polynomial evaluation with Horner evaluation scheme
            //@TODO: use Functions.polynom?
            double r = 0;
            double partialExp = 1;
            for (int i = 0; i < coefficients[j].length; i++) {
                /* @invariants partialExp = (x - t[j])^i && r = &sum<sub>k=0,..i</sub>(coefficients[j][k] * (x - t[j])^k) */
                r += coefficients[j][i] * partialExp;
                partialExp *=  x - t[j];
            }
            return Values.getDefaultInstance().valueOf(r);
        } 
        public Function derive() throws ArithmeticException {
            //@todo already constructed as s_j'[t[j]] = coefficients[j][1]?
            throw new UnsupportedOperationException("derivative of spline interpolation not yet implemented");
        } 
        public Function integrate() throws ArithmeticException {
            throw new UnsupportedOperationException("integral of spline interpolation not yet implemented");
        } 
        public String toString() {
            return "[" + "spline" + order + "]";
        } 
    }

    /**
     * Bezier curve.
     * <p>
     * Currently implemented with de Casteljau.
     * </p>
     * @param t0 the starting point in the parameter interval [t0,..tz].
     * @param tz the ending point in the parameter interval [t0,..tz].
     * @param bezierNodes the row-vectors specify the bezier nodes in the given order.
     * @return a vectorial bezier curve with the specified bezier nodes and a parameter from t0 to tz.
     */
    public static Function bezierCurve(double t0, double tz, Matrix bezierNodes) {
        final Values vf = Values.getDefaultInstance();
        /* array of bezier nodes */
        Vector[] bezier = new Vector[bezierNodes.dimension().height];
        {
            int i = 0;
            for (Iterator it = bezierNodes.getRows(); it.hasNext(); )
                bezier[i++] = (Vector) it.next();
        } 

        /* the transformation of [t0..tz] to [0..1] phi(t) = (t-a)/(b-a) */
        Function phi = (Function) Operations.times.apply(Functions.constant(vf.valueOf(1 / (tz - t0))), Functionals.bindSecond(Operations.subtract, vf.valueOf(t0)));

        /*
         * The sub polynoms b<span class="doubleIndex"><sub>i</sub><sup>k</sup></span> = b[i][k].
         * This matrix will be an upper triangular matrix.
         * first column b[i][0] will remain constant function bezier[i] independent of t.
         */
        Function[][] b = new Function[bezier.length][bezier.length];
        for (int i = 0; i < b.length; i++)
            b[i][0] = Functions.constant(bezier[i]);

        /*
         * performs de Casteljau recursion, which follows dynamic programming
         * b[i][k] = (1-phi)*b[i][k-1] + phi*b[i+1][k-1]
         */
        for (int k = 1; k < b.length; k++)
            for (int i = 0; i < b[k].length - k; i++) {
                Function t = Functionals.compose(Operations.subtract, Functions.constant(Values.getDefault().ONE()), phi);
                b[i][k] = (Function) Operations.plus.apply(Operations.times.apply(b[i][k - 1], t), Operations.times.apply(b[i + 1][k - 1], phi));
            } 

        // we would already know the k-th derivatives from b[0][n-k],..b[k][n-k]
        // but maybe it's cheaper to forget than to keep them :-(
        return b[0][b[0].length - 1];
    } 
    
    
    // numerical integration (numerische Quadratur)
    
    /**
     * Returns &asymp; &int;<span class="doubleIndex"><sub>a</sub><sup>b</sup></span> f <i>d</i>x.
     * <p>
     * Currently implemented as Newton-Cotes numerical integration.
     * </p>
     * @see MathUtilities#integrate(orbital.math.functional.Function, Arithmetic, Arithmetic)
     */
    public static Arithmetic integrate(orbital.math.functional.Function f, Arithmetic a, Arithmetic b) {
        // Newton-Cotes-Formula for equi-distant supporting nodes t[k] = a + k*h
        // lambda[k] = 1 / n * &int; &prod;<sub>j=0,..k, j!=k</sub> (s-j)/(i-j) ds
        // @see Lagrange-Polynom
        // Milne-Rule of error term for n = 4,
        double lambda[] = {7/90., 32/90., 12/90., 32/90., 7/90.};
        int        n = lambda.length - 1;
        double h = ((Number) b.subtract(a)).doubleValue() / n;
        
        // return (b-a) * &sum;(lambda[k] * f(a + k*h))
        // @see Functionals#banana
        final Values vf = Values.getDefaultInstance();
        double array[] = new double[lambda.length];
        for (int k = 0; k < array.length; k++)
            array[k] = lambda[k] * ((Number) f.apply(a.add(vf.valueOf(k * h)))).doubleValue();
        return b.subtract(a).scale(vf.valueOf(Evaluations.sum(array)));
    }

    /**
     * Returns a numerical solution x of the differential equation
     * x'(t) = f(t,x(t)), x(&tau;)=&eta; on [a,b].
     * <p>
     * Currently implemented as an explicit Runge-Kutta method.
     * </p>
     * @param f the right-hand side of the differential equation.
     * @param tau the initial time &tau; of the initial values &eta;.
     * @param eta the vector &eta; of initial values.
     * @param step the number m of discretisation steps defining h=(a-b)/m.
     * @param order the desired order p of the global discretisation error, i.e., with errors in O(h<sup>p</sup>).
     * @return a numerical solution x of the differential equation system
     *   <center>
     *     x'(t)=f(t,x(t)) on [a,b]<br />
     *     x(&tau;)=&eta;
     *   </center>
     * @see AlgebraicAlgorithms#dSolve(Matrix,Vector,Real,Vector)
     */
    public static orbital.math.functional.Function dSolve(orbital.math.functional.BinaryFunction f, Real tau, Vector/*<Real>*/ eta,
							  Real a, Real b,
							  int steps, int order) {
	return dSolve(f, tau, eta, a, b, steps, getButcherTableau(order));
    }
    /**
     * Returns a numerical solution x of the differential equation
     * x'(t) = f(t,x(t)), x(&tau;)=&eta; on [a,b].
     * <p>
     * Implements the explicit Runge-Kutta with given Butcher tableau.
     * <center>x<sub>n+1</sub> = x<sub>n</sub> + h&sum;<sub>i=1</sub><sup>s</sup> b<sub>i</sub>k<sub>i</sub></center>
     * with
     * <center>k<sub>i</sub> = f(t<sub>n</sub>+c<sub>i</sub>h, y<sub>n</sub> + h&sum;<sub>j=1</sub><sup>i-1</sup> a<sub>i,j</sub>hk<sub>j</sub></center>
     * and discretisation step defined as in {@link #dSolve(orbital.math.functional.Function,Real,Vector,Real,Real,int)}.
     * </p>
     * @param butcher is a consistent Butcher tableau
     *   <table>
     *     <tr><td>c<sub>1</sub></td> </tr>
     *     <tr><td>c<sub>2</sub></td> <td>a<sub>2,1</sub></td></tr>
     *     <tr><td>c<sub>3</sub></td> <td>a<sub>3,1</sub></td><td>a<sub>3,2</sub></td></tr>
     *     <tr><td>...</td> <td></td><td></td><td>...</td></tr>
     *     <tr><td>c<sub>s</sub></td> <td>a<sub>s,1</sub></td><td>a<sub>s,2</sub></td><td>...</td><td>a<sub>s,s-1</sub></td></tr>
     *   </table>
     * @precondition butcher is consistent, i.e., &sum;<sub>j=1</sub><sup>i-1</sup> a<sub>i,j</sub>=c<sub>i</sub>
     * @see #dSolve(orbital.math.functional.Function,Real,Vector,Real,Real,int)
     */
    public static orbital.math.functional.Function dSolve(orbital.math.functional.BinaryFunction/*<Real,Vector<Real>>*/ f, Real tau, Vector/*<Real>*/ eta,
							  Real min, Real max,
							  int steps,
							  Matrix/*<Real>*/ butcher) {
	if (!MathUtilities.isin(tau, min,max))
	    throw new IllegalArgumentException("initial point " + eta + " out of solution bounds [" + min + "," + max + "]");
	if (!isConsistentButcher(butcher))
	    throw new IllegalArgumentException("Butcher tableau is inconsistent: " + butcher);
	if (eta.dimension() != 1)
	    throw new UnsupportedOperationException("not yet implemented for dimension>1: initial values " + eta);
	if (!tau.equals(min))
	    throw new UnsupportedOperationException("not yet implemented for inner initial time " + tau + " not being left border of [" + min + "," + max + "]");
        final ValueFactory vf = Values.getDefault();
	// discretisation mesh
	final Real h = max.subtract(min).divide(vf.valueOf(steps));
	// split Butcher tableau
	final Vector c = butcher.getColumn(0).subVector(0, butcher.dimension().height - 2);
	final Vector b = butcher.getRow(butcher.dimension().height - 1).subVector(1, butcher.dimension().width - 1);
	final Matrix A = butcher.subMatrix(0, butcher.dimension().height - 2,
					   1, butcher.dimension().width - 1);
	// number of stages of Runge-Kutta
	final int s = b.dimension();
	assert c.dimension() == b.dimension();
	assert A.dimension().height == s;
	assert A.dimension().width == s;

	// the list of (t,y) coordinates of supporting nodes of the discretisation
	Matrix/*<Real>*/ nodes = vf.ZERO(steps, 2);

	int n = 0;
	// last t-value tn
	Real tn = tau;
	// last y-value yn
	Vector/*<Real>*/ yn = eta;
	// f(t0=tau,y0=eta)
	nodes.set(0, 0, tn);
	nodes.set(0, 1, yn.get(0));
	n++;
	while (n < steps) {
	    final Real tnp1 = tn.add(h);
	    // ki=(0,0,...,0)
	    final Vector/*<Vector<Real>>*/ ki = vf.newInstance(s);
	    for (ListIterator kis = ki.iterator(); kis.hasNext(); ) {
		kis.next();
		assert yn.zero() instanceof Vector;
		kis.set(yn.zero());
		assert kis.previous() instanceof Vector && yn.zero().equals(kis.next()) : "setting zero has effect";
	    }
	    assert orbital.util.Setops.all(ki.iterator(), new orbital.logic.functor.Predicate() {public boolean apply(Object o) {return o instanceof Vector;}}) : "vectorial notation in " + ki;
	    for (int i = 0; i < s; i++) {
		// k<sub>i</sub> = f(tn+ci*h, yn + h*A.k)
		// k<sub>i</sub> = f(t<sub>n</sub>+c<sub>i</sub>h, y<sub>n</sub> + h&sum;<sub>j=1</sub><sup>i-1</sup> a<sub>i,j</sub>hk<sub>j</sub>
		/*ki.set(i, (Arithmetic)f.apply(
					      tn.add(c.get(i).multiply(h)),
					      yn.add(ki.multiply(A).multiply(h))
					      ));*/
		// accumulates A.k
		Vector acc = (Vector)yn.zero();
		for (int j = 0; j < i; j++) {
		    assert ki.get(j) instanceof Vector : "assuming vectorial notation " + ki.get(j);
		    assert A.get(i,j) instanceof Real;
		    acc = acc.add(((Vector)ki.get(j)).multiply((Real)A.get(i, j)));
		}
		Real   tval = tn.add((Real)(c.get(i)).multiply(h));
		Vector yval = yn.add(acc.multiply(h));
		Arithmetic fval = (Arithmetic)f.apply(tval, yval);
		assert fval instanceof Vector : "assuming vectorial functions f(" + tval + ", " + yval + ") = " + fval;
		ki.set(i, (Vector)fval);
	    }
	    // yn+1 = yn + h*(b.ki)
	    // observe that ki is a vector of vectors, hence the type cast
	    Vector ynp1 = yn.add(((Vector)ki.multiply(b)).multiply(h));
	    // f(tn+1,yn+1)
	    nodes.set(n, 0, tnp1);
	    assert ynp1.dimension() == 1 : "not yet implemented for dimension>1: initial values " + ynp1;
	    nodes.set(n, 1, ynp1.get(0));
	    n++;
	    tn = tnp1;
	    yn = ynp1;
	}
	return splineInterpolation(4, nodes, NATURAL_SPLINE_INTERPOLATION);
    }

    /**
     * Returns a numerical solution x of the one-dimensional differential equation
     * x'(t) = f(t,x(t)), x(&tau;)=&eta; on [a,b].
     * @see #dSolve(orbital.math.functional.Function,Real,Vector,Real,Real,int,int)
     */
    public static orbital.math.functional.Function dSolve(orbital.math.functional.BinaryFunction f, Real tau, Real eta,
							  Real a, Real b,
							  int steps, int order) {
	return dSolve(f, tau, eta, a, b, steps, getButcherTableau(order));
    }

    /**
     * Returns a numerical solution x of the one-dimensional differential equation
     * x'(t) = f(t,x(t)), x(&tau;)=&eta; on [a,b].
     * @see #dSolve(orbital.math.functional.Function,Real,Vector,Real,Real,int)
     */
    public static orbital.math.functional.Function dSolve(orbital.math.functional.BinaryFunction/*<Real,Real>*/ f, Real tau, Real eta,
							  Real min, Real max,
							  final int steps,
							  Matrix/*<Real>*/ butcher) {
	if (!MathUtilities.isin(tau, min,max))
	    throw new IllegalArgumentException("initial point " + eta + " out of solution bounds [" + min + "," + max + "]");
	if (!isConsistentButcher(butcher))
	    throw new IllegalArgumentException("Butcher tableau is inconsistent: " + butcher);
	if (!tau.equals(min))
	    throw new UnsupportedOperationException("not yet implemented for inner initial time " + tau + " not being left border of [" + min + "," + max + "]");
        final ValueFactory vf = Values.getDefault();
	// discretisation mesh
	final Real h = max.subtract(min).divide(vf.valueOf(steps));
	// split Butcher tableau
	final Vector c = butcher.getColumn(0).subVector(0, butcher.dimension().height - 2);
	final Vector b = butcher.getRow(butcher.dimension().height - 1).subVector(1, butcher.dimension().width - 1);
	final Matrix A = butcher.subMatrix(0, butcher.dimension().height - 2,
					   1, butcher.dimension().width - 1);
	// number of stages of Runge-Kutta
	final int s = b.dimension();
	assert c.dimension() == b.dimension();
	assert A.dimension().height == s;
	assert A.dimension().width == s;

	// the list of (t,y) coordinates of supporting nodes of the discretisation
	Matrix/*<Real>*/ nodes = vf.ZERO(steps, 2);

	int n = 0;
	// last t-value tn
	Real tn = tau;
	// last y-value yn
	Real yn = eta;
	// f(t0=tau,y0=eta)
	nodes.set(0, 0, tn);
	nodes.set(0, 1, yn);
	n++;
	while (n < steps) {
	    final Real tnp1 = tn.add(h);
	    // k=(0,0,...,0)
	    final Vector/*<Real>*/ k = vf.ZERO(s);
	    for (int i = 0; i < s; i++) {
		// k<sub>i</sub> = f(tn+ci*h, yn + h*A.k)
		// k<sub>i</sub> = f(t<sub>n</sub>+c<sub>i</sub>h, y<sub>n</sub> + h&sum;<sub>j=1</sub><sup>i-1</sup> a<sub>i,j</sub>hk<sub>j</sub>
		//System.out.println("\t" + tn + " + " + c.get(i) + "*" + h);
		//System.out.println("\t= " + tn.add(c.get(i).multiply(h)));
		//System.out.println("\t" + yn + " + " + A + "." + k + "*" + h);
		//System.out.println("\t= " + yn.add(A.multiply(k).multiply(h)));
		//k.set(i, (Arithmetic)f.apply(
		//			     tn.add(c.get(i).multiply(h)),
		//			     yn.add(A.multiply(k).multiply(h))
		//			     ));
		// accumulates A.k
		Real acc = (Real)yn.zero();
		for (int j = 0; j < i; j++) {
		    acc = acc.add(((Real)k.get(j)).multiply((Real)A.get(i, j)));
		}
		Real   tval = tn.add((Real)(c.get(i)).multiply(h));
		Real yval = yn.add(acc.multiply(h));
		Arithmetic fval = (Arithmetic)f.apply(tval, yval);
		k.set(i, (Real)fval);
	    }
	    // yn+1 = yn + h*(b.k)
	    // observe that ki is a vector of vectors, hence the type cast
            Real ynp1 = yn.add((Real)k.multiply(b).multiply(h));
	    // f(tn+1,yn+1)
	    nodes.set(n, 0, tnp1);
	    nodes.set(n, 1, ynp1);
	    n++;
	    tn = tnp1;
	    yn = ynp1;
	}
	return splineInterpolation(4, nodes, NATURAL_SPLINE_INTERPOLATION);
    }

    private static Matrix getButcherTableau(int order) {
	switch (order) {
	case 2:
	    // improved polygons
	    return Values.getDefault().valueOf(new double[][] {
		//c,    aij
		{0,     0,   0},
		{1/2.,  1/2.,0},
		// b:
		{0,     0,   1}  //b
	    });
	    /*case 2:
	    // Heun (p=2)
	    return Values.getDefault().valueOf(new double[][] {
		//c,    aij
		{0,  0,0},
		{1,  1,0},
		// b:
		{0,  1/2.,1/2.}  //b
		});*/
	case 3:
	    // Heun (p=3)
	    return Values.getDefault().valueOf(new double[][] {
		//c,    aij
		{0,     0,   0,    0},
		{1/3.,  1/3.,0,    0},
		{2/3.,  0,   2/3., 0},
		// b:
		{0,     1/4.,0,   3/4.}  //b
	    });
	    /*case 3:
	    // Kutta, Simpson-Regel (p=3)
	    return Values.getDefault().valueOf(new double[][] {
		//c,    aij
		{0,     0,   0,    0},
		{1/2.,  1/2.,0,    0},
		{1,     -1,  2, 0},
		// b:
		{0,     1/6.,4/6., 1/6.}  //b
	    });*/
	case 4:
	    // Runge-Kutta 4
	    return Values.getDefault().valueOf(new double[][] {
		//c,    aij
		{0,     0,   0,   0,   0},
		{1/2.,  1/2.,0,   0,   0},
		{1/2.,  0,   1/2.,0,   0},
		{1,     0,   0,   1,   0},
		// b:
		{0,     1/6.,2/6.,2/6.,1/6.}  //b
	    });
	    /*case 4:
	    // Runge-Kutta 3/8 rule
	    return Values.getDefault().valueOf(new double[][] {
		//c,    aij
		{0,     0,   0,   0,   0},
		{1/3.,  1/3.,0,   0,   0},
		{2/3., -1/3.,1,   0,   0},
		{1,     1,  -1,   1,   0},
		// b:
		{0,     1/8.,3/8.,3/8.,1/8.}  //b
	    });*/
	default:
	    throw new UnsupportedOperationException("No method of order " + order + " supported. Try a different order");
	}
    }

    // helpers
    
    private static boolean isConsistentButcher(Matrix/*<Real>*/ A) {
	if (!A.isSquare())
	    return false;
	if (!A.get(A.dimension().height-1,0).isZero())
	    // should carry no information
	    return false;
	// test for non-strict upper diagonal matrix
	for (int i = 0; i < A.dimension().height; i++) {
	    for (int j = i + 1; j < A.dimension().width; j++) {
		if (!A.get(i, j).isZero())
		    // should carry no information
		    return false;
	    }
	}
        final Real zero = (Real)A.get(0, 0).zero();
	// each row is consistent
	for (Iterator i = A.getRows(); i.hasNext(); ) {
	    Vector v = (Vector)i.next();
	    if (!i.hasNext())
		// excluding last b row so skip
		continue;
	    if (!MathUtilities.equalsCa(v.get(0), (Real)Operations.sum.apply(v.subVector(1,v.dimension()-1))))
		return false;
	}
	return true;
    }

    private static boolean isSorted(double a[]) {
        for (int i = 0; i < a.length - 1; i++) {
	    if (a[i] > a[i+1])
		return false;
	}
	return true;
    }
}
