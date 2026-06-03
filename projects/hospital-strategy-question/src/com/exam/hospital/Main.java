package com.exam.hospital;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 医疗系统 模块测试...");
        Handler handler = new Handler();
        handler.handle(new Patient("001", "TYPE_A"));
        handler.handle(new Patient("002", "TYPE_B"));
        
        // TODO: 使用策略模式（Strategy Pattern）重构 switch-case 逻辑
    }
}
