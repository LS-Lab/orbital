

import orbital.logic.functor.*;
import orbital.util.*;
import orbital.math.*;
import orbital.math.functional.Operations;
import orbital.math.functional.Functionals;
import orbital.math.functional.Functions;
import orbital.math.Integer;
import java.util.*;

/**
 * Demonstrate functional recursion schemes.
 * The functional recursion schemes applied have funky names like:
 *  banana - catamorphism
 *  lense - anamorphism
 *  envelope - hylomorphism
 *  barbed wire - paramorphism
 * @see <a href="http://www.willamette.edu/~fruehr/haskell/evolution.html">The Evolution of a Haskell Programmer</a>
 */
public class BananaTest {
    public static void main(String arg[]) {
	List numbers = Arrays.asList(new Object[] {
	    Values.valueOf(1), Values.valueOf(2), Values.valueOf(3), Values.valueOf(9), Values.valueOf(7)
	});
	System.out.println("Testing banana (|..|) with sum functional");
	System.out.print(new Functionals.Catamorphism(Values.valueOf(0), Operations.plus).apply(numbers.iterator()) + " = ");
	System.out.println(Operations.sum.apply(numbers.iterator()));
	System.out.println("Testing banana (|..|) foldLeft with prod functional");
	System.out.println(Operations.product.apply(numbers.iterator()));
	Predicate P = Functionals.bindSecond(Predicates.greater, Values.valueOf(2));
	System.out.println("Testing banana (|..|) with filter " + P);
	System.out.println(filter(P).apply(numbers.iterator()));
	Function f = Functions.pow(2);
	System.out.println("Testing lense |(..)| with map functional for " + f);
	System.out.println(map(f, numbers.iterator()));

	System.out.println("Testing envelope |[..]| with factorial");
	for (int i = 0; i < 10; i++)
	    System.out.println(i + "! = " + factorial(i));
	System.out.println("Testing barbed wire {|..|} with factorial");
	for (int i = 0; i < 10; i++)
	    System.out.println(i + "! = " + factorialb(i));

	System.out.println("Testing barbed wire {|..|} with tails");
	List r = (List) Functionals.barbedwire(new LinkedList(), new BinaryFunction() {
		public Object apply(Object a, Object b) {
		    Pair	 p = (Pair) b;
		    Iterator as = (Iterator) p.A;
		    List	 tls = (List) p.B;
		    List	 l = new LinkedList();
		    l.add(a);
		    while (as.hasNext())
			l.add(as.next());
		    tls.add(0, l);
		    return tls;
		} 
	    }, numbers.iterator());
	for (Iterator i = r.iterator(); i.hasNext(); )
	    System.out.println("    " + i.next());

	System.out.println("Comparing recursive banana (|..|) with iterative foldRight and iterative foldLeft for associativity");

	// short hand form of g = (BinaryFunction) Operations.plus.apply(Functions.projectFirst, Functionals.onSecond(Functions.linear(Values.valueOf(2))));
	BinaryFunction g = new BinaryFunction() {
		public Object apply(Object a, Object b) {
		    return ((Arithmetic) a).add(Values.valueOf(2).multiply((Arithmetic) b));
		} 
		public String toString() {
		    return "x + 2*y";
		} 
	    };
	System.out.println("for the binary function g(x,y) := " + g);
	System.out.print("is " + new Functionals.Catamorphism(Values.valueOf(0), g).apply(numbers.iterator()));
	System.out.print(" == " + Functionals.foldRight(g, Values.valueOf(0), numbers));
	System.out.println(" == " + Functionals.foldLeft(g, Values.valueOf(0), numbers) + ", or not?");
	List formula = Arrays.asList(new Object[] {
	    Values.symbol("a"), Values.symbol("b"), Values.symbol("c")
	});
	System.out.println("because symbolic it is");
	System.out.println("  " + new Functionals.Catamorphism(Values.valueOf(1), g).apply(formula.iterator()) + "\tfor banana");
	System.out.println("  " + Functionals.foldRight(g, Values.valueOf(1), formula) + "\tfor foldRight");
	System.out.println("  " + Functionals.foldLeft(g, Values.valueOf(1), formula) + "\tfor foldLeft");
	orbital.math.functional.BinaryFunction h = Functions.binarySymbolic("f");
	System.out.println("for an arbitrary symbolic function " + h + "(x,y) it is");
	System.out.println("  " + new Functionals.Catamorphism(Values.valueOf(1), h).apply(formula.iterator()) + "\tfor banana");
	System.out.println("  " + Functionals.foldRight(h, Values.valueOf(1), formula) + "\tfor foldRight");
	System.out.println("  " + Functionals.foldLeft(h, Values.valueOf(1), formula) + "\tfor foldLeft");
	System.out.println(h + " / d(x,y) = " + h.derive() + "\n  integral " + h + " dx = " + h.integrate(0));
	System.out.println(h + " / d(x,y) = " + h.derive() + "\n  integral " + h + " dy = " + h.integrate(1));
    } 


    /**
     * filter = (|nil, f|)
     * Where
     * f(a, as) = cons(a, as)  <= p(a)
     * = as           <= not p(a)
     */
    public static Function filter(final Predicate p) {
	return new Functionals.Catamorphism(new LinkedList(), new BinaryFunction() {
		public Object apply(Object a, Object as) {
		    if (p.apply(a)) {
			((List) as).add(0, a);
			return as;
		    } else
			return as;
		} 
	    });
    } 

    /**
     * banana map
     * map == (|nil, g|)
     * Where
     * g(a, bs) = cons(f a, bs)
     * 
     * Which will lead to the usual map operator.
     * map f nil = f nil
     * map f cons(first, rest) = cons(f first, map f rest)
     */

    /**
     * lense map
     * map == |(g, p)|
     * Where
     * p(as) = as.isEmpty()
     * g(cons(a,as)) = (f(a), as)
     */
    public static Collection map(final Function f, Iterator a) {
	return Functionals.lense(new Function() {
		public Object apply(Object o) {
		    Iterator i = (Iterator) o;
		    return new Pair(f.apply(i.next()), i);
		} 
	    }, new Predicate() {
		    public boolean apply(Object o) {
			Iterator i = (Iterator) o;
			return !i.hasNext();
		    } 
		}, a);
    } 

    /**
     * exists value = (|nil, f|)
     * f(first, rest) = first   <= value == first
     * f(first, rest) = nil     <= &not; value == first
     */

    /**
     * Hylomorphism factorial.
     * @return <span class="envelopeBracket">[[</span>(1,*),(g,&lambda;x. x==0)<span class="envelopeBracket">]]</span> n.
     *  where g(1 + n) = (1 + n, n).
     */
    public static int factorial(int n) {
	return ((Number) Functionals.envelope(Values.valueOf(1), Operations.times, new Function() {
		public Object apply(Object no) {
		    int n = ((Number) no).intValue();
		    return new Pair(no, Values.valueOf(n - 1));
		} 
	    }, Functionals.bindSecond(Predicates.equal, Values.valueOf(0)), Values.valueOf(n))).intValue();
    } 

    /**
     * efficient factorial with barbed wire.
     * @return <span class="barbedwireBracket">{|</span>1, (1+x)*y<span class="barbedwireBracket">|}</span> n
     */
    public static int factorialb(int n) {
	return Functionals.barbedwire(1, new BinaryFunction() {
		public Object apply(Object n, Object m) {
		    return new java.lang.Integer((int) ((1 + ((Number) n).doubleValue()) * ((Number) m).doubleValue()));
		} 
	    }, n);
    } 
}
