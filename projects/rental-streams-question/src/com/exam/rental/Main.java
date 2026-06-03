package com.exam.rental;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("启动 租车系统 模块测试...");
        List<Car> list = new ArrayList<>();
        list.add(new Car(true, "Item A"));
        list.add(new Car(false, "Item B"));
        list.add(new Car(true, "Item C"));
        
        Processor processor = new Processor();
        List<String> result = processor.processList(list);
        System.out.println("处理结果: " + result);
        
        // TODO: 使用 Java 8 Stream API 重构 processList 方法
    }
}
