package com.exam.library;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 library 模块测试...");
        Storage storage = new Storage();
        storage.add(new Book("101", "Java编程思想"));
        storage.add(new Book("102", "算法导论"));
        storage.add(new Book("103", "计算机网络"));
        
        Book item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
