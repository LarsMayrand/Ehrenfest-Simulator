
public class HittingTimeFormulas {

	double M = 4; 
	double d = 4;
	
	public static void main(String[] args) {
		new HittingTimeFormulas().run();
	}
	
	void run() {
	
		System.out.println(drunkerConductance(1) + ", " + drunkConductance(1));
//		System.out.println(emptyRecursion());
//		M = 40;
//		System.out.println(drunkerConductance((int) M/2) + ", " + drunkConductance((int) M/2));
//		System.out.println(emptyRecursion());
		
		
		//System.out.println(emptyRecursion(2));
		//System.out.println(fillEmptyConductance(0, M, d));
//		System.out.println(emptyRecursion(d));
//		System.out.println(emptyElectric(d));
//		System.out.println(blorg(d));
//		System.out.println(2 * fillEmptyConductance(0, M, d));
//		System.out.println(2 * fillConductance(M, 0, d));
//		System.out.println(blorg(d));
//		System.out.println(fillRecursion(d));
//		
		
//		for (M = 1; M < 100; M++) {
//			for (int d = 0; d < 20; d++) {
//				System.out.println(emptyElectric(d) / fillEmptyConductance(M, 0, d));
//			}
//			
//		}
//		System.out.println(resistanceEvenToFull());
//		System.out.println(E3());
//		for (int i = 3; i < 18; i += 3) {
//			M = i;
//			System.out.println("M = " + M + " " + evenToFull());
//		}
//		System.out.println(fullToEven());
//		System.out.println(evenToFull());
//		System.out.println(resistanceEvenToFull());
//		System.out.println(evenToFull());
	}
	
	public double fillConductance(int d) {
		double sum = 0;
		for (int z = 0; z < M + 1; z++) {
			sum += c(z) * (xi(0, M - 1, d) + xi(0, z - 1, d) - xi(z, M - 1, d));
		}
		return sum - (2 * xi(0, M - 1, d) / Math.pow(d, M));
	}

	double drunkConductance(int k) {
		double sum = 0, tempSum = 0;
		for (int z = 1; z < k + 1; z++) {
			sum += c(z) * r(0, z - 1);
		}
		sum *= r(k, (int) M - 1) / r(0, (int) M - 1);
		for (int z = k + 1; z < M; z++) {
			tempSum += c(z) * r(z, (int) M - 1);
		}
		tempSum *= r(0, k - 1) / r(0, (int) M - 1);
		return sum + tempSum /*+ c(k) * r(0, k - 1) * r(k, (int) M - 1) / r(0, (int) M - 1)*/;
	}
	
	/** Drunk conductance take 2. */
	public double drunkerConductance(int k) {
		double sum = R(k);
		for (int z = 1; z < k; z++) {
			sum += c(z) * (R(z) - r(z, k - 1) * (r(k, (int) M - 1) + r(0, z - 1)) / r(0, (int) M - 1));
		}
		sum += (c(k) - c(0) - c(M)) * R(k);
		//sum += c(k) * R(k);
		for (int z = k + 1; z < M; z++) {
			sum += c(z) * (R(z) - r(k, z - 1) * (r(z, (int) M - 1) + r(0, k - 1)) / r(0, (int) M - 1));
		}
		return sum / 2;
	}
	
	public double R(int k) {
		return r(0, k - 1) * r(k, (int) M - 1) / r(0, (int) M - 1);
	}
	
	public double r(int a, int b) {
		double sum = 0;
		for (int i = a; i <= b; i++) {
			sum += Math.pow(d, M) / (choose(M - 1, i) * Math.pow(d - 1, M - i - 1));
		}
		return sum;
	}
	
	public double xi(double lo, double hi, double d) {
		double sum = 0;
		for (int k = (int) lo; k <= hi; k++) {
			sum += Math.pow(d, M) / ((choose(M - 1, k) * Math.pow(d - 1, M - k - 1))); // last one was M - k - 1
		}
		return sum;
	}
	
