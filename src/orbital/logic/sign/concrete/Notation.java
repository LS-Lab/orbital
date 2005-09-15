/**
 * @(#)Notation.java 1.0 2000/06/27 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.concrete;

import java.io.Serializable;

import orbital.logic.Composite;
import orbital.logic.functor.*;

import java.util.Collection;
import java.util.Map;

import java.util.Arrays;
import java.util.Collections;

import orbital.util.graph.Node;
import orbital.util.graph.ListTree;
import orbital.util.KeyValuePair;
import java.util.Iterator;
import java.util.ListIterator;

import java.util.HashMap;

import java.beans.IntrospectionException;
import orbital.util.Utility;

/**
 * Represents notational variants of compositor applications.
 * Defines the concrete syntax and thus the linearization of symbols.
 * @see <a href="{@docRoot}/Patterns/Design/enum.html">typesafe enum pattern</a>
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @invariants a.equals(b) &hArr; a==b
 * @todo invariants     && sorted(compositorNotation)
 */
public abstract class Notation implements Serializable, Comparable {
    private static final long serialVersionUID = -3071672372655194662L;
    private static final int PRECEDENCE_HIGH = 499;
    private static final int PRECEDENCE_LOW = 500;
    private static final int PRECEDENCE_DEFAULT = PRECEDENCE_LOW;

    /**
     * the name to display for this enum value
     * @serial
     */
    private final String name;

    /**
     * Ordinal of next enum value to be created
     */
    private static int nextOrdinal = 0;

    /**
     * Table of all canonical references to Notation classes
     */
    private static Notation[] values = new Notation[15];

    /**
     * Assign an ordinal to this enum value
     * @serial
     */
    private final int ordinal = nextOrdinal++;

    /**
     * Which notation to use for DEFAULT.
     * @see #getDefault()
     * @todo = Preferences.get... sometime
     */
    private static Notation defaultNotation;

    //TODO: gjc compiles class files that complain about IllegalAccessExceptions when private
    /*private*/protected Notation(String name) {
        this.name = name;
        values[nextOrdinal - 1] = this;
    }

    /**
     * Format compositor with arguments in this notation.
     * @param compositor the compositor to apply, or the object describing the compositor to apply.
     * @param arg the argument or array of arguments to apply the compositor on, or the array of
     *  objects describing the corresponding arguments.
     * @return a good format of the compositor applied on the specified arguments.
     */
    public abstract String format(Object compositor, Object arg);

    public String toString() {
        return this.name;
    } 

    /**
     * Order imposed by ordinals according to the order of creation.
     * @postconditions consistent with equals
     */
    public int compareTo(Object o) {
        return ordinal - ((Notation) o).ordinal;
    } 

    /**
     * Maintains the guarantee that all equal objects of the enumerated type are also identical.
     * @postconditions a.equals(b) &hArr; if a==b.
     */
    public final boolean equals(Object that) {
        return super.equals(that);
    } 
    public final int hashCode() {
        return super.hashCode();
    } 

    /**
     * Maintains the guarantee that there is only a single object representing each enum constant.
     * @serialData canonicalized deserialization
     */
    private Object readResolve() throws java.io.ObjectStreamException {
        // canonicalize
        return values[ordinal];
    } 


    /**
     * Get the notation used for the synonym DEFAULT.
     */
    protected static final Notation getDefault() {
        return defaultNotation;
    } 

    /**
     * Set which notation to use for DEFAULT.
     * <p>
     * Call to specify system default notation behaviour.</p>
     * @param notation which notation to use for the case of {@link #DEFAULT} notation.
     * @throws IllegalArgumentException if notation==DEFAULT
     *  since this would result in an infinte recursion at runtime.
     * @preconditions notation != DEFAULT
     * @postconditions getDefault() == notation
     */
    public static final void setDefault(Notation notation) {
        if (notation == DEFAULT)
            throw new IllegalArgumentException("true notation expected, no synonym");
        defaultNotation = notation != null ? notation : PREFIX;
    } 

