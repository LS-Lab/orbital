import orbital.algorithm.evolutionary.*;
import orbital.logic.functor.Function;

/**
 * Knapsack problem solved with a genetic algorithm.
 * Knapsack is NP-complete.
 * 
 * @author Andr&eacute; Platzer
 * @version %I%, %G%
 */
public class Knapsack extends orbital.moon.awt.Demonstratos implements Runnable, GeneticAlgorithmProblem {
    static final int MAXWEIGHT = 17;
    static final int ItemDesc[][] = {   // Array of {Weight, Value}
	{3,4}, {3,4}, {3,4}, {3,4}, {3,4},
	{4,5}, {4,5}, {4,5},
	{7,10},{7,10},{8,11},{8,11},{9,13}};

    public static void main(String arg[]) {
	new Knapsack().run();
    } 


    protected GeneticAlgorithm ga;

    public Knapsack() {}

    public Function /* <Object, Number> */ getWeighting() {
	return new KnapsackWeighting(this);
    } 

    public Population getPopulation() {
	int	   populationSize = 6;
	Genome g = new Genome();
	g.add(new Gene.BitSet(ItemDesc.length));
	return Population.create(g, populationSize);
    } 

    public void init() {
	super.init();
	out.println("init() initializing");
    } 
    public void start() {
	super.start();
	out.println("start() start thread");
	new Thread(this).start();
    } 

    public void run() {
	out.println("run() creating population");
	double maxCrossover = 0.1;
	double maxMutation = 0.2;
	ga = new IncrementalGeneticAlgorithm(2, 2, maxCrossover, maxMutation);
	ga.setSelection(Selectors.likelyBetter());
	out.println("run() breeding population");
	ga.solve(this);
	out.println("run() found solution");
	out.println(ga.getPopulation());
    } 

    int weight;
    int value;
    void calcVW(Genome g) {
	Gene.BitSet c = (Gene.BitSet) g.get(0);
	weight = 0;
	value = 0;
	for (int iBit = 0; iBit < c.length(); iBit++)
	    if (c.get(iBit)) {
		weight += ItemDesc[iBit][0];
		value += ItemDesc[iBit][1];
	    } 
    } 

    public boolean isSolution(Population pop) {
	final int ANSWER = 24;
	out.println("isSolution?\n" + pop);
	for (int iChrom = 0; iChrom < pop.size(); iChrom++) {
	    calcVW(pop.get(iChrom));
	    if (weight <= MAXWEIGHT && value == ANSWER)
		return true;
	} 
	return false;
    } 
}

class KnapsackWeighting implements Function /* <Object, Number> */ {
    public KnapsackWeighting(Knapsack t) {
	ks = t;
    }
    private Knapsack ks;

    final double	 PENALTY = 3.0;
    public Object apply(Object genome) {
	try {
	    Genome g = (Genome) genome;

	    ks.calcVW(g);
	    if (ks.weight > ks.MAXWEIGHT)
		return new Double(ks.value - PENALTY * (ks.weight - ks.MAXWEIGHT));
	    else
		return new Double(ks.value);
	} catch (ClassCastException err) {
	    throw new Error("panic");
	} 
    } 
}
