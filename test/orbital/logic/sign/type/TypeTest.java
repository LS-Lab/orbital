/**
 * @(#)TypeTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.type;

import orbital.math.*;
import java.lang.Integer;
import orbital.util.*;
import java.util.*;
import orbital.logic.functor.*;

import junit.framework.*;

/**
 * A sample test case, testing Values.
 * @version 1.1, 2002-09-14
 */
public class TypeTest extends check.TestCase {
    /**
     * the type system to test.
     */
    private TypeSystem typeSystem;
    private Type INDIVIDUAL;
    private Type TRUTH;
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(TypeTest.class);
    }
    protected void setUp() {
	typeSystem = Types.getDefault();
	INDIVIDUAL = Types.INDIVIDUAL;
	TRUTH = Types.TRUTH;
    }

    // hook into these methods all additional tests on all types occuring anywhere in this test
    
    protected void testTypesAdditionally(Type[] types) {
	testSupInfNeutral(types);
	for (int i = 0; i + 1 < types.length; i++)
	    testSupInfSubtypeCaseCorrelation(types[i], types[i+1]);
    }

    protected void testTypeAdditionally(Type type) {
	testSupInfIdempotent(type);
    }

    //

    public void testTypeConstructors() {
	testTypeConstructorsWith(new Type[] {typeSystem.UNIVERSAL()}, true);
	testTypeConstructorsWith(new Type[] {TRUTH}, true);
	testTypeConstructorsWith(new Type[] {INDIVIDUAL}, true);
	testTypeConstructorsWith(new Type[] {typeSystem.NOTYPE()}, true);
	testTypeConstructorsWith(new Type[] {typeSystem.ABSURD()}, true);
	testTypeConstructorsWith(new Type[] {typeSystem.UNIVERSAL(), TRUTH}, true);
	testTypeConstructorsWith(new Type[] {typeSystem.UNIVERSAL(), typeSystem.UNIVERSAL()}, true);
	testTypeConstructorsWith(new Type[] {typeSystem.UNIVERSAL(), INDIVIDUAL}, true);
	testTypeConstructorsWith(new Type[] {INDIVIDUAL, INDIVIDUAL}, true);
	testTypeConstructorsWith(new Type[] {typeSystem.objectType(Double.class), typeSystem.objectType(Integer.class)}, false);
	testTypeConstructorsWith(new Type[] {typeSystem.objectType(Double.class), typeSystem.objectType(Integer.class), typeSystem.objectType(Float.class)}, false);
	testTypeConstructorsWith(new Type[] {typeSystem.objectType(Double.class), typeSystem.objectType(Integer.class), typeSystem.objectType(Number.class)}, true);
    }

    private void testTypeConstructorsWith(Type a[], boolean specialCase) {
	for (int i = 0; i < a.length; i++)
	    constructed(a[i], a[i]);
	if (a.length > 1) {
	    constructed(typeSystem.map(a[0], a[1]), typeSystem.map(a[0], a[1]),
			typeSystem.map(), new Type[] {a[0], a[1]}, specialCase);
	    constructed(typeSystem.predicate(a[0]), typeSystem.predicate(a[0]),
			typeSystem.map(), new Type[] {a[0], Types.TRUTH}, specialCase);
	}
	constructed(typeSystem.product(a), typeSystem.product(a),
		    typeSystem.product(), a, specialCase || a.length <= 1);
	if (typeSystem.product(a) != typeSystem.ABSURD())
	    constructed(typeSystem.predicate(typeSystem.product(a)), typeSystem.predicate(typeSystem.product(a)),
			typeSystem.map(), new Type[] {typeSystem.product(a), Types.TRUTH}, specialCase);
	constructed(typeSystem.inf(a), typeSystem.inf(a),
		    typeSystem.inf(), null, specialCase);
	constructed(typeSystem.sup(a), typeSystem.sup(a),
		    typeSystem.sup(), null, specialCase);
	constructed(typeSystem.collection(a[0]), typeSystem.collection(a[0]),
		    typeSystem.collection(), a[0], specialCase);
	constructed(typeSystem.set(a[0]), typeSystem.set(a[0]),
		    typeSystem.set(), a[0], specialCase);
	constructed(typeSystem.list(a[0]), typeSystem.list(a[0]),
		    typeSystem.list(), a[0], specialCase);
	//constructed(typeSystem.bag(a[0]), typeSystem.bag(a[0]));
	// typeSystem.bag(), a[0]);

	// also test subtypes
	Type s, t;
	s = typeSystem.set(a[0]);
	t = typeSystem.collection(a[0]);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.list(a[0]);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);

	testTypesAdditionally(a);
    }

    /**
     * Called with two types constructed in essentially the same way
     * (so one is at most a clone, or even the same canonical reference, of the other).
     * Both typeConstructor and typeComponent <code>null</code> indicates (here) that
     * s is expected not to be composite.
     * @param typeConstructor expected type constructor of s, any if null.
     * @param typeComponent expected type component of s, any if null.
     */
    private void constructed(Type s, Type equalingS,
			     Functor/*<Type,Type>*/ typeConstructor, Object typeComponent,
			     boolean specialCase) {
	constructed(s, equalingS);
	if (typeConstructor == null && typeComponent == null)
	    assertTrue( !(s instanceof Type.Composite) , s + " not composite");
	else if (!specialCase) {
	    assertTrue( s instanceof Type.Composite , s + " composite");
	    Type.Composite c = (Type.Composite) s;
	    assertTrue (typeConstructor == null || Utility.equals(typeConstructor, c.getCompositor()) , s + " has compositor " + c.getCompositor() + " equalling " + typeConstructor);
	    assertTrue (typeComponent == null || Utility.equalsAll(typeComponent, c.getComponent()) , s + " has component " + c.getComponent() + " equalling " + typeComponent);
	}
	if (typeConstructor instanceof Function && typeComponent != null)
	    assertEquals( ((Function)typeConstructor).apply(typeComponent), s);
    }
    private void constructed(Type s, Type equalingS) {
	assertTrue( s.equals(s) , "x=x");
	assertTrue( s.compareTo(s) == 0, "x cmp x == 0");
	assertTrue( s.equals(equalingS) , "x=x'");
	assertTrue( s.compareTo(equalingS) == 0 , "x cmp x' == 0");
	assertEquals( s, equalingS);
	testTypeAdditionally(s);
    }

    public void testSameConstructorSubtype() {
	Type s, t;
	s = typeSystem.predicate(typeSystem.UNIVERSAL());
	t = typeSystem.map(INDIVIDUAL, TRUTH);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.predicate(typeSystem.UNIVERSAL());
	t = typeSystem.predicate(INDIVIDUAL);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.map(typeSystem.objectType(String.class), INDIVIDUAL);
	t = typeSystem.map(INDIVIDUAL, INDIVIDUAL);
	assertTrue( compare(s,t) >= 0 , s + " >= " + t);
	
	s = typeSystem.product(new Type[] {TRUTH, typeSystem.objectType(String.class)});
	t = typeSystem.product(new Type[] {TRUTH, INDIVIDUAL});
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);

	s = typeSystem.inf(new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class), INDIVIDUAL});
	t = typeSystem.inf(new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class)});
	assertTrue( s.equals(t) , s + " = " + t);
	s = typeSystem.sup(new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class), INDIVIDUAL});
	t = typeSystem.sup(new Type[] {typeSystem.predicate(INDIVIDUAL), INDIVIDUAL});
	assertTrue( s.equals(t) , s + " = " + t);
	s = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), INDIVIDUAL});
	//assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.inf(new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.inf(new Type[] {typeSystem.predicate(INDIVIDUAL), INDIVIDUAL});
	//assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.sup(new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.sup(new Type[] {typeSystem.predicate(INDIVIDUAL), INDIVIDUAL});
	//assertTrue( compare(s,t) <= 0 , s + " =< " + t);

	
	s = typeSystem.set(typeSystem.objectType(String.class));
	t = typeSystem.collection(typeSystem.objectType(Comparable.class));
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.set(typeSystem.objectType(String.class));
	t = typeSystem.set(typeSystem.objectType(Comparable.class));
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
    }

    public void testSupInfSubtype() {
	Type s, t;
	Type a[];
	a = new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class)};
	s = a[1];
	t = typeSystem.sup(a);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	a = new Type[] {typeSystem.predicate(INDIVIDUAL), typeSystem.objectType(String.class)};
	s = a[1];
	t = typeSystem.inf(a);
	assertTrue( compare(s,t) >= 0 , s + " >= " + t);

	// mixed
	s = typeSystem.inf(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), INDIVIDUAL});
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = typeSystem.inf(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class)});
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
    }

    private void testSupInfNeutral(Type[] types) {
	Type s, t;
	s = typeSystem.inf(types);
	t = typeSystem.inf((Type[]) Setops.union(Arrays.asList(types), Collections.singletonList(typeSystem.UNIVERSAL())).toArray(new Type[0]));
	assertTrue( s.equals(t) , s + " = " + t);
	s = typeSystem.sup(types);
	t = typeSystem.sup((Type[]) Setops.union(Arrays.asList(types), Collections.singletonList(typeSystem.ABSURD())).toArray(new Type[0]));
	assertTrue( s.equals(t) , s + " = " + t);
    }

    public void testSupInfNeutral() {
	Type s, t;
	s = typeSystem.inf(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.inf(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class), typeSystem.UNIVERSAL()});
	assertTrue( s.equals(t) , s + " = " + t);
	s = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class)});
	t = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class), typeSystem.ABSURD()});
	assertTrue( s.equals(t) , s + " = " + t);
    }
    
    public void testSupInfAssociative() {
	Type s, t;
	s = typeSystem.inf(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class), typeSystem.inf(new Type[] {typeSystem.objectType(Number.class), typeSystem.objectType(RuntimeException.class)})});
	t = typeSystem.inf(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class), typeSystem.objectType(Number.class), typeSystem.objectType(RuntimeException.class)});
	assertTrue( s.equals(t) , s + " = " + t);
	s = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class), typeSystem.sup(new Type[] {typeSystem.objectType(Number.class), typeSystem.objectType(RuntimeException.class)})});
	t = typeSystem.sup(new Type[] {typeSystem.set(INDIVIDUAL), typeSystem.objectType(String.class), typeSystem.objectType(Number.class), typeSystem.objectType(RuntimeException.class)});
	assertTrue( s.equals(t) , s + " = " + t);
    }

    //@todo introduce public void testSupInfDistributive()

    private void testSupInfIdempotent(Type tau) {
	Type s, t;
	s = typeSystem.inf(new Type[] {tau, tau});
	t = tau;
	assertTrue( s.equals(t) , s + " = " + t);
	s = typeSystem.sup(new Type[] {tau, tau});
	t = tau;
	assertTrue( s.equals(t) , s + " = " + t);
    }

    private void testSupInfSubtypeCaseCorrelation(Type sigma, Type tau) {
	assertTrue( sigma.subtypeOf(tau) == typeSystem.sup(new Type[] {sigma, tau}).equals(tau) , "TypeSystem.sup@postconditions " + sigma + "=<" + tau + " iff " + typeSystem.sup(new Type[] {sigma, tau}) + " = " + sigma + " sup " + tau + " = " + tau);
	assertTrue( sigma.subtypeOf(tau) == typeSystem.inf(new Type[] {sigma, tau}).equals(sigma) , "TypeSystem.sup@postconditions " + sigma + "=<" + tau + " iff " + typeSystem.sup(new Type[] {sigma, tau}) + " = " + sigma + " sup " + tau + " = " + sigma);
	// swap types
	Type t = sigma;
	sigma = tau;
	tau = t;
	// and check the other way around
	assertTrue( sigma.subtypeOf(tau) == typeSystem.sup(new Type[] {sigma, tau}).equals(tau) , "TypeSystem.sup@postconditions " + sigma + "=<" + tau + " iff " + typeSystem.sup(new Type[] {sigma, tau}) + " = " + sigma + " sup " + tau + " = " + tau);
	assertTrue( sigma.subtypeOf(tau) == typeSystem.inf(new Type[] {sigma, tau}).equals(sigma) , "TypeSystem.sup@postconditions " + sigma + "=<" + tau + " iff " + typeSystem.sup(new Type[] {sigma, tau}) + " = " + sigma + " sup " + tau + " = " + sigma);
    }

    public void testSupInfEmptyConstructions() {
	Type s, t;
	s = typeSystem.inf(new Type[] {});
	t = typeSystem.UNIVERSAL();
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	s = typeSystem.sup(new Type[] {});
	t = typeSystem.ABSURD();
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	s = typeSystem.product(new Type[] {});
	t = typeSystem.ABSURD();
	//assertTrue( s.equals(t) && s == t, s + " = " + t); //?
    }

    public void testStrict() {
	Type s;
	final Type t = typeSystem.ABSURD();
	s = typeSystem.inf(new Type[] {INDIVIDUAL, t});
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	//s = typeSystem.sup(new Type[] {INDIVIDUAL, t}); //@todo ?
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	s = typeSystem.product(new Type[] {INDIVIDUAL, t});
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	//s = typeSystem.map(INDIVIDUAL, t);
	//assertTrue( s.equals(t) && s == t, s + " = " + t);
	//s = typeSystem.map(t, INDIVIDUAL);
	//assertTrue( s.equals(t) && s == t, s + " = " + t); //@todo ?
    }
    public void testCollectionsOfAbsurd() {
	Type s;
	final Type t = typeSystem.ABSURD();
	s = typeSystem.collection(t);
	assertTrue( !s.equals(t), s + " != " + t);
	s = typeSystem.set(t);
	assertTrue( !s.equals(t), s + " != " + t);
	s = typeSystem.list(t);
	assertTrue( !s.equals(t), s + " != " + t);
	//s = typeSystem.bag(t);
	//assertTrue( !s.equals(t), s + " != " + t);
    }

    public void testDifferentConstructorSubtype() {
    }

    public void testIncomparableTypes() {
	Type s, t;
	s = typeSystem.list(typeSystem.UNIVERSAL());
	t = typeSystem.map(INDIVIDUAL, TRUTH);
	assertComparable(s,t, false);
	// non-extensional but intensional
	s = typeSystem.set(INDIVIDUAL);
	t = typeSystem.predicate(INDIVIDUAL);
	assertComparable(s,t, false);
	s = typeSystem.set(typeSystem.UNIVERSAL());
	t = typeSystem.predicate(typeSystem.UNIVERSAL());
	assertComparable(s,t, false);

	s = typeSystem.list(typeSystem.UNIVERSAL());
	t = typeSystem.set(typeSystem.UNIVERSAL());
	assertComparable(s,t, false);
	s = INDIVIDUAL;
	t = typeSystem.map(INDIVIDUAL, TRUTH);
	assertComparable(s,t, false);
	s = INDIVIDUAL;
	t = typeSystem.map(INDIVIDUAL, typeSystem.objectType(String.class));
	assertComparable(s,t, false);

	s = typeSystem.product(new Type[] {INDIVIDUAL, typeSystem.objectType(String.class)});
	t = typeSystem.inf(new Type[] {INDIVIDUAL, typeSystem.objectType(String.class)});
	assertComparable(s,t, false);
	s = typeSystem.product(new Type[] {INDIVIDUAL, typeSystem.objectType(String.class)});
	t = typeSystem.sup(new Type[] {INDIVIDUAL, typeSystem.objectType(String.class)});
	assertComparable(s,t, false);
    }

    public void testMetaTypes() {
	Type s, t;
	s = typeSystem.TYPE();
	constructed(s, s,
		    null, null, false);
	t = TRUTH;
	assertComparable(s,t, false);
	s = typeSystem.TYPE();
	t = INDIVIDUAL;
	assertComparable(s,t, false);
	s = typeSystem.TYPE();
	t = typeSystem.objectType(java.lang.Number.class);
	assertComparable(s,t, false);
    }
    
    
    private void assertComparable(Type s, Type t, boolean comparable) {
	final String desc = comparable ? "comparable" : "incomparable";
	try {
	    int cmp = compare(s, t);
	    assertTrue(comparable , s + " " + desc + " " + t + "\n\tcompared to " + s + " " + (cmp < 0 ? "<" : cmp > 0 ? ">" : "=") + " " + t);
	} catch (IncomparableException incomparable) {
	    assertTrue(!comparable , s + " " + desc + " " + t + "\n\tincomparable");
	}
    }
    
    private int compare(Type s, Type t) {
	assertTrue( s.compareTo(typeSystem.UNIVERSAL()) <= 0, "=<Universal");
	assertTrue( s.equals(typeSystem.UNIVERSAL()) | s.compareTo(typeSystem.UNIVERSAL()) < 0, "<Universal or =Universal");
	assertTrue( typeSystem.UNIVERSAL().compareTo(s) >= 0, "Universal>=");
	assertTrue( s.equals(typeSystem.UNIVERSAL()) | typeSystem.UNIVERSAL().compareTo(s) > 0, "Universal> or Universal=");
	assertTrue( s.compareTo(typeSystem.ABSURD()) >= 0, ">=Absurd");
	assertTrue( s.equals(typeSystem.ABSURD()) | s.compareTo(typeSystem.ABSURD()) > 0, ">Absurd or =Absurd");
	assertTrue( typeSystem.ABSURD().compareTo(s) <= 0, "Absurd=<");
	assertTrue( s.equals(typeSystem.ABSURD()) | typeSystem.ABSURD().compareTo(s) <= 0, "Absurd< or Absurd=");

	assertTrue( s.compareTo(s) == 0 , "reflexive");
	assertTrue( s.equals(s) , "reflexive");

	// may throw IncomparableException
	assertTrue( MathUtilities.sign(s.compareTo(t)) == -MathUtilities.sign(t.compareTo(s)) , "antisymmetric");
	testTypeAdditionally(s);
	testTypesAdditionally(new Type[] {s, t});
	testSupInfSubtypeCaseCorrelation(s, t);
	return s.compareTo(t);
    }

}
