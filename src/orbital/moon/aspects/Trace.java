/*

Copyright (c) Xerox Corporation 1998, 1999, 2000.  All rights reserved.

Use and copying of this software and preparation of derivative works based
upon this software are permitted.  Any distribution of this software or
derivative works must comply with all applicable United States export control
laws.

This software is made available AS IS, and Xerox Corporation makes no warranty
about the software, its performance or its conformity to any specification.

|<---            this code is formatted to fit into 80 columns             --->|
|<---            this code is formatted to fit into 80 columns             --->|
|<---            this code is formatted to fit into 80 columns             --->|

*/

package orbital.moon.aspects;

import java.io.PrintStream;

/**
 *
 * This class provides support for printing trace messages into a stream. 
 * It defines 3 abstract crosscuts for injecting that tracing functionality 
 * into any constructions and other events of any application classes.
 *
 */
abstract aspect Trace {

  /*
   * Functional part
   */

  /**
   * There are 3 trace levels (values of TRACELEVEL):
   * 0 - No messages are printed
   * 1 - Trace messages are printed, but there is no indentation 
   *     according to the call stack
   * 2 - Trace messages are printed, and they are indented
   *     according to the call stack
   */
  public static int TRACELEVEL = 0;
  protected static PrintStream stream = null;
  protected static int callDepth = 0;

  /**
   * Initialization.
   */
  public static void initStream(PrintStream s) {
    stream = s;
  }

  protected static void traceEntry(String str) {
    if (TRACELEVEL == 0) return;
    if (TRACELEVEL == 2) callDepth++;
    printEntering(str);
  }

  protected static void traceExit(String str) {
    if (TRACELEVEL == 0) return;
    printExiting(str);
    if (TRACELEVEL == 2) callDepth--;
  }

  private static void printEntering(String str) {
    printIndent();
    stream.println("Entering " + str);
  }

  private static void printExiting(String str) {
    printIndent();
    stream.println("Exiting " + str);
  }


  private static void printIndent() {
    for (int i = 0; i < callDepth; i++)
      stream.print("  ");
  }


  /*
   * Crosscut part
   */

  /**
   * Application classes - left unspecified.
   * Subclasses should concretize this pointcut with class names.
   */
  abstract pointcut classes();
  /**
   * Other events to trace - left unspecified.
   */
  abstract pointcut otherEvents();

  before(): classes() && otherEvents() {
      traceEntry(thisJoinPoint.className + "." + thisJoinPoint.methodName);
  }
  after(): classes() && otherEvents() {
      traceExit(thisJoinPoint.className + "." + thisJoinPoint.methodName);
  }

}
