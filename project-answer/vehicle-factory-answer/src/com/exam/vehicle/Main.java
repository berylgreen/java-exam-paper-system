package com.exam.vehicle;
interface VehicleProcessor { void process(); }
class Car implements VehicleProcessor {
    private String name;
    public Car(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础交通工具：" + name); }
}
class Truck implements VehicleProcessor {
    private String name;
    public Truck(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级交通工具：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        VehicleProcessor[] processors = { new Car("汽车"), new Truck("卡车") };
        for (VehicleProcessor p : processors) { p.process(); }
    }
}
