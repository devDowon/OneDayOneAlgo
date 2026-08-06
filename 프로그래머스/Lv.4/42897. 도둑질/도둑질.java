import java.util.*;
import java.io.*;

class Solution {
    public int solution(int[] money) {
        int answer = 0;

        int current_rub_zero = money[0];
        int current_norub_zero = 0;
        int current_rub_withoutZero = 0;
        int current_norub_withoutZero = 0;
        for(int i = 1; i < money.length; i++) {
        	int next_rub_zero = current_norub_zero + money[i];
        	int next_norub_zero = Math.max(current_rub_zero, current_norub_zero);
        	
        	int next_rub_withoutZero = current_norub_withoutZero + money[i];
        	int next_norub_withoutZero = Math.max(current_rub_withoutZero, current_norub_withoutZero);
        	
            current_rub_zero = next_rub_zero;
            current_norub_zero = next_norub_zero;
            current_rub_withoutZero = next_rub_withoutZero;
            current_norub_withoutZero = next_norub_withoutZero;
        }

        int withoutZeroMaxValue = Math.max(current_norub_withoutZero, current_rub_withoutZero);
        int withZeroMaxValue = Math.max(current_norub_zero, current_rub_zero - money[money.length - 1]);
        answer = Math.max(answer, withZeroMaxValue);
        answer = Math.max(answer, withoutZeroMaxValue);
        return answer;
    }
    
}