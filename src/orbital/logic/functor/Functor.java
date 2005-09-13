/**
 * @(#)Functor.java 1.0 1997/06/13 Andre Platzer
 * 
 * Copyright (c) 1997-2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import java.io.Serializable;
import orbital.logic.sign.concrete.Notation;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.beans.IntrospectionException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import orbital.util.InnerCheckedException;
import orbital.util.Utility;

//NOTE: descendants are template classes

/**
 * An abstract base interface for all functors of an arity n applicable
 * in any predicate-logic style.
 * Usually denoted like <code>"f(...)"</code> or <code>"P(...)"</code>.
 * It provides a way for callers applying the functor, to callback callees
 * implementing a derivative of Functor.
 * <p>
 * A <dfn>Functor</dfn> f/n is any function-like object (resembling function-pointers in C++)
 * implementing <code>Functor</code>.
 * Its signature Specification declares which methods are contained.
 * The exact signature specification of a functor can either be defined
 * explicitly with a sub interface of <code>Functor</code> that encapsulates those methods,
 * or generically with an implicit interface accessed via {@link Specification Functor.Specification}.
 * <p>
 * <dl class="def">
 * <a id="Types">Types of functors:</a>
 *   <dt>predicates</dt> <dd>the return-type will be modelled as a <span class="keyword">boolean</span>-value,
 *     and P/n is interpreted as a relation, i.e. a subset of a cartesian product.</dd>
 *   <dt>functions</dt> <dd class="@todo">the return-type will be any type and the symbol f/n is interpreted as a function-object.</dd>
 *   <dt>functionals</dt> <dd>(a higher-order function) are special functions where the return-type or any of the argument-types will be a kind of <code>Functor</code>.</dd>
 * </dl>
 * <h5 id="FunctionalRelationalDuality">Duality between functions and predicates:</h5>
 * Every function f/n:A&rarr;B induces an implicit predicate with the same extensional semantics
 * <div><sub>f</sub>P/(n+1) := <big>{(</big>a<sub>1</sub>,...,a<sub>n</sub>,f(a<sub>1</sub>,...,a<sub>n</sub>)<big>)</big> &brvbar; (a<sub>1</sub>,a<sub>2</sub>,...,a<sub>n</sub>)&isin;A<big>}</big></div>
 * If a predicate P/n&sube;A is unique with respect to a certain parameter a<sub>k</sub>
 * it induces an implicit function with the same extensional semantics
 * <div><sub>P</sub>f/(n-1) := <big>{</big>f(a<sub>1</sub>,...,a<sub>k-1</sub>,a<sub>k+1</sub>,...a<sub>n</sub>):=a<sub>k</sub> &brvbar; P(a<sub>1</sub>,a<sub>2</sub>,...,a<sub>n</sub>) is true<big>}</big></div>
 * Also, whether the extension of a predicate is specified as a subset &rho;&isin;&weierp;(A),
 * or with its {@link Functionals#asFunction(Predicate) characterisitic function}
 * &chi;<sub>&rho;</sub> with &chi;<sub>&rho;</sub>(x)=1 iff x&isin;&rho;
 * is a matter purely syntactic variant.
 * Note however, that inspite of all this duality, functions and predicates can differ intensionally
 * regardless of their extensional equality.
 * <p>Finally, functions, predicates, relations, and graphs are all "isomorph" anyhow!</p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see Functor.Specification
 * @see Functor.Specification#getSpecification(Functor)
 * @see Function
 * @see Predicate
 * @see <a href="doc-files/Relations.html#function">Properties of Functions</a>
 * @see <a href="../../math/functional/doc-files/Categories.html#functor">Functors in the sense of category-theory</a>
 * @todo enhance documentation. Additionally improve defined terms
 */
