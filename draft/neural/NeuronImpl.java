/**
 * @(#)NeuronImpl.java 0.9 2001/05/23 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */
 
import java.io.Serializable;
import java.util.Collection;
import orbital.math.functional.Function;

import dkfz.collections.graph.Edge;

import java.util.Iterator;
import orbital.math.Arithmetic;
import orbital.math.Real;
import orbital.math.Values;

/**
 * Usual active neuron for neural networks.
 *
 * @version 0.9, 2001/05/23
 * @author  Andr&eacute; Platzer
 */
public
class NeuronImpl extends Neuron.Abstract {
	private static final long serialVersionUID = -7182672999891715715L;
	/**
	 * The neural network that we are contained in.
	 * Note that this is somehow duplicate to the graph structure in some graphs,
	 * namely for a MatrixVertex.
	 * @serial
	 */
	NeuralNetwork network;
	
	/**
	 * the potential u<sub>i</sub> of the last activation.
	 * Cache variable.
	 */
	private transient Arithmetic potential;

	/**
	 * part of the relative error term &Delta;<sub>i</sub> of the last training example.
	 * Cache variable.
	 * <p>
	 * &Delta;<sub>i</sub> = &phi;'(u<sub>i</sub>) * delta</p>
	 */
	private transient Arithmetic delta;
	
    public NeuronImpl() {}
    
    // get/set methods
    
    /**
     * Get the nonlinear part of the activation function.
     * <p>
     * Note that &phi; is not the full activation function, but only the nonlinear part of it.
     * The full activation function depends upon the activity in the full cell neighbourhood
     * and is performed by {@link #activate(Collection)}.</p>
     * @return &phi;:<b>R</b>&rarr;<b>R</b>.
     * @see NeuralNetwork#getActivationFunction()
     */
    protected Function getActivationFunction() {
    	return network.getActivationFunction();
	}
    
    Arithmetic getPotential() {
    	return potential;
    }
    private void setPotential(Arithmetic u) {
    	this.potential = u;
    }

    Arithmetic getDelta() {
    	return delta;
    }
    void setDelta(Arithmetic delta) {
    	this.delta = delta;
    }
    
    /**
     * Apply the activation function &alpha;<sub>i</sub>(<span class="vector">a</span>) = &phi;(<span class="matrix">W</span><sub>i</sub>&sdot;<span class="vector">a</span>).
     * <p>
     * <ul>
     *   <li>&alpha;<sub>i</sub> defines the future activity in terms of the activities
     *   of the neurons in the local neighbourhood of linked neurons.</li>
     *   <li><span class="vector">a</span> is the vector of activity levels of the input neurons.</li>
     *   <li><span class="matrix">W</span><sub>i</sub> is the i-th row vector of the weight matrix <span class="matrix">W</span>.
     *   Thus it contains the weights of all incomming links from neurons that influence
     *   this neuron.</li>
     *   <li>u<sub>i</sub> = <span class="matrix">W</span><sub>i</sub>&sdot;<span class="vector">a</span> is the potential at this neuron.</li>
     *   <li>The nonlinear part &phi; of the activation function is defined by {@link #getActivationFunction()}.</li>
     * </ul></p>
     * <p>
     * The real calculation a<sub>i</sub> := &alpha;<sub>i</sub>(<span class="vector">a</span>) of the future activity might be performed concurrently.</p>
     * @todo optimizable
     */
    public void activate(Collection/*<Edge>*/ presynaptic) {
    	// potential u = <span class="matrix">W</span><sub>i</sub> &sdot; a
    	// dot-product Functionals.foldRight(Operations.plus, Values.valueOf(0), ...)
    	Arithmetic u = Values.ZERO;
    	for (Iterator i = presynaptic.iterator(); i.hasNext(); ) {
    		Edge e = (Edge) i.next();
    		u = u.add(((Arithmetic) e).multiply((Arithmetic) e.getFromVertex().getObject()));
    	}
    	
    	// &alpha;<sub>i</sub>(a) = &phi;(u)
    	setActivity((Real) getActivationFunction().apply(u));
    	// cache for derivative &phi;'(u)
   		setPotential(u);
    	// reset delta error for deferred partial summation @see Backpropagation#learn(Vector,Vector)
    	setDelta(Values.ZERO);
    }

}