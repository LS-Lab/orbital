/**
 * @(#)Neuron.java 0.9 2001/05/23 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

import java.util.Collection;
import java.io.Serializable;
import orbital.math.Arithmetic;
import orbital.math.Real;

import orbital.math.Values;
import orbital.util.Utility;

/**
 * Neuron representation for discrete neural networks.
 * <p>
 * Note that this architecture does not support time-continuous networks but only
 * discrete simulations.</p>
 *
 * @version 0.9, 2001/05/23
 * @author  Andr&eacute; Platzer
 * @todo are we only Arithmetic, because we want reside in a matrix and contain an arithmetic object?
 */
public interface Neuron extends Arithmetic {
    // get/set methods

    /**
     * Get the current activity a<sub>i</sub> of this neuron.
     */
    Real getActivity();

    /**
     * Set the current activity a<sub>i</sub> of this neuron.
     * @throws UnsupportedOperationException if this neuron's activity is read-only and cannot be changed.
     */
    void setActivity(Real a) throws UnsupportedOperationException;

    // central virtual method

    /**
     * Apply the activation function &alpha;<sub>i</sub>(<span class="vector">a</span>).
     * <p>
     * <ul>
     *   <li>&alpha;<sub>i</sub> defines the future activity in terms of the activities
     *   of the neurons in the local neighbourhood of linked neurons.</li>
     *   <li><span class="vector">a</span> is the vector of activity levels (of the input neurons).</li>
     * </ul></p>
     * @param presynaptic the edges (alias links) from input neurons.
     *  The strength W<sub>j,i</sub> of a link is contained in the edge, and its from-vertex contains the input activity level a<sub>j</sub>.
     */
    void activate(Collection/*<Edge>*/ presynaptic);

    /**
     * Neuron representation for discrete neural networks.
     *
     * @version 0.9, 2001/05/23
     * @author  Andr&eacute; Platzer
     * @todo extend Real??, instead to remove double activity variables
     */
    static abstract class Abstract extends orbital.math.AbstractReal.Double implements Neuron, Serializable {
    	private static final long serialVersionUID = -2711865166630462282L;
        /**
         * The current activity level a<sub>i</sub> of this neuron with index i.
         * @serial
         */
    	private Real activity;

    	public Abstract(Real activity) {
	    super(activity.doubleValue());
	    this.activity = activity;
    	}
    	public Abstract() {
	    super(java.lang.Double.NaN);
    	}
    	public Abstract(double activity) {
	    this(Values.valueOf(activity));
    	}

        public boolean equals(Object o) {
	    return o instanceof Neuron && Utility.equals(getActivity(), ((Neuron)o).getActivity());
        }

        public int hashCode() {
	    return Utility.hashCode(getActivity());
        }

        // get/set methods

        /**
         * Get the activity a<sub>i</sub> of this neuron.
         */
        public Real getActivity() {
	    return activity;
        }

        public void setActivity(Real a) {
	    this.activity = a;
        }

        public double doubleValue() {
	    return activity.doubleValue();
        }

	public String toString() {
	    return '\'' + (getActivity() + "") + '\'';
	}
    }

    /**
     * A fixed neuron with constant activation.
     * <p>
     * Fixed neurons can be used as input neurons of a feed-forward neural network.
     * Due to the missing of cycles, such networks have no need to update activity of input
     * neurons in the flow of an evaluation, at all. Instead, setting the activity of an
     * input neuron is used to specify the networks input arguments.</p>
     *
     * @version 0.9, 2001/05/23
     * @author  Andr&eacute; Platzer
     */
    public static class ConstantNeuron extends Neuron.Abstract {
    	private static final long serialVersionUID = -8817102220574046092L;
        public ConstantNeuron(Real fixedActivity) {
            super(fixedActivity);
        }
        public ConstantNeuron(double fixedActivity) {
            super(fixedActivity);
        }
        public ConstantNeuron() {}

        /**
         * Performs no activation and remains constant.
         */
        public final void activate(Collection/*<Edge>*/ presynaptic) {}
    }
}
