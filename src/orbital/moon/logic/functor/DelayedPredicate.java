/*
 * @(#)DelayedPredicate.java 0.9 1999/01/10 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.logic.functor;

import orbital.logic.functor.Predicate;

/**
 * DelayedPredicate is a helper class that applies a Predicate after having waited
 * a certain amount of time.
 * 
 * @version 0.9, 10/01/99
 * @author  Andr&eacute; Platzer
 */
public
class DelayedPredicate/*<A>*/ {
	protected final Predicate/*<A>*/ delayed;
	protected final long	     delayMillis;
	public DelayedPredicate(Predicate/*<A>*/ delayed, long delayMillis) {
		this.delayed = delayed;
		this.delayMillis = delayMillis;
	}

	public void apply(final Object/*>A<*/ arg) {
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(delayMillis);
					delayed.apply(arg);
				} catch (InterruptedException irq) {
					Thread.currentThread().interrupt();
				} 
			} 
		}, "DelayedPredicate sleeper").start();
	} 
}
