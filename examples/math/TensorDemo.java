import orbital.math.*;

public class TensorDemo {
    public static void main(String arg[]) throws Exception {
	Arithmetic ms[][][] = {
	    {{Values.valueOf(2),Values.valueOf(1)},{Values.valueOf(0),Values.valueOf(-2)}},
	    {{Values.valueOf(1),Values.valueOf(2)},{Values.valueOf(4),Values.valueOf(1)}},
	    {{Values.valueOf(-2),Values.valueOf(1)},{Values.valueOf(2),Values.valueOf(-2)}},
	    {{Values.valueOf(-3),Values.valueOf(0)},{Values.valueOf(1),Values.valueOf(-4)}}
	};
	Tensor M = Values.tensor(ms);
	Tensor N = (Tensor) M.clone();
	Arithmetic ps[][] = {
	    {Values.valueOf(4),Values.valueOf(3)},
	    {Values.valueOf(8),Values.valueOf(6)}
	};
	Tensor P = Values.tensor(ps);
	Arithmetic ts[][][] = {
	    {{Values.valueOf(2),Values.valueOf(1)},{Values.valueOf(0),Values.valueOf(-2)}},
	    {{Values.valueOf(1),Values.valueOf(2)},{Values.valueOf(4),Values.valueOf(1)}}
	};
	Tensor T = Values.tensor(ts);
	System.out.println(M + "\n+\n" + N + "\n=\n" + M.add(N));
	System.out.println();
	System.out.println(M + "\n part =\n" + M.subTensor(1,1));
	System.out.println();
	System.out.println(M + "\n(x)\n" + P + "\n=\n" + M.tensor(P));
	System.out.println();
	System.out.println(M + "\n.\n" + T + "\n=\n" + M.multiply(T));
    }

}
