import orbital.algorithm.template.*;
import orbital.math.MathUtilities;

/**
 * QuickSort with explicit algorithmic template of divide and conquer.
 * @attribute complexity O(n^2) to sort n elements
 */
public class QuickSort implements DivideAndConquerProblem {
	public static void main(String arg[]) {
		DivideAndConquer s = new DivideAndConquer();
		double[]		 o = new double[] {
			1, 4, 5, 2, 7, 8, 1, 9, 0
		};
		Object			 solution = s.solve(new QuickSort(o));
		System.out.println(MathUtilities.format((double[]) solution));
	} 

	private final double[] a;
	private final int	   left;
	private final int	   right;
	/**
	 * The problem of sorting an array (of doubles).
	 */
	public QuickSort(double[] a) {
		this(a, 0, a.length - 1);
	}
	/**
	 * The problem of sorting part of an array (of doubles) ranging from left to right.
	 */
	private QuickSort(double[] a, int left, int right) {
		this.a = a;
		this.left = left;
		this.right = right;
	}

	public boolean smallEnough() {
		return left >= right;
	} 

	public Object basicSolve() {
		// the data part with a length of 1 is already sorted, we simply return it
		// (of course it is embedded in the larger array)
		return a;
	} 

	public DivideAndConquerProblem[] divide() {
		final double pivot = a[left];
		int	   l = left, r = right;
		while (l < r) {
			while (l < right && a[l] <= pivot)
				l++;
			while (r > left && pivot < a[r])
				r--;
			if (l < r)
				swap(l, r);
		} 
		final int middle = r;
		swap(left, middle);
		return new DivideAndConquerProblem[] {
			new QuickSort(a, left, middle - 1), new QuickSort(a, middle + 1, right)
		};
	} 

	public Object merge(Object[] partialSolutions) {
		// since only data partitioning was done, there's no need to merge the data any further
		return a;
	} 

	private void swap(int x, int y) {
		double t = a[x];
		a[x] = a[y];
		a[y] = t;
	} 
}
