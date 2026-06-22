package com.exam.school;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 school 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "小明");
        handler.handle("TYPE_B", "小红");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
