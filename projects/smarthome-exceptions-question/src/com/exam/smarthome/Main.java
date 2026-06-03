package com.exam.smarthome;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 智能家居系统 模块测试...");
        DataParser parser = new DataParser();
        String[] data = {"100", "200", "abc", "300"}; // 包含非法数据
        
        // 运行将会抛出 NumberFormatException 并中断程序
        parser.parseData(data);
        
        // TODO: 使用 try-catch 捕获异常，抛出自定义异常，保证后续数据继续处理
    }
}
