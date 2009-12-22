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
 * <p id="Theory">
 * For a 2 by 2 cube the (reachable) state space is of size
 * &le; 3<sup>8/sup>&middot;8!.
 * An upper bound for the size of the state space reachable by performing at most d actions is:
 * (2*3)<sup>d</sup>.
 * So for a depth of 14 steps its size limit is 78364164096.
 * The exact size of the state space is somewhat difficult to calculate due to the
 * various interpretations of states and their symmetries.
 * I still do not know the number of moves neccessary to reach every possible state.
 * </p>
 * <p>
 * For a 3 by 3 cube the (reachable) state space is of size
 * 2<sup>12</sup>&middot;12!&middot;3<sup>8</sup>&middot;8!/12 &asymp; 4.3*10<sup>19</sup>.
 * An upper bound for the size of the state space reachable by performing at most d actions is:
 * <center>1 + (6*2) + (6*2)*11<sup>1</sup> + (6*2)*11<sup>2</sup> + ... + (6*2)*11<sup>n-1</sup> = 1 + (6*2) * 11<sup>n-1</sup> / (11 - 1)</center>
 * Because
 * <ol>
 *   <li>there is the single initial state</li>
 *   <li>with the first action you can chose either of the
 *   six sides and turn them into one of two directions</li>
 *   <li>with the following action you cannot reach another 6*2 new states
 *   since there is one action that takes us back to the previous state.
 *   So new states are at most 6*2-1 = 11. In fact, for the third step this number drops again.
 *   But this calculation still leads to an upper bound.</li>
 * </ol>
 * If actions like L+2 would be allowed as single steps (and not as L+1, L+1)
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
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see <a href="http://www.npac.syr.edu/projects/java/magic/">Visualization of Rubik's Cube</a>
 */
public class RubiksCube implements GeneralSearchProblem {

    /**
     * Up to which depth to search for a solution.
     */
    public static final int MAX_STEPS = 16;

    // enum for SEQUENCE
    /**
     * The SEQUENCE mode with a random sequence of at most depth MAX_STEPS.
     */
    public static final int RANDOM = 0;
    /**
     * The SEQUENCE mode with a complex sequence of swapping two edges without distrubing the rest.
     * Has a known minimum step depth of 12.
     * The complex sequence cannot be solved, with MAX_STEPS < 12.
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
     * Whether to restrict actions "canonically", and allow only
     * turning front/right/down.  These "canonical" actions
     * can(@todo?) sometimes emulate the others, but will generally
     * require more moves.  Except for rotational symmetries of the
     * whole cube, which would allow the restriction without changing
     * the search depth.
     */
    private static final boolean RESTRICT_TO_CANONICAL_ACTIONS = true;

