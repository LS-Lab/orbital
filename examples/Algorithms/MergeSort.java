import orbital.algorithm.template.*;
import orbital.math.MathUtilities;

/**
 * MergeSort with algorithmic template of divide and conquer.
 * @attribute complexity O(n*log(n)) to sort n elements
 */
public class MergeSort implements DivideAndConquerProblem {
    public static void main(String arg[]) {
	DivideAndConquer s = new DivideAndConquer();
	double[]		 o = new double[] {
	    1, 4, 5, 7, 8, 6, 4, 3, 6, 2, -1, 2, 7, 8, 1, 9, 0
	};
	Object			 solution = s.solve(new MergeSort(o));
	System.out.println(MathUtilities.format((double[]) solution));
    } 

    private double[] a;
    private int		 left;
    private int		 right;
    public MergeSort(double[] a) {
	this(a, 0, a.length - 1);
    }
    private MergeSort(double[] a, int left, int right) {
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

    // note: sorting is not done in place for simplicity in the merge method.
    public DivideAndConquerProblem[] divide() {
	int		 middle = (left + right) / 2;
	double[] x = new double[middle - left + 1];	   // from left to middle
	double[] y = new double[right - middle];	   // from middle+1 to right
	System.arraycopy(a, left, x, 0, x.length);
	System.arraycopy(a, middle + 1, y, 0, y.length);
	return new DivideAndConquerProblem[] {
	    new MergeSort(x), new MergeSort(y)
	};
    } 

    public Object merge(Object[] sortedParts) {

	// merge the sorted arrays of the partial solutions into one
	//assert sortedParts.length == 2 : "we have divided into two partial problems";
	double[] x = (double[]) sortedParts[0];
	double[] y = (double[]) sortedParts[1];
	//assert x.length + y.length == a.length : "the two partial arrays (sized " + x.length + " and " + y.length + ") make up the single whole array (sized " + a.length + ")";
	int i = 0;			   // index in the array a in which to merge
	int ix = 0, iy = 0;	   // indices in arrays x and y

	// merge both arrays as long as both contain data
	while (ix < x.length && iy < y.length)
	    if (x[ix] <= y[iy])
		a[i++] = x[ix++];
	    else
		a[i++] = y[iy++];

	// append the single array that still does contain data
	while (ix < x.length)
	    a[i++] = x[ix++];
	while (iy < y.length)
	    a[i++] = y[iy++];
	return a;
    } 
}
