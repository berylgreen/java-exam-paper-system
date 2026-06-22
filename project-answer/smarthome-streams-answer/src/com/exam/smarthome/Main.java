package com.exam.smarthome;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 智能家居系统 模块测试...");
        List<Device> list = new ArrayList<>();
        list.add(new Device(true, "Item A"));
        list.add(new Device(false, "Item B"));
        list.add(new Device(true, "Item C"));
        
        Processor processor = new Processor();
        List<String> result = processor.processList(list);
        System.out.println("处理结果: " + result);
        
        // TODO: 使用 Java 8 Stream API 重构 processList 方法
    }
}
