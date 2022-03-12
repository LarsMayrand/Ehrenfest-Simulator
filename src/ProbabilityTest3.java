import java.util.ArrayList;
import java.util.Collections;

/***
 * 
 * @author larspmayrand
 */
public class ProbabilityTest3 {

	/** Number of trials in simulation. */
	static final double TRIALS = 1_000_000;
	
	public static void main(String[] args) {
		//System.out.println(exponential(3));
		// System.out.println(hatProblem(10));
		System.out.println(p(4));
	}

	/** Returns a exponentially distributed random variable with parameter λ. */
	static double exponential(double λ) {
		return - Math.log(1 - Math.random()) / λ;
	}
	
	/** Finds P(hatProblem(n) = 2n). */
	static double p(int n) {
		double counter = 0;
		for (int i = 0; i < TRIALS; i++) {
			if (hatProblem(n) == 2 * n) counter++;
		}
		System.out.println(counter);
		return counter / TRIALS;
	}
	
	/**
	 * @param n = number of twins 
	 * @return number of people who got their own hats back
	 */
	static double hatProblem(int n) {
		ArrayList<Integer> hats = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) { // 0 0 1 1 2 2 3 3 ... n n
			hats.add(i);
			hats.add(i);
		}
//		System.out.println(hats.toString());
		Collections.shuffle(hats);
//		System.out.println(hats.toString());
		int counter = 0;
		for (int i = 0; i < 2 * n; i++) {  
			if (hats.get(i) == i / 2) counter++;
			if (hats.get(++i) == i / 2) counter++;
		}
		return counter;
	}

}
