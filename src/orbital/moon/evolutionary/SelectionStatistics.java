/**
 * @(#)SelectionStatistics.java 0.9 2001/04/08 Andre Platzer
 *
 * Copyright (c) 2001 Andre Platzer. All Rights Reserved.
 */

package orbital.moon.evolutionary;

import orbital.algorithm.evolutionary.Genome;
import orbital.algorithm.evolutionary.Population;

/**
 * SelectionStatistics class is an internal statistics class.
 * <p>
 * Note: you should handle this class as if it were package-level protected.</p>
 *
 * @version 0.9, 2001/04/08
 * @author  Andr&eacute; Platzer
 */
public class SelectionStatistics {
    public static final SelectionStatistics selectionStatistics = new SelectionStatistics();
	
    protected int selected[];

    public void setSelected(Population population, Genome selected[]) {
	this.selected = new int[selected.length];
	for (int i = 0; i < selected.length; i++)
	    this.selected[i] = population.getMembers().indexOf(selected[i]);
    }
	
    public int[] getSelected() {
	return selected;
    }
}
