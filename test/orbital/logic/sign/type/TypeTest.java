/**
 * @(#)TypeTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.imp;

import orbital.math.*;
import java.lang.Integer;
import orbital.util.*;

import junit.framework.*;

/**
 * A sample test case, testing Values.
 * @version 1.1, 2002-09-14
 */
public class TypeTest extends check.TestCase {
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(TypeTest.class);
    }
    protected void setUp() {
    }

    public void testTypeConstructors() {
	testTypeConstructorsWith(new Type[] {Types.UNIVERSAL});
	testTypeConstructorsWith(new Type[] {Types.TRUTH});
	testTypeConstructorsWith(new Type[] {Types.INDIVIDUAL});
	testTypeConstructorsWith(new Type[] {Types.NOTYPE});
	testTypeConstructorsWith(new Type[] {Types.ABSURD});
	testTypeConstructorsWith(new Type[] {Types.UNIVERSAL, Types.TRUTH});
	testTypeConstructorsWith(new Type[] {Types.UNIVERSAL, Types.UNIVERSAL});
	testTypeConstructorsWith(new Type[] {Types.UNIVERSAL, Types.INDIVIDUAL});
	testTypeConstructorsWith(new Type[] {Types.INDIVIDUAL, Types.INDIVIDUAL});
	testTypeConstructorsWith(new Type[] {Types.objectType(Double.class), Types.objectType(Integer.class)});
	testTypeConstructorsWith(new Type[] {Types.objectType(Double.class), Types.objectType(Integer.class), Types.objectType(Float.class)});
	testTypeConstructorsWith(new Type[] {Types.objectType(Double.class), Types.objectType(Integer.class), Types.objectType(Number.class)});
    }

    private  void testTypeConstructorsWith(Type a[]) {
	for (int i = 0; i < a.length; i++)
	    constructed(a[i], a[i]);
	if (a.length > 1) {
	    constructed(Types.map(a[0], a[1]), Types.map(a[0], a[1]));
	    constructed(Types.predicate(a[0]), Types.predicate(a[0]));
	}
	constructed(Types.product(a), Types.product(a));
	if (Types.product(a) != Types.ABSURD)
	    constructed(Types.predicate(Types.product(a)), Types.predicate(Types.product(a)));
	constructed(Types.inf(a), Types.inf(a));
	constructed(Types.sup(a), Types.sup(a));
	constructed(Types.collection(a[0]), Types.collection(a[0]));
	constructed(Types.set(a[0]), Types.set(a[0]));
	constructed(Types.list(a[0]), Types.list(a[0]));
	//constructed(Types.bag(a[0]), Types.bag(a[0]));

	// also test subtypes
	Type s, t;
	s = Types.set(a[0]);
	t = Types.collection(a[0]);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.list(a[0]);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
    }

    /**
     * Called with two types constructed in essentially the same way
     * (so one is at most a clone, or even the same canonical reference, of the other).
     */
    private void constructed(Type s, Type equalingS) {
	assertTrue( s.equals(s) , "x=x");
	assertTrue( s.compareTo(s) == 0, "x cmp x == 0");
	assertTrue( s.equals(equalingS) , "x=x'");
	assertTrue( s.compareTo(equalingS) == 0, "x cmp x' == 0");
    }

    public void testSameConstructorSubtype() {
	Type s, t;
	s = Types.predicate(Types.UNIVERSAL);
	t = Types.map(Types.INDIVIDUAL, Types.TRUTH);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.predicate(Types.UNIVERSAL);
	t = Types.predicate(Types.INDIVIDUAL);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.map(Types.objectType(String.class), Types.INDIVIDUAL);
	t = Types.map(Types.INDIVIDUAL, Types.INDIVIDUAL);
	assertTrue( compare(s,t) >= 0 , s + " >= " + t);
	
	s = Types.product(new Type[] {Types.TRUTH, Types.objectType(String.class)});
	t = Types.product(new Type[] {Types.TRUTH, Types.INDIVIDUAL});
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);

	s = Types.inf(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class), Types.INDIVIDUAL});
	t = Types.inf(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class)});
	assertTrue( s.equals(t) , s + " = " + t);
	s = Types.sup(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class), Types.INDIVIDUAL});
	t = Types.sup(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.INDIVIDUAL});
	assertTrue( s.equals(t) , s + " = " + t);
	s = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.INDIVIDUAL});
	//assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.inf(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.inf(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.INDIVIDUAL});
	//assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.sup(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.sup(new Type[] {Types.predicate(Types.INDIVIDUAL), Types.INDIVIDUAL});
	//assertTrue( compare(s,t) <= 0 , s + " =< " + t);

	
	s = Types.set(Types.objectType(String.class));
	t = Types.collection(Types.objectType(Comparable.class));
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.set(Types.objectType(String.class));
	t = Types.set(Types.objectType(Comparable.class));
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
    }

    public void testSupInfSubtype() {
	Type s, t;
	Type a[];
	a = new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class)};
	s = a[1];
	t = Types.sup(a);
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	a = new Type[] {Types.predicate(Types.INDIVIDUAL), Types.objectType(String.class)};
	s = a[1];
	t = Types.inf(a);
	assertTrue( compare(s,t) >= 0 , s + " >= " + t);

	// mixed
	s = Types.inf(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.INDIVIDUAL});
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
	s = Types.inf(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class)});
	assertTrue( compare(s,t) <= 0 , s + " =< " + t);
    }

    public void testSupInfNeutral() {
	Type s, t;
	s = Types.inf(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.inf(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class), Types.UNIVERSAL});
	assertTrue( s.equals(t) , s + " = " + t);
	s = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class)});
	t = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class), Types.ABSURD});
	assertTrue( s.equals(t) , s + " = " + t);
    }

    public void testSupInfAssociative() {
	Type s, t;
	s = Types.inf(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class), Types.inf(new Type[] {Types.objectType(Number.class), Types.objectType(RuntimeException.class)})});
	t = Types.inf(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class), Types.objectType(Number.class), Types.objectType(RuntimeException.class)});
	assertTrue( s.equals(t) , s + " = " + t);
	s = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class), Types.sup(new Type[] {Types.objectType(Number.class), Types.objectType(RuntimeException.class)})});
	t = Types.sup(new Type[] {Types.set(Types.INDIVIDUAL), Types.objectType(String.class), Types.objectType(Number.class), Types.objectType(RuntimeException.class)});
	assertTrue( s.equals(t) , s + " = " + t);
    }

    public void testSupInfEmptyConstructions() {
	Type s, t;
	s = Types.inf(new Type[] {});
	t = Types.UNIVERSAL;
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	s = Types.sup(new Type[] {});
	t = Types.ABSURD;
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	s = Types.product(new Type[] {});
	t = Types.ABSURD;
	//assertTrue( s.equals(t) && s == t, s + " = " + t); //?
    }

    public void testStrict() {
	Type s;
	final Type t = Types.ABSURD;
	s = Types.inf(new Type[] {Types.INDIVIDUAL, t});
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	//s = Types.sup(new Type[] {Types.INDIVIDUAL, t}); //@todo ?
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	s = Types.product(new Type[] {Types.INDIVIDUAL, t});
	assertTrue( s.equals(t) && s == t, s + " = " + t);
	//s = Types.map(Types.INDIVIDUAL, t);
	//assertTrue( s.equals(t) && s == t, s + " = " + t);
	//s = Types.map(t, Types.INDIVIDUAL);
	//assertTrue( s.equals(t) && s == t, s + " = " + t); //@todo ?
    }
    public void testCollectionsOfAbsurd() {
	Type s;
	final Type t = Types.ABSURD;
	s = Types.collection(t);
	assertTrue( !s.equals(t), s + " != " + t);
	s = Types.set(t);
	assertTrue( !s.equals(t), s + " != " + t);
	s = Types.list(t);
	assertTrue( !s.equals(t), s + " != " + t);
	//s = Types.bag(t);
	//assertTrue( !s.equals(t), s + " != " + t);
    }

    public void testDifferentConstructorSubtype() {
    }

    public void testIncomparableTypes() {
	Type s, t;
	s = Types.list(Types.UNIVERSAL);
	t = Types.map(Types.INDIVIDUAL, Types.TRUTH);
	assertTrue( !comparable(s,t) , s + " incomparable " + t);
	// non-extensional but intensional
	s = Types.set(Types.INDIVIDUAL);
	t = Types.predicate(Types.INDIVIDUAL);
	assertTrue( !comparable(s,t) , s + " incomparable " + t);
	s = Types.set(Types.UNIVERSAL);
	t = Types.predicate(Types.UNIVERSAL);
	assertTrue( !comparable(s,t) , s + " incomparable " + t);

	s = Types.list(Types.UNIVERSAL);
	t = Types.set(Types.UNIVERSAL);
	assertTrue( !comparable(s,t) , s + " incomparable " + t);
	s = Types.INDIVIDUAL;
	t = Types.map(Types.INDIVIDUAL, Types.TRUTH);
	assertTrue( !comparable(s,t) , s + " incomparable " + t);
	s = Types.INDIVIDUAL;
	t = Types.map(Types.INDIVIDUAL, Types.objectType(String.class));
	assertTrue( !comparable(s,t) , s + " incomparable " + t);

	s = Types.product(new Type[] {Types.INDIVIDUAL, Types.objectType(String.class)});
	t = Types.inf(new Type[] {Types.INDIVIDUAL, Types.objectType(String.class)});
	assertTrue( !comparable(s,t) , s + " incomparable " + t);
	s = Types.product(new Type[] {Types.INDIVIDUAL, Types.objectType(String.class)});
	t = Types.sup(new Type[] {Types.INDIVIDUAL, Types.objectType(String.class)});
	assertTrue( !comparable(s,t) , s + " incomparable " + t);
    }

    
    private boolean comparable(Type s, Type t) {
	try {
	    compare(s, t);
	    return true;
	} catch (IncomparableException incomparable) {
	    return false;
	}
    }
    
    private int compare(Type s, Type t) {
	assertTrue( s.compareTo(Types.UNIVERSAL) <= 0, "=<Universal");
	assertTrue( Types.UNIVERSAL.compareTo(s) >= 0, "Universal>=");
	assertTrue( s.compareTo(Types.ABSURD) >= 0, ">=Absurd");
	assertTrue( Types.ABSURD.compareTo(s) <= 0, "Absurd=<");

	assertTrue( s.compareTo(s) == 0 , "reflexive");
	assertTrue( s.equals(s) , "reflexive");
	assertTrue( MathUtilities.sign(s.compareTo(t)) == -MathUtilities.sign(t.compareTo(s)) , "antisymmetric");
	return s.compareTo(t);
    }

}
