import orbital.math.*;
import orbital.math.Integer;
import orbital.math.functional.Operations;

/**
 * Application of Chinese Remainder Theorem
 * for simultaneously solving independent congruences.
 * <h3>Assignement (in german)</h3>
 * <blockquote>
 * Drei Karlsruher Studenten haben eine Kneipe entdeckt, die sie
 * regelm&auml;&szlig;ig besuchen. Der erste besuchte sie an einem
 * Montag zum ersten mal, der zweite am darauffolgenden Tag und der
 * Dritte am darauffolgenden Donnerstag. Da sich alle drei in
 * gemeinn&uuml;tzigen Or­ ganisationen einbringen, k&ouml;nnen sie
 * leider nicht t&auml;glich die Kneipe besuchen. So kann der erste
 * nur jeden dritten, der Zweite nur jeden vierten und der Dritte nur
 * jeden f&uuml;nften Tag. Trotzdem trafen sie sich am 2. Advent
 * dieses Jahres zum ersten mal in dieser Kneipe und vereinbarten,
 * dass sie immer dann Skat spielen, wenn sie sich sonntags
 * treffen. Wann besuchte der erste Student zum ersten mal die Kneipe
 * und wann k&ouml;nnen sie das erste mal Skat spielen?
 * (from the 8. assignements in algebra I at the University of Karlsruhe on 2001-12-7
 *   by F. Herrlich and H. Utz)
 * </blockquote>
 * <h3>Solution</h3>
 * <p>
 * We identify the days of the week per
 * <table border="1">
 *  <tr>
 *    <th>Day of week</th>
 *    <th>Monday</th>
 *    <th>Tuesday</th>
 *    <th>Wednesday</th>
 *    <th>Thursday</th>
 *    <th>Friday</th>
 *    <th>Saturday</th>
 *    <th>Sunday</th>
 *  </tr>
 *  <tr>
 *    <th>Wochentag</th>
 *    <td>Montag</td>
 *    <td>Dienstag</td>
 *    <td>Mittwoch</td>
 *    <td>Donnerstag</td>
 *    <td>Freitag</td>
 *    <td>Samstag</td>
 *    <td>Sonntag</td>
 *  </tr>
 *  <tr>
 *    <th>Number</th>
 *    <td>0</td>
 *    <td>1</td>
 *    <td>2</td>
 *    <td>3</td>
 *    <td>4</td>
 *    <td>5</td>
 *    <td>6</td>
 *  </tr>
 * </table>
 * And then extract the congruences from the text
 * <table border="1">
 *  <tr>
 *    <th>Student</th>
 *    <th>First arrival</th>
 *    <th>Period of visits</th>
 *    <th>Congruence</th>
 *  </tr>
 *  <tr>
 *    <th>A</th>
 *    <td>0=Monday</td>
 *    <td>3</td>
 *    <td>x&#8801;0 (mod 3)</td>
 *  </tr>
 *  <tr>
 *    <th>B</th>
 *    <td>1=Tuesday</td>
 *    <td>4</td>
 *    <td>x&#8801;1 (mod 4)</td>
 *  </tr>
 *  <tr>
 *    <th>C</th>
 *    <td>3=Thursday</td>
 *    <td>5</td>
 *    <td>x&#8801;3 (mod 5)</td>
 *  </tr>
 *  <tr>
 *    <th>Play &quot;Skat&quot;</th>
 *    <td>6=Sunday</td>
 *    <td>7</td>
 *    <td>x&#8801;6 (mod 7)</td>
 *  </tr>
 * </table>
 * <p>
 * These congruences are independent, since
 * gcd(3,4)=1, gcd(3,5)=1, gcd(3,7)=1, gcd(4,5)=1, gcd(4,7)=1, and gcd(5,7)=1.
 * And then we solve the congruences with respect to x.
 * If we ignore the last congruence we solve the first question of when they first met.
 * If, however, we take into account the last congruence we solve the second question
 * of when they will first play "Skat".
 * </p>
 * @author Andr&eacute; Platzer
 * @version 1.0, 2002-08-13
 */
public class CRTApplication{
    public static void main(String[] args){
	// get us a value factory for creating arithmetic objects
	final Values vf = Values.getDefaultInstance();
	Integer x[] = {vf.valueOf(0), vf.valueOf(1), vf.valueOf(3)};
	Integer m[] = {vf.valueOf(3), vf.valueOf(4), vf.valueOf(5)};
	Integer umod = (Integer) Operations.product.apply(vf.valueOf(m));

	System.out.println("computing \"Anzahl Tage vor dem 2.Advent fuer erstes Treffen\"");
	System.out.println("computing \"number of days before 2.Advent for the first meeting\"");
	System.out.println("congruent values: " + MathUtilities.format(x));
	System.out.println("modulo values:    " + MathUtilities.format(m));
	System.out.println("solution:         " + AlgebraicAlgorithms.chineseRemainder(x,m));
	// print nonnegative normalized representation of the solution
	// (since the number of days is not negative)
	System.out.println("              (== " +
			   (((Integer)AlgebraicAlgorithms.chineseRemainder(x,m).representative()).intValue() + umod.intValue()) % umod.intValue() + ")");
	System.out.println("is unique modulo: " + umod);
	System.out.println();

	x = new Integer[] {vf.valueOf(0), vf.valueOf(1), vf.valueOf(3), vf.valueOf(6)};
	m = new Integer[] {vf.valueOf(3), vf.valueOf(4), vf.valueOf(5), vf.valueOf(7)};

	System.out.println("computing \"Anzahl Tage bis zum ersten Skatspiel\"");
	System.out.println("computing \"number of day to first game of \"Skat\"\"");
	System.out.println("congruent values: " + MathUtilities.format(x));
	System.out.println("modulo values:    " + MathUtilities.format(m));
	System.out.println("solution:         " + AlgebraicAlgorithms.chineseRemainder(x,m));
	System.out.println("is unique modulo: " + Operations.product.apply(vf.valueOf(m)));
    }
} // CRTApplication
