import orbital.game.*;
import orbital.game.AdversarySearch.Option;
import orbital.robotic.*;

import java.util.*;
import orbital.util.Pair;

import orbital.math.MathUtilities;
import orbital.util.OutOfCheeseError;

/**
 * A ChessField is a field that knows with which ChessRules it is playing.
 */
public class ChessField extends Field {
    ChessRules rules;
    private boolean end = false;
    public ChessField(ChessRules rules, int w,int h) {
        super(w, h);
        this.rules = rules;
    }
    public ChessField() {}
    public Object clone() {
        try {
            ChessField r = (ChessField) super.clone();
            r.rules = rules;
            r.end = end;
            return r;
        } catch(CloneNotSupportedException imp) {
            imp.printStackTrace();
            throw new OutOfCheeseError();
        }
    }
    
    boolean isEnd() {
        return end;
    }
    void setEnd(boolean end) {
        this.end = end;
    }
    
    public void setTurn(int turn) {
        super.setTurn(turn);
    }
    /**
     * Made a move, so current turn done.
     */
    void doTurn() {
        int old_turn = getTurn();
        setTurn(old_turn == ChessRules.WHITE ? ChessRules.BLACK : ChessRules.WHITE);
        getFieldChangeMulticaster().stateChanged(new FieldChangeEvent(this, FieldChangeEvent.END_OF_TURN, new Integer(old_turn)));
    }

    FieldChangeListener mygetFieldChangeMulticaster() {
        return super.getFieldChangeMulticaster();
    }

    
    // optimized: only expand valid moves that stick to the rules
    public Iterator expand() { 
        if (isEnd())
            return Collections.EMPTY_LIST.iterator();
        // the single rules for all children after one turn //@xxx why can't we use = rules?
        ChessRules afterStepRules = (ChessRules) rules.clone();
        List r = new LinkedList();
        for (Iterator i = iterateNonEmpty(); i.hasNext(); ) {
            Figure figure = (Figure) i.next();
            if (figure.getLeague() != getTurn())
                // we only move our figures, forget about moving opponents
                continue;
            for (Iterator j = figure.possibleMoves(); j.hasNext(); ) {
                Option    opt = (Option) j.next();
                Move      move = opt.getMove();
                Position  destination = opt.getDestination();
                int targetLeague = isBeating(move, destination);
                if (targetLeague == figure.getLeague())
                    // disallow, only beat foreigners not allies
                    continue;
                ChessField field = (ChessField) clone();
                field.rules = (ChessRules) rules.clone();
                // optimized version could be introduced here as well
                field.move((Position) figure, move);
                field.rules = afterStepRules;

                r.add(new Option(field, destination, figure, move));
            }
        }
        return r.iterator();
    }
}
