package com.exam.ecommerce;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 电商系统 模块测试...");
        Order[] items = { new RegularOrder(), new VIPOrder() };
        Processor processor = new Processor();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
