import orbital.algorithm.template.*;
import orbital.math.MathUtilities;

/**
 * direct DP MatrixKlammerung.
 * Optimize association of matrix multiplications to minimize number of scalar multiplications.
 * This is valid since for matrices it is true that
 * (A1*A2)*A3 = A1*(A2*A3)
 * 
 * @version 0.8,, 2000/07/31
 * @author  Andr&eacute; Platzer
 */
public class MatrixKlammerung_Direct implements DynamicProgrammingProblem {

    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	new MatrixKlammerung_Direct(new int[] {
	    10, 100, 5, 50
	}).run();
    } 

    private final int n;
    private final int dimensions[];

    /**
     * memorize weights analogue to partialSolutions
     */
    private int[][]   weights;

    /**
     * Runnable-init entry point.
     */
    public MatrixKlammerung_Direct(int dimensions[]) {
	this.dimensions = dimensions;
	n = dimensions.length;
    }

    /**
     * Runnable-start entry point.
     */
    public void run() {
	new DynamicProgramming().solve(this);
    } 

    public Object[] getInitialPartialSolutions() {
	weights = new int[n][n];
	for (int i = 0; i < n; i++)
	    weights[i][i] = 0;
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
	final int i = part[0], j = part[1];
	System.err.println(MathUtilities.format(weights) + "\n");
	int w = Integer.MIN_VALUE;
	int bestk = i;

	// check for maximum option
	for (int k = i; k < j; k++) {
	    int wk = (weights[i][k] + weights[k + 1][j] - dimensions[i - 1] * dimensions[k] * dimensions[j]);
	    if (wk > w) {
		w = wk;
		bestk = k;
	    } 
	} 

	// memorize weight as well
	weights[i][j] = w;
	return new Integer(bestk);
    } 

    public Object merge(Object[] partialSolutions) {
	System.err.println("Solution:");
	System.err.println(MathUtilities.format(weights));
	return ((Object[]) partialSolutions[1])[n - 1];
    } 


    private static void dump(Object[][] a) {
	for (int i = 0; i < a.length; i++) {
	    for (int j = 0; j < a[i].length; j++)
		System.err.print((a[i][j] != null ? a[i][j] : ".") + ", \t");
	    System.err.println();
	} 
    } 
}
