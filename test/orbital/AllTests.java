package orbital;

import junit.framework.*;

/**
 * TestSuite that runs all the tests, of the Orbital library.
 */
public class AllTests {

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	TestSuite suite= new TestSuite("All " + AllTests.class.getPackage() + ".* Tests");
	//suite.addTest(orbital.algorithm.template.AllTests.suite());
	suite.addTest(orbital.math.AllTests.suite());
	suite.addTest(orbital.math.functional.AllTests.suite());
	suite.addTest(orbital.moon.logic.AllTests.suite());
	return suite;
    }
}
