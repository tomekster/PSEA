package artificialDM;

public class SingleObjectiveDM extends ArtificialDM {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1702586872373803894L;
	
	private int optimizedObjective;
	
	public SingleObjectiveDM(int optObj) {
		optimizedObjective = optObj;
	}

	@Override
	public double eval(double[] obj) {
		return obj[optimizedObjective];
	}
	
	public int getOptimizedObjective(){
		return optimizedObjective;
	}
	
	public void setOptimizedObjective(int optObj){
		optimizedObjective = optObj;
	}
}
