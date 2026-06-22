package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 restaurant 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "宫保鸡丁");
        handler.handle("TYPE_B", "鱼香肉丝");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
