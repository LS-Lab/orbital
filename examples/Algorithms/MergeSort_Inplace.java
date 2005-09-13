

import orbital.algorithm.template.*;
import orbital.math.MathUtilities;

/**
 * MergeSort_Inplace with algorithmic template of divide and conquer.
 * Sorting is done in place which makes this implementation a little more confusing.
 * @attribute complexity O(n*log(n)) to sort n elements
 */
public class MergeSort_Inplace implements DivideAndConquerProblem {
    public static void main(String arg[]) {
	DivideAndConquer s = new DivideAndConquer();
	double[]		 o = new double[] {
	    1, 4, 5, 2, 7, 8, 1, 9, 3
	};
	Object			 solution = s.solve(new MergeSort_Inplace(o));
	System.out.println(MathUtilities.format((double[]) solution));
    } 

    private double[] a;
    private int		 left;
    private int		 right;
    private int		 middle;
    public MergeSort_Inplace(double[] a) {
	this(a, 0, a.length - 1);
    }
    private MergeSort_Inplace(double[] a, int left, int right) {
	this.a = a;
	this.left = left;
	this.right = right;
    }

    public boolean smallEnough() {
	return left >= right;
    } 

    public Object basicSolve() {

	// the data part with length 1 is already sorted, we simply return it
	return a;
    } 

    public DivideAndConquerProblem[] divide() {
	middle = (left + right) / 2;
	return new DivideAndConquerProblem[] {
	    new MergeSort_Inplace(a, left, middle), new MergeSort_Inplace(a, middle + 1, right)
	};
    } 

    public Object merge(Object[] sortedParts) {

	// merge the sorted arrays of the partial solutions into one
	//assert sortedParts.length == 2 : "we have divided into two partial problems";
	double[] x = (double[]) sortedParts[0];	   // == a
	double[] y = (double[]) sortedParts[1];	   // == a
	double[] t = new double[right - left + 1];	  // avoid overwriting during merge
	int		 i = 0;							   // index in the new array t in which to merge
	int		 ix = left, iy = middle + 1;	   // indices in arrays x and y

	// merge both arrays as long as both contain data
	while (ix <= middle && iy <= right)
	    if (x[ix] <= y[iy])
		t[i++] = x[ix++];
	    else
		t[i++] = y[iy++];

	// append the single array that still does contain data
	while (ix <= middle)
	    t[i++] = x[ix++];
	while (iy <= right)
	    t[i++] = y[iy++];
	//assert i == t.length && ix - 1 == middle && iy - 1 == right : "we have merged all values, so our indices are most right now";

	// copy t back into a
	for (int k = 0; k < t.length; k++)
	    a[left + k] = t[k];
	return a;
    } 
}
