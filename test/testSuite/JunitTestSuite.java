package testSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import core.NSGAIIITest;
import core.SolutionTest;
import core.hyperplane.HyperplaneTest;
import core.hyperplane.ReferencePointTest;
import problems.DTLZ1Test;
import utils.ComparatorTest;
import utils.GaussianEliminationTest;
import utils.NonDominatedSortTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   HyperplaneTest.class,
   ReferencePointTest.class,
   ComparatorTest.class,
   SolutionTest.class,
   NonDominatedSortTest.class,
   GaussianEliminationTest.class,
   DTLZ1Test.class,
   NSGAIIITest.class
})
public class JunitTestSuite {
}  	