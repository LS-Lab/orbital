/**
 * @(#)setup.java 0.9 2000/06/24 Andre Platzer
 * 
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

package orbital.moon;

import orbital.awt.WizardView;
import orbital.moon.awt.GUITool;
import orbital.logic.functor.Predicate;

import java.awt.*;
import orbital.awt.UIUtilities;
import orbital.awt.Closer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JOptionPane;

import orbital.io.IOUtilities;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.io.IOException;
import java.lang.reflect.*;

import orbital.signe;

/**
 * Runnable application setup program.
 * 
 * @stereotype &laquo;Tool&raquo;
 * @version 0.8,, 2000/06/24
 * @author  Andr&eacute; Platzer
 */
public class setup extends WizardView implements Runnable, GUITool {
    /**
     * Application-start entry point.
     */
    public static void main(String arg[]) throws Exception {
	setup f = new setup();
	f.init();
	f.start();
    } 

    private final ResourceBundle resources;
    private String		   libraryPath;
    		
    		

    private Checkbox	   acceptLicense;
    private Checkbox	   currentInstallation[];
    private Checkbox	   conflictingInstallation[];
    private TextComponent  currentVersion;
    private Checkbox	   doInstallFiles;
    private TextComponent  installPath;
    private Checkbox	   installSecurity;
    private Checkbox	   setDefaultLogLevel;
    private Choice		   defaultLogLevel;
    private Checkbox	   setDefaultReporter;
    private TextComponent  defaultReporter;
    private Checkbox	   backupFiles;

    private static Component[] constructSteps() {
	Component[] r = new Component[6];
	TextArea	t;
	Panel		p = new Panel(new BorderLayout());
	p.add(new Label(signe.getInfo() + " setup wizard", Label.CENTER));
	r[0] = p;
		
	p = new Panel(new BorderLayout());
	p.add(new Label("You must accept the License Agreement to proceed!"), BorderLayout.NORTH);
	r[1] = p;
		
	GridBagConstraints nl = new GridBagConstraints();
	nl.gridwidth = GridBagConstraints.REMAINDER;
	nl.anchor = GridBagConstraints.NORTHWEST;

	p = new Panel(new GridBagLayout());
	p.add(new Label("Checking current installation status"), nl);
	p.add(new Label("And old installation of " + signe.getInfo() + " ..."), nl);
	r[2] = p;
		
	p = new Panel(new GridBagLayout());
	p.add(new Label("Choose options for installation"), nl);
	r[3] = p;
		
	p = new Panel(new BorderLayout());
	p.add(t = new TextArea("The configuration you chose will now be written to the disk. Press the Next button to proceed storing the configuration, or press Back to change some options.", 5, 30, TextArea.SCROLLBARS_VERTICAL_ONLY), BorderLayout.CENTER);
	t.setEditable(false);
	r[4] = p;
		
	p = new Panel(new BorderLayout());
	p.add(t = new TextArea("Congratulations! Setup wizard has finished configuration and installation. The installation is now complete, and " + signe.getInfo() + " ready for use. Press the Finish button to end setup wizard, or press Back to change some options.", 5, 30, TextArea.SCROLLBARS_VERTICAL_ONLY), BorderLayout.CENTER);
	t.setEditable(false);
	r[5] = p;
	return r;
    } 

