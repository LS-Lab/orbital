/**
 * @(#)Resolution.java 0.9 2001/07/30 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;
import orbital.moon.logic.ClassicalLogic;
import orbital.moon.logic.ClassicalLogic.Utilities;

import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.Types;
import orbital.logic.sign.Expression.Composite;

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
import orbital.logic.sign.concrete.Notation.NotationSpecification;

import orbital.math.Values;

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
 *   <li>drop the &forall;-quantifiers and write the remaining conjunctions of disjunctions of literals
 *     as sets of clauses.
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
 * @todo introduce "class Clause extends Set<Formula>" adding more type-safety for clauses and sets of clauses which are both sets.
 * @todo Skolem-Normalfrom mit KNF-Matrix (mit Erweiterung der Signatur)
 * @todo schlaues pränex-Normalform Bilden, um einfacher skolemisieren zu können. Minimiere die Anzahl der Allquantoren vor den Existenzquantoren.
 *  etwa &exist;x&forall;y P &and; &exist;z&forall;w Q  == &exist;x&exist;z &forall;y&forall;w P&and;Q statt == &exist;x&forall;y&exist;z&forall;w P&and;Q denn im ersteren Fall skolemisiert x zu a und z zu b, im zweiteren aber x zu a und z zu f(y).
 *  Oder wähle alternativen (einfacheren?) TRS-Algorithmus Ü 7.95
 * @todo use do/undo instead of copying the whole set of derived formulas every time.
 * @todo use optimizations of "Deduktions- und Inferenzsysteme"
 * @internal proving F |= A->B and F |= B->A separately often is far more performant than proving F |= A<->B.
 * @todo sometime. By using that "<->" is a congruence, we could perhaps optimize the following:
 *  A premise p<->q could be used to substitute p by q throughout.
 *  A premise p<->F&G... could be used to substitute p by F&G... throughout.
 *  But a premise p|q<->a|b cannot, generally.
 * @todo optimize by using a non-branching search, somewhat like local optimizers, since we do not want to optimize but only to reach the goal. If we have derived a formula once, we should never switch to another branch and forget it again.
 * @fixme we seem to have a race condition: trying to prove the wrong "[]A -> <>[]A", we only succeed to say no, when any loggers are active (even when they never log anything).
 * But we can only prove when that's the first conjecture, when it's hidden in "modal-equivalence.txt" our proof does not terminate either.
 * It also depends on how many conjectures are in "modal-equivalence.txt"
 */
public class Resolution implements Inference {
    private static final boolean ASYNCHRONOUS_EXPAND = false;
    /**
     * Whether or not to use simplified clausal forms.
     */
    private static final boolean SIMPLIFYING = false;

    private static final Logger logger = Logger.getLogger(Resolution.class.getName());
    private static final ClassicalLogic logic = new ClassicalLogic();
	
    /**
     * the search algorithm used.
     * @xxx maybe for reentrance we should always instantiate a new search algorithm for each infer() ?
     */
    private final GeneralSearch search;
    public Resolution() {
	//@internal we do search for cheapest solutions but for first solution
        //this.search = new IterativeDeepening();
        //@xxx BreadthFirstSearch will lead us to producing a lot of dangling threads if ASYNCHRONOUS_EXPAND=true
	//        this.search = new BreadthFirstSearch();
        //this.search = new IterativeDeepening();

	//@todo use IDA* with a non-admissible heuristic h(s):=5 or anything such that we always deepen the bound by more than 1?
	//@xxx IE(h=5) is incomplete for "|=  ((a|b)&c ) <=> ( (a&c)|(b&c) )"
	//this.search = new IterativeExpansion(orbital.math.functional.Functions.constant(Values.getDefaultInstance().valueOf(5)));
	//this.search = new IterativeDeepeningAStar(orbital.math.functional.Functions.constant(Values.getDefaultInstance().valueOf(2)));
	//this.search = new IterativeDeepeningAStar(orbital.math.functional.Functions.constant(Values.getDefaultInstance().valueOf(0)));

        this.search = new IterativeDeepeningAStar(heuristic);
    }

