import orbital.algorithm.evolutionary.*;
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
public class GATest implements Runnable, GeneticAlgorithmProblem {
    public static void main(String arg[]) {
	//@fixme should call this application with -Djava.util.logging.config.file=....
	//java.util.logging.Logger.getLogger(GeneticAlgorithm.class.getPackage().getName()).setLevel(java.util.logging.Level.FINEST);
	new GATest().run();
    } 


    protected GeneticAlgorithm ga;

    public Function getWeighting() {
	return new Function() {
		public Object apply(Object o) {
		    Genome g = (Genome) o;
		    Gene   c = (Gene) g.get(0);
		    return c.get();
		} 
	    };
    } 

    public Population getPopulation() {
	int	   populationSize = 8;
	Genome g = new Genome();
	g.add(new Gene.Integer(10));
	return Population.create(g, populationSize);
    } 

    public void run() {
	System.out.println("run(): creating population");
	double maxCrossover = 0.1;
	double maxMutation = 0.3;
	ga = new IncrementalGeneticAlgorithm(2, 2, maxCrossover, maxMutation);
	ga.setSelection(Selectors.likelyBetter());
	System.out.println("run(): breeding population");
	ga.solve(this);
	System.out.println("run(): found solution");
	System.out.println(ga);
    } 

    public boolean isSolution(Population pop) {
	return pop.get(0).getFitness() == Integer.MAX_VALUE;
    } 
}
