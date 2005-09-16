/**
 * @(#)AlgorithmicTemplate.java 1.0 2000/07/07 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.algorithm.template;

import orbital.math.functional.Function;
import java.io.Serializable;
import java.util.Map;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import orbital.util.InnerCheckedException;

/**
 * Base interface for algorithmic template frameworks.
 * <p>
 * An <dfn>algorithmic template</dfn> is a class that implements a problem-solving algorithm in a
 * general way, such that it is applicable in a multitude of different problems with the same
 * essential structure. It solves this whole bunch of problems by providing a maximum of methods
 * with shared behaviour, and deferring the problem-specific part into a problem interface.
 * Each algorithmic template class has an associated hook interface which is a sub-interface of
 * {@link AlgorithmicProblem} and declares the wholes to fill.
 * So this hook interface will contain the part that distinguishes two individual problems of the
 * same common structure solved by the algorithmic template class.
 * In short, algorithmic templates decouple algorithms from the problems such that they the
 * solution procedure is independent of the specific problem at hand. They determine <em>how</em>
 * instances of the problem are solved in general, perhaps still providing some additional
 * parameters for adjusting the concrete problem solving approach.
 * </p>
 * <p>
 * Of course, algorithmic templates are more useful if they only require a very small
 * and almost declarative algorithmic problem hook.</p>
 * 
 * @structure aggregates problem:AlgorithmicProblem
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see AlgorithmicProblem
 * @see <a href="{@docRoot}/Patterns/Design/Strategy.html">&asymp;Strategy</a>
 */
public interface AlgorithmicTemplate/*<Problem extends AlgorithmicProblem, Solution extends Object>*/ {

    /**
     * Generic solve method for a given algorithmic problem.
     * @param p algorithmic problem hook class which must fit the concrete
     * algorithmic template framework implementation.
     * @return the solution to the problem p, or null if solving failed.
     * @see orbital.logic.functor.Function#apply(Object)
     */
    Object/*>Solution<*/ solve(AlgorithmicProblem/*>Problem<*/ p);

    /**
     * Measure for the asymptotic time complexity of the central solution operation in O-notation.
     * @return the function f for which the solve() method of this algorithm runs in O<big>(</big>f(n)<big>)</big>
     *  assuming the algorithmic problem hook to run in O(1).
     * @preconditions true
     * @postconditions RES == OLD(RES) && OLD(this) == this
     * @see #solve(AlgorithmicProblem)
     */
    Function complexity();

    /**
     * Measure for the asymptotic space complexity of the central solution operation in O-notation.
     * @return the function f for which the solve() method of this algorithm consumes memory with an amount in O<big>(</big>f(n)<big>)</big>
     *  assuming the algorithmic problem hook uses space in O(1).
     * @preconditions true
     * @postconditions RES == OLD(RES) && OLD(this) == this
     * @see #solve(AlgorithmicProblem)
     */
    Function spaceComplexity();


