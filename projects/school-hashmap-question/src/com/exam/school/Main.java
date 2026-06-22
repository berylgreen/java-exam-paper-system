package com.exam.school;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 school 模块测试...");
        Storage storage = new Storage();
        storage.add(new Student("101", "小明"));
        storage.add(new Student("102", "小红"));
        storage.add(new Student("103", "小刚"));
        
        Student item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
