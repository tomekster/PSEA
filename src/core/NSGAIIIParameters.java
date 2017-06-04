package core;

/***
 * Singleton class for storing NSGAIII execution and display parameters
 * 
 * @author tomasz
 *
 */
public class NSGAIIIParameters {
	private String problemName;
	private int numberObjectives;
	private int numberExplorationGenerations;
	private int numberExploitationGenerations;
	private int numElicitations1;
	private int numElicitations2;
	private int numberRuns;
	private int elicitationInterval;
	private int numLambdaDirections;
	private double spreadThreshold;
	
	private boolean showTargetPoints;
	private boolean showLambdas;
	private boolean showComparisons;
	
	private static NSGAIIIParameters instance = null;

//	if(problem.getNumObjectives() == 3){
//		numElicitations1 = 50;
//		numElicitations2 = 30;
//	}
//	if(problem.getNumObjectives() == 5){
//		numElicitations1 = 70;
//		numElicitations2 = 30;
//	}
//	if(problem.getNumObjectives() == 8){
//		numElicitations1 = 100;
//		numElicitations2 = 30;
//	}
	
	protected NSGAIIIParameters(){
		// Exists only to defeat instantiation.
		problemName = "DTLZ1";
		numberObjectives = 3;
		numberExplorationGenerations = 100;
		numberExploitationGenerations = 100;
		numElicitations1 = 50;
		numElicitations2 = 30;
		numberRuns = 1;
		elicitationInterval = 1;
		showTargetPoints = true;
		showLambdas = true;
		showComparisons = true;
		numLambdaDirections = 50;
	}

	public static NSGAIIIParameters getInstance() {
		if (instance == null) {
			instance = new NSGAIIIParameters();
		}
		return instance;
	}

	public String getProblemName() {
		return problemName;
	}

	public int getNumberObjectives() {
		return numberObjectives;
	}

	public int getNumberExplorationGenerations() {
		return numberExplorationGenerations;
	}
	
	public int getNumberExploitationGenerations() {
		return numberExploitationGenerations;
	}

	public int getNumberRuns() {
		return numberRuns;
	}

	public boolean isShowTargetPoints() {
		return showTargetPoints;
	}
	
	public boolean isShowLambdas() {
		return showLambdas;
	}
	
	public boolean isShowComparisons() {
		return showComparisons;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public void setNumberObjectives(int numberObjectives) {
		this.numberObjectives = numberObjectives;
	}

	public void setNumberExplorationGenerations(int numberGenerations) {
		this.numberExplorationGenerations = numberGenerations;
	}
	
	public void setNumberExploitationGenerations(int numberGenerations) {
		this.numberExploitationGenerations = numberGenerations;
	}

	public void setNumberRuns(int numberRuns) {
		this.numberRuns = numberRuns;
	}

	public void setShowTargetPoints(boolean showTargetPoints) {
		this.showTargetPoints = showTargetPoints;
	}

	public void setShowLambdas(boolean showLambdas) {
		this.showLambdas = showLambdas;
	}

	public void setShowComparisons(boolean showComparisons) {
		this.showComparisons = showComparisons;
	}

	public int getElicitationInterval() {
		return elicitationInterval;
	}

	public void setElicitationInterval(int elicitationInterval) {
		this.elicitationInterval = elicitationInterval;
	}

	public int getNumElicitations1() {
		return numElicitations1;
	}

	public void setNumElicitations1(int numElicitations1) {
		this.numElicitations1 = numElicitations1;
	}

	public int getNumElicitations2() {
		return numElicitations2;
	}

	public void setNumElicitations2(int numElicitations2) {
		this.numElicitations2 = numElicitations2;
	}

	public int getNumLambdaDirections() {
		return numLambdaDirections;
	}

	public void setNumLambdaDirections(int numLambdaDirections) {
		this.numLambdaDirections = numLambdaDirections;
	}

	public double getSpreadThreshold() {
		return spreadThreshold;
	}

	public void setSpreadThreshold(double spreadThreshold) {
		this.spreadThreshold = spreadThreshold;
	}

}