    /**
     * Runnable-init entry point.
     */
    public setup() throws IOException {
	super(new Frame(), "Setup Wizard", constructSteps(), null);
	this.libraryPath = System.getProperty("java.ext.dirs");
	try {
	    try {
		resources = new PropertyResourceBundle(getClass().getResourceAsStream("/orbital/resources/setup.properties"));
	    }
	    catch (IOException x) {throw x;}
	    catch (Exception x) {throw new IOException(x.toString());}
	    try {
		URL icon = getClass().getResource('/' + resources.getString("main.icon"));
		if (icon != null)
		    if (getParent() instanceof Frame)
			((Frame) getParent()).setIconImage(getToolkit().getImage(icon));
	    }
	    catch (Exception ignore) {}
    		
	    // read license
	    Container step = ((Container) getSteps()[1]);
	    TextArea t;
	    step.add(t = new TextArea(readFully(getClass().getResource("/orbital/resources/license.txt")), 10, 60, TextArea.SCROLLBARS_VERTICAL_ONLY));
	    t.setEditable(false);
	    Panel p = new Panel(new FlowLayout(FlowLayout.CENTER));
	    p.add(acceptLicense = new Checkbox("I do accept the License Agreement", false));
	    ((Container) getSteps()[1]).add(p, BorderLayout.SOUTH);
    		
	    GridBagConstraints c = new GridBagConstraints();
	    c.anchor = GridBagConstraints.NORTHWEST;
	    GridBagConstraints nl = new GridBagConstraints();
	    nl.gridwidth = GridBagConstraints.REMAINDER;
	    nl.anchor = GridBagConstraints.NORTHWEST;

	    // check current installation status
	    step = ((Container) getSteps()[2]);
	    CheckboxGroup currentInstallationGroup = new CheckboxGroup();
	    currentInstallation = new Checkbox[2];
	    step.add(currentInstallation[0] = new Checkbox(" ... is not installed", false, currentInstallationGroup), nl);
	    step.add(currentInstallation[1] = new Checkbox(" ... is installed", false, currentInstallationGroup), nl);
	    currentInstallation[0].setEnabled(false);
	    currentInstallation[1].setEnabled(false);
	    CheckboxGroup conflictingInstallationGroup = new CheckboxGroup();
	    conflictingInstallation = new Checkbox[2];
	    step.add(conflictingInstallation[0] = new Checkbox(" ... will not lead to conflicts", false, conflictingInstallationGroup), nl);
	    step.add(conflictingInstallation[1] = new Checkbox(" ... could possibly lead to conflicts", false, conflictingInstallationGroup), nl);
	    conflictingInstallation[0].setEnabled(false);
	    conflictingInstallation[1].setEnabled(false);
	    /*step.add(new Label("Current version installed: "), c);
	      step.add(currentVersion = new TextField("", 6), nl);
	      currentVersion.setEditable(false);*/
	    step.add(t = new TextArea("If you have installed an old version of " + signe.getInfo() + " that would could possibly lead to conflicts, you should remove its library files from the class-path and any lib/ext directories, first. Note that there is no absolute requirement to remove old files, but it usually safes trouble with the class lookup policy of the Java Virtual Machine.", 3, 60, TextArea.SCROLLBARS_VERTICAL_ONLY), nl);
	    t.setEditable(false);

	    // provide options
	    step = ((Container) getSteps()[3]);
	    step.add(doInstallFiles = new Checkbox("Install library files to", libraryPath != null), c);
	    step.add(installPath = new TextField(libraryPath, 22), nl);
	    doInstallFiles.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
			installPath.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		    } 
    		});
	    step.add(installSecurity = new Checkbox("Configure as a security provider", false), nl);
	    installSecurity.setEnabled(false);
	    step.add(setDefaultLogLevel = new Checkbox("Default log level", true), c);
	    step.add(defaultLogLevel = new Choice(), nl);
	    try {
		Class  adjointClass = Class.forName("orbital.Adjoint");
		int[]  allLogLevels = (int[]) adjointClass.getMethod("getAllLogLevels", null).invoke(null, null);
		Method toString = adjointClass.getMethod("toString", new Class[] {Integer.TYPE});
		for (int i = 0; i < allLogLevels.length; i++)
		    defaultLogLevel.add((String) toString.invoke(null, new Object[] {new Integer(allLogLevels[i])}));
		defaultLogLevel.select((String) toString.invoke(null, new Object[] {adjointClass.getField("defaultLogLevel").get(null)}));
	    }
	    catch (Exception ignore) {}
	    catch (Error ignore) {}
	    setDefaultLogLevel.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
			defaultLogLevel.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		    } 
    		});
	    step.add(setDefaultReporter = new Checkbox("Default assertion reporter", true), c);
	    step.add(defaultReporter = new TextField("", 22), nl);
	    try {
		Field currentReporter = Class.forName("orbital.SP").getField("reporter");
		defaultReporter.setText(currentReporter.get(null) != null ? currentReporter.getType().getName() : "");
	    }
	    catch (Exception ignore) {}
	    catch (Error ignore) {}
	    setDefaultReporter.addItemListener(new ItemListener() {
		    public void itemStateChanged(ItemEvent e) {
			defaultReporter.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		    } 
    		});
	    step.add(backupFiles = new Checkbox("Backup files that are changed", true), nl);

	    // set action steps
	    setActionSteps(new Predicate[] {
		null,
		new Predicate() {
		    public boolean apply(Object e) {
			if (((String) e).startsWith("next") && !acceptLicense.getState()) {
			    JOptionPane.showMessageDialog(setup.this, "You cannot continue setup without accepting the License Agreement.", "No License Agreement", JOptionPane.WARNING_MESSAGE);
			    return false;
			} else {
			    try {
				priorInstallationCheck();			// necessary for the next step
			    }
			    catch (IOException ex) {
				ex.printStackTrace();
			    }
			    return true;
			}
		    } 
		},
		null,
		new Predicate() {
		    public boolean apply(Object e) {
			if (doInstallFiles.getState()) {
			    String path = installPath.getText();
			    if (path == null || path.length() == 0) {
				JOptionPane.showMessageDialog(setup.this, "You want to install the library files but have not specified the destination path of the extension libraries to copy to.", "No Destination Path", JOptionPane.ERROR_MESSAGE);
				return false;
			    }
			    File f = new File(path);
			    if (f.isDirectory() && f.exists())
				return true;
			    if (!f.isFile())
				if (JOptionPane.showConfirmDialog(setup.this, "The directory '" + path + "' does not exist. Create it?", "Create Destination Path", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				    f.mkdirs();
				else
				    return false;
			    return f.isDirectory() && f.exists();
			}
			return true;
		    } 
		},
		new Predicate() {
		    public boolean apply(Object e) {
			try {
			    run();
			    return true;
			} catch (RuntimeException x) {
			    JOptionPane.showMessageDialog(setup.this, "An error occured while setup is not complete, aborting\n" + x, "Error", JOptionPane.ERROR_MESSAGE);
			    return false;
			}
		    } 
		},
		new Predicate() {
		    public boolean apply(Object e) {
			setVisible(false);
			System.exit(0);
			return true;
		    } 
		}
	    });
    		
	    Closer closer = new Closer((Frame) getOwner(), "Abort setup and cancel configuration?", this, true, true);
	    setSize(450, 300);
	} catch (IOException e) {
	    JOptionPane.showMessageDialog(this, "An error occured initializing setup wizard. The package seems corrupt or a resource is missing, aborting\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
	    throw e;
	} 
	UIUtilities.setCenter(this);
    }

    public void init() {
	//@todo do things here, rather than in constructor
    }

    public void start() {
	setVisible(true);
    }

    public void stop() {
	setVisible(false);
    }

    public void destroy() {
	//@todo resources = null;
    }


    /**
     * Runnable-start entry point.
     */
    public void run() {
	try {
	    install();
	} catch (IOException e) {
	    throw new RuntimeException(e.toString());
	} 
    } 

    private String readFully(URL u) throws IOException {
	if (u == null)
	    throw new InternalError("Invalid package, file resource missing");
	return IOUtilities.readFully(new InputStreamReader(u.openStream()));
    } 
	
    private void priorInstallationCheck() throws IOException {
	try {
	    preinstalledCheck();
	}
	catch (Exception ignore) {
	    ignore.printStackTrace();
	}
	try {
	    uninstallCheck();
	}
	catch (Exception ignore) {
	    ignore.printStackTrace();
	}
	// this check does not say a thing as long as we use orbital.* stuff in the installer
	/*try {
	  classAvailableCheck();
	  }
	  catch (Exception ignore) {
	  ignore.printStackTrace();
	  }*/
    }
    private void preinstalledCheck() throws IOException {
	// check for preinstalled version
	int count = Integer.parseInt(resources.getString("preinstalled.library.count"));
	//TODO: can we use resources.getStringArray(...) instead?
	for (int i = 0; i < count; i++) {
	    String file = resources.getString("preinstalled.library.file." + i);
	    if (new File(libraryPath + File.separator + file).exists()) {
		currentInstallation[1].setState(true);
		return;
	    }
	}
	currentInstallation[0].setState(true);
    }
    private void uninstallCheck() throws IOException {
	// check for conflicting preinstalled version that should be uninstalled
	int count = Integer.parseInt(resources.getString("uninstall.library.count"));
	//TODO: can we use resources.getStringArray(...) instead?
	for (int i = 0; i < count; i++) {
	    String file = resources.getString("uninstall.library.file." + i);
	    if (new File(libraryPath + File.separator + file).exists()) {
		conflictingInstallation[1].setState(true);
		return;
	    }
	}
	conflictingInstallation[0].setState(true);
    }
    private void classAvailableCheck() throws IOException {
	// check for preinstalled version
	final String preinstalled = resources.getString("preinstalled.class");
	if (preinstalled == null)
	    return;
	try {
	    Class preinstallationCheckClass = Class.forName(preinstalled);
	    currentVersion.setText("<available>");
	    String versionQueryMethod = resources.getString("preinstalled.class.version.query");
	    if (versionQueryMethod == null)
		return;
	    currentVersion.setText("<querying>");
	    currentVersion.setText(preinstallationCheckClass.getMethod(versionQueryMethod, new Class[0]).invoke(null, null) + "");
	}
	catch (ClassNotFoundException notfound) {
	    currentVersion.setText("<none>");
	}
	catch (NoSuchMethodException ex) {
	    ex.printStackTrace();
	}
	catch (IllegalAccessException ex) {
	    ex.printStackTrace();
	}
	catch (IllegalArgumentException ex) {
	    ex.printStackTrace();
	}
	catch (InvocationTargetException ex) {
	    ex.printStackTrace();
	}
	catch (Exception trial) {}
	catch (Error trial) {}
    }

    private void install() throws IOException {
	if (doInstallFiles.getState()) {
	    int count = Integer.parseInt(resources.getString("install.library.count"));
	    //TODO: can we use resources.getStringArray(...) instead?
	    for (int i = 0; i < count; i++) {
		String file = resources.getString("install.library.file." + i);
		copy(file, installPath.getText() + File.separator + file);
		//copy(getClass().getProtectionDomain().getCodeSource().getLocation() file, installPath.getText() + File.separator + file);
	    }
	} 
	if (installSecurity.getState()) {
	    String file = System.getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "java.security";
	    if (backupFiles.getState()) {
		File bak = new File(file + ".bak");
		if (!bak.exists())
		    copy(file, bak);
	    } 
	    Properties security = new Properties();
	    security.load(new FileInputStream(file));

	    boolean changed = addToPropertyList(security, "security.provider.", 1, "orbital.moon.io.cryptix.provider.Orbital");
	    if (changed) {
		OutputStream os = new FileOutputStream(file);
		security.store(os, " This is the \"master security properties file\".");
		os.close();
	    } 
	} 
	if (setDefaultLogLevel.getState() || setDefaultReporter.getState()) {
	    String file = System.getProperty("user.home") + File.separator + ".java" + File.separator + "properties";
	    if (backupFiles.getState()) {
		File bak = new File(file + ".bak");
		if (!bak.exists())
		    copy(file, bak);
	    } 

	    Properties user = new Properties();
	    user.load(new FileInputStream(file));

	    boolean changed = false;
	    if (setDefaultLogLevel.getState())
		changed |= addToProperties(user, "orbital.Adjoint.loglevel", defaultLogLevel.getSelectedItem());
	    if (setDefaultReporter.getState())
		changed |= addToProperties(user, "orbital.SP.reporter", defaultReporter.getText());
	    if (changed) {
		OutputStream os = new FileOutputStream(file);
		user.store(os, " Java(TM) Plug-In-Properties");
		os.close();
	    } 
	} 
    } 

    /**
     * Copy a file from source to destination.
     */
    private void copy(File source, File destination) throws IOException {
	OutputStream os = new FileOutputStream(destination);
	IOUtilities.copy(new FileInputStream(source), os);
	os.close();
    }
    private void copy(File source, String destination) throws IOException {
	copy(source, new File(destination));
    }
    private void copy(String source, File destination) throws IOException {
	copy(new File(source), destination);
    }
    private void copy(String source, String destination) throws IOException {
	copy(new File(source), new File(destination));
    }

    /**
     * Add a property value into a list of properties with a given keyPrefix.
     * @return whether a modification was necessary or the value was already present.
     */
    private boolean addToPropertyList(Properties properties, String keyPrefix, int startIndex, String value) {
	String t;
	int	   count = startIndex;
	while ((t = properties.getProperty(keyPrefix + count)) != null)
	    if (value.equals(t))	// déjà là
		return false;
	    else
		count++;
	properties.setProperty(keyPrefix + count, value);
	return true;
    } 
    private boolean addToProperties(Properties properties, String key, String value) {
	String old = properties.getProperty(key);
	if ((value == null && old == null) || (value != null && value.equals(old)))
	    return false;	 		// déjà là
	properties.setProperty(key, value);
	return true;
    } 
}
