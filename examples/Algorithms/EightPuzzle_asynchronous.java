

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.util.StreamMethod;
import orbital.math.*;
import java.util.*;

/**
 * For 8-puzzle the goal is to place (n^2-1)=8 (or 15,...) tiles in the right order on an (n x n) sliding puzzle
 * by moving tiles next to the single empty tile.
 */
public class EightPuzzle_asynchronous extends EightPuzzle {
	public static void main(String arg[]) {
		Function	  h = createHeuristic();
		GeneralSearch s;

		// here we decide which exact search algorithm to use
		// the single difference in using another search algorithm
		// would only concern the constructor call
		s = new AStar(h);

		// really solve our problem
		Option solution = s.solve(new EightPuzzle_asynchronous(8));

		System.out.println("Found solution:\n" + MathUtilities.format((int[][]) solution.getState()) + " for total accumulated cost " + solution.getCost());
	} 

	public EightPuzzle_asynchronous(int tiles) {
		super(tiles);
	}

	public Iterator expand(final Option n) {
		return new StreamMethod(false) {
			public void runStream() {
        		int[][] s = (int[][]) n.getState();
        		int		e[] = indexOf(s, EMPTY);
        		List	ex = new LinkedList();
        		for (int dir = 0; dir <= MAX_MOVE; dir++) {
        			int[] t = nextOf(e, dir);
        
        			// use the old position of the empty tile (is the new position of the tile moved)
        			// as the action
        			if (t != null)
        				resumedReturn(new Option(swap(s, e, t), e, n.getCost() + getCost(null)));
        		} 
			}
		}.apply();
	} 

}
