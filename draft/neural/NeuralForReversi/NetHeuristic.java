/**
 * @(#)NetHeuristic.java 0.9 2001/06/27 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

//package agent007;

import java.io.Serializable;

import orbital.math.Vector;
import orbital.math.RVector;

/**
 * Heuristic that summarizes single feature heuristics with a neural network.
 *
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public
class NetHeuristic implements FieldHeuristic, Serializable {
	private static final long serialVersionUID = 3789769419281530843L;
	protected FieldHeuristic  feature[];
	protected NeuralNetwork   net;
	/**
	 * Create a summarizing read-only net heuristic.
	 * @param feature the array of precomputed heuristic feature lists to use.
	 * @param net the neural network to work upon. Must fit input/output encoding conventions.
	 */
    public NetHeuristic(FieldHeuristic feature[], NeuralNetwork net) {
    	this.feature = feature;
        this.net = net;
    }
    protected NetHeuristic() {}

	/**
	 * Transforms any net heuristic into a new read-only net heuristic.
	 * <p>
	 * Replaces learning neural networks with ordinary FeedForwardNeuralNetworks.</p>
	 * @return a NetHeuristic which is a copy of h, except for its learning abilities.
	 */
    public static NetHeuristic asReadOnly(NetHeuristic h) {
    	return new NetHeuristic(h.feature, new FeedForwardNeuralNetwork(h.net));
    }

	/**
	 * Transforms any phase heuristic containing nets into a new read-only net heuristic.
	 * <p>
	 * Replaces learning neural networks with ordinary FeedForwardNeuralNetworks.</p>
	 * @return a PhaseHeuristic which is a copy of h, except for its learning abilities.
	 */
    public static PhaseHeuristic asReadOnly(PhaseHeuristic h) {
		FieldHeuristic hi[] = new NetHeuristic[h.heuristic.length];
		for (int i = 0; i < hi.length; i++)
			hi[i] = h.heuristic[i] instanceof NetHeuristic
					? NetHeuristic.asReadOnly((NetHeuristic) h.heuristic[i])
					: h.heuristic[i];
		return new PhaseHeuristic(hi, h.finalReward, h.phase);
    }
    
	/**
	 * Query the heuristic for the utility evaluation of a board.
	 * <p>
	 * The heuristic used depends upon the phase of the game in the situation of board.</p>
	 */
	public float h(AbstractGameBoard board) {
		return valueOf(net.apply(encode(board)));
	}

	/**
	 * Encode the board to fit into the network.
	 * @internal see LearningNetHeuristic#create(int, int)
	 */
	protected Object encode(AbstractGameBoard board) {
		RVector state = new RVector(feature.length);
		// precomputed heuristic features
		for (int i = 0; i < feature.length; i++)
			state.set(i, feature[i].h(board));
		return state;
	}
    
	private float valueOf(Object u) {
		if (u instanceof Vector) {
			Vector v = (Vector) u;
			assert v.dimension() == 1 : "utility values are scalar values";
			u = v.get(0);
		}
		return ((Number) u).floatValue();
	}
}