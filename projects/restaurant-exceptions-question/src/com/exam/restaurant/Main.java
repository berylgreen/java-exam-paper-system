package com.exam.restaurant;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 restaurant 模块测试...");
        DataParser parser = new DataParser();
        String[] data = {"100", "200", "abc", "300"}; // 包含非法数据
        
        // 运行将会抛出 NumberFormatException 并中断程序，后续的 "300" 无法被处理
        parser.parseData(data);
        
        // TODO: 自定义异常类，并在解析过程中使用 try-catch 捕获异常，保证后续数据继续处理
    }
}
