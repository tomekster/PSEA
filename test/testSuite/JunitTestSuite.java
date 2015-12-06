package testSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import core.SolutionTest;
import core.hyperplane.HyperplaneTest;
import core.hyperplane.ReferencePointTest;
import utils.ComparatorTest;
import utils.NonDominatedSortTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   HyperplaneTest.class,
   ReferencePointTest.class,
   ComparatorTest.class,
   SolutionTest.class,
   NonDominatedSortTest.class
})
public class JunitTestSuite {
}  	