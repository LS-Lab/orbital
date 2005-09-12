

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.util.StreamMethod;
import orbital.math.*;
import java.util.*;

/**
 * For 8-puzzle the goal is to place (n^2-1)=8 (or 15,...) tiles in
 * the right order on an (n x n) sliding puzzle by moving tiles next
 * to the single empty tile.
 * Implementation variant using asynchronous expansion of actions.
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
	State solution = (State) s.solve(new EightPuzzle_asynchronous(8));

	System.out.println("Found solution:\n" + solution);
    } 

    public EightPuzzle_asynchronous(int tiles) {
	super(tiles);
    }

    public Iterator actions(final Object n) {
	return new StreamMethod(false) {
		public void runStream() {
		    EightPuzzle.State s = (EightPuzzle.State) n;
		    int   empty[] = indexOf(s.slides, EMPTY);
		    for (int dir = 0; dir <= MAX_MOVE; dir++) {
			int[] pos = nextOf(empty, dir);

			if (pos != null)
			    resumedReturn(pos);
		    } 
		}
	    }.apply();
    } 

}
