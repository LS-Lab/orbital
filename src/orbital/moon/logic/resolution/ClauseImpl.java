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
public class ClauseImpl extends HashSet/*<Formula>*/ implements Clause {
    private static final Logger logger = Logger.getLogger(ClauseImpl.class.getName());
    //@xxx do not stick to this single logic, here, although resolution is rather limited to classical logic
    private static final Logic logic = new ClassicalLogic();

    /**
     * Copy constructor.
     * @internal transitively public constructors required for Functionals.map to produce Clauses.
     */
    public ClauseImpl(Set/*<Formula>*/ literals) {
	super(literals);
	assert Setops.all(literals, Functionals.bindSecond(Utility.instanceOf, Formula.class)) : "instanceof Set<Formula>";
    }
    public ClauseImpl() {}

    // factory-methods
    
    /**
     * Instantiates a new clause.
     * @return a new (yet empty) clause of the same type as this.
     * @postconditions RES&ne;RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     */
    protected Clause newInstance() {
	return new ClauseImpl();
    }

    /**
     * Instantiates a new clause.
     * @param literals the set of literals for the new clause.
     * @return a new clause of the same type as this, with the specified literals.
     * @postconditions RES&ne;RES
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     * @see #clone()
     * @todo rename or remove
     */
    protected ClauseImpl construct(Set literals) {
	return new ClauseImpl(literals);
    }

    //

    public Signature getFreeVariables() {
	// return banana (|&empty;,&cup;|) (map ((&lambda;x)x.freeVariables()), this)
	Set freeVariables = new HashSet();
	for (Iterator i = iterator(); i.hasNext(); )
	    freeVariables.addAll(((Formula)i.next()).getVariables());
	return new SignatureBase(freeVariables);
    }
    
    //@internal could also use a StreamMethod for implementation.
    public Iterator/*_<Clause>_*/ resolveWith(Clause G) {
	final Clause F = this;
	assert F.getFreeVariables().intersection(G.getFreeVariables()).isEmpty() : "@preconditions disjoint variable variants required for resolution";
	// resolvents will contain all resolvents of F and G
	Set/*_<Clause>_*/ resolvents = new HashSet();
	// try to resolve G with F
	// choose any literal L&isin;F
	for (Iterator j = iterator(); j.hasNext(); ) {
	    final Formula L = (Formula) j.next();
	    // choose any literal K&isin;G
	    for (Iterator k = G.iterator(); k.hasNext(); ) {
		final Formula K = (Formula) k.next();
		// resolution
		final Clause  R = resolventWith(G, L, K);
		if (R != null) {
		    final Clause factorizedR = R.factorize();
		    logger.log(Level.FINER, "resolved {0} from {1} and {2}. Factorized to {3}. Lengths {4} from {5} and {6}.", new Object[] {R, F, G, factorizedR, new Integer(R.size()), new Integer(F.size()), new Integer(G.size())});

		    // @internal for perfect performance (and catastrophal structure) could already perform a goal lookahead by R.equals(Utilities.CONTRADICTION)

		    //@xxx add factorized and original, or only one, or? Or better yet factorize elsewhere?
		    resolvents.add(R);
		    if (!factorizedR.equals(R)) {
			logger.log(Level.FINER, "Adding factorized {3} of resolvent {0} from {1} and {2}.", new Object[] {R, F, G, factorizedR, new Integer(R.size()), new Integer(F.size()), new Integer(G.size())});
		    }
		    resolvents.add(factorizedR);
		}
	    }
	}
	return resolvents.iterator();
    }

