ORBITAL LIBRARY

The Orbital library is a Java class library providing object-oriented representations and algorithms for logic, mathematics, and computer science. This Java library provides computer algebra, numerical algorithms, theorem proving, search and planning etc. Generally speaking, the conceptual idea behind the Orbital library is to provide extensional services and components for Java, which surround the heart of many scientific applications. Hence the name Orbital library. Orbital library is designed with the goals of flexibility, conceptual simplicity and general applicability

ORBITAL LIBRARY ON THE WEB

  http://symbolaris.com/orbital/index.html
  
  https://github.com/LS-Lab/orbital                        


COPYRIGHT

Copyright and license are discussed in COPYRIGHT.txt


REQUIREMENTS

At run-time the Orbital library needs a standard Java Virtual
Machine. Some parts need JVM 1.4+, but most are satisfied with
1.2+. No additional libraries are required at run-time.

In order to build all parts of the Orbital library, examples, tests
and additional files, you may benefit from some additional software. See description
of build.xml for details.

INSTALLATION

Compiling the Orbital library involves the following steps:

  ant all
  
  ant test
  
If this does not work right out of the box, edit build.properties to configure the paths in your system

# specify system-specific paths to JavaCC, JLink and MathKernel below.
# Depending on your installation, you may also have to put ant-commons-net.jar and commons-net.jar into your CLASSPATH for ant
env.javacchome=/path/to/javacc
# for unit testing purposes only
junit.framework.jar=/path/to/junit.jar
com.wolfram.jlink.kernel=/path/to/MathKernel -mathlink
com.wolfram.jlink.jar=/path/to/Mathematica/SystemFiles/Links/JLink/JLink.jar
com.wolfram.jlink.libdir=/path/to/Mathematica/SystemFiles/Links/JLink/SystemFiles/Libraries/MacOSX


RELEASE HISTORY
The Orbital library experiences continuous development since 1996. The major milestones include (without minor releases like 1.1.3)

2009-03-05: First release of Orbital library version 1.3.0
2007-08-20: First release of Orbital library version 1.2.0
2003-02-21: First release of Orbital library version 1.1.0
2000-06-22: First release of Orbital library version 1.0.0
1998-11-08: First public stable release 0.9 of the Orbital library.