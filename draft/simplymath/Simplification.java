/**
 * @(#)Simplification.java 0.7 2002/10/07 Andre Platzer
 * 
 * Copyright (c) 2002 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

//package orbital.moon.logic;

import orbital.moon.logic.MathParser;
import orbital.math.*;

import orbital.logic.trs.*;
import java.util.Arrays;
import orbital.logic.functor.Functionals;

//@todo replace by java.text.ParseException
import orbital.io.ParseException;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * Implementation of algebraic simplification.
 * @version 0.7, 2002/10/07
 * @author  Andr&eacute; Platzer
 * @todo how to recognize variables. Values.symbol(String, boolean variable)
 */
public class Simplification {
    public static void main(String[] arg) throws Exception {
	Object x;
	x = MathParser.createExpression("2*x+(x+0)");
	System.out.println(x + "\t=\t" + simplify(x)); 
	x = MathParser.createExpression("1*x+1*(x+0)*1");
	System.out.println(x + "\t=\t" + simplify(x)); 
	x = MathParser.createExpression("2*x+(x+0)");
	System.out.println(x + "\t=\t" + simplify(x)); 
	x = MathParser.createExpression("2*x+(x+0)");
	System.out.println(x + "\t=\t" + simplify(x)); 
    }

    /**
     * prevent instantiation - module class.
     */
    private Simplification() {}
    
    /**
     * Simplifies an expression.
     * @preconditions true
     * @postconditions RES &equiv; f
     */
    public static Object simplify(Object f) {
	// eliminate derived junctors not in the basis (&forall;,&and;,&or;&not;)
	if (simplifytrs == null) simplifytrs = readTRS(new InputStreamReader(Simplification.class.getResourceAsStream("math-simplify.trs")));
	// necessary and does not disturb local confluency? conditional commutative (according to lexical order)
	//		new LexicalConditionalUnifyingMatcher(MathParser.createExpression("_X2&_X1"), MathParser.createExpression("_X1&_X2"), MathParser.createAtomicLiteralVariable("_X1"), MathParser.createAtomicLiteralVariable("_X2")),
	// necessary and does not disturb local confluency? conditional associative (according to lexical order)
	return Functionals.fixedPoint(simplifytrs, f);
    }
    // lazy initialized cache for TRS rules
    private static Substitution simplifytrs;
}
