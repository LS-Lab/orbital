/**
 * @(#)ArithmeticFormat.java 1.0 2000/09/26 Andre Platzer
 *
 * Copyright (c) 2000 Andre Platzer. All Rights Reserved.
 */

package orbital.math;

import java.text.Format;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.ParseException;
import java.util.Locale;

import orbital.math.functional.MathFunctor;

import java.text.NumberFormat;
import java.util.Iterator;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * ArithmeticFormat is responsible for formatting and parsing arithmetic objects.
 * <p>
 * With this instance of Format it is possible to format as well as parse the various
 * arithmetic objects.</p>
 * <p>
 * Although there is a {@link #getInstance(Locale)} method that is aware of a locale,
 * this class will most likely be instantiated in a locale-independent manner
 * using scientific mathematical notation (see {@link #getDefaultInstance()}).
 * Nevertheless ArithmeticFormat's formatting details can be configured
 * to fit different notation requirements.
 * </p>
 * <p>
 * You can adjust the output of pure real numbers of an ArithmeticFormat by modifying
 * the corresponding properties of its {@link #getNumberFormat()} instance.
 * However, to adjust the common format on the whole math system, use {@link MathUtilities#DefaultPrecisionDigits}, etc.
 * </p>
 *
 * @version 1.0, 2000/09/26
 * @author  Andr&eacute; Platzer
 * @todo test
 * @see NumberFormat#INTEGER_FIELD
 * @see NumberFormat#FRACTION_FIELD
 * @see NumberFormat
 */
public class ArithmeticFormat extends Format {
    private static class Debug {
	private Debug() {}
	//@fixme class Debug produces an error with gjc error: type parameter orbital.math.Arithmetic[] is not within its bound orbital.math.Arithmetic
	public static void main(String arg[]) throws Exception {
	    Matrix M = Values.valueOf(new Arithmetic[][] {
		{Values.valueOf(2), Values.complex(3, 4), Values.rational(-1, 2)},
		{Values.valueOf(3.12), Values.rational(1, 2), Values.valueOf(-1)},
		{Values.rational(-1, 2), Values.valueOf(0), Values.rational(1)}
	    });
	    Vector v = Values.valueOf(new Arithmetic[] {
		Values.valueOf(1), Values.rational(-1, 3), Values.rational(1, 2)
	    });
	    System.out.println(M + "*" + v + "=" + M.multiply(v));
	    ArithmeticFormat f = ArithmeticFormat.getDefaultInstance();
	    System.out.println("Type arithmetic examination object to parse (finish with Esc or Ctrl-Z or '#')");
	    String n = "";
	    while (true) {
		int ch = System.in.read();
		if (ch == -1 || ch == 0x1b || ch == '#')
		    break;
		n += (char) ch;
	    } 
	    System.out.println("From '"+n+"'");
	    Arithmetic s = f.parse(n);
	    System.out.println("Parsed\n"+s);
	    System.out.println("Understood?");
	    assert f.parse(s.toString()).equals(s) : "re-parse of representation of an object should equal that object " + n;
	    System.out.println("And various cases:");
	    String test[][] = new String[][] {
		{"1","-1","i","-i"},
		{"1+i","i+1","1-i","-i+1","-1+i","i-1","-1-i","-i-1"},
		{"2+3*i","2+i*3","3*i+2","i*3+2","2+3i","2+i*3","3i+2","i*3+2"},
		{"2-3*i","2-i*3","-3*i+2","-i*3+2","2-3i","2-i*3","-3i+2","-i*3+2"},
		{"-2+3*i","-2+i*3","3*i-2","i*3-2","-2+3i","-2+i*3","3i-2","i*3-2"},
		{"-2-3*i","-2-i*3","-3*i-2","-i*3-2","-2-3i","-2-i*3","-3i-2","-i*3-2"},
		{"2+-3*i","2+i*-3","-3*i+2","i*-3+2","2+-3i","2+i*-3","-3i+2","i*-3+2"}};
	    for (int i=0;i<test.length;i++) {
		for (int j=0;j<test[i].length;j++) {
		    Arithmetic value = f.parse(test[i][j]);
		    System.out.println(test[i][j] + "\t== " + value + ",");
		    assert f.parse(value.toString()).equals(value) : "re-parse of representation of an object should equal that object " + test[i][j];
		}
		System.out.println();
	    }
	}
    }	// Debug


    private static final Logger logger = Logger.getLogger(ArithmeticFormat.class.getPackage().getName());
    private static final long serialVersionUID = 4708045695735837065L;
	
    // partial FieldPosition identifiers (not applicable for all arithmetic objects)
	
    //TODO: general number field? public static final int NUMBER_FIELD = 2;
    public static final int NUMERATOR_FIELD = 2;
    public static final int DENOMINATOR_FIELD = 3;
    public static final int REAL_FIELD = 4;
    public static final int IMAGINARY_FIELD = 5;
    public static final int SYMBOL_FIELD = 10;
	
    /**
     * Inner number format of corresponding locale used to format pure real numbers.
     * @serial
     */
    private NumberFormat numberFormat;
	
    // format configuration and customization data
	
    private String rationalSeparator			= "/";

    // real [<complexPositiveSeparator> | <complexNegativeSeparator>] <complexUnit> <complexUnitSeparator> imaginary
    // or: real [<complexPositiveSeparator> | <complexNegativeSeparator>] imaginary <complexUnitSeparator> <complexUnit> 
    private String complexPositiveSeparator		= "+";
    private String complexNegativeSeparator		= "-";
    private boolean complexUnitLast				= false;
    private String complexUnit 					= "i";
    private String complexUnitSeparator 		= "*";
    private boolean complexAbbreviateNullReal	= true;
    private boolean complexAbbreviateNullImaginary= true;
    private boolean complexAbbreviateOne		= true;
    private boolean complexAbbreviateNullRealPositiveSeparator = true;

    // <vectorPrefix> el0 <vectorSeparator> el1 <vectorSeparator> ... el(n-1) <vectorSuffix> 
    private String vectorPrefix					= "(";
    private String vectorSeparator				= ",";
    // alternatives are accepted for parsing, only, but not used for output formatting
    private String vectorSeparatorAlternatives[]= {"\t", "|"};
    private String vectorSuffix					= ")";

    // <matrixPrefix>
    // <matrixRowPrefix> el0,0 <matrixSeparator> el0,1 <matrixSeparator> ... el0,(m-1) <matrixRowSuffix>
    // <matrixRowPrefix> el1,0 <matrixSeparator> el1,1 <matrixSeparator> ... el1,(m-1) <matrixRowSuffix>
    // ...
    // <matrixSuffix> 
    private String matrixPrefix					= "";
    private String matrixSeparator				= ",\t";
    private String matrixSeparatorAlternatives[]= {",", "\t"};
    private String matrixSuffix					= "";
    private String matrixRowPrefix				= "[";
    private String matrixRowSeparator			= System.getProperty("line.separator");
    // alternatives are accepted for parsing, only, but not used for output formatting //@xxx " " will not allow multiple spaces as separator
    private String matrixRowSeparatorAlternatives[] = {";", " ", "\t", ""};
    private String matrixRowSuffix				= "]";
	
    //@todo improve syntax (and make it more flexible for c1 and c0
    // <polynomialPrefix> cn <polynomialTimesOperator> <polynomialVariable> <polynomialPowerOperator> n (<polynomialPlusOperator>|<polynomialPlusAlternative>) ... c2 <polynomialTimesOperator> <polynomialVariable> <polynomialPowerOperator> 2 (<polynomialPlusOperator>|<polynomialPlusAlternative>)<polynomialSuffix> c1 <polynomialTimesOperator> <polynomialVariable> (<polynomialPlusOperator>|<polynomialPlusAlternative>)<polynomialSuffix> c0 <polynomialSuffix> 
    private String polynomialPrefix					= "";
    private String polynomialTimesOperator = "";
    private String polynomialVariable = "X";
    private String polynomialPowerOperator = "^";
    private String polynomialPlusOperator = "+";
    private String polynomialPlusAlternative = "-";
    private String polynomialSuffix					= "";

    public String getPolynomialVariable() {
	return polynomialVariable;
    }

    /**
     * Set the denotation of the formal parameter of polynomials.
     * @param polynomialVariable the string that is used to denote the formal parameter X of R[X].
     */
    public void setPolynomialVariable(String polynomialVariable) {
	this.polynomialVariable = polynomialVariable;
    }

    /**
     * Create a new arithmetic formatter for a specific locale.
     */
    public ArithmeticFormat(Locale locale) {
	//@xxx this numberFormat instance cannot format 17*10^50 and perhaps not even 1.23456789e-10
	numberFormat = NumberFormat.getNumberInstance(locale);
	numberFormat.setGroupingUsed(false);
	numberFormat.setMaximumFractionDigits(MathUtilities.getDefaultPrecisionDigits());
    }

    /**
     * Get the inner number format used to format pure real numbers.
     */
    public NumberFormat getNumberFormat() {
	return numberFormat;
    }

    /**
     * Set the inner number format used to format pure real numbers.
     */
    protected void setNumberFormat(NumberFormat newNumberFormat) {
	this.numberFormat = newNumberFormat;
    }
	
    /**
     * Get a formatted string representation of an arithmetic object.
     * {@inheritDoc}
     */
    public String format(Arithmetic obj) {
        return format(obj, new StringBuffer(), new FieldPosition(0)).toString();
    }

    /**
     * Specialization of format.
     */
    public String format(Scalar obj) {
        return format(obj, new StringBuffer(), new FieldPosition(0)).toString();
    }

    public StringBuffer format(Object obj, StringBuffer result, FieldPosition fieldPosition) {
	if (obj == null)
	    return new StringBuffer("null");
	if (obj instanceof Arithmetic)
	    return format((Arithmetic)obj, result, fieldPosition);
	else
            throw new IllegalArgumentException("Cannot format given Object as an arithmetic object: " + obj.getClass());
    }

    public StringBuffer format(Arithmetic obj, StringBuffer result, FieldPosition fieldPosition) {
	if (obj == null)
	    return new StringBuffer("null");
	if (obj instanceof Scalar)
	    return format((Scalar) obj, result, fieldPosition);
	else if (obj instanceof Vector)
	    return format((Vector) obj, result, fieldPosition);
	else if (obj instanceof Matrix)
	    return format((Matrix) obj, result, fieldPosition);
	else if (obj instanceof Polynomial)
	    return format((Polynomial) obj, result, fieldPosition);
	else if (obj instanceof Symbol)
	    return format((Symbol) obj, result, fieldPosition);
	else if (obj instanceof Fraction)
	    return format((Fraction) obj, result, fieldPosition);
	else if (obj instanceof Quotient)
	    return format((Quotient) obj, result, fieldPosition);
	else if (obj instanceof MathFunctor)
	    return format((MathFunctor) obj, result, fieldPosition);
	else
            throw new IllegalArgumentException("Cannot format given Object as an arithmetic object: " + obj.getClass());
    }

    /**
     * Specialization of format, formatting scalar objects as they please.
     */
    public StringBuffer format(Scalar obj, StringBuffer result, FieldPosition fieldPosition) {
	if (Complex.hasType.apply(obj))
	    return format((Complex) obj, result, fieldPosition);
	else if (Real.hasType.apply(obj))
	    return format((Real) obj, result, fieldPosition);
	else if (Rational.hasType.apply(obj))
	    return format((Rational) obj, result, fieldPosition);
	else if (Integer.hasType.apply(obj))
	    return format((Integer) obj, result, fieldPosition);
	else
	    throw new IllegalArgumentException("Cannot format given Object as an arithmetic object");
    }
	
    /**
     * Specialization of format.
     */
    public StringBuffer format(Complex v, StringBuffer result, FieldPosition fieldPosition) {
	if (!Complex.hasType.apply(v))
	    return format((Scalar)v, result, fieldPosition);
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

	if (fieldPosition.getField() == REAL_FIELD)
	    fieldPosition.setBeginIndex(result.length());
		
	Real re = v.re();
	Real im = v.im();
	if (!re.equals(Values.ZERO) || (complexAbbreviateNullReal && im.equals(Values.ZERO)))
	    format(re, result, fieldPosition);

	if (fieldPosition.getField() == REAL_FIELD)
	    fieldPosition.setEndIndex(result.length());
	else if (fieldPosition.getField() == IMAGINARY_FIELD)
	    fieldPosition.setBeginIndex(result.length());

	if (!(complexAbbreviateNullImaginary && im.equals(Values.ZERO))) {
	    boolean negative = im.compareTo(Values.ZERO) < 0;
	    if (negative) {
		im = (Real) im.minus();
		result.append(complexNegativeSeparator);
	    } else if (!(complexAbbreviateNullRealPositiveSeparator && re.equals(Values.ZERO)))
		result.append(complexPositiveSeparator);
	    if (!complexUnitLast)
		result.append(complexUnit);
	    if (!(complexAbbreviateOne && (im.norm().equals(Values.ONE)))) {
		if (!complexUnitLast)
		    result.append(complexUnitSeparator);
		format(im, result, fieldPosition);
		if (complexUnitLast)
		    result.append(complexUnitSeparator);
	    }
	    if (complexUnitLast)
		result.append(complexUnit);
	}

	if (fieldPosition.getField() == IMAGINARY_FIELD)
	    fieldPosition.setEndIndex(result.length());

	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Real v, StringBuffer result, FieldPosition fieldPosition) {
	if (!Real.hasType.apply(v))
	    return format((Scalar)v, result, fieldPosition);
	return numberFormat.format(v.doubleValue(), result, fieldPosition);
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Rational v, StringBuffer result, FieldPosition fieldPosition) {
	if (!Rational.hasType.apply(v))
	    return format((Scalar)v, result, fieldPosition);
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

	if (fieldPosition.getField() == NUMERATOR_FIELD || fieldPosition.getField() == REAL_FIELD)
	    fieldPosition.setBeginIndex(result.length());

	numberFormat.format(v.numerator(), result, fieldPosition);

	if (fieldPosition.getField() == NUMERATOR_FIELD)
	    fieldPosition.setEndIndex(result.length());
	else if (fieldPosition.getField() == DENOMINATOR_FIELD)
	    fieldPosition.setBeginIndex(result.length());

	if (logger.isLoggable(Level.FINER) || !v.denominator().equals(Values.ONE)) {
	    result.append(rationalSeparator);
	    numberFormat.format(v.denominator(), result, fieldPosition);
	}

	if (fieldPosition.getField() == DENOMINATOR_FIELD || fieldPosition.getField() == REAL_FIELD)
	    fieldPosition.setEndIndex(result.length());

	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Integer v, StringBuffer result, FieldPosition fieldPosition) {
	if (!Integer.hasType.apply(v))
	    return format((Scalar)v, result, fieldPosition);
	return numberFormat.format(v.longValue(), result, fieldPosition);
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Vector v, StringBuffer result, FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);
		
	result.append(vectorPrefix);
	for (Iterator i = v.iterator(); i.hasNext(); ) {
	    format(i.next(), result, fieldPosition);
	    if (i.hasNext())
		result.append(vectorSeparator);
	}
	result.append(vectorSuffix);

	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Matrix v, StringBuffer result, FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);

	result.append(matrixPrefix);
	for (int i = 0; i < v.dimension().height; i++) {
	    if (i != 0)
		result.append(matrixRowSeparator);
	    result.append(matrixRowPrefix);
	    for (int j = 0; j < v.dimension().width; j++) {
		if (j != 0)
		    result.append(matrixSeparator);
		format(v.get(i, j), result, fieldPosition);
	    }
	    result.append(matrixRowSuffix);
	} 
	result.append(matrixSuffix);

	return result;
    }

    /**
     * Specialization of format.
     * @todo provide a parser
     */
    public StringBuffer format(Polynomial p, StringBuffer result, FieldPosition fieldPosition) {
        fieldPosition.setBeginIndex(0);
        fieldPosition.setEndIndex(0);
		
	// @todo improve format
	result.append(polynomialPrefix);
	// we call the degree of 0 deg(0)=0, here 
	for (int i = Math.max(p.degreeValue(),0); i >= 0; i--) {
	    Arithmetic ci = p.get(i);
	    // only print nonzero elements (but print the 0-th coefficient if it is the only one)
	    if (!ci.norm().equals(Values.ZERO)
		|| (i == 0 && result.length() == 0)) {
		int startIndex = result.length();
		format(ci, result, fieldPosition);
		// separator for all but the first coefficient,
		// provided that there is not already an alternative separator
		if (i < p.degreeValue() &&
		    !(result.length() > startIndex && result.substring(startIndex).startsWith(polynomialPlusAlternative)))
		    result.insert(startIndex, polynomialPlusOperator);
		if (i != 0)
		    result.append(polynomialTimesOperator + polynomialVariable + (i > 1 ? polynomialPowerOperator + i : ""));
	    }
	}
	result.append(polynomialSuffix);

	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Quotient q, StringBuffer result, FieldPosition fieldPosition) {
	result.append(q.representative().toString());
	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Fraction as, StringBuffer result, FieldPosition fieldPosition) {
	result.append("(" + as.numerator() + ") / (" + as.denominator() + ")");
	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(Symbol s, StringBuffer result, FieldPosition fieldPosition) {
	if (fieldPosition.getField() == SYMBOL_FIELD)
	    fieldPosition.setBeginIndex(result.length());
	result.append(s.toString());
	if (fieldPosition.getField() == SYMBOL_FIELD)
	    fieldPosition.setEndIndex(result.length());
	return result;
    }

    /**
     * Specialization of format.
     */
    public StringBuffer format(MathFunctor f, StringBuffer result, FieldPosition fieldPosition) {
	//@TODO: update fieldPosition
	return result.append("" + f);
	//@XXX: how about return orbital.logic.functor.Notation.DEFAULT.format(f, null);
    }


    /**
     * Parse an arithmetic object string representation.
     * {@inheritDoc}
     */
    public Arithmetic parse(String source, ParsePosition status) {
	int initialIndex = status.getIndex();
	try {
	    do {
		// Matrix
		if (source.startsWith(matrixPrefix, status.getIndex())
		    && source.startsWith(matrixRowPrefix, status.getIndex() + matrixPrefix.length())) {
		    status.setIndex(status.getIndex() + matrixPrefix.length());
		    /* maximum column width */
		    int	 colWidth = 0;
		    List rows = new LinkedList();
		    List col = new LinkedList();
		    while (found(matrixRowPrefix, source, status)) {
			Arithmetic el;
			while ( (el = parse(source, status)) != null) {
			    col.add(el);
			    if (!found(matrixSeparator, source, status) && !found(matrixSeparatorAlternatives, source, status))
				break;
			}
			consume(matrixRowSuffix, source, status);
			rows.add(col);
			if (col.size() > colWidth)
			    colWidth = col.size();
			col = new ArrayList(colWidth);
			if (!found(matrixRowSeparator, source, status) && !found(matrixRowSeparatorAlternatives, source, status))
			    break;
		    }
		    consume(matrixSuffix, source, status);
		    Matrix v = Values.getInstance(rows.size(), colWidth);
		    for (int i = 0; i < v.dimension().height; i++) {
			col = (List) rows.get(i);
			for (int j = 0; j < v.dimension().width; j++) {
			    v.set(i, j, j < col.size() ? (Arithmetic) col.get(j) : Values.ZERO);
			} 
		    } 
		    return v;
		}
        
		// Vector
		else if (found(vectorPrefix, source, status)) {
		    List components = new LinkedList();
		    Arithmetic el;
		    while ( (el = parse(source, status)) != null) {
			components.add(el);
			if (!found(vectorSeparator, source, status) && !found(vectorSeparatorAlternatives, source, status))
			    break;
		    }
		    consume(vectorSuffix, source, status);
		    Vector v = Values.getInstance(components.size());
		    for (int i = 0; i < v.dimension(); i++)
			v.set(i, (Arithmetic) components.get(i));
		    return v;
		}
        
		// single Scalar
    
		//TODO: limit preview via upto the next "delimiter" like one of ",\t)" and so on.
		// Will limit useless lookahead as in (3,4,5,2/9) all will first be parsed as a rational
        		
		// Complex
		else if (source.indexOf(complexUnit, status.getIndex()) >= 0) {
		    int fallbackIndex = status.getIndex();
		    try {
			//TODO: improve source code
			Real re = null, im = null;
			boolean imaginaryPart = false;
			int sign = 1;
			Real val = null;
			while ((re == null || im == null) && status.getIndex() < source.length()) {
			    // whether there is  i*  so we would accept a number following
			    boolean allowUnitNumberSuffix = false;
			    // collect values
			    if (val == null)
				val = realValueOf(numberFormat.parse(source, status));
                		
                            // collect additional information
			    //(!) VERY tricky boolean condition with side-effects!
			    if (!imaginaryPart && (
						   // a[*]i
						   (val != null && found(complexUnitSeparator, source, status) && found(complexUnit, source, status))
						   ||		// i[*]a
						   (found(complexUnit, source, status) | (allowUnitNumberSuffix = found(complexUnitSeparator, source, status)))
						   )) {
				if (im != null)
				    throw new NumberFormatException("no two imaginary parts");
				else
				    imaginaryPart = true;
			    }
                			
			    // collect values
			    if (allowUnitNumberSuffix && val == null)
				val = realValueOf(numberFormat.parse(source, status));
                		
			    // use values
			    if (val != null) {
				val = Values.valueOf(sign * val.doubleValue());
				if (imaginaryPart)
				    if (im == null)
					im = val;
				    else
					throw new NumberFormatException("no two imaginary parts");
				else
				    if (re == null)
					re = val;
				    else
					throw new NumberFormatException("no two real parts");
				val = null;
				imaginaryPart = false;
				sign = 1;
			    }
			    // non-value part
			    else if (found("+", source, status)) {
				assert val == null : "else-case";
				if (im == null && imaginaryPart) {
				    im = Values.valueOf(sign * 1);
				}
				sign = 1;
				imaginaryPart = false;
			    } else if (found("-", source, status)) {
				assert val == null : "else-case";
				if (im == null && imaginaryPart) {
				    im = Values.valueOf(sign * 1);
				}
				sign = -1;
				imaginaryPart = false;
			    } else if (status.getIndex() < source.length())
				break;
			}
			// process i and -i
			if (im == null && imaginaryPart) {
			    assert val == null : "";
			    im = Values.valueOf(sign * 1);
			}
        
			if (im == null)
			    throw new NumberFormatException("real value does not need to be parsed as a complex");
			else if (re == null)
			    return Values.complex(Values.ZERO, im);
			else
			    return Values.complex(re, im);
		    }
		    catch(NumberFormatException trial) {
			status.setIndex(fallbackIndex);
		    }
            	}
    
		// Rational
		if (source.indexOf(rationalSeparator, status.getIndex()) >= 0) {
		    int fallbackIndex = status.getIndex();
		    try {
			NumberFormat f = (NumberFormat) numberFormat.clone();
			f.setParseIntegerOnly(true);
			Integer numerator = (Integer) Values.valueOf(f.parse(source, status));
			if (numerator == null)
			    throw new NumberFormatException("numerator expected");
			if (!found(rationalSeparator, source, status))
			    throw new NumberFormatException("'" + rationalSeparator + "' expected");
			Integer denominator = (Integer) Values.valueOf(f.parse(source, status));
			if (denominator == null)
			    throw new NumberFormatException("denominator expected");
			return Values.rational(numerator, denominator);
		    }
		    catch(NumberFormatException trial) {
			status.setIndex(fallbackIndex);
		    }
            	}
    
		// Real or Integer
		Number n = numberFormat.parse(source, status);
		//@todo parse Real.Big and Integer.Big as well
		if (n != null)
		    return Values.narrow(Values.valueOf(n));
    		
		// skip any whitespaces not yet recognized
		if (Character.isWhitespace(source.charAt(status.getIndex())))
		    status.setIndex(status.getIndex() + 1);
		else
		    break;
	    } while (true);

	    // Symbol identifier [a-zA-Z][a-zA-Z0-9]*
	    //TODO:
	    return null;
	}
	catch(ExpectationHurtParseException invalid) {
	    status.setIndex(initialIndex);
	    return null;
	}			
    }

    public Object parseObject(String source, ParsePosition status) {
	return parse(source, status);
    }

    /**
     * Parse an arithmetic object string representation.
     * {@inheritDoc}
     */
    public Arithmetic parse(String source) throws ParseException {
        ParsePosition status = new ParsePosition(0);
        Arithmetic result = parse(source, status);
        if (status.getIndex() == 0) {
            throw new ParseException("ArithmeticFormat.parse(String) failed at " + status + " '" + source.charAt(status.getErrorIndex()) + "'", status.getErrorIndex());
        }
        //@todo shouldn't we check whether all (non-whitespace) characters have been parsed? But src.jar says different
        return result;
    }

    /**
     * Consume an expected value, or fail.
     * @return whether the expected value was the next token in source.
     *  If true, the parse position will already have been advanced.
     *  If false, the parse position will have a correct error index set
     *  and an exception is thrown.
     */
    private final void consume(String expectedValue, String source, ParsePosition status) throws ExpectationHurtParseException {
	if (!found(expectedValue, source, status)) {
	    status.setErrorIndex(status.getIndex());
	    throw new ExpectationHurtParseException("'" + expectedValue + "' expected", status.getErrorIndex());
	}
    }

    /**
     * Checks whether we found a value.
     * @return whether the value was the next token in source.
     *  If true, the parse position will already have been advanced.
     *  If false, the parse position will not have changed.
     */
    private final boolean found(String value, String source, ParsePosition status) {
	if (source.startsWith(value, status.getIndex())) {
	    status.setIndex(status.getIndex() + value.length());
	    return true;
	} else
	    return false;
    }

    /**
     * Checks whether we found one of several alternative values.
     * @param values an array containing the alternative values.
     *  Processed in the given order.
     *  Note that if an optional specifier ("") is contained in this array it should always
     *  be the last element.
     * @return whether one of the alternative values was the next token in source.
     *  If true, the parse position will already have been advanced.
     *  If false, the parse position will not have changed.
     */
    private final boolean found(String values[], String source, ParsePosition status) {
	for (int i = 0; i < values.length; i++)
	    if (found(values[i], source, status))
		return true;
	return false;
    }
	
    //@todo introduce for JDK1.4 public AttributedCharacterIterator formatToCharacterIterator(Object obj)
	
    /**
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static ArithmeticFormat getInstance(Locale locale) {
	return new ArithmeticFormat(locale);
    }

    /**
     * Returns the default number format for the current default locale.
     * @see #getDefaultInstance()
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     */
    public static ArithmeticFormat getInstance() {
	return new ArithmeticFormat(Locale.getDefault());
    }

    /**
     * Default instance of format for use in toString methods in this package.
     */
    private static final ArithmeticFormat defaultFormat = getInstance(Locale.ENGLISH);
	
    /**
     * Get the default instance of format that does scientific mathematical formatting.
     * Used in {@link Object#toString()} methods in this package.
     * @see java.text.NumberFormat#getScientificInstance()
     * @see <a href="{@docRoot}/DesignPatterns/FacadeFactory.html">&quot;FacadeFactory&quot;</a>
     * @todo rename to getMathematicalInstance() or getScientificInstance()?
     */
    public static final ArithmeticFormat getDefaultInstance() {
	return defaultFormat;
    }
    /*static final ArithmeticFormat getScientificInstance() {
		
    }*/
	
    private static final Real realValueOf(Number number) {
	return number == null ? null : Values.valueOf(number.doubleValue());
    }
}

class ExpectationHurtParseException extends ParseException {
    private static final long serialVersionUID = -6851188252624548071L;
    public ExpectationHurtParseException(String s, int errorOffset) {
	super(s, errorOffset);
    }
}
