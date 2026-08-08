import java.util.*;
import java.io.*;

public class Solution {
	static boolean[] prime = makePrime(30);
	
	static boolean[] makePrime(int n) {
		boolean[] s = new boolean[n + 1];
		Arrays.fill(s, true);
		s[0] = false;
		s[1] = false;
		for(int i = 2; i <= n; i++) {
			for(int j = i * 2; j <= n; j+=i) {
				s[j] = false;
			}
		}
		
		return s;
	}
	
	static long nCr(int n, int r) {
		long result = 1;
		for(int i = 0; i < r; i++) {
			result = result * (n - i) / (i + 1);
		}
		return result;
	}
	
	static double getNotPrimeScoreProbability(double p) {
		double total = 0;
		for(int i = 0; i <= 30; i++) {
			if(!prime[i]) {
				double t = nCr(30, i) * Math.pow(p, i) * Math.pow(100 - p, 30 - i) / Math.pow(100, i) / Math.pow(100, 30 - i);
				total += t;
			}
		}
		return total;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		int T = Integer.parseInt(br.readLine().trim());

		for (int tc = 1; tc <= T; tc++) {
			StringTokenizer st = new StringTokenizer(br.readLine());

			int P_A = Integer.parseInt(st.nextToken());
			int P_B = Integer.parseInt(st.nextToken());
			
			
			double notPrimeP_A = getNotPrimeScoreProbability(P_A);
			double notPrimeP_B = getNotPrimeScoreProbability(P_B);
			
			double ans = 1 - notPrimeP_A * notPrimeP_B;

			bw.write(String.format("#%d %.5f", tc, ans));
			bw.newLine();
			bw.flush();
		}

		br.close();
		bw.close();
	}
}
