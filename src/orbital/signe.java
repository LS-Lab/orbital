/**
 * @(#)signe.java 1.0 1999/01/10 Andre Platzer
 * 
 * Copyright (c) 1996-2002 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital;

import java.applet.Applet;

import java.awt.Label;
import java.awt.TextArea;

/**
 * signe showing Applet and Application or Thread-Runnable.
 * 
 * @version 1.0, 2000/09/29
 * @author Andr&eacute; Platzer
 * @exclude javadoc
 * @see java.lang.Package
 */
public final class signe extends Applet implements Runnable {
    private static final long serialVersionUID = 7383629572654119544L;
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	System.err.println("signe");
	new signe().run();
	if (arg.length > 0 && ("-??".equals(arg[0]) || "--version".equals(arg[0])))
	    System.out.println(signe.getManifest());
	else
	    System.err.println(signe.getHelpAboutHelp());
    } 

    /**
     * Checks whether the given argument is an command-line request for help.
     * <p>
     * Such requests may include command-line arguments like <code>-?</code>, or
     * <code>--help</code> etc.
     * </p>
     */
    public static final boolean isHelpRequest(String arg) {
	return "-?".equals(arg) || "--help".equals(arg) || "-h".equals(arg) || "/?".equals(arg);
    }
    /**
     * Checks whether the given list of arguments contains a command-line request for help.
     * @see #isHelpRequest(String)
     */
    public static final boolean isHelpRequest(String arg[]) {
	for (int i = 0; i < arg.length; i++)
	    if (isHelpRequest(arg[i]))
		return true;
	return false;
    }
    /**
     * Get an argument that works as a command-line request for help.
     */
    public static final String getHelpRequest() {
	return "--help";
    }
    /**
     * Get help about (getting) help.
     */
    public static final String getHelpAboutHelp() {
	return "use " + getHelpRequest() + " for help";
    }

    /**
     * Runnable-init entry point.
     */
    public signe() {}

    /**
     * Applet-init entry point.
     */
    public void init() {
	add("North", new Label("signe"));		   // @version 1.0
	add("Center", new TextArea(getInfo() + ", " + getVersion()));	   // @version 1.0
    } 

    /**
     * Applet-start entry point.
     */
    public void start() {}

    /**
     * Runnable-start entry point.
     */
    public void run() {
	System.out.println(getInfo() + ", " + getVersion());
    } 


    /**
     * Contains the internal id number information.
     */
    private static final int	ID = 0xBabe;

    /**
     * Contains the id information.
     */
    private static final String id = "orbital";

    /**
     * Contains the signe information.
     */
    private static final String info = "The Orbital library";

    /**
     * Contains the creator information.
     */
    private static final String creator = "Andre Platzer";

    /**
     * Contains the created information.
     */
    private static final String created = "(c) 1996-2002";

    /**
     * Contains the version information in Dewey Decimal syntax.
     * Thus it consists of positive decimal integers separated by periods ".".
     */
    private static String		version = "@VERSION@";

    /**
     * Contains the default name for the <em>Orbital library service</em>.
     */
    public static final String	DefaultService = "Orbital";

    /**
     * Get the id information.
     * @see #id
     */
    public static String getId() {
	return id;
    } 

    /**
     * Get the signe information.
     * @see #info
     */
    public static String getInfo() {
	return info;
    } 

    /**
     * Get the creator information.
     * @see #creator
     */
    public static String getCreator() {
	return creator;
    } 

    /**
     * Get the created information.
     * @see #created
     */
    public static String getCreated() {
	return created;
    } 

    /**
     * Get the version information.
     * @see #version
     */
    public static String getVersion() {
	return version;
    } 

    /**
     * Get the version number information.
     * Returns a double with the dot separation of all minor version numbers stripped of.
     * @see #version
     * @return <var>Majorversion</var>.<var>MinorversionMicroversionNanoversionPicoVersion...</var> as a double.
     */
    public static final double getVersionNumber() {
	assert version.indexOf('-') < 0 : "positive";
	int majorpos = version.indexOf('.');
	if (majorpos < 0 || version.indexOf('.', majorpos + 1) < 0)	   // simple major.minor format
	    return Double.parseDouble(version);
	assert version.indexOf('.', version.indexOf('.')) > 0 : "two dots in number";
	StringBuffer release = new StringBuffer(version.substring(0, majorpos) + ".");
	for (int i = majorpos; i + 1 < version.length(); ) {
	    int n = version.indexOf('.', i + 1);
	    if (n < 0)
		n = version.length();
	    release.append(version.substring(i + 1, n));
	    i = n;
	} 
	return Double.parseDouble(release.toString());
    } 

    /**
     * Manifest.
     */
    public static String getManifest() {
	return getInfo() + ", " + getVersion() + " " + getCreated() + " by " + getCreator();
    } 

    /**
     * Applet-Info.
     */
    public String getAppletInfo() {
	return "signe Applet that displays the signe: " + getManifest();
    } 

    /**
     * Return a string representation of the object.
     */
    public String toString() {
	return getClass().getName() + "[" + getInfo() + "]";
    } 
}
