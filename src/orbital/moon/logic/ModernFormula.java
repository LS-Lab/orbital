/**
 * @(#)ModernFormula.java 0.7 1999/01/16 Andre Platzer
 * 
 * Copyright (c) 1999-2002 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital.moon.logic;

import orbital.logic.imp.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.*;
import orbital.logic.sign.ParseException;
import orbital.logic.sign.concrete.Notation;

import orbital.logic.functor.Functor;
import orbital.logic.functor.Functor.Composite; //@todo sure? or better Expression.Composite
import orbital.logic.functor.*;

import java.util.Set;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import java.util.Collections;
import java.util.Arrays;
import orbital.util.Setops;
import orbital.util.Utility;

/**
 * The formula implementation of (usually truth-functional) modern logic.
 * @version 0.8, 1999/01/16
 * @version 0.7, 1999/01/16
 * @author  Andr&eacute; Platzer
 * @internal in fact, we currently don't rely on LogicBasis anyway.
 */
abstract class ModernFormula extends LogicBasis implements Formula {
    /**
     * The underlying logic of this formula (used for composition).
     * @note an alternative implementation would make this class an inner instance class of a Logic implementation basis, saving this instance variable.
     * @xxx then, however, default constructor newInstance() can no longer set the right underlying logic. And that logic cannot even be set directly by setComponent, setCompositor
     */
    private Logic logic = new ClassicalLogic();
    protected ModernFormula(Logic underlyingLogic) {
	this.logic = underlyingLogic;
    }

    /**
     * The symbols of the logical junctors.
     */
    private static final Symbol NOT, AND, OR, XOR, IMPL, EQUIV, FORALL, EXISTS;
    static {
	//@note assuming the symbols and notation of ClassicalLogic, here
	final Logic logic = new ClassicalLogic();
	final Signature core = logic.coreSignature();
	// we also avoid creating true formulas, it's (more or less) futile
	//@xxx we need some valid non-null arguments.
	final Formula B = (Formula) logic.createAtomic(new SymbolBase("B", SymbolBase.BOOLEAN_ATOM));
	final Formula OBJ = (Formula) logic.createAtomic(new SymbolBase("OBJ", SymbolBase.UNIVERSAL_ATOM));
	Formula[] arguments = {B};
	NOT = core.get("~", arguments);
	assert NOT != null : "operators in core signature";

	arguments = new Formula[] {B, B};
	AND = core.get("&", arguments);
	assert AND != null : "operators in core signature";
	OR = core.get("|", arguments);
	assert OR != null : "operators in core signature";
	XOR = core.get("^", arguments);
	assert XOR != null : "operators in core signature";
	IMPL = core.get("->", arguments);
	assert IMPL != null : "operators in core signature";
	EQUIV = core.get("<->", arguments);
	assert EQUIV != null : "operators in core signature";

	final Formula f = (Formula) logic.createAtomic(new SymbolBase("f", Types.getDefault().map(Types.INDIVIDUAL,Types.TRUTH)));
	arguments = new Formula[] {f};
	FORALL = core.get("°", arguments);
	assert FORALL != null : "operators in core signature";
	EXISTS = core.get("?", arguments);
	assert EXISTS != null : "operators in core signature";
    }

    /**
     * Get the underlying logic of this formula.
     */
    Logic getUnderlyingLogic() {
	return logic;
    }

    /**
     * Set the underlying logic of this formula.
     */
    private void setUnderlyingLogic(Logic underlyingLogic) {
	this.logic = underlyingLogic;
    }

    /**
     * Checks whether the underlying logics of this formula and the given one are compatible.
     * An undefined underlying logic of <code>null</code> is compatible with any logic.
     */
    boolean isCompatibleUnderlyingLogic(Formula formula) {
	Logic myUnderlying = getUnderlyingLogic();
	Logic itsUnderlying = ((ModernFormula)formula).getUnderlyingLogic();
	return myUnderlying == null || ((ModernLogic)myUnderlying).compatible(itsUnderlying);
    }

