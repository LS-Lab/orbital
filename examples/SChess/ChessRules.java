import orbital.robotic.*;
import orbital.game.*;
import orbital.logic.functor.Function;
import orbital.util.InnerCheckedException;
import java.applet.Applet;
import java.awt.*;
import java.util.*;

public class ChessRules extends AbstractGameRules implements Cloneable {

    // Leagues
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int LEAGUES = WHITE + 1;

    // possible figure types
    public static final int KING = 1;
    public static final int PAWN = 2;
    public static final int FIGURES = PAWN + 1;


    private static final Move[][] legalMoves = stringToMove(new String[][] {
        {""},
        {"F_*","/LF_*","L_*","/LB_*","B_*","/RB_*","R_*","/RF_*"},
        {"F","/LF*","/RF*"}
    });

    /**
     * Which league is our's, i.e. for whom we started evaluating hypotheses in order to choose
     * a move.
     * May differ from the current league turn making the next move.
     */
    private int ourLeague = Figure.NOONE;

    public ChessRules() {
	super("F", ".gif");
    }

    public Object clone() {
    	try {
	    return super.clone();
    	}
    	catch (CloneNotSupportedException imp) {throw new Error("Cloneable cannot be cloned?");}
    }

    /**
     * Get our league that is currently thinking which figure to move.
     * All heuristic evaluations take the perspective of our league.
     */
    public int getOurLeague() {
    	return ourLeague;
    }
    /**
     * Set our league that is currently thinking which figure to move.
     * All heuristic evaluations take the perspective of our league.
     */
    protected void setOurLeague(int league) {
    	this.ourLeague = league;
    }
    // convenience methods
    /**
     * Get our league that is currently thinking which figure to move.
     * All heuristic evaluations take the perspective of our league.
     */
    public static int getOurLeague(Field field) {
    	return ((ChessField) field).rules.getOurLeague();
    }


    // GameRules implementation
    public Function startAIntelligence(String arg) {
	return "old".equalsIgnoreCase(arg)
	    ? (Computer) new WeightingComputer()
	    : (Computer) new UtilityComputer();
    } 
    public Field startField(Component comp) {
	final ChessField	  field = new ChessField(this, 8, 8);
	Dimension dim = field.getDimension();
	loadAllImages(comp);

	for (int h = 0; h < dim.height; h++)
	    for (int w = 0; w < dim.width; w++) {
		int	   leag = getLeag(w, h);
		int	   typ = getTyp(w, h);
		Figure f = newFigure(w, h, leag, typ);
		if (f.getLeague() == WHITE)
		    f.setDirection(new Direction(Direction.West));
		field.setFigure(new Position(w, h), f);
	    } 
	// commence
	setTurn(ChessRules.BLACK);
	field.addFieldChangeListener(new FieldChangeAdapter() {
		public void stateChanged(FieldChangeEvent evt) {
		    if (evt.getType() == FieldChangeEvent.END_OF_TURN) {
			int winner = checkWinner(field);
			if (winner != Figure.NOONE)
			    field.mygetFieldChangeMulticaster().stateChanged(new FieldChangeEvent(field, FieldChangeEvent.END_OF_GAME, new Integer(winner)));
		    }
		}
	    });
	return field;
    } 
    public int getLeagues() {
	return LEAGUES;
    } 
    public int getFigureTypes() {
	return FIGURES;
    } 

    public int checkWinner(Field field) {
    	final int done = turnDoneImpl(field);
    	if (done != NOONE) {
	    System.out.println((done == BLACK ? "BLACK" : "WHITE") + " has won");
	    ((ChessField) field).setEnd(true);
    	}
    	return done;
    }
    static int turnDoneImpl(Field field) {
	if (!leagueExists(WHITE, field))
	    return BLACK;
	else if (!leagueExists(BLACK, field))
	    return WHITE;
	else if (!hasKing(WHITE, field))
	    return BLACK;
	else if (!hasKing(BLACK, field))
	    return WHITE;
	else if (figuresReached(WHITE, field))
	    return WHITE;
	else if (figuresReached(BLACK, field))
	    return BLACK;
	else
	    return NOONE;
    } 

    private static boolean leagueExists(int league, Field field) {
	for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
	    if (((Figure) i.next()).getLeague() == league)
		return true;
	} 
	return false;
    } 

    private static boolean hasKing(int league, Field field) {
	for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
	    Figure f = (Figure) i.next();
	    if (f.getLeague() == league && f.getType() == KING)
		return true;
	} 
	return false;
    } 

    /**
     * check whether a figure of the given league is at the winning position
     * after the other player has done his move.
     * If the other player did not even have time to react this method will simply return false.
     */
    private static boolean figuresReached(int league, Field field) {
	if (league != ((ChessField) field).getTurn())	// must have rest one turn. So only check when its up to opponents turn
	    return false;
	for (Iterator i = field.iterateNonEmpty(); i.hasNext(); ) {
	    Figure f = (Figure) i.next();
	    if (f.getLeague() != league)
		continue;
	    if ((f.getLeague() == BLACK && f.x == field.getDimension().width - 1)
		|| (f.getLeague() == WHITE && f.x == 0)) {
		return true;	// 2nd turn here already?
	    } 
	} 
	return false;
    } 

    // implementation helpers

    Figure newFigure(int x, int y, int leag, int typ) {
	FigureImpl f = new ChessFigure(x, y, leag, typ);
	f.setDirection(new Direction(Direction.East));
	f.setLegalMoves(legalMoves[typ]);
	f.setImage(getImage(f));
	return f;
    } 

    private static int getLeag(int w, int h) {
	return w == 0 || w == 1 ? BLACK : (w == 7 || w == 6 ? WHITE : NOONE);
    } 
    private static int getTyp(int w, int h) {
	if (w == 1 || w == 6)
	    return PAWN;
	if (w != 0 && w != 1 && w != 6 && w != 7)
	    return EMPTY;
	return h == 3 ? KING : EMPTY;
    } 
    public String toString() {
	return getClass().getName() + "[" + getTurn() + "]";
    }
}
