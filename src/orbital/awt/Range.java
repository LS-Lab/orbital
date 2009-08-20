/*
 * @(#)Range.java 0.9 1999/03/16 Andre Platzer
 * 
 * Copyright (c) 1996-1999 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.io.Serializable;

import orbital.math.Vector;

import orbital.math.Values;

/**
 * Graphical range holder.
 * @invariants min.dimension() == max.dimension()
 */
public class Range implements Serializable {
        private static final long serialVersionUID = -5056929164974606200L;
        

        public Vector min;

    public Vector max;

    public Range(Vector min, Vector max) {
        if (min.dimension() != max.dimension())
            throw new IllegalArgumentException("Range Vectors must have same dimension");
        this.min = min;
        this.max = max;
    }
    public Range(double minx, double miny, double maxx, double maxy) {
        this(Values.getDefaultInstance().valueOf(new double[] {minx, miny}),
             Values.getDefaultInstance().valueOf(new double[] {maxx, maxy}));
    }
    
    public Object clone() {
        return new Range((Vector) min.clone(), (Vector) max.clone());
    }
    
    public boolean equals(Object o) {
        if (o instanceof Range) {
            Range b = (Range)o;
            return (min == null ? b.min==null : min.equals(b.min))
                && (max == null ? b.max==null : max.equals(b.max));
        }
        return false;
    }
        
    public int hashCode() {
        return min.hashCode() ^ max.hashCode();
    }
    
    public double getLength(int dimension) {
        return max.get(dimension).subtract(min.get(dimension)).norm().doubleValue();
    } 
    
    public String toString() {
        return getClass().getName() + "[" + min + ".." + max + "]";
    } 
}
