package com.exam.smarthome;
public class Main {
    public static void main(String[] args) {
        System.out.println("--- 执行测试用例 ---");
        Storage storage = new Storage();
        storage.add(new Device("101", "智能灯"));
        storage.add(new Device("102", "智能空调"));
        storage.add(new Device("103", "智能门锁"));
        System.out.println("添加后设备数量：" + storage.size());
        Device item = storage.get("102");
        System.out.println("查询 id=102 的设备：" + (item != null ? item.getName() : "null"));
        storage.remove("102");
        System.out.println("删除后再次查询 id=102：" + (storage.get("102") != null ? storage.get("102").getName() : "未找到"));
    }
}
