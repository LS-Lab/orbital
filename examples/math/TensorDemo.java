import orbital.math.*;

public class TensorDemo {
    public static void main(String arg[]) throws Exception {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	int ms[][][] = {
	    {{2,1},{0,-2}},
	    {{1,2},{4,1}},
	    {{-2,1},{2,-2}},
	    {{-3,0},{1,-4}}
	};
	Tensor M = vf.tensor(ms);
	Tensor N = (Tensor) M.clone();
	Arithmetic ps[][] = {
	    {vf.valueOf(4),vf.valueOf(3.0)},
	    {vf.valueOf(8),vf.valueOf(6)}
	};
	Tensor P = vf.tensor(ps);
	int ts[][][] = {
	    {{2,1},{0,-2}},
	    {{1,2},{4,1}}
	};
	Tensor T = vf.tensor(ts);
	System.out.println(M + "\n+\n" + N + "\n=\n" + M.add(N));
	System.out.println();
	System.out.println(M + "\n part =\n" + M.subTensor(1,1));
	System.out.println();
	System.out.println(M + "\n(x)\n" + P + "\n=\n" + M.tensor(P));
	System.out.println();
	System.out.println(M + "\n.\n" + T + "\n=\n" + M.multiply(T));
    }

}
