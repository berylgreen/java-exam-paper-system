package com.exam.library;

public class DataParser {
    public void parseData(String[] data) {
        for (String item : data) {
            int value = Integer.parseInt(item);
            System.out.println("成功解析数据: " + value);
        }
        System.out.println("全部数据处理完毕");
    }
}
