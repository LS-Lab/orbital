/**
 * @(#)NeuralNetwork.java 0.9 2001/05/23 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import dkfz.collections.graph.Vertex;
import java.io.Serializable;

import orbital.math.functional.Function;

import orbital.math.Vector;
import orbital.math.Arithmetic;
import orbital.math.Real;
import orbital.math.Values;

import java.util.Collection;
import java.util.Iterator;

import orbital.util.Utility;

/**
 * Representation of artificial neural networks.
 * <p>
 * A neural network can be used as an implicit representation of a function to approximize
 * and has well-known learning algorithms that enable it to learn inductive from examples.
 * Neural networks are an numeric approach from connectionist theory.
 * Indeed, a neural network itself is nothing but a regular though complex composition of simple functions.
 * <p>
 * In essence, neural networks learning algorithms are a kind of attribute-based learning
 * that perform nonlinear regression, tuning parameters to fit data in the training set.
 * Neural networks are rather famous, not only due to their natural counterpart,
 * but also because they usually generalize examples and are quite immune to noise in
 * example data.</p>
 * <p>
 * However, back-propagation for multilayer networks is neither efficient nor guaranteed to
 * converge to a global optimum.
 * Computational learning theory shows that learning general functions by induction is an
 * intractable problem in the worst case, regardless of the method applied.</p>
 * <blockquote>
 *     "neural networks are the second best way of doing just about anything" (Denker)
 * </blockquote></p>
 * <p>
 * The number of degrees of freedom in a neural network (i.e. the number of weights) is
 * called its plasticity. A higher plasticity decreases the overall error of a training set,
 * whereas a lower plasticity decreases the error of interpolation.
 * So a trade-off between correct learning of samples and a good generalization must be found.</p>
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see "Hertz et al. Introduction to the Theory of Neural Computation. Addison-Wesley, Reading, Massachusetts. 1991."
 * @see "Kohonen, T. Self-Organization and Associative Memory. Springer-Verlag, Berlin, third edition. 1989."
 * @see "Judd, J. S. Neural Network Design and the Complexity of Learning. MIT Press, Cambridge, Massachusetts. 1990."
 * @see "McCullach, W. S. and Pitts, W. A logical calculus of the ideas immanent in nervous activity. Bulletin of Mathematical Biophysics, 5:115-137, 1943."
 */
public
abstract class NeuralNetwork extends MatrixGraph implements orbital.logic.functor.Function/*<Vector, Vector>*/, Serializable {
	private static final long serialVersionUID = 175158501123254510L;
	/**
	 * the learning rate &lambda;.
	 * @serial
	 */
	private double  learningRate = java.lang.Double.NaN;
	
    /**
     * The nonlinear part of the default activation function.
     * <p>
     * The default activation function is used for all neurons that do not have a specific
     * function set.</p>
     * @serial
     */
    private Function activationFunction;
	
    protected NeuralNetwork() {}

    protected NeuralNetwork(int capacity) {
        super(capacity);
    }
    
    /**
     * Neural network shallow copy constructor.
     */
    protected NeuralNetwork(NeuralNetwork b) {
    	super(b.getVertices(), b.getEdges());
    	setLearningRate(b.getLearningRate());
    	setActivationFunction(b.getActivationFunction());
    }
    
    /**
     * Compares two neural networks for equality.
     * Two neural networks are equal, if they have the same graph structure and activation function,
     * regardless of the learning rate.
     */
    public boolean equals(Object o) {
    	return (o instanceof NeuralNetwork) && super.equals(o)
    			&& Utility.equals(getActivationFunction(), ((NeuralNetwork) o).getActivationFunction());
    }
    
    public int hashCode() {
    	return super.hashCode() ^ Utility.hashCode(getActivationFunction());
    }
    
    // Get/set methods
    
