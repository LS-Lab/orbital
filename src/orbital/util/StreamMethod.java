/**
 * @(#)StreamMethod.java 1.0 2000/08/09 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.util;

import java.util.Iterator;

import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Base class for stream method coroutines that are concurrent and streamed architectural connectors.
 * Stream methods allow "continuation reentrance" for abstract communication connectors that return an iterated stream
 * and might be running concurrent or synchronous.
 * Coroutines also provide an easy way of implementing non-deterministic functions with a multitude
 * of return-values instead of a single distinct return-value. The caller will receive these
 * multiple return-values in the given order.
 * <p>
 * You can easily use stream method coroutines like with this preorder traversal implementation<pre>
 * <span class="Class">Iterator</span> traversal = <span class="keyword">new</span> <span class="Orbital">StreamMethod</span>(<span class="keyword">true</span>) {
 *     <span class="keyword">public</span> <span class="keyword">void</span> runStream() {
 *         visit(getRoot());
 *     }
 *     <span class="keyword">protected</span> <span class="keyword">final</span> <span class="keyword">void</span> visit(<span class="Orbital">Node</span> node) {
 *         resumedReturn(node);
 *         <span class="keyword">for</span> (<span class="Class">Iterator</span> i <span class="operator">=</span> node.edges(); i.hasNext(); )
 *             visit((<span class="Orbital">Node</span>) i.next());
 *     }
 * }.apply();
 * </pre>
 * </p>
 * <p>
 * <b>Note:</b> This class is not necessarily thread-safe. You should not use an instance of
 * StreamMethod from several threads without explicit synchronization.
 * </p>
 * <p>
 * Also make sure that the Thread using the iterator of this StreamMethod coroutine has either
 * the same priority as the creator thread of it and therefore the stream method.
 * Or at least provide that the StreamMethod has the higher one.
 * Otherwise in a system with many threads but without priority inheritance or priority ceiling,
 * the StreamMethod might not get a chance to produce new data at all.
 * </p>
 * <hr>
 * <p>
 * The implementation follows a modified producer/consumer pattern.
 * It uses safe suspend and resume techniques, but nevertheless be cautious when obtaining a lock
 * on the monitor of critical system resources within runStream() to prevent deadlocks.
 * </p>
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @see <a href="doc-files/StreamMethod.png">concurrent implementation</a>
 * @see <a href="">UML</a>
 * @see <a href="{@docRoot}/Patterns/Design/ConsumerProducer.html">Consumer Producer</a>
 * @internal This is a very tricky concurrent implementation!
 * @todo note that StreamMethod is deadlock prone if the buffer is not restricted in size.
 * @todo volatilize the entry list as in  [4 - Monitors.pdf]
 * @todo proof that StreamMethod itself cannot lead to deadlocks but implements Consumer Producer pattern correctly.
 * @fixme we "sometimes" seem to have a deadlock because we get stuck up in old threads that don't gonna vanish (perhaps @see System#runFinalizersOnExit(boolean))
 */
public abstract class StreamMethod extends Thread implements Callback {
    static final Logger logger = Logger.getLogger(StreamMethod.class.getName());
    /**
     * Common ThreadGroup for all StreamMethods.
     * @todo what to do with uncaught exceptions? Rethrow when an iterator method is called?
     */
    private static final ThreadGroup streamMethodCoroutines = new ThreadGroup("Stream method coroutines");
    static {
        // we might still need our thread group later, so we do not let it destroy itself, once it feels no longer needed
        streamMethodCoroutines.setDaemon(false);
    }

