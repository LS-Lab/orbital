import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.*;
import java.util.*;

/**
 * For 8-puzzle the goal is to place (n^2-1)=8 (or 15,...) tiles in the right order on an (n x n) sliding puzzle
 * by moving tiles next to the single empty tile.
 * <p>
 * Note that the generalization of 6-puzzle to n-puzzle is NP-complete.
 * </p>
 */
public class EightPuzzle implements GeneralSearchProblem {
    public static final int MAX_STEPS = 12;
    public static void main(String arg[]) {
	Function	  h = createHeuristic();
	GeneralSearch s;

	// here we decide which exact search algorithm to use
	// the single difference in using another search algorithm
	// would only concern the constructor call
	s = new AStar(h);

	// really solve our problem
	Option solution = s.solve(new EightPuzzle(8));

	System.out.println("Found solution:\n" + MathUtilities.format((int[][]) solution.getState()) + " for total accumulated cost " + solution.getCost());
    } 

    protected static final Function createHeuristic() {
	return new Function() {
		public Object apply(Object n) {
		    Option  o = (Option) n;
		    int[][] s = (int[][]) o.getState();
		    int	    pos[] = (int[]) o.getAction();

		    // no action yet? initial state is for free
		    if (pos == null)
			return Values.valueOf(0);
		    else
			return Values.valueOf(manhattan(pos, s[pos[0]][pos[1]]));

		    /*
		     * note that a better heuristic would be a pairwise manhattan distance
		     * adding +2 to the distance if two pieces need to pass each other
		     * in order to get to their destination (because they are on the same
		     * row or column, but in the wrong order)
		     */
		} 
	    };
    }
    /**
     * Get the manhattan distance d<sub>1</sub>.
     * Where d<sub>1</sub> is the metric induced by the 1-norm.
     * Visually speaking, the manhatten distance is the distance following
     * the rectangular streets of manhattan
     * = delta x + delta y.
     */
    private static int manhattan(int[] pos, int tile) {
	int[] dest = new int[] {
	    (tile - 1) % size, (tile - 1) / size
	};
	return Math.abs(dest[0] - pos[0]) + Math.abs(dest[1] - pos[1]);
    } 

    /**
     * direction enumeration
     */
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    public static final int MAX_MOVE = DOWN;

    /**
     * empty tile
     */
    public static final int EMPTY = 0;

    private static int		size;

    private int[][]			goal;

    public EightPuzzle(int tiles) {
	EightPuzzle.size = (int) Math.ceil(Math.sqrt(tiles + 1));
	if (tiles != size * size - 1)
	    throw new IllegalArgumentException("no such n-puzzle with n=" + tiles + " which is not a k^2-1");
	this.goal = goalState(size);
    }

    public Object getInitialState() {
	int[][] r = null;
	r = shuffle(goalState(size));
	System.out.println(MathUtilities.format(r) + " to be solved\n");
	return r;
    } 

    public boolean isSolution(Option n) {
	int[][] s = (int[][]) n.getState();
	System.out.println(MathUtilities.format(s) + " for " + n.getCost() + "\n");
	for (int i = 0; i < s.length; i++)
	    for (int j = 0; j < s[i].length; j++)
		if (s[i][j] != goal[i][j])
		    return false;
	return true;
    } 

    public Iterator expand(Option n) {
	int[][] s = (int[][]) n.getState();
	int	e[] = indexOf(s, EMPTY);
	List	ex = new LinkedList();
	for (int dir = 0; dir <= MAX_MOVE; dir++) {
	    int[] t = nextOf(e, dir);

	    // use the old position of the empty tile (is the new position of the tile moved)
	    // as the action
	    if (t != null)
		ex.add(new Option(swap(s, e, t), e, n.getCost() + getCost(null)));
	} 
	return ex.iterator();
    } 

    public double getCost(Option n) {
	return 1;
    } 


    // implementation helpers
	
    /**
     * search for the specified tile.
     * @return indices of the tile in the puzzle.
     */
    static int[] indexOf(int[][] s, int tile) {
	int e[] = new int[2];
	for (e[0] = 0; e[0] < s.length; e[0]++)
	    for (e[1] = 0; e[1] < s[e[0]].length; e[1]++)
		if (s[e[0]][e[1]] == tile)
		    return e;
	return null;
    } 

    /**
     * get a new array like s with the element at i and j swapped.
     */
    static int[][] swap(int[][] s, int[] i, int[] j) {
	int[][] n_s = new int[s.length][];
	for (int k = 0; k < s.length; k++)
	    n_s[k] = (int[]) s[k].clone();
	n_s[i[0]][i[1]] = s[j[0]][j[1]];
	n_s[j[0]][j[1]] = s[i[0]][i[1]];
	return n_s;
    } 

    /**
     * Get the indices of the tile next to i, or null if not on the sliding puzzle.
     */
    static int[] nextOf(int[] i, int direction) {
	int[] r = (int[]) i.clone();
	switch (direction) {
	case LEFT:
	    r[0]--;
	    break;
	case RIGHT:
	    r[0]++;
	    break;
	case UP:
	    r[1]--;
	    break;
	case DOWN:
	    r[1]++;
	    break;
	default:
	    throw new IllegalArgumentException();
	}
	return 0 <= r[0] && r[0] < size && 0 <= r[1] && r[1] < size ? r : null;
    } 

    /**
     * Get the indices of any tile around i.
     * Guarantee that the position is on the sliding puzzle.
     */
    private static int[] anyOf(int[] i) {
	List dirs = new ArrayList(4);
	for (int dir = 0; dir <= MAX_MOVE; dir++) {
	    int[] t = nextOf(i, dir);

	    // use the old position of the empty tile (is the new position of the tile moved)
	    // as the action
	    if (t != null)
		dirs.add(t);
	} 

	// chose one of them at random
	int chosen = (int) (Math.random() * dirs.size());
	return (int[]) dirs.get(chosen);
    } 

    /**
     * Shuffles a sliding puzzle by making several random moves.
     * @param probability is the probability of continuing the shuffling moves after each step.
     */
    private static int[][] shuffle(int[][] r) {
	int[] e = indexOf(r, EMPTY);
	for (int i = (int) (MAX_STEPS / 2 + Math.random() * MAX_STEPS / 2); i >= 0; i--) {
	    r = swap(r, e, e = anyOf(e));
	} 
	return r;
    } 

    /**
     * Get the goal state of size.
     */
    private static int[][] goalState(int size) {
	int[][] r = new int[size][size];
	for (int i = 0; i < r.length; i++)
	    for (int j = 0; j < r[i].length; j++)
		r[i][j] = i * size + j + 1;
	r[r.length - 1][r[r.length - 1].length - 1] = EMPTY;
	return r;
    } 
}
