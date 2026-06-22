package com.exam.parser;

public class Main {
    public static void main(String[] args) {
        String[] rawData = {"85", "90", "invalid", "100", null, "75"};
        DataParser parser = new DataParser();
        
        System.out.println("--- 开始解析 ---");
        parser.parseScores(rawData);
        System.out.println("--- 解析完成 ---");
    }
}
