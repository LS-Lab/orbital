/**
 * @(#)ResolutionBase.java 1.1 2003-11-05 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.resolution;
import orbital.moon.logic.ClassicalLogic;
import orbital.moon.logic.ClassicalLogic.Utilities;

import orbital.logic.imp.*;
import orbital.logic.sign.*;

import orbital.logic.functor.*;

import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Basic skeleton for resolution theorem provers.
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
 *     try to resolve from E (with building variants to achieve disjunct variables) the empty clause &#9633;.
 *     Use a refinement or stronger variant of the following nondeterministic procedure:
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
 * @version 0.8, 2003-11-05
 * @author  Andr&eacute; Platzer
 * @todo Skolem-Normalfrom mit KNF-Matrix (mit Erweiterung der Signatur)
 * @todo schlaues pränex-Normalform Bilden, um einfacher skolemisieren zu können. Minimiere die Anzahl der Allquantoren vor den Existenzquantoren.
 *  etwa &exist;x&forall;y P &and; &exist;z&forall;w Q  == &exist;x&exist;z &forall;y&forall;w P&and;Q statt == &exist;x&forall;y&exist;z&forall;w P&and;Q denn im ersteren Fall skolemisiert x zu a und z zu b, im zweiteren aber x zu a und z zu f(y).
 *  Oder wähle alternativen (einfacheren?) TRS-Algorithmus Ü 7.95
 */
public abstract class ResolutionBase implements Inference {
    /**
     * Whether or not to use simplified clausal forms.
     */
    private static final boolean SIMPLIFYING = false;

    static final Logger logger = Logger.getLogger(ResolutionBase.class.getName());

    private static final ClausalFactory clausalFactory = new DefaultClausalFactory();
    protected static ClausalFactory getClausalFactory() {
	return clausalFactory;
    }
    
    /**
     * Add verbosity, i.e. print out a proof tree.
     */
    public void setVerbose(boolean newVerbose) {
	if (getClausalFactory() instanceof DefaultClausalFactory) {
	    ((DefaultClausalFactory)getClausalFactory()).setVerbose(newVerbose);
	}
    }

    /**
     * {@inheritDoc}
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     */
    public boolean infer(final Formula[] B, final Formula D) {
        final ClausalSet knowledgebase = skolemClauseForm(Arrays.asList(B), "knowledgebase ");
	// if IllegalStateException occurs here it means ("premises are inconsistent since they already contain a contradiction, so ex falso quodlibet");

        // negate query since we are a negative test calculus
        final Formula query = D.not();
	final ClausalSet S = skolemClauseForm(Collections.singleton(query), "negated goal ");
	// if IllegalStateException occurs here it means return true due to inconsistent query

	logger.log(Level.FINE, "proving that knowledgebase {0} and query {1} are inconsistent", new Object[] {knowledgebase, S});
        final boolean proven = prove(knowledgebase, S);
	logger.log(Level.FINE, "found proof {0}", Boolean.valueOf(proven));
        return proven;
    }
	
    public boolean isSound() {
	return true;
    }

    public boolean isComplete() {
	// assuming knowledge base W is consistent, we are refutation-complete
	return true;
    }

    /**
     * Try to prove or disprove the conjecture.
     * @param knowledgebase knowledge base W assumed consistent.  W is
     * kept in clausal normal form, and thus contains sets of
     * literals.
     * @param query the query &lnot;D, i.e. negated goal D forming the
     * initial set of support.
     * @return whether W &not; &lnot;D is inconsistent, i.e. W <span
     * class="inference">|~</span> D holds.
     */
    protected abstract boolean prove(ClausalSet knowledgebase, ClausalSet query);

    
    // Tools

    /**
     * Transforms a set of formulas to a set of Skolemized clauses.
     * Skolemizes, drops quantifiers, converts to clauses,
     * removes tautologies, and handles contradictions.
     * @param B the set of formulas to transform to clauses.
     * @param logPrefix the string to prepend to all logging information.
     * @throws IllegalStateException if the clauses are inconsistent
     * since they already contain a contradiction.
     */
    static ClausalSet skolemClauseForm(Collection/*_<Formula>_*/ B, String logPrefix) {
        // skolemize B and drop quantifiers
        final List/*_<Formula>_*/ skolemizedB = new ArrayList(B.size());
        for (Iterator i = B.iterator(); i.hasNext(); ) {
	    Formula f = (Formula) i.next();
	    skolemizedB.add(Utilities.dropQuantifiers(Utilities.skolemForm(f)));
	    if (logger.isLoggable(Level.FINEST))
		logger.log(Level.FINEST, "{0} skolemForm( {1} ) == {2}", new Object[] {logPrefix, f, Utilities.skolemForm(f)});
	}

        // convert B to clausalForm clausebase
	// @internal clausebase = Functionals.map(clausalForm, skolemizedB)
        ClausalSet clausebase = getClausalFactory().newClausalSet();
        for (Iterator i = skolemizedB.iterator(); i.hasNext(); ) {
	    clausebase.addAll(clausalForm((Formula) i.next(), SIMPLIFYING));
	}
        logger.log(Level.FINER, "{0} as clausal {1}", new Object[] {logPrefix, clausebase});

	// remove tautologies and handle contradictions
    	// for all clauses F&isin;clausebase
    	for (Iterator i = clausebase.iterator(); i.hasNext(); ) {
	    final Clause F = (Clause) i.next();
	    if (F.equals(Clause.CONTRADICTION))
		throw new IllegalStateException("clauses are inconsistent since they already contain a contradiction");
	    else if (F.isElementaryValid())
		// if F is obviously valid, forget about it for resolving a contradiction
		i.remove();
	}

        logger.log(Level.FINE, "{0} finally is {1}", new Object[] {logPrefix, clausebase});
        if (logger.isLoggable(Level.FINEST)) {
	    for (Iterator i = B.iterator(); i.hasNext(); ) {
		logger.log(Level.FINEST, "{0} thereby contains transformation of original formula {1}", new Object[] {logPrefix, Utilities.conjunctiveForm((Formula) i.next(), SIMPLIFYING)});
	    }
	}
	return clausebase;
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
	return getClausalFactory().createClausalSet
	    (
	     Functionals.map(new Function() {
		     public Object apply(Object C) {
			 return getClausalFactory().createClause((Set)C);
		     }
		 }, ClassicalLogic.Utilities.clausalForm(f, simplifying))
	     );
    }
}
