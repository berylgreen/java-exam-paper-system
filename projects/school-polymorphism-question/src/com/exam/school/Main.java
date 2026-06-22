package com.exam.school;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 school 模块测试...");
        Processor processor = new Processor();
        Object[] items = { new Undergraduate(), new Graduate() };
        
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
