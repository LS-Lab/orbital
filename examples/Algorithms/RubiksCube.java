import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.logic.functor.MutableFunction;
import orbital.math.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.text.*;
import java.awt.Color;
import util.Basic;

/**
 * Solves Rubik's Cube with state search.
 * Since Rubik's Cube has a rather large state space, solving an
 * arbitrary cube might still take a while.
 * <p><a name="Theory"></a>
 * For a 2 by 2 cube the state space has a size of at most (2*3)<sup>d</sup>.
 * So for a depth of 14 steps its size limit is 78364164096.
 * The exact size of the state space is somewhat difficult to calculate due to the
 * various interpretations of states and their symmetries.
 * I still do not know the number of moves neccessary to reach every state possible.
 * </p>
 * <p>
 * For a 3 by 3 cube the (reachable) state space is of size
 * 2<sup>12</sup>·12!·3<sup>8</sup>·8!/12 &asymp; 4.3*10<sup>19</sup>.
 * An upper bound for the size of the state space reachable by performing at most d actions is:
 * <center>1 + (6*2) + (6*2)*11<sup>1</sup> + (6*2)*11<sup>2</sup> + ... + (6*2)*11<sup>n-1</sup> = 1 + (6*2) * 11<sup>n-1</sup> / (11 - 1)</center>
 * Because<ol>
 * <li>there is the single initial state</li>
 * <li>with the first action you can chose either of the
 * six sides and turn them into one of two directions</li>
 * <li>with the following action you cannot reach another 6*2 new states
 * since there is one action that takes us back to the previous state.
 * So new states are at most 6*2-1 = 11. In fact, for the third step this number drops again.
 * But this calculation still leads to an upper bound.</li>
 * </ol>
 * If actions like L2 would be allowed as single steps (and not as L1, L1)
 * then the number of reachable states is below
 * <center>1 + (6*3) * 15<sup>n-1</sup> / (15 - 1)</center>
 * </p>
 * <p>
 * By comparing the size of the state space and the size of the state space reachable by performing
 * at most d actions, one can deduce that no algorithm can exist
 * that is able to solve every initial cube with 20 moves or less.
 * Note that this minimum depth required still depends upon the exact
 * moves allowed.</p>
 * 
 * @version 0.9, 2000/09/22
 * @author  Ute Platzer
 * @author  Andr&eacute; Platzer
 * @see <a href="http://www.npac.syr.edu/projects/java/magic/">Visualization of Rubik's Cube</a>
 */
public class RubiksCube implements GeneralSearchProblem {

    /**
     * Up to which depth to search for a solution.
     */
    public static final int MAX_STEPS = 8;

    // enum for SEQUENCE
    /**
     * The SEQUENCE mode with a random sequence of at most depth MAX_STEPS.
     */
    public static final int RANDOM = 0;
    /**
     * The SEQUENCE mode with a complex sequence of swapping two edges without distrubing the rest.
     * Has a known minimum step depth of 12.
     * The complex sequence cannot be solved, if MAX_STEPS < 12.
     */
    public static final int COMPLEX = 1;
    /**
     * The SEQUENCE mode with a standard sequence 
     * The complex sequence cannot be solved, if MAX_STEPS is too small.
     */
    public static final int STANDARD = 2;
    /**
     * Choose which SEQUENCE to solve.
     */
    public static int SEQUENCE = RANDOM;

