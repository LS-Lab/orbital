import orbital.game.*;
import orbital.robotic.strategy.*;
import orbital.robotic.*;
import orbital.logic.functor.Function;
import orbital.util.*;
import java.util.*;
import java.io.Serializable;

import java.util.logging.Level;

/**
 * Implements a naive Computer-Player for Chess to show how it works.
 * To see what can be done with a more interesting game there is
 * an implementation of a board game called Seti.
 * @see <a href="http://www.functologic.com/">Seti Game</a>
 */
public class WeightingComputer extends Computer {
    public WeightingComputer(ChessAspects aspects) {
        super(aspects);
    }
    public WeightingComputer() {
        this(new ChessAspects());
    }

    /**
     * Calculate the next Move and Figure that this Computer retrieves
     * as being the best for a situation like curField.
     */
    protected MoveWeighting.Argument determineNextMove(Field curField) {
        try {
            Field          field = (Field) curField.clone();    // clone a Field for preEvaluation
            Evaluation ev =
                new Evaluation(Selection.Selecting.best(),
                    new FieldWeighting(Selection.Selecting.best(),
                        new ChessFigureWeighting(this, Selection.Selecting.best(),
                            new ChessMoveWeighting(this))));
            ev.apply(field);

            Object eva = ev.evaluate();

            if (eva == null)
                return null;
            Pair arg = (Pair) Evaluation.getArg(eva);
            return (MoveWeighting.Argument) arg.B;
        } catch (CloneNotSupportedException err) {
            throw new InternalError("panic");
        } 
    } 
}


/**
 * used to choose which of our figures to move.
 */
final class ChessFigureWeighting extends FigureWeighting implements Function /* <Object, Number> */ {
    protected Computer computer;
    public ChessFigureWeighting(Computer c, Selection sel, Function /* <Object, Number> */ w) {
        super(sel, w);
        this.computer = c;
    }
    public Object apply(Object arg) {
        Argument i = (Argument) arg;
        Computer.logger.log(Level.FINER, " SF:weighting ... ( {0} -->", i);

        // we only move our figures, forget about moving opponents
        if (i.figure.getLeague() != ((ChessField) i.field).getTurn())
            return new Double(Double.NaN);
        double val = ((Number) super.apply(arg)).doubleValue();

        if (i.figure.getType() != ChessRules.KING)
            val += computer.aspects.offensiveFigure;

        Computer.logger.log(Level.FINER, " SF:  ) is weighted to {0}", new Double(val));
        return new Double(val);
    } 
}

/**
 * used to choose which move to take.
 */
final class ChessMoveWeighting extends MoveWeighting implements Function /* <Object, Number> */ {
    protected Computer computer;
    public ChessMoveWeighting(Computer c) {
        this.computer = c;
    }
    public Object apply(Object arg) {
        try {
            Argument    i = (Argument) arg;
            double      val = ((Number) super.apply(arg)).doubleValue();
            ChessFigure f = (ChessFigure) i.figure;

            Computer.logger.log(Level.FINER, "    SM: --> {0}...", i.destination);

            int targetLeague = i.field.isBeating(i.move, i.destination);
            if (targetLeague == i.figure.getLeague())
                return new Double(Double.NaN);    // disallow, only beat foreigners not cover allies
            if (targetLeague != Figure.NOONE)    // beating aspect for current move
                val += computer.aspects.beatingEnemies;

            // here you can evaluate the result further. Omitted for simplicity

            // storming aspect of marching figures
            if (f.stormFront(i.destination) > 0)
                val += f.stormFront(i.destination) * (f.front() > 4 ? 1 : 0.6) * computer.aspects.stormingOffensive;

            // bottom storm around run aspect
            val += i.destination.y * computer.aspects.topStorm;
            Computer.logger.log(Level.FINER, "    SM:  {0}|{1}: w={2}", new Object[] {new Integer(i.destination.x), new Integer(i.destination.y), new Double(val)});
            return new Double(val);
        } catch (ClassCastException err) {
            throw new InternalError("panic");
        } 
    } 
}
