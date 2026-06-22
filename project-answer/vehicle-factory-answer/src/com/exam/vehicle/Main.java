package com.exam.vehicle;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        VehicleProcessor[] processors = { new Car("汽车"), new Truck("卡车") };
        for (VehicleProcessor p : processors) { p.process(); }
    }
}
