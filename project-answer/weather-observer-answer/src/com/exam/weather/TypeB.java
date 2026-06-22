package com.exam.weather;
public class TypeB implements WeatherDataProcessor {
    private String name;
    public TypeB(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级气象数据：" + name); }
}
