package com.exam.weather;
public class TypeA implements WeatherDataProcessor {
    private String name;
    public TypeA(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础气象数据：" + name); }
}
