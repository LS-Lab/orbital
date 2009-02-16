/**
 * @(#)Gene.java 1.0 1998/08/01 Andre Platzer
 * 
 * Copyright (c) 1995-1998 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.evolutionary;

//TODO: implement other mutations (like single-point, like number mutation with different deviation)
//TODO: think about: squashing function 1 - e^-(d(x,y)^2/(2sigma^2)) transforms d to a better (non-linear) distance measure?

import java.io.Serializable;
import orbital.math.Metric;
import orbital.util.DelegateList;
import java.util.Iterator;

import orbital.math.Real;
import orbital.math.Values;
import orbital.math.MathUtilities;
import orbital.math.Stat;
import orbital.util.Utility;
import java.util.ArrayList;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.NoSuchElementException;

import java.util.logging.Logger;
import java.util.logging.Level;

import orbital.util.InnerCheckedException;

/**
 * Base interface for a gene data model in a genome.
 * <p>
 * A gene provides the following operators and functions:
 * <ul>
 *   <li id="reproduce">reproduction operators consisting of
 *     <ul>
 *       <li><strong>mutate</strong> that defines how its mutation is done, depending on a probability.</li>
 *       <li><strong>recombine</strong> for recombining genetic information of the parents
 *         to generate genes of the children (reproduction).
 *         This is the essential characteristic and advantage of genetic algorithms.
 *       </li>
 *     </ul>
 *   </li>
 *   <li>and a distance measure on genes.</li>
 * </ul>
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo implement property editors
 * @todo move to package orbital.algorithm.representation or anything, to make the (genetic) data representation independent of the (genetic and evolutionary or standard hill-climbing) algorithms.
 */
public interface Gene {
    /**
     * Returns a clone of this gene.
     * @postconditions RES.equals(this) &and; RES&ne;OLD(RES)
     */
    Object clone();

    boolean equals(Object o);
        
    int hashCode();

    // get/set methods

    /**
     * Get the gene interpretation value.
     * <p>
     * Useful for interpreting a gene for fitness evaluation and interpretation of solutions.</p>
     * @return the Object represented by this gene.
     *  Interprets gene by decoding its data.
     */
    Object get();

    /**
     * Set the gene value.
     * <p>
     * Consider setting fitness to <code>Double.NaN</code> due to the change to remind evaluator.</p>
     * @param value the Object this gene should represent.
     *  Encodes the value such that this gene represents the given object.
     */
    void set(Object value);

    // central virtual methods
    // transformation methods

    /**
     * Get a <strong>mutated</strong> version of this Gene.
     * @param probability the probability rating of mutation level.
     *  f.ex. probability with that each bit of the Gene mutates.
     * @return a new gene that is a mutation of this one.
     */
    Gene mutate(double probability);

    /**
     * Genetically <strong>recombine</strong> gene data of parents to their children
     * via reproduction.
     * <pre>
     * a       a  = direct ancestors to be used
     * n * --- ;    n  = number of children to be produced
     * p       p  = probability for each part of parent's Gene to be inherited
     * a/p = elongation of Gene length
     * n/a = growth of population size
     * if n/a &lt; 1 the population is contracting.
     * if n/a = 1 the population size is fixed.
     * if n/a &gt; 1 the population is growing.
     * </pre>
     * Usually it is p=n/a.
     * @param parents the Genes to be used as parents for the children.
     *  <code>a</code> is the number of parents (direct ancestors).
     * parents are <b>readonly</b>.
     * @param childrenCount the number of Gene children to produce and return.
     *  <code>n</code> is the number of children to be produced.
     * @param recombinationProbability the probability with that parts of the inherited gene data
     *  is recombined.
     *  This does not necessarily imply an exchange of data, Genes might as well
     *  realign or repair at random. But it makes a data recombination possible.
     * @preconditions &exist;i parents[i] == this, and parents are allels
     * @return the new <code>childrenCount</code> children produced.
     * @postconditions RES.length == childrenCount
     * @todo enhance funny documentation
     */
    Gene[] recombine(final Gene[] parents, int childrenCount, double recombinationProbability);

    /**
     * Get an inverted version of this Gene.
     * <p>
     * Used in {@link Population#create(Population, Genome, int)} to form a balanced
     * and least biased initial population.</p>
     * @return the complementary inverted Gene.
     */
    Gene inverse();

    /**
     * Get the distance measure for this class.
     * <p>
     * Used to determine how different two genes are.</p>
     * @return a distance measure whose deviation is 1
     *  such that it can easily be used as a measure for similarity.
     *  Additionally, only positive numbers should be returned.
     *  So this distance measure only has values in [0,1].
     */
    Metric/*<Gene>*/ distanceMeasure();


