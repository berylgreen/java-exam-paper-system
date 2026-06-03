package com.exam.hotel;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 酒店管理系统 模块测试...");
        Room[] items = { new RegularRoom(), new VIPRoom() };
        Processor processor = new Processor();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
