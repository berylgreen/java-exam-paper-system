package com.exam.smarthome;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 智能家居系统 模块测试...");
        Device[] items = { new RegularDevice(), new VIPDevice() };
        Processor processor = new Processor();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
