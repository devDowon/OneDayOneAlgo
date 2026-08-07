import java.util.HashMap;
import java.util.Map;

class Solution {
	Node root;
	public class Node {
		Node parent;
		Map<Character, Node> childs = new HashMap<>();
		int wordCount = 0;
	}
	
	public void init(String[] words) {
		root = new Node();
		for(String word: words) {
			Node current = root;
			char[] charWord = word.toCharArray();
			for(char c : charWord) {
				current.childs.putIfAbsent(c, new Node());
				current = current.childs.get(c);
				current.wordCount++;
			}
		}
	}
	
	public int solution(String[] words) {
		int answer = 0;
		
		init(words);
		
		for(String word: words) {
			Node current = root;
			char[] charWord = word.toCharArray();
			for(char c : charWord) {
				if(current.childs.get(c).wordCount == 1) {
					answer++;
					break;
				}
				else {
					current = current.childs.get(c);
					answer++;
				}
			}
		}
		return answer;
	}
}