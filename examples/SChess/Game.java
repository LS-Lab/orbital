/**
 * @(#)Game.java    0.9 98/07/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

import orbital.game.GameRules;
import orbital.moon.awt.AppletFrame;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Runnable application Game.
 *
 * @version 0.8, 2001/11/23
 * @author  Andr&eacute; Platzer
 */
public class Game extends orbital.game.Game {
    /**
     * Application-start entry point.
     */
    public static void main(String args[]) throws Exception {
	System.out.println("usage: java Game gameRules=ChessRules aIntelligence=1\nor\tjava Game gameRules=ChessRules aIntelligence=0");
	AppletFrame.showApplet(new Game(), "Game Application", args);
    } 

    /**
     * Runnable-init entry point.
     */
    public Game() {}

    protected GameRules createGameRules(String gameRules) throws InstantiationException, IllegalAccessException, ClassNotFoundException, ClassCastException {
	return (GameRules) Class.forName(gameRules).newInstance();
    } 

    public void load(ObjectInputStream is) throws ClassNotFoundException, IOException {
	super.load(is);
    } 
}
