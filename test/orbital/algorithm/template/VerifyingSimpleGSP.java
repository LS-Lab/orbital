import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.Scalar;
import orbital.math.Values;
import orbital.math.MathUtilities;
import orbital.math.Real;
import java.util.*;

/**
 * A variation of SimpleGSP that knows how to check a solution for being optimal.
 */
class VerifyingSimpleGSP extends SimpleGSP {
    public VerifyingSimpleGSP(int start, int goal) {
	super(start,goal);
	this.g = getAccumulatedCostFunction();
    }
    private Function g;

    // diverse
    
    public String toString() {
    	return super.toString() + " with optimal cost " + getOptimalSolutionCost();
    }

    /**
     * Verify optimal solutions.
     * @return true if n is an optimal solution to this problem
     * (which we know because we cheat).
     */
    boolean isOptimalSolution(Object n) {
	return isSolution(n) && !(((Real)g.apply(n)).doubleValue() != getOptimalSolutionCost());
    } 

    /**
     * For verifying optimal solutions.
     */
    private int getOptimalSolutionCost() {
	int goal = getGoal();
	int start = getStart();
	return (
		goal < start			// goal is to the left?
		? start-goal			// simply step L till reaching the goal
		: start < goal			// goal is to the right?
		? (goal-start+1)/2 + (MathUtilities.odd(goal-start) ? 1 : 0)		// step RR until reaching the goal, but for an odd distance step L in between
		: - PAY_FOR_PASSING		// undo +PAY_FOR_PASSING since initial state already is solution
		) + PAY_FOR_PASSING;
    }
}
