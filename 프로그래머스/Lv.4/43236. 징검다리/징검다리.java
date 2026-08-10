import java.util.Arrays;

class Solution {
	public int getRemovedRock(int minDist, int[] rocks, int distance) {
		int removed = 0;
		int idx = 1;
		int lastRock = 0;

		if (rocks[0] < minDist) {
			removed++;
			lastRock = -1;
		}

		while (idx < rocks.length) {
			int last = (lastRock == -1 ? 0 : rocks[lastRock]);
			int dist = rocks[idx] - last;
			if (dist < minDist) {
				removed++;
			} else {
				lastRock = idx;
			}
			idx++;
		}

		if (distance - (lastRock == -1 ? 0 : rocks[lastRock]) < minDist) {
			removed++;
		}

		return removed;
	}

	public int solution(int distance, int[] rocks, int n) {
		int answer = 0;

		Arrays.sort(rocks);

		int L = 1;
		int R = distance;
		int mid = L + (R - L + 1) / 2;
		while (L < R) {
			mid = L + (R - L + 1) / 2;

			int removed = getRemovedRock(mid, rocks, distance);
			if (removed <= n)
				L = mid;
			else
				R = mid - 1;
		}

		return L;
	}
}