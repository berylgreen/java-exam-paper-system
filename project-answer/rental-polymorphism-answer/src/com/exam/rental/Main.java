package com.exam.rental;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 租车系统 模块测试...");
        Vehicle[] items = { new RegularVehicle(), new VIPVehicle() };
        Processor processor = new Processor();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 Processor 中的 instanceof 判断
    }
}
