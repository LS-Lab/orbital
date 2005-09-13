import orbital.logic.functor.*;
import orbital.logic.functor.Functor.Specification;
import orbital.logic.sign.concrete.Notation;

public class FunctorSpec {
    public static void main(String arg[]) throws Exception {
	System.out.println("demonstrating flexibility of functor specifications");
	System.out.println("arities of common functors");
	System.out.println("VoidFunction \n\tf" + Specification.getSpecification(new VoidFunction() {
		public Object apply() {
		    return null;
		} 
	    }));
	System.out.println("Function \n\tf" + Specification.getSpecification(new Function() {
		public Object apply(Object a) {
		    return null;
		} 
	    }));
	System.out.println("BinaryFunction \n\tf" + Specification.getSpecification(new BinaryFunction() {
		public Object apply(Object a, Object b) {
		    return null;
		} 
	    }));
	System.out.println("VoidPredicate \n\tP" + Specification.getSpecification(new VoidPredicate() {
		public boolean apply() {
		    return false;
		} 
	    }));
	System.out.println("Predicate \n\tP" + Specification.getSpecification(new Predicate() {
		public boolean apply(Object a) {
		    return false;
		} 
	    }));
	System.out.println("BinaryPredicate \n\tP" + Specification.getSpecification(new BinaryPredicate() {
		public boolean apply(Object a, Object b) {
		    return false;
		} 
	    }));
	System.out.println("explicitly specified arity and type \n\tf" + Specification.getSpecification(new DisambiguatedFunctor()));
	System.out.println("implicitly specified arity and type \n\tf" + Specification.getSpecification(new Functor() {
		public Object apply(Object a) {
		    return null;
		} 
	    }));
	System.out.println("notation testing function notation");
	BinaryFunction fo = new BinaryFunction() {
		public Object apply(Object o, Object b) {
		    return b;
		} 
		public String toString() {
		    return "o";
		} 
	    };
	Function fl = new Function() {
		public Object apply(Object o) {
		    return o;
		} 
		public String toString() {
		    return "l";
		} 
	    };
	Function fr = new Function() {
		public Object apply(Object o) {
		    return o;
		} 
		public String toString() {
		    return "r";
		} 
	    };
	Function.Composite f = Functionals.compose(fo, fl, fr);
	f.setNotation(Notation.PREFIX);
	System.out.println("prefix notation :\t" + f);
	f.setNotation(Notation.INFIX);
	System.out.println("infix notation  :\t" + f);
	f.setNotation(Notation.POSTFIX);
	System.out.println("postfix notation:\t" + f);
    } 
	
    public static class DisambiguatedFunctor implements Functor {
	public int apply(float a, double b, int c, Number d, Object e) {
	    return 0;
	} 
	public static final Specification callTypeDeclaration = new Specification(new Class[] {Float.TYPE, Double.TYPE, Integer.TYPE, Number.class, Object.class}, Integer.TYPE);
	public int apply(float a, int b, Object c) {
	    return 0;
	} 
    };
}
