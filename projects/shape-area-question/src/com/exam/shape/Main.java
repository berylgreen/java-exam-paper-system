package com.exam.shape;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 shape 模块测试...");
        Storage storage = new Storage();
        storage.add(new Shape("101", "圆形"));
        storage.add(new Shape("102", "矩形"));
        storage.add(new Shape("103", "三角形"));
        
        Shape item = storage.get("102");
        System.out.println("查询到: " + (item != null ? item.getName() : "null"));
        
        // TODO: 使用 HashMap 优化存储结构，提升查询效率
    }
}
