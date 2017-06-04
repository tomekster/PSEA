package experiment;

public class ExecutionParameters {
	private int populationSize;
	private int numGenerations;
	private int numExplorationGenerations;
	private int numExploitationGenerations;
	private int numElicitations1;
	private int numElicitations2;
	private int elicitationInterval;
	private int numLambdaDirections;
	private double spreadThreshold;
	
	public ExecutionParameters(int popSize, int numGen, int numExplor, int numExploit, int numElic1, int numElic2, int elicInter, int numLambdaDirections, double spreadThresh){
		this(popSize, numGen, numExplor, numExploit, numElic1, numElic2, elicInter, numLambdaDirections, spreadThresh, numElic1 + numElic2);
	}
	
	public ExecutionParameters(int popSize, int numGen, int numExplor, int numExploit, int numElic1, int numElic2, int elicInter, int numLambdaDirections, double spreadThresh, int numSpreadElicitations){
		this.setPopulationSize(popSize);
		this.numGenerations = numGen;
		this.numExplorationGenerations = numExplor;
		this.numExploitationGenerations = numExploit;
		this.numElicitations1 = numElic1;
		this.numElicitations2 = numElic2;
		this.elicitationInterval = elicInter;
		this.numLambdaDirections = numLambdaDirections;
		this.spreadThreshold = spreadThresh;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("ExecutionParameters: ");
		sb.append("popSize=" + populationSize);
		sb.append(", ");
		sb.append("numGen=" + numGenerations);
		sb.append(", ");
		sb.append("numExplor=" + numExplorationGenerations);
		sb.append(", ");
		sb.append("numExploit=" + numExploitationGenerations);
		sb.append(", ");
		sb.append("numElic1=" + numElicitations1);
		sb.append(", ");
		sb.append("numElic2=" + numElicitations2);
		sb.append(", ");
		sb.append("elicInter=" + elicitationInterval);
		sb.append(", ");
		sb.append("numLambdaDirections=" + numLambdaDirections);
		sb.append(", ");
		sb.append("spreadThresh" + spreadThreshold);
		return sb.toString();
	}
	
	public int getNumGenerations() {
		return numGenerations;
	}
	public void setNumGenerations(int numGenerations) {
		this.numGenerations = numGenerations;
	}
	public int getNumExplorationGenerations() {
		return numExplorationGenerations;
	}
	public void setNumExplorationGenerations(int numExplorationGenerations) {
		this.numExplorationGenerations = numExplorationGenerations;
	}
	public int getNumExploitationGenerations() {
		return numExploitationGenerations;
	}
	public void setNumExploitationGenerations(int numExploitationGenerations) {
		this.numExploitationGenerations = numExploitationGenerations;
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
	public int getElicitationInterval() {
		return elicitationInterval;
	}
	public void setElicitationInterval(int elicitationInterval) {
		this.elicitationInterval = elicitationInterval;
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

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}
}
