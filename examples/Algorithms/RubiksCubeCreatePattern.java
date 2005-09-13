import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.*;
import orbital.util.DelegateMap;
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
 * Creates the pattern database for the Rubik's cube heuristic.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @internal note that our heuristic does not always return zero for (rotation symmetric) goal states! The same goes true for states that can more easily be reached from rotated states.
 */
public class RubiksCubeCreatePattern extends RubiksCube {
    /**
     * Whether to compress the pattern database.
     */
    public static final boolean compressed = true;

    /**
     * Up to which depth to produce entries in the pattern database.
     */
    public static final int     MAX_STEPS = 8;
    /**
     * The database file where the Rubik's cube heuristics are stored.
     */
    public static final String  patternDatabaseFile = "RubiksCube.patterndb";

    public static void main(String arg[]) throws Exception {
        final Map patternDatabase = new HashOnlyMap();
        System.out.println("Note that creating the pattern database file may take a while,\ndepending upon the pattern depth (" + MAX_STEPS + ")");
        System.out.println("creating pattern database ...");

        RubiksCubeCreatePattern problem = new RubiksCubeCreatePattern(RubiksCube.SIZE);
        final Function accumulatedCostFunction = problem.getAccumulatedCostFunction();
        GeneralSearch s = new BranchAndBound(new Function() {

                // creates the pattern database in passing the state space
                public Object apply(Object n) {
                    Scalar old = (Scalar) patternDatabase.get(n);
                    Real v = (Real) accumulatedCostFunction.apply(n);
                    assert v.compareTo(Values.ZERO) >= 0;

                    // store better value, since we underestimate
                    if (old == null || v.compareTo(old) < 0)
                        patternDatabase.put(n, v);

                    // categorically underestimate (we don't really
                    // need a solution, so the heuristic isn't very
                    // important either
                    return Values.getDefaultInstance().valueOf(1);
                } 
            }, MAX_STEPS);

        s.solve(problem);
        System.out.println("up to depth " + MAX_STEPS);

        // store patterns
        OutputStream fos = new FileOutputStream(patternDatabaseFile);
        if (compressed)
            fos = new DeflaterOutputStream(fos, new Deflater(Deflater.BEST_COMPRESSION));
        ObjectOutputStream os = new ObjectOutputStream(fos);
        os.writeInt(MAX_STEPS);
        os.writeObject(patternDatabase);
        os.close();
        System.out.println("Writing pattern database ...");
        System.out.println("up to depth " + MAX_STEPS + " with " + patternDatabase.size() + " heurisitc value entries.");
    } 

    public RubiksCubeCreatePattern(int size) {
        super(size);
    }

    public Object getInitialState() {
        // we start with a good cube and count the cost to all
        // reachable states (up to MAX_STEPS)
        return new Cube(size, 0.0);
    } 

    public boolean isSolution(Object n) {
        return false;
    } 
}

class HashOnlyMap extends DelegateMap implements Externalizable {
    public HashOnlyMap() {
        super(new HashMap());
    }

    private final Object transform(Object key) {
        return new java.lang.Integer(key == null ? 0 : key.hashCode());
    } 

    public boolean containsKey(Object key) {
        return super.containsKey(transform(key));
    } 

    public Object get(Object key) {
        return super.get(transform(key));
    } 

    public Object put(Object key, Object value) {
        return super.put(transform(key), value);
    } 

    public Object remove(Object key) {
        return super.remove(transform(key));
    } 

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(size());
        for (Iterator i = entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            out.writeInt(((java.lang.Integer) e.getKey()).intValue());
            out.writeFloat(((Real) e.getValue()).floatValue());
        } 
    } 

    public void readExternal(ObjectInput in) throws IOException {
        int size = in.readInt();
        setDelegatee(new HashMap(size));
        for (int i = 0; i < size; i++) {
            put(new java.lang.Integer(in.readInt()),
                Values.getDefaultInstance().valueOf(in.readFloat()));
        } 
    } 
}
