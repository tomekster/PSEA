package algorithm.evolutionary.interactive.artificialDM;

import java.util.ArrayList;

import utils.math.AsfFunction;
import utils.math.Geometry;
import utils.math.structures.Point;

public class AsfDmBuilder{
	public static AsfDm getAsfDm(int id, int dim, Point ideal, double rho){
		String name = "---";
		Point refPoint = new Point(dim);
		
		double weights[] = new double[dim];
		switch(id){
		case 1:
			for(int i=0; i<dim; i++){weights[i] = 1;}
			name = "1AllObjEqual";
			break;
		case 2:
			for(int i=0; i<dim; i++){weights[i] = 1;}
			weights[0] = dim-1;
			name = "2MinimizeFirstObj";
			break;
		case 3:
			for(int i=0; i<dim; i++){weights[i] = 1;}
			weights[dim/2] = dim-1;
			name = "3MinimizeMiddleObj";
			break;
		case 4:
			for(int i=0; i<dim; i++){weights[i] = 1;}
			weights[dim-1] = dim-1;
			name = "4MinimizeLastObj";
			break;
		case 5:
			for(int i=0; i<dim; i++){weights[i] = 10;}
			weights[0] = 1;
			name = "5MaximizeFirstObj";
			break;
		case 6:
			for(int i=0; i<dim; i++){weights[i] = 10;}
			weights[dim/2] = 1;
			name = "6MaximizeMiddleObj";
			break;
		case 7:
			for(int i=0; i<dim; i++){weights[i] = 10;}
			weights[dim-1] = 1;
			name = "7MaximizeLastObj";
			break;
		case 8:
			for(int i=0; i<dim; i++){weights[i] = i+1;}
			name = "8IncreasingCoef";
			break;
		case 9:
			for(int i=0; i<dim; i++){weights[i] = dim-i;}
			name = "9DecreasingCoef";
			break;
		}
		
		double lambda[] = Geometry.normalizeSum(weights, 1);
		return new AsfDm(new AsfFunction(lambda, rho, refPoint), name);
	}
	
	public static ArrayList<AsfDm> getAsfDms(int numObj, Point ideal, double rho){
			ArrayList <AsfDm> rankersList = new ArrayList<>();
			for(int id=1; id<=9; id++){
				rankersList.add(getAsfDm(id, numObj, ideal, rho));
			}
			return rankersList;
	}
}
