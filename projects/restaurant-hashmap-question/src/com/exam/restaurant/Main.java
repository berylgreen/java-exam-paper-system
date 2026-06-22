package com.exam.restaurant;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 restaurant 模块测试...");
        Storage storage = new Storage();
        storage.add(new Dish("101", "宫保鸡丁"));
        storage.add(new Dish("102", "鱼香肉丝"));
        storage.add(new Dish("103", "红烧肉"));
        
        Dish item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
