import java.util.*;
import java.io.*;

/*
조건
n종류 동전
가치 V 색 C
V_1 = 1
V_n = V_n-1의 배수
1<=n<=60

입력
T
n
v_1 v_2 ... v_n
출력
은행에 동전을 달라고 요청해야 하는 최소 횟수


아이디어
다음 항은 항상 이전항의 배수 -> n진법처럼 생각해서 해보면?
v1은 1개 v2는 두개 ...
-> 이건 안됨. v_n/v_n-1 보다 갯수를 크게 못함.

그럼 v_n/v_n-1이 작은것부터 갯수를 설정하면?
한번 방문으로 표현가능한 갯수 = r_n개
k날에 r_n^k개의 표현이 모든 동전에 다르게 매칭가능하면? 가능함 // (0, 0, ..., 0) 은 안되니까 r_n^k - 1개임 풀다가 깨달음

 */
class Solution
{
    static double rList[];
	public static void main(String args[]) throws Exception
	{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

		int T = Integer.parseInt(br.readLine());

		for(int test_case = 1; test_case <= T; test_case++)
		{
            int N = Integer.parseInt(br.readLine());
            
            rList = new double[N - 1];
            StringTokenizer st = new StringTokenizer(br.readLine());
            long lastV = Long.parseLong(st.nextToken());
            for(int i = 0; i < N - 1; i++) {
                long currentV = Long.parseLong(st.nextToken());
                rList[i] = currentV / lastV;
                lastV = currentV;
            }
            
            if(N == 1) {
                sb.append("#" + test_case + " " + 1 + "\n");
                continue;
            }

            sb.append("#" + test_case + " ");
            Arrays.sort(rList);
            loop:
            for(int day = 1; day <= 60; day++) {
                for(int i = 0; i < N - 1; i++) {
                    // 가능한 표현수 Math.pow(rList[i], day) - 1
                    // 앞에서 미리 사용한 표현 수 i
                    // Math.pow(rList[i], day) - 1 - i -> 지금 동전이 사용가능한 표현수
                    if(Math.pow(rList[i], day) - 1 - i <= 0) {
                        continue loop;
                    }
                }

                sb.append(day + "\n");
                break;
            }
		}

        System.out.print(sb);
        br.close();
	}
}