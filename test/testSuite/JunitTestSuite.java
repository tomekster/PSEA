package testSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import core.NicheCountSelectionTest;
import core.PopulationTest;
import problems.DTLZ1Test;
import utils.NonDominatedSortTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   //HyperplaneTest.class,
   NicheCountSelectionTest.class,
   //ReferencePointTest.class,
   //NSGAIIITest.class,
   PopulationTest.class,
   DTLZ1Test.class,
   //ComparatorTest.class,
   //GaussianEliminationTest.class,
   //GeometryTest.class,
   NonDominatedSortTest.class
})
public class JunitTestSuite {
}  	