/**
 * @(#)BreederControl.java 0.9 2000/03/19 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 *
 * This software is the confidential and proprietary information
 * of Andre Platzer. ("Confidential Information"). You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into.
 */

// Title:      Breeder
// Version:
// Copyright:  Copyright (c) 2000
// Author:     André Platzer
// Company:
// Description:front-end for evolutionary algorithms
package orbital.moon.evolutionary;

import orbital.moon.awt.GUITool;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.util.Enumeration;
import java.lang.reflect.*;
import orbital.awt.*;
import orbital.algorithm.evolutionary.*;
import orbital.awt.CustomizerViewController;
import orbital.moon.awt.SystemRequestor;
import orbital.math.Stat;
import orbital.math.MathUtilities;
import orbital.logic.functor.Predicate;
import orbital.logic.functor.Function;
import java.util.logging.Logger;
import java.util.logging.*;
import orbital.io.IOUtilities;
import orbital.util.InnerCheckedException;

import orbital.signe;
import orbital.moon.awt.AboutDialog;

import java.net.URL;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;

import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * front-end for evolutionary algorithms.
 *
 * @stereotype UI
 * @stereotype Tool
 * @version $Id$
 * @author  Andr&eacute; Platzer
 * @todo could distribute BreederControl and the Server running the GeneticAlgorithm, remotely.
 */
public class BreederControl extends JFrame implements Runnable, GUITool {
    private static final long serialVersionUID = -4085070962882209628L;
    /**
     * default values
     * @todo privatize?
     */
    public static final int	 defaultParents = 2;
    public static final int	 defaultChildren = 2;
    public static final double	 defaultMaxRecombination = 0.6;
    public static final double	 defaultMaxMutation = 0.1;

    /**
     * tool-main
     */
    public static void main(String arg[]) throws Exception {
	UIUtilities.setDefaultLookAndFeel();
	BreederControl control;
	if (arg.length > 0 && !(orbital.signe.isHelpRequest(arg[0])))
	    control = new BreederControl((GeneticAlgorithmProblem) Class.forName(arg[0]).newInstance());
	else
	    control = createExample();
	control.init();
	UIUtilities.setCenter(control);
	control.validate();
	control.setVisible(true);
	if (orbital.signe.isHelpRequest(arg)) {
	    control.about();
	    return;
	} 
    } 

    private static BreederControl createExample() {
	BreederControl control = new BreederControl();
	DemoGeneticAlgorithmProblem.control = control;
	control.setGeneticAlgorithmProblem(new DemoGeneticAlgorithmProblem());
	return control;
    }

    private static class DemoGeneticAlgorithmProblem implements GeneticAlgorithmProblem {
	private static BreederControl control;
    	public Population getPopulation() {
	    return PopulationImpl.create(/*new ParallelEvaluationPopulation(), */control.createGenome(), control.getInitialPopulationSize());
	}
        
        public boolean isSolution(Population pop) {
	    return false;
        }
		
	public Function getEvaluation() {
	    return new DemoWeightingFunction();
	}
	private static class DemoWeightingFunction implements Function, Serializable {
	    private static final long serialVersionUID = 4988957966615705107L;
	    private static final long BUSY = 1000L;
	    private static final int DELAY = 100;
	    private java.util.Random r = new java.util.Random();
	    private double			 sum;
	    private double[]		 values = null;
    
	    // a little more stable fitness weighting that has a chance to give a result
	    public Object apply(Object o) {
		Genome g = (Genome) o;
		Gene.BitSet c = (Gene.BitSet) g.get(0);
		if (values == null || values.length != c.length())
		    update(c.length());
		try {Thread.sleep(DELAY);} catch(InterruptedException irq) {}
		for (long l = 0; l < BUSY; l++)
		    doNothing();

		double w = 0;
		for (int i = 0; i < c.length(); i++)
		    w += c.get(i) ? values[i] : 0.;
		return new Double(100 * w / sum);
	    } 
	    protected void doNothing() {}
	    /**
	     * assign new random weighting values for each bit
	     */
	    private void update(int length) {
		values = new double[length];
		sum = 0;
		for (int i = 0; i < values.length; i++) {
		    values[i] = r.nextGaussian();
		    sum += Math.max(values[i], 0);
		} 
	    } 
	}
    }

    public static final String usage = "usage: " + BreederControl.class.getName() + " [gapClassName]" + System.getProperty("line.separator") + "    gapClassName - name of the class\n    that implements the\n    GeneticAlgorithmProblem to solve";
    
