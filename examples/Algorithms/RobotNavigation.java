import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.robotic.*;
import orbital.robotic.Map;
import java.util.*;
import java.util.List;
import java.io.*;
import java.awt.*;
import orbital.math.MathUtilities;
import orbital.awt.*;

/**
 * Robot navigation.
 * A robot navigates through a labyrinth with either deterministic or
 * non-deterministic actions searching for a goal.
 * Tumbling through the maze he will learn to find better ways after some trials.
 * @internal note that reinforcement learning problems are MDPs with c(s,a) = - E[r<sub>t+1</sub>|s<sub>t</sub>=s,a<sub>t</sub>=a]
 * @todo spawn a new example "Race Track Problem" [Barto et al. 1993]
 * @TODO: introduce turning backwards so that we do not need to turn twice. And what's its reaction?
 * @todo provide exact settings for more labyrinths. Implement field for "LOST" with negative reward.
 */
public class RobotNavigation implements MarkovDecisionProblem {
    public static final int	MOVE_DELAY = 400;
    public static final int	TRIALS = 50;
    public static final boolean DETERMINISTIC = true;

    public static void main(String arg[]) throws IOException {
	RobotNavigation nav = new RobotNavigation();
        InputStream input = new FileInputStream(arg.length > 0 ? arg[0] : "default.lab.txt");
	nav.view.load(input);
	input.close();
	Map map = nav.view.getMap();
	Moving start = new Moving(searchAny(map, ROBOT), Direction.South);
    	nav.init((Map) map.clone(), (Moving) start.clone());

        MarkovDecisionProcess planner;
        // here we decide which exact MDP planning algorithm to use
        // the single difference in using another planning algorithm
        // would only concern the constructor call
        planner = new RealTimeDynamicProgramming(nav.getHeuristic());
        //planner = new GaussSeidelDynamicProgramming(nav.getHeuristic(), nav.allStates(), 0.1);

	Frame f = new Frame();
	new Closer(f, true, true);
	f.add(nav.view);
	f.pack();
	f.setVisible(true);

        // really obtain a plan
        Function plan = planner.solve(nav);

        // multiple trials of following it (for Trial-RTDP to achieve convergence)
        for (int i = 0; i < TRIALS; i++) {
            try {Thread.sleep(2 * MOVE_DELAY);} catch(InterruptedException x) {}
            // follow the plan
            for (Object state = start; !nav.isSolution(state); state = nav.observe()) {
                nav.perform(plan.apply(state));
            }
            start = new Moving(searchAny(map, ROBOT), Direction.South);
	    nav.init((Map) map.clone(), (Moving) start.clone());
        }
    }

    
    public static final Character GOAL = new Character('G');
    public static final Character WALL = new Character('#');
    public static final Character ROBOT = new Character('R');
    public static final Character LOST = new Character('L');
	
    // original problem's data
    /**
     * the original problem's map.
     */
    private Map map;
    /**
     * the heuristic goal position on map.
     */
    private Position goalPosition;

    // for performing navigation 
    /**
     * the robot's current position on the map of view.
     */
    private Moving currentPosition;
    /**
     * the current map view.
     * view.getMap() may differ from map in that the robot moves on view
     * but not on the original map.
     */
    private MapView view;
	
    public RobotNavigation() {
	this.map = null;
	this.goalPosition = null;
	this.currentPosition = null;
	this.view = new RobotMapView(map);
    }

    public void init(Map map, Moving start) {
	this.map = map;
	this.currentPosition = start;
	goalPosition = searchAny(map, GOAL);
	// view only gets a clone of the original map, since we want the robot to wander on it
	view.setMap((Map) map.clone());
    }

    public boolean isSolution(Object state) {
	Position pos = (Position) state;
	return GOAL.equals(map.get(pos)) || goalPosition.equals(pos)
	    // negative "solution" when we lost the game
	    || LOST.equals(map.get(pos));
    }

    public Iterator actions(Object state) {
        return transitions.keySet().iterator();
    }
	
