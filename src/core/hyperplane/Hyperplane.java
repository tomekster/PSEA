package core.hyperplane;

import java.util.ArrayList;

public class Hyperplane {
	
	private ArrayList <ReferencePoint> referencePoints;
	
	public Hyperplane(int M, ArrayList <Integer> partitions){
		referencePoints = new ArrayList <ReferencePoint>();
		generateReferencePoints(M,partitions);
	}

	private void generateReferencePoints(int M, ArrayList <Integer> partitions) {
		ArrayList <ReferencePoint> boundaryLayer = new ArrayList<>();
		ArrayList <ReferencePoint> insideLayer = new ArrayList<>();
		
		int p = partitions.get(0);
		generateRecursive(new ReferencePoint(M), 1.0 / p, 0 , p, boundaryLayer);
		referencePoints.addAll(boundaryLayer);
		if(partitions.size() > 1){
			p = partitions.get(1);
			ReferencePoint rp = new ReferencePoint(M);
			generateRecursive(rp, 0.5 / p, 0 , p, insideLayer);
			referencePoints.addAll(insideLayer);
		}
	}
	
	private void generateRecursive(ReferencePoint rp, double step, int startDim, int left, ArrayList <ReferencePoint> layer){
		if(left == 0){
			layer.add(rp);
			return;
		}
		
		if(startDim == rp.getNumDimensions() - 1){
			rp.incrDim(startDim, step * left);
			layer.add(rp);
			return;
		}
			
		for(int i=0; i <= left; i++){
			generateRecursive(new ReferencePoint(rp), step , startDim + 1, left - i, layer);	
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
