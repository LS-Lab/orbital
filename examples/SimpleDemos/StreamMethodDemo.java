import orbital.util.StreamMethod;
import java.util.Iterator;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Demonstrates stream methods and their performance in synchronous and asynchronous mode.
 * The main method will start various stream methods synchronously as well as asynchronously.
 * Extensive processing is simulated by a call to Thread.sleep().
 * One can use it to compare the runtime behaviour of synchronous and asynchronous methods.
 * 
 * @version 0.9, 2000/08/11
 * @author  Andr&eacute; Platzer
 */
public class StreamMethodDemo {
    private static final Logger logger = Logger.getLogger(StreamMethod.class.getName());

    /**
     * simulate extensive processing.
     */
    private static final int DELIVERY_DELAY = 2000;

    /**
     * simulate an extensive processing gap between request and use of data.
     */
    private static final int USE_DELAY = 7000;
    /**
     * delay time in between two experiment sets.
     */
    private static final int INTERMEDIATE_DELAY = 4000;

    public static void main(String arg[]) throws Exception {
	// all sychronous connectors
	logger.log(Level.INFO, "Show {0}", "synchronous connectors");
	test_synchronous();
	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "Show {0}", "asynchronous connectors");
	Thread.sleep(INTERMEDIATE_DELAY);
	test_asynchronous();
	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "Show {0}", "synchronous connectors (half used)");
	test_synchronousHalf();
    } 

    private static void test_synchronous() throws InterruptedException {
	logger.log(Level.INFO, "synchronous connector {0}", "IDIU - instant delivery instant use");
	StreamMethod m = new StreamMethod(true) {
		public void runStream() {
		    resumedReturn("1");
		    resumedReturn("2");
		    resumedReturn("3");
		} 
	    };
	Iterator i = m.apply();
	while (i.hasNext())
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "synchronous connector {0}", "IDDU - instant delivery delayed use");
	m = new StreamMethod(true) {
		public void runStream() {
		    resumedReturn("1");
		    resumedReturn("2");
		    resumedReturn("3");
		} 
	    };
	i = m.apply();
	Thread.sleep(USE_DELAY);
	while (i.hasNext())
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "synchronous connector {0}", "DDIU - delayed delivery instant use");
	m = new StreamMethod(true) {
		public void runStream() {
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("1");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("2");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("3");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		} 
	    };
	i = m.apply();
	while (i.hasNext())
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "synchronous connector {0}", "DDDU - delayed delivery delayed use");
	m = new StreamMethod(true) {
		public void runStream() {
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("1");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("2");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("3");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		} 
	    };
	i = m.apply();
	Thread.sleep(USE_DELAY);
	while (i.hasNext())
	    System.out.println(i.next());
    } 

    private static void test_asynchronous() throws InterruptedException {

	// all asychronous connectors
	logger.log(Level.INFO, "asynchronous connector {0}", "IDIU - instant delivery instant use");
	StreamMethod m = new StreamMethod(false) {
		public void runStream() {
		    resumedReturn("1");
		    resumedReturn("2");
		    resumedReturn("3");
		} 
	    };
	Iterator i = m.apply();
	while (i.hasNext())
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "asynchronous connector {0}", "IDDU - instant delivery delayed use");
	m = new StreamMethod(false) {
		public void runStream() {
		    resumedReturn("1");
		    resumedReturn("2");
		    resumedReturn("3");
		} 
	    };
	i = m.apply();
	Thread.sleep(USE_DELAY);
	while (i.hasNext())
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "asynchronous connector {0}", "DDIU - delayed delivery instant use");
	m = new StreamMethod(false) {
		public void runStream() {
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("1");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("2");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("3");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		} 
	    };
	i = m.apply();
	while (i.hasNext())
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "asynchronous connector {0}", "DDDU - delayed delivery delayed use");
	m = new StreamMethod(false) {
		public void runStream() {
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("1");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("2");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("3");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		} 
	    };
	i = m.apply();
	Thread.sleep(USE_DELAY);
	while (i.hasNext())
	    System.out.println(i.next());
    } 

    private static void test_synchronousHalf() throws InterruptedException {
	final int useLength = 2;
	logger.log(Level.INFO, "synchronous connector {0}", "IDIU - instant delivery instant use");
	StreamMethod m = new StreamMethod(true) {
		public void runStream() {
		    resumedReturn("1");
		    resumedReturn("2");
		    resumedReturn("3");
		} 
	    };
	Iterator i = m.apply();
	for (int j = 0; j < useLength && i.hasNext(); j++)
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "synchronous connector {0}", "IDDU - instant delivery delayed use");
	m = new StreamMethod(true) {
		public void runStream() {
		    resumedReturn("1");
		    resumedReturn("2");
		    resumedReturn("3");
		} 
	    };
	i = m.apply();
	Thread.sleep(USE_DELAY);
	for (int j = 0; j < useLength && i.hasNext(); j++)
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "synchronous connector {0}", "DDIU - delayed delivery instant use");
	m = new StreamMethod(true) {
		public void runStream() {
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("1");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("2");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("3");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		} 
	    };
	i = m.apply();
	for (int j = 0; j < useLength && i.hasNext(); j++)
	    System.out.println(i.next());

	Thread.sleep(INTERMEDIATE_DELAY);
	logger.log(Level.INFO, "synchronous connector {0}", "DDDU - delayed delivery delayed use");
	m = new StreamMethod(true) {
		public void runStream() {
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("1");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("2");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		    resumedReturn("3");
		    try {
			Thread.sleep(DELIVERY_DELAY);
		    } catch (InterruptedException x) {}
		} 
	    };
	i = m.apply();
	Thread.sleep(USE_DELAY);
	for (int j = 0; j < useLength && i.hasNext(); j++)
	    System.out.println(i.next());
    } 
}
