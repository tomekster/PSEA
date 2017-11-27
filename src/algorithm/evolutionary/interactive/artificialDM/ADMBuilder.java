package algorithm.evolutionary.interactive.artificialDM;

import java.util.ArrayList;

import utils.math.AsfFunction;
import utils.math.Geometry;
import utils.math.structures.Point;

public class ADMBuilder{
	public static AsfDM getAsfDm(int id, int dim, double ideal[], double rho){
		String name = "---";
		Point refPoint = new Point(dim);
 		
 		if(ideal != null){
 			refPoint = new Point(ideal.clone());
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
		
		double lambda[] = Geometry.invert(Geometry.normalizeSum(direction, 1));
		return new AsfDM(new AsfFunction(lambda, rho, refPoint), name);
	}
	
	public static ArrayList<AsfDM> getAsfDms(int numObj, double ideal[], double rho){
			ArrayList <AsfDM> rankersList = new ArrayList<>();
			for(int id=1; id<=9; id++){
				rankersList.add(getAsfDm(id, numObj, ideal, rho));
			}
			return rankersList;
	}
}
