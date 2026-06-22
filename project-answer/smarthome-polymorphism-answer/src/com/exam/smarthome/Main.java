package com.exam.smarthome;
interface DeviceProcessor { void process(); }
class Sensor implements DeviceProcessor {
    private String name;
    public Sensor(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础设备：" + name); }
}
class Controller implements DeviceProcessor {
    private String name;
    public Controller(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级设备：" + name); }
}
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        DeviceProcessor[] processors = { new Sensor("智能灯"), new Controller("智能空调") };
        for (DeviceProcessor p : processors) { p.process(); }
    }
}
