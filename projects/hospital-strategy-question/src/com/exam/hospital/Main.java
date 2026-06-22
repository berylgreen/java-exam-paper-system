package com.exam.hospital;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 hospital 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "张三的病历");
        handler.handle("TYPE_B", "李四的病历");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