    /**
     * Get the nonlinear part of the default activation function.
     * <p>
     * The default activation function is used for all neurons that do not have a specific
     * function set.</p>
     * <p>
     * Note that &phi; is not the full activation function, but only the nonlinear part of it.
     * The full activation function depends upon the activity in the full cell neighbourhood
     * and is performed by {@link Neuron#activate(Collection)}.</p>
     * @return &phi;:<b>R</b>&rarr;<b>R</b>.
     * @see NeuronImpl#getActivationFunction()
     */
    public Function getActivationFunction() {
    	return activationFunction;
	}
    /**
     * Set the nonlinear part of the default activation function.
     * <p>
     * The default activation function is used for all neurons that do not have a specific
     * function set.</p>
     * <p>
     * Note that &phi; is not the full activation function, but only the nonlinear part of it.
     * The full activation function depends upon the activity in the full cell neighbourhood
     * and is performed by {@link Neuron#activate(Collection)}.</p>
     * @return &phi;:<b>R</b>&rarr;<b>R</b>.
     * @see NeuronImpl#getActivationFunction()
     * @see orbital.math.functional.Function.Functions#sigmoid
     * @see orbital.math.functional.Function.Functions#sign
     * @see orbital.math.functional.Function.Functions#step(Comparable)
     * @see orbital.math.functional.Function.Functions#sin
     * @see orbital.math.functional.Function.Functions#tanh
     * @see orbital.math.functional.Function.Functions#arctan
     */
    public void setActivationFunction(Function phi) {
    	this.activationFunction = phi;
	}

	/**
	 * Get the learning rate &lambda;.
	 * @return &lambda;
	 */
	public double getLearningRate() {
		return learningRate;
	}
	/**
	 * Set the learning rate &lambda;.
	 * @param lambda the learning rate to apply, usually a value in (0,1].
	 *  A value of 0 disables learning.
	 *  While a value of 1 will usually lead the learning algorithm to memorize the last
	 *  example, only.
	 */
	public void setLearningRate(double lambda) {
		this.learningRate = lambda;
	}
	
	/**
	 * Get a measure of global network activity.
	 * <p>
	 * Global network activity can be useful for investigating convergence or construction information.</p>
	 * @return k(<span class="vector">a</span>) := 1/2 * <span class="vector">a</span>&sdot;span class="matrix">W</span>*<span class="vector">a</span> - <span class="vector">w<sub>0</sub></span>&sdot;<span class="vector">a</span>.
	 *  Where <span span class="vector">w<sub>0</sub></span> is the vector of bias weights.
	 * @todo require one single "on"-neuron to get the bias vector from?
	 */
	public double getActivity() {
		Collection vertices = getVertices();
		// activity vector <span class="vector">a</span>
		Vector a = Values.getDefault().newInstance(vertices.size());
		int j = 0;
		for (Iterator i = vertices.iterator(); i.hasNext(); j++)
			a.set(j, (Arithmetic) ((Vertex) i.next()).getObject());
		
		//@todo what's W? the matrix of weights including bias? Not really, ho?
		//return Values.valueOf(1/2.).multiply(a.multiply(W.multiply(a))).add(w0.multiply(a));
		throw new UnsupportedOperationException("not yet implemented @todo");
	}

	/**
	 * Calculate the network error vector for an example.
	 * <p>
	 * Use <pre>
	 * <span class="Orbital">Vector</span> error <span class="operator">=</span> getError(input, expectedOutput);
	 * <span class="keyword">double</span> lms <span class="operator">=</span> error.multiply(error);
	 * </pre>
	 * to determine the least mean square error of an example.</p>
	 * @param input the training input vector I<sup>(e)</sup> of this example e.
	 * @param expectedOutput the expected training output vector T<sup>(e)</sup> of this example e.
	 * @return T<sup>(e)</sup> - O<sup>(e)</sup>, after applying the mapping I<sup>(e)</sup> &#8614; O<sup>(e)</sup> to the output vector as computed by the current neural network.
	 */
	protected Vector getError(Vector input, Vector expectedOutput) {
		// compute the output for the given example input
		Vector output = (Vector) apply(input);
		// compute the error
		return expectedOutput.subtract(output);
	}

