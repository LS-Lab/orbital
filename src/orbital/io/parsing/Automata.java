/*
 * @(#)Automata.java 0.9 1998/05/11 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.io.parsing;

import orbital.logic.State;
import orbital.io.ParseException;
import java.io.IOException;

/**
 * This interface represents a (finite) automaton.
 * Mightyness: CH-3.
 * A finite automaton (especially a finite acceptor) <tt>(&Sigma;,Q,q<sub>0</sub>,F,P)</tt> with:<ul>
 * <li><tt>&Sigma;</tt> is the set of possible input tokens (often single characters).
 * <li><tt>Q</tt> is the finite set of states that is non-empty.
 * <li><tt>q<sub>0</sub></tt> in Q is the initial state.
 * <li><tt>F&sub;Q</tt> is the set of final states.
 * <li><tt>P</tt> is the set of transitions <tt>qa->q'</tt> where <tt>q,q'&isin;Q, a&isin;&Sigma;</tt>.
 * <li><tt>T</tt> <i>is the set of output tokens (optional).</i>
 * </ul>
 * <p>
 * Such an automata accepts the language:<blockquote>
 * L(A) = {x &brvbar; x&isin;&Sigma;<sup>*</sup>, q<sub>0</sub>x =><sup>*</sup>q', q'&isin;F}
 * </blockquote>
 * 
 * @version 0.9, 11/05/98
 * @author  Andr&eacute; Platzer
 * @xxx rename to Automaton? Generalize and move to another package?
 */
public interface Automata {

    /**
     * With the current State being state, process the automata input tokens
     * from the TokenSequence.
     * If this automata is finite, set state to new State and consume all Tokens processed.
     * The output result is returned.
     * @param state the current state, which will be set to the resulting state on return.
     * @param tokens the sequence of tokens scanned. On return all tokens used up will be consumed.
     * @return the output object.
     * @throws java.io.IOException if the underlying input stream throws exceptions.
     */
    public Object processAutomata(State state, TokenSequence tokens) throws ParseException, IOException, ClassCastException, IllegalArgumentException;
}
