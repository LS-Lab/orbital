/**
 * @(#)FeedForwardNeuralNetwork.java 0.9 2001/05/25 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import dkfz.collections.graph.Vertex;
import dkfz.collections.graph.Edge;
import dkfz.collections.graph.GraphFactory;

import orbital.math.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import orbital.util.SequenceIterator;
import orbital.math.Arithmetic;
import orbital.math.Real;
import orbital.math.Values;

import orbital.util.Utility;

/**
 * A multilayer feed-forward neural network.
 *
 * @version 0.9, 2001/05/25
 * @author  Andr&eacute; Platzer
 * @invariant vertices are kept in order of calculation that is in order of their layers.
 *  &and; <"on"-neuron> &cup; getInputLayer() &cup; getHiddenLayer() &cup; getOutputLayer() = getVertices()
 *  &and; getInputLayer() &sube;! getVertices()
 *  &and; getHiddenLayer() &sube;! getVertices()
 *  &and; getOutputLayer() &sube;! getVertices()
 */
public
class FeedForwardNeuralNetwork extends NeuralNetwork {
	private static final long serialVersionUID = 1213851271736780257L;
	
	/**
	 * the input layer view which is the first layer of neurons.
	 * @serial
	 */
	private List inputLayer;
	/**
	 * the hidden layer view which are the layers of neurons between input and output layer.
	 * @serial
	 */
	private List hiddenLayer;
	/**
	 * the output layer view which is the last layer of neurons.
	 * @serial
	 */
	private List outputLayer;
	
    public FeedForwardNeuralNetwork() {}

    public FeedForwardNeuralNetwork(int capacity) {
        super(capacity);
    }

    /**
     * Neural network shallow copy constructor.
     * @param b the neural network to shallow copy.
     *  If b instancoef FeedForwardNeuralNetwork, then the layers will be copied as well,
     *  using the invariant.
     *  Otherwise, this class will have no layers set, yet.
     */
    public FeedForwardNeuralNetwork(NeuralNetwork b) {
    	super(b);
    	if (b instanceof FeedForwardNeuralNetwork) {
    		FeedForwardNeuralNetwork o = (FeedForwardNeuralNetwork) b;
    		List vertices = (List) getVertices();
    		int inputLayerSize = o.getInputLayer().size();
    		int outputLayerOffset = vertices.size() - o.getOutputLayer().size();
    		// set the corresponding layers
    		setInputLayer(vertices.subList(1, 1 + inputLayerSize));
    		setHiddenLayer(vertices.subList(1 + inputLayerSize , outputLayerOffset));
    		setOutputLayer(vertices.subList(outputLayerOffset, vertices.size()));
			assert 1 + getInputLayer().size() + getHiddenLayer().size() + getOutputLayer().size() == getVertices().size() : "performant invariant";
    	}
    }

    /**
     * Compares two neural networks for equality.
     * Two equal neural networks with equal layers are equal.
     * @internal note that due to the additional checks for FeedForwardNeuralNetworks, only (which are required for symmetry to hold), hashCode is not refined
     */
    public boolean equals(Object o) {
    	if (super.equals(o))
    		if (o instanceof FeedForwardNeuralNetwork) {
    			// additional checks for FeedForwardNeuralNetworks, only
        		FeedForwardNeuralNetwork b = (FeedForwardNeuralNetwork) o;
        		return Utility.equals(getInputLayer(), b.getInputLayer())
        				&& Utility.equals(getHiddenLayer(), b.getHiddenLayer())
        				&& Utility.equals(getOutputLayer(), b.getOutputLayer());
    		} else
    			return true;
    	else
    		return false;
    }

	// view methods
	
	/**
	 * Get the input layer view which is the first layer of neurons.
	 */
	protected List getInputLayer() {
		return inputLayer;
	}
	/**
	 * Get the hidden layer view which are the layers of neurons between input and output layer.
	 */
	protected List getHiddenLayer() {
		return hiddenLayer;
	}
	/**
	 * Get the output layer view which is the last layer of neurons.
	 */
	protected List getOutputLayer() {
		return outputLayer;
	}

	/**
	 * Set the input layer view which is the first layer of neurons.
	 * @pre layer is a subList view of getVertices()
	 */
	protected void setInputLayer(List layer) {
		this.inputLayer = layer;
	}
	/**
	 * Set the hidden layer view which are the layers of neurons between input and output layer.
	 * @pre layer is a subList view of getVertices()
	 */
	protected void setHiddenLayer(List layer) {
		this.hiddenLayer = layer;
	}
	/**
	 * Set the output layer view which is the last layer of neurons.
	 * @pre layer is a subList view of getVertices()
	 */
	protected void setOutputLayer(List layer) {
		this.outputLayer = layer;
	}
	
