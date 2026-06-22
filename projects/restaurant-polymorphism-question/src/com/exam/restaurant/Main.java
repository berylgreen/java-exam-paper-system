package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 restaurant 模块测试...");
        Processor processor = new Processor();
        Object[] items = { new ColdDish(), new HotDish() };
        
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