    /**
     * Represents a container gene that contains a list of other genes.
     * <p>
     * This list is especially useful for aggregating a collection of correlated genes into
     * a single logical gene.
     * </p>
     *
     * @structure extends DelegateList<Gene>
     * @structure aggregates members:List<Gene>
     * @invariants sub classes support nullary constructor (for virtual new instance).
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    static class List extends DelegateList/*<Gene>*/ implements Gene, Serializable {
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long serialVersionUID = 1450216289981749502L;
    
        /**
         * new list of Genes.
         * @param geneType the type of the member genes.
         * @param size the initial size of this list. i.e. the initial number of Genes.
         */
        public List(Class/*<? extends Gene>*/ geneType, int size) throws InstantiationException, IllegalAccessException {
            super(new ArrayList(size));
            for (int i = 0; i < size; i++)
                add(geneType.newInstance());
        }
    
        public List() {
            super(new ArrayList());
        }

        /**
         * Create a new instance of the exact same type ensuring a minimum capacity.
         * Used to create an object of the same type without copying its data.
         */
        private List newInstance(int capacity) {
            try {
                List l = (List) getClass().newInstance();
                //XXX: instantiated twice for List type, now, could optimize
                l.setDelegatee(new ArrayList(capacity));
                return l;
            }
            catch (InstantiationException e) {throw new InnerCheckedException("invariant: sub classes of " + Gene.List.class + " must support nullary constructor for cloning.", e);}
            catch (IllegalAccessException e) {throw new InnerCheckedException("invariant: sub classes of " + Gene.List.class + " must support nullary constructor for cloning.", e);}
        }
    
        /**
         * Returns a deep copy of this exact type of gene.
         */
        public Object clone() {
            List n = newInstance(size());
            for (Iterator/*<Gene>*/ i = iterator(); i.hasNext(); )
                n.add((Gene) ((Gene) i.next()).clone());
            return n;
        } 
    
        public boolean equals(Object o) {
            if (o != null && getClass() == o.getClass()) {
                return super.equals(((List) o).getDelegatee());
            } 
            return false;
        } 
        
        public int hashCode() {
            return super.hashCode();
        }

        // get/set methods
    
        public Object/*java.util.List*/ get() {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(new RuntimePermission("accessDeclaredMembers"));
            } 
            return getDelegatee();
        } 
        public void set(Object/*java.util.List*/ list) {
            setDelegatee((java.util.List) list);
        }

        // central virtual methods
        // transformation methods

        /**
         * Get a mutated version of this Gene.
         * <p>Implemented as Element-wise mutation.</p>
         */
        public Gene mutate(double probability) {
            if (!MathUtilities.isProbability(probability))
                throw new IllegalArgumentException("invalid probability " + probability);
            List n = newInstance(size());
            for (Iterator/*<Gene>*/ i = iterator(); i.hasNext(); )
                //TODO: randomly swap adjacent members
                n.add(((Gene) i.next()).mutate(probability));
            return n;
        } 
    