    /**
     * Set our underlying logic like the underlying logic of the given formula.
     * @preconditions getUnderlyingLogic()&ne;null &and; formula.getUnderlyingLogic()&ne;null &rArr;
     *  isCompatibleUnderlyingLogic(formula)
     */
    void setUnderlyingLogicLikeIn(Formula formula) {
	Logic myUnderlying = getUnderlyingLogic();
	Logic itsUnderlying = ((ModernFormula)formula).getUnderlyingLogic();
	assert isCompatibleUnderlyingLogic(formula) : "expected compatible underlying logics";
	setUnderlyingLogic(itsUnderlying);
    }

    
    // Formula implementation

    public Set getVariables() {
	return Setops.union(getFreeVariables(), getBoundVariables());
    }

    public Set getFreeVariables() {
    	throw new UnsupportedOperationException("not yet implemented for " + getClass());
    }

    public Set getBoundVariables() {
    	throw new UnsupportedOperationException("not yet implemented for " + getClass());
    }

    
    public Formula not() {
	return compose(NOT, new Formula[] {this});
    } 

    public Formula and(Formula B) {
	return compose(AND, new Formula[] {this, B});
    }

    public Formula or(Formula B) {
	return compose(OR, new Formula[] {this, B});
    } 

    //@todo introduce (currently derived from LogicBasis) xor, impl, equiv

    public Formula exists(Symbol x) {
	return compose(EXISTS, new Formula[] {
	    compose(ClassicalLogic.LAMBDA, new Formula[] {createSymbol(getUnderlyingLogic(), x), this})
	});
    } 
	
    public Formula forall(Symbol x) {
	return compose(FORALL, new Formula[] {
	    compose(ClassicalLogic.LAMBDA, new Formula[] {createSymbol(getUnderlyingLogic(), x), this})
	});
    } 

    // Derived logical operations. (in classical logic)
    //@internal note that we could also rely on LogicBasis, but then formatting would be unintuitive.
    /* @xxx hinders formation of conjunctiveForm in ModalLogicTest
    public Formula xor(Formula B) {
	return compose(XOR, new Formula[] {this, B});
    } 

    public Formula impl(Formula B) {
	return compose(IMPL, new Formula[] {this, B});
    } 

    public Formula equiv(Formula B) {
	return compose(EQUIV, new Formula[] {this, B});
    } 
    */
    private Formula compose(Symbol op, Formula[] arguments) {
	try {
	    return (Formula) logic.compose(logic.createAtomic(op), arguments);
	}
	catch (ParseException ex) {throw (InternalError) new InternalError("errorneous internal composition").initCause(ex);}
    }


    // base case atomic symbols

    /**
     * Construct (a formula view of) an atomic symbol.
     * @param symbol the symbol for which to create a formula representation
     * @see Logic#createAtomic(Symbol)
     */
    public static Formula createSymbol(Logic underlyingLogic, Symbol symbol) {
	return new AtomicSymbol(underlyingLogic, symbol);
    }
    /**
     * Construct (a formula view of) an atomic symbol with a fixed interpretation.
     * @param symbol the symbol for which to create a formula representation
     * @param referent the fixed interpretation of this symbol
     * @param core whether symbol is in the core such that it does not belong to the proper signature.
     * @see Logic#createAtomic(Symbol)
     */
    public static Formula createFixedSymbol(Logic underlyingLogic, Symbol symbol, Object referent, boolean core) {
	return new FixedAtomicSymbol(underlyingLogic, symbol, referent, core);
    }
    /**
     * This atomic expression formula is variable iff its symbols is.
     * The interpretation of this formula is the interpretation of the symbol.
     * @structure delegate symbol:Variable
     */
    static class AtomicSymbol extends ModernFormula implements orbital.logic.trs.Variable {
	private Symbol symbol;
	/**
	 * Construct (a formula view of) an atomic symbol.
	 * @param symbol the symbol for which to create a formula representation
	 */
	public AtomicSymbol(Logic underlyingLogic, Symbol symbol) {
	    super(underlyingLogic);
	    this.symbol = symbol;
	}
		
	public boolean equals(Object o) {
	    return getClass() == o.getClass() && Utility.equals(symbol, ((AtomicSymbol) o).symbol);
	}
	public int hashCode() {
	    return Utility.hashCode(symbol);
	}
		
        public Type getType() {
	    return symbol.getType();
        }
	public boolean isVariable() {return symbol.isVariable();}
	Symbol getSymbol() {
	    return symbol;
	}

