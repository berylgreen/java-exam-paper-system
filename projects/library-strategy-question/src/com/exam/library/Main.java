package com.exam.library;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 library 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "Java编程思想");
        handler.handle("TYPE_B", "算法导论");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
