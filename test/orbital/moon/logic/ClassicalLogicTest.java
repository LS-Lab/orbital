/**
 * @(#)ClassicalLogicTest.java 1.1 2002-09-15 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic;

import junit.framework.*;
import orbital.logic.sign.*;
import orbital.logic.sign.type.*;
import orbital.logic.imp.*;
import orbital.logic.sign.ParseException;

/**
 * A sample test case, testing ClassicalLogic.
 * @version $Id$
 */
public class ClassicalLogicTest extends check.TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
        return new TestSuite(ClassicalLogicTest.class);
    }

    private Logic logic;
    /**
     * The symbols of the logical junctors.
     */
    private Symbol NOT, AND, OR, XOR, IMPL, EQUIV;
    private Symbol FORALL, EXISTS;

    protected void setUp() {
        //@note assuming the symbols and notation of ClassicalLogic, here
        logic = new ClassicalLogic();
        final Signature core = logic.coreSignature();
        // we also avoid creating true formulas, it's (more or less) futile
        //@xxx we need some valid non-null arguments.
        final Formula B = (Formula) logic.createAtomic(new SymbolBase("B", SymbolBase.BOOLEAN_ATOM));
        final Formula OBJ = (Formula) logic.createAtomic(new SymbolBase("OBJ", SymbolBase.UNIVERSAL_ATOM));
        Formula[] arguments = {B};
        NOT = core.get("~", arguments);
        assert NOT != null : "operators in core signature";

        arguments = new Formula[] {B, B};
        AND = core.get("&", arguments);
        assert AND != null : "operators in core signature";
        OR = core.get("|", arguments);
        assert OR != null : "operators in core signature";
        XOR = core.get("^", arguments);
        assert XOR != null : "operators in core signature";
        IMPL = core.get("->", arguments);
        assert IMPL != null : "operators in core signature";
        EQUIV = core.get("<->", arguments);
        assert EQUIV != null : "operators in core signature";

        final Formula f = (Formula) logic.createAtomic(new SymbolBase("f", Types.getDefault().map(Types.INDIVIDUAL,Types.TRUTH)));
        arguments = new Formula[] {f};
        FORALL = core.get("\u00b0", arguments);
        assert FORALL != null : "operators in core signature";
        EXISTS = core.get("?", arguments);
        assert EXISTS != null : "operators in core signature";
    }

    protected void test(String name) {
        try {
            ClassicalLogic.main(new String[] {"-inference=SEMANTIC_INFERENCE", name});
        }
        catch (Throwable ex) {
            ex.printStackTrace();
            fail(ex.getMessage() + " in file " + name);
        }
    }
    public void testEquivalences() {
        test("all");
    }
    public void testGarbage() {
        test("none");
    }
    public void testProperties() {
        test("properties");
    }


    /**
     * Test whether type-checks are working.
     */
    public void testTypeCheck_positive() {
        final Formula B = (Formula) logic.createAtomic(new SymbolBase("B", SymbolBase.BOOLEAN_ATOM));
        final Formula OBJ = (Formula) logic.createAtomic(new SymbolBase("OBJ", SymbolBase.UNIVERSAL_ATOM));
        try {
            Expression e = logic.compose(logic.createAtomic(AND),
                new Expression[] {
                    B,
                    B
                });
        }
        catch (TypeException fallthrough) {
            System.out.println(fallthrough);
            assertTrue(false, "type-safe expression should be composable");
        }
        catch (ParseException fallthrough) {
            System.out.println(fallthrough);
            assertTrue(false, "type-safe expression should be composable");
        }
        catch (IllegalArgumentException fallthrough) {
            System.out.println(fallthrough);
            assertTrue(false, "type-safe expression should be composable");
        }
        try {
            Expression e = logic.compose(logic.createAtomic(NOT),
                new Expression[] {
                    B
                });
        }
        catch (TypeException fallthrough) {
            System.out.println(fallthrough);
            assertTrue(false, "type-safe expression should be composable");
        }
        catch (ParseException fallthrough) {
            System.out.println(fallthrough);
            assertTrue(false, "type-safe expression should be composable");
        }
        catch (IllegalArgumentException fallthrough) {
            System.out.println(fallthrough);
            assertTrue(false, "type-safe expression should be composable");
        }
    }

    /**
     * Test whether type-checks are working.
     */
    public void testTypeCheck_negative() {
        final Formula B = (Formula) logic.createAtomic(new SymbolBase("B", SymbolBase.BOOLEAN_ATOM));
        final Formula OBJ = (Formula) logic.createAtomic(new SymbolBase("OBJ", SymbolBase.UNIVERSAL_ATOM));
        try {
            Expression e = logic.compose(logic.createAtomic(AND),
                new Expression[] {
                    B,
                    OBJ
                });
            assertTrue(false, "type-incorrect expression should not be composable");
            return;
        }
        catch (TypeException fallthrough) {
            System.out.println(fallthrough);
        }
        catch (ParseException fallthrough) {
            System.out.println(fallthrough);
        }
        catch (IllegalArgumentException fallthrough) {
            System.out.println(fallthrough);
        }
        try {
            Expression e = logic.compose(logic.createAtomic(NOT),
                new Expression[] {
                    OBJ
                });
            assertTrue(false, "type-incorrect expression should not be composable");
            return;
        }
        catch (TypeException fallthrough) {
            System.out.println(fallthrough);
        }
        catch (ParseException fallthrough) {
            System.out.println(fallthrough);
        }
        catch (IllegalArgumentException fallthrough) {
            System.out.println(fallthrough);
        }
    }
}
