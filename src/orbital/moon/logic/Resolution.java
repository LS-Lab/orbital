/**
 * @(#)Resolution.java 0.9 2001/07/30 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;
import orbital.moon.logic.ClassicalLogic.Utilities;

import orbital.logic.imp.*;

import orbital.algorithm.template.GeneralSearchProblem;
import orbital.algorithm.template.*;
import orbital.logic.functor.*;
import orbital.logic.trs.Substitution;
import orbital.logic.trs.Substitutions;

import orbital.moon.logic.bridge.SubstitutionImpl.UnifyingMatcher;

import orbital.util.SequenceIterator;
import orbital.util.StreamMethod;
import orbital.util.Setops;
import java.util.*;

import orbital.logic.functor.Functor.Specification;
import orbital.logic.functor.Notation.NotationSpecification;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Set of support resolution.
 * <p>
 * General resolution procedure for M<sub>1</sub>,...,M<sub>n</sub> &#8872; A is
 * <ol>
 *   <li>E := {Cl<sub>&forall;</sub>M<sub>1</sub>,...,Cl<sub>&forall;</sub>M<sub>1</sub>,&not;Cl<sub>&forall;</sub>A}</li>
 *   <li>transform each formula in E to prenex normal form</li>
 *   <li>
 *     transform each formula in E to Skolem normal form with matrices in conjunctive normal form.
 *     The Skolem-functions introduced are new and distinct.
 *   </li>
 *   <li>drop the &forall;-quantifiers and write the remaining conunctions of disjunctions of literals
 *     als sets of clauses.
 *     The union of these clauses is (again) called E.
 *   </li>
 *   <li>
 *     try to resolve from E (with building variants to achieve disjunct variables) the empty clause &#9633;:
 *     <pre>
 *     R := E
 *     <span class="keyword">while</span> &#9633;&notin;R <span class="keyword">do</span>
 *         <span class="keyword">if</span> there are clauses C<sub>1</sub>,C<sub>2</sub>&isin;R, and a variable renaming &rho;
 *                 with Resolvent(C<sub>1</sub>, &rho;(C<sub>2</sub>)) &notin; R
 *         <span class="keyword">then</span>
 *             <span class="keyword">choose</span> such clauses C<sub>1</sub>,C<sub>2</sub>&isin;R, and
 *             <span class="keyword">choose</span> such a variable renaming &rho;
 *             R := R&cup;{Resolvent(C<sub>1</sub>, &rho;(C<sub>2</sub>))}
 *         <span class="keyword">else</span>
 *             <span class="keyword">return</span> fail
 *         <span class="keyword">fi</span>
 *     <span class="keyword">end</span>
 *     <span class="keyword">return</span> success
 *     </pre>
 *   </li>
 * </ol>
 * </p>
 *
 * @version 0.7, 2001/07/30
 * @author  Andr&eacute; Platzer
 * @todo Skolem-Normalfrom mit KNF-Matrix (mit Erweiterung der Signatur)
 * @todo schlaues pränex-Normalform Bilden, um einfacher skolemisieren zu können. Minimiere die Anzahl der Allquantoren vor den Existenzquantoren.
 *  etwa &exist;x&forall;y P &and; &exist;z&forall;w Q  == &exist;x&exist;z &forall;y&forall;w P&and;Q statt == &exist;x&forall;y&exist;z&forall;w P&and;Q denn im ersteren Fall skolemisiert x zu a und z zu b, im zweiteren aber x zu a und z zu f(y).
 *  Oder wähle alternativen (einfacheren?) TRS-Algorithmus Ü 7.95
 * @todo use do/undo instead of copying the whole set of derived formulas every time.
 * @todo delegate GSP into a private inner class
 * @todo use optimizations of "Deduktions- und Inferenzsysteme"
 */
public class Resolution implements Inference {
    private static final boolean UNDER_CONSTRUCTION = true;
    private static final boolean ASYNCHRONOUS_EXPAND = true;
    private static final Logger logger = Logger.getLogger(Resolution.class.getPackage().getName());
    private static final ClassicalLogic logic = new ClassicalLogic();
	
