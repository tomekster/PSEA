package core.hyperplane;

import java.util.ArrayList;

public class Hyperplane {
	
	private ArrayList <ReferencePoint> referencePoints;
	
	public Hyperplane(int M, int p){
		referencePoints = new ArrayList <ReferencePoint>();
		generateReferencePoints(M,p);
	}

	private void generateReferencePoints(int M, int p) {
		generateRecursive(new ReferencePoint(M), 1.0 / p, 0 , p);		
	}
	
	private void generateRecursive(ReferencePoint rp, double step, int startDim, int left){
		if(left == 0){
			referencePoints.add(rp);
			return;
		}
		
		if(startDim == rp.getNumDimensions() -1){
			rp.incrDim(startDim, step * left);
			referencePoints.add(rp);
			return;
		}
			
		for(int i=0; i <= left; i++){
			generateRecursive(new ReferencePoint(rp), step , startDim + 1, left - i);	
			rp.incrDim(startDim,step);
		}
	}
	
	public ArrayList <ReferencePoint> getReferencePoints(){
		return this.referencePoints;
	}

	public void resetAssociations() {
		for(ReferencePoint rp : referencePoints){
			rp.resetAssociation();
		}
	}
	
}
