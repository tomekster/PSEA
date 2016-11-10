package testSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import core.LambdaTest;
import core.NicheCountSelectionTest;
import core.PopulationTest;
import core.hyperplane.HyperplaneTest;
import core.points.ReferencePointTest;
import core.points.SolutionTest;
import problems.DTLZ1Test;
import utils.GaussianEliminationTest;
import utils.GeometryTest;
import utils.NonDominatedSortTest;
@RunWith(Suite.class)
@Suite.SuiteClasses({
   HyperplaneTest.class,
   ReferencePointTest.class,
   SolutionTest.class,
   NicheCountSelectionTest.class,
   PopulationTest.class,
   DTLZ1Test.class,
   GaussianEliminationTest.class,
   GeometryTest.class,
   GeometryTest.class,
   NonDominatedSortTest.class,
   LambdaTest.class
})
public class JunitTestSuite {
}  	