/**
 * @(#)Types.java 1.1 2002-09-08 Andre Platzer
 *
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;
import orbital.logic.sign.type.Type.Composite;

import orbital.logic.sign.Expression;
import orbital.logic.sign.SymbolBase;

import orbital.logic.trs.Variable;
import orbital.logic.functor.Functor;

import orbital.logic.sign.Symbol;

import java.beans.IntrospectionException;
import orbital.util.InnerCheckedException;
import java.lang.reflect.*;

import java.util.logging.Logger;
import java.util.logging.Level;
import orbital.moon.GetPropertyAction;

/**
 * Manager for type system and factories.
 * Manages the current {@link TypeSystem type system} of your system.
 * <p>
 * Simple applications will generally use {@link #getDefault()} to get the current
 * system default type system. While applications that require more control over
 * the particular arithmetic object implementations will get their own instance
 * of {@link TypeSystem} and configure it according to their needs.
 * </p>
 * <p>
 * <table id="SystemProperties" border="2">
 *   <caption>Properties: type system settings</caption>
 *   <tr>
 *     <th>Property Name</th>
 *     <th>Property Value</th>
 *   </tr>
 *   <tr>
 *     <td><tt>orbital.logic.sign.type.TypeSystem.default</tt></td>
 *     <td>class name of the initial default TypeSystem implementation returned by {@link Types#getDefault()}.</td>
 *   </tr>
 * </table>
 * This properties allow a different vendor's factory implementation of type systems
 * to be "plugged in".
 * </p>
 *
 * @author Andr&eacute; Platzer
 * @version $Id$
 * @see TypeSystem
 */
public final class Types {
    /**
     * @internal see javax.xml.parser.FactoryFinder#findClassLoader() for a version that - supposedly - runs under every JVM.
     */
    private static final TypeSystem instantiate(String className) {
        ClassLoader cl = TypeSystem.class.getClassLoader();
        if (cl == null)
            cl = ClassLoader.getSystemClassLoader();

        try {
            return (TypeSystem) Class.forName(className, true, cl).newInstance();
        } catch (ClassNotFoundException ex) {
            try {
                cl = Thread.currentThread().getContextClassLoader();
                if (cl == null)
                    cl = ClassLoader.getSystemClassLoader();
                return (TypeSystem) Class.forName(className, true, cl).newInstance();
            } catch (Exception ex_again) {
                throw new FactoryConfigurationError("can't instantiate TypeSystem implementation " + className, ex_again);
            }
        } catch (Exception ex) {
            throw new FactoryConfigurationError("can't instantiate TypeSystem implementation " + className, ex);
        }
    }
    
    /**
     * Default instance.
     */
    private static TypeSystem defaultTypeSystem =
        instantiate(GetPropertyAction.getProperty(TypeSystem.class.getName() + ".default",
                                          orbital.moon.logic.sign.type.StandardTypeSystem.class.getName()));

    /**
     * Get the (single) default type system instance.
     * @see <a href="{@docRoot}/Patterns/Design/Singleton.html">&quot;Singleton&quot;</a>
     * @see <a href="{@docRoot}/Patterns/Design/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @see #setDefault(TypeSystem)
     */
    public static final TypeSystem getDefault() {
        return defaultTypeSystem;
    }

    /**
     * Set the (single) default type system instance.
     * @param newTypeSystem the new default factory of this Virtual Machine.
     * @see #getDefault()
     * @permission orbital.logic.imp.TypeSystem.default write
     */
    public static final void setDefault(TypeSystem newTypeSystem) {
        if (newTypeSystem == null)
            throw new NullPointerException("Can't set default type system to " + newTypeSystem);
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(new java.util.PropertyPermission
                        ("orbital.logic.imp.TypeSystem.default", "write"));
        defaultTypeSystem = newTypeSystem;
    }


    /**
     * The type <span class="type">&iota;</span> of individuals ({@link java.lang.Object objects}).
     * @xxx remove?
     */
    public static final Type INDIVIDUAL = getDefault().objectType(Object.class);
    /**
     * The type <span class="type">&omicron;</span> = <span class="type">()</span> of truth-values.
     * @xxx for multi-valued logics this is not limited to boolean.
     * @todo what about Boolean.TYPE and Boolean.class? Should TRUTH =< INDIVIDUAL?
     * @xxx remove?
     */
    public static final Type TRUTH = getDefault().objectType(Boolean.class);

