/**
 * @(#)ClausalSetImpl.java 0.8 2003-04-23 Andre Platzer
 *
 * Copyright (c) 2001-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;

import java.util.Set;
import java.util.LinkedHashSet;
import orbital.logic.imp.Formula;


import java.util.Iterator;
import orbital.util.Utility;
import orbital.util.Setops;
import orbital.logic.functor.Functionals;
import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;
import orbital.logic.sign.SymbolBase;
import orbital.logic.sign.type.Types;
import orbital.moon.logic.ClassicalLogic;

/**
 * Default implementation of a representation of a set of clauses.
 * @version 0.8, 2003-04-23
 * @author  Andr&eacute; Platzer
 */
public class ClausalSetImpl extends LinkedHashSet/*_<Clause>_*/ implements ClausalSet {
    private static final Formula FORMULA_TRUE = (Formula) new ClassicalLogic().createAtomic(new SymbolBase("true", Types.TRUTH));

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public ClausalSetImpl(Set/*_<Clause>_*/ clauses) {
	super(clauses);
	assert Setops.all(clauses, Functionals.bindSecond(Utility.instanceOf, Clause.class)) : "instanceof Set<Formula>";
    }
    public ClausalSetImpl() {}

    public Iterator/*_<Clause>_*/ getProbableComplementsOf(Clause C) {
	//@todo use indexing for far better implementation
	return iterator();
    }

    public boolean removeAllSubsumedBy(ClausalSet T) {
	boolean avoidSelf = false;
	if (T.isEmpty()) {
	    return false;
	} else if (this == T) {
	    avoidSelf = true;
	} else if (this.equals(T)) {
	    throw new IllegalArgumentException("directly subsuming a set of clauses with itself would illegally result in the empty set (without additional constraints): \n   " + this + "\n = " + T);
	}
	boolean changed = false;
removeSubsumed:
	for (Iterator i = iterator(); i.hasNext(); ) {
	    final Clause D = (Clause)i.next();
	
	    for (Iterator j = T.iterator(); j.hasNext(); ) {
		final Clause C = (Clause)j.next();
		if (avoidSelf && C == D)
		    // avoid self-subsumption, since then everything would be subsumed
		    continue;

		if (C.subsumes(D)) {
		    i.remove();
		    changed = true;
		    continue removeSubsumed;
		}
	    }
	}
	return changed;
    }

    public Formula toFormula() {
	Iterator i = Functionals.map(new Function() {
		public Object apply(Object C) {
		    return ((Clause)C).toFormula();
		}
	    }, iterator());
	Formula f0;
	if (!i.hasNext())
	    return FORMULA_TRUE;
	return (Formula/*__*/) Functionals.foldRight(
	    //@see orbital.moon.logic.functor.Operations.or on Formulas
	    new BinaryFunction() {
		public Object apply(Object F, Object G) {
		    return ((Formula)F).and((Formula)G);
		}
		//@internal evaluation order dependent
	    }, i.next(), i);
    }
}
