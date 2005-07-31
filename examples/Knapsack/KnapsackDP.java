

import orbital.math.MathUtilities;

/**
 * Knapsack problem solved with dynamic programming.
 * Knapsack is NP-complete.
 * 
 * @author Uwe A&szlig;mann
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @TODO: implements DynamicProgrammingProblem
 */
public class KnapsackDP {
    public static void main(String arg[]) {
	new KnapsackDP(6, new int[] {
	    1, 2, 2, 3, 4, 5
	}).solve();
    } 

    private static final int NOSOLUTION = 0;
    private static final int SOL_TAKE = 1;
    private static final int SOL_LEAVE = 9;

    private int[]			 element;
    private int				 size;

    /*
     * <ul>
     * <li> dp[i, j], where i = #Elemente, j = 0..Rucksackgroesse
     * <li> dp[i, j, 0] == NOSOLUTION <=> Loesung existiert nicht
     * <li> dp[i, j, 0] == 1 <=> Loesung existiert
     * <li> dp[i, j, 1] == SOL_LEAVE <=> Loesung existiert ohne i­ tes Element
     * <li> dp[i, j, 1] == SOL_TAKE <=> Loesung existiert mit i­ tem Element
     * </ul>
     */
    private double			 dp[][];
    public KnapsackDP(int size, int[] element) {
	this.size = size;
	this.element = element;
	dp = new double[element.length][size];
    }


    public void solve() {
	solveByDynamicProgramming();
	System.out.println(MathUtilities.format((double[][]) dp));
    } 

    /**
     * Method declaration
     * 
     * O(n*K)
     * @see
     */
    private void solveByDynamicProgramming() {

	for (int i = 0; i < size; i++) {	// initialize
	    dp[0][i] = NOSOLUTION;
	} 

	dp[0][0] = SOL_LEAVE;

	for (int i = /* 0 */ 1; i < element.length; i++) {
	    for (int k = 0; k < size; k++) {
		dp[i][k] = NOSOLUTION;	  // initialize

		if (dp[i - 1][k] != NOSOLUTION) {
		    dp[i][k] = SOL_LEAVE;
		} else {
		    if (k - element[i] >= 0) {
			if (dp[i - 1][k - element[i]] != NOSOLUTION) {
			    dp[i][k] = SOL_TAKE;
			} 
		    } 
		} 
	    } 
	} 
    } 

}
