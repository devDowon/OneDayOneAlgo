import java.util.*;
import java.io.*;

class Solution {
	static int[] c_visit;
	static int[] r_visit;
	static int[] g_visit;
	static Friction[][] map;
	static boolean[][] visited;
	static boolean found;
	
	public static class Friction {
		int ja;
		int mo;
		boolean friction;
		
		public Friction(int ja, int mo, boolean friction) {
			this.ja = ja;
			this.mo = mo;
			this.friction = friction;
		}
		
		public boolean checkComplete() {
			if(this.friction && this.ja != -1 && this.mo != -1) {
				return true;
			}
			else if(!this.friction && this.ja != -1) {
				return true;
			}
			
			return false;
		}
		
		@Override
		public String toString() {
			if(this.friction) {
				return String.format("%d/%d", this.ja, this.mo);
			}
			else {
				return ""+ja;
			}
		}
	}
	
	static int getGroupNum(int row, int col) {
		return 2 * (row / 2) + col / 3;
	}
	
	static int getPossibleNum(int row, int col) {
		int visited = r_visit[row] | c_visit[col] | g_visit[getGroupNum(row, col)];
		int mask = 0b1111111110;
		return (~visited) & mask;
	}
	
	static boolean checkAll() {
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 6; j++) {
				if(!map[i][j].checkComplete()) return false;
			}
		}
		return true;
	}
	
	static int[] findMinPoint() {
		int minRow = -1;
		int minCol = -1;
		int minPossible = Integer.MAX_VALUE;
		
		for(int i = 0; i < 6; i++) {
			for(int j = 0; j < 6; j++) {
				if(visited[i][j]) continue;
				int possible = Integer.bitCount(getPossibleNum(i, j));
				if(minPossible > possible) {
					minRow = i;
					minCol = j;
					minPossible = possible;
				}
			}
		}
		
		return new int[] {minRow, minCol};
	}
	
	static void dfs(int row, int col) {
		Friction f = map[row][col];
		if(f.checkComplete()) {
			visited[row][col] = true;
			int[] nxt = findMinPoint();
			if(nxt[0] == -1 && nxt[1] == -1) {
				found = checkAll();
				return;
			}
			dfs(nxt[0], nxt[1]);
			if(found) return;
			visited[row][col] = false;
			return;
		}
		
		int unvisited = getPossibleNum(row, col);
		if(unvisited == 0) return;
		
		unvisited = unvisited >> 1;
		List<Integer> possibleList = new ArrayList<>();
		int idx = 1;
		while(unvisited > 0) {
			if((unvisited & 1) == 1) {
				possibleList.add(idx);
			}
			
			idx++;
			unvisited = (unvisited >> 1);
		}
		for(int n : possibleList) {
			if(f.friction) {
				//ja 먼저 -> 다음 mo
				if(f.ja == -1) {
					//ja 설정
					if(f.mo != -1 && f.mo < n) continue;
					f.ja = n;
					r_visit[row] += (1 << n);
					c_visit[col] += (1 << n);
					g_visit[getGroupNum(row, col)] += (1 << n);

					dfs(row, col);
					if(found) return;
					
					r_visit[row] -= (1 << n);
					c_visit[col] -= (1 << n);
					g_visit[getGroupNum(row, col)] -= (1 << n);
					f.ja = -1;
				}
				else if(f.mo == -1) {
					if(f.ja > n) continue;
					f.mo = n;
					r_visit[row] += (1 << n);
					c_visit[col] += (1 << n);
					g_visit[getGroupNum(row, col)] += (1 << n);
					visited[row][col] = true;
					
					int[] nxt = findMinPoint();
					if(nxt[0] == -1 && nxt[1] == -1) {
						found = checkAll();
						return;
					}
					dfs(nxt[0], nxt[1]);
					if(found) return;
					
					r_visit[row] -= (1 << n);
					c_visit[col] -= (1 << n);
					g_visit[getGroupNum(row, col)] -= (1 << n);
					visited[row][col] = false;
					
					f.mo = -1;
				}
			}
			else {
				f.ja = n;
				
				r_visit[row] += (1 << n);
				c_visit[col] += (1 << n);
				g_visit[getGroupNum(row, col)] += (1 << n);
				visited[row][col] = true;

				int[] nxt = findMinPoint();
				if(nxt[0] == -1 && nxt[1] == -1) {
					found = checkAll();
					return;
				}
				dfs(nxt[0], nxt[1]);
				if(found) return;
				
				r_visit[row] -= (1 << n);
				c_visit[col] -= (1 << n);
				g_visit[getGroupNum(row, col)] -= (1 << n);
				visited[row][col] = false;
				
				f.ja = -1;
			}
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sb = new StringBuilder();
		
		int T = Integer.parseInt(br.readLine());
		int tc = 0;
		while(tc++ < T) {
			
			c_visit = new int[6];
			r_visit = new int[6];
			g_visit = new int[6];
			map = new Friction[6][6];
			visited = new boolean[6][6];
			
			Arrays.fill(c_visit, 0);
			Arrays.fill(r_visit, 0);
			Arrays.fill(g_visit, 0);
			
			for(int i = 0; i < 6; i++) {
				String line = br.readLine();
				String[] parsedLine = line.split(" ");
				
				for(int j = 0; j < 6; j++) {
					String num = parsedLine[j];
					if(num.contains("/")) {
						String strJa = num.split("/")[0];
						String strMo = num.split("/")[1];
						
						
						int ja = strJa.equals("-") ? -1 : Integer.parseInt(strJa);
						int mo = strMo.equals("-") ? -1 : Integer.parseInt(strMo);
						map[i][j] = new Friction(ja, mo, true);
						
						if(ja != -1 ) {
							r_visit[i] += (1 << ja);
							c_visit[j] += (1 << ja);
							g_visit[getGroupNum(i, j)] += (1 << ja);
						}
						
						if(mo != -1) {
							r_visit[i] += (1 << mo);
							c_visit[j] += (1 << mo);
							g_visit[getGroupNum(i, j)] += (1 << mo);
						}
						
						if(ja != -1 && mo != -1) {
							visited[i][j] = true;
						}
					}
					else {
						int n = num.equals("-") ? -1 : Integer.parseInt(num);
						if(n != -1) {
							r_visit[i] += (1 << n);
							c_visit[j] += (1 << n);
							g_visit[getGroupNum(i, j)] += (1 << n);							
						}

						map[i][j] = new Friction(n, 1, false);
						if(n != -1) {
							visited[i][j] = true;
						}
					}
				}
			}
			
			found = false;
			int[] nxt = findMinPoint();
			dfs(nxt[0], nxt[1]);
			
			sb.append("#").append(tc).append("\n");
			for(int i = 0; i < 6; i++) {
				for(int j = 0; j < 6; j++) {
					sb.append(map[i][j]).append(" ");
				}
				sb.append("\n");
			}
		}
		
		System.out.println(sb);
		
		br.close();
	}
}