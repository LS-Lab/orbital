/**
 * @(#)SubstitutionTest.java 1.1 2002-09-14 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.trs;

import orbital.logic.sign.type.*;
import orbital.logic.sign.*;
import orbital.logic.sign.ParseException;
import java.util.*;
import orbital.moon.logic.*;

import junit.framework.*;

/**
 * A sample test case, testing substitutions.
 * @version 1.1, 2002-09-14
 */
public class SubstitutionTest extends check.TestCase {
    private TypeSystem typeSystem;
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	return new TestSuite(SubstitutionTest.class);
    }
    public void setUp() {
	typeSystem = Types.getDefault();
    }

    public void testNonTypeConformSubstitution() {
	Type s = typeSystem.objectType(Float.class);
	Type t = typeSystem.objectType(String.class);
	try {
	    Substitution sigma =
		Substitutions.getInstance(Collections.singleton(
								Substitutions.createExactMatcher(
												 new SymbolBase("a", s),
												 new SymbolBase("b", t)
												 )
								));
	    fail(TypeException.class + " expected for nonconform substitution " + sigma);
	}
	catch (TypeException incompatibleType) {
	    System.out.println(incompatibleType);
	    assertEquals("required type", incompatibleType.getRequired(), s); 
	    assertEquals("occurred type", incompatibleType.getOccurred(), t); 
	}
    }

    public void testMultiReplacementSubstitution() {
	Type s = typeSystem.objectType(Float.class);
	try {
	    Substitution sigma =
		Substitutions.getInstance(Arrays.asList(new Object[] {
		    Substitutions.createExactMatcher(
						     new SymbolBase("a", s),
						     new SymbolBase("b", s)
						     ),
		    Substitutions.createExactMatcher(
						     new SymbolBase("a", s),
						     new SymbolBase("c", s)
						     )
		}));
	    boolean assertionEnabled = false;
	    assert (assertionEnabled = true) == true;
	    assertTrue(!assertionEnabled, "multiple matching replacements in " + sigma);
	}
	catch (AssertionError multipleMatchingReplacements) {
	}
    }

    public void testComposition() throws ParseException {
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
 	Substitution sigma =
	    Substitutions.getInstance(Arrays.asList(new Object[] {
		Substitutions.createExactMatcher(
						 syntax.createTerm("x"),
						 syntax.createTerm("a")
						 ),
		Substitutions.createExactMatcher(
						 syntax.createTerm("y"),
						 syntax.createTerm("b")
						 )
	    }));
	Substitution tau =
	    Substitutions.getInstance(Arrays.asList(new Object[] {
		Substitutions.createExactMatcher(
						 syntax.createTerm("x"),
						 syntax.createTerm("c")
						 ),
		Substitutions.createExactMatcher(
						 syntax.createTerm("z"),
						 syntax.createTerm("f(x)")
						 )
	    }));
	Substitution c = Substitutions.compose(sigma, tau);
	assertEquals("expected composition of " + sigma + " o " + tau,
		     c,
		     Substitutions.getInstance(Arrays.asList(new Object[] {
			 Substitutions.createExactMatcher(
							  syntax.createTerm("x"),
							  syntax.createTerm("c")
							  ),
			 Substitutions.createExactMatcher(
							  syntax.createTerm("z"),
							  syntax.createTerm("f(a)")
							  ),
			 Substitutions.createExactMatcher(
							  syntax.createTerm("y"),
							  syntax.createTerm("b")
							  ),
		     }))
		     );
    }

    public void testUnification0() throws ParseException {
	Collection c = Collections.EMPTY_SET;
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     Substitutions.id
		     );
    }
    
    public void testUnification1() throws ParseException {
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Collections.singleton(new Double(7));
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     Substitutions.id
		     );

	c = Collections.singleton("A");
	mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     Substitutions.id
		     );

	c = Collections.singleton(syntax.createExpression("p(x)"));
	mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     Substitutions.id
		     );
    }

    public void testUnification() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("$x $y f(x,g(y))"),
	    syntax.createFormula("$x $y $z f(g(a),g(z))")
	});
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     Substitutions.getInstance(Arrays.asList(new Object[] {
			 Substitutions.createExactMatcher(
							  syntax.createAtomic(new SymbolBase("x", Types.INDIVIDUAL, null, true)),
							  syntax.createTerm("g(a)")
							  ),
			 //@internal could be y->z or z->y, here
			 Substitutions.createExactMatcher(
							  syntax.createAtomic(new SymbolBase("y", Types.INDIVIDUAL, null, true)),
							  syntax.createAtomic(new SymbolBase("z", Types.INDIVIDUAL, null, true))
							  )
		     }))
		     );

    }

    public void testNameClash() throws ParseException {
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("f(x)"),
	    syntax.createFormula("g(a)")
	});
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     null
		     );

	c = Arrays.asList(new Object[] {
	    syntax.createFormula("$x f(x)"),
	    syntax.createFormula("$x g(x)")
	});
	mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     null
		     );
    }

    public void testLateNon() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("$x$y p(f(x,y),g(x,y))"),
	    syntax.createFormula("$z p(f(h(z),h(z)),g(h(z),z))")
	});
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     null
		     );

    }

    public void testGrowth() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("$x1$x2 p(f(x1,x1),x1,f(x2,x2),x2)"),
	    syntax.createFormula("$y1$y2$y3 p(y1,f(y2,y2),y2,f(y3,y3))")
	});
	Substitution mu = Substitutions.unify(c);
	assertTrue("expected unification of " + c,
		   mu != null
		   );

    }

    public void testOccurCheck() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("$x p(x)"),
	    syntax.createFormula("$x p(f(x))")
	});
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     null
		     );

    }

    public void testIndirectNonUnification() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("$y$z$w p(f(y),w,g(z))"),
	    syntax.createFormula("$v$u p(v,u,v)")
	});
	Substitution mu = Substitutions.unify(c);
	assertEquals("expected unification of " + c,
		     mu,
		     null
		     );

    }

    public void testIndirectUnification() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    syntax.createFormula("$y$z$w p(f(y),w,g(z))"),
	    syntax.createFormula("$v$u p(u,u,v)")
	});
	Substitution mu = Substitutions.unify(c);
	assertTrue("expected unification of " + c,
		     mu != null
		   );

    }

    public void testUnifyNull() throws ParseException {
	//@internal declaring symbols as variables is difficult, here
	// instantiate a parser
	ClassicalLogic syntax = new ClassicalLogic();
	Collection c = Arrays.asList(new Object[] {
	    null,
	    syntax.createFormula("$v$u p(u,u,v)")
	});
	try {
	    Substitution mu = Substitutions.unify(c);
	    assertTrue("null-pointer expected during unification of " + c,
		       false
		       );
	}
	catch (NullPointerException unifyNull) {}

	c = Arrays.asList(new Object[] {
	    syntax.createFormula("$y$z$w p(f(y),w,g(z))"),
	    null
	});
	try {
	    Substitution mu = Substitutions.unify(c);
	    assertTrue("null-pointer expected during unification of " + c,
		       false
		       );
	}
	catch (NullPointerException unifyNull) {}
	
	c = Arrays.asList(new Object[] {
	    null,
	    null
	});
	try {
	    Substitution mu = Substitutions.unify(c);
	    assertTrue("null-pointer expected during unification of " + c,
		       false
		       );
	}
	catch (NullPointerException unifyNull) {}
    }
}

