package utils;

import java.util.Random;

public class NSGAIIIRandom extends Random {

	private static final long serialVersionUID = -7103478373171899983L;

	private static NSGAIIIRandom instance = null;

	private NSGAIIIRandom() { }

	public static NSGAIIIRandom getInstance() {
		if (instance == null) {
			instance = new NSGAIIIRandom();
		}
		return instance;
	}

}
