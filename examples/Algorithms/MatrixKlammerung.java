

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.MathUtilities;
import java.util.Collection;

import java.util.LinkedList;

/**
 * DP MatrixKlammerung.
 * Optimize association of matrix multiplications to minimize number of scalar multiplications.
 * This is valid since for matrices it is true that
 * (A1*A2)*A3 = A1*(A2*A3)
 * 
 * @version 0.8,, 2000/07/31
 * @author  Andr&eacute; Platzer
 */
public class MatrixKlammerung extends DynamicProgrammingOptimizingProblem {

    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	new MatrixKlammerung(new int[] {
	    10, 100, 5, 50
	}).run();
    } 

    private final int n;
    private final int dimensions[];

    /**
     * Runnable-init entry point.
     */
    public MatrixKlammerung(int dimensions[]) {
	this.dimensions = dimensions;
	n = dimensions.length;
    }

    /**
     * Runnable-start entry point.
     */
    public void run() {
	System.out.println("solved: " + new DynamicProgramming().solve(this));
    } 

    public Object[] getInitialPartialSolutions() {
	Double[][] w = new Double[n][n];
	for (int i = 0; i < n; i++)
	    w[i][i] = new Double(0);
	init(w);
	return new Object[n][n];
    } 

    public boolean isSolution(Object[] partialSolutions) {
	return ((Object[]) partialSolutions[1])[n - 1] != null;
    } 

    private int o_h = 1;
    private int o_i = 1;

    /**
     * loop through diagonals right from the main diagonal.
     * Step through each single diagonal in any order, f.ex. top-down.
     */
    public int[] nextPart() {
	if (o_i + o_h >= n) {
	    o_i = 1;
	    if (o_i + ++o_h >= n)
		throw new InternalError("should already end now");
	} 
	int[] r = new int[] {
	    o_i, o_i + o_h
	};
	o_i++;
	return r;
    } 
    public Object solve(int[] part, Object[] partialSolutions) {
	System.err.println(MathUtilities.format(getPartialWeights()) + "\n");
	return super.solve(part, partialSolutions);
    } 

    public Collection getOptionsFor(int[] part) {
	final int  i = part[0], j = part[1];
	Collection opt = new LinkedList();
	for (int k = i; k < j; k++)
	    opt.add(new Integer(k));
	return opt;
    } 
    public Function getWeightingFor(final int[] part) {
	final int i = part[0], j = part[1];
	return new Function() {
		public Object apply(Object o) {
		    int k = ((Number) o).intValue();
		    return new Double(getPartialWeight(new int[] {
			i, k
		    }) + getPartialWeight(new int[] {
			k + 1, j
		    }) - dimensions[i - 1] * dimensions[k] * dimensions[j]);
		} 
	    };
    } 

    public Object merge(Object[] partialSolutions) {
	System.err.println("Solution:\n" + MathUtilities.format(getPartialWeights()));
	return ((Object[]) partialSolutions[1])[n - 1];
    } 
}
