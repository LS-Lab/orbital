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
	System.out.println(M + "\n+\n" + N + "\n=\n" + M.add(N));
    } 
}
