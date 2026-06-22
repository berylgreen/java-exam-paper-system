package com.exam.hotel;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 酒店管理系统 模块测试...");
        List<Room> list = new ArrayList<>();
        list.add(new Room(true, "Item A"));
        list.add(new Room(false, "Item B"));
        list.add(new Room(true, "Item C"));
        
        Processor processor = new Processor();
        List<String> result = processor.processList(list);
        System.out.println("处理结果: " + result);
        
        // TODO: 使用 Java 8 Stream API 重构 processList 方法
    }
}
