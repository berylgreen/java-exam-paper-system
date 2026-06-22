package com.exam.rental;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        VehicleProcessor[] processors = { new Car("丰田卡罗拉"), new Truck("本田雅阁") };
        for (VehicleProcessor p : processors) { p.process(); }
    }
}
