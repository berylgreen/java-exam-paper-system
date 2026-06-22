package com.exam.smarthome;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 smarthome 模块测试...");
        Storage storage = new Storage();
        storage.add(new Device("101", "智能灯"));
        storage.add(new Device("102", "智能空调"));
        storage.add(new Device("103", "智能门锁"));
        
        Device item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
