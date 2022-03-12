import java.util.Arrays;
import java.util.Iterator;

/***
 * 
 * @author Lars Mayrand
 *
 */
public class ReducedEhrenfestModel {
	
	/** Number of simulations. */
	static final int N = 10_000_000; 
			
	public static void main(String[] args) {
		new ReducedEhrenfestModel().run();
	}

	public void run() {
		System.out.println(expected(2, 3));
		
	}
	
	/*
	 * full --> even data  
	 * d = M = 2: 1
	 * d = M = 2: 4.5
	 * d = M = 3: 12.6626945 
	 * d = M = 4: 31.3195283
	 * d = M = 5: 75.2695131
	 * d = M = 6: 182.4195079
	 * d = M = 7: 449.9848664
	 * d = M = 8: 
	 * d = M = 9: 
	 
	
	
	(2, 1) (3, 4.5) (3, 12.6) (4, 31.31) (5, 75.26) (182.419) (7, 449.98)
1
4.5
12.6626945 
31.3195283
75.2695131
182.4195079
449.9848664
	*/
	public double fullDescription(int d, int M)  {
		// runs process
		
		int[] a = new int[M];

//		System.out.println(//isFull());
			// randomly distributed
//		for (int i = 0; i < M; i++) {
//			//rand = StdRandom.uniform(d);
//			a[i] = 0;
//					//rand == 1 ? 0 : rand;
//		}
		
//		 	evenly distributed
//		for (int i = 0, z = 0; i < d; i++) {
//			for (int j = 0; j < M / d; j++) {
//				a[z++] = i;
//			}
//		}
		
		int i, rand;
		for (i = 0; !isFull(a, 1); i++) {	
//			System.out.println(Arrays.toString(a));
			rand = StdRandom.uniform(M);
			a[rand] = (a[rand] + StdRandom.uniform(d - 1) + 1) % d;
		}
		//System.out.println(i + " " + Arrays.toString(a));
		return i; 
	}
	
	boolean somethingFull(int[] a) {
		for (int i = 0; i < a.length - 1; i++) {
			if (a[i] != a[i + 1]) return false;
		}
		return true;
	}
	
	/** Not general. */
	boolean isBinomial(int[] a, int M, int d) {
		int[] b = new int[d];
		for (int i : a) {
			b[i]++;
		}
		return b[0] == 2 && b[1] == 2;
		//return b[0] == 1 && b[1] == 2 && b[2] == 1;
//		if (b[0] != 1) return false;
//		if (b[1] != 2) return false;
//		if (b[2] != 1) return false;
//		for (int i : b) {
//			//System.out.println(choose(d - 1, i));
//			if (i != choose(d - 1, i)) {
//				return false;
//			}
//		}
		//return true;
	}
	
	public boolean isEven(int[] a, int d, int M) {
		int[] b = new int[d];
		for (int i : a) {
			b[i]++;
		}
		for (int i : b) {
			if (i != M / d) return false;
		}
		return true;
	}
	
	public double somethingElseFilled(int d, int M) {
		double sum = 0;
		for (int i = 0; i < M; i++) {
			sum += Math.pow(d, i) / (i + 1);
		}
		return M * sum;
	}
	
	
	/** Returns true is it's full duh **/
	public boolean isFull(int a[], int b) {
		for (int i : a) {
			if (i != b) return false;
		}
		return true;
	}
	
	// returns true if b is not in a
	public boolean isEmpty(int a[], int b) {
		for (int i : a) {
			if (i == b) return false;
		}	
		return true;
	}
	
	public double expectedDrunkGeneral(int d, int k, int M) {
		double average = 0;
		for (int i = 0; i < N; i++) {
			average += drunkD(d, k, M);
		}
		return average / N;
	}
	
