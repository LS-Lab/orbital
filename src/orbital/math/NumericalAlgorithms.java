/**
 * @(#)NumericalAlgorithms.java 1.0 2000/06/10 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Function;
import orbital.math.functional.Functionals;

import orbital.math.functional.Operations;
import orbital.math.functional.Functions;

import orbital.moon.math.AbstractFunctor;

import java.util.Iterator;
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
 * @version 1.0, 2000/06/10
 * @author  Andr&eacute; Platzer
 * @see orbital.math.MathUtilities
 * @see LUDecomposition
 * @todo introduce GMRES-Algorithm
 * @todo introduce Newton-Iteration or more general fixed point iteration (see Functionals)
 * @todo introduce a row Pivot with a threshold of &asymp;1.5
 * @todo introduce GaussJordanAlgorithm
 */
public class NumericalAlgorithms {
    private static final Logger logger = Logger.getLogger(NumericalAlgorithms.class.getName());
    /**
     * prevent instantiation - final static class
     */
    private NumericalAlgorithms() {}

    // interpolation type enum values

    /**
     * complete spline interpolation wishing s'(a)=f'(a) and s'(b)=f'(b).
     */
    public static final int COMPLETE_SPLINE_INTERPOLATION = 1;

    /**
     * natural spline interpolation wishing s''(a)=0 and s''(b)=0.
     */
    public static final int NATURAL_SPLINE_INTERPOLATION = 2;

    /**
     * periodical spline interpolation wishing s'(a)=s'(b) and s''(a)=s''(b).
     * The length of the period is b-a.
     */
    public static final int PERIODICAL_SPLINE_INTERPOLATION = 3;


    // utility methods calculating numerical "Kenngrößen"
	
    /**
     * Checks whether a matrix is diagonally dominant.
     * @return whether &forall;i&isin;{0,...,n-1} &sum;<sub>k&isin;{0,...,n-1}\{i}</sub> |a<sub>k,i</sub>| &le; |a<sub>i,i</sub>|.
     *  Or perhaps passing to absolutes |.| instead.
     * @pre A.isSquare()
     * @post RES == &forall;i&isin;{0,...,A.dimension().width-1} &sum;<sub>k&isin;{0,...,A.dimension().height-1}\{i}</sub> Math.abs(a<sub>k,i</sub>) &le; Math.abs(a<sub>i,i</sub>)
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
     * @pre A.isPositiveDefinite()
     * @post L*L^T == A
     * @todo test
     * @todo optimize
     */
    public static Matrix decomposeCholesky(Matrix A) throws ArithmeticException {
	Utility.pre(A.isSymmetric() && A.isDefinite() > 0, "Only symmetric and positive-definite matrices can be Cholesky-decomposed");
	// Utility.pre(A.isPositiveDefinite(), "Only positive-definite matrices can be Cholesky-decomposed");
	// we restrict ourselves to AbstractMatrix because they have these fast getDoubleValue methods
	AbstractMatrix A_ = (AbstractMatrix) A;
	AbstractMatrix L = (AbstractMatrix) Values.ZERO(A.dimension().width, A.dimension().width);
	for (int k = 0; k < A_.dimension().width; k++) {
	    double t = 0;
	    for (int j = 0; j < k; j++) {
		double L_kj = L.getDoubleValue(k, j);
		t += L_kj * L_kj;
	    } 
	    L.set(k, k, Values.valueOf(Math.sqrt(A_.getDoubleValue(k, k) - t)));

	    for (int i = k + 1; i < A_.dimension().height; i++) {
		t = 0;
		for (int j = 0; j < k; j++)
		    t += L.getDoubleValue(i, j) * L.getDoubleValue(k, j);
		L.set(i, k, Values.valueOf((A_.getDoubleValue(i, k) - t) / L.getDoubleValue(k, k)));
	    } 
	} 
	assert L.multiply(L.transpose()).equals(A_) : "L.L^T = A";
	return L;
    } 

