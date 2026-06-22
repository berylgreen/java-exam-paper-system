package com.exam.ecommerce;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 ecommerce 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "笔记本电脑");
        handler.handle("TYPE_B", "智能手机");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
