package com.exam.school;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 教务系统 模块测试...");
        Storage storage = new Storage();
        storage.add(new Student("001", "Info 1"));
        
        Student item = storage.get("001");
        System.out.println("获取到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
