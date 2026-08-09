class Solution {
    public int solution(int n) {
        int answer = 0;
        int[] ans = new int[n + 1];
        ans[0] = 1;
        ans[1] = 1;
        for(int i = 2; i <= n; i++) {
        	int sum = 0;
        	for(int j = 0; j < i; j++) {
        		sum += ans[j] * ans[i - j - 1];
        	}
        	ans[i] = sum;
        }
        answer = ans[n];
        return answer;
    }
}