    // enumeration of compositor notations

    /**
     * Specifies to use system default notation.
     * <p>
     * Compositors with DEFAULT notation will use the current system default notation.</p>
     * @see #setDefault(Notation)
     */
    public static final Notation DEFAULT = new Notation("default") {
            private static final long serialVersionUID = 5644030897053785928L;
            public String format(Object compositor, Object arg) {
                return getDefault().format(compositor, arg);
            } 
        };

    /**
     * Specifies automatic compositor-dependant notation as registered.
     * <p>
     * Delegates to the {@link Notation#getNotation(Object) registered notation}.
     * Compositors with registered default notation like +, -, *, /, ^ have a notation set.
     * All unknown symbols are treated as prefix.
     * </p>
     * @see #PREFIX
     * @see #INFIX
     * @see #POSTFIX
     */
    public static final Notation AUTO = new Notation("auto") {
            private static final long serialVersionUID = -5725522528292770323L;
            public String format(Object compositor, Object arg) {
                NotationSpecification spec = getNotation(compositor);
                if (spec != null)
                    return spec.notation.format(compositor, arg);
                else
                    return PREFIX.format(compositor, arg);
            } 
        };
        
    /**
     * Specifies prefix notation <code>"f(a,b)"</code>.
     */
    public static final Notation PREFIX = new Notation("prefix") {
            private static final long serialVersionUID = -5933847939038152414L;
            public String format(Object compositor, Object arg) {
                StringBuffer sb = new StringBuffer();
                if (compositor != null) {
                    if (compositor instanceof Composite)
                        // descend into composite compositors. will receive brackets, automatically, since !hasCompactBrackets("")
                        sb.append(format("", compositor));
                    else
                        sb.append(compositor + "");
                }
                if (arg == null)
                    arg = getPureParameters(compositor);
                if (arg != null) {
                    if (!hasCompactBrackets(compositor))
                        sb.append('(');
                    for (Iterator i = Utility.asCollection(arg).iterator(); i.hasNext(); )
                        sb.append(i.next() + (i.hasNext() ? "," : ""));
                    if (!hasCompactBrackets(compositor))
                        sb.append(')');
                } 
                return sb.toString();
            } 
        };

    /**
     * Specifies infix notation <code>"a f b"</code>.
     * <p>
     * n-ary this becomes <code>"a f b f c"</code>.</p>
     * <p>
     * Be aware that formatting a complex compound expression with simple infix only, might not result
     * in a term with usual precedence, so brackets would be required.</p>
     * @see #BESTFIX
     */
    public static final Notation INFIX = new Notation("infix") {
            private static final long serialVersionUID = 585674879470556509L;
            public String format(Object compositor, Object arg_) {
                if (arg_ == null)
                    arg_ = getPureParameters(compositor);
                final Collection arg = Utility.asCollection(arg_);
                if (arg == null || arg.size() == 0)
                    return compositor + "";
                final StringBuffer sb = new StringBuffer();
                final int          precedence = precedenceOf(compositor);

                // special handling for unary infix formatting
                if (arg.size() == 1) {
                    if (compositor instanceof Composite)
                        // descend into composite compositors with brackets
                        sb.append("(" + format("", compositor) + ") @");
                    else
                        sb.append(compositor + " @ ");
                }
                for (Iterator i = arg.iterator(); i.hasNext(); ) {
                    sb.append(i.next());
                    if (i.hasNext()) {
                        if (!isHigh(precedence))
                            sb.append(' ');
                        if (compositor instanceof Composite)
                            // descend into composite compositors with brackets
                            sb.append("(" + format("", compositor) + ")");
                        else
                            sb.append(compositor);
                        if (!isHigh(precedence))
                            sb.append(' ');
                    }
                } 
                return sb.toString();
            } 
        };

