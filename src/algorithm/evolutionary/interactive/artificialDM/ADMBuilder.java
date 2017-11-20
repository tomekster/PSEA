package algorithm.evolutionary.interactive.artificialDM;

import java.util.ArrayList;

import algorithm.evolutionary.interactive.artificialDM.implementations.AsfDM;
import utils.math.Geometry;

public class ADMBuilder{
	public static AsfDM getAsfDm(int id, int dim, double ideal[]){
		String name = "---";
		double refPoint[] = new double[dim];
 		if(ideal == null){
			for(int i=0; i<dim; i++) refPoint[i]=0;
		}
 		else{
 			refPoint = ideal.clone();
 		}
		
		double direction[] = new double[dim];
		switch(id){
		case 1:
			for(int i=0; i<dim; i++){direction[i] = 1;}
			name = "1AllObjEqual";
			break;
		case 2:
			for(int i=0; i<dim; i++){direction[i] = 1;}
			direction[0] = dim-1;
			name = "2MinimizeFirstObj";
			break;
		case 3:
			for(int i=0; i<dim; i++){direction[i] = 1;}
			direction[dim/2] = dim-1;
			name = "3MinimizeMiddleObj";
			break;
		case 4:
			for(int i=0; i<dim; i++){direction[i] = 1;}
			direction[dim-1] = dim-1;
			name = "4MinimizeLastObj";
			break;
		case 5:
			for(int i=0; i<dim; i++){direction[i] = 10;}
			direction[0] = 1;
			name = "5MaximizeFirstObj";
			break;
		case 6:
			for(int i=0; i<dim; i++){direction[i] = 10;}
			direction[dim/2] = 1;
			name = "6MaximizeMiddleObj";
			break;
		case 7:
			for(int i=0; i<dim; i++){direction[i] = 10;}
			direction[dim-1] = 1;
			name = "7MaximizeLastObj";
			break;
		case 8:
			for(int i=0; i<dim; i++){direction[i] = i+1;}
			name = "8IncreasingCoef";
			break;
		case 9:
			for(int i=0; i<dim; i++){direction[i] = dim-i;}
			name = "9DecreasingCoef";
			break;
		}
		
		direction = Geometry.normalizeSum(direction, 1);
		return new AsfDM(refPoint, direction, name);
	}
	
	public static ArrayList<AsfDM> getAsfDms(int numObj, double ideal[]){
			ArrayList <AsfDM> rankersList = new ArrayList<>();
			for(int id=1; id<=9; id++){
				rankersList.add(getAsfDm(id, numObj, ideal));
			}
			return rankersList;
	}
}
