//@todo phases could be startgame, midgame, endgame win, endgame lose

import orbital.logic.functor.Function;
import orbital.math.functional.Functions;
import orbital.game.*;
import orbital.math.*;
import java.util.*;
import java.util.List;
import java.io.*;

/**
 * New simple Chess Computer that employs &alpha;-&beta;-pruning and explicit utility functions.
 * @version 1.0, 2001/08/20
 * @author  Andr&eacute; Platzer
 */
public class UtilityComputer extends Computer {
    private Function/*<Field, Number>*/ utility;
    
    public UtilityComputer(Function/*<Field, Number>*/ utility) {
    	this.utility = utility;
    	aspects.maxDepth = 4;
    }
    public UtilityComputer() {
    	this(new Function() {
    		private int[] count;
    		public Object apply(Object arg) {
		    Field field = (Field) arg;
		    final int our = ChessRules.getOurLeague(field);
		    init(field);
		    int done = ChessRules.turnDoneImpl(field);
		    double winningPart = done == ChessRules.NOONE ? 0 : done == our ? 1 : -1;
		    //@todo add win/loose heuristic
		    return Functions.tanh.apply(Values.valueOf(winningPart * 4 + 0.1 * (count[ChessRules.KING] + count[ChessRules.PAWN])));
    		}
    		private int opponent(int league) {
		    switch (league) {
		    case ChessRules.BLACK: return ChessRules.WHITE;
		    case ChessRules.WHITE: return ChessRules.BLACK;
		    default: throw new IllegalArgumentException("the league " + league + " has no opponent");
		    }
    		}
    			
        	/**
        	 * Get the figure counts of each type.
        	 */
        	protected int[] getCount(Field field) {
		    return count;
        	}
        	private void init(Field field) {
		    //this.cachedField = field;
		    final int our = ChessRules.getOurLeague(field);
		    if (count == null)
			this.count = new int[ChessRules.FIGURES];
		    else
			Arrays.fill(count, 0);
		    for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
			Figure figure = (Figure) i.next();
			//assert MathUtilities.isin(figure.getLeague(), ChessRules.BLACK, ChessRules.WHITE) : "non-empty seti figures are either black or white";
			count[figure.getType()] += figure.getLeague() == our ? +1 : -1;
		    }
        	}
	    });
    }
    
    protected Function/*<Field, Number>*/ getUtility() {
    	return utility;
    }
    
    /**
     * Calculate the next Move and Figure that this Computer retrieves
     * as being the best for a situation like curField.
     */
    protected MoveWeighting.Argument determineNextMove(Field field) {
    	AlphaBetaPruning search = new AlphaBetaPruning(aspects.maxDepth, utility);

        AlphaBetaPruning.Option eva = search.solve(field);
        //assert eva != null : "computers should evaluate to anything at least";

	return new MoveWeighting.Argument(eva);
    }
    
    public String toString() {
    	return getClass().getName() + "[" + utility + "]";
    }
}