    /**
     * Specifies best mixed notation <code>"a*(b+f(c)) + d"</code> inserting
     * brackets whenever necessary.  <p> Decomposes compositors into
     * its components whenever possible building a function tree to
     * optimize formatting.  This more sophisticated notation is aware
     * of the {@link Notation#getNotation(Object) registered default notation}
     * and will only insert brackets when necessary.  </p>
     * @see #AUTO
     * @xxx if the compositor is itself composite then descend formatting it (with accurate brackets) as well.
     */
    public static final Notation BESTFIX = new Notation("bestfix") {
            private static final long serialVersionUID = 2361099498303659521L;
            public String format(Object compositor, Object arg_) {
                if (!(compositor instanceof Functor))
                    return PREFIX.format(compositor, arg_);
                //@todo explicitly work on graph-structure induced by Composite without importing orbital.util.graph for this sole reason
                // however, how to briefly append arguments to the compositor object (just for formatting), then?
                Node root;
                if (arg_ == null)
                    arg_ = getPureParameters(compositor);
                // convert to function tree
                Collection arg = Utility.asCollection(arg_);
                if (arg == null || arg.size() == 0)
                    root = compositeTree(compositor);
                else {
                    root = new ListTree.TreeNode(compositor, compositor.toString());
                    // root.addAll(Functionals.map(compositeTree, arg));
                    for (Iterator i = arg.iterator(); i.hasNext(); )
                        root.add(compositeTree(i.next()));
                } 
                // traverse and format
                return new Function/*<Node, String>*/() {
                        private StringBuffer sb;
                        public Object/*>String<*/ apply(Object/*>Node<*/ root) {
                            return visit((Node) root);
                        } 
                        private final String visit(Node node) {
                            if (node.isLeaf())
                                return ((KeyValuePair) node).getValue() + "";
                            NotationSpecification spec = getNotation(((KeyValuePair) node).getKey());
                            int    apos = 0;

                            // will contain formatted arguments
                            String argDesc[] = new String[node.getEdgeCount()];
                            int    i = 0;
                            for (Iterator it = node.edges(); it.hasNext(); i++) {

                                // ignore compositor position in association format specifier
                                if (spec != null && spec.associativity.charAt(apos) == 'f')
                                    apos++;
                                Node                  n = (Node) it.next();
                                NotationSpecification childSpec = getNotation(((KeyValuePair) n).getKey());
                                String                inner = visit(n);

                                // handle associativity if specified
                                if (spec != null) {
                                    assert spec.associativity.length() > apos : "wrong associativity specifier " + spec + " for " + ((KeyValuePair) node).getKey() + " at position " + apos + " in argument " + i + " (" + ((KeyValuePair) n).getKey() + ")";
                                    switch (spec.associativity.charAt(apos++)) {
                                    case 'x':
                                        if (childSpec == null || childSpec.compareTo(spec) < 0)
                                            argDesc[i] = inner;
                                        else
                                            argDesc[i] = '(' + inner + ')';
                                        break;
                                    case 'y':
                                        if (childSpec == null || childSpec.compareTo(spec) <= 0)
                                            argDesc[i] = inner;
                                        else
                                            argDesc[i] = '(' + inner + ')';
                                        break;
                                    default:
                                        throw new NumberFormatException("wrong associativity specifier " + spec);
                                    }
                                } else
                                    argDesc[i] = inner;
                            } 
                            return AUTO.format(((KeyValuePair) node).getKey(), argDesc);
                        } 
                    }.apply(root).toString();
            } 

        };

    /**
     * Specifies mixfix notation <code>"f1 a f2 b f3"</code>.
     */

    // public static final int MIXFIX = 2.5;

    /**
     * Specifies postfix notation <code>"(a,b) f"</code>.
     */
    public static final Notation POSTFIX = new Notation("postfix") {
            private static final long serialVersionUID = -7892084161142935847L;
            public String format(Object compositor, Object arg) {
                StringBuffer sb = new StringBuffer();
                if (arg == null)
                    arg = getPureParameters(compositor);
                if (arg != null) {
                    if (!hasCompactBrackets(compositor))
                        sb.append('(');
                    for (Iterator i = Utility.asCollection(arg).iterator(); i.hasNext(); )
                        sb.append(i.next() + (i.hasNext() ? "," : ""));
                    if (!hasCompactBrackets(compositor))
                        sb.append(')');
                } 
                if (compositor instanceof Composite)
                    // descend into composite compositors. will receive brackets, automatically, since !hasCompactBrackets("")
                    sb.append(format("", compositor));
                else
                    sb.append(compositor + "");
                return sb.toString();
            } 
        };

