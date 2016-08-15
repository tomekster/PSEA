package core.hyperplane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

import core.Population;
import core.Solution;
import preferences.TchebyshevFunction;
import utils.Geometry;
import utils.MyComparator;
import utils.Pair;

public class ReferencePoint {
	private double dimensions[];
	private int numDimensions;
	private int nicheCount;
	private boolean coherent;
	private PriorityQueue<Association> associatedSolutions;
	//Rho value maximizing eps in RACS Linear Programming task
	private double rho;
	//Maximum eps achievable in RACS Linear Programming task
	private double eps;
	//Stores pairs <Solution, Chebysheff's Augmented function value> corresponding to current reference point
	private ArrayList<Solution> ranking;

	public ReferencePoint(int numDimensions) {
		this.numDimensions = numDimensions;
		this.dimensions = new double[numDimensions];
		this.associatedSolutions = new PriorityQueue<Association>(MyComparator.associationComparator);
		this.coherent = false;
		for (int i = 0; i < numDimensions; i++){
			this.dimensions[i] = 0.0;
		}
	}

	public ReferencePoint(ReferencePoint rp) {
		this.numDimensions = rp.getNumDimensions();
		this.dimensions = rp.getDimensions().clone();
		this.associatedSolutions = new PriorityQueue<Association> (rp.getAssociatedSolutionsQueue());
		this.coherent = rp.isCoherent();
		this.nicheCount = rp.getNicheCount();
	}
	
	public ArrayList<Solution> buildRanking(Population pop){
		ArrayList < Pair<Solution, Double> > solutionValuePairs = new ArrayList < Pair<Solution, Double>>();
		for(Solution s : pop.getSolutions()){
			double chebyshevValue = TchebyshevFunction.eval(s, Geometry.invert(this.dimensions), this.rho, null);
			solutionValuePairs.add( new Pair <Solution, Double>(s, chebyshevValue));
		}
		Collections.sort(solutionValuePairs, new Comparator<Pair<Solution, Double>>(){
			@Override
			public int compare(final Pair<Solution, Double> o1, final Pair<Solution, Double> o2){
				//Sort pairs by Chebyshev Function value descending
				return -Double.compare(o1.second, o2.second);
			}
		});
		
		ranking = new ArrayList<Solution>();
		for(Pair<Solution, Double> p : solutionValuePairs){
			ranking.add(p.first);
		}
		assert ranking.size() == pop.size();
		return ranking;
	}

	public double getDim(int index) {
		return this.dimensions[index];
	}
	
	public void setDim(int index, double val) {
		this.dimensions[index] = val;
	}
	
	public void incrDim(int index, double value) {
		this.dimensions[index] += value;
	}

	public void decrDim(int index, double value) {
		this.dimensions[index] += value;
	}

	public void incrNicheCount() {
		this.nicheCount++;
	}
	
	public void decrNicheCount() {
		this.nicheCount--;
	}

	public int getNicheCount() {
		return nicheCount;
	}

	public int getNumDimensions() {
		return this.numDimensions;
	}

	public double[] getDimensions() {
		return this.dimensions;
	}

	public void setNicheCount(int i) {
		this.nicheCount = i;
	}

	public void resetAssociation() {
		this.nicheCount = 0;
		this.associatedSolutions.clear();
	}

	public void addAssociation(Association association) {
		this.associatedSolutions.add(association);
	}

	public PriorityQueue<Association> getAssociatedSolutionsQueue() {
		return associatedSolutions;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (double d : dimensions) {
			sb.append(d + ", ");
		}
		sb.replace(sb.length() - 2, sb.length(), "]\n");
		if (!associatedSolutions.isEmpty()) {
			sb.append("Associations: [\n");
			for (Association as : associatedSolutions) {
				sb.append(as.toString() + ",\n");
			}
			sb.replace(sb.length() - 2, sb.length(), "]");
		} else {
			sb.append("Associations: none");
		}
		return sb.toString();
	}

	public boolean isCoherent() {
		return coherent;
	}

	public void setCoherent(boolean coherent) {
		this.coherent = coherent;
	}

	public ReferencePoint copy() {
		ReferencePoint rp = new ReferencePoint(this);
		return rp;
	}
	
	public void setDimensions(double dim[]){
		this.dimensions = dim;
	}
	
	public double getRho(){
		return this.rho;
	}

	public void setRho(double rho) {
		this.rho = rho;
	}
	
	public double getEps(){
		return this.eps;
	}

	public void setEps(double eps) {
		this.eps = eps;
	}

	public ArrayList<Solution> getRanking() {
		return ranking;
	}

	public Solution getRankingElement(int i) {
		return ranking.get(i);
	}
}
