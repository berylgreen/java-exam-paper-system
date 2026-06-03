package com.exam.logistics;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 物流系统 模块测试...");
        Manager manager = new Manager();
        manager.add(new Package("001"));
        manager.add(new Package("002"));
        manager.add(new Package("001")); // 重复数据
        
        manager.printAll();
        
        // TODO: 使用 ArrayList 或 HashSet 替代定长数组，并实现去重和排序
    }
}
