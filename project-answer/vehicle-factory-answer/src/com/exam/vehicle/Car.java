package com.exam.vehicle;
public class Car implements VehicleProcessor {
    private String name;
    public Car(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础交通工具：" + name); }
}