    /**
     * A map containing the possible transitions and transition probabilities
     * for each intended action.
     */
    private static final java.util.Map transitions = new HashMap();
    static {
	if (DETERMINISTIC) {
	    // deterministic actions
	    // we append a transition for resting at a place because this can happen when moving on a wall
	    transitions.put("FF",	new Transition[] {new Transition("FF", 1), new Transition(" ", 0)});
	    transitions.put("l",	new Transition[] {new Transition("l", 1)});
	    transitions.put("r",	new Transition[] {new Transition("r", 1)});
	    //transitions.put("b",	new Transition[] {new Transition("b", 1), new Transition(" ", 0)});
	    transitions.put(" ",	new Transition[] {new Transition(" ", 1)});
	} else {
	    // non-deterministic actions
	    transitions.put("FF",	new Transition[] {new Transition("FF", .73), new Transition("/LF/LF", .05), new Transition("/RF/RF", .05), new Transition("/LF/LFl", .05), new Transition("/RF/RFr", .05), new Transition("l", .03), new Transition("r", .03), new Transition(" ", 0.01)});
	    transitions.put("l",	new Transition[] {new Transition("l", .8), new Transition("b", .1), new Transition("r", .05), new Transition(" ", .05)});
	    transitions.put("r",	new Transition[] {new Transition("r", .8), new Transition("b", .1), new Transition("l", .05), new Transition(" ", .05)});
	    //transitions.put("b",	new Transition[] {new Transition("b", .4), new Transition("l", .1), new Transition("r", .1), new Transition(" ", .1), new Transition("lFF", .05), new Transition("rFF", .05), new Transition("lFFl", .05), new Transition("rFFr", .05), new Transition("/LF/LFl", .05), new Transition("/RF/RFr", .05), new Transition(" ", 0)});
	    transitions.put(" ",	new Transition[] {new Transition(" ", 1)});
	}
    }

    public Iterator states(Object action, Object state) {
	Moving s = (Moving) state;
	Set t = new HashSet();
	Transition[] ts = (Transition[]) transitions.get(action);
	for (int i = 0; i < ts.length; i++) {
	    Moving sp = (Moving) s.clone();
	    sp.move(ts[i].move);
	    // version with information caching:
	    //t.add(new Option(sp, transitionProbability(sp, s, action, ts[i]), getCost(s, action)));#
	    t.add(sp);
	}
	return t.iterator();
    }

    public ProbabilisticTransition transition(Object action, Object state, Object statep) {
	return new MarkovDecisionProblem.DefaultTransition(transitionProbability((Moving)statep, (Moving)state, action),
							   getCost((Moving)state, action));
    }
	
    private double transitionProbability(Moving sp, Moving s, Object action) {
        // check special cases, first
        
	// adjust transition model such that intended transitions into walls or off board are assigned a probability of 1 of resting at the original state
	// find out whether the action would normally lead us straight into a wall
        if (wouldBounceWall(s, action))
	    // bounced against a wall
	    return sp.equals(s) ? 1 : 0;
        
        // goal and lost states are terminal states. Then no action leads anywhere else.
        // (note that this is important for the undiscounted case to converge)
        if (GOAL.equals(map.get(s)) || LOST.equals(map.get(s)))
	    return sp.equals(s) ? 1 : 0;

	// normal case
	Transition[] ts = (Transition[]) transitions.get(action);
	for (int i = 0; i < ts.length; i++)
	    // would we get to sp by this move?
	    if (ts[i].explains(s, sp))
		return ts[i].probability;
        return 0;
    }

    /**
     * Check whether the given action in the given state would lead us into a wall or off board.
     */
    private boolean wouldBounceWall(Moving state, Object action) {
        Moving moving = (Moving) state.clone();
    	// move as intended, i.e. as if the world were deterministic
    	moving.move(((Transition[]) transitions.get(action))[0].move);
        return !map.inRange(moving) || WALL.equals(map.get(moving));
    }
    
	
    private double getCost(Moving state, Object action) {
        // where the action's target is
        Moving target = ((Moving) state.clone());
        target.move((String)action);
        if (!map.inRange(target) || WALL.equals(map.get(target)))
	    // bounced against a wall
	    return 70;
        if (LOST.equals(map.get(target)))
	    // bound to lose when this succeeds
	    return 200;
        return 2; // or return action.length();
    }