        public Signature getSignature() {
	    return new SignatureBase(Collections.singleton(symbol));
        }
        public Set getVariables() {
	    return getFreeVariables();
        }
        public Set getFreeVariables() {
	    return isVariable()
		? Collections.singleton(symbol)
		: Collections.EMPTY_SET;
        }
        public Set getBoundVariables() {
	    return Collections.EMPTY_SET;
        }

	public Object apply(Object i) {
	    Interpretation I = (Interpretation)i;
	    if (I == null)
		throw new IllegalStateException("cannot get the truth-value of a symbol '" + symbol + "' with interpretation " + I);
            
	    // symbols
	    try {
		Object referent = I.get(symbol);
		assert validInterpretation(referent) : "check referent has legal type";
		return referent;
	    }
	    catch (NullPointerException ex) {
		throw (IllegalStateException) new IllegalStateException("interpretation of '" + symbol + "' is invalid, due to " + ex + " in " + I).initCause(ex);
	    }
	    catch (IllegalArgumentException ex) {
		throw (IllegalStateException) new IllegalStateException("interpretation of '" + symbol + "' is invalid, due to " + ex + " in " + I).initCause(ex);
	    }
	}
		
        /**
         * Get a boolean interpretation for classical logic.
         * allow more than Boolean, but assert that the types of symbol and desc fit.
	 * @todo document update this old documentation
         */ 
    	private final boolean validInterpretation(Object desc) {
	    if (desc == null)
		throw new NullPointerException(desc + " is not a valid interpretation");
	    else if (symbol.getType().apply(desc))
		return true;
	    else
		throw new TypeException("incompatible interpretation " + desc + " of " + desc.getClass() + " for " + symbol.getType(), symbol.getType(), Types.typeOf(desc));
    	}

	public String toString() { return symbol + ""; }
    }
    //@todo should we also implement VoidFunction?
    static class FixedAtomicSymbol extends AtomicSymbol {
	private Object referent;
	private boolean core;
	/**
	 * Construct (a formula view of) an atomic symbol with a fixed interpretation.
	 * @param symbol the symbol for which to create a formula representation
	 * @param referent the fixed interpretation of this symbol
	 * @param core whether symbol is in the core such that it does not belong to the proper signature.
	 */
	public FixedAtomicSymbol(Logic underlyingLogic, Symbol symbol, Object referent, boolean core) {
	    super(underlyingLogic, symbol);
	    if (symbol.isVariable())
		throw new IllegalArgumentException("do not use fixed referents for variable symbols");
	    if (!symbol.getType().apply(referent))
		throw new TypeException("incompatible interpretation " + referent + " of " + referent.getClass() + " for " + symbol.getType(), symbol.getType(), Types.typeOf(referent));
	    this.referent= referent;
	    this.core = core;
	}
	public boolean equals(Object o) {
	    return getClass() == o.getClass() && Utility.equals(referent, ((FixedAtomicSymbol) o).referent);
	}
	public int hashCode() {
	    return Utility.hashCode(referent);
	}
	Object getReferent() {
	    return referent;
	}
        public Signature getSignature() {
	    return core ? SignatureBase.EMPTY : super.getSignature();
	}
	public Object apply(Object I) {
	    return referent;
	}
    }

    // composition
    
    /**
     * Delayed composition of a symbol with some arguments.
     * Usually for user-defined predicates etc. or predicates subject to interpretation.
     * @param f the compositing formula.
     * @param arguments the arguments to the composition by f.
     * @param notation the notation for the composition (usually determined by the composing symbol).
     */
    public static Formula.Composite composeDelayed(Logic underlyingLogic, Formula f, Expression arguments[], Notation notation) {
        //@xxx was notat = notation; but either we disable DEFAULT=BESTFIX formatting, or we ignore the signature's notation choice
        Notation notat = notation;
	switch(arguments.length) {
	case 0:
	    return new ModernFormula.VoidAppliedVariableFormula(underlyingLogic, f, notation);
	case 1:
	    return new ModernFormula.AppliedVariableFormula(underlyingLogic, f, (Formula) arguments[0], notat);
	case 2:
	    return new ModernFormula.BinaryAppliedVariableFormula(underlyingLogic, f, (Formula) arguments[0], (Formula) arguments[1], notat);
	default:
	    //@xxx provide implementation on Object[]
	    // could simply compose f(arguments), here, if f understands arrays
	    return new ModernFormula.NaryAppliedVariableFormula(underlyingLogic, f, (Formula[]) Arrays.asList(arguments).toArray(new Formula[0]), notat);
	    //@todo which Locator to provide, here?
	    //throw new IllegalArgumentException("illegal number of arguments, " + f + " applied to " + Types.typeOf(arguments) + " is undefined. Or " + arguments.length + ">2");
	}
    }

