package testSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import core.hyperplane.HyperplaneTest;
import core.hyperplane.ReferencePointTest;
import utils.ComparatorTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   HyperplaneTest.class,
   ReferencePointTest.class,
   ComparatorTest.class
})
public class JunitTestSuite {
}  	