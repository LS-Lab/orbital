package orbital.util;

import java.util.Observable;

/**
 * Class PredicateObservable is an Observable class which may be used
 * for some Predicates and callback Methods in Combination with the
 * Observer interface.
 * 
 * @deprecated since orbital0.9
 */
class PredicateObservable extends Observable {

	/**
	 * That's what you need to call before a call of notifyObservers.
	 */
	public void setChanged() {
		super.setChanged();
	} 
}
