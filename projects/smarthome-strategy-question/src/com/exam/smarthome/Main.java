package com.exam.smarthome;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 smarthome 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "智能灯");
        handler.handle("TYPE_B", "智能空调");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
