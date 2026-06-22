package com.exam.lambda;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 lambda 模块测试...");
        Storage storage = new Storage();
        storage.add(new Element("101", "元素1"));
        storage.add(new Element("102", "元素2"));
        storage.add(new Element("103", "元素3"));
        
        Element item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
