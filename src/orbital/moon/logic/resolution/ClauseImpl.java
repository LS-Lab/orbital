/**
 * @(#)ClauseImpl.java 0.8 2003-04-23 Andre Platzer
 *
 * Copyright (c) 2001-2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;
import orbital.moon.logic.ClassicalLogic.Utilities;
import orbital.moon.logic.*;

import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.Types;

import orbital.logic.functor.*;
import orbital.logic.sign.Expression.Composite;
import orbital.logic.trs.Substitution;
import orbital.logic.trs.Substitutions;

import orbital.util.Utility;
import orbital.util.Setops;
import orbital.logic.functor.Functionals;
import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents a clause, i.e. a set of literals.  A clause
 * {L<sub>1</sub>,...,L<sub>n</sub>} is a different notation for the
 * disjunction L<sub>1</sub>&or;...&or;L<sub>n</sub>.
 *
 * @version 0.8, 2003-04-23
 * @author  Andr&eacute; Platzer
 */
public class ClauseImpl extends HashSet/*<Formula>*/ implements Clause {
    private static final Logger logger = Logger.getLogger(ClauseImpl.class.getName());

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public ClauseImpl(Set/*<Formula>*/ literals) {
	super(literals);
	assert Setops.all(literals, Functionals.bindSecond(Utility.instanceOf, Formula.class)) : "instanceof Set<Formula>";
    }
    public ClauseImpl() {}

    public Signature getFreeVariables() {
	// return banana (|&empty;,&cup;|) (map ((&lambda;x)x.freeVariables()), this)
	Set freeVariables = new HashSet();
	for (Iterator i = iterator(); i.hasNext(); )
	    freeVariables.addAll(((Formula)i.next()).getVariables());
	return new SignatureBase(freeVariables);
    }
    
    public Iterator/*_<Clause>_*/ resolveWith(Clause G) {
	final Clause F = this;
	// resolvents will contain all resolvents of F and G
	Set/*_<Clause>_*/ resolvents = new HashSet();
	// try to resolve G with F
	// choose any literal Fj&isin;F
	for (Iterator j = iterator(); j.hasNext(); ) {
	    final Formula Fj = (Formula) j.next();
	    final Formula notFj = Utilities.negation(Fj);
	    // choose any literal Gk&isin;G
	    for (Iterator k = G.iterator(); k.hasNext(); ) {
		final Formula      Gk = (Formula) k.next();
		// generalized resolution
		final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {Gk, notFj}));
		logger.log(Level.FINEST, "resolving literals {0} with {1} is {2}", new Object[] {Gk, notFj, mu});
		if (mu != null) {
		    // resolve F and G at complementary literals Fj resp. Gk
		    final Clause Gp = new ClauseImpl((Set) Functionals.map(mu, setWithout(G, Gk)));
		    final Clause Fp = new ClauseImpl((Set) Functionals.map(mu, setWithout(F, Fj)));
                        				
		    logger.log(Level.FINER, "resolving {0} with res {1} from {2} and {3}. not yet factorized. Lengths {4} from {5} and {6}.", new Object[] {Fp,Gp, F, G, new Integer(Fp.size()), new Integer(F.size()), new Integer(G.size())});

		    if (Fp.isElementaryValidUnion(Gp))
			// cut that possibility since resolving with tautologies will never lead to false (the contradiction)
			//@xxx 100% sure that for completeness, we can also remove G from setOfSupport, if it only resolves to isElementaryValid clauses. Or must we keep it, even though we don't have to keep the (elementary true) resolvent
			continue;

		    // the resolvent R of F and G at complementary literals Fj resp. Gk
		    final Clause R = Gp;
		    R.addAll(Fp);
		    final Clause factorizedR = R.factorize();
		    logger.log(Level.FINER, "resolved {0} from {1} and {2}. Factorized to {3}. Lengths {4} from {5} and {6} .", new Object[] {R, F, G, factorizedR, new Integer(R.size()), new Integer(F.size()), new Integer(G.size())});

		    // @internal for perfect performance (and catastrophal structure) could already perform a goal lookahead by R.equals(Utilities.CONTRADICTION)

		    resolvents.add(factorizedR);
		}
	    }
	}
	return resolvents.iterator();
    }

    public Clause variant(Signature disjunctify) {
	List/*_<Symbol>_*/ renaming = new ArrayList(disjunctify.size());
	for (Iterator i = disjunctify.iterator(); i.hasNext(); ) {
	    Symbol s = (Symbol) i.next();
	    assert s.isVariable() : "we only form variants by renaming variables";
	    renaming.add(Substitutions.createExactMatcher(s, new UniqueSymbol(s.getType(), null, s.isVariable())));
	}
	return (Clause) Functionals.map(Substitutions.getInstance(renaming), this);
    }

    // proof utilities
	
    public boolean isElementaryValidUnion(Clause G) {
	// for all literals Fj&isin;F
    	for (Iterator j = iterator(); j.hasNext(); ) {
	    final Formula Fj = (Formula) j.next();
	    final Formula notFj = Utilities.negation(Fj);
	    // for all literals Gk&isin;G
	    for (Iterator k = G.iterator(); k.hasNext(); ) {
		final Formula Gk = (Formula) k.next();
		if (Gk.equals(notFj))
		    return true;
	    }
        }
        return false;
    }

    public boolean isElementaryValid() {
	return isElementaryValidUnion(this);
    }
	
    public Clause factorize() {
	// we need a list view of the set for traversing distinct literals, then we will also only modify listF not this
	final List listF = new LinkedList(this);
	if (factorizeImpl(listF))
	    return new ClauseImpl(new HashSet(listF));
	else
	    return this;
    }
    /**
     * Implementation of {@link #factorize()}.
     * @param listF list of literals which will be <em>modified</em> according to factorization.
     * @return whether factorization was possible, and thus listF has changed.
     */
    private boolean factorizeImpl(List listF) {
	final Clause F = this;
	// for all literals Fi&isin;F
    	for (ListIterator i = listF.listIterator(); i.hasNext(); ) {
	    final Formula Fi = (Formula) i.next();
	    // for all literals Fj&isin;F with j>i
	    for (ListIterator j = listF.listIterator(i.nextIndex()); j.hasNext(); ) {
		final Formula Fj = (Formula) j.next();
		final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {Fi, Fj}));
		if (mu != null) {
		    // factorize
                    final String logPrevious = logger.isLoggable(Level.FINEST) ? F + "" : "";
		    // optimized removing Fi from the set, since mu(Fi) = mu(Fj) anyway (notice the set representation)
		    assert mu.apply(Fi).equals(mu.apply(Fj));
		    j.remove();
                    listF = Functionals.map(mu, listF);
                    logger.log(Level.FINEST, "factorized {1} from {0} by unifying {3} and {4} with {2}", new Object[] {logPrevious, F, mu, Fi, Fj});
		    // factorize again
		    //@todo could optimize away recursive call
		    factorizeImpl(listF);
		    return true;
		}
	    }
        }
	// no factorization possible
        return false;
    }

    // Diverse utilities

    /**
     * @return S\{x} as a new set.
     */
    private static Set setWithout(Set S, Object x) {
	Set Sp = new HashSet(S);
	Sp.remove(x);
	return Sp;
    }

}
