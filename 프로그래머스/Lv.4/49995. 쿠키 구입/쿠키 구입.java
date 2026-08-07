import java.util.*;
import java.io.*;

class Solution {
	int[] sumlist;
	int cookieSize;
	public void init(int[] cookie) {
		sumlist = new int[cookieSize + 1];
		sumlist[1] = cookie[1];
		for(int i = 0; i < cookie.length; i++) {
			sumlist[i + 1] = sumlist[i] + cookie[i];
		}
	}
	
	public int findMaxCookie(int mid) {
		int ans = 0;
		int L = mid;
		int R = mid + 1;
		while(L >= 1 && R <= cookieSize) {
			int leftAns = sumlist[mid] - sumlist[L - 1];
			int rightAns = sumlist[R] - sumlist[mid];
			if(leftAns == rightAns)  {
				ans = Math.max(leftAns, ans);
				if(L > 1) L--;
				else R++;
			}
			else if(leftAns > rightAns) {
				if(R == cookieSize) L--;
				else R++;
			}
			else {
				if(L == 1) R++;
				else L--;
			}
			
			if(L < 1 || R > cookieSize) break;
		}
		return ans;
	}
	
	public int solution(int[] cookie) {
        if(cookie.length == 1) return 0;
		int answer = -1;
		cookieSize = cookie.length;
		init(cookie);
		for(int i = 1; i <= cookieSize - 1; i++) {
			answer = Math.max(answer, findMaxCookie(i));
		}
		return answer;
	}
}