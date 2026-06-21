package com.exam.rental;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 租车系统 模块测试...");
        Storage storage = new Storage();
        storage.add(new Vehicle("001", "Info 1"));
        
        Vehicle item = storage.get("001");
        System.out.println("获取到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
