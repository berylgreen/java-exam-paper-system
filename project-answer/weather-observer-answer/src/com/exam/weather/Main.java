package com.exam.weather;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        WeatherDataProcessor[] processors = { new TypeA("温度数据"), new TypeB("湿度数据") };
        for (WeatherDataProcessor p : processors) { p.process(); }
    }
}