    /**
     * prevent instantiation - module class
     */
    private Types() {}

    
    // Utility methods

    /**
     * Checks whether the type specification is compatible with the given list of arguments.
     * Convenience method.
     * @preconditions true
     * @param compositorType the type of the compositor to apply to the arguments.
     * @param args the arguments to check for compatibility with this symbol.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @return whether a compositor of type compositorType is <a href="../Expression.html#freeAlgebraOfTerms">applicable</a> to the given arguments.
     *  Which means that the arguments are assignable to the required parameter types of this symbol.
     *  This especially includes whether the number of arguments matches the arity of the compositorTypes' domain.
     * @postconditions RES == (typeOf(args) &le; compositorType.domain()) == (compositorType &le; typeOf(args)<span class="type">&rarr;&#8868;</span>)
     *  == succeedes(compositorType.on(typeOf(args)))
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @see Type#on(Type)
     * @see Type#subtypeOf(Type)
     * @see orbital.logic.functor.Functor.Specification#isApplicableTo(Object[])
     * @todo introduce isComposableWith(Type,Type) or anything that uses argType.codomain() =< compositorType.domain()
     * @xxx remove convenience?
     */
    public static final boolean isApplicableTo(Type compositorType, Expression[] args) {
        try {
            compositorType.on(typeOf(args));
            return true;
        }
        catch (TypeException incompatibleTypes) {
            return false;
        }
    }

    // Stuff

    //@xxx decide acccessibility (privatize or package-level protect if possible)
    
    /**
     * Get the number of components n of a product type <span class="type">&prod;<sub>i</sub>&tau;<sub>i</sub></span> = <span class="type">&tau;<sub>1</sub>&times;&#8230;&times;&tau;<sub>n</sub></span>.
     * @todo rename
     * @todo 19 optimize this hotspot during proving
     */
    public static final int arityOf(Type type) {
        final TypeSystem typeSystem = type.typeSystem();
        return type == typeSystem.ABSURD()
            // strict
            ? Integer.MIN_VALUE
            : type.equals(typeSystem.NOTYPE())
            ? 0
            : arityOf_perhapsProduct(type);
    }
    private static final int arityOf_perhapsProduct(Type type) {
        if (type instanceof Type.Composite) {
            Type.Composite t = (Type.Composite)type;
            if (t.getCompositor() == type.typeSystem().product())
                return ((Type[]) t.getComponent()).length;
        }
        return 1;
    }
    
    /**
     * Returns the type of a list of arguments.
     * Convenience method.
     * @preconditions true
     * @param args the arguments whose (combined) type to return.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience method</a>
     * @see TypeSystem#product(Type[])
     * @see Expression#getType()
     * @todo package protect but share with orbital.moon.logic.
     */
    /*private*/public static final Type typeOf(Typed[] args) {
        if (args == null || args.length == 0)
            return getDefault().NOTYPE();
        final Type argumentTypes[] = new Type[args.length];
        for (int i = 0; i < argumentTypes.length; i++)
            argumentTypes[i] = args[i].getType();
        return getDefault().product(argumentTypes);
    }

    /**
     * Guesses the type of an object.
     * The guess may be wrong.
     * @todo package protect but share with orbital.moon.logic.
     */
    /*private*/public static final Type typeOf(Object args) {
        if (args == null)
            return getDefault().NOTYPE();
        else if (args instanceof Typed)
            return ((Typed)args).getType();
        else if (args instanceof Typed[])
            return typeOf((Typed[])args);
        else
            return null;
    }

    /**
     * Converts a functor specification to a type (guesses it from the declared type information).
     * (experimental)
     * @xxx we cannot know that ClassicalLogic & Co implement AND as a BinaryFunction, not as a BinaryPredicate<Boolean,Boolean>
     */
    private static final Type declaredTypeOf(Functor.Specification spec) {
        return getDefault().map(typeOf(spec.getParameterTypes()), getDefault().objectType(spec.getReturnType()));
    }