    /**
     * Whether or not to use simplified clausal forms.
     */
    private static final boolean simplifying = false;

    /**
     * contradictory clause &empty; &equiv; &#9633; &equiv; &perp;.
     * <p>
     * The contradictory set of clauses is {&empty;}={&#9633;}
     * while the tautological set of clauses is {}.
     * </p>
     */
    public static final Set/*_<Formula>_*/ CONTRADICTION = Collections.EMPTY_SET;

    private static final Formula FORMULA_FALSE = (Formula) logic.createAtomic(new SymbolBase("false", SymbolBase.BOOLEAN_ATOM));
    private static final Formula FORMULA_TRUE = (Formula) logic.createAtomic(new SymbolBase("true", SymbolBase.BOOLEAN_ATOM));
	
    /**
     * the search algorithm used.
     */
    private final GeneralSearch search;
    public Resolution() {
        //@xxx IterativeDeepending does not want to stop sometimes (f.ex. for (a|a)<=>~a which is false) even for (a&b)<=>(b&a) which is true?
        //this.search = new IterativeDeepening();
        //@xxx BreadthFirstSearch will lead us to producing a lot of dangling threads
        this.search = new BreadthFirstSearch();
    }

    public boolean infer(Formula[] B, Formula D) {
	if (D == ModernFormula.EMPTY)
	    // avoid funny case that results from the fact, that we cannot identify EMPTY with true, here
	    return true;

        // skolemize B
        List/*_<Formula>_*/ skolemizedB = new ArrayList(B.length);
        for (int i = 0; i < B.length; i++)
	    skolemizedB.add(Utilities.dropQuantifiers(Utilities.skolemForm(B[i])));

        // convert skolemizedB to clausalForm knowledgebase
        Set/*_<Set<Formula>>_*/ knowledgebase = new HashSet();
        for (Iterator i = skolemizedB.iterator(); i.hasNext(); )
	    knowledgebase.addAll(clausalForm((Formula) i.next()));

	// factorize and remove tautologies
    	// for all clauses F&isin;knowledgebase
    	for (Iterator i = knowledgebase.iterator(); i.hasNext(); ) {
	    Set F = (Set) i.next();
	    final Set factorizedF = factorize(F);
	    if (factorizedF != null)
		F = factorizedF;
	    if (F.equals(CONTRADICTION))
		throw new IllegalStateException("knowledge base is inconsistent since it already contains a contradiction, so ex falso quodlibet");
	    else if (isElementaryValid(F, F))
		// if F is obviously valid, forget about it for resolving a contradiction
		i.remove();
	}

        logger.log(Level.FINE, "W = {0}", knowledgebase);
        if (logger.isLoggable(Level.FINER))
	    for (int i = 0; i < B.length; i++)
		logger.log(Level.FINER, "W contains original {0}", Utilities.conjunctiveForm(B[i], simplifying));

        // negate query since we are a negative test calculus
        Formula query = D.not();

	// skolemize query
	Formula skolemizedQuery = Utilities.dropQuantifiers(Utilities.skolemForm(query));

        // convert negated query to clausalForm S forming the initial set of support
	Set S = clausalForm(skolemizedQuery);

        if (logger.isLoggable(Level.FINEST))
	    logger.log(Level.FINEST, "negated goal S = {0} = {1} (= {2} original)", new Object[] {skolemizedQuery, S, Utilities.conjunctiveForm(query, simplifying)});
	else
	    logger.log(Level.FINER, "negated goal S = {0} = {1}", new Object[] {skolemizedQuery, S});

	// factorize and remove tautologies
    	// for all clauses F&isin;S
    	for (Iterator i = S.iterator(); i.hasNext(); ) {
	    Set F = (Set) i.next();
	    final Set factorizedF = factorize(F);
	    if (factorizedF != null)
		F = factorizedF;
	    if (F.equals(CONTRADICTION))
		// note that we could just as well check for contradictions prior to factorizing, because factorization does not introduce contradictions
		throw new IllegalStateException("the query already contains a contradiction");
	    if (isElementaryValid(F, F))
		// if F is obviously valid, forget about it for resolving a contradiction
		i.remove();
	}    		

        final Object solution = search.solve(new ResolutionProblem(knowledgebase, S));
	logger.log(Level.FINE, "found solution {0}", solution);
        return solution != null;
    }
	
