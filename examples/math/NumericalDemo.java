

import orbital.math.*;
import orbital.math.functional.*;
import orbital.awt.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

class NumericalDemo {
    // get us a value factory for creating arithmetic objects
    private static final Values vf = Values.getDefaultInstance();
    public static void main(String arg[]) throws Exception {
	test_interpolation();
	test_decomposition();
	test_integration();
    } 
    static void test_decomposition() {
	System.out.println("test decompositions");
	Matrix A = vf.valueOf(new double[][] {
	    {2,5,-2,6},
	    {3,9./2,3,6},
	    {6,6,0,-3},
	    {3./2,3,-1,-9./4}
	});
	/*A = vf.valueOf(new double[][] {
	  {2,1,1},
	  {4,3,3},
	  {8,7,9}});*/
	System.out.println("decomposed \n" + A + "\ninto: ");
	LUDecomposition lu = LUDecomposition.decompose(A);
	System.out.println("L " + lu.getL() + ", ");
	System.out.println("U " + lu.getU() + ", ");
	System.out.println("P " + lu.getP() + ", ");
	System.out.println("which is " + (lu.getL().multiply(lu.getU()).equals(lu.getP().multiply(A)) ? "correct" : "incorrect") + " for P.A = L.U");
	System.out.println();
	Matrix B = vf.valueOf(new double[][] {
	    {9,12,6},
	    {12,20,14},
	    {6,14,15}
	});
	System.out.println("decomposed \n" + B + "\ninto: ");
	Matrix L = NumericalAlgorithms.decomposeCholesky(B);
	System.out.println(L);
	System.out.println("which is " + (L.multiply(L.transpose()).equals(B) ? "correct" : "incorrect") + " for L.L^T == B");
	System.out.println();

	Matrix C = vf.valueOf( new double[][]{
	    {1,1},
	    {1,2}});
	Vector b = vf.valueOf(new double[] {2,-1});
	System.out.println("solved \n" + C + "*x=" + b + " with cg-algorithm as: ");
	Vector x = NumericalAlgorithms.cgSolve(C, vf.ZERO(2), b);
	System.out.println(x);
    } 

    static void test_interpolation() {
	System.out.println("test interpolation");
	Function f = (Function) Operations.inverse.apply(Operations.plus.apply(vf.valueOf(1), Functions.square));
	System.out.println("interpolating function " + f);

	/*
	 * f = new Function() {
	 * public Object apply(Object arg) {double x = ((Number)arg).doubleValue();return vf.valueOf(1./(1+x*x));}
	 * public Function derive() {return null;}
	 * public Function integrate() {return null;}
	 * };
	 */
	final int n = 8;
	Matrix	  equidistant = vf.newInstance(n + 1, 2);
	for (int k = 0; k <= n; k++) {
	    Arithmetic xk = vf.valueOf(-5 + 10 * k / n);
	    equidistant.set(k, 0, xk);
	    equidistant.set(k, 1, (Arithmetic) f.apply(xk));
	} 
	Function p1 = NumericalAlgorithms.polynomialInterpolation(equidistant);

	// Chebyshev
	Matrix   tschebyscheff = vf.newInstance(n + 1, 2);
	for (int k = 0; k <= n; k++) {
	    Scalar xk = vf.valueOf(-5 * Math.cos(Math.PI / 2 * (2 * k + 1) / (n + 1)));
	    tschebyscheff.set(k, 0, xk);
	    tschebyscheff.set(k, 1, (Arithmetic) f.apply(xk));
	} 
	Function	   p2 = NumericalAlgorithms.polynomialInterpolation(tschebyscheff);
	Function	   p3 = NumericalAlgorithms.splineInterpolation(4, tschebyscheff, NumericalAlgorithms.NATURAL_SPLINE_INTERPOLATION, null);
	Function	   p4 = NumericalAlgorithms.splineInterpolation(4, tschebyscheff, NumericalAlgorithms.COMPLETE_SPLINE_INTERPOLATION, new Object[] {
	    new Double(-0.4), new Double(-4)
	});

	final Function b = NumericalAlgorithms.bezierCurve(0, 1, vf.valueOf(new double[][] {
	    {0,0},
	    {.2,1},
	    {1,.9},
	    {.9,0},
	    {.4,-.1}
	}));
	System.err.println("b(.5) = " + b.apply(vf.valueOf(.5)));

	ChartModel diag = new ChartModel();
	diag.setRainbow(true);
	Map attributes = new HashMap();
	attributes.put("color", Color.black);
	attributes.put("precision", new Double(0.05));
	diag.add(f, attributes);
	diag.add(equidistant);
	diag.add(p1);
	diag.add(tschebyscheff);
	diag.add(p2);
	diag.add(p3);
	diag.add(p4);
	diag.add(b);
	diag.setAutoScaling();
	diag.setScale(vf.CONST(2, vf.valueOf(1)));
	Frame  frame = new Frame();
	Plot2D plot = new Plot2D();
	plot.setModel(diag);
	plot.setAutoScaling(true);
	plot.addMouseListener(new CustomizerViewController(frame));
	frame.add(plot);
	frame.setSize(300, 200);
	new Closer(frame, true, true);
	frame.setVisible(true);
    } 

    static void test_integration() {
	System.out.println("test integration");
	Function f = Functions.pow(3);
	double test_case_a[] = {2, 4, 2, -3, 0};
	double test_case_b[] = {7, 4, 3, 3, 1};
	for (int i = 0; i < test_case_a.length; i++) {
	    Arithmetic a = vf.valueOf(test_case_a[i]), b = vf.valueOf(test_case_b[i]);
	    System.out.println("integral " + f + " from " + a + " to " + b + " is");
	    System.out.println("\tsymbolic\t" + MathUtilities.integrate(f, a, b) + " = " + ((Number) MathUtilities.integrate(f, a, b)).doubleValue());
	    System.out.println("\tnumerical\t" + NumericalAlgorithms.integrate(f, a, b));
	} 
	f = Functions.pow(9);
	for (int i = 0; i < test_case_a.length; i++) {
	    Arithmetic a = vf.valueOf(test_case_a[i]), b = vf.valueOf(test_case_b[i]);
	    System.out.println("integral " + f + " from " + a + " to " + b + " is");
	    System.out.println("\tsymbolic\t" + MathUtilities.integrate(f, a, b) + " = " + ((Number) MathUtilities.integrate(f, a, b)).doubleValue());
	    System.out.println("\tnumerical\t" + NumericalAlgorithms.integrate(f, a, b));
	} 
	f = Functionals.compose(Functions.exp, (Function) Functions.square.minus());
	for (int i = 0; i < test_case_a.length; i++) {
	    Arithmetic a = vf.valueOf(test_case_a[i]), b = vf.valueOf(test_case_b[i]);
	    System.out.println("integral " + f + " from " + a + " to " + b + " is");
	    try {
		System.out.println("\tsymbolic\t" + MathUtilities.integrate(f, a, b) + " = " + ((Number) MathUtilities.integrate(f, a, b)).doubleValue());
	    } catch (Throwable x) {
		System.out.println("\tsymbolic\t<not explicit>");
	    } 
	    System.out.println("\tnumerical\t" + NumericalAlgorithms.integrate(f, a, b));
	} 
    } 
}


