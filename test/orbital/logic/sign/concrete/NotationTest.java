/**
 * @(#)NotationTest.java 1.1 2004-10-02 Andre Platzer
 * 
 * Copyright (c) 2004 Andre Platzer. All Rights Reserved.
 */

package orbital.logic.sign.concrete;

import orbital.logic.sign.*;
import orbital.logic.imp.*;
import orbital.moon.logic.*;
import orbital.logic.sign.ParseException;
import junit.framework.*;


/**
 * A sample test case, testing Notation, formatting and parsing.
 * @version $Id$
 */
public class NotationTest extends check.TestCase {
    private Logic syntax;
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(NotationTest.class);
    }
    protected void setUp() {
        syntax = new ClassicalLogic();
    }

    protected Formula test(String expr) {
        try {
            System.out.println("Original formula:\t" + expr);
            System.out.print("Parsed formula:\t"); System.out.flush();
            Expression p = syntax.createExpression(expr);
            System.out.println(p);
            System.out.print("Reparsed formula:\t"); System.out.flush();
            Expression p2 = syntax.createExpression(p.toString());
            System.out.println(p2);
            assertTrue(p.equals(p2), p + " equals reparsed " + p2);
            return (Formula)p;
        }
        catch (Throwable ex) {
            System.out.println();
            ex.printStackTrace();
            fail(ex.getMessage() + " in expression: " + expr);
            return null;
        }
    }
    protected void equal(String expr1, String expr2, boolean equalOrDiffer) {
        final String desc = expr1 + " and\n" + expr2 + " " + (equalOrDiffer ? "equal" : "differ");
        Formula f1 = test(expr1);
        Formula f2 = test(expr2);
        assertTrue(f1 != null);
        assertTrue(f2 != null);
        if (f1.equals(f2)) {
            assertTrue(equalOrDiffer == true, desc);
        } else {
            assertTrue(equalOrDiffer == false, desc);
        }
    }
    protected void parsable(String expr, boolean expectparsable) {
        final String desc = expr + " " + (expectparsable ? "is" : "is not") + " parsable";
        try {
            System.out.println("is this a parsable expression:\t" + expr);
            Expression p = syntax.createExpression(expr);
            assertTrue(true == expectparsable , desc);
            return;
        }
        catch (ParseException fallthrough) {
            System.out.println(fallthrough);
        }
        catch (IllegalArgumentException fallthrough) {
            System.out.println(fallthrough);
        }
        assertTrue(false == expectparsable , desc);
    }
    protected void compactedBrackets(String expr, int expectedNumberOfOpenBrackets) {
        Formula f = test(expr);
        String fs = f.toString();
        int count = 0;
        for (int i = 0; i < fs.length(); i++) {
            if (fs.charAt(i) == '(')
                count++;
        }
        assertTrue(count == expectedNumberOfOpenBrackets, "expected " + expectedNumberOfOpenBrackets + " brackets, found " + count);
    }

    public void testBracketing() {
        test("a & b | c");
        test("a & b | c & d");
        test("a & b | c & (b | d)");
        test("(a & b | c)");
        test("a & (b | c)");
        test("a & b | c");
        test("all x p(x) | q(x)");
        test("all x (p(x) | q(x))");
    }

    public void testBindingPreferences() {
        equal("a & b | c", "(a & b) | c", true);
        equal("a & b | c", "a & (b | c)", false);
        equal("a & b -> c", "(a & b) -> c", true);
        equal("a & b -> c", "a & (b -> c)", false);
        equal("a & b <-> c", "(a & b) <-> c", true);
        equal("a & b <-> c", "a & (b <-> c)", false);
        equal("~a & b | c", "(~a) & b | c", true);
        equal("~a & b | c", "~(a & b | c)", false);
        equal("a -> b <-> c", "(a -> b) <-> c", true);
        equal("a -> b <-> c", "a -> (b <-> c)", false);
        equal("a -> b -> c", "a -> (b -> c)", true);
        equal("a -> b -> c", "(a -> b) -> c", false);
        equal("all x p(x) <-> q(x)", "(all x p(x)) <-> q(x)", true);
        equal("all x p(x) <-> q(x)", "all x (p(x) <-> q(x))", false);
        equal("some x p(x) <-> q(x)", "(some x p(x)) <-> q(x)", true);
        equal("some x p(x) <-> q(x)", "some x (p(x) <-> q(x))", false);
        equal("all x p(x) -> q(x)", "(all x p(x)) -> q(x)", true);
        equal("all x p(x) -> q(x)", "all x (p(x) -> q(x))", false);
        equal("some x p(x) -> q(x)", "(some x p(x)) -> q(x)", true);
        equal("some x p(x) -> q(x)", "some x (p(x) -> q(x))", false);
        equal("all x p(x) | q(x)", "(all x p(x)) | q(x)", true);
        equal("all x p(x) | q(x)", "all x (p(x) | q(x))", false);
        equal("some x p(x) | q(x)", "(some x p(x)) | q(x)", true);
        equal("some x p(x) | q(x)", "some x (p(x) | q(x))", false);
        equal("all x p(x) & q(x)", "(all x p(x)) & q(x)", true);
        equal("all x p(x) & q(x)", "all x (p(x) & q(x))", false);
        equal("some x p(x) & q(x)", "(some x p(x)) & q(x)", true);
        equal("some x p(x) & q(x)", "some x (p(x) & q(x))", false);
    }

    public void testCompactBracketing() {
        //compactedBrackets("all x p(x) | q(x)");
    }
    
    public void testNonFormulas() {
        parsable("a&", false);
        parsable("a&b&|c", false);
        parsable("all x", false);
        parsable("-> p", false);
        parsable("all x p(x", false);
        parsable("a ! b", false);
    }
}
