import java.util.*;
import java.io.*;

class Solution
{
	public static void main(String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		int T;
		T=Integer.parseInt(br.readLine());

		for(int test_case = 1; test_case <= T; test_case++)
		{
			String[] lines = br.readLine().split(" ");
			int ans = 0;
			for(String strNum : lines) {
				int num = Integer.parseInt(strNum);
				if(num % 2 == 1) ans += num;
			}
			System.out.println(String.format("#%d %d", test_case, ans));
		}
	}
}