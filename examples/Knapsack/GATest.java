import orbital.algorithm.evolutionary.*;
import orbital.algorithm.evolutionary.GeneticAlgorithm.Configuration;
import orbital.logic.functor.Function;

/**
 * Very simple test of a GeneticAlgorithmProblem that simply maximizes numbers
 * with a genetic algorithm.
 * This class has no other use, than to test the genetic algorithms and
 * provide a rather simple example. It is some kind of "Hello World".
 * Performance could be improved, if we would use a class derived from
 * Gene.Integers whose mutation uses a procentual deviation, instead of
 * constant 1.
 */
public class GATest implements GeneticAlgorithmProblem {
    /**
     * An arbitrary value up to which fitness the algorithm should run.
     */
    private static final int GOAL_FITNESS = 1000;
    public static void main(String arg[]) {
        //@xxx should call this application with -Djava.util.logging.config.file=....
        //java.util.logging.Logger.getLogger(GeneticAlgorithm.class.getPackage().getName()).setLevel(java.util.logging.Level.FINEST);
        System.out.println("This demonstrates how simple GeneticAlgorithmProblems are written.");
        System.out.println("The problem is very dull, such that the algorithm runs very long.");
        System.out.println("But it is the most simple application of genetic algorithms.");
        run();
    } 

    public static void run() {
        double maxRecombination = 0.1;
        double maxMutation = 0.3;
        Configuration config =
            new GeneticAlgorithm.Configuration(new GATest(),
                                               Selectors.likelyBetter(),
                                               maxRecombination,
                                               maxMutation,
                                               IncrementalGeneticAlgorithm.class);

        System.out.println("breeding population");
        Object solution = config.solve();
        System.out.println("found solution");
        System.out.println(solution);
    } 


    public Function getEvaluation() {
        return new Function() {
                public Object apply(Object o) {
                    Genome g = (Genome) o;
                    Gene   c = (Gene) g.get(0);
                    return c.get();
                } 
            };
    } 

    public Population getPopulation() {
        System.out.println("creating population");
        int    populationSize = 8;
        Genome g = new Genome();
        g.add(new Gene.Integer(10));
        return Population.create(g, populationSize);
    } 

    public boolean isSolution(Population pop) {
        System.out.println("isSolution?\n" + pop);
        return pop.get(0).getFitness() == GOAL_FITNESS;
    } 

}
