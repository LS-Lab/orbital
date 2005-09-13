

import orbital.algorithm.template.*;
import orbital.logic.functor.Function;
import orbital.math.MathUtilities;
import orbital.util.Pair;
import java.util.*;

/**
 * continuous Knapsack problem solved with greedy algorithm.
 * In continuous Knapsack, instead of packing or leaving,
 * you can pack a fraction of a good, as well,
 * for example one third of the second item.
 * Unlike discrete Knapsack, continuous Knapsack is in P as you can see below.
 */
public class KnapsackGreedy implements GreedyProblem, Function {
    static final int MAXWEIGHT = 17;
    static final int ItemDesc[][] = {    // Array of {Weight, Value}
        {3,4}, {3,4}, {3,4}, {3,4}, {3,4},
        {4,5}, {4,5}, {4,5},
        {7,10},{7,10},{8,11},{8,11},{9,13}};
    public static void main(String arg[]) {
        Greedy s = new Greedy();
        List   solution = s.solve(new KnapsackGreedy(MAXWEIGHT, ItemDesc));
        print(solution);
    } 
    public static final int WEIGHT = 0;
    public static final int VALUE = 1;

    private int                         size;
    private int[][]                     elements;        // array of {weight, value}
    private List                        currentChoices;
    public KnapsackGreedy(int size, int[] weight, int[] value) {
        this.size = size;
        elements = new int[weight.length][];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new int[2];
            elements[i][WEIGHT] = weight[i];
            elements[i][VALUE] = value[i];
        } 
    }
    public KnapsackGreedy(int size, int[][] elements) {
        this.size = size;
        this.elements = elements;
    }

    public List getInitialCandidates() {
        List els = new LinkedList();
        for (int i = 0; i < elements.length; i++)
            els.add(new Pair(new Integer(elements[i][WEIGHT]), new Integer(elements[i][VALUE])));
        return els;
    } 
    public List nextCandidates(List candidates) {

        // candidates don't change
        return candidates;
    } 

    public boolean isPartialSolution(List choices) {
        print(choices);
        return isSolution(choices);
    } 
    public List nextPartialSolution(List choices, Object new_choice) {
        if (totalWeight(choices) + decode(new_choice)[WEIGHT] <= size)
            choices.add(new_choice);
        return choices;
    } 
    public boolean isSolution(List choices) {
        return totalWeight(choices) <= size;
    } 

    public Function getWeightingFor(List choices) {
        currentChoices = choices;
        return this;
    } 
    final double PENALTY = 30.0;
    public Object apply(Object a) {
        double[] x = decode(a);
        double   r = x[VALUE] / x[WEIGHT];
        if (totalWeight(currentChoices) + x[WEIGHT] <= size)
            return new Double(r);
        else
            return new Double(r - PENALTY * (totalWeight(currentChoices) + x[WEIGHT] - size));
    } 

    /**
     * calculates the total weight of a collection of objects.
     */
    private static double totalWeight(Collection choices) {
        double totalWeight = 0;
        for (Iterator i = choices.iterator(); i.hasNext(); ) {
            double[] x = decode(i.next());
            totalWeight += x[WEIGHT];
        } 
        return totalWeight;
    } 

    /**
     * decode weight and value of an object.
     */
    private static double[] decode(Object a) {
        Pair   p = (Pair) a;
        double w = ((Number) p.B).doubleValue();
        double v = ((Number) p.A).doubleValue();
        return new double[] {
            w, v
        };
    } 


    private static void print(List choices) {
        double totalWeight = 0;
        double totalValue = 0;
        for (Iterator i = choices.iterator(); i.hasNext(); ) {
            double[] x = decode(i.next());
            totalWeight += x[WEIGHT];
            totalValue += x[VALUE];
            System.out.print("(" + MathUtilities.format(x[WEIGHT]) + ", " + MathUtilities.format(x[VALUE]) + "$),\t");
        } 
        System.out.println("total:\t" + MathUtilities.format(totalWeight) + ", " + MathUtilities.format(totalValue) + "$");
    } 
}