public /*abstract template*/ abstract interface Functor/* abstract <class return-type, class[] arg-type> abstract */ {
    
    /**
     * {@inheritDoc}.
     * <p>
     * Note that functors will often provide intensional equality only,
     * since the mathematical notion of extensional equality for functions and predicates
     * is undecidable anyway (Proposition of Rice).
     * Nevertheless implementations are encouraged to provide a larger subset
     * of extensional equality as far as possible.
     * </p>
     */
    boolean equals(Object o);
        
    int hashCode();

    /**
     * Returns a string representation of the Functor.
     * <p>
     * This method is already provided in {@link java.lang.Object#toString()}.
     * If it is overwritten it should return a nice name for the functor.
     * </p>
     * @return a nice name for the functor.
     */
    String toString();
    
    
    /**
     * Called to apply the Functor.
     * <pre>public <var>return-type</var> <b>apply</b>(<var>arg-type</var>,<var>arg-type</var>,<i>...</i>,<var>arg-type</var>)</pre>
     * @return evaluates to f(a<sub>1</sub>,...,a<sub>n</sub>).
     */
    //template <var>return-type</var> apply(...);
    //template <var>return-type</var> operator()(...);

    /**
     * Returns the arity n.
     * The arity is the number of arguments needed in a call to apply.
     * This method is in a way optional, since the number and type of arguments to apply(...)
     * can be requested via reflection if only one apply-method is contained.
     * @return the arity n of a Functor f/n.
     * @see Functor.Specification#arity(Functor)
     * @see java.lang.Object#getClass()
     * @see java.lang.Class#getMethods()
     * @see java.lang.Class#getDeclaredMethods()
     * @see java.lang.reflect.Method
     * @todo remove since usually accessible via reflection.
     */
    //Specification callTypeDeclaration();


    /**
     * The base interface for all functors that are composed of other functors.
     * <p>
     * Composition of functors is possible in several variations.
     * <div>compose: (f,g) &#8614; f &#8728; g := f(g), or</div>
     * <div>compose: (f,(g<sub>1</sub>,...,g<sub>k</sub>)) &#8614; f &#8728; (g<sub>1</sub>,...,g<sub>k</sub>) := f<big>(</big>g<sub>1</sub>,...,g<sub>k</sub><big>)</big><sup>T</sup> vectorial, or</div>
     * <div>compose: (f,(g<sub>0,0</sub>,...,g<sub>n-1,m-1</sub>)) &#8614; f &#8728; (g<sub>0,0</sub>,...,g<sub>n-1,m-1</sub>) := f<big>(</big>g<sub>0,0</sub>,...,g<sub>n-1,m-1</sub><big>)</big> matrix.</div>
     * </p>
     * 
     * @structure inherits Functor
     * @structure aggregate compositor:Functor
     * @structure aggregate component:Object (usually Functor, Functor[], or Functor[][])
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @internal see AbstractFunctorComposite
     * @see <a href="{@docRoot}/Patterns/Design/Composite.html">(unidirectional and multiple) Composite Pattern</a>
     */
    static interface Composite extends orbital.logic.Composite, Functor {
        /**
         * Get the notation used.
         * @return the notation used for formatting this composite functor.
         */
        Notation getNotation();
        /**
         * Set the notation to use.
         * <p>
         * Optional operation.</p>
         * @param notation the notation to use for formatting this composite functor.
         * @throws UnsupportedOperationException if setting the notation is not supported.
         */
        void setNotation(Notation notation);
    }


    /**
     * Represents a signature and type specification belonging to a functor.
     * This class provides an <dfn>implicit interface</dfn> which declares what methods a corresponding functor contains.
     * <p>
     * A functor f is specified by a signature declaration of the form
     * <blockquote>
     *   f/n: A<sub>1</sub>&times;A<sub>2</sub>&times;&#8230;&times;A<sub>n</sub>&rarr;B
     * </blockquote>
     * Such a declaration is represented as the functor signature pecification
     * which specifies its arity <var>n</var>, argument-types <var>A<sub>1</sub></var>,...,<var>A<sub>n</sub></var> and return-type <var>B</var>
     * as well as additional properties.
     * </p>
     * <p>
     * Functors to which the signature specification above belongs apply on
     * <ul>
     *   <li><b>argument<sub>1</sub></b> of type <code><var>arg-type<sub>1</sub></var></code> = A<sub>1</sub>.</li>
     *   <li><b>argument<sub>2</sub></b> of type <code><var>arg-type<sub>2</sub></var></code> = A<sub>2</sub>.</li>
     *   <li>...</li>
     *   <li><b>argument<sub>n</sub></b> of type <code><var>arg-type<sub>n</sub></var></code> = A<sub>n</sub>.</li>
     *   <li><b>return</b> a value of type <code><var>return-type</var></code> = B.</li>
     * </ul>
     * </p>
     * <p>
     * The exact signature specification of a functor can either be defined
     * explicitly with a sub interface of <code>Functor</code> that encapsulates those methods,
     * or generically with an implicit interface introspected by {@link Functor.Specification#getSpecification(Functor)}
     * with the use of reflection.
     * </p>
     * <p>
     * By convention, the methods affected by a specification are those with an invocation-signature of
     * <pre>
     * <span class="keyword">public</span> <var>return-type</var> apply<b>(</b><var>arg-type<sub>1</sub></var><b>,</b><var>arg-type<sub>2</sub></var><b>,</b><i>...</i><b>,</b><var>arg-type<sub>n</sub></var><b>)</b>
     * <span class="keyword">public</span> <span class="Class">String</span> toString<b>()</b>
     * </pre>
     * </p>
     * 
     * @structure associated Functor
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @see Functor
     * @see java.lang.Class
     * @see java.lang.Object#getClass()
     * @see java.lang.Class#getMethods()
     * @see java.lang.Class#getDeclaredMethods()
     * @see java.lang.reflect.Method
     * @see java.beans.Introspector
     * @todo how to handle Function<Object,Function<Comparable,Number>>, or parametric generics like Function<S,A>?
     */
    static class Specification implements Comparable, Serializable {
        private static final long serialVersionUID = 7951104941212844811L;
        /**
         * specification of the name of the method to apply.
         * @todo definalize reinstantiate, to allow functors with other method names than apply.
         */
        private static final String spec_method = "apply";

        /**
         * specification of return-type.
         */
        private Class   spec_returnType = java.lang.Object.class;

        /**
         * specification of all paramter-types.
         */
        private Class[] spec_parameterTypes = null;

        /**
         * Create an exact specification with all properties declared.
         * @param parameterTypes an array of all parameter-types in order of calling. Its length is called arity.
         * @param returnType the type of a resulting value.
         * @preconditions parameterTypes&ne;null &and; returnType&ne;null
         */
        public Specification(Class[] parameterTypes, Class returnType) {
            if (parameterTypes == null)
                throw new NullPointerException("parameterTypes is null");
            else if (returnType == null)
                throw new NullPointerException("returnType is null");
            this.spec_parameterTypes = parameterTypes;
            this.spec_returnType = returnType;
        }
        /**
         * Create an exact specification with all properties declared.
         * The types given are represented themselves as specifications.
         * @param parameterTypes an array of all parameter-types in order of calling. Its length is called arity.
         * @param returnType the type of a resulting value.
         * @preconditions parameterTypes&ne;null &and; returnType&ne;null
         */
        public Specification(Specification[] parameterTypes, Specification returnType) {
            // for n=1 would need Specification(Function<parameterTypes[0].getParameterTypes(),parameterTypes[0].getReturnType()>, Function<returnType.getParameterTypes(),returnType.getReturnType()>);
            if (parameterTypes.length == 1 && parameterTypes[0].arity() == 0
                && returnType.arity() == 0) {
                this.spec_parameterTypes = new Class[] {parameterTypes[0].getReturnType()};
                this.spec_returnType = returnType.getReturnType();
            } else
                throw new UnsupportedOperationException("@xxx how to represent the type " + orbital.math.MathUtilities.format(parameterTypes) + "->" + returnType);
        }

        /**
         * Create an exact <em>predicate</em> specification with all properties declared.
         * <p>
         * The return-type will be {@link Boolean#TYPE} for representing predicates.
         * </p>
         * @param parameterTypes an array of all parameter-types in order of calling. Its length is called arity.
         * @preconditions parameterTypes&ne;null
         */
        public Specification(Class[] parameterTypes) {
            this(parameterTypes, Boolean.TYPE);
        }

        /**
         * Create an exact specification with the arity and return-type set.
         * The other properties will be set generically to the type <code>java.lang.Object</code>.
         * @param arity the arity specified. The arity is the number of arguments needed in a call to apply.
         * @param returnType the type of a resulting value.
         * @preconditions arity&ge;0 &and; returnType&ne;null
         */
        public Specification(int arity, Class returnType) {
            this(new Class[arity], returnType);
            for (int i = 0; i < arity; i++)
                this.spec_parameterTypes[i] = java.lang.Object.class;
        }

        /**
         * Create an exact specification with solely the arity set.
         * The other properties will be set most generically to the type <code>java.lang.Object</code>.
         * @param arity the arity specified. The arity is the number of arguments needed in a call to apply.
         */
        public Specification(int arity) {
            this(arity, java.lang.Object.class);
        }

        /**
         * Create a non-conform specification with all properties declared.
         * Other apply method names than "apply" are not fully conform with the general functor specification
         * but may sometimes be useful as well.
         * @param method the name of the method to call for applying the functor. Usually <span class="String">"apply"</span>.
         * @param arity the arity specified. The arity is the number of arguments needed in a call to apply.
         * @param parameterTypes an array of all parameter-types in order of calling. Must have the length arity.
         * @param returnType the type of a resulting value.
         * @preconditions parameterTypes&ne;null &and; returnType&ne;null &and; method&ne;null
         * @postconditions <span class="consistent">&#9671;</span>abnormal(name of applyMethod)
         */
        public Specification(String method, Class[] parameterTypes, Class returnType) {
            this(parameterTypes, returnType);
            if (method == null)
                throw new NullPointerException("method name is null");
            else if (!"apply".equals(method))
                throw new UnsupportedOperationException("not yet implemented");
            /*
             * this.spec_method = method;
             */
        }

        /**
         * Clones this specification.
         * @postconditions RES != RES && RES != this && RES.equals(this)
         */
        public Object clone() {
            return new Specification(spec_parameterTypes, spec_returnType);
        }

        /**
         * Compares two specifications.
         * <p>
         * This implementation compares for arity in favor of parameter-types in favor of return-type.
         * </p>
         * @todo couldn't we compare for the partial order "compatiblity", instead?
         */
        public int compareTo(Object o) {
            Specification b = (Specification) o;
            int order = arity() - b.arity();
            if (order != 0)
                return order;
            assert spec_parameterTypes.length == arity() && b.spec_parameterTypes.length == arity() : "arity is the number of arguments, and difference of arities is zero";
            for (int i = 0; i < spec_parameterTypes.length; i++) {
                order = spec_parameterTypes[i].getName().compareTo(b.spec_parameterTypes[i].getName());
                if (order != 0)
                    return order;
            }
            return spec_returnType.getName().compareTo(b.spec_returnType.getName());
        }
                
                
        public boolean equals(Object arg) {
            if (arg instanceof Specification) {
                Specification B = (Specification) arg;
                if (arity() != B.arity() || !spec_returnType.equals(B.spec_returnType))
                    return false;
                assert spec_parameterTypes.length == B.spec_parameterTypes.length : "same arity same parameter length";
                for (int i = 0; i < spec_parameterTypes.length; i++)
                    if (!spec_parameterTypes[i].equals(B.spec_parameterTypes[i]))
                        return false;
                return true;
            } 
            return false;
        } 
                
        public int hashCode() {
            return arity() ^ (spec_returnType.hashCode() >>/*rotr*/ 1) ^ Utility.hashCodeAll(spec_parameterTypes);
        }

        /**
         * Specifies the arity <code>n</code>.
         * The arity is the number of arguments needed in a call to apply.
         * @return the arity <code>n</code> of the specified Functor <code>f/n</code>.
         * @postconditions RES == getParameterTypes().length
         */
        public int arity() {
            return spec_parameterTypes.length;
        } 

        /**
         * Specifies the argument-types of a Functor.
         * @return an array (of size n, starting at index 0) that contains:
         *  <ul>
         *    <li>type <code><var>arg-type<sub>1</sub></var></code> of <b>Argument<sub>1</sub></b>.</li>
         *    <li>type <code><var>arg-type<sub>2</sub></var></code> of <b>Argument<sub>2</sub></b>.</li>
         *    <li>...</li>
         *    <li>type <code><var>arg-type<sub>n</sub></var></code> of <b>Argument<sub>n</sub></b>.</li>
         *  </ul>
         */
        public Class[] getParameterTypes() {
            return spec_parameterTypes;
        } 

        protected void setParameterTypes(Class[] newParameterTypes) {
            this.spec_parameterTypes = newParameterTypes;
        } 

        /**
         * Specifies the return-type of a functor.
         * @return the type of the <code><var>return-type</var></code> value.
         */
        public Class getReturnType() {
            return spec_returnType;
        } 
        protected void setReturnType(Class newReturnType) {
            this.spec_returnType = newReturnType;
        } 

        // type compatibility query method

        /**
         * Checks whether the given specification is compatible with this.
         * measures compatibility.
         * <p>
         * This method will check the given specification against this.
         * The given specification is compatible if it fulfills the arity and returnType of this specification
         * as well as all arguments are assignement compatible with this, i.e.
         * they are of the same kind (same type or a subtype).
         * </p>
         * @param b the specification of the functor that is to be checked for compatibility with this specification.
         * @return whether <code>b &le; this</code>,
         *  i.e. if this specification is more general than b.
         *  This means that b is more special than this and fulfills the requirements
         *  of this specification, thus can be considered a subtype.
         * @see #isApplicableTo(Object[])
         * @see java.lang.Class#isAssignableFrom(java.lang.Class)
         */
        public boolean isCompatible(Specification b) {
            if (arity() != b.arity() || !spec_returnType.equals(b.spec_returnType))
                return false;
            assert spec_parameterTypes.length == b.spec_parameterTypes.length : "same arity same parameter length";
            for (int i = 0; i < spec_parameterTypes.length; i++)
                if (!spec_parameterTypes[i].isAssignableFrom(b.spec_parameterTypes[i]))
                    return false;
            return true;
        } 

        /**
         * Checks whether the type specification is compatible with the given list of arguments.
         * @preconditions true
         * @param args the arguments to check for compatibility with this symbol.
         *  <code>null</code>, or an array of length <span class="number">0</span> can be used for zero arguments.
         * @return whether the arguments are assignable to the required parameter types of this symbol.
         *  This especially includes whether the number of arguments matches this symbol's arity.
         * @see #isCompatible(Functor.Specification)
         * @see Types#isApplicableTo(Type,Expression[])
         */
        public boolean isApplicableTo(Object[] args) {
            if ((args == null || args.length == 0) && arity() == 0)
                return true;
            //@internal now we could as well call isCompatible(Specification) with a specification that we generate from args, setting the expected return type of getType().
            if (args.length != arity())
                return false;
            Class[] spec_parameterTypes = getParameterTypes();
            assert args.length == spec_parameterTypes.length : "same arity same parameter length";
            for (int i = 0; i < spec_parameterTypes.length; i++)
                if (!(args[i] == null | spec_parameterTypes[i].isInstance(args[i])))
                    return false;
            return true;
        }

        /**
         * Checks whether the given functor object is conform to this specification.
         * <p>
         * Can be overwritten in order to perform checks in addition to type conformity.
         * </p>
         * @param f the object to check for conformity to this specification.
         *  <code>null</code> does not conform to any specification.
         * @return whether the object f is conform to this specification.
         * @todo perhaps we should change the argument type to java.lang.Object?
         * @see Class#isInstance(Object)
         * @since Orbital1.1
         */
        public boolean isConform(Functor f) {
            try {
                return f != null && getMethod(f.getClass()) != null;
            }
            catch (NoSuchMethodException no) {return false;}
        }

        // apply
        
        /**
         * Get the apply method in a given class that corresponds to this specification.
         * @param cls the class whose apply method to get.
         *  Which apply method of cls is chosen depends on this specification.
         * @return the apply(...) method within the given class, according to this specification.
         * @preconditions (&forall;o:cls) this.isConform(o)
         * @throws NoSuchMethodException if the given class does not conform to this specification.
         *  This may be the case because cls does not provide the right apply method.
         * @throws SecurityException if access to the information is denied, see {@link Class#getMethod(String, Class[])}.
         */
        public Method getMethod(Class cls) throws NoSuchMethodException {
            Method apply = cls.getMethod(spec_method, getParameterTypes());
            if (!apply.getReturnType().equals(getReturnType()))
                //@note if there are multiple methods of the same parameter types, then we need to go through getMethods(), explicitly
                throw new NoSuchMethodException("attempt to provide method with a different return-type. required: " + getReturnType() + " found: " + apply + ". Perhaps covariant return-types have been added to the Java Language Specification?");
            return apply;
        } 

        /**
         * Get a human readable string representation of this specification of a functor.
         */
        public String toString() {
            String argumentTypes = typeListSpec(getParameterTypes());
            return "/" + arity() + ":" + (
                  Boolean.TYPE.equals(getReturnType())
                  // predicate
                  ? "(" + (argumentTypes != null ? argumentTypes : "") + ")"
                  // function
                  : (argumentTypes != null ? argumentTypes : "{()}") + "->" + getReturnType().getName()
                  );
        } 

        private static final String typeListSpec(Class[] v) {
            if (v == null || v.length == 0)
                return null;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < v.length - 1; i++)
                sb.append(v[i].getName() + "*");
            if (v.length > 0)
                sb.append(v[v.length - 1].getName());
            return sb.toString();
        } 

        
        // static utilities
                
        /**
         * Introspect on a functor and get a specification of his exposed signature.
         * <p>
         * This static method will analyze a functor object and generate a specification
         * of his signature. It works for both, explicit and implicit interfaces.
         * </p>
         * <p>
         * If available, the introspection is improved by looking for a field
         * <pre>
         * <span class="keyword">static</span> <span class="keyword">final</span> <span class="Orbital">Functor.Specification</span> callTypeDeclaration;
         * </pre>
         * in the corresponding class of the functor object.
         * </p>
         * 
         * @param f the functor object to be analyzed.
         * @return a signature specification object describing the target functor.
         * @postconditions RES.isConform(f)
         * @throws IntrospectionException if an exception occurs during introspection.
         * @internal see #getDynamicSpecification(Functor)
         * @internal see #getStaticSpecification(Class)
         * @internal see #searchMethod(Class)
         * @permission Needs access to the functor class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
         * @see orbital.logic.sign.type.Types#declaredTypeOf(orbital.logic.functor.Functor)
         */
        public static final Specification getSpecification(Functor f) throws IntrospectionException {
            Specification spec;
            //spec = getDynamicSpecification(f);
            //if (spec != null)
            //  return spec;
            spec = getStaticSpecification(f.getClass());
            if (spec != null)
                return spec;
            Method apply = searchMethod(f.getClass());
            return new Specification(apply.getParameterTypes(), apply.getReturnType());
        } 

        /**
         * Invokes the corresponding apply method of the Functor specified.
         * <p>
         * This method provides much dynamic flexibility when dealing with unknown functors
         * at run-time.
         * However note that due to the reflection required during the execution of this method,
         * directly calling the apply method is preferred whenever possible.
         * </p>
         * @throws IntrospectionException if the functor is invalid or specifies none or too many apply-methods.
         * @throws NoSuchMethodException if the functor made an explicit specification which he does not conform to.
         * @throws IllegalArgumentException if the number of actual arguments and formal parameters differ, or if an unwrapping conversion fails.
         * @throws InvocationTargetException if the underlying method throws an exception.
         * @permission Needs access to the functor class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
         * @todo depending upon the arguments and their types, we should invoke the corresponding apply-method, if there are multiple apply-methods. Even if they do not fit the default specification in the field specification.
         * @see #getSpecification(Functor)
         * @see #getMethod(Class)
         * @see Method#invoke(Object, Object[])
         */
        public static final Object invoke(Functor f, Object[] args) throws IntrospectionException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
            Specification spec = getSpecification(f);
            try {
                Method apply = spec.getMethod(f.getClass());
                if (!apply.isAccessible())
                    apply.setAccessible(true);
                Object r = apply.invoke(f, args);
                assert spec.isApplicableTo(args) : "(at least) when apply succeeded, its specification was applicable to the arguments";
                return r;
            } catch (IllegalAccessException inner) {
                throw (IntrospectionException) new IntrospectionException("invalid functor").initCause(inner);
            } catch (IllegalArgumentException ex) {
                //assert !spec.isApplicableTo(args) : "when the arguments were wrong, then its specification was not applicable to the arguments";
                throw ex;
            } 
        } 


        // private implementation methods for introspection reading
                
        /**
         * Get the dynamic specification specified in the given object instance.
         * Get the specification specified as a return-value of the method call to that
         * method having a signature
         * <pre>
         * <span class="Orbital">Functor.Specification</span> specification();
         * </pre>
         * or <code>null</code> if no such method exists.
         * @param f a valid Functor that contains a <code>specification()</code> method.
         * @return <code>f.specification()</code> if defined. and <code>null</code> otherwise.
         * @throws IllegalArgumentException if f is no valid functor.
         * @permission Needs access to the functor class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
         * @see #getSpecification(Functor)
         */
