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
     * The diverse AI-players.
     * <code>players[i] instanceof HumanPlayer</code> if and only if <code>i</code> is a human player.
     * @serial
     */
    private Function	players[];


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
     * @todo serial?
     */
    private HumanPlayer humanPlayer = null;


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
     * @xxx we cannot store threads, can we?
     */
    //XXX: concurrent synchronization may be required for this volatile field
    private volatile Thread runner = null;

    /**
     * Create a new gamemaster for the game with the given rules.
     * Already specify a particular initial field for playing the game on.
     * @param component for which component to load images etc.
     * @param rules the rules of the game to play.
     * @param players the individual players of our game.
     *  If you set a component to <code>null</code>, a human player will take the part of that league.
     *  Especially, the value of <code>players[Figure.NOONE]</code> will be ignored.
     * @param initialField the field to play the game on.
     *  When <code>null</code>, a new field will be {@link GameRules#startField(Component) started from the rules}.
     * @pre initialField fits to rules
     */
    public Gamemaster(Component component, GameRules rules, Function players[], Field initialField) {
	if (players.length != rules.getLeagues())
	    throw new IllegalArgumentException("Illegal number of players, expected: " + rules.getLeagues() + " found: " + players.length);
	this.component = component;
	this.setGameRules(rules);
	this.setField(initialField);
	this.setPlayers(players);
    }
    /**
     * Create a new gamemaster for the game with the given rules.
     * Since no field is specified, a new field will be started from the rules.
     * @param component for which component to load images etc.
     * @param rules the rules of the game to play.
     * @param players the individual players of our game.
     *  If you set a component to <code>null</code>, a human player will take the part of that league.
     *  Especially, the value of <code>players[Figure.NOONE]</code> will be ignored.
     */
    public Gamemaster(Component component, GameRules rules, Function players[]) {
	this(component, rules, players, null);
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
    private void setPlayers(Function newPlayers[]) {
	assert newPlayers[Figure.NOONE] == null : Figure.NOONE + " needs no player, so you can safely set it to null";
	this.players = new Function[newPlayers.length];
	for (int i = Figure.NOONE + 1; i < players.length; i++) {
	    this.players[i] = newPlayers[i] != null
		? newPlayers[i]
		//@xxx horribly complicated formulation
		: (this.humanPlayer = (this.humanPlayer != null ? this.humanPlayer : new HumanPlayer()));
	}
    }	
    
    /**
     * Get the field which the game is played on.
     */
    public Field getField() {
	return field;
    }
    protected final void setField(Field field) {
	this.field = field;
    }

    /**
     * Get the thread we are currently running on.
     * @return non-<code>null</code> until we have been stopped.
     */
    protected final Thread getRunner() {
	return runner;
    }

    
    /**
     * (Start entry point) starts a new game.
     */
    public void start() {
	if (getField() == null)
	    setField(rules.startField(component));
	getField().addFieldChangeListener(endOfGameListener);
	if (humanPlayer != null)
	    getField().addFieldChangeListener(humanPlayer);
	this.runner = new Thread(this, "Gamemaster");
	runner.start();
		// in case human players exist, but computer commences, we should obey the following:
		//@internal let our clients at least have a chance to finish initialization after start() returns so that they can register listeners.
		//@xxx However this is not 100% reliable because the following thread could start too early.
    } 

    /**
     * (Stop exit point) stops the current game.
     */
    public void stop() {
	Thread moribund = runner;
	runner = null;	  // runner.stop();
	if (moribund != null)
	    moribund.interrupt();
	Field field = getField();
	if (field != null) {
	    field.removeFieldChangeListener(endOfGameListener);
	    if (humanPlayer != null)
		field.removeFieldChangeListener(humanPlayer); //@todo right?
	}
    } 

    /**
     * (Destroy exit point).
     */
    public void destroy() {
	setGameRules(null);
	setField(null);
	players = null;
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
	}
    } 

    /**
     * Called at the end of each user turn.
     * Once all real players made their turn, it will let all AIs take their actions.
     * Notifies the current GameRules implementation that a turn is done.
     * @see GameRules#performedMove(Field)
     * @internal this is a kind of event handler.
     */
    private final void playGame() {
	Thread thisThread = Thread.currentThread();
	// do moves while it's an AI's turn
	for (int turn = getField().getTurn();
	     runner == thisThread && !Thread.interrupted();
	     turn = getField().getTurn()) {
	    ////showStatus(getResources().getString("statusbar.ai.thinking"));
	    System.err.println("statusbar.ai.thinking");
	    Object action = players[turn].apply(getField());
	    ////showStatus(getResources().getString("statusbar.ai.moving"));
	    if (action instanceof Option) {
		Option move = (Option) action;
		Position source = new Position(move.getFigure());
		// if we could rely on our AI, then we could optimize away this expensive moving and simply use the resulting setField(move.getState());
		// But unfortunately, all our field's listeners would then vanish, possibly including end of game checks.
		//@internal cloning the position information is necessary, otherwise move would detect that it gets lost during swap.
		if (!getField().move(source, move.getMove()))
		    throw new Error("player " + players[turn] + " for league " + turn + " should only take legal moves: " + move);
	    } else
		throw new Error("player " + players[turn] + " for league " + turn + " found no move: " + action);
	} 
    } 


    /**
     * A human player that waits for user I/O and delivers the user's decision.
     * @version 1.1, 2003-01-04
     * @author Andr&eacute; Platzer
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
	private final Object userAction = new Object();
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
		if (r != null)
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
