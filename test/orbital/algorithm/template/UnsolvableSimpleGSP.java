import orbital.algorithm.template.*;
import java.util.*;

import orbital.math.Real;
import orbital.logic.functor.Function;

/**
 * An unsolvable variant of SimpleGSP with a finite search space.
 */
class UnsolvableSimpleGSP extends SimpleGSP {
    private int maxDepth;
    public UnsolvableSimpleGSP(int maxDepth) {
        super(2, 2+10*maxDepth);
        this.maxDepth = maxDepth;
        this.g = getAccumulatedCostFunction();
    }
    private Function g;

    public boolean isSolution(Object s) {
        // categorically no
        return false;
    } 
    public Iterator actions(Object s) {
        if (((Real)g.apply(s)).doubleValue() <= maxDepth)
            return super.actions(s);
        else
            return Collections.EMPTY_SET.iterator();
    }

    public String toString() {
        return super.toString() + " with restriction to maxDepth=" + maxDepth;
    }
}
