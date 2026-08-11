class Solution {
    int[] parent;
    
    // Find: 경로 압축 적용
    public int find(int x) {
        if (parent[x] == x) return x;
        return parent[x] = find(parent[x]); // 경로 압축
    }
    
    // Union
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX != rootY) {
            parent[rootX] = rootY;
        }
    }
    
    public int solution(int n, int[][] computers) {
        parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i; // 초기엔 자기 자신이 루트
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) { // i==j는 스킵, 대칭이라 j=i+1부터
                if (computers[i][j] == 1) {
                    union(i, j);
                }
            }
        }
        
        // 서로 다른 루트의 개수 = 네트워크 개수
        int answer = 0;
        for (int i = 0; i < n; i++) {
            if (find(i) == i) answer++;
        }
        return answer;
    }
}