	// drunkard's walk with d urns
	public double drunkD(int d, int k, int M) {
		
		// puts all balls in urn 0
		int[] a = new int[M];
		for (int i = 0; i < k; i++) { 
			a[i] = 0;
		}
		for (int i = k; i < M; i++) {
			a[i] = StdRandom.uniform(d - 1) + 1;
		}
		//System.out.println(Arrays.toString(a));
		// runs process
		int i;
		for (i = 0; !isEmpty(a, 0) && !isFull(a, 0); i++) {
			//System.out.println(Arrays.toString(a));
			int rand = StdRandom.uniform(M);
			a[rand] = (a[rand] + StdRandom.uniform(d - 1) + 1) % d;
		}
		//System.out.println("DONE" + Arrays.toString(a));
		return i; 
	}
	
	public double expected(int d, int M) {
		double average = 0;
		for (int i = 0; i < N; i++) {
			average += fullDescription(d, M);
			if (i == N / 2) System.out.println("halfway there!");
		}
		return average / N;
	}

	/** 
	 * input: start and end states
	 * output: average time it took 
	 */
	public double expectedHittingTime(int start, int end, int balls) {
		double average = 0;
		//int hittingTime;
		for (int i = 0; i < N; i++) {
//			hittingTime = hittingTime(start, end);
//			System.out.println(hittingTime);
//			average += hittingTime;
			average += hittingTime(start, end, balls);
		}
		return average / N;
	}
	
	public int hittingTime(int start, int end, int balls) {
		double x = start;
		for (int i = 0; ; i++) {
			//System.out.println("x = " + x);
			//if (x == balls || x == 0) {
			if (x == end) {
				//System.out.println("END FOUND  x = " + x);
				return i; // end state found 
			}
			if (StdRandom.uniform() >= x / balls) x++;
			else x--;
		}
	}
	
	public double expectedHittingEnds(int start, int end, int balls) {
		double average = 0;
		//int hittingTime;
		for (int i = 0; i < N; i++) {
//			hittingTime = hittingEnds(start, end, balls);
//			System.out.println(hittingTime);
//			average += hittingTime;
//			
			average += hittingEnds(start, end, balls);
		}
		return average / N;
	}
	
	/**
	 * Probability hit zero before M
	 */
	public int hittingEnds(int start, int end, int balls) {
		double x = start;
		for (int i = 0; ; i++) {
			//System.out.println("x = " + x);
			if (x == balls) {
				//System.out.println("END FOUND  x = " + x);
				return 0; // end state found 
			} else if (x == 0) {
				return 1;
			}
			
			if (StdRandom.uniform() >= x / balls) x++;
			else x--;
		}
	}
	
	public double choose(int n, int k) {
		return n == k || k == 0 ? 1 : factorial(n) / factorial(k) * 1 / factorial(n - k);
	}
	
	public double factorial(int x) {
		return x < 2 ? x : x * factorial(--x);
	}
	
	/*
	 * drunkard's walk: d = 3, M = 3
	 * k = 0: 0
	 * k = 1: 3.5
	 * k = 2: 4
	 * 
	 * drunkard's walk: d = 3, M = 6 
	 * k = 0: 0
	 * k = 1: 19.8888294
	 * k = 2: 25.45279
	 * k = 3: 28
	 * k = 4: 29?
	 * k = 5: 27.55
	 * k = 6: 0
	 * 
	 */
	
	/*
	 * M = d = 4
	 * full --> even : 12.66
	 */
	
	/*
	 * 3 urns, 9 balls
	 * even --> something full: 7043.91 ish 
	 * full -->           even: 20.24
	 * 
	 * even --> something full: 270.6 
	 * full -->           even: 11.82
	 */
	
	/* T = specific urn full
	 * t = any urn full
	 *  
	 */
	
	/*
	 * even --> something full: 270.6933266
	 * something full --> even: 11.82
	 */
	
	/*
	 * even --> 0 full:          31.0039885
	 * even --> 0 or 1 or 2 full: 9.0020501
	 */
	
	
	/*
	 * urn 0 full --> even, using simulation 
	 * (3, 27) = 81.1376857
	 * (3, 45) = 150.9733334
	 */

}
