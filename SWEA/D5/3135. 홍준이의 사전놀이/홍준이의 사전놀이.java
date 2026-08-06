import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Solution {

	public static class UserSolution {
		Node[] alphabetList;
		public UserSolution() {}
		public static class Node {
			private int c;
			Node parentNode;
			List<Node> childNodeList = new ArrayList<>();
			private int count = 0;
			
			public Node(int c, Node p) {
				this.c = c;
				this.parentNode = p;
			}
			
			public void addChild(Node c) {
				childNodeList.add(c);
			}
			public int getCount() {
				return count;
			}
			public void addCount() {
				this.count += 1;
			}
			
			int getIdx() {
				return c;
			}
			
			char getChar() {
				return (char)c;
			}
		}
		
		int charToInt(char c) {
			return c - 'a';
		}
		
		char intToChar(int n) {
			return (char)(n + 'a');
		}
		
		void init() {
			alphabetList = new Node[26];
			for(int i = 0; i < 26; i++) {
				alphabetList[i] = new Node(i, null);
			}
		}
		
		void insert(int buffer_size, String buf) {
			char[] char_buf = buf.toCharArray();
			Node p = null;
			for(char c : char_buf) {
				int charIdx = charToInt(c);
				if(p == null) {
					p = alphabetList[charIdx];
					p.addCount();
				}
				else {
					List<Node> childs = p.childNodeList;
					boolean found = false;
					for(Node child : childs) {
						if(child.getIdx() == charIdx) {
							child.addCount();
							p = child;
							found = true;
							break;
						}
					}
					if(!found) {
						Node newChild = new Node(charIdx, p);
						newChild.addCount();
						p.addChild(newChild);
						p = newChild;
					}
				}
			}
		}
		
		int query(int buffer_size, String buf) {
			char[] char_buf = buf.toCharArray();
			Node target = null;
			for(char c : char_buf) {
				int charIdx = charToInt(c);
				if(target == null) {
					Node node = alphabetList[charIdx];
					target = node;
				}
				else {
					List<Node> childs = target.childNodeList;
					boolean found = false;
					for(Node child : childs) {
						if(child.getIdx() == charIdx) {
							target = child;
							found = true;
							break;
						}
					}
					if(!found) return 0;
				}
			}
			
			return target.getCount();
		}
	}

	
	public static void main(String[] args) {
		InputStream inputStream = System.in;
		OutputStream outputStream = System.out;
		InputReader in = new InputReader(inputStream);
		PrintWriter out = new PrintWriter(outputStream);

		UserSolution dictManager = new UserSolution();
		
		for (int TestCase = in.nextInt(), tc = 1; tc <= TestCase; tc = tc + 1) {

			int Query_N = in.nextInt();

			out.print("#" + tc);

			dictManager.init();

			for (int i = 1; i <= Query_N; i++) {
				int type = in.nextInt();

				if (type == 1) {
					String buf = in.next();
					dictManager.insert(buf.length(), buf);
				}
				else {
					String buf = in.next();
					int answer = dictManager.query(buf.length(), buf);
					out.print(" " + answer);
				}
			}
			out.println("");
		}
		out.close();
	}

	static class InputReader {
		public BufferedReader reader;
		public StringTokenizer tokenizer;

		public InputReader(InputStream stream) {
			reader = new BufferedReader(new InputStreamReader(stream), 32768);
			tokenizer = null;
		}

		public String next() {
			while (tokenizer == null || !tokenizer.hasMoreTokens()) {
				try {
					tokenizer = new StringTokenizer(reader.readLine());
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			return tokenizer.nextToken();
		}

		public int nextInt() {
			return Integer.parseInt(next());
		}

	}
}