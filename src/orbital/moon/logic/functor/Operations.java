/**
 * @(#)Operations.java 1.1 2002-08-30 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.functor;

import orbital.logic.functor.Function;
import orbital.logic.functor.BinaryFunction;

import orbital.logic.functor.Functionals;
import orbital.logic.functor.Predicates;

import orbital.util.Utility;

import orbital.logic.sign.type.*;

/**
 * Provides central logical operations for truth-value types.
 * <p>
 * Operations contains BinaryFunction abstractions of
 * mathematical operations like <code>&and; &or; &not; &rArr; &hArr;</code> etc.
 * <!-- For truth-values, the corresponding elemental function is performed,
 * for functions the operations are defined pointwise.
 * So these Operations can be applied to truth-values as well as functions in the same manner! -->
 * 
 * All function objects in this class provide canonical equality:
 * <center>a.equals(b) if and only if a <span class="operator">==</span> b</center>
 * </p>
 * From now on, a set <span class="set">D</span> of designated truth-values is fixed.
 * It contains the values true and false of classical logic.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.math.functional.Functionals#compose(Function, Function)
 * @see orbital.math.functional.Functionals#genericCompose(BinaryFunction, Object, Object)
 * @todo also implement on formulas? Caveat @see ClassicalLogic
 * @todo how to select the right logic apart from classical two-valued?
 * @todo extend orbital.math.functional.Operations? or the other way around?
 */
public interface Operations {
    /**
     * Class alias object.
     */
    public static final Operations operations = new Operations() {};

    /*private*/ static final TypeSystem typeSystem = Types.getDefault();

    static final Type UNARY_LOGICAL_JUNCTOR = typeSystem.predicate(Types.TRUTH);
    static final Type BINARY_LOGICAL_JUNCTOR= typeSystem.predicate(typeSystem.product(new Type[] {Types.TRUTH, Types.TRUTH}));
    static final Type FOLD_LOGICAL_JUNCTOR  = typeSystem.predicate(typeSystem.list(Types.TRUTH));

    // (still) identical to @see orbital.moon.logic.ClassicalLogic.LogicFunctions.not...

    /**
     * Negation not &not;:<span class="set">D</span><sub>t</sub>&rarr;<span class="set">D</span><sub>t</sub>; x &#8614; &not;x.
     */
    // Basic logical operations (elemental junctors).
    public static final Function not = new Function() {
            private final Type logicalTypeDeclaration = UNARY_LOGICAL_JUNCTOR;
            public Object apply(Object a) {
                return PackageUtilities.toTruth(!PackageUtilities.getTruth(a));
            }
            public String toString() { return "~"; }
        }; 


    /**
     * Conjunction and &and;:<span class="set">D</span><sub>t</sub>&times;<span class="set">D</span><sub>t</sub>&rarr;<span class="set">D</span><sub>t</sub>; (x,y) &#8614; x&and;y.
     * @xxx this implementation only works for Boolean, neither for other truth-values nor even for formulas or functions.
     */
    public static final BinaryFunction/*<Boolean,Boolean, Boolean>*/ and = new BinaryFunction() {
            private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
            public Object apply(Object a, Object b) {
                return PackageUtilities.toTruth(PackageUtilities.getTruth(a) && PackageUtilities.getTruth(b));
            }
            public String toString() { return "&"; }
        };


    /**
     * n-ary and &#8896;: <span class="set">D</span><sub>t</sub><sup>n</sup>&rarr;<span class="set">D</span><sub>t</sub>; (x<sub>i</sub>) &#8614; &#8896;<sub>i</sub> x<sub>i</sub> = <span class="bananaBracket">(|</span>true,&and;<span class="bananaBracket">|)</span> (x<sub>i</sub>).
     * <p>
     * Treats its argument as a list like {@link orbital.logic.functor.Functionals.Catamorphism}.
     * </p>
     * @see orbital.logic.functor.Functionals.Catamorphism
     * @see Predicates#TRUE
     * @see #and
     * @todo keep/improve/refuse/rename
     * @xxx Caveat: unlike binary, n-ary and is not yet fully supported w.r.t. trs during theorem proving!
     */
    public static final Function/*<List<Boolean>, Boolean>*/ andFold = new Function() {
            private final Type logicalTypeDeclaration = FOLD_LOGICAL_JUNCTOR;
            public Object apply(Object a) {
                return Functionals.foldLeft(and, PackageUtilities.toTruth(true), Utility.asIterator(a));
            }
            public String toString() { return "\u22C0"; }
        };
        
    //@todo document
    public static final BinaryFunction or = new BinaryFunction() {
            private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
            public Object apply(Object a, Object b) {
                return PackageUtilities.toTruth(PackageUtilities.getTruth(a) || PackageUtilities.getTruth(b));
            }
            public String toString() { return "|"; }
        };

    public static final Function/*<List<Boolean>, Boolean>*/ orFold = new Function() {
            private final Type logicalTypeDeclaration = FOLD_LOGICAL_JUNCTOR;
            public Object apply(Object a) {
                return Functionals.foldLeft(or, PackageUtilities.toTruth(false), Utility.asIterator(a));
            }
            public String toString() { return "\u22C1"; }
        };

    // Derived logical operations.

    //@todo The following functions for derived logical operations could be generalized (see LogicBasis)
    public static final BinaryFunction xor = new BinaryFunction() {
            private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
            public Object apply(Object a, Object b) {
                return PackageUtilities.toTruth(PackageUtilities.getTruth(a) ^ PackageUtilities.getTruth(b));
            }
            public String toString() { return "^"; }
        };

    public static final BinaryFunction impl = new BinaryFunction() {
            private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
            public Object apply(Object a, Object b) {
                return PackageUtilities.toTruth(!PackageUtilities.getTruth(a) || PackageUtilities.getTruth(b));
            }
            public String toString() { return "->"; }
        };

    //@todo rename
    public static final BinaryFunction reverseImpl = new BinaryFunction() {
            private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
            public Object apply(Object a, Object b) {
                return PackageUtilities.toTruth(PackageUtilities.getTruth(a) || !PackageUtilities.getTruth(b));
            }
            public String toString() { return "<-"; }
        };

    public static final BinaryFunction equiv = new BinaryFunction() {
            private final Type logicalTypeDeclaration = BINARY_LOGICAL_JUNCTOR;
            public Object apply(Object a, Object b) {
                return PackageUtilities.toTruth(PackageUtilities.getTruth(a) == PackageUtilities.getTruth(b));
            }
            public String toString() { return "<->"; }
        };
}