        /**
         * {@inheritDoc}.
         * @see #elementwiseRecombine(Gene[],int,double)
         */
        public Gene[] recombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
            return elementwiseRecombine(parents, childrenCount, recombinationProbability);
        }

        /**
         * <p>Implemented as element-wise recombination, each gene does recombine, separately.</p>
         */
        protected Gene[] elementwiseRecombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
            if (!MathUtilities.isProbability(recombinationProbability))
                throw new IllegalArgumentException("invalid probability " + recombinationProbability);
            List[] parentsc = (List[]) parents;
            List[] children = (List[]) Array.newInstance(parents[0].getClass(), childrenCount);
            Iterator/*<Gene>*/[] iterator = new Iterator[parents.length];
    
            // create new children gene objects
            for (int i = 0; i < children.length; i++)
                children[i] = newInstance(size());

            // recombine each gene for the children
            Iterator j = commonIterator(parentsc);
            for (int i = 0; j.hasNext(); i++) {
                Gene[] g = (Gene[]) j.next();
                Gene[] recombined = g[0].recombine(g, childrenCount, recombinationProbability);
                for (int c = 0; c < children.length; c++)
                    children[c].add(i, recombined[c]);
            }
            return children;
        } 

        /**
         * Iterate over an array of lists such that next() returns an array of the corresponding next elements.
         */
        private static Iterator commonIterator(List[] lists) {
            final Iterator[] iterator = new Iterator[lists.length];
            for(int i = 0; i < iterator.length; i++)
                iterator[i] = lists[i].iterator();
            return new Iterator() {
                    public boolean hasNext() {
                        boolean result = iterator[0].hasNext();
                        assert validate(result) : "all iterators have the same length";
                        return result;
                    }
                    /**
                     * @see Setops#all(Collection, Predicate)
                     */
                    private boolean validate(boolean value) throws AssertionError {
                        for(int i = 0; i < iterator.length; i++)
                            if (value != iterator[i].hasNext())
                                return false;
                        return true;
                    }
                        
                    public Object next() {
                        Object r0 = iterator[0].next();
                        Object[] r = (Object[]) Array.newInstance(r0 != null ? r0.getClass() : Object.class, iterator.length);
                        try {
                            r[0] = r0;
                            for(int i = 1; i < iterator.length; i++)
                                r[i] = iterator[i].next();
                            return r;
                        }
                        catch (NoSuchElementException asserted) {throw new AssertionError(asserted);}
                    }
                        
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
        }
    
        /**
         * <p>Implemented as uniform recombination, uniformly distributes genes to
         * the children.</p>
         */
        protected Gene[] uniformRecombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
            if (!MathUtilities.isProbability(recombinationProbability))
                throw new IllegalArgumentException("invalid probability " + recombinationProbability);
            List[] parentsc = (List[]) parents;
            List[] children = (List[]) Array.newInstance(parents[0].getClass(), childrenCount);
            final int     a = parentsc.length;    // a
            final int     n = children.length;    // n
            final double  p = a;                                  // p
    
            // create new children gene objects
            for (int i = 0; i < children.length; i++)
                children[i] = newInstance(size());

            // uniformly distribute Gene data of all parents over the children
            //XXX: check logic if it's okay for lists
            UniqueShuffle par = new UniqueShuffle(parentsc.length);
            for (int i = 0; i < parentsc[0].size(); i++) {
                if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), recombinationProbability))
                    par.reShuffle(GeneticAlgorithm.geneticAlgorithm.getRandom());
                else
                    par.unShuffle();
                for (int c = 0; c < children.length; c++)
                    children[c].add(i, parentsc[par.next()].get(i));
            } 
            return children;
        } 

        public Gene inverse() {
            List n = newInstance(size());
            for (Iterator i = iterator(); i.hasNext(); )
                //TODO: invert order, or would this be garbage?
                n.add(((Gene) i.next()).inverse());
            return n;
        } 

        public Metric distanceMeasure() {
            return metric;
        } 
        private static final Metric metric = new Metric() {
                //@todo rewrite pure functionally
                public Real distance(Object o1, Object o2) {
                    List a = (List) o1;
                    List b = (List) o2;
                    if (a.size() != b.size())
                        return Values.getDefault().ONE();
                    double difference = 0;
                    Iterator/*<Gene>*/ i = a.iterator(), j = b.iterator();
                    while (i.hasNext() && j.hasNext()) {
                        Gene e = (Gene) i.next();
                        double singleDistance = Math.abs(e.distanceMeasure().distance(e, j.next()).doubleValue());
                        assert 0 <= singleDistance && singleDistance <= 1 : "distance measure for Genes has values in [0,1], only. found " + singleDistance + " for " + e.getClass();
                        difference += singleDistance;
                    }
                    assert !i.hasNext() && !j.hasNext() : "equally sized collections have iterators with equal lengths";
                    return Values.getDefaultInstance().valueOf(difference / a.size());
                } 
            };

        public String toString() {
            String                sep = (this.get(0) + "").length() < 10 ? ",\t" : System.getProperty("line.separator");
            StringWriter  wr = new StringWriter();
            for (Iterator i = iterator(); i.hasNext(); ) {
                wr.write(i.next() + (i.hasNext() ? sep : ""));
            }
            return "[" + wr.toString() + "]";
        } 
    
    }

    /**
     * Returns a new Gene object initialized to the value of the specified String.
     * The argument is interpreted as representing a sequence of boolean values coded as the characters <code>1</code> and <code>0</code>.
     * 
     * @param s the string to be parsed.
     * @return a newly constructed Gene initialized to the value represented by the string argument.
     * @throws NumberFormatException - if the string does not contain a parsable value.
     */
    /*public static Gene valueOf(String s) throws NumberFormatException {
      Gene c = new BitSet(s.length());
      //TODO: implement parsing other Genes than BitSet?
      for (int i = 0; i < s.length(); i++)
      switch (s.charAt(i)) {
      case '1':
      c.data[i] = true;
      break;
      case '0':
      c.data[i] = false;
      break;
      default:
      throw new NumberFormatException("Gene data contains illegal character '" + s.charAt(i) + "' at index " + i);
      }
      return c;
      } */

    /**
     * Bit string gene.
     * <p>
     * Much like DNA, this implementation uses a boolean-array as Gene data.
     * However, the interpretation of this bit string is, of course, problem specific.
     * </p>
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    public static class BitSet implements Gene, Serializable {
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long          serialVersionUID = -2845226398297436088L;
    
        /**
         * The set of Gene data represented as booleans.
         * binary string implementation.
         * @serial
         */
        private boolean[] data;
    
        /**
         * Create a Gene of a certain length.
         * @param length the number of boolean data flags in this Gene.
         */
        public BitSet(int length) {
            this.data = new boolean[length];
            for (int i = 0; i < length; i++)
                data[i] = false;
        }
        private BitSet(boolean[] data) {
            this.data = data;
        }
    
        public Object clone() {
            return new BitSet((boolean[]) data.clone());
        } 
    
        public boolean equals(Object o) {
            if (o instanceof BitSet) {
                BitSet B = (BitSet) o;
                if (length() != B.length())
                    return false;
                for (int i = 0; i < length(); i++)
                    if (data[i] != B.data[i])
                        return false;
                return true;
            } 
            return false;
        } 
    
        /**
         * @todo see java.util.BitSet#hashCode() is somewhat tricky
         */
        public int hashCode() {
            int hashCode = 1;
            for(int i = 0; i < data.length; i++) {
                hashCode = (hashCode << 1) + (data[i] ? 0 : 1);
            }
            return hashCode;
        }

        // get/set methods
    
        /**
         * Get the length of the boolean data.
         */
        public final int length() {
            return data.length;
        } 
    
        /**
         * Get the boolean value at index.
         * @return the boolean data at the bit with index.
         */
        public boolean get(int index) {
            if (index < 0 || index > length())
                throw new ArrayIndexOutOfBoundsException(index + " should be in [0;" + length() + "[");
            return data[index];
        } 
    
        /**
         * Set the boolean value at index.
         * <p>
         * Consider setting fitness to <code>Double.NaN</code> due to the change to remind evaluator.</p>
         * @param value  the boolean value to be set at index.
         */
        public void set(int index, boolean value) {
            if (index < 0 || index > length())
                throw new ArrayIndexOutOfBoundsException(index + " should be in [0;" + length() + "[");
            data[index] = value;
        } 
    
        public Object/*boolean[]*/ get() {
            return data;
        }
        
        public void set(Object/*boolean[]*/ n) {
            data = (boolean[]) n;
        } 
    
        // central virtual methods
        // transformation methods
    
        /**
         * Get a mutated version of this Gene.
         * <p>Implemented as uniform mutation. Each bit of Gene data will be flipped with a specified probability.</p>
         * @param probability the probability with that each bit of the Gene mutates.
         */
        public Gene mutate(double probability) {
            if (!MathUtilities.isProbability(probability))
                throw new IllegalArgumentException("invalid probability " + probability);
            BitSet n = (BitSet) clone();
            for (int i = 0; i < n.length(); i++)
                if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), probability))
                    n.data[i] = !n.data[i];
            return n;
        } 
    
    
        /**
         * {@inheritDoc}.
         * <p>Implemented as uniform recombination.</p>
         */
        public Gene[] recombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
            if (!MathUtilities.isProbability(recombinationProbability))
                throw new IllegalArgumentException("invalid probability " + recombinationProbability);
            BitSet[]      parentsc = (BitSet[]) parents;
            BitSet[]      children = new BitSet[childrenCount];
            final int     a = parentsc.length;    // a
            final int     n = children.length;    // n
            final double  p = a;                                  // p
    
            // create new children gene objects
            for (int i = 0; i < children.length; i++)
                //TODO: optimize: filling .data is not required here
                children[i] = new BitSet(length());

            // uniformly distribute Gene data of all parents over the children
            UniqueShuffle par = new UniqueShuffle(a);
            for (int i = 0; i < parentsc[0].length(); i++) {
                if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), recombinationProbability))
                    par.reShuffle(GeneticAlgorithm.geneticAlgorithm.getRandom());
                else
                    par.unShuffle();
                for (int c = 0; c < n; c++)
                    children[c].data[i] = parentsc[par.next()].data[i];
            } 
            return children;
        } 
    
        /**
         * Get an inverted version of this Gene.
         * @return the complementary inverted Gene where all data booleans are negated.
         */
        public Gene inverse() {
            BitSet r = new BitSet(length());
            for (int i = 0; i < length(); i++)
                r.data[i] = !data[i];
            return r;
        } 
    
        public Metric distanceMeasure() {
            return metric;
        } 
        private static final Metric metric = new Metric() {
                public Real distance(Object o1, Object o2) {
                    BitSet a = (BitSet) o1;
                    BitSet b = (BitSet) o2;
                    if (a.length() != b.length())
                        return Values.getDefault().ONE();
                    int    differences = 0;
                    for (int i = 0; i < a.length(); i++)
                        if (a.data[i] != b.data[i])
                            differences++;
                    return Values.getDefaultInstance().valueOf((double) differences / a.length());
                } 
            };
    
        /**
         * Returns a string representation of this object.
         */
        public String toString() {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length(); i++)
                sb.append(data[i] ? "1" : "0");
            return sb.toString();
        } 
    }

    /**
     * Numeric gene data.
     * <p>
     * This class is the base class for gene data with a numeric interpretation.
     * </p>
     * <p>
     * Note however, that all numeric genes could as well be encoded with a mere bit string.
     * Although default uniform mutations and recombination would then have a dramatic effect on
     * these bit string encoded values. This is the reason for introducing explicit genes with
     * numeric interpretations.
     * </p>
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    public static abstract class Number extends java.lang.Number implements Gene, Serializable {
        private static final Logger logger = Logger.getLogger(Number.class.getName());
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long       serialVersionUID = 373388107171340893L;
        
        private static final double relativeDeviation = 2;
        
        public abstract Object clone();
        
        /**
         * Checks two gene numbers for equality according to their doubleValue.
         */
        public boolean equals(Object o) {
            if (!(o instanceof Number))
                return false;
            Number b = (Number) o;
            //@see Double#compare(double,double)
            return java.lang.Double.doubleToLongBits(doubleValue()) == java.lang.Double.doubleToLongBits(b.doubleValue());
        }
    
        /**
         * Calculates a hash code according to the doubleValue.
         */
        public int hashCode() {
            //@see Double#hashCode()
            long bits = java.lang.Double.doubleToLongBits(doubleValue());
            return (int)(bits ^ (bits >>> 32));
        }
    
        public int intValue() {
            return (int) longValue();
        } 
        public long longValue() {
            return (long) doubleValue();
        } 
        public float floatValue() {
            return (float) doubleValue();
        } 
    
        public String toString() {
            return doubleValue() + "";
        } 

        // central virtual methods
        // transformation methods

        public Gene inverse() {
            Number n = (Number) clone();
            n.set(new java.lang.Double(-doubleValue()));
            return n;
        } 

        public Gene mutate(double probability) {
            if (!MathUtilities.isProbability(probability))
                throw new IllegalArgumentException("invalid probability " + probability);
            Number n = (Number) clone();
            double value = n.doubleValue();
            if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), probability))
                //@xxx sure that nextGaussian()&isin;[-&infin;,+&infin;] with deviation 1.0 around 0 is a good choice? Perhaps this leads to NaN in learning Seti? No it cannot.
                n.set(new java.lang.Double(value + GeneticAlgorithm.geneticAlgorithm.getRandom().nextGaussian() * relativeDeviation));
            return n;
        } 

        public Metric distanceMeasure() {
            return metric;
        } 
        private static final Metric metric = new Metric() {
                public Real distance(Object o1, Object o2) {
                    return Values.getDefaultInstance().valueOf(d(((Number) o1).doubleValue(), ((Number) o2).doubleValue()));
                } 
            };
    
        /**
         * Somewhat like the heavyside function cutting v/range to [-1,1]
         * @see Gene.BoundedFloat#trim(double, double, double)
         */
        private static double h(double v, double range) {
            if (v >= range)
                return 1;
            else if (v <= -range)
                return -1;
            else {
                assert range != 0 : "v isin (-range, range) implies range != 0";
                return v / range;
            }                                                                                       
        }
        /**
         * distance of two numbers for similarity.
         * @todo see smooth stretching functions for other smooth transformations of the simple "pointwise" distance measure
         */
        private static double d(double a, double b) {
            //XXX: how to really norm this to the range [-1,1], jolly good?
            double r = h(a - b, Math.max(Math.abs(a), Math.abs(b)));
            if (Double.isNaN(r))
                logger.log(Level.SEVERE, "distance d(" + a + "," + b + ") is " + r, " due to " + (a-b) + " and " + Math.max(a,b));
            return r;
        }
    }

    /**
     * Integer gene data.
     * <p>This implementation uses an {@link java.lang.Integer} as gene data.</p>
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    public static class Integer extends Number {
    
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long          serialVersionUID = 7710737617378991898L;
    
        /**
         * The Gene data represented as a number.
         * @serial
         */
        private int data;
    
        public Integer(int data) {
            this.data = data;
        }
        /**
         * Construct a non-initialized Integer.
         * <p>
         * <b>Note:</b> non-initialized (that is {@link java.lang.Integer#MIN_VALUE}) Integers
         * are no good starting-point for creating a population.</p>
         */
        public Integer() {
            this(java.lang.Integer.MIN_VALUE);
        }
    
        public Object clone() {
            return new Integer(data);
        } 
    
        // get/set methods
    
        public Object/*java.lang.Integer*/ get() {
            return new java.lang.Integer(intValue());
        }
        public int intValue() {
            return data;
        }
        public double doubleValue() {
            return data;
        }

        public void set(Object/*java.lang.Integer*/ n) {
            set(((java.lang.Number) n).intValue());
        } 
        public void set(int n) {
            data = n;
        } 
    
        // central virtual methods
        // transformation methods
    
        public Gene[] recombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
            if (!MathUtilities.isProbability(recombinationProbability))
                throw new IllegalArgumentException("invalid probability " + recombinationProbability);
            Integer[]     parentsc = (Integer[]) parents;
            Integer[]     children = new Integer[childrenCount];
            final int     a = parentsc.length;    // a
            final int     n = children.length;    // n
            final double  p = a;                                  // p
    
            // create new children gene objects
            for (int i = 0; i < children.length; i++)
                //TODO: optimize: filling .data is not required here
                children[i] = (Integer) clone();

            // uniformly distribute Gene data of all parents over the children
            //TODO: recombine randomly leads to intermediary arithmetic mean of parents?
            UniqueShuffle par = new UniqueShuffle(a);
            if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), recombinationProbability))
                par.reShuffle(GeneticAlgorithm.geneticAlgorithm.getRandom());
            else
                par.unShuffle();
            for (int c = 0; c < n; c++)
                children[c].set(parentsc[par.next()].get());
            return children;
        } 
    
        public String toString() {
            return data + "";
        } 
    }

    /**
     * Bounded integer gene data.
     * <p>This implementation uses an {@link java.lang.Integer} bounded to a specified range as gene data.</p>
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @invariants data &isin; [min, max]
     */
    public static class BoundedInteger extends Integer {
    
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long          serialVersionUID = -8621283398816731542L;
        
        /**
         * lower bound for value.
         * @serial
         */
        private int min;
        /**
         * upper bound for value.
         * @serial
         */
        private int max;
        
        public BoundedInteger(int data, int min, int max) {
            super(trim(data, min, max));
            this.min = min;
            this.max = max;
        }
        public BoundedInteger(int min, int max) {
            this(java.lang.Integer.MIN_VALUE, min, max);
        }

        public Object clone() {
            return new BoundedInteger(intValue(), min, max);
        } 

        public int hashCode() {
            return intValue() ^ (min << 1) ^ (max >> 1);
        }

        public boolean equals(Object o) {
            if (o instanceof BoundedInteger) {
                BoundedInteger b = (BoundedInteger) o;
                return intValue() == b.intValue() && min == b.min && max == b.max;
            } 
            return false;
        } 


        /**
         * somewhat like the heavyside function cutting value to [min,max]
         */
        private static int trim(int value, int min, int max) {
            if (value < min)
                return min;
            else if (value > max)
                return max;
            else
                return value;
        }
    
        // get/set methods
    
        /**
         * Get the lower bound for value.
         */
        public int getMin() {
            return min;
        }
        /**
         * Get the upper bound for value.
         */
        public int getMax() {
            return max;
        }
        
        public void set(int n) {
            super.set(trim(n, min ,max));
        } 
    
        // central virtual methods
        // transformation methods
    
        /**
         * @todo implement with less deviation
         */
        /*public Gene mutate(double probability) {
          if (!MathUtilities.isProbability(probability))
          throw new IllegalArgumentException("invalid probability " + probability);
          Integer n = (Integer) clone();
          if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), probability))
          //XXX: use less deviation when close to min or max
          n.set((int) (n.data * (1 + GeneticAlgorithm.geneticAlgorithm.getRandom().nextGaussian() * relativeDeviation)));
          return n;
          }*/
    }
    
    /**
     * Floating point gene data.
     * <p>This implementation uses a floating-point {@link java.lang.Double} as gene data.</p>
     * <p>
     * {@link java.lang.Double#NaN}, {@link java.lang.Double#NEGATIVE_INFINITY} and
     * {@link java.lang.Double#POSITIVE_INFINITY} are immune to mutation and
     * thus fixed points (apart from recombination).
     * </p>
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    public static class Float extends Number {
    
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long          serialVersionUID = -2522837867138188120L;
    
        /**
         * The Gene data represented as a number.
         * @serial
         */
        private double data;
    
        public Float(double data) {
            this.data = data;
        }
        /**
         * Construct a non-initialized Float.
         * <p>
         * <b>Note:</b> non-initialized (that is {@link java.lang.Double#NaN}) Floats are immune to mutation
         * which is no good starting-point for creating a population.</p>
         */
        public Float() {
            //XXX: which to use? Remember mutation must work with it
            this(java.lang.Double.NaN);
        }
    
        public Object clone() {
            return new Float(data);
        } 
    
        // get/set methods
    
        public Object/*java.lang.Double*/ get() {
            return new java.lang.Double(doubleValue());
        }
        public double doubleValue() {
            return data;
        }
        
        public void set(Object/*java.lang.Double*/ n) {
            set(((java.lang.Number) n).doubleValue());
        } 
        public void set(double n) {
            if (Double.isNaN(n))
                throw new InternalError("NaN is no good idea for a gene, it's an attractor");
            data = n;
        } 
    
        // central virtual methods
        // transformation methods
    
        public Gene[] recombine(final Gene[] parents, int childrenCount, double recombinationProbability) {
            if (!MathUtilities.isProbability(recombinationProbability))
                throw new IllegalArgumentException("invalid probability " + recombinationProbability);
            Float[]       parentsc = (Float[]) parents;
            Float[]       children = new Float[childrenCount];
            final int     a = parentsc.length;    // a
            final int     n = children.length;    // n
            final double  p = a;                                  // p
    
            // create new children gene objects
            for (int i = 0; i < children.length; i++)
                //TODO: optimize: filling .data is not required here
                children[i] = (Float) clone();

            UniqueShuffle par = new UniqueShuffle(a);
            if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), recombinationProbability))
                if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), recombinationProbability)) {
                    // recombine randomly leads to intermediary arithmetic mean of parents?
                    double parentalValues[] = new double[a];
                    for (int i = 0; i < parentalValues.length; i++)
                        parentalValues[i] = parentsc[i].doubleValue();
                    for (int c = 0; c < n; c++) {
                        float rnd = GeneticAlgorithm.geneticAlgorithm.getRandom().nextFloat();
                        if (rnd >= 2/3f)
                            children[c].set(Stat.arithmeticMean(parentalValues));
                        else if (rnd >= 1/3f)
                            children[c].set(Stat.harmonicMean(parentalValues));
                        else {
                            double v = Stat.geometricMean(parentalValues);
                            if (!Double.isNaN(v))
                                children[c].set(v);
                            else if (rnd >= 1/2f)
                                children[c].set(Stat.arithmeticMean(parentalValues));
                            else
                                children[c].set(Stat.harmonicMean(parentalValues));
                        }
                    }
                    return children;
                }
                else
                    par.reShuffle(GeneticAlgorithm.geneticAlgorithm.getRandom());
            else
                par.unShuffle();

            // uniformly distribute Gene data of all parents over the children
            for (int c = 0; c < n; c++)
                children[c].set(parentsc[par.next()].get());
            return children;
        } 
    }

    /**
     * Bounded floating point gene data.
     * <p>This implementation uses a floating-point {@link java.lang.Double} bounded to a specified range as Gene data.</p>
     * 
     * @version $Id$
     * @author  Andr&eacute; Platzer
     * @invariants data &isin; [min, max]
     */
    public static class BoundedFloat extends Float {
    
        /**
         * version of this class for versioning with serialization and deserialization.
         */
        private static final long          serialVersionUID = -7547201720957271215L;
        
        /**
         * lower bound for value.
         * @serial
         */
        private double min;
        /**
         * upper bound for value.
         * @serial
         */
        private double max;
        
        public BoundedFloat(double data, double min, double max) {
            super(trim(data, min, max));
            this.min = min;
            this.max = max;
        }
        /**
         * Construct a <em>non-initialized</em> BoundedFloat.
         * <p>
         * <b>Note:</b> non-initialized (that is {@link java.lang.Double#NaN}) Floats are immune to mutation
         * which is no good starting-point for creating a population.
         * </p>
         */
        public BoundedFloat(double min, double max) {
            //XXX: which to use? Remember mutation must work with it
            this(java.lang.Double.NaN, min, max);
        }

        public Object clone() {
            return new BoundedFloat(doubleValue(), min, max);
        } 
    
        /**
         * Checks for equality.
         */
        public boolean equals(Object o) {
            if (o instanceof BoundedFloat) {
                BoundedFloat b = (BoundedFloat) o;
                return this.get().equals(b.get()) && min == b.min && max == b.max;
            } 
            return false;
        } 

        public int hashCode() {
            int hashCode = super.hashCode();
            //@see Double#hashCode()
            long bits = java.lang.Double.doubleToLongBits(min) << 1;
            hashCode ^= (int)(bits ^ (bits >>> 32));
            //@see Double#hashCode()
            bits = java.lang.Double.doubleToLongBits(max) >> 1;
            hashCode ^= (int)(bits ^ (bits >>> 32));
            return hashCode;
        }
    
        /**
         * somewhat like the heavyside function cutting value to [min,max]
         */
        private static double trim(double value, double min, double max) {
            if (value < min)
                return min;
            else if (value > max)
                return max;
            else
                return value;
        }
    
        // get/set methods
    
        public void set(double n) {
            if (Double.isNaN(n))
                throw new InternalError("NaN is no good idea for a gene, it's an attractor");
            super.set(trim(n, min ,max));
        } 
    
        /**
         * Get the lower bound for value.
         */
        public double getMin() {
            return min;
        }
        
        /**
         * Get the upper bound for value.
         */
        public double getMax() {
            return max;
        }

        // central virtual methods
        // transformation methods
    
        /**
         * @todo implement with less deviation
         */
        /*public Gene mutate(double probability) {
          if (!MathUtilities.isProbability(probability))
          throw new IllegalArgumentException("invalid probability " + probability);
          //TODO: optimize: filling .data is not required here
          Float n = (Float) clone();
          if (Utility.flip(GeneticAlgorithm.geneticAlgorithm.getRandom(), probability))
          //XXX: use less deviation when close to min or max
          n.set(n.data * (1 + GeneticAlgorithm.geneticAlgorithm.getRandom().nextGaussian() * relativeDeviation));
          return n;
          } */
    }

    /**
     * Fixed point gene data.
     * <p>This implementation uses a {@link Gene.BitSet} encoded fixed-point numbers as gene data.</p>
     * <p>
     * Note that default uniform mutations and recombinations have a dramatic effect on
     * bit string encoded values.
     * </p>
     * 
     * @version $Id$
     * @author Andr&eacute; Platzer
     * @see Gene.Number
     */
    public static class Fixed extends Gene.BitSet {
        private static final long serialVersionUID = 7829878649179715782L;
        /**
         * The number of bits for the integer part.
         * @serial
         */
        private final int                integerGranularity;
        /**
         * The number of bits for the fractional part.
         * @serial
         */
        private final int                fractionalGranularity;
        /**
         * The number of bits for the sign part.
         */
        private static final int signGranularity = 1;
    
        public Fixed(int integerGranularity, int fractionalGranularity) {
            super(signGranularity + fractionalGranularity + integerGranularity);
            this.integerGranularity = integerGranularity;
            this.fractionalGranularity = fractionalGranularity;
        }

        public Object/*java.lang.Double*/ get() {
            return new java.lang.Double(interpretDouble());
        }
        
        public void set(Object/*java.lang.Double*/ n) {
            encodeDouble(((java.lang.Double) n).doubleValue());
        } 

        /**
         * Get the number of bits for the integer part.
         */
        protected int getIntegerGranularity() {
            return integerGranularity;
        }
        
        /**
         * Get the number of bits for the fractional part.
         */
        protected int getFractionalGranularity() {
            return fractionalGranularity;
        }
        
        /**
         * Get the number of bits for the sign part.
         */
        protected int getSignGranularity() {
            return signGranularity;
        }
        

        /** 2-adische Entwicklung */ 
        private double interpretDouble() {
            int position = 0;
            double r = 0.0;
            for(int i=1;i<=fractionalGranularity;i++) {
                if (this.get(position++))
                    r += 1./(1<<i);
            }
            for(int i=0;i<integerGranularity;i++) {
                if (this.get(position++))
                    r += 1<<i;
            }
            if (this.get(position++))
                r *= -1;
            return r;
        }

        /** 2-adischer Wert */ 
        private void encodeDouble(double r) {
            int position = 0;
            boolean sgn = r<0;
            if (sgn) r*=-1;
            for(int i=1;i<=fractionalGranularity;i++) {
                this.set(position++, ((long)(r*(1<<i))&1)==1);
            }
            for(int i=0;i<integerGranularity;i++) {
                this.set(position++, (((long)r>>i)&1)==1);
            }
            this.set(position++, sgn);
        }
 
        /** 2-adischer Wert */ 
        private double interpretFract() {
            int position = 0;
            double r = 0.0;
            for(int i=1;i<=fractionalGranularity;i++) {
                if (this.get(position++))
                    r += 1./(1<<i);
            }
            if (this.get(position++))
                r *= -1;
            return r;
        }
        /** 2-adische Entwicklung */ 
        private void encodeFract(double r) {
            int position = 0;
            boolean sgn = r<0;
            if (sgn) r*=-1;
            for(int i=1;i<=fractionalGranularity;i++) {
                this.set(position++,((long)(r*(1<<i))&1)==1);
            }
            this.set(position++,sgn);
        }
        /** 2-adischer Wert */ 
        private int interpretInt() {
            int position = 0;
            int r = 0;
            for(int i=0;i<integerGranularity;i++) {
                if (this.get(position++))
                    r += 1<<i;
            }
            if (this.get(position++))
                r *= -1;
            return r;
        }
        /** 2-adische Entwicklung */ 
        private void encodeInt(int r) {
            int position = 0;
            boolean sgn = r<0;
            if (sgn) r*=-1;
            for(int i=0;i<integerGranularity;i++) {
                this.set(position++,((r>>i)&1)==1);
            }
            this.set(position++,sgn);
        }
    }
}
