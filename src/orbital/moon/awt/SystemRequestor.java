/**
 * @(#)SystemRequestor.java 0.9 1999/08/01 Andre Platzer
 * 
 * Copyright (c) 1999 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import orbital.logic.functor.Predicate;
import java.awt.Component;

/**
 * This class is a SystemRequestor that will call a predicate on interrupt or abort
 * requests when registered as a key listener.
 * <p>
 * Use one of:<pre>
 * component.addKeyListener(<span class="keyword">new</span> <span class="Orbital">SystemRequestor</span>(<var>reactorPredicate</var>));
 * <span class="keyword">new</span> <span class="Orbital">SystemRequestor</span>(<var>reactorPredicate</var>, component);
 * </pre></p>
 * <p>
 * Note that if you intend to let the predicate abort the application without further user feedback
 * SystemRequestor needs being the <em>first</em> key listener to notify.</p>
 * 
 * @version 0.9, 1999/08/01
 * @author  Andr&eacute; Platzer
 */
public
class SystemRequestor extends KeyAdapter {
	public static final int INTERRUPT = 2;
	public static final int ABORT = 8;
	protected Predicate		request;

	/**
	 * Construct a SystemRequestor calling the given predicate on interrupt and abort requests.
	 * @param requestReactor the predicate called when an interrupt or abort request occured.
	 * This predicate can perform the corresponding operations to interrupt running thread
	 * or abort the program.
	 */
	public SystemRequestor(Predicate requestReactor) {
		this.request = requestReactor;
	}

	/**
	 * Construct a SystemRequestor calling the given predicate on interrupt and abort requests.
	 * Automatically registers as a key listener of the component specified.
	 * @param requestReactor the predicate called when an interrupt or abort request occured.
	 * This predicate can perform the corresponding operations to interrupt running thread
	 * or abort the program.
	 * @param comp the component to register to.
	 */
	public SystemRequestor(Predicate requestReactor, Component comp) {
		this(requestReactor);
		comp.addKeyListener(this);
	}

	/**
	 * Check for Abort and Interrupt requests.
	 */
	public void keyPressed(KeyEvent e) {
		if (e.isAltDown()) {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_COMMA:					 // Alt-, "Alt-Comma" => Interrupt
					request.apply(new Integer(INTERRUPT));
					break;
				case KeyEvent.VK_PERIOD:				 // Alt-. "Alt-Period" => Abort
					request.apply(new Integer(ABORT));
					break;
				default:
					return;
			}
		} else if (e.isControlDown())

			// TODO dont go: Ctrl-Break does never occur
			if (e.getKeyCode() == KeyEvent.VK_CANCEL)	 // Ctrl-Break
				request.apply(new Integer(ABORT));

				// TODO: catch SysReq system request
	} 
}
