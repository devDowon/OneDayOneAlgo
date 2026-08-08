import java.util.*;


class Solution {
	Map<Long, Long> rooms;
	public long findRoom(long idx) {
		if(rooms.containsKey(idx)) {
			long root = findRoom(rooms.get(idx));
			rooms.put(idx, root);
    		return root;
    	}
    	else {
    		rooms.put(idx, idx + 1);
    		return idx;
    	}
	}
	
    public long[] solution(long k, long[] room_number) {
        long[] answer = new long[room_number.length];
        
        rooms = new HashMap<>();
        int cnt = 0;
        for(long idx : room_number) {
        	long nxt = findRoom(idx);
        	rooms.put(idx, nxt + 1);
        	answer[cnt++] = nxt;
        }
        
        return answer;
    }
}