package com.exam.hotel;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 hotel 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "总统套房");
        handler.handle("TYPE_B", "豪华大床房");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
