package com.exam.library;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 图书馆系统 模块测试...");
        Handler handler = new Handler();
        handler.handle(new Book("001", "TYPE_A"));
        handler.handle(new Book("002", "TYPE_B"));
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
