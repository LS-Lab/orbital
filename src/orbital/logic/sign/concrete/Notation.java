/**
 * @(#)Notation.java 1.0 2000/06/27 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.functor;

import java.io.Serializable;

import java.io.ObjectStreamException;

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
import orbital.math.functional.Operations;
import orbital.util.Utility;

/**
 * Represents notational variants of functor applications.
 * @see <a href="{@docRoot}/DesignPatterns/enum.html">typesafe enum pattern</a>
 * @version 1.0, 2000/08/25
 * @author  Andr&eacute; Platzer
 * @internal typesafe enumeration pattern class to specify functor notation
 * @invariant a.equals(b) &hArr; a==b
 * @todo invariant 	&& sorted(functorNotation)
 */
public abstract class Notation implements Serializable, Comparable {
    private static final long serialVersionUID = -3071672372655194662L;
    private static final int PRECEDENCE_DEFAULT = 100;
    private static final int PRECEDENCE_HIGH = 499;

    /**
     * the name to display for this enum value
     * @serial
     */
    private final String	  name;

    /**
     * Ordinal of next enum value to be created
     */
    private static int		  nextOrdinal = 0;

    /**
     * Table of all canonical references to Notation classes
     */
    private static Notation[] values = new Notation[8];

    /**
     * Assign an ordinal to this enum value
     * @serial
     */
    private final int		  ordinal = nextOrdinal++;

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
     * Format functor with arguments in this notation.
     * @param functor the functor to apply, or the object describing the functor to apply.
     * @param arg the argument or array of arguments to apply the functor on, or the array of
     *  objects describing the corresponding arguments.
     * @return a good format of the functor applied on the specified arguments.
     */
    public abstract String format(Object functor, Object arg);

    public String toString() {
	return this.name;
    } 

    /**
     * Order imposed by ordinals according to the order of creation.
     * @post consistent with equals
     */
    public int compareTo(Object o) {
	return ordinal - ((Notation) o).ordinal;
    } 

    /**
     * Maintains the guarantee that all equal objects of the enumerated type are also identical.
     * @post a.equals(b) &hArr; if a==b.
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
    private Object readResolve() throws ObjectStreamException {
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
     * @pre notation != DEFAULT
     * @post getDefault() == notation
     */
    public static final void setDefault(Notation notation) {
	if (notation == DEFAULT)
	    throw new IllegalArgumentException("true notation expected, no synonym");
	defaultNotation = notation != null ? notation : PREFIX;
    } 

    // enumeration of functor notations

    /**
     * Specifies to use system default notation.
     * <p>
     * Functors with DEFAULT notation will use the current system default notation.</p>
     * @see #setDefault(Notation)
     */
    public static final Notation DEFAULT = new Notation("default") {
	    private static final long serialVersionUID = 5644030897053785928L;
	    public String format(Object functor, Object arg) {
		return getDefault().format(functor, arg);
	    } 
	};

    /**
     * Specifies automatical functor-dependant notation as registered.
     * <p>
     * Delegates to the registered notation.
     * Functors with registered default notation like +, -, *, /, ^ have a fixed precedence set.
     * All others are treated as prefix.</p>
     * @see #PREFIX
     * @see #INFIX
     * @see #POSTFIX
     */
    public static final Notation AUTO = new Notation("auto") {
	    private static final long serialVersionUID = -5725522528292770323L;
	    public String format(Object functor, Object arg) {
		NotationSpecification spec = notationOf(functor);
		if (spec != null)
		    return spec.notation.format(functor, arg);
		else
		    return PREFIX.format(functor, arg);
	    } 
	};
	
