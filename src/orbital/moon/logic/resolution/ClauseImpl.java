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
import orbital.util.Pair;
import orbital.logic.functor.Functionals;
import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Default implementation of a representation of a clause, i.e. a set of literals.  A clause
 * {L<sub>1</sub>,...,L<sub>n</sub>} is a different notation for the
 * disjunction L<sub>1</sub>&or;...&or;L<sub>n</sub>.
 *
 * @version 0.8, 2003-04-23
 * @author  Andr&eacute; Platzer
 * @xxx Should we provide implements Composite for unify to work on whole clauses? do unifiable clauses with a different initial number of literals unify? Not in our implementation, I'm afraid.
 */
public class ClauseImpl extends LinkedHashSet/*<Formula>*/ implements Clause {
    private static final Logger logger = Logger.getLogger(ClauseImpl.class.getName());
    //@xxx do not stick to this single logic, here, although resolution is rather limited to classical logic
    private static final Logic logic = new ClassicalLogic();

    protected static ClausalFactory getClausalFactory() {
	return ResolutionBase.getClausalFactory();
    }

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public ClauseImpl(Set/*<Formula>*/ literals) {
	super(literals);
	assert Setops.all(literals, Functionals.bindSecond(Utility.instanceOf, Formula.class)) : "instanceof Set<Formula>";
    }
    public ClauseImpl() {}

    //

    public Signature getFreeVariables() {
	// return banana (|&empty;,&cup;|) (map ((&lambda;x)x.freeVariables()), this)
	Signature freeVariables = new SignatureBase();
	for (Iterator i = iterator(); i.hasNext(); )
	    freeVariables.addAll(((Formula)i.next()).getVariables());
	return freeVariables;
    }
    
    //@internal could also use a StreamMethod for implementation.
    public Iterator/*_<Clause>_*/ resolveWith(Clause G) {
	final Clause F = this;
	assert F.getFreeVariables().intersection(G.getFreeVariables()).isEmpty() : "@preconditions disjoint variable variants required for resolution";
	// resolvents will contain all resolvents of F and G
	Set/*_<Clause>_*/ resolvents = new LinkedHashSet();
	// try to resolve G with F
	// choose any literal L&isin;F
	for (Iterator j = F.getResolvableLiterals(); j.hasNext(); ) {
	    final Formula L = (Formula) j.next();
	    // list versions of probable unifiables
	    final List/*_<Formula>_*/ puinGwithnotL =
		new ArrayList(Setops.asList(G.getProbableUnifiables(ClassicalLogic.Utilities.negation(L))));
	    // choose any literal K&isin;G
	    for (Iterator k = puinGwithnotL.iterator(); k.hasNext(); ) {
		final Formula K = (Formula) k.next();
		// resolution
		final Clause  R = resolventWith(G, L, K);
		if (R != null) {
		    logger.log(Level.FINER, "resolved {0} from {1} and {2}.", new Object[] {R, F, G});
		    //@todo also add factors of R
		    // @internal for perfect performance (and catastrophal structure) could already perform a goal lookahead by R.equals(Utilities.CONTRADICTION)
		    resolvents.add(R);
		}
	    }
	}
	return resolvents.iterator();
    }

