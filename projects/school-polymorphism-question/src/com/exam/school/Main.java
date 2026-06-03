package com.exam.school;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 教务系统 模块测试...");
        Student[] items = { new RegularStudent(), new VIPStudent() };
        Processor processor = new Processor();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
