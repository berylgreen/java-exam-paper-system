package com.exam.logistics;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 logistics 模块测试...");
        Storage storage = new Storage();
        storage.add(new Package("101", "电子产品包裹"));
        storage.add(new Package("102", "书籍包裹"));
        storage.add(new Package("103", "衣物包裹"));
        
        Package item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
