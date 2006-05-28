/**
 * @(#)CoordinateCompositeFunction..java 1.0 1999/03/16 Andre Platzer
 *
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math.functional;

import orbital.math.functional.Function;
import orbital.math.Arithmetic;
import orbital.logic.sign.concrete.Notation;

import orbital.logic.functor.Functor;
import orbital.logic.Composite;

import orbital.math.Vector;
import orbital.math.Scalar;
import orbital.math.Values;

import orbital.util.Utility;

        /**
         * A CoordinateCompositeFunction is a vectorial Function defined component-wise with unary component-functions.
         * <p>
         * Map(A,B)<sup>n</sup>&rarr;Map(A<sup>n</sup>,B<sup>n</sup>); x&#8614;f(x) = <big>(</big>f<sub>1</sub>(x<sub>1</sub>),...,f<sub>n</sub>(x<sub>n</sub>)<big>)</big><sup>T</sup>.
         * </p>
         * <p>
         * For convenience, it expands a scalar argument to a constant vector
         * so it can be applied on scalars as well.</p>
         * <p>
         * A coordinate composite function equals
         * <div>ComponentCompositeFunction(Functionals.compose(f<sub>1</sub>,Functions.projection(1)),... Functionals.compose(f<sub>n</sub>,Functions.projection(n)))</div>
         * but is faster and less to type.
         * </p>
         * <p>
         * <i><b>Evolves:</b></i> will very probably made package-level protected and accessible by a
         * facade method.</p>
         * 
         * @structure composite componentFunction:Function[] unidirectional
         * @version $Id$
         * @author  Andr&eacute; Platzer
         * @todo privatize
         * @internal was extends ComponentCompositeFunction
         */
        public /*public static*/ class CoordinateCompositeFunction extends AbstractFunctor implements Function.Composite {
                protected Function componentFunction[];
                public CoordinateCompositeFunction(Function componentFunction[]) {
                        this(componentFunction, null);
                }
                public CoordinateCompositeFunction(Function componentFunction[], Notation notation) {
                        this.componentFunction = componentFunction;
                        setNotation(notation);
                }

                /**
                 * Get the dimension of the resulting vectors.
                 */
                public int dimension() {
                        return componentFunction.length;
                } 
                public Object getCompositor() {
                        return null;
                } 

                /**
                 * Get the inner component functions applied per element.
                 */
                public Object getComponent() {
                        return componentFunction;
                } 

                public void setCompositor(Object f) throws IllegalArgumentException {
                        if (f != null)
                                throw new IllegalArgumentException("cannot set compositor");
                }
                public void setComponent(Object g) throws ClassCastException {
                        this.componentFunction = (Function[]) g;
                }

                /**
                 * Get the dimension desired for the argument vectors.
                 */
                public int argumentDimension() {
                        return dimension();
                } 

                public Object apply(Object x) {
                        // convert single scalar to constant vector
                        if (!(x instanceof Vector) && x instanceof Scalar)
                                x = Values.getDefaultInstance().CONST(dimension(), (Arithmetic)x);
                        Vector xv = (Vector) x;
                        Vector r = Values.getDefaultInstance().newInstance(dimension());
                        for (int i = 0; i < r.dimension(); i++)
                                r.set(i, (Arithmetic) componentFunction[i].apply(xv.get(i)));
                        return r;
                } 

                public Function derive() {
                        //TODO:
                        throw new UnsupportedOperationException("not yet implemented");
                } 

                public Function integrate() {
                        //TODO:
                        throw new UnsupportedOperationException("not yet implemented");
                } 

                // identical to @see orbital.math.functional.MathFunctor_CompositeFunctor or perhaps orbital.logic.functor.Functor.Composite.Abstract
                /**
                 * the current notation used for displaying this composite functor.
                 * @serial
                 */
                private Notation notation;
    
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
                        Functor.Composite b = (Functor.Composite) o;
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
