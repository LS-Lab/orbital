/**
 * @(#)Variable.java 0.8 2001/06/24 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.trs;

/**
 * Variable tagging interface.
 * <p>
 * This interface is used to tag classes whose objects are potentially treated as variable expressions
 * subject to binding during {@link orbital.logic.trs.Substitutions#unify(java.util.Collection) unification}
 * and {@link orbital.logic.imp.Inference reasoning}.
 * An object <code>o</code> implementing <code>Variable</code> is treated as a variable iff
 * <code>o.{@link #isVariable()}</code> is <span class="keyword">true</span>.
 * </p>
 *
 * @version $Id$
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo couldn't we get rid of constant/variable distinction if we simply introduced distinguishing bound variables/free variables instead? We would need an interface BoundingExpression or alike
 * @internal alternative get rid of isVariable() and dynamically implement Variable via java.lang.reflect.Proxy.
 */
public abstract interface Variable {
    /**
     * Whether this concrete object is variable.
     * <p>
     * This notion usually refers to the variability in the context of
     * Term Rewrite Systems and Reasoning.
     * </p>
     * @preconditions true
     * @return <code>true</code> if this object is variable,
     *  and <code>false</code> if this object is constant.
     * @postconditions usually RES==OLD(RES)
     */
    boolean isVariable();
}
