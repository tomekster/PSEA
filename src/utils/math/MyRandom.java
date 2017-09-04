package utils.math;

import java.util.Random;

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

}