    // against dangling threads (empirically does not seem to be necessary)
    //  /**
    //   * Controller for StreamMethod coroutines that encourages finalization
    //   * when number of threads in streamMethodCoroutines is high
    //   */
    //  private static final Thread              streamMethodCoroutineController = new Thread(streamMethodCoroutines, "Stream method coroutine controller") {
    //          private final int SUSPICIOUS_THREAD_THRESHOLD = 100;
    //          private final int CONTROLLER_ACTIVITY_PERIOD = 1000;
    //          //@todo optimize, we can let this thread wait when activeCount()==0
    //          public void run() {
    //                  try {
    //                          while (true) {
    //                                  int activeThreads = Thread.activeCount();
    //                                  if (activeThreads > SUSPICIOUS_THREAD_THRESHOLD) {
    //                        System.runFinalization();
    //                        System.gc();
    //                        logger.log(Level.FINER, "run finalization due to {0} threads", new Integer(activeThreads));
    //                        //@todo yield to finalizer such that he has max priority (priority inheritance)
    //                        Thread.yield();
    //                                  } else if (activeThreads == 0)
    //                                          ;       // wait for the next Thread in our ThreadGroup to appear, instead of just sleeping a period
    //                                  Thread.sleep(CONTROLLER_ACTIVITY_PERIOD);
    //                          }
    //                  }
    //                  catch (InterruptedException irq) {
    //                          Thread.currentThread().interrupt();
    //                  }
    //          }
    //  };
    //  static {
    //        // if coroutines are the only threads left, then nobody seems to need them anymore, so quit
    //        streamMethodCoroutineController.setDaemon(true);
    //        // ordinary priority for streamMethodCoroutineController, because otherwise (higher priority) we might end up controlling finalization only, because we really need 3000 Threads. Or (lower priority) the controller would never get the chance to improve anything at all
    //    }
                
        
    /**
     * For autonumbering anonymous threads.
     */
    private static int streamMethodInitNumber;
    private static synchronized int nextStreamMethodNum() {
        return streamMethodInitNumber++;
    }

    /**
     * Whether this stream method is a synchronous or asynchronous (i.e. concurrent) connector.
     * @serial
     */
    private final boolean                        synchronousConnector;

    /**
     * The queued iterator stream buffering the results.
     * @serial
     */
    private QueuedIterator                       resultStream;

    /**
     * Construct this StreamMethod as a synchronous coroutine stream connector.
     */
    protected StreamMethod() {
        this(true);
    }

    /**
     * Construct this StreamMethod as a coroutine stream connector.
     * @param synchronousConnector <code>true</code> if this StreamMethod should run
     *  as a synchronous connector with synchronous method calls running on demand.
     *  If <tt>false</tt>, it is running as an asynchronous connector with an asynchronous thread
     *  running in background to collect results.
     *  <p>
     *  While asynchronous connectors usually have an advantage in speed, they may need a
     *  big buffer for the results, whether they will ever be used or not.</p>
     */
    protected StreamMethod(boolean synchronousConnector) {
        super(streamMethodCoroutines, "StreamMethod-" + nextStreamMethodNum());
        // if coroutines are the only threads left, then nobody seems to need them anymore, so quit
        setDaemon(true);
        this.synchronousConnector = synchronousConnector;
        this.resultStream = synchronousConnector
            ? new ResumingQueuedIterator(this)
            : new InquiringQueuedIterator(this);
        //              if (!streamMethodCoroutineController.isAlive())
        //              streamMethodCoroutineController.start();
    }

    /**
     * Call to apply this stream method coroutine.
     * <p>
     * <b>Note:</b> This method is not necessarily thread-safe. You should not use an instance of StreamMethod
     * from several threads without explicit synchronization.</p>
     * @return a stream iterator containing the return values of this coroutine.
     */
    public Iterator apply() {
        // to prevent someone from setting it to null just because run is already done
        QueuedIterator moribund = resultStream;
        if (synchronousConnector)
            safeSuspend();
        start();
        return moribund;
    } 

    /**
     * Call this method to return a value from the stream coroutine.
     * If not aborted, execution will resume afteron, although some time may have passed in between.
     * <p>
     * This method can be used to return a multitude of values to the caller.</p>
     * @param ret the return-value to pass to the caller
     */
    protected void resumedReturn(Object ret) {
        synchronized (this) {
            resultStream.add(ret);
            requested = false;

            logger.log(Level.FINER, "< resumedReturn ", ret);
            notifyAll();        // notify of new data
        } 

        // let other threads do their job as well
        Thread.yield();

        // sychronous connectors can pause now
        if (synchronousConnector && !requested)
            safeSuspend();
        checkSuspend();

        logger.log(Level.FINER, "> resume ... ", ret);
    } 

    private volatile boolean requested = false;

    /**
     * Request next data forcing resume and wait if necessary.
     */
    public void request() {
        if (isAlive()) {                 // could still produce stream data
            requested = true;
            if (isSuspended())
                safeResume();    // wake up to produce new stream data

            // if we have do not have any data but still have a chance to get some, wait
            // XXX: synchronized checking for isEmpty or using sychronized collection?
            try {
                while (resultStream != null && resultStream.isEmpty()) {
                    synchronized (this) {
                        wait();    // wait for resumedReturn or permanent end of stream
                    } 
                } 
            } catch (InterruptedException irq) {
                Thread.currentThread().interrupt();
            } 
        } 
    } 

