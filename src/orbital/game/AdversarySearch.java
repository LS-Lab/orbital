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
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see orbital.algorithm.template.GeneralSearch
 * @todo generalize even more? implement a general interface?
 */
public abstract class AdversarySearch {
    public AdversarySearch() {}
    
    /**
     * Search for the best option to take.
     * @param state in which state to choose an action.
     * @preconditions this implementation assumes a two league game (but not necessarily two player game)
     * @return the best move option found.
     */
    public abstract Option solve(Field state);
    
    /**
     * Get all reachable states.
     * @return an iterator of options that are the successors of state.
     * @see Field#expand()
     * @see <a href="{@docRoot}/Patterns/Design/Facade.html">Facade</a>
     */
    protected Iterator successors(Field state) {
    	return state.expand();
    }

    /**
     * Whether the turn of the given state is up to our league.
     * <p>
     * Thus when <code>true</code> it's a maximizer's turn,
     * when <code> false</code> it's a minimizer's turn.
     * </p>
     * @return whether the active league that performs the next move is of our league,
     *  or belongs to the opponents.
     * @see <a href="{@docRoot}/Patterns/Design/TemplateMethod.html">Template Method</a>
     * @todo improve name to isOurLeague'sTurn, or ourLeagueMoves, ...
     */
    protected abstract boolean isOurLeaguesTurn(Field state);
    
    

    /**
     * Represents an option node during an adversary search problem.
     * <p>
     * An option node is a triple &lang;&lang;s,d&rang;,&lang;f,m&rang;,u&rang;
     * of a field state, the destination, the figure, its move, and the utility of the state.</p>
     * @invariants figure.moveFigure(move).equals(destination)
     * @stereotype Structure
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
	 * @param state the state s&isin;S reached after this move option.
	 * @param destination the destination that the figure reached.
	 * @param figure the moving figure to reach the state
	 * (at the original position on the initial field).
	 * @param move the applicable move performed by the figure to reach the state.
	 * @param utility the utility u(s) of the state.
	 */
	public Option(Field state, Position destination, Figure figure, Move move, double utility) {
	    this.state = state;
	    this.destination = destination;
	    this.figure = figure;
	    this.move = move;
	    this.utility = utility;
	    //@todo assert state != figure.getField() : "(next) state different from the source state figure.getField()";
	}
	public Option(Field state, Position destination, Figure figure, Move move) {
	    this(state, destination, figure, move, 0/*@xxx Double.NaN instead?*/);
	}
	public Option(Field state, Figure figure, Move move, Position destination) {
	    this(state, destination, figure, move);
	}
	/**
	 * @deprecated Since 1.0, this constructor is for convenience of converting old code, only.
	 */
	public Option(MoveWeighting.Argument arg) {
	    this(arg.field, arg.destination, arg.figure, arg.move, 0);
	}

	/**
	 * Get the state reached after this move option.
	 * @return the state s of this node.
	 */
	public Field getState() {
	    return state;
	}
		
	protected void setState(Field state) {
	    this.state = state;
	}
		
	/**
	 * Get the destination reached by the figure.
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
	 * (at the original position on the initial field).
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
		
	/**
	 * Checks whether the given move option represents a "perform no
	 * move at all" option.
	 * @todo find a better name.
	 */
	boolean isNoMove() {
	    return getFigure() == null && getMove() == null && getDestination() == null;
	}

	/**
	 * Create an option representing the choice to perform no move at all.
	 * @param field the field reached after performing no move
	 * (which thus (almost) equals the field on which to perform
	 * no action).
	 * @todo find a better name.
	 */
	static Option createNoMove(Field field) {
	    return new Option(field, (Figure)null, (Move)null, (Position)null);
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
	    return "[" + figure.x + "|" + figure.y + "--" + move.getMovementString() + "-->" + destination.x + "|" + destination.y + "]";
	} 
    }
}
