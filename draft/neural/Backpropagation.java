/**
 * @(#)Backpropagation.java 0.9 2001/05/25 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import dkfz.collections.graph.Vertex;
import dkfz.collections.graph.Edge;
import dkfz.collections.graph.Graph;
import dkfz.collections.graph.GraphFactory;

import orbital.logic.functor.MutableFunction;

import orbital.math.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import orbital.math.Arithmetic;
import orbital.math.Values;

import java.util.Random;
import orbital.math.MathUtilities;

/**
 * A multilayer feed-forward network with back-propagation learning.
 * <p>
 * Back-propagation is a gradient descent search in weight space.
 * It is a local and supervised method of learning a neural network representation.</p>
 * <p>
 * Note that back-propagation is not limited to feed-forward networks but could do for
 * recursive networks as well. In any case, the semantics have not been proven that well-founded,
 * then.</p>
 *
 * @version 0.9, 2001/05/25
 * @author  Andr&eacute; Platzer
 * @invariant vertices are kept in order of calculation that is in order of their layers.
 * @see "Bryson, A. E. and Ho, Y.-C. (1969). Applied Optimal Control. Blaisdell, New York."
 */
public class Backpropagation extends FeedForwardNeuralNetwork implements MutableFunction/*<Vector, Vector>*/ {
    public Backpropagation() {}

    public Backpropagation(int capacity) {
        super(capacity);
    }

    /**
     * Neural network shallow copy constructor.
     * @see FeedForwardNeuralNetwork#FeedForwardNeuralNetwork(NeuralNetwork)
     */
    public Backpropagation(NeuralNetwork b) {
    	super(b);
    }

    public Object clone() throws CloneNotSupportedException {
	throw new CloneNotSupportedException("Graph not clonable");
    }

    public Object/*>Vector<*/ set(Object/*>Vector<*/ input, Object/*>Vector<*/ expectedOutput) {
	// compute the output for the given example input
	Vector output = (Vector) apply(input);
	// compute the error
	Vector error = ((Vector) expectedOutput).subtract(output);
	// optimized call to {@link #learn(Vector, Vector) learn}(input, expectedOutput)
	learn(error);
	return output;
    }
	
    /**
     * General learn method.
     * @param input the input part of the example to learn.
     *  If input is a {@link Vector}, performs one step of learning the example.
     *  Otherwise if input is a {@link Vector Vector[]}, performs a step of learning all given examples.
     * @param expectedOutput the desired output part of the example to learn.
     * @pre input.getClass().equals(expectedOutput.getClass())
     */
    public void learn(Object input, Object expectedOutput) {
	if ((input instanceof Vector) && (expectedOutput instanceof Vector))
	    learn((Vector) input, (Vector) expectedOutput);
	else if ((input instanceof Vector[]) && (expectedOutput instanceof Vector[]))
	    learn((Vector[]) input, (Vector[]) expectedOutput);
	else
	    throw new ClassCastException("illegal argument types " + input.getClass().getName() + " and " + expectedOutput.getClass().getName());
    }

    /**
     * Perform one step of learning an example with back-propagation.
     * <p>
     * <ol>
     *   <li>Apply the network to calculate the mapping I<sup>(e)</sup> &#8614; O<sup>(e)</sup> to the output vector as computed by the current neural network.</li>
     *   <li>Calculate the relative error term &Delta;<sub>i</sub> for the neuron i, per
     *     <ul>
     *       <li>&Delta;<sub>i</sub> = &phi;'(u<sub>i</sub>) * (T<sup>(e)</sup><sub>i</sub> - O<sup>(e)</sup><sub>i</sub>) at the output layer.</li>
     *       <li>&Delta;<sub>i</sub> = &phi;'(u<sub>i</sub>) * &sum;<sub>k</sub> <span class="matrix">W</span><sub>i,k</sub>*&Delta;<sub>k</sub> at subsequent hidden layers.</li>
     *     </ul>
     *   Where u<sub>i</sub> = <span class="matrix">W</span><sub>i</sub>&sdot;<span class="vector">a</span> is the cached potential at the neuron i.
     *   Due to the second case for subsequent hidden layers, the error terms &Delta;<sub>i</sub>
     *   will be propagated back from the output layer to the input layer.</li>
     *   <li>
     *     Update the weights of the link from j to i as
     *     <center><span class="Formula"><span class="matrix">W</span><sub>j,i</sub> := <span class="matrix">W</span><sub>j,i</sub> + &lambda;*<span class="vector">a</span><sub>j</sub>*&Delta;<sub>i</sub></span></center>
     *     Once all relative error terms &Delta;<sub>i</sub> have been propagated.
     *   </li>
     * </ol>
     * </p>
     * <p>
     * This implementation uses deferred partial summation for the back-propagation of the
     * relative error terms &Delta;<sub>i</sub> to speed up the calculation.
     *      <center><span class="Formula">&Delta;<sub>j</sub> := &Delta;<sub>j</sub> + <span class="matrix">W</span><sub>j,i</sub> * &Delta;<sub>i</sub></span></center>
     * while calculating the weight updates for the nodes i.</li>
     * </p>
     * <p>
     * Sequentially calling this method with all examples as vectors until convergence
     * leads to on-line training.</p>
     * @param input the training input vector I<sup>(e)</sup> of this example e.
     * @param expectedOutput the expected training output vector T<sup>(e)</sup> of this example e.
     * @internal see #learn(Vector)
     */
    public void learn(Vector input, Vector expectedOutput) {
	if (getLearningRate() == 0)
	    return;

	// compute the error
	learn(getError(input, expectedOutput));
    }

