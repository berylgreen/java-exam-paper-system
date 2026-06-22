package com.exam.bank;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 bank 模块测试...");
        Storage storage = new Storage();
        storage.add(new Account("101", "张三的账户"));
        storage.add(new Account("102", "李四的账户"));
        storage.add(new Account("103", "王五的账户"));
        
        Account item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
