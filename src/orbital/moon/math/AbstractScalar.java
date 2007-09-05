/**
 * @(#)AbstractScalar.java 1.0 2000/08/08 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.math;
import orbital.math.*;

import java.io.Serializable;

abstract class AbstractScalar extends Number implements Scalar, Serializable {
    private static final long serialVersionUID = -7969203760535813244L;

    /**
     * Compares this object with the specified object for order.
     * @preconditions this and o have comparable types
     * @return a negative integer, zero, or a positive integer as this object is
     *  less than, equal to, or greater than the specified object.
     * @postconditions &forall;e1,e2&isin;class (e1.compareTo((Object)e2)==0) &hArr; (e1.equals((Object)e2), i.e. compareTo is consistent with equals.
     * @throws ClassCastException if the specified object's type prevents it from being compared to this Object.
     * @throws UnsupportedOperationException if this type of scalars is indeed unordered and !this.equals(o).
     * @see Comparable#compareTo(Object)
     */
    public abstract int compareTo(Object o);

    public boolean equals(Object o, Real tolerance) {
        if (!(o instanceof Scalar))
            return false;
        return Metric.INDUCED.distance(this, (Arithmetic)o).compareTo(tolerance) <= 0;
    }

    public boolean isZero() {
        return equals(zero());
    }
    public boolean isOne() {
        return equals(one());
    }

    public int intValue() {
        return (int) longValue();
    } 
    public float floatValue() {
        return (float) doubleValue();
    } 

    /**
     * Returns a string representation of the object.
     * @see ArithmeticFormat#format(Arithmetic)
     */
    public String toString() {
        return ArithmeticFormat.getDefaultInstance().format(this);
    } 

    public Arithmetic scale(Arithmetic alpha) {
        return multiply(alpha);
    }

    /**
     * Checks whether the given arithmetic object is a number.
     * @return whether v is complex, real, rational or an integer.
     */
    /*
      public static boolean isa(Arithmetic v) {
      if (v instanceof Scalar)
      return true;
      if (v instanceof Number)
      return Complex.isa((Number) v);
      else
      return false;
      }
    */
}
