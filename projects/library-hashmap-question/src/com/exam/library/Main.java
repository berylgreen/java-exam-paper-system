package com.exam.library;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 图书馆系统 模块测试...");
        Storage storage = new Storage();
        storage.add("001", new Book("001", "Info 1"));
        
        Book item = storage.get("001");
        System.out.println("获取到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
