

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.*;
import orbital.util.DelegateMap;
import java.util.*;
import java.io.*;
import java.util.zip.*;

/**
 * Creates the pattern database for the rubic's cube heuristic.
 * 
 * @version 0.9, 2000/09/24
 * @author  Andr&eacute; Platzer
 * @internal note that our heuristic does not always return zero for (rotation symmetric) goal states! The same goes true for states that can more easily be reached from rotated states.
 */
public class RubicsCubeCreatePattern extends RubicsCube {
    /**
     * Whether to compress the pattern database.
     */
    public static final boolean compressed = true;

    /**
     * Up to which depth to produce entries in the pattern database.
     */
    public static final int     MAX_STEPS = 8;

    public static void main(String arg[]) throws Exception {
	final Map patternDatabase = new HashOnlyMap();
	System.err.println("Note that creating the pattern database file may take a while\ndepending upon the pattern depth (" + MAX_STEPS + ")");
	System.out.println("creating pattern database ...");

	RubicsCubeCreatePattern problem = new RubicsCubeCreatePattern(RubicsCube.SIZE);
	final Function accumulatedCostFunction = problem.getAccumulatedCostFunction();
	GeneralSearch s = new BranchAndBound(new Function() {

		// creates the pattern database in passing
		public Object apply(Object n) {
		    Scalar old = (Scalar) patternDatabase.get(n);
		    Real v = (Real) accumulatedCostFunction.apply(n);

		    // store better value
		    if (old == null || v.compareTo(old) < 0)
			patternDatabase.put(n, v);

		    // categorically underestimate (we don't really
		    // need a solution, so the heuristic isn't very
		    // important either
		    return Values.valueOf(1);
		} 
	    }, MAX_STEPS);

	s.solve(problem);
	System.out.println("up to depth " + MAX_STEPS);

	// store patterns
	OutputStream fos = new FileOutputStream("RubicsCube.patterndb");
	if (compressed)
	    fos = new DeflaterOutputStream(fos, new Deflater(Deflater.BEST_COMPRESSION));
	ObjectOutputStream os = new ObjectOutputStream(fos);
	os.writeInt(MAX_STEPS);
	os.writeObject(patternDatabase);
	os.close();
    } 

    public RubicsCubeCreatePattern(int size) {
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
	    out.writeInt(((Number) e.getKey()).intValue());
	    out.writeFloat(((Number) e.getValue()).floatValue());
	} 
    } 

    public void readExternal(ObjectInput in) throws IOException {
	int size = in.readInt();
	setDelegatee(new HashMap(size));
	for (int i = 0; i < size; i++) {
	    put(new java.lang.Integer(in.readInt()), Values.valueOf(in.readFloat()));
	} 
    } 
}
