/**
 * @(#)LearningNetHeuristic.java 0.9 2001/06/27 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

//package agent007;

import java.io.Serializable;

import orbital.math.functional.Functions;

/**
 * Learning heuristic that summarizes single feature heuristics with a neural network.
 * <p>
 * We have decided to use a non-classical approach to reinforcement learning in games.
 * Classical approaches model the game as a nondeterministic MDP with a transition model
 * that performs a transition i to j whenever the game was in state i, we chose to do action a,
 * and our opponent's reaction took us to state j. This would be a rather natural way of
 * building an MDP around a game problem. However, it does not take into account the nature of
 * lookahead performed by the adversary search.</p>
 * <p>
 * Instead, we define a prognostic "transition" from i to j to hold whenever the game is in state
 * i, we chose to do action a and will eventually reach a state j <em>after several turns</em>,
 * on the basis that our opponent will react like the adversary search
 * took into account, and we will react again like our search decided, etc.
 * This way, although the prognostic "transitions" leave out some nondeterministic steps
 * in between, we can profit from a multiple-ply lookahead, if only our search was right.
 * Still, the markov property holds approximately in the same way as for classical approaches.
 * With respect to the computational requirements, this approximization is necessary.</p>
 * <p>
 * Another advantage of the non-classical prognostic "transitions" is that temporal difference learning
 * will not depend that much on the mind-boggingly stupid moves - in terms of our heuristic - 
 * our opponent made. But instead consider all possible alternatives by adversary search.
 * <var>Consequently</var>, we simply ignore the external reward at the end of the game
 * and replace it by a final reward evaluation function that can as well tell us,
 * what might have happened, if our opponent took a different finishing move. Additionally,
 * we will not learn rewards that result from our opponent violating the rules, but we will only
 * accept rewards that result from our own heuristics.</p>
 *
 * @invariant net.length == learner.length
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public
final class LearningNetHeuristic extends NetHeuristic implements LearningFieldHeuristic {
	private static final long serialVersionUID = -1904110809060354508L;
	private TemporalDifferenceLearning learner;
	/**
	 * Create a summarizing learning net heuristic.
	 * @param feature the array of precomputed heuristic feature lists to use.
	 * @param net the neural network to work upon. Must fit input/output encoding conventions.
	 */
    public LearningNetHeuristic(FieldHeuristic feature[], Backpropagation net) {
    	super(feature, net);
        net.setLearningRate(0.05);
    	this.learner = new TemporalDifferenceLearning(net);
        learner.setLearningRate(1);							//already set for net, so set 1, here since the product counts
	    learner.setDiscount(1);
    }
    protected LearningNetHeuristic() {}
    
	public void update(AbstractGameBoard board) {
		learner.update(board.futureCoolness, encode(board), 0);
	}

	public void update() {
		((BatchBackpropagation) net).update();
	}

    // central constructor method
	
	/**
	 * Convenience method to create a summarizing learning phase heuristic.
	 * @param feature the array of precomputed heuristic feature lists to use.
	 *  For each phase i, feature[i] will be the array of heuristic features to use in phase i.
	 * @param finalReward the evaluation function used to compute the final reward at the end of
	 *  game.
	 *  This evaluation function will be the sole "heuristic" function for end of game situations.
	 * @param phase an array specifying after how many moves to switch phases.
	 *  Each phase will have a new combination of weights.
	 * @pre phase[0] == 0 && heuristic.length == phase.length
	 */
	public static final LearningFieldHeuristic create(FieldHeuristic[] feature[], FieldHeuristic finalReward, int phase[]) {
    	if (feature.length != phase.length)
    		throw new IllegalArgumentException("each heuristic is for one phase, so those arrays should have the same length");
		LearningNetHeuristic hi[] = new LearningNetHeuristic[feature.length];
		for (int i = 0; i < phase.length; i++)
			hi[i] = new LearningNetHeuristic(feature[i], create(i, feature[i].length));
		return new PhaseHeuristic(hi, finalReward, phase);
	}

	/**
	 * Create a network for a phase with a given number of input features heuristics.
	 * @internal see NetHeuristic#encode(int, AbstractGameBoard)
	 */
	private static Backpropagation create(int phase, int numberOfHeuristicFeatures) {
		Backpropagation net = new BatchBackpropagation();
		// tiny linear "network"
		net.createLayers(new int[] {numberOfHeuristicFeatures, 1});
		net.setActivationFunction(Functions.id);
		return net;
	}
}