//      private static Specification getDynamicSpecification(Functor f) throws IntrospectionException {
//          Class cls = f.getClass();
//          try {
//              Method spec = cls.getMethod("specification", null);
//              if (Specification.class.isAssignableFrom(spec.getReturnType())) {
//                  if (!spec.isAccessible())
//                      spec.setAccessible(true);
//                  return (Specification) spec.invoke(f, null);
//              }
//          }
//          catch (NoSuchMethodException no_valid_specification_function) {}
//          catch (IllegalAccessException inner) {
//              throw (IntrospectionException) new IntrospectionException("invalid functor").initCause(inner);
//          } catch (InvocationTargetException inner) {
//              throw (IntrospectionException) new IntrospectionException("functor threw").initCause(inner);
//          } catch (ClassCastException no_valid_specification_function) {}
//          return null;
//      } 

        /**
         * Get the fixed static specification specified in the given functor class.
         * Get the specification specified by the field with the signature
         * <pre>
         * <span class="keyword">static</span> <span class="keyword">final</span> <span class="Orbital">Functor.Specification</span> callTypeDeclaration;
         * </pre>
         * or <code>null</code> if no such field exists.
         * Implementations may also consider non-static fields of the same name.
         * @permission Needs access to the functor class and will therefore call {@link java.lang.reflect.AccessibleObject#setAccessible(boolean) setAccessible(true)}.
         * @see #getSpecification(Functor)
         * @see orbital.logic.sign.type.Types#declaredTypeOf(orbital.logic.functor.Functor)
         * @xxx getField(...) won't work for non-public fields.
         */
        private static Specification getStaticSpecification(Class c) {
            try {
                Field spec = c.getField("callTypeDeclaration");
                //int   expectedModifier = Modifier.STATIC | Modifier.FINAL;
                int   requiredModifier = Modifier.FINAL;
                if ((spec.getModifiers() & requiredModifier) == requiredModifier
                    && Specification.class.isAssignableFrom(spec.getType())) {
                    if (!spec.isAccessible())
                        spec.setAccessible(true);
                    return (Specification) spec.get(null);
                }
            }
            catch (NoSuchFieldException trial) {}
            catch (IllegalAccessException trial) {}
            return null;
        }

        /**
         * Search for a (single) apply method of the functor class.
         * @throws IntrospectionException if the functor is invalid or specifies none or more than one apply-methods.
         * @see #getSpecification(Functor)
         */
        private static Method searchMethod(Class c) throws IntrospectionException {
            Method   found = null;                  // the method already found (null if none)
            Method[] methods = c.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (spec_method.equals(methods[i].getName())) {
                    if (found == null)
                        found = methods[i];             // first apply method
                    else                                                // multiple apply methods
                        throw new IntrospectionException("invalid functor: apply(...)-methods are ambiguous");
                } 
            } 
            if (found != null)
                return found;
            else
                throw new IntrospectionException("invalid functor: no apply method found");
        } 
    }
}