    private static final String RESOURCE_BUNDLE_NAME = BreederControl.class.getName();
    /**
     * Program resources.
     */
    protected static final ResourceBundle resources;
    static {
        try {
	    resources = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(null, "An error occured initializing " + BreederControl.class.getName() + ".\nThe package seems corrupt or a resource is missing, aborting\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
	    throw new InnerCheckedException(e);
	} 
    }
    private static final Logger logger = Logger.getLogger(BreederControl.class.getName(), RESOURCE_BUNDLE_NAME);


    // member variables
    protected PopulationTableModel			 data = new PopulationTableModel(null);
    protected final CustomizerViewController custom;
    protected Closer	 closer;
    protected Date	 startTime = null;
    protected Date	 stopTime = null;
    protected int	 startGeneration;

    // view variables
    JMenuBar			 menuBar1 = new JMenuBar();
    JMenu				 menuPopulation = new JMenu();
    JMenu				 menuGenome = new JMenu();
    JMenuItem			 menuHelpAbout = new JMenuItem();
    JLabel				 statusBar = new JLabel();
    BorderLayout		 borderLayout1 = new BorderLayout();
    JMenuItem			 jMenuPopulationNew = new JMenuItem();
    JMenuItem			 jMenuPopulationLoad = new JMenuItem();
    JMenuItem			 jMenuPopulationSave = new JMenuItem();
    JMenuItem			 jMenuPopulationSaveAs = new JMenuItem();
    JMenuItem			 jMenuPopulationCreateAndGo = new JMenuItem();
    JMenuItem			 jMenuPopulationSwitchGAP = new JMenuItem();
    JMenuItem			 jMenuProperties = new JMenuItem();
    JMenu				 menuHelp = new JMenu();
    JMenuItem			 jMenuGenomeNew = new JMenuItem();
    JMenuItem			 jMenuGenomeRemove = new JMenuItem();
    JMenuItem			 jMenuGenomeImport = new JMenuItem();
    JMenuItem			 jMenuGenomeExport = new JMenuItem();
    JMenuItem			 jMenuManipulate = new JMenuItem();
    JMenuItem			 menuFileExit = new JMenuItem();
    JMenu				 jMenuBreed = new JMenu();
    JRadioButtonMenuItem	 jMenuStart = new JRadioButtonMenuItem();
    JRadioButtonMenuItem	 jMenuStop = new JRadioButtonMenuItem();
    JMenuItem				 jMenuStatistics = new JMenuItem();
    JMenuItem				 jMenuBreedReevaluate = new JMenuItem();
    JScrollPane				 jScrollPane1 = new JScrollPane();
    JTable					 jPopulationTable = new JTable(data);
    JPanel					 panel1 = new JPanel();
    FlowLayout				 flowLayout1 = new FlowLayout(FlowLayout.LEFT);
    JLabel					 jLabel1 = new JLabel();
    JTextField				 tGeneration = new JTextField();
    JMenu					 jMenuOptions = new JMenu();
    JMenu					 jMenuOptionsSelector = new JMenu();
    ButtonGroup				 jItemSelectors = new ButtonGroup();
    JRadioButtonMenuItem	 jRadioButtonMenuItemSelector[];
    JMenu					 jMenuOptionsAlgorithmType = new JMenu();
    ButtonGroup				 jItemAlgorithmTypes = new ButtonGroup();
    JRadioButtonMenuItem	 jRadioButtonMenuItemAlgorithmType[];


    // Construct the frame
    /**
     * Construct a BreederControl frame for solving the given problem.
     */
    public BreederControl(GeneticAlgorithmProblem problem) {
	this();
	this.problem = problem;
    }	
    /**
     * Construct a BreederControl frame.
     */
    public BreederControl() {
	//prior to initializing any other handlers
	new SystemRequestor(new Predicate() {
		public boolean apply(Object e) {
		    int i = ((Number) e).intValue();
		    if (i == SystemRequestor.INTERRUPT)
			stop();
		    else if (i == SystemRequestor.ABORT)
			quit();
		    return true;
		}
	    }, this);
	custom = new CustomizerViewController(this);
	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	closer = new Closer(this, this, true, false);
	closer.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
		    quit();
        	}
	    });
	this.protocol = Logger.getLogger(getClass().getName(), RESOURCE_BUNDLE_NAME);
	// remove all default handlers
	Handler defaultHandlers[] = protocol.getHandlers();
	for (int i = 0; i < defaultHandlers.length; i++)
	    protocol.removeHandler(defaultHandlers[i]);
	//@todo should we add a MemoryHandler that can later flush data into a newly created protocol?
    }
    
    /**
     * Current population file processed.
     * @serial
     * @see #ga
     */
    protected File						 file;
    
    /**
     * Protocol written for current population.
     * @serial
     * @see #ga
     * @see #protocolHeader(GeneticAlgorithm)
     * @see #protocolHandler
     */
    protected Logger						 protocol;
    /**
     * Protocol handler for current population.
     * @serial
     * @see #protocol
     */
    private Handler						 protocolHandler = null;

    /**
     * The current genetic algorithm problem to solve.
     * @serial
     */
    protected GeneticAlgorithmProblem		 problem;

    /**
     * The current genetic algorithm.
     * @serial
     */
    protected GeneticAlgorithm				 ga;

    /**
     * Get program resources.
     */
    protected ResourceBundle getResources() {
	return resources;
    }

    /**
     * Get the current genetic algorithm problem to solve.
     * @see #problem
     */
    protected final GeneticAlgorithmProblem getGeneticAlgorithmProblem() {
	return problem;
    }
    protected final void setGeneticAlgorithmProblem(GeneticAlgorithmProblem gap) {
	this.problem = gap;
    }
    /**
     * Get the current genetic algorithm used for solving.
     * @see #ga
     */
    protected final GeneticAlgorithm getGeneticAlgorithm() {
	return ga;
    }
	
    // virtual behaviour methods that can be overwritten by subclasses

    /**
     * Create a problem-specific genome prototype.
     * Used for mutation etc.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    protected Genome createGenome() {
	JOptionPane.showMessageDialog(this, resources.getString("message.genome.newRandom.warning.text"), resources.getString("message.genome.newRandom.warning.title"), JOptionPane.WARNING_MESSAGE);
	//@internal almost identical to return (Genome) Selectors.uniform().apply(getGeneticAlgorithmProblem().getPopulation());
	Population temppop = getGeneticAlgorithmProblem().getPopulation();
	return temppop.get(getGeneticAlgorithm().getRandom().nextInt(temppop.size()));
    }

    protected GeneticAlgorithm create() throws InstantiationException {
	String type = getSelectedAlgorithmType();
	//@TODO could use .newInstance() and .set... instead
	if (type == null)
	    throw new InstantiationException("because no genetic algorithm type was selected");
	GeneticAlgorithm ga;
	try {
	    Class algorithmClass = Class.forName(type);
	    if (type.equals(SteadyStateGeneticAlgorithm.class.getName()))
		ga = new SteadyStateGeneticAlgorithm(/*defaultParents, defaultChildren, defaultMaxRecombination, defaultMaxMutation,*/ getReplacements());
	    else
		ga = (GeneticAlgorithm) algorithmClass.getConstructor(new Class[] {/*Integer.TYPE, Integer.TYPE, Double.TYPE, Double.TYPE*/}).newInstance(new Object[] {/*new Integer(defaultParents), new Integer(defaultChildren), new Double(defaultMaxRecombination), new Double(defaultMaxMutation)*/});
	}
	catch (ClassNotFoundException x) {throw new InstantiationException(x.toString());}
	catch (NoSuchMethodException x) {throw new InstantiationException(x.toString());}
	catch (IllegalAccessException x) {throw new InstantiationException(x.toString());}
	catch (InvocationTargetException x) {throw new InstantiationException(x.toString() + ": " + x.getTargetException());}
	ga.setEvaluation(problem.getEvaluation());
	ga.setPopulation(problem.getPopulation());
	PopulationImpl pop = (PopulationImpl) ga.getPopulation();
	pop.setParentCount(defaultParents);
	pop.setChildrenCount(defaultChildren);
	pop.setMaximumRecombination(defaultMaxRecombination);
	pop.setMaximumMutation(defaultMaxMutation);
	protocol(resources.getString("protocol.population.create") + ga + "\r\n" + ga.getPopulation());
	return ga;
    }

    /**
     * Load population and genetic algorithm.
     * <p>
     * <b>Note:</b> Remember that class names are relative to this package for serialization.
     * If you desire your own package to be default, simply overwrite this method with the body<pre>
     * super.load(is);
     * </pre>to circumvent confusion with different default packages.</p>
     */
    protected GeneticAlgorithm load(InputStream is) throws IOException, ClassNotFoundException {
	return (GeneticAlgorithm) ((ObjectInput) is).readObject();
    }

    /**
     * Save population and genetic algorithm.
     */
    protected void save(GeneticAlgorithm ga, OutputStream os) throws IOException {
	((ObjectOutput) os).writeObject(ga);
    }

    protected void properties(GeneticAlgorithm ga) {
	custom.showCustomizer(ga);
	protocol(resources.getString("protocol.population.changedProperties") + ga);
    }

    /**
     * Load genome to import.
     * @return the genome to import, or <code>null</code> if it should not be inserted.
     *  (either because it's invalid, or because it has already been inserted in a custom way).
     */
    protected Genome importGenome(InputStream is) throws IOException, ClassNotFoundException {
	return (Genome) ((ObjectInput) is).readObject();
    }

    /**
     * Save genome to export.
     */
    protected void exportGenome(Genome genome, OutputStream os) throws IOException {
	((ObjectOutput) os).writeObject(genome);
    }

    protected void manipulate(Genome c) {
	custom.showCustomizer(c);
	protocol(resources.getString("protocol.genome.manipulated") + c);
    }

    /**
     * Get an input stream for a file.
     * <p>
     * Overwrite to change the stream that data is read from.</p>
     */
    protected InputStream getInputStream(File file) throws FileNotFoundException, IOException {
	return new ObjectInputStream(new FileInputStream(file));
    }

    /**
     * Get an output stream for a file.
     * <p>
     * Overwrite to change the stream that data is written to.</p>
     */
    protected OutputStream getOutputStream(File file) throws FileNotFoundException, IOException {
	return new ObjectOutputStream(new FileOutputStream(file));
    }

    protected void statistics(GeneticAlgorithm ga) {
      	StringBuffer buf = new StringBuffer();
      	buf.append(Stat.statistics(ga.getPopulation().getFitnessArray()) + "\r\noverall distance: " + ga.getPopulation().getOverallDistance());
      	if (startTime != null) {
	    long duration = (stopTime!=null ? stopTime : new Date()).getTime() - startTime.getTime();
	    double seconds = duration / 1000.0;
	    buf.append("\r\nAverage Performance:\r\ngenerations per second: " + MathUtilities.format((double) (ga.getPopulation().getGeneration() - startGeneration) / seconds) + "\t seconds per generation: " + MathUtilities.format((double) seconds / (ga.getPopulation().getGeneration() - startGeneration)));
      	}
      	JTextArea msg = new JTextArea(buf.toString());
      	msg.setEditable(false);
      	JOptionPane.showMessageDialog(this, msg, resources.getString("dialog.statistics.title"), JOptionPane.INFORMATION_MESSAGE);
    }
	
	
    // book keeping
    /**
     * Protocolize a text.
     * @todo remove and individually use the logger protocol
     * @internal see #protocol
     * @internal see #protocolHeader(GeneticAlgorithm)
     */
    protected void protocol(String text) {
	protocol.log(Level.INFO, protocolHeader(ga) + text);
    }

    /**
     * Get the protocol header used for the given GeneticAlgorithm.
     * @todo move into the Logger and its Formatters and stuff. However, do we still know the state of the ga and population once we really get called? For sure, cloning them just for this purpose is too expensive
     *  Perhaps we'd have to modify the Handler in order to keep remember this information instantly
     * @see #protocol(String)
     */
    private String protocolHeader(GeneticAlgorithm ga) {
	NumberFormat header = NumberFormat.getInstance();
	((DecimalFormat) header).applyPattern("000: ");
	return (ga != null && ga.getPopulation() != null ? header.format(ga.getPopulation().getGeneration()) : "???: ")
	    + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date()) + ' ';
    }


    private final void newProtocol(File file) {
	if (protocolHandler != null) {
	    protocol.removeHandler(protocolHandler);
	    protocolHandler.close();
	    this.protocolHandler = null;
	}
	try {
	    this.protocolHandler = new FileHandler(IOUtilities.changeExtension(file, "protocol").toString(), true);
	    protocolHandler.setFormatter(new SimpleFormatter());
	    protocol.addHandler(protocolHandler);
	}
	catch (IOException trial) {this.protocolHandler = null;}
    }

    /**
     * Store history file sequence.
     */
    protected void history() {
	if (this.file == null)
	    return;
	File historyFile;
	if (!(historyFile = IOUtilities.changeExtension(this.file, ga.getPopulation().getGeneration() + ".breed")).exists())
	    try {
		assert !historyFile.exists() : "history files do not overwrite existing";
		OutputStream os = getOutputStream(historyFile);
		save(ga, os);
		os.close();
	    } catch (IOException x) {
		logger.log(Level.INFO, "protocol.history.error", x);
	    }
    }


    private volatile Thread breeding = null;
    /**
     * @todo concurrent synchronize is this thread safe?
     */
    public void start() {
	// start only when ready after having stopped
	if (breeding != null)
	    return;
	if (ga == null || ga.getSelection() == null) {
	    jMenuStop_actionPerformed(null);
	    JOptionPane.showMessageDialog(this, resources.getString("message.run.invalidGeneticAlgorithm") + (ga != null && ga.getSelection() == null ? resources.getString("message.run.invalidSelection") : "Genetic Algorithm " + ga + " is invalid"), resources.getString("message.run.invalid.title"), JOptionPane.ERROR_MESSAGE);
	    return;
	}
	if (breeding != null)
	    return;
	breeding = new Thread(this, "breeding");
	breeding.start();
	statusBar.setText(resources.getString("statusbar.breed.start"));
	protocol(resources.getString("protocol.breed.start"));
    }

    public void stop() {
	breeding = null;
	// nice stop please, without loss of data!  breeding.stop();
	protocol(resources.getString("protocol.breed.stop"));
    }

    /**
     * Runnable-start entry point.
     */
    public void run() {
	try {
	    statusBar.setText(resources.getString("statusbar.breed.running"));
	    Thread thisThread = breeding;
	    stopTime = null;
	    startTime = new Date();
	    startGeneration = ga.getPopulation().getGeneration();
	    while (thisThread == breeding && !problem.isSolution(ga.getPopulation())) {
		evolve();
		Thread.currentThread().yield();
	    }
	    stopTime = new Date();
	    ready();
	    if (startTime != null) {
		long duration = (stopTime != null ? stopTime : new Date()).getTime() - startTime.getTime();
		double seconds = duration / 1000.0;
		protocol(resources.getString("protocol.breed.stopped") + "\tgenerations per second: " + MathUtilities.format((double) (ga.getPopulation().getGeneration() - startGeneration) / seconds) + "\t seconds per generation: " + MathUtilities.format((double) seconds / (ga.getPopulation().getGeneration() - startGeneration)));
	    } else
		protocol(resources.getString("protocol.breed.stopped"));
    	}
    	catch (Throwable e) {
	    logger.log(Level.SEVERE, "protocol.error", e);
	    String eClass = e.getClass().getName().substring(e.getClass().getPackage().getName().length() + 1);
	    statusBar.setText(resources.getString("statusbar.error") + eClass + ':' + e.getLocalizedMessage());
	    protocol(resources.getString("protocol.error") + e.getLocalizedMessage());
    	}	
    	finally {
	    jMenuStop.setSelected(true);
	    breeding = null;
    	}
    }

    /**
     * Called when the breeder control is ready again.
     */
    protected void ready() {
	statusBar.setText(resources.getString("statusbar.ready"));
	jMenuStop.setSelected(true);
	URL url = getClass().getResource(resources.getString("statusbar.ready.sound"));
	try {
	    // try playing direct audio sound
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            AudioFormat		 format = stream.getFormat();
            DataLine.Info	 info = new DataLine.Info(Clip.class, stream.getFormat(),
							  ((int) stream.getFrameLength() * format.getFrameSize()));
    
            Clip			 clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception alternative) {
	    try {
		// try playing midi sound then
            	Sequence  sequence = MidiSystem.getSequence(url);
            	Sequencer sequencer = MidiSystem.getSequencer();
            	sequencer.open();
    
            	sequencer.setSequence(sequence);
            	sequencer.start();
            }
            catch (Exception ignore) {logger.log(Level.INFO, "BreederControl.ready", alternative + "\r\n" + ignore);}
        }
    }

    /**
     * Get the desired initial population size from the user.
     */
    protected int getInitialPopulationSize() throws NumberFormatException {
	String populationSize = JOptionPane.showInputDialog(this, resources.getString("message.population.getInitialPopulationSize"), resources.getString("message.population.create"), JOptionPane.QUESTION_MESSAGE);
	if (populationSize != null)
	    try {
		return Integer.parseInt(populationSize);
	    }
	    catch(NumberFormatException x) {JOptionPane.showMessageDialog(this, resources.getString("message.population.illegalSize") + x, resources.getString("illegalNumber"), JOptionPane.ERROR_MESSAGE);throw x;}
	throw new NumberFormatException("Illegal number: '" + populationSize + "'");
    }
    private int getReplacements() throws NumberFormatException {
	String replacements = JOptionPane.showInputDialog(this, resources.getString("message.population.getReplacements"), resources.getString("message.population.create"), JOptionPane.QUESTION_MESSAGE);
	if (replacements != null)
	    try {
		return Integer.parseInt(replacements);
	    }
	    catch(NumberFormatException x) {JOptionPane.showMessageDialog(this, resources.getString("message.population.illegalReplacements") + x, resources.getString("illegalNumber"), JOptionPane.ERROR_MESSAGE);throw x;}
	throw new NumberFormatException("Illegal number: '" + replacements + "'");
    }

    public void init() {
	// already done in constructor and jbInit().
	try {
	    jbInit();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void destroy() {
	removeAll();
    }
    
    /**
     * Evolves and updates view.
     */
    protected void evolve() {
	ga.evolve();
	if (ga.getPopulation() instanceof ParallelEvaluationPopulation)
	    ga.getPopulation().evaluate(false);
	tGeneration.setText("" + ga.getPopulation().getGeneration());
	data.fireTableDataChanged();
	ListSelectionModel selection = jPopulationTable.getSelectionModel();
	int[] selected = SelectionStatistics.selectionStatistics.getSelected();
	selection.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	selection.setSelectionInterval(selected[0], selected[0]);
	for (int i = 1; i < selected.length; i++)
	    selection.addSelectionInterval(selected[i], selected[i]);
	selection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	history();
    }

    /**
     * Enable menu options etc. depending upon the population's presence.
     * @param e true to enable some menu options etc. because a population is present.
     *  false to disable some menu options because no population is present.
     */
    protected void popEnable(boolean e) {
	jMenuPopulationSave.setEnabled(e);
	jMenuPopulationSaveAs.setEnabled(e);
	jMenuProperties.setEnabled(e);
	jMenuBreed.setEnabled(e);
	menuGenome.setEnabled(e);
    }

    /**
     * Get the selector selected.
     */
    private String getSelectedSelector() {
	for (Enumeration i = jItemSelectors.getElements(); i.hasMoreElements(); ) {
	    AbstractButton b = (AbstractButton) i.nextElement();
	    if (b.isSelected())
		return b.getActionCommand();
	}
	return null;
    }
    /**
     * Set the selected selector.
     * @param selectorDesc either the toString() description of the selector, as given in Selectors return types
     *  or the method name in class Selectors called to get the selector.
     *  Class name is of no use, since that is an anonymous inner class.
     */
    private void setSelectedSelector(String selectorDesc) {
	for (Enumeration i = jItemSelectors.getElements(); i.hasMoreElements(); ) {
	    AbstractButton b = (AbstractButton) i.nextElement();
	    if (selectorDesc.equals(b.getActionCommand()) || selectorDesc.equals(b.getText())) {
		b.setSelected(true);
		return;
	    }
	}
    }

    /**
     * Get the algorithm type selected.
     * @return the class name of the algorithm type selected.
     */
    protected String getSelectedAlgorithmType() {
	for (Enumeration i = jItemAlgorithmTypes.getElements(); i.hasMoreElements(); ) {
	    AbstractButton b = (AbstractButton) i.nextElement();
	    if (b.isSelected())
		return b.getActionCommand();
	}
	return null;
    }
    /**
     * Set the selected algorithm type used.
     * @param selectorDesc either the fully qualified class name, or the class name.
     */
    private void setSelectedAlgorithmType(String selectorDesc) {
	for (Enumeration i = jItemAlgorithmTypes.getElements(); i.hasMoreElements(); ) {
	    AbstractButton b = (AbstractButton) i.nextElement();
	    if (selectorDesc.equals(b.getActionCommand()) || selectorDesc.equals(b.getText())) {
		b.setSelected(true);
		return;
	    }
	}
    }

    /**
     * Set the genetic algorithm used an update the graphical view.
     */
    private void setGeneticAlgorithm(GeneticAlgorithm ga) {
	this.ga = ga;
	if (ga.getPopulation() != null) {
	    setSelectedAlgorithmType(ga.getClass().getName());
	    data.setPopulation(ga.getPopulation());
	    tGeneration.setText("" + ga.getPopulation().getGeneration());
	    popEnable(true);
	    if (ga.getSelection() != null)
		setSelectedSelector(ga.getSelection().toString());
	} else
	    popEnable(false);
    }

    // Component initialization
    private void jbInit() throws Exception {
	this.getContentPane().setLayout(borderLayout1);
	this.setSize(new Dimension(400, 300));
	this.setTitle(resources.getString("application.title"));
	menuPopulation.setMnemonic('P');
	menuPopulation.setText("Population");
	menuGenome.setMnemonic('G');
	menuGenome.setText("Genome");
	menuHelpAbout.setText("About");
	menuHelpAbout.addActionListener(new ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    about();
		}
	    });
	jMenuPopulationNew.setMnemonic('N');
	jMenuPopulationNew.setText("Create new");
	jMenuPopulationNew.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(final ActionEvent e) {
		    BreederControl.this.file = null;
		    if (BreederControl.this.protocolHandler != null) {
			BreederControl.this.protocolHandler.close();
			BreederControl.this.protocolHandler = null;
		    }
		    new Thread(new Runnable() {
			    public void run() {
				try {
				    jMenuPopulationNew_actionPerformed(e);
                		}
                		catch (InstantiationException x) {
				    logger.log(Level.WARNING, "message.population.create.error.title", x);
				    JOptionPane.showMessageDialog(BreederControl.this, resources.getString("message.population.create.error.text") + x, resources.getString("message.population.create.error.title"), JOptionPane.ERROR_MESSAGE);
                		}
			    }
			}).start();
		}
	    });
	jMenuPopulationLoad.setMnemonic('O');
	jMenuPopulationLoad.setText("Load...");
	jMenuPopulationLoad.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuPopulationLoad_actionPerformed(e);
		}
	    });
	jMenuPopulationSave.setMnemonic('S');
	jMenuPopulationSave.setText("Save");
	jMenuPopulationSave.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuPopulationSave_actionPerformed(e);
		}
	    });
	jMenuPopulationSaveAs.setMnemonic('A');
	jMenuPopulationSaveAs.setText("Save As...");
	jMenuPopulationSaveAs.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuPopulationSaveAs_actionPerformed(e);
		}
	    });
	jMenuProperties.setText("Properties...");
	jMenuProperties.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuProperties_actionPerformed(e);
		}
	    });
	jMenuPopulationCreateAndGo.setText("Create & Go...");
	jMenuPopulationCreateAndGo.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(final ActionEvent e) {
		    new Thread(new Runnable() {
			    public void run() {
				jMenuPopulationCreateAndGo_actionPerformed(e);
			    }
			}).start();
		}
	    });
	jMenuPopulationSwitchGAP.setText("Switch Problem...");
	jMenuPopulationSwitchGAP.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(final ActionEvent e) {
		    new Thread(new Runnable() {
			    public void run() {
				jMenuPopulationSwitchGAP_actionPerformed(e);
			    }
			}).start();
		}
	    });
	menuHelp.setMnemonic('?');
	menuHelp.setText("?");
	jMenuGenomeNew.setText("New random");
	jMenuGenomeNew.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuGenomeNew_actionPerformed(e);
		}
	    });
	jMenuGenomeRemove.setText("Remove");
	jMenuGenomeRemove.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuGenomeRemove_actionPerformed(e);
		}
	    });
	jMenuGenomeImport.setMnemonic('I');
	jMenuGenomeImport.setText("Import...");
	jMenuGenomeImport.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuGenomeImport_actionPerformed(e);
		}
	    });
	jMenuGenomeExport.setMnemonic('E');
	jMenuGenomeExport.setText("Export...");
	jMenuGenomeExport.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuGenomeExport_actionPerformed(e);
		}
	    });
	jMenuManipulate.setText("Manipulate...");
	jMenuManipulate.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuManipulate_actionPerformed(e);
		}
	    });
	menuFileExit.setMnemonic('X');
	menuFileExit.setText("Exit");
	menuFileExit.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    fileExit_actionPerformed(e);
		}
	    });
	jMenuBreed.setMnemonic('B');
	jMenuBreed.setText("Breed");
	jMenuStart.setMnemonic('R');
	jMenuStart.setText("Start");
	jMenuStart.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuStart_actionPerformed(e);
		}
	    });
	jMenuStop.setSelected(true);
	jMenuStop.setMnemonic('P');
	jMenuStop.setText("Stop");
	jMenuStop.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuStop_actionPerformed(e);
		}
	    });
	tGeneration.setColumns(4);
	jMenuStatistics.setText("Statistics...");
	jMenuStatistics.addActionListener(new java.awt.event.ActionListener() {

		public void actionPerformed(ActionEvent e) {
		    jMenuStatistics_actionPerformed(e);
		}
	    });
	jMenuBreedReevaluate.setText("Re-evaluate");
	jMenuBreedReevaluate.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    statusBar.setText(resources.getString("statusbar.breed.re_evaluate"));
		    ga.getPopulation().evaluate(true);
		    ready();
		}
	    });
	jMenuOptions.setText("Options");
	jMenuOptions.setMnemonic('O');
	jMenuOptionsSelector.setText("Selector");
	jMenuOptionsSelector.setMnemonic('S');
	Method selectors[] = Selectors.class.getDeclaredMethods();
	jRadioButtonMenuItemSelector = new JRadioButtonMenuItem[selectors.length];
	for (int i = 0; i < selectors.length; i++) {
	    jRadioButtonMenuItemSelector[i] = new JRadioButtonMenuItem();
	    jRadioButtonMenuItemSelector[i].setText(selectors[i].invoke(null, null).toString());
	    jRadioButtonMenuItemSelector[i].setActionCommand(selectors[i].getName());
	    jRadioButtonMenuItemSelector[i].addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			jRadioButtonMenuItemSelector_actionPerformed(e);
		    }
		});
    	    jMenuOptionsSelector.add(jRadioButtonMenuItemSelector[i]);
    	    jItemSelectors.add(jRadioButtonMenuItemSelector[i]);
    	}

	jMenuOptionsAlgorithmType.setText("Algorithm type");
	jMenuOptionsAlgorithmType.setMnemonic('A');
	int algorithmCount = Integer.parseInt(resources.getString("algorithm-count"));
	jRadioButtonMenuItemAlgorithmType = new JRadioButtonMenuItem[algorithmCount];
	for (int i = 0; i < algorithmCount; i++) {
	    Class algorithmType = Class.forName(resources.getString("algorithm-" + i + ".class"));
	    jRadioButtonMenuItemAlgorithmType[i] = new JRadioButtonMenuItem();
	    jRadioButtonMenuItemAlgorithmType[i].setText(resources.getString("algorithm-" + i + ".name"));
	    jRadioButtonMenuItemAlgorithmType[i].setActionCommand(algorithmType.getName());
	    jRadioButtonMenuItemAlgorithmType[i].setToolTipText(resources.getString("algorithm-" + i + ".description"));
	    jRadioButtonMenuItemAlgorithmType[i].addActionListener(new java.awt.event.ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (BreederControl.this.ga != null)
			    JOptionPane.showMessageDialog(BreederControl.this, "The request to change the type of algorithm will\nnot affect the current genetic algorithm used.\nCreate a new population to change the type.", "Does not affect current algorithm", JOptionPane.INFORMATION_MESSAGE);;
		    }
		});
    	    jMenuOptionsAlgorithmType.add(jRadioButtonMenuItemAlgorithmType[i]);
    	    jItemAlgorithmTypes.add(jRadioButtonMenuItemAlgorithmType[i]);
    	    if (i == 0)
    	    	jMenuOptionsAlgorithmType.setSelected(true);
    	}

    	menuBar1.add(menuPopulation);
	menuBar1.add(menuGenome);
    	menuBar1.add(jMenuOptions);
	menuBar1.add(jMenuBreed);
        menuBar1.add(Box.createHorizontalGlue());
	//menuBar1.setHelpMenu(menuHelp);
	menuBar1.add(menuHelp);
	this.setJMenuBar(menuBar1);
	this.getContentPane().add(panel1, BorderLayout.NORTH);
	this.getContentPane().add(jScrollPane1, BorderLayout.CENTER);
	this.getContentPane().add(createStatusBar(), BorderLayout.SOUTH);
	jPopulationTable.setColumnSelectionAllowed(false);
	jPopulationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	jScrollPane1.getViewport().add(jPopulationTable, null);
	panel1.setLayout(flowLayout1);
	jLabel1.setText("Generation:");
	tGeneration.setEditable(false);
	panel1.add(jLabel1, null);
	panel1.add(tGeneration, null);
	menuPopulation.add(jMenuPopulationNew);
	menuPopulation.add(jMenuPopulationLoad);
	menuPopulation.add(jMenuPopulationSave);
	menuPopulation.add(jMenuPopulationSaveAs);
	menuPopulation.addSeparator();
	menuPopulation.add(jMenuPopulationSwitchGAP);
	menuPopulation.add(jMenuPopulationCreateAndGo);
	menuPopulation.addSeparator();
	menuPopulation.add(jMenuProperties);
	menuPopulation.addSeparator();
	menuPopulation.add(menuFileExit);
	menuHelp.add(menuHelpAbout);
	menuGenome.add(jMenuGenomeNew);
	menuGenome.add(jMenuGenomeRemove);
	menuGenome.add(jMenuGenomeImport);
	menuGenome.add(jMenuGenomeExport);
	menuGenome.addSeparator();
	menuGenome.add(jMenuManipulate);
	ButtonGroup gr = new ButtonGroup();
	jMenuBreed.add(jMenuStart);
	gr.add(jMenuStart);
	jMenuBreed.add(jMenuStop);
	gr.add(jMenuStop);
	jMenuBreed.addSeparator();
	jMenuBreed.add(jMenuStatistics);
	jMenuBreed.addSeparator();
	jMenuBreed.add(jMenuBreedReevaluate);
	jMenuOptions.add(jMenuOptionsAlgorithmType);
	jMenuOptions.addSeparator();
	jMenuOptions.add(jMenuOptionsSelector);
	jMenuOptions.addSeparator();
	UIUtilities.addLookAndFeelMenuItems(getRootPane(), jMenuOptions);
	popEnable(false);
	statusBar.setText(resources.getString("statusbar.initialized"));
    }
    
    /**
     * Create a status bar to display.
     * @see <a href="{@docRoot}/Patterns/Design/FactoryMethod.html">Factory Method</a>
     */
    protected Component createStatusBar() {
	statusBar.setText("");
    	return statusBar;
    }
    
    // File | Exit action performed
    public void fileExit_actionPerformed(ActionEvent e) {
	closer.actionPerformed(e);
    }

    /**
     * Performs clean up actions just before program quits.
     */
    private void quit() {
	if (protocolHandler != null)
	    protocolHandler.close();
	System.exit(0);
    }

    // Help | About action performed
    public void about() {
	String nl = System.getProperty("line.separator");
	AboutDialog.showAboutDialog(this, resources.getString("dialog.about.text") + nl + usage + nl + nl + nl + resources.getString("application.title") + " and " + signe.getManifest(), resources.getString("dialog.about.title") + " " + resources.getString("application.title"));
    }

    void jMenuPopulationNew_actionPerformed(ActionEvent e) throws InstantiationException {
	statusBar.setText(resources.getString("statusbar.population.create"));
	this.ga = create();
	if (ga.getPopulation() instanceof ParallelEvaluationPopulation)
	    ga.getPopulation().evaluate(false);
	try {
	    String selector = getSelectedSelector();
	    if (ga != null && selector != null)
		ga.setSelection((Function) Selectors.class.getMethod(selector, null).invoke(null, null));
	}
	catch (Exception x) {JOptionPane.showMessageDialog(this, resources.getString("message.selector.illegal.text") + x, resources.getString("message.selector.illegal.title"), JOptionPane.ERROR_MESSAGE);}
	setGeneticAlgorithm(ga);
	history();
	ready();
    }

    void jMenuPopulationSwitchGAP_actionPerformed(ActionEvent e) {
	String problem
	    = JOptionPane.showInputDialog(this, resources.getString("dialog.problem.switch.text"), resources.getString("dialog.problem.switch.title"), JOptionPane.PLAIN_MESSAGE);
	if (problem == null)
	    return;
	try {
	    setGeneticAlgorithmProblem((GeneticAlgorithmProblem) Class.forName(problem).newInstance());
	    JOptionPane.showMessageDialog(this, resources.getString("dialog.problem.switch.success.text"), resources.getString("dialog.problem.switch.success.title"), JOptionPane.INFORMATION_MESSAGE);
	}
	catch (ClassNotFoundException x) {
	    logger.log(Level.WARNING, "message.problem.switch.error.ClassNotFoundException.title", x);
	    JOptionPane.showMessageDialog(this, resources.getString("message.problem.switch.error.ClassNotFoundException.text") + x, resources.getString("message.problem.switch.error.ClassNotFoundException.title"), JOptionPane.ERROR_MESSAGE);
	    return;
	}
	catch (InstantiationException x) {
	    logger.log(Level.WARNING, "message.problem.switch.error.InstantiationException.title", x);
	    JOptionPane.showMessageDialog(this, resources.getString("message.problem.switch.error.InstantiationException.text") + x, resources.getString("message.problem.switch.error.InstantiationException.title"), JOptionPane.ERROR_MESSAGE);
	    return;
	}
	catch (IllegalAccessException x) {
	    logger.log(Level.WARNING, "message.problem.switch.error.IllegalAccessException.title", x);
	    JOptionPane.showMessageDialog(this, resources.getString("message.problem.switch.error.IllegalAccessException.text") + x, resources.getString("message.problem.switch.error.IllegalAccessException.title"), JOptionPane.ERROR_MESSAGE);
	    return;
	}
    }

    /**
     * Shortcut to Create, Save, Start.
     */
    void jMenuPopulationCreateAndGo_actionPerformed(ActionEvent e) {
	FileDialog dlg = new FileDialog(this, resources.getString("dialog.history.store.title"), FileDialog.SAVE);
	dlg.setVisible(true);
	String f = dlg.getFile();
	if (f == null)
	    return;
	this.file = new File(dlg.getDirectory(), f);
	newProtocol(file);
	try {
	    jMenuPopulationNew_actionPerformed(e);
	}
	catch (InstantiationException x) {
	    logger.log(Level.WARNING, "message.population.create.error.title", x);
	    JOptionPane.showMessageDialog(this, resources.getString("message.population.create.error.text") + x, resources.getString("message.population.create.error.title"), JOptionPane.ERROR_MESSAGE);
	    return;
	}
	jMenuPopulationSave_actionPerformed(e);
	jMenuStart_actionPerformed(e);
    }

    void jMenuPopulationSaveAs_actionPerformed(ActionEvent e) {
	FileDialog dlg = new FileDialog(this, resources.getString("dialog.population.save.title"), FileDialog.SAVE);
	dlg.setVisible(true);
	String f = dlg.getFile();
	if (f == null)
	    return;
	this.file = new File(dlg.getDirectory(), f);
	newProtocol(file);
	// Save to chosen file
	jMenuPopulationSave_actionPerformed(e);
    }

    void jMenuPopulationSave_actionPerformed(ActionEvent e) {
	if (this.file == null) {
	    // Save As instead
	    jMenuPopulationSaveAs_actionPerformed(e);
	    return;
	}
	try {
	    OutputStream os = getOutputStream(file);
	    save(ga, os);
	    os.close();
	    statusBar.setText(resources.getString("statusbar.population.save"));
	} catch (IOException x) {
	    logger.log(Level.WARNING, "message.population.save.error.title", x);
	    JOptionPane.showMessageDialog(this, resources.getString("message.population.save.error.text") + " '" + file + "'\n" + x, resources.getString("message.population.save.error.title"), JOptionPane.ERROR_MESSAGE);
	}
    }

    void jMenuPopulationLoad_actionPerformed(ActionEvent e) {
	FileDialog dlg = new FileDialog(this, resources.getString("dialog.population.load.title"), FileDialog.LOAD);
	dlg.setVisible(true);
	String f = dlg.getFile();
	if (f == null)
	    return;
	this.file = new File(dlg.getDirectory(), f);
	if (file.exists())
	    try {
		InputStream is = getInputStream(file);
		this.ga = load(is);
		is.close();
		if (protocolHandler != null) {
		    protocol.removeHandler(protocolHandler);
		    protocolHandler.close();
		    this.protocolHandler = null;
		}
		if (ga == null)
		    throw new NullPointerException("loading '" + file + "' failed. Perhaps, an old version of JSX did not find the package?");
		newProtocol(file);
		setGeneticAlgorithm(ga);
		statusBar.setText(resources.getString("statusbar.population.load"));
	    } catch (Exception x) {
		logger.log(Level.WARNING, "message.population.load.error.title", x);
		JOptionPane.showMessageDialog(this, resources.getString("message.population.load.error.title") + " '" + file + "'\n" + x, resources.getString("message.population.load.error.title"), JOptionPane.ERROR_MESSAGE);
	    }
	else
	    JOptionPane.showMessageDialog(this, resources.getString("message.population.load.error.noSuchFile.text") + "'" + file + "'", resources.getString("message.population.load.error.noSuchFile.title"), JOptionPane.ERROR_MESSAGE);
    }

    void jMenuProperties_actionPerformed(ActionEvent e) {
	properties(ga);
	data.fireTableDataChanged();
    }

    void jMenuGenomeImport_actionPerformed(ActionEvent e) {
	FileDialog dlg = new FileDialog(this, resources.getString("dialog.genome.load.title"), FileDialog.LOAD);
	dlg.setVisible(true);
	String f = dlg.getFile();
	if (f == null)
	    return;
	File file = new File(dlg.getDirectory(), f);
	if (file.exists())
	    try {
		InputStream is = getInputStream(file);
		Genome genome = importGenome(is);
		if (genome != null)
		    ga.getPopulation().add(genome);
		is.close();
		protocol(resources.getString("protocol.genome.import") + file);
		data.fireTableDataChanged();
	    } catch (Exception x) {
		logger.log(Level.WARNING, "message.genome.load.error.title", x);
		JOptionPane.showMessageDialog(this, resources.getString("message.genome.load.error.text") + "'" + file + "'\n" + x, resources.getString("message.genome.load.error.title"), JOptionPane.ERROR_MESSAGE);
	    }
	else
	    JOptionPane.showMessageDialog(this, resources.getString("message.genome.load.error.noSuchFile.text") + "'" + file + "'", resources.getString("message.genome.load.error.noSuchFile.title"), JOptionPane.ERROR_MESSAGE);
    }

    void jMenuGenomeExport_actionPerformed(ActionEvent e) {
	int i = jPopulationTable.getSelectedRow();
	if (i < 0)
	    return;
	FileDialog dlg = new FileDialog(this, resources.getString("dialog.genome.save.title"), FileDialog.SAVE);
	dlg.setVisible(true);
	String f = dlg.getFile();
	if (f == null)
	    return;
	File file = new File(dlg.getDirectory(), f);
	try {
	    OutputStream os = getOutputStream(file);
	    exportGenome(ga.getPopulation().get(i), os);
	    os.close();
	} catch (IOException x) {
	    logger.log(Level.WARNING, "message.genome.save.error.title", x);
	    JOptionPane.showMessageDialog(this, resources.getString("message.genome.save.error.text") + " '" + file + "'\n" + x, resources.getString("message.genome.save.error.title"), JOptionPane.ERROR_MESSAGE);
	}
    }

    void jMenuGenomeRemove_actionPerformed(ActionEvent e) {
	int i = jPopulationTable.getSelectedRow();
	if (i < 0)
	    return;
	ga.getPopulation().remove(i);
	protocol(resources.getString("protocol.genome.remove") + i);
	data.fireTableDataChanged();
    }

    void jMenuGenomeNew_actionPerformed(ActionEvent e) {
	Object genome = createGenome().mutate(((PopulationImpl) ga.getPopulation()).getMaximumMutation());
	ga.getPopulation().add(genome);
	protocol(resources.getString("protocol.genome.new") + genome);
	data.fireTableDataChanged();
    }

    void jMenuManipulate_actionPerformed(ActionEvent e) {
	int i = jPopulationTable.getSelectedRow();
	if (i < 0)
	    return;
	manipulate(ga.getPopulation().get(i));
	data.fireTableCellUpdated(i, 2);
	data.fireTableCellUpdated(i, 3);
    }

    void jMenuStatistics_actionPerformed(ActionEvent e) {
	statistics(ga);
    }

    void jRadioButtonMenuItemSelector_actionPerformed(ActionEvent e) {
	try {
	    if (ga != null)
		ga.setSelection((Function) Selectors.class.getMethod(e.getActionCommand(), null).invoke(null, null));
	}
	catch (Exception x) {JOptionPane.showMessageDialog(this, resources.getString("message.selector.illegal.text") + x, resources.getString("message.selector.illegal.title"), JOptionPane.ERROR_MESSAGE);}
    }

    void jMenuStart_actionPerformed(ActionEvent e) {
	// select in case user didn't, but we
	jMenuStart.setSelected(true);
	start();
    }

    void jMenuStop_actionPerformed(ActionEvent e) {
	// select in case user didn't, but we
	jMenuStop.setSelected(true);
	stop();
	statusBar.setText(resources.getString("statusbar.breed.stop"));
    }
}

class PopulationTableModel extends AbstractTableModel implements TableModel {
    private static String[] columnNames = {
	"No", "Fitness", "Genome"
    };
    protected Population	population;
    public PopulationTableModel(Population pop) {
	this.population = pop;
    }
    public PopulationTableModel() {
	this(null);
    }

    public void setPopulation(Population pop) {
	this.population = pop;
	fireTableDataChanged();
    }
    public String getColumnName(int i) {
	return columnNames[i];
    }
    public int getColumnCount() {
	return columnNames.length;
    }
    public int getRowCount() {
	if (population == null)
	    return 0;
	return population.size();
    }
    public boolean isCellEditable(int row, int col) {
	return false;
    }
    public Object getValueAt(int row, int col) {
	switch (col) {
	case 0:
	    return new Integer(row);
	case 1:
	    return row < population.size() ? new Double(population.get(row).getFitness()) : null;
	case 2:
	    return population.get(row).get().toString();
	default:
	    throw new IllegalArgumentException("invalid column");
	}
    }
}
