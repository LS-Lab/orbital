import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.functional.Functions;
import orbital.math.Scalar;
import orbital.math.Values;
import orbital.awt.*;
import java.lang.reflect.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Displays a table of all known AlgorithmicTemplate implementations
 * especially GeneralSearch descendants along with their properties like
 * complexity, use of heuristics, completenes, optimality, etc.
 * @version $Id$
 * @author  Andr&eacute; Platzer
 */
public class AlgorithmicTable {
    private static final Logger logger = Logger.global;
    public static final String defaultAlgo[] = {
        "DepthFirstSearch", "BreadthFirstSearch", "IterativeDeepening", "IterativeBroadening", "AStar", "IterativeDeepeningAStar", "IterativeExpansion", "BranchAndBound", "ParallelBranchAndBound",
        "HillClimbing", "SimulatedAnnealing", "ThresholdAccepting",
        "GaussSeidelDynamicProgramming", "RealTimeDynamicProgramming",
        "IncrementalGeneticAlgorithm", "SimpleGeneticAlgorithm", "SteadyStateGeneticAlgorithm", "ConcurrenceGeneticAlgorithm",
        "Backtracking", "Greedy", "DivideAndConquer", "DynamicProgramming",
        // "WAStar" has more complex constructor
    };
    public static void main(String arg[]) throws Exception {
        if (arg.length == 0) {
            System.out.println("No list of algorithm class names specified, using default");
            arg = defaultAlgo;
        } 
        //UIUtilities.setDefaultLookAndFeel();
        show(getAlgorithms(arg));
    } 

    private static final String[] prefixes = {
        "", AlgorithmicTemplate.class.getPackage().getName() + ".", orbital.algorithm.evolutionary.GeneticAlgorithm.class.getPackage().getName() + "."
    };
    protected static AlgorithmicTemplate getAlgorithms(String names[])[] throws IllegalAccessException {
        AlgorithmicTemplate algo[] = new AlgorithmicTemplate[names.length];
        for (int i = 0; i < algo.length; i++) {
            for (int j = 0; algo[i] == null && j < prefixes.length; j++)
                try {
                    algo[i] = instantiate(prefixes[j] + names[i]);
                }
                catch (ClassNotFoundException trial) {logger.log(Level.FINER, "search class", trial);}
                catch (InstantiationException trial) {System.out.println(trial);}
                catch (IllegalAccessException trial) {System.out.println(trial);}
            if (algo[i] == null)
                try {
                    algo[i] = instantiate(prefixes[0] + names[i]);
                }
                catch (ClassNotFoundException trial) {System.out.println(trial);}
                catch (InstantiationException trial) {System.out.println(trial);}
                catch (IllegalAccessException trial) {System.out.println(trial);}
        } 
        return algo;
    } 

    private static final String[] columnNames = {
        "Algorithm", "Search", "Complete", "Correct", "Optimal", "Heuristic", "Probabilistic", "Time", "Space"
    };
    protected static void show(AlgorithmicTemplate algo[]) {
        Object[][] data = new Object[algo.length][columnNames.length];
        for (int i = 0; i < algo.length; i++)
            try {
                int j = 0;
                data[i][j++] = algo[i].getClass().getName().substring(algo[i].getClass().getPackage().getName().length() + 1);
                data[i][j++] = new Boolean(algo[i] instanceof GeneralSearch);
                try {
                    data[i][j++] = new Boolean(algo[i].complexity() != Functions.nondet 

                                               //@xxx: norm is infinite for all polynoms, what else!
                                               &&!(algo[i].complexity().equals(Functions.constant(Values.POSITIVE_INFINITY))));
                }
                catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
                try {
                    data[i][j++] = new Boolean(algo[i] instanceof ProbabilisticAlgorithm ? ((ProbabilisticAlgorithm) algo[i]).isCorrect() : true);
                }
                catch(UnsupportedOperationException x) {logger.log(Level.INFO, "unsupported", x);}
                data[i][j++] = algo[i] instanceof GeneralSearch ? new Boolean(((GeneralSearch) algo[i]).isOptimal()) : null;
                data[i][j++] = new Boolean(algo[i] instanceof HeuristicAlgorithm);
                data[i][j++] = new Boolean(algo[i] instanceof ProbabilisticAlgorithm);
                data[i][j++] = "O(" + (algo[i].complexity() != Functions.nondet ? algo[i].complexity().apply(Values.getDefaultInstance().symbol("n")) : algo[i].complexity()) + ")";
                data[i][j++] = "O(" + (algo[i].spaceComplexity() != Functions.nondet ? algo[i].spaceComplexity().apply(Values.getDefaultInstance().symbol("n")) : algo[i].spaceComplexity()) + ")";
            } catch (Exception ignore) {
                logger.log(Level.FINER, "introspection", ignore);
            } 
        JFrame f = new JFrame("Comparison of Algorithms");
        new Closer(f, true, true);
        JTable table;
        f.getContentPane().add(new JScrollPane(table = new JTable(new ArrayTableModel(columnNames, data))), BorderLayout.CENTER);
        table.setAutoCreateColumnsFromModel(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMinWidth(100);
        table.getColumnModel().getColumn(0).setPreferredWidth(220);
        f.pack();
        f.setVisible(true);
    } 

    private static AlgorithmicTemplate instantiate(String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazz = Class.forName(name);
        try {
            return (AlgorithmicTemplate) clazz.newInstance();
        }
        catch (InstantiationException trial) {logger.log(Level.FINER, "instantiation trial", trial);}
        catch (IllegalAccessException trial) {logger.log(Level.FINER, "instantiation trial", trial);}
        Constructor constructor[] = clazz.getDeclaredConstructors();
        for (int i = 0; i < constructor.length; i++)
            try {
                Class  args[] = constructor[i].getParameterTypes();
                Object dummyArg[] = new Object[args.length];
                for (int j = 0; j < dummyArg.length; j++)
                    dummyArg[j] = args[j].isPrimitive()
                        ? (Object) new Integer(0)
                        : Function.class.isAssignableFrom(args[j])
                        // special case avoid null functions as heuristics
                        ? (Object) Functions.zero
                        : null;
                constructor[i].setAccessible(true);
                return (AlgorithmicTemplate) constructor[i].newInstance(dummyArg);
            }
            catch (IllegalArgumentException trial) {System.out.println(trial + " in " + constructor[i]);}
            catch (InvocationTargetException trial) {System.out.println(trial + ": " + trial.getTargetException() + " in " + constructor[i]);}
            catch (IllegalAccessException trial) {logger.log(Level.FINER, "constructor trial", trial);}

        // if nothing works, then we fail with an ordinary exception
        return (AlgorithmicTemplate) clazz.newInstance();
    } 
}

class ArrayTableModel extends AbstractTableModel implements TableModel {
    private String[]     columnNames;
    protected Object[][] data;
    public ArrayTableModel(String[] columnNames, Object[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }

    public String getColumnName(int i) {
        return columnNames[i];
    } 
    public int getColumnCount() {
        return columnNames.length;
    } 
    public int getRowCount() {
        if (data == null)
            return 1;
        return data.length;
    } 
    public boolean isCellEditable(int row, int col) {
        return false;
    } 
    public Object getValueAt(int row, int col) {
        return data[row][col];
    } 
}
