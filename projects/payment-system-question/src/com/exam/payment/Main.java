package com.exam.payment;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 payment 模块测试...");
        Processor processor = new Processor();
        Object[] items = { new TypeA(), new TypeB() };
        
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
