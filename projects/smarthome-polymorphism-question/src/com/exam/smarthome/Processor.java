package com.exam.smarthome;

public class Processor {
    // 原始设计：使用大量 if-else 和 instanceof 进行类型判断
    public void processAll(Device[] items) {
        for (Device obj : items) {
            if (obj instanceof RegularDevice) {
                System.out.println("处理普通业务逻辑");
            } else if (obj instanceof VIPDevice) {
                System.out.println("处理VIP业务逻辑");
            } else {
                System.out.println("处理未知业务逻辑");
            }
        }
    }
}
