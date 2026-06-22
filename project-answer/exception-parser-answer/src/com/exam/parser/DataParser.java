package com.exam.parser;

public class DataParser {
    public void parseScores(String[] rawData) {
        for (String data : rawData) {
            // FIXME: 这里遇到 null 或字母会抛出未捕获的运行时异常，导致循环中断
            int score = Integer.parseInt(data);
            System.out.println("成功解析分数: " + score);
        }
    }
}