    /**
     * The size of the Rubik's cube to solve.
     */
    public static final int SIZE = 2;
    public static void main(String arg[]) throws Exception {
        DateFormat df = new SimpleDateFormat("H:mm:ss:S");
        df.setTimeZone(TimeZone.getTimeZone("Greenwich/Meantime"));
        Date     loadeta;
        Function h;
        try {
            File databaseFile = new File(RubiksCubeCreatePattern.patternDatabaseFile);
            if (!databaseFile.exists()) {
                System.err.println("File \"" + databaseFile + "\" does not exist.  Will create one.");
                RubiksCubeCreatePattern.main(arg);
            } 

            // load patterns
            System.out.println("Loading");
            long        loading = System.currentTimeMillis();
            InputStream fis = new FileInputStream(databaseFile);
            if (RubiksCubeCreatePattern.compressed)
                fis = new InflaterInputStream(fis);
            ObjectInputStream is = new ObjectInputStream(fis);
            final int         patternDepth = is.readInt();
            final Map         patternDatabase = (Map) is.readObject();
            is.close();
            h = new Function() {
                    final Real UNDERESTIMATE = Values.getDefaultInstance().valueOf(patternDepth + 1);
                    public Object apply(Object o) {
                        // out of exact pattern heuristic, so we need more than patternDepth steps
                        // XXX: need better heuristics!! Count number of tiles in wrong position, etc.
                        //@fixme also we only measure the distance to one solved state, not to all solved states that result from rotating the whole cube, so our database heuristic as well as our UNDERESTIMATE heuristic may be wrong if reaching the other goal states is ok.
                        return UNDERESTIMATE;
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
        long          start = System.currentTimeMillis();

        GeneralSearch s;

        // here we decide which exact search algorithm to use
        // the single difference in using another search algorithm
        // would only concern the constructor call
        //s = new IterativeDeepeningAStar(h);
        //s = new BranchAndBound(h, MAX_STEPS + 1);
        s = new IterativeExpansion(h);

                
        // really solve our problem
        Cube solution = (Cube) s.solve(new RubiksCube(SIZE));

        Date eta = new Date(System.currentTimeMillis() - start);

        console.color(Color.white);
        if (solution != null) {
            System.out.println("Found:\n" + solution + "\n");
            printCube(solution.field, 2, 30);
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
    public static final int        left = 0;
    public static final int        back = 1;
    public static final int        top = 2;
    public static final int        front = 3;
    public static final int        right = 4;
    public static final int        down = 5;
    private static final String    names[] = {
        "L", "B", "T", "F", "R", "D"
    };

    // different colors for tiles
    public static final int        orange = 0;
    public static final int        blue = 1;
    public static final int        yellow = 2;
    public static final int        white = 3;
    public static final int        red = 4;
    public static final int        green = 5;
    private static final Color     colors[] = {
        Color.orange.darker(), Color.blue, Color.yellow.brighter(), Color.white, Color.red, Color.green
    };

    /**
     * The size of the Rubik's cube.
     */
    protected final int size;
    /**
     * The actual state of the cube to solve.
     */
    private final Cube _initialState;

    public RubiksCube(int size) {
        if (size != 2)
            throw new InternalError("only implemented for size 2");
        this.size = size;
        this._initialState = constructInitialState();
    }

    /**
     * Pose the problem by constructing the initial Rubik's cube state.
     */
    private Cube constructInitialState() {
        // 'mache einen heilen Wuerfel:
        Cube c = new Cube(size, 0.0);
        switch (SEQUENCE) {
        case COMPLEX:
            // '2 ecken gedreht
            c.field[16] = blue;
            c.field[6] = yellow;
            c.field[9] = red;
            c.field[10] = red;
            c.field[19] = white;
            c.field[13] = yellow;
            break;
        case STANDARD: 
            // 'test: einige drehungen
            c.turn(2, -1); c.turn(2, -1); c.turn(1, 1); c.turn(2, 1);
            //c.turn(1, 1); //c.turn(2, -1); c.turn(2, -1); c.turn(3, -1); c.turn(2, -1); c.turn(3, 1); c.turn(2, -1);
            break;
        case RANDOM:
            {
                Random random = new Random();
                final int MIN_ACTION =
                    RESTRICT_TO_CANONICAL_ACTIONS
                    ? front
                    : left;
                for (int i = 0; i < MAX_STEPS; i++) {
                    int side = MIN_ACTION + random.nextInt(down - MIN_ACTION + 1);
                    int direction = -1 + 2 * random.nextInt(2);
                    c.turn(side, direction);
                }
                System.out.println(c + " of maximum depth " + MAX_STEPS);
            }
            break;
        default:
            throw new IllegalStateException(SEQUENCE + " is an illegal mode for SEQUENCE");
        } 

        c.clearActionLog();
        return c;
    } 

    public Object getInitialState() {
        System.out.println(_initialState + "\n to be solved.\n");
        printCube(_initialState.field, 2, 2);
        return _initialState.clone();
    }

    public boolean isSolution(Object n) {
        Cube c = (Cube) n;
        if (c.isGood()) {
            System.out.println("Solution: " + n);
            return true;
        } 
        return false;
    } 

    public Iterator actions(Object n) {
        Cube s = (Cube) n;
        List ex = new LinkedList();
        final int MIN_ACTION =
            RESTRICT_TO_CANONICAL_ACTIONS
            ? front
            : left;
            
        for (int side = MIN_ACTION; side <= down; side++)
            for (int dir = -1; dir <= 1; dir += 2) {
                Cube t = (Cube) s.clone();
                t.turn(side, dir);
                ex.add(t);
            } 
        return ex.iterator();
    } 

    public final Iterator states(Object action, Object state) {
        return Collections.singleton(action).iterator();
    } 

    public TransitionModel.Transition transition(Object action, Object state, Object statep) {
        // uniform cost 1
        return new Transition(action, 1);
    } 


    public MutableFunction getAccumulatedCostFunction() {
        return _accumulatedCostFunction;
    }
    private static final MutableFunction _accumulatedCostFunction = new MutableFunction() {
            public Object apply(Object state) {
                return ((Cube)state).accumulatedCost;
            }
            public Object set(Object state, Object newAccumulatedCost) {
                Object old = ((Cube)state).accumulatedCost;
                ((Cube)state).accumulatedCost = (Real)newAccumulatedCost;
                return old;
            }
            public Object clone() {
                throw new UnsupportedOperationException();
            }
        };


    // implementation helpers
        
    /**
     * The cube state class.
     */
    protected static class Cube {
        /**
         * The internal description of the cube's state.
         * front side of cube is in the middle (12,13,14,15). Back side is to the right
         * (4,5,6,7).
         * <pre>
         *        +-------+
         *        | 8   9 | 
         *        | 11 10 |
         * +------+-------+-------+------+
         * | 1  2 | 12 13 | 19 16 | 6  7 |
         * | 0  3 | 15 14 | 18 17 | 5  4 | 
         * +------+-------+-------+------+
         *        | 22 23 |
         *        | 21 20 |
         *        +-------+
         * </pre>
         */
        private int[] field;
        /**
         * the accumulated cost of for the actions that lead here.
         */
        private Real accumulatedCost;
        /**
         * the log of actions that lead to this state (from the initial state).
         */
        private StringBuffer actionLog;

        private Cube(int[] field, Real accumulatedCost, StringBuffer actionLog) {
            if (field.length != 2 * 2 * 6)
                throw new InternalError("not implemented for size " + Math.sqrt(field.length / 6.0));
            this.field = field;
            this.accumulatedCost = accumulatedCost;
            clearActionLog();
            //@internal calling append((StringBuffer)actionLog); would not be executable under JVM1.3 but only JVM1.4+
            this.actionLog.append((Object)actionLog);
        }

        /**
         * Create initial cube with all sides good.
         */
        public Cube(int size, Real accumulatedCost) {
            this(new int[size * size * 6], accumulatedCost, new StringBuffer());
            if (size != 2)
                throw new InternalError("not implemented for size " + size);
            for (int i = 0; i < 4; i++)
                field[i] = orange;
            for (int i = 4; i < 8; i++)
                field[i] = blue;
            for (int i = 8; i < 12; i++)
                field[i] = yellow;
            for (int i = 12; i < 16; i++)
                field[i] = white;
            for (int i = 16; i < 20; i++)
                field[i] = red;
            for (int i = 20; i < 24; i++)
                field[i] = green;
        }
        public Cube(int size, double accumulatedCost) {
            this(size, Values.getDefaultInstance().valueOf(accumulatedCost));
        }

        public Object clone() {
            return new Cube((int[]) field.clone(), accumulatedCost, actionLog);
        } 

        public boolean equals(Object o) {
            if (!(o instanceof Cube))
                return false;
            Cube b = (Cube) o;
            if (field.length != b.field.length)
                return false;
            for (int i = 0; i < field.length; i++)
                if (field[i] != b.field[i])
                    return false;
            return true;
        } 

        /**
         * good hashCode required for pattern database.
         * @todo use rotation symmetries (also in equals)
         */
        public int hashCode() {
            int hash = 0;
            int shift = 0;
            for (int i = 0; i < field.length; i++) {
                hash |= field[i] << shift;
                if ((shift += 3) + 2 >= 32)
                    shift -= 32 - 2;
            } 
            return hash;
        } 

        public String toString() {
            return MathUtilities.format(field) + "\n for total accumulated cost " + accumulatedCost + "\n\tmoves that lead here: " + actionLog;
        } 

        public boolean isGood() {
            for (int i = 0; i < field.length; i = i + 4) {
                if (!(field[i] == field[i + 1] && field[i] == field[i + 2] && field[i] == field[i + 3]))
                    return false;
            } 
            return true;
        } 

        /**
         * turn the specified side in the specified direction.
         * @param side which side of the cube to turn.
         * @param direction in which direction to turn the side.
         *  A value of 1 means clockwise, a value of -1 counter-clockwise.
         * @preconditions side&isin;{left,right,top,down,back,front}
         *      &and; direction&isin;{1,-1}
         */
        public void turn(int side, int direction) {
            switch (side) {
            case left:          // 'left - okay
                swaplines(0, 1, 2, 3, direction);
                swaplines(8, 12, 22, 4, direction);
                swaplines(11, 15, 21, 7, direction);
                break;
            case back:  // 'back
                swaplines(4, 5, 6, 7, direction);
                swaplines(1, 21, 17, 9, direction);
                swaplines(0, 20, 16, 8, direction);
                break;
            case top:           // 'top
                swaplines(8, 9, 10, 11, direction);
                swaplines(1, 6, 19, 12, direction);
                swaplines(2, 7, 16, 13, direction);
                break;
            case front:         // 'front
                swaplines(12, 13, 14, 15, direction);
                swaplines(3, 11, 19, 23, direction);
                swaplines(2, 10, 18, 22, direction);
                break;
            case right: // 'right
                swaplines(16, 17, 18, 19, direction);
                swaplines(9, 5, 23, 13, direction);
                swaplines(10, 6, 20, 14, direction);
                break;
            case down:          // 'down
                swaplines(20, 21, 22, 23, direction);
                swaplines(5, 0, 15, 18, direction);
                swaplines(4, 3, 14, 17, direction);
                break;
            default:
                throw new InternalError("unknown side " + side);
            }
            logAction(side, direction);
        } 

        protected void swaplines(int a, int b, int c, int d, int direction) {
            int temp = field[a];
            switch (direction) {
            case -1:    // counter-clockwise
                field[a] = field[b];
                field[b] = field[c];
                field[c] = field[d];
                field[d] = temp;
                break;
            case 1:    // clockwise
                field[a] = field[d];
                field[d] = field[c];
                field[c] = field[b];
                field[b] = temp;
                break;
            default:
                throw new InternalError("unknown direction " + direction);
            }
        } 


        /*protected int goodSide (int field[], int side) {
          int i = side * 4 - 3;
          if (field[i] == field[i + 1] && field[i] == field[i + 2] && field[i] == field[i + 3] ) {
          //'pr?fe kanten
          switch(side) {
          case 1:
          if (field[8] == field[11] && field[12] == field[15] && field[4] == field[7] && field[21] == field[22]) return 1;
          break;
          case 2:
          if (field[8] == field[9] && field[0] == field[1] && field[16] == field[17] && field[20] == field[21]) return 1;
          break;
          case 3:
          if (field[7] == field[6] && field[16] == field[19] && field[12] == field[13] && field[1] == field[2]) return 1;
          break;
          case 5:
          if (field[10] == field[9] && field[13] == field[14] && field[6] == field[5] && field[20] == field[23]) return 1;
          break;
          case 4:
          if (field[11] == field[10] && field[18] == field[19] && field[2] == field[3] && field[23] == field[22]) return 1;
          break;
          case 6:
          if (field[18] == field[17] && field[15] == field[14] && field[4] == field[5] && field[0] == field[3]) return 1;
          break;
          }
          }
          return 0;
          }*/

        /**
         * Remember an action in the log of action sequence.
         * This sequence can then be printed for the solution.
         */
        private void logAction(int side, int direction) {
            actionLog.append(',');
            actionLog.append(names[side]);
            if (direction >= 0)
                actionLog.append('+');
            actionLog.append(direction);
        }
        /**
         * Clears the log of action sequence.
         */
        public void clearActionLog() {
            actionLog =  new StringBuffer();
        }
    }


    private static Basic console = null;

    /**
     * Display the cube.
     */
    protected static void printCube(int field[], int y, int x) {

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
        console.color(colors[field[4]]);
        console.print(f, false);
        console.color(colors[field[5]]);
        console.print(f, false);
        console.LOCATE(x + 2, y + 1);
        console.color(colors[field[7]]);
        console.print(f, false);
        console.color(colors[field[6]]);
        console.print(f, false);

        console.LOCATE(x, y + 2);
        for (int i = 0; i < 17; i += 8) {
            console.color(colors[field[i]]);
            console.print(f, false);
            console.color(colors[field[i + 1]]);
            console.print(f, false);
        } 
        console.color(colors[field[20]]);
        console.print(f, false);
        console.color(colors[field[21]]);
        console.print(f, false);

        console.LOCATE(x, y + 3);
        for (int i = 3; i < 20; i += 8) {
            console.color(colors[field[i]]);
            console.print(f, false);
            console.color(colors[field[i - 1]]);
            console.print(f, false);
        } 
        console.color(colors[field[23]]);
        console.print(f, false);
        console.color(colors[field[22]]);
        console.print(f, false);


        console.LOCATE(x + 2 * f.length(), y + 4);
        console.color(colors[field[12]]);
        console.print(f, false);
        console.color(colors[field[13]]);
        console.print(f, false);
        console.LOCATE(x + 2, y + 5);
        console.color(colors[field[15]]);
        console.print(f, false);
        console.color(colors[field[14]]);
        console.print(f);
    } 
}