    // implementation helpers

    /**
     * Search all positions on map that equal o.
     */
    protected static List searchAll(Map map, Object o) {
	List l = new LinkedList();
	Rectangle r = map.getBounds();
	for (int y = r.y; y < r.height; y++)
	    for (int x = r.x; x < r.width; x++) {
		Position p = new Position(x, y);
		if (o.equals(map.get(p)))
		    l.add(p);
	    }
	return !l.isEmpty() ? l : null;
    }
    /**
     * Search any position on map that equals o.
     * Ties break randomly.
     */
    protected static Position searchAny(Map map, Object o) {
	List l = searchAll(map, o);
	return (Position) l.get((int) (Math.random() * l.size()));
    }

    /**
     * Get the set S of all states.
     * For academic toy algorithms, only.
     */
    private Set allStates() {
	// get all states (even states that can never be reached, at all)
	/*Set states = new HashSet();
	  for (int i = 0; i < map.getDimension().width; i++)
	  for (int j = 0; j < map.getDimension().height; j++) {
	  Moving s = new Moving(new Position(i, j), Direction.South);
	  if (WALL.equals(map.get(s)))
	  continue;
	  for (int k = 0; k < 4; k++) {
	  s.move(Move.Left);
	  states.add(s.clone());
	  }
	  }
	  return states;*/
	// get all reachable states, only
	//return MarkovDecisionProblemSearch.getReachableStates(this, currentPosition);
	return null;
    }

    // navigation methods
    
    private void perform(Object action) {
        String a = (String) action;
	// chose a non-deterministic transition
	double dice = Math.random();
	Transition[] ts = (Transition[]) transitions.get(action);
	double distribution = 0;
	for (int i = 0; i < ts.length; i++) {
	    distribution += ts[i].probability;
	    if (dice <= distribution) {
		a = ts[i].move;
		break;
	    }
	}

        Moving moving = (Moving) currentPosition.clone();
    	moving.move(a);
        //System.out.println(" --"+a+"--> "+moving);
        if (!view.getMap().inRange(moving) || WALL.equals(view.getMap().get(moving)))
	    if (action.equals(a))
		// intended illegal move
		throw new InternalError("intended illegal move " + action + " to " + (view.getMap().inRange(moving) ? view.getMap().get(moving) : "OutOfBoundsException") + "@" + moving);
	    else
		// accidentally hit the wall
		// then we rebounced to original position
		moving = currentPosition;
        view.setMap(currentPosition, new Character(' '));
        currentPosition = moving;
    	char robot;
    	switch (moving.direction.getDirection()) {
	case Direction.North: robot = '^'; break;
	case Direction.South: robot = 'v'; break;
	case Direction.East:  robot = '>'; break;
	case Direction.West:  robot = '<'; break;
	default: robot = 'R';
    	}
        view.setMap(currentPosition, new Character(robot));
        try {Thread.sleep(MOVE_DELAY);} catch(InterruptedException x) {}
    }

    private Object observe() {
    	return currentPosition;
    }
    
    // heuristic function
    
    /**
     * use euclidian distance plus turning cost to the goal as heuristic function.
     */
    private Function getHeuristic() {
	return new Function() {
		public Object apply(Object state) {
		    Moving s = (Moving) state;
		    double cost = manhattan(s, goalPosition);//s.subtract(goalPosition).length();
		    // whether we are on the wrong x/y coordinate.
		    cost += 2*turnDistance(s, goalPosition);
		    //System.out.println("\t\th("+s+")\t= " + cost);
		    return new Double(cost);
        	}
	    };
    }

	
    // utilities
	
    /**
     * The turn distance of m to p.
     * If m is at the center position '>' looking east, then this will return the numbers
     * on the corresponding fields.
     * <pre>
     * 2...211...1
     * .   ...   .
     * 2...211...1
     * 2...2>0...0
     * 2...211...1
     * .   ...   .
     * 2...211...1
     * </pre>
     */
    public static int turnDistance(Moving m, Position p) {
	Position t = m.direction.getDirectionVector();
	// component-wise normalized delta (p-m normalized in d<sub>&infin;</sub>-Norm).
	Position d = new Position(MathUtilities.sign(p.x - m.x), MathUtilities.sign(p.y - m.y));
	if (t.equals(d))
	    return 0;
	// s = t*d
	int s = t.x * d.x + t.y * d.y;
	return s < 0 ? 2 : 1;
    }

