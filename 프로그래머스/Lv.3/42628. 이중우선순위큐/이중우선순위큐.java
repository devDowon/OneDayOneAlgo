import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

class Solution {
	Queue<Integer> large = new PriorityQueue<>(Comparator.reverseOrder());
	Queue<Integer> small = new PriorityQueue<>();
	Map<Integer, Integer> numCount = new HashMap<>();
	
	enum OperationType {
		INSERT, DELETE_LARGE, DELETE_SMALL
	}
	
	static class Operation {
		OperationType type;
		int num;
		
		public Operation(OperationType type, int num) {
			super();
			this.type = type;
			this.num = num;
		}
	}
	
	public Operation parseOperation(String operation) {
		String[] parsedOperation = operation.split(" ");
		int num = Integer.parseInt(parsedOperation[1]);
		if(parsedOperation[0].equals("I")) {
			return new Operation(OperationType.INSERT, num);
		}
		else {
			if(num < 0) {
				return new Operation(OperationType.DELETE_SMALL, num);
			}
			else {
				return new Operation(OperationType.DELETE_LARGE, num);
			}
		}
	}
	
	public void insert(int num) {
		large.add(num);
		small.add(num);
	}
	
	void syncQueue() {
		while(!small.isEmpty()) {
			int num = small.peek();
			if(numCount.get(num) == 0) small.poll();
			else break;
		}
		
		while(!large.isEmpty()) {
			int num = large.peek();
			if(numCount.get(num) == 0) large.poll();
			else break;
		}
	}
	
	public int getTargetNum(OperationType type) {
		if(type == OperationType.DELETE_LARGE) {
			return large.peek();
		}
		else {
			return small.peek();
		}
	}
	
	public void delete(OperationType type) {
		syncQueue();
		if(large.isEmpty() || small.isEmpty()) return;
		int targetNum = getTargetNum(type);
		if(numCount.get(targetNum) >= 1) {
			if(type == OperationType.DELETE_LARGE) large.poll();
			else small.poll();
			
			numCount.put(targetNum, numCount.get(targetNum) - 1);
		}
	}
	
	public int[] solution(String[] operations) {
        int[] answer = {};
        for(String operation : operations) {
        	Operation op = parseOperation(operation);
        	if(op.type == OperationType.INSERT) {
        		insert(op.num);
        		numCount.put(op.num, numCount.getOrDefault(op.num, 0) + 1);
        	}
        	else {
        		delete(op.type);
        	}
        }
        
        syncQueue();
        if(!large.isEmpty() && !small.isEmpty()) {
        	answer = new int[] {large.peek().intValue(), small.peek().intValue()};
        }
        else {
        	answer = new int[] {0, 0};        	
        }
        return answer;
    }
}