    //@todo introduce     public static final Notation MIXFIX(String prefix, String infix, String suffix) {

    

    // Utilities

    /**
     * Get the pure formal parameters of a compositor.
     * @return an argument array filled with the pure arguments #0, #1, #2, ... #n-1 upto the arity n of compositor.
     * @todo how to achieve pure functions like "*" without any explicit parameters being printed as #0*#1 instead of * or x*y?
     */
    private static Object[] getPureParameters(Object compositor) {
        if (compositor instanceof Functor) {
            if (compositor instanceof VoidFunction || compositor instanceof VoidPredicate)
                return null;
            else if (compositor instanceof Function || compositor instanceof Predicate)
                return new String[] {"#0"};
            else if (compositor instanceof BinaryFunction || compositor instanceof BinaryPredicate)
                return new String[] {"#0", "#1"};
            else
                try {
                    // use arity of functor specification
                    Functor.Specification spec = Functor.Specification.getSpecification((Functor) compositor);
                    String[] pure = new String[spec.arity()];
                    for (int i = 0; i < pure.length; i++)
                        pure[i] = "#" + i;
                    return pure;
                }
                catch (IntrospectionException trying) {
                    return null;
                }
        } else
            return null;
    }

    /**
     * Get the default notation specification registered for a compositor.
     * @return the default notation specification of the compositor, or <code>null</code> if not set.
     * @see #setNotation(Object, NotationSpecification)
     */
    public static NotationSpecification getNotation(Object compositor) {
        return (NotationSpecification/*__*/) compositorNotation.get(compositor);
    } 

    /**
     * Get the precedence of a compositor (with 1 being the highest precedence).
     * @return the precedence of the compositor, or 0 if not set.
     * @see #getNotation(Object)
     */
    private static int precedenceOf(Object compositor) {
        NotationSpecification spec = getNotation(compositor);
        return spec != null ? spec.precedence : 0;
    } 

