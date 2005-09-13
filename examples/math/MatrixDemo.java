import orbital.math.*;

public class MatrixDemo {
    public static void main(String arg[]) throws Exception {
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	double ms[][] = {
	    {2,1,0,-2},
	    {1,2,4,1},
	    {-2,1,2,-2},
	    {-3,0,1,-4}};
	double vs[] = {
	    1, 2, 1, 2
	};
	double us[] = {
	    2, 1, 0, -3
	};
	Matrix M = vf.valueOf(ms);
	Vector v = vf.valueOf(vs);
	Vector u = vf.valueOf(us);
	System.out.println(M + "*" + v + "=" + M.multiply(v));
	System.out.println(u + "*" + v + "=" + u.multiply(v));
	System.out.println(v + "*" + 2 + "=" + v.multiply(vf.valueOf(2)));
	System.out.println("norm ||M||\t=" + M.norm());
	System.out.println("column sum norm\t=" + M.norm(1));
	System.out.println("row sum norm\t=" + M.norm(Double.POSITIVE_INFINITY));
	System.out.println("Rank M\t=" + M.linearRank());
	System.out.println("det M\t= |M|=" + M.det());
	System.out.println("Tr M\t=" + M.trace());
	System.out.println("3-norm of second row\t=" + M.getRow(1).norm(3));
	System.out.println("5-norm of third column\t=" + M.getColumn(2).norm(5));
	System.out.println("M^-1\t=" + M.inverse());
	System.out.println("Type examination Matrix N to multiply with");
	String n = "";
	while (true) {
	    int ch = System.in.read();
	    if (ch == -1 || ch == 0x1b)
		break;
	    n += (char) ch;
	} 
	Matrix N = (Matrix) vf.valueOf(n);
	System.out.println("||N||=" + N.norm());
	System.out.println("|N|=" + N.det());
	System.out.println("Tr N=" + N.trace());
	System.out.print(M + "\n*\n" + N);
	System.out.println("=" + M.multiply(N));
	System.out.println("N^-1=" + N.inverse());
    } 
}
