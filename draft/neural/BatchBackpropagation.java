/**
 * @(#)BatchBackpropagation.java 0.9 2001/06/14 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import dkfz.collections.graph.Vertex;
import dkfz.collections.graph.Edge;

import orbital.math.Vector;
import orbital.math.Matrix;
import orbital.math.Arithmetic;
import orbital.math.Values;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A multilayer feed-forward network with back-propagation learning in batch update mode.
 * <p>
 * Assumes that the network is represented as a matrix graph.</p>
 *
 * @version 0.9, 2001/06/14
 * @author  Andr&eacute; Platzer
 * @invariant this instanceof MatrixGraph
 */
public
class BatchBackpropagation extends Backpropagation {
	private static final long serialVersionUID = 7979698524523777303L;
    private static final Logger logger = Logger.getLogger(BatchBackpropagation.class.getName());
    public BatchBackpropagation() {}

    public BatchBackpropagation(int capacity) {
        super(capacity);
    }
    
    /**
     * Neural network shallow copy constructor.
     * @see FeedForwardNeuralNetwork#FeedForwardNeuralNetwork(NeuralNetwork)
     */
    public BatchBackpropagation(NeuralNetwork b) {
    	super(b);
    }

    /**
     * Accumulating link weight update values for batch update.
     * @see #update(Edge, Arithmetic)
     * @see #update()
     */
    private transient Matrix updates;
    /**
     * Number of accumulating updates performed since the last call to {@link #update()}.
     * @see #updates
     */
    private transient int	 updateCount;

	public void learn(Vector input, Vector expectedOutput) {
		if (getLearningRate() == 0)
			return;
        
		update();
		// perform a unitary "batch" update consisting of this single example
        delayedLearn(input, expectedOutput);
        update();
	}

	/**
	 * Perform one step of delayed learning an input/output example
	 * <p>
	 * The changes calculated will not be applied until {@link #update()} is called.</p>
	 * @param input the list of training input vectors I<sup>(e)</sup> of the examples e.
	 * @param expectedOutput the list of expected training output vectors T<sup>(e)</sup> of the examples e.
	 * @pre input.length == expectedOutput.length && {@link #update()} has been called for initilization && {@link #update()} will be called for apply, later
	 */
	protected void delayedLearn(Vector input, Vector expectedOutput) {
		this.updateCount++;
        super.learn(input, expectedOutput);
    }

	void learn(Vector error) {
		this.updateCount++;
		super.learn(error);
	}

	/**
	 * delayed update link weight as batch update.
	 * <p>
	 * Accumulates changes.</p>
	 * @param e the link from j to i whose weight to adjust.
	 * @param change the amount of change to perform to the weight.
	 */
	protected void update(Edge e, Arithmetic change) {
		int i = ((MatrixVertex) e.getFromVertex()).getIndex();
		int j = ((MatrixVertex) e.getToVertex()).getIndex();
		if (logger.isLoggable(Level.FINER))
			logger.log(Level.FINER, "update ({0},{1}): {2} := {3} + {4}", new Object[] {new java.lang.Integer(i), new java.lang.Integer(j), updates.get(i, j).add(change), updates.get(i, j), change});
		this.updates.set(i, j, updates.get(i, j).add(change));
	}
	
	/**
	 * Really performs the update resulting from the current delayed update values.
	 * <p>
	 * Applies the accumulated changes resulting from the last calls to
	 * {@link #delayedLearn(Vector, Vector)} with a single "batch" update.</p>
	 * @see #update(Edge, Arithmetic)
	 */
	protected void update() {
		// have there been delayed updates?
		if (this.updateCount > 0) {
    		// edges = edges.add(updates);
    		assert edges.dimension().equals(updates.dimension()) : "updates still has edges' dimensions, as instantiated";
    		for (int i = 0; i < edges.dimension().height; i++)
    			for (int j = 0; j < edges.dimension().width; j++) {
    				Edge e = (Edge) edges.get(i, j);
    				if (e != null)
    					//@todo was division by example set size, ok? Verify theoretically! Perhaps, it is only required for unbound activation functions. Provably, for a constant set of examples, it just decreases the learning rate, so it will still converge, though perhaps more slowly.
    					//@todo optimizable
    					super.update((Edge) e, updates.get(i, j).divide(orbital.math.Values.getDefault().valueOf(updateCount)));
    			}
		}
		// init and reset
		reinit();
	}

	/**
	 * Initialize a new update phase.
	 * <p>
	 * Resets update count and update values.</p>
	 * @see #update()
	 */
	private final void reinit() {
		this.updates = Values.getDefault().ZERO(edges.dimension());
		this.updateCount = 0;
		logger.log(Level.FINER, "update reinit");
	}

	/**
	 * Perform one epoche of learning examples with back-propagation.
	 * <p>
	 * Another possibility would be to compute the sum of individiual weight update values for each
	 * example and perform one single combined weight update, only (so called "batch" update).
	 * </p>
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
		update();
		for (int i = 0; i < input.length; i++)
			delayedLearn(input[i], expectedOutput[i]);
		// "batch" update for all examples
		update();
	}

}
