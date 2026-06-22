package com.exam.smarthome;
public class Sensor implements DeviceProcessor {
    private String name;
    public Sensor(String name) { this.name = name; }
    @Override public void process() { System.out.println("统一处理基础设备：" + name); }
}
