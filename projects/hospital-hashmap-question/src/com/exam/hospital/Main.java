package com.exam.hospital;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 hospital 模块测试...");
        Storage storage = new Storage();
        storage.add(new Patient("101", "张三的病历"));
        storage.add(new Patient("102", "李四的病历"));
        storage.add(new Patient("103", "王五的病历"));
        
        Patient item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
