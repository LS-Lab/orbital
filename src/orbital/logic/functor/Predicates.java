/**
 * @(#)Predicates.java 0.7 2001/05/25 Andre Platzer
 * 
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;


import orbital.util.Utility;
import orbital.logic.sign.concrete.Notation;
import orbital.logic.sign.concrete.Notation.NotationSpecification;

/**
 * Predicate Implementations.
 * 
 * @stereotype Module
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Functionals
 */
public final class Predicates {

    /**
     * Class alias object.
     */
    public static final Predicates predicates = new Predicates();

    /**
     * prevent instantiation - module class
     */
    private Predicates() {}


    // void predicates
        
    /**
     * A constant predicate.
     * <p>
     * constant &acirc;: {()}&rarr;Boole; () &#8614; a.</p>
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    private static final class ConstantPredicate implements VoidPredicate {
        private boolean a;
        ConstantPredicate(boolean a) {
            this.a = a;
        }
        
        public boolean getConstant() {
            return a;
        }
        
        public boolean apply() {
            return a;
        } 
        // canonical equality
        public final boolean equals(Object o) {
            return super.equals(o);
        }
        public final int hashCode() {
            return super.hashCode();
        }
        public String toString() {
            return a + "";
        } 
    }

    /**
     * true = &#8868;.
     */
    public static final VoidPredicate TRUE = new ConstantPredicate(true);

    /**
     * false = &perp;.
     */
    public static final VoidPredicate FALSE = new ConstantPredicate(false);
    

    // binary predicates
        
    /**
     * =.
     * In first-order logic, equality "=" is uniquely determined by
     * <ul>
     *   <li>reflexive, i.e. &forall;x (x=x)</li>
     *   <li>substitutive, &forall;&phi;&isin;Formula(&Sigma;) a=b,&phi; &#8872; &phi;[a&rarr;b]</li>
     * </ul>
     * @attribute equivalent
     * @attribute congruent for all f, P
     * @attribute substitutive
     */
    public static final BinaryPredicate/*<Object,Object>*/ equal = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                return Utility.equals(a, b);
            }
            public String toString() { return "="; }
        };

    /**
     * &ne;.
     * <p>
     * Inequality is defined as x&ne;y :&hArr; &not;(x=y).
     * </p>
     * @attribute irreflexive
     * @attribute symmetric
     * @see #equal
     */
    public static final BinaryPredicate/*<Object,Object>*/ unequal = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                return !Utility.equals(a, b);
            }
            public String toString() { return "!="; }
        };

    /**
     * &lt;.
     * <p>
     * It is true that x&lt;y &hArr; x&le;y &and; x&ne;y.
     * </p>
     * @attribute strict order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ less = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                return ((Comparable) a).compareTo(b) < 0;
            }
            public String toString() { return "<"; }
        };

    /**
     * &gt;.
     * <p>
     * It is defined as x&gt;y :&hArr; y&lt;x.
     * </p>
     * @attribute strict order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ greater = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                return ((Comparable) a).compareTo(b) > 0;
            }
            public String toString() { return ">"; }
        };

    /**
     * &le;.
     * <p>
     * It is true that x&le;y &hArr; x&lt;y &or; x&lt;y.
     * </p>
     * @attribute order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ lessEqual = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                return ((Comparable) a).compareTo(b) <= 0;
            }
            public String toString() { return "=<"; }
        };

    /**
     * &ge;.
     * <p>
     * It is defined as x&ge;y :&hArr; y&le;x.
     * </p>
     * @attribute order
     * @see java.lang.Comparable
     */
    public static final BinaryPredicate/*<Object,Object>*/ greaterEqual = new BinaryPredicate/*<Object,Object>*/() {
            public boolean apply(Object a, Object b) {
                return ((Comparable) a).compareTo(b) >= 0;
            }
            public String toString() { return ">="; }
        };

    //@internal must be down here such that static initialization of Predicates.equal != null has already happened
    static {
        Notation.setAllNotations(new Object[][] {
            {Predicates.equal,                                          // "=="
             new NotationSpecification(700, "xfx", Notation.INFIX)},
            {Predicates.unequal,                                        // "!="
             new NotationSpecification(700, "xfx", Notation.INFIX)},
            {Predicates.greater,                                        // ">"
             new NotationSpecification(700, "xfx", Notation.INFIX)},
            {Predicates.less,                                           // "<"
             new NotationSpecification(700, "xfx", Notation.INFIX)},
            {Predicates.greaterEqual,                                   // ">="
             new NotationSpecification(700, "xfx", Notation.INFIX)},
            {Predicates.lessEqual,                                      // "=<"
             new NotationSpecification(700, "xfx", Notation.INFIX)}
        });
    }
}
