package com.exam.smarthome;

abstract class Device {
    public abstract void process();
}

class LightDevice extends Device {
    @Override
    public void process() {
        System.out.println("统计并处理照明设备数据");
    }
}

class AirConditionerDevice extends Device {
    @Override
    public void process() {
        System.out.println("统计并处理空调设备数据");
    }
}

class SecurityDevice extends Device {
    @Override
    public void process() {
        System.out.println("统计并处理安防设备数据");
    }
}

class DeviceStatistics {
    public void processAll(Device[] devices) {
        for (Device device : devices) {
            device.process();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Device[] devices = {
            new LightDevice(),
            new AirConditionerDevice(),
            new SecurityDevice()
        };

        DeviceStatistics statistics = new DeviceStatistics();
        statistics.processAll(devices);
    }
}
