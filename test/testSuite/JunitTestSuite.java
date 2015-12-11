package testSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import core.NSGAIIITest;
import core.PopulationTest;
import core.SolutionTest;
import core.hyperplane.HyperplaneTest;
import core.hyperplane.NicheCountSelectionTest;
import core.hyperplane.ReferencePointTest;
import problems.DTLZ1Test;
import utils.ComparatorTest;
import utils.GaussianEliminationTest;
import utils.NonDominatedSortTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   HyperplaneTest.class,
   NicheCountSelectionTest.class,
   ReferencePointTest.class,
   NSGAIIITest.class,
   PopulationTest.class,
   SolutionTest.class,
   DTLZ1Test.class,
   ComparatorTest.class,
   GaussianEliminationTest.class,
   NonDominatedSortTest.class
})
public class JunitTestSuite {
}  	