    /**
     * The size of the Rubik's cube to solve.
     */
    public static final int SIZE = 2;
    public static void main(String arg[]) throws Exception {
	DateFormat df = new SimpleDateFormat("H:mm:ss:S");
	df.setTimeZone(TimeZone.getTimeZone("Greenwich/Meantime"));
	Date	 loadeta;
	Function h;
	try {
	    File databaseFile = new File(RubiksCubeCreatePattern.patternDatabaseFile);
	    if (!databaseFile.exists()) {
		System.err.println("File \"" + databaseFile + "\" does not exist.  Will create one.");
		RubiksCubeCreatePattern.main(arg);
	    } 

	    // load patterns
	    System.out.println("Loading");
	    long	loading = System.currentTimeMillis();
	    InputStream fis = new FileInputStream(databaseFile);
	    if (RubiksCubeCreatePattern.compressed)
		fis = new InflaterInputStream(fis);
	    ObjectInputStream is = new ObjectInputStream(fis);
	    final int	      patternDepth = is.readInt();
	    Map		      patternDatabase = (Map) is.readObject();
	    is.close();
	    h = new Function() {
		    public Object apply(Object o) {

			// out of exact pattern heuristic, so we need more than patternDepth steps
			// XXX: need better heuristics!! Count number of tiles in wrong position, etc.
			return Values.getDefaultInstance().valueOf(patternDepth + 1);
		    } 
		};
	    h = new HeuristicAlgorithm.PatternDatabaseHeuristic(h, patternDatabase);
	    loadeta = new Date(System.currentTimeMillis() - loading);
	    System.out.println("Completed loading " + df.format(loadeta));
	    if (patternDepth != RubiksCubeCreatePattern.MAX_STEPS) {
		System.out.println("Warning: File \"" + databaseFile + "\" does not seem up to date. Consider calling");
		System.out.println("\tjava RubiksCubeCreatePattern");
		System.out.println("to re-create \"" + databaseFile + "\".");
	    } 
	} catch (IOException x) {
	    System.err.println(x);
	    System.err.println("Make sure that the pattern database file has been created by calling");
	    System.err.println("\tjava RubiksCubeCreatePattern");
	    return;
	} 

	System.out.println("Start");
	long	      start = System.currentTimeMillis();

	GeneralSearch s;

	// here we decide which exact search algorithm to use
	// the single difference in using another search algorithm
	// would only concern the constructor call
	s = new IterativeDeepeningAStar(h);
	//s = new IterativeExpansion(h);

		
	// really solve our problem
	Cube solution = (Cube) s.solve(new RubiksCube(SIZE));

	Date eta = new Date(System.currentTimeMillis() - start);

	console.color(Color.white);
	if (solution != null) {
	    System.out.println("Found:\n" + solution + "\n");
	    printcubus(solution.feld, 2, 30);
	    console.color(Color.white);
	    console.LOCATE(4, 9);
	    console.print("total accumulated cost of " + NumberFormat.getInstance().format(solution.accumulatedCost) + " steps", false);
	} else {
	    System.out.println("NO solution");
	    console.LOCATE(30, 2);
	    console.print("NO solution");
	} 
	console.LOCATE(4, 10);
	console.print("Duration  " + df.format(eta), false);
	console.LOCATE(4, 11);
	console.print("Load Time " + df.format(loadeta), false);
    } 

    // sides of the cube
    public static final int	   left = 0;
    public static final int	   back = 1;
    public static final int	   top = 2;
    public static final int	   front = 3;
    public static final int	   right = 4;
    public static final int	   down = 5;
    public static final String names[] = {
	"L", "B", "T", "F", "R", "D"
    };

    // colors
    public static final int	   orange = 0;
    public static final int	   blue = 1;
    public static final int	   yellow = 2;
    public static final int	   white = 3;
    public static final int	   red = 4;
    public static final int	   green = 5;
    private static final Color colors[] = {
	Color.orange.darker(), Color.blue, Color.yellow.brighter(), Color.white, Color.red, Color.green
    };

    /**
     * The size of the Rubik's cube.
     */
    protected static int	   size;
    /**
     * The actual problem to solve.
     */
    private final Cube _initialState;

    public RubiksCube(int size) {
	RubiksCube.size = size;
	if (size != 2)
	    throw new InternalError("only implemented for size 2");
	this._initialState = constructInitialState();
    }

    public MutableFunction getAccumulatedCostFunction() {
	return _accumulatedCostFunction;
    }
    private static final MutableFunction _accumulatedCostFunction = new MutableFunction() {
	    public Object apply(Object state) {
		return Values.getDefaultInstance().valueOf(((Cube)state).accumulatedCost);
	    }
	    public Object set(Object state, Object accumulatedCost) {
		Object old = Values.getDefaultInstance().valueOf(((Cube)state).accumulatedCost);
		((Cube)state).accumulatedCost = ((orbital.math.Real)accumulatedCost).doubleValue();
		return old;
	    }
	    public Object clone() {
		throw new UnsupportedOperationException();
	    }
	};

    /**
     * Pose the problem by constructing the initial Rubik's cube state.
     */
    private Cube constructInitialState() {
	// 'mache einen heilen Würfel:
	Cube c = new Cube(size, 0.0);
	switch (SEQUENCE) {
	case COMPLEX:
	    // '2 ecken gedreht
	    c.feld[16] = blue;
	    c.feld[6] = yellow;
	    c.feld[9] = red;
	    c.feld[10] = red;
	    c.feld[19] = white;
	    c.feld[13] = yellow;
	    break;
	case STANDARD: 
	    // 'test: einige drehungen
	    c.drehe(2, -1); c.drehe(2, -1); c.drehe(1, 1); c.drehe(2, 1);
	    //c.drehe(1, 1); //c.drehe(2, -1); c.drehe(2, -1); c.drehe(3, -1); c.drehe(2, -1); c.drehe(3, 1); c.drehe(2, -1);
	    break;
	case RANDOM:
	    for (int i = 0; i < MAX_STEPS; i++) {
		int seite = (int) (left + Math.random() * (down - front + 1));
		int richtung = -1 + (int) (Math.random() * 2);
		if (richtung == 0)
		    richtung = 1;
		c.drehe(seite, richtung);
	    } 
	    break;
	default:
	    throw new IllegalStateException(SEQUENCE + " is an illegal mode for SEQUENCE");
	} 

	return c;
    } 

