package com.exam.rental;
interface VehicleProcessor { void process(); }
class Car implements VehicleProcessor {
    private String name;
    public Car(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础车辆：" + name); }
}
class Truck implements VehicleProcessor {
    private String name;
    public Truck(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级车辆：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        VehicleProcessor[] processors = { new Car("丰田卡罗拉"), new Truck("本田雅阁") };
        for (VehicleProcessor p : processors) { p.process(); }
    }
}