    /**
     * Default implementation of algorithmic configuration objects.
     * Does not yet provide any additional parameters which would really justify the use
     * of algorithmic configurations because of their benefits. Instead, this class is used
     * as the base class for such algorithmic configuration implementations.
     * @author Andr&eacute; Platzer
     * @version $Id$
     */
    public static /*abstract*/ class Configuration/*<Problem extends AlgorithmicProblem, Solution extends Object>*/
	implements AlgorithmicConfiguration/*<Problem,Solution>*/, Serializable {
        private static final long serialVersionUID = -3040364728746853685L;

        /**
         * @serial
         */
        private AlgorithmicProblem/*>Problem<*/ problem;
    
        /**
         * @serial
         */
        private Class algorithm;

        /**
         * Create a new configuration.
         * @param problem the problem to solve.
         * @param algorithm the class of the AlgorithmicTemplate to instantiate for solving the problem.
         */
        public Configuration(AlgorithmicProblem/*>Problem<*/ problem, Class algorithm) {
            this(problem, algorithm, AlgorithmicTemplate.class);
        }
        /**
         * Create a new configuration limited to certain classes of algorithms.
         * @param problem the problem to solve.
         * @param algorithm the class of the AlgorithmicTemplate to instantiate for solving the problem.
         * @param superClass the class which algorithm must be a subclass of.
         */
        protected Configuration(AlgorithmicProblem/*>Problem<*/ problem, Class algorithm, Class superClass) {
            if (!superClass.isAssignableFrom(algorithm))
                throw new IllegalArgumentException("subclasses of " + superClass + " expected");
            this.problem = problem;
            this.algorithm = algorithm;
        }
        
        public AlgorithmicProblem/*>Problem<*/ getProblem() {
            return problem;
        }
        
        public AlgorithmicTemplate getAlgorithm() {
            try {
                return (AlgorithmicTemplate) algorithm.newInstance();
            } catch (InstantiationException ex) {
                throw (IllegalStateException) new IllegalStateException("algorithm " + algorithm + " does not support nullary constructor").initCause(ex);
            } catch (IllegalAccessException ex) {
                throw (IllegalStateException) new IllegalStateException("algorithm " + algorithm + " does have an accessible nullary constructor").initCause(ex);
            } catch (ClassCastException ex) {
                throw new AssertionError("only subclasses of AlgorithmicTemplate have been accepted");
            }
        }

        public Object/*>Solution<*/ solve() {
            return getAlgorithm().solve(getProblem());
        }

        /**
         * Create a new flexible configuration.
         * Flexible implementation of algorithmic configuration objects with properties at runtime.
         * This implementation will dynamically set the given bean property values at runtime.
         * @param problem the problem to solve.
         * @param properties the property values used for configuring the algorithm.
         *  A Map&lt;String,Object&gt; mapping property names to their values.
         * @param algorithm the class of the AlgorithmicTemplate to instantiate for solving the problem.
         * @throws IntrospectionException if algorithm could not be introspected.
         */
        public static final /*<Problem extends AlgorithmicProblem, Solution extends Object>*/
	    Configuration/*<Problem, Solution>*/ flexible(AlgorithmicProblem/*>Problem<*/ problem, Map properties, Class algorithm) throws IntrospectionException {
            return new FlexibleConfiguration/*<Problem, Solution>*/(problem, properties, algorithm, AlgorithmicTemplate.class);
        }

        /**
         * Flexible implementation of algorithmic configuration objects with properties at runtime.
         * This implementation will dynamically set the given bean property values at runtime.
         *
         * @author Andr&eacute; Platzer
         * @version $Id$
         */
        private static class FlexibleConfiguration/*<Problem extends AlgorithmicProblem, Solution extends Object>*/
	    extends AlgorithmicTemplate.Configuration {
            private static final long serialVersionUID = 8767047546408218154L;

            /**
             * @serial
             */
            private Map/*<String,PropertyDescriptor>*/ beanProperties;
    
            /**
             * @serial
             */
            private Map/*<String,Object>*/ propertyValues;

            /**
             * Create a new configuration limited to certain classes of algorithms.
             * @param problem the problem to solve.
             * @param properties the property values used for configuring the algorithm.
             *  A Map&lt;String,Object&gt; mapping property names to their values.
             * @param algorithm the class of the AlgorithmicTemplate to instantiate for solving the problem.
             * @param superClass the class which algorithm must be a subclass of.
             * @throws IntrospectionException if algorithm could not be introspected.
             */
            protected FlexibleConfiguration(AlgorithmicProblem/*>Problem<*/ problem, Map properties, Class algorithm, Class superClass) throws IntrospectionException {
                super(problem, algorithm, superClass);
                this.propertyValues = properties;
                final Class beanClass = algorithm;
                BeanInfo info = Introspector.getBeanInfo(beanClass, Introspector.USE_ALL_BEANINFO);
                if (info == null)
                    throw new IntrospectionException("no BeanInfo for class: " + beanClass);
                PropertyDescriptor[] beanProperties = info.getPropertyDescriptors();
                if (beanProperties == null)
                    throw new IntrospectionException("no PropertyDescriptors for class: " + beanClass);

                this.beanProperties = new HashMap();
                for (int i = 0; i < beanProperties.length; i++)
                    this.beanProperties.put(beanProperties[i].getName(), beanProperties[i]);
            }
        
            public AlgorithmicTemplate getAlgorithm() {
                AlgorithmicTemplate algo = super.getAlgorithm();
                setAllProperties(algo);
                return algo;
            }

            private void setAllProperties(AlgorithmicTemplate algorithm) {
                for (Iterator i = propertyValues.entrySet().iterator(); i.hasNext(); ) {
                    Map.Entry e = (Map.Entry) i.next();
                    PropertyDescriptor property = (PropertyDescriptor) beanProperties.get(e.getKey());
                    if (property == null)
                        throw new IllegalStateException(algorithm + " does not support property " + e.getKey());
                    try {
                        if (property.getWriteMethod() == null)
                            throw new InnerCheckedException("read-only property " + e.getKey(), new Exception("read-only property " + e.getKey()));
                        property.getWriteMethod().invoke(algorithm, new Object[] {e.getValue()});
                    } catch (IllegalAccessException inner) {
                        throw new InnerCheckedException("no access to property write method for property " + e.getKey(), inner);
                    } catch (InvocationTargetException inner) {
                        if (inner.getTargetException() instanceof IllegalArgumentException)
                            throw (IllegalArgumentException) inner.getTargetException();
                        else
                            throw new InnerCheckedException("", inner);
                    }
                }
            }
        }// FlexibleConfiguration
        
    }
}