	// central method
	

	/**
	 * Applies this neural network to an input vector.
	 * <p>
	 * Iterative unrolling of successive neural activation ("forward propagation").</p>
	 * @param input the input {@link Vector} to fill into the neurons of the input layer.
	 * @return the resulting output {@link Vector} computed in the neurons of the output layer
	 *  after running the network.
	 * @pre invariant
	 */
	public Object/*>Vector<*/ apply(Object/*>Vector<*/ input) {
		Vector inputVector = (Vector) input;
		List   inputLayer = getInputLayer();
		if (inputVector.dimension() != inputLayer.size())
			throw new IllegalArgumentException("input must have size " + inputLayer.size());

		// set input activity levels
		for (Iterator i = inputVector.iterator(), it = inputLayer.iterator(); i.hasNext(); )
			((Neuron) ((Vertex) it.next()).getObject()).setActivity((Real) i.next());
		
		List	outputLayer = getOutputLayer();

		// successively compute activity (implicitly layer by layer due to invariant!)
		// optimized version already skips input layer activation
		for (Iterator it = new SequenceIterator(new Iterator[] {getHiddenLayer().iterator(), outputLayer.iterator()});
				it.hasNext(); ) {
			Vertex v = (Vertex) it.next();
			((Neuron) v.getObject()).activate(getToEdges(v));
		}
		
		// return output activity
		Vector outputVector = Values.getInstance(outputLayer.size());
		Iterator it = outputLayer.iterator();
		for (int i = 0; i < outputVector.dimension(); i++)
			outputVector.set(i, ((Neuron) ((Vertex) it.next()).getObject()).getActivity());
		return outputVector;
	}
    
    // utilities
    
	/**
	 * Create feed-forward layers and fully connect them.
	 * <p>
	 * Creates sizes.length fully connected layers with sizes[i] neurons, each. The layers
	 * are fully connected means, that each neuron of layer i is connected to all neurons of
	 * layer i+1.
	 * An artificial "on"-neuron is inserted for bias as the very first layer, just before
	 * the input layer.
	 * The last layer is the so called output layer.</p>
	 * <p>
	 * The resulting layers are
	 * <small><pre>
	 * "on"-neuron, input layer, layer<sub>1</sub>, ...,layer<sub>n-1</sub>, output layer
	 * </pre></small></p>
	 */
	public void createLayers(int sizes[]) {
		GraphFactory f = getGraphFactory();
		// insert bias "on"-neuron
		Vertex bias;
		add(bias = f.createVertex(new Bias()));

		// add input layer
		for (int c = 0; c < sizes[0]; c++)
			add(f.createVertex(new Neuron.ConstantNeuron()));
		// remember the size of the input layer
    	/*
    	 * The size of the input layer which is the first layer of neurons, starting at index 1.
    	 */
		int inputLayerSize = sizes[0];
		assert 1 + inputLayerSize == nodes.size() : "starting with an empty network leads to a bias layer and an input layer as its first layer";
		
    	/*
    	 * The offset of the output layer which is the last layer of neurons.
    	 */
		int outputLayerOffset = inputLayerSize + 1;

		int lastLayerOffset = 1;
		for (int i = 1; i < sizes.length; i++) {
			int layerOffset = nodes.size();
			// append layer
			for (int c = 0; c < sizes[i]; c++) {
				Vertex v = f.createVertex();
				add(v);
				// connect from its "on"-neuron
				add(f.createEdge(bias, v));
				// fully connect from previous layer
				for (int j = 0; j < sizes[i - 1]; j++)
					add(f.createEdge((Vertex) nodes.get(lastLayerOffset + j), v));
			}
			// the last layer created is the output layer
			if (i == sizes.length - 1)
				outputLayerOffset = layerOffset;
			assert layerOffset == lastLayerOffset + sizes[i - 1] : "after last layer comes the next layer";
			lastLayerOffset = layerOffset;
		}
		
		List vertices = (List) getVertices();
		// set the layers created
		setInputLayer(vertices.subList(1, 1 + inputLayerSize));
		setHiddenLayer(vertices.subList(1 + inputLayerSize , outputLayerOffset));
		setOutputLayer(vertices.subList(outputLayerOffset, vertices.size()));
		assert 1 + getInputLayer().size() + getHiddenLayer().size() + getOutputLayer().size() == getVertices().size() : "performant invariant";
	}
}
