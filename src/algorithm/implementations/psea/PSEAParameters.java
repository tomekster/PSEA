package algorithm.implementations.psea;

public class PSEAParameters {

	private static PSEAParameters instance = null;
	private String problemName;
	private Integer numObj;
	private Integer numExplorGen;
	private Integer numExploitGen;
	private Integer numRuns;
	private Integer elicitFreq;
	
	private PSEAParameters(){
		
	}
	
	public static PSEAParameters getInstance() {
		if(instance == null){
			instance = new PSEAParameters();
		}
		return instance;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public void setNumberObjectives(Integer numObj) {
		this.numObj = numObj;
	}

	public void setNumberExplorationGenerations(Integer numExplorGen) {
		this.numExplorGen = numExplorGen;
	}

	public String getProblemName() {
		return problemName;
	}

	public Integer getNumObj() {
		return numObj;
	}

	public Integer getNumExplorGen() {
		return numExplorGen;
	}

	public Integer getNumExploitGen() {
		return numExploitGen;
	}

	public Integer getNumRuns() {
		return numRuns;
	}

	public Integer getElicitFreq() {
		return elicitFreq;
	}

	public void setNumberExploitationGenerations(Integer numExploitGen) {
		this.numExploitGen = numExploitGen;
	}

	public void setNumberRuns(Integer numRuns) {
		this.numRuns = numRuns;
	}

	public void setElicitationInterval(Integer elicitFreq) {
		this.elicitFreq = elicitFreq;
		
	}
}
