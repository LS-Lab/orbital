/**
 * @(#)AdversarySearch.java 0.9 2001/07/01 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import java.io.Serializable;

import java.util.Iterator;
import orbital.robotic.Move;
import orbital.robotic.Position;

import orbital.util.Utility;

/**
 * Adversary search.
 *
 * @version 0.8, 2001/07/01
 * @author  Andr&eacute; Platzer
 * @see orbital.algorithm.template.GeneralSearch
 * @todo generalize even more? implement a general interface?
 */
public abstract class AdversarySearch {
    public AdversarySearch() {}
    
    /**
     * Search for the best option to take.
     * @param state in which state to choose an action.
     * @return the best move option found.
     */
    public abstract Option solve(Field state);
    
    /**
     * Get all reachable states.
     * @return an iterator of options that are the successors of state.
     * @see Field#expand()
     * @see <a href="{@docRoot}/DesignPatterns/Facade.html">Facade</a>
     */
    protected Iterator successors(Field state) {
    	return state.expand();
    }


    /**
     * Represents an option node during an adversary search problem.
     * <p>
     * An option node is a triple &lang;&lang;s,d&rang;,&lang;f,m&rang;,u&rang;
     * of a field state, the destination, the figure, its move, and the utility of the state.</p>
     * @invariant figure.moveFigure(move).equals(destination)
     * @stereotype &laquo;Structure&raquo;
     */
    public static class Option implements Comparable, Serializable {
	private static final long serialVersionUID = -6512650518865382543L;
	/**
	 * the field state s&isin;S of this option node.
	 * @serial
	 */
	private Field state;
	/**
	 * the destination that the figure reached.
	 * @serial
	 */
	private Position destination;
	/**
	 * the acting figure moved to reach this state.
	 * @serial
	 */
	private Figure figure;
	/**
	 * the applicable move performed by the figure to reach this state.
	 * @serial
	 */
	private Move move;
	/**
	 * the utility u(s) of this node.
	 * @serial
	 */
	private double utility;
	/**
	 * Create a new option.
	 * @param state the state s&isin;S.
	 * @param destination the destination that the figure reached.
	 * @param figure the acting figure moved to reach the state.
	 * @param move the applicable move performed by the figure to reach the state.
	 * @param utility the utility u(s) of the state.
	 */
	public Option(Field state, Position destination, Figure figure, Move move, double utility) {
	    this.state = state;
	    this.destination = destination;
	    this.figure = figure;
	    this.move = move;
	    this.utility = utility;
	}
	public Option(Field state, Position destination, Figure figure, Move move) {
	    this(state, destination, figure, move, 0);
	}
	/**
	 * @deprecated Since 1.0, this constructor is for convenience of converting old code, only.
	 */
	public Option(MoveWeighting.Argument arg) {
	    this(arg.field, arg.destination, arg.figure, arg.move, 0);
	}

	/**
	 * Get the state.
	 * @return the state s of this node.
	 */
	public Field getState() {
	    return state;
	}
		
	protected void setState(Field state) {
	    this.state = state;
	}
		
	/**
	 * Get the destination reached.
	 * @return the destination that the figure reached.
	 */
	public Position getDestination() {
	    return destination;
	}
		
	protected void setDestination(Position destination) {
	    this.destination = destination;
	}

	/**
	 * Get the figure moving.
	 * @return the acting figure moved to reach this state.
	 */
	public Figure getFigure() {
	    return figure;
	}
		
	protected void setFigure(Figure figure) {
	    this.figure = figure;
	}

	/**
	 * Get the move performed.
	 * @return the applicable move performed by the figure to reach this state.
	 */
	public Move getMove() {
	    return move;
	}

	protected void setMove(Move move) {
	    this.move = move;
	}
		
	/**
	 * Get the utility.
	 * @return the utility u(s) of this node.
	 */
	public double getUtility() {
	    return utility;
	}
		
	public void setUtility(double utility) {
	    this.utility = utility;
	}
		
	public boolean equals(Object o) {
	    if (!(o instanceof Option))
		return false;
	    Option b = (Option) o;
	    return utility == b.utility
		&& Utility.equals(getState(), b.getState())
		&& Utility.equals(getDestination(), b.getDestination())
		&& Utility.equals(getFigure(), b.getFigure())
		&& Utility.equals(getMove(), b.getMove());
	}
		
	public int hashCode() {
	    //@see Double#hashCode()
	    long bits = Double.doubleToLongBits(utility);
	    return Utility.hashCode(getState())
		^ Utility.hashCode(getDestination())
		^ Utility.hashCode(getFigure())
		^ Utility.hashCode(getMove())
		^ (int) (bits ^ (bits >>> 32));
	}
		
	/**
	 * Compares options according to their utility.
	 */
	public int compareTo(Object o) {
	    //@see Double#compare(double,double)
	    return new Double(getUtility()).compareTo(new Double(((Option)o).getUtility()));
	}
		
	public String toString() {
	    return "[" + figure.x + "|" + figure.y + "--" + move.movement + "-->" + destination.x + "|" + destination.y + "]";
	} 
    }
}