    /**
     * Instant composition of functors with a fixed core interperation
     * Usually for predicates etc. subject to fixed core interpretation.
     * @param f the compositing formula.
     * @param arguments the arguments to the composition by f.
     * @param fsymbol the symbol with with the fixed interpretation f.
     */
    public static Formula.Composite composeFixed(Logic underlyingLogic, Symbol fsymbol, Functor f, Expression arguments[]) {
        //@xxx was notat = fsymbol.getNotation().getNotation(); but either we disable DEFAULT=BESTFIX formatting, or we ignore the signature's notation choice
        Notation notat = fsymbol.getNotation().getNotation();
	switch(arguments.length) {
	case 0:
	    if (f instanceof VoidPredicate && !(f instanceof VoidFunction))
		f = Functionals.asFunction((VoidPredicate) f);
	    return new ModernFormula.AppliedFormula(underlyingLogic, fsymbol, Functionals.onVoid((VoidFunction) f), (Formula) arguments[0], fsymbol.getNotation().getNotation());
	case 1:
	    if (f instanceof Predicate && !(f instanceof Function))
		f = Functionals.asFunction((Predicate) f);
	    assert f instanceof Function : f + " of " + f.getClass() + " instanceof " + Function.class;
	    assert arguments[0] instanceof Formula : arguments[0] + " of " + arguments[0].getClass() + " instanceof " + Formula.class;
	    return new ModernFormula.AppliedFormula(underlyingLogic, fsymbol, (Function) f, (Formula) arguments[0], notat);
	case 2:
	    if (f instanceof BinaryPredicate && !(f instanceof BinaryFunction))
		f = Functionals.asFunction((BinaryPredicate) f);
	    return new ModernFormula.BinaryAppliedFormula(underlyingLogic, fsymbol, (BinaryFunction) f, (Formula) arguments[0], (Formula) arguments[1], notat);
	default:
	    // could simply compose f(arguments), here, if f understands arrays
	    //@todo which Locator to provide, here?
	    throw new IllegalArgumentException("illegal number of arguments, " + f + " applied to " + Types.typeOf(arguments) + " is undefined. Or " + arguments.length + ">2");
	}
    }

    
    /**
     * Encapsulates the common implementation part of composite formulas.
     * @author  Andr&eacute; Platzer
     * @version 1.1, 2002-11-10
     */
    static abstract class AbstractCompositeFormula extends ModernFormula implements Composite {
	protected AbstractCompositeFormula(Logic underlyingLogic, Notation notation) {
	    super(underlyingLogic);
	    setNotation(notation);
	}
	protected AbstractCompositeFormula(Logic underlyingLogic) {
	    super(underlyingLogic);
	    this.notation = Notation.DEFAULT;
	}
	// for modification cloning
	protected AbstractCompositeFormula() {
	    this(null);
	}

    	// identical to @see orbital.logic.functor.Functor.Composite.Abstract

	/**
	 * the current notation used for displaying this composite functor.
	 * @serial
	 */
	private Notation notation;
	public Notation getNotation() {
	    return notation;
	}
	public void setNotation(Notation notation) {
	    this.notation = notation == null ? Notation.DEFAULT : notation;
	}
		
	public orbital.logic.Composite construct(Object f, Object g) {
	    try {
		//@internal our sub classes' nullary constructors must be accessible to us, for newInstance() to work
		orbital.logic.Composite c = (orbital.logic.Composite) getClass().newInstance();
		c.setCompositor(f);
		c.setComponent(g);
		return c;
	    }
	    catch (InstantiationException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite construct(Object,Object)").initCause(ass);
	    }
	    catch (IllegalAccessException ass) {
		throw (UnsupportedOperationException) new UnsupportedOperationException("invariant: sub classes of " + Functor.Composite.class + " must either support nullary constructor for modification cloning or overwrite construct(Object,Object)").initCause(ass);
	    }
	}

