package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

import core.hyperplane.Association;
import core.hyperplane.Hyperplane;
import core.hyperplane.ReferencePoint;
import utils.Geometry;
import utils.MyComparator;

/***
 * Class encapsulates "selectKPoints" method from NSGA-III algorithm 
 */
public class NicheCountSelection {

	public static Population selectKPoints(Population allFronts, Population allButLastFront, Population lastFront, 
			int k, Hyperplane hyperplane){
		associate(allFronts, hyperplane);
		Population kPoints = niching(allButLastFront, lastFront, k, hyperplane);
		return kPoints;
	}

	public static void associate(Population population, Hyperplane hyperplane) {
		hyperplane.resetAssociations();
		ArrayList<ReferencePoint> refPoints = hyperplane.getReferencePoints();

		for (Solution s : population.getSolutions()) {
			double minDist = Double.MAX_VALUE;
			ReferencePoint bestRefPoint = null;
			for (int i = 0; i < refPoints.size(); i++) {
				ReferencePoint curRefPoint = refPoints.get(i);
				double dist = Geometry.pointLineDist(s.getObjectives(), curRefPoint.getDimensions());
				if (dist < minDist) {
					minDist = dist;
					bestRefPoint = curRefPoint;
				}
			}
			bestRefPoint.addAssociation(new Association(s, minDist));
		}
	}

	private static Population niching(Population allButLastFront, Population lastFront, int K, Hyperplane hyperplane) {
		Population kPoints = new Population();
		HashMap<Solution, Boolean> isLastFront = new HashMap<>();
		for (Solution s : allButLastFront.getSolutions()) {
			isLastFront.put(s, false);
		}
		for (Solution s : lastFront.getSolutions()) {
			isLastFront.put(s, true);
		}
		
		PriorityQueue<ReferencePoint> refPQ = new PriorityQueue<>(MyComparator.referencePointComparator);
		for (ReferencePoint rp : hyperplane.getReferencePoints()) {
			refPQ.add(rp);
		}

		while (kPoints.size() < K) {
			ReferencePoint smallestNicheCountRefPoint = refPQ.poll();
			PriorityQueue<Association> associatedSolutionsQueue = smallestNicheCountRefPoint
					.getAssociatedSolutionsQueue();
			while (!associatedSolutionsQueue.isEmpty()) {
				Solution s = associatedSolutionsQueue.poll().getSolution();
				if (isLastFront.get(s)) {
					kPoints.addSolution(s);
					smallestNicheCountRefPoint.incrNicheCount();
					refPQ.add(smallestNicheCountRefPoint);
					break;
				}
			}
		}
		return kPoints;
	}
}
