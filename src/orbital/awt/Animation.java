/*
 * @(#)Animation.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

/**
 * Animation interface, a high level abstraction
 * of any video/graphic/audio animation.
 * 
 * @version 0.9, 04/03/96
 * @author  Andr&eacute; Platzer
 */
public
interface Animation {

	/**
	 * Start playing the animation. Each time this method is called
	 * the animation restarts at the beginning.
	 */
	void play();

	/**
	 * Start playing the animation in a loop.
	 */
	void loop();

	/**
	 * Stop playing the animation.
	 */
	void stop();

	/**
	 * Sets the speed of this animation.
	 */
	void speed(int ms);
}
