/**
 * @(#)Functor.Composite.Abstract.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;
import orbital.logic.functor.Functor.Composite;

import orbital.logic.sign.concrete.Notation;
import java.io.Serializable;

import orbital.util.Utility;

/**
 * A base class for all functors that are composed of other functors.
 * <p>
 * compose: (f,g) &#8641; f &#8728; g := f(g).<br />
 * compose: (f,g<sub>1</sub>,...,g<sub>k</sub>) &#8614; f &#8728; (g<sub>1</sub>,...,g<sub>k</sub>) := f<big>(</big>g<sub>1</sub>,...,g<sub>k</sub><big>)</big><sup>T</sup>.</p>
 * 
 * @structure inherit orbital.logic.functor.Functor.Composite
 * @structure implements Serializable
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @note package-level protected to orbital.logic.functor
 * @note the design name of this class is orbital.logic.functor.Functor.Composite.Abstract and is a package-level protected inner class to Functor.Composite. This is not possible with Java, however, so we moved it here.
 */
abstract class AbstractCompositeFunctor implements Composite, Serializable {
    private static final long serialVersionUID = 4993561537088832042L;
    // identical copy under @see orbital.math.functional.MathFunctor_CompositeFunctor and orbital.moon.logic.*Formula.*CompositeFormula and also @see orbital.moon.math.functional.CoordinateCompositeFunction and @see orbital.logic.imp.Types.AbstractCompositeType
    /**
     * the current notation used for displaying this composite functor.
     * @serial
     */
    private Notation notation;
    protected AbstractCompositeFunctor(Notation notation) {
        setNotation(notation);
    }
    protected AbstractCompositeFunctor() {
        this(null);
    }
    
    public orbital.logic.Composite construct(Object f, Object g) {
        try {
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
        Composite b = (Composite) o;
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
        return getNotation().format(getCompositor(), getComponent());
    }

    /**
     * Get the notation used for displaying this composite functor.
     */
    public Notation getNotation() {
        return notation;
    }
    /**
     * Set the notation used for displaying this composite functor.
     */
    public void setNotation(Notation notation) {
        this.notation = notation == null ? Notation.DEFAULT : notation;
    }
}
