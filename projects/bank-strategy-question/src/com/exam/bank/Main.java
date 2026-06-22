package com.exam.bank;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 bank 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "张三的账户");
        handler.handle("TYPE_B", "李四的账户");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