    /**
     * Specifies prefix notation <code>"f(a,b)"</code>.
     */
    public static final Notation PREFIX = new Notation("prefix") {
	    private static final long serialVersionUID = -5933847939038152414L;
	    public String format(Object functor, Object arg) {
		StringBuffer sb = new StringBuffer();
		if (functor != null) {
		    if (functor instanceof Functor.Composite)
			// descend composite functors with brackets
			sb.append("(" + format("", functor) + ")");
		    else
			sb.append(functor + "");
		}
		if (arg == null)
		    arg = getPureParameters(functor);
		if (arg != null) {
		    if (!hasCompactBrackets(functor))
			sb.append('(');
		    for (Iterator i = Utility.asCollection(arg).iterator(); i.hasNext(); )
			sb.append(i.next() + (i.hasNext() ? "," : ""));
		    if (!hasCompactBrackets(functor))
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
	    public String format(Object functor, Object arg_) {
		if (arg_ == null)
		    arg_ = getPureParameters(functor);
		Collection arg = Utility.asCollection(arg_);
		if (arg == null || arg.size() == 0)
		    return functor + "";
		StringBuffer sb = new StringBuffer();
		int			 precedence = precedenceOf(functor);

		// special handling for unary infix formatting
		if (arg.size() == 1)
		    if (functor instanceof Functor.Composite)
			// descend composite functors with brackets
			sb.append("(" + format("", functor) + ") °");
		    else
			sb.append(functor + " ° ");
		for (Iterator i = arg.iterator(); i.hasNext(); ) {
		    sb.append(i.next());
		    if (i.hasNext()) {
			if (!isHigh(precedence))
			    sb.append(' ');
			if (functor instanceof Functor.Composite)
			    // descend composite functors with brackets
			    sb.append("(" + format("", functor) + ")");
			else
			    sb.append(functor);
			if (!isHigh(precedence))
			    sb.append(' ');
		    }
		} 
		return sb.toString();
	    } 
	};

    /**
     * Specifies best notation <code>"a*(b+f(c)) + d"</code> inserting brackets whenever necessary.
     * <p>
     * Decomposes functors into its components whenever possible
     * building a function tree to optimize formatting.
     * This more sophisticated notation is aware of the registered default notation
     * and will only insert brackets for infix functors when necessary.</p>
     * @see #AUTO
     * @xxx if the functor is itself composite then descend formatting it (with accurate brackets) as well.
     */
    public static final Notation BESTFIX = new Notation("bestfix") {
	    private static final long serialVersionUID = 2361099498303659521L;
	    public String format(Object functor, Object arg_) {
		if (!(functor instanceof Functor))
		    return PREFIX.format(functor, arg_);
		//@todo explicitly work on graph-structure induced by Functor.Composite without importing orbital.util.graph for this sole reason
		// however, how to briefly append arguments to the functor object (just for formatting), then?
		Node root;
		if (arg_ == null)
		    arg_ = getPureParameters(functor);
		Collection arg = Utility.asCollection(arg_);
		if (arg == null || arg.size() == 0)
		    root = functionTree(functor);
		else {
		    root = new ListTree.TreeNode(functor, functor.toString());
		    // root.addAll(Functionals.map(functionTree, arg));
		    for (Iterator i = arg.iterator(); i.hasNext(); )
			root.add(functionTree(i.next()));
		} 
		return new Function/*<Node, String>*/() {
			private StringBuffer sb;
			public Object/*>String<*/ apply(Object/*>Node<*/ root) {
			    return visit((Node) root);
			} 
			private final String visit(Node node) {
			    if (node.isLeaf())
				return ((KeyValuePair) node).getValue() + "";
			    NotationSpecification spec = notationOf(((KeyValuePair) node).getKey());
			    int	   apos = 0;

			    // will contain formatted arguments
			    String argDesc[] = new String[node.getEdgeCount()];
			    int	   i = 0;
			    for (Iterator it = node.edges(); it.hasNext(); i++) {

				// ignore functor position in association format specifier
				if (spec != null && spec.associativity.charAt(apos) == 'f')
				    apos++;
				Node                  n = (Node) it.next();
				NotationSpecification childSpec = notationOf(((KeyValuePair) n).getKey());
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
	    public String format(Object functor, Object arg) {
		StringBuffer sb = new StringBuffer();
		if (arg == null)
		    arg = getPureParameters(functor);
		if (arg != null) {
		    if (!hasCompactBrackets(functor))
			sb.append('(');
		    for (Iterator i = Utility.asCollection(arg).iterator(); i.hasNext(); )
			sb.append(i.next() + (i.hasNext() ? "," : ""));
		    if (!hasCompactBrackets(functor))
			sb.append(')');
		} 
		if (functor instanceof Functor.Composite)
		    // descend composite functors with brackets
		    sb.append("(" + format("", functor) + ")");
		else
		    sb.append(functor + "");
		return sb.toString();
	    } 
	};


    // Utilities

    /**
     * Get the pure formal parameters of a functor.
     * @return an argument array filled with the pure arguments #0, #1, #2, ... #n upto the arity n of functor.
     * @todo how to achieve pure functions like "*" without any explicit parameters being printed as #0*#1 instead of * or x*y?
     */
    private static Object[] getPureParameters(Object functor) {
	if (functor instanceof Functor) {
	    if (functor instanceof VoidFunction)
		return null;
	    else if (functor instanceof Function)
		return new String[] {"#0"};
	    else if (functor instanceof BinaryFunction)
		return new String[] {"#0", "#1"};
	    else try {
		// use arity of functor specification
		Functor.Specification spec = Functor.Specification.getSpecification((Functor) functor);
		String[] pure = new String[spec.arity()];
		for (int i = 0; i < pure.length; i++)
		    pure[i] = "#" + i;
		return pure;
	    }
	    catch (IntrospectionException trying) {return null;}
	} else
	    return null;
    }

    /**
     * Returns the functor belonging to a specified notation and arguments, if registered (experimental).
     * <p>
     * The most usual use of the arguments array is to check for its length to distinguish
     * unary minus '-' from binary subtraction '-'. But in principle, type checking could be
     * required as well.</p>
     * @param notation the notation string of the functor.
     * @param arg the arguments the functor belonging to the notation is called with.
     * @return the functor belonging to the notation if registered, or <tt>null</tt>.
     * @see #notationOf(Object)
     * @todo change arguments to (String, Object) as well?
     */
    public static Functor functorOf(String notation, Object[] arg) {
	assert arg.length <= 2 : "functor notations are currently used for at most 2 arguments";
	for (Iterator/*_<Functor>_*/ i = functorNotation.keySet().iterator(); i.hasNext(); ) {
	    Functor functor = (Functor/*__*/) i.next();
	    if (arg.length == 1 && !((functor instanceof Function) || (functor instanceof Predicate)))
		continue;
	    if (arg.length == 2 && !((functor instanceof BinaryFunction) || (functor instanceof BinaryPredicate)))
		continue;
	    if (notation.equals(functor.toString()))
		return functor;
	}
	return null;
    }

    /**
     * Get the default notation specification of a functor.
     * @return the default notation specification of the functor, or <code>null</code> if not set.
     * @see #functorOf(String, Object[])
     */
    protected static NotationSpecification notationOf(Object functor) {
	return (NotationSpecification/*__*/) functorNotation.get(functor);
    } 

    /**
     * Get the precedence of a functor (with 1 being the highest precedence).
     * @return the precedence of the functor, or 0 if not set.
     * @see #notationOf(Object)
     */
    protected static int precedenceOf(Object functor) {
	NotationSpecification spec = notationOf(functor);
	return spec != null ? spec.precedence : 0;
    } 

    /**
     * Sets the default notation specification for a functor.
     * @param f the functor whose notation specification to set.
     * @param spec the notation specification to set as default for the functor f.
     * @return <code>true</code> if the default notation specification for the functor f
     *  changed as a result of the call.
     */
    public static boolean setNotation(Functor f, NotationSpecification spec) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("setStatic.notationSpecification"));
	} 
	return functorNotation.put(f, spec) != null;
    }
    /**
     * Remove any default notation specifications for a functor.
     * @param f the functor whose notation specification to remove.
     * @return previous value associated with specified functor, or <code>null</code> if there was no mapping for functor.
     */
    public static NotationSpecification removeNotation(Functor f) {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPermission(new RuntimePermission("setStatic.notationSpecification"));
	} 
	return (NotationSpecification/*__*/) functorNotation.remove(f);
    }
	
    /**
     * Whether the functor has a default notation defined and is unary.
     */
    private static boolean hasCompactBrackets(Object functor) {
	// distinguish unary from binary registered functors
	NotationSpecification spec = notationOf(functor);
	return spec != null ? spec.associativity.length() == 2 : false;
    } 
    
    /**
     * Whether the specified precedence is "high" such that its functor is formated
     * without separators.
     */
    static boolean isHigh(int precedence) {
	return precedence <= PRECEDENCE_HIGH;
    } 

    // Utility methods
	
    /**
     * Creates a function tree view of a functor by decomposing it into its components.
     * @see Functor.Composite
     */
    private static Node functionTree(Object f) {
	if (!(f instanceof Functor.Composite))
	    return new ListTree.TreeNode(f, f + "");
	Functor.Composite c = (Functor.Composite) f;
	Functor			  compositor = c.getCompositor();
	Collection		  components = Utility.asCollection(c.getComponent());
	Node			  n = new ListTree.TreeNode(compositor, compositor + "");
	if (components == null)
	    throw new NullPointerException(f + " of " + f.getClass() + " has compositor " + compositor + " and components " + components);
	// n.addAll(Functionals.map(functionTree, components));
	for (Iterator i = components.iterator(); i.hasNext(); )
	    n.add(functionTree(i.next()));
	return n;
    } 

    /**
     * Contains the specification of the default notation for a functor.
     * @invariant precedence > 0 && associativity and notation match
     * @version 1.0, 2000/08/25
     * @author  Andr&eacute; Platzer
     */
    public static class NotationSpecification implements Comparable, Serializable {
	private static final long serialVersionUID = -8249931256922519844L;
	/**
	 * The precedence of the functor (with 1 being the highest precedence).
	 * @serial
	 */
	protected int precedence;
	/**
	 * The associativity specification of the functor.
    	 * Associativity is one of
    	 * <pre>
    	 * xf, yf, xfx, xfy, yfx, yfy, fy, fx
    	 * </pre>
    	 * (and alike for functors of arbitrary arity).
    	 * Where
    	 * <ul>
    	 *   <li>f specifies the position of the functor.</li>
    	 *   <li>x specifies the position of an argument with precedence of y &lt; the precedence of f.</li>
    	 *   <li>y specifies the position of an argument with precedence of y &le; the precedence of f.</li>
    	 * </ul>
    	 * <table>
    	 *   <caption>Quick overview of associativty specification</caption>
    	 *   <tr><th>specification</th> <th>effect</th></tr>
    	 *   <tr><td colspan="2">prefix notation</td></tr>
    	 *   <tr><td>fx</td> <td>unary prefix notation non-associative</td></tr>
    	 *   <tr><td>fy</td> <td>unary prefix notation associative</td></tr>
    	 *   <tr><td colspan="2">infix notation</td></tr>
    	 *   <tr><td>yfx</td> <td>left associative</td></tr>
    	 *   <tr><td>xfy</td> <td>right associative</td></tr>
    	 *   <tr><td>xfx</td> <td>non-associative</td></tr>
    	 * </table>
    	 * The functor position specification used <em>must</em> match the concept of notation objects.
    	 * </p>
	 * @serial
	 */
	protected String associativity;
	/**
	 * The notation object to use for formatting.
	 * @serial
	 */
	protected Notation notation;
	//TODO: generalize to specify arity as well, to distinguish -/2 from -/1, from -/7, or rely on Functor.Specification for that?
	//TODO: do we even need to introduce protected String functor; ?
	/**
	 * Create a specification of a functor's notation.
	 * @see #associativity
	 */
	public NotationSpecification(int precedence, String associativity, Notation notation) {
	    this.precedence = precedence;
	    this.associativity = associativity;
	    this.notation = notation;
	}
	/**
	 * Create a specification of a functor's notation.
	 * @see #associativity
	 */
	public NotationSpecification(int precedence, String associativity, Notation notation, int arity) {
	    this(precedence, associativity, notation);
	    assert arity == associativity.length() : "associativity description must match arity";
	}
	/**
	 * Create a specification of a functor's notation with automatic notation resolution.
	 * <p>
	 * This constructor will determine the notation object to use for formatting
	 * according to the associativity string which must be correct, then!
	 * </p>
	 * @see #associativity
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
	 * Create a very basic default notation specification of a functor's notation.
	 * <p>
	 * The default notation specification is
	 * non-associative system {@link Notation#DEFAULT default notation} of very strong precedence.
	 * </p>
	 */
	public NotationSpecification(int arity) {
	    this(PRECEDENCE_DEFAULT, prefixNonAssociativity(arity), Notation.DEFAULT);
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
	 * Functors that bind stronger have a stronger precedence (the lower value)
	 * and are ordered before weaker ones.
	 * @return a negative value if this binds stronger than o (this&lt;o),
	 *  zero if binding precedence is identical,
	 *  a positive value if this binds weaker than a (this&gt;o).
	 * @post only <em>semi</em>-consistent with equals
	 */
	public int compareTo(Object o) {
	    return precedence - ((NotationSpecification) o).precedence;
	}
	public String toString() {
	    return "[" + precedence + "," + associativity + "," + notation + "]";
	}
		
	public int getPrecedence() {
	    return precedence;
	}
	public String getAssociativity() {
	    return associativity;
	}
	public Notation getNotation() {
	    return notation;
	}
    }

    /**
     * Asssociates functors to their default notation (in precedence order).
     * @todo invariant sorted and without duplicates
     * @todo use LinkedHashMap instead to ensure sorting??
     */
    private static Map/*_<Functor, NotationSpecification>_*/		functorNotation;

    /**
     * Contains initial functors who have a default notation set, in precedence order.
     * In the same order as {@link #initialFunctorNotation}
     * @invariant sorted, i.e. precedenceOf[i] < precedenceOf[i+1]
     */
    //TODO: use List(new KeyValuePair()) instead of functorList<->functorNotation synchronization
    private static final Functor[]		initialFunctorList       = {
	Operations.inverse,						// "^-1",
	Operations.power,						// "^",
	Operations.times, Operations.divide,	// "*", "/"
	Operations.minus,						// "-"/1,
	Operations.plus, Operations.subtract,	// "+", "-"/2

	Predicates.equal,						// "=="
	Predicates.unequal,						// "!="
	Predicates.greater,						// ">"
	Predicates.less,						// "<"
	Predicates.greaterEqual,				// ">="
	Predicates.lessEqual,					// "=<"
    };

    /**
     * Contains notation specifications of the initial registered functors.
     * In the same order as {@link #initialFunctorList}.
     * @invariant in the same order as initialFunctorList
     * @TODO: + and * could have yfy as well? Would avoid 1+(2+3)
     */
    private static final NotationSpecification[] initialFunctorNotation = {
	new NotationSpecification(195, "xf", POSTFIX),
	new NotationSpecification(200, "xfy", INFIX),
	new NotationSpecification(400, "yfx", INFIX), new NotationSpecification(400, "yfx", INFIX),
	new NotationSpecification(500, "fx", PREFIX),
	new NotationSpecification(500, "yfx", INFIX), new NotationSpecification(500, "yfx", INFIX),

	new NotationSpecification(700, "xfx", INFIX),
	new NotationSpecification(700, "xfx", INFIX),
	new NotationSpecification(700, "xfx", INFIX),
	new NotationSpecification(700, "xfx", INFIX),
	new NotationSpecification(700, "xfx", INFIX),
	new NotationSpecification(700, "xfx", INFIX),
    };
	
    static {
	functorNotation = new HashMap();
	assert initialFunctorNotation.length == initialFunctorList.length : "initial functor containers have equal lengths";
	for (int i = 0; i < initialFunctorNotation.length; i++)
	    functorNotation.put(initialFunctorList[i], initialFunctorNotation[i]);
	defaultNotation = BESTFIX;
    }
}
