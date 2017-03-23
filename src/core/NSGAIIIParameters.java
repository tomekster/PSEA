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
	private int numberGenerations;
	private int numberRuns;
	private int elicitationInterval;
	
	private boolean showTargetPoints;
	private boolean showLambdas;
	private boolean showComparisons;
	
	private static NSGAIIIParameters instance = null;

	protected NSGAIIIParameters(){
		// Exists only to defeat instantiation.
		problemName = "DTLZ1";
		numberObjectives = 3;
		numberGenerations = 50;
		numberRuns = 1;
		elicitationInterval = 20;
		showTargetPoints = true;
		showLambdas = true;
		showComparisons = true;
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

	public int getNumberGenerations() {
		return numberGenerations;
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

	public int getElicitationInterval() {
		return elicitationInterval;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public void setNumberObjectives(int numberObjectives) {
		this.numberObjectives = numberObjectives;
	}

	public void setNumberGenerations(int numberGenerations) {
		this.numberGenerations = numberGenerations;
	}

	public void setNumberRuns(int numberRuns) {
		this.numberRuns = numberRuns;
	}

	public void setElicitationFrequency(int elicitationInterval) {
		this.elicitationInterval = elicitationInterval;
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

}
