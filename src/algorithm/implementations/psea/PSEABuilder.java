package algorithm.implementations.psea;

import algorithm.evolutionary.EA;
import algorithm.evolutionary.interactive.artificialDM.AsfDm;
import algorithm.evolutionary.interactive.artificialDM.RferencePointDm;
import algorithm.evolutionary.solutions.Solution;
import problems.Problem;

public class PSEABuilder<S extends Solution> {

	private final Problem<S> problem;
	private final RferencePointDm simulatedDm;
	private final EA.GeneticOperators<S> go;

	private double lambdaMutationProbability;
	private double lambdaMutationNeighborhoodRadius;
	private double lambdaRho;
	private int maxExplorationComparisons;
	private int maxExploitationComparisons;
	private int maxZeroDiscriminativePower;
	private int elicitationInterval;
	private int maxExploitGenerations;
	private int maxNumGenWithNoSpreadImprovment;
	private int asfBundleSize;
	private double spreadThreshold;
	private boolean asfDmMutation;

	public PSEABuilder(Problem<S> problem, RferencePointDm simulatedDm, EA.GeneticOperators<S> go) {
		// MandatoryFields
		this.problem 		= problem;
		this.simulatedDm 	= simulatedDm;
		this.go 			= go;

		// OptionalFields
		this.spreadThreshold 					= 0.95;
		this.lambdaMutationProbability 			= 0.05;
		this.lambdaMutationNeighborhoodRadius 	= 0.3;
		this.lambdaRho 							= 0.0001;
		this.maxExplorationComparisons 			= 20;
		this.maxExploitationComparisons 		= 20;
		this.maxZeroDiscriminativePower 		= 5;
		this.elicitationInterval 				= 10;
		this.maxExploitGenerations 				= 800;
		this.maxNumGenWithNoSpreadImprovment 	= 50;
		this.asfBundleSize 						= 50;
		this.asfDmMutation 						= false;
	}

	public PSEABuilder<S> setAsfDmMutation(boolean asfDmMutation) {
		this.asfDmMutation = asfDmMutation;
		return this;
	}

	public double getLambdaMutationProbability() {
		return lambdaMutationProbability;
	}

	public PSEABuilder<S> setLambdaMutationProbability(double lambdaMutationProbability) {
		this.lambdaMutationProbability = lambdaMutationProbability;
		return this;
	}

	public double getLambdaMutationNeighborhoodRadius() {
		return lambdaMutationNeighborhoodRadius;
	}

	public PSEABuilder<S> setLambdaMutationNeighborhoodRadius(double lambdaMutationNeighborhoodRadius) {
		this.lambdaMutationNeighborhoodRadius = lambdaMutationNeighborhoodRadius;
		return this;
	}

	public double getLambdaRho() {
		return lambdaRho;
	}

	public PSEABuilder<S> setLambdaRho(double lambdaRho) {
		this.lambdaRho = lambdaRho;
		return this;
	}

	public int getMaxExplorationComparisons() {
		return maxExplorationComparisons;
	}

	public PSEABuilder<S> setMaxExplorationComparisons(int maxExplorationComparisons) {
		this.maxExplorationComparisons = maxExplorationComparisons;
		return this;
	}

	public int getMaxExploitationComparisons() {
		return maxExploitationComparisons;
	}

	public PSEABuilder<S> setMaxExploitationComparisons(int maxExploitationComparisons) {
		this.maxExploitationComparisons = maxExploitationComparisons;
		return this;
	}

	public int getMaxZeroDiscriminativePower() {
		return maxZeroDiscriminativePower;
	}

	public PSEABuilder<S> setMaxZeroDiscriminativePower(int maxZeroDiscriminativePower) {
		this.maxZeroDiscriminativePower = maxZeroDiscriminativePower;
		return this;
	}

	public int getElicitationInterval() {
		return elicitationInterval;
	}

	public PSEABuilder<S> setElicitationInterval(int elicitationInterval) {
		this.elicitationInterval = elicitationInterval;
		return this;
	}

	public int getMaxExploitGenerations() {
		return maxExploitGenerations;
	}

	public PSEABuilder<S> setMaxExploitGenerations(int maxExploitGenerations) {
		this.maxExploitGenerations = maxExploitGenerations;
		return this;
	}

	public int getMaxNumGenWithNoSpreadImprovment() {
		return maxNumGenWithNoSpreadImprovment;
	}

	public PSEABuilder<S> setMaxNumGenWithNoSpreadImprovment(int maxNumGenWithNoSpreadImprovment) {
		this.maxNumGenWithNoSpreadImprovment = maxNumGenWithNoSpreadImprovment;
		return this;
	}

	public int getAsfBundleSize() {
		return asfBundleSize;
	}

	public PSEABuilder<S> setAsfBundleSize(int asfBundleSize) {
		this.asfBundleSize = asfBundleSize;
		return this;
	}

	public boolean isAsfDmMutation() {
		return asfDmMutation;
	}

	public PSEABuilder<S> setAsfDMsMutation(boolean asfDMsMutation) {
		this.asfDmMutation = asfDMsMutation;
		return this;
	}

	public double getSpreadThreshold() {
		return spreadThreshold;
	}

	public PSEABuilder<S> setSpreadThreshold(double spreadThreshold) {
		this.spreadThreshold = spreadThreshold;
		return this;
	}

	public Problem<S> getProblem() {
		return problem;
	}

	public RferencePointDm getAdm() {
		return simulatedDm;
	}

	public EA.GeneticOperators<S> getGo() {
		return go;
	}
}