    public Iterator/*_<Clause>_*/ resolveWithFactors(Clause _G) {
	final ClauseImpl F = this;
	final ClauseImpl G = (ClauseImpl)_G;
	assert F.getFreeVariables().intersection(G.getFreeVariables()).isEmpty() : "@preconditions disjoint variable variants required for resolution";

	// list version F, just necessary for formulating assertions
	final List/*_<Formula>_*/ listF = new ArrayList(Setops.asList(F.getResolvableLiterals()));
	
	// resolvents will contain all resolvents of F and G
	Set/*_<Clause>_*/ resolvents = new LinkedHashSet();
	// try to resolve G with F
	// choose any literal L&isin;F
	for (ListIterator j = listF.listIterator(); j.hasNext(); ) {
	    final Formula L = (Formula) j.next();
	    // list versions of probable unifiables
	    final List/*_<Formula>_*/ puinFwithL =
		new ArrayList(Setops.asList(F.getProbableUnifiables(L)));
	    final List/*_<Formula>_*/ puinGwithnotL =
		new ArrayList(Setops.asList(G.getProbableUnifiables(ClassicalLogic.Utilities.negation(L))));
	    final int puinFwithL_Lindex = puinFwithL.indexOf(L);
	    assert puinFwithL_Lindex >= 0 : L + " is probably unifiable with itself";
	    // choose any literal K&isin;G
	    for (ListIterator k = puinGwithnotL.listIterator(); k.hasNext(); ) {
		final Formula K = (Formula) k.next();
		// resolution
		final Clause  R = F.resolventWith(G, L, K);
		if (R != null) {
		    resolvents.add(R);

		    assert getUnifiablesOf(puinFwithL.subList(puinFwithL_Lindex + 1, puinFwithL.size()), L).equals(getUnifiablesOf(listF.subList(j.nextIndex(), listF.size()), L)) : "the same set of unifiables results, regardless of the base list used. Extracting probable unifiables either from to the right of the total list, or from to the right of the occurrence in the _stable_ probable unifiable list results in the same set of unifiables.";
		    // also add resolvents of factors of F and G
		    // form all subsets of literals (that are unifiable with L) to the right of L that really include L
		    final Set/*_<Set<Formula>>_*/ factorFLiteralCombinations =
			Setops.powerset(getUnifiablesOf(puinFwithL.subList(puinFwithL_Lindex + 1, puinFwithL.size()), L));
		    // form all subsets of literals (that are unifiable with K) to the right of K that really include K
		    final Set/*_<Set<Formula>>_*/ factorGLiteralCombinations =
			Setops.powerset(getUnifiablesOf(puinGwithnotL.subList(k.nextIndex(), puinGwithnotL.size()), K));
		    for (Iterator f = factorFLiteralCombinations.iterator(); f.hasNext(); ) {
			final Set/*_<Formula>_*/ factorFLiterals = (Set)f.next();
			factorFLiterals.add(L);
			final Pair pF = F.factorize2(factorFLiterals);
			if (pF.A == null) {
			    //@internal even though the literals unify individually, this particular combination does not.
			    continue;
			}
			final ClauseImpl factorF = (ClauseImpl)pF.B;
			// factorL corresponds to L (is one remaining literal after factorization)
			final Formula factorL = (Formula) ((Substitution)pF.A).apply(L);

			for (Iterator g = factorGLiteralCombinations.iterator(); g.hasNext(); ) {
			    final Set/*_<Formula>_*/ factorGLiterals = (Set)g.next();
			    factorGLiterals.add(K);
			    if (factorFLiterals.size() < 2 && factorGLiterals.size() < 2) {
				// factoring neither F nor G simply leads to R which we already have calculated above
				continue;
			    }
			    final Pair pG = G.factorize2(factorGLiterals);
			    if (pG.A == null) {
				//@internal even though the literals unify individually, this particular combination does not.
				continue;
			    }
			    final Clause factorG = (Clause)pG.B;
			    // factorK corresponds to K (is one remaining literal after factorization)
			    final Formula factorK = (Formula) ((Substitution)pG.A).apply(K);

			    // resolution of factors
			    final Clause factorR = factorF.resolventWith(factorG, factorL, factorK);
			    if (factorR != null) {
				logger.log(Level.FINER, "Adding factor-resolvent {4} of factors {0} from {1} and {2} from {3}.", new Object[] {factorF, F, factorG, G, factorR});
				resolvents.add(factorR);
			    }
			}
		    }
		}
	    }
	}
	return resolvents.iterator();
    }

