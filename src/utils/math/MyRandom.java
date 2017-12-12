package utils.math;

import java.util.Random;

import utils.math.structures.Pair;

public class MyRandom extends Random {

	private static final long serialVersionUID = -7103478373171899983L;

	private static MyRandom instance = null;

	private MyRandom() { }

	public static MyRandom getInstance() {
		if (instance == null) {
			instance = new MyRandom();
		}
		return instance;
	}
	
	public Pair<Integer, Integer> getRandomPair(int upperBound){
		int i = nextInt(upperBound);
		int j = nextInt(upperBound-1);
		if(j >=i) j++;
		return new Pair<Integer, Integer>(i, j);
	}

}
