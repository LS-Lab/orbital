/*
 * @(#)Closer.java 0.9 1998/02/15 Andre Platzer
 * 
 * Copyright (c) 1998 Andre Platzer. All Rights Reserved.
 */

package orbital.awt;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.Frame;
import java.awt.Window;
import orbital.logic.functor.Predicate;

import javax.swing.JOptionPane;
import java.awt.AWTEventMulticaster;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.NotSerializableException;

/**
 * If Closer is registered to a component, it will automatically close it on user requests.
 * On close, it will send an ActionEvent <code>"close"</code> to all listeners registered to Closer.
 * If an additional parental Frame is specified, the Component
 * is only closed on Confirmation.
 * <p>
 * A Closer registered to window events will react on closing events.
 * When it registers to key events, it will react on the keycombos <code>Alt-.</code> or <code>Alt-,</code> or <code>Ctrl-Break</code> as well.
 * Remember that, in some situations, you might have to register it to TextFields etc. by hand.
 * <p>
 * For example:<pre>
 * <span class="Class">Frame</span> f <span class="operator">=</span> <span class="operator">new</span> <span class="Class">Frame</span>();
 * <span class="comment">// register to closing WindowEvents</span>
 * <span class="keyword">boolean</span> register <span class="operator">=</span> <span class="keyword">true</span>;
 * <span class="comment">// and to special KeyEvents</span>
 * <span class="comment">// without parental Frame</span>
 * <span class="Orbital">Closer</span> closer <span class="operator">=</span> <span class="keyword">new</span> <span class="Orbital">Closer</span>(f,register);
 * <span class="Class">Button</span> b <span class="operator">=</span> <span class="keyword">new</span> <span class="Class">Button</span>(<span class="String">"Please Close"</span>);
 * f.add(b);
 * <span class="comment">// register to Button's ActionEvents</span>
 * b.addActionListener(closer);
 * </pre>
 * <p>
 * Never register a closer as an actionListener to itself.
 * 
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public final class Closer extends WindowAdapter implements ActionListener, Predicate {
    /**
     * parental frame. If set, will ask before closing.
     */
    protected Frame             parent;

    /**
     * the Component or Window to be closed
     */
    protected Component component;

    /**
     * the question to ask when closing. Positively formulated, will close on affirmation.
     */
    private String              questionOnClosing = "Close window without Saving?";

    /**
     * Close instantly.
     * @param comp the component to be closed on demand.
     */
    public Closer(Component comp) {
        this(null, comp);
    }

    /**
     * Close instantly; if register, self-register to WindowEvents.
     * @param comp the component to be closed on demand.
     * @param register whether this Closer should automatically register to comp's <tt>WindowEvents</tt> for closing events via <code>comp.addWindowListener(this)</code>.
     */
    public Closer(Window comp, boolean register) {
        this(null, comp, register);
    }

    /**
     * Close instantly; if register, self-register to WindowEvents,
     * @param comp the component to be closed on demand.
     * @param register whether this Closer should automatically register to comp's <tt>WindowEvents</tt> for closing events via <code>comp.addWindowListener(this)</code>.
     * @param definitively whether after a successful close action, Closer should exit the JVM via <code>System.exit(0)</code>.
     */
    public Closer(Window comp, boolean register, boolean definitively) {
        this(null, comp, register, definitively);
    }

    /**
     * Close only after confirmation dialog
     * @param parent the frame to be used as parent for confirmation dialogs. <code>null</code> if no confirmation is desired.
     * @param comp the component to be closed on demand.
     */
    public Closer(Frame parent, Component comp) {
        this();
        this.parent = parent;
        this.component = comp;
    }

    /**
     * Close only after confirmation dialog; if register, self-register to WindowEvents.
     * @param parent the frame to be used as parent for confirmation dialogs. <code>null</code> if no confirmation is desired.
     * @param comp the component to be closed on demand.
     * @param register whether this Closer should automatically register to comp's <tt>WindowEvents</tt> for closing events via <code>comp.addWindowListener(this)</code>.
     */
    public Closer(Frame parent, Window comp, boolean register) {
        this();
        this.parent = parent;
        this.component = comp;
        if (register) {
            comp.addKeyListener(new orbital.moon.awt.SystemRequestor(this));
            comp.addWindowListener(this);
        } 
    }

    /**
     * Close only after confirmation dialog; if register, self-register to WindowEvents,
     * if definitifely force termination via System.exit().
     * @param parent the frame to be used as parent for confirmation dialogs. <code>null</code> if no confirmation is desired.
     * @param comp the component to be closed on demand.
     * @param register whether this Closer should automatically register to comp's <tt>WindowEvents</tt> for closing events via <code>comp.addWindowListener(this)</code>.
     * @param definitively whether after a successful close action, Closer should exit the JVM via <code>System.exit(0)</code>.
     */
    public Closer(Frame parent, Window comp, boolean register, boolean definitively) {
        this(parent, comp, register);
        if (definitively)
            addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // definite close
                        System.exit(0);
                    } 
                });
    }

    /**
     * Close only after confirmation dialog; if register, self-register to WindowEvents,
     * if definitifely force termination via System.exit().
     * @param parent the frame to be used as parent for confirmation dialogs. <code>null</code> if no confirmation is desired.
     * @param questionOnClosing which question to ask before closing component.
     * Will only close on affirmation.
     * @param comp the component to be closed on demand.
     * @param register whether this Closer should automatically register to comp's <tt>WindowEvents</tt> for closing events via <code>comp.addWindowListener(this)</code>.
     * @param definitively whether after a successful close action, Closer should exit the JVM via <code>System.exit(0)</code>.
     */
    public Closer(Frame parent, String questionOnClosing, Window comp, boolean register, boolean definitively) {
        this(parent, comp, register, definitively);
        this.questionOnClosing = questionOnClosing;
    }

    private Closer() {
        addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (component instanceof Window) {
                        // dispose
                        component.setVisible(false);
                        ((Window) component).dispose();
                    } else
                        // inivisibilize
                        component.setVisible(false);
                } 
            });
    }



    /**
     * The close action. Will send an ActionEvent "close" to all registered listeners.
     */
    public void close() {
        // initiate close
        new Thread(new Runnable() {
                public void run() {
                    processActionEvent(new ActionEvent(this, 0xFade, "close"));
                }
            }).start();
    } 

    /**
     * Request to close, but (if parent is specified) only after confirmation.
     */
    public void requestClose() {
        if (parent != null) new Thread(new Runnable() {
                public void run() {
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, questionOnClosing, "Close", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE))
                        /*QuestionDialog confirm = new QuestionDialog(parent, "Close", questionOnClosing);
                          confirm.start();
                          if (confirm.getResult().equals("Yes"))*/
                        close();
                    else
                        ;        // closing request rejected
                }
            }).start();
        else
            close();
    } 


    // listeners invocing a requestClose()

    public boolean apply(Object a) {
        // TODO: interrupt instead of close for SystemRequestor.INTERRUPT
        requestClose();
        return true;
    } 

    /**
     * when notified, request a close.
     */
    public void actionPerformed(ActionEvent e) {
        requestClose();
    } 

    public void windowClosing(WindowEvent e) {
        requestClose();
    } 

    /*
     * public void windowClosed(WindowEvent e) {
     * // window closed event
     * }
     */


    final static String          actionListenerK = "actionL";
    transient ActionListener actionListener = null;

    /**
     * Processes action events occurring on this object
     * by dispatching them to any registered
     * <code>ActionListener</code> objects.
     * 
     * @param       e the action event.
     * @see         java.awt.event.ActionListener
     * @see         #addActionListener(ActionListener)
     */
    protected void processActionEvent(ActionEvent e) {
        if (actionListener != null) {
            actionListener.actionPerformed(e);
        } 
    } 

    /**
     * Adds the specified action listener to receive action events from
     * this object.
     * If l is null, no exception is thrown and no action is performed.
     * 
     * @param         l the action listener
     * @see           java.awt.event.ActionListener
     * @see           #removeActionListener(ActionListener)
     */
    public synchronized void addActionListener(ActionListener l) {
        if (l == null) {
            return;
        } 
        actionListener = AWTEventMulticaster.add(actionListener, l);
    } 

    /**
     * Removes the specified action listener so that it no longer
     * receives action events from this object.
     * If l is null, no exception is thrown and no action is performed.
     * 
     * @param           l     the action listener
     * @see             java.awt.event.ActionListener
     * @see             #addActionListener(ActionListener)
     */
    public synchronized void removeActionListener(ActionListener l) {
        if (l == null) {
            return;
        } 
        actionListener = AWTEventMulticaster.remove(actionListener, l);
    } 

    /**
     * Writes default serializable fields to stream.  Writes
     * a list of serializable ItemListener(s) as optional data.
     * The non-serializable ItemListner(s) are detected and
     * no attempt is made to serialize them.
     * 
     * @serialData Null terminated sequence of 0 or more pairs.
     * The pair consists of a String and Object.
     * The String indicates the type of object and
     * is one of the following :
     * itemListenerK indicating and ItemListener object.
     * 
     * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
     * @see java.awt.Component#itemListenerK
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        throw new NotSerializableException("serialization depends upon AWTEventMulticaster.save()");

        /*
         * s.defaultWriteObject();
         * AWTEventMulticaster.save(s, actionListenerK, actionListener);
         * s.writeObject(null);
         */
    } 

    /**
     * Read the ObjectInputStream and if it isnt null
     * add a listener to receive item events fired
     * by the button.
     * Unrecognised keys or values will be Ignored.
     * @serial
     * @see #removeActionListener(ActionListener)
     * @see #addActionListener(ActionListener)
     */
    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        throw new NotSerializableException("deserialization depends upon serialization");

        /*
         * s.defaultReadObject();
         * 
         * Object keyOrNull;
         * while(null != (keyOrNull = s.readObject())) {
         * String key = ((String)keyOrNull).intern();
         * 
         * if (actionListenerK == key)
         * addActionListener((ActionListener)(s.readObject()));
         * 
         * else // skip value for unrecognized key
         * s.readObject();
         * }
         */
    } 
}
