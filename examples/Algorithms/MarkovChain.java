/** $Id$
 * @(#)$RCSfile$ 1.1 2003-05-17 Andre Platzer
 *
 * Copyright (c) 2003 Andre Platzer. All Rights Reserved.
 */

import java.util.Iterator;
import orbital.algorithm.template.TransitionModel;
import orbital.algorithm.template.TransitionModel.Transition;

/**
 * Represents a Markov chain.
 *
 *
 * @author <a href="mailto:NOSPAM@functologic.com">Andr&eacute; Platzer</a>
 * @version 1.1, 2003-05-17
 * @version-revision $Revision$, $Date$
 * @see MarkovDecisionProcess
 */
public class MarkovChain implements TransitionModel  {
    private final Matrix/*<Real>*/ transitionMatrix;
    public MarkovChain(Matrix/*<Real>*/ transitionMatrix) {
	this.transitionMatrix = transitionMatrix;
    }
    // implementation of orbital.algorithm.template.TransitionModel interface

    public Iterator actions(Object param1)
    {
	// TODO: implement this orbital.algorithm.template.TransitionModel method
	return null;
    }

    public Iterator states(Object param1, Object param2)
    {
	// TODO: implement this orbital.algorithm.template.TransitionModel method
	return null;
    }

    public TransitionModel$Transition transition(Object param1, Object param2, Object param3)
    {
	// TODO: implement this orbital.algorithm.template.TransitionModel method
	return null;
    }

}// MarkovChain
