package com.exam.weather;
interface WeatherDataProcessor { void process(); }
class TypeA implements WeatherDataProcessor {
    private String name;
    public TypeA(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础气象数据：" + name); }
}
class TypeB implements WeatherDataProcessor {
    private String name;
    public TypeB(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级气象数据：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        WeatherDataProcessor[] processors = { new TypeA("温度数据"), new TypeB("湿度数据") };
        for (WeatherDataProcessor p : processors) { p.process(); }
    }
}