    public boolean add(Vertex v) {
    	if (v.getObject() instanceof NeuronImpl)
    		((NeuronImpl) v.getObject()).network = this;
    	return super.add(v);
    }

    public boolean remove(Vertex v) {
    	if (v.getObject() instanceof NeuronImpl)
    		((NeuronImpl) v.getObject()).network = null;
    	return super.remove(v);
    }

	/**
	 * Applies this neural network to an input vector.
	 * <p>
	 * <b>Note</b>: the semantics of running a neural network depend heavily on the order
	 * in which the neurons are activated. This class poses no general restriction or invariant
	 * on the order of neurons, leaving this to the sub classes' discretion.
	 * However, if a sub class does not care about a valid order, the results of applying
	 * this method become increasingly nondeterministic.</p>
	 * @param input the input {@link Vector} to fill into the neurons of the input layer.
	 * @return the resulting output {@link Vector} computed in the neurons of the output layer
	 *  after running the network.
	 */
	public abstract Object/*>Vector<*/ apply(Object/*>Vector<*/ input);

	
    // Utilities
    
    /**
     * An artificial bias neuron (so called "on"-neuron).
     * <p>
     * An "on"-neuron is an artificial neuron with a fixed activity of <code style="whitespace: nowrap">-1</code>
     * that is used as input to a neuron in place of additional bias values or threshold values.
     * Using "on"-neurons instead of explicit bias values for each neuron simplifies notation
     * and prevents additional bias calculation code, although it results in additional
     * edges that may complicate visual views.</p>
     *
     * @version $Id$
     * @author  Andr&eacute; Platzer
     */
    protected static final class Bias extends Neuron.ConstantNeuron {
    	private static final long serialVersionUID = 2282052612834943609L;
        public Bias() {
            super(-1);
        }
        
        // get/set methods
        
        public Real getActivity() {
        	return Values.getDefault().valueOf(-1);
        }
       
        public void setActivity(Real a) {
        	throw new UnsupportedOperationException("fixed artificial activity of on neurons cannot be changed");
        }

   		public String toString() {
			return "(" + this.getActivity() + ")";
		}
    }

	/**
	 * A link (Edge) that does weight-sharing with other links.
	 * 
	 * @version $Id$
	 * @author  Andr&eacute; Platzer
	 * @see #createWeightSharing(Vertex, Vertex)
	 * @see "Rumelhart et al. 1986"
	 * @see "Le Cun et al. 1989"
	 * @todo check that this construction really should update its weight n times during back-propagation if it is shared with n links.
	 */
	static class WeightSharingEdge extends MatrixGraph.ArithmeticEdge implements Arithmetic, Serializable {
		//@todo private static final long serialVersionUID = -7539784203267770666L;

		/**
		 * A holder container for the real content object.
		 * Such a holder is shared per reference among weight-sharing links.
		 */
		private static class Holder {
			Object content;
			public Holder(Object o) {
				this.content = o;
			}
			public Holder() {}

    		public Object getObject() {
    			return content;
    		}
    
    		public void setObject(Object o) {
    			this.content = o;
    		}
		}

		/**
		 * Create a new link that is ready for weight-sharing.
		 */
		public WeightSharingEdge(Vertex from, Vertex to) {
			super(from, to, new Holder());
		}
		/**
		 * Create a new link that is ready for weight-sharing.
		 */
		public WeightSharingEdge(Vertex from, Vertex to, Object o) {
			super(from, to, new Holder(o));
		}
		private WeightSharingEdge(Vertex from, Vertex to, Holder sharingHolder) {
			super(from, to, sharingHolder);
		}
		
		/**
		 * Create a link that is weight-sharing with this link.
		 * @return a new Edge that is sharing its weight with this object.
		 */
		public WeightSharingEdge createWeightSharing(Vertex from, Vertex to) {
			return new WeightSharingEdge(from, to, (Holder) getObject());
		}
		
		public Object getObject() {
			return ((Holder) super.getObject()).getObject();
		}

		public void setObject(Object o) {
			((Holder) super.getObject()).setObject(o);
		}
	}
}
