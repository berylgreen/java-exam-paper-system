package com.exam.library;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 图书馆系统 模块测试...");
        List<Book> list = new ArrayList<>();
        list.add(new Book(true, "Item A"));
        list.add(new Book(false, "Item B"));
        list.add(new Book(true, "Item C"));
        
        Processor processor = new Processor();
        List<String> result = processor.processList(list);
        System.out.println("处理结果: " + result);
        
        // TODO: 使用 Java 8 Stream API 重构 processList 方法
    }
}
