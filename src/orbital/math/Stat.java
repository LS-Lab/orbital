/**
 * @(#)Stat.java 1.0 1999/03/08 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import orbital.math.functional.Functionals;
import orbital.math.functional.Function;
import orbital.math.functional.BinaryFunction;

import orbital.math.functional.Operations;
import orbital.math.functional.Functions;
import orbital.moon.math.CoordinateCompositeFunction;
import orbital.util.Utility;

/**
 * This class contains algorithms and utlities for stochastics and statistical mathematics.
 * It works much like the standard class {@link java.lang.Math} extended for
 * statistics.
 * 
 * @stereotype &laquo;Utilities&raquo;
 * @version 1.0, 1999/03/08
 * @author  Andr&eacute; Platzer
 * @see orbital.math.MathUtilities
 * @see orbital.algorithm.Combinatorical
 */
public final class Stat {
    /**
     * prevent instantiation - module class
     */
    private Stat() {}

    // one-dimensional descriptive statistics

    /**
     * Returns the arithmetic mean (average) of a set of n values.
     * Sensitive to errorneous data.
     * @return 1/n * &sum;<span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> x<sub>i</sub> where n is the length of x.
     * @param x the array of double values representing the set of statistical data.
     * @pre x.length>0
     */
    public static double arithmeticMean(double x[]) {
	return Evaluations.sum(x) / x.length;
    } 

    /**
     * Returns the geometric mean of a set of n values.
     * <p>
     * It is always true that geometricMean &le; arithmeticMean.
     * Sensitive to errorneous data.
     * @param x the array of double values representing the set of statistical data.
     * @return <sup><span style="text-decoration: underline">n</span></sup>&radic;<span style="text-decoration: overline"></span>(&prod;<span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> x<sub>i</sub>).
     */
    public static double geometricMean(double x[]) {
	return Math.pow(Functionals.foldRight(Operations.times, 1, x), 1. / x.length);
    } 

    /**
     * Returns the harmonic mean of a set of n values.
     * @param x the array of double values representing the set of statistical data.
     * @return n / &sum;<span class="doubleIndex"><sub>i=0</sub><sup>n-1</sup></span> (1/x<sub>i</sub>).
     */
    public static double harmonicMean(double x[]) {
	return x.length / Functionals.foldRight(Operations.plus, 0, Functionals.map(Functions.reciprocal, x));
    } 

    /**
     * Normal arithmetic mean of a set of values. Also called Average.
     * @see #arithmeticMean(double[])
     * @see #average(double[])
     */
    public static double mean(double x[]) {
	return arithmeticMean(x);
    } 

    /**
     * Normal arithmetic mean of a set of values. Also called Average.
     * @see #arithmeticMean(double[])
     * @see #mean(double[])
     */
    public static double average(double x[]) {
	return arithmeticMean(x);
    } 

    /**
     * Returns the variance of a set of n values.
     * Sensitive to errorneous data.
     * @param x the array of double values representing the set of statistical data.
     * @return 1/(n-1)*Sum<big>(</big>(x<sub>i</sub>-mean)<sup>2</sup><big>)</big>.
     */
    public static double variance(double x[]) {
	double xm = mean(x);
	double s = 0;
	for (int i = 0; i < x.length; i++)
	    s += (x[i] - xm) * (x[i] - xm);
	return 1. / (x.length - 1.) * s;
    } 

    /**
     * Returns the standard deviation of a set of n values.
     * Sensitive to errorneous data.
     * @param x the array of double values representing the set of statistical data.
     * @return Sqrt(variance).
     */
    public static double standardDeviation(double x[]) {
	return Math.sqrt(variance(x));
    } 

    /**
     * Returns the coefficient of variation of a set of n values.
     * Sensitive to errorneous data.
     * @param x the array of double values representing the set of statistical data.
     * @return standardDeviation/mean.
     */
    public static double coefficientOfVariation(double x[]) {
	return standardDeviation(x) / mean(x);
    } 

