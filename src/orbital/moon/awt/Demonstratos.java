/*
 * @(#)Demonstratos.java 1.0 1997/06/07 Andre Platzer
 * 
 * Copyright (c) 1997 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.applet.Applet;
import java.io.PrintStream;
import java.io.OutputStream;

import java.awt.TextComponent;
import java.awt.TextArea;
import java.awt.BorderLayout;

/**
 * Demonstratos is a base class for normal applications that want to show as an applet.
 * The text output (calced values, algorithmic information, debugging status) is redirected
 * to a TextArea.
 * Mainly this class is used as a base class for a demonstration of
 * simple things that should run as Application as well as Applet.
 * 
 * All a sub-class should do is: use Demonstratos.out for printing output.
 * 
 * The mechanism that distinguishes between a run as an Application and a
 * use as an Applet at Runtime simply checks wether start() has been called.
 * When called the output will be redirected as needed.
 * 
 * @version 1.0, 07/06/97
 * @author  Andr&eacute; Platzer
 * 
 * @see orbital.moon.awt.AppletFrame
 * @see java.io.PrintStream
 * @see java.applet.Applet
 */

// Application to Applet view Applet
public class Demonstratos extends Applet {

    /**
     * The Outputstream where to print to. This class decides whether or not to
     * print output to the Screen or into a TextArea. Depending on whether
     * or not Applet method start() was called.
     * @serial
     */
    public PrintStream out;

    /**
     * @serial
     */
    protected TextArea view;

    /**
     * @serial
     */
    protected String   runnable;

    /**
     * @serial
     */
    protected Thread   runnableThread;

    /**
     * When called (implicitly) without a later call of the Applet method start()
     * all output will be delivered to the Console-Screen as defaulted.
     * 
     * @see #start
     */
    public Demonstratos() {
	out = System.out;
	try {
	    System.setOut(out);
	}
	catch (SecurityException trial) {}
	catch (Throwable trial) {}		// this is necessary because Internet Explorer cheats and does not throw a SecurityException
    }

    public void init() {
	super.init();
	String param;

	param = getParameter("ROWS");
	int rows = param == null ? 10 : Integer.parseInt(param);

	param = getParameter("COLS");
	int cols = param == null ? 40 : Integer.parseInt(param);

	runnable = getParameter("RUNNABLE");

	view = new TextArea(rows, cols);
	view.setEditable(false);
	setLayout(new BorderLayout());
	add("Center", view);	// @version 1.0
    } 

    /**
     * If called (implicitly after constructor Demonstratos) all output
     * will now be directed to a TextArea sized as specified in the
     * Parameters ROWS, COLS.
     * 
     * @see Demonstratos#Demonstratos
     */
    public void start() {
	out = new PrintStream(new TextAreaOutputStream(view));
	try {
	    System.setOut(out);
	} catch (SecurityException trial) {}

	if (runnable != null)
	    try {
		Runnable r = (Runnable) Class.forName(runnable).newInstance();
		runnableThread = new Thread(r, "demonstrated");
		runnableThread.start();
	    } catch (ClassNotFoundException x) {
		x.printStackTrace();
	    } catch (ClassCastException x) {
		x.printStackTrace();
	    } catch (IllegalAccessException x) {
		x.printStackTrace();
	    } catch (InstantiationException x) {
		x.printStackTrace();
	    } 
    } 

    public void stop() {
	if (runnable != null) {
	    if (runnableThread != null)
		runnableThread.stop();
	} 
    } 

    /**
     * Info.
     */
    public String getAppletInfo() {
	return "Demonstratos Applet that demonstrates the results and output of Applications. (c) 1998 by Andre Platzer";
    } 

    /**
     * Parameter Info
     */
    public String[][] getParameterInfo() {
	String[][] info = {
	    {
		"rows", "int", "number of rows in which the result will be displayed"
	    }, {
		"cols", "int", "number of columns in which the result will be displayed"
	    }, {
		"runnable", "String", "String-name of Class implementing Runnable that contains the main run() Method"
	    }
	};
	return info;
    } 
}
