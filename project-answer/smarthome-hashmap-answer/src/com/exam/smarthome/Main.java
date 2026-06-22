package com.exam.smarthome;

import java.util.HashMap;
import java.util.Map;

class Device {
    private String id;
    private String name;

    public Device(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Device{id='" + id + "', name='" + name + "'}";
    }
}

class DeviceManager {
    private final Map<String, Device> deviceMap = new HashMap<>();

    // 添加设备
    public void addDevice(Device device) {
        deviceMap.put(device.getId(), device);
    }

    // 根据 id 查询设备
    public Device getDevice(String id) {
        return deviceMap.get(id);
    }

    // 根据 id 删除设备
    public void removeDevice(String id) {
        deviceMap.remove(id);
    }

    // 获取设备数量（用于测试输出）
    public int getDeviceCount() {
        return deviceMap.size();
    }
}

public class Main {
    public static void main(String[] args) {
        DeviceManager manager = new DeviceManager();

        // 添加测试数据
        manager.addDevice(new Device("101", "智能灯"));
        manager.addDevice(new Device("102", "智能空调"));
        manager.addDevice(new Device("103", "智能门锁"));

        System.out.println("添加后设备数量：" + manager.getDeviceCount());

        // 查询设备
        System.out.println("查询 id=102 的设备：" + manager.getDevice("102"));

        // 删除设备
        manager.removeDevice("102");

        // 删除后再次查询
        System.out.println("删除后再次查询 id=102：" + manager.getDevice("102"));
    }
}