    /**
     * StreamMethod implementation method.
     * Can use {@link #resumedReturn(Object)} to return a value and resume execution,
     * and <span class="keyword">return</span> to end continuation reentrant execution.
     */
    protected abstract void runStream();

    /**
     * Do not call, directly.
     */
    public final void run() {
        checkSuspend();
        runStream();
        logger.log(Level.FINEST, "< return and exit");
        synchronized (this) {
            resultStream = null;        // mark permanent end of stream
            notifyAll();                        // notify of permanent end of stream
        } 
    } 

    // Safe suspension implementation

    /**
     * Common suspension lock.
     * @serial
     */
    private Object                       suspension = new Object();

    /**
     * Whether we are suspended.
     * <b>Note:</b> this volatile field seems to occur in sychronized blocks, only. Might be redundant?
     * @serial
     */
    private volatile boolean suspended = false;

    /**
     * Safely suspends this thread.
     * Will apply the next time, checkSuspend() is called.
     * @see #checkSuspend()
     * @see Thread#suspend()
     */
    protected final void safeSuspend() {
        synchronized (suspension) {
            suspended = true;
        } 
    } 

    /**
     * Safely resumes this thread.
     * @see Thread#resume()
     */
    protected final void safeResume() {
        synchronized (suspension) {
            if (!suspended)
                return;
            suspended = false;
            suspension.notify();
        } 
    } 

    /**
     * Whether this thread is safely suspended
     */
    protected final boolean isSuspended() {
        synchronized (suspension) {
            return suspended;
        } 
    } 

    /**
     * Checks for suspend and if safe suspended, then waits in a loop.
     */
    private final void checkSuspend() {
        if (suspended)
            try {
                synchronized (suspension) {
                    // wait loop when we are (safe) suspended
                    while (suspended) {
                        suspension.wait();
                        assert !suspended : "if we wait, we should only wake up when we are not suspended any more";
                    } 
                } 
            } catch (InterruptedException irq) {
                logger.log(Level.WARNING, "safeSuspend/resumedReturn() had wait interrupted", irq);
                Thread.currentThread().interrupt();
            } 
    } 

    /**
     * Safely stops this thread.
     * @see Thread#stop()
     */
    final void safeStop() {
        logger.log(Level.FINER, "stop coroutine");
        // when we are the last thread and blocked (alias safe suspended in checkSuspend()) then we must stop in order for our daemon thread group to stop and quit the virtual machine. But this is already achieved by making only this thread as a daemon!
        this.interrupt();
        // let this thread at least have a short chance of packing our things and go
        Thread.yield(/*this*/);
    }


    /**
     * Print an information string in a nice way. Left or right, depending which thread calls.
     * A debug utility method which is necessary!
     */
    /*private static void print(String info) {
      Thread t = Thread.currentThread();
      //logger
      if (t.getName().startsWith("StreamMethod"))
      System.err.println("\t" + info + "\t--");
      else
      System.err.println("\t\t\t\t--\t" + info);
      }*/
}


/**
 * QueuedIterator that inquires the StreamMethod for new data instead
 * of instantly returning false from hasNext().
 */
class InquiringQueuedIterator extends QueuedIterator {

    /**
     * The stream method where the data comes from.
     * This is the object to resume.
     * @serial
     */
    private final StreamMethod method;
        
    protected final StreamMethod getMethod() {
        return method;
    }

    public InquiringQueuedIterator(StreamMethod method) {
        super(false);
        this.method = method;
    }

    public boolean hasNext() {
        if (super.hasNext())
            return true;
        getMethod().request();
        return super.hasNext();
    } 
    public Object next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return super.next();
    } 
}

/**
 * QueuedIterator that resumes the StreamMethod on every request for next() (resp. hasNext()).
 */
// TODO: do we need this class?
class ResumingQueuedIterator extends InquiringQueuedIterator {
    public ResumingQueuedIterator(StreamMethod method) {
        super(method);
    }

    /*
      public Object next() {
      //TODO: request and resume in every case, even if we still have one
      return super.next();
      }
    */
         
    /**
     * If no client requires this iterator any more, this object should be finalized.
     * Then we (at least try to) stop the stream method thread.
     */
    protected void finalize() throws Throwable {
        getMethod().safeStop();
        // how about .stop() as well, because if the thread never gets running again, it will never have a chance to pack his things and go. However, if we simply stop the whole thing, any locks that the thread obtains will not get unlocked.
        //Thread.yield() was already called in safeStop()
        getMethod().stop();
        super.finalize();
        StreamMethod.logger.log(Level.FINER, "finalizing {0}", getMethod());
    }
}