    /**
     * Resolve clause F with G by the complementary resolution
     * literals L&isin;F and K&isin;G.
     * @preconditions this.contains(L) &and; G.contains(K)
     * @return the resolvent ((F\{L})&cup;(G\{K}))&mu; when
     *  &exist;&mu;&isin;mgU{L,&not;K}.  Or <code>null</code> if the
     *  resolution of F with G by L and K is impossible because of
     *  mgU{L,&not;K}=&empty;.
     */
    protected Clause resolventWith(Clause G, Formula L, Formula K) {
	return (Clause) resolventWith2(G, L, K).B;
    }
    /**
     * Workaround for returning 2 arguments.
     * @return the pair of substitution and resulting clause, respectively <code>(null,null)</code>.
     */
    protected Pair/*<Substitution,Clause>*/ resolventWith2(Clause G, Formula L, Formula K) {
	final Clause F = this;
	final Formula notL = Utilities.negation(L);
	final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {K, notL}));
	logger.log(Level.FINEST, "resolving literals\n   \t{0} (negation of {3})\n with\t{1}\n is\t{2}", new Object[] {notL, K, mu, L});
	if (mu == null) {
	    return new Pair(null, null);
	} else {
	    // resolve F and G at complementary literals L resp. K
	    final Clause Gp = map(mu, clauseWithout(G, K));
	    final Clause Fp = map(mu, clauseWithout(F, L));
                        				
	    logger.log(Level.FINER, "resolving {0} with res {1} from {2} and {3}. not yet factorized. Lengths {4} from {5} and {6}.", new Object[] {new ClauseImpl(Fp),new ClauseImpl(Gp), F, G, new Integer(Fp.size()), new Integer(F.size()), new Integer(G.size())});

	    if (Fp.isElementaryValidUnion(Gp)) {
		// cut that resolution possibility since resolving with tautologies will never lead to false (the contradiction)
		//@xxx 100% sure that for completeness, we can also remove G from setOfSupport, if it only resolves to isElementaryValid clauses. Or must we keep it, even though we don't have to keep the (elementary true) resolvent
		return new Pair(null, null);
	    }

	    // the resolvent R of F and G at complementary literals L resp. K
	    final Clause R = Gp;
	    R.addAll(Fp);
	    return new Pair(mu, R);
	}
    }


    public Clause variant(Signature disjointify) {
	return variant(disjointify, false);
    }
    /**
     * @param constantify whether to make replace with constants. Use
     * <code>false</code> to replace constants with constants, and
     * variables with variables. Use <code>true</code> to always
     * replace with constants.
     */
    private Clause variant(Signature disjointify, boolean constantify) {
	List/*_<Symbol>_*/ renaming = new ArrayList(disjointify.size());
	for (Iterator i = disjointify.iterator(); i.hasNext(); ) {
	    Symbol s = (Symbol) i.next();
	    assert s.isVariable() : "we only form variants by renaming variables";
	    //@internal this is an embedding of symbols into atomic formulas (otherwise x will never occur, since the atomic formula x is not compound of anything, and should not be compound as well)
	    //renaming.add(Substitutions.createExactMatcher(s, new UniqueSymbol(s.getType(), null, s.isVariable())));
	    renaming.add(Substitutions.createExactMatcher(logic.createAtomic(s),
							  logic.createAtomic(new UniqueSymbol(s.getType(), null, !constantify && s.isVariable()))));
	}
	final Clause variant = map(Substitutions.getInstance(renaming), this);
	logger.log(Level.FINEST, "variant of {0} with respect to {1} is\n\t {2} via {3}", new Object[] {this, disjointify, variant, Substitutions.getInstance(renaming)});
	assert variant.getFreeVariables().intersection(disjointify).isEmpty() : "@postconditions RES.getFreeVariables().intersection(disjointify).isEmpty()";
	return variant;
    }

    public Iterator/*_<Clause>_*/ resolveWithVariant(Clause G) {
	Clause          F = this;
	final Signature FVariables = F.getFreeVariables();
	final Signature overlappingVariables = G.getFreeVariables().intersection(FVariables);
	if (!overlappingVariables.isEmpty()) {
	    // make a variant of F such that the variables of F and G are disjunct
	    //@todo optimize would it be quicker if we always build variants, regardless of disjointness or not? Also unique variables would alleviate the need for variant building altogether, except for self-resolution of identical clauses (which can be compared for by ==).
	    final Clause Fprime = F.variant(overlappingVariables);
	    logger.log(Level.FINEST, "variant for resolution is {0} instead of {1} with {2} because of overlapping variables {3}", new Object[] {Fprime, F, G, overlappingVariables});
	    F = Fprime;
	} else {
	    logger.log(Level.FINEST, "no variant for resolution of {0} with {1} because of no overlapping variables {2}", new Object[] {F, G, overlappingVariables});
	}
	return F.resolveWith(G);
    }

    public Iterator/*_<Clause>_*/ resolveWithVariantFactors(Clause G) {
	Clause          F = this;
	final Signature FVariables = F.getFreeVariables();
	final Signature overlappingVariables = G.getFreeVariables().intersection(FVariables);
	if (!overlappingVariables.isEmpty()) {
	    // make a variant of F such that the variables of F and G are disjunct
	    //@todo optimize would it be quicker if we always build variants, regardless of disjointness or not? Also unique variables would alleviate the need for variant building altogether.
	    final Clause Fprime = F.variant(overlappingVariables);
	    logger.log(Level.FINEST, "variant for resolution is {0} instead of {1} with {2} because of overlapping variables {3}", new Object[] {Fprime, F, G, overlappingVariables});
	    F = Fprime;
	} else {
	    logger.log(Level.FINEST, "no variant for resolution of {0} with {1} because of no overlapping variables {2}", new Object[] {F, G, overlappingVariables});
	}
	return F.resolveWithFactors(G);
    }
    
    // proof utilities
	
    public boolean isElementaryValidUnion(Clause G) {
	// for all literals Fj&isin;F
    	for (Iterator j = iterator(); j.hasNext(); ) {
	    final Formula Fj = (Formula) j.next();
	    final Formula notFj = Utilities.negation(Fj);
	    // for all literals Gk&isin;G
	    //@internal we can use Gk&isin;G.getProbableUnifiables(notFj) instead, since this usually is a smaller set
	    for (Iterator k = G.getProbableUnifiables(notFj); k.hasNext(); ) {
		final Formula Gk = (Formula) k.next();
		if (Gk.equals(notFj))
		    return true;
	    }
        }
        return false;
    }

    public boolean isElementaryValid() {
	//@todo if we had lists, we could implement a slightly quicker version of return isElementaryValidUnion(this); which uses j>k
	return isElementaryValidUnion(this);
    }
    
    public Iterator factorize() {
	throw new UnsupportedOperationException("not yet implemented, use Clause.resolveWithFactors(Clause) instead, which also has improved performance.");
    }

    /**
     * Factorize a clause by the specified literals.
     * <p>
     * Will implement the factorization rule necessary for binary resolution:
     * <div>{L1,...,Ln} |- {s(L1),...,s(Lk)} with s=mgU({Lk,...,Ln})</div>
     * Which is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Lk,...,Ln})</div>
     * because of the set representation.
     * </p>
     * @param literals the literals {Lk,...,Ln} to factorize to a single literal.
     * @return the factorized clause, or <code>null</code> if no factorization was possible.
     * @preconditions this.containsAll(literals)
     */
    protected Clause factorize(Collection/*_<Clause>_*/ literals) {
	return (Clause) factorize2(literals).B;
    }
    /**
     * Workaround for returning 2 arguments.
     * @return the pair of substitution and resulting factor, respectively <code>(null,null)</code>.
     */
    protected Pair/*<Substitution,Clause>*/ factorize2(Collection/*_<Clause>_*/ literals) {
	assert this.containsAll(literals) : "can only factorize literals contained in this clause";
	if (literals.size() < 2) {
	    //@internal just a speedup optimization
	    return new Pair(Substitutions.id, this);
	}
	Clause previous = null;
	assert (previous = getClausalFactory().createClause(this)) != null;
	final Substitution mu = Substitutions.unify(literals);
	assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
	if (mu == null) {
	    return new Pair(null, null);
	} else {
	    // factorize
	    //@todo optimize by removing all (but one in) literals from clause prior to applying mu
	    // apply unification and remove duplicates
	    Clause factor = map(mu, this);
	    assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
	    if (logger.isLoggable(Level.FINEST)) {
		logger.log(Level.FINEST, "factorized {1} from {0} by unifying {2} by {3}", new Object[] {getClausalFactory().createClause(this), getClausalFactory().createClause(factor), mu, literals});
	    }
	    return new Pair(mu, factor);
	}
    }


    /**
     * Implements subsumption based on unit input semi-ground resolution.
     */
    public boolean subsumes(Clause D) {
	if (size() > D.size())
	    return false;
	// negate D and replace all variables with distinct constants (also distinct for each literal)
	final ClausalSet notDground = newClausalSet();
	for (Iterator i = D.iterator(); i.hasNext(); ) {
	    final ClauseImpl notDi = (ClauseImpl)getClausalFactory().createClause(Collections.singleton(Utilities.negation((Formula)i.next())));
	    final Clause notDiground = notDi.variant(notDi.getFreeVariables(), true);
	    assert notDiground.getFreeVariables().isEmpty() : "ground instances have no free variables " + notDiground + " stemming from " + notDi + " has FV=" + notDiground.getFreeVariables();
	    logger.log(Level.FINEST, "variant for subsumption is constantification {0} instead of {1} because of free variables {2}", new Object[] {notDiground, notDi, notDi.getFreeVariables()});
	    notDground.add(notDiground);
	}

	ClausalSet u = getClausalFactory().newClausalSet();
	u.add(getClausalFactory().createClause(this));
	final ClausalSet input = notDground;
	assert !input.contains(Clause.CONTRADICTION) : "contains no elementary contradiction";
	assert !u.contains(Clause.CONTRADICTION) : "contains no elementary contradiction, otherwise " + this + " is " + Clause.CONTRADICTION;
	int count = 0;
	while (!u.isEmpty()) {
	    // the set of resolvents obtained from resolution of any C1 with any C2
	    final ClausalSet newResolvents = getClausalFactory().newClausalSet();

	    // for each clause C1&isin;U
	    for (Iterator i = u.iterator(); i.hasNext(); ) {
		final Clause C1 = (Clause)i.next();
		assert !C1.equals(Clause.CONTRADICTION) : "already checked for contradiction";

		// choose any clause C2&isin;input
		for (Iterator i2 = input.getProbableComplementsOf(C1); i2.hasNext(); ) {
		    final Clause C2 = (Clause) i2.next();
		    // try to resolve C1 with C2
		    //@internal no variant forming needed since the input of the unit input resolution is ground
		    for (Iterator resolvents = C1.resolveWith(C2); resolvents.hasNext(); ) {
			final Clause R = (Clause)resolvents.next();
			if (R.equals(Clause.CONTRADICTION)) {
			    logger.log(Level.FINE, "subsumption of {3} =< {4} resolved contradiction {0} from {1} and {2}",  new Object[] {R, C1, C2, this, D});
			    return true;
			} else {
			    newResolvents.add(R);
			}
		    }
		}
	    }
	    u = newResolvents;
	    assert count < input.size() + 3 : "the size of the unit input set is an upper bound to the length of unit input resolution " + count + "<" + input.size() + "\n input=" + input + "\n u=" + u;
	    count++;
	}

	return false;
    }


    // lookup methods

    public Iterator/*_<Formula>_*/ getResolvableLiterals() {
	return iterator();
    }

    public Iterator/*_<Formula>_*/ getProbableUnifiables(Formula L) {
	return iterator();
    }

    public Set/*_<Formula>_*/ getUnifiables(Formula L) {
	return getUnifiablesOf(getProbableUnifiables(L), L);
    }

    // Helpers
    
    /**
     * Get all literals contained in C that unify with L.
     * @postconditions RES = {K&isin;C &exist;mgU{L,K}}
     * @see #getProbableUnifiables(Formula)
     * @see #getUnifiables(Formula)
     */
    private static Set/*_<Formula>_*/ getUnifiablesOf(Collection/*_<Formula>_*/ C, Formula L) {
	return getUnifiablesOf(C.iterator(), L);
    }
    private static Set/*_<Formula>_*/ getUnifiablesOf(Iterator/*_<Formula>_*/ C, Formula L) {
	Set/*_<Formula>_*/ r = new LinkedHashSet();
	while (C.hasNext()) {
	    Formula F = (Formula)C.next();
	    //@todo optimizable, we could remember the unifier instead of recalculating it lateron (f.ex. during factorization)
	    if (Substitutions.unify(Arrays.asList(new Formula[] {L,F})) != null) {
		r.add(F);
	    }
	}
	return r;
    }
    

    // Diverse utilities

    /**
     * @see Functionals#map
     */
    private Clause map(Function f, Clause c) {
	Set fc = (Set) Functionals.map(f, c);
	return fc instanceof Clause ? (Clause)fc : getClausalFactory().createClause(fc);
    }

    /**
     * @return S\{x} as a new clause.
     */
    private Clause clauseWithout(Clause S, Object x) {
	Clause Sp = getClausalFactory().createClause(S);
	Sp.remove(x);
	return Sp;
    }

    /**
     * @return S\{x} as a new set.
     */
    private static Set setWithout(Set S, Object x) {
	Set Sp = new LinkedHashSet(S);
	Sp.remove(x);
	return Sp;
    }

    /**
     * Delegates to {@link ClausalFactory#newClausalSet()}.
     */
    private final ClausalSet newClausalSet() {
	return SetOfSupportResolution.INDEXING
	    ? new ClausalSetImpl()
	    : getClausalFactory().newClausalSet();
    }

}