	/**
	 * Checks for equality.
	 * Two CompositeFunctors are equal iff their classes,
	 * their compositors and their components are equal.
	 */
	public boolean equals(Object o) {
	    if (o == null || getClass() != o.getClass())
		return false;
	    // note that it does not matter to which .Composite we cast since we have already checked for class equality
	    Expression.Composite b = (Expression.Composite) o;
	    return Utility.equals(getCompositor(), b.getCompositor())
		&& Utility.equalsAll(getComponent(), b.getComponent());
	}

	public int hashCode() {
	    return Utility.hashCode(getCompositor()) ^ Utility.hashCodeAll(getComponent());
	}

	/**
	 * Get a string representation of the composite functor.
	 * @return <code>{@link Notation#format(Object, Object) notation.format}(getCompositor(), getComponent())</code>.
	 */
	public String toString() {
	    return getNotation().format((Functor)getCompositor(), getComponent());
	}
    }


    // alternative implementation 1 (delayed: variable outer functions defined by formulas)
	
    /**
     * <p>
     * This class is in fact a workaround for multiple inheritance of
     * {@link ModernFormula}, {@link orbital.logic.functor.Functor.Composite.Abstract}
     * and some parts of {@link orbital.logic.functor.Compositions.CompositeFunction}.</p>
     * 
     * @structure inherits ModernFormula
     * @structure inherits Compositions.CompositeFunction
     * @todo change type of outer to Formula, and use ConstantFormulas for coreInterpretation instead
     * @see AppliedFormula
     * @internal apply is the S combinator
     */
    static class AppliedVariableFormula extends AbstractCompositeFormula {
	protected Formula outer;
	protected Formula inner;
	public AppliedVariableFormula(Logic underlyingLogic, Formula f, Formula g, Notation notation) {
	    super(underlyingLogic, notation);
	    this.outer = f;
	    this.inner = g;
	}
	public AppliedVariableFormula(Logic underlyingLogic, Formula f, Formula g) {
	    this(underlyingLogic, f, g, null);
	}
		
	// for modification cloning
	protected AppliedVariableFormula() {}
		
        public Type getType() {
	    return outer.getType().codomain();
        }
        public Signature getSignature() {
	    return inner.getSignature().union(outer.getSignature());
        }

	public Set getFreeVariables() {
	    return Setops.union(inner.getFreeVariables(),
				outer.getFreeVariables());
	}

	public Set getBoundVariables() {
	    return Setops.union(inner.getBoundVariables(),
				outer.getBoundVariables());
	}

	/**
	 * The functions applied are subject to interpretation.
	 */
	public Object apply(Object/*>Interpretation<*/ arg) {
	    Object f = outer.apply(arg);
	    if (f instanceof Predicate && !(f instanceof Function))
		f = Functionals.asFunction((Predicate) f);
	    return ((Function) f).apply(inner.apply(arg));
	} 
		

