import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

class Solution {
	Queue<Pos> q = new PriorityQueue<>();
	int[][][] minWeight;

	public enum Direction {
		UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);

		final int dx, dy;

		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}

	public class Pos implements Comparable<Pos> {
		Direction d;
		int x;
		int y;
		int w;

		public Pos(int x, int y, Direction d, int w) {
			super();
			this.d = d;
			this.x = x;
			this.y = y;
			this.w = w;
		}

		@Override
		public int compareTo(Solution.Pos o) {
			return Integer.compare(w, o.w);
		}
	}

	public int solution(int[][] board) {
		int answer = Integer.MAX_VALUE;
		minWeight = new int[board.length][board.length][4];
		for (int i = 0; i < minWeight.length; i++) {
			for(int j = 0; j < minWeight[i].length; j++)
				Arrays.fill(minWeight[i][j], Integer.MAX_VALUE);
		}
		q.offer(new Pos(0, 0, Direction.RIGHT, 0));
		q.offer(new Pos(0, 0, Direction.DOWN, 0));
		minWeight[0][0][Direction.RIGHT.ordinal()] = 0;
		minWeight[0][0][Direction.DOWN.ordinal()] = 0;

		while (!q.isEmpty()) {
			Pos p = q.poll();

			for (Direction dir : Direction.values()) {
				int nx = p.x + dir.dx;
				int ny = p.y + dir.dy;

				if (nx < 0 || nx >= board.length || ny < 0 || ny >= board.length)
					continue;
				if (board[nx][ny] == 1)
					continue;

				int weight = (dir == p.d) ? 100 : 500 + 100;
				if (minWeight[nx][ny][dir.ordinal()] >= p.w + weight) {
					minWeight[nx][ny][dir.ordinal()] = p.w + weight;
					q.offer(new Pos(nx, ny, dir, p.w + weight));
				}
			}
		}
		
		for(int i = 0; i < 4; i++) {
			answer = Math.min(answer, minWeight[board.length - 1][board.length - 1][i]);
		}
		return answer;
	}
}