    public Object getInitialState() {
	System.out.println(_initialState + " to be solved\n");
	printcubus(_initialState.feld, 2, 2);
	return _initialState.clone();
    }

    public boolean isSolution(Object n) {
	Cube c = (Cube) n;
	if (c.isGood()) {
	    System.out.println("Solution: " + n);
	    //@todo we could also print a protocol of all moves that lead to the solution
	    return true;
	} 
	return false;
    } 

    public Iterator actions(Object n) {
	Cube s = (Cube) n;
	List ex = new LinkedList();
	for (int seite = front; seite <= down; seite++)
	    for (int dir = -1; dir <= 1; dir += 2) {
		Cube t = (Cube) s.clone();
		t.drehe(seite, dir);
		ex.add(t);
		// could protocol action per s.getAction() + "," + names[seite] + dir
	    } 
	return ex.iterator();
    } 

    public Iterator states(Object action, Object state) {
	return Collections.singleton(action).iterator();
    } 

    public TransitionModel.Transition transition(Object action, Object state, Object statep) {
	// uniform cost 1
	return new Transition(action, 1);
    } 


    // implementation helpers
	
    /**
     * The cube state class.
     */
    protected static class Cube {
	int[] feld;
	double accumulatedCost;

	private Cube(int[] field, double accumulatedCost) {
	    this.feld = field;
	    this.accumulatedCost = accumulatedCost;
	}
	private Cube(int[] field) {
	    this(field, Double.NaN);
	}

	/**
	 * Create initial cube with all sides good.
	 */
	public Cube(int size) {
	    this(size, Double.NaN);
	}
	/**
	 * Create initial cube with all sides good.
	 */
	public Cube(int size, double accumulatedCost) {
	    this(new int[size * size * 6], accumulatedCost);
	    if (size != 2)
		throw new InternalError("not implemented for size " + size);
	    for (int i = 0; i < 4; i++)
		feld[i] = orange;
	    for (int i = 4; i < 8; i++)
		feld[i] = blue;
	    for (int i = 8; i < 12; i++)
		feld[i] = yellow;
	    for (int i = 12; i < 16; i++)
		feld[i] = white;
	    for (int i = 16; i < 20; i++)
		feld[i] = red;
	    for (int i = 20; i < 24; i++)
		feld[i] = green;
	}

	public Object clone() {
	    return new Cube((int[]) feld.clone(), accumulatedCost);
	} 

	public boolean equals(Object o) {
	    if (!(o instanceof Cube))
		return false;
	    Cube b = (Cube) o;
	    if (feld.length != b.feld.length)
		return false;
	    for (int i = 0; i < feld.length; i++)
		if (feld[i] != b.feld[i])
		    return false;
	    return true;
	} 

	/**
	 * good hashCode required for pattern database.
	 */
	public int hashCode() {
	    int hash = 0;
	    int shift = 0;
	    for (int i = 0; i < feld.length; i++) {
		hash |= feld[i] << shift;
		if ((shift += 3) + 2 >= 32)
		    shift -= 32 - 2;
	    } 
	    return hash;
	} 

	public String toString() {
	    return MathUtilities.format(feld) + "\n for total accumulated cost " + accumulatedCost;
	} 

	public boolean isGood() {
	    for (int i = 0; i < feld.length; i = i + 4) {
		if (!(feld[i] == feld[i + 1] && feld[i] == feld[i + 2] && feld[i] == feld[i + 3]))
		    return false;
	    } 
	    return true;
	} 