	// identical to @see orbital.logic.functor.Compositions.CompositeFunction (apart from Formula instead of Function)
	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    setUnderlyingLogicLikeIn((Formula) f);
	    this.outer = (Formula) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    assert isCompatibleUnderlyingLogic((Formula)g) : "only compose formulas of compatible logics";
	    this.inner = (Formula) g;
	}
    }

    /**
     * <p>
     * This class is in fact a workaround for multiple inheritance of
     * {@link ModernFormula} and {@link orbital.logic.functor.Compositions.VoidCompositeFunction}.</p>
     * 
     * @structure inherits ModernFormula
     * @structure inherits Compositions.CompositeFunction
     * @todo change type of outer to Formula, and use ConstantFormulas for coreInterpretation instead
     * @see AppliedFormula
     */
    static class VoidAppliedVariableFormula extends AbstractCompositeFormula {
	protected Formula outer;
	public VoidAppliedVariableFormula(Logic underlyingLogic, Formula f, Notation notation) {
	    super(underlyingLogic, notation);
	    this.outer = f;
	}
	public VoidAppliedVariableFormula(Logic underlyingLogic, Formula f) {
	    this(underlyingLogic, f, null);
	}
		
	// for modification cloning
	protected VoidAppliedVariableFormula() {}
		
        public Type getType() {
	    return outer.getType().codomain();
        }
        public Signature getSignature() {
	    return outer.getSignature();
        }

	public Set getFreeVariables() {
	    return outer.getFreeVariables();
	}

	public Set getBoundVariables() {
	    return outer.getBoundVariables();
	}

	/**
	 * The functions applied are subject to interpretation.
	 */
	public Object apply(Object/*>Interpretation<*/ arg) {
	    Object f = outer.apply(arg);
	    if (f instanceof VoidPredicate && !(f instanceof VoidFunction))
		f = Functionals.asFunction((VoidPredicate) f);
	    return ((VoidFunction) f).apply();
	} 
		

	// identical? to @see orbital.logic.functor.Compositions.VoidCompositeFunction (apart from Formula instead of Function)
	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return new Formula[0];
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    setUnderlyingLogicLikeIn((Formula) f);
	    this.outer = (Formula) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    // arity 0 case trickily embedded here
	    if ((g instanceof Formula[]) && ((Formula[])g).length == 0)
		return;
	    else
		throw new IllegalArgumentException("illegal component for arity 0: " + g + " of " + g.getClass());
	}

    }

    /**
     * <p>
     * This class is in fact a workaround for multiple inheritance of
     * {@link ModernFormula} and {@link orbital.logic.functor.Functionals.BinaryCompositeFunction}.</p>
     * 
     * @structure inherits ModernFormula
     * @structure inherits Functionals.BinaryCompositeFunction
     * @see BinaryAppliedFormula
     */
    static class BinaryAppliedVariableFormula extends AbstractCompositeFormula {
	protected Formula outer;
	protected Formula left;
	protected Formula right;
	public BinaryAppliedVariableFormula(Logic underlyingLogic, Formula f, Formula g, Formula h, Notation notation) {
	    super(underlyingLogic, notation);
	    this.outer = f;
	    this.left = g;
	    this.right = h;
	}
	public BinaryAppliedVariableFormula(Logic underlyingLogic, Formula f, Formula g, Formula h) {
	    this(underlyingLogic, f, g, h, null);
	}
		
	// for modification cloning
	protected BinaryAppliedVariableFormula() {}

        public Type getType() {
	    return outer.getType().codomain();
        }
        public Signature getSignature() {
	    //@todo could cache signature as well, provided left and right don't change
	    return left.getSignature().union(right.getSignature()).union(outer.getSignature());
        }

	public Set getFreeVariables() {
	    return Setops.union(
				Setops.union(left.getFreeVariables(),
					     right.getFreeVariables()),
				outer.getFreeVariables());
	}

	public Set getBoundVariables() {
	    return Setops.union(
				Setops.union(left.getBoundVariables(),
					     right.getBoundVariables()),
				outer.getBoundVariables());
	}

	/**
	 * The functions applied are subject to interpretation.
	 */
	public Object apply(Object/*>Interpretation<*/ arg) {
	    Object f = outer.apply(arg);
	    if (f instanceof BinaryPredicate && !(f instanceof BinaryFunction))
		f = Functionals.asFunction((BinaryPredicate) f);
	    return ((BinaryFunction) f).apply(left.apply(arg), right.apply(arg));
	} 
		

	// identical to @see orbital.logic.functor.Functionals.BinaryCompositeFunction
	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return new Formula[] {
		left, right
	    };
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    setUnderlyingLogicLikeIn((Formula) f);
	    this.outer = (Formula) f;
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    Formula[] a = (Formula[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(Formula.class + "[2] expected");
	    assert isCompatibleUnderlyingLogic(a[0]) && isCompatibleUnderlyingLogic(a[1]) : "only compose formulas of compatible logics";
	    this.left = a[0];
	    this.right = a[1];
	}

    }

    /**
     * n-ary
     * @version 1.1, 2002-11-25
     * @author  Andr&eacute; Platzer
     * @see orbital.math.functional.ComponentCompositions.ComponentCompositeFunction
     */
    static class NaryAppliedVariableFormula extends AbstractCompositeFormula {
	protected Formula outer;
	protected Formula[] inner;
	public NaryAppliedVariableFormula(Logic underlyingLogic, Formula f, Formula g[], Notation notation) {
	    super(underlyingLogic, notation);
	    this.outer = f;
	    this.inner = g;
	}
	public NaryAppliedVariableFormula(Logic underlyingLogic, Formula f, Formula g[]) {
	    this(underlyingLogic, f, g, null);
	}
		
	// for modification cloning
	protected NaryAppliedVariableFormula() {}

        public Type getType() {
	    return outer.getType().codomain();
        }

	//@todo could move to super class of Nary and Binary and formulate in terms of getComponent()
        public Signature getSignature() {
	    //@todo could cache signature as well, provided left and right don't change
	    //@internal @see Setops.all
	    Signature sigma = outer.getSignature();
	    for (int i = 0; i < inner.length; i++)
		sigma = sigma.union(inner[i].getSignature());
	    return sigma;
        }

	public Set getFreeVariables() {
	    //@internal @see Setops.all
	    Set s = outer.getFreeVariables();
	    for (int i = 0; i < inner.length; i++)
		s = Setops.union(s, inner[i].getFreeVariables());
	    return s;
	}

	public Set getBoundVariables() {
	    //@internal @see Setops.all
	    Set s = outer.getBoundVariables();
	    for (int i = 0; i < inner.length; i++)
		s = Setops.union(s, inner[i].getBoundVariables());
	    return s;
	}

	/**
	 * The functions applied are subject to interpretation.
	 */
	public Object apply(Object/*>Interpretation<*/ arg) {
	    Object f = outer.apply(arg);
	    //@internal @see Setops.all
	    Object[] x = new Object[inner.length];
	    for (int i = 0; i < x.length; i++)
		x[i] = inner[i].apply(arg);
	    return ((Function) f).apply(x);
	} 
		

	// not quite identical to @see orbital.math.functional.ComponentCompositions.ComponentCompositeFunction
	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    setUnderlyingLogicLikeIn((Formula) f);
	    this.outer = (Formula) f;
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    Formula[] a = (Formula[]) g;
	    assert isCompatibleUnderlyingLogic(a) : "only compose formulas of compatible logics";
	    this.inner = a;
	}

	private final boolean isCompatibleUnderlyingLogic(Formula[] a) {
	    //@internal @see Setops.all
	    for (int i = 0; i < a.length; i++)
		if (!isCompatibleUnderlyingLogic(a[i]))
		    return false;
	    return true;
	}
    }

    

    // alternative implementation 2 (instant: fixed outer functions)
	
    /**
     * <p>
     * This class is in fact a workaround for multiple inheritance of
     * {@link ModernFormula} and {@link orbital.logic.functor.Compositions.CompositeFunction}.</p>
     * 
     * @structure inherits ModernFormula
     * @structure inherits Compositions.CompositeFunction
     * @todo change type of outer to Formula, and use ConstantFormulas for coreInterpretation instead
     */
    static class AppliedFormula extends AbstractCompositeFormula {
	/**
	 * The symbol of the fixed interpretation outer.
	 */
	protected Symbol outerSymbol;
	protected Function outer;
	protected Formula inner;
	public AppliedFormula(Logic underlyingLogic, Symbol fsymbol, Function f, Formula g, Notation notation) {
	    super(underlyingLogic, notation);
	    if (fsymbol == null)
		throw new IllegalArgumentException("illegal compositor symbol " + fsymbol + " for compositor referent " + f + " applied to " + g);
	    this.outerSymbol = fsymbol;
	    this.outer = f;
	    this.inner = g;
	}
	public AppliedFormula(Logic underlyingLogic, Symbol fsymbol, Function f, Formula g) {
	    this(underlyingLogic, fsymbol, f, g, null);
	}
		
	// for modification cloning
	protected AppliedFormula() {}
		
	public orbital.logic.Composite construct(Object f, Object g) {
	    AppliedFormula c = (AppliedFormula) super.construct(f, g);
	    c.outerSymbol = outerSymbol;
	    return c;
	}

        public Type getType() {
	    assert outerSymbol != null && outerSymbol.getType() != null : outerSymbol + " != null && " + (outerSymbol == null ? null : outerSymbol.getType()) + " != null\ncompositor symbol " + outerSymbol + " for compositor referent " + outer + " applied to " + inner;
	    return outerSymbol.getType().codomain();
        }
        public Signature getSignature() {
	    //@xxx shouldn't we unite with getCompositor().getSignature() in case of formulas representing predicate or function?
	    return ((Formula) getComponent()).getSignature();
        }

	public Set getFreeVariables() {
	    return inner.getFreeVariables();
	}

	public Set getBoundVariables() {
	    return inner.getBoundVariables();
	}

	// identical to @see orbital.logic.functor.Compositions.CompositeFunction
	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return inner;
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (Function) f;
	}
	public void setComponent(Object g) throws ClassCastException {
	    setUnderlyingLogicLikeIn((Formula) g);
	    this.inner = (Formula) g;
	}

	public Object apply(Object/*>Interpretation<*/ arg) {
	    return outer.apply(inner.apply(arg));
	} 
		
    }

    /**
     * <p>
     * This class is in fact a workaround for multiple inheritance of
     * {@link ModernFormula} and {@link orbital.logic.functor.Functionals.BinaryCompositeFunction}.</p>
     * 
     * @structure inherits ModernFormula
     * @structure inherits Functionals.BinaryCompositeFunction
     */
    static class BinaryAppliedFormula extends AbstractCompositeFormula {
	protected Symbol outerSymbol;
	protected BinaryFunction outer;
	protected Formula left;
	protected Formula right;
	public BinaryAppliedFormula(Logic underlyingLogic, Symbol fsymbol, BinaryFunction f, Formula g, Formula h, Notation notation) {
	    super(underlyingLogic, notation);
	    if (fsymbol == null)
		throw new IllegalArgumentException("illegal compositor symbol " + fsymbol + " for compositor referent " + f + " applied to " + g + " and " + h);
	    this.outerSymbol = fsymbol;
	    this.outer = f;
	    this.left = g;
	    this.right = h;
	}
	public BinaryAppliedFormula(Logic underlyingLogic, Symbol fsymbol, BinaryFunction f, Formula g, Formula h) {
	    this(underlyingLogic, fsymbol, f, g, h, null);
	}
		
	// for modification cloning
	protected BinaryAppliedFormula() {}

	public orbital.logic.Composite construct(Object f, Object g) {
	    BinaryAppliedFormula c = (BinaryAppliedFormula) super.construct(f, g);
	    c.outerSymbol = outerSymbol;
	    return c;
	}


        public Type getType() {
	    assert outerSymbol != null && outerSymbol.getType() != null : "outer symbol " + outerSymbol + " != null && its type " + (outerSymbol == null ? null : outerSymbol.getType()) + " != null\ncompositor symbol " + outerSymbol + " for compositor referent " + outer + " applied to " + left + " and " + right;
	    return outerSymbol.getType().codomain();
        }
        public Signature getSignature() {
	    //@todo could cache signature as well, provided left and right don't change
	    return left.getSignature().union(right.getSignature());
        }

	public Set getFreeVariables() {
	    return Setops.union(left.getFreeVariables(),
				right.getFreeVariables());
	}

	public Set getBoundVariables() {
	    return Setops.union(left.getBoundVariables(),
				right.getBoundVariables());
	}

	// identical to @see orbital.logic.functor.Functionals.BinaryCompositeFunction
	public Object getCompositor() {
	    return outer;
	} 
	public Object getComponent() {
	    return new Formula[] {
		left, right
	    };
	} 

	public void setCompositor(Object f) throws ClassCastException {
	    this.outer = (BinaryFunction) f;
	    //@xxx what to use as outerSymbol?
	}
	public void setComponent(Object g) throws IllegalArgumentException, ClassCastException {
	    Formula[] a = (Formula[]) g;
	    if (a.length != 2)
		throw new IllegalArgumentException(Formula.class + "[2] expected");
	    assert ((ModernFormula)a[0]).isCompatibleUnderlyingLogic(a[1]) : "only compose formulas of compatible logics";
	    setUnderlyingLogicLikeIn(a[0]);
	    this.left = a[0];
	    this.right = a[1];
	}

	public Object apply(Object/*>Interpretation<*/ arg) {
	    return outer.apply(left.apply(arg), right.apply(arg));
	} 
		
    }

}



// /**
//  * Tags expressions (and functions) that introduces bindings.
//  * @see ClassicalLogic.LambdaAbstraction already solve this, n'est-ce pas?
//  */
// interface BindingExpression {
//     /**
//      * Get the symbol bound by this binding expression.
//      * @todo generalize to a set of symbols bound alltogether?
//      * @note however for BindingExpression functions cannot guess their left expression symbol with which they are composed in a AppliedFormula.
//      */
//     Symbol binding();
// }
