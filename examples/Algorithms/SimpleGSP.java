import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.logic.functor.MutableFunction;
import orbital.math.Scalar;
import orbital.math.Values;
import orbital.math.MathUtilities;
import java.util.*;

/**
 * A very simple general search problem.
 * There is a row of places numbered from with integers.
 * The task is simply to get an agent from a starting positon to the goal with
 * the actions
 * <ul>
 *   <li>"go left (one field)"</li>
 *   <li>"go right two fields"</li>
 *   <li>"stay where you are"</li>
 *   <li>(each action cost is 1 with extra payment for passing the goal)</li>
 * </ul>
 * Well of course, the solution is to always go into the direction of the goal,
 * but our simple agent does not know anything about its environment.
 * He does not even know that there is only one single row to go. The only
 * things that our agent knows is which actions he could take, and an estimate
 * of the distance to the goal.
 * </p>
 * <p>
 * However, to complicate things a little bit, the agent must pay for each time
 * he passes the goal.
 * </p>
 * @version 1.0, 2001/09/24
 * @author  Andr&eacute; Platzer
 */
public class SimpleGSP implements GeneralSearchProblem {
    /**
     * How much an agent has to pay for passing the goal.
     * 0 to disable extra amount for passing.
     */
    static final int PAY_FOR_PASSING = 4;
    public static void main(String arg[]) {
	Function      h = createHeuristic();
	GeneralSearch s;

	// here we decide which exact search algorithm to use
	// the single difference in using another search algorithm
	// would only concern the constructor call
	s = new IterativeDeepeningAStar(h);

	GeneralSearchProblem problem = new SimpleGSP(1, 8);

	// really solve our problem
	System.out.println("solving " + problem);
	State solution = (State) s.solve(problem);

	System.out.println("Found solution:\n" + solution + " for total accumulated cost " + solution.getAccumulatedCost());
    } 

    protected static final Function createHeuristic() {
	return new Function() {
		/**
		 * Estimate distance to goal.
		 * The closer to the goal, the better the estimate.
		 */
		public Object apply(Object n) {
		    State s = (State) n;
		    return Values.getDefaultInstance().valueOf(estimate(Math.abs(s.position - goal)));
		} 
			
		/**
		 * Estimate v.
		 * The closer to zero, the better the estimate.
		 */
		private double estimate(double v) {
		    return v <= 1 ? v : Math.sqrt(v);
		}
	    };
    }

    /**
     * Where we aim to be.
     */
    private static int goal;
    /**
     * Where we start our task.
     */
    private int start;
    public SimpleGSP(int start, int goal) {
	this.start = start;
	this.goal = goal;
    }
    int getGoal() {
	return goal;
    }
    int getStart() {
	return start;
    }

    public Object getInitialState() {
	return new State(start, 0.0);
    }

    public MutableFunction getAccumulatedCostFunction() {
	return _accumulatedCostFunction;
    }
    private static final MutableFunction _accumulatedCostFunction = new MutableFunction() {
	    public Object apply(Object state) {
		return Values.getDefaultInstance().valueOf(((State)state).accumulatedCost);
	    }
	    public Object set(Object state, Object accumulatedCost) {
		Object old = Values.getDefaultInstance().valueOf(((State)state).accumulatedCost);
		((State)state).accumulatedCost = ((orbital.math.Real)accumulatedCost).doubleValue();
		return old;
	    }
	    public Object clone() {
		throw new UnsupportedOperationException();
	    }
	};

    public boolean isSolution(Object n) {
	State s = (State) n;
	return s.position == goal;
    } 

    private static final List actions = Arrays.asList(new String[] {
	"-L",
	"-RR",
	"-"
    });
    public Iterator actions(Object n) {
	// the possible actions
	return actions.iterator();
    } 

    public Iterator states(Object action, Object n) {
	String a = (String) action;
	State s = (State) n;
	// the result of the action
	Object r;
	if ("-L".equals(a))
	    r = new State(s.position - 1);
	else if ("-RR".equals(a))
	    r = new State(s.position + 2);
	else if ("-".equals(a))
	    r = new State(s.position);
	else
	    throw new InapplicableActionException("" + a);
	return Collections.singletonList(r).iterator();
    } 

    public TransitionModel.Transition transition(Object action, Object state, Object statep) {
	String a = (String) action;
	return new Transition(a, getCost((State)statep, a));
    } 
    
//     private State accumulateCost(State s, String action) {
// 	s.accumulatedCost += getCost(s, action);
// 	return s;
//     }     

    private double getCost(State s, String action) {
	if ((action.endsWith("-L") && s.position == goal)
	    || (action.endsWith("-RR") && goal <= s.position && s.position <= goal + 1)
	    || (action.endsWith("-") && s.position == goal))
	    return 1 + PAY_FOR_PASSING;
	else
	    return 1;
    }     

    public String toString() {
	return getClass().getName() + "[start=" + start + " to goal=" + goal + "]";
    }

    private static class State {
	int position;
	double accumulatedCost;
	public State(int position) {
	    this(position, Double.NaN);
	}
	public State(int position, double accumulatedCost) {
	    this.position = position;
	    this.accumulatedCost = accumulatedCost;
	}

	double getAccumulatedCost() {
	    return accumulatedCost;
	}

	public String toString() {
	    return (char) (position-1 + 'A') + "(" + getAccumulatedCost() + ")";
	}
    }
}