    /**
     * Returns the median of a set of n values sorted in ascending numerical order.
     * @param x the <b>sorted</b> array of double values representing the set of statistical data.
     * @pre sorted(x)
     * @return <code>x<sub>(n-1)/2</sub></code> if n is odd, and <code>(x<sub>n/2-1</sub> + x<sub>n/2</sub>) / 2</code> if n is even.
     * @see java.util.Arrays#sort(double[])
     * @see java.lang.System#arraycopy
     */
    public static double median(double x[]) {
	Utility.pre(Utility.sorted(x, true), "sorted values");
	if (MathUtilities.odd(x.length))
	    return x[(x.length - 1) / 2];
	else
	    return (x[x.length / 2 - 1] + x[x.length / 2]) / 2;
    } 

    /**
     * Returns the a-quantile of a set of n values sorted in ascending numerical order.
     * <p>
     * quantile(x,0.25) is called "lower quartile", and quantil(ex,0.75) is called "upper quartile".
     * The interquartile range, quantile(x,0.75)-quantile(x,0.25), is a good measure for statistical deviation.
     * It is median(x)==quantile(x,0.5).
     * @param x the <b>sorted</b> array of double values representing the set of statistical data.
     * @param a a number within the open range ]0,1[ that defines the quantile of which part of the data is desired.
     * @pre a &isin; (0,1) && sorted(x)
     * @return <code>x<sub>k</sub></code> if n*a is no natural number (but fractional), and <code>(x<sub>k-1</sub> + x<sub>k</sub>) / 2</code> if n*a is a natural number, with <tt>k:=[n*a]</tt> (gaussian brackets).
     * @see java.util.Arrays#sort(double[])
     * @see java.lang.System#arraycopy
     */
    public static double quantile(double x[], double a) {
	Utility.pre(0 < a && a < 1, "quantile must be in the range of (0,1)");
	Utility.pre(Utility.sorted(x, true), "sorted values");
	int k = MathUtilities.gaussian(x.length * a);
	if (MathUtilities.equalsCa(MathUtilities.fract(x.length * a), 0.0))
	    return x[k];
	else
	    return (x[k - 1] + x[k]) / 2;
    } 

    /**
     * Returns the mean of a set of n values, sorted in ascending numerical order,
     * with a fraction a of entries at each end dropped.
     * @param x the <b>sorted</b> array of double values representing the set of statistical data.
     * @param a a number within the semi-open range of [0,0.5[.
     * @pre a &isin; [0, 0.5) && sorted(x)
     * @return <code>1/(n-2k)*(x<sub>k</sub> + ... + x<sub>n-k-1</sub>)</code>, with <tt>k:=[n*a]</tt> (gaussian brackets).
     * @see java.util.Arrays#sort(double[])
     * @see java.lang.System#arraycopy
     */
    public static double trimmedMean(double x[], double a) {
	Utility.pre(0 <= a && a < 0.5, "quantile must be in range of [0,0.5)");
	Utility.pre(Utility.sorted(x, true), "sorted values");
	int	   k = MathUtilities.gaussian(x.length * a);
	double s = 0;
	for (int i = k; i < x.length - k; i++)
	    s += x[i];
	return 1. / (x.length - 2 * k) * s;
    } 

    /**
     * Returns the mean absolute deviation of a set of n values.
     * It is a good measure for statistical deviations.
     * @param x the array of double values representing the set of statistical data.
     * @return 1/n*Sum<big>(</big>|x<sub>i</sub>-mean|<big>)</big>.
     */
    public static double meanDeviation(double x[]) {
	double xm = mean(x);
	double s = 0;
	for (int i = 0; i < x.length; i++)
	    s += Math.abs(x[i] - xm);
	return 1. / x.length * s;
    } 

    /**
     * Returns a string with the usual descriptive statistics for an array of double values.
     */
    public static String statistics(double x[]) {
	return "min: " + MathUtilities.format(Evaluations.min(x)) + "\tmax: " + MathUtilities.format(Evaluations.max(x)) + "\tavg: " + MathUtilities.format(Stat.average(x)) + "\tstdDev: " + MathUtilities.format(Stat.standardDeviation(x));
    } 

    // two-dimensional descriptive statistics

