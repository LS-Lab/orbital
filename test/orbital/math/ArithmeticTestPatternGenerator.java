/**
 * @(#)ArithmeticTestPatternGenerator.java 1.2 2007-08-24 Andre Platzer
 * 
 * Copyright (c) 2002-2007 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.awt.Dimension;

import java.util.*;

import orbital.util.Utility;

/**
 * Automatically generates test cases.
 * @version $Id$
 */
public class ArithmeticTestPatternGenerator {
    private final Random random;
    private final ValueFactory vf;
    private double min;
    private double max;

    /**
     * Unique symbol counter
     */
    private int symbolId = 1;
    
    /**
     * Create a test pattern generator for values in the given range [min,max].
     */
    public ArithmeticTestPatternGenerator(double min, double max, ValueFactory valueFactory) {
        this.random = new Random();
        this.vf = valueFactory;
        this.min = min;
        this.max = max;
    }
    public ArithmeticTestPatternGenerator(double min, double max) {
        this(min, max, Values.getDefault());
    }

    public ArithmeticTestPatternGenerator() {
        this(-1000, 1000);
    }
    
    // randomly generate Arithmetic values
    
    public int randomInt(int min, int max) {
        return min + random.nextInt(max-min + 1);
    }
    public int randomInt() {
        return randomInt((int)Math.ceil(min), (int)Math.floor(max));
    }
    public double randomDouble(double min, double max) {
        return ((max-min) * random.nextDouble() + min);
    }
    public double randomDouble() {
        return randomDouble(min, max);
    }

    /**
     * Randomly generate a scalar or symbol from the given list of possible types.
     */
    public Arithmetic randomScalary(double min, double max, List/*<Class>*/ types) {
        Class t = (Class) types.get(randomInt(0, types.size() - 1));
        if (t == Integer.class)
            return vf.valueOf(randomInt((int)Math.ceil(min), (int)Math.floor(max)));
        else if (t == Rational.class)
            return vf.rational(randomInt((int)min, (int)max), randomInt(1, (int)max));
        else if (t == Symbol.class)
            return vf.symbol("a" + (symbolId++));
        else if (t == Complex.class) {
            List realtypes = new LinkedList(types);
            realtypes.remove(Complex.class);
            return vf.complex((Real)randomScalary(min, max, realtypes), (Real)randomScalary(min, max, realtypes));
        } else
            throw new IllegalArgumentException("no known type provided " + types);
    }
    public Arithmetic randomScalary(List/*<Class>*/ types) {
        return randomScalary(min, max, types);
    }


    /**
     * Randomly generate a matrix with the given list of possible component types.
     */
    public Matrix randomMatrix(double min, double max, List/*<Class>*/ types, Dimension dim) {
        Matrix x = vf.newInstance(dim);
        if (types.equals(Collections.singletonList(Real.class)) && Utility.flip(random, 0.5))
            // randomly switch to RMatrix
            x = vf.valueOf(new double[dim.height][dim.width]);
        for (int i = 0; i < dim.height; i++)
            for (int j = 0; j < dim.width; j++)
                x.set(i,j, randomScalary(min, max, scalars(types)));
        return x;
    }
    public Matrix randomMatrix(List/*<Class>*/ types, Dimension dim) {
        return randomMatrix(min, max, types, dim);
    }
    /**
     * Randomly generate a vector with the given list of possible component types.
     */
    public Vector randomVector(double min, double max, List/*<Class>*/ types, int dim) {
        Vector x = vf.newInstance(dim);
        if (types.equals(Collections.singletonList(Real.class)) && Utility.flip(random, 0.5))
            // randomly switch to RVector
            x = vf.valueOf(new double[dim]);
        for (int i = 0; i < dim; i++)
            x.set(i, randomScalary(min, max, scalars(types)));
        return x;
    }
    public Vector randomVector(List/*<Class>*/ types, int dim) {
        return randomVector(min, max, types, dim);
    }

    private static List scalars(List types) {
        List l = new LinkedList(types);
        l.remove(Vector.class);
        l.remove(Matrix.class);
        l.remove(Tensor.class);
        return l;
    }
}
