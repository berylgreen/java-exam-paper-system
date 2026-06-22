package com.exam.hotel;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 hotel 模块测试...");
        Storage storage = new Storage();
        storage.add(new Room("101", "总统套房"));
        storage.add(new Room("102", "豪华大床房"));
        storage.add(new Room("103", "标准间"));
        
        Room item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
