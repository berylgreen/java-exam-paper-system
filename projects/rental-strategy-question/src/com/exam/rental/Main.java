package com.exam.rental;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 rental 模块测试...");
        BusinessHandler handler = new BusinessHandler();
        
        handler.handle("TYPE_A", "丰田卡罗拉");
        handler.handle("TYPE_B", "本田雅阁");
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