	/** Conductance at a vertex. */
	double c(double z) {
		return choose(M, z) * Math.pow(d - 1, M - z) / Math.pow(d, M); // "z" was M - z
	}
	
	public double fullToEven() {
		return Math.pow(d, M) * M * (d - 1) * resistanceEvenToFull() - evenToFull(); 
	}
	
	public double resistanceEvenToFull() {
		double sum = 0;
		for (int i = 0; i < M - (M / d) - 1; i++) {
			sum += 1 / (choose(M, i) * Math.pow(d - 1, i + 1) * (M - i));
//			System.out.println("asdf " + (choose(M, i) * Math.pow(d - 1, i + 1) * i));
//			System.out.println(sum);
		}
		
		return sum + 1 / ((1 / E2()) + (1 / (E1() + E3() + E4()))); 
	}
	
	double bob() {
		return (choose(M, M - (M / d)) * Math.pow(d - 1, M - (M / d))) - bobJr(); 
	}
	
	double bobJr() {
		return factorial(M) / Math.pow(factorial(M / d), d);
	}
	
	double E1() {
		return 1 / ((M - (M / d)) * bob());
	}
	
	public double E2() {
		return 1 / ((M - (M / d)) * bobJr());
	}
	
	double e3() {
		return 1 / ((d - 2) * (M - (M / d)) * bobJr());
	}
	
	double E3() {
		return (E1() * e3()) / (E1() + e3() + E2());
	}
	
	double E4() {
		return (E2() * e3()) / (E1() + e3() + E2());
	}
	
	double evenToFull() {
		double sum = 0;
		for (int z = 0; z < M / d; z++) {
			sum += c(z) * (xi(M / d, M - 1, d));
		}
		
		for (int z = (int) (M / d); z < M + 1; z++) {
			sum += c(z) * (xi(z, M - 1, d));
		}
		return sum;
	}
	
	public double blorg(int d) {
		double sum = 0;
		for (int z = 0; z < M + 1; z++) {
			for (int j = z; j < M; j++) { // j = 0 to z - 1 for empty
				sum += choose(M, z) * (Math.pow(d - 1, j + 1 - z) / choose(M - 1, j));
			}
		}
		return sum;
	}
	
	/** Drunkard's Walk: recursion. */
	public double drunkRecursion(int k) {
		
		double sum1 = 0;
		double innerSum;
		double sum2 = 0;
		
		for (int j = k; j < M; j++) {
			innerSum = 0;
			for (int z = 1; z < j + 1; z++) {
				innerSum += choose(M, z);
			}
			sum1 += innerSum / choose(M - 1, j);
		}
		for (int j = 1; j < k; j++) {
			innerSum = 0;
			for (int z = 1; z < j + 1; z++) {
				innerSum += choose(M, z); 
			}
			sum2 += innerSum / choose(M - 1, j);
		}
		return (sum(0, k - 1) * sum1 - sum(k, M - 1) * sum2) / sum(0, M - 1);
	}
	
	public double sum(double start, double end) {
		double sum = 0;
		for (int i = (int) start; i <= end; i++) {
			sum += 1 / choose(M - 1, i);
		}
		return sum;
	}
	
	/** Drunkard's Walk: electric networks. */
	public double drunkElectric(int k) {
		return (Math.pow(2, M - 1) + (choose(M, k) / 2) - 1) * beta(k) + part2(k) + part3(k);
	}
	
	public double part2(int k) {
		double d = 0;
		for (int z = 1; z <= k - 1; z++) {
			d += choose(M, z) * (beta(z) - (sum(z, k - 1) * (sum(k, M - 1) + sum(0, z - 1)))/(sum(0, M - 1))); 
		}
		return d / 2;
	}
	
