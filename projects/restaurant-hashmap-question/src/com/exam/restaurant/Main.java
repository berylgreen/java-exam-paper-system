package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 餐饮系统 模块测试...");
        Storage storage = new Storage();
        storage.add("001", new Dish("001", "Info 1"));
        
        Dish item = storage.get("001");
        System.out.println("获取到: " + (item != null ? item.info : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
