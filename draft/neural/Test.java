import dkfz.collections.graph.*;
import dkfz.collections.graph.view.*;
import java.awt.*;
import java.util.Random;
import orbital.awt.*;

import orbital.math.*;
import orbital.math.functional.*;
import orbital.logic.functor.Function;


class Test {
    public static void main(String arg[]) {
	MathUtilities.setDefaultPrecisionDigits(4);
	test_batch();
    }
    protected static void show(Graph net) {
	Frame f = new Frame("Neural Network");
	new Closer(f, true, true);
	ViewGraph view = new ViewGraph();
	view.setEdgePainter(new StraightLinePainter());
	view.setVertexPainter(new StraightLinePainter());
	view.setModel(MatrixGraph.converseGraph(net));
	f.add(view);
	f.setLocation(200, 100);
	f.setSize(400, 400);
	new TreeLayouter().layout(view);
	f.setVisible(true);
	view.registerController(new GraphController(view,
						    new Class[] {NeuronImpl.class},
						    new String[] {"Neuron"},
						    new Class[] {Real.class},
						    new String[] {"Real"}));
    }
    private static void test_top() {
	Backpropagation net = new Backpropagation();
	net.createLayers(new int[] {4, 2, 1});
	net.setActivationFunction(Functions.logistic);
	Vector v = Values.valueOf(new double[] {1, 1, 1, 2});
	System.out.println(v + " -->");
	System.out.println("--> " + net.apply(v));
	show(net);
    }

    private static void test_learnFct() {
	Backpropagation net = new Backpropagation();
	net.createLayers(new int[] {1, 1, 1, 1});
	net.setActivationFunction(Functions.id);
	net.setLearningRate(.2);

	Function f = (Function) Operations.plus.apply(Functions.linear(Values.valueOf(1/4.)), Functions.constant(Values.valueOf(.3)));
	Vector e[] = new Vector[20], t[] = new Vector[e.length];
	for (int i = 0; i < e.length; i++) {
	    double x = Math.random();
	    e[i] = Values.valueOf(new double[] {x});
	    t[i] = Values.valueOf(new Arithmetic[] {(Arithmetic) f.apply(Values.valueOf(x))});
	}
        
        for (int i = 0; i < 400; i++)
	    net.learn(e, t);

	System.out.println("learning " + f);
	Vector v = Values.valueOf(new double[] {.5});
	System.out.println(v + " -->");
	System.out.println("--> " + net.apply(v));
	System.out.println("correct value " + f.apply(v.get(0)));
	show(net);
    }
	
    private static void test_learnMaj() {
	final int size = 11;
	Backpropagation net = new Backpropagation();
	net.createLayers(new int[] {size, 1});
	net.setActivationFunction(Functions.logistic);
	net.setLearningRate(.05);
	Random r = new Random();

	Function f = majority;
	Vector e[] = new Vector[40], t[] = new Vector[e.length];
	for (int i = 0; i < e.length; i++) {
	    Vector x = Values.getInstance(size);
	    for (int j = 0; j < x.dimension(); j++)
		x.set(j, Values.valueOf(r.nextInt(2)));
	    e[i] = x;
	    t[i] = Values.valueOf(new Arithmetic[] {(Arithmetic) f.apply(x)});
	}
        
        for (int i = 0; i < 1000; i++)
	    net.learn(e, t);

	System.out.println("learning " + f);

	// test result
	Vector v = Values.getInstance(size);
	for (int j = 0; j < v.dimension(); j++)
	    v.set(j, Values.valueOf(r.nextInt(2)));
	System.out.println(v + " -->");
	System.out.println("--> " + net.apply(v));
	System.out.println("correct value " + f.apply(v));
	show(net);
    }

    /**
     * Returns 1 if the majority of the elements in the input vector are 0, and 0 otherwise.
     */
    private static final Function majority = new Function() {
	    public Object apply(Object o) {
		Vector v = (Vector) o;
		int count = 0;
		for (int i = 0; i < v.dimension(); i++)
		    if (v.get(i).norm().equals(Values.ZERO))
			if (++count > v.dimension() / 2)
			    return Values.valueOf(1);
		return Values.valueOf(0);
	    }
	};

    private static void test_batch() {
	Backpropagation net = new BatchBackpropagation();
	net.createLayers(new int[] {1, 1});
	net.setActivationFunction(Functions.id);
	net.setLearningRate(.2);

	Function f = (Function) Operations.plus.apply(Functions.linear(Values.valueOf(1/4.)), Functions.constant(Values.valueOf(.3)));
	Vector e[] = new Vector[10], t[] = new Vector[e.length];
	for (int i = 0; i < e.length; i++) {
	    double x = Math.random();
	    e[i] = Values.valueOf(new double[] {x});
	    t[i] = Values.valueOf(new Arithmetic[] {(Arithmetic) f.apply(Values.valueOf(x))});
	}
        
        for (int i = 0; i < 200; i++)
	    net.learn(e, t);
        
	System.out.println("learning " + f);
	Vector v = Values.valueOf(new double[] {.5});
	System.out.println(v + " -->");
	System.out.println("--> " + net.apply(v));
	System.out.println("correct value " + f.apply(v.get(0)));
	show(net);
    }
}
