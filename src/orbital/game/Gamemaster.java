/**
 * @(#)Gamemaster.java    1.1 2003-01-03 Andre Platzer
 * 
 * Copyright (c) 1996-2002 Andre Platzer. All Rights Reserved.
 */

package orbital.game;
import orbital.game.AdversarySearch.Option;

import orbital.logic.functor.Function;
import java.awt.Component;

import orbital.robotic.Position;

/**
 * Gamemaster manages a game and coordinates the interactions of its
 * players.  To let the gamemaster run a specific game, you must define
 * the games' rules with any instance implementing the {@link
 * GameRules} interface.  This interface can then start an AI upon
 * request via {@link GameRules#startAIntelligence(String)}.
 *
 * @events FieldChangeEvent.END_OF_GAME at the end of game.
 * @version 1.1, 2003-01-03
 * @author Andr&eacute; Platzer
 * @xxx performedMove: perhaps we can get rid of this old way of using events. Also we need a more customizable way of deciding when to end a turn (f.ex. some games may allow a player to perform multiple moves before ending his turn)
 */
public class Gamemaster implements Runnable {
    // {{DECLARE_PARAMETERS

    /**
     * Instance of the game rules to use.
     * @serial
     */
    private GameRules   rules;

    /**
     * Arguments passed to the AIs in {@link #players}.
     * <code>playersArguments[i] == null || playersArguments[i] = "null"</code> if and only if <code>i</code> is a human player.
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
     * <code>players[i] instanceof HumanPlayer</code> if and only if <code>i</code> is a human player.
     * @serial
     */
    private Function	players[];


    private FieldChangeListener endOfGameListener = new FieldChangeAdapter() {
		//@internal we are transient
		public void stateChanged(FieldChangeEvent evt) {
		    //@todo assert evt.getField() == getField() : "we have only registered ourselves to our field";
		    if (evt.getType() == FieldChangeEvent.END_OF_GAME)
			stop();
		}
	};

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
     *  If you set a component to <code>null</code> or <code class="String">&quot;null&quot;</code>
     *  a human player will take the part of that league.
     * @param field the field to play the game on.
     *  When <code>null</code>, a new field will be started from the rules.
     */
    public Gamemaster(Component component, GameRules rules, String playerArguments[], Field initialField) {
	if (playerArguments.length != rules.getLeagues())
	    throw new IllegalArgumentException("expected: " + rules.getLeagues() + " player arguments. found: " + playerArguments.length);
	this.component = component;
	this.setGameRules(rules);
	this.playerArguments = playerArguments;
	this.setField(initialField);
    }
    /**
     * Create a new gamemaster for the game with the given rules.
     * Since no field is specified, a new field will be started from the rules.
     * @param component for which component to load images etc.
     * @param rules the rules of the game to play.
     * @param playerArguments the arguments to pass to the individual players.
     *  If you set a component to <code>null</code> or <code class="String">&quot;null&quot;</code>
     *  a human player will take the part of that league.
     */
    public Gamemaster(Component component, GameRules rules, String playerArguments[]) {
	this(component, rules, playerArguments, null);
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
    void setField(Field field) {
	this.field = field;
    }

    
    /**
     * (Start entry point) starts a new game.
     */
    public void start() {
	if (getField() == null)
	    setField(rules.startField(component));
	getField().addFieldChangeListener(endOfGameListener);
	players = new Function[playerArguments.length];
	HumanPlayer humanPlayer = new HumanPlayer();
	getField().addFieldChangeListener(humanPlayer);
	int realPlayers = 0;
	for (int i = Figure.NOONE + 1; i < players.length; i++) {
	    if (playerArguments[i] == null || playerArguments[i].equals("null")) {
		players[i] = humanPlayer;
		realPlayers++;
	    } else
		players[i] = rules.startAIntelligence(playerArguments[i]);
	}

	runner = new Thread(this, "AI_Runner");
	if (realPlayers == 0)
	    // computer players only
	    runner.start();
	else {
	    if (players[getField().getTurn()] != null)
		// if a computer commences, let him act
		//@internal let our clients at least have a chance to finish initialization after start() returns so that they can register listeners.
		//@xxx However this is not 100% reliable because the following thread could start too early.
		runner.start();
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
	if (field != null)
	    field.removeFieldChangeListener(endOfGameListener);

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
	setGameRules(null);
	setField(null);
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
	    playGame();
	}
	finally {
	    setField(null);
	    players = null;
	}
    } 

    /**
     * Called at the end of each user turn.
     * Once all real players made their turn, it will let all AIs take their actions.
     * Notifies the current GameRules implementation that a turn is done.
     * @see GameRules#performedMove(Field)
     * @internal this is a kind of event handler.
     */
    private void playGame() {
	Thread thisThread = Thread.currentThread();
	// do moves while it's an AI's turn
	for (int turn = getField().getTurn();
	     runner == thisThread && !Thread.interrupted();
	     turn = getField().getTurn()) {
	    // @xxx doesn't this policy (a single player can move several times until it's another player's turn) conflict with AlphaBetaPruning which simply doesn't know about it?
	    ////showStatus(getResources().getString("statusbar.ai.thinking"));
	    System.err.println("statusbar.ai.thinking");
	    Object action = players[turn].apply(getField());
	    ////showStatus(getResources().getString("statusbar.ai.moving"));
	    if (action instanceof Option) {
		Option move = (Option) action;
		Position source = new Position(move.getFigure());
		// if we could rely on our AI, then we could optimize away this expensive moving and simply use the resulting field = move.field
		//@internal cloning the position information is necessary, otherwise move would detect that it gets lost during swap.
		if (!getField().move(source, move.getMove()))
		    throw new Error("player " + players[turn] + " for league " + turn + " should only take legal moves: " + move);
	    } else
		throw new Error("player " + players[turn] + " for league " + turn + " found no move: " + action);
	} 
    } 


    /**
     * A human player that waits for user I/O and delivers the user's decision.
     */
    private class HumanPlayer extends FieldChangeAdapter implements Function {
	//@internal we are transient
	/**
	 * The option that the user has chosen.
	 */
	private Option option;
	/**
	 * The communication lock.
	 */
	private Object userAction = new Object();
	public Object apply(Object field) {
	    Option user;
	    try {
		synchronized(userAction) {
		    if (option == null)
			userAction.wait();
		    user = this.option;
		    this.option = null;
		}
		return user;
	    }
	    catch (InterruptedException interrupt) {
		Thread r = runner;
		stop();
		r.interrupt();
		Thread.currentThread().interrupt();
		throw new InternalError("OutOfCheeseError");
	    }
	}
	public void movePerformed(FieldChangeEvent evt) {
	    assert evt.getField() == getField() : "we have only registered ourselves to our field";
	    if ((evt.getType() & (FieldChangeEvent.USER_ACTION | FieldChangeEvent.MOVE)) == (FieldChangeEvent.USER_ACTION | FieldChangeEvent.MOVE)) {
		synchronized(userAction) {
		    this.option = (Option) evt.getChangeInfo();
		    userAction.notify();
		}
	    }
	}
    }
}
