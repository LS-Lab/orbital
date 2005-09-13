import orbital.game.*;
import orbital.robotic.strategy.*;
import orbital.robotic.*;
import orbital.logic.functor.Function;
import orbital.util.*;
import java.util.*;
import java.io.Serializable;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Our subclasses implement a naive Computer-Player for Chess just to
 * show how it works. To see what can be done with a more interesting
 * game there is an implementation of a board game called Seti.
 * @see <a href="http://www.functologic.com/">Seti Game</a>
 * @see orbital.game.AlphaBetaPruning
 */
public abstract class Computer implements Function {
    static final Logger logger = Logger.global;
    ChessAspects aspects;
    public Computer(ChessAspects aspects) {
	this.aspects = aspects;
    }
    public Computer() {
	this(new ChessAspects());
    }

    /**
     * Act as an AIntelligence: make a Move for the Computer.
     */
    public Object apply(Object arg) {
	ChessField field = (ChessField) arg;
        field.rules.setOurLeague(field.rules.getTurn());
	Computer.logger.log(Level.FINER, "AI {0} action {", field.rules);
	MoveWeighting.Argument move = determineNextMove(field);
	Computer.logger.log(Level.FINER, "} AI {0} will perform: {1}", new Object[] {field.rules, move});
	return move;
    } 

    /**
     * Calculate the next Move and Figure that this Computer retrieves
     * as being the best for a situation like curField.
     */
    protected abstract MoveWeighting.Argument determineNextMove(Field curField);
}