    /**
     * Converts a functor specification to a type (guesses it from the declared type information).
     * Also looks for additional declarations of logical type.
     * (experimental)
     * <pre>
     * <span class="keyword">static</span> <span class="keyword">final</span> <span class="Orbital">Type</span> logicalTypeDeclaration;
     * </pre>
     * @throws IntrospectionException if an exception occurs during introspection.
     * @permission Needs access to the object's class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
     * @see orbital.logic.functor.Functor.Specification#getSpecification(orbital.logic.functor.Functor)
     * @todo package protect but share with orbital.moon.logic
     * @fixme we cannot know that ClassicalLogic & Co implement AND as a BinaryFunction, not as a BinaryPredicate<Boolean,Boolean>
     */
    /*private*/public static final Type declaredTypeOf(Functor f) throws IntrospectionException {
        Type type = getTypeDeclaration(f);
        return type != null ? type : declaredTypeOf(Functor.Specification.getSpecification(f));
    }

    /**
     * Returns the type of a list of classes.
     * Legacy conversion.
     * @preconditions true
     * @param args the arguments whose (combined) type to return.
     *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
     * @see TypeSystem#product(Type[])
     * @see TypeSystem#objectType(Class)
     */
    private static final Type typeOf(Class[] args) {
        if (args == null || args.length == 0)
            return getDefault().NOTYPE();
        final Type argumentTypes[] = new Type[args.length];
        for (int i = 0; i < argumentTypes.length; i++)
            argumentTypes[i] = getDefault().objectType(args[i]);
        return getDefault().product(argumentTypes);
    }

    /**
     * Get the logical type description specified in the given object.
     * <pre>
     * <span class="keyword">static</span> <span class="keyword">final</span> <span class="Orbital">Type</span> logicalTypeDeclaration;
     * </pre>
     * or <code>null</code> if no such field exists.
     * Implementations may also consider non-static fields of the same name.
     * @permission Needs access to the object's class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
     * @see orbital.logic.functor.Functor.Specification#getSpecification(orbital.logic.functor.Functor)
     */
    private static Type getTypeDeclaration(Object f) {
        if (f instanceof Type)
            return getDefault().TYPE();
        Class c = f.getClass();
        try {
            Field spec = getFieldOrInherited(c, "logicalTypeDeclaration");
            int   requiredModifier = Modifier.FINAL;
            if ((spec.getModifiers() & requiredModifier) == requiredModifier
                && Type.class.isAssignableFrom(spec.getType())) {
                if (true || !spec.isAccessible())
                    spec.setAccessible(true);
                return (Type) spec.get(f);
            }
        }
        catch (NoSuchFieldException trial) {}
        catch (IllegalAccessException trial) {}
        return null;
    }

    /**
     * Gets either a public or a declared field (not yet declared fields of super class).
     * @todo move to orbital.util.Utility
     */
    private static Field getFieldOrInherited(Class c, String name) throws NoSuchFieldException {
        try {
            return c.getField(name);
        }
        catch (NoSuchFieldException trial) {}
        return c.getDeclaredField(name);
    }

    /**
     * Get a string describing a typed object and its type.
     * @return <span class="String">s:<span class="type">&tau;</span></span> if s has type <span class="type">&tau;</span>.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see Object#toString()
     * @see Typed#getType()
     */
    public static final String toTypedString(Typed s) {
        return s == null
            ? "<null>"
            //@internal SymbolBase.toString() sometimes already prints like toTypedString, so we omit duplicate type descriptions, here
            : (s instanceof Symbol ? ((Symbol)s).getSignifier() : s.toString())
            + ':' + s.getType() + (SymbolBase.isFullForm() && s instanceof Variable && ((Variable)s).isVariable() ? "[var]" : "");
    }

    public static final String toTypedString(Typed s[]) {
        /* @internal pure functional rewrite would be
           MathUtilities.format(Functionals.map(new Function() {
                    public Object apply(Object s) {
                    //@internal SymbolBase.toString() sometimes already prints like toTypedString, so we omit duplicate type descriptions, here
                        return (s instanceof Symbol ? ((Symbol)s).getSignifier() : s.toString());
                    }
                    }, s)) */
        if (s == null)
            return "<null>";
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for (int i = 0; i < s.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Typed si = s[i];
            //@internal SymbolBase.toString() sometimes already prints like toTypedString, so we omit duplicate type descriptions, here
            sb.append((si instanceof Symbol ? ((Symbol)si).getSignifier() : si.toString()));
        }
        sb.append(']');
        sb.append(' ');
        sb.append(':');
        sb.append(' ');
        sb.append(typeOf(s));
        return sb.toString();
    }
}// Types
