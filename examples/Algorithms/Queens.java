

import orbital.algorithm.template.*;
import java.util.*;

/**
 * Will place 8 queens on a checker board avoiding that one can move to another next turn.
 */
public class Queens implements BacktrackingProblem {
	public static void main(String arg[]) {
		Backtracking s = new Backtracking();
		List		 solution = s.solve(new Queens());
	} 

	private int solutionCount = 1;
	private int curRow = 0;
	private int nextPossibility[];
	public Queens() {
		nextPossibility = new int[8];
		for (int row = 0; row < 8; row++)
			nextPossibility[row] = 0;
	}

	public int getNumberOfVars() {
		return 8;
	} 
	public int getNumberOfVariants(int row) {
		return 8;
	} 

	public boolean isConsistent(List choices, int row) {
		int n_x = ((Integer) choices.get(row)).intValue();
		for (int i = 0; i < row; i++) {
			if (canBeat(((Integer) choices.get(i)).intValue(), i, n_x, row))
				return false;
		} 
		if (row + 1 == getNumberOfVars()) {
			System.out.println("Solution " + solutionCount);
			solutionCount++;
			printBoard(choices);
		} 
		return true;
	} 

	/**
	 * try the next possibility.
	 */
	public Object chooseNext(int row) {
		if (row > curRow)	 // backtrack then try again
			nextPossibility[row] = 0;
		if (nextPossibility[row] >= 8)
			return null;
		curRow = row;
		return new Integer(nextPossibility[row]++);
	} 

	/**
	 * whether two queens positioned at x1|y1 and x2|y2 can beat each other.
	 */
	private boolean canBeat(int x1, int y1, int x2, int y2) {
		return x1 == x2 || y1 == y2 || Math.abs(x1 - x2) == Math.abs(y1 - y2);
	} 

	private static void printBoard(List board) {
		for (int i = 0; i < 8; i++) {
			int col = ((Integer) board.get(i)).intValue();
			for (int j = 0; j < 8; j++)
				System.out.print(j == col ? "Q" : ".");
			System.out.println();
		} 
	} 
}