    /**
     * Returns the (2D) coefficient of correlation of a set of n pairs (x<sub>i</sub>,y<sub>i</sub>).
     * @param x the array of double values representing the x part of the set of statistical data (with same length and in same order as y).
     * @param y the array of double values representing the y part of the set of statistical data (with same length and in same order as x).
     * @pre x.length == y.length
     * @return 1/(n-1)*Sum<big>(</big>(x<sub>i</sub>-mean(x))*(x<sub>i</sub>-mean(y))<big>)</big> / (standardDeviation(x)*standardDeviation(y)).
     */
    public static double coefficientOfCorrelation(double x[], double y[]) {
	Utility.pre(x.length == y.length, "double arrays representing pairs must have the same length");
	double xm = mean(x);
	double ym = mean(y);
	double s = 0;
	for (int i = 0; i < x.length; i++)
	    s += (x[i] - xm) * (y[i] - ym);
	return 1. / (x.length - 1.) * s / standardDeviation(x) / standardDeviation(y);
    } 


    // linear regression

    /**
     * Performs linear regression to estimate a composed function with
     * least squares.
     * <p>
     * Unlike {@link #regression(Function[],Matrix)}, this method is a facade
     * that works for single parametric functions, only.</p>
     * @see <a href="{@docRoot}/DesignPatterns/Facade.html">Facade Pattern</a>
     */
    public static Function functionalRegression(final Function composedFunc, Matrix experiment) {
	Function	 theories[] = {composedFunc};
	final Vector a = regression(theories, experiment);
	return (Function) Operations.times.apply(composedFunc, a);
    } 

    /**
     * Performs linear regression to estimate the statistical mean according to
     * least squares.
     * For a (vectorial) theory <code><var>u</var> = <var>a</var>*f(<var>x</var>)</code>
     * the coefficient-vector <var>a</var> can be estimated using experimental data.
     * <p>
     * The data of an experiment is represented as a matrix
     * containing the experimental data for the parameter row-vectors x in the first columns
     * and - in the last column - the experimental data of the result scalar <var>u<sub>i</sub></var>
     * of each experiment i.</p>
     * <p>
     * Here <var>&ucirc;</var> denotes the vector of n response variables,
     * and <var>&acirc;</var> denotes the vector of p unknown parameters to be estimated.
     * The function f must be determined according to a thesis.
     * <center>
     * f:<b>R</b><sup>k</sup>&rarr;<b>R</b><sup>m</sup>; x&#8614;f(x) = <big>(</big>f<sub>1</sub>(x<sub>1</sub>),...f<sub>m</sub>(x<sub>k</sub>)<big>)</big><sup>T</sup> if k=m.
     * </center>
     * The theoretical function f can apply any combination of
     * real functions f<sub>i</sub> on the different parameters x<sub>i</sub>.
     * </p>
     * 
     * @pre experiment.dimension().width - 1 == funcs.length
     * @return an estimate <var>a&#771;</var> for the true coefficients vector <var>&acirc;</var>.
     * @see #regression(Vector, Matrix, Matrix)
     */
    public static Vector regression(Function funcs[], Matrix experiment) {
	Utility.pre(experiment.dimension().width - 1 == funcs.length, "Experiments parametric data and linear combination of functions must fit");
	if (experiment.dimension().width != 2)
	    throw new UnsupportedOperationException("Regression with >2 parameters not yet supported by this method. Use regression(Vector,Matrix,Matrix) instead");
	// find maximum argument dimension required by the funcs
	int dimensions[] = new int[funcs.length];
	// foreach Function v in funcs
	for (int v = 0; v < funcs.length; v++)
	    dimensions[v] = funcs[v] instanceof CoordinateCompositeFunction
		? ((CoordinateCompositeFunction) funcs[v]).dimension()
		: 1;
	Matrix A = Values.newInstance(experiment.dimension().height, Evaluations.max(dimensions));
	if (A.dimension().width > A.dimension().height)
	    throw new ArithmeticException("linear coefficients exceed experiment datasets (" + A.dimension().width + ">" + A.dimension().height + ") the statistical solution is ambiguous and (n-m) parametric");

	// convert experiment-parameters to Matrix
	// foreach Function v
	for (int v = 0; v < funcs.length; v++)
	    // foreach Dataset j
	    for (int j = 0; j < experiment.dimension().height; j++) {
		Vector arg = experiment.getRow(j);
		arg = arg.remove(arg.dimension() - 1);					// strip response variable
		if (arg.dimension() == 1)								// extend single value to whole vector?
		    if (funcs[v] instanceof CoordinateCompositeFunction)
			arg = Values.CONST( ((CoordinateCompositeFunction) funcs[v]).argumentDimension(), arg.get(0));
		    else
			throw new UnsupportedOperationException("Supports only regression for single or full parameters. Use elementary regression(Vector, Matrix, Matrix) instead");
		A.setRow(j, (Vector) funcs[v].apply(arg));
	    } 
	Vector u = experiment.getColumn(experiment.dimension().width - 1);

	// println("u="+u+"="+A+"*"+experiment.getColumn(0)+" estimation...");
	return regression(u, A, Values.IDENTITY(A.dimension().height, A.dimension().height));
    } 