    /**
     * Get the manhattan distance d<sub>1</sub>.
     * Where d<sub>1</sub> is the metric induced by the 1-norm.
     * Visually speaking, the manhatten distance is the distance following
     * the rectangular streets of manhattan
     * = delta x + delta y.
     */
    private static int manhattan(Position a, Position b) {
    	return Math.abs(b.x - a.x) + Math.abs(b.y - a.y);
    }
    
    /**
     * Stochastic transition holder.
     */
    private static class Transition {
    	String move;
    	double probability;
    	public Transition(String move, double probability) {
	    this.move = move;
	    this.probability = probability;
    	}
    	public Transition() {
	    this(null, 0);
    	}

	/**
	 * Whether this transition would lead to source moving to destination.
	 */
	public boolean explains(Moving source, Moving destination) {
	    Moving m = (Moving) source.clone();
	    m.move(move);
	    // we would get to t by this move m?
	    return destination.equals(m);
	}
    }
}


// Utilities

class RobotMapView extends MapView {
    public RobotMapView(Map map) {
    	super(map);
    }

    protected Object newField(char ch) {
	return new Character(ch);
    }
    protected char fieldValue(Object field) {
	return ((Character) field).charValue();
    }
}

// /**
//  * Extract <em>some</em> features of a Markov Decision Problem, and formulate it
//  * as a state-model.
//  * This approach restricts the solution abilities, of course, but is convenient
//  * for simple sub tasks such as finding all reachable states.
//  * This class is some kind of deterministic projection into state space.
//  */
// class MarkovDecisionProblemSearch implements GeneralSearchProblem {
//     private final MarkovDecisionProblem problem;
//     private final Object initialState;
//     /**
//      * The set of states build up of states that reachable from initialState.
//      */
//     private Set reachable;
//     public MarkovDecisionProblemSearch(MarkovDecisionProblem problem, Object initialState) {
// 	this.problem = problem;
// 	this.initialState = initialState;
//     }
	
//     /**
//      * get all states that are reachable from initialState in the given MDP.
//      * Reachable are those states for that an action policy exists that has a
//      * non-zero probability of finally reaching them.
//      */
//     public static Set getReachableStates(MarkovDecisionProblem problem, Object initialState) {
// 	MarkovDecisionProblemSearch p = new MarkovDecisionProblemSearch(problem, initialState);
// 	new DepthFirstSearch().solve(p);
// 	return p.getReachableStates();
//     }
	
//     /**
//      * Get the computed set of all reachable states.
//      * @return the reachable set computed in the last run.
//      */
//     Set getReachableStates() {
// 	return reachable;
//     }
	
//     public Object getInitialState() {
// 	this.reachable = new HashSet();
//     	return initialState;
//     }
//     public boolean isSolution(Option n) {
//     	reachable.add(n.getState());
//     	return false;
//     }
//     /**
//      * Returns states that are reachable under any actions at all,
//      * regardless of transition probability.
//      */
//     public Iterator expand(Option n) {
// 	Set		   destinations = new HashSet();
// 	for (Iterator i = problem.actions(n.getState()); i.hasNext(); ) {
// 	    Object	   action = i.next();
// 	    Collection states = problem.nextStates(n.getState(), action);
// 	    double	   cost = problem.getCost(n.getState(), action);
// 	    for (Iterator j = states.iterator(); j.hasNext(); ) {
// 		Object st = j.next();

// 		// if the state has not already been reached but has a non-zero transition probability
// 		if (!reachable.contains(st) && problem.transitionProbability(st, n.getState(), action) != 0)
// 		    destinations.add(new Option(st, action, n.getCost() + cost));
// 	    } 
// 	} 
// 	return destinations.iterator();
//     }
//     public double getCost(Option n) {
//     	return problem.getCost(n.getState(), n.getAction());
//     }
// }
