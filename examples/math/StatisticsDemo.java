

import orbital.Adjoint;
import orbital.math.*;
import java.util.Arrays;
import orbital.math.functional.*;
import orbital.moon.math.CoordinateCompositeFunction;

public class StatisticsDemo {
	public static void main(String arg[]) throws Exception {
		descriptive();
		linRegCompare();
		regression();
	} 
	private static void descriptive() {
		Adjoint.print("DESCRIPTIVE", "Descriptive statistics");
		double v[] = {
			2.16, 2.44, 2.90, 2.04, .58, 1.26, 1.52, 4.02, 1.90, 2.22, 5.08, 3.44, 5.24, -.56, 2.7, 3.06, 3.1, 2.9, 4.26, 1.36, 5, 1.74, 2.52, 4.04, 1.14, 0.1, 6.52, 5.16, .46, 7.72, 3.34, .74, 1.04, 3.2, 5.3, 5.08, 5.08, 2.36, 2.2, 5.12, 3.76, 2.14, 3.34, 4.78, 4.8, 5.38, .62, 3.2, 4.14, 3.58
		};
		Adjoint.log.println("arithmeticMean=" + Stat.arithmeticMean(v) + " geometricMean=" + Stat.geometricMean(v) + " harmonicMean=" + Stat.harmonicMean(v));
		Adjoint.log.println("mean=" + Stat.mean(v) + ", var=" + Stat.variance(v) + ", stdDev=" + Stat.standardDeviation(v) + ", covar=" + Stat.coefficientOfVariation(v));
		java.util.Arrays.sort(v);
		Adjoint.log.println("sorted: " + MathUtilities.format(v));
		Adjoint.log.println("median=" + Stat.median(v) + ", quantile(0.25)=" + Stat.quantile(v, .25) + ", quantile(0.75)=" + Stat.quantile(v, .75) + ", trimmed(.2)=" + Stat.trimmedMean(v, .2) + ", meandev=" + Stat.meanDeviation(v) + "\n");
	} 

	private static void linRegCompare() {
		Adjoint.print("LINREGCOMPARE", "compare two linear regressions");

		// Linear Regression Statistics
		Adjoint.print("Linear Regression", "in two dee (2D)");
		double vx[] = {
			10, 10, 10, 10, 10, 20, 20, 20, 20, 20, 50, 50, 50, 50, 50, 100, 100, 100, 100, 100
		};
		double vy[] = {
			51.4, 44.2, 56.4, 33.6, 46.6, 51.5, 49.5, 52.6, 43.8, 75.4, 81.9, 100.2, 79.3, 72.9, 84.6, 135.6, 128.4, 112.6, 122.2, 129.2
		};
		Adjoint.log.println("mean=" + Stat.mean(vx) + "|" + Stat.mean(vy) + ", stdDev=" + Stat.standardDeviation(vx) + "|" + Stat.standardDeviation(vy) + ", correlation=" + Stat.coefficientOfCorrelation(vx, vy));
		double _ba = Stat.coefficientOfCorrelation(vx, vy) * Stat.standardDeviation(vy) / Stat.standardDeviation(vx);
		double _aa = Stat.mean(vy) - _ba * Stat.mean(vx);
		Adjoint.log.println("Simplified formula results in... y=" + _aa + "+" + _ba + "*x");

		Matrix A = Values.getInstance(vx.length, 2);
		A.setColumn(0, Values.valueOf(vx));
		A.setColumn(1, Values.valueOf(vy));
		Function	   fls[] = {
			Functions.one, Functions.id
		};
		final Function fl[] = {
			new CoordinateCompositeFunction(fls)
		};
		Adjoint.log.println("Exact linear regression results in... " + Stat.regression(fl, A));
		java.util.Arrays.sort(vx);
		java.util.Arrays.sort(vy);
		Adjoint.log.println("median=" + Stat.median(vx) + "|" + Stat.median(vy) + ", quantile(0.25)=" + Stat.quantile(vx, .25) + "|" + Stat.quantile(vy, .25) + ", quantile(0.75)=" + Stat.quantile(vx, .75) + "|" + Stat.quantile(vy, .75) + ", trimmed(.1)=" + Stat.trimmedMean(vx, .1) + "|" + Stat.trimmedMean(vy, .1));
		Adjoint.log.println();
	} 

	private static void regression() {
		Adjoint.print("REGRESSION", "Example for general linear regression");
		double		   data[][] = {
			 {1, 0},
			 {2, 0},
			 {3, 2},
			 {4, 6},
			 {5, 12}
		};
		Matrix		   Ex = Values.valueOf(data);
		Function	   fs[] = {
			Functions.one, Functions.id, Functions.square
		};
		final Function func = new CoordinateCompositeFunction(fs);
		Adjoint.print("regression", Ex + " regression...");
		Function f = Stat.functionalRegression(func, Ex);
		Adjoint.print("regression", "found: " + f);
		Adjoint.print("regression", "lets look if it fits and what it interpolates");
		Arithmetic x;
		for (int i = 1; i <= 5; i++) {
			x = Values.valueOf(i);
			Adjoint.log.println(x + "|" + f.apply(x) + "\t");
		} 
		x = Values.valueOf(4.5);
		Adjoint.log.println(x + "|" + f.apply(x) + "\t");
		x = Values.valueOf(5.5);
		Adjoint.log.println(x + "|" + f.apply(x) + "\t");
	} 
}
