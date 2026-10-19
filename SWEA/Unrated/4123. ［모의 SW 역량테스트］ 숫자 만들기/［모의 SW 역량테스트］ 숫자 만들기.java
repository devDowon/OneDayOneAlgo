import java.io.*;
import java.util.*;

public class Solution {
    static enum Operator {
        PLUS, MINUS, MULTI, DIVIDE;

        public int operate(int a, int b) {
            switch(this) {
                case PLUS:
                    return a + b;
                case MULTI:
                    return a * b;
                case DIVIDE:
                    return a / b;
                case MINUS:
                    return a - b;
                default:
                    return -1;
            }
        }

        static public Operator of(int i) {
            return VALUES[i];
        }

        final static Operator[] VALUES = Operator.values();
    }

    // static Operator[] operations;
    static int[] opCount = new int[4];
    static Operator[] selected;
    static boolean[] isSelected;
    static int[] nums;

    static int N;
    static int max, min;
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        int T = Integer.parseInt(br.readLine());
        int tc = 0;
        while(tc++ < T) {
            N = Integer.parseInt(br.readLine());

            StringTokenizer st = new StringTokenizer(br.readLine());
            opCount = new int[4];
            for (int i = 0; i < 4; i++) {
                opCount[i] = Integer.parseInt(st.nextToken());
            }

            nums = new int[N];
            st = new StringTokenizer(br.readLine());
            for(int i = 0; i < N; i++) {
                nums[i] = Integer.parseInt(st.nextToken());
            }

            selected = new Operator[N - 1];
            isSelected = new boolean[N - 1];

            max = Integer.MIN_VALUE;
            min = Integer.MAX_VALUE;
            dfs(0);
            int ans = max - min;
            sb.append("#" + tc + " " + ans + "\n");
        }

        br.close();
        System.out.print(sb);
    }

    public static void dfs(int current) {
        if(current == N - 1) {
            int value = calculate();
            max = Math.max(max, value);
            min = Math.min(min, value);
            return;
        }

        for (int op = 0; op < 4; op++) {
            if (opCount[op] == 0) continue;
            opCount[op]--;
            selected[current] = Operator.of(op);
            dfs(current + 1);
            opCount[op]++;
        }
    }

    public static int calculate() {
        int value = nums[0];
        for(int i = 0; i < N - 1; i++) {
            value = selected[i].operate(value, nums[i + 1]);
        }
        return value;
    }
}
