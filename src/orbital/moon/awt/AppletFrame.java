/**
 * @(#)AppletFrame.java 0.9 1996/03/01 Andre Platzer
 *
 * Copyright (c) 1996 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.awt;

import java.awt.Frame;
import java.awt.Event;
import java.awt.Dimension;
import java.applet.Applet;

import java.applet.AppletStub;
import java.applet.AppletContext;
import java.net.URL;
import java.awt.Image;
import java.applet.AudioClip;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.InputStream;

import java.io.IOException;
import java.net.MalformedURLException;
import java.awt.Toolkit;
import java.util.Vector;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;

import orbital.awt.UIUtilities;
import orbital.util.InnerCheckedException;

/**
 * A Frame that can be started to run applets as an application.
 * Used to convert an applet to an application.
 * <p>
 * To start an applet as an application use a main method like:
 * <pre>
 * <span class="keyword">public</span> <span class="keyword">static</span> <span class="keyword">void</span> main(<span class="Class">String</span> args[]) {
 *     <span class="Orbital">AppletFrame</span>.showApplet(<span class="String">"<i>className</i>"</span>, <span class="String">"<i>Application Title</i>"</span>, args);
 * }
 * </pre></p>
 * @version 1.0
 * @version 0.9, 1996/03/01
 * @author  Andr&eacute; Platzer
 */
public class AppletFrame extends Frame {
    // Applet to Application view Frame window
   
    /**
     * When orbital.AppletFrame is called as an Application, then the arguments are:
     * <div><kbd>java orbital.AppletFrame <i>className</i> <i>Title</i> (<i>parameter</i>=<i>value</i>)*</kbd></div>
     */
    public static void main(String args[]) throws Exception {
	if (args.length < 1 || orbital.signe.isHelpRequest(args[0])) {
	    System.out.println(usage);
	    return;
	} 
	if (args.length == 2 && orbital.signe.isHelpRequest(args[1])) {
	    System.out.println(usage);
	    // fall-through and let showApplet display info
	} 

	UIUtilities.setDefaultLookAndFeel();
	String className = args[0];
	String title = args.length > 1 ? args[1] : "Application Title";
	System.out.println("starting Applet " + className + " '" + title + "'");
	// strip consumed arguments <className> and <Title>, and pass the remaining args to the applet
	int consumedArguments = args.length > 1 ? 2 : 1;
	String remainingArgs[] = new String[args.length - consumedArguments];
	System.arraycopy(args, consumedArguments, remainingArgs, 0, remainingArgs.length);
	AppletFrame.showApplet(className, title, remainingArgs);
    } 
    public static final String usage = "usage: " + AppletFrame.class + " <className> [<Title> (<parameter>=<value>)* | " + orbital.signe.getHelpRequest() + "]" + System.getProperty("line.separator") + "\twill display the applet <className> in a new frame called <Title>." + System.getProperty("line.separator") + "\tThe applet has access to the values assigned to the parameters." + System.getProperty("line.separator") + "\t" + orbital.signe.getHelpRequest() + "\tdisplay available parameter info for applet <className>";

    /**
     * Get information on an applet.
     */
    public static String info(Applet a) {
	final String nl = System.getProperty("line.separator");
	StringBuffer sb = new StringBuffer("applet ");
	sb.append(a.getClass().getName());
	sb.append(nl);
	sb.append("supports the following parameters:");
	sb.append(nl);
	String[][] params = a.getParameterInfo();
	for (int i = 0; i < params.length; i++) {
	    for (int j = 0; j < params[i].length; j++)
		sb.append((j > 0 ? " -- " : "") + params[i][j]);
	    sb.append(nl);
	}
	return sb.toString();
    }

    public static void showApplet(String appletClassName, String title) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	showApplet(appletClassName, title, null);
    } 

    /**
     * Show an instance of the applet with the given class.
     * @param appletClassName the fully qualified class name of the applet to instantiate.
     * @param title the title to display.
     * @param args arguments passed to the applet.
     *  args is a list of arguments of the form <span class="String">"<i>parameter</i>=<i>value</i>"</span>.
     * @see #usage
     * @see #showApplet(Applet, String, String[])
     */
    public static void showApplet(String appletClassName, String title, String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
	// create an instance of the applet class
	Applet a = (Applet) Class.forName(appletClassName).newInstance();
	showApplet(a, title, args);
    } 

    /**
     * Show an Applet in a new AppletFrame.
     * Can be called from an Application-Applet's method main to provide
     * comfortable application-like behaviour.
     */
    public static void showApplet(Applet a, String title) {
	showApplet(a, title, new String[0]);
    }
    /**
     * Show an Applet in a new AppletFrame.
     * Can be called from an Application-Applet's method main to provide
     * comfortable application-like behaviour.
     * @param a the applet to display in a frame.
     * @param title the title to display.
     * @param args arguments passed to the applet.
     *  args is a list of arguments of the form <span class="String">"<i>parameter</i>=<i>value</i>"</span>.
     */
    public static void showApplet(Applet a, String title, String args[]) {
	if (args.length == 1 && orbital.signe.isHelpRequest(args[0])) {
	    System.out.println(info(a));
	} 

	// create new application frame window
	AppletFrame frame = new AppletFrame(title);
	a.setStub(new StandaloneAppletStub(a, frame, args));

	// add applet to frame window
	frame.add("Center", a);	   // @version 1.0

	// initialize the applet
	a.init();
	a.start();

	// resize frame window to fit applet
	// assumes that the applet has its preferred size set
	frame.pack();

	// show the window
	frame.show();
    } 

    // constructor needed to pass window title to class Frame
    public AppletFrame(String name) {
	super(name);
    }

    /**
     * needed to allow window to close in Java 1.0 style.
     * @see orbital.awt.Closer
     * @xxx for Rhythmomachia and Seti, x-ing sometimes does not work.
     */
    public boolean handleEvent(Event e) {
	// Window Destroy event
	if (e.id == Event.WINDOW_DESTROY) {
	    System.exit(0);
	    return true;
	} 
	return super.handleEvent(e);
    } 
}

