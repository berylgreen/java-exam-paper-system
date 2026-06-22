package com.exam.logistics;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 logistics 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "电子产品包裹");
        handler.handle("TYPE_B", "书籍包裹");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
