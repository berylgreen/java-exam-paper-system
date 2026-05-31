package com.exam.lambda;

import java.util.ArrayList;
import java.util.List;

public class DataFilter {
    
    // FIXME: 这种硬编码针对每种条件的过滤方式极为死板
    public List<Integer> filterEvenNumbers(List<Integer> numbers) {
        List<Integer> result = new ArrayList<>();
        for (Integer n : numbers) {
            if (n % 2 == 0) result.add(n);
        }
        return result;
    }

    public List<Integer> filterPositiveNumbers(List<Integer> numbers) {
        List<Integer> result = new ArrayList<>();
        for (Integer n : numbers) {
            if (n > 0) result.add(n);
        }
        return result;
    }
}