    public boolean infer(final Formula[] B, final Formula D) {
        // skolemize B and drop quantifiers
        List/*_<Formula>_*/ skolemizedB = new ArrayList(B.length);
        for (int i = 0; i < B.length; i++) {
	    skolemizedB.add(Utilities.dropQuantifiers(Utilities.skolemForm(B[i])));
	    if (logger.isLoggable(Level.FINEST))
		logger.log(Level.FINEST, "in W skolemForm( {0} ) == {1}", new Object[] {B[i], Utilities.skolemForm(B[i])});
	}

        // convert B to clausalForm knowledgebase
        ClausalSet knowledgebase = new ClausalSetImpl();
        for (Iterator i = skolemizedB.iterator(); i.hasNext(); ) {
	    knowledgebase.addAll(clausalForm((Formula) i.next(), SIMPLIFYING));
	}

	// factorize and remove tautologies
    	// for all clauses F&isin;knowledgebase
    	for (Iterator i = knowledgebase.iterator(); i.hasNext(); ) {
	    final Clause F = ((Clause) i.next()).factorize();
	    if (F.equals(Utilities.CONTRADICTION))
		throw new IllegalStateException("premises are inconsistent since they already contain a contradiction, so ex falso quodlibet");
	    else if (F.isElementaryValid())
		// if F is obviously valid, forget about it for resolving a contradiction
		i.remove();
	}

        logger.log(Level.FINE, "W == {0}", knowledgebase);
        if (logger.isLoggable(Level.FINEST))
	    for (int i = 0; i < B.length; i++)
		logger.log(Level.FINEST, "W thus contains transformation of original formula {0}", Utilities.conjunctiveForm(B[i], SIMPLIFYING));

        // negate query since we are a negative test calculus
        Formula query = D.not();

	// skolemize (negated) query
	//@todo could we optimize by already transforming query to CNF, somewhat earlier? At least avoid transforming ~(a<->b) to ~(cnf(a<->b))
	Formula skolemizedQuery = Utilities.dropQuantifiers(Utilities.skolemForm(query));

        // convert (negated) query to clausalForm S, forming the initial set of support
	ClausalSet S = clausalForm(skolemizedQuery, SIMPLIFYING);

	// factorize and remove tautologies
    	// for all clauses F&isin;S
    	for (Iterator i = S.iterator(); i.hasNext(); ) {
	    final Clause F = ((Clause) i.next()).factorize();
	    if (F.equals(Utilities.CONTRADICTION))
		// note that we could just as well check for contradictions prior to factorizing, because factorization does not introduce contradictions
		throw new IllegalStateException("the query already contains a contradiction: " + D.not() + " = " + S);
	    if (F.isElementaryValid())
		// if F is obviously valid, forget about it for resolving a contradiction
		i.remove();
	}    		

        if (logger.isLoggable(Level.FINEST))
	    logger.log(Level.FINEST, "negated goal S == {0}\n == {1}\n (== {2} original in CNF)", new Object[] {skolemizedQuery, S, Utilities.conjunctiveForm(query, SIMPLIFYING)});
	logger.log(Level.FINE, "negated goal S == {0}\n == {1}", new Object[] {skolemizedQuery, S});

        final Object solution = search.solve(new ResolutionProblem(knowledgebase, S));
	logger.log(Level.FINE, "found solution {0}", solution);
        return solution != null;
    }
	
    public boolean isSound() {
	return true;
    }

    public boolean isComplete() {
	// assuming knowledge base W is consistent, we are refutation-complete
	return true;
    }

    /**
     * A (non-admissible) heuristic that prefers smaller length of clauses for resolving.
     * @internal in each resolution step only one literal is resolved so the size decreases by one
     *  (apart from cases where the same literals occur in both clauses being resolved).
     */
    private final Function heuristic = new Function() {
	    private final Values valueFactory = Values.getDefaultInstance();
	    public Object apply(Object o) {
		Set R = ((Proof)o).resolvent;
		return valueFactory.valueOf(R == null ? 0 : R.size());
	    }
	};

    /**
     * @internal we identify S=A here such that we can perform all work in actions().
     */
    private final class ResolutionProblem implements GeneralSearchProblem/*<Proof,Proof>*/ {
    	/**
    	 * knowledge base W assumed consistent.
    	 * W is kept in clausal normal form, and thus contains sets of literals.
    	 */
    	private final ClausalSet knowledgebase;
    	/**
    	 * the initial set of support.
    	 */
    	private final ClausalSet setOfSupport;
    	public ResolutionProblem(ClausalSet knowledgebase, ClausalSet setOfSupport) {
	    //@internal unmodifiable final is optional
	    this.knowledgebase = new ClausalSetImpl(Collections.unmodifiableSet(knowledgebase));
	    this.setOfSupport = new ClausalSetImpl(Collections.unmodifiableSet(setOfSupport));
    	}

        public Object getInitialState() {
	    //@internal since the proof states may modify setOfSupport, use a copy so that we can reuse the initial state
	    return new Proof(new ClausalSetImpl(setOfSupport), null, Values.ZERO);
    	}
	public MutableFunction getAccumulatedCostFunction() {
	    return _accumulatedCostFunction;
	}
	private final MutableFunction _accumulatedCostFunction = new MutableFunction() {
		public Object apply(Object s) {
		    return ((Proof) s).accumulatedCost;
		}
		public Object set(Object s, Object newAccumulatedCost) {
		    Proof p = (Proof)s;
		    Object old = p.accumulatedCost;
		    p.accumulatedCost = newAccumulatedCost;
		    return old;
		}
		public Object clone() {
		    throw new UnsupportedOperationException();
		}
	    };
        public boolean isSolution(Object n) {
	    final ClausalSet S = ((Proof) n).setOfSupport;
	    // solely rely on goal lookahead (see below)
	    final boolean goal = S.size() == 1 && S.contains(Utilities.CONTRADICTION);
	    logger.log(Level.FINE, "isSolution=={0} of the clauses {1}", new Object[] {new Boolean(goal), S});
	    return goal;
    	}
        //@todo optimizable by far! And also optimize space by do/undo
        public Iterator actions(final Object/*>S<*/ n) {
	    return new StreamMethod(ASYNCHRONOUS_EXPAND) {
		    public void runStream() {
                	final ClausalSet S = ((Proof) n).setOfSupport;
			// we use a list view of the set S for optimized resolving (after having resolved G with F, we won't resolve F with G again). But we only modify the set F&isin;S=listS, and thus - indirectly - S and listS.
			final List	 listS = Collections.unmodifiableList(new LinkedList(S));
                	Collection	 r = new LinkedList();
                	// choose any clause G&isin;S
                	for (ListIterator i = listS.listIterator(); i.hasNext(); ) {
			    final Clause    G = (Clause) i.next();
			    final Signature GVariables = G.getFreeVariables();
			    boolean	    resolvable = false;
			    assert !G.equals(Clause.CONTRADICTION) : "already checked for goal in isSolution() although this is somewhat less performant. So we do not need to check again in actions()";
    
			    // if we already tried to resolve F with G, we don't need to resolve G with F, again, so
			    // choose any clause F&isin;W&cup;S (that does not occur before G in S)
			    for (Iterator i2 = new SequenceIterator(new Iterator[] {knowledgebase.iterator(), listS.listIterator(i.previousIndex())});
				 i2.hasNext(); ) {
				Clause             F = (Clause) i2.next();
				final Signature	   FVariables = F.getFreeVariables();
				final Signature	   overlappingVariables = GVariables.intersection(FVariables);
				if (!overlappingVariables.isEmpty()) {
				    // make a variant of F such that the variables of F and G are disjunct
				    //@todo optimize would it be quicker if we always build variants, regardless of disjunctness or not?
				    F = F.variant(overlappingVariables);
				}

				// try to resolve G with F (to L)
                        	for (Iterator resolvents = F.resolveWith(G); resolvents.hasNext(); ) {
				    resolvable = true;
				    Clause R = (Clause)resolvents.next();
				    // goal lookahead
				    if (R.equals(Clause.CONTRADICTION)) {
					logger.log(Level.FINE, "resolved contradiction {0} from {1} and {2}",  new Object[] {R, F, G});
					// construct a special clause, that only contains the contradiction (in order to simplify isSolution)
					resumedReturn(new Proof(ClausalSet.CONTRADICTION_SINGLETON_SET, R));
					// cut the search tree after resuming with {Utilities.CONTRADICTION} as clauses
					return;
				    }
				    
				    if (!S.contains(R)) {
					final ClausalSet resultingClauseSet = new ClausalSetImpl(S);
					resultingClauseSet.add(R);
					logger.log(Level.FINE, "appended resolvent {0}\n from {1}\nand  {2}\nto   {3}.\nLengths are {4} from {5} and {6} to {7} thereof.", new Object[] {R, F, G, new HashSet(S), new Integer(R.size()), new Integer(F.size()), new Integer(G.size()), new Integer(S.size())});
					resumedReturn(new Proof(resultingClauseSet, R));
				    }
				}
			    }
    
			    if (!resolvable)
				//@todo optimize couldn't we somehow know the index from our list iterator i for removing G by i.remove()?
				//@internal note that our successors currently have a (modified) copy of S, anyway, so perhaps we could simplify much?
				S.remove(G);
                	}
		    }
    		}.apply();
    	}

	public Iterator states(Object action, Object state) {
	    // since A=S
	    return Collections.singletonList(action).iterator();
	}

	public TransitionModel.Transition transition(Object action, Object state, Object statep) {
	    return new Transition(action, 1);
	}
    }


    /**
     * The state during a proof (i.e. a set of formulas forming the current set of support).
     * @stereotype Structure
     * @author Andr&eacute; Platzer
     */
    private static class Proof {
	/**
	 * the current set of support.
	 * (containing all formulas already deduced, or in initial set of support)
	 */
	ClausalSet setOfSupport;

	/**
	 * the current resolvent resolved.
	 */
	Clause resolvent;

	Object accumulatedCost;

	public Proof(ClausalSet setOfSupport, Clause resolvent) {
	    this.setOfSupport = setOfSupport;
	    this.resolvent = resolvent;
	}
	public Proof(ClausalSet setOfSupport, Clause resolvent, Object accumulatedCost) {
	    this(setOfSupport, resolvent);
	    this.accumulatedCost = accumulatedCost;
	}
    }	    


    /**
     * Transforms into clausal form.
     * <p>
     * Defined per structural induction.
     * </p>
     * @param simplifying Whether or not to use simplified CNF for calculating clausal forms.
     * @todo assert
     * @todo move to orbital.moon.logic.resolution....?
     */
    public static final ClausalSet clausalForm(Formula f, boolean simplifying) {
	return new ClausalSetImpl
	    (
	     Functionals.map(new Function() {
		     public Object apply(Object C) {
			 return new ClauseImpl((Set)C);
		     }
		 }, ClassicalLogic.Utilities.clausalForm(f, simplifying))
	     );
    }
}
