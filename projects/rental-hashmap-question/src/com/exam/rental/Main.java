package com.exam.rental;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 rental 模块测试...");
        Storage storage = new Storage();
        storage.add(new Vehicle("101", "丰田卡罗拉"));
        storage.add(new Vehicle("102", "本田雅阁"));
        storage.add(new Vehicle("103", "宝马X5"));
        
        Vehicle item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