	public double part3(int k) {
		double d = 0;
		for (int z = k + 1; z <= M - 1; z++) {
			d += choose(M, z) * (beta(z) - (sum(k, z - 1) * (	sum(0, k - 1) + sum(z, M - 1)))/(sum(0, M - 1))); 
		}
		return d / 2;
	}
	
	public double beta(double k) {
		return sum(0, k - 1) * sum(k, M - 1) / sum(0, M - 1);
	}
	
	/**
	 * Fill / Empty: conductance.
	 * @param s = starting number of balls 
	 * @param t = ending number of balls
	 * works for s > t
	 */
	public double fillEmptyConductance(int s, int t, int d) {
		double sum = 0;
		for (int i = 0; i < t; i++) {
			sum += c(i) * (xi(t, s - 1, d) + xi(i, t - 1, d) - xi(i, s - 1, d));
		}
		//System.out.println("belh " + sum);
		for (int i = t; i < s; i++) {
			sum += c(i) * (xi(t, s - 1, d) + xi(t, i - 1, d) - xi(i, s - 1, d));
			//System.out.println("i = " + i + ", " + c(d, i));
		}
		//System.out.println("here " + sum);
		for (int i = s; i < M + 1; i++) {
			sum += c(i) * (xi(t, s - 1, d) + xi(t, i - 1, d) - xi(s, i - 1, d));
		}
		//System.out.println("and here " + sum);
		return sum / 2;
	}
	
	public double fillConductance(int s, int t, int d) {
		double sum = 0;
		for (int i = 0; i < s; i++) {
			sum += c(i) * (xi(s, t - 1, d) + xi(i, s - 1, d) - xi(i, t - 1, d));
		}
		//System.out.println("belh " + sum);
		for (int i = s; i < t; i++) {
			sum += c(i) * (xi(s, t - 1, d) + xi(s, i - 1, d) - xi(i, t - 1, d));
			//System.out.println("i = " + i + ", " + c(d, i));
		}
		//System.out.println("here " + sum);
		for (int i = t; i < M + 1; i++) {
			sum += c(i) * (xi(s, t - 1, d) + xi(s, i - 1, d) - xi(t, i - 1, d));
		}
		//System.out.println("and here " + sum);
		return sum / 2;
	}
	
	public double emptyConductance(int d) {
		return 0;
	}
	
	/* Empty: electric networks. */
	public double emptyElectric(int d) {
		double sum = 0;;
		for (int z = 0; z <= M; z++) {
			sum += choose(M, z) * Math.pow(d - 1, z) / Math.pow(d, M) * (newSum(z, (int) M - 1, d) - newSum(0, z - 1, d));
		}
		return (newSum(0, (int) M - 1, d) + sum) / 2;
	}
	
	public double newSum(int lo, int hi, int d) {
		double sum = 0;
		for (int i = lo; i <= hi; i++) {
			sum += Math.pow(d, M) / ((choose(M - 1, i) * Math.pow(d - 1, i)));
		}
		return sum;
	}
	
	/* Empty: Recursion. 
	 * (M) sum from j = 0 to M - 1 of [d/(d-1)]^j / (j + 1). */
	public double emptyRecursion() {
		double sum = 0;
		for (int i = 0; i < M; i++) {
			sum += Math.pow(d / (d - 1), i) / (i + 1);
		}
		return M * sum;
	}
	
	/*
	 * Fill: Recursion. 
	 * M (d - 1) sum from j = 0 to j = M - 1 of (d^j) / (j + 1)
	 */
	public double fillRecursion() {
		double sum = 0;
		for (int i = 0; i < M; i++) {
			sum += Math.pow(d, i) / (i + 1);
		}
		return M * (d - 1) * sum;
	}
	
	public double choose(double n, double k) {
		return n == k || k == 0 ? 1 : factorial(n) / factorial(k) * 1 / factorial(n - k);
	}
	
	public double factorial(double x) {
		return x < 2 ? x : x * factorial(--x);
	}
	
}
