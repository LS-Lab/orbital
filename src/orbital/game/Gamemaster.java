/**
 * @(#)Gamemaster.java    1.1 2003-01-03 Andre Platzer
 * 
 * Copyright (c) 1996-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;
import java.awt.Component;

import orbital.robotic.Position;

/**
 * Gamemaster manages a game and coordinates the interactions of its
 * players.  To let the gamemaster run a certain game, you must define
 * the games' rules with any instance implementing the {@link
 * GameRules} interface.  This interface can then start an AI upon
 * request via {@link GameRules#startAIntelligence(String)}.
 * 
 * @version 1.1, 2003-01-03
 * @author Andr&eacute; Platzer
 * @xxx performedMove: perhaps we can get rid of this old way of using events. Also we need a more customizable way of deciding when to end a turn (f.ex. some games may allow a player to perform multiple moves before ending his turn)
 */
public class Gamemaster implements Runnable {
    //private static final long serialVersionUID = 0;
    // {{DECLARE_PARAMETERS

    /**
     * Instance of the game rules to use.
     * @serial
     */
    private GameRules   rules;

    /**
     * Arguments passed to the AIs in {@link #players}.
     * <code>playersArguments[i] == null</code> if and only if <code>i</code> is a human player.
     * @serial
     */
    private String	playerArguments[];

    /**
     * for which component to load images etc.
     */
    private transient final Component component;

    // }}

    /**
     * The field to play on.
     * @serial
     */
    private Field       field;

    /**
     * The diverse AI-players.
     * <code>players[i] == null</code> if and only if <code>i</code> is a human player.
     * @serial
     */
    private Function	players[];

    /**
     * only for pure AI games without real players.
     * @serial
     * @todo we cannot store threads, can we?
     */
    //XXX: concurrent synchronization may be required for this volatile field
    private volatile Thread runner = null;

    /**
     * Create a new gamemaster for the game with the given rules.
     * @param component for which component to load images etc.
     * @param rules the rules of the game to play.
     * @param playerArguments the arguments to pass to the individual players.
     */
    public Gamemaster(Component component, GameRules rules, String playerArguments[]) {
	if (playerArguments.length != rules.getLeagues())
	    throw new IllegalArgumentException("expected: " + rules.getLeagues() + " player arguments. found: " + playerArguments.length);
	this.component = component;
	this.setGameRules(rules);
	this.playerArguments = playerArguments;
    }

    /**
     * Get the game rules used.
     */
    public GameRules getGameRules() {
	return rules;
    }
    protected void setGameRules(GameRules newRules) {
	this.rules = newRules;
    }

    /**
     * Get all players currently playing.
     */
    public Function[] getPlayers() {
	return players;
    } 
    
    /**
     * Get the field which the game is played on.
     */
    public Field getField() {
	return field;
    }
    private void setField(Field field) {
	this.field = field;
    }

    
    /**
     * (Start entry point) starts a new game.
     */
    public void start() {
	setField(rules.startField(component));
	players = new Function[playerArguments.length];
	int realPlayers = 0;
	for (int i = Figure.NOONE + 1; i < players.length; i++) {
	    if (playerArguments[i] == null) {
		players[i] = null;
		realPlayers++;
	    } else
		players[i] = rules.startAIntelligence(playerArguments[i]);
	}

	// computer players only
	if (realPlayers == 0) {
	    runner = new Thread(this, "AI_Runner");
	    runner.start();
	} else {
	    runner = null;
	    if (players[getField().getTurn()] != null)
		// if a computer commences, let him act
		turn();
	}
    } 

    /**
     * (Stop exit point) stops the current game.
     */
    public void stop() {
	Thread moribund = runner;
	runner = null;	  // runner.stop();
	if (moribund != null)
	    moribund.interrupt();

	// alternative implementation
	/*
	 * Thread[] ts = new Thread[Thread.currentThread().activeCount()];
	 * for (int i=Thread.currentThread().enumerate(ts)-1; i>=0; i--)
	 * if ("AI_Runner".equals(ts[i].getName()))
	 * ts[i].interrupt();
	 */
    } 

    /**
     * (Destroy exit point).
     */
    public void destroy() {
	rules = null;
	field = null;
	players = null;
	playerArguments = null;
	//runner.destroy();
    } 


    /**
     * Runnable-start entry point.
     * @see #action(Event, Object)
     * @internal see #turn()
     */
    public void run() {
	try {
	    Thread thisThread = Thread.currentThread();
	    while (runner == thisThread && !Thread.interrupted()) {
		//@xxx the above check executes no longer since change of turn to infinite loop.
		if (turn() != Figure.NOONE)
		    //@todo generate event
		    return;
	    }
	}
	finally {
	    // clean up: forget about references
	    field = null;
	    players = null;
	    runner = null;
	}
    } 

    /**
     * Called at the end of each user turn.
     * Once all real players made their turn, it will let all AIs take their actions.
     * Notifies the current GameRules implementation that a turn is done.
     * @see GameRules#performedMove(Field)
     * @internal this is a kind of event handler.
     */
    protected int turn() {
	// check for any winners
	int winner = rules.performedMove(getField());
	if (winner != Figure.NOONE)
	    return winner;

	// do moves while it's an AI's turn
	for (int turn = getField().getTurn();
	     players[turn] != null;
	     turn = getField().getTurn()) {
	    // @xxx doesn't this policy (a single player can move several times until it's another player's turn) conflict with AlphaBetaPruning which simply doesn't know about it?
	    ////showStatus(getResources().getString("statusbar.ai.thinking"));
	    Object action = players[turn].apply(getField());
	    ////showStatus(getResources().getString("statusbar.ai.moving"));
	    if (action instanceof MoveWeighting.Argument) {
		MoveWeighting.Argument move = (MoveWeighting.Argument) action;
		Position source = new Position(move.figure);
		// if we could rely on our AI, then we could optimize away this expensive moving and simply use the resulting field = move.field
		//@internal cloning the position information is necessary, otherwise move would detect that it gets lost during swap.
		if (!getField().move(source, move.move))
		    throw new Error("AI should only take legal moves: " + move);
		////board.repaint(source);
		////board.repaint(move.destination);
	    } else
		throw new Error("AI found no move: " + action);
	    winner = rules.performedMove(getField());
	    if (winner != Figure.NOONE)
		return winner;
	} 

	return winner;
    } 

}
