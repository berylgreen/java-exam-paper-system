package com.exam.bank;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 银行系统 模块测试...");
        Logger logger = new Logger();
        logger.writeLog("System started");
        logger.writeLog("Operation success");
        System.out.println("日志方法调用完成。");
    }
}