    /**
     * Sets/registers the default notation specification for a compositor.
     * @param f the compositor whose notation specification to set.
     * @param spec the notation specification to set as default for the compositor f.
     * @return <code>true</code> if the default notation specification for the compositor f
     *  changed as a result of the call.
     */
    public static boolean setNotation(Object f, NotationSpecification spec) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(new RuntimePermission("setStatic.notationSpecification"));
        } 
        return compositorNotation.put(f, spec) != null;
    }
    /**
     * Remove any default notation specifications for a compositor.
     * @param f the compositor whose notation specification to remove.
     * @return previous value associated with specified compositor, or <code>null</code> if there was no mapping for compositor.
     */
    public static NotationSpecification removeNotation(Object f) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(new RuntimePermission("setStatic.notationSpecification"));
        } 
        return (NotationSpecification/*__*/) compositorNotation.remove(f);
    }
        
    /**
     * Sets/registers all notations contained in an array.
     * @param compositorsAndNotations Contains compositors and their notation specifications.
     *  Stored as an array of length-2 arrays
     *  with compositors[i][0] being the {@link Object},
     *  and compositor[i][1] being a {@link Notation.NotationSpecification}.
     * @see <a href="{@docRoot}/Patterns/Design/Convenience.html">Convenience Method</a>
     * @see #setNotation(Object,Notation.NotationSpecification)
     */
    public static final void setAllNotations(Object[][] compositorsAndNotations) {
        for (int i = 0; i < compositorsAndNotations.length; i++) {
            if (compositorsAndNotations[i].length != 2)
                throw new IllegalArgumentException("array of dimension [][2] expected");
            final Object f = compositorsAndNotations[i][0];
            NotationSpecification notation = (NotationSpecification)compositorsAndNotations[i][1];
            if (f == null)
                throw new NullPointerException("illegal compositor " + f + " for " + notation);
            if (notation == null)
                throw new NullPointerException("illegal notation " + notation + " for " + f);
            Notation.setNotation(f, notation);
        }
    }

    /**
     * Whether the compositor has a default notation defined and is unary,
     * thus displayed with compact (i.e. invisible) brackets.
     */
    private static boolean hasCompactBrackets(Object compositor) {
        // distinguish unary from binary registered compositors
        NotationSpecification spec = getNotation(compositor);
        return spec != null && spec.arity() == 1;
    } 
    
    /**
     * Whether the specified precedence is "high" such that its compositor is formated
     * without separators.
     */
    static boolean isHigh(int precedence) {
        return precedence <= PRECEDENCE_HIGH;
    } 

    // Utility methods
        
    /**
     * Creates a function tree view of a composite object by decomposing it into its components.
     * @see Composite
     */
    private static Node compositeTree(Object f) {
        if (!(f instanceof Composite))
            return new ListTree.TreeNode(f, f + "");
        Composite  c = (Composite) f;
        Object     compositor = c.getCompositor();
        Collection components = Utility.asCollection(c.getComponent());
        Node       n = new ListTree.TreeNode(compositor, compositor + "");
        if (components == null)
            throw new NullPointerException(f + " of " + f.getClass() + " has compositor " + compositor + " and components " + components);
        // n.addAll(Functionals.map(compositeTree, components));
        for (Iterator i = components.iterator(); i.hasNext(); )
            n.add(compositeTree(i.next()));
        return n;
    } 


    
    /**
     * Contains the specification of the default notation for a compositor.
     * @invariants precedence > 0 && associativity and notation match
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @stereotype data-type
     */
    public static class NotationSpecification implements Comparable, Serializable {
        private static final long serialVersionUID = -8249931256922519844L;
        /**
         * The precedence of the sign (with 1 being the highest precedence).
         * @serial
         */
        private int precedence;
        /**
         * The associativity specification of the sign.
         * @serial
         */
        private String associativity;
        /**
         * The notation object to use for formatting.
         * @serial
         */
        private Notation notation;
        //TODO: generalize to specify arity as well, to distinguish -/2 from -/1, from -/7, or rely on Functor.Specification for that? No! That's what the object formatted should know itself.
        //TODO: do we even need to introduce protected String compositor;? No, because object should know as well?
        /**
         * Create a specification of a compositor's notation.
         * @see Notation#getAssociativity()
         */
        public NotationSpecification(int precedence, String associativity, Notation notation) {
            this.precedence = precedence;
            this.associativity = associativity;
            this.notation = notation;
        }
        /**
         * Create a specification of a compositor's notation.
         * @see Notation#getAssociativity()
         */
        public NotationSpecification(int precedence, String associativity, Notation notation, int arity) {
            this(precedence, associativity, notation);
            int assocArity = associativity.length() - 1;
            //@xxx instead of -1 should do -associativity.numberOf('f')
            if (arity != assocArity)
                throw new IllegalArgumentException("associativity description " + associativity + " with " + assocArity + " arguments must match arity " + arity);
        }
        /**
         * Create a specification of a compositor's notation.
         * @see Notation#getAssociativity()
         */
        public NotationSpecification(int precedence, Notation notation, int arity) {
            this(precedence, prefixNonAssociativity(arity), notation);
        }
        /**
         * Create a specification of a compositor's notation with automatic notation resolution.
         * <p>
         * This constructor will determine the notation object to use for formatting
         * according to the associativity string which must be correct, then!
         * </p>
         * @see Notation#getAssociativity()
         */
        public NotationSpecification(int precedence, String associativity) {
            this(precedence, associativity, null);
            int f = associativity.indexOf('f');
            if (f == 0)
                this.notation = PREFIX;
            else if (f == associativity.length() - 1)
                this.notation = POSTFIX;
            else if (f > 0)
                this.notation = INFIX;
            else
                throw new IllegalArgumentException("could not guess notation from associativity '" + associativity + "'");
        }
        /**
         * Create a very basic default notation specification of a compositor's notation.
         * <p>
         * The default notation specification is
         * non-associative system {@link Notation#DEFAULT default notation} of very strong precedence.
         * </p>
         */
        public NotationSpecification(int arity) {
            this(PRECEDENCE_DEFAULT, Notation.DEFAULT, arity);
        }
        private static final String prefixNonAssociativity(int arity) {
            char associativityArguments[] = new char[arity];
            Arrays.fill(associativityArguments, 'y');
            return "f" + new String(associativityArguments);
        }
                
        public boolean equals(Object o) {
            if (o instanceof NotationSpecification) {
                NotationSpecification b = (NotationSpecification) o;
                return precedence == b.precedence
                    && associativity.equals(b.associativity) && notation.equals(b.notation);
            }
            return false;
        }
        public int hashCode() {
            return precedence ^ Utility.hashCode(associativity) ^ Utility.hashCode(notation);
        }
        /**
         * Compares for precedence.
         * Compositors that bind stronger have a stronger precedence (the lower value)
         * and are ordered before weaker ones.
         * @return a negative value if this binds stronger than o (this&lt;o),
         *  zero if binding precedence is identical,
         *  a positive value if this binds weaker than a (this&gt;o).
         * @postconditions only <em>semi</em>-consistent with equals
         */
        public int compareTo(Object o) {
            return precedence - ((NotationSpecification) o).precedence;
        }
        public String toString() {
            return "[" + precedence + "," + associativity + "," + notation + "]";
        }
                
        /**
         * Get the precedence of the sign (with 1 being the highest precedence).
         */
        public int getPrecedence() {
            return precedence;
        }
        /**
         * Get the associativity specification of the sign.
         * Associativity is one of
         * <pre>
         * xf, yf, xfx, xfy, yfx, yfy, fy, fx
         * </pre>
         * (and alike for compositors of arbitrary arity).
         * Where
         * <ul>
         *   <li>f specifies the position of the compositor.</li>
         *   <li>x specifies the position of an argument with precedence of y &lt; the precedence of f.</li>
         *   <li>y specifies the position of an argument with precedence of y &le; the precedence of f.</li>
         * </ul>
         * <table>
         *   <caption>Quick overview of associativty specification</caption>
         *   <tr><th>specification</th> <th>effect</th></tr>
         *   <tr><td colspan="2">(unary) prefix notation</td></tr>
         *   <tr><td>fx</td> <td>unary prefix notation non-associative</td></tr>
         *   <tr><td>fy</td> <td>unary prefix notation associative</td></tr>
         *   <tr><td colspan="2">(binary) infix notation</td></tr>
         *   <tr><td>yfx</td> <td>left associative</td></tr>
         *   <tr><td>xfy</td> <td>right associative</td></tr>
         *   <tr><td>xfx</td> <td>non-associative</td></tr>
         * </table>
         * <p>
         * The compositor position specification used <em>must</em> match the concept of notation objects.
         * </p>
         */
        public String getAssociativity() {
            return associativity;
        }
        /**
         * Get the notation object to use for formatting.
         */
        public Notation getNotation() {
            return notation;
        }
        /**
         * Get the arity to use for formatting, i.e. the number of
         * argument places in the associativity description.
         */
        int arity() {
            return getAssociativity().length() - 1;
        }
    }

    /**
     * Asssociates compositors to their default notation (in precedence order).
     * @todo invariant sorted and without duplicates
     * @todo use LinkedHashMap instead to ensure sorting??
     * @todo couldn't we switch to storing this in a "Signature" or "Interpretation"?
     */
    private static Map/*_<Object, NotationSpecification>_*/ compositorNotation;

    static {
        compositorNotation = new HashMap();
        defaultNotation = BESTFIX;
    }
}
