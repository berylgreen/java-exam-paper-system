package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 餐饮系统 模块测试...");
        Dish[] items = { new RegularDish(), new VIPDish() };
        DishStatistics processor = new DishStatistics();
        processor.processAll(items);
        
        // TODO: 使用多态重构，消除 DishStatistics 中的 instanceof 判断
    }
}