    /**
     * Performs elemental linear regression to estimate the statistical mean
     * according to the method of least squares.
     * For a (vectorial) theory <code><var>u</var> = A*<var>a</var></code> an estimate <var>a&#771;</var>
     * for the true coefficient-vector <var>&acirc;</var> is predicted such that
     * <center>||A*<var>a&#771;</var> - <var>u</var>||<sub>2</sub> = min<sub><var>a</var></sub> ||A*<var>a</var> - <var>u</var>||<sub>2</sub>.
     * <p>
     * Here <var>&ucirc;</var> denotes the vector of n response variables,
     * and <var>&acirc;</var> denotes the vector of p unknown parameters to be estimated.
     * The p predictor variables for n experiments are denoted by the n&times;p Matrix A.</p>
     * <p>
     * This method is called with the experimentally deviated data u, A and
     * the covariance Cu of the variables.</p>
     * <p>
     * Comments: For Cu=IDENTITY(n), the result equals
     * <center>{@link Matrix#pseudoInverse() pseudoInverse}(A) * <var>u</var></center>
     * because it is the minimum-norm-solution.
     * <ul>
     *   <li>linear equalization with least square uses ||.||<sub>2</sub> as the norm</li>
     *   <li>linear optimization uses ||.||<sub>1</sub> as the norm.</li>
     *   <li>tschebyscheff equalization uses ||.||<sub>&infin;</sub> as the norm.</li>
     * </ul>
     * </p>
     * 
     * @param u the experimental vector of n response variables.
     * @param A the n&times;p Matrix of predictor variables for n experiments.
     * @param Cu the n&times;n covariance Matrix of u.
     * The diagonal vector contains the variance during experimental determination of each variable,
     * the other components contain the covariance, with other values.
     * @pre u.dimension() == A.dimension().height
     * @return <var>a&#771;</var> an estimate for the true coefficients vector <var>&acirc;</var>.
     *  With regard to the statistical deviation, this vector <var>a</var> can be used to calculate
     *  the estimated scalar <var>u</var> for other parameter-vectors <var>p</var> as
     *  <center>
     *  <var>u</var> = <var>p</var>*<var>a</var>
     *  </center>
     * @throws IllegalArgumentException if response vector has another size than matrix height n.
     * @throws ArithmeticException if the solution would be (n-m) parametric since less experiments exist than unknown parameters.
     */
    public static Vector regression(Vector u, Matrix A, Matrix Cu) throws ArithmeticException {
	Utility.pre(u.dimension() == A.dimension().height, "Result Vector must have smae dimension as the height of the Matrix");
	if (A.dimension().width > A.dimension().height)
	    throw new ArithmeticException("linear coefficients exceed experiment datasets (" + A.dimension().width + ">" + A.dimension().height + ") the statistical solution is ambiguous and n-m parametric");

	/*
	 * u = A*a + v  exist it an exact â=E(a) and û=E(u), such that
	 * û = A*â
	 * Weighting:  P = Cu^-1
	 * â can be estimated with Covariance Ca_e of a:  Ca_e = (A^T*P*A)^-1
	 * Estimation:  a_e= Ca_e*A^T*P*u  because of normal equation: A^T*P*A*a = A^T*P*u
	 * Where Covariance Cu_e of u is:  Cu_e= A*Ca_e*F^T
	 */

	// weighting matrix
	Matrix P = (Matrix) Cu.inverse();
	Matrix Ca_e = (A.transpose().multiply(P).multiply(A));	  // isSymmetric(), isSquare(), diagonal[i]!=0 forall i
	assert Ca_e.isSymmetric() : "Matrix to be inverted during calculation is symmetric " + Ca_e;
	Matrix R = (Matrix) Ca_e.inverse().multiply(A.transpose()).multiply(P);
	return R.multiply(u);
    } 
}