    public Iterator/*_<Clause>_*/ resolveWithFactors(Clause _G) {
	final ClauseImpl F = this;
	final ClauseImpl G = (ClauseImpl)_G;
	assert F.getFreeVariables().intersection(G.getFreeVariables()).isEmpty() : "@preconditions disjoint variable variants required for resolution";
	// list views
	final List/*_<Formula>_*/ listF = new ArrayList(F);
	final List/*_<Formula>_*/ listG = new ArrayList(G);

	// resolvents will contain all resolvents of F and G
	Set/*_<Clause>_*/ resolvents = new HashSet();
	// try to resolve G with F
	// choose any literal L&isin;F
	for (ListIterator j = listF.listIterator(); j.hasNext(); ) {
	    final Formula L = (Formula) j.next();
	    // choose any literal K&isin;G
	    for (ListIterator k = listG.listIterator(); k.hasNext(); ) {
		final Formula K = (Formula) k.next();
		// resolution
		final Clause  R = resolventWith(G, L, K);
		if (R != null) {
		    resolvents.add(R);

		    // also add resolvents of factors of F and G
		    // form all subsets of literals (that are unifiable with L) to the right of L that really include L
		    final Set/*_<Set<Formula>>_*/ factorFLiteralCombinations =
			Setops.powerset(F.getUnifiables(listF.subList(j.nextIndex(), listF.size()), L));
		    // form all subsets of literals (that are unifiable with K) to the right of K that really include K
		    final Set/*_<Set<Formula>>_*/ factorGLiteralCombinations =
			Setops.powerset(G.getUnifiables(listG.subList(k.nextIndex(), listG.size()), K));
		    for (Iterator f = factorFLiteralCombinations.iterator(); f.hasNext(); ) {
			final Set/*_<Formula>_*/ factorFLiterals = (Set)f.next();
			factorFLiterals.add(L);
			final Pair pF = F.factorize2(factorFLiterals);
			if (pF == null) {
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
			    if (pG == null) {
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
			    }
			    resolvents.add(factorR);
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
	final Clause F = this;
	final Formula notL = Utilities.negation(L);
	final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {K, notL}));
	logger.log(Level.FINEST, "resolving literals {0} with {1} is {2}", new Object[] {K, notL, mu});
	if (mu == null) {
	    return null;
	} else {
	    // resolve F and G at complementary literals L resp. K
	    final Clause Gp = map(mu, clauseWithout(G, K));
	    final Clause Fp = map(mu, clauseWithout(F, L));
                        				
	    logger.log(Level.FINER, "resolving {0} with res {1} from {2} and {3}. not yet factorized. Lengths {4} from {5} and {6}.", new Object[] {new ClauseImpl(Fp),new ClauseImpl(Gp), F, G, new Integer(Fp.size()), new Integer(F.size()), new Integer(G.size())});

	    if (Fp.isElementaryValidUnion(Gp)) {
		// cut that resolution possibility since resolving with tautologies will never lead to false (the contradiction)
		//@xxx 100% sure that for completeness, we can also remove G from setOfSupport, if it only resolves to isElementaryValid clauses. Or must we keep it, even though we don't have to keep the (elementary true) resolvent
		return null;
	    }

	    // the resolvent R of F and G at complementary literals L resp. K
	    final Clause R = Gp;
	    R.addAll(Fp);
	    return R;
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
	    //@todo optimize would it be quicker if we always build variants, regardless of disjointness or not? Also unique variables would alleviate the need for variant building altogether.
	    final Clause Fprime = F.variant(overlappingVariables);
	    logger.log(Level.FINEST, "variant for resolution is {0} instead of {1} with {2} because of overlapping variables {3}", new Object[] {Fprime, F, G, overlappingVariables});
	    F = Fprime;
	} else {
	    logger.log(Level.FINEST, "no variant for resolution of {0} with {1} because of overlapping variables {2}", new Object[] {F, G, overlappingVariables});
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
	    logger.log(Level.FINEST, "no variant for resolution of {0} with {1} because of overlapping variables {2}", new Object[] {F, G, overlappingVariables});
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
	    for (Iterator k = G.iterator(); k.hasNext(); ) {
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
	
    public Clause factorize() {
	// we need a list version("view") of the set for traversing distinct literals, then we will also only modify listF not this
	final List listF = new LinkedList(this);
	assert this.equals(new HashSet(listF)) : "factorizing initial list version of this";
	final List listFfactorized = factorizeImpl(listF);
	if (listFfactorized != listF) {
	    Clause factor = construct(new HashSet(listFfactorized));
	    assert factor.size() < size() : "factorization leads to shorter clauses if applicable";
	    return factor;
	} else {
	    return this;
	}
    }
    /**
     * Implementation of {@link #factorize()}.
     * @param listF list of literals.
     * @return a new list if factorization was possible, and listF if no factorization was possible.
     * @xxx do we need all factorizations, i.e. all possible variants of factorization, or just a single greedy factorization?
     */
    private List factorizeImpl(List listF) {
	Clause previous = null;
	assert (previous = construct(this)) != null;
	try {
	    // for all literals Fi&isin;F
	    for (ListIterator i = listF.listIterator(); i.hasNext(); ) {
		final Formula Fi = (Formula) i.next();
		// for all literals Fj&isin;F with j>i
		for (ListIterator j = listF.listIterator(i.nextIndex()); j.hasNext(); ) {
		    final Formula Fj = (Formula) j.next();
		    final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {
			Fi,
			Fj
		    }));
		    assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
		    if (mu != null) {
			// factorize
			final String logPrevious = logger.isLoggable(Level.FINEST) ? construct(new HashSet(listF)) + "" : "";
			// optimized removing Fi from the set, since mu(Fi) = mu(Fj) anyway (notice the set representation). But there no optimization here, isn't it?
			assert mu.apply(Fi).equals(mu.apply(Fj));
			j.remove();
			// apply unification and remove duplicates, but convert to list again.
			listF = new LinkedList(new HashSet(Functionals.map(mu, listF)));
			assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
			if (logger.isLoggable(Level.FINEST)) {
			    logger.log(Level.FINEST, "factorized {1} from {0} by unifying {3} and {4} with {2}", new Object[] {logPrevious, construct(new HashSet(listF)), mu, Fi, Fj});
			}
			// factorize again
			//@todo could optimize away recursive call
			return factorizeImpl(listF);
		    }
		}
	    }
	    // no factorization possible
	    return listF;
	}
	finally {
	    assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
	}
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
	Pair p = factorize2(literals);
	return p == null ? null : (Clause)p.B;
    }
    /**
     * Workaround for returning 2 arguments.
     * @return the pair of substitution and resulting factor, respectively <code>null</code>.
     */
    protected Pair/*<Substitution,Clause>*/ factorize2(Collection/*_<Clause>_*/ literals) {
	assert this.containsAll(literals) : "can only factorize literals contained in this clause";
	if (literals.size() < 2) {
	    //@internal just a speedup optimization
	    return new Pair(Substitutions.id, this);
	}
	Clause previous = null;
	assert (previous = construct(this)) != null;
	final Substitution mu = Substitutions.unify(literals);
	assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
	if (mu == null) {
	    return null;
	} else {
	    // factorize
	    //@todo optimize by removing all (but one in) literals from clause prior to applying mu
	    // apply unification and remove duplicates, but convert to list again.
	    Clause factor = map(mu, this);
	    assert this.equals(previous) : "modifications during factorization work on copies, and leave the original clause unmodified";
	    if (logger.isLoggable(Level.FINEST)) {
		logger.log(Level.FINEST, "factorized {1} from {0} by unifying {2} by {3}", new Object[] {construct(this), construct(factor), mu, literals});
	    }
	    return new Pair(mu, factor);
	}
    }

    
    public boolean subsumes(Clause D) {
	if (size() > D.size())
	    return false;
	// negate D and replace all variables with distinct constants (also distinct for each literal)
	final ClausalSet notDground = new ClausalSetImpl();
	for (Iterator i = D.iterator(); i.hasNext(); ) {
	    final ClauseImpl notDi = new ClauseImpl(Collections.singleton(Utilities.negation((Formula)i.next())));
	    final Clause notDiground = notDi.variant(notDi.getFreeVariables(), true);
	    assert notDiground.getFreeVariables().isEmpty() : "ground instances have no free variables " + notDiground + " stemming from " + notDi + " has FV=" + notDiground.getFreeVariables();
	    notDground.add(notDiground);
	}

	ClausalSet u = new ClausalSetImpl();
	u.add(new ClauseImpl(this));
	final ClausalSet input = notDground;
	assert !input.contains(Clause.CONTRADICTION) : "contains no elementary contradiction";
	assert !u.contains(Clause.CONTRADICTION) : "contains no elementary contradiction, otherwise " + this + " is " + Clause.CONTRADICTION;
	int count = 0;
	while (!u.isEmpty()) {
	    // the set of resolvents obtained from resolution of any C1 with any C2
	    final ClausalSet newResolvents = new ClausalSetImpl();

	    // for each clause C1&isin;U
	    for (Iterator i = u.iterator(); i.hasNext(); ) {
		final Clause C1 = (Clause)i.next();
		assert !C1.equals(Clause.CONTRADICTION) : "already checked for contradiction";

		// choose any clause C2&isin;input
		for (Iterator i2 = input.iterator(); i2.hasNext(); ) {
		    final Clause C2 = (Clause) i2.next();
		    // try to resolve C1 with C2
		    //@internal no variant forming needed since input of unit input resolution is ground
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

    public Iterator/*_<Formula>_*/ getProbableUnifiables(Formula L) {
	//@todo use indexing for far better implementation
	return iterator();
    }

    public Set/*_<Formula>_*/ getUnifiables(Formula L) {
	return getUnifiables(this, L);
    }
    
    /**
     * Get all literals contained in C that unify with
     * L. <p>Implementations may use indexing or links to estimate the
     * clauses to return very quickly.</p>
     * @preconditions C&sube;this
     * @postconditions RES = {F&isin;this &exist;mgU{L,F}}
     * @see #getProbableUnifiables(Formula)
     */
    public Set/*_<Formula>_*/ getUnifiables(Collection/*_<Formula>_*/ C, Formula L) {
	Set/*_<Formula>_*/ r = new HashSet();
	for (Iterator i = C.iterator(); i.hasNext(); ) {
	    Formula F = (Formula)i.next();
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
	return fc instanceof Clause ? (Clause)fc : construct(fc);
    }

    /**
     * @return S\{x} as a new clause.
     */
    private Clause clauseWithout(Clause S, Object x) {
	Clause Sp = construct(S);
	Sp.remove(x);
	return Sp;
    }

    /**
     * @return S\{x} as a new set.
     */
    private static Set setWithout(Set S, Object x) {
	Set Sp = new HashSet(S);
	Sp.remove(x);
	return Sp;
    }

}
