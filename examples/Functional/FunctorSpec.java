

import orbital.logic.functor.*;
import orbital.logic.functor.Functor.Specification;

public class FunctorSpec {
    public static void main(String arg[]) throws Exception {
	orbital.Adjoint.print("demonstrating flexibility of functor specifications");
	orbital.Adjoint.print("arities", "arities of common functors");
	orbital.Adjoint.print("VoidFunction", "\n\tf" + Specification.getSpecification(new VoidFunction() {
		public Object apply() {
		    return null;
		} 
	    }));
	orbital.Adjoint.print("Function", "\n\tf" + Specification.getSpecification(new Function() {
		public Object apply(Object a) {
		    return null;
		} 
	    }));
	orbital.Adjoint.print("BinaryFunction", "\n\tf" + Specification.getSpecification(new BinaryFunction() {
		public Object apply(Object a, Object b) {
		    return null;
		} 
	    }));
	orbital.Adjoint.print("VoidPredicate", "\n\tP" + Specification.getSpecification(new VoidPredicate() {
		public boolean apply() {
		    return false;
		} 
	    }));
	orbital.Adjoint.print("Predicate", "\n\tP" + Specification.getSpecification(new Predicate() {
		public boolean apply(Object a) {
		    return false;
		} 
	    }));
	orbital.Adjoint.print("BinaryPredicate", "\n\tP" + Specification.getSpecification(new BinaryPredicate() {
		public boolean apply(Object a, Object b) {
		    return false;
		} 
	    }));
	orbital.Adjoint.print("explicitly specified arity and type", "\n\tf" + Specification.getSpecification(new DisambiguatedFunctor()));
	orbital.Adjoint.print("implicitly specified arity and type", "\n\tf" + Specification.getSpecification(new Functor() {
		public Object apply(Object a) {
		    return null;
		} 
	    }));
	orbital.Adjoint.print("notation", "testing function notation");
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
	orbital.Adjoint.print("prefix notation", f);
	f.setNotation(Notation.INFIX);
	orbital.Adjoint.print("infix notation", f);
	f.setNotation(Notation.POSTFIX);
	orbital.Adjoint.print("postfix notation", f);
    } 
	
    public static class DisambiguatedFunctor implements Functor {
	public int apply(float a, double b, int c, Number d, Object e) {
	    return 0;
	} 
	public static final Specification specification = new Specification(new Class[] {Float.TYPE, Double.TYPE, Integer.TYPE, Number.class, Object.class}, Integer.TYPE);
	public int apply(float a, int b, Object c) {
	    return 0;
	} 
    };
}
