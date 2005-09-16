/**
 * @(#)AbstractFunction.java 1.0 1999/06/01 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math.functional;

import orbital.moon.math.functional.AbstractFunctor;
import orbital.math.Arithmetic;

//TODO: make public or make public inner class of Function?
// this implements the union of MathFunctor and Function, and extends the default pointwise arithmetic operations
/*private static*/ abstract class AbstractFunction/*<A extends Arithmetic, B extends Arithmetic>*/  extends AbstractFunctor implements Function/*<A,B>*/ {}