/**
 * The AppletStub of an applet run as standalone Application.
 */
class StandaloneAppletStub implements AppletStub {
    private Applet applet;
    private Frame frame;
    private String args[];
    public StandaloneAppletStub(Applet applet, Frame frame, String args[]) {
	this.applet = applet;
	this.frame = frame;
	this.args = args;
    }

    public boolean isActive() {
    	return true;
    }
    public URL getDocumentBase() {
    	return getCodeBase();
    }
    public URL getCodeBase() {
    	try {
	    //@xxx or applet.getClass().getResource(".");
	    return new URL("file:///" + System.getProperty("user.dir") + "/");
    	}
    	catch(MalformedURLException e) {throw new InnerCheckedException("no codebase", e);}
    }
    public String getParameter(String name) {
    	if (args == null)
	    return null;
    	for (int i = 0; i < args.length; i++) {
	    int tok = args[i].indexOf('=');
	    if (tok < 0)
		continue;
	    if (args[i].substring(0, tok).equalsIgnoreCase(name))
		return args[i].substring(tok + 1);
    	}
    	return null;
    }
    public AppletContext getAppletContext() {
    	return new StandaloneAppletContext(applet);
    }
    public void appletResize(int width, int height) {
    	frame.resize(width, height);
    }
}

/**
 * The AppletContext of an applet run as standalone Application.
 */
class StandaloneAppletContext implements AppletContext {
    private Toolkit tk;
    private Applet applet;
    public StandaloneAppletContext(Applet applet) {
	this.applet = applet;
	this.tk = applet.getToolkit();
    }
    public InputStream getStream(String key) {throw new UnsupportedOperationException("new to JDK1.4");}
    public Iterator getStreamKeys() {throw new UnsupportedOperationException("new to JDK1.4");}
    public void setStream(String key, InputStream stream) {throw new UnsupportedOperationException("new to JDK1.4");}
    public AudioClip getAudioClip(URL url) {
	return new AudioSystemAudioClip(url);
    }
    public Image getImage(URL url) {
    	return tk.getImage(url);
    }
    public Applet getApplet(String name) {
    	return null;
    }
    public Enumeration getApplets() {
    	Vector v = new Vector(1);
    	v.addElement(applet);
    	return v.elements();
    }
    public void showDocument(URL url) {
        String cmd = System.getProperty("os.command");
        String os = System.getProperty("os.name");
        if (cmd == null)
	    if (os != null) 
            	if (os.startsWith("Windows NT") || os.startsWith("Windows 20"))
		    cmd = "cmd /C start ";
            	else if (os.startsWith("Windows 9") || os.startsWith("Windows ME"))
		    cmd = "command.com /C start ";
            	else if (os.startsWith("Linux") || os.startsWith("Unix"))
		    cmd = "/bin/sh ${BROWSER} ";
    	    	else
		    cmd = "";
	    else
		cmd = "";
        cmd += url;
        try {
	    Process help = Runtime.getRuntime().exec(cmd);
        }
        catch(IOException x) {}
    }
    public void showDocument(URL url, String target) {
    	showDocument(url);
    }
    public void showStatus(String status) {
    	System.out.println(status);
    }
}

class AudioSystemAudioClip implements AudioClip {
    private URL 	  url;
    private Clip	  clip = null;
    private Sequencer sequencer = null;
    public AudioSystemAudioClip(URL url) {
	this.url = url;
    }

    protected void init(URL url) throws Exception {
	try {
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            AudioFormat		 format = stream.getFormat();
            DataLine.Info	 info = new DataLine.Info(Clip.class, stream.getFormat(),
							  ((int) stream.getFrameLength() * format.getFrameSize()));
    
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
        }
        catch (Exception alternative) {
	    Sequence sequence = MidiSystem.getSequence(url);
	    sequencer = MidiSystem.getSequencer();
	    sequencer.open();

	    sequencer.setSequence(sequence);
        }
    }

    private boolean opened() {
        if (clip == null && sequencer == null)
            try {
            	init(url);
            }
            catch (Exception ignore) {}
        return clip != null || sequencer != null;
    }

    public void play() {
        if (opened())
	    if (clip != null)
		clip.start();
	    else
		sequencer.start();
    }

    public void loop() {
        if (opened())
	    if (clip != null)
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	    else
		throw new UnsupportedOperationException("midi does not support loop");
    }

    public void stop() {
        if (opened())
	    if (clip != null)
		clip.stop();
	    else
		sequencer.stop();
    }
	
    protected void finalize() throws Throwable {
	if (clip != null)
	    clip.close();
	if (sequencer != null)
	    sequencer.close();
    }
}
