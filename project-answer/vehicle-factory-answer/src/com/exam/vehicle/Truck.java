package com.exam.vehicle;
public class Truck implements VehicleProcessor {
    private String name;
    public Truck(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级交通工具：" + name); }
}
