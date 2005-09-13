/**
 * @(#)MathFunctor_CompositeFunctor.java 1.0 2000/08/05 Andre Platzer
 * 
 * Copyright (c) 2000-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.logic.functor.Functor;
import orbital.logic.sign.concrete.Notation;

import orbital.moon.math.functional.AbstractFunctor;
import orbital.util.Utility;

        /**
         * A base class for all mathematical functors that are composed of other functors.
         * <p>
         * This class is in fact a workaround for multiple inheritance of
         * {@link orbital.logic.functor.AbstractCompositeFunctor} and {@link MathFunctor.AbstractFunctor}.
         * </p>
         * 
         * @structure inherits orbital.logic.functor.Functor.Composite.Abstract
         * @structure inherits MathFunctor.AbstractFunctor
         * @version $Id$
         * @author  Andr&eacute; Platzer
         */
        abstract class MathFunctor_CompositeFunctor extends AbstractFunctor implements MathFunctor.Composite {
                // identical to @see orbital.logic.functor.AbstractCompositeFunctor
                /**
                 * the current notation used for displaying this composite functor.
                 * @serial
                 */
                private Notation notation;
                protected MathFunctor_CompositeFunctor(Notation notation) {
                        setNotation(notation);
                }
                protected MathFunctor_CompositeFunctor() {
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
            
                public Notation getNotation() {
                        return notation;
                }
                public void setNotation(Notation notation) {
                        this.notation = notation == null ? Notation.DEFAULT : notation;
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
                 * @return <code>{@link Notation#format(Object, Object) notation.format}((Functor)getCompositor(), getComponent())</code>.
                 */
                public String toString() {
                        return getNotation().format((Functor)getCompositor(), getComponent());
                }
        }
