package com.exam.smarthome;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DeviceProcessor[] processors = { new Sensor("智能灯"), new Controller("智能空调") };
        for (DeviceProcessor p : processors) { p.process(); }
    }
}