    public boolean isSound() {
	return true;
    }

    public boolean isComplete() {
	if (UNDER_CONSTRUCTION)
	    return false;
	// assuming knowledge base W is consistent, we are refutation-complete
	return true;
    }
	
    private final class ResolutionProblem implements GeneralSearchProblem {
    	/**
    	 * knowledge base W assumed consistent.
    	 * W is kept in clausal normal form, and thus contains sets of literals.
    	 */
    	private final Set/*_<Set<Formula>>_*/ knowledgebase;
    	/**
    	 * the initial set of support.
    	 */
    	private final Set/*_<Set<Formula>>_*/ setOfSupport;
    	public ResolutionProblem(Set/*_<Set<Formula>>_*/ knowledgebase, Set/*_<Set<Formula>>_*/ setOfSupport) {
	    this.knowledgebase = knowledgebase;
	    this.setOfSupport = setOfSupport;
    	}

        public Object getInitialState() {
	    return setOfSupport;
    	}
        public boolean isSolution(Option n) {
	    logger.log(Level.FINE, "isSolution=={0} of the option {1}", new Object[] {new Boolean(CONTRADICTION.equals(n.getAction())), n});
	    // solely rely on goal lookahead
	    return CONTRADICTION.equals(n.getAction());
	    /*Set S = (Set) n.getState();
	      return S.contains(CONTRADICTION);*/
    	}
        //@todo optimizable by far! And also optimize space by do/undo
        public Iterator expand(final Option n) {
	    return new StreamMethod(ASYNCHRONOUS_EXPAND) {
		    public void runStream() {
                	final Set/*_<Set<Formula>>_*/ S = (Set) n.getState();
			// we use a list view of the set for optimized resolving (after having resolved G with F, we won't resolve F with G again), but we will only need to modify the set F
			final List					  listS = Collections.unmodifiableList(new LinkedList(S));
                	Collection					  r = new LinkedList();
                	// choose any clause G&isin;S
                	for (ListIterator i = listS.listIterator(); i.hasNext(); ) {
			    final Set/*_<Formula>_*/ G = (Set) i.next();
			    final Signature			 GVariables = clausalFreeVariables(G);
			    boolean					 resolvable = false;
			    assert !G.equals(CONTRADICTION) : "already checked for goal in isSolution() although this is somewhat less performant";
    
			    // if we already tried to resolve F with G, we don't need to resolve G with F, again, so
			    // choose any clause F&isin;W&cup;S (that does not occur before G in S)
			    for (Iterator i2 = new SequenceIterator(new Iterator[] {knowledgebase.iterator(), listS.listIterator(i.previousIndex())});
				 i2.hasNext(); ) {
				Set/*_<Formula>_*/ F = (Set) i2.next();
				final Signature	   FVariables = clausalFreeVariables(F);
				final Signature	   overlappingVariables = GVariables.intersection(FVariables);
				if (!overlappingVariables.isEmpty()) {
				    // make a variant of F such that the variables of F and G are disjunct
				    F = variantOf(F, overlappingVariables);
				}
				// try to resolve G with F (to L)
				// choose any literal Fj&isin;F
                        	for (Iterator j = F.iterator(); j.hasNext(); ) {
				    final Formula Fj = (Formula) j.next();
				    final Formula notFj = negation(Fj);
				    // choose any literal Gk&isin;G
				    for (Iterator k = G.iterator(); k.hasNext(); ) {
                            		final Formula	   Gk = (Formula) k.next();
                            		// generalized resolution
					final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {Gk, notFj}));
					logger.log(Level.FINEST, "resolving literals {0} with {1} leads to {2}", new Object[] {Gk, notFj, mu});
					if (mu != null) {
					    resolvable = true;
					    // resolve F and G into a new clause L
					    Set Gp = new HashSet(G);
					    Gp.remove(Gk);
					    Set Fp = new HashSet(F);
					    Fp.remove(Fj);
					    Gp = (Set) Functionals.map(mu, Gp);
					    Fp = (Set) Functionals.map(mu, Fp);
                        				
					    if (isElementaryValid(Fp, Gp))
						// cut that possibility since resolving with tautologies will never lead to false (the contradiction)
						continue;
                        				
					    Set R = Gp;
					    R.addAll(Fp);
					    logger.log(Level.FINER, "resolved {0} from {1} and {2}", new Object[] {R, F, G});
					    final Set factorizedR = factorize(R);
					    if (factorizedR != null)
						R = factorizedR;
					    final Set resultingClauseSet = new HashSet(S);
					    resultingClauseSet.add(R);
					    logger.log(Level.FINEST, "RESRET {0} @todo what's this?", R);
					    resumedReturn(new Option(resultingClauseSet, R, n.getCost() + getCost(null)));
                        				
					    // goal lookahead
					    if (R.equals(CONTRADICTION)) {
						logger.log(Level.FINE, "resolved contradiction {0} from {1} and {2}",  new Object[] {R, F, G});
						// cut the search tree after resuming with CONTRADICTION as action
						return;
					    }
					}
				    }
				}
			    }
    
			    if (!resolvable)
				//@todo couldn't we somehow now the index from our list iterator i for removing G by i.remove()?
				S.remove(G);
                	}
		    }
    		}.apply();
    	}
        public double getCost(Option n) {
	    return 1;
    	}
    }
    
    // clause and clause set handling
	
    /**
     * Transforms into clausal form.
     * <p>
     * Defined per structural induction.
     * </p>
     * @todo assert
     */
    public static final Set/*_<Set<Formula>>_*/ clausalForm(Formula f) {
	try {
	    return clausalFormClauses(Utilities.conjunctiveForm(f, simplifying));
	}
	catch (IllegalArgumentException ex) {
	    throw (AssertionError) new AssertionError(ex.getMessage() + " in " + Utilities.conjunctiveForm(f, simplifying) + " of " + f).initCause(ex);
	}
    }
    /**
     * convert a formula (that is in CNF) to a set of clauses.
     */
    private static final Set/*_<Set<Formula>>_*/ clausalFormClauses(Formula term) {
	//@todo assert assume right-associative nesting of &
	if (term instanceof Functor.Composite) {
            Functor.Composite f = (Functor.Composite) term;
	    Functor           op = (Functor) f.getCompositor();
	    if (op == ClassicalLogic.LogicFunctions.and) {
		Formula[] components = (Formula[]) f.getComponent();
		assert components.length == 2 : "binary " + op + "/" + components.length + " expected";
		return Setops.union(clausalFormClauses(components[0]), clausalFormClauses(components[1]));
	    } else if (op == ClassicalLogic.LogicFunctions.or) {
		Formula[] components = (Formula[]) f.getComponent();
		assert components.length == 2 : "binary " + op + "/" + components.length + " expected";
		Set C = Setops.union(clausalFormClause(components[0]), clausalFormClause(components[1]));
		return C.contains(FORMULA_TRUE) ? Collections.EMPTY_SET : singleton(C);
	    } else if (op == ClassicalLogic.LogicFunctions.not) {
		Object c = f.getComponent();
		// evaluate constants
		if (FORMULA_FALSE.equals(c))
		    return clausalFormClauses(FORMULA_TRUE);
		else if (FORMULA_TRUE.equals(c))
		    return clausalFormClauses(FORMULA_FALSE);
		return singleton(singleton(term));
	    } else if (!(op instanceof ModernFormula.AtomicSymbol))
		throw new IllegalArgumentException("conjunctive normal form should not contain " + op + " of " + op.getClass());
        }
	// atomic parts
	return term.toString().equals("false")
	    ? singleton(CONTRADICTION)
	    : term.toString().equals("true")
	    ? Collections.EMPTY_SET
	    : singleton(singleton(term));
    }
    /**
     * convert a formula (that is a disjunction of literals (from CNF)) to a single clause.
     * @return the clause, note that the clause can be further collapsed if it contains true.
     */
    private static final Set/*_<Formula>_*/ clausalFormClause(Formula term) {
	if (term instanceof Functor.Composite) {
            Functor.Composite f = (Functor.Composite) term;
	    Functor			  op = (Functor) f.getCompositor();
	    if (op == ClassicalLogic.LogicFunctions.or) {
		Formula[] components = (Formula[]) f.getComponent();
		assert components.length == 2 : "binary " + op + "/" + components.length + " expected";
		return Setops.union(clausalFormClause(components[0]), clausalFormClause(components[1]));
	    } else if (op == ClassicalLogic.LogicFunctions.not) {
		Object c = f.getComponent();
		// evaluate constants
		if (FORMULA_FALSE.equals(c))
		    return clausalFormClause(FORMULA_TRUE);
		else if (FORMULA_TRUE.equals(c))
		    return clausalFormClause(FORMULA_FALSE);
		return singleton(term);
	    } else if (op == ClassicalLogic.LogicFunctions.and)
		throw new IllegalArgumentException("(right-associative) conjunctive normal form should not contain " + op + ". Make sure the formula is right-associative for &");
	    else if (!(op instanceof ModernFormula.AtomicSymbol))
		throw new IllegalArgumentException("conjunctive normal form should not contain " + op);
        }
	// atomic parts
	return term.toString().equals("false")
	    ? CONTRADICTION
	    : singleton(term);
    }

    private static final Set singleton(Object o) {
	Set r = new HashSet();
	r.add(o);
	return r;
    }

    /**
     * Get the free variables of a formula represented as a clause.
     * @return freeVariables(clause)
     * @internal note that for clauses FV(C)=V(C) &and; BV(C)=&empty;
     */
    private static final Signature clausalFreeVariables(Set/*_<Formula>_*/ clause) {
	// return banana (|&empty;,&cup;|) (map ((&lambda;x)x.freeVariables()), clause)
	Set freeVariables = new HashSet();
	for (Iterator i = clause.iterator(); i.hasNext(); )
	    freeVariables.addAll(((Formula)i.next()).getVariables());
	return new SignatureBase(freeVariables);
    }

    /**
     * Get a variant of the clause F with the given variables renamed.
     * &alpha;-conversion
     * @param disjunctify the variables to rename (in order to produce a variable disjunct variant of F relative to some formula G).
     * @internal Variantenbildung in disjunkte Variablen erforderlich(!). Dazu entweder die aus V(F)&cap;V(G) etwa in F umbenennen, oder mit Variable.setSymbol(Variable.getSymbol()+neueNummer) alles explizit fortzählen.
     */
    private static final Set/*_<Formula>_*/ variantOf(Set/*_<Formula>_*/ F, Signature disjunctify) {
	List/*_<Symbol>_*/ renaming = new ArrayList(disjunctify.size());
	for (Iterator i = disjunctify.iterator(); i.hasNext(); ) {
	    renaming.add(Substitutions.createExactMatcher(i.next(), new DistinctSymbol(SymbolBase.UNIVERSAL_ATOM, null, true)));
	}
	return (Set) Functionals.map(Substitutions.getInstance(renaming), F);
    }

    // negation aware of duplex negatio est affirmatio
	
    /**
     * Get the negation of F without introducing duplex negatios.
     * @return G if F=&not;G, and &not;F otherwise.
     * @post RES==ClassicalLogic.conjunctiveForm(F.not())
     */
    private static final Formula negation(Formula F) {
	// used duplex negatio est affirmatio (optimizable)
	if ((F instanceof Functor.Composite)) {
	    Functor.Composite f = (Functor.Composite) F;
	    Object			  g = f.getCompositor();
	    if (g == ClassicalLogic.LogicFunctions.not)
		// use duplex negatio est affirmatio to avoid double negations
		return (Formula) f.getComponent();
	}
	// two special cases of negation that can be evaluated
	if (FORMULA_FALSE.equals(F))
	    return FORMULA_TRUE;
	else if (FORMULA_TRUE.equals(F))
	    return FORMULA_FALSE;
	return F.not();
    }

    // proof utilities
	
    /**
     * Returns true whether the union F&cup;G would obviously contain an elementary tautology.
     * That is a p&or;&not;p &isin; F&cup;G
     * <p>
     * We can forget about elementary valid clauses for resolving false,
     * because true formulas will only imply true formulas, never false ones.
     * </p>
     * @param F a clause
     * @param G a clause
     * @todo or even when there is a single-sided matcher of p and q in p&or;&not;q?
     * @pre (&not;isElementaryValid(F) &and; &not;isElementaryValid(G)) &or; F=G
     * @todo look for true&isin;F?
     */
    private static boolean isElementaryValid(Set/*_<Formula>_*/ F, Set/*_<Formula>_*/ G) {
	// for all literals Fj&isin;F
    	for (Iterator j = F.iterator(); j.hasNext(); ) {
	    final Formula Fj = (Formula) j.next();
	    final Formula notFj = negation(Fj);
	    // for all literals Gk&isin;G
	    for (Iterator k = G.iterator(); k.hasNext(); ) {
		final Formula Gk = (Formula) k.next();
		if (Gk.equals(notFj))
		    return true;
	    }
        }
        return false;
    }
	
    /**
     * Factorize a clause as much as possible.
     * <p>
     * Will implement the factorization rule necessary for binary resolution:
     * <div>{L1,...,Ln} |- {s(L1),...,s(Lk)} with s=mgU({Lk,...,Ln})</div>
     * Which is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Lk,...,Ln})</div>
     * because of the set representation, and which again is the same as
     * <div>{L1,...,Ln} |- {s(L1),...,s(Ln)} with s=mgU({Li,Lj})</div>
     * because of set notation. The latter is the way we (currently) implement things.
     * </p>
     * @return the factorized clause, or <code>null</code> if no factorization was possible.
     */
    private static final Set/*_<Formula>_*/ factorize(Set/*_<Formula>_*/ F) {
	// we need a list view of the set for traversing distinct literals, but we will only need to modify the set F
	final List listF = Collections.unmodifiableList(new LinkedList(F));
	// for all literals Fi&isin;F
    	for (ListIterator i = listF.listIterator(); i.hasNext(); ) {
	    final Formula Fi = (Formula) i.next();
	    // for all literals Fj&isin;F with j>i
	    for (ListIterator j = listF.listIterator(i.nextIndex()); j.hasNext(); ) {
		final Formula Fj = (Formula) j.next();
		final Substitution mu = Substitutions.unify(Arrays.asList(new Object[] {Fi, Fj}));
		if (mu != null) {
                    final String logPrevious = logger.isLoggable(Level.FINEST) ? F + "" : "";
		    // optimized removing Fj from the set, since mu(Fj) = mu(Fk) anyway (notice the set representation)
		    //@todo couldn't we somehow now the index from our list iterator j?
		    F.remove(Fj);
                    F = (Set) Functionals.map(mu, F);
                    logger.log(Level.FINEST, "factorized {1} from {0} by unifying {3} and {4} with {2}", new Object[] {logPrevious, F, mu, Fi, Fj});
		    Set/*_<Formula>_*/ factorizedAgain = factorize(F);
		    return factorizedAgain != null ? factorizedAgain : F;
		}
	    }
        }
        return F;
    }
}