	public void drehe(int seite, int richtung) {
	    switch (seite) {
	    case left:		// 'left - okay
		reihentausch(0, 1, 2, 3, richtung);
		reihentausch(8, 12, 22, 4, richtung);
		reihentausch(11, 15, 21, 7, richtung);
		break;
	    case back:	// 'back
		reihentausch(4, 5, 6, 7, richtung);
		reihentausch(1, 21, 17, 9, richtung);
		reihentausch(0, 20, 16, 8, richtung);
		break;
	    case top:		// 'top
		reihentausch(8, 9, 10, 11, richtung);
		reihentausch(1, 6, 19, 12, richtung);
		reihentausch(2, 7, 16, 13, richtung);
		break;
	    case front:		// 'front
		reihentausch(12, 13, 14, 15, richtung);
		reihentausch(3, 11, 19, 23, richtung);
		reihentausch(2, 10, 18, 22, richtung);
		break;
	    case right:	// 'right
		reihentausch(16, 17, 18, 19, richtung);
		reihentausch(9, 5, 23, 13, richtung);
		reihentausch(10, 6, 20, 14, richtung);
		break;
	    case down:		// 'down
		reihentausch(20, 21, 23, 23, richtung);
		reihentausch(5, 0, 15, 18, richtung);
		reihentausch(4, 3, 14, 17, richtung);
		break;
	    default:
		throw new InternalError();
	    }
	} 

	protected void reihentausch(int a, int b, int c, int d, int richtung) {
	    int temp = feld[a];
	    switch (richtung) {
	    case -1:	// gegen den uhrzeigersinn
		feld[a] = feld[b];
		feld[b] = feld[c];
		feld[c] = feld[d];
		feld[d] = temp;
		break;
	    case 1:
		feld[a] = feld[d];
		feld[d] = feld[c];
		feld[c] = feld[b];
		feld[b] = temp;
		break;
	    default:
		throw new InternalError();
	    }
	} 


        /*protected int seiteheil (int feld[], int seite) {
          int i = seite * 4 - 3;
          if (feld[i] == feld[i + 1] && feld[i] == feld[i + 2] && feld[i] == feld[i + 3] ) {
	  //'pr?fe kanten
	  switch(seite) {
	  case 1:
	  if (feld[8] == feld[11] && feld[12] == feld[15] && feld[4] == feld[7] && feld[21] == feld[22]) return 1;
	  break;
	  case 2:
	  if (feld[8] == feld[9] && feld[0] == feld[1] && feld[16] == feld[17] && feld[20] == feld[21]) return 1;
	  break;
	  case 3:
	  if (feld[7] == feld[6] && feld[16] == feld[19] && feld[12] == feld[13] && feld[1] == feld[2]) return 1;
	  break;
	  case 5:
	  if (feld[10] == feld[9] && feld[13] == feld[14] && feld[6] == feld[5] && feld[20] == feld[23]) return 1;
	  break;
	  case 4:
	  if (feld[11] == feld[10] && feld[18] == feld[19] && feld[2] == feld[3] && feld[23] == feld[22]) return 1;
	  break;
	  case 6:
	  if (feld[18] == feld[17] && feld[15] == feld[14] && feld[4] == feld[5] && feld[0] == feld[3]) return 1;
	  break;
	  }
          }
          return 0;
	  }*/

    }


    private static Basic console = null;

    /**
     * Display the cube.
     */
    protected static void printcubus(int feld[], int y, int x) {

	// lazy instantiation of the frame for displaying
	if (console == null)
	    Basic.show(console = new Basic(40, 10), true, true);
	String f = "#";

	if (x == 0 || y == 0) {
	    y = console.CSRLIN();
	    x = console.POS(0);
	    if (y > 19) {
		console.inkey();
		console.cls();
		x = y = 1;
	    } 
	} 
	console.LOCATE(x + 2 * f.length(), y);
	console.color(colors[feld[4]]);
	console.print(f, false);
	console.color(colors[feld[5]]);
	console.print(f, false);
	console.LOCATE(x + 2, y + 1);
	console.color(colors[feld[7]]);
	console.print(f, false);
	console.color(colors[feld[6]]);
	console.print(f, false);

	console.LOCATE(x, y + 2);
	for (int i = 0; i < 17; i += 8) {
	    console.color(colors[feld[i]]);
	    console.print(f, false);
	    console.color(colors[feld[i + 1]]);
	    console.print(f, false);
	} 
	console.color(colors[feld[20]]);
	console.print(f, false);
	console.color(colors[feld[21]]);
	console.print(f, false);

	console.LOCATE(x, y + 3);
	for (int i = 3; i < 20; i += 8) {
	    console.color(colors[feld[i]]);
	    console.print(f, false);
	    console.color(colors[feld[i - 1]]);
	    console.print(f, false);
	} 
	console.color(colors[feld[23]]);
	console.print(f, false);
	console.color(colors[feld[22]]);
	console.print(f, false);


	console.LOCATE(x + 2 * f.length(), y + 4);
	console.color(colors[feld[12]]);
	console.print(f, false);
	console.color(colors[feld[13]]);
	console.print(f, false);
	console.LOCATE(x + 2, y + 5);
	console.color(colors[feld[15]]);
	console.print(f, false);
	console.color(colors[feld[14]]);
	console.print(f);
    } 
}
