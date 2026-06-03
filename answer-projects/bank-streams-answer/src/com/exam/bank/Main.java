package com.exam.bank;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 银行系统 模块测试...");
        List<Account> list = new ArrayList<>();
        list.add(new Account(true, "Item A"));
        list.add(new Account(false, "Item B"));
        list.add(new Account(true, "Item C"));
        
        Processor processor = new Processor();
        List<String> result = processor.processList(list);
        System.out.println("处理结果: " + result);
        
        // TODO: 使用 Java 8 Stream API 重构 processList 方法
    }
}
