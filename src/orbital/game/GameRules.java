/*
 * @(#)GameRules.java 0.9 1996/04/03 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.game;

import orbital.logic.functor.Function;
import java.awt.Component;
import java.awt.Image;

/**
 * An interface defining the behaviour of a generic game with a set of rules.
 * 
 * @version 0.9, 2000/02/26
 * @author  Andr&eacute; Platzer
 */
public interface GameRules {

    /**
     * Constructs a new Field to start the Game on.
     * @param component for which component to load images etc.
     *  Will often be an instance of <code>java.applet.Applet</code>.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Field startField(Component component);

    /**
     * Constructs a new AI Computer-Opponent for the Game.
     * @param arg can contain any value used to initialize the AI, including <code>null</code>.
     * @return a function that, when called with a situation state as its argument,
     *  will return the action it wants to take in that state. (See {@link MoveWeighting.Argument}).
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    Function startAIntelligence(String arg);

    /**
     * Get the image-object to be used for the given figure.
     * <p>
     * Do not use the <code>Figure.image</code> field, since after deserialization
     * this will be empty.
     * @param f the figure-template that we seek an image-object to set for.
     * @return an image-object that should be set for f, depending on the league, type, and possibly other information.
     * This image-object can either be looked-up from a cache or newly created.
     * @see FigureImpl#setImage(java.awt.Image)
     * @see Game#load(java.io.ObjectInputStream)
     */
    Image getImage(Figure f);

    /**
     * Get the number of different leagues including {@link Figure#NOONE}.
     * Assumes that the leagues are numbered consequently in ascending order from 1.
     * @return the number of leagues.
     */
    int getLeagues();

    /**
     * get the number of different Figure types including {@link Figure#EMPTY}.
     * Assumes that the types are numbered consequently in ascending order from 1.
     * @return the number of figure types.
     */
    int getFigureTypes();

    /**
     * Is called after each turn to check who won.
     * @param field the field the game is being played on to check who won the game.
     * @return a value whose absolute specifies the league that has won, {@link Figure#NOONE} if there is no winner yet.
     *  A positive number says that the specified player has reached the goal of the game.
     *  {@link Figure#NOONE} says that no one has won yet.
     *  The value is allowed to be negative in which case it indicates that
     *  the winner, instead of actively reaching the goal of the game, only won because all others have lost.
     */
    int turnDone(Field field);
}
