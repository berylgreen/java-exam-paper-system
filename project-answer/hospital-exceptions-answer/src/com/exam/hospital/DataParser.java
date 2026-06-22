package com.exam.hospital;

public class DataParser {
    // 原始设计：没有异常处理，遇到非法数据直接导致程序崩溃
    public void parseData(String[] data) {
        for (String s : data) {
            int value = Integer.parseInt(s);
            System.out.println("Parsed: " + value);
        }
    }
}
