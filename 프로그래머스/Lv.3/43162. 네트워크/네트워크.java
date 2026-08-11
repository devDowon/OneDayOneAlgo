import java.util.ArrayList;
import java.util.List;

class Solution {
	int[] nodesGroup;
	
	public void dfs(int idx, int groupNum, int[][] computers) {
		for(int i = 0; i < computers[idx].length; i++) {
			if(i == idx) continue;
			if(computers[idx][i] == 0) continue;
			if(nodesGroup[i] != 0) continue;
			
			nodesGroup[i] = groupNum;
			dfs(i, groupNum, computers);
		}
	}
	
    public int solution(int n, int[][] computers) {
        int answer = 0;
        nodesGroup = new int[n];
        for(int i = 0; i < nodesGroup.length; i++) {
        	if(nodesGroup[i] != 0) continue;
        	
        	dfs(i, ++answer, computers);
        }
        return answer;
    }
}