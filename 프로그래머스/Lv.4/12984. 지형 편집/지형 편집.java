import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Solution {
    public long solution(int[][] land, int P, int Q) {
        long answer = Long.MAX_VALUE;
        
        long[] blocks = new long[land.length * land.length];
        long totalBlock = 0;
        long totalCell = 0;
        
        int idx = 0;
        for(int[] row : land) {
        	for(int height : row) {
        		blocks[idx++] = height;
        		totalBlock += height;
        		totalCell++;
        	}
        }
        
        Arrays.sort(blocks);
        
        List<long[]> map = new ArrayList<>();
        int count = 0;
        long currentHeight = blocks[0];

        for(int i = 0; i < blocks.length; i++) {
        	long height = blocks[i];
        	if(height != currentHeight) {
        		map.add(new long[] {currentHeight, count});
        		currentHeight = height;
        		count = 0;
        	}
        	
        	count++;
        	
        	if(i == blocks.length - 1) {
        		map.add(new long[] {currentHeight, count});
        	}
        }
        
        // prefixCount | 지금 높이보다 낮은 칸 수
        // prefixSum   | 지금보다 높이가 낮은 칸의 블럭수
        long prefixCount = 0;
        long prefixSum = 0;
        for(long[] block : map) {
        	long height = block[0];
        	long blockCount = block[1];
        	
        	long highBlockCnt = totalCell - prefixCount;
        	
        	long qBlock = totalBlock - prefixSum;
        	long qCost = (qBlock - height * highBlockCnt) * Q;
        	
        	long pCost = (height * prefixCount - prefixSum) * P;
        	
        	answer = Math.min(qCost + pCost, answer);
        	
        	prefixCount += blockCount;
        	prefixSum += (height * blockCount);
        }
        
        return answer;
    }
}