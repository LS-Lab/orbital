package orbital.moon.logic;

import junit.framework.*;

/**
 * TestSuite that runs all the tests, here.
 *
 */
public class AllTests {

    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    public static Test suite() {
	TestSuite suite= new TestSuite("All " + AllTests.class.getPackage() + " Tests");
	suite.addTest(ClassicalLogicTest.suite());
	return suite;
    }
}
