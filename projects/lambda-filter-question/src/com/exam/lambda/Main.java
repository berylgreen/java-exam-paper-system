package com.exam.lambda;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> data = Arrays.asList(-5, -2, 0, 3, 4, 8, 10);
        DataFilter filter = new DataFilter();
        
        System.out.println("偶数: " + filter.filterEvenNumbers(data));
        System.out.println("正数: " + filter.filterPositiveNumbers(data));
        
        // TODO: 重构后，要求可以使用下面类似的 Lambda 语法
        // System.out.println("大于5: " + filter.filter(data, n -> n > 5));
    }
}
