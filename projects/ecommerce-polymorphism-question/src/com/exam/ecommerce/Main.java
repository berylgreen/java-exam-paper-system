package com.exam.ecommerce;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ecommerce 模块测试...");
        Processor processor = new Processor();
        Object[] items = { new StandardOrder(), new ExpressOrder() };
        
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
