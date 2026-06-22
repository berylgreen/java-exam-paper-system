package com.exam.logistics;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 物流系统 模块测试...");
        Package[] items = { new RegularPackage(), new VIPPackage() };
        PackageStatistics processor = new PackageStatistics();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 PackageStatistics 中的 instanceof 判断
    }
}

class VIPPackage extends Package { public void process() {} }