    /**
     * Perform one step of learning an example with back-propagation.
     * <p>
     * Implementation method (for increased performance in some situations).
     * This method can be called in place of {@link #learn(Vector)} in those rare occasions
     * where the error will depend upon the computed output value. Thus O<sup>(e)</sup>
     * will have already been computed prior to calling this method.
     * Note that this is a strict prerequisite.</p>
     * @param error the difference T<sup>(e)</sup> - O<sup>(e)</sup>
     * @pre called after applying the mapping I<sup>(e)</sup> &#8614; O<sup>(e)</sup> to the output vector as computed by the current neural network.
     * @internal see #learn(Vector, Vector)
     * @todo optimizable
     */
    void learn(Vector error) {
	// compute errors and distribute error terms to the output layer: &Delta;<sub>i</sub> = T<sup>(e)/<sup><sub>i</sub> - O<sub>i</sub>
	int	   eI = 0;
	for (Iterator i = getOutputLayer().iterator(); i.hasNext(); eI++) {
	    Vertex	   v = (Vertex) i.next();
	    Arithmetic ei = error.get(eI);
	    assert v.getObject() instanceof NeuronImpl : "output layer consists of full-blown NeuronImpl";
	    NeuronImpl n = (NeuronImpl) v.getObject();
	    n.setDelta(ei);
	}

	// back-propagate error and update weights leading to the subsequent layers, starting at the output layer
	List   vertices = (List) getVertices();
	for (ListIterator i = vertices.listIterator(vertices.size()); i.hasPrevious(); ) {
	    Vertex	   v = (Vertex) i.previous();
	    if (!(v.getObject() instanceof NeuronImpl)) {
		assert getToEdges(v).size() == 0 : "active layers consist of full-blown NeuronImpl";
		continue;
	    }
	    NeuronImpl n = (NeuronImpl) v.getObject();
	    Arithmetic phid = (Arithmetic) n.getActivationFunction().derive().apply(n.getPotential());
	    Arithmetic deltai = phid.multiply(n.getDelta());
	    // the following assertion is only true if the weights Wji had not yet been updated, but only delta has been back-propagated
	    /*Arithmetic t = null;
	      assert MathUtilities.equal(n.getDelta(),
	      i.nextIndex() >= getOutputLayerOffset() ? error.get(i.nextIndex() - getOutputLayerOffset())
	      : (t = (Arithmetic) errorSum(getFromEdges(v))), MathUtilities.DefaultTolerance) : "deferred partial summation must equal " + (i.nextIndex() >= getOutputLayerOffset() ? "output error" : "explicit error summation") + " " + ((Number) n.getDelta()).doubleValue() + "!=" + (t != null ? ((Number) t).doubleValue() + "" : "null"));*/
			
	    // update links leading to v
	    for (Iterator j = getToEdges(v).iterator(); j.hasNext(); ) {
		Edge	   e = (Edge) j.next();
		Arithmetic Wji = (Arithmetic) e.getObject();
		Neuron	   aj = (Neuron) e.getFromVertex().getObject();
		assert Wji != null : "links have weights";
				
		// first, back-propagate relative error, to active neurons
		// Beware: could we separate delta back-propagation from weight adaption, to prevent update from inflicting changes to the delta calculation?
		// performs deferred partial summation &Delta;<sub>j</sub> := &Delta;<sub>j</sub> + <span class="matrix">W</span><sub>j,i</sub> * &Delta;<sub>i</sub>
		// delta should already have been set to 0, once during activation phase. @see NeuronImpl#activate(Collection)
		if (aj instanceof NeuronImpl) {
		    assert ((NeuronImpl) aj).getDelta() != null : "active neurons have delta terms at " + i.nextIndex();
		    ((NeuronImpl) aj).setDelta(((NeuronImpl) aj).getDelta().add(Wji.multiply(n.getDelta())));
		}

		// second, update link weight to reduce error
		// change <span class="matrix">W</span><sub>j,i</sub> by &lambda;*<span class="vector">a</span><sub>j</sub>*&Delta;<sub>i</sub>*&phi;'(u<sub>i</sub>)
		update(e, Values.valueOf(getLearningRate()).multiply(aj).multiply(deltai));
	    }
	}
    }
    /**
     * explicit calculation of error sum.
     * @return &Delta;<sub>i</sub> = &phi;'(u<sub>i</sub>) * &sum;<sub>k</sub> <span class="matrix">W</span><sub>i,k</sub>*&Delta;<sub>k</sub>.
     */
    private static final Arithmetic errorSum(Collection edges) {
    	// dot-product
    	Arithmetic delta = Values.valueOf(0);
    	for (Iterator i = edges.iterator(); i.hasNext(); ) {
	    Edge e = (Edge) i.next();
	    delta = delta.add(((Arithmetic) e).multiply(((NeuronImpl) e.getToVertex().getObject()).getDelta()));
    	}
	return delta;
    }

