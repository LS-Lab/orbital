/*
 * @(#)TextAnimator.java 0.9 1996/03/02 Andre Platzer
 * 
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import orbital.awt.Animation;
import java.awt.Canvas;

/**
 * an Animator Class that animates text. It uses a given Canvas as a display.
 */
public
class TextAnimator implements Animation, Runnable {
	protected Canvas		 tCanvas;
	protected String		 text;
	private String			 disp = null;
	private int				 width;
	private volatile boolean looping = false;
	private int				 delayTime = 500;
	private volatile Thread  engine = null;

	public TextAnimator(String text, Canvas tCanvas) {
		this.text = text;
		this.tCanvas = tCanvas;
	}

	public void play() {
		engine = new Thread(this, "animation engine");
		//XXX: concurrent synchronization could be required since engine could have already changed again
		engine.start();
	} 

	public void stop() {
		looping = false;	// force run to exit
		Thread moribund = engine;
		engine = null;	  // engine.stop();
		moribund.interrupt();
	} 

	public void loop() {
		looping = true;
		play();
	} 

	public void speed(int ms) {
		delayTime = ms;
	} 


	public Canvas getCanvas() {
		return tCanvas;
	} 

	public void run() {
		Thread thisThread = engine;
		width = tCanvas.size().width;
		do {
			try {
				for (int pos = 0; pos < text.length(); pos++) {
					disp = text.substring(pos, text.length());
					tCanvas.repaint();
					Thread.currentThread().sleep(delayTime);
				} 
			} catch (InterruptedException irq) {Thread.currentThread().interrupt();}
		} while (looping && engine == thisThread);
	} 

}