    /**
     * cg-algorithm for solving A&middot;x=b iteratively starting with x0.
     * @pre A.isPositiveDefinite() && b.dimension()==A.dimension().width && x0.dimension() == A.dimension().width
     * @post A.multiply(x) == b
     * @return the solution vector x solving Ax=b.
     * @todo test sometime
     */
    public static Vector cgSolve(Matrix A, Vector x0, Vector b) {
	assert A.isSquare() : "Only square matrices can be solved with cg-algorithm";
	// assert A.isPositiveDefinite(),"Only positive-definite matrices can be solved with cg-algorithm");
	assert b.dimension() == A.dimension().width : "compatible result vector dimension required";
	assert x0.dimension() == A.dimension().width : "compatible starting vector dimension required";
	Vector p;								  // A-orthogonal basis vectors
	Vector x = (Vector) x0.clone();			  // trial solution
	Vector r = b.subtract(A.multiply(x0));	  // residual
	p = (Vector) r.clone();
	final Real tolerance = Values.valueOf(MathUtilities.getDefaultTolerance());
	for (int m = 0; m < A.dimension().width && !((AbstractVector) r).equals(Values.ZERO(r.dimension()), tolerance); m++) {
	    Vector o_r = (Vector) r.clone();	// old residual
	    Vector a = A.multiply(p);
	    Scalar alpha = (Scalar/*__*/) ((Scalar/*__*/)Functions.square.apply(r.norm(2))).divide(a.multiply(p));
	    x = x.add(p.multiply(alpha));
	    r = r.subtract(a.multiply(alpha));
	    Scalar beta = (Scalar/*__*/) ((Scalar/*__*/)Functions.square.apply(r.norm(2))).divide((Scalar/*__*/)Functions.square.apply(o_r.norm(2)));
	    p = r.add(p.multiply(beta));
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
     * @pre A.dimension().width == 2
     * @see Polynomial
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
	    return Values.valueOf(p[t.length - 1][t.length - 1]);
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
     * @param A the matrix with supporting nodes (first column is &Delta;, second column are y-values).
     *  A must be <b>ordered</b> by ascending x-values.
     * @param interpolationType specifies which type of interpolation to do (see config).
     * <ul>
     *  <li>{@link #NATURAL_SPLINE_INTERPOLATION} - natural spline interpolation</li>
     *  <li>{@link #PERIODICAL_SPLINE_INTERPOLATION} - periodical spline interpolation</li>
     * </ul>
     * @pre A is ordered by ascending x-values. &forall;i A.get(i, 0) < A.get(i+1, 0)
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
     * @param A the matrix with supporting nodes (first column is &Delta;, second column are y-values).
     *  A must be <b>ordered</b> by ascending x-values.
     * @param interpolationType specifies which type of interpolation to do
     * <ul>
     *  <li>{@link #COMPLETE_SPLINE_INTERPOLATION} - complete spline interpolation.</li>
     * </ul>
     * @param fp_a the value f'(a) of the derivative of f at a.
     * @param fp_b the value f'(b) of the derivative of f at b.
     * @pre A is ordered by ascending x-values. &forall;i A.get(i, 0) < A.get(i+1, 0)
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
	double[]       t = MathUtilities.toDoubleArray(A.getColumn(0));
	double[]	   f = MathUtilities.toDoubleArray(A.getColumn(1));
	assert t.length == f.length : "same number of supporting x-values as corresponding y-values";
	int			   l = t.length - 2;

	// node distances
	double[]	   h = new double[t.length];
	for (int j = 1; j < t.length; j++)
	    h[j] = t[j] - t[j - 1];

	// calculate momentum
	Matrix mom; // = Matrix.getInstance(l + 2, l + 2);
	// avoid null in the sparse tridiagonal matrix to allow multiplication
	mom = Values.ZERO(l + 2, l + 2);
	Vector d = Values.getInstance(l + 2);
	for (int j = 0; j < mom.dimension().height; j++)
	    mom.set(j, j, Values.valueOf(2));
	for (int j = 1; j <= l; j++) {
	    double lambdaj = h[j + 1] / (h[j] + h[j + 1]);
	    // lambda[j]
	    mom.set(j, j + 1, Values.valueOf(lambdaj));
	    // my[j]
	    mom.set(j, j - 1, Values.valueOf(1 - lambdaj));
	    // d[j]
	    d.set(j, Values.valueOf(((f[j + 1] - f[j]) / h[j + 1] - (f[j] - f[j - 1]) / h[j]) * 6 / (h[j] + h[j + 1])));
	} 

	switch (interpolationType) {
	case COMPLETE_SPLINE_INTERPOLATION:						// complete interpolation
	    // s'(a) = f'(a), s'(b) = f'(b)
	    double fda = ((Number) config[0]).doubleValue();	// f'(a)
	    double fdb = ((Number) config[1]).doubleValue();	// f'(b)
	    mom.set(0, 1, Values.valueOf(1));					// lambda[0]
	    d.set(0, Values.valueOf(((f[1] - f[0]) / h[1] - fda) * 6 / h[1]));	  // d[0]
	    mom.set(l + 1, l, Values.ONE);				// my[l+1]
	    d.set(l + 1, Values.valueOf((fdb - (f[l + 1] - f[1]) / h[l + 1]) * 6 / h[l + 1]));	  // d[l+1]
	    break;
	case NATURAL_SPLINE_INTERPOLATION:						// natural interpolation
	    // s''(a) = 0 = s''(b)
	    mom.set(0, 1, Values.ZERO);					// lambda[0]
	    d.set(0, Values.ZERO);						// d[0]
	    mom.set(l + 1, l, Values.ZERO);				// my[l+1]
	    d.set(l + 1, Values.ZERO);					// d[l+1]
	    break;
	case PERIODICAL_SPLINE_INTERPOLATION:
	    throw new UnsupportedOperationException("periodical spline interpolation not yet implemented");
	default:
	    throw new IllegalArgumentException("no valid interpolation type: " + interpolationType);
	}
        
	// solve sparse tridiagonal LES with diagonal dominant matrix momentum (is f.ex. LU stable)
	Vector		solution = (Vector) mom.inverse().multiply(d);

	// momentum values m[j] = s_j''[t[j]]
	double[]	m = MathUtilities.toDoubleArray(solution);

	// calculate coefficients of spline from momentum values m[j] = s_j''[t[j]]
	double[][]  coefficients = new double[l + 1][k];
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
	 * @see Polynomial
	 */
	public Object apply(Object arg) {
	    double x = ((Number) arg).doubleValue();

	    // search the interval [t[j],t[j+1]) which contains x
	    int	   j;
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
		/* @invariant partialExp = (x - t[j])^i && r = &sum<sub>k=0,..i</sub>(coefficients[j][k] * (x - t[j])^k) */
		r += coefficients[j][i] * partialExp;
		partialExp *=  x - t[j];
	    }
	    return Values.valueOf(r);
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
     * @param bezierNodess the row-vectors specify the bezier nodes in the given order.
     * @return a vectorial bezier curve with the specified bezier nodes and a parameter from t0 to tz.
     */
    public static Function bezierCurve(double t0, double tz, Matrix bezierNodes) {

	/* array of bezier nodes */
	Vector[] bezier = new Vector[bezierNodes.dimension().height];
	{
	    int i = 0;
	    for (Iterator it = bezierNodes.getRows(); it.hasNext(); )
		bezier[i++] = (Vector) it.next();
	} 

	/* the transformation of [t0..tz] to [0..1] phi(t) = (t-a)/(b-a) */
	Function	 phi = (Function) Operations.times.apply(Functions.constant(Values.valueOf(1 / (tz - t0))), Functionals.bindSecond(Operations.subtract, Values.valueOf(t0)));

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
		Function t = Functionals.compose(Operations.subtract, Functions.constant(Values.ONE), phi);
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
    	int	   n = lambda.length - 1;
    	double h = ((Number) b.subtract(a)).doubleValue() / n;
    	
    	// return (b-a) * &sum;(lambda[k] * f(a + k*h))
    	// @see Functionals#banana
    	double array[] = new double[lambda.length];
    	for (int k = 0; k < array.length; k++)
	    array[k] = lambda[k] * ((Number) f.apply(a.add(Values.valueOf(k * h)))).doubleValue();
    	return b.subtract(a).multiply(Values.valueOf(Evaluations.sum(array)));
    }
}
