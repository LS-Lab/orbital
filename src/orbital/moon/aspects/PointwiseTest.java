package orbital.moon.aspects;

import orbital.math.Arithmetic;

import orbital.math.functional.*;
import orbital.logic.functor.Notation;

import orbital.math.Scalar;

class PointwiseTest implements Function {
	public static void main(String arg[]) {
		BinaryFunction b = Operations.times;
		System.out.println(b + "(4,6)" + b.apply(Values.valueOf(4),Values.valueOf(6)));
		Function u = new PointwiseTest(b, Values.valueOf(6));
		System.out.println(u + "(4)" + u.apply(Values.valueOf(4)));
		u = (Function) u.add(u);
		System.out.println(u + "(4)" + u.apply(Values.valueOf(4)));
	}
	
	protected BinaryFunction f;
	protected Object x;
	public PointwiseTest(BinaryFunction f, Object x) {
		this.f = f;
		this.x = x;
	}
    public Object apply(Object y) {
    	return f.apply(x, y);
    } 
    public Function derive() {
    	return Functionals.bindFirst(f.derive(1), x);
    } 
    public Function integrate() {
    	return Functionals.bindFirst(f.integrate(1), x);
    }
    public String toString() {
    	return Notation.DEFAULT.format(f, new Object[] {
    		x, "#0"
    	});
    } 
}
