/*
 * @(#)ContainerWeighting.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.robotic.strategy;

import orbital.logic.functor.Function;

/**
 * ContainerWeighting class weighting via a given weighting implementation.
 * 
 * TODO:  not yet finished
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class ContainerWeighting extends Evaluation {

    /**
     * constructs a ContainerWeighting by a Selection of Weightings.
     */
    public ContainerWeighting(Selection selection, Function/*<Object, Number>*/ weighting) {
        super(selection, weighting);
    }
    public ContainerWeighting(Function/*<Object, Number>*/ weighting) {
        super(weighting);
    }
    public ContainerWeighting() {
        super();
    }

    /**
     * return weight value of a situation-Object by Number via the Container Evaluation.
     */
    public Object/*>Number<*/ applyContainer(Object arg) {
        return weightImpl(arg);
    } 

    /**
     * returns weight value of an arg by Number.
     */
    public Object/*>Number<*/ apply(Object arg) {
        return applyContainer(arg);
    } 
}
