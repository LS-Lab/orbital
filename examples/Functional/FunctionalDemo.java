

import orbital.math.*;
import orbital.math.functional.*;

public class FunctionalDemo {
	public static void main(String arg[]) throws Exception {
		Function f;
		Function g = Functions.square;
		Function h = (Function) Operations.times.apply(Functions.id, Functions.id);
		f = g;
		System.out.println(f);
		System.out.println("has some values");
		for (int i = 0; i < 4; i++) {
			Scalar x = Values.valueOf(i);
			System.out.println("(" + x + "|" + f.apply(x) + ")\t");
		} 
		f = h;
		System.out.println(f);
		System.out.println("has some values");
		for (int i = 0; i < 4; i++) {
			Scalar x = Values.valueOf(i);
			System.out.println("(" + x + "|" + f.apply(x) + ")\t");
		} 
		f = g.derive();
		System.out.println("(" + g + ")' = " + f);
		System.out.println("has some values");
		for (int i = 0; i < 4; i++) {
			Scalar x = Values.valueOf(i);
			System.out.println("(" + x + "|" + f.apply(x) + ")\t");
		} 
		f = h.derive();
		System.out.println("(" + h + ")' = " + f);
		System.out.println("has some values");
		for (int i = 0; i < 4; i++) {
			Scalar x = Values.valueOf(i);
			System.out.println("(" + x + "|" + f.apply(x) + ")\t");
		} 
		f = f.derive();
		System.out.println("(" + f + ")' = " + f);
		System.out.println("has some values");
		for (int i = 0; i < 4; i++) {
			Scalar x = Values.valueOf(i);
			System.out.println("(" + x + "|" + f.apply(x) + ")\t");
		} 
	} 
}
