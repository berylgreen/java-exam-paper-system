package com.exam.smarthome;
public class Controller implements DeviceProcessor {
    private String name;
    public Controller(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理高级设备：" + name); }
}