    /**
     * update link weight to reduce error.
     * <p>
     * <span class="matrix">W</span><sub>j,i</sub> := <span class="matrix">W</span><sub>j,i</sub> + &Delta;<sub>j,i</sub>
     * </p>
     * @param e the link from j to i whose weight to adjust.
     * @param change the amount of change &Delta;<sub>j,i</sub> to apply to the weight.
     */
    protected void update(Edge e, Arithmetic change) {
	Arithmetic Wji = (Arithmetic) e.getObject();
	Wji = Wji.add(change);
	e.setObject(Wji);
    }

    /**
     * Perform one epoche of learning examples with back-propagation.
     * <p>
     * This implementation trains all examples in the vectors subsequently (so called "on-line training").
     * Another possibility would be to compute the sum of individiual weight update values for each
     * example and perform one single combined weight update, only (so called "batch" update).
     * The on-line updates risk oscillating around the real gradient, a lot.
     * However, if the training examples are selected randomly, then the individual computed
     * updates will equal the real gradient on average.</p>
     * @param input the list of training input vectors I<sup>(e)</sup> of the examples e.
     * @param expectedOutput the list of expected training output vectors T<sup>(e)</sup> of the examples e.
     * @pre input.length == expectedOutput.length
     * @see #learn(Vector, Vector)
     */
    public void learn(Vector input[], Vector expectedOutput[]) {
	if (input.length != expectedOutput.length)
	    throw new IllegalArgumentException("training set of examples must have same number of input vectors as of expected output vectors");
	if (getLearningRate() == 0)
	    return;
	for (int i = 0; i < input.length; i++)
	    learn(input[i], expectedOutput[i]);
    }

    public GraphFactory getGraphFactory() {
    	return graphFactory;
    }
    private static final GraphFactory graphFactory = new Factory(new Random());
    public static class Factory extends MatrixGraph.Factory {
    	protected Random random;
    	public Factory(Random random) {
	    this.random = random;
    	}
        public Vertex createVertex() {
	    // neurons as vertex-marking
	    return super.createVertex(new NeuronImpl());
    	}
        public Edge createEdge(Vertex from, Vertex to) {
	    Edge e = super.createEdge(from, to);
	    // link weights as edge-marking
	    // simply use random initialization in (-0.05, +0.05]
	    //@internal the initial network weights should be uniformly distributed and small, such that the activation function is in the "Schaltungsbereich".
	    e.setObject(Values.valueOf(0.05 - 0.1 * random.nextDouble()));
	    return e;
    	}
        public Graph createGraph() {
	    return new Backpropagation();
    	}
    	protected Graph newInstance(int capacity) {
	    return new Backpropagation(capacity);
    	}